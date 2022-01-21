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
package org.apache.commons.math4.ga.population;

import org.apache.commons.math4.ga.chromosome.Chromosome;

/**
 * A collection of chromosomes that facilitates generational evolution.
 *
 * @param <P> phenotype of chromosome
 * @since 2.0
 */
public interface Population<P> extends Iterable<Chromosome<P>> {

    /**
     * Access the current population size.
     * @return the current population size.
     */
    int getPopulationSize();

    /**
     * Access the maximum population size.
     * @return the maximum population size.
     */
    int getPopulationLimit();

    /**
     * Start the population for the next generation.
     * @param elitismRate the Elitism Rate
     * @return the beginnings of the next generation.
     */
    Population<P> nextGeneration(double elitismRate);

    /**
     * Add the given chromosome to the population.
     * @param chromosome the chromosome to add.
     */
    void addChromosome(Chromosome<P> chromosome);

    /**
     * Access the fittest chromosome in this population.
     * @return the fittest chromosome.
     */
    Chromosome<P> getFittestChromosome();
}
