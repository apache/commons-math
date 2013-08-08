/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.commons.math3.distribution;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for GeometricDistribution.
 * <p>
 * See class javadoc for IntegerDistributionAbstractTest for details.
 *
 * @version $Id$
 * @since 3.3
 */
public class GeometricDistributionTest extends IntegerDistributionAbstractTest {

    // -------------- Implementations for abstract methods --------------------

    /** Creates the default discrete distribution instance to use in tests. */
    @Override
    public IntegerDistribution makeDistribution() {
        return new GeometricDistribution(0.40);
    }

    /** Creates the default probability density test input values */
    @Override
    public int[] makeDensityTestPoints() {
        return new int[] { -1,  0,  1,  2,  3,  4,  5,  6,  7,  8,
                            9, 10, 11, 12, 13, 14, 15, 16, 17, 18,
                           29, 20, 21, 22, 23, 24, 25, 26, 27, 28 };
    }

    /** Creates the default probability density test expected values */
    @Override
    public double[] makeDensityTestValues() {
        return new double[] {
            0.000000e+00, 4.000000e-01, 2.400000e-01, 1.440000e-01,
            8.640000e-02, 5.184000e-02, 3.110400e-02, 1.866240e-02,
            1.119744e-02, 6.718464e-03, 4.031078e-03, 2.418647e-03,
            1.451188e-03, 8.707129e-04, 5.224278e-04, 3.134567e-04,
            1.880740e-04, 1.128444e-04, 6.770664e-05, 4.062398e-05,
            2.437439e-05, 1.462463e-05, 8.774780e-06, 5.264868e-06,
            3.158921e-06, 1.895353e-06, 1.137212e-06, 6.823269e-07,
            4.093961e-07, 2.456377e-07
        };
    }

    /** Creates the default cumulative probability density test input values */
    @Override
    public int[] makeCumulativeTestPoints() {
        return makeDensityTestPoints();
    }

    /** Creates the default cumulative probability density test expected values */
    @Override
    public double[] makeCumulativeTestValues() {
        return new double[] {
            0.0000000, 0.4000000, 0.6400000, 0.7840000, 0.8704000,
            0.9222400, 0.9533440, 0.9720064, 0.9832038, 0.9899223,
            0.9939534, 0.9963720, 0.9978232, 0.9986939, 0.9992164,
            0.9995298, 0.9997179, 0.9998307, 0.9998984, 0.9999391,
            0.9999634, 0.9999781, 0.9999868, 0.9999921, 0.9999953,
            0.9999972, 0.9999983, 0.9999990, 0.9999994, 0.9999996
        };
    }

    /** Creates the default inverse cumulative probability test input values */
    @Override
    public double[] makeInverseCumulativeTestPoints() {
        return new double[] {
            0.000, 0.005, 0.010, 0.015, 0.020, 0.025, 0.030, 0.035, 0.040,
            0.045, 0.050, 0.055, 0.060, 0.065, 0.070, 0.075, 0.080, 0.085,
            0.090, 0.095, 0.100, 0.105, 0.110, 0.115, 0.120, 0.125, 0.130,
            0.135, 0.140, 0.145, 0.150, 0.155, 0.160, 0.165, 0.170, 0.175,
            0.180, 0.185, 0.190, 0.195, 0.200, 0.205, 0.210, 0.215, 0.220,
            0.225, 0.230, 0.235, 0.240, 0.245, 0.250, 0.255, 0.260, 0.265,
            0.270, 0.275, 0.280, 0.285, 0.290, 0.295, 0.300, 0.305, 0.310,
            0.315, 0.320, 0.325, 0.330, 0.335, 0.340, 0.345, 0.350, 0.355,
            0.360, 0.365, 0.370, 0.375, 0.380, 0.385, 0.390, 0.395, 0.400,
            0.405, 0.410, 0.415, 0.420, 0.425, 0.430, 0.435, 0.440, 0.445,
            0.450, 0.455, 0.460, 0.465, 0.470, 0.475, 0.480, 0.485, 0.490,
            0.495, 0.500, 0.505, 0.510, 0.515, 0.520, 0.525, 0.530, 0.535,
            0.540, 0.545, 0.550, 0.555, 0.560, 0.565, 0.570, 0.575, 0.580,
            0.585, 0.590, 0.595, 0.600, 0.605, 0.610, 0.615, 0.620, 0.625,
            0.630, 0.635, 0.640, 0.645, 0.650, 0.655, 0.660, 0.665, 0.670,
            0.675, 0.680, 0.685, 0.690, 0.695, 0.700, 0.705, 0.710, 0.715,
            0.720, 0.725, 0.730, 0.735, 0.740, 0.745, 0.750, 0.755, 0.760,
            0.765, 0.770, 0.775, 0.780, 0.785, 0.790, 0.795, 0.800, 0.805,
            0.810, 0.815, 0.820, 0.825, 0.830, 0.835, 0.840, 0.845, 0.850,
            0.855, 0.860, 0.865, 0.870, 0.875, 0.880, 0.885, 0.890, 0.895,
            0.900, 0.905, 0.910, 0.915, 0.920, 0.925, 0.930, 0.935, 0.940,
            0.945, 0.950, 0.955, 0.960, 0.965, 0.970, 0.975, 0.980, 0.985,
            0.990, 0.995
        };
    }

    /**
     * Creates the default inverse cumulative probability density test expected
     * values
     */
    @Override
    public int[] makeInverseCumulativeTestValues() {
        return new int[] {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
            3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5,
            5, 5, 6, 6, 6, 6, 7, 7, 8, 9, 10
        };
    }

    // ----------------- Additional test cases ---------------------------------

    @Test
    public void testMoments() {
        final double tol = 1e-9;
        GeometricDistribution dist;

        dist = new GeometricDistribution(0.5);
        Assert.assertEquals(dist.getNumericalMean(), (1.0d - 0.5d) / 0.5d, tol);
        Assert.assertEquals(dist.getNumericalVariance(), (1.0d - 0.5d) / (0.5d * 0.5d), tol);

        dist = new GeometricDistribution(0.3);
        Assert.assertEquals(dist.getNumericalMean(), (1.0d - 0.3d) / 0.3d, tol);
        Assert.assertEquals(dist.getNumericalVariance(), (1.0d - 0.3d) / (0.3d * 0.3d), tol);
    }
}
