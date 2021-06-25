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
import org.apache.commons.math4.legacy.analysis.UnivariateFunction;
import org.apache.commons.math4.legacy.stat.regression.SimpleRegression;

/**
 * Class that models a straight line defined as {@code y = a x + b}.
 * The parameters of problem are:
 * <ul>
 *  <li>{@code a}</li>
 *  <li>{@code b}</li>
 * </ul>
 * The model functions are:
 * <ul>
 *  <li>for each pair (a, b), the y-coordinate of the line.</li>
 * </ul>
 */
class StraightLineProblem {
    /** Cloud of points assumed to be fitted by a straight line. */
    private final ArrayList<double[]> points;
    /** Error (on the y-coordinate of the points). */
    private final double sigma;

    /**
     * @param error Assumed error for the y-coordinate.
     */
    StraightLineProblem(double error) {
        points = new ArrayList<>();
        sigma = error;
    }

    public void addPoint(double px, double py) {
        points.add(new double[] { px, py });
    }

    /**
     * @return the list of x-coordinates.
     */
    public double[] x() {
        final double[] v = new double[points.size()];
        for (int i = 0; i < points.size(); i++) {
            final double[] p = points.get(i);
            v[i] = p[0]; // x-coordinate.
        }

        return v;
    }

    /**
     * @return the list of y-coordinates.
     */
    public double[] y() {
        final double[] v = new double[points.size()];
        for (int i = 0; i < points.size(); i++) {
            final double[] p = points.get(i);
            v[i] = p[1]; // y-coordinate.
        }

        return v;
    }

    public double[] target() {
        return y();
    }

    public double[] weight() {
        final double weight = 1 / (sigma * sigma);
        final double[] w = new double[points.size()];
        for (int i = 0; i < points.size(); i++) {
            w[i] = weight;
        }

        return w;
    }

    public MultivariateVectorFunction getModelFunction() {
        return new MultivariateVectorFunction() {
            @Override
            public double[] value(double[] params) {
                final Model line = new Model(params[0], params[1]);

                final double[] model = new double[points.size()];
                for (int i = 0; i < points.size(); i++) {
                    final double[] p = points.get(i);
                    model[i] = line.value(p[0]);
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

    /**
     * Directly solve the linear problem, using the {@link SimpleRegression}
     * class.
     */
    public double[] solve() {
        final SimpleRegression regress = new SimpleRegression(true);
        for (double[] d : points) {
            regress.addData(d[0], d[1]);
        }

        final double[] result = { regress.getSlope(), regress.getIntercept() };
        return result;
    }

    private double[][] jacobian(double[] params) {
        final double[][] jacobian = new double[points.size()][2];

        for (int i = 0; i < points.size(); i++) {
            final double[] p = points.get(i);
            // Partial derivative wrt "a".
            jacobian[i][0] = p[0];
            // Partial derivative wrt "b".
            jacobian[i][1] = 1;
        }

        return jacobian;
    }

    /**
     * Linear function.
     */
    public static class Model implements UnivariateFunction {
        private final double a;
        private final double b;

        Model(double a,
              double b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public double value(double x) {
            return a * x + b;
        }
    }
}
