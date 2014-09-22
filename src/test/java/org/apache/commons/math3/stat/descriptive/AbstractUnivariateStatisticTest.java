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
package org.apache.commons.math3.stat.descriptive;


import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for AbstractUnivariateStatistic
 *
 */
public class AbstractUnivariateStatisticTest {

    protected double[] testArray = {0, 1, 2, 3, 4, 5};
    protected double[] testWeightsArray = {0.3, 0.2, 1.3, 1.1, 1.0, 1.8};
    protected double[] testNegativeWeightsArray = {-0.3, 0.2, -1.3, 1.1, 1.0, 1.8};
    protected double[] nullArray = null;
    protected double[] singletonArray = {0};
    protected Mean testStatistic = new Mean();

    @Test
    public void testTestPositive() {
        for (int j = 0; j < 6; j++) {
            for (int i = 1; i < (7 - j); i++) {
                Assert.assertTrue(testStatistic.test(testArray, 0, i));
            }
        }
        Assert.assertTrue(testStatistic.test(singletonArray, 0, 1));
        Assert.assertTrue(testStatistic.test(singletonArray, 0, 0, true));
    }

    @Test
    public void testTestNegative() {
        Assert.assertFalse(testStatistic.test(singletonArray, 0, 0));
        Assert.assertFalse(testStatistic.test(testArray, 0, 0));
        try {
            testStatistic.test(singletonArray, 2, 1);  // start past end
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
        try {
            testStatistic.test(testArray, 0, 7);  // end past end
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
        try {
            testStatistic.test(testArray, -1, 1);  // start negative
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
        try {
            testStatistic.test(testArray, 0, -1);  // length negative
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
        try {
            testStatistic.test(nullArray, 0, 1);  // null array
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
        try {
            testStatistic.test(testArray, nullArray, 0, 1);  // null weights array
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
        try {
            testStatistic.test(singletonArray, testWeightsArray, 0, 1);  // weights.length != value.length
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
        try {
            testStatistic.test(testArray, testNegativeWeightsArray, 0, 6);  // can't have negative weights
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
    }
}
