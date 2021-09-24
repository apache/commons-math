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
package org.apache.commons.math4.genetics.crossover;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math4.genetics.chromosome.BinaryChromosome;
import org.apache.commons.math4.genetics.chromosome.ChromosomePair;
import org.apache.commons.math4.genetics.exception.GeneticException;
import org.apache.commons.math4.genetics.utils.DummyListChromosomeDecoder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class UniformCrossoverTest {
    private static final int LEN = 10000;
    private static final List<Integer> p1 = new ArrayList<>(LEN);
    private static final List<Integer> p2 = new ArrayList<>(LEN);

    @SuppressWarnings("boxing")
    @BeforeClass
    public static void setUpBeforeClass() {
        for (int i = 0; i < LEN; i++) {
            p1.add(0);
            p2.add(1);
        }
    }

    @Test(expected = GeneticException.class)
    public void testRatioTooLow() {
        new UniformCrossover<Integer, String>(-0.5d);
    }

    @Test(expected = GeneticException.class)
    public void testRatioTooHigh() {
        new UniformCrossover<Integer, String>(1.5d);
    }

    @Test
    public void testCrossover() {
        // test crossover with different ratios
        performCrossover(0.5);
        performCrossover(0.7);
        performCrossover(0.2);
    }

    private void performCrossover(double ratio) {
        final BinaryChromosome<String> p1c = new BinaryChromosome<>(p1, chromosome -> 0,
                new DummyListChromosomeDecoder<>("0"));
        final BinaryChromosome<String> p2c = new BinaryChromosome<>(p2, chromosome -> 0,
                new DummyListChromosomeDecoder<>("0"));

        final CrossoverPolicy<String> cp = new UniformCrossover<Integer, String>(ratio);

        // make a number of rounds
        for (int i = 0; i < 20; i++) {
            final ChromosomePair<String> pair = cp.crossover(p1c, p2c, 1.0);

            final List<Integer> c1 = ((BinaryChromosome<String>) pair.getFirst()).getRepresentation();
            final List<Integer> c2 = ((BinaryChromosome<String>) pair.getSecond()).getRepresentation();

            int from1 = 0;
            int from2 = 0;

            // check first child
            for (int val : c1) {
                if (val == 0) {
                    from1++;
                } else {
                    from2++;
                }
            }

            Assert.assertEquals(1.0 - ratio, (double) from1 / LEN, 0.1);
            Assert.assertEquals(ratio, (double) from2 / LEN, 0.1);

            from1 = 0;
            from2 = 0;

            // check second child
            for (int val : c2) {
                if (val == 0) {
                    from1++;
                } else {
                    from2++;
                }
            }

            Assert.assertEquals(ratio, (double) from1 / LEN, 0.1);
            Assert.assertEquals(1.0 - ratio, (double) from2 / LEN, 0.1);
        }
    }

}
