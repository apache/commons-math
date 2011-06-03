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

package org.apache.commons.math.optimization.fitting;

import org.apache.commons.math.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math.optimization.DifferentiableMultivariateVectorialOptimizer;

/** This class implements a curve fitting specialized for polynomials.
 * <p>Polynomial fitting is a very simple case of curve fitting. The
 * estimated coefficients are the polynomial coefficients. They are
 * searched by a least square estimator.</p>
 * @version $Id$
 * @since 2.0
 */

public class PolynomialFitter extends CurveFitter {
    /** Polynomial degree. */
    private final int degree;

    /**
     * Simple constructor.
     * <p>The polynomial fitter built this way are complete polynomials,
     * ie. a n-degree polynomial has n+1 coefficients.</p>
     *
     * @param degree Maximal degree of the polynomial.
     * @param optimizer Optimizer to use for the fitting.
     */
    public PolynomialFitter(int degree, final DifferentiableMultivariateVectorialOptimizer optimizer) {
        super(optimizer);
        this.degree = degree;
    }

    /**
     * Get the polynomial fitting the weighted (x, y) points.
     *
     * @return the coefficients of the polynomial that best fits the observed points.
     * @throws org.apache.commons.math.exception.ConvergenceException
     * if the algorithm failed to converge.
     */
    public double[] fit() {
        return fit(new PolynomialFunction.Parametric(), new double[degree + 1]);
    }
}
