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

import org.apache.commons.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math4.legacy.analysis.MultivariateMatrixFunction;
import org.apache.commons.math4.legacy.analysis.MultivariateVectorFunction;

/**
 * Class used in the tests.
 */
class CircleVectorial {
    private ArrayList<Vector2D> points;

    CircleVectorial() {
        points  = new ArrayList<>();
    }

    public void addPoint(double px, double py) {
        points.add(Vector2D.of(px, py));
    }

    public int getN() {
        return points.size();
    }

    public double getRadius(Vector2D center) {
        double r = 0;
        for (Vector2D point : points) {
            r += point.distance(center);
        }
        return r / points.size();
    }

    public MultivariateVectorFunction getModelFunction() {
        return new MultivariateVectorFunction() {
            @Override
            public double[] value(double[] params) {
                Vector2D center = Vector2D.of(params[0], params[1]);
                double radius = getRadius(center);
                double[] residuals = new double[points.size()];
                for (int i = 0; i < residuals.length; i++) {
                    residuals[i] = points.get(i).distance(center) - radius;
                }

                return residuals;
            }
        };
    }

    public MultivariateMatrixFunction getModelFunctionJacobian() {
        return new MultivariateMatrixFunction() {
            @Override
            public double[][] value(double[] params) {
                final int n = points.size();
                final Vector2D center = Vector2D.of(params[0], params[1]);

                double dRdX = 0;
                double dRdY = 0;
                for (Vector2D pk : points) {
                    double dk = pk.distance(center);
                    dRdX += (center.getX() - pk.getX()) / dk;
                    dRdY += (center.getY() - pk.getY()) / dk;
                }
                dRdX /= n;
                dRdY /= n;

                // Jacobian of the radius residuals.
                double[][] jacobian = new double[n][2];
                for (int i = 0; i < n; i++) {
                    final Vector2D pi = points.get(i);
                    final double di = pi.distance(center);
                    jacobian[i][0] = (center.getX() - pi.getX()) / di - dRdX;
                    jacobian[i][1] = (center.getY() - pi.getY()) / di - dRdY;
                }

                return jacobian;
            }
        };
    }
}
