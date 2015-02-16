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

import java.util.ArrayList;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableVectorFunction;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * Class used in the tests.
 */
@Deprecated
class CircleVectorial implements MultivariateDifferentiableVectorFunction {
    private ArrayList<Vector2D> points;

    public CircleVectorial() {
        points  = new ArrayList<Vector2D>();
    }

    public void addPoint(double px, double py) {
        points.add(new Vector2D(px, py));
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

    private DerivativeStructure distance(Vector2D point,
                                         DerivativeStructure cx, DerivativeStructure cy) {
        DerivativeStructure dx = cx.subtract(point.getX());
        DerivativeStructure dy = cy.subtract(point.getY());
        return dx.multiply(dx).add(dy.multiply(dy)).sqrt();
    }

    public DerivativeStructure getRadius(DerivativeStructure cx, DerivativeStructure cy) {
        DerivativeStructure r = cx.getField().getZero();
        for (Vector2D point : points) {
            r = r.add(distance(point, cx, cy));
        }
        return r.divide(points.size());
    }

    public double[] value(double[] variables) {
        Vector2D center = new Vector2D(variables[0], variables[1]);
        double radius = getRadius(center);

        double[] residuals = new double[points.size()];
        for (int i = 0; i < residuals.length; ++i) {
            residuals[i] = points.get(i).distance(center) - radius;
        }

        return residuals;
    }

    public DerivativeStructure[] value(DerivativeStructure[] variables) {
        DerivativeStructure radius = getRadius(variables[0], variables[1]);

        DerivativeStructure[] residuals = new DerivativeStructure[points.size()];
        for (int i = 0; i < residuals.length; ++i) {
            residuals[i] = distance(points.get(i), variables[0], variables[1]).subtract(radius);
        }

        return residuals;
    }

}
