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

import org.apache.commons.math4.ga.chromosome.Chromosome;
import org.apache.commons.math4.ga.chromosome.IntegralValuedChromosome;
import org.apache.commons.math4.ga.chromosome.RealValuedChromosome;
import org.apache.commons.math4.ga.internal.exception.GeneticException;
import org.apache.commons.math4.ga.utils.ChromosomeRepresentationUtils;
import org.apache.commons.math4.ga.utils.DummyListChromosomeDecoder;
import org.apache.commons.math4.ga.utils.RandomGenerator;
import org.junit.Assert;
import org.junit.Test;

public class IntegralValuedMutationTest {

    @Test(expected = GeneticException.class)
    public void testCheckValidity() {
        int min = 0;
        int max = 10;
        Chromosome<String> chromosome = new RealValuedChromosome<>(
                ChromosomeRepresentationUtils.randomNormalizedDoubleRepresentation(10), c -> 0,
                new DummyListChromosomeDecoder<>("0"));
        IntegralValuedMutation<String> mutation = new IntegralValuedMutation<>(min - 10, max);
        mutation.checkValidity(chromosome);
    }

    @Test(expected = GeneticException.class)
    public void testCheckValidity1() {
        int min = 0;
        int max = 10;
        IntegralValuedChromosome<String> chromosome = new IntegralValuedChromosome<>(
                ChromosomeRepresentationUtils.randomIntegralRepresentation(10, min, max), c -> 0,
                new DummyListChromosomeDecoder<>("0"), min, max);
        IntegralValuedMutation<String> mutation = new IntegralValuedMutation<>(min - 10, max);
        mutation.checkValidity(chromosome);
    }

    @Test(expected = GeneticException.class)
    public void testIntegralValuedMutation() {
        new IntegralValuedMutation<>(10, 5);
    }

    @Test
    public void testGetMinMax() {
        int min = 0;
        int max = 10;
        IntegralValuedMutation<String> mutation = new IntegralValuedMutation<>(min, max);
        Assert.assertEquals(min, mutation.getMin());
        Assert.assertEquals(max, mutation.getMax());
    }

    @Test
    public void testMutateGene() {
        int min = 0;
        int max = 10;
        IntegralValuedMutation<String> mutation = new IntegralValuedMutation<>(min, max);
        for (int i = 0; i < 100; i++) {
            int origValue = min + RandomGenerator.getRandomGenerator().nextInt(max - min);
            int mutatedValued = mutation.mutateGene(origValue);
            Assert.assertTrue(min <= mutatedValued && mutatedValued < max);
        }
    }

}
