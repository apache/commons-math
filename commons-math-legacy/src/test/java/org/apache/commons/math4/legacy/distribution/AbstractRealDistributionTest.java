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
package org.apache.commons.math4.legacy.distribution;

import org.apache.commons.math4.legacy.analysis.UnivariateFunction;
import org.apache.commons.math4.legacy.analysis.integration.RombergIntegrator;
import org.apache.commons.math4.legacy.analysis.integration.UnivariateIntegrator;
import org.apache.commons.math4.legacy.exception.OutOfRangeException;
import org.junit.Assert;
import org.junit.Test;

/** Various tests related to MATH-699. */
public class AbstractRealDistributionTest {

    @Test
    public void testContinuous() {
        final double x0 = 0.0;
        final double x1 = 1.0;
        final double x2 = 2.0;
        final double x3 = 3.0;
        final double p12 = 0.5;
        final AbstractRealDistribution distribution;
        distribution = new AbstractRealDistribution() {
            private static final long serialVersionUID = 1L;

            @Override
            public double cumulativeProbability(final double x) {
                if ((x < x0) || (x > x3)) {
                    throw new OutOfRangeException(x, x0, x3);
                }
                if (x <= x1) {
                    return p12 * (x - x0) / (x1 - x0);
                } else if (x <= x2) {
                    return p12;
                } else if (x <= x3) {
                    return p12 + (1.0 - p12) * (x - x2) / (x3 - x2);
                }
                return 0.0;
            }

            @Override
            public double density(final double x) {
                if ((x < x0) || (x > x3)) {
                    throw new OutOfRangeException(x, x0, x3);
                }
                if (x <= x1) {
                    return p12 / (x1 - x0);
                } else if (x <= x2) {
                    return 0.0;
                } else if (x <= x3) {
                    return (1.0 - p12) / (x3 - x2);
                }
                return 0.0;
            }

            @Override
            public double getMean() {
                return ((x0 + x1) * p12 + (x2 + x3) * (1.0 - p12)) / 2.0;
            }

            @Override
            public double getVariance() {
                final double meanX = getMean();
                final double meanX2;
                meanX2 = ((x0 * x0 + x0 * x1 + x1 * x1) * p12 + (x2 * x2 + x2
                        * x3 + x3 * x3)
                        * (1.0 - p12)) / 3.0;
                return meanX2 - meanX * meanX;
            }

            @Override
            public double getSupportLowerBound() {
                return x0;
            }

            @Override
            public double getSupportUpperBound() {
                return x3;
            }

            @Override
            public boolean isSupportConnected() {
                return false;
            }
        };
        final double expected = x1;
        final double actual = distribution.inverseCumulativeProbability(p12);
        Assert.assertEquals("", expected, actual,
                distribution.getSolverAbsoluteAccuracy());
    }

    @Test
    public void testDiscontinuous() {
        final double x0 = 0.0;
        final double x1 = 0.25;
        final double x2 = 0.5;
        final double x3 = 0.75;
        final double x4 = 1.0;
        final double p12 = 1.0 / 3.0;
        final double p23 = 2.0 / 3.0;
        final AbstractRealDistribution distribution;
        distribution = new AbstractRealDistribution() {
            private static final long serialVersionUID = 1L;

            @Override
            public double cumulativeProbability(final double x) {
                if ((x < x0) || (x > x4)) {
                    throw new OutOfRangeException(x, x0, x4);
                }
                if (x <= x1) {
                    return p12 * (x - x0) / (x1 - x0);
                } else if (x <= x2) {
                    return p12;
                } else if (x <= x3) {
                    return p23;
                } else {
                    return (1.0 - p23) * (x - x3) / (x4 - x3) + p23;
                }
            }

            @Override
            public double density(final double x) {
                if ((x < x0) || (x > x4)) {
                    throw new OutOfRangeException(x, x0, x4);
                }
                if (x <= x1) {
                    return p12 / (x1 - x0);
                } else if (x <= x2) {
                    return 0.0;
                } else if (x <= x3) {
                    return 0.0;
                } else {
                    return (1.0 - p23) / (x4 - x3);
                }
            }

            @Override
            public double getMean() {
                final UnivariateFunction f = new UnivariateFunction() {

                    @Override
                    public double value(final double x) {
                        return x * density(x);
                    }
                };
                final UnivariateIntegrator integrator = new RombergIntegrator();
                return integrator.integrate(Integer.MAX_VALUE, f, x0, x4);
            }

            @Override
            public double getVariance() {
                final double meanX = getMean();
                final UnivariateFunction f = new UnivariateFunction() {

                    @Override
                    public double value(final double x) {
                        return x * x * density(x);
                    }
                };
                final UnivariateIntegrator integrator = new RombergIntegrator();
                final double meanX2 = integrator.integrate(Integer.MAX_VALUE,
                        f, x0, x4);
                return meanX2 - meanX * meanX;
            }

            @Override
            public double getSupportLowerBound() {
                return x0;
            }

            @Override
            public double getSupportUpperBound() {
                return x4;
            }

            @Override
            public boolean isSupportConnected() {
                return false;
            }
        };
        final double expected = x2;
        final double actual = distribution.inverseCumulativeProbability(p23);
        Assert.assertEquals("", expected, actual,
                distribution.getSolverAbsoluteAccuracy());

    }
}
