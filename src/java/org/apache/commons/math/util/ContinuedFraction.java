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
package org.apache.commons.math.util;

import org.apache.commons.math.analysis.ConvergenceException;

/**
 * Provides a generic means to evaluate continued fractions.  Subclasses simply
 * provided the a and b coefficients to evaluate the continued fraction.
 * 
 * References:
 * <ul>
 * <li><a href="http://mathworld.wolfram.com/ContinuedFraction.html">
 * Continued Fraction</a></li>
 * </ul>
 * @version $Revision: 1.3 $ $Date: 2003/07/09 20:04:12 $
 */
public abstract class ContinuedFraction {
    /** Maximum allowed numerical error. */
    private static final double DEFAULT_EPSILON = 10e-9;

    /**
     * Default constructor.
     */
    protected ContinuedFraction() {
        super();
    }

    /**
     * Access the n-th a coefficient of the continued fraction.  Since a can be
     * a function of the evaluation point, x, that is passed in as well.
     * @param n the coefficient index to retrieve.
     * @param x the evaluation point.
     * @return the n-th a coefficient.
     */
    protected abstract double getA(int n, double x);

    /**
     * Access the n-th b coefficient of the continued fraction.  Since b can be
     * a function of the evaluation point, x, that is passed in as well.
     * @param n the coefficient index to retrieve.
     * @param x the evaluation point.
     * @return the n-th b coefficient.
     */
    protected abstract double getB(int n, double x);

    /**
     * Evaluates the continued fraction at the value x.
     * @param x the evaluation point.
     * @return the value of the continued fraction evaluated at x. 
     */
    public double evaluate(double x) {
        return evaluate(x, DEFAULT_EPSILON, Integer.MAX_VALUE);
    }

    /**
     * Evaluates the continued fraction at the value x.
     * @param x the evaluation point.
     * @param epsilon maximum error allowed.
     * @return the value of the continued fraction evaluated at x. 
     */
    public double evaluate(double x, double epsilon) {
        return evaluate(x, epsilon, Integer.MAX_VALUE);
    }

    /**
     * Evaluates the continued fraction at the value x.
     * @param x the evaluation point.
     * @param maxIterations maximum number of convergents
     * @return the value of the continued fraction evaluated at x. 
     */
    public double evaluate(double x, int maxIterations) {
        return evaluate(x, DEFAULT_EPSILON, maxIterations);
    }

    /**
     * Evaluates the continued fraction at the value x.
     * 
     * The implementation of this method is based on:
     * <ul>
     * <li>O. E-gecio-glu, C . K. Koc, J. Rifa i Coma,
     * <a href="http://citeseer.nj.nec.com/egecioglu91fast.html">
     * Fast Computation of Continued Fractions</a>, Computers Math. Applic.,
     * 21(2--3), 1991, 167--169.</li>
     * </ul>
     * 
     * @param x the evaluation point.
     * @param epsilon maximum error allowed.
     * @param maxIterations maximum number of convergents
     * @return the value of the continued fraction evaluated at x. 
     */
    public double evaluate(double x, double epsilon, int maxIterations) {
        double[][] f = new double[2][2];
        double[][] a = new double[2][2];
        double[][] an = new double[2][2];

        a[0][0] = getA(0, x);
        a[0][1] = 1.0;
        a[1][0] = 1.0;
        a[1][1] = 0.0;

        return evaluate(1, x, a, an, f, epsilon, maxIterations);
    }

    /**
     * Evaluates the n-th convergent, fn = pn / qn, for this continued fraction
     * at the value x.
     * @param n the convergent to compute.
     * @param x the evaluation point.
     * @param a (n-1)-th convergent matrix.  (Input)
     * @param an the n-th coefficient matrix. (Output)
     * @param f the n-th convergent matrix. (Output)
     * @param epsilon maximum error allowed.
     * @param maxIterations maximum number of convergents
     * @return the value of the the n-th convergent for this continued fraction
     *         evaluated at x. 
     */
    private double evaluate(
        int n,
        double x,
        double[][] a,
        double[][] an,
        double[][] f,
        double epsilon,
        int maxIterations) {
        double ret;

        // create next matrix
        an[0][0] = getA(n, x);
        an[0][1] = 1.0;
        an[1][0] = getB(n, x);
        an[1][1] = 0.0;

        // multiply a and an, save as f
        f[0][0] = (a[0][0] * an[0][0]) + (a[0][1] * an[1][0]);
        f[0][1] = (a[0][0] * an[0][1]) + (a[0][1] * an[1][1]);
        f[1][0] = (a[1][0] * an[0][0]) + (a[1][1] * an[1][0]);
        f[1][1] = (a[1][0] * an[0][1]) + (a[1][1] * an[1][1]);

        // determine if we're close enough
        if (Math.abs((f[0][0] * f[1][1]) - (f[1][0] * f[0][1]))
            < Math.abs(epsilon * f[1][0] * f[1][1])) {
            ret = f[0][0] / f[1][0];
        } else {
            if (n >= maxIterations) {
                throw new ConvergenceException(
                    "Continued fraction convergents failed to converge.");
            }
            // compute next
            ret = evaluate(n + 1, x, f /* new a */
            , an /* reuse an */
            , a /* new f */
            , epsilon, maxIterations);
        }

        return ret;
    }
}
