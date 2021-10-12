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

package org.panda_lang.reposilite.repository

import groovy.transform.CompileStatic
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.panda_lang.reposilite.repository.IRepository.Builder

import static org.junit.jupiter.api.Assertions.*

@CompileStatic
class RepositoryTest {

    @TempDir
    protected static File temp

    private static IRepository repository

    @BeforeAll
    static void prepare() {
        repository = IRepository.builder("releases").dir(temp).build();

        repository.getFile("group", "artifact", "version").mkdirs()
        repository.getFile("group", "artifact", "version", "test").createNewFile()
    }

    @Test
    void 'should find requested entity' () {
        assertFalse repository.contains("unknown")
        assertTrue repository.contains("group", "artifact", "version", "test")
    }

    @Test
    void 'should return file' () {
        assertEquals "test", repository.getFile("test").getName()
    }

    @Test
    void 'should return repository name' () {
        assertEquals "releases", repository.getName()
    }

}