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

package org.apache.commons.math3.optimization.general;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import org.apache.commons.math3.analysis.DifferentiableMultivariateVectorFunction;
import org.apache.commons.math3.analysis.MultivariateMatrixFunction;

/**
 * Class used in the tests.
 */
class CircleVectorial implements DifferentiableMultivariateVectorFunction {
    private ArrayList<Point2D.Double> points;

    public CircleVectorial() {
        points  = new ArrayList<Point2D.Double>();
    }

    public void addPoint(double px, double py) {
        points.add(new Point2D.Double(px, py));
    }

    public int getN() {
        return points.size();
    }

    public double getRadius(Point2D.Double center) {
        double r = 0;
        for (Point2D.Double point : points) {
            r += point.distance(center);
        }
        return r / points.size();
    }

    private double[][] jacobian(double[] point) {
        int n = points.size();
        Point2D.Double center = new Point2D.Double(point[0], point[1]);

        // gradient of the optimal radius
        double dRdX = 0;
        double dRdY = 0;
        for (Point2D.Double pk : points) {
            double dk = pk.distance(center);
            dRdX += (center.x - pk.x) / dk;
            dRdY += (center.y - pk.y) / dk;
        }
        dRdX /= n;
        dRdY /= n;

        // jacobian of the radius residuals
        double[][] jacobian = new double[n][2];
        for (int i = 0; i < n; ++i) {
            Point2D.Double pi = points.get(i);
            double di   = pi.distance(center);
            jacobian[i][0] = (center.x - pi.x) / di - dRdX;
            jacobian[i][1] = (center.y - pi.y) / di - dRdY;
        }

        return jacobian;
    }

    public double[] value(double[] variables) {
        Point2D.Double center = new Point2D.Double(variables[0], variables[1]);
        double radius = getRadius(center);

        double[] residuals = new double[points.size()];
        for (int i = 0; i < residuals.length; ++i) {
            residuals[i] = points.get(i).distance(center) - radius;
        }

        return residuals;
    }

    public MultivariateMatrixFunction jacobian() {
        return new MultivariateMatrixFunction() {
            public double[][] value(double[] point) {
                return jacobian(point);
            }
        };
    }
}
