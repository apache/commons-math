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

import org.apache.commons.math4.ga.chromosome.IntegralValuedChromosome;
import org.apache.commons.math4.ga.internal.stats.PopulationStatisticalSummaryImpl;
import org.apache.commons.math4.ga.population.ListPopulation;
import org.apache.commons.math4.ga.population.Population;
import org.apache.commons.math4.ga.stats.PopulationStatisticalSummary;
import org.apache.commons.math4.ga.utils.DummyListChromosomeDecoder;
import org.junit.Assert;
import org.junit.Test;

public class AdaptiveLinearMaximumRankBasedCrossoverRateGeneratorTest {

    @Test
    public void testGenerate() {
        final double minCrossoverRate = .5;
        final double maxCrossoverRate = 1.0;

        IntegralValuedChromosome<String> chromosome1 = IntegralValuedChromosome.randomChromosome(10, c -> 1,
                new DummyListChromosomeDecoder<>("Fixed"), 0, 10);
        IntegralValuedChromosome<String> chromosome2 = IntegralValuedChromosome.randomChromosome(10, c -> 2,
                new DummyListChromosomeDecoder<>("Fixed"), 0, 10);
        IntegralValuedChromosome<String> chromosome3 = IntegralValuedChromosome.randomChromosome(10, c -> 3,
                new DummyListChromosomeDecoder<>("Fixed"), 0, 10);
        IntegralValuedChromosome<String> chromosome4 = IntegralValuedChromosome.randomChromosome(10, c -> 4,
                new DummyListChromosomeDecoder<>("Fixed"), 0, 10);
        IntegralValuedChromosome<String> chromosome5 = IntegralValuedChromosome.randomChromosome(10, c -> 5,
                new DummyListChromosomeDecoder<>("Fixed"), 0, 10);

        Population<String> population = new ListPopulation<>(5);
        population.addChromosome(chromosome1);
        population.addChromosome(chromosome2);
        population.addChromosome(chromosome3);
        population.addChromosome(chromosome4);
        population.addChromosome(chromosome5);
        PopulationStatisticalSummary<String> stats = new PopulationStatisticalSummaryImpl<>(population);

        CrossoverRateGenerator<String> crossoverRateGenerator = new AdaptiveLinearMaximumRankBasedCrossoverRateGenerator<>(
                minCrossoverRate, maxCrossoverRate);

        Assert.assertEquals(minCrossoverRate, crossoverRateGenerator.generate(chromosome1, chromosome5, stats, 1),
                .00000001);
        Assert.assertEquals(minCrossoverRate, crossoverRateGenerator.generate(chromosome5, chromosome2, stats, 1),
                .00000001);
        Assert.assertEquals(minCrossoverRate, crossoverRateGenerator.generate(chromosome3, chromosome5, stats, 1),
                .00000001);
        Assert.assertEquals(minCrossoverRate, crossoverRateGenerator.generate(chromosome4, chromosome5, stats, 1),
                .00000001);

    }

}
