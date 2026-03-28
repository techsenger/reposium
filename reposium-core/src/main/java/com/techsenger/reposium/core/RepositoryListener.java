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

import org.eclipse.aether.AbstractRepositoryListener;
import org.eclipse.aether.RepositoryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
class RepositoryListener extends AbstractRepositoryListener {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryListener.class);

    RepositoryListener() {

    }

//    @Override
//    public void artifactDownloading(RepositoryEvent event) {
//        logger.debug("Downloading artifact {} from {}", event.getArtifact(), event.getRepository());
//    }

    @Override
    public void artifactDownloaded(RepositoryEvent event) {
        logger.debug("Downloaded artifact {} from {}", event.getArtifact(), event.getRepository());
    }

}
