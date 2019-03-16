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
package org.goffi.my.vault.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.goffi.core.domainmodel.Constants;
import org.goffi.core.domainmodel.crypto.DataTransformer;
import org.goffi.my.vault.exceptions.MyVaultException;
import org.goffi.my.vault.model.Document;
import org.goffi.my.vault.services.FileService;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterInputStream;
import java.util.zip.InflaterOutputStream;

public class FileServiceImpl implements FileService {

    private ObjectMapper objectMapper;

    public FileServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(Document document, DataTransformer encoder, File file) {
        try {
            String jsonString = objectMapper.writeValueAsString(document);
            byte[] jsonAsBytes = jsonString.getBytes(Constants.DEFAULT_CHARSET);

            try (ByteArrayInputStream byteArrayInputStream =
                         new ByteArrayInputStream(jsonAsBytes);
                 DeflaterInputStream deflaterInputStream =
                         new DeflaterInputStream(byteArrayInputStream);
                 FileOutputStream fileOutputStream = new FileOutputStream(file);
                 BufferedOutputStream bufferedOutputStream = new
                         BufferedOutputStream(
                         fileOutputStream)) {
                encoder.transform(deflaterInputStream, bufferedOutputStream);
            }
        } catch (IOException e) {
            throw new MyVaultException(e.getMessage(), e);
        }
    }

    @Override
    public Document open(File file, DataTransformer decoder) {
        try {
            try (FileInputStream fileInputStream = new FileInputStream(file);
                 BufferedInputStream bufferedInputStream = new
                         BufferedInputStream(
                         fileInputStream);
                 ByteArrayOutputStream byteArrayOutputStream = new
                         ByteArrayOutputStream();
                 InflaterOutputStream inflaterOutputStream =
                         new InflaterOutputStream(byteArrayOutputStream)) {
                decoder.transform(bufferedInputStream, inflaterOutputStream);

                return objectMapper.readValue(
                        byteArrayOutputStream.toByteArray(), Document.class);
            }
        } catch (IOException e) {
            throw new MyVaultException(e.getMessage(), e);
        }
    }
}
