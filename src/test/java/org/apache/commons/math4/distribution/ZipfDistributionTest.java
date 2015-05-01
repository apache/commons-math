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

package org.apache.commons.math4.distribution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.math4.TestUtils;
import org.apache.commons.math4.analysis.UnivariateFunction;
import org.apache.commons.math4.analysis.integration.SimpsonIntegrator;
import org.apache.commons.math4.distribution.IntegerDistribution;
import org.apache.commons.math4.distribution.ZipfDistribution;
import org.apache.commons.math4.distribution.ZipfDistribution.ZipfRejectionSampler;
import org.apache.commons.math4.exception.NotStrictlyPositiveException;
import org.apache.commons.math4.random.AbstractRandomGenerator;
import org.apache.commons.math4.random.RandomGenerator;
import org.apache.commons.math4.random.Well1024a;
import org.apache.commons.math4.util.FastMath;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test cases for {@link ZipfDistribution}.
 * Extends IntegerDistributionAbstractTest.
 * See class javadoc for IntegerDistributionAbstractTest for details.
 */
public class ZipfDistributionTest extends IntegerDistributionAbstractTest {

    /**
     * Constructor to override default tolerance.
     */
    public ZipfDistributionTest() {
        setTolerance(1e-12);
    }

    @Test(expected=NotStrictlyPositiveException.class)
    public void testPreconditions1() {
        new ZipfDistribution(0, 1);
    }

    @Test(expected=NotStrictlyPositiveException.class)
    public void testPreconditions2() {
        new ZipfDistribution(1, 0);
    }

    //-------------- Implementations for abstract methods -----------------------

    /** Creates the default discrete distribution instance to use in tests. */
    @Override
    public IntegerDistribution makeDistribution() {
        return new ZipfDistribution(10, 1);
    }

