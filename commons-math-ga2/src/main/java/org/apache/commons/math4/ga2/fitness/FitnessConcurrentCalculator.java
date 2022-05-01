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

package org.apache.commons.math4.ga2.fitness;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.math4.ga2.FitnessService;

/**
 * Perform parallel computation of fitnesses.
 *
 * @param <G> Genotype.
 * @param <P> Phenotype.
 */
public class FitnessConcurrentCalculator<G, P> implements FitnessService<G, P> {
    /** Logger. */
    private static final Logger LOG = LoggerFactory.getLogger(FitnessConcurrentCalculator.class);
    /** Fitness function. */
    private final ToDoubleFunction<P> function;
    /** Executor. */
    private final ExecutorService executor;

    /**
     * @param fitness Fitness function.
     * Note: This class <em>must</em> be thrad-safe.
     * @param service Executor.
     */
    public FitnessConcurrentCalculator(ToDoubleFunction<P> fitness,
                                       ExecutorService service) {
        function = fitness;
        executor = service;
    }

    /**
     * {@inheritDoc}
     *
     * Note: If the fitness fails, or is interrupted, the corresponding slot
     * will contain {@link Double#NaN NaN}.
     */
    @Override
    public double[] apply(Function<G, P> decoder,
                          Collection<G> chromosomes) {
        final double[] fitnesses = new double[chromosomes.size()];
        int c = 0;
        for (Future<Double> f : compute(decoder, chromosomes)) {
            double value = Double.NaN; // Default.
            try {
                // Fitness computations were submitted to the executor:
                // Wait for all results.
                value = f.get();
            } catch (InterruptedException |
                     ExecutionException e) {
                LOG.error("Unexpected failure: {}", e.getMessage());
            }
            fitnesses[c++] = value;
        }
        return fitnesses;
    }

    /**
     * Compute fitness of every chromosome, using all available threads.
     *
     * @param decoder Genotype to phenotype converter.
     * @param chromosomes Chromosomes.
     * @return the fitness values, in the same order as the input.
     */
    private List<Future<Double>> compute(Function<G, P> decoder,
                                         Collection<G> chromosomes) {
        final List<Future<Double>> futures = new ArrayList<>();
        for (G chromosome : chromosomes) {
            futures.add(executor.submit(() -> function.applyAsDouble(decoder.apply(chromosome))));
        }
        return futures;
    }
}
