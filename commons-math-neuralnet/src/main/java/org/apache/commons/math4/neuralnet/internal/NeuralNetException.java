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
package org.apache.commons.math4.neuralnet.internal;

import java.text.MessageFormat;

/**
 * Exception class with constants for frequently used messages.
 */
public class NeuralNetException extends IllegalArgumentException {
    /** Error message for "out of range" condition. */
    public static final String OUT_OF_RANGE = "Number {0} is out of range [{1}, {2}]";
    /** Error message for "not strictly positive" condition. */
    public static final String NOT_STRICTLY_POSITIVE = "Number {0} is not strictly positive";
    /** Error message for "too large" condition. */
    public static final String TOO_LARGE = "Number {0} is larger than {1}";
    /** Error message for "too small" condition. */
    public static final String TOO_SMALL = "Number {0} is smaller than {1}";
    /** Error message for "out of range" condition. */
    public static final String NO_DATA = "No data";
    /** Error message for "size mismatch" condition. */
    public static final String SIZE_MISMATCH = "Size mismatch: {0} != {1}";
    /** Error message for "identifier already used" condition. */
    public static final String ID_IN_USE = "Identifier already in use: {0}";
    /** Error message for "identifier not found" condition. */
    public static final String ID_NOT_FOUND = "Identifier not found: {0}";

    /** Serializable version identifier. */
    private static final long serialVersionUID = 20210515L;

    /**
     * Create an exception where the message is constructed by applying
     * the {@code format()} method from {@code java.text.MessageFormat}.
     *
     * @param message Message format (with replaceable parameters).
     * @param formatArguments Actual arguments to be displayed in the message.
     */
    public NeuralNetException(String message, Object... formatArguments) {
        super(MessageFormat.format(message, formatArguments));
    }
}
