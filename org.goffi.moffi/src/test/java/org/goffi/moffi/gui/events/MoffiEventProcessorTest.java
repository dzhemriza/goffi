/*
 * org.goffi.moffi
 *
 * File Name: MoffiEventProcessorTest.java
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class MoffiEventProcessorTest {

    private MoffiEventProcessor eventProcessor;

    @Before
    public void setup() {
        eventProcessor = new MoffiEventProcessor();
    }

    class TestEvent1 extends AbstractGuiEvent {
        TestEvent1(Object sender) {
            super(sender);
        }
    }

    class TestEvent2 extends AbstractGuiEvent {
        TestEvent2(Object sender) {
            super(sender);
        }
    }

    class TestEvent3 extends AbstractGuiEvent {
        TestEvent3(Object sender) {
            super(sender);
        }
    }

    class TestEvent4 extends AbstractGuiEvent {
        TestEvent4(Object sender) {
            super(sender);
        }
    }

    class TestEvent5 extends AbstractGuiEvent {
        TestEvent5(Object sender) {
            super(sender);
        }
    }

    @Test
    public void testRegisterListener() {
        AtomicInteger count = new AtomicInteger(0);

        eventProcessor.add((e) -> count.incrementAndGet());

        eventProcessor.fire(new TestEvent1(this));

        Assert.assertEquals(1, count.get());
    }

    @Test
    public void testRegisterMultipleListeners() {
        int[] firedEvents = new int[]{0, 0, 0, 0, 0, 0};

        // General case all events with root class GuiEvent
        eventProcessor.add((e) -> firedEvents[0]++);

        // TestEvent1 only listener
        eventProcessor.add((TestEvent1 e) -> firedEvents[1]++);

        // TestEvent2 only listener
        eventProcessor.add((TestEvent2 e) -> firedEvents[2]++);

        // TestEvent3 only listener
        eventProcessor.add((TestEvent3 e) -> firedEvents[3]++);

        // TestEvent4 only listener
        eventProcessor.add((TestEvent4 e) -> firedEvents[4]++);

        // TestEvent5 only listener
        eventProcessor.add((TestEvent5 e) -> firedEvents[5]++);

        eventProcessor.fire(new TestEvent1(this));
        eventProcessor.fire(new TestEvent2(this));
        eventProcessor.fire(new TestEvent3(this));
        eventProcessor.fire(new TestEvent4(this));

        Assert.assertArrayEquals(new int[] {4, 1, 1, 1, 1, 0}, firedEvents);
    }
}
