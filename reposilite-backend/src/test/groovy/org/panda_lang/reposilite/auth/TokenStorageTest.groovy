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

package org.panda_lang.reposilite.auth

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.panda_lang.reposilite.ReposiliteConstants
import org.panda_lang.utilities.commons.FileUtils

import static org.junit.jupiter.api.Assertions.*

@CompileStatic
final class TokenStorageTest {

    @TempDir
    public File workingDirectory

    @Test
    void 'should convert old data file' () {
        def tokenStorage = new TokenStorage(new TokenService(workingDirectory), workingDirectory)

        FileUtils.overrideFile(new File(workingDirectory, 'tokens.yml'), 'tokens: []')
        tokenStorage.loadTokens()

        def dataFile = new File(workingDirectory, ReposiliteConstants.TOKENS_FILE_NAME)
        assertTrue dataFile.exists()
        assertEquals 'tokens: []', FileUtils.getContentOfFile(dataFile)
    }

}
