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
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
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
package org.apache.commons.math.special;

import org.apache.commons.math.util.ContinuedFraction;

/**
 * This is a utility class that provides computation methods related to the
 * Beta family of functions.
 * 
 * @version $Revision: 1.8 $ $Date: 2003/07/30 21:58:10 $
 */
public class Beta {
    /** Maximum allowed numerical error. */
    private static final double DEFAULT_EPSILON = 10e-9;

    /**
     * Default constructor.  Prohibit instantiation.
     */
    private Beta() {
        super();
    }

    /**
     * Returns the regularized beta function I(x, a, b).
     * 
     * @param x ???
     * @param a ???
     * @param b ???
     * @return the regularized beta function I(x, a, b)
     */
    public static double regularizedBeta(double x, double a, double b) {
        return regularizedBeta(x, a, b, DEFAULT_EPSILON, Integer.MAX_VALUE);
    }

    /**
     * Returns the regularized beta function I(x, a, b).
     * 
     * @param x ???
     * @param a ???
     * @param b ???
     * @param epsilon When the absolute value of the nth item in the
     *                series is less than epsilon the approximation ceases
     *                to calculate further elements in the series.
     * @return the regularized beta function I(x, a, b)
     */
    public static double regularizedBeta(double x, double a, double b,
        double epsilon) {
            
        return regularizedBeta(x, a, b, epsilon, Integer.MAX_VALUE);
    }

    /**
     * Returns the regularized beta function I(x, a, b).
     * 
     * @param x ???
     * @param a ???
     * @param b ???
     * @param maxIterations Maximum number of "iterations" to complete. 
     * @return the regularized beta function I(x, a, b)
     */
    public static double regularizedBeta(double x, double a, double b,
        int maxIterations) {
            
        return regularizedBeta(x, a, b, DEFAULT_EPSILON, maxIterations);
    }
    
    /**
     * Returns the regularized beta function I(x, a, b).
     * 
     * The implementation of this method is based on:
     * <ul>
     * <li>
     * <a href="http://mathworld.wolfram.com/RegularizedBetaFunction.html">
     * Regularized Beta Function</a>.</li>
     * <li>
     * <a href="http://functions.wolfram.com/06.21.10.0001.01">
     * Regularized Beta Function</a>.</li>
     * </ul>
     * 
     * @param x ???
     * @param a ???
     * @param b ???
     * @param epsilon When the absolute value of the nth item in the
     *                series is less than epsilon the approximation ceases
     *                to calculate further elements in the series.
     * @param maxIterations Maximum number of "iterations" to complete. 
     * @return the regularized beta function I(x, a, b)
     */
    public static double regularizedBeta(double x, final double a,
        final double b, double epsilon, int maxIterations) {
            
        double ret;

        if (Double.isNaN(x) || Double.isNaN(a) || Double.isNaN(b) || (x < 0) ||
            (x > 1) || (a <= 0.0) || (b <= 0.0)) {
            ret = Double.NaN;
        } else {
            ContinuedFraction fraction = new ContinuedFraction() {
                protected double getB(int n, double x) {
                    double ret;
                    double m;
                    switch (n) {
                        case 1 :
                            ret = 1.0;
                            break;
                        default :
                            if (n % 2 == 0) { // even
                                m = (n - 2.0) / 2.0;
                                ret = -((a + m) * (a + b + m) * x) /
                                    ((a + (2 * m)) * (a + (2 * m) + 1.0));
                            } else {
                                m = (n - 1.0) / 2.0;
                                ret = (m * (b - m) * x) /
                                    ((a + (2 * m) - 1) * (a + (2 * m)));
                            }
                            break;
                    }
                    return ret;
                }

                protected double getA(int n, double x) {
                    double ret;
                    switch (n) {
                        case 0 :
                            ret = 0.0;
                            break;
                        default :
                            ret = 1.0;
                            break;
                    }
                    return ret;
                }
            };
            ret = Math.exp((a * Math.log(x)) + (b * Math.log(1.0 - x)) -
                Math.log(a) - logBeta(a, b, epsilon, maxIterations)) *
                fraction.evaluate(x, epsilon, maxIterations);
        }

        return ret;
    }

    /**
     * Returns the natural logarithm of the beta function B(a, b).
     * 
     * @param a ???
     * @param b ???
     * @return log(B(a, b))
     */
    public static double logBeta(double a, double b) {
        return logBeta(a, b, DEFAULT_EPSILON, Integer.MAX_VALUE);
    }
    
    /**
     * Returns the natural logarithm of the beta function B(a, b).
     *
     * The implementation of this method is based on:
     * <ul>
     * <li><a href="http://mathworld.wolfram.com/BetaFunction.html">
     * Beta Function</a>, equation (1).</li>
     * </ul>
     * 
     * @param a ???
     * @param b ???
     * @param epsilon When the absolute value of the nth item in the
     *                series is less than epsilon the approximation ceases
     *                to calculate further elements in the series.
     * @param maxIterations Maximum number of "iterations" to complete. 
     * @return log(B(a, b))
     */
    public static double logBeta(double a, double b, double epsilon,
        int maxIterations) {
            
        double ret;

        if (Double.isNaN(a) || Double.isNaN(b) || (a <= 0.0) || (b <= 0.0)) {
            ret = Double.NaN;
        } else {
            ret = Gamma.logGamma(a) + Gamma.logGamma(b) -
                Gamma.logGamma(a + b);
        }

        return ret;
    }
}
