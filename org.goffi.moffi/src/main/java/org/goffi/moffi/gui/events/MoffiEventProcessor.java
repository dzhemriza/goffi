/*
 * org.goffi.moffi
 *
 * File Name: MoffiEventProcessor.java
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

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of {@link GuiEventProcessor}
 */
public class MoffiEventProcessor implements GuiEventProcessor {

	private final List<GuiEventListener<? extends GuiEvent>> listeners = new ArrayList<>();

	@Override
	public void add(GuiEventListener<? extends GuiEvent> listener) {
		listeners.add(listener);
	}

	@Override
	public void remove(GuiEventListener<? extends GuiEvent> listener) {
		listeners.remove(listener);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void fire(GuiEvent event) {
		for (GuiEventListener eventListener : listeners) {
			try {
				eventListener.handle(event);
			} catch (ClassCastException e) {
				// If java is unable to call the generic EventListener<E extends
				// Event>.action it will throw
				// ClassCastException, in this case we simply ignore this error
				// and try to call the next listener.
			}
		}
	}
}
