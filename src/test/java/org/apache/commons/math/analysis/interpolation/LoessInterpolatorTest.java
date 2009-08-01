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
package org.apache.commons.math.analysis.interpolation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.math.MathException;
import org.junit.Test;

/**
 * Test of the LoessInterpolator class.
 */
public class LoessInterpolatorTest {

    @Test
    public void testOnOnePoint() throws MathException {
        double[] xval = {0.5};
        double[] yval = {0.7};
        double[] res = new LoessInterpolator().smooth(xval, yval);
        assertEquals(1, res.length);
        assertEquals(0.7, res[0], 0.0);
    }

    @Test
    public void testOnTwoPoints() throws MathException {
        double[] xval = {0.5, 0.6};
        double[] yval = {0.7, 0.8};
        double[] res = new LoessInterpolator().smooth(xval, yval);
        assertEquals(2, res.length);
        assertEquals(0.7, res[0], 0.0);
        assertEquals(0.8, res[1], 0.0);
    }

    @Test
    public void testOnStraightLine() throws MathException {
        double[] xval = {1,2,3,4,5};
        double[] yval = {2,4,6,8,10};
        LoessInterpolator li = new LoessInterpolator(0.6, 2);
        double[] res = li.smooth(xval, yval);
        assertEquals(5, res.length);
        for(int i = 0; i < 5; ++i) {
            assertEquals(yval[i], res[i], 1e-8);
        }
    }

    @Test
    public void testOnDistortedSine() throws MathException {
        int numPoints = 100;
        double[] xval = new double[numPoints];
        double[] yval = new double[numPoints];
        double xnoise = 0.1;
        double ynoise = 0.2;

        generateSineData(xval, yval, xnoise, ynoise);

        LoessInterpolator li = new LoessInterpolator(0.3, 4);

        double[] res = li.smooth(xval, yval);

        // Check that the resulting curve differs from
        // the "real" sine less than the jittered one

        double noisyResidualSum = 0;
        double fitResidualSum = 0;

        for(int i = 0; i < numPoints; ++i) {
            double expected = Math.sin(xval[i]);
            double noisy = yval[i];
            double fit = res[i];

            noisyResidualSum += Math.pow(noisy - expected, 2);
            fitResidualSum += Math.pow(fit - expected, 2);
        }

        assertTrue(fitResidualSum < noisyResidualSum);
    }

    @Test
    public void testIncreasingBandwidthIncreasesSmoothness() throws MathException {
        int numPoints = 100;
        double[] xval = new double[numPoints];
        double[] yval = new double[numPoints];
        double xnoise = 0.1;
        double ynoise = 0.1;

        generateSineData(xval, yval, xnoise, ynoise);

        // Check that variance decreases as bandwidth increases

        double[] bandwidths = {0.1, 0.5, 1.0};
        double[] variances = new double[bandwidths.length];
        for (int i = 0; i < bandwidths.length; i++) {
            double bw = bandwidths[i];

            LoessInterpolator li = new LoessInterpolator(bw, 4);

            double[] res = li.smooth(xval, yval);

            for (int j = 1; j < res.length; ++j) {
                variances[i] += Math.pow(res[j] - res[j-1], 2);
            }
        }

        for(int i = 1; i < variances.length; ++i) {
            assertTrue(variances[i] < variances[i-1]);
        }
    }

    @Test
    public void testIncreasingRobustnessItersIncreasesSmoothnessWithOutliers() throws MathException {
        int numPoints = 100;
        double[] xval = new double[numPoints];
        double[] yval = new double[numPoints];
        double xnoise = 0.1;
        double ynoise = 0.1;

        generateSineData(xval, yval, xnoise, ynoise);

        // Introduce a couple of outliers
        yval[numPoints/3] *= 100;
        yval[2 * numPoints/3] *= -100;

        // Check that variance decreases as the number of robustness
        // iterations increases

        double[] variances = new double[4];
        for (int i = 0; i < 4; i++) {
            LoessInterpolator li = new LoessInterpolator(0.3, i);

            double[] res = li.smooth(xval, yval);

            for (int j = 1; j < res.length; ++j) {
                variances[i] += Math.abs(res[j] - res[j-1]);
            }
        }

        for(int i = 1; i < variances.length; ++i) {
            assertTrue(variances[i] < variances[i-1]);
        }
    }

    @Test
    public void testUnequalSizeArguments() {
        try {
            new LoessInterpolator().smooth(new double[] {1,2,3}, new double[] {1,2,3,4});
            fail();
        } catch(MathException e) {
            // Expected
        }
    }

    @Test
    public void testEmptyData() {
        try {
            new LoessInterpolator().smooth(new double[] {}, new double[] {});
            fail();
        } catch(MathException e) {
            // Expected
        }
    }

    @Test
    public void testNonStrictlyIncreasing() {
        try {
            new LoessInterpolator().smooth(new double[] {4,3,1,2}, new double[] {3,4,5,6});
            fail();
        } catch(MathException e) {
            // Expected
        }
        try {
            new LoessInterpolator().smooth(new double[] {1,2,2,3}, new double[] {3,4,5,6});
            fail();
        } catch(MathException e) {
            // Expected
        }
    }

    @Test
    public void testNotAllFiniteReal() {
        try {
            new LoessInterpolator().smooth(new double[] {1,2,Double.NaN}, new double[] {3,4,5});
            fail();
        } catch(MathException e) {
            // Expected
        }
        try {
            new LoessInterpolator().smooth(new double[] {1,2,Double.POSITIVE_INFINITY}, new double[] {3,4,5});
            fail();
        } catch(MathException e) {
            // Expected
        }
        try {
            new LoessInterpolator().smooth(new double[] {1,2,Double.NEGATIVE_INFINITY}, new double[] {3,4,5});
            fail();
        } catch(MathException e) {
            // Expected
        }
        try {
            new LoessInterpolator().smooth(new double[] {3,4,5}, new double[] {1,2,Double.NaN});
            fail();
        } catch(MathException e) {
            // Expected
        }
        try {
            new LoessInterpolator().smooth(new double[] {3,4,5}, new double[] {1,2,Double.POSITIVE_INFINITY});
            fail();
        } catch(MathException e) {
            // Expected
        }
        try {
            new LoessInterpolator().smooth(new double[] {3,4,5}, new double[] {1,2,Double.NEGATIVE_INFINITY});
            fail();
        } catch(MathException e) {
            // Expected
        }
    }

    @Test
    public void testInsufficientBandwidth() {
        try {
            LoessInterpolator li = new LoessInterpolator(0.1, 3);
            li.smooth(new double[] {1,2,3,4,5,6,7,8,9,10,11,12}, new double[] {1,2,3,4,5,6,7,8,9,10,11,12});
            fail();
        } catch(MathException e) {
            // Expected
        }
    }

    @Test
    public void testCompletelyIncorrectBandwidth() {
        try {
            new LoessInterpolator(-0.2, 3);
            fail();
        } catch(MathException e) {
            // Expected
        }
        try {
            new LoessInterpolator(1.1, 3);
            fail();
        } catch(MathException e) {
            // Expected
        }
    }

    private void generateSineData(double[] xval, double[] yval, double xnoise, double ynoise) {
        double dx = 2 * Math.PI / xval.length;
        double x = 0;
        for(int i = 0; i < xval.length; ++i) {
            xval[i] = x;
            yval[i] = Math.sin(x) + (2 * Math.random() - 1) * ynoise;
            x += dx * (1 + (2 * Math.random() - 1) * xnoise);
        }
    }

}
