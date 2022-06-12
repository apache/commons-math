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

package org.apache.commons.math4.examples.ga.mathfunctions;

import java.util.BitSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.BiPredicate;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math4.ga2.gene.binary.Chromosome;
import org.apache.commons.math4.ga2.gene.binary.Operators;
import org.apache.commons.math4.ga2.Population;
import org.apache.commons.math4.ga2.GeneticOperator;
import org.apache.commons.math4.ga2.FitnessService;
import org.apache.commons.math4.ga2.Selection;
import org.apache.commons.math4.ga2.GeneticAlgorithmFactory;
import org.apache.commons.math4.ga2.ChromosomeFactory;
import org.apache.commons.math4.ga2.GenerationCallback;
import org.apache.commons.math4.ga2.ApplicationRate;
import org.apache.commons.math4.ga2.stop.UnchangedFitness;
import org.apache.commons.math4.ga2.select.Tournament;
import org.apache.commons.math4.ga2.fitness.FitnessConcurrentCalculator;
import org.apache.commons.math4.ga2.rate.RateGenerators;

/**
 * Optimizer example.
 */
public final class MathFunctionOptimizer2 {
    /** Chromosome length. */
    private static final int GENES_PER_DIMENSION = 12;
    /** Scaling factor. */
    private static final double SCALE = 0.01;

    /**
     * @param dim Dimension.
     * @param crossover Crossover rate.
     * @param mutation Mutation rate.
     * @param elitism Elitism rate.
     * @param tournament Tournament size.
     * @param numGeneration Number of generations unchanged best fitness.
     * @param popSize Population size.
     * @param jobs Number of threads for computing the fitnesses.
     */
    public void optimize(int dim,
                         double crossover,
                         double mutation,
                         double elitism,
                         int tournament,
                         int numGeneration,
                         int popSize,
                         int jobs) {
        final int numGenes = dim * GENES_PER_DIMENSION;

        // Random genotypes generator.
        final ChromosomeFactory<Chromosome> initFactory =
            new Chromosome.RandomFactory(RandomSource.ISAAC);

        // Genotype to phenotype decoder.
        final Function<Chromosome, Coordinates> decoder = gene -> {
            final BitSet rep = gene.asBitSet();
            final double[] coord = new double[dim];
            for (int i = 0; i < dim; i++) {
                final int start = i * GENES_PER_DIMENSION;
                final long[] coordRep = rep.get(start, start + GENES_PER_DIMENSION).toLongArray();
                if (coordRep.length == 0) {
                    coord[i] = 0;
                } else if (coordRep.length == 1) {
                    coord[i] = coordRep[0] * SCALE;
                } else {
                    // Should not happen.
                    throw new IllegalStateException("Unsupported representation size: " +
                                                    coordRep.length);
                }
            }
            return new Coordinates(coord);
        };

        // Stopping condition (not thread-safe).
        final BiPredicate<Population<Chromosome, Coordinates>, Integer> stop =
            new UnchangedFitness(UnchangedFitness.Type.BEST,
                                 numGeneration);

        final ToDoubleFunction<Coordinates> fitnessFunction = coord -> {
            double s = 0;
            for (double v : coord.getValues()) {
                final double vM10 = v - 10;
                s += vM10 * vM10;
            }
            final double a = Math.sin(50 * Math.pow(s, 0.1));
            final double b = -Math.pow(s, 0.25) * (a * a + 1);
            return b;
        };

        // Setup for parallel computation of fitnesses.
        final ExecutorService exec = Executors.newFixedThreadPool(jobs);
        final FitnessService<Chromosome, Coordinates> fitness =
            new FitnessConcurrentCalculator(fitnessFunction,
                                            exec);

        // Parents selection scheme.
        final Selection<Chromosome, Coordinates> selection =
            new Tournament(tournament,
                           RandomSource.SPLIT_MIX_64);

        // Offspring generators.
        final Map<GeneticOperator<Chromosome>, ApplicationRate> operators = new HashMap<>();
        operators.put(Operators.mutation(mutation), RateGenerators.constant(1));
        operators.put(Operators.nPointCrossover(1), RateGenerators.constant(crossover));

        final Callable<Population<Chromosome, Coordinates>> ga =
            GeneticAlgorithmFactory.<Chromosome, Coordinates>create(numGenes,
                                                                    initFactory,
                                                                    popSize,
                                                                    decoder,
                                                                    stop,
                                                                    fitness,
                                                                    selection,
                                                                    operators,
                                                                    elitism,
                                                                    RandomSource.KISS,
                                                                    new GenerationLogger());

        try {
            // Run the GA and retrieve the best individual from the last generation.
            final Map.Entry<Chromosome, Double> best = ga.call().contents(true).get(0);

            // CHECKSTYLE: stop all
            System.out.println("fitness=" + best.getValue() +
                               " for " + decoder.apply(best.getKey()).toString());
            // CHECKSTYLE: resume all
        } catch (Exception e) {
            // Rethrow.
            throw new RuntimeException(e);
        } finally {
            exec.shutdown();
        }
    }
}

/**
 * Log evolution.
 */
class GenerationLogger implements GenerationCallback<Chromosome> {
    /** Logger. */
    private static final Logger LOG = LoggerFactory.getLogger(GenerationLogger.class);

    /** {@inheritDoc} */
    @Override
    public void update(List<Map.Entry<Chromosome, Double>> contents,
                       int generation) {
        final SummaryStatistics stats = new SummaryStatistics();
        for (Map.Entry<Chromosome, Double> e : contents) {
            stats.addValue(e.getValue());
        }

        LOG.info("fitness at generation {}: min={} max={} mean={} stddev={}",
                 generation,
                 stats.getMin(),
                 stats.getMax(),
                 stats.getMean(),
                 stats.getStandardDeviation());
    }
}
