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

package org.apache.commons.math3.optimization;


import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.apache.commons.math3.analysis.DifferentiableMultivariateFunction;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.optimization.general.ConjugateGradientFormula;
import org.apache.commons.math3.optimization.general.NonLinearConjugateGradientOptimizer;
import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomVectorGenerator;
import org.apache.commons.math3.random.UncorrelatedRandomVectorGenerator;
import org.junit.Assert;
import org.junit.Test;

public class DifferentiableMultivariateMultiStartOptimizerTest {

    @Test
    public void testCircleFitting() {
        Circle circle = new Circle();
        circle.addPoint( 30.0,  68.0);
        circle.addPoint( 50.0,  -6.0);
        circle.addPoint(110.0, -20.0);
        circle.addPoint( 35.0,  15.0);
        circle.addPoint( 45.0,  97.0);
        NonLinearConjugateGradientOptimizer underlying =
            new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.POLAK_RIBIERE,
                                                    new SimpleValueChecker(1.0e-10, 1.0e-10));
        JDKRandomGenerator g = new JDKRandomGenerator();
        g.setSeed(753289573253l);
        RandomVectorGenerator generator =
            new UncorrelatedRandomVectorGenerator(new double[] { 50.0, 50.0 }, new double[] { 10.0, 10.0 },
                                                  new GaussianRandomGenerator(g));
        DifferentiableMultivariateMultiStartOptimizer optimizer =
            new DifferentiableMultivariateMultiStartOptimizer(underlying, 10, generator);
        PointValuePair optimum =
            optimizer.optimize(200, circle, GoalType.MINIMIZE, new double[] { 98.680, 47.345 });
        Assert.assertEquals(200, optimizer.getMaxEvaluations());
        PointValuePair[] optima = optimizer.getOptima();
        for (PointValuePair o : optima) {
            Point2D.Double center = new Point2D.Double(o.getPointRef()[0], o.getPointRef()[1]);
            Assert.assertEquals(69.960161753, circle.getRadius(center), 1.0e-8);
            Assert.assertEquals(96.075902096, center.x, 1.0e-8);
            Assert.assertEquals(48.135167894, center.y, 1.0e-8);
        }
        Assert.assertTrue(optimizer.getEvaluations() > 70);
        Assert.assertTrue(optimizer.getEvaluations() < 90);
        Assert.assertEquals(3.1267527, optimum.getValue(), 1.0e-8);
    }

    private static class Circle implements DifferentiableMultivariateFunction {

        private ArrayList<Point2D.Double> points;

        public Circle() {
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

        public double value(double[] variables) {

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
}
