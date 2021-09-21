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

package org.apache.commons.math4.genetics.crossover.rategenerator;

import org.apache.commons.math4.genetics.Chromosome;
import org.apache.commons.math4.genetics.stats.PopulationStatisticalSummary;

/**
 * This abstraction represents crossover rate generator.
 * @param <P> phenotype of chromosome
 */
public interface CrossoverRateGenerator<P> {

    /**
     * Generates crossover rate.
     * @param first           The first parent chromosome participating in crossover
     * @param second          The second parent chromosome participating in
     *                        crossover
     * @param populationStats statistical properties of population
     * @param generation      number of generations evolved
     * @return crossover rate
     */
    double generate(Chromosome<P> first, Chromosome<P> second, PopulationStatisticalSummary<P> populationStats,
            int generation);

}
