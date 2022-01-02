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
 * This class represents a constant crossover rate generator.
 * @param <P> phenotype of chromosome
 */
public class ConstantCrossoverRateGenerator<P> implements CrossoverRateGenerator<P> {

    /** the fixed value of crossover rate. **/
    private final double crossoverRate;

    /**
     * @param crossoverRate crossover rate
     */
    public ConstantCrossoverRateGenerator(double crossoverRate) {
        this.crossoverRate = crossoverRate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double generate(Chromosome<P> first,
            Chromosome<P> second,
            PopulationStatisticalSummary<P> populationStats,
            int generation) {
        return crossoverRate;
    }

}
