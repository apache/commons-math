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
package org.apache.commons.math4.legacy.genetics;

import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.junit.Assert;
import org.junit.Test;

public class CycleCrossoverTest {

    @Test
    public void testCrossoverExample() {
        // taken from http://www.rubicite.com/Tutorials/GeneticAlgorithms/CrossoverOperators/CycleCrossoverOperator.aspx
        final Integer[] p1 = new Integer[] { 8, 4, 7, 3, 6, 2, 5, 1, 9, 0 };
        final Integer[] p2 = new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        final DummyListChromosome p1c = new DummyListChromosome(p1);
        final DummyListChromosome p2c = new DummyListChromosome(p2);

        final CrossoverPolicy cp = new CycleCrossover<Integer>();
        final ChromosomePair pair = cp.crossover(p1c, p2c);

        final Integer[] c1 = ((DummyListChromosome) pair.getFirst()).getRepresentation().toArray(new Integer[p1.length]);
        final Integer[] c2 = ((DummyListChromosome) pair.getSecond()).getRepresentation().toArray(new Integer[p2.length]);

        final Integer[] c1e = new Integer[] { 8, 1, 2, 3, 4, 5, 6, 7, 9, 0 };
        final Integer[] c2e = new Integer[] { 0, 4, 7, 3, 6, 2, 5, 1, 8, 9 };

        Assert.assertArrayEquals(c1e, c1);
        Assert.assertArrayEquals(c2e, c2);
    }

    @Test
    public void testCrossoverExample2() {
        // taken from http://www.scribd.com/doc/54206412/32/Cycle-crossover
        final Integer[] p1 = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        final Integer[] p2 = new Integer[] { 9, 3, 7, 8, 2, 6, 5, 1, 4};
        final DummyListChromosome p1c = new DummyListChromosome(p1);
        final DummyListChromosome p2c = new DummyListChromosome(p2);

        final CrossoverPolicy cp = new CycleCrossover<Integer>();
        final ChromosomePair pair = cp.crossover(p1c, p2c);

        final Integer[] c1 = ((DummyListChromosome) pair.getFirst()).getRepresentation().toArray(new Integer[p1.length]);
        final Integer[] c2 = ((DummyListChromosome) pair.getSecond()).getRepresentation().toArray(new Integer[p2.length]);

        final Integer[] c1e = new Integer[] { 1, 3, 7, 4, 2, 6, 5, 8, 9 };
        final Integer[] c2e = new Integer[] { 9, 2, 3, 8, 5, 6, 7, 1, 4 };

        Assert.assertArrayEquals(c1e, c1);
        Assert.assertArrayEquals(c2e, c2);
    }

    @Test
    public void testCrossover() {
        final Integer[] p1 = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        final Integer[] p2 = new Integer[] { 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 };
        final DummyListChromosome p1c = new DummyListChromosome(p1);
        final DummyListChromosome p2c = new DummyListChromosome(p2);

        final CrossoverPolicy cp = new CycleCrossover<Integer>(true);

        for (int i = 0; i < 20; i++) {
            final ChromosomePair pair = cp.crossover(p1c, p2c);

            final Integer[] c1 = ((DummyListChromosome) pair.getFirst()).getRepresentation().toArray(new Integer[p1.length]);
            final Integer[] c2 = ((DummyListChromosome) pair.getSecond()).getRepresentation().toArray(new Integer[p2.length]);

            int index = 0;
            // Determine if it is in the same spot as in the first parent, if
            // not it comes from the second parent.
            for (final Integer j : c1) {
                if (!p1[index].equals(j)) {
                    Assert.assertEquals(j, p2[index]);
                } else {
                    Assert.assertEquals(j, p1[index]);
                }
                index++;
            }

            // Same as above only for the second parent.
            index = 0;
            for (final Integer k : c2) {
                if (p2[index] != k) {
                    Assert.assertEquals(k, p1[index]);
                } else {
                    Assert.assertEquals(k, p2[index]);
                }
                index++;
            }
        }
    }

    @Test(expected = DimensionMismatchException.class)
    public void testCrossoverDimensionMismatchException() {
        final Integer[] p1 = new Integer[] { 1, 0, 1, 0, 0, 1, 0, 1, 1 };
        final Integer[] p2 = new Integer[] { 0, 1, 1, 0, 1 };

        final BinaryChromosome p1c = new DummyBinaryChromosome(p1);
        final BinaryChromosome p2c = new DummyBinaryChromosome(p2);

        final CrossoverPolicy cp = new CycleCrossover<Integer>();
        cp.crossover(p1c, p2c);
    }

    @Test(expected = MathIllegalArgumentException.class)
    public void testCrossoverInvalidFixedLengthChromosomeFirst() {
        final Integer[] p1 = new Integer[] { 1, 0, 1, 0, 0, 1, 0, 1, 1 };
        final BinaryChromosome p1c = new DummyBinaryChromosome(p1);
        final Chromosome p2c = new Chromosome() {
            @Override
            public double fitness() {
                // Not important
                return 0;
            }
        };

        final CrossoverPolicy cp = new CycleCrossover<Integer>();
        cp.crossover(p1c, p2c);
    }

    @Test(expected = MathIllegalArgumentException.class)
    public void testCrossoverInvalidFixedLengthChromosomeSecond() {
        final Integer[] p1 = new Integer[] { 1, 0, 1, 0, 0, 1, 0, 1, 1 };
        final BinaryChromosome p2c = new DummyBinaryChromosome(p1);
        final Chromosome p1c = new Chromosome() {
            @Override
            public double fitness() {
                // Not important
                return 0;
            }
        };

        final CrossoverPolicy cp = new CycleCrossover<Integer>();
        cp.crossover(p1c, p2c);
    }
}
