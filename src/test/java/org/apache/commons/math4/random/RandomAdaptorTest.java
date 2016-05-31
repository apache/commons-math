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

import org.apache.commons.math4.random.RandomAdaptor;
import org.apache.commons.math4.random.RandomGenerator;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the RandomAdaptor class
 *
 */

public class RandomAdaptorTest {

    @Test
    public void testAdaptor() {
        ConstantGenerator generator = new ConstantGenerator();
        Random random = RandomAdaptor.createAdaptor(generator);
        checkConstant(random);
        RandomAdaptor randomAdaptor = new RandomAdaptor(generator);
        checkConstant(randomAdaptor);
    }

    private void checkConstant(Random random) {
        byte[] bytes = new byte[] {0};
        random.nextBytes(bytes);
        Assert.assertEquals(0, bytes[0]);
        Assert.assertFalse(random.nextBoolean());
        Assert.assertEquals(0, random.nextDouble(), 0);
        Assert.assertEquals(0, random.nextFloat(), 0);
        Assert.assertEquals(0, random.nextGaussian(), 0);
        Assert.assertEquals(0, random.nextInt());
        Assert.assertEquals(0, random.nextInt(1));
        Assert.assertEquals(0, random.nextLong());
        random.setSeed(100);
        Assert.assertEquals(0, random.nextDouble(), 0);
    }

    /*
     * "Constant" generator to test Adaptor delegation.
     * "Powered by Eclipse ;-)"
     *
     */
    public static class ConstantGenerator implements RandomGenerator {

        private final double value;

        public ConstantGenerator() {
            value = 0;
        }

        public ConstantGenerator(double value) {
            this.value = value;
        }

        @Override
        public boolean nextBoolean() {
            return false;
        }

        @Override
        public void nextBytes(byte[] bytes) {
        }

        @Override
        public double nextDouble() {
            return value;
        }

        @Override
        public float nextFloat() {
            return (float) value;
        }

        @Override
        public double nextGaussian() {
            return value;
        }

        @Override
        public int nextInt() {
            return (int) value;
        }

        @Override
        public int nextInt(int n) {
            return (int) value;
        }

        @Override
        public long nextLong() {
            return (int) value;
        }

        @Override
        public void setSeed(int seed) {
        }

        @Override
        public void setSeed(int[] seed) {
        }

        @Override
        public void setSeed(long seed) {
        }

    }
}
