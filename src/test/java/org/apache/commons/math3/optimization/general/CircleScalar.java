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
import org.apache.commons.math3.analysis.DifferentiableMultivariateFunction;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;

/**
 * Class used in the tests.
 */
class CircleScalar implements DifferentiableMultivariateFunction {
    private ArrayList<Point2D.Double> points;

    public CircleScalar() {
        points  = new ArrayList<Point2D.Double>();
    }

    public void addPoint(double px, double py) {
        points.add(new Point2D.Double(px, py));
    }

    public double getRadius(Point2D.Double center) {
        double r = 0;
        for (Point2D.Double point : points) {
            r += point.distance(center);
        }
        return r / points.size();
    }

    private double[] gradient(double[] point) {
        // optimal radius
        Point2D.Double center = new Point2D.Double(point[0], point[1]);
        double radius = getRadius(center);

        // gradient of the sum of squared residuals
        double dJdX = 0;
        double dJdY = 0;
        for (Point2D.Double pk : points) {
            double dk = pk.distance(center);
            dJdX += (center.x - pk.x) * (dk - radius) / dk;
            dJdY += (center.y - pk.y) * (dk - radius) / dk;
        }
        dJdX *= 2;
        dJdY *= 2;

        return new double[] { dJdX, dJdY };
    }

    public double value(double[] variables)  {
        Point2D.Double center = new Point2D.Double(variables[0], variables[1]);
        double radius = getRadius(center);

        double sum = 0;
        for (Point2D.Double point : points) {
            double di = point.distance(center) - radius;
            sum += di * di;
        }

        return sum;
    }

    public MultivariateVectorFunction gradient() {
        return new MultivariateVectorFunction() {
            public double[] value(double[] point) {
                return gradient(point);
            }
        };
    }

    public MultivariateFunction partialDerivative(final int k) {
        return new MultivariateFunction() {
            public double value(double[] point) {
                return gradient(point)[k];
            }
        };
    }
}
