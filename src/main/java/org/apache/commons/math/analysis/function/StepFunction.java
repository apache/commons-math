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

package org.apache.commons.math.analysis.function;

import java.util.Arrays;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.exception.NoDataException;
import org.apache.commons.math.util.MathUtils;

/**
 * <a href="http://en.wikipedia.org/wiki/Step_function">
 *  Step function</a>.
 *
 * @version $Id$
 * @since 3.0
 */
public class StepFunction implements UnivariateRealFunction {
    /** Abscissae. */
    private final double[] abscissa;
    /** Ordinates. */
    private final double[] ordinate;

    /**
     * Builds a step function from a list of abscissae and the corresponding
     * ordinates.
     *
     * @param x Abscissae.
     * @param y Ordinates.
     * @throws org.apache.commons.math.exception.NonMonotonousSequenceException
     * if the {@code x} array is not sorted in strictly increasing order.
     * @throws NullArgumentException if {@code x} or {@code y} are {@code null}.
     * @throws NoDataException if {@code x} or {@code y} are zero-length.
     */
    public StepFunction(double[] x,
                        double[] y) {
        if (x == null ||
            y == null) {
            throw new NullArgumentException();
        }
        if (x.length == 0 ||
            y.length == 0) {
            throw new NoDataException();
        }
        if (y.length != x.length) {
            throw new DimensionMismatchException(y.length, x.length);
        }
        MathUtils.checkOrder(x);

        abscissa = MathUtils.copyOf(x);
        ordinate = MathUtils.copyOf(y);
    }

    /** {@inheritDoc} */
    public double value(double x) {
        int index = Arrays.binarySearch(abscissa, x);
        double fx = 0;

        if (index < -1) {
            // "x" is between "abscissa[-index-2]" and "abscissa[-index-1]".
            fx = ordinate[-index-2];
        } else if (index >= 0) {
            // "x" is exactly "abscissa[index]".
            fx = ordinate[index];
        } else {
            // Otherwise, "x" is smaller than the first value in "abscissa"
            // (hence the returned value should be "ordinate[0]").
            fx = ordinate[0];
        }

        return fx;
    }
}
