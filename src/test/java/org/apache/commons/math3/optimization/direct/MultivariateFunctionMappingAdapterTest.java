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

package org.apache.commons.math3.optimization.direct;


import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.junit.Assert;
import org.junit.Test;

@Deprecated
public class MultivariateFunctionMappingAdapterTest {

    @Test
    public void testStartSimplexInsideRange() {

        final BiQuadratic biQuadratic = new BiQuadratic(2.0, 2.5, 1.0, 3.0, 2.0, 3.0);
        final MultivariateFunctionMappingAdapter wrapped =
                new MultivariateFunctionMappingAdapter(biQuadratic,
                                                           biQuadratic.getLower(),
                                                           biQuadratic.getUpper());

        SimplexOptimizer optimizer = new SimplexOptimizer(1e-10, 1e-30);
        optimizer.setSimplex(new NelderMeadSimplex(new double[][] {
            wrapped.boundedToUnbounded(new double[] { 1.5, 2.75 }),
            wrapped.boundedToUnbounded(new double[] { 1.5, 2.95 }),
            wrapped.boundedToUnbounded(new double[] { 1.7, 2.90 })
        }));

        final PointValuePair optimum
            = optimizer.optimize(300, wrapped, GoalType.MINIMIZE,
                                 wrapped.boundedToUnbounded(new double[] { 1.5, 2.25 }));
        final double[] bounded = wrapped.unboundedToBounded(optimum.getPoint());

        Assert.assertEquals(biQuadratic.getBoundedXOptimum(), bounded[0], 2e-7);
        Assert.assertEquals(biQuadratic.getBoundedYOptimum(), bounded[1], 2e-7);

    }

    @Test
    public void testOptimumOutsideRange() {

        final BiQuadratic biQuadratic = new BiQuadratic(4.0, 0.0, 1.0, 3.0, 2.0, 3.0);
        final MultivariateFunctionMappingAdapter wrapped =
                new MultivariateFunctionMappingAdapter(biQuadratic,
                                                           biQuadratic.getLower(),
                                                           biQuadratic.getUpper());

        SimplexOptimizer optimizer = new SimplexOptimizer(1e-10, 1e-30);
        optimizer.setSimplex(new NelderMeadSimplex(new double[][] {
            wrapped.boundedToUnbounded(new double[] { 1.5, 2.75 }),
            wrapped.boundedToUnbounded(new double[] { 1.5, 2.95 }),
            wrapped.boundedToUnbounded(new double[] { 1.7, 2.90 })
        }));

        final PointValuePair optimum
            = optimizer.optimize(100, wrapped, GoalType.MINIMIZE,
                                 wrapped.boundedToUnbounded(new double[] { 1.5, 2.25 }));
        final double[] bounded = wrapped.unboundedToBounded(optimum.getPoint());

        Assert.assertEquals(biQuadratic.getBoundedXOptimum(), bounded[0], 2e-7);
        Assert.assertEquals(biQuadratic.getBoundedYOptimum(), bounded[1], 2e-7);

    }

    @Test
    public void testUnbounded() {

        final BiQuadratic biQuadratic = new BiQuadratic(4.0, 0.0,
                                                        Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
                                                        Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        final MultivariateFunctionMappingAdapter wrapped =
                new MultivariateFunctionMappingAdapter(biQuadratic,
                                                           biQuadratic.getLower(),
                                                           biQuadratic.getUpper());

        SimplexOptimizer optimizer = new SimplexOptimizer(1e-10, 1e-30);
        optimizer.setSimplex(new NelderMeadSimplex(new double[][] {
            wrapped.boundedToUnbounded(new double[] { 1.5, 2.75 }),
            wrapped.boundedToUnbounded(new double[] { 1.5, 2.95 }),
            wrapped.boundedToUnbounded(new double[] { 1.7, 2.90 })
        }));

        final PointValuePair optimum
            = optimizer.optimize(300, wrapped, GoalType.MINIMIZE,
                                 wrapped.boundedToUnbounded(new double[] { 1.5, 2.25 }));
        final double[] bounded = wrapped.unboundedToBounded(optimum.getPoint());

        Assert.assertEquals(biQuadratic.getBoundedXOptimum(), bounded[0], 2e-7);
        Assert.assertEquals(biQuadratic.getBoundedYOptimum(), bounded[1], 2e-7);

    }

    @Test
    public void testHalfBounded() {

        final BiQuadratic biQuadratic = new BiQuadratic(4.0, 4.0,
                                                        1.0, Double.POSITIVE_INFINITY,
                                                        Double.NEGATIVE_INFINITY, 3.0);
        final MultivariateFunctionMappingAdapter wrapped =
                new MultivariateFunctionMappingAdapter(biQuadratic,
                                                           biQuadratic.getLower(),
                                                           biQuadratic.getUpper());

        SimplexOptimizer optimizer = new SimplexOptimizer(1e-13, 1e-30);
        optimizer.setSimplex(new NelderMeadSimplex(new double[][] {
            wrapped.boundedToUnbounded(new double[] { 1.5, 2.75 }),
            wrapped.boundedToUnbounded(new double[] { 1.5, 2.95 }),
            wrapped.boundedToUnbounded(new double[] { 1.7, 2.90 })
        }));

        final PointValuePair optimum
            = optimizer.optimize(200, wrapped, GoalType.MINIMIZE,
                                 wrapped.boundedToUnbounded(new double[] { 1.5, 2.25 }));
        final double[] bounded = wrapped.unboundedToBounded(optimum.getPoint());

        Assert.assertEquals(biQuadratic.getBoundedXOptimum(), bounded[0], 1e-7);
        Assert.assertEquals(biQuadratic.getBoundedYOptimum(), bounded[1], 1e-7);

    }

    private static class BiQuadratic implements MultivariateFunction {

        private final double xOptimum;
        private final double yOptimum;

        private final double xMin;
        private final double xMax;
        private final double yMin;
        private final double yMax;

        public BiQuadratic(final double xOptimum, final double yOptimum,
                           final double xMin, final double xMax,
                           final double yMin, final double yMax) {
            this.xOptimum = xOptimum;
            this.yOptimum = yOptimum;
            this.xMin     = xMin;
            this.xMax     = xMax;
            this.yMin     = yMin;
            this.yMax     = yMax;
        }

        public double value(double[] point) {

            // the function should never be called with out of range points
            Assert.assertTrue(point[0] >= xMin);
            Assert.assertTrue(point[0] <= xMax);
            Assert.assertTrue(point[1] >= yMin);
            Assert.assertTrue(point[1] <= yMax);

            final double dx = point[0] - xOptimum;
            final double dy = point[1] - yOptimum;
            return dx * dx + dy * dy;

        }

        public double[] getLower() {
            return new double[] { xMin, yMin };
        }

        public double[] getUpper() {
            return new double[] { xMax, yMax };
        }

        public double getBoundedXOptimum() {
            return (xOptimum < xMin) ? xMin : ((xOptimum > xMax) ? xMax : xOptimum);
        }

        public double getBoundedYOptimum() {
            return (yOptimum < yMin) ? yMin : ((yOptimum > yMax) ? yMax : yOptimum);
        }

    }

}
