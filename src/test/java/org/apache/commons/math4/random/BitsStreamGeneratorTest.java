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

import org.apache.commons.math4.random.BitsStreamGenerator;
import org.apache.commons.math4.random.RandomGenerator;

/**
 * Test cases for the BitStreamGenerator class
 *
 */

public class BitsStreamGeneratorTest extends RandomGeneratorAbstractTest {

    public BitsStreamGeneratorTest() {
        super();
    }

    @Override
    protected RandomGenerator makeGenerator() {
        RandomGenerator generator = new TestBitStreamGenerator();
        generator.setSeed(1000);
        return generator;
    }

    /**
     * Test BitStreamGenerator using a Random as bit source.
     */
    static class TestBitStreamGenerator extends BitsStreamGenerator {

        private static final long serialVersionUID = 1L;
        private BitRandom ran = new BitRandom();

        @Override
        public void setSeed(int seed) {
           ran.setSeed(seed);
           clear();
        }

        @Override
        public void setSeed(int[] seed) {
            ran.setSeed(seed[0]);
        }

        @Override
        public void setSeed(long seed) {
            ran.setSeed((int) seed);

        }

        @Override
        protected int next(int bits) {
            return ran.nextBits(bits);
        }
    }

    /**
     * Extend Random to expose next(bits)
     */
    @SuppressWarnings("serial")
    static class BitRandom extends Random {
        public BitRandom() {
            super();
        }
        public int nextBits(int bits) {
            return next(bits);
        }
    }

}
