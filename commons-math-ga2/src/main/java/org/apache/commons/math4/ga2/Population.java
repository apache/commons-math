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
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.function.Function;
import org.apache.commons.rng.UniformRandomProvider;

/**
 * Collection of chromosomes and associated fitness.
 *
 * <p>
 * Notes:
 * <ul>
 *  <li>
 *   Class is <em>not</em> thread-safe.
 *  </li>
 *  <li>
 *   Class assumes that each chromosome instance (of type {@code <G>})
 *   identifies a unique individual, irrespective of whether other
 *   individuals share the same sequence of genes; hence type {@code <G>}
 *   must <em>not</em> override method {@link Object#equals(Object)
 *   equals(Object o)}.
 *   In other words, if an overridden {@code equals} compares equality of
 *   gene sequences, the current implementation of this class will only
 *   contain individuals whose gene sequence is unique.
 *  </li>
 * </ul>
 *
 * @param <G> Genotype.
 * @param <P> Phenotype.
 */
public class Population<G, P> {
    /** Population data (fitness). */
    private final Map<G, Double> chromo2fit = new HashMap<>();
    /** Population data (rank). */
    private final Map<G, Integer> chromo2rank = new HashMap<>();
    /** Decoder. */
    private final Function<G, P> decoder;
    /** Fitness function. */
    private final FitnessService<G, P> fitnessCalculator;
    /** Maximum population size. */
    private final int maxSize;

    /**
     * @param max Maximum allowed number of chromosomes.
     * @param geno2pheno Genotype to phenotype converter.
     * @param function Fitness calculator.
     */
    public Population(int max,
                      Function<G, P> geno2pheno,
                      FitnessService<G, P> function) {
        maxSize = max;
        decoder = geno2pheno;
        fitnessCalculator = function;
    }

    /** @return the number of free slots in the population. */
    public int allowedInsertions() {
        return Math.max(0, maxSize - chromo2fit.size());
    }

    /** @return the number of individuals in the population. */
    public int size() {
        return chromo2fit.size();
    }

    /**
     * Insert chromosomes into the population.
     *
     * Fitness and rank are calculated.
     * If the fitness is {@code NaN}, the corresponding chromosome is
     * <em>not</em> added to the population.
     * <p>
     * Note: All the {@code chromosomes} are passed in a single call to the
     * {@link FitnessService#apply(Function,Collection) fitness calculator}.
     *
     * @param chromosomes Chromosomes.
     */
    public void add(Collection<G> chromosomes) {
        if (chromosomes.size() > allowedInsertions()) {
            throw new IllegalArgumentException("Too many chromosomes");
        }

        final double[] fitness = fitnessCalculator.apply(decoder, chromosomes);
        int c = 0;
        boolean atLeastOneInsertion = false;
        for (G chromosome : chromosomes) {
            final double value = fitness[c++];
            if (!Double.isNaN(value)) {
                // Insert.
                chromo2fit.put(chromosome, value);
                atLeastOneInsertion = true;
            }
        }

        if (atLeastOneInsertion) { // Recompute ranks.
            final List<Map.Entry<G, Double>> list = contents(true);
            for (int i = 0; i < list.size(); i++) {
                chromo2rank.put(list.get(i).getKey(), i);
            }
        }
    }

    /**
     * Retrieves the rank.
     * Ranks are attributed in the range [0, N - 1] (where N is the
     * number of individuals in the population) in inverse order of
     * fitness (i.e. the chromosome with highest fitness has rank 0).
     *
     * @param chromosome Chromosome.
     * @return the rank of the given {@code chromosome}.
     * @throws IllegalArgumentException if the {@code chromosome} does
     * not belong to this population.
     */
    public int rank(G chromosome) {
        final Integer r = chromo2rank.get(chromosome);

        if (r == null) {
            throw new IllegalArgumentException("Chromosome not found");
        }

        return r;
    }

    /**
     * @param sorted If {@code true}, the contents will be sorted in decreasing
     * order of fitness.
     * @return a copy of the population contents.
     */
    public List<Map.Entry<G, Double>> contents(boolean sorted) {
        final List<Map.Entry<G, Double>> out = new ArrayList<>(chromo2fit.size());

        for (Map.Entry<G, Double> e : chromo2fit.entrySet()) {
            out.add(new AbstractMap.SimpleImmutableEntry<>(e));
        }

        if (sorted) {
            sort(out);
        }

        return out;
    }

    /**
     * @param numOffsprings Number of offsprings to generate.
     * @param selection Parents selector.
     * @param operators Generators to be applied on the parents.
     * @param rng RNG.
     * @return the offsprings.
     */
    public Collection<G> offsprings(int numOffsprings,
                                    Selection<G, P> selection,
                                    Map<GeneticOperator<G>, ApplicationRate> operators,
                                    UniformRandomProvider rng) {
        final List<G> candidates = new ArrayList<>(numOffsprings);
        OFFSPRING: while (true) {
            for (Map.Entry<GeneticOperator<G>, ApplicationRate> e : operators.entrySet()) {
                final GeneticOperator<G> op = e.getKey();
                final List<G> parents = selection.apply(op.numberOfParents(), this);
                final double prob = e.getValue().compute(this, parents);
                final List<G> offsprings = rng.nextDouble() < prob ?
                    op.apply(parents, rng) :
                    parents;

                for (int i = 0; i < offsprings.size(); i++) {
                    if (candidates.size() >= numOffsprings) {
                        break OFFSPRING;
                    }
                    candidates.add(offsprings.get(i));
                }
            }
        }

        return Collections.unmodifiableList(candidates);
    }

    /**
     * Sort (in-place) in decreasing order of fitness.
     *
     * @param contents List of individuals.
     *
     * @param <G> Genotype.
     */
    public static <G> void sort(List<Map.Entry<G, Double>> contents) {
        Collections.sort(contents,
                         Map.Entry.<G, Double>comparingByValue().reversed());
    }
}
