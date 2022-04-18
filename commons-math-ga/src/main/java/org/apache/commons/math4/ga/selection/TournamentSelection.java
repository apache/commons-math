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
package org.apache.commons.math4.ga.selection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math4.ga.chromosome.Chromosome;
import org.apache.commons.math4.ga.chromosome.ChromosomePair;
import org.apache.commons.math4.ga.internal.exception.GeneticIllegalArgumentException;
import org.apache.commons.math4.ga.population.ListPopulation;
import org.apache.commons.math4.ga.population.Population;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.rng.simple.ThreadLocalRandomSource;

/**
 * Tournament selection scheme. Each of the two selected chromosomes is selected
 * based on n-ary tournament -- this is done by drawing {@link #arity} random
 * chromosomes without replacement from the population, and then selecting the
 * fittest chromosome among them.
 * @param <P> phenotype of chromosome
 * @since 2.0
 */
public class TournamentSelection<P> implements SelectionPolicy<P> {

    /** number of chromosomes included in the tournament selections. */
    private final int arity;

    /** The random source for random number generation. **/
    private final RandomSource randomSource;

    /**
     * Constructs a new TournamentSelection instance with given arity and default
     * random source.
     *
     * @param arity how many chromosomes will be drawn to the tournament
     */
    public TournamentSelection(final int arity) {
        this.arity = arity;
        this.randomSource = RandomSource.XO_RO_SHI_RO_128_PP;
    }

    /**
     * Constructs a new TournamentSelection instance with given arity and random
     * source.
     *
     * @param arity        how many chromosomes will be drawn to the tournament
     * @param randomSource random source to instantiate UniformRandomProvider.
     */
    public TournamentSelection(final int arity, final RandomSource randomSource) {
        this.arity = arity;
        this.randomSource = randomSource;
    }

    /**
     * Select two chromosomes from the population. Each of the two selected
     * chromosomes is selected based on n-ary tournament -- this is done by drawing
     * {@link #arity} random chromosomes without replacement from the population,
     * and then selecting the fittest chromosome among them.
     *
     * @param population the population from which the chromosomes are chosen.
     * @return the selected chromosomes.
     */
    @Override
    public ChromosomePair<P> select(final Population<P> population) {
        if (!(population instanceof ListPopulation<?>)) {
            throw new GeneticIllegalArgumentException(GeneticIllegalArgumentException.ILLEGAL_ARGUMENT, population);
        }
        return new ChromosomePair<>(tournament((ListPopulation<P>) population),
                tournament((ListPopulation<P>) population));
    }

    /**
     * Helper for {@link #select(Population)}. Draw {@link #arity} random
     * chromosomes without replacement from the population, and then select the
     * fittest chromosome among them.
     *
     * @param population the population from which the chromosomes are chosen.
     * @return the selected chromosome.
     */
    private Chromosome<P> tournament(final ListPopulation<P> population) {
        if (population.getPopulationSize() < this.arity) {
            throw new GeneticIllegalArgumentException(GeneticIllegalArgumentException.TOO_LARGE, arity,
                    population.getPopulationSize());
        }

        // create a copy of the chromosome list
        final List<Chromosome<P>> chromosomes = new ArrayList<>(population.getChromosomes());
        final List<Chromosome<P>> selectedChromosomes = new ArrayList<>();

        for (int i = 0; i < this.arity; i++) {
            // select a random individual and add it to the tournament
            final int rind = ThreadLocalRandomSource.current(randomSource).nextInt(chromosomes.size());
            selectedChromosomes.add(chromosomes.get(rind));
            // do not select it again
            chromosomes.remove(rind);
        }

        // the winner takes it all
        return Collections.max(selectedChromosomes);
    }

    /**
     * Gets the arity (number of chromosomes drawn to the tournament).
     *
     * @return arity of the tournament
     */
    public int getArity() {
        return arity;
    }
}
