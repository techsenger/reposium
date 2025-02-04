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

package com.techsenger.reposium.cli.commands;

import com.beust.jcommander.Parameter;
import com.techsenger.reposium.cli.Command;
import com.techsenger.reposium.core.ArtifactDescriptor;
import com.techsenger.reposium.core.ConsoleMessagePrinter;
import com.techsenger.reposium.core.DefaultArtifactDescriptor;
import com.techsenger.reposium.core.MavenRepo;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
class ResolveCommand implements Command {

    private static final Logger logger = LoggerFactory.getLogger(ResolveCommand.class);

    /**
     * Remote repos.
     * Example: -r "local:file:c:/Users/blah/.m2/repository","central:https://repo1.maven.org/maven2/"
     * or without quotes (if no spaces)
     * -r local:file:/home/user/.m2/repository,central:https://repo1.maven.org/maven2/
     */
    @Parameter(names = {"-r"}, description = "Remote repo URLs. Format repoName:url")
    private List<String> remoteRepos = new ArrayList<>();

    /**
     * Local repo path.
     * Example: -l "file:c:/Users/blah/.m2/repository"
     * or without quotes (if no spaces)
     * -l file:c:/Users/blah/.m2/repository
     */
    @Parameter(names = {"-l"}, description = "Local repo URL")
    private String localRepo;

    /**
     * Artifact list.
     * Example: -a org.slf4j:slf4j-api:1.8.0-beta4,org.openjsfx:javafx-base:jar:linux:1.8.0-beta4
     */
    @Parameter(names = {"-a"},
            description = "Artifacats in format <groupId>:<artifactId>[:<extension>[:<classifier>]]:<version>")
    private List<String> artifacts = new ArrayList<>();

    @Override
    public void execute() {
        Map<String, String> repoUrlsByName = new HashMap<>();
        for (var remoteRepo : remoteRepos) {
            var nameIndex = remoteRepo.indexOf(":");
            var name = remoteRepo.substring(0, nameIndex);
            var url = remoteRepo.substring(nameIndex + 1);
            repoUrlsByName.put(name, url);
        }

        List<ArtifactDescriptor> artifactDescriptors = new ArrayList<>();
        for (var artifactStr : artifacts) {
            //in order not to parse artifact string we use aether artifact
            Artifact artifact = new DefaultArtifact(artifactStr);
            var descriptor = new DefaultArtifactDescriptor(artifact.getGroupId(), artifact.getArtifactId(),
                    artifact.getVersion(), artifact.getClassifier(), artifact.getExtension());
            artifactDescriptors.add(descriptor);
        }
        try {
            var repo = new MavenRepo();
            repo.resolve(Paths.get(localRepo), repoUrlsByName, artifactDescriptors, new ConsoleMessagePrinter());
        } catch (Exception e) {
            logger.error("Error installing artifacts", e);
        }
    }
}
