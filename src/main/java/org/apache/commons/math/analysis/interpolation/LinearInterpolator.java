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
package org.apache.commons.math.analysis.interpolation;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math.util.LocalizedFormats;

/**
 * Implements a linear function for interpolation of real univariate functions.
 */
public class LinearInterpolator implements UnivariateRealInterpolator {
    /**
     * Computes a linear interpolating function for the data set.
     * @param x the arguments for the interpolation points
     * @param y the values for the interpolation points
     * @return a function which interpolates the data set
    */
    public PolynomialSplineFunction interpolate(double x[], double y[]) {
        if (x.length != y.length) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, x.length, y.length);
        }

        if (x.length < 2) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.WRONG_NUMBER_OF_POINTS, 2, x.length);
        }

        // Number of intervals.  The number of data points is n + 1.
        int n = x.length - 1;

        for (int i = 0; i < n; i++) {
            if (x[i] >= x[i + 1]) {
                throw MathRuntimeException.createIllegalArgumentException(
                LocalizedFormats.NOT_STRICTLY_INCREASING_NUMBER_OF_POINTS,
                i, i+1, x[i], x[i+1]);
            }
        }

        // Slope of the lines between the datapoints.
        final double m[] = new double[n];
        for (int i = 0; i < n; i++) {
            m[i] = (y[i + 1] - y[i]) / (x[i + 1] - x[i]);
        }

        PolynomialFunction polynomials[] = new PolynomialFunction[n];
        final double coefficients[] = new double[2];
        for (int i = 0; i < n; i++) {
            coefficients[0] = y[i];
            coefficients[1] = m[i];
            polynomials[i] = new PolynomialFunction(coefficients);
        }

        return new PolynomialSplineFunction(x, polynomials);
    }

}
