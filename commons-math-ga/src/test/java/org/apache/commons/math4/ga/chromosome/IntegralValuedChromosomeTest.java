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
package org.apache.commons.math4.ga.chromosome;

import org.apache.commons.math4.ga.dummy.DummyListChromosomeDecoder;
import org.apache.commons.math4.ga.internal.exception.GeneticException;

import org.apache.commons.math4.ga.utils.ChromosomeRepresentationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IntegralValuedChromosomeTest {

    @Test
    public void testIntegralValuedChromosome() {
        int min = 0;
        int max = 10;
        IntegralValuedChromosome<String> chromosome = new IntegralValuedChromosome<>(
                ChromosomeRepresentationUtils.randomIntegralRepresentation(10, min, max), c -> 0,
                new DummyListChromosomeDecoder<>("0"), min, max);
        Assertions.assertEquals(min, chromosome.getMin());
        Assertions.assertEquals(max, chromosome.getMax());

        IntegralValuedChromosome<String> chromosome1 = new IntegralValuedChromosome<>(
                ChromosomeRepresentationUtils.randomIntegralRepresentation(10, min, max).toArray(new Integer[10]),
            c -> 0, new DummyListChromosomeDecoder<>("0"), min, max);
        Assertions.assertEquals(min, chromosome1.getMin());
        Assertions.assertEquals(max, chromosome1.getMax());
    }

    @Test
    public void testCheckValidity() {
        int min = 0;
        int max = 10;
        Assertions.assertThrows(GeneticException.class, () -> {
            new IntegralValuedChromosome<>(ChromosomeRepresentationUtils.randomIntegralRepresentation(10, min, max),
                c -> 0, new DummyListChromosomeDecoder<>("0"), max, min);
        });
    }

    @Test
    public void testCheckValidity1() {
        int min = 0;
        int max = 10;
        Assertions.assertThrows(GeneticException.class, () -> {
            new IntegralValuedChromosome<>(
                    ChromosomeRepresentationUtils.randomIntegralRepresentation(10, min - 10, max + 10), c -> 0,
                    new DummyListChromosomeDecoder<>("0"), min, max);
        });

    }

    @Test
    public void testNewChromosome() {
        int min = 0;
        int max = 10;
        IntegralValuedChromosome<String> chromosome = new IntegralValuedChromosome<>(
                ChromosomeRepresentationUtils.randomIntegralRepresentation(10, min, max), c -> 0,
                new DummyListChromosomeDecoder<>("0"), min, max);
        IntegralValuedChromosome<String> newChromosome = chromosome
                .newChromosome(ChromosomeRepresentationUtils.randomIntegralRepresentation(10, min, max));
        Assertions.assertEquals(chromosome.getMin(), newChromosome.getMin());
        Assertions.assertEquals(chromosome.getMax(), newChromosome.getMax());
        Assertions.assertEquals(chromosome.getDecoder(), newChromosome.getDecoder());
        Assertions.assertEquals(chromosome.getFitnessFunction(), newChromosome.getFitnessFunction());

    }

    @Test
    public void testRandomChromosome() {
        int min = 0;
        int max = 10;
        IntegralValuedChromosome<String> chromosome = IntegralValuedChromosome.<String>randomChromosome(10, c -> 0,
                new DummyListChromosomeDecoder<>("0"), min, max);
        Assertions.assertEquals(min, chromosome.getMin());
        Assertions.assertEquals(max, chromosome.getMax());
    }

}
