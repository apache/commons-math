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

package org.apache.commons.math4.genetics.stats.internal;

import java.util.Arrays;

import org.apache.commons.math4.genetics.model.Chromosome;
import org.apache.commons.math4.genetics.model.Population;
import org.apache.commons.math4.genetics.stats.PopulationStatisticalSummary;

/**
 * This class represents an implementation of population statistical summary.
 */
public class PopulationStatisticalSummaryImpl implements PopulationStatisticalSummary {

    /** array of fitness of the population. **/
    private double[] fitnesses;

    /** maximum fitness of the population. **/
    private double maxFitness;

    /** minimum fitness of the population. **/
    private double minFitness;

    /** mean fitness of the population. **/
    private double meanFitness;

    /** variance of population fitness. **/
    private double variance;

    /**
     * constructor.
     * @param population
     */
    public PopulationStatisticalSummaryImpl(Population population) {
        double[] populationFitnesses = getFitnesses(population);
        Arrays.sort(populationFitnesses);
        this.fitnesses = populationFitnesses;
        this.maxFitness = populationFitnesses[populationFitnesses.length - 1];
        this.minFitness = populationFitnesses[0];
        this.meanFitness = calculateMeanFitness(populationFitnesses);
        this.variance = calculateVariance(populationFitnesses);
    }

    private double[] getFitnesses(Population population) {
        double[] populationFitnesses = new double[population.getPopulationSize()];
        int index = 0;
        for (Chromosome chromosome : population) {
            populationFitnesses[index++] = chromosome.getFitness();
        }
        return populationFitnesses;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMeanFitness() {
        return this.meanFitness;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getFitnessVariance() {
        return this.variance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMaxFitness() {
        return this.maxFitness;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMinFitness() {
        return this.minFitness;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getPopulationSize() {
        return this.fitnesses.length;
    }

    /**
     * calculate mean fitness.
     * @param populationFitnesses
     * @return mean fitness
     */
    private double calculateMeanFitness(double[] populationFitnesses) {
        double sum = 0.0;
        for (double fitness : populationFitnesses) {
            sum += fitness;
        }
        return sum / populationFitnesses.length;
    }

    /**
     * calculate variance of population fitness.
     * @param populationFitnesses
     * @return variance
     */
    private double calculateVariance(double[] populationFitnesses) {
        if (this.meanFitness == 0) {
            this.meanFitness = calculateMeanFitness(populationFitnesses);
        }
        double sumOfSquare = 0.0;
        for (double fitness : populationFitnesses) {
            sumOfSquare += Math.pow(fitness, 2);
        }

        return (sumOfSquare / populationFitnesses.length) - Math.pow(this.meanFitness, 2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findRank(Chromosome chromosome) {
        return Arrays.binarySearch(fitnesses, chromosome.getFitness());
    }

}
