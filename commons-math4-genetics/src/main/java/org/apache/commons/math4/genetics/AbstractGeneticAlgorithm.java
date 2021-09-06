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

import org.apache.commons.math4.genetics.listeners.ConvergenceListenerRegistry;
import org.apache.commons.math4.genetics.model.Population;
import org.apache.commons.math4.genetics.operators.CrossoverPolicy;
import org.apache.commons.math4.genetics.operators.MutationPolicy;
import org.apache.commons.math4.genetics.operators.SelectionPolicy;
import org.apache.commons.math4.genetics.operators.StoppingCondition;

public abstract class AbstractGeneticAlgorithm {

	/** the crossover policy used by the algorithm. */
	private final CrossoverPolicy crossoverPolicy;

	/** the mutation policy used by the algorithm. */
	private final MutationPolicy mutationPolicy;

	/** the selection policy used by the algorithm. */
	private final SelectionPolicy selectionPolicy;

	/**
	 * the number of generations evolved to reach {@link StoppingCondition} in the
	 * last run.
	 */
	private int generationsEvolved = 0;

	/** The elitism rate haveing default value of .25 */
	private double elitismRate = .25;

	public AbstractGeneticAlgorithm(final CrossoverPolicy crossoverPolicy, final MutationPolicy mutationPolicy,
			final SelectionPolicy selectionPolicy) {
		this.crossoverPolicy = crossoverPolicy;
		this.mutationPolicy = mutationPolicy;
		this.selectionPolicy = selectionPolicy;
	}

	public AbstractGeneticAlgorithm(final CrossoverPolicy crossoverPolicy, final MutationPolicy mutationPolicy,
			final SelectionPolicy selectionPolicy, double elitismRate) {
		this.crossoverPolicy = crossoverPolicy;
		this.mutationPolicy = mutationPolicy;
		this.selectionPolicy = selectionPolicy;
	}

	/**
	 * Returns the crossover policy.
	 * 
	 * @return crossover policy
	 */
	public CrossoverPolicy getCrossoverPolicy() {
		return crossoverPolicy;
	}

	/**
	 * Returns the mutation policy.
	 * 
	 * @return mutation policy
	 */
	public MutationPolicy getMutationPolicy() {
		return mutationPolicy;
	}

	/**
	 * Returns the selection policy.
	 * 
	 * @return selection policy
	 */
	public SelectionPolicy getSelectionPolicy() {
		return selectionPolicy;
	}

	/**
	 * Returns the number of generations evolved to reach {@link StoppingCondition}
	 * in the last run.
	 *
	 * @return number of generations evolved
	 * @since 2.1
	 */
	public int getGenerationsEvolved() {
		return generationsEvolved;
	}

	/**
	 * Evolve the given population. Evolution stops when the stopping condition is
	 * satisfied. Updates the {@link #getGenerationsEvolved() generationsEvolved}
	 * property with the number of generations evolved before the StoppingCondition
	 * is satisfied.
	 *
	 * @param initial   the initial, seed population.
	 * @param condition the stopping condition used to stop evolution.
	 * @return the population that satisfies the stopping condition.
	 */
	public Population evolve(final Population initial, final StoppingCondition condition) {
		Population current = initial;
		// check if stopping condition is satisfied otherwise produce the next
		// generation of population.
		while (!condition.isSatisfied(current)) {
			// notify interested listener
			ConvergenceListenerRegistry.getInstance().notifyAll(current);

			current = nextGeneration(current);
			this.generationsEvolved++;
		}

		return current;
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
	 * @param current the current population
	 * @return the population for the next generation.
	 */
	protected abstract Population nextGeneration(final Population current);

	/**
	 * Returns the elitism rate.
	 * 
	 * @return elitism rate
	 */
	public double getElitismRate() {
		return elitismRate;
	}

}
