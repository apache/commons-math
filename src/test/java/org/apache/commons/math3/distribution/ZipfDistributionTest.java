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

package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;

import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link ZipfDistribution}.
 * Extends IntegerDistributionAbstractTest.  See class javadoc for
 * IntegerDistributionAbstractTest for details.
 *
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
     *  Reference values are from R, version 2.15.3 (VGAM package 0.9-0).
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
}
