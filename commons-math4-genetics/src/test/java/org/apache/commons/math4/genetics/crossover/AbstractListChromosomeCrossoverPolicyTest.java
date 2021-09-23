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
package org.apache.commons.math4.genetics.crossover;

import org.apache.commons.math4.genetics.chromosome.AbstractChromosome;
import org.apache.commons.math4.genetics.chromosome.AbstractListChromosome;
import org.apache.commons.math4.genetics.chromosome.Chromosome;
import org.apache.commons.math4.genetics.chromosome.ChromosomePair;
import org.apache.commons.math4.genetics.dummy.DummyListChromosome;
import org.apache.commons.math4.genetics.exception.GeneticException;
import org.apache.commons.math4.genetics.utils.ChromosomeRepresentationUtils;
import org.junit.Test;

public class AbstractListChromosomeCrossoverPolicyTest {

    @Test(expected = GeneticException.class)
    public void testCrossoverWithNonListChromosome() {

        CrossoverPolicy<String> crossoverPolicy = new AbstractListChromosomeCrossoverPolicy<Integer, String>() {

            @Override
            protected ChromosomePair<String> mate(AbstractListChromosome<Integer, String> first,
                    AbstractListChromosome<Integer, String> second) {
                return new ChromosomePair<>(first, second);
            }
        };
        Chromosome<String> ch1 = new AbstractChromosome<String>(c -> {
            return 0;
        }, c -> {
            return "0";
        }) {
        };

        Chromosome<String> ch2 = new AbstractChromosome<String>(c -> {
            return 1;
        }, c -> {
            return "1";
        }) {
        };

        crossoverPolicy.crossover(ch1, ch2, 1.0);
    }

    @Test(expected = GeneticException.class)
    public void testCrossoverWithUnEqualLengthChromosome() {

        CrossoverPolicy<String> crossoverPolicy = new AbstractListChromosomeCrossoverPolicy<Integer, String>() {

            @Override
            protected ChromosomePair<String> mate(AbstractListChromosome<Integer, String> first,
                    AbstractListChromosome<Integer, String> second) {
                return new ChromosomePair<>(first, second);
            }
        };
        Chromosome<String> ch1 = new DummyListChromosome(ChromosomeRepresentationUtils.randomBinaryRepresentation(10));

        Chromosome<String> ch2 = new DummyListChromosome(ChromosomeRepresentationUtils.randomBinaryRepresentation(20));

        crossoverPolicy.crossover(ch1, ch2, 1.0);
    }

}
