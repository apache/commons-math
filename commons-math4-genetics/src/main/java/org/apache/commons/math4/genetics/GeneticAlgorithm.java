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
package org.apache.commons.math4.genetics;

import java.util.List;

import org.apache.commons.math4.genetics.exception.GeneticException;
import org.apache.commons.math4.genetics.listeners.ConvergenceListener;
import org.apache.commons.math4.genetics.model.ChromosomePair;
import org.apache.commons.math4.genetics.model.Population;
import org.apache.commons.math4.genetics.operators.CrossoverPolicy;
import org.apache.commons.math4.genetics.operators.MutationPolicy;
import org.apache.commons.math4.genetics.operators.SelectionPolicy;
import org.apache.commons.math4.genetics.stats.PopulationStatisticalSummary;
import org.apache.commons.math4.genetics.utils.Constants;

/**
 * Implementation of a genetic algorithm. All factors that govern the operation
 * of the algorithm can be configured for a specific problem.
 *
 * @since 2.0
 */
public class GeneticAlgorithm extends AbstractGeneticAlgorithm {

	/** the rate of crossover for the algorithm. */
	private final double crossoverRate;

	/** the rate of mutation for the algorithm. */
	private final double mutationRate;

	/**
	 * Create a new genetic algorithm.
	 * 
	 * @param crossoverPolicy The {@link CrossoverPolicy}
	 * @param crossoverRate   The crossover rate as a percentage (0-1 inclusive)
	 * @param mutationPolicy  The {@link MutationPolicy}
	 * @param mutationRate    The mutation rate as a percentage (0-1 inclusive)
	 * @param selectionPolicy The {@link SelectionPolicy}
	 * @throws GeneticException if the crossover or mutation rate is outside the [0,
	 *                          1] range
	 */
	public GeneticAlgorithm(final CrossoverPolicy crossoverPolicy, final double crossoverRate,
			final MutationPolicy mutationPolicy, final double mutationRate, final SelectionPolicy selectionPolicy) {
		super(crossoverPolicy, mutationPolicy, selectionPolicy);

		if (crossoverRate < 0 || crossoverRate > 1) {
			throw new GeneticException(GeneticException.OUT_OF_RANGE, crossoverRate, Constants.CROSSOVER_RATE, 0, 1);
		}
		if (mutationRate < 0 || mutationRate > 1) {
			throw new GeneticException(GeneticException.OUT_OF_RANGE, mutationRate, Constants.MUTATION_RATE, 0, 1);
		}
		this.crossoverRate = crossoverRate;
		this.mutationRate = mutationRate;
	}

	/**
	 * Evolve the given population into the next generation.
	 * <ol>
	 * <li>Get nextGeneration population to fill from <code>current</code>
	 * generation, using its nextGeneration method</li>
	 * <li>Loop until new generation is filled:
	 * <ul>
	 * <li>Apply configured SelectionPolicy to select a pair of parents from
	 * <code>current</code></li>
	 * <li>With probability = {@link #getCrossoverRate()}, apply configured
	 * {@link CrossoverPolicy} to parents</li>
	 * <li>With probability = {@link #getMutationRate()}, apply configured
	 * {@link MutationPolicy} to each of the offspring</li>
	 * <li>Add offspring individually to nextGeneration, space permitting</li>
	 * </ul>
	 * </li>
	 * <li>Return nextGeneration</li>
	 * </ol>
	 *
	 * @param current         the current population.
	 * @param populationStats the statistical summary of population.
	 * @return the population for the next generation.
	 */
	protected Population nextGeneration(final Population current, PopulationStatisticalSummary populationStats) {
		Population nextGeneration = current.nextGeneration();

		while (nextGeneration.getPopulationSize() < nextGeneration.getPopulationLimit()) {
			// select parent chromosomes
			ChromosomePair pair = getSelectionPolicy().select(current);

			// apply crossover policy to create two offspring
			pair = getCrossoverPolicy().crossover(pair.getFirst(), pair.getSecond(), crossoverRate);

			// apply mutation policy to the chromosomes
			pair = new ChromosomePair(getMutationPolicy().mutate(pair.getFirst(), mutationRate),
					getMutationPolicy().mutate(pair.getSecond(), mutationRate));

			// add the first chromosome to the population
			nextGeneration.addChromosome(pair.getFirst());
			// is there still a place for the second chromosome?
			if (nextGeneration.getPopulationSize() < nextGeneration.getPopulationLimit()) {
				// add the second chromosome to the population
				nextGeneration.addChromosome(pair.getSecond());
			}
		}

		return nextGeneration;
	}

	/**
	 * Returns the crossover rate.
	 * 
	 * @return crossover rate
	 */
	public double getCrossoverRate() {
		return crossoverRate;
	}

	/**
	 * Returns the mutation rate.
	 * 
	 * @return mutation rate
	 */
	public double getMutationRate() {
		return mutationRate;
	}

}
