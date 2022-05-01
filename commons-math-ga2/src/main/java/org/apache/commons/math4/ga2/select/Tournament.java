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
package org.apache.commons.math4.ga2.select;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.rng.sampling.ListSampler;
import org.apache.commons.math4.ga2.Population;
import org.apache.commons.math4.ga2.Selection;

/**
 * Select chromosomes from a population.
 *
 * @param <G> Genotype.
 * @param <P> Phenotype.
 */
public class Tournament<G, P> implements Selection<G, P> {
    /** RNG. */
    private final UniformRandomProvider rng;
    /** Number of chromosomes that take part in a tournament. */
    private final int poolSize;

    /**
     * @param poolSize Number of chromosomes that take part in each tournament.
     * @param source Source of randomness.
     */
    public Tournament(int poolSize,
                      RandomSource source) {
        this.poolSize = poolSize;
        rng = source.create();
    }

    /** {@inheritDoc} */
    @Override
    public List<G> apply(int n,
                         Population<G, P> population) {
        final List<Map.Entry<G, Double>> all = population.contents(false);

        final List<G> selected = new ArrayList<G>(n);
        for (int i = 0; i < n; i++) {
            selected.add(selectFrom(all));
        }

        return selected;
    }

    /**
     * @param population Population.
     * @return the winner of a tournament.
     */
    private G selectFrom(List<Map.Entry<G, Double>> population) {
        // Randomly draw participants to the tournament.
        final List<Map.Entry<G, Double>> pool = ListSampler.sample(rng,
                                                                   population,
                                                                   poolSize);
        // Within the pool, choose the best individual.
        Population.sort(pool);
        return pool.get(0).getKey();
    }
}
