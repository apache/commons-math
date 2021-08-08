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
package org.apache.commons.math4.ga.convergencecond;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math4.ga.chromosome.AbstractChromosome;
import org.apache.commons.math4.ga.chromosome.Chromosome;
import org.apache.commons.math4.ga.convergence.StoppingCondition;
import org.apache.commons.math4.ga.convergence.UnchangedMeanFitness;
import org.apache.commons.math4.ga.internal.stats.PopulationStatisticalSummaryImpl;
import org.apache.commons.math4.ga.population.ListPopulation;
import org.apache.commons.math4.ga.population.Population;
import org.junit.Assert;
import org.junit.Test;

public class UnchangedMeanFitnessTest {

    @Test
    public void testIsSatisfied() {

        final int noOfGenerationsWithUnchangedMeanFitness = 5;
        StoppingCondition<String> stoppingCondition = new UnchangedMeanFitness<>(
                noOfGenerationsWithUnchangedMeanFitness);

        double[] fitnesses = new double[10];
        for (int i = 0; i < 10; i++) {
            fitnesses[i] = i;
        }
        List<Chromosome<String>> chromosomes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final double fitness = fitnesses[i];
            Chromosome<String> ch = new AbstractChromosome<String>(c -> fitness, c -> "Fixed") {
            };
            chromosomes.add(ch);
        }
        Population<String> pop = new ListPopulation<>(chromosomes, 10);

        double initialAverageFitness = new PopulationStatisticalSummaryImpl<>(pop).getMeanFitness();

        int counter = 0;
        while (!stoppingCondition.isSatisfied(pop)) {
            counter++;
        }

        double averageFitnessAfterConvergence = new PopulationStatisticalSummaryImpl<>(pop).getMeanFitness();

        Assert.assertEquals(initialAverageFitness, averageFitnessAfterConvergence, .001);
        Assert.assertEquals(noOfGenerationsWithUnchangedMeanFitness, counter);
    }

}
