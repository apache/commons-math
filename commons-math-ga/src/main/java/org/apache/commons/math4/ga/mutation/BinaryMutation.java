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
package org.apache.commons.math4.ga.mutation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.math4.ga.chromosome.BinaryChromosome;
import org.apache.commons.math4.ga.chromosome.Chromosome;
import org.apache.commons.math4.ga.internal.exception.GeneticIllegalArgumentException;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.rng.simple.ThreadLocalRandomSource;

/**
 * Mutation operator for {@link BinaryChromosome}s. Randomly changes few genes.
 * @param <P> phenotype of chromosome
 * @since 4.0
 */
public class BinaryMutation<P> implements MutationPolicy<P> {

    /** The default RandomSource for random number generation. **/
    private final RandomSource randomSource;

    /**
     * Initializes a binary mutation policy with default random source.
     */
    public BinaryMutation() {
        this.randomSource = RandomSource.XO_RO_SHI_RO_128_PP;
    }

    /**
     * Initializes a binary mutation policy with provided random source.
     * @param randomSource random source to instantiate UniformRandomProvider.
     */
    public BinaryMutation(final RandomSource randomSource) {
        this.randomSource = randomSource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BinaryChromosome<P> mutate(Chromosome<P> original, double mutationRate) {
        // check for validity.
        checkValidity(original);
        final BinaryChromosome<P> chromosome = (BinaryChromosome<P>) original;
        final long[] representation = chromosome.getRepresentation();
        final long[] newRep = new long[representation.length];
        System.arraycopy(representation, 0, newRep, 0, representation.length);

        final Map<Integer, Set<Integer>> mutableGeneIndexMap = getMutableGeneIndexes(chromosome.getLength(),
                mutationRate);
        for (Entry<Integer, Set<Integer>> entry : mutableGeneIndexMap.entrySet()) {
            final int alleleBlockIndex = entry.getKey();
            long mask = 0;
            final Set<Integer> alleleElementIndexes = mutableGeneIndexMap.get(alleleBlockIndex);
            for (int index : alleleElementIndexes) {
                mask += index == 0 ? Long.MIN_VALUE : Math.pow(2, Long.SIZE - 1 - index);
            }
            newRep[alleleBlockIndex] = newRep[alleleBlockIndex] ^ mask;
        }

        return chromosome.from(newRep, chromosome.getLength());
    }

    /**
     * Checks input chromosome validity.
     * @param original chromosome to be mutated
     */
    private void checkValidity(Chromosome<P> original) {
        if (!BinaryChromosome.class.isAssignableFrom(original.getClass())) {
            throw new GeneticIllegalArgumentException(GeneticIllegalArgumentException.ILLEGAL_ARGUMENT,
                    original.getClass().getSimpleName());
        }
    }

    /**
     * Selects and returns mutable gene indexes based on mutation rate.
     * @param length       no of alleles/genes in chromosome
     * @param mutationRate mutation rate of the allele/gene
     * @return mutable gene indexes
     */
    private Map<Integer, Set<Integer>> getMutableGeneIndexes(long length, double mutationRate) {

        // calculate the total mutation rate of all the alleles i.e. chromosome.
        final double chromosomeMutationRate = mutationRate * length;
        final Map<Integer, Set<Integer>> indexMap = new HashMap<>();
        UniformRandomProvider randomProvider = ThreadLocalRandomSource.current(randomSource);

        // if chromosomeMutationRate >= 1 then more than one allele will be mutated.
        if (chromosomeMutationRate >= 1) {
            final int noOfMutation = (int) Math.round(chromosomeMutationRate);
            final Set<Long> mutationIndexes = new HashSet<>();
            for (int i = 0; i < noOfMutation; i++) {
                final long mutationIndex = generateMutationIndex(length, mutationIndexes, randomProvider);
                mutationIndexes.add(mutationIndex);
                updateIndexMap(indexMap, length, mutationIndex);
            }
        } else if (randomProvider.nextDouble() < chromosomeMutationRate) {
            updateIndexMap(indexMap, length, randomProvider);
        }
        return indexMap;
    }

    private long generateMutationIndex(long length, Set<Long> mutationIndexes, UniformRandomProvider randomProvider) {
        long mutationIndex = 0;
        do {
            mutationIndex = randomProvider.nextLong(length);
        } while (mutationIndexes.contains(mutationIndex));
        return mutationIndex;
    }

    private void updateIndexMap(Map<Integer, Set<Integer>> indexMap, long length, long mutationIndex) {
        final int offset = (int) (length % Long.SIZE == 0 ? 0 : Long.SIZE - length % Long.SIZE);
        final long offsettedMutableAlleleIndex = offset + mutationIndex;

        final int alleleBlockIndex = (int) (offsettedMutableAlleleIndex / Long.SIZE);

        if (!indexMap.containsKey(alleleBlockIndex)) {
            indexMap.put(alleleBlockIndex, new HashSet<>());
        }
        final int alleleElementIndex = (int) (offsettedMutableAlleleIndex % Long.SIZE);

        indexMap.get(alleleBlockIndex).add(alleleElementIndex);
    }

    private void updateIndexMap(Map<Integer, Set<Integer>> indexMap,
            long length,
            UniformRandomProvider randomProvider) {
        updateIndexMap(indexMap, length, randomProvider.nextLong(length));
    }

}
