/*
 * Copyright 2003-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.stat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test cases for the {@link StatUtils} class.
 * @version $Revision: 1.13 $ $Date: 2004/02/21 21:35:17 $
 */

public final class StatUtilsTest extends TestCase {

    private double one = 1;
    private float two = 2;
    private int three = 3;
    private double mean = 2;
    private double sumSq = 18;
    private double sum = 8;
    private double var = 0.666666666666666666667;
    private double std = Math.sqrt(var);
    private double n = 4;
    private double min = 1;
    private double max = 3;
    private double skewness = 0;
    private double kurtosis = 0.5;
    private int kClass = DescriptiveStatistics.LEPTOKURTIC;
    private double tolerance = 10E-15;

    public StatUtilsTest(String name) {
        super(name);
    }

    public void setUp() {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(StatUtilsTest.class);
        suite.setName("StatUtil Tests");
        return suite;
    }

    /** test stats */
    public void testStats() {
        double[] values = new double[] { one, two, two, three };
        assertEquals("sum", sum, StatUtils.sum(values), tolerance);
        assertEquals("sumsq", sumSq, StatUtils.sumSq(values), tolerance);
        assertEquals("var", var, StatUtils.variance(values), tolerance);
        assertEquals("mean", mean, StatUtils.mean(values), tolerance);
        assertEquals("min", min, StatUtils.min(values), tolerance);
        assertEquals("max", max, StatUtils.max(values), tolerance);
    }

    public void testN0andN1Conditions() throws Exception {
        double[] values = new double[0];

        assertTrue(
            "Mean of n = 0 set should be NaN",
            Double.isNaN(StatUtils.mean(values)));
        assertTrue(
            "Variance of n = 0 set should be NaN",
            Double.isNaN(StatUtils.variance(values)));

        values = new double[] { one };

        assertTrue(
            "Mean of n = 1 set should be value of single item n1",
            StatUtils.mean(values) == one);
        assertTrue(
            "Variance of n = 1 set should be zero",
            StatUtils.variance(values) == 0);
    }

    public void testSkewAndKurtosis() {

        double[] values =
            {
                12.5,
                12,
                11.8,
                14.2,
                14.9,
                14.5,
                21,
                8.2,
                10.3,
                11.3,
                14.1,
                9.9,
                12.2,
                12,
                12.1,
                11,
                19.8,
                11,
                10,
                8.8,
                9,
                12.3 };

        assertEquals("mean", 12.40455, StatUtils.mean(values), 0.0001);
        assertEquals("variance", 10.00236, StatUtils.variance(values), 0.0001);
    }

    public void testProductAndGeometricMean() throws Exception {
        double[] values = { 1.0, 2.0, 3.0, 4.0 };

        assertEquals(
            "Product not expected",
            24.0,
            StatUtils.product(values),
            Double.MIN_VALUE);
    }

    public void testArrayIndexConditions() throws Exception {
        double[] values = { 1.0, 2.0, 3.0, 4.0 };

        assertEquals(
            "Sum not expected",
            5.0,
            StatUtils.sum(values, 1, 2),
            Double.MIN_VALUE);
        assertEquals(
            "Sum not expected",
            3.0,
            StatUtils.sum(values, 0, 2),
            Double.MIN_VALUE);
        assertEquals(
            "Sum not expected",
            7.0,
            StatUtils.sum(values, 2, 2),
            Double.MIN_VALUE);

        try {
            StatUtils.sum(values, 2, 3);
            assertTrue("Didn't throw exception", false);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            StatUtils.sum(values, -1, 2);
            assertTrue("Didn't throw exception", false);
        } catch (Exception e) {
            assertTrue(true);
        }

    }
}