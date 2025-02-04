/*
 * Copyright 2018-2025 Pavel Castornii.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.techsenger.reposium.core;

import com.techsenger.toolkit.core.file.FileUtils;
import com.techsenger.toolkit.core.file.PathUtils;
import com.techsenger.toolkit.core.os.OsUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public class MavenRepo {

    private static final Logger logger = LoggerFactory.getLogger(MavenRepo.class);

    private static class RepoFileVisitor extends SimpleFileVisitor<Path> {

        private final Path localRepo;

        private final List<ArtifactDescriptor> descriptors;

        RepoFileVisitor(Path localRepo, List<ArtifactDescriptor> descriptors) {
            this.localRepo = localRepo;
            this.descriptors = descriptors;
        }

        @Override
        public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
            String pathString = path.toString().toLowerCase();
            if (pathString.endsWith("jar") || pathString.endsWith("war")) {
                var versionPath = path.getParent();
                var artifactPath = versionPath.getParent();
                List<String> subGroups = new ArrayList<>();
                var groupPath = artifactPath.getParent();
                while (!groupPath.equals(localRepo)) {
                    subGroups.add(groupPath.getFileName().toString());
                    groupPath = groupPath.getParent();
                }
                Collections.reverse(subGroups);
                var descriptor = new DefaultArtifactDescriptor(String.join(".", subGroups),
                        artifactPath.getFileName().toString(),
                        versionPath.getFileName().toString(), null, PathUtils.getFileExtension(path));
                descriptors.add(descriptor);

            }
            return FileVisitResult.CONTINUE;
        }
    };

    public boolean resolve(Path localRepo, Map<String, String> remoteReposByName, ArtifactDescriptor descriptor,
            MessagePrinter printer) {
        return this.resolve(localRepo, remoteReposByName, List.of(descriptor), printer);
    }

    public boolean resolve(Path localRepo, Map<String, String> remoteReposByName, List<ArtifactDescriptor> descriptors,
            MessagePrinter printer) {
        RepositorySystem localSystem = RepoUtils.newRepositorySystem();
        //building session
        RepositorySystemSession localSession = RepoUtils.newRepositorySystemSession(localSystem,
                localRepo.toAbsolutePath().toString(), new RepositoryListener(), null);
        //remote repos, order is saved if LinkedHashMap is used.
        List<RemoteRepository> remoteRepositories = remoteReposByName
                .entrySet()
                .stream()
                .map(e -> new RemoteRepository.Builder(e.getKey(), "default", e.getValue()).build())
                //.map(e -> RepoUtils.newCentralRepository(e.getValue()))
                .collect(Collectors.toList());

        boolean result = true;
        //resolve each artifact
        for (var descriptor : descriptors) {
            Artifact artifact = new DefaultArtifact(descriptor.getGroupId(), descriptor.getArtifactId(),
                    descriptor.getClassifier(), descriptor.getType(), descriptor.getVersion());
            ArtifactRequest artifactRequest = new ArtifactRequest();
            artifactRequest.setArtifact(artifact);
            artifactRequest.setRepositories(remoteRepositories);
            ArtifactResult artifactResult;
            try {
                artifactResult = localSystem.resolveArtifact(localSession, artifactRequest);
                if (!artifactResult.isResolved()) {
                    result = false;
                }
                artifact = artifactResult.getArtifact();
                logger.debug("Resolved {} to {}", artifact, artifact.getFile());
            } catch (ArtifactResolutionException ex) {
                logger.error("Error resolving artifact={}", descriptor, ex);
                result = false;
            }
        }
        return result;
    }

    public boolean unresolve(Path localRepo, ArtifactDescriptor descriptor, MessagePrinter printer) {
        return this.unresolve(localRepo, List.of(descriptor), printer);
    }

    public boolean unresolve(Path localRepo, List<ArtifactDescriptor> descriptors, MessagePrinter printer) {
        Path absolutePath = null;
        try {
            for (var descriptor : descriptors) {
                String relativePath = null;
                if (OsUtils.isUnix()) {
                    relativePath = descriptor.getGroupId().replaceAll(Pattern.quote("."), File.separator);
                } else if (OsUtils.isWindows()) {
                    relativePath = descriptor.getGroupId().replaceAll(Pattern.quote("."), "\\\\");
                }
                relativePath = relativePath
                        + File.separator
                        + descriptor.getArtifactId()
                        + File.separator
                        + descriptor.getVersion();
                absolutePath = localRepo.resolve(relativePath).toAbsolutePath();
                if (Files.exists(absolutePath)) {
                    FileUtils.deleteDirectory(absolutePath.toFile());
                }
                logger.debug("Unresolved artifact id={}, version={}", descriptor.getArtifactId(),
                        descriptor.getVersion());
            }
            return true;
        } catch (Exception e) {
            logger.error("Error unresolving artifact {}", absolutePath, e);
            return false;
        }
    }

    public List<ArtifactDescriptor> scanRepo(Path localRepo) throws IOException {
        List<ArtifactDescriptor> descriptors = new ArrayList<>();
        FileVisitor<Path> fv = new RepoFileVisitor(localRepo, descriptors);
        Files.walkFileTree(localRepo, fv);
        return descriptors;
    }
}
