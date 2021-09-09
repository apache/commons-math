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

package org.apache.commons.math4.genetics.operators;

import org.apache.commons.math4.genetics.model.Chromosome;
import org.apache.commons.math4.genetics.model.ChromosomePair;
import org.apache.commons.math4.genetics.utils.RandomGenerator;

/**
 * An abstraction to represent the base crossover policy.
 */
public abstract class AbstractChromosomeCrossoverPolicy implements CrossoverPolicy {

    /**
     * {@inheritDoc}
     */
    @Override
    public ChromosomePair crossover(Chromosome first, Chromosome second, double crossoverRate) {
        if (RandomGenerator.getRandomGenerator().nextDouble() < crossoverRate) {
            return crossover(first, second);
        } else {
            return new ChromosomePair(first, second);
        }
    }

    /**
     * Performs crossover of two chromosomes.
     * @param first
     * @param second
     * @return chromosome pair
     */
    protected abstract ChromosomePair crossover(Chromosome first, Chromosome second);

}
