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

import org.apache.commons.math.ConvergenceException;

/**
 * This is a utility class that provides computation methods related to the
 * Gamma family of functions.
 * 
 * @author Brent Worden
 */
public class Gamma {
    /** Maximum number of iteration allowed for iterative methods. */
    private static final int MAXIMUM_ITERATIONS = 100;

    /** Maximum allowed numerical error. */
    private static final double EPSILON = 10e-9;

    /**
     * Default constructor.  Prohibit instantiation.
     */
    private Gamma() {
        super();
    }

    /**
     * Returns the regularized gamma function P(a, x) defined by
     * http://mathworld.wolfram.com/RegularizedGammaFunction.html. 
     *
     * This implementation is based on the formulas and descriptions presented
     * in Press, et. al. "Numerical Recipes in C."  This implementation uses
     * the series representation of the regularized gamma function for all
     * values a and x.  Later on, this method can be improved upon for certain
     * values of a and x using continuous fractions as the series converges
     * slowly for x >> a.
     * 
     * @param a ???
     * @param x ???
     * @return the regularized gamma function P(a, x)
     */
    public static double regularizedGammaP(double a, double x) {
        double ret;

        if (a <= 0.0) {
            throw new IllegalArgumentException("a must be positive");
        } else if (x <= 0.0) {
            throw new IllegalArgumentException("x must be non-negative");
        } else {
            // calculate series
            double n = 0.0; // current element index
            double an = 1.0 / a; // n-th element in the series
            double sum = an; // partial sum
            while (Math.abs(an) > EPSILON && n < MAXIMUM_ITERATIONS) {
                // compute next element in the series
                n = n + 1.0;
                an = an * (x / (a + n));

                // update partial sum
                sum = sum + an;
            }
            if (n >= MAXIMUM_ITERATIONS) {
                throw new ConvergenceException(
                    "maximum number of iterations reached");
            } else {
                ret = Math.exp(-x + (a * Math.log(x)) - logGamma(a)) * sum;
            }
        }

        return ret;
    }

    /**
     * Returns the natural logarithm of the gamma function &#915;(x) defined
     * by http://mathworld.wolfram.com/GammaFunction.html
     *
     * This implementation is based on the formulas and descriptions presented
     * in Press, et. al. "Numerical Recipes in C" and the Lanczos coefficient
     * research conducted by Paul Godfrey.
     * 
     * @param x ???
     * @return log(&#915;(x))
     */
    public static double logGamma(double x) {
        double ret;

        if (x <= 0.0) {
            throw new IllegalArgumentException(
                "x must be non-negative");
        } else {
            double g = 607.0 / 128.0;

            // Lanczos coefficients
            double[] c =
                {
                    0.99999999999999709182,
                    57.156235665862923517,
                    -59.597960355475491248,
                    14.136097974741747174,
                    -0.49191381609762019978,
                    .33994649984811888699e-4,
                    .46523628927048575665e-4,
                    -.98374475304879564677e-4,
                    .15808870322491248884e-3,
                    -.21026444172410488319e-3,
                    .21743961811521264320e-3,
                    -.16431810653676389022e-3,
                    .84418223983852743293e-4,
                    -.26190838401581408670e-4,
                    .36899182659531622704e-5,
                    };

            double sum = 0.0;
            for (int i = 1; i < c.length; ++i) {
                sum = sum + (c[i] / (x + i));
            }
            sum = sum + c[0];

            double tmp = x + g + .5;
            ret =
                (x + .5) * Math.log(tmp)
                    - tmp
                    + (.5 * Math.log(2.0 * Math.PI))
                    + Math.log(sum / x);
        }

        return ret;
    }
}
