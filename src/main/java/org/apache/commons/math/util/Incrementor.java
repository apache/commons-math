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
package org.apache.commons.math.util;

import org.apache.commons.math.exception.MaxCountExceededException;

/**
 * Utility that increments a counter until a maximum is reached, at which
 * point it will throw an exception.
 *
 * @version $Id$
 * @since 3.0
 */
public class Incrementor {
    /**
     * Upper limit for the counter.
     */
    private int maximalCount;
    /**
     * Current count.
     */
    private int count;

    /**
     * Set the upper limit for the counter.
     *
     * @param max Upper limit of the counter.
     */
    public void setMaximalCount(int max) {
        maximalCount = max;
    }

    /**
     * Get the upper limit of the counter.
     *
     * @return the counter upper limit.
     */
    public int getMaximalCount() {
        return maximalCount;
    }

    /**
     * Get the current count.
     *
     * @return the current count.
     */
    public int getCount() {
        return count;
    }

    /**
     * Perform multiple increments.
     * See the other {@link #incrementCount() incrementCount} method).
     *
     * @param value Number of increments.
     * @throws MaxCountExceededException at counter exhaustion.
     */
    public void incrementCount(int value) {
        for (int i = 0; i < value; i++) {
            incrementCount();
        }
    }

    /**
     * Add one to the current iteration count.
     *
     * @throws MaxCountExceededException at counter exhaustion.
     */
    public void incrementCount() {
        if (++count > maximalCount) {
            throw new MaxCountExceededException(maximalCount);
        }
    }

    /**
     * Reset the counter to 0.
     */
    public void resetCount() {
        count = 0;
    }
}
