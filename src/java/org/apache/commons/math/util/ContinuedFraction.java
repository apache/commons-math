/*
 * Copyright 2003-2004 The Apache Software Foundation.
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
package org.apache.commons.math.util;

import java.io.Serializable;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.MathException;

/**
 * Provides a generic means to evaluate continued fractions.  Subclasses simply
 * provided the a and b coefficients to evaluate the continued fraction.
 *
 * <p>
 * References:
 * <ul>
 * <li><a href="http://mathworld.wolfram.com/ContinuedFraction.html">
 * Continued Fraction</a></li>
 * </ul>
 * </p>
 *
 * @version $Revision: 1.14 $ $Date: 2004/06/23 16:26:16 $
 */
public abstract class ContinuedFraction implements Serializable {
    
    /** Serialization UID */
    static final long serialVersionUID = 1768555336266158242L;
    
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
     * @throws MathException if the algorithm fails to converge.
     */
    public double evaluate(double x) throws MathException {
        return evaluate(x, DEFAULT_EPSILON, Integer.MAX_VALUE);
    }

    /**
     * Evaluates the continued fraction at the value x.
     * @param x the evaluation point.
     * @param epsilon maximum error allowed.
     * @return the value of the continued fraction evaluated at x. 
     * @throws MathException if the algorithm fails to converge.
     */
    public double evaluate(double x, double epsilon) throws MathException {
        return evaluate(x, epsilon, Integer.MAX_VALUE);
    }

    /**
     * Evaluates the continued fraction at the value x.
     * @param x the evaluation point.
     * @param maxIterations maximum number of convergents
     * @return the value of the continued fraction evaluated at x. 
     * @throws MathException if the algorithm fails to converge.
     */
    public double evaluate(double x, int maxIterations) throws MathException {
        return evaluate(x, DEFAULT_EPSILON, maxIterations);
    }

    /**
     * Evaluates the continued fraction at the value x.
     * 
     * The implementation of this method is based on:
     * <ul>
     * <li>O. E-gecio-glu, C . K. Koc, J. Rifa i Coma,
     * <a href="http://citeseer.ist.psu.edu/egecioglu91fast.html">
     * On Fast Computation of Continued Fractions</a>, Computers Math. Applic.,
     * 21(2--3), 1991, 167--169.</li>
     * </ul>
     * 
     * @param x the evaluation point.
     * @param epsilon maximum error allowed.
     * @param maxIterations maximum number of convergents
     * @return the value of the continued fraction evaluated at x. 
     * @throws MathException if the algorithm fails to converge.
     */
    public double evaluate(double x, double epsilon, int maxIterations)
        throws MathException
    {
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
     * @throws MathException if the algorithm fails to converge.
     */
    private double evaluate(
        int n,
        double x,
        double[][] a,
        double[][] an,
        double[][] f,
        double epsilon,
        int maxIterations) throws MathException 
    {
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
        if (Math.abs((f[0][0] * f[1][1]) - (f[1][0] * f[0][1])) <
            Math.abs(epsilon * f[1][0] * f[1][1]))
        {
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
