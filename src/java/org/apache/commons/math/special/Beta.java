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

/**
 * This is a utility class that provides computation methods related to the
 * Gamma family of functions.
 * 
 * @author Brent Worden
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
     * <p>
     * Returns the regularized beta function I(x, a, b).
     * </p>
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
     * <p>
     * Returns the regularized beta function I(x, a, b).
     * </p>
     * 
     * @param x ???
     * @param a ???
     * @param b ???
     * @return the regularized beta function I(x, a, b)
     */
    public static double regularizedBeta(double x, double a, double b, double epsilon) {
        return regularizedBeta(x, a, b, epsilon, Integer.MAX_VALUE);
    }

    /**
     * <p>
     * Returns the regularized beta function I(x, a, b).
     * </p>
     * 
     * @param x ???
     * @param a ???
     * @param b ???
     * @return the regularized beta function I(x, a, b)
     */
    public static double regularizedBeta(double x, double a, double b, int maxIterations) {
        return regularizedBeta(x, a, b, DEFAULT_EPSILON, maxIterations);
    }
    
    /**
     * <p>
     * Returns the regularized beta function I(x, a, b).
     * </p>
     * 
     * <p>
     * The implementation of this method is based on:
     * <ul>
     * <li>
     * <a href="http://mathworld.wolfram.com/RegularizedBetaFunction.html">
     * Regularized Beta Function</a>.</li>
     * <li>
     * <a href="http://mathworld.wolfram.com/IncompleteBetaFunction.html">
     * Incomplete Beta Function</a>.</li>
     * </ul>
     * </p>
     * 
     * @param x ???
     * @param a ???
     * @param b ???
     * @return the regularized beta function I(x, a, b)
     */
    public static double regularizedBeta(double x, final double a, final double b, double epsilon, int maxIterations) {
        double ret;

        if (a <= 0.0) {
            throw new IllegalArgumentException("a must be positive");
        } else if (b <= 0.0) {
            throw new IllegalArgumentException("b must be positive");
        } else if (x < 0.0 || x > 1.0) {
            throw new IllegalArgumentException(
                "x must be between 0.0 and 1.0, inclusive");
        } else if(x == 0.0){
            ret = 0.0;
        } else if(x == 1.0){
            ret = 1.0;
        } else {
            double n = 0.0;
            double an = 1.0 / a;
            double s = an;
            while(Math.abs(an) > epsilon && n < maxIterations){
                n = n + 1.0;
                an = an * (n - b) / n * x / (a + n) * (a + n - 1);
                s = s + an;
            }
            ret = Math.exp(a * Math.log(x) - logBeta(a, b)) * s;
        }

        return ret;
    }

    /**
     * <p>
     * Returns the natural logarithm of the beta function B(a, b).
     * </p>
     *
     * <p> 
     * The implementation of this method is based on:
     * <ul>
     * <li><a href="http://mathworld.wolfram.com/BetaFunction.html">
     * Beta Function</a>, equation (1).</li>
     * </ul>
     * </p>
     * 
     * @param x ???
     * @return log(B(a, b))
     */
    public static double logBeta(double a, double b) {
        double ret;

        if (a <= 0.0) {
            throw new IllegalArgumentException("a must be positive");
        } else if (b <= 0.0) {
            throw new IllegalArgumentException("b must be positive");
        } else {
            ret = Gamma.logGamma(a) + Gamma.logGamma(b)
                - Gamma.logGamma(a + b);
        }

        return ret;
    }
}
