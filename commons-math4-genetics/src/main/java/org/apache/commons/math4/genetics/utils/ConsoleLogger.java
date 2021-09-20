/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math4.genetics.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.commons.math4.genetics.exception.GeneticException;

/**
 * This class is responsible for logging messages to console.
 */
public final class ConsoleLogger {

    /** writer instance to log messages to system console. **/
    private static final BufferedWriter WRITER = new BufferedWriter(new OutputStreamWriter(System.out));

    static {

        // Create a shutdown hook to close the writer.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                WRITER.close();
            } catch (IOException e) {
                throw new GeneticException(e);
            }
        }));
    }

    /**
     * constructor.
     */
    private ConsoleLogger() {
    }

    /**
     * Logs a message.
     * @param message
     */
    public static void log(String message) {
        try {
            WRITER.write(message);
            WRITER.newLine();
            WRITER.flush();
        } catch (IOException e) {
            throw new GeneticException(e);
        }
    }

    /**
     * Logs the message after formatting with the args.
     * @param message
     * @param args
     */
    public static void log(String message, Object... args) {
        try {
            WRITER.write(String.format(message, args));
            WRITER.newLine();
            WRITER.flush();
        } catch (IOException e) {
            throw new GeneticException(e);
        }
    }

}
