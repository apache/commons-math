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

import org.apache.commons.math4.genetics.population.Population;

/**
 * This class represents a stopping condition based on best fitness value.
 * Convergence will be stopped once best fitness remains unchanged for
 * predefined number of generations.
 * @param <P> phenotype of chromosome
 */
public class UnchangedBestFitness<P> implements StoppingCondition<P> {

    /** best fitness of previous generation. **/
    private double lastBestFitness = Double.MIN_VALUE;

    /**
     * The configured number of generations for which optimization process will
     * continue with unchanged best fitness value.
     **/
    private final int maxGenerationsWithUnchangedBestFitness;

    /** Number of generations the best fitness value has not been changed. **/
    private int generationsHavingUnchangedBestFitness;

    /**
     * constructor.
     * @param maxGenerationsWithUnchangedAverageFitness maximum number of
     *                                                  generations with unchanged
     *                                                  best fitness
     */
    public UnchangedBestFitness(final int maxGenerationsWithUnchangedAverageFitness) {
        this.maxGenerationsWithUnchangedBestFitness = maxGenerationsWithUnchangedAverageFitness;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSatisfied(Population<P> population) {
        final double currentBestFitness = population.getFittestChromosome().evaluate();

        if (lastBestFitness == currentBestFitness) {
            generationsHavingUnchangedBestFitness++;
            if (generationsHavingUnchangedBestFitness == maxGenerationsWithUnchangedBestFitness) {
                return true;
            }
        } else {
            this.generationsHavingUnchangedBestFitness = 0;
            lastBestFitness = currentBestFitness;
        }

        return false;
    }

}
