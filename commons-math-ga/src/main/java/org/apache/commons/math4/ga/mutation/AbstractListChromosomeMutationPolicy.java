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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math4.ga.chromosome.AbstractListChromosome;
import org.apache.commons.math4.ga.chromosome.Chromosome;
import org.apache.commons.math4.ga.internal.exception.GeneticIllegalArgumentException;
import org.apache.commons.math4.ga.utils.RandomProviderManager;
import org.apache.commons.rng.UniformRandomProvider;

/**
 * An abstraction of mutation operator for {@link AbstractListChromosome}.
 * @param <T> genotype of chromosome
 * @param <P> phenotype of chromosome
 * @since 4.0
 */
public abstract class AbstractListChromosomeMutationPolicy<T, P> implements MutationPolicy<P> {

    /**
     * Mutate the given chromosome based on mutation rate. Checks chromosome
     * validity and finds the mutable gene indexes. For each selected gene invokes
     * {@link #mutateGene(Object)} to perform mutation.
     * @param original     the original chromosome.
     * @param mutationRate the rate of mutation per gene
     * @return the mutated chromosome.
     */
    @Override
    public AbstractListChromosome<T, P> mutate(Chromosome<P> original, double mutationRate) {

        // check for validity
        checkValidity(original);

        @SuppressWarnings("unchecked")
        final AbstractListChromosome<T, P> chromosome = (AbstractListChromosome<T, P>) original;
        final List<T> newRep = new ArrayList<>(chromosome.getRepresentation());

        final Set<Integer> mutableGeneIndexes = getMutableGeneIndexes(chromosome.getLength(), mutationRate);
        for (int mutableGeneIndex : mutableGeneIndexes) {
            newRep.set(mutableGeneIndex, mutateGene(newRep.get(mutableGeneIndex)));
        }

        return chromosome.newChromosome(newRep);
    }

    /**
     * Validates input chromosome.
     * @param original chromosome
     */
    private void checkValidity(Chromosome<P> original) {
        if (!AbstractListChromosome.class.isAssignableFrom(original.getClass())) {
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
    private Set<Integer> getMutableGeneIndexes(int length, double mutationRate) {

        // calculate the total mutation rate of all the alleles i.e. chromosome.
        final double chromosomeMutationRate = mutationRate * length;
        final Set<Integer> indexSet = new HashSet<>();
        final UniformRandomProvider randomProvider = RandomProviderManager.getRandomProvider();

        // if chromosomeMutationRate >= 1 then more than one allele will be mutated.
        if (chromosomeMutationRate >= 1) {
            final int noOfMutation = (int) Math.round(chromosomeMutationRate);
            while (indexSet.size() < noOfMutation) {
                indexSet.add(randomProvider.nextInt(length));
            }
        } else if (randomProvider.nextDouble() < chromosomeMutationRate) {
            indexSet.add(randomProvider.nextInt(length));
        }
        return indexSet;
    }

    /**
     * Mutates an individual gene/allele.
     * @param originalValue the original value of gene
     * @return mutated value of gene
     */
    protected abstract T mutateGene(T originalValue);

}
