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
package org.apache.commons.math4.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math4.exception.MathArithmeticException;
import org.apache.commons.math4.exception.MathIllegalArgumentException;
import org.apache.commons.math4.exception.NotPositiveException;
import org.apache.commons.math4.exception.NumberIsTooLargeException;
import org.apache.commons.numbers.core.ArithmeticUtils;
import org.apache.commons.numbers.combinatorics.BinomialCoefficient;
import org.apache.commons.math4.util.CombinatoricsUtils;
import org.apache.commons.math4.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the {@link CombinatoricsUtils} class.
 *
 */
public class CombinatoricsUtilsTest {

    /** cached binomial coefficients */
    private static final List<Map<Integer, Long>> binomialCache = new ArrayList<>();

    @Test
    public void testStirlingS2() {

        Assert.assertEquals(1, CombinatoricsUtils.stirlingS2(0, 0));

        for (int n = 1; n < 30; ++n) {
            Assert.assertEquals(0, CombinatoricsUtils.stirlingS2(n, 0));
            Assert.assertEquals(1, CombinatoricsUtils.stirlingS2(n, 1));
            if (n > 2) {
                Assert.assertEquals((1l << (n - 1)) - 1l, CombinatoricsUtils.stirlingS2(n, 2));
                Assert.assertEquals(BinomialCoefficient.value(n, 2),
                                    CombinatoricsUtils.stirlingS2(n, n - 1));
            }
            Assert.assertEquals(1, CombinatoricsUtils.stirlingS2(n, n));
        }
        Assert.assertEquals(536870911l, CombinatoricsUtils.stirlingS2(30, 2));
        Assert.assertEquals(576460752303423487l, CombinatoricsUtils.stirlingS2(60, 2));

        Assert.assertEquals(   25, CombinatoricsUtils.stirlingS2( 5, 3));
        Assert.assertEquals(   90, CombinatoricsUtils.stirlingS2( 6, 3));
        Assert.assertEquals(   65, CombinatoricsUtils.stirlingS2( 6, 4));
        Assert.assertEquals(  301, CombinatoricsUtils.stirlingS2( 7, 3));
        Assert.assertEquals(  350, CombinatoricsUtils.stirlingS2( 7, 4));
        Assert.assertEquals(  140, CombinatoricsUtils.stirlingS2( 7, 5));
        Assert.assertEquals(  966, CombinatoricsUtils.stirlingS2( 8, 3));
        Assert.assertEquals( 1701, CombinatoricsUtils.stirlingS2( 8, 4));
        Assert.assertEquals( 1050, CombinatoricsUtils.stirlingS2( 8, 5));
        Assert.assertEquals(  266, CombinatoricsUtils.stirlingS2( 8, 6));
        Assert.assertEquals( 3025, CombinatoricsUtils.stirlingS2( 9, 3));
        Assert.assertEquals( 7770, CombinatoricsUtils.stirlingS2( 9, 4));
        Assert.assertEquals( 6951, CombinatoricsUtils.stirlingS2( 9, 5));
        Assert.assertEquals( 2646, CombinatoricsUtils.stirlingS2( 9, 6));
        Assert.assertEquals(  462, CombinatoricsUtils.stirlingS2( 9, 7));
        Assert.assertEquals( 9330, CombinatoricsUtils.stirlingS2(10, 3));
        Assert.assertEquals(34105, CombinatoricsUtils.stirlingS2(10, 4));
        Assert.assertEquals(42525, CombinatoricsUtils.stirlingS2(10, 5));
        Assert.assertEquals(22827, CombinatoricsUtils.stirlingS2(10, 6));
        Assert.assertEquals( 5880, CombinatoricsUtils.stirlingS2(10, 7));
        Assert.assertEquals(  750, CombinatoricsUtils.stirlingS2(10, 8));

    }

    @Test(expected=NotPositiveException.class)
    public void testStirlingS2NegativeN() {
        CombinatoricsUtils.stirlingS2(3, -1);
    }

    @Test(expected=NumberIsTooLargeException.class)
    public void testStirlingS2LargeK() {
        CombinatoricsUtils.stirlingS2(3, 4);
    }

    @Test(expected=MathArithmeticException.class)
    public void testStirlingS2Overflow() {
        CombinatoricsUtils.stirlingS2(26, 9);
    }
}
