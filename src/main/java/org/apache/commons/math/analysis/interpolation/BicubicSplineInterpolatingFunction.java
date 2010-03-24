/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.analysis.interpolation;

import org.apache.commons.math.MathException;
import org.apache.commons.math.util.MathUtils;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.DimensionMismatchException;
import org.apache.commons.math.analysis.BivariateRealFunction;

/**
 * Function that implements the
 * <a href="http://en.wikipedia.org/wiki/Bicubic_interpolation">
 * bicubic spline interpolation</a>.
 *
 * @version $Revision$ $Date$
 */
public class BicubicSplineInterpolatingFunction
    implements BivariateRealFunction {
    /**
     * Matrix to compute the spline coefficients from the function values
     * and function derivatives values
     */
    private final static double[][] aInv = {
        { 1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 },
        { 0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0 },
        { -3,3,0,0,-2,-1,0,0,0,0,0,0,0,0,0,0 },
        { 2,-2,0,0,1,1,0,0,0,0,0,0,0,0,0,0 },
        { 0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0 },
        { 0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0 },
        { 0,0,0,0,0,0,0,0,-3,3,0,0,-2,-1,0,0 },
        { 0,0,0,0,0,0,0,0,2,-2,0,0,1,1,0,0 },
        { -3,0,3,0,0,0,0,0,-2,0,-1,0,0,0,0,0 },
        { 0,0,0,0,-3,0,3,0,0,0,0,0,-2,0,-1,0 },
        { 9,-9,-9,9,6,3,-6,-3,6,-6,3,-3,4,2,2,1 },
        { -6,6,6,-6,-3,-3,3,3,-4,4,-2,2,-2,-2,-1,-1 },
        { 2,0,-2,0,0,0,0,0,1,0,1,0,0,0,0,0 },
        { 0,0,0,0,2,0,-2,0,0,0,0,0,1,0,1,0 },
        { -6,6,6,-6,-4,-2,4,2,-3,3,-3,3,-2,-1,-2,-1 },
        { 4,-4,-4,4,2,2,-2,-2,2,-2,2,-2,1,1,1,1 }
    };

    /** Samples x-coordinates */
    private final double[] xval;
    /** Samples y-coordinates */
    private final double[] yval;
    /** Set of cubic splines pacthing the whole data grid */
    private final BicubicSplineFunction[][] splines;

    /**
     * @param x Sample values of the x-coordinate, in increasing order
     * @param y Sample values of the y-coordinate, in increasing order
     * @param z Values of the function on every grid point
     * @param dZdX Values of the partial derivative of function with respect
     * to x on every grid point
     * @param dZdY Values of the partial derivative of function with respect
     * to y on every grid point
     * @param dZdXdY Values of the cross partial derivative of function on
     * every grid point
     * @throws DimensionMismatchException if the various arrays do not contain
     * the expected number of elements.
     * @throws IllegalArgumentException if {@code x} or {@code y} are not strictly
     * increasing.
     */
    public BicubicSplineInterpolatingFunction(double[] x,
                                              double[] y,
                                              double[][] z,
                                              double[][] dZdX,
                                              double[][] dZdY,
                                              double[][] dZdXdY)
        throws MathException {
        final int xLen = x.length;
        final int yLen = y.length;

        if (xLen == 0 || yLen == 0 || z.length == 0 || z[0].length == 0) {
            throw MathRuntimeException.createIllegalArgumentException("no data");
        }
        if (xLen != z.length) {
            throw new DimensionMismatchException(xLen, z.length);
        }
        if (xLen != dZdX.length) {
            throw new DimensionMismatchException(xLen, dZdX.length);
        }
        if (xLen != dZdY.length) {
            throw new DimensionMismatchException(xLen, dZdY.length);
        }
        if (xLen != dZdXdY.length) {
            throw new DimensionMismatchException(xLen, dZdXdY.length);
        }

        MathUtils.checkOrder(x, 1, true);
        MathUtils.checkOrder(y, 1, true);
        
        xval = x.clone();
        yval = y.clone();

        final int lastI = xLen - 1;
        final int lastJ = yLen - 1;
        splines = new BicubicSplineFunction[lastI][lastJ];

        for (int i = 0; i < lastI; i++) {
            if (z[i].length != yLen) {
                throw new DimensionMismatchException(z[i].length, yLen);
            }
            if (dZdX[i].length != yLen) {
                throw new DimensionMismatchException(dZdX[i].length, yLen);
            }
            if (dZdY[i].length != yLen) {
                throw new DimensionMismatchException(dZdY[i].length, yLen);
            }
            if (dZdXdY[i].length != yLen) {
                throw new DimensionMismatchException(dZdXdY[i].length, yLen);
            }
            final int ip1 = i + 1;
            for (int j = 0; j < lastJ; j++) {
                final int jp1 = j + 1;
                final double[] beta = new double[] {
                    z[i][j],      z[ip1][j],      z[i][jp1],      z[ip1][jp1],
                    dZdX[i][j],   dZdX[ip1][j],   dZdX[i][jp1],   dZdX[ip1][jp1],
                    dZdY[i][j],   dZdY[ip1][j],   dZdY[i][jp1],   dZdY[ip1][jp1],
                    dZdXdY[i][j], dZdXdY[ip1][j], dZdXdY[i][jp1], dZdXdY[ip1][jp1]
                };

                splines[i][j] = new BicubicSplineFunction(computeSplineCoefficients(beta));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public double value(double x, double y) {
        final int i = searchIndex(x, xval);
        if (i == -1) {
            throw MathRuntimeException.createIllegalArgumentException("{0} out of [{1}, {2}] range",
                                                                      x, xval[0], xval[xval.length - 1]);
        }
        final int j = searchIndex(y, yval);
        if (j == -1) {
            throw MathRuntimeException.createIllegalArgumentException("{0} out of [{1}, {2}] range",
                                                                      y, yval[0], yval[yval.length - 1]);
        }

        final double xN = (x - xval[i]) / (xval[i + 1] - xval[i]);
        final double yN = (y - yval[j]) / (yval[j + 1] - yval[j]);

        return splines[i][j].value(xN, yN);
    }

    /**
     * @param c coordinate
     * @param val coordinate samples
     * @return the index in {@code val} corresponding to the interval
     * containing {@code c}, or {@code -1} if {@code c} is out of the
     * range defined by the end values of {@code val}
     */
    private int searchIndex(double c, double[] val) {
        if (c < val[0]) {
            return -1;
        }

        for (int i = 1, max = val.length; i < max; i++) {
            if (c <= val[i]) {
                return i - 1;
            }
        }

        return -1;
    }

    /**
     * Compute the spline coefficients from the list of function values and
     * function partial derivatives values at the four corners of a grid
     * element. They must be specified in the following order:
     * <ul>
     *  <li>f(0,0)</li>
     *  <li>f(1,0)</li>
     *  <li>f(0,1)</li>
     *  <li>f(1,1)</li>
     *  <li>fx(0,0)</li>
     *  <li>fx(1,0)</li>
     *  <li>fx(0,1)</li>
     *  <li>fx(1,1)</li>
     *  <li>fy(0,0)</li>
     *  <li>fy(1,0)</li>
     *  <li>fy(0,1)</li>
     *  <li>fy(1,1)</li>
     *  <li>fxy(0,0)</li>
     *  <li>fxy(1,0)</li>
     *  <li>fxy(0,1)</li>
     *  <li>fxy(1,1)</li>
     * </ul>
     * @param beta List of function values and function partial derivatives
     * values
     * @return the spline coefficients
     */
    private double[] computeSplineCoefficients(double[] beta) {
        final double[] a = new double[16];
        
        for (int i = 0; i < 16; i++) {
            double result = 0;
            final double[] row = aInv[i];
            for (int j = 0; j < 16; j++) {
                result += row[j] * beta[j];
            }
            a[i] = result;
        }

        return a;
    }
}

/**
 * 2D-spline function.
 */
class BicubicSplineFunction
    implements BivariateRealFunction {
    /** Coefficients */
    private final double
        a00, a01, a02, a03,
        a10, a11, a12, a13,
        a20, a21, a22, a23,
        a30, a31, a32, a33;

    /**
     * @param a Spline coefficients
     */
    public BicubicSplineFunction(double[] a) {
        this.a00 = a[0];
        this.a10 = a[1];
        this.a20 = a[2];
        this.a30 = a[3];
        this.a01 = a[4];
        this.a11 = a[5];
        this.a21 = a[6];
        this.a31 = a[7];
        this.a02 = a[8];
        this.a12 = a[9];
        this.a22 = a[10];
        this.a32 = a[11];
        this.a03 = a[12];
        this.a13 = a[13];
        this.a23 = a[14];
        this.a33 = a[15];
    }

    /**
     * @param x x-coordinate of the interpolation point
     * @param y y-coordinate of the interpolation point
     * @return the interpolated value.
     */
    public double value(double x, double y) {
        if (x < 0 || x > 1) {
            throw MathRuntimeException.createIllegalArgumentException("{0} out of [{1}, {2}] range",
                                                                      x, 0, 1);
        }
        if (y < 0 || y > 1) {
            throw MathRuntimeException.createIllegalArgumentException("{0} out of [{1}, {2}] range",
                                                                      y, 0, 1);
        }
        
        final double x2 = x * x;
        final double x3 = x2 * x;
        final double y2 = y * y;
        final double y3 = y2 * y;
        
        return a00 + a01 * y + a02 * y2 + a03 * y3
            + a10 * x + a11 * x * y + a12 * x * y2 + a13 * x * y3
            + a20 * x2 + a21 * x2 * y + a22 * x2 * y2 + a23 * x2 * y3
            + a30 * x3 + a31 * x3 * y + a32 * x3 * y2 + a33 * x3 * y3;
    }
}
