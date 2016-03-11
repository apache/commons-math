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
package org.apache.commons.math4.rng.internal.util;

import org.junit.Assert;
import org.junit.Test;

import org.apache.commons.math4.exception.DimensionMismatchException;

/**
 * Tests for the {@link NumberFactory}.
 */
public class NumberFactoryTest {
    /** sizeof(int). */
    final int INT_SIZE = 4;
    /** sizeof(long). */
    final int LONG_SIZE = 8;

    /** Test values. */
    private static final long[] LONG_TEST_VALUES = new long[] {
        0L,
        1L,
        -1L,
        19337L,
        1234567891011213L,
        -11109876543211L,
        Long.valueOf(Integer.MAX_VALUE),
        Long.valueOf(Integer.MIN_VALUE),
        Long.MAX_VALUE,
        Long.MIN_VALUE,
    };
    /** Test values. */
    private static final int[] INT_TEST_VALUES = new int[] {
        0,
        1,
        -1,
        19337,
        1234567891,
        -1110987656,
        Integer.MAX_VALUE,
        Integer.MIN_VALUE,
    };

    @Test
    public void testMakeIntFromLong() {
        for (long v : LONG_TEST_VALUES) {
            final int vL = NumberFactory.extractLo(v);
            final int vH = NumberFactory.extractHi(v);

            final long actual = (((long) vH) << 32) | (vL & 0xffffffffL);
            Assert.assertEquals(v, actual);
        }
    }

    @Test
    public void testLong2Long() {
        for (long v : LONG_TEST_VALUES) {
            final int vL = NumberFactory.extractLo(v);
            final int vH = NumberFactory.extractHi(v);

            Assert.assertEquals(v, NumberFactory.makeLong(vH, vL));
        }
    }

    @Test
    public void testLongFromByteArray2Long() {
        for (long expected : LONG_TEST_VALUES) {
            final byte[] b = NumberFactory.makeByteArray(expected);
            Assert.assertEquals(expected, NumberFactory.makeLong(b));
        }
    }

    @Test
    public void testLongArrayFromByteArray2LongArray() {
        final byte[] b = NumberFactory.makeByteArray(LONG_TEST_VALUES);
        Assert.assertArrayEquals(LONG_TEST_VALUES,
                                 NumberFactory.makeLongArray(b));
    }

    @Test
    public void testIntFromByteArray2Int() {
        for (int expected : INT_TEST_VALUES) {
            final byte[] b = NumberFactory.makeByteArray(expected);
            Assert.assertEquals(expected, NumberFactory.makeInt(b));
        }
    }

    @Test
    public void testIntArrayFromByteArray2IntArray() {
        final byte[] b = NumberFactory.makeByteArray(INT_TEST_VALUES);
        Assert.assertArrayEquals(INT_TEST_VALUES,
                                 NumberFactory.makeIntArray(b));
    }

    @Test
    public void testMakeIntPrecondition1() {
        for (int i = 0; i <= 10; i++) {
            try {
                NumberFactory.makeInt(new byte[i]);
                if (i != INT_SIZE) {
                    Assert.fail("Exception expected");
                }
            } catch (DimensionMismatchException e) {
                // Expected.
            }
        }
    }

    @Test
    public void testMakeIntArrayPrecondition1() {
        for (int i = 0; i <= 20; i++) {
            try {
                NumberFactory.makeIntArray(new byte[i]);
                if (i != 0 && (i % INT_SIZE != 0)) {
                    Assert.fail("Exception expected");
                }
            } catch (DimensionMismatchException e) {
                // Expected.
            }
        }
    }

    @Test
    public void testMakeLongPrecondition1() {
        for (int i = 0; i <= 10; i++) {
            try {
                NumberFactory.makeLong(new byte[i]);
                if (i != LONG_SIZE) {
                    Assert.fail("Exception expected");
                }
            } catch (DimensionMismatchException e) {
                // Expected.
            }
        }
    }

    @Test
    public void testMakeLongArrayPrecondition1() {
        for (int i = 0; i <= 20; i++) {
            try {
                NumberFactory.makeLongArray(new byte[i]);
                if (i != 0 && (i % LONG_SIZE != 0)) {
                    Assert.fail("Exception expected");
                }
            } catch (DimensionMismatchException e) {
                // Expected.
            }
        }
    }
}
