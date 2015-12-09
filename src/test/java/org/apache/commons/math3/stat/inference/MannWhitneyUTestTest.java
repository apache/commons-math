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
package org.apache.commons.math3.stat.inference;

import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for the MannWhitneyUTestImpl class.
 *
 */

public class MannWhitneyUTestTest {

    protected MannWhitneyUTest testStatistic = new MannWhitneyUTest();

    @Test
    public void testMannWhitneyUSimple() {
        /* Target values computed using R version 2.11.1
         * x <- c(19, 22, 16, 29, 24)
         * y <- c(20, 11, 17, 12)
         * wilcox.test(x, y, alternative = "two.sided", mu = 0, paired = FALSE, exact = FALSE, correct = FALSE)
         * W = 17, p-value = 0.08641
         */
        final double x[] = {19, 22, 16, 29, 24};
        final double y[] = {20, 11, 17, 12};

        Assert.assertEquals(17, testStatistic.mannWhitneyU(x, y), 1e-10);
        Assert.assertEquals(0.08641, testStatistic.mannWhitneyUTest(x, y), 1e-5);
    }


    @Test
    public void testMannWhitneyUInputValidation() {
        /* Samples must be present, i.e. length > 0
         */
        try {
            testStatistic.mannWhitneyUTest(new double[] { }, new double[] { 1.0 });
            Assert.fail("x does not contain samples (exact), NoDataException expected");
        } catch (NoDataException ex) {
            // expected
        }

        try {
            testStatistic.mannWhitneyUTest(new double[] { 1.0 }, new double[] { });
            Assert.fail("y does not contain samples (exact), NoDataException expected");
        } catch (NoDataException ex) {
            // expected
        }

        /*
         * x and y is null
         */
        try {
            testStatistic.mannWhitneyUTest(null, null);
            Assert.fail("x and y is null (exact), NullArgumentException expected");
        } catch (NullArgumentException ex) {
            // expected
        }

        try {
            testStatistic.mannWhitneyUTest(null, null);
            Assert.fail("x and y is null (asymptotic), NullArgumentException expected");
        } catch (NullArgumentException ex) {
            // expected
        }

        /*
         * x or y is null
         */
        try {
            testStatistic.mannWhitneyUTest(null, new double[] { 1.0 });
            Assert.fail("x is null (exact), NullArgumentException expected");
        } catch (NullArgumentException ex) {
            // expected
        }

        try {
            testStatistic.mannWhitneyUTest(new double[] { 1.0 }, null);
            Assert.fail("y is null (exact), NullArgumentException expected");
        } catch (NullArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testBigDataSet() {
        double[] d1 = new double[1500];
        double[] d2 = new double[1500];
        for (int i = 0; i < 1500; i++) {
            d1[i] = 2 * i;
            d2[i] = 2 * i + 1;
        }
        double result = testStatistic.mannWhitneyUTest(d1, d2);
        Assert.assertTrue(result > 0.1);
    }

    @Test
    public void testBigDataSetOverflow() {
        // MATH-1145
        double[] d1 = new double[110000];
        double[] d2 = new double[110000];
        for (int i = 0; i < 110000; i++) {
            d1[i] = i;
            d2[i] = i;
        }
        double result = testStatistic.mannWhitneyUTest(d1, d2);
        Assert.assertTrue(result == 1.0);
    }
}
