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
package org.apache.commons.math4.legacy.fitting.leastsquares;

import java.util.ArrayList;

import org.apache.commons.math4.legacy.analysis.MultivariateMatrixFunction;
import org.apache.commons.math4.legacy.analysis.MultivariateVectorFunction;
import org.apache.commons.math4.core.jdkmath.JdkMath;

/**
 * Class that models a circle.
 * The parameters of problem are:
 * <ul>
 *  <li>the x-coordinate of the circle center,</li>
 *  <li>the y-coordinate of the circle center,</li>
 *  <li>the radius of the circle.</li>
 * </ul>
 * The model functions are:
 * <ul>
 *  <li>for each triplet (cx, cy, r), the (x, y) coordinates of a point on the
 *   corresponding circle.</li>
 * </ul>
 */
class CircleProblem {
    /** Cloud of points assumed to be fitted by a circle. */
    private final ArrayList<double[]> points;
    /** Error on the x-coordinate of the points. */
    private final double xSigma;
    /** Error on the y-coordinate of the points. */
    private final double ySigma;
    /** Number of points on the circumference (when searching which
        model point is closest to a given "observation". */
    private final int resolution;

    /**
     * @param xError Assumed error for the x-coordinate of the circle points.
     * @param yError Assumed error for the y-coordinate of the circle points.
     * @param searchResolution Number of points to try when searching the one
     * that is closest to a given "observed" point.
     */
    CircleProblem(double xError,
                  double yError,
                  int searchResolution) {
        points = new ArrayList<>();
        xSigma = xError;
        ySigma = yError;
        resolution = searchResolution;
    }

    /**
     * @param xError Assumed error for the x-coordinate of the circle points.
     * @param yError Assumed error for the y-coordinate of the circle points.
     */
    CircleProblem(double xError,
                  double yError) {
        this(xError, yError, 500);
    }

    public void addPoint(double[] p) {
        points.add(p);
    }

    public double[] target() {
        final double[] t = new double[points.size() * 2];
        for (int i = 0; i < points.size(); i++) {
            final double[] p = points.get(i);
            final int index = i * 2;
            t[index] = p[0];
            t[index + 1] = p[1];
        }

        return t;
    }

    public double[] weight() {
        final double wX = 1 / (xSigma * xSigma);
        final double wY = 1 / (ySigma * ySigma);
        final double[] w = new double[points.size() * 2];
        for (int i = 0; i < points.size(); i++) {
            final int index = i * 2;
            w[index] = wX;
            w[index + 1] = wY;
        }

        return w;
    }

    public MultivariateVectorFunction getModelFunction() {
        return new MultivariateVectorFunction() {
            @Override
            public double[] value(double[] params) {
                final double cx = params[0];
                final double cy = params[1];
                final double r = params[2];

                final double[] model = new double[points.size() * 2];

                final double twopi = 2 * Math.PI;
                final double deltaTheta = twopi / resolution;
                for (int i = 0; i < points.size(); i++) {
                    final double[] p = points.get(i);
                    final double px = p[0];
                    final double py = p[1];

                    double bestX = 0;
                    double bestY = 0;
                    double dMin = Double.POSITIVE_INFINITY;

                    // Find the angle for which the circle passes closest to the
                    // current point (using a resolution of 100 points along the
                    // circumference).
                    for (double theta = 0; theta <= twopi; theta += deltaTheta) {
                        final double currentX = cx + r * JdkMath.cos(theta);
                        final double currentY = cy + r * JdkMath.sin(theta);
                        final double dX = currentX - px;
                        final double dY = currentY - py;
                        final double d = dX * dX + dY * dY;
                        if (d < dMin) {
                            dMin = d;
                            bestX = currentX;
                            bestY = currentY;
                        }
                    }

                    final int index = i * 2;
                    model[index] = bestX;
                    model[index + 1] = bestY;
                }

                return model;
            }
        };
    }

    public MultivariateMatrixFunction getModelFunctionJacobian() {
        return new MultivariateMatrixFunction() {
            @Override
            public double[][] value(double[] point) {
                return jacobian(point);
            }
        };
    }

    private double[][] jacobian(double[] params) {
        final double[][] jacobian = new double[points.size() * 2][3];

        for (int i = 0; i < points.size(); i++) {
            final int index = i * 2;
            // Partial derivative wrt x-coordinate of center.
            jacobian[index][0] = 1;
            jacobian[index + 1][0] = 0;
            // Partial derivative wrt y-coordinate of center.
            jacobian[index][1] = 0;
            jacobian[index + 1][1] = 1;
            // Partial derivative wrt radius.
            final double[] p = points.get(i);
            jacobian[index][2] = (p[0] - params[0]) / params[2];
            jacobian[index + 1][2] = (p[1] - params[1]) / params[2];
        }

        return jacobian;
    }
}
