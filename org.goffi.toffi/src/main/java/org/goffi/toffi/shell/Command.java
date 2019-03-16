/*
 * org.goffi.toffi
 *
 * File Name: Command.java
 *
 * Copyright 2014 Dzhem Riza
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.goffi.toffi.shell.exceptions.CommandException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.shell.core.ExecutionProcessor;
import org.springframework.shell.event.ParseResult;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public abstract class Command implements ExecutionProcessor {

    private static final Logger LOG = LogManager.getLogger(Command.class);

    @Autowired
    @Qualifier("outputLog")
    protected PrintStream log;

    @Override
    public ParseResult beforeInvocation(ParseResult invocationContext) {
        return invocationContext;
    }

    @Override
    public void afterReturningInvocation(ParseResult invocationContext, Object result) {
    }

    @Override
    public void afterThrowingInvocation(ParseResult invocationContext, Throwable thrown) {
        try {
            throw thrown;
        } catch (CommandException e) {
            // This is special exception that prevents printing stack trace
            log.println(e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace(log); // print the stacktrace on the console
            LOG.error(t.getMessage(), t); // print the stacktrace in the logs
        }
    }

    protected String getCurrentTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }
}
