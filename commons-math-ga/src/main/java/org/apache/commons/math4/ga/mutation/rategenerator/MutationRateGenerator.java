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
 * This abstraction represents mutation rate generator.
 * @param <P> phenotype of chromosome
 */
public interface MutationRateGenerator<P> {

    /**
     * Generates mutation rate based on input params.
     * @param chromosome      chromosome
     * @param populationStats population statictics summary
     * @param generation      generation count
     * @return mutation rate
     */
    double generate(Chromosome<P> chromosome, PopulationStatisticalSummary<P> populationStats, int generation);

}
