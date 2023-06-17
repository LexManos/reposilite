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


import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertFalse
import static org.junit.jupiter.api.Assertions.assertTrue

final class DiskQuotaTest {

    @TempDir
    public File workingDirectory

    @Test
    @SuppressWarnings('GroovyAccessibility')
    void 'should create quota of the given percentage' () {
        def quota = DiskQuota.of(workingDirectory, '90%')
        while (!quota.ready)
            Thread.sleep(10)

        def size = quota.@quota.longValue()

        assertTrue size > 0
        assertEquals 0, quota.@usage.longValue()
        assertTrue quota.hasUsableSpace()

        quota.allocate(1)
        assertTrue quota.hasUsableSpace()

        quota.allocate(size)
        assertFalse quota.hasUsableSpace()
    }

    @Test
    @SuppressWarnings('GroovyAccessibility')
    void 'should create quota of the given size' () {
        def size = 10L * 1024 * 1024 * 1024
        def quota = DiskQuota.of(workingDirectory, '10GB')
        while (!quota.ready)
            Thread.sleep(10)

        assertEquals size, quota.@quota.longValue()
        assertEquals 0, quota.@usage.longValue()
        assertTrue quota.hasUsableSpace()

        quota.allocate(1)
        assertTrue quota.hasUsableSpace()

        quota.allocate(size)
        assertFalse quota.hasUsableSpace()
    }

}
