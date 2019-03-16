/*
 * org.goffi.core
 *
 * File Name: ExecutorUtils.java
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
package org.goffi.core.concurrent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutorUtils {
    private static final Logger LOG = LogManager.getLogger(ExecutorUtils.class);

    public static final long DEFAULT_TIMEOUT_IN_SECONDS = 10;

    public static void shutdownAndAwaitTermination(ExecutorService pool,
            long timeout, TimeUnit unit) {
        // Code from https://docs.oracle
        // .com/javase/9/docs/api/java/util/concurrent/ExecutorService.html
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(timeout, unit)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(timeout, unit)) {
                    LOG.error("Pool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    public static void shutdownAndAwaitTermination(ExecutorService pool) {
        shutdownAndAwaitTermination(pool, DEFAULT_TIMEOUT_IN_SECONDS,
                TimeUnit.SECONDS);
    }
}
