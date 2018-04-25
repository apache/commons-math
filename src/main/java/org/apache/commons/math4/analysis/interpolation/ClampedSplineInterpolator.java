/*
 * Copyright 2017 The Apache Software Foundation.
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
package org.apache.commons.math3.analysis.interpolation;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.MathArrays;

/**
 * @author murc_cr
 * Computes a clamped cubic spline interpolation for the data set.
 * <p>
 * The {@link #interpolate(double[], double[], double FPO, double FPN)} method returns a {@link PolynomialSplineFunction}
 * consisting of n cubic polynomials, defined over the subintervals determined by the x values,
 * {@code x[0] < x[i] ... < x[n].}  The x values are referred to as "knot points."
 * <p>
 * The value of the PolynomialSplineFunction at a point x that is greater than or equal to the smallest
 * knot point and strictly less than the largest knot point is computed by finding the subinterval to which
 * x belongs and computing the value of the corresponding polynomial at <code>x - x[i] </code> where
 * <code>i</code> is the index of the subinterval.  See {@link PolynomialSplineFunction} for more details.
 * </p>
 * <p>
 * The interpolating polynomials satisfy: <ol>
 * <li>The value of the PolynomialSplineFunction at each of the input x values equals the
 *  corresponding y value.</li>
 * <li>Adjacent polynomials are equal through two derivatives at the knot points (i.e., adjacent polynomials
 *  "match up" at the knot points, as do their first and second derivatives).</li>
 * </ol>
 * <p>
 * The clamped spline interpolation algorithm implemented is as described in R.L. Burden, J.D. Faires,
 * <u>Numerical Analysis</u>, 9th Ed., 2010, PWS-Kent, ISBN-13: 978-0-538-73351-9 ISBN-10: 0-538-73351-9, pp 155-156.
 * </p>
 *
 */

public class ClampedSplineInterpolator { 
    
     /**
     * Computes an interpolating function for the data set.
     * @param x the arguments for the interpolation points
     * @param y the values for the interpolation points
     * @param FPO value of the first derivative in x0 (Slope that clamps the spline in its starting point)
     * @param FPN value of the first derivative in xn (Slope that clamps the spline in its ending point)
     * @return a function which interpolates the data set
     * @throws DimensionMismatchException if {@code x} and {@code y}
     * have different sizes.
     * @throws NonMonotonicSequenceException if {@code x} is not sorted in
     * strict increasing order.
     * @throws NumberIsTooSmallException if the size of {@code x} is smaller
     * than 3.
     */
    
    public PolynomialSplineFunction interpolate(double x[], double y[], double FPO, double FPN)
            throws DimensionMismatchException, NumberIsTooSmallException, NonMonotonicSequenceException
    {
        if (x.length != y.length) {
            throw new DimensionMismatchException(x.length, y.length);
        }
 
        if (x.length < 3) {
            throw new NumberIsTooSmallException(LocalizedFormats.NUMBER_OF_POINTS,
                    x.length, 3, true);
        }
 
        // Number of intervals.  The number of data points is n + 1.
        final int n = x.length - 1;
 
        MathArrays.checkOrder(x);
 
         MathArrays.checkOrder(x);

        // Differences between knot points
        final double h[] = new double[n];
        for (int i = 0; i < n; i++) {
            h[i] = x[i + 1] - x[i];
        }       
        
        final double mu[] = new double[n];
        final double z[] = new double[n + 1];
        final double alpha[] = new double[n + 1];
        final double l[] = new double[n+1];

        alpha[0] = 3d * (y[1] - y[0])/h[0] - 3d * FPO;
        alpha[n] = 3d * FPN - 3d * (y[n] - y[n-1])/h[n-1];
        
        mu[0] = 0.5d;        
        l[0] = 2d * h[0];
        z[0] = alpha[0]/l[0];
        
        for (int i = 1; i < n; i++) {
            
            alpha[i] = (3d / h[i]) * (y[i+1] - y[i]) - (3d / h[i - 1]) * (y[i] - y[i - 1]);
            l[i] = 2d * (x[i + 1] - x[i - 1]) - h[i - 1]* mu[i - 1];
            mu[i] = h[i] / l[i];                     
            z[i] = (alpha[i] - h[i - 1] * z[i - 1]) / l[i];
        }      
        // cubic spline coefficients --  b is linear, c quadratic, d is cubic (original y's are constants)
        final double b[] = new double[n];
        final double c[] = new double[n + 1];
        final double d[] = new double[n];        
        l[n] = h[n - 1] * (2d - mu[n-1]);
        z[n] = (alpha[n] - h[n-1]*z[n-1])/l[n];
        c[n] = z[n];    
        
        for (int j = n -1; j >=0; j--) {
            c[j] = z[j] - mu[j] * c[j + 1];
            b[j] = ((y[j + 1] - y[j]) / h[j]) - h[j] * (c[j + 1] + 2d * c[j]) / 3d;
            d[j] = (c[j + 1] - c[j]) / (3d * h[j]);
        }
        
        final PolynomialFunction polynomials[] = new PolynomialFunction[n];
        final double coefficients[] = new double[4];
        for (int i = 0; i < n; i++) {
            coefficients[0] = y[i];
            coefficients[1] = b[i];
            coefficients[2] = c[i];
            coefficients[3] = d[i];
            polynomials[i] = new PolynomialFunction(coefficients);
        }
        
        return new PolynomialSplineFunction(x, polynomials); 
    }
    
    public PolynomialSplineFunction interpolate(double x[], double y[]) {
        /**
         * If the first derivatives evaluated in x0 and xn are not provided
         * They are derived from the b0 and bn coefficients of the natural spline that passes through the set of points
         * as described in R.L. Burden, J.D. Faires, * <u>Numerical Analysis</u>, 9th Ed., 2010, PWS-Kent, 
         * ISBN-13: 978-0-538-73351-9 ISBN-10: 0-538-73351-9, pp 148.
        */        
        double FPO, FPN;
        SplineInterpolator spliner = new SplineInterpolator();
        PolynomialSplineFunction spline = spliner.interpolate(x, y);
        int nPolinomyals = spline.getPolynomials().length;
        FPO = spline.derivative().value(x[0]);
        
        FPN = spline.derivative().value(x[x.length - 1]);;        
        return interpolate(x, y, FPO, FPN);
    }  
    
} 
