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

package org.apache.commons.math4.ga;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.math4.ga.convergence.StoppingCondition;
import org.apache.commons.math4.ga.crossover.CrossoverPolicy;
import org.apache.commons.math4.ga.listener.ConvergenceListener;
import org.apache.commons.math4.ga.mutation.MutationPolicy;
import org.apache.commons.math4.ga.population.Population;
import org.apache.commons.math4.ga.selection.SelectionPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstraction for all Genetic algorithm implementation comprising the basic
 * properties and operations.
 * @param <P> phenotype of chromosome
 * @since 4.0
 */
public abstract class AbstractGeneticAlgorithm<P> {

    /** instance of logger. **/
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractGeneticAlgorithm.class);

    /** the crossover policy used by the algorithm. */
    private final CrossoverPolicy<P> crossoverPolicy;

    /** the mutation policy used by the algorithm. */
    private final MutationPolicy<P> mutationPolicy;

    /** the selection policy used by the algorithm. */
    private final SelectionPolicy<P> selectionPolicy;

    /** The elitism rate having default value of .25. */
    private final double elitismRate;

    /**
     * the number of generations evolved to reach {@link StoppingCondition} in the
     * last run.
     */
    private int generationsEvolved;

    /** The registry for all interested convergence listeners. **/
    private ConvergenceListenerRegistry<P> convergenceListenerRegistry = new ConvergenceListenerRegistry<>();

    /**
     * @param crossoverPolicy      The {@link CrossoverPolicy}
     * @param mutationPolicy       The {@link MutationPolicy}
     * @param selectionPolicy      The {@link SelectionPolicy}
     * @param elitismRate          The elitism rate
     * @param convergenceListeners An optional collection of
     *                             {@link ConvergenceListener} with variable arity
     */
    @SafeVarargs
    protected AbstractGeneticAlgorithm(final CrossoverPolicy<P> crossoverPolicy,
            final MutationPolicy<P> mutationPolicy,
            final SelectionPolicy<P> selectionPolicy,
            double elitismRate,
            ConvergenceListener<P>... convergenceListeners) {
        this.crossoverPolicy = crossoverPolicy;
        this.mutationPolicy = mutationPolicy;
        this.selectionPolicy = selectionPolicy;
        this.elitismRate = elitismRate;

        if (convergenceListeners.length > 0) {
            for (ConvergenceListener<P> convergenceListener : convergenceListeners) {
                convergenceListenerRegistry.addConvergenceListener(convergenceListener);
            }
        }
    }

    /**
     * Returns the crossover policy.
     * @return crossover policy
     */
    protected CrossoverPolicy<P> getCrossoverPolicy() {
        return crossoverPolicy;
    }

    /**
     * Returns the mutation policy.
     * @return mutation policy
     */
    protected MutationPolicy<P> getMutationPolicy() {
        return mutationPolicy;
    }

    /**
     * Returns the selection policy.
     * @return selection policy
     */
    protected SelectionPolicy<P> getSelectionPolicy() {
        return selectionPolicy;
    }

    /**
     * Returns the number of generations evolved to reach {@link StoppingCondition}
     * in the last run.
     * @return number of generations evolved
     */
    public int getGenerationsEvolved() {
        return generationsEvolved;
    }

    /**
     * Evolve the given population. Evolution stops when the stopping condition is
     * satisfied. Updates the {@link #getGenerationsEvolved() generationsEvolved}
     * property with the number of generations evolved before the StoppingCondition
     * is satisfied.
     * @param initial     the initial, seed population.
     * @param condition   the stopping condition used to stop evolution.
     * @param threadCount number of threads for executor service.
     * @return the population that satisfies the stopping condition.
     */
    public Population<P> evolve(final Population<P> initial,
            final StoppingCondition<P> condition,
            final int threadCount) {
        final ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        try {
            return evolve(initial, condition, executorService);
        } finally {
            executorService.shutdown();
        }
    }

    /**
     * Evolve the given population. Evolution stops when the stopping condition is
     * satisfied. Updates the {@link #getGenerationsEvolved() generationsEvolved}
     * property with the number of generations evolved before the StoppingCondition
     * is satisfied.
     * @param initial         the initial, seed population.
     * @param condition       the stopping condition used to stop evolution.
     * @param executorService the executor service to run threads.
     * @return the population that satisfies the stopping condition.
     */
    public Population<P> evolve(final Population<P> initial,
            final StoppingCondition<P> condition,
            ExecutorService executorService) {
        Population<P> current = initial;

        LOGGER.info("Starting evolution process.");
        // check if stopping condition is satisfied otherwise produce the next
        // generation of population.
        while (!condition.isSatisfied(current)) {
            // notify interested listener
            convergenceListenerRegistry.notifyAll(generationsEvolved, current);

            current = nextGeneration(current, executorService);
            this.generationsEvolved++;
        }
        LOGGER.info("Population convergence achieved after generations: " + generationsEvolved);

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
     * <code>current</code>,</li>
     * <li>apply configured {@link CrossoverPolicy} to parents,</li>
     * <li>apply configured {@link MutationPolicy} to each of the offspring</li>
     * <li>Add offspring individually to nextGeneration, space permitting</li>
     * </ul>
     * </li>
     * <li>Return nextGeneration</li>
     * </ol>
     *
     * @param current         the current population
     * @param executorService the executor service to run threads
     * @return the population for the next generation.
     */
    protected abstract Population<P> nextGeneration(Population<P> current, ExecutorService executorService);

    /**
     * Returns the elitism rate.
     * @return elitism rate
     */
    protected double getElitismRate() {
        return elitismRate;
    }

    /**
     * Responsible for registering the interested listeners and notifying all when
     * required.
     * @param <P> phenotype
     */
    private class ConvergenceListenerRegistry<P> {

        /**
         * List of registered listeners.
         */
        private final List<ConvergenceListener<P>> listeners = new ArrayList<>();

        /**
         * Registers the interested ConvergenceListener passed as an argument.
         * @param convergenceListener The {@link ConvergenceListener}
         */
        private void addConvergenceListener(ConvergenceListener<P> convergenceListener) {
            Objects.requireNonNull(convergenceListener);
            this.listeners.add(convergenceListener);
        }

        /**
         * Notifies all registered ConvergenceListeners about the population statistics.
         * @param generation current generation
         * @param population population of chromosomes
         */
        private void notifyAll(int generation, Population<P> population) {
            for (ConvergenceListener<P> convergenceListener : listeners) {
                convergenceListener.notify(generation, population);
            }
        }
    }

}
