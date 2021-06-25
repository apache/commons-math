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
package org.apache.commons.math4.legacy.fitting;

import org.apache.commons.math4.legacy.analysis.polynomials.PolynomialFunction;

/**
 * Fits points to a {@link
 * org.apache.commons.math4.legacy.analysis.polynomials.PolynomialFunction.Parametric polynomial}
 * function.
 * <br>
 * The size of the {@link #withStartPoint(double[]) initial guess} array defines the
 * degree of the polynomial to be fitted.
 * They must be sorted in increasing order of the polynomial's degree.
 * The optimal values of the coefficients will be returned in the same order.
 *
 * @since 3.3
 */
public final class PolynomialCurveFitter extends SimpleCurveFitter {
    /** Parametric function to be fitted. */
    private static final PolynomialFunction.Parametric FUNCTION = new PolynomialFunction.Parametric();

    /**
     * Constructor used by the factory methods.
     *
     * @param initialGuess Initial guess.
     * @param maxIter Maximum number of iterations of the optimization algorithm.
     */
    private PolynomialCurveFitter(double[] initialGuess,
                                  int maxIter) {
        super(FUNCTION, initialGuess, null, maxIter);
    }

    /**
     * Creates a default curve fitter.
     * Zero will be used as initial guess for the coefficients, and the maximum
     * number of iterations of the optimization algorithm is set to
     * {@link Integer#MAX_VALUE}.
     *
     * @param degree Degree of the polynomial to be fitted.
     * @return a curve fitter.
     *
     * @see #withStartPoint(double[])
     * @see #withMaxIterations(int)
     */
    public static PolynomialCurveFitter create(int degree) {
        return new PolynomialCurveFitter(new double[degree + 1], Integer.MAX_VALUE);
    }
}
