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

package org.apache.commons.math4.examples.ga.tsp.legacy;

import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.StoppingCondition;

/**
 * This class represents the stopping condition based on unchanged best fitness.
 */
public class UnchangedBestFitness implements StoppingCondition {

    /** last best fitness. **/
    private double lastBestFitness = Double.MIN_VALUE;

    /** maximum number of generations evolved with unchanged best fitness. **/
    private final int maxGenerationsWithUnchangedBestFitness;

    /** generations having unchanged best fitness. **/
    private int generationsHavingUnchangedBestFitness;

    /**
     * constructor.
     * @param maxGenerationsWithUnchangedAverageFitness maximum number of
     *                                                  generations evolved with
     *                                                  unchanged best fitness
     */
    public UnchangedBestFitness(final int maxGenerationsWithUnchangedAverageFitness) {
        this.maxGenerationsWithUnchangedBestFitness = maxGenerationsWithUnchangedAverageFitness;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSatisfied(Population population) {
        final double currentBestFitness = population.getFittestChromosome().getFitness();

        if (lastBestFitness == currentBestFitness) {
            this.generationsHavingUnchangedBestFitness++;
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
