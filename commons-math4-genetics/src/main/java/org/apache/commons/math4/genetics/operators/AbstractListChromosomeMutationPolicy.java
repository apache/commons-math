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

package org.apache.commons.math4.genetics.operators;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math4.genetics.exception.GeneticException;
import org.apache.commons.math4.genetics.model.AbstractListChromosome;
import org.apache.commons.math4.genetics.model.Chromosome;
import org.apache.commons.math4.genetics.utils.RandomGenerator;

public abstract class AbstractListChromosomeMutationPolicy<T> implements MutationPolicy {

	/**
	 * Mutate the given chromosome. Randomly changes few genes depending on mutation
	 * rate.
	 *
	 * @param original     the original chromosome.
	 * @param mutationRate the rate of mutation per gene
	 * @return the mutated chromosome.
	 * @throws GeneticException if <code>original</code> is not an instance of
	 *                          {@link AbstractListChromosome}.
	 */
	@Override
	public Chromosome mutate(Chromosome original, double mutationRate) {
		if (!AbstractListChromosome.class.isAssignableFrom(original.getClass())) {
			throw new GeneticException(GeneticException.ILLEGAL_ARGUMENT, original.getClass().getSimpleName());
		}
		AbstractListChromosome<T> chromosome = (AbstractListChromosome<T>) original;
		List<T> newRep = new ArrayList<>(chromosome.getRepresentation());

		int[] geneIndexes = getMutableGeneIndexes(chromosome.getLength(), mutationRate);
		for (int geneIndex : geneIndexes) {
			newRep.set(geneIndex, mutateGene(newRep.get(geneIndex)));
		}
		return chromosome.newFixedLengthChromosome(newRep);
	}

	/**
	 * Selects and returns mutable gene indexes based on mutation rate.
	 * 
	 * @param length       no of alleles in chromosome
	 * @param mutationRate of the allele
	 * @return mutable gene indexes
	 */
	protected int[] getMutableGeneIndexes(int length, double mutationRate) {
		// calculate the total mutation rate of all the alleles i.e. chromosome.
		double chromosomeMutationRate = mutationRate * length;

		// if chromosomeMutationRate > 1 then more than one allele will be mutated.
		if (chromosomeMutationRate >= 1) {
			int noOfMutation = (int) Math.round(chromosomeMutationRate);
			int[] indexes = new int[noOfMutation];
			for (int i = 0; i < noOfMutation; i++) {
				indexes[i] = RandomGenerator.getRandomGenerator().nextInt(length);
			}
			return indexes;
		} else if (RandomGenerator.getRandomGenerator().nextDouble() < chromosomeMutationRate) {
			// randomly selects only one gene for mutation.
			return new int[] { RandomGenerator.getRandomGenerator().nextInt(length) };
		} else {
			// return a blank array of indexes.
			return new int[0];
		}
	}

	/**
	 * Mutates an individual gene.
	 * 
	 * @param originalValue the original value of gene
	 * @return mutated value of gene
	 */
	protected abstract T mutateGene(T originalValue);

}
