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
package org.apache.commons.math4.ga2.stop;

import java.util.Map;
import java.util.function.ToDoubleFunction;
import java.util.function.BiPredicate;
import org.apache.commons.math4.ga2.Population;

/**
 * Criterion for asserting convergence of a population.
 * Notes:
 * <ul>
 *  <li>Class is <em>not</em> thread-safe.</li>
 *  <li>A <em>new</em> instance must created for each GA run (otherwise
 *   an {@link IllegalStateException} will be thrown).</li>
 * </ul>
 *
 * @param <G> Genotype.
 * @param <P> Phenotype.
 */
public class UnchangedFitness<G, P> implements BiPredicate<Population<G, P>, Integer> {
    /** Function that computes the reference value. */
    private final ToDoubleFunction<Population<G, P>> calculator;
    /** Number of generations during which no change has happened. */
    private final int maxGenerations;
    /** Value for previous population. */
    private double previousFitness = Double.NaN;
    /** Generation at which the last change has occurred. */
    private int updatedGeneration = 0;

    /** What needs to be unchanged. */
    public enum Type {
        /** Best fitness. */
        BEST,
        /** Mean fitness. */
        MEAN;
    }

    /**
     * @param criterion Reference value that when unchanged for the given
     * number of {@code generations}, signals convergence.
     * @param maxGenerations Number of generations during which the reference
     * value must have been the same.
     */
    public UnchangedFitness(Type criterion,
                            int maxGenerations) {
        switch (criterion) {
        case BEST:
            calculator = p -> bestFitness(p);
            break;
        case MEAN:
            calculator = p -> meanFitness(p);
            break;
        default:
            calculator = null;
            throw new IllegalStateException(); // Should never happen.
        }

        this.maxGenerations = maxGenerations;
    }

    /** {@inheritDoc} */
    @Override
    public boolean test(Population<G, P> population,
                        Integer generationCounter) {
        final int genDiff = generationCounter - updatedGeneration;
        if (genDiff < 0) {
            throw new IllegalStateException("Incorrect usage");
        }

        final double fitness = calculator.applyAsDouble(population);
        if (fitness == previousFitness) {
            if (genDiff > maxGenerations) {
                return true;
            }
        } else {
            updatedGeneration = generationCounter;
            previousFitness = fitness;
        }

        return false;
    }

    /**
     * @param population Population.
     * @return the best fitness.
     *
     * @param <G> Genotype.
     * @param <P> Phenotype.
     */
    private static <G, P> double bestFitness(Population<G, P> population) {
        return population.contents(true).get(0).getValue();
    }

    /**
     * @param population Population.
     * @return the mean fitness.
     *
     * @param <G> Genotype.
     * @param <P> Phenotype.
     */
    private static <G, P> double meanFitness(Population<G, P> population) {
        double mean = 0;
        int count = 0;

        for (Map.Entry<G, Double> e : population.contents(false)) {
            mean += e.getValue();
        }

        return mean / count;
    }
}
