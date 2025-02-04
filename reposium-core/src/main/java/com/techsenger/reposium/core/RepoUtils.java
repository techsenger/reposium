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

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositoryListener;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transfer.TransferListener;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Repo utils.
 *
 * @author Pavel Castornii
 */
final class RepoUtils {

    private static final Logger logger = LoggerFactory.getLogger(RepoUtils.class);

    static RepositorySystem newRepositorySystem() {
        //Aether's components implement org.eclipse.aether.spi.locator.Service to ease manual wiring and using the
        //prepopulated DefaultServiceLocator, we only need to register the repository connector and transporter
        //factories.
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
        locator.setErrorHandler(new DefaultServiceLocator.ErrorHandler() {
            @Override
            public void serviceCreationFailed(Class<?> type, Class<?> impl, Throwable exception) {
               logger.error("Service creation failed for type={} with implementation={}", type, impl, exception);
            }
        });
        return locator.getService(RepositorySystem.class);
    }

    static RepositorySystemSession newRepositorySystemSession(RepositorySystem system,
            String repoPath, RepositoryListener repoListener, TransferListener transferListener) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
        LocalRepository localRepo = new LocalRepository(repoPath);
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));
        session.setRepositoryListener(repoListener);
        session.setTransferListener(transferListener);
        // uncomment to generate dirty trees
        // session.setDependencyGraphTransformer( null );
        return session;
    }

    /**
     *
     * @param url for example https://repo.maven.apache.org/maven2/
     * @return
     */
    static RemoteRepository newCentralRepository(String url) {
        return new RemoteRepository.Builder("central", "default", url).build();
    }

    private RepoUtils() {
        //empty
    }
}
