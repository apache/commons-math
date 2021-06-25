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
package org.apache.commons.math4.transform;

import java.text.MessageFormat;

/**
 * Exception class with constants for frequently used messages.
 * Class is package-private (for internal use only).
 */
class TransformException extends IllegalArgumentException {
    /** Error message for "out of range" condition. */
    public static final String FIRST_ELEMENT_NOT_ZERO = "First element ({0}) must be 0";
    /** Error message for "not strictly positive" condition. */
    public static final String NOT_STRICTLY_POSITIVE = "Number {0} is not strictly positive";
    /** Error message for "too large" condition. */
    public static final String TOO_LARGE = "Number {0} is larger than {1}";
    /** Error message for "size mismatch" condition. */
    public static final String SIZE_MISMATCH = "Size mismatch: {0} != {1}";
    /** Error message for "pow(2, n) + 1". */
    public static final String NOT_POWER_OF_TWO_PLUS_ONE = "{0} is not equal to 1 + pow(2, n), for some n";
    /** Error message for "pow(2, n)". */
    public static final String NOT_POWER_OF_TWO = "{0} is not equal to pow(2, n), for some n";

    /** Serializable version identifier. */
    private static final long serialVersionUID = 20210522L;

    /**
     * Create an exception where the message is constructed by applying
     * the {@code format()} method from {@code java.text.MessageFormat}.
     *
     * @param message Message format (with replaceable parameters).
     * @param formatArguments Actual arguments to be displayed in the message.
     */
    TransformException(String message, Object... formatArguments) {
        super(MessageFormat.format(message, formatArguments));
    }
}
