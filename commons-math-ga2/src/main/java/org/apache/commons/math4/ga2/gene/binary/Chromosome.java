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
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.math4.ga2.ChromosomeFactory;

/**
 * Represents a chromosome.
 */
public final class Chromosome {
    /** Length. */
    private final int size;
    /** Internal representation. */
    private final BitSet representation;

    /**
     * Forbid direct instantiation.
     *
     * @param representation Representation.  Reference is copied: Factory
     * methods that call this constructor <em>must</em> pass a defensive copy.
     * @param size Number of genes.
     */
    private Chromosome(BitSet representation,
                       int size) {
        this.size = size;
        this.representation = representation;
    }

    /** @return the number of genes. */
    public int size() {
        return size;
    }

    /**
     * Factory method.
     *
     * @param r Representation.
     * @param size Number of genes.
     * @return a new instance whose internal representation is a copy of the
     * bits of {@code r} (truncating, or expanding with {@code false}, so that
     * the number of genes is equal to {@code size}).
     */
    public static Chromosome from(BitSet r,
                                  int size) {
        return new Chromosome((BitSet) r.clone(), size);
    }

    /**
     * Factory method.
     *
     * @param rng Random generator.
     * @param size Number of genes.
     * @return a new instance whose genes are drawn from the given generator.
     */
    public static Chromosome from(UniformRandomProvider rng,
                                  int size) {
        final BitSet r = new BitSet(size);
        for (int i = 0; i < size; i++) {
            r.set(i, rng.nextBoolean());
        }

        return new Chromosome(r, size);
    }

    /** @return a copy of the internal representation. */
    public BitSet asBitSet() {
        return (BitSet) representation.clone();
    }

    /**
     * Generate chromosomes with random genes.
     * Class is <em>not</em> thread-safe.
     */
    public static class RandomFactory implements ChromosomeFactory<Chromosome> {
        /** RNG. */
        private final UniformRandomProvider rng;

        /**
         * @param random Source of randomness.
         */
        public RandomFactory(RandomSource random) {
            rng = random.create();
        }

        /** {@inheritDoc} */
        @Override
        public Chromosome with(int size) {
            return Chromosome.from(rng, size);
        }
    }
}
