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
package org.apache.commons.math4.userguide.genetics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.math4.exception.util.LocalizedFormats;
import org.apache.commons.math4.genetics.AbstractListChromosome;
import org.apache.commons.math4.genetics.Chromosome;
import org.apache.commons.math4.genetics.ElitisticListPopulation;
import org.apache.commons.math4.genetics.GeneticAlgorithm;
import org.apache.commons.math4.genetics.InvalidRepresentationException;
import org.apache.commons.math4.genetics.MutationPolicy;
import org.apache.commons.math4.genetics.OnePointCrossover;
import org.apache.commons.math4.genetics.Population;
import org.apache.commons.math4.genetics.StoppingCondition;
import org.apache.commons.math4.genetics.TournamentSelection;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.math4.util.Precision;

public class HelloWorldExample {
    public static final int    POPULATION_SIZE   = 1000;
    public static final double CROSSOVER_RATE    = 0.9;
    public static final double MUTATION_RATE     = 0.03;
    public static final double ELITISM_RATE      = 0.1;
    public static final int    TOURNAMENT_ARITY  = 2;

    public static final String TARGET_STRING = "Hello World!";
    public static final int DIMENSION = TARGET_STRING.length();

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        // initialize a new genetic algorithm
        GeneticAlgorithm ga = new GeneticAlgorithm(new OnePointCrossover<Character>(), CROSSOVER_RATE,
                                                   new RandomCharacterMutation(), MUTATION_RATE,
                                                   new TournamentSelection(TOURNAMENT_ARITY));

        // initial population
        Population initial = getInitialPopulation();

        // stopping condition
        StoppingCondition stoppingCondition = new StoppingCondition() {
            
            int generation = 0;
            
//            @Override
            public boolean isSatisfied(Population population) {
                Chromosome fittestChromosome = population.getFittestChromosome();
                
                if (generation == 1 || generation % 10 == 0) {
                    System.out.println("Generation " + generation + ": " + fittestChromosome.toString());
                }
                generation++;

                double fitness = fittestChromosome.fitness();
                if (Precision.equals(fitness, 0.0, 1e-6)) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        System.out.println("Starting evolution ...");
        
        // run the algorithm
        Population finalPopulation = ga.evolve(initial, stoppingCondition);

        // Get the end time for the simulation.
        long endTime = System.currentTimeMillis();

        // best chromosome from the final population
        Chromosome best = finalPopulation.getFittestChromosome();
        System.out.println("Generation " + ga.getGenerationsEvolved() + ": " + best.toString());
        System.out.println("Total execution time: " + (endTime - startTime) + "ms");
    }
    
    private static List<Character> randomRepresentation(int length) {
        return asList(RandomStringUtils.randomAscii(length));
    }

    private static List<Character> asList(String str) {
        return Arrays.asList(ArrayUtils.toObject(str.toCharArray()));    
    }
    
    private static Population getInitialPopulation() {
        List<Chromosome> popList = new LinkedList<Chromosome>();

        for (int i = 0; i < POPULATION_SIZE; i++) {
            popList.add(new StringChromosome(randomRepresentation(DIMENSION)));
        }
        return new ElitisticListPopulation(popList, 2 * popList.size(), ELITISM_RATE);
    }

    /**
     * String Chromosome represented by a list of characters.
     */
    public static class StringChromosome extends AbstractListChromosome<Character> {

        public StringChromosome(List<Character> repr) {
            super(repr);
        }

        public StringChromosome(String str) {
            this(asList(str));
        }

        public double fitness() {
            String target = TARGET_STRING;
            int f = 0; // start at 0; the best fitness
            List<Character> chromosome = getRepresentation();
            for (int i = 0, c = target.length(); i < c; i++) {
                // subtract the ascii difference between the target character and the chromosome character.
                // Thus 'c' is fitter than 'd' when compared to 'a'.
                f -= FastMath.abs(target.charAt(i) - chromosome.get(i).charValue());
            }
            return f;
        }

        @Override
        protected void checkValidity(List<Character> repr) throws InvalidRepresentationException {
            for (char c : repr) {
                if (c < 32 || c > 126) {
                    throw new InvalidRepresentationException(LocalizedFormats.INVALID_FIXED_LENGTH_CHROMOSOME);
                }
            }
        }

        public List<Character> getStringRepresentation() {
            return getRepresentation();
        }

        @Override
        public StringChromosome newFixedLengthChromosome(List<Character> repr) {
            return new StringChromosome(repr);
        }

        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            for (Character i : getRepresentation()) {
                sb.append(i.charValue());
            }
            return String.format("(f=%s '%s')", getFitness(), sb.toString());
        }

    }

    private static class RandomCharacterMutation implements MutationPolicy {
        public Chromosome mutate(Chromosome original) {
            if (!(original instanceof StringChromosome)) {
                throw new IllegalArgumentException();
            }

            StringChromosome strChromosome = (StringChromosome) original;
            List<Character> characters = strChromosome.getStringRepresentation();
            
            int mutationIndex = GeneticAlgorithm.getRandomGenerator().nextInt(characters.size());

            List<Character> mutatedChromosome = new ArrayList<Character>(characters);
            char newValue = (char) (32 + GeneticAlgorithm.getRandomGenerator().nextInt(127 - 32));
            mutatedChromosome.set(mutationIndex, newValue);

            return strChromosome.newFixedLengthChromosome(mutatedChromosome);
        }
    }
}
