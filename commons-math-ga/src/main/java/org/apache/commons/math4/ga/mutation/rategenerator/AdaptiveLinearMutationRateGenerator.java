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

package org.apache.commons.math4.ga.mutation.rategenerator;

import org.apache.commons.math4.ga.chromosome.Chromosome;
import org.apache.commons.math4.ga.stats.PopulationStatisticalSummary;

/**
 * Generates mutation rate using linear function of relative rank of input
 * chromosome in population.
 * @param <P> phenotype of chromosome
 */
public class AdaptiveLinearMutationRateGenerator<P> implements MutationRateGenerator<P> {

    /** minimum crossover rate. **/
    private final double minimumRate;

    /** maximum crossover rate. **/
    private final double maximumRate;

    /**
     * @param minimumRate minimum mutation rate
     * @param maximumRate maximum mutation rate
     */
    public AdaptiveLinearMutationRateGenerator(double minimumRate, double maximumRate) {
        this.minimumRate = minimumRate;
        this.maximumRate = maximumRate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double generate(Chromosome<P> chromosome, PopulationStatisticalSummary<P> populationStats, int generation) {
        return minimumRate + (maximumRate - minimumRate) *
                (1.0 - (double) populationStats.findRank(chromosome) / (populationStats.getPopulationSize() - 1));
    }

}
