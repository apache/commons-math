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
package org.apache.commons.math4.legacy.util;

import org.apache.commons.math4.legacy.exception.MathArithmeticException;
import org.apache.commons.math4.legacy.exception.NotPositiveException;
import org.apache.commons.math4.legacy.exception.NumberIsTooLargeException;
import org.apache.commons.numbers.combinatorics.BinomialCoefficient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test cases for the {@link CombinatoricsUtils} class.
 */
public class CombinatoricsUtilsTest {

    @Test
    public void testStirlingS2() {

        Assertions.assertEquals(1, CombinatoricsUtils.stirlingS2(0, 0));

        for (int n = 1; n < 30; ++n) {
            Assertions.assertEquals(0, CombinatoricsUtils.stirlingS2(n, 0));
            Assertions.assertEquals(1, CombinatoricsUtils.stirlingS2(n, 1));
            if (n > 2) {
                Assertions.assertEquals((1L << (n - 1)) - 1L, CombinatoricsUtils.stirlingS2(n, 2));
                Assertions.assertEquals(BinomialCoefficient.value(n, 2),
                                    CombinatoricsUtils.stirlingS2(n, n - 1));
            }
            Assertions.assertEquals(1, CombinatoricsUtils.stirlingS2(n, n));
        }
        Assertions.assertEquals(536870911L, CombinatoricsUtils.stirlingS2(30, 2));
        Assertions.assertEquals(576460752303423487L, CombinatoricsUtils.stirlingS2(60, 2));

        Assertions.assertEquals(   25, CombinatoricsUtils.stirlingS2( 5, 3));
        Assertions.assertEquals(   90, CombinatoricsUtils.stirlingS2( 6, 3));
        Assertions.assertEquals(   65, CombinatoricsUtils.stirlingS2( 6, 4));
        Assertions.assertEquals(  301, CombinatoricsUtils.stirlingS2( 7, 3));
        Assertions.assertEquals(  350, CombinatoricsUtils.stirlingS2( 7, 4));
        Assertions.assertEquals(  140, CombinatoricsUtils.stirlingS2( 7, 5));
        Assertions.assertEquals(  966, CombinatoricsUtils.stirlingS2( 8, 3));
        Assertions.assertEquals( 1701, CombinatoricsUtils.stirlingS2( 8, 4));
        Assertions.assertEquals( 1050, CombinatoricsUtils.stirlingS2( 8, 5));
        Assertions.assertEquals(  266, CombinatoricsUtils.stirlingS2( 8, 6));
        Assertions.assertEquals( 3025, CombinatoricsUtils.stirlingS2( 9, 3));
        Assertions.assertEquals( 7770, CombinatoricsUtils.stirlingS2( 9, 4));
        Assertions.assertEquals( 6951, CombinatoricsUtils.stirlingS2( 9, 5));
        Assertions.assertEquals( 2646, CombinatoricsUtils.stirlingS2( 9, 6));
        Assertions.assertEquals(  462, CombinatoricsUtils.stirlingS2( 9, 7));
        Assertions.assertEquals( 9330, CombinatoricsUtils.stirlingS2(10, 3));
        Assertions.assertEquals(34105, CombinatoricsUtils.stirlingS2(10, 4));
        Assertions.assertEquals(42525, CombinatoricsUtils.stirlingS2(10, 5));
        Assertions.assertEquals(22827, CombinatoricsUtils.stirlingS2(10, 6));
        Assertions.assertEquals( 5880, CombinatoricsUtils.stirlingS2(10, 7));
        Assertions.assertEquals(  750, CombinatoricsUtils.stirlingS2(10, 8));
    }

    @Test
    public void testStirlingS2NegativeN() {
        Assertions.assertThrows(NotPositiveException.class, () -> CombinatoricsUtils.stirlingS2(3, -1));
    }

    @Test
    public void testStirlingS2LargeK() {
        Assertions.assertThrows(NumberIsTooLargeException.class, () -> CombinatoricsUtils.stirlingS2(3, 4));
    }

    @Test
    public void testStirlingS2Overflow() {
        Assertions.assertThrows(MathArithmeticException.class, () -> CombinatoricsUtils.stirlingS2(26, 9));

        Assertions.assertEquals(9223372036854775807L, CombinatoricsUtils.stirlingS2(64, 2));
        Assertions.assertThrows(MathArithmeticException.class, () -> CombinatoricsUtils.stirlingS2(65, 2));
    }
}
