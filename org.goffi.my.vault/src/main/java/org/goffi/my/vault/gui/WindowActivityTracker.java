/*
 * org.goffi.my.vault
 *
 * File Name: WindowActivityTracker.java
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
package org.goffi.my.vault.gui;

import javafx.stage.Window;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WindowActivityTracker {

    private static long DEFAULT_DELAY = 30_000; // in milliseconds

    public enum WindowState {
        ACTIVE, INACTIVE
    }

    private ScheduledExecutorService scheduledExecutorService;

    /**
     * Keeps track of active/inactive windows
     */
    private Map<Window, WindowState> tracker = new HashMap<>();
    private List<Event> scheduledEvents = new ArrayList<>();
    private boolean inactiveState = false;

    public WindowActivityTracker(
            ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.scheduledExecutorService
                .scheduleWithFixedDelay(this::tick, DEFAULT_DELAY,
                        DEFAULT_DELAY, TimeUnit.MILLISECONDS);
    }

    public synchronized void recordActivity(Window window, boolean active) {
        recordActivity(window, active ?
                WindowState.ACTIVE : WindowState.INACTIVE);
    }

    public synchronized void recordActivity(Window window,
            WindowState windowState) {
        tracker.put(window, windowState);

        inactiveState = false; // Assume we are in active state atm

        // To determinate active/inactive state of the app we just
        // check that all registered windows are inactive

        if (!tracker.isEmpty()) {
            inactiveState = tracker.values().stream()
                    .allMatch(state -> state == WindowState.INACTIVE);
        } // else if it's empty we assume we are in active state

        if (inactiveState) {
            // State is inactive we need to reset all the scheduledEvents
            scheduledEvents.stream().forEach(Event::reset);
        }
    }

    /**
     * Schedules a event represented as {@link Runnable} to be executed only
     * once if the tracker detects inactivity for a specific period of time.
     *
     * @param event
     * @param deadline
     * @param timeUnit
     * @return Event ID represented as {@link UUID}
     */
    public synchronized UUID scheduleEventOnIdle(Runnable event, long deadline,
            TimeUnit timeUnit) {
        Event internalEvent = new Event(event, deadline, timeUnit);
        scheduledEvents.add(internalEvent);
        return internalEvent.getEventId();
    }

    public synchronized boolean cancelEvent(UUID eventId) {
        return scheduledEvents.removeIf(
                event -> event.getEventId().equals(eventId));
    }

    private synchronized void tick() {
        try {
            if (!inactiveState) {
                // Skip this tick
                return;
            }

            List<Event> readyToFire = new ArrayList<>();

            // Move the expired events to a separate list
            scheduledEvents.stream()
                    .filter(Event::didMeetTheDeadline)
                    .forEach(readyToFire::add);

            // Remove the expired items
            scheduledEvents.removeIf(Event::didMeetTheDeadline);

            // Fire the events that are ready
            readyToFire.forEach(Event::fire);
        } catch (Throwable t) {
            t.printStackTrace(System.out);
        }
    }

    private class Event {

        private final UUID eventId;
        private Runnable runnable;
        private long delay;
        private TimeUnit timeUnit;
        /**
         * Represents the deadline based on the creation of this {@link Event}
         */
        private long deadline;

        public Event(Runnable runnable, long delay, TimeUnit timeUnit) {
            this.eventId = UUID.randomUUID();
            this.delay = delay;
            this.timeUnit = timeUnit;
            this.runnable = runnable;
            reset();
        }

        public UUID getEventId() {
            return eventId;
        }

        public void reset() {
            this.deadline =
                    Instant.now().toEpochMilli() + timeUnit.toMillis(delay);
        }

        public boolean didMeetTheDeadline() {
            return this.deadline <= Instant.now().toEpochMilli();
        }

        public void fire() {
            runnable.run();
        }
    }
}
