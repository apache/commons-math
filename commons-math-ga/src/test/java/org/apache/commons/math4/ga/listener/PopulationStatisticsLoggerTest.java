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
package org.apache.commons.math4.ga.listener;

import org.apache.commons.math4.ga.chromosome.IntegralValuedChromosome;
import org.apache.commons.math4.ga.dummy.DummyListChromosomeDecoder;
import org.apache.commons.math4.ga.population.ListPopulation;
import org.apache.commons.math4.ga.population.Population;
import org.apache.commons.math4.ga.utils.ChromosomeRepresentationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PopulationStatisticsLoggerTest {

    @Test
    public void testPopulationStatisticsLogger() {
        Population<String> population = new ListPopulation<String>(2);
        population.addChromosome(
                new IntegralValuedChromosome<>(ChromosomeRepresentationUtils
                        .randomIntegralRepresentation(10, 0, 10), c -> 0, new DummyListChromosomeDecoder<>("0"),
                        0, 10));
        population.addChromosome(
                new IntegralValuedChromosome<>(ChromosomeRepresentationUtils
                        .randomIntegralRepresentation(10, 0, 10), c -> 0, new DummyListChromosomeDecoder<>("0"),
                        0, 10));
        PopulationStatisticsLogger<String> logger = new PopulationStatisticsLogger<>();
        logger.notify(1, population);
        Assertions.assertTrue(true);
    }

}
