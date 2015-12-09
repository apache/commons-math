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
 * @since 3.3
 */
public class GeometricDistributionTest extends IntegerDistributionAbstractTest {

    /**
     * Constructor to override default tolerance.
     */
    public GeometricDistributionTest() {
        setTolerance(1e-12);
    }

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
                           19, 20, 21, 22, 23, 24, 25, 26, 27, 28 };
    }

    /**
     * Creates the default probability density test expected values.
     * Reference values are from R, version version 2.15.3.
     */
    @Override
    public double[] makeDensityTestValues() {
        return new double[] {
            0d, 0.4, 0.24, 0.144, 0.0864, 0.05184, 0.031104, 0.0186624,
            0.01119744, 0.006718464, 0.0040310784, 0.00241864704,
            0.001451188224,0.0008707129344, 0.00052242776064, 0.000313456656384,
            0.00018807399383, 0.000112844396298, 6.77066377789e-05, 4.06239826674e-05,
            2.43743896004e-05, 1.46246337603e-05, 8.77478025615e-06, 5.26486815369e-06,
            3.15892089221e-06, 1.89535253533e-06, 1.1372115212e-06, 6.82326912718e-07,
            4.09396147631e-07, 2.45637688579e-07
        };
    }

    /**
     * Creates the default log probability density test expected values.
     * Reference values are from R, version version 2.14.1.
     */
    @Override
    public double[] makeLogDensityTestValues() {
        return new double[] {
            Double.NEGATIVE_INFINITY, -0.916290731874155, -1.42711635564015, -1.93794197940614,
            -2.44876760317213, -2.95959322693812, -3.47041885070411, -3.9812444744701,
            -4.49207009823609, -5.00289572200208, -5.51372134576807, -6.02454696953406,
            -6.53537259330005, -7.04619821706604, -7.55702384083203, -8.06784946459802,
            -8.57867508836402, -9.08950071213001, -9.600326335896, -10.111151959662,
            -10.621977583428, -11.132803207194, -11.64362883096, -12.154454454726,
            -12.6652800784919, -13.1761057022579, -13.6869313260239, -14.1977569497899,
            -14.7085825735559, -15.2194081973219
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
        final double[] densities = makeDensityTestValues();
        final int n = densities.length;
        final double[] ret = new double[n];
        ret[0] = densities[0];
        for (int i = 1; i < n; i++) {
            ret[i] = ret[i - 1] + densities[i];
        }
        return ret;
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
            0.990, 0.995, 1.000
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
            5, 5, 6, 6, 6, 6, 7, 7, 8, 9, 10, Integer.MAX_VALUE
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
