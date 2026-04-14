/*
 * Copyright 2018-2026 Pavel Castornii.
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

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;

/**
 *
 * @author Pavel Castornii
 */
public final class ArtifactConverter {

    public static Artifact convert(ArtifactDescriptor descriptor) {
        Artifact artifact = new DefaultArtifact(descriptor.getGroupId(), descriptor.getArtifactId(),
                    descriptor.getClassifier(), descriptor.getType(), descriptor.getVersion());
        return artifact;
    }

    public static ArtifactDescriptor convert(Artifact artifact) {
        ArtifactDescriptor descriptor = new DefaultArtifactDescriptor(
                artifact.getGroupId(),
                artifact.getArtifactId(),
                artifact.getVersion(),
                artifact.getClassifier(),
                artifact.getExtension());
        return descriptor;
    }

    private ArtifactConverter() {
        // empty
    }
}
