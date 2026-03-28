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

module com.techsenger.reposium.core {
    requires com.techsenger.toolkit.core;
    requires org.slf4j;
    requires maven.resolver.provider;
    requires org.apache.maven.resolver;
    requires org.apache.maven.resolver.spi;
    requires org.apache.maven.resolver.impl;
    requires org.apache.maven.resolver.connector.basic;
    requires org.apache.maven.resolver.transport.file;
    requires org.apache.maven.resolver.transport.http;

    exports com.techsenger.reposium.core;
}
