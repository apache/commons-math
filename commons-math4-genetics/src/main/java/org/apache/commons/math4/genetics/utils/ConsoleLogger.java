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
import java.io.UnsupportedEncodingException;

import org.apache.commons.math4.genetics.exception.GeneticException;

/**
 * This class is responsible for logging messages to console.
 */
public final class ConsoleLogger {

    /** instance of ConsoleLogger. **/
    private static volatile ConsoleLogger instance;

    /** writer instance to log messages to system console. **/
    private final BufferedWriter writer;

    /**
     * constructor.
     * @param encoding
     */
    private ConsoleLogger(String encoding) {
        try {
            writer = new BufferedWriter(new OutputStreamWriter(System.out, encoding));

            // Create a shutdown hook to close the writer.
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    writer.close();
                } catch (IOException e) {
                    throw new GeneticException(e);
                }
            }));
        } catch (UnsupportedEncodingException e1) {
            throw new GeneticException(e1);
        }
    }

    /**
     * Logs a message.
     * @param message message to log
     */
    public void log(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new GeneticException(e);
        }
    }

    /**
     * Logs the message after formatting with the args.
     * @param message message to log
     * @param args    args to format the message
     */
    public void log(String message, Object... args) {
        try {
            writer.write(String.format(message, args));
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new GeneticException(e);
        }
    }

    /**
     * Returns the instance of ConsoleLogger.
     * @param encoding encoding to be used with writing
     * @return instance of ConsoleLogger
     */
    public static ConsoleLogger getInstance(final String encoding) {
        ValidationUtils.checkForNull("Encoding of ConsoleLogger", encoding);
        if (instance == null) {
            synchronized (ConsoleLogger.class) {
                if (instance == null) {
                    instance = new ConsoleLogger(encoding);
                }
            }
        }
        return instance;
    }

}
