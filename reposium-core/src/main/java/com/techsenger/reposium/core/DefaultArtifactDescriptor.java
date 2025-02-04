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

/**
 *
 * @author Pavel Castornii
 */
public class DefaultArtifactDescriptor implements ArtifactDescriptor {

    private final String groupId;

    private final String artifactId;

    private final String version;

    private final String classifier;

    private final String type;

    public DefaultArtifactDescriptor(String groupId, String artifactId, String version, String classifier,
            String type) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.classifier = classifier;
        this.type = type;
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public String getArtifactId() {
        return artifactId;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getClassifier() {
        return classifier;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "DefaultArtifactDescriptor{" + "groupId=" + groupId + ", artifactId=" + artifactId
                + ", version=" + version + ", classifier=" + classifier + ", type=" + type + '}';
    }
}
