/*
 * 
 * Copyright (c) 2004 The Apache Software Foundation. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *  
 */
package org.apache.commons.math.analysis;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.math.MathException;

/**
 * Represents a cubic spline function.
 * Spline functions map a certain interval of real numbers to real numbers.
 * A cubic spline consists of segments of cubic functions. For this class,
 * polynominal coefficents are used.
 * Arguments outside of the domain cause an IllegalArgumentException.
 * 
 * @version $Revision: 1.11 $ $Date: 2004/01/29 16:48:49 $
 */
public class CubicSplineFunction implements UnivariateRealFunction, Serializable {
    /** Spline segment interval delimiters. Size is N+1 for N segments. */
    private double xval[];

    /**
     * The spline segment's polynominal coefficients.
     * The first index runs over the intervals, size is N.
     * The second index adresses the coefficients in the segment, with
     * index 0 being the absolute coefficient and index 3 the coefficient
     * for the third power.
     * The coefficients are setup so that x runs from 0 to xval[i+1]-xval[i].
     */
    private double c[][];

    /**
     * Construct a function with the given segment delimiters and polynomial
     * coefficients.
     * @param xval Spline segment interval delimiters
     * @param c spline segment's polynominal coefficients
     */
    public CubicSplineFunction(double xval[], double c[][]) {
        super();
        // TODO: should copy the arguments here, for safety. This could be a major overhead.
        this.xval = xval;
        this.c = c;
    }

    /**
     * Compute the value for the function.
     * @param x the point for which the function value should be computed
     * @return the value
     * @throws MathException if the function couldn't be computed due to
     *  missing additional data or other environmental problems.
     * @see UnivariateRealFunction#value(double)
     */
    public double value(double x) throws MathException {
        if (x < xval[0] || x > xval[xval.length - 1]) {
            throw new IllegalArgumentException("Argument outside domain");
        }
        int i = Arrays.binarySearch(xval, x);
        if (i < 0) {
            i = -i - 2;
        }
        x = x - xval[i];
        return ((c[i][3] * x + c[i][2]) * x + c[i][1]) * x + c[i][0];
    }

    /**
     * Compute the value for the first derivative of the function.
     * It is recommended to provide this method only if the first derivative is
     * analytical. Numerical derivatives may be acceptable in some cases.
     * An implementation should throw an UnsupportedOperationException if
     * this method is not implemented.
     * @param x the point for which the first derivative should be computed
     * @return the value
     * @throws MathException if the derivative couldn't be computed.
     */
    public double firstDerivative(double x) throws MathException {
        if (x < xval[0] || x > xval[xval.length - 1]) {
            throw new IllegalArgumentException("Argument outside domain");
        }
        int i = Arrays.binarySearch(xval, x);
        if (i < 0) {
            i = -i - 2;
        }
        x = x - xval[i];
        return (3 * c[i][3] * x + 2 * c[i][2]) * x + c[i][1];
    }

    /**
     * Compute the value for the second derivative of the function.
     * It is recommended to provide this method only if the second derivative is
     * analytical. Numerical derivatives may be acceptable in some cases.
     * An implementation should throw an UnsupportedOperationException if
     * this method is not implemented.
     * @param x the point for which the first derivative should be computed
     * @return the value
     * @throws MathException if the second derivative couldn't be computed.
     */
    public double secondDerivative(double x) throws MathException {
        if (x < xval[0] || x > xval[xval.length - 1]) {
            throw new IllegalArgumentException("Argument outside domain");
        }
        int i = Arrays.binarySearch(xval, x);
        if (i < 0) {
            i = -i - 2;
        }
        x = x - xval[i];
        return 6 * c[i][3] * x + 2 * c[i][2];
    }

}
