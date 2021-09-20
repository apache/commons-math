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

package org.apache.commons.math4.examples.genetics.tsp.legacy;

import java.util.List;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.CrossoverPolicy;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.MutationPolicy;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.SelectionPolicy;
import org.apache.commons.math3.genetics.StoppingCondition;
import org.apache.commons.math4.examples.genetics.tsp.Node;
import org.apache.commons.math4.examples.genetics.tsp.TSPFitnessFunction;
import org.apache.commons.math4.genetics.ListPopulation;
import org.apache.commons.math4.genetics.RealValuedChromosome;
import org.apache.commons.math4.genetics.decoder.RandomKeyDecoder;
import org.apache.commons.math4.genetics.listener.ConvergenceListenerRegistry;

public class LegacyGeneticAlgorithm extends GeneticAlgorithm {

    private int generationsEvolved;

    public LegacyGeneticAlgorithm(CrossoverPolicy crossoverPolicy, double crossoverRate, MutationPolicy mutationPolicy,
            double mutationRate, SelectionPolicy selectionPolicy) throws OutOfRangeException {
        super(crossoverPolicy, crossoverRate, mutationPolicy, mutationRate, selectionPolicy);
    }

    @Override
    public Population evolve(Population initial, StoppingCondition condition) {
        Population current = initial;
        generationsEvolved = 0;
        while (!condition.isSatisfied(current)) {
            ConvergenceListenerRegistry.<List<Node>>getInstance().notifyAll(generationsEvolved, transform(current));
            current = nextGeneration(current);
            generationsEvolved++;
        }
        return current;
    }

    @Override
    public int getGenerationsEvolved() {
        // TODO Auto-generated method stub
        return super.getGenerationsEvolved();
    }

    private org.apache.commons.math4.genetics.Population<List<Node>> transform(Population population) {
        org.apache.commons.math4.genetics.Population<List<Node>> newPopulation = new ListPopulation<List<Node>>(
                population.getPopulationLimit());
        for (Chromosome chromosome : population) {
            TSPChromosome tspChromosomeLegacy = (TSPChromosome) chromosome;
            RealValuedChromosome<List<Node>> tspChromosome = new RealValuedChromosome<>(
                    tspChromosomeLegacy.getRepresentation(), new TSPFitnessFunction(),
                    new RandomKeyDecoder<Node>(tspChromosomeLegacy.getNodes()));
            newPopulation.addChromosome(tspChromosome);
        }
        return newPopulation;
    }

}