/*
 * org.goffi.text.encoder
 *
 * File Name: TextTransformerService.java
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
package org.goffi.text.encoder.gui;

import javafx.beans.property.*;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * Text encode/decode transformation service. Because the encryption could be costly sometimes.
 */
public class TextTransformerService extends Service<String> {

    private final StringProperty textToBeTransformed = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();
    private final StringProperty repeatedPassword = new SimpleStringProperty();
    private final StringProperty memorableWord = new SimpleStringProperty();
    private final StringProperty pin = new SimpleStringProperty();
    private final BooleanProperty useCompression = new SimpleBooleanProperty();
    private final ObjectProperty<TextTransformer> textTransformer = new SimpleObjectProperty<>();

    @Override
    protected Task<String> createTask() {
        return new Task<>() {
            @Override
            protected String call() throws Exception {
                return getTextTransformer().transform(
                        getPassword().toCharArray(),
                        getRepeatedPassword().toCharArray(),
                        getMemorableWord().toCharArray(),
                        getPin().toCharArray(),
                        getUseCompression(),
                        getTextToBeTransformed());
            }
        };
    }

    public TextTransformer getTextTransformer() {
        return textTransformer.get();
    }

    public void setTextTransformer(TextTransformer v) {
        textTransformer.set(v);
    }

    public ObjectProperty<TextTransformer> textTransformerProperty() {
        return textTransformer;
    }

    public boolean getUseCompression() {
        return useCompression.get();
    }

    public void setUseCompression(boolean useCompression) {
        this.useCompression.set(useCompression);
    }

    public BooleanProperty useCompressionProperty() {
        return useCompression;
    }

    public String getPin() {
        return pin.get();
    }

    public void setPin(String v) {
        pin.set(v);
    }

    public StringProperty pinProperty() {
        return pin;
    }

    public String getMemorableWord() {
        return memorableWord.get();
    }

    public void setMemorableWord(String v) {
        memorableWord.set(v);
    }

    public StringProperty memorableWordProperty() {
        return memorableWord;
    }

    public String getRepeatedPassword() {
        return repeatedPassword.get();
    }

    public void setRepeatedPassword(String v) {
        repeatedPassword.set(v);
    }

    public StringProperty repeatedPasswordProperty() {
        return repeatedPassword;
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String p) {
        password.set(p);
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public String getTextToBeTransformed() {
        return textToBeTransformed.get();
    }

    public void setTextToBeTransformed(String newValue) {
        textToBeTransformed.set(newValue);
    }

    public StringProperty textToBeTransformedProperty() {
        return textToBeTransformed;
    }
}
