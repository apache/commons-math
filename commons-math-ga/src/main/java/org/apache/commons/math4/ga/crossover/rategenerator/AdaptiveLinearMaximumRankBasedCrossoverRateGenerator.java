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

package org.apache.commons.math4.ga.crossover.rategenerator;

import org.apache.commons.math4.ga.chromosome.Chromosome;
import org.apache.commons.math4.ga.stats.PopulationStatisticalSummary;

/**
 * Generates crossover rate based on linear function of relative maximum rank of
 * input chromosomes in population.
 * @param <P> phenotype of chromosome
 */
public class AdaptiveLinearMaximumRankBasedCrossoverRateGenerator<P> implements CrossoverRateGenerator<P> {

    /** minimum crossover rate. **/
    private final double minimumRate;

    /** maximum crossover rate. **/
    private final double maximumRate;

    /**
     * @param minimumRate minimum crossover rate
     * @param maximumRate maximum crossover rate
     */
    public AdaptiveLinearMaximumRankBasedCrossoverRateGenerator(double minimumRate, double maximumRate) {
        this.maximumRate = maximumRate;
        this.minimumRate = minimumRate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double generate(Chromosome<P> first,
            Chromosome<P> second,
            PopulationStatisticalSummary<P> populationStats,
            int generation) {
        final int maximumRank = Math.max(populationStats.findRank(first), populationStats.findRank(second));
        return minimumRate +
                (maximumRate - minimumRate) * (1.0 - (double) maximumRank / (populationStats.getPopulationSize() - 1));
    }

}
