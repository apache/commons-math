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
package org.apache.commons.math4.ga.crossover;

import org.apache.commons.math4.ga.chromosome.AbstractChromosome;
import org.apache.commons.math4.ga.chromosome.AbstractListChromosome;
import org.apache.commons.math4.ga.chromosome.Chromosome;
import org.apache.commons.math4.ga.chromosome.ChromosomePair;
import org.apache.commons.math4.ga.dummy.DummyListChromosome;
import org.apache.commons.math4.ga.internal.exception.GeneticException;
import org.apache.commons.math4.ga.utils.ChromosomeRepresentationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AbstractListChromosomeCrossoverPolicyTest {

    @Test
    public void testCrossoverWithNonListChromosome() {

        CrossoverPolicy<String> crossoverPolicy = new AbstractListChromosomeCrossoverPolicy<Integer, String>() {

            @Override
            protected ChromosomePair<String> mate(AbstractListChromosome<Integer, String> first,
                    AbstractListChromosome<Integer, String> second) {
                return new ChromosomePair<>(first, second);
            }
        };
        Chromosome<String> ch1 = new AbstractChromosome<String>(c -> 0, c -> "0") {
        };

        Chromosome<String> ch2 = new AbstractChromosome<String>(c -> 1, c -> "1") {
        };

        Assertions.assertThrows(GeneticException.class, () -> {
            crossoverPolicy.crossover(ch1, ch2, 1.0);
        });

    }

    @Test
    public void testCrossoverWithUnEqualLengthChromosome() {

        CrossoverPolicy<String> crossoverPolicy = new AbstractListChromosomeCrossoverPolicy<Integer, String>() {

            @Override
            protected ChromosomePair<String> mate(AbstractListChromosome<Integer, String> first,
                    AbstractListChromosome<Integer, String> second) {
                return new ChromosomePair<>(first, second);
            }
        };
        Chromosome<String> ch1 = new DummyListChromosome(
                ChromosomeRepresentationUtils.randomIntegralRepresentation(10, 0, 2));

        Chromosome<String> ch2 = new DummyListChromosome(
                ChromosomeRepresentationUtils.randomIntegralRepresentation(12, 0, 2));

        Assertions.assertThrows(GeneticException.class, () -> {
            crossoverPolicy.crossover(ch1, ch2, 1.0);
        });

    }

}
