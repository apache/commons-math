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

package org.apache.commons.math4.legacy.stat.descriptive.moment;

import org.apache.commons.math4.legacy.TestUtils;
import org.apache.commons.math4.legacy.exception.NullArgumentException;
import org.apache.commons.math4.legacy.stat.StatUtils;
import org.apache.commons.math4.legacy.stat.descriptive.moment.SemiVariance.Direction;
import org.junit.Assert;
import org.junit.Test;


public class SemiVarianceTest {

    @Test
    public void testInsufficientData() {
        double[] nothing = null;
        SemiVariance sv = new SemiVariance();
        try {
            sv.evaluate(nothing);
            Assert.fail("null is not a valid data array.");
        } catch (NullArgumentException nae) {
        }

        try {
            sv.evaluate(nothing, 44.0);
            Assert.fail("null is not a valid data array.");
        } catch (NullArgumentException nae) {
        }
        nothing = new double[] {};
        Assert.assertTrue(Double.isNaN(sv.evaluate(nothing)));
    }

    @Test
    public void testProperties() {
        SemiVariance sv = new SemiVariance();
        Assert.assertEquals(Direction.DOWNSIDE, sv.getVarianceDirection());
        sv.setVarianceDirection(Direction.UPSIDE);
        Assert.assertEquals(Direction.UPSIDE, sv.getVarianceDirection());
        Assert.assertTrue(sv.isBiasCorrected());
        sv.setBiasCorrected(false);
        Assert.assertFalse(sv.isBiasCorrected());
    }

    @Test
    public void testCopy() {
        for (Direction d : Direction.values()) {
            for (boolean b : new boolean[] {true, false}) {
                SemiVariance sv = new SemiVariance();
                sv.setVarianceDirection(d);
                sv.setBiasCorrected(b);
                SemiVariance copy = sv.copy();
                Assert.assertEquals(d, copy.getVarianceDirection());
                Assert.assertEquals(b, copy.isBiasCorrected());
            }
        }
    }

    @Test
    public void testSingleDown() {
        SemiVariance sv = new SemiVariance();
        double[] values = { 50.0d };
        double singletest = sv.evaluate(values);
        Assert.assertEquals(0.0d, singletest, 0);
    }

    @Test
    public void testSingleUp() {
        SemiVariance sv = new SemiVariance();
        sv.setVarianceDirection(Direction.UPSIDE);
        Assert.assertEquals(Direction.UPSIDE, sv.getVarianceDirection());
        double[] values = { 50.0d };
        double singletest = sv.evaluate(values);
        Assert.assertEquals(0.0d, singletest, 0);
    }

    @Test
    public void testSample() {
        final double[] values = { -2.0d, 2.0d, 4.0d, -2.0d, 22.0d, 11.0d, 3.0d, 14.0d, 5.0d };
        final int length = values.length;
        final double mean = StatUtils.mean(values); // 6.333...
        final SemiVariance sv = new SemiVariance();  // Default bias correction is true
        final double downsideSemiVariance = sv.evaluate(values); // Downside is the default
        final double expectedDownside = TestUtils.sumSquareDev(new double[] {-2d, 2d, 4d, -2d, 3d, 5d}, mean) / (length - 1);
        Assert.assertEquals(expectedDownside, downsideSemiVariance, 1E-14);

        sv.setVarianceDirection(Direction.UPSIDE);
        final double upsideSemiVariance = sv.evaluate(values);
        final double expectedUpside = TestUtils.sumSquareDev(new double[] {22d, 11d, 14d}, mean) / (length - 1);
        Assert.assertEquals(expectedUpside, upsideSemiVariance, 1E-14);

        // Test sub-range
        final double[] values2 = new double[values.length + 2];
        System.arraycopy(values, 0, values2, 1, values.length);
        Assert.assertEquals(expectedUpside, sv.evaluate(values2, 1, values.length), 1E-14);
        sv.setVarianceDirection(Direction.DOWNSIDE);
        Assert.assertEquals(expectedDownside, sv.evaluate(values2, 1, values.length), 1E-14);
    }

