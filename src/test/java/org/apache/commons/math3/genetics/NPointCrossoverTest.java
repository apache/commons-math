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
package org.apache.commons.math3.genetics;

import java.util.List;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.junit.Assert;
import org.junit.Test;

public class NPointCrossoverTest {

    @Test(expected = DimensionMismatchException.class)
    public void testCrossoverDimensionMismatchException() {
        final Integer[] p1 = new Integer[] {1,0,1,0,0,1,0,1,1};
        final Integer[] p2 = new Integer[] {0,1,1,0,1};

        final BinaryChromosome p1c = new DummyBinaryChromosome(p1);
        final BinaryChromosome p2c = new DummyBinaryChromosome(p2);

        final CrossoverPolicy cp = new NPointCrossover<Integer>(1);
        cp.crossover(p1c,p2c);
    }

    @Test(expected = NumberIsTooLargeException.class)
    public void testNumberIsTooLargeException() {
        final Integer[] p1 = new Integer[] {1,0,1,0,0,1,0,1,1};
        final Integer[] p2 = new Integer[] {0,1,1,0,1,0,1,1,1};

        final BinaryChromosome p1c = new DummyBinaryChromosome(p1);
        final BinaryChromosome p2c = new DummyBinaryChromosome(p2);

        final CrossoverPolicy cp = new NPointCrossover<Integer>(15);
        cp.crossover(p1c,p2c);
    }

    @Test(expected = MathIllegalArgumentException.class)
    public void testCrossoverInvalidFixedLengthChromosomeFirst() {
        final Integer[] p1 = new Integer[] {1,0,1,0,0,1,0,1,1};
        final BinaryChromosome p1c = new DummyBinaryChromosome(p1);
        final Chromosome p2c = new Chromosome() {
            public double fitness() {
                // Not important
                return 0;
            }
        };

        final CrossoverPolicy cp = new NPointCrossover<Integer>(1);
        cp.crossover(p1c,p2c);
    }

    @Test(expected = MathIllegalArgumentException.class)
    public void testCrossoverInvalidFixedLengthChromosomeSecond() {
        final Integer[] p1 = new Integer[] {1,0,1,0,0,1,0,1,1};
        final BinaryChromosome p2c = new DummyBinaryChromosome(p1);
        final Chromosome p1c = new Chromosome() {
            public double fitness() {
                // Not important
                return 0;
            }
        };

        final CrossoverPolicy cp = new NPointCrossover<Integer>(1);
        cp.crossover(p1c,p2c);
    }

    @Test
    public void testCrossover() {
        Integer[] p1 = new Integer[] {1,0,1,0,1,0,1,0,1};
        Integer[] p2 = new Integer[] {0,1,0,1,0,1,0,1,0};

        BinaryChromosome p1c = new DummyBinaryChromosome(p1);
        BinaryChromosome p2c = new DummyBinaryChromosome(p2);

        final int order = 3;
        NPointCrossover<Integer> npc = new NPointCrossover<Integer>(order);

        // the two parent chromosomes are different at each position, so it is easy to detect
        // the number of crossovers that happened for each child
        for (int i=0; i<20; i++) {
            ChromosomePair pair = npc.crossover(p1c,p2c);

            Integer[] c1 = new Integer[p1.length];
            Integer[] c2 = new Integer[p2.length];

            c1 = ((BinaryChromosome) pair.getFirst()).getRepresentation().toArray(c1);
            c2 = ((BinaryChromosome) pair.getSecond()).getRepresentation().toArray(c2);

            Assert.assertEquals(order, detectCrossoverPoints(p1c, p2c, (BinaryChromosome) pair.getFirst()));
            Assert.assertEquals(order, detectCrossoverPoints(p2c, p1c, (BinaryChromosome) pair.getSecond()));
        }
    }

    private int detectCrossoverPoints(BinaryChromosome p1, BinaryChromosome p2, BinaryChromosome c) {
        int crossovers = 0;
        final int length = p1.getLength();

        final List<Integer> p1Rep = p1.getRepresentation();
        final List<Integer> p2Rep = p2.getRepresentation();
        final List<Integer> cRep = c.getRepresentation();

        List<Integer> rep = p1Rep;
        for (int i = 0; i < length; i++) {
            if (rep.get(i) != cRep.get(i)) {
                crossovers++;
                rep = rep == p1Rep ? p2Rep : p1Rep;
            }
        }

        return crossovers;
    }

}
