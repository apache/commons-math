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

import org.apache.commons.math4.ga.chromosome.BinaryChromosome;
import org.apache.commons.math4.ga.chromosome.IntegralValuedChromosome;
import org.apache.commons.math4.ga.dummy.DummyListChromosomeDecoder;
import org.apache.commons.math4.ga.internal.exception.GeneticException;
import org.apache.commons.math4.ga.utils.ChromosomeRepresentationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BinaryMutationTest {

    @Test
    public void testCheckValidity() {
        BinaryMutation<String> mutation = new BinaryMutation<>();
        Assertions.assertThrows(GeneticException.class, () -> {
            mutation.checkValidity(new IntegralValuedChromosome<String>(
                    ChromosomeRepresentationUtils.randomIntegralRepresentation(10, 0, 2), c -> 0,
                    new DummyListChromosomeDecoder<>("0"), 0, 2));
        });
    }

    @Test
    public void testMutate() {
        BinaryMutation<String> mutation = new BinaryMutation<>();

        // stochastic testing for single gene mutation :)
        for (int i = 0; i < 20; i++) {
            BinaryChromosome<String> original = BinaryChromosome.<String>randomChromosome(10, chromosome -> 0,
                    new DummyListChromosomeDecoder<>("0"));
            BinaryChromosome<String> mutated = (BinaryChromosome<String>) mutation.mutate(original, .1);

            // one gene should be different
            Assertions.assertEquals(1, calculateNoOfMutatedBits(original, mutated));
        }

        // stochastic testing for two gene mutation :)
        for (int i = 0; i < 20; i++) {
            BinaryChromosome<String> original = BinaryChromosome.<String>randomChromosome(10, chromosome -> 0,
                c -> "0");
            BinaryChromosome<String> mutated = (BinaryChromosome<String>) mutation.mutate(original, .2);

            // one gene should be different
            Assertions.assertEquals(2, calculateNoOfMutatedBits(original, mutated));
        }

        // stochastic testing for three gene mutation :)
        for (int i = 0; i < 20; i++) {
            BinaryChromosome<String> original = BinaryChromosome.<String>randomChromosome(10, chromosome -> 0,
                c -> "0");
            BinaryChromosome<String> mutated = (BinaryChromosome<String>) mutation.mutate(original, .3);

            // three genes should be different
            Assertions.assertEquals(3, calculateNoOfMutatedBits(original, mutated));
        }
    }

    private int calculateNoOfMutatedBits(BinaryChromosome<String> original, BinaryChromosome<String> mutated) {
        int numDifferent = 0;
        long[] originalReps = original.getRepresentation();
        long[] mutatedReps = mutated.getRepresentation();
        for (int j = 0; j < originalReps.length; j++) {
            long xORValue = originalReps[j] ^ mutatedReps[j];
            String xORValueStr = Long.toBinaryString(xORValue);
            for (int k = 0; k < xORValueStr.length(); k++) {
                if (xORValueStr.charAt(k) == '1') {
                    numDifferent++;
                }
            }
        }
        return numDifferent;
    }

}
