/*
 * 
 * Copyright (c) 2003-2004 The Apache Software Foundation. All rights reserved.
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

/**
 * Represents a polynomial function with real coefficients.
 * 
 * @version $Revision: 1.7 $ $Date: 2004/02/22 22:01:29 $
 */
public class PolynomialFunction implements UnivariateRealFunction, Serializable {

    /**
     * The coefficients of the polynomial, ordered by degree -- i.e.,  c[0] is the constant term
     * and c[n] is the coefficient of x^n where n is the degree of the polynomial.
     */
    private double c[];

    /**
     * Construct a polynomial with the given coefficients
     * 
     * @param c polynominal coefficients
     */
    public PolynomialFunction(double c[]) {
        super();
        this.c = new double[c.length];
        System.arraycopy(c, 0, this.c, 0, c.length);
    }

    /**
     * Compute the value of the function for the given argument.
     *
     * <p>This can be explicitly determined by 
     *   <tt>c_n * x^n + ... + c_1 * x  + c_0</tt>
     * </p>
     *
     * @param x the argument for which the function value should be computed
     * @return the value
     * @throws MathException if the function couldn't be computed due to
     *  missing additional data or other environmental problems.
     * @see UnivariateRealFunction#value(double)
     */
    public double value(double x)  {

        double value = c[0];

        for (int i=1; i < c.length; i++ ) {
            value += c[i] * Math.pow( x, (int)i);
        }

        return value;
    }


    /**
     * Compute the value for the first derivative of the function.
     *
     * <p>This can be explicitly determined by 
     *   <tt>n * c_n * x^(n-1) + ... + 2 * c_2 * x  + c_1</tt>
     * </p>
     *
     * @param x the point for which the first derivative should be computed
     * @return the value
     */
    public double firstDerivative(double x)  {

        if (this.degree() == 0) {
            return 0;
        }
        double value = c[1];

        if ( c.length > 1 ) {
            for (int i=2; i < c.length; i++ ) {
                value += i * c[i] * Math.pow( x, (int)i-1);
            }
        }

        return value;
    }

    /**
     * Compute the value for the second derivative of the function.
     * 
     * <p>This can be explicitly determined by 
     *   <tt>n * (n-1) * c_n * x^(n-2) + ... + 3 * 2 * c_3 * x  + 2 * c_2</tt>
     * </p>
     * 
     * @param x the point for which the first derivative should be computed
     * @return the value
     */
    public double secondDerivative(double x)  {

        if (this.degree() < 2) {
            return 0;
        }
        double value = 2.0 * c[2];

        if ( c.length > 2 ) {
            for (int i=3; i < c.length; i++ ) {
                value += i * (i-1) * c[i] * Math.pow( x, (int)i-2);
            }
        }

        return value;
    }

    /**
     *  Returns the degree of the polynomial
     * 
     * @return the degree of the polynomial
     */
    public int degree() {
        return c.length - 1;
    }
}
