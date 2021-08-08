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
package org.apache.commons.math4.ga.internal.stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.math4.ga.chromosome.Chromosome;
import org.apache.commons.math4.ga.population.Population;
import org.apache.commons.math4.ga.stats.PopulationStatisticalSummary;

/**
 * This class represents an implementation of population statistical summary.
 * @param <P> phenotype of chromosome
 * @since 4.0
 */
public class PopulationStatisticalSummaryImpl<P> implements PopulationStatisticalSummary<P> {

    /** maximum fitness of the population. **/
    private final double maxFitness;

    /** minimum fitness of the population. **/
    private final double minFitness;

    /** mean fitness of the population. **/
    private double meanFitness;

    /** variance of population fitness. **/
    private final double variance;

    /** population size. **/
    private final int populationSize;

    /** a map of chromosome Id and corresponding rank in population. **/
    private final Map<String, Integer> chromosomeIdRankMap = new HashMap<>();

    /**
     * @param population current population {@link Population} of chromosomes
     */
    public PopulationStatisticalSummaryImpl(Population<P> population) {

        // Fetch all chromosomes.
        List<Chromosome<P>> chromosomes = getChromosomes(Objects.requireNonNull(population));

        // Sort all chromosomes.
        Collections.sort(chromosomes);

        this.populationSize = chromosomes.size();
        this.maxFitness = chromosomes.get(chromosomes.size() - 1).evaluate();
        this.minFitness = chromosomes.get(0).evaluate();
        this.meanFitness = calculateMeanFitness(chromosomes);
        this.variance = calculateVariance(chromosomes);

        updateChromosomeIdRankMap(chromosomes);

    }

    /**
     * Updates chromosome Id and rank.
     * @param chromosomes list of chromosomes
     */
    private void updateChromosomeIdRankMap(List<Chromosome<P>> chromosomes) {
        for (int rank = 0; rank < chromosomes.size(); rank++) {
            this.chromosomeIdRankMap.put(chromosomes.get(rank).getId(), rank);
        }
    }

    /**
     * Fetches chromosomes.
     * @param population
     * @return list of chromosomes
     */
    private List<Chromosome<P>> getChromosomes(Population<P> population) {
        List<Chromosome<P>> chromosomes = new ArrayList<>(population.getPopulationSize());
        for (Chromosome<P> chromosome : population) {
            chromosomes.add(chromosome);
        }
        return chromosomes;
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
        return this.populationSize;
    }

    /**
     * Calculate mean fitness.
     * @param chromosomes list of chromosomes
     * @return returns mean fitness
     */
    private double calculateMeanFitness(List<Chromosome<P>> chromosomes) {
        double sum = 0.0;
        for (Chromosome<P> chromosome : chromosomes) {
            sum += chromosome.evaluate();
        }
        return sum / chromosomes.size();
    }

    /**
     * Calculate variance of population fitness.
     * @param chromosomes List of chromosomes
     * @return fitness variance
     */
    private double calculateVariance(List<Chromosome<P>> chromosomes) {
        if (this.meanFitness == 0) {
            this.meanFitness = calculateMeanFitness(chromosomes);
        }
        double sumOfSquare = 0.0;
        for (Chromosome<P> chromosome : chromosomes) {
            sumOfSquare += Math.pow(chromosome.evaluate(), 2);
        }

        return (sumOfSquare / chromosomes.size()) - Math.pow(this.meanFitness, 2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findRank(Chromosome<P> chromosome) {
        return chromosomeIdRankMap.get(chromosome.getId());
    }

}
