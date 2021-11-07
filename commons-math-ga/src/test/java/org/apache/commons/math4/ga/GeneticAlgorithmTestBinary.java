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
package org.apache.commons.math4.ga;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math4.ga.chromosome.BinaryChromosome;
import org.apache.commons.math4.ga.chromosome.Chromosome;
import org.apache.commons.math4.ga.convergence.FixedGenerationCount;
import org.apache.commons.math4.ga.convergence.StoppingCondition;
import org.apache.commons.math4.ga.crossover.OnePointCrossover;
import org.apache.commons.math4.ga.decoder.TransparentListChromosomeDecoder;
import org.apache.commons.math4.ga.internal.exception.GeneticException;
import org.apache.commons.math4.ga.listener.ConvergenceListener;
import org.apache.commons.math4.ga.listener.ConvergenceListenerRegistry;
import org.apache.commons.math4.ga.mutation.BinaryMutation;
import org.apache.commons.math4.ga.population.ListPopulation;
import org.apache.commons.math4.ga.population.Population;
import org.apache.commons.math4.ga.selection.TournamentSelection;
import org.apache.commons.math4.ga.utils.ChromosomeRepresentationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * This is also an example of usage.
 */
public class GeneticAlgorithmTestBinary {

    // parameters for the GA
    private static final int DIMENSION = 50;
    private static final int POPULATION_SIZE = 50;
    private static final int NUM_GENERATIONS = 50;
    private static final double CROSSOVER_RATE = 1;
    private static final double MUTATION_RATE = 0.1;
    private static final int TOURNAMENT_ARITY = 2;

    @Test
    public void test() {
        removeListeners();

        // initialize a new genetic algorithm
        GeneticAlgorithm<List<Integer>> ga = new GeneticAlgorithm<>(new OnePointCrossover<Integer, List<Integer>>(),
                CROSSOVER_RATE, new BinaryMutation<List<Integer>>(), MUTATION_RATE,
                new TournamentSelection<List<Integer>>(TOURNAMENT_ARITY));

        Assertions.assertEquals(0, ga.getGenerationsEvolved());

        // initial population
        Population<List<Integer>> initial = randomPopulation();
        // stopping conditions
        StoppingCondition<List<Integer>> stopCond = new FixedGenerationCount<>(NUM_GENERATIONS);

        // best initial chromosome
        Chromosome<List<Integer>> bestInitial = initial.getFittestChromosome();

        // run the algorithm
        Population<List<Integer>> finalPopulation = ga.evolve(initial, stopCond);

        // best chromosome from the final population
        Chromosome<List<Integer>> bestFinal = finalPopulation.getFittestChromosome();

        // the only thing we can test is whether the final solution is not worse than
        // the initial one
        // however, for some implementations of GA, this need not be true :)

        Assertions.assertTrue(bestFinal.compareTo(bestInitial) > 0);
        Assertions.assertEquals(NUM_GENERATIONS, ga.getGenerationsEvolved());

    }

    private void removeListeners() {
        try {
            ConvergenceListenerRegistry<String> registry = ConvergenceListenerRegistry.<String>getInstance();
            Field listenersField = registry.getClass().getDeclaredField("listeners");
            boolean accessible = listenersField.isAccessible();
            if (!accessible) {
                listenersField.setAccessible(true);
            }
            @SuppressWarnings("unchecked")
            List<ConvergenceListener<String>> listeners = (List<ConvergenceListener<String>>) listenersField
                    .get(ConvergenceListenerRegistry.getInstance());
            listeners.clear();
            listenersField.setAccessible(accessible);
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes a random population.
     */
    private static ListPopulation<List<Integer>> randomPopulation() {
        List<Chromosome<List<Integer>>> popList = new LinkedList<>();

        for (int i = 0; i < POPULATION_SIZE; i++) {
            BinaryChromosome<List<Integer>> randChrom = new FindOnes(
                    ChromosomeRepresentationUtils.randomBinaryRepresentation(DIMENSION));
            popList.add(randChrom);
        }
        return new ListPopulation<>(popList, popList.size());
    }

    /**
     * Chromosomes represented by a binary chromosome.
     *
     * The goal is to set all bits (genes) to 1.
     */
    private static class FindOnes extends BinaryChromosome<List<Integer>> {

        FindOnes(List<Integer> representation) {
            super(representation, phenotype -> {
                Integer val = 0;
                for (Integer num : phenotype) {
                    val += num;
                }
                return val;
            }, new TransparentListChromosomeDecoder<>());
        }
    }

    @Test
    public void testCrossoverRate() {
        Assertions.assertThrows(GeneticException.class, () -> {
            new GeneticAlgorithm<>(new OnePointCrossover<>(), 1.5, new BinaryMutation<>(), .01,
                    new TournamentSelection<>(10));
        });
    }

    @Test
    public void testMutationRate() {
        Assertions.assertThrows(GeneticException.class, () -> {
            new GeneticAlgorithm<>(new OnePointCrossover<>(), .5, new BinaryMutation<>(), 1.5,
                    new TournamentSelection<>(10));
        });
    }

}
