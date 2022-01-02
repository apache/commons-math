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
import org.apache.commons.math4.ga.internal.exception.GeneticException;
import org.apache.commons.math4.ga.utils.RandomProviderManager;

/**
 * Mutation for {@link BinaryChromosome}s. Randomly changes few genes.
 * @param <P> phenotype of chromosome
 * @since 4.0
 */
public class BinaryMutation<P> implements MutationPolicy<P> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Chromosome<P> mutate(Chromosome<P> original, double mutationRate) {
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
//        for (int alleleBlockIndex : mutableGeneIndexMap.keySet()) {
//            long mask = 0;
//            final Set<Integer> alleleElementIndexes = mutableGeneIndexMap.get(alleleBlockIndex);
//            for (int index : alleleElementIndexes) {
//                mask += index == 0 ? Long.MIN_VALUE : Math.pow(2, Long.SIZE - 1 - index);
//            }
//            newRep[alleleBlockIndex] = newRep[alleleBlockIndex] ^ mask;
//        }

        return chromosome.newChromosome(newRep, chromosome.getLength());
    }

    /**
     * Checks input chromosome validity.
     * @param original chromosome to be mutated
     */
    protected void checkValidity(Chromosome<P> original) {
        if (!BinaryChromosome.class.isAssignableFrom(original.getClass())) {
            throw new GeneticException(GeneticException.ILLEGAL_ARGUMENT, original.getClass().getSimpleName());
        }
    }

    /**
     * Selects and returns mutable gene indexes based on mutation rate.
     * @param length       no of alleles/genes in chromosome
     * @param mutationRate mutation rate of the allele/gene
     * @return mutable gene indexes
     */
    protected Map<Integer, Set<Integer>> getMutableGeneIndexes(long length, double mutationRate) {

        // calculate the total mutation rate of all the alleles i.e. chromosome.
        final double chromosomeMutationRate = mutationRate * length;
        final Map<Integer, Set<Integer>> indexMap = new HashMap<>();

        // if chromosomeMutationRate >= 1 then more than one allele will be mutated.
        if (chromosomeMutationRate >= 1) {
            final int noOfMutation = (int) Math.round(chromosomeMutationRate);
            final Set<Long> mutationIndexes = new HashSet<>();
            for (int i = 0; i < noOfMutation; i++) {
                final long mutationIndex = generateMutationIndex(length, mutationIndexes);
                mutationIndexes.add(mutationIndex);
                updateIndexMap(indexMap, length, mutationIndex);
            }
        } else if (RandomProviderManager.getRandomProvider().nextDouble() < chromosomeMutationRate) {
            updateIndexMap(indexMap, length);
        }
        return indexMap;
    }

    private long generateMutationIndex(long length, Set<Long> mutationIndexes) {
        long mutationIndex = 0;
        do {
            mutationIndex = RandomProviderManager.getRandomProvider().nextLong(length);
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

    private void updateIndexMap(Map<Integer, Set<Integer>> indexMap, long length) {
        updateIndexMap(indexMap, length, RandomProviderManager.getRandomProvider().nextLong(length));
    }

}
