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

package org.apache.commons.math4.genetics.convergencecond;

import org.apache.commons.math4.genetics.chromosome.Chromosome;
import org.apache.commons.math4.genetics.population.Population;

/**
 * This class represents a stopping condition based on mean fitness value.
 * Convergence will be stopped once mean fitness remains unchanged for
 * predefined number of generations.
 * @param <P> phenotype of chromosome
 * @since 4.0
 */
public class UnchangedMeanFitness<P> implements StoppingCondition<P> {

    /** Mean fitness of previous generation. **/
    private double lastMeanFitness = Double.MIN_VALUE;

    /**
     * The configured number of generations for which optimization process will
     * continue with unchanged best fitness value.
     **/
    private final int maxGenerationsWithUnchangedMeanFitness;

    /** Number of generations the mean fitness value has not been changed. **/
    private int generationsHavingUnchangedMeanFitness;

    /**
     * constructor.
     * @param maxGenerationsWithUnchangedMeanFitness maximum number of generations
     *                                               with unchanged mean fitness
     */
    public UnchangedMeanFitness(final int maxGenerationsWithUnchangedMeanFitness) {
        this.maxGenerationsWithUnchangedMeanFitness = maxGenerationsWithUnchangedMeanFitness;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSatisfied(Population<P> population) {

        final double currentMeanFitness = calculateMeanFitness(population);

        if (lastMeanFitness == currentMeanFitness) {
            generationsHavingUnchangedMeanFitness++;
            if (generationsHavingUnchangedMeanFitness == maxGenerationsWithUnchangedMeanFitness) {
                return true;
            }
        } else {
            this.generationsHavingUnchangedMeanFitness = 0;
            lastMeanFitness = currentMeanFitness;
        }

        return false;
    }

    /**
     * calculates mean fitness of the population.
     * @param population
     * @return mean fitness
     */
    private double calculateMeanFitness(Population<P> population) {
        double totalFitness = 0.0;
        for (Chromosome<P> chromosome : population) {
            totalFitness += chromosome.evaluate();
        }
        return totalFitness / population.getPopulationSize();
    }
}
