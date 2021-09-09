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

import org.apache.commons.math4.genetics.model.Population;

/**
 * This class represents a stopping condition based on best fitness value.
 * Convergence will be stopped once best fitness remains unchanged for
 * predefined number of generations.
 */
public class UnchangedBestFitness implements StoppingCondition {

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
     * @param maxGenerationsWithUnchangedAverageFitness
     */
    public UnchangedBestFitness(final int maxGenerationsWithUnchangedAverageFitness) {
        this.maxGenerationsWithUnchangedBestFitness = maxGenerationsWithUnchangedAverageFitness;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSatisfied(Population population) {
        double currentBestFitness = population.getFittestChromosome().getFitness();

        if (lastBestFitness == currentBestFitness) {
            if (generationsHavingUnchangedBestFitness == maxGenerationsWithUnchangedBestFitness) {
                return true;
            } else {
                this.generationsHavingUnchangedBestFitness++;
            }
        } else {
            this.generationsHavingUnchangedBestFitness = 0;
            lastBestFitness = currentBestFitness;
        }

        return false;
    }

}
