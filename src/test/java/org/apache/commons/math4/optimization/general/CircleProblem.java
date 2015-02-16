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
import org.apache.commons.math3.util.FastMath;

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
@Deprecated
class CircleProblem implements MultivariateDifferentiableVectorFunction {
    /** Cloud of points assumed to be fitted by a circle. */
    private final ArrayList<Vector2D> points;
    /** Error on the x-coordinate of the points. */
    private final double xSigma;
    /** Error on the y-coordinate of the points. */
    private final double ySigma;

    /**
     * @param xError Assumed error for the x-coordinate of the circle points.
     * @param yError Assumed error for the y-coordinate of the circle points.
     */
    public CircleProblem(double xError,
                         double yError) {
        points = new ArrayList<Vector2D>();
        xSigma = xError;
        ySigma = yError;
    }

    public void addPoint(Vector2D p) {
        points.add(p);
    }

    public double[] target() {
        final double[] t = new double[points.size() * 2];
        for (int i = 0; i < points.size(); i++) {
            final Vector2D p = points.get(i);
            final int index = i * 2;
            t[index]     = p.getX();
            t[index + 1] = p.getY();
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

    public double[] value(double[] params) {
        final double cx = params[0];
        final double cy = params[1];
        final double r = params[2];

        final double[] model = new double[points.size() * 2];

        for (int i = 0; i < points.size(); i++) {
            final Vector2D p = points.get(i);

            // Find the circle point closest to the observed point
            // (observed points are points add through the addPoint method above)
            final double dX = cx - p.getX();
            final double dY = cy - p.getY();
            final double scaling = r / FastMath.hypot(dX, dY);
            final int index  = i * 2;
            model[index]     = cx - scaling * dX;
            model[index + 1] = cy - scaling * dY;

        }

        return model;
    }

    public DerivativeStructure[] value(DerivativeStructure[] params) {
        final DerivativeStructure cx = params[0];
        final DerivativeStructure cy = params[1];
        final DerivativeStructure r = params[2];

        final DerivativeStructure[] model = new DerivativeStructure[points.size() * 2];

        for (int i = 0; i < points.size(); i++) {
            final Vector2D p = points.get(i);

            // Find the circle point closest to the observed point
            // (observed points are points add through the addPoint method above)
            final DerivativeStructure dX = cx.subtract(p.getX());
            final DerivativeStructure dY = cy.subtract(p.getY());
            final DerivativeStructure scaling = r.divide(dX.multiply(dX).add(dY.multiply(dY)).sqrt());
            final int index  = i * 2;
            model[index]     = cx.subtract(scaling.multiply(dX));
            model[index + 1] = cy.subtract(scaling.multiply(dY));

        }

        return model;

    }

}
