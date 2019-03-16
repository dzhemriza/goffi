/*
 * org.goffi.toffi
 *
 * File Name: StackData.java
 *
 * Copyright 2016 Dzhem Riza
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.goffi.toffi.domainmodel.files;

import java.nio.file.Path;

class StackData {

    private Path source;
    private Path target;
    private DirectoryMetadata directoryMetadata = new DirectoryMetadata();

    public StackData() {
    }

    public StackData(Path source, Path target) {
        this.source = source;
        this.target = target;
    }

    public Path getSource() {
        return source;
    }

    public void setSource(Path source) {
        this.source = source;
    }

    public Path getTarget() {
        return target;
    }

    public void setTarget(Path target) {
        this.target = target;
    }

    public DirectoryMetadata getDirectoryMetadata() {
        return directoryMetadata;
    }

    public void setDirectoryMetadata(DirectoryMetadata directoryMetadata) {
        this.directoryMetadata = directoryMetadata;
    }

    @Override
    public String toString() {
        return "StackData{" +
                "source=" + source +
                ", target=" + target +
                ", directoryMetadata=" + directoryMetadata +
                '}';
    }
}
