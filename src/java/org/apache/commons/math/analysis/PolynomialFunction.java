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
 *    nor may "Apache" appear in their name without prior written
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



import java.io.Serializable;

import org.apache.commons.math.MathException;

/**
 * Represents a Polynomial function.
 * Spline functions map a certain interval of real numbers to real numbers.
 * A cubic spline consists of segments of cubic functions. For this class,
 * polynominal coefficents are used.
 * Arguments outside of the domain cause an IllegalArgumentException.
 * 
 * @version $Revision: 1.2 $ $Date: 2003/11/19 03:28:23 $
 */
public class PolynomialFunction implements UnivariateRealFunction, Serializable {

    /**
     * The polynominal coefficients.
     * The index represents the coefficients of the polynomail, with
     * index 0 being the absolute coefficient and index N the coefficient
     * for the Nth power.
     */
    private double c[];

    /**
     * Construct a function with the given segment delimiters and polynomial
     * coefficients.
     * @param c polynominal coefficients
     */
    public PolynomialFunction(double c[]) {
        super();
        // TODO: should copy the arguments here, for safety. This could be a major overhead.
        this.c = c;
    }

    /**
     * Compute the value for the function.
     *
     * <p>This can be explicitly determined by 
     *   <tt>c_n * x^n + ... + c_1 * x  + c_0</tt>
     * </p>
     *
     * @param x the point for which the function value should be computed
     * @return the value
     * @throws MathException if the function couldn't be computed due to
     *  missing additional data or other environmental problems.
     * @see UnivariateRealFunction#value(double)
     */
    public double value(double x) throws MathException {

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
     * @throws MathException if the derivative couldn't be computed.
     */
    public double firstDerivative(double x) throws MathException {

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
     * @throws MathException if the second derivative couldn't be computed.
     */
    public double secondDerivative(double x) throws MathException {

        double value = 2.0 * c[2];

        if ( c.length > 2 ) {
            for (int i=3; i < c.length; i++ ) {
                value += i * (i-1) * c[i] * Math.pow( x, (int)i-2);
            }
        }

        return value;
    }


    /** 
     * local power function using integer powers.
     * <p>The Math.pow() function always returns absolute value,
     *   and is a bit 'heavier' since it can handle double values
     *   for the exponential value.</p>
     * @param x any double value
     * @param n must be 0 or greater 
     * @return x^n (or 0 if n < 0 ).
     * @throws MathException if n < 0.
     */
//     private double pow( double x, int n ) throws MathException {
//         double value = x;
//         if ( n < 0 ) {
//             throw new MathException( "power n must be 0 or greater" );
//         } else if ( n == 0 ) {
//             // x^0 = 1 always.
//             value = 1.0;
//         } else {
//             // only multiply for powers > 1.
//             for (int i=1; i < n; i++) {
//                 value *= x;
//             }
//         }

//         System.out.println("pow:"+x+"^"+n+"="+value);
//         return value;
//     }

}
