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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math4.ga.chromosome.Chromosome;
import org.apache.commons.math4.ga.chromosome.RealValuedChromosome;
import org.apache.commons.math4.ga.convergence.FixedGenerationCount;
import org.apache.commons.math4.ga.convergence.StoppingCondition;
import org.apache.commons.math4.ga.crossover.OnePointCrossover;
import org.apache.commons.math4.ga.decoder.RandomKeyDecoder;
import org.apache.commons.math4.ga.fitness.FitnessFunction;
import org.apache.commons.math4.ga.mutation.RealValuedMutation;
import org.apache.commons.math4.ga.population.ListPopulation;
import org.apache.commons.math4.ga.population.Population;
import org.apache.commons.math4.ga.selection.TournamentSelection;
import org.apache.commons.math4.ga.utils.ChromosomeRepresentationUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * This is also an example of usage.
 *
 * This algorithm does "stochastic sorting" of a sequence 0,...,N.
 *
 */
public class GeneticAlgorithmTestPermutations {

    // parameters for the GA
    private static final int DIMENSION = 20;
    private static final int POPULATION_SIZE = 80;
    private static final int NUM_GENERATIONS = 200;
    private static final double ELITISM_RATE = 0.2;
    private static final double CROSSOVER_RATE = 1;
    private static final double MUTATION_RATE = 0.08;
    private static final int TOURNAMENT_ARITY = 2;

    // numbers from 0 to N-1
    private static final List<Integer> sequence = new ArrayList<>();
    static {
        for (int i = 0; i < DIMENSION; i++) {
            sequence.add(i);
        }
    }

    @Test
    public void test() {
        // to test a stochastic algorithm is hard, so this will rather be an usage
        // example

        // initialize a new genetic algorithm
        GeneticAlgorithm<List<Integer>> ga = new GeneticAlgorithm<>(new OnePointCrossover<Integer, List<Integer>>(),
                CROSSOVER_RATE, new RealValuedMutation<List<Integer>>(), MUTATION_RATE,
                new TournamentSelection<List<Integer>>(TOURNAMENT_ARITY), ELITISM_RATE);

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

        Assert.assertTrue(bestFinal.compareTo(bestInitial) > 0);

    }

    /**
     * Initializes a random population
     */
    private static Population<List<Integer>> randomPopulation() {
        List<Chromosome<List<Integer>>> popList = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            Chromosome<List<Integer>> randChrom = new MinPermutations(
                    ChromosomeRepresentationUtils.randomPermutation(DIMENSION));
            popList.add(randChrom);
        }
        return new ListPopulation<List<Integer>>(popList, popList.size());
    }

    /**
     * Chromosomes representing a permutation of (0,1,2,...,DIMENSION-1).
     *
     * The goal is to sort the sequence.
     */
    private static class MinPermutations extends RealValuedChromosome<List<Integer>> {

        MinPermutations(List<Double> representation) {
            super(representation, new MinPermutationsFitnessFunction(), new RandomKeyDecoder<>(sequence));
        }

        @Override
        public RealValuedChromosome<List<Integer>> newChromosome(List<Double> chromosomeRepresentation) {
            return new MinPermutations(chromosomeRepresentation);
        }

    }

    private static class MinPermutationsFitnessFunction implements FitnessFunction<List<Integer>> {

        @Override
        public double compute(List<Integer> decodedChromosome) {
            double res = 0.0;
            for (int i = 0; i < decodedChromosome.size(); i++) {
                int value = decodedChromosome.get(i);
                if (value != i) {
                    // bad position found
                    res += Math.abs(value - i);
                }
            }
            // the most fitted chromosome is the one with minimal error
            // therefore we must return negative value
            return -res;
        }

    }

}
