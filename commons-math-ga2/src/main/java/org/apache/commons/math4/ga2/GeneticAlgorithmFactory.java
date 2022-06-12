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
package org.apache.commons.math4.ga2;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.BiPredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.rng.UniformRandomProvider;

/**
 * Genetic algorithm factory.
 *
 * @param <G> Genotype.
 * @param <P> Phenotype.
 */
public final class GeneticAlgorithmFactory<G, P> implements Callable<Population<G, P>> {
    /** Logger. */
    private static final Logger LOG = LoggerFactory.getLogger(GeneticAlgorithmFactory.class);
    /** Initial list of chromosomes. */
    private final Collection<G> init;
    /** Genotype to phenotype converter. */
    private final Function<G, P> decoder;
    /** Criterion for stopping the evolution. */
    private final BiPredicate<Population<G, P>, Integer> stop;
    /** Fitness calculator. */
    private final FitnessService<G, P> fitness;
    /** Chromosome selector. */
    private final Selection<G, P> selection;
    /** Offspring generators. */
    private final Map<GeneticOperator<G>, ApplicationRate> operators;
    /** Elitism. */
    private final double elitism;
    /** RNG. */
    private final RandomSource random;
    /** Callback. */
    private final GenerationCallback<G> callback;

    /**
     * @param init Initial list of chromosomes.
     * @param decoder Genotype to phenotype converter.
     * @param stop Criterion for stopping the evolution.
     * @param fitness Fitness calculator.
     * @param selection Chromosome selector.
     * @param operators Offspring generators.
     * @param elitism Fraction of the population that will be unconditionally
     * transferred to the next generation.
     * @param random Source of randomness.
     * @param callback Callback.
     */
    private GeneticAlgorithmFactory(Collection<G> init,
                                    Function<G, P> decoder,
                                    BiPredicate<Population<G, P>, Integer> stop,
                                    FitnessService<G, P> fitness,
                                    Selection<G, P> selection,
                                    Map<GeneticOperator<G>, ApplicationRate> operators,
                                    double elitism,
                                    RandomSource random,
                                    GenerationCallback<G> callback) {
        this.init = init;
        this.decoder = decoder;
        this.stop = stop;
        this.fitness = fitness;
        this.selection = selection;
        this.operators = Collections.unmodifiableMap(operators);
        this.elitism = elitism;
        this.random = random;
        this.callback = callback;
    }

    /**
     * @param init Initial list of chromosomes.
     * @param decoder Genotype to phenotype converter.
     * @param stop Criterion for stopping the evolution.
     * @param fitness Fitness calculator.
     * @param selection Chromosome selector.
     * @param operators Offspring generators.
     * @param elitism Fraction of the population that will be unconditionally
     * transferred to the next generation.
     * @param random Source of randomness.
     * @param callback Callback.
     * @return a new instance.
     *
     * @param <G> Genotype.
     * @param <P> Phenotype.
     */
    public static <G, P> Callable<Population<G, P>> create(Collection<G> init,
                                                           Function<G, P> decoder,
                                                           BiPredicate<Population<G, P>, Integer> stop,
                                                           FitnessService<G, P> fitness,
                                                           Selection<G, P> selection,
                                                           Map<GeneticOperator<G>, ApplicationRate> operators,
                                                           double elitism,
                                                           RandomSource random,
                                                           GenerationCallback<G> callback) {
        return new GeneticAlgorithmFactory<>(init,
                                             decoder,
                                             stop,
                                             fitness,
                                             selection,
                                             operators,
                                             elitism,
                                             random,
                                             callback);
    }

    /**
     * @param chromosomeSize Number of genes.
     * @param initFactory Factory for creating the initial chromosomes.
     * @param populationSize Number of chromosomes per generation.
     * @param decoder Genotype to phenotype converter.
     * @param stop Criterion for stopping the evolution.
     * @param fitness Fitness calculator.
     * @param selection Chromosome selector.
     * @param operators Offspring generators.
     * @param elitism Fraction of the population that will be unconditionally
     * transferred to the next generation.
     * @param random Source of randomness.
     * @param callback Callback.
     * @return a new instance.
     *
     * @param <G> Genotype.
     * @param <P> Phenotype.
     */
    public static <G, P> Callable<Population<G, P>> create(int chromosomeSize,
                                                           ChromosomeFactory<G> initFactory,
                                                           int populationSize,
                                                           Function<G, P> decoder,
                                                           BiPredicate<Population<G, P>, Integer> stop,
                                                           FitnessService<G, P> fitness,
                                                           Selection<G, P> selection,
                                                           Map<GeneticOperator<G>, ApplicationRate> operators,
                                                           double elitism,
                                                           RandomSource random,
                                                           GenerationCallback<G> callback) {
        // Create initial population.
        final List<G> init = new ArrayList<>(populationSize);
        for (int i = 0; i < populationSize; i++) {
            init.add(initFactory.with(chromosomeSize));
        }

        return create(init,
                      decoder,
                      stop,
                      fitness,
                      selection,
                      operators,
                      elitism,
                      random,
                      callback);
    }

    /** {@inheritDoc} */
    @Override
    public Population<G, P> call() {
        int generation = 0;
        final int popSize = init.size();

        Population<G, P> currentGen = new Population<>(popSize, decoder, fitness);
        currentGen.add(init);
        final UniformRandomProvider rng = random.create();

        while (!stop.test(currentGen, ++generation)) {
            final Population<G, P> nextGen = new Population<>(popSize, decoder, fitness);

            applyElitism(currentGen, nextGen);

            // Loop for allowing that some fitnesses are "NaN" (thus
            // avoiding that the new generation is not fuly populated).
            while (true) {
                final int numCandidates = nextGen.allowedInsertions();
                if (numCandidates == 0) {
                    break;
                }

                // Generate and compute fitness.
                nextGen.add(currentGen.offsprings(numCandidates,
                                                  selection,
                                                  operators,
                                                  rng));

                if (nextGen.allowedInsertions() == numCandidates) {
                    LOG.error("Zero insertion at generation {}",
                              generation);
                }
            }

            if (callback != null) {
                // Notify caller.
                callback.update(nextGen.contents(true), generation);
            }

            // Replace with new generation.
            currentGen = nextGen;
        }

        return currentGen;
    }

    /**
     * @param from Population from which to retrieve the best individuals.
     * @param to Population to which the individuals must be added.
     */
    private void applyElitism(Population<G, P> from,
                              Population<G, P> to) {
        final List<Map.Entry<G, Double>> contents = from.contents(true);

        // Number of elite individuals to be transferred.
        final int elite = (int) (contents.size() * elitism);

        final List<G> eliteList = new ArrayList<>(elite);
        for (int i = 0; i < elite; i++) {
            eliteList.add(contents.get(i).getKey());
        }

        to.add(eliteList);
    }
}
