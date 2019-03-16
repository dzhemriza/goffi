/*
 * org.goffi.toffi
 *
 * File Name: PathConverter.java
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
package org.goffi.toffi.shell;

import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Simple converter from {@link String} to {@link java.nio.file.Path}
 */
@Component
public class PathConverter implements Converter<Path> {

    @Override
    public boolean supports(Class<?> aClass, String s) {
        return Path.class.isAssignableFrom(aClass);
    }

    @Override
    public Path convertFromText(String s, Class<?> aClass, String s1) {
        return Paths.get(s).toAbsolutePath();
    }

    @Override
    public boolean getAllPossibleValues(List<Completion> list, Class<?> aClass,
            String s, String s1, MethodTarget methodTarget) {
        return false;
    }
}
