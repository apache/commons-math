/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.commons.math.analysis;

import java.util.Arrays;

import org.apache.commons.math.MathException;

/**
 * Represents a cubic spline function.
 * Spline functions map a certain interval of real numbers to real numbers.
 * A cubic spline consists of segments of cubic functions. For this class,
 * polynominal coefficents are used.
 * Arguments outside of the domain cause an IllegalArgumentException.
 * 
 * @version $Revision: 1.4 $ $Date: 2003/10/13 08:09:31 $
 */
public class CubicSplineFunction implements UnivariateRealFunction {
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
     * @see UnivariateRealFunction#firstDerivative(double)
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
     * @see UnivariateRealFunction#secondDerivative(double)
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
