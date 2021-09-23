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
package org.apache.commons.math4.genetics.population;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math4.genetics.chromosome.Chromosome;
import org.apache.commons.math4.genetics.exception.GeneticException;
import org.apache.commons.math4.genetics.utils.Constants;

/**
 * Population of chromosomes represented by a {@link List}.
 *
 * @param <P> phenotype of chromosome
 * @since 2.0
 */
public class ListPopulation<P> implements Population<P> {

    /** List of chromosomes. */
    private final List<Chromosome<P>> chromosomes;

    /** maximal size of the population. */
    private int populationLimit;

    /**
     * Creates a new ListPopulation instance and initializes its inner chromosome
     * list.
     *
     * @param populationLimit maximal size of the population
     */
    public ListPopulation(final int populationLimit) {
        this(Collections.<Chromosome<P>>emptyList(), populationLimit);
    }

    /**
     * Creates a new ListPopulation instance.
     * <p>
     * Note: the chromosomes of the specified list are added to the population.
     *
     * @param chromosomes     list of chromosomes to be added to the population
     * @param populationLimit maximal size of the population
     */
    public ListPopulation(final List<Chromosome<P>> chromosomes, final int populationLimit) {

        if (chromosomes == null) {
            throw new GeneticException(GeneticException.NULL_ARGUMENT, "chromosomes");
        }
        if (populationLimit <= 0) {
            throw new GeneticException(GeneticException.NOT_STRICTLY_POSITIVE, populationLimit);
        }
        if (chromosomes.size() > populationLimit) {
            throw new GeneticException(GeneticException.LIST_OF_CHROMOSOMES_BIGGER_THAN_POPULATION_SIZE,
                    chromosomes.size(), populationLimit);
        }
        this.populationLimit = populationLimit;
        this.chromosomes = new ArrayList<>(populationLimit);
        this.chromosomes.addAll(chromosomes);
    }

    /**
     * Add a {@link Collection} of chromosomes to this {@link Population}.
     * @param chromosomeColl a {@link Collection} of chromosomes
     * @since 3.1
     */
    public void addChromosomes(final Collection<Chromosome<P>> chromosomeColl) {
        if (chromosomes.size() + chromosomeColl.size() > populationLimit) {
            throw new GeneticException(GeneticException.LIST_OF_CHROMOSOMES_BIGGER_THAN_POPULATION_SIZE,
                    chromosomes.size(), populationLimit);
        }
        this.chromosomes.addAll(chromosomeColl);
    }

    /**
     * Returns an unmodifiable list of the chromosomes in this population.
     * @return the unmodifiable list of chromosomes
     */
    public List<Chromosome<P>> getChromosomes() {
        return Collections.unmodifiableList(chromosomes);
    }

    /**
     * Access the list of chromosomes.
     * @return the list of chromosomes
     * @since 3.1
     */
    protected List<Chromosome<P>> getChromosomeList() {
        return chromosomes;
    }

    /**
     * Add the given chromosome to the population.
     * @param chromosome the chromosome to add.
     */
    @Override
    public void addChromosome(final Chromosome<P> chromosome) {
        if (chromosomes.size() >= populationLimit) {
            throw new GeneticException(GeneticException.LIST_OF_CHROMOSOMES_BIGGER_THAN_POPULATION_SIZE,
                    chromosomes.size(), populationLimit);
        }
        this.chromosomes.add(chromosome);
    }

    /**
     * Access the fittest chromosome in this population.
     * @return the fittest chromosome.
     */
    @Override
    public Chromosome<P> getFittestChromosome() {
        // best so far
        return Collections.max(this.chromosomes);
    }

    /**
     * Access the maximum population size.
     * @return the maximum population size.
     */
    @Override
    public int getPopulationLimit() {
        return this.populationLimit;
    }

    /**
     * Sets the maximal population size.
     * @param populationLimit maximal population size.
     */
    public void setPopulationLimit(final int populationLimit) {
        if (populationLimit <= 0) {
            throw new GeneticException(GeneticException.POPULATION_LIMIT_NOT_POSITIVE, populationLimit);
        }
        if (populationLimit < chromosomes.size()) {
            throw new GeneticException(GeneticException.POPULATION_LIMIT_LESS_THAN_LIST_OF_CHROMOSOMES_SIZE,
                    populationLimit, chromosomes.size());
        }
        this.populationLimit = populationLimit;
    }

    /**
     * Access the current population size.
     * @return the current population size.
     */
    @Override
    public int getPopulationSize() {
        return this.chromosomes.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder populationStrRepr = new StringBuilder();
        for (Chromosome<P> chromosome : chromosomes) {
            populationStrRepr.append(chromosome.toString());
            populationStrRepr.append(Constants.NEW_LINE);
        }
        return populationStrRepr.toString();
    }

    /**
     * Returns an iterator over the unmodifiable list of chromosomes.
     * <p>
     * Any call to {@link Iterator#remove()} will result in a
     * {@link UnsupportedOperationException}.
     * </p>
     *
     * @return chromosome iterator
     */
    @Override
    public Iterator<Chromosome<P>> iterator() {
        return getChromosomes().iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Population<P> nextGeneration(final double elitismRate) {
        final List<Chromosome<P>> oldChromosomes = getChromosomeList();

        if (oldChromosomes.size() * elitismRate == 0) {
            // if no of elite chromosome is 0 crete and return an empty population instance.
            return new ListPopulation<P>(getPopulationLimit());
        } else {
            // create a new generation of chromosomes with same parameters and add the elit
            // individuals.
            final ListPopulation<P> nextGeneration = new ListPopulation<>(getPopulationLimit());

            // Sort the chromosome according to ascending order of fitness.
            Collections.sort(oldChromosomes);

            // index of the last "not good enough" chromosome
            final int boundIndex = (int) Math.ceil((1.0 - elitismRate) * oldChromosomes.size());
            for (int i = boundIndex; i < oldChromosomes.size(); i++) {
                nextGeneration.addChromosome(oldChromosomes.get(i));
            }
            return nextGeneration;
        }
    }

}
