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
package org.apache.commons.math.stat.inference;

import junit.framework.TestCase;

/**
 * Test cases for the ChiSquareTestImpl class.
 *
 * @version $Revision$ $Date$
 */

public class MannWhitneyUTestTest extends TestCase {

    protected MannWhitneyUTest testStatistic = new MannWhitneyUTestImpl();

    public MannWhitneyUTestTest(String name) {
        super(name);
    }

    public void testMannWhitneyUSimple() throws Exception {
        /* Target values computed using R version 2.11.1
         * x <- c(19, 22, 16, 29, 24)
         * y <- c(20, 11, 17, 12)
         * wilcox.test(x, y, alternative = "two.sided", mu = 0, paired = FALSE, exact = FALSE, correct = FALSE)
         * W = 17, p-value = 0.08641 
         */
        final double x[] = {19, 22, 16, 29, 24};
        final double y[] = {20, 11, 17, 12};
        
        assertEquals(17, testStatistic.mannWhitneyU(x, y), 1e-10);
        assertEquals(0.08641, testStatistic.mannWhitneyUTest(x, y), 1e-5);
    }


    public void testMannWhitneyUInputValidation() throws Exception {
        /* Samples must be present, i.e. length > 0
         */
        try {
            testStatistic.mannWhitneyUTest(new double[] { }, new double[] { 1.0 });
            fail("x does not contain samples (exact), IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }

        try {
            testStatistic.mannWhitneyUTest(new double[] { 1.0 }, new double[] { });
            fail("y does not contain samples (exact), IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }

        /*
         * x and y is null
         */
        try {
            testStatistic.mannWhitneyUTest(null, null);
            fail("x and y is null (exact), IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        
        try {
            testStatistic.mannWhitneyUTest(null, null);
            fail("x and y is null (asymptotic), IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        
        /*
         * x or y is null
         */
        try {
            testStatistic.mannWhitneyUTest(null, new double[] { 1.0 });
            fail("x is null (exact), IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        
        try {
            testStatistic.mannWhitneyUTest(new double[] { 1.0 }, null);
            fail("y is null (exact), IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }
}
