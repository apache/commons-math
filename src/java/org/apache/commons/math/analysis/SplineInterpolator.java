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

/**
 * Computes a natural spline interpolation for the data set.
 *
 * @version $Revision: 1.6 $ $Date: 2003/10/13 08:09:31 $
 *
 */
public class SplineInterpolator implements UnivariateRealInterpolator {
    /** the natural spline coefficients. */
    private double[][] c = null;

    /**
     * Computes an interpolating function for the data set.
     * @param xval the arguments for the interpolation points
     * @param yval the values for the interpolation points
     * @return a function which interpolates the data set
     */
    public UnivariateRealFunction interpolate(double[] xval, double[] yval) {
        if (xval.length != yval.length) {
            throw new IllegalArgumentException("Dataset arrays must have same length.");
        }

        if (c == null) {
            // Number of intervals. The number of data points is N+1.
            int n = xval.length - 1;
            // Check whether the xval vector has ascending values.
            // Separation should be checked too (not implemented: which criteria?).
            for (int i = 0; i < n; i++) {
                if (xval[i] >= xval[i + 1]) {
                    throw new IllegalArgumentException("Dataset must specify sorted, ascending x values.");
                }
            }
            // Vectors for the equation system. There are n-1 equations for the unknowns s[i] (1<=i<=N-1),
            // which are second order derivatives for the spline at xval[i]. At the end points, s[0]=s[N]=0.
            // Vectors are offset by -1, except the lower diagonal vector which is offset by -2. Layout:
            // d[0]*s[1]+u[0]*s[2]                                           = b[0]
            // l[0]*s[1]+d[1]*s[2]+u[1]*s[3]                                 = b[1]
            //           l[1]*s[2]+d[2]*s[3]+u[2]*s[4]                       = b[2]
            //                           ...
            //                     l[N-4]*s[N-3]+d[N-3]*s[N-2]+u[N-3]*s[N-1] = b[N-3]
            //                                   l[N-3]*s[N-2]+d[N-2]*s[N-1] = b[N-2]
            // Vector b is the right hand side (RHS) of the system.
            double b[] = new double[n - 1];
            // Vector d is diagonal of the matrix and also holds the computed solution.
            double d[] = new double[n - 1];
            // Setup right hand side and diagonal.
            double dquot = (yval[1] - yval[0]) / (xval[1] - xval[0]);
            for (int i = 0; i < n - 1; i++) {
                // TODO avoid recomputing the term
                //    (yval[i + 2] - yval[i + 1]) / (xval[i + 2] - xval[i + 1])
                // take it from the previous loop pass. Note: the interesting part of performance
                // loss is the range check in the array access, not the computation itself.
                double dquotNext = 
                    (yval[i + 2] - yval[i + 1]) / (xval[i + 2] - xval[i + 1]);
                b[i] = 6.0 * (dquotNext - dquot);
                d[i] = 2.0 * (xval[i + 2] - xval[i]);
                dquot = dquotNext;
            }
            // u[] and l[] (for the upper and lower diagonal respectively) are not
            // really needed, the computation is folded into the system solving loops.
            // Keep this for documentation purposes:
            //double u[] = new double[n - 2]; // upper diagonal
            //double l[] = new double[n - 2]; // lower diagonal
            // Set up upper and lower diagonal. Keep the offsets in mind.
            //for (int i = 0; i < n - 2; i++) {
            //  u[i] = xval[i + 2] - xval[i + 1];
            //  l[i] = xval[i + 2] - xval[i + 1];
            //}
            // Solve the system: forward pass.
            for (int i = 0; i < n - 2; i++) {
                double delta = xval[i + 2] - xval[i + 1];
                double deltaquot = delta / d[i];
                d[i + 1] -= delta * deltaquot;
                b[i + 1] -= b[i] * deltaquot;
            }
            // Solve the system: backward pass.
            d[n - 2] = b[n - 2] / d[n - 2];
            for (int i = n - 3; i >= 0; i--) {
                d[i] = (b[i] - (xval[i + 2] - xval[i + 1]) * d[i + 1]) / d[i];
            }
            // Compute coefficients as usual polynomial coefficients.
            // Not the best with respect to roundoff on evaluation, but simple.
            c = new double[n][4];
            double delta = xval[1] - xval[0];
            c[0][3] = d[0] / delta / 6.0;
            c[0][2] = 0.0;
            c[0][1] = (yval[1] - yval[0]) / delta - d[0] * delta / 6.0;
            for (int i = 1; i < n - 2; i++) {
                delta = xval[i + 1] - xval[i];
                c[i][3] = (d[i] - d[i - 1]) / delta / 6.0;
                c[i][2] = d[i - 1] / 2.0;
                c[i][1] =
                    (yval[i + 1] - yval[i]) / delta
                        - (d[i] / 2.0 - d[i - 1]) * delta / 3.0;
            }
            delta = (xval[n] - xval[n - 1]);
            c[n - 1][3] = -d[n - 2] / delta / 6.0;
            c[n - 1][2] = d[n - 2] / 2.0;
            c[n - 1][1] =
                (yval[n] - yval[n - 1]) / delta - d[n - 2] * delta / 3.0;
            for (int i = 0; i < n; i++) {
                c[i][0] = yval[i];
            }
        }

        // TODO: copy xval, unless copied in CubicSplineFunction constructor
        return new CubicSplineFunction(xval, c);
    }

}
