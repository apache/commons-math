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
package org.apache.commons.math.genetics;

/**
 * Implementation of a genetic algorithm. All factors that govern the operation
 * of the algorithm can be configured for a specific problem.
 * @version $Revision$ $Date$
 */
public class GeneticAlgorithm {
    /** the crossover policy used by the algorithm. */
    private CrossoverPolicy crossoverPolicy;

    /** the rate of crossover for the algorithm. */
    private double crossoverRate;

    /** the mutation policy used by the algorithm. */
    private MutationPolicy mutationPolicy;

    /** the rate of mutation for the algorithm. */
    private double mutationRate;

    /** the selection policy used by the algorithm. */
    private SelectionPolicy selectionPolicy;

    /**
     * Evolve the given population. Evolution stops when the stopping condition
     * is satisfied.
     * 
     * @param initial the initial, seed population.
     * @param condition the stopping condition used to stop evolution.
     * @return the population that satisfies the stopping condition.
     */
    public Population evolve(Population initial, StoppingCondition condition) {
        Population current = initial;
        while (!condition.isSatisfied(current)) {
            current = nextGeneration(current);
        }
        return current;
    }

    /**
     * Access the crossover policy.
     * 
     * @return the crossover policy.
     */
    private CrossoverPolicy getCrossoverPolicy() {
        return crossoverPolicy;
    }

    /**
     * Access the crossover rate.
     * 
     * @return the crossover rate.
     */
    private double getCrossoverRate() {
        return crossoverRate;
    }

    /**
     * Access the mutation policy.
     * 
     * @return the mutation policy.
     */
    private MutationPolicy getMutationPolicy() {
        return mutationPolicy;
    }

    /**
     * Access the mutation rate.
     * 
     * @return the mutation rate.
     */
    private double getMutationRate() {
        return mutationRate;
    }

    /**
     * Access the selection policy.
     * 
     * @return the selection policy.
     */
    private SelectionPolicy getSelectionPolicy() {
        return selectionPolicy;
    }

    /**
     * Evolve the given population into the next generation.
     * 
     * @param current the current population.
     * @return the population for the next generation.
     */
    private Population nextGeneration(Population current) {
        Population nextGeneration = current.nextGeneration();

        while (nextGeneration.getPopulationSize() < nextGeneration
                .getPopulationLimit()) {
            // select parent chromosomes
            ChromosomePair pair = getSelectionPolicy().select(current);

            // apply crossover policy to create two offspring
            if (Math.random() < getCrossoverRate()) {
                pair = getCrossoverPolicy().crossover(pair.getFirst(),
                        pair.getSecond());
            }

            // apply mutation policy to first offspring
            if (Math.random() < getMutationRate()) {
                nextGeneration.addChromosome(getMutationPolicy().mutate(
                        pair.getFirst()));

                if (nextGeneration.getPopulationSize() < nextGeneration
                        .getPopulationLimit()) {
                    // apply mutation policy to second offspring
                    nextGeneration.addChromosome(getMutationPolicy().mutate(
                            pair.getSecond()));
                }
            }
        }

        return nextGeneration;
    }

    /**
     * Modify the crossover policy.
     * 
     * @param value the new crossover policy.
     */
    public void setCrossoverPolicy(CrossoverPolicy value) {
        this.crossoverPolicy = value;
    }

    /**
     * Modify the crossover rate.
     * 
     * @param value the new crossover rate.
     */
    public void setCrossoverRate(double value) {
        this.crossoverRate = value;
    }

    /**
     * Modify the mutation policy.
     * 
     * @param value the new mutation policy.
     */
    public void setMutationPolicy(MutationPolicy value) {
        this.mutationPolicy = value;
    }

    /**
     * Modify the mutation rate.
     * 
     * @param value the new mutation rate.
     */
    public void setMutationRate(double value) {
        this.mutationRate = value;
    }

    /**
     * Modify the selection policy.
     * 
     * @param value the new selection policy.
     */
    public void setSelectionPolicy(SelectionPolicy value) {
        this.selectionPolicy = value;
    }
}
