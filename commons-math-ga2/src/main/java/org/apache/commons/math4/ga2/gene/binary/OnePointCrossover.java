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
/* package-private */ class OnePointCrossover extends AbstractCrossover<Chromosome> {
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

        // Index of crossover point.
        final int xIndex = 1 + rng.nextInt(size - 1);
        final BitSet p1 = parent1.asBitSet();
        final BitSet p2 = parent2.asBitSet();
        final BitSet c1;
        final BitSet c2;

        final int midIndex = size / 2;
        if (xIndex > midIndex) {
            c1 = parent1.asBitSet();
            c2 = parent2.asBitSet();

            for (int i = xIndex; i < size; i++) {
                c1.set(i, p2.get(i));
                c2.set(i, p1.get(i));
            }
        } else {
            c1 = parent2.asBitSet();
            c2 = parent1.asBitSet();

            for (int i = 0; i < xIndex; i++) {
                c1.set(i, p1.get(i));
                c2.set(i, p2.get(i));
            }
        }

        final List<Chromosome> offsprings = new ArrayList<>(2);
        offsprings.add(Chromosome.from(c1, size));
        offsprings.add(Chromosome.from(c2, size));

        return offsprings;
    }
}
