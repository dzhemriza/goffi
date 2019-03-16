/*
 * org.goffi.my.vault
 *
 * File Name: FileService.java
 *
 * Copyright 2017 Dzhem Riza
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
package org.goffi.my.vault.services;

import org.goffi.core.domainmodel.crypto.DataTransformer;
import org.goffi.my.vault.model.Document;

import java.io.File;

public interface FileService {

    void save(Document document, DataTransformer encoder, File file);

    Document open(File file, DataTransformer decoder);
}
