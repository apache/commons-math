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
package org.apache.commons.math4.legacy.analysis.interpolation;

import org.apache.commons.math4.legacy.analysis.polynomials.PolynomialFunctionLagrangeForm;
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.NonMonotonicSequenceException;
import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;

/**
 * Implements the <a href="http://mathworld.wolfram.com/NevillesAlgorithm.html">
 * Neville's Algorithm</a> for interpolation of real univariate functions. For
 * reference, see <b>Introduction to Numerical Analysis</b>, ISBN 038795452X,
 * chapter 2.
 * <p>
 * The actual code of Neville's algorithm is in PolynomialFunctionLagrangeForm,
 * this class provides an easy-to-use interface to it.</p>
 *
 * @since 1.2
 */
public class NevilleInterpolator implements UnivariateInterpolator {
    /**
     * Computes an interpolating function for the data set.
     *
     * @param x Interpolating points.
     * @param y Interpolating values.
     * @return a function which interpolates the data set
     * @throws DimensionMismatchException if the array lengths are different.
     * @throws NumberIsTooSmallException if the number of points is less than 2.
     * @throws NonMonotonicSequenceException if two abscissae have the same
     * value.
     */
    @Override
    public PolynomialFunctionLagrangeForm interpolate(double[] x, double[] y)
        throws DimensionMismatchException,
               NumberIsTooSmallException,
               NonMonotonicSequenceException {
        return new PolynomialFunctionLagrangeForm(x, y);
    }
}
