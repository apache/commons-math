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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.math4.genetics.chromosome.ChromosomePair;
import org.apache.commons.math4.genetics.dummy.DummyListChromosome;
import org.junit.Assert;
import org.junit.Test;

public class OrderedCrossoverTest {

    @Test
    public void testCrossover() {
        final Integer[] p1 = new Integer[] {8, 4, 7, 3, 6, 2, 5, 1, 9, 0};
        final Integer[] p2 = new Integer[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        final DummyListChromosome p1c = new DummyListChromosome(p1);
        final DummyListChromosome p2c = new DummyListChromosome(p2);

        final CrossoverPolicy<String> cp = new OrderedCrossover<Integer, String>();

        for (int i = 0; i < 20; i++) {
            final Set<Integer> parentSet1 = new HashSet<>(Arrays.asList(p1));
            final Set<Integer> parentSet2 = new HashSet<>(Arrays.asList(p2));

            final ChromosomePair<String> pair = cp.crossover(p1c, p2c, 1.0);

            final Integer[] c1 = ((DummyListChromosome) pair.getFirst()).getRepresentation()
                    .toArray(new Integer[p1.length]);
            final Integer[] c2 = ((DummyListChromosome) pair.getSecond()).getRepresentation()
                    .toArray(new Integer[p2.length]);

            Assert.assertNotSame(p1c, pair.getFirst());
            Assert.assertNotSame(p2c, pair.getSecond());

            // make sure that the children have exactly the same elements as their parents
            for (int j = 0; j < c1.length; j++) {
                Assert.assertTrue(parentSet1.contains(c1[j]));
                parentSet1.remove(c1[j]);
                Assert.assertTrue(parentSet2.contains(c2[j]));
                parentSet2.remove(c2[j]);
            }
        }
    }

}
