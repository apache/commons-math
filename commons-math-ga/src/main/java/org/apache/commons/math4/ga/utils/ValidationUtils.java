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
package org.apache.commons.math4.ga.utils;

import org.apache.commons.math4.ga.exception.GeneticException;

/**
 * This class contains common validation methods.
 * @since 4.0
 */
public interface ValidationUtils {

    /**
     * Checks for Null value.
     * @param name  alias of the parameter
     * @param value value of the parameter
     */
    static void checkForNull(String name, Object value) {
        if (value == null) {
            throw new GeneticException(GeneticException.NULL_ARGUMENT, name);
        }
    }

    /**
     * Checks for min and max, throws error if min is greater than or equals to max.
     * @param min minimum value
     * @param max maximum value
     */
    static void checkForMinMax(int min, int max) {
        if (min >= max) {
            throw new GeneticException(GeneticException.TOO_LARGE, min, max);
        }
    }

    /**
     * Checks for min and max, throws error if min is greater than or equals to max.
     * @param min minimum value
     * @param max maximum value
     */
    static void checkForMinMax(double min, double max) {
        if (min >= max) {
            throw new GeneticException(GeneticException.TOO_LARGE, min, max);
        }
    }

}
