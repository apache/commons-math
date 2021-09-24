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

package org.apache.commons.math4.genetics.stats;

import org.apache.commons.math4.genetics.chromosome.Chromosome;

/**
 * This interface represents the statistical summary for population fitness.
 * @param <P> phenotype of chromosome
 * @since 4.0
 */
public interface PopulationStatisticalSummary<P> {

    /**
     * Returns the arithmetic mean of population fitness.
     * @return The mean or Double.NaN if no values have been added.
     */
    double getMeanFitness();

    /**
     * Returns the variance of the population fitness.
     * @return The variance, Double.NaN if no values have been added or 0.0 for a
     *         single value set.
     */
    double getFitnessVariance();

    /**
     * Returns the minimum fitness of the population.
     * @return The max or Double.NaN if no values have been added.
     */
    double getMinFitness();

    /**
     * Returns the maximum fitness of the population.
     * @return The max or Double.NaN if no values have been added.
     */
    double getMaxFitness();

    /**
     * Returns the population size.
     * @return The number of available values
     */
    long getPopulationSize();

    /**
     * Calculates the rank of chromosome in population based on its fitness.
     * @param chromosome chromosome, for which rank would be found
     * @return the rank of chromosome
     */
    int findRank(Chromosome<P> chromosome);

}
