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
package org.apache.commons.math4.genetics.model;

import java.util.Collections;
import java.util.List;

import org.apache.commons.math4.genetics.exception.GeneticException;
import org.apache.commons.math4.genetics.utils.Constants;

/**
 * Population of chromosomes which uses elitism (certain percentage of the best
 * chromosomes is directly copied to the next generation).
 *
 * @since 2.0
 */
public class ElitisticListPopulation extends ListPopulation {

	/** percentage of chromosomes copied to the next generation. */
	private double elitismRate = 0.9;

	/**
	 * Creates a new {@link ElitisticListPopulation} instance.
	 *
	 * @param chromosomes     list of chromosomes in the population
	 * @param populationLimit maximal size of the population
	 * @param elitismRate     how many best chromosomes will be directly transferred
	 *                        to the next generation [in %]
	 * @throws GeneticException     if the list of chromosomes is {@code null}
	 * @throws GeneticException      if the population limit is not a positive
	 *                                   number (&lt; 1)
	 * @throws GeneticException if the list of chromosomes exceeds the
	 *                                   population limit
	 * @throws GeneticException       if the elitism rate is outside the [0, 1]
	 *                                   range
	 */
	public ElitisticListPopulation(final List<Chromosome> chromosomes, final int populationLimit,
			final double elitismRate) {
		super(chromosomes, populationLimit);
		setElitismRate(elitismRate);
	}

	/**
	 * Creates a new {@link ElitisticListPopulation} instance and initializes its
	 * inner chromosome list.
	 *
	 * @param populationLimit maximal size of the population
	 * @param elitismRate     how many best chromosomes will be directly transferred
	 *                        to the next generation [in %]
	 * @throws GeneticException if the population limit is not a positive number
	 *                              (&lt; 1)
	 * @throws GeneticException  if the elitism rate is outside the [0, 1] range
	 */
	public ElitisticListPopulation(final int populationLimit, final double elitismRate) {
		super(populationLimit);
		setElitismRate(elitismRate);
	}

	/**
	 * Start the population for the next generation. The
	 * <code>{@link #elitismRate}</code> percents of the best chromosomes are
	 * directly copied to the next generation.
	 *
	 * @return the beginnings of the next generation.
	 */
	@Override
	public Population nextGeneration() {
		// initialize a new generation with the same parameters
		ElitisticListPopulation nextGeneration = new ElitisticListPopulation(getPopulationLimit(), getElitismRate());

		final List<Chromosome> oldChromosomes = getChromosomeList();
		Collections.sort(oldChromosomes);

		// index of the last "not good enough" chromosome
		int boundIndex = (int) Math.ceil((1.0 - getElitismRate()) * oldChromosomes.size());
		for (int i = boundIndex; i < oldChromosomes.size(); i++) {
			nextGeneration.addChromosome(oldChromosomes.get(i));
		}
		return nextGeneration;
	}

	/**
	 * Sets the elitism rate, i.e. how many best chromosomes will be directly
	 * transferred to the next generation [in %].
	 *
	 * @param elitismRate how many best chromosomes will be directly transferred to
	 *                    the next generation [in %]
	 * @throws GeneticException if the elitism rate is outside the [0, 1] range
	 */
	public void setElitismRate(final double elitismRate) {
		if (elitismRate < 0 || elitismRate > 1) {
			throw new GeneticException(GeneticException.OUT_OF_RANGE, elitismRate, Constants.ELITISM_RATE, 0, 1);
		}
		this.elitismRate = elitismRate;
	}

	/**
	 * Access the elitism rate.
	 * 
	 * @return the elitism rate
	 */
	public double getElitismRate() {
		return this.elitismRate;
	}

}
