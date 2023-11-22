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
package org.apache.commons.math4.legacy.optim.nonlinear.scalar;

import org.apache.commons.math4.legacy.exception.NotStrictlyPositiveException;
import org.apache.commons.math4.legacy.optim.Tolerance;

/**
 * Tolerances for line search.
 *
 * @since 4.0
 */
public class LineSearchTolerance extends Tolerance {
    /** Range. */
    private final double initialBracketingRange;

    /**
     * @param relative Relative tolerance.
     * @param absolute Absolute tolerance.
     * @param range Extent of the initial interval used to find an interval
     * that brackets the optimum.
     * If the optimized function varies a lot in the vicinity of the optimum,
     * it may be necessary to provide a value lower than the distance between
     * successive local minima.
     */
    public LineSearchTolerance(double relative,
                               double absolute,
                               double range) {
        super(relative, absolute);

        if (range <= 0) {
            throw new NotStrictlyPositiveException(range);
        }

        initialBracketingRange = range;
    }

    /** @return the initial bracketing range. */
    public double getInitialBracketingRange() {
        return initialBracketingRange;
    }
}
