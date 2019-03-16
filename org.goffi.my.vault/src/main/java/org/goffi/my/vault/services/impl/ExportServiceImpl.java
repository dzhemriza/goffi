/*
 * org.goffi.my.vault
 *
 * File Name: ExportServiceImpl.java
 *
 * Copyright 2018 Dzhem Riza
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
import org.apache.commons.codec.binary.Base64;
import org.goffi.core.domainmodel.Constants;
import org.goffi.core.domainmodel.crypto.DataTransformer;
import org.goffi.my.vault.exceptions.MyVaultException;
import org.goffi.my.vault.model.Document;
import org.goffi.my.vault.services.ClipboardService;
import org.goffi.my.vault.services.ExportService;
import org.goffi.my.vault.services.exceptions.InvalidBase64TextInClipboardException;
import org.goffi.my.vault.services.exceptions.NoTextFoundInClipboardException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.zip.DeflaterInputStream;
import java.util.zip.InflaterOutputStream;

public class ExportServiceImpl implements ExportService {

    private ObjectMapper objectMapper;
    private ClipboardService clipboardService;

    public ExportServiceImpl(ObjectMapper objectMapper,
            ClipboardService clipboardService) {
        this.objectMapper = objectMapper;
        this.clipboardService = clipboardService;
    }

    @Override
    public void exportToClipboard(Document document, DataTransformer encoder) {
        try {
            String jsonString = objectMapper.writeValueAsString(document);
            byte[] jsonAsBytes = jsonString.getBytes(Constants.DEFAULT_CHARSET);

            try (ByteArrayInputStream byteArrayInputStream =
                         new ByteArrayInputStream(jsonAsBytes);
                 DeflaterInputStream deflaterInputStream =
                         new DeflaterInputStream(byteArrayInputStream);
                 ByteArrayOutputStream byteArrayOutputStream =
                         new ByteArrayOutputStream()) {
                encoder.transform(deflaterInputStream, byteArrayOutputStream);

                clipboardService.copyToClipboard(Base64.encodeBase64String(
                        byteArrayOutputStream.toByteArray()));
            }

        } catch (IOException e) {
            throw new MyVaultException(e.getMessage(), e);
        }
    }

    @Override
    public Document importFromClipboard(DataTransformer decoder) {
        try {
            Optional<String> text = clipboardService.readTextFromClipboard();
            if (!text.isPresent()) {
                throw new NoTextFoundInClipboardException();
            }

            String clipboardText = text.get();
            if (!Base64.isBase64(clipboardText)) {
                throw new InvalidBase64TextInClipboardException(clipboardText);
            }

            byte[] inputByteArray = Base64.decodeBase64(clipboardText);

            try (ByteArrayInputStream byteArrayInputStream =
                         new ByteArrayInputStream(inputByteArray);
                 ByteArrayOutputStream byteArrayOutputStream =
                         new ByteArrayOutputStream();
                 InflaterOutputStream inflaterOutputStream =
                         new InflaterOutputStream(byteArrayOutputStream)) {
                decoder.transform(byteArrayInputStream, inflaterOutputStream);

                return objectMapper.readValue(
                        byteArrayOutputStream.toByteArray(), Document.class);
            }
        } catch (IOException e) {
            throw new MyVaultException(e.getMessage(), e);
        }
    }
}
