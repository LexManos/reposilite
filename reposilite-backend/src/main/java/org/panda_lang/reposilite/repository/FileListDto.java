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

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
final class FileListDto implements Serializable {

    private final List<? extends FileDetailsDto> files;

    FileListDto(List<? extends FileDetailsDto> files) {
        this.files = files;
    }

    public List<? extends FileDetailsDto> getFiles() {
        return files;
    }

}