    @Test
    public void testPopulation() {
        double[] values = { -2.0d, 2.0d, 4.0d, -2.0d, 22.0d, 11.0d, 3.0d, 14.0d, 5.0d };
        SemiVariance sv = new SemiVariance();
        sv.setBiasCorrected(false);

        double singletest = sv.evaluate(values);
        Assert.assertEquals(19.556d, singletest, 0.01d);

        sv.setVarianceDirection(Direction.UPSIDE);
        singletest = sv.evaluate(values);
        Assert.assertEquals(36.222d, singletest, 0.01d);
    }

    @Test
    public void testNonMeanCutoffs() {
        double[] values = { -2.0d, 2.0d, 4.0d, -2.0d, 22.0d, 11.0d, 3.0d, 14.0d, 5.0d };
        SemiVariance sv = new SemiVariance(); 
        // Turn off bias correction - use df = length
        sv.setBiasCorrected(false);

        double singletest = sv.evaluate(values, 1.0d, 0, values.length);
        final double expectedDownside = TestUtils.sumSquareDev(new double[] { -2d, -2d }, 1.0d) / values.length;
        Assert.assertEquals(expectedDownside, singletest, 0.01d);

        sv.setVarianceDirection(Direction.UPSIDE);
        singletest = sv.evaluate(values, 3.0d, 0, values.length);
        final double expectedUpside = TestUtils.sumSquareDev(new double[] { 4d, 22d, 11d, 14d, 5d }, 3.0d) / values.length;
        Assert.assertEquals(expectedUpside, singletest, 0.01d);

        // Test sub-range
        final double[] values2 = new double[values.length + 2];
        System.arraycopy(values, 0, values2, 1, values.length);
        Assert.assertEquals(expectedUpside, sv.evaluate(values2, 3.0d, 1, values.length), 1E-14);
        sv.setVarianceDirection(Direction.DOWNSIDE);
        Assert.assertEquals(expectedDownside, sv.evaluate(values2, 1.0d, 1, values.length), 1E-14);
    }

    /**
     * Check that the lower + upper semivariance against the mean sum to the
     * variance.
     */
    @Test
    public void testVarianceDecompMeanCutoff() {
        double[] values = { -2.0d, 2.0d, 4.0d, -2.0d, 22.0d, 11.0d, 3.0d, 14.0d, 5.0d };
        double variance = StatUtils.variance(values);
        SemiVariance sv = new SemiVariance();
        sv.setVarianceDirection(Direction.DOWNSIDE);
        final double lower = sv.evaluate(values);
        sv.setVarianceDirection(Direction.UPSIDE);
        final double upper = sv.evaluate(values);
        Assert.assertEquals(variance, lower + upper, 10e-12);
    }

    /**
     * Check that upper and lower semivariances against a cutoff sum to the sum
     * of squared deviations of the full set of values against the cutoff
     * divided by df = length - 1 (assuming bias-corrected).
     */
    @Test
    public void testVarianceDecompNonMeanCutoff() {
        double[] values = { -2.0d, 2.0d, 4.0d, -2.0d, 22.0d, 11.0d, 3.0d, 14.0d, 5.0d };
        double target = 0;
        double totalSumOfSquares = TestUtils.sumSquareDev(values, target);
        SemiVariance sv = new SemiVariance();
        sv.setVarianceDirection(Direction.DOWNSIDE);
        double lower = sv.evaluate(values, target);
        sv.setVarianceDirection(Direction.UPSIDE);
        double upper = sv.evaluate(values, target);
        Assert.assertEquals(totalSumOfSquares / (values.length - 1), lower + upper, 10e-12);
    }

    @Test
    public void testNoVariance() {
        final double[] values = {100d, 100d, 100d, 100d};
        SemiVariance sv = new SemiVariance();
        Assert.assertEquals(0, sv.evaluate(values), 10E-12);
        Assert.assertEquals(0, sv.evaluate(values, 1, 2), 10E-12);
        Assert.assertEquals(0, sv.evaluate(values, 100d), 10E-12);
        Assert.assertEquals(0, sv.evaluate(values, 100d, 1, 2), 10E-12);
    }
}
