/*
 * org.goffi.moffi
 *
 * File Name: GuiEventProcessor.java
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
package org.goffi.moffi.gui.events;

/**
 * Gui Event Processor
 */
public interface GuiEventProcessor {

    /**
     * Adds a new {@link GuiEventListener}
     *
     * @param listener
     */
    void add(GuiEventListener<? extends GuiEvent> listener);

    /**
     * Remove existing {@link GuiEventListener}
     *
     * @param listener
     */
    void remove(GuiEventListener<? extends GuiEvent> listener);

    /**
     * Fire new GuiEvent
     *
     * @param event
     */
    void fire(GuiEvent event);
}
