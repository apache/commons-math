/*
 * Copyright 2003-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.analysis;

import java.io.Serializable;
import org.apache.commons.math.MathException;

/**
 * Implements the <a href="http://mathworld.wolfram.com/NevillesAlgorithm.html">
 * Neville's Algorithm</a> for interpolation of real univariate functions. For
 * reference, see <b>Introduction to Numerical Analysis</b>, ISBN 038795452X,
 * chapter 2.
 * <p>
 * The actual code of Neville's evalution is in PolynomialFunctionLagrangeForm,
 * this class provides an easy-to-use interface to it.
 *
 * @version $Revision$ $Date$
 */
public class NevilleInterpolator implements UnivariateRealInterpolator,
    Serializable {

    /** serializable version identifier */
    static final long serialVersionUID = 3003707660147873733L;

    /**
     * Computes an interpolating function for the data set.
     *
     * @param x the interpolating points array
     * @param y the interpolating values array
     * @return a function which interpolates the data set
     * @throws MathException if arguments are invalid
     */
    public UnivariateRealFunction interpolate(double x[], double y[]) throws
        MathException {

        PolynomialFunctionLagrangeForm p;
        p = new PolynomialFunctionLagrangeForm(x, y);
        return p;
    }
}
