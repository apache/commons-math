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
package org.apache.commons.math4.random;

import java.util.Random;

import org.apache.commons.math4.exception.OutOfRangeException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the generic implementations of the methods defined in the
 * {@link BaseRandomGenerator} class, using the standard {@link Random}
 * class as the source of randomness.
 */
public class BaseRandomGeneratorTest extends RandomGeneratorAbstractTest {
    /** To simplify testing of additional utility methods. */
    protected BaseRandomGenerator baseRandomGenerator;

    @Before
    public void setUp() {
        baseRandomGenerator = (BaseRandomGenerator) generator;
    }

    @Override
    protected RandomGenerator makeGenerator() {
        final RandomGenerator generator = new TestGenerator();
        generator.setSeed(1000);
        return generator;
    }

    @Test(expected=OutOfRangeException.class)
    public void testNextBytesPrecondition1() {
        final int len = 3;
        final byte[] b = new byte[len];
        baseRandomGenerator.nextBytes(b, -1, 1);
    }

    @Test(expected=OutOfRangeException.class)
    public void testNextBytesPrecondition2() {
        final int len = 3;
        final byte[] b = new byte[len];
        baseRandomGenerator.nextBytes(b, len, 0);
    }

    @Test(expected=OutOfRangeException.class)
    public void testNextBytesPrecondition3() {
        final int len = 3;
        final byte[] b = new byte[len];
        baseRandomGenerator.nextBytes(b, 0, len + 1);
    }

    @Test
    public void testNextBytesSubArray() {
        final int size = 123;
        final int insert = 72;
        final int len = 37;

        final byte[] buffer = new byte[size];
        baseRandomGenerator.nextBytes(buffer, insert, len);

        final byte[] bufferCopy = buffer.clone();
        baseRandomGenerator.nextBytes(buffer, insert, len);

        for (int i = 0; i < insert; i++) {
            Assert.assertEquals(bufferCopy[i], buffer[i]);
        }
        final int maxInsert = insert + len;
        for (int i = insert; i < maxInsert; i++) {
            Assert.assertNotEquals(bufferCopy[i], buffer[i]);
        }
        for (int i = maxInsert; i < size; i++) {
            Assert.assertEquals(bufferCopy[i], buffer[i]);
        }
    }

    /**
     * Test RNG delegating to {@link Random}.
     */
    private static class TestGenerator extends BaseRandomGenerator {
        /** Delegate. */
        private Random random = new Random();

        @Override
        public void setSeed(int seed) {
           random.setSeed(seed);
           clear();
        }

        @Override
        public void setSeed(int[] seed) {
            random.setSeed(seed[0]);
        }

        @Override
        public void setSeed(long seed) {
            random.setSeed((int) seed);
        }

        @Override
        public int nextInt() {
            // Delegate.
            return random.nextInt();
        }
    }
}
