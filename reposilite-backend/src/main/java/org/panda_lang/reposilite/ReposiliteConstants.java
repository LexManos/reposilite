/*
 * Copyright (c) 2020 Dzikoysk
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

package org.panda_lang.reposilite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ReposiliteConstants {
    private static final Logger LOGGER = LoggerFactory.getLogger("Reposilite");

    public static final String NAME = "Reposilite";

    public static final String VERSION = loadVersion();

    public static final String REMOTE_VERSION = ""; //"https://repo.panda-lang.org/org/panda-lang/reposilite/latest";

    public static final String CONFIGURATION_FILE_NAME = "reposilite.cdn";

    public static final String TOKENS_FILE_NAME = "tokens.dat";

    public static final String STATS_FILE_NAME = "stats.dat";

    private ReposiliteConstants() { }

    private static String loadVersion() {
        LOGGER.debug("Version package {} from {}", ReposiliteConstants.class.getPackage(), ReposiliteConstants.class.getClassLoader());
        String version = ReposiliteConstants.class.getPackage().getImplementationVersion();
        if (version == null && System.getProperty("reposilite.tests.version") != null) version = System.getProperty("reposilite.tests.version");
        if (version == null) throw new RuntimeException("Missing version, cannot continue");
        LOGGER.debug("Found version {}", version);
        return version;
    }
}
