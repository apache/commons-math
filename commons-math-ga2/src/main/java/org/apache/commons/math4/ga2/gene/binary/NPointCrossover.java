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

package org.apache.commons.math4.ga2.gene.binary;

import java.util.BitSet;
import java.util.List;
import java.util.ArrayList;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.math4.ga2.AbstractCrossover;

/**
 * Genetic operator.
 * Class is immutable.
 */
/* package-private */ class NPointCrossover extends AbstractCrossover<Chromosome> {
    /** Number of crossover points. */
    private final int numberOfPoints;

    /**
     * @param n Number of crossover points.
     */
    NPointCrossover(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Not strictly positive: " + n);
        }

        numberOfPoints = n;
    }

    /** {@inheritDoc} */
    @Override
    protected List<Chromosome> apply(Chromosome parent1,
                                     Chromosome parent2,
                                     UniformRandomProvider rng) {
        final int size = parent1.size();
        if (size != parent2.size()) {
            throw new IllegalArgumentException("Parents of unequal sizes: " +
                                               size + " != " +
                                               parent2.size());
        }

        final BitSet c1 = parent1.asBitSet();
        final BitSet c2 = parent2.asBitSet();

        // Number or remaining crossover points.
        int remainingPoints = numberOfPoints;
        // Index of crossover end point.
        int xEnd = 0;
        while (remainingPoints > 0) {
            // Index of crossover start point.
            final int xStart = xEnd + 1 + rng.nextInt(size - xEnd - remainingPoints);

            if (--remainingPoints > 0) {
                xEnd = xStart + 1 + rng.nextInt(size - xStart - remainingPoints);

                swap(c1, c2, xStart, xEnd);

                --remainingPoints;
            } else {
                xEnd = xStart;
                break;
            }
        }

        if (numberOfPoints % 2 != 0) {
            swap(c1, c2, xEnd, size);
        }

        final List<Chromosome> offsprings = new ArrayList<>(2);
        offsprings.add(Chromosome.from(c1, size));
        offsprings.add(Chromosome.from(c2, size));

        return offsprings;
    }

    /**
     * Swaps contents (in-place) within the given range.
     *
     * @param a Chromosome.
     * @param b Chromosome.
     * @param start Index from which contents should be swapped.
     * @param end Index at which swapping should stop.
     */
    private void swap(BitSet a,
                      BitSet b,
                      int start,
                      int end) {
        for (int i = start; i < end; i++) {
            final boolean aV = a.get(i);
            final boolean bV = b.get(i);
            a.set(i, bV);
            b.set(i, aV);
        }
    }
}
