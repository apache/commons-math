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
package org.apache.commons.math4.ga.crossover;

import org.apache.commons.math4.ga.chromosome.BinaryChromosome;
import org.apache.commons.math4.ga.chromosome.ChromosomePair;
import org.apache.commons.math4.ga.utils.ChromosomeRepresentationUtils;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class OnePointBinaryCrossoverTest {

    @Test
    public void testCrossover() {

        OnePointBinaryCrossover<String> opc = new OnePointBinaryCrossover<>();

        // test a stochastic method.
        for (int i = 0; i < 1000; i++) {
            long length = 32 + (long) (200 * Math.random());
            BinaryChromosome<String> p1c = new BinaryChromosome<>(
                    ChromosomeRepresentationUtils.randomBinaryRepresentation(length), length, c -> 0, c -> "0");
            BinaryChromosome<String> p2c = new BinaryChromosome<>(
                    ChromosomeRepresentationUtils.randomBinaryRepresentation(length), length, c -> 0, c -> "0");

            ChromosomePair<String> pair = opc.crossover(p1c, p2c, 1.0);

            long[] p1 = p1c.getRepresentation();
            long[] p2 = p2c.getRepresentation();

            long[] c1 = ((BinaryChromosome<String>) pair.getFirst()).getRepresentation();
            long[] c2 = ((BinaryChromosome<String>) pair.getSecond()).getRepresentation();

            final int offset = (int) (length % 64 == 0 ? 0 : 64 - (length % 64));

            // first and last values will be the same
            Assertions.assertEquals(prependZero(Long.toBinaryString(p1[0])).charAt(offset),
                    prependZero(Long.toBinaryString(c1[0])).charAt(offset));
            Assertions.assertEquals(prependZero(Long.toBinaryString(p2[0])).charAt(offset),
                    prependZero(Long.toBinaryString(c2[0])).charAt(offset));
            Assertions.assertEquals(prependZero(Long.toBinaryString(p1[p1.length - 1])).charAt(63),
                    prependZero(Long.toBinaryString(c2[c2.length - 1])).charAt(63));
            Assertions.assertEquals(prependZero(Long.toBinaryString(p2[p2.length - 1])).charAt(63),
                    prependZero(Long.toBinaryString(c1[c1.length - 1])).charAt(63));

        }
    }

    private String prependZero(String value) {
        StringBuilder modValue = new StringBuilder();
        for (int i = 64 - value.length(); i > 0; i--) {
            modValue.append('0');
        }
        modValue.append(value);

        return modValue.toString();
    }

}
