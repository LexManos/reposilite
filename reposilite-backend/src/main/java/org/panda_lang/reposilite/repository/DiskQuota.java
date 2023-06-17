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

package org.panda_lang.reposilite.repository;

import org.apache.commons.io.FileUtils;
import org.panda_lang.reposilite.utils.FilesUtils;

import java.io.File;
import java.util.concurrent.atomic.AtomicLong;

public final class DiskQuota {

    private final AtomicLong quota;
    private final AtomicLong usage;

    DiskQuota(long quota, File directory) {
        this.quota = new AtomicLong(quota);
        this.usage = new AtomicLong(-1);
        // Run this in the background because it can take some time. We want to be able to serve files while this is calculating.
        new Thread(() -> {
            long usage = FileUtils.sizeOfDirectory(directory);
            this.usage.set(usage);
        }).start();
    }

    void allocate(long size) {
        usage.addAndGet(size);
    }

    public boolean hasUsableSpace() {
        return !isReady() ? false : usage.get() < quota.get();
    }

    public long getUsage() {
        return usage.get();
    }

    // Don't allow using space while we are calculating
    public boolean isReady() {
        return usage.get() != -1;
    }

    static DiskQuota of(File workingDirectory, String value) {
        if (value.endsWith("%")) {
            int percentage = Integer.parseInt(value.substring(0, value.length() - 1));
            return new DiskQuota(Math.round(workingDirectory.getTotalSpace() * (percentage / 100D)), workingDirectory);
        }

        return new DiskQuota(FilesUtils.displaySizeToBytesCount(value), workingDirectory);
    }

}
