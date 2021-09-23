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

package org.apache.commons.math4.genetics.exception;

import java.text.MessageFormat;

/**
 * This class represents the Exception encountered during GA optimization.
 */
public class GeneticException extends RuntimeException {

    /** Error message for "out of range" condition. */
    public static final String OUT_OF_RANGE = "Value {0} of {1} is out of range [{2}, {3}]";

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

    /** Error message for "generic illegal argument" condition. */
    public static final String ILLEGAL_ARGUMENT = "Illegal Argument Exception: {0}";

    /** Error message for "generic illegal argument" condition. */
    public static final String ILLEGAL_RANGE = "Illegal Range of Value Exception: " +
            "[Expected min-{0}, max-{1}], [Passed min-{2}, max-{3}]";

    /** Error message for "generic illegal argument" condition. */
    public static final String INVALID_FIXED_LENGTH_CHROMOSOME = "Invalid Fixed Length Chromosome.";

    /** Error message for "NULL ARGUMENT" condition. */
    public static final String NULL_ARGUMENT = "Null Argument Exception: {0}";

    /**
     * Error message for "List of Chromosome bigger than population size" condition.
     */
    public static final String LIST_OF_CHROMOSOMES_BIGGER_THAN_POPULATION_SIZE = "List of chromosome bigger than " +
            "population size: {0} > {1}";

    /**
     * Error message for "population limit not positive" condition.
     */
    public static final String POPULATION_LIMIT_NOT_POSITIVE = "Population limit not positive :{0}";

    /**
     * Error message for " population limit less than list of chromosomes size"
     * condition.
     */
    public static final String POPULATION_LIMIT_LESS_THAN_LIST_OF_CHROMOSOMES_SIZE = "Population limit is " +
            " lesser than list of chromosomes size : {0} < {1}";

    /**
     * Error message for different origin and permuted data.
     */
    public static final String DIFFERENT_ORIG_AND_PERMUTED_DATA = "Different original and permuted data";

    /** Serializable version identifier. */
    private static final long serialVersionUID = 20210516L;

    /**
     * Create an exception where the message is constructed by applying the
     * {@code format()} method from {@code java.text.MessageFormat}.
     *
     * @param message         Message format (with replaceable parameters).
     * @param formatArguments Actual arguments to be displayed in the message.
     */
    public GeneticException(String message, Object... formatArguments) {
        super(MessageFormat.format(message, formatArguments));
    }

    /**
     * Create an exception.
     * @param t instance of {@link Throwable}
     */
    public GeneticException(Throwable t) {
        super(t);
    }

    /**
     * Create an exception having both stacktrace and message.
     * @param message         the exception message
     * @param t               the instance of {@link Throwable}
     * @param formatArguments arguments to format the exception message
     */
    public GeneticException(String message, Throwable t, Object... formatArguments) {
        super(MessageFormat.format(message, formatArguments), t);
    }

}
