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

import org.apache.commons.math4.ga.chromosome.IntegralValuedChromosome;
import org.apache.commons.math4.ga.dummy.DummyListChromosomeDecoder;
import org.apache.commons.math4.ga.internal.stats.PopulationStatisticalSummaryImpl;
import org.apache.commons.math4.ga.population.ListPopulation;
import org.apache.commons.math4.ga.population.Population;
import org.apache.commons.math4.ga.utils.RandomProviderManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConstantMutationRateGeneratorTest {

    @Test
    public void testGenerate() {
        for (int i = 0; i < 100; i++) {
            double mutationRate = RandomProviderManager.getRandomProvider().nextDouble();
            MutationRateGenerator<String> mutationRateGenerator = new ConstantMutationRateGenerator<>(mutationRate);
            IntegralValuedChromosome<String> chromosome = IntegralValuedChromosome.randomChromosome(10, c -> 0,
                    new DummyListChromosomeDecoder<>("Fixed"), 0, 2);
            Population<String> population = new ListPopulation<>(1);
            population.addChromosome(chromosome);
            Assertions.assertEquals(mutationRate,
                    mutationRateGenerator.generate(chromosome, new PopulationStatisticalSummaryImpl<>(population), i),
                    .0000001);
        }
    }

}