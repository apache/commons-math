/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.commons.math.stat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test cases for the {@link StatUtils} class.
 * @version $Revision: 1.7 $ $Date: 2003/10/13 08:08:38 $
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
    private int kClass = StoreUnivariate.LEPTOKURTIC;
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