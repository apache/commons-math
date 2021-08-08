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

public class RealValuedMutationTest {

    @Test(expected = GeneticException.class)
    public void testCheckValidity() {
        int min = 0;
        int max = 10;
        Chromosome<String> chromosome = new IntegralValuedChromosome<>(
                ChromosomeRepresentationUtils.randomIntegralRepresentation(10, min, max), c -> 0,
                new DummyListChromosomeDecoder<>("0"), min, max);
        RealValuedMutation<String> mutation = new RealValuedMutation<>(min - 10, max);
        mutation.checkValidity(chromosome);
    }

    @Test(expected = GeneticException.class)
    public void testCheckValidity1() {
        double min = 0;
        double max = 10;
        RealValuedChromosome<String> chromosome = new RealValuedChromosome<>(
                ChromosomeRepresentationUtils.randomDoubleRepresentation(10, min, max), c -> 0,
                new DummyListChromosomeDecoder<>("0"), min, max);
        RealValuedMutation<String> mutation = new RealValuedMutation<>(min - 10, max);
        mutation.checkValidity(chromosome);
    }

    @Test(expected = GeneticException.class)
    public void testIntegralValuedMutation() {
        new RealValuedMutation<>(10, 5);
    }

    @Test
    public void testGetMinMax() {
        double min = 0;
        double max = 10;
        RealValuedMutation<String> mutation = new RealValuedMutation<>(min, max);
        Assert.assertEquals(min, mutation.getMin(), .001);
        Assert.assertEquals(max, mutation.getMax(), .001);
    }

    @Test
    public void testMutateGene() {
        double min = 0;
        double max = 10;
        RealValuedMutation<String> mutation = new RealValuedMutation<>(min, max);
        for (int i = 0; i < 100; i++) {
            double origValue = min + (max - min) * RandomGenerator.getRandomGenerator().nextDouble();
            double mutatedValue = mutation.mutateGene(origValue);
            Assert.assertTrue(min <= mutatedValue && mutatedValue < max);
        }
    }

}
