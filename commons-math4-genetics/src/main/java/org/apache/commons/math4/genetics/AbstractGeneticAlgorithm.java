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

import org.apache.commons.math4.genetics.convergencecond.StoppingCondition;
import org.apache.commons.math4.genetics.crossover.CrossoverPolicy;
import org.apache.commons.math4.genetics.listener.ConvergenceListenerRegistry;
import org.apache.commons.math4.genetics.mutation.MutationPolicy;
import org.apache.commons.math4.genetics.population.Population;
import org.apache.commons.math4.genetics.selection.SelectionPolicy;

/**
 * This class represents an abstraction for all Genetic algorithm implementation
 * comprising the basic properties and operations.
 * @param <P> phenotype of chromosome
 */
public abstract class AbstractGeneticAlgorithm<P> {

    /** the crossover policy used by the algorithm. */
    private final CrossoverPolicy<P> crossoverPolicy;

    /** the mutation policy used by the algorithm. */
    private final MutationPolicy<P> mutationPolicy;

    /** the selection policy used by the algorithm. */
    private final SelectionPolicy<P> selectionPolicy;

    /**
     * the number of generations evolved to reach {@link StoppingCondition} in the
     * last run.
     */
    private int generationsEvolved;

    /** The elitism rate haveing default value of .25. */
    private double elitismRate = .25;

    /**
     * constructor.
     * @param crossoverPolicy The {@link CrossoverPolicy}
     * @param mutationPolicy  The {@link MutationPolicy}
     * @param selectionPolicy The {@link SelectionPolicy}
     */
    public AbstractGeneticAlgorithm(final CrossoverPolicy<P> crossoverPolicy, final MutationPolicy<P> mutationPolicy,
            final SelectionPolicy<P> selectionPolicy) {
        this.crossoverPolicy = crossoverPolicy;
        this.mutationPolicy = mutationPolicy;
        this.selectionPolicy = selectionPolicy;
    }

    /**
     * constructor.
     * @param crossoverPolicy The {@link CrossoverPolicy}
     * @param mutationPolicy  The {@link MutationPolicy}
     * @param selectionPolicy The {@link SelectionPolicy}
     * @param elitismRate     The elitism rate
     */
    public AbstractGeneticAlgorithm(final CrossoverPolicy<P> crossoverPolicy, final MutationPolicy<P> mutationPolicy,
            final SelectionPolicy<P> selectionPolicy, double elitismRate) {
        this.crossoverPolicy = crossoverPolicy;
        this.mutationPolicy = mutationPolicy;
        this.selectionPolicy = selectionPolicy;
        this.elitismRate = elitismRate;
    }

    /**
     * Returns the crossover policy.
     * @return crossover policy
     */
    public CrossoverPolicy<P> getCrossoverPolicy() {
        return crossoverPolicy;
    }

    /**
     * Returns the mutation policy.
     * @return mutation policy
     */
    public MutationPolicy<P> getMutationPolicy() {
        return mutationPolicy;
    }

    /**
     * Returns the selection policy.
     * @return selection policy
     */
    public SelectionPolicy<P> getSelectionPolicy() {
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
    public Population<P> evolve(final Population<P> initial, final StoppingCondition<P> condition) {
        Population<P> current = initial;
        // check if stopping condition is satisfied otherwise produce the next
        // generation of population.
        while (!condition.isSatisfied(current)) {
            // notify interested listener
            ConvergenceListenerRegistry.<P>getInstance().notifyAll(generationsEvolved, current);

            current = nextGeneration(current);
            this.generationsEvolved++;
        }

        return current;
    }

    /**
     * Evolve the given population into the next generation.
     * <ol>
     *  <li>Get nextGeneration population to fill from <code>current</code>
     *          generation, using its nextGeneration method</li>
     *  <li>Loop until new generation is filled:
     *      <ul>
     *          <li>Apply configured SelectionPolicy to select a pair of parents from
     *                  <code>current</code>,</li>
     *          <li> apply configured {@link CrossoverPolicy} to parents,</li>
     *          <li> apply configured {@link MutationPolicy} to each of the offspring</li>
     *          <li>Add offspring individually to nextGeneration, space permitting</li>
     *      </ul>
     *  </li>
     *  <li>Return nextGeneration</li>
     * </ol>
     *
     * @param current the current population
     * @return the population for the next generation.
     */
    protected abstract Population<P> nextGeneration(Population<P> current);

    /**
     * Returns the elitism rate.
     * @return elitism rate
     */
    public double getElitismRate() {
        return elitismRate;
    }

}