    /** Creates the default probability density test input values */
    @Override
    public int[] makeDensityTestPoints() {
        return new int[] {-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
    }

    /**
     * Creates the default probability density test expected values.
     * Reference values are from R, version 2.15.3 (VGAM package 0.9-0).
     */
    @Override
    public double[] makeDensityTestValues() {
        return new double[] {0d, 0d, 0.341417152147, 0.170708576074, 0.113805717382, 0.0853542880369, 0.0682834304295,
            0.0569028586912, 0.0487738788782, 0.0426771440184, 0.0379352391275, 0.0341417152147, 0};
    }

    /**
     * Creates the default logarithmic probability density test expected values.
     * Reference values are from R, version 2.14.1.
     */
    @Override
    public double[] makeLogDensityTestValues() {
        return new double[] {Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
            -1.07465022926458, -1.76779740982453, -2.17326251793269, -2.46094459038447,
            -2.68408814169868, -2.86640969849264, -3.0205603783199, -3.15409177094442,
            -3.2718748066008, -3.37723532225863, Double.NEGATIVE_INFINITY};
    }

    /** Creates the default cumulative probability density test input values */
    @Override
    public int[] makeCumulativeTestPoints() {
        return makeDensityTestPoints();
    }

    /** Creates the default cumulative probability density test expected values */
    @Override
    public double[] makeCumulativeTestValues() {
        return new double[] {0, 0, 0.341417152147, 0.512125728221, 0.625931445604, 0.71128573364,
            0.77956916407, 0.836472022761, 0.885245901639, 0.927923045658, 0.965858284785, 1d, 1d};
        }

    /** Creates the default inverse cumulative probability test input values */
    @Override
    public double[] makeInverseCumulativeTestPoints() {
        return new double[] {0d, 0.001d, 0.010d, 0.025d, 0.050d, 0.3413d, 0.3415d, 0.999d,
                0.990d, 0.975d, 0.950d, 0.900d, 1d};
        }

    /** Creates the default inverse cumulative probability density test expected values */
    @Override
    public int[] makeInverseCumulativeTestValues() {
        return new int[] {1, 1, 1, 1, 1, 1, 2, 10, 10, 10, 9, 8, 10};
    }

    @Test
    public void testMoments() {
        final double tol = 1e-9;
        ZipfDistribution dist;

        dist = new ZipfDistribution(2, 0.5);
        Assert.assertEquals(dist.getNumericalMean(), FastMath.sqrt(2), tol);
        Assert.assertEquals(dist.getNumericalVariance(), 0.24264068711928521, tol);
    }


    /**
     * Test sampling for various number of points and exponents.
     */
    @Test
    public void testSamplingExtended() {
        int sampleSize = 1000;

        int[] numPointsValues = {
            2, 3, 4, 5, 6, 7, 8, 9, 10, 15, 20, 25, 30, 35, 40, 45, 50, 60, 70, 80, 90, 100
        };
        double[] exponentValues = {
            1e-10, 1e-9, 1e-8, 1e-7, 1e-6, 1e-5, 1e-4, 1e-3, 1e-2, 1e-1,
            1. - 1e-9, 1.0, 1. + 1e-9, 1.1, 1.2, 1.3, 1.5, 1.6, 1.7, 1.8, 2.0,
            2.5, 3.0, 4., 5., 6., 7., 8., 9., 10., 20., 30.
        };

        for (int numPoints : numPointsValues) {
            for (double exponent : exponentValues) {
                double weightSum = 0.;
                double[] weights = new double[numPoints];
                for (int i = numPoints; i>=1; i-=1) {
                    weights[i-1] = Math.pow(i, -exponent);
                    weightSum += weights[i-1];
                }

                ZipfDistribution distribution = new ZipfDistribution(numPoints, exponent);
                distribution.reseedRandomGenerator(6); // use fixed seed, the test is expected to fail for more than 50% of all seeds because each test case can fail with probability 0.001, the chance that all test cases do not fail is 0.999^(32*22) = 0.49442874426

                double[] expectedCounts = new double[numPoints];
                long[] observedCounts = new long[numPoints];
                for (int i = 0; i < numPoints; i++) {
                    expectedCounts[i] = sampleSize * (weights[i]/weightSum);
                }
                int[] sample = distribution.sample(sampleSize);
                for (int s : sample) {
                    observedCounts[s-1]++;
                }
                TestUtils.assertChiSquareAccept(expectedCounts, observedCounts, 0.001);
            }
        }
    }

    @Test
    public void testSamplerIntegratePowerFunction() {
        final double tol = 1e-6;
        final double[] exponents = {
            -1e-5, -1e-4, -1e-3, -1e-2, -1e-1, -1e0, -1e1
        };
        final double[] limits = {
            0.5, 1., 1.5, 2., 2.5, 3., 3.5, 4., 4.5, 5., 5.5, 6.0, 6.5, 7.0,
            7.5, 8.0, 8.5, 9.0, 9.5, 10.0
        };

        for (final double exponent : exponents) {
            for (int lowerLimitIndex = 0; lowerLimitIndex < limits.length; ++lowerLimitIndex) {
                final double lowerLimit = limits[lowerLimitIndex];
                for (int upperLimitIndex = lowerLimitIndex+1; upperLimitIndex < limits.length; ++upperLimitIndex) {
                    final double upperLimit = limits[upperLimitIndex];
                    final double result1 = new SimpsonIntegrator().integrate(10000, new UnivariateFunction() {
                        @Override
                        public double value(double x) {
                            return Math.pow(x, exponent);
                        }
                    }, lowerLimit, upperLimit);

                    final double result2 =
                            ZipfRejectionSampler.integratePowerFunction(exponent, lowerLimit, upperLimit);
                    assertEquals(result1, result2, (result1+result2)*tol);
                }
            }
        }
    }

    @Test
    public void testSamplerAcceptanceRate() {
        final double tol = 1e-12;
        final double[] exponents = {
            1e-5, 1e-4, 1e-3, 1e-2, 1e-1, 1e0, 2e0, 5e0, 1e1, 1e2, 1e3
        };
        final int[] values = {
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14
        };
        final int numberOfElements = 1000;
        for (final double exponent : exponents) {
            ZipfRejectionSampler sampler = new ZipfRejectionSampler(numberOfElements, exponent);
            for (final int value : values) {
                double expected = FastMath.pow(value, -exponent);
                double result = sampler.acceptanceRateForTailSample(value) *
                                ZipfRejectionSampler.integratePowerFunction(-exponent, value - 0.5, value + 0.5);
                TestUtils.assertRelativelyEquals(expected, result, tol);
                assertTrue(result <= 1.); // test Jensen's inequality
            }
        }
    }

    @Test
    public void testSamplerInverseInstrumentalDistribution() {
        final double tol = 1e-14;
        final double[] exponentValues = {
            1e-5, 1e-4, 1e-3, 1e-2, 1e-1, 1e0, 2E0, 3e0, 4e0, 5e0, 6., 7., 8., 9., 10., 50.
        };
        final double[] qValues = {
            0., 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0
        };
        final int[] numberOfElementsValues = {
            2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 30, 40, 50, 100
        };

        for (final double exponent : exponentValues) {
            for (final int numberOfElements : numberOfElementsValues) {
                final ZipfRejectionSampler sampler = new ZipfRejectionSampler(numberOfElements, exponent);
                for (final double q : qValues) {
                    int result = sampler.sampleFromInstrumentalDistributionTail(q);
                    double total =
                            ZipfRejectionSampler.integratePowerFunction(-exponent, 1.5, numberOfElements + 0.5);
                    double lowerBound =
                            ZipfRejectionSampler.integratePowerFunction(-exponent, 1.5, result - 0.5) / total;
                    double upperBound =
                            ZipfRejectionSampler.integratePowerFunction(-exponent, 1.5, result + 0.5) / total;
                    assertTrue(lowerBound <= q*(1.+tol));
                    assertTrue(upperBound >= q*(1.-tol));
                }
            }
        }
    }

    @Test
    public void testSamplerHelper1() {
        final double tol = 1e-14;
        final double[] qValues = {
            0., 1e-12, 1e-11, 1e-10, 1e-9, 9e-9, 1e-8, 1e-7, 1e-6, 1e-5, 1e-4,
            1e-3, 1e-2, 1e-1, 1e0
        };
        final double[] xValues = {
            -Double.MAX_VALUE, -1e10, -1e9, -1e8, -1e7, -1e6, -1e5, -1e4, -1e3,
            -1e2, -1e1, -1e0, -1e-1, -1e-2, -1e-3, -1e-4, -1e-5, -1e-6, -1e-7,
            -1e-8, -1e-9, -1e-10, -Double.MIN_VALUE, 0.0, Double.MIN_VALUE,
            1e-10, 1e-9, 1e-8, 1e-7, 1e-6, 1e-5, 1e-4, 1e-3, 1e-2, 1e-1, 1e0,
            1e1, 1e2, 1e3, 1e4, 1e5, 1e6, 1e7, 1e8, 1e9, 1e10, Double.MAX_VALUE
        };

        for (final double q : qValues) {
            for(final double x : xValues) {
                double calculated = ZipfRejectionSampler.helper1(q, x);
                TestUtils.assertRelativelyEquals((1.-q)+q*Math.exp(x), FastMath.exp(calculated*x), tol);
            }
        }
    }

    @Test
    public void testSamplerHelper2() {
        final double tol = 1e-12;
        final double[] testValues = {
            -1e0, -1e-1, -1e-2, -1e-3, -1e-4, -1e-5, -1e-6, -1e-7, -1e-8,
            -1e-9, -1e-10, -1e-11, 0., 1e-11, 1e-10, 1e-9, 1e-8, 1e-7, 1e-6,
            1e-5, 1e-4, 1e-3, 1e-2, 1e-1, 1e0
        };
        for (double testValue : testValues) {
            final double expected = FastMath.expm1(testValue);
            TestUtils.assertRelativelyEquals(expected, ZipfRejectionSampler.helper2(testValue)*testValue, tol);
        }
    }

    @Ignore
    @Test
    public void testSamplerPerformance() {
        int[] numPointsValues = {1, 2, 5, 10, 100, 1000, 10000};
        double[] exponentValues = {1e-3, 1e-2, 1e-1, 1., 2., 5., 10.};
        int  numGeneratedSamples = 1000000;

        long sum = 0;

        for (int numPoints : numPointsValues) {
            for (double exponent : exponentValues) {
                long start = System.currentTimeMillis();
                final int[] randomNumberCounter = new int[1];

                RandomGenerator randomGenerator  = new AbstractRandomGenerator() {

                    private final RandomGenerator r = new Well1024a(0L);

                    @Override
                    public void setSeed(long seed) {
                    }

                    @Override
                    public double nextDouble() {
                        randomNumberCounter[0]+=1;
                        return r.nextDouble();
                    }
                };

                final ZipfDistribution distribution = new ZipfDistribution(randomGenerator, numPoints, exponent);
                for (int i = 0; i < numGeneratedSamples; ++i) {
                    sum += distribution.sample();
                }

                long end = System.currentTimeMillis();
                System.out.println("n = " + numPoints + ", exponent = " + exponent + ", avg number consumed random values = " + (double)(randomNumberCounter[0])/numGeneratedSamples + ", measured time = " + (end-start)/1000. + "s");
            }
        }
        System.out.println(sum);
    }

}
