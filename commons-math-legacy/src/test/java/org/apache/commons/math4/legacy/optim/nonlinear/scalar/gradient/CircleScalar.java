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

package org.apache.commons.math4.legacy.optim.nonlinear.scalar.gradient;

import java.util.ArrayList;

import org.apache.commons.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math4.legacy.analysis.MultivariateFunction;
import org.apache.commons.math4.legacy.analysis.MultivariateVectorFunction;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.ObjectiveFunctionGradient;

/**
 * Class used in the tests.
 */
public class CircleScalar {
    private ArrayList<Vector2D> points;

    public CircleScalar() {
        points  = new ArrayList<>();
    }

    public void addPoint(double px, double py) {
        points.add(Vector2D.of(px, py));
    }

    public double getRadius(Vector2D center) {
        double r = 0;
        for (Vector2D point : points) {
            r += point.distance(center);
        }
        return r / points.size();
    }

    public ObjectiveFunction getObjectiveFunction() {
        return new ObjectiveFunction(new MultivariateFunction() {
                @Override
                public double value(double[] params)  {
                    Vector2D center = Vector2D.of(params[0], params[1]);
                    double radius = getRadius(center);
                    double sum = 0;
                    for (Vector2D point : points) {
                        double di = point.distance(center) - radius;
                        sum += di * di;
                    }
                    return sum;
                }
            });
    }

    public ObjectiveFunctionGradient getObjectiveFunctionGradient() {
        return new ObjectiveFunctionGradient(new MultivariateVectorFunction() {
                @Override
                public double[] value(double[] params) {
                    Vector2D center = Vector2D.of(params[0], params[1]);
                    double radius = getRadius(center);
                    // gradient of the sum of squared residuals
                    double dJdX = 0;
                    double dJdY = 0;
                    for (Vector2D pk : points) {
                        double dk = pk.distance(center);
                        dJdX += (center.getX() - pk.getX()) * (dk - radius) / dk;
                        dJdY += (center.getY() - pk.getY()) * (dk - radius) / dk;
                    }
                    dJdX *= 2;
                    dJdY *= 2;

                    return new double[] { dJdX, dJdY };
                }
            });
    }
}
