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
package org.apache.commons.math4.ga.mutation;

import java.util.List;

import org.apache.commons.math4.ga.chromosome.Chromosome;
import org.apache.commons.math4.ga.chromosome.IntegralValuedChromosome;
import org.apache.commons.math4.ga.chromosome.RealValuedChromosome;
import org.apache.commons.math4.ga.dummy.DummyListChromosomeDecoder;
import org.apache.commons.math4.ga.internal.exception.GeneticIllegalArgumentException;
import org.apache.commons.math4.ga.utils.ChromosomeRepresentationUtils;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.rng.simple.ThreadLocalRandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RealValuedMutationTest {

    @Test
    public void testCheckValidity() {
        int min = 0;
        int max = 10;
        Chromosome<String> chromosome = new IntegralValuedChromosome<>(
                ChromosomeRepresentationUtils.randomIntegralRepresentation(10, min, max), c -> 0,
                new DummyListChromosomeDecoder<>("0"), min, max);
        RealValuedMutation<String> mutation = new RealValuedMutation<>(min - 10, max);
        Assertions.assertThrows(GeneticIllegalArgumentException.class, () -> {
            mutation.mutate(chromosome, .1);
        });
    }

    @Test
    public void testCheckValidity1() {
        double min = 0;
        double max = 10;
        RealValuedChromosome<String> chromosome = new RealValuedChromosome<>(
                ChromosomeRepresentationUtils.randomDoubleRepresentation(10, min, max), c -> 0,
                new DummyListChromosomeDecoder<>("0"), min, max);
        RealValuedMutation<String> mutation = new RealValuedMutation<>(min - 10, max);
        Assertions.assertThrows(GeneticIllegalArgumentException.class, () -> {
            mutation.mutate(chromosome, .1);
        });
    }

    @Test
    public void testGetMutableGeneIndexes() {
        RealValuedMutation<String> mutationPolicy = new RealValuedMutation<>(0, 10);
        List<Double> representation = ChromosomeRepresentationUtils.randomDoubleRepresentation(10, 0, 10);
        RealValuedChromosome<String> chromosome = new RealValuedChromosome<>(representation, c -> 0,
                new DummyListChromosomeDecoder<>("0"), 0, 10);
        RealValuedChromosome<String> mutatedChromosome = mutationPolicy.mutate(chromosome, .2);
        Assertions.assertEquals(2, calculatedMutatedAlleleCount(representation, mutatedChromosome.getRepresentation()));
        mutatedChromosome = mutationPolicy.mutate(chromosome, .1);
        Assertions.assertEquals(1, calculatedMutatedAlleleCount(representation, mutatedChromosome.getRepresentation()));
    }

    private int calculatedMutatedAlleleCount(List<Double> representation, List<Double> mutatedRepresentation) {
        int mutationCount = 0;
        for (int i = 0; i < representation.size(); i++) {
            if (representation.get(i) != mutatedRepresentation.get(i)) {
                mutationCount++;
            }
        }
        return mutationCount;
    }

    @Test
    public void testRealValuedMutation() {
        Assertions.assertThrows(GeneticIllegalArgumentException.class, () -> {
            new RealValuedMutation<>(10, 5);
        });
    }

    @Test
    public void testGetMinMax() {
        double min = 0;
        double max = 10;
        RealValuedMutation<String> mutation = new RealValuedMutation<>(min, max);
        Assertions.assertEquals(min, mutation.getMin(), .001);
        Assertions.assertEquals(max, mutation.getMax(), .001);
    }

    @Test
    public void testMutateGene() {
        double min = 0;
        double max = 10;
        RealValuedMutation<String> mutation = new RealValuedMutation<>(min, max);
        for (int i = 0; i < 100; i++) {
            double origValue = min +
                    (max - min) * ThreadLocalRandomSource.current(RandomSource.XO_RO_SHI_RO_128_PP).nextDouble();
            double mutatedValue = mutation.mutateGene(origValue);
            Assertions.assertTrue(min <= mutatedValue && mutatedValue < max);
            Assertions.assertNotEquals(origValue, mutatedValue);
        }
    }

}
