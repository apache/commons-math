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
package org.apache.commons.math4.genetics.mutation;

import org.apache.commons.math4.genetics.chromosome.BinaryChromosome;
import org.apache.commons.math4.genetics.utils.DummyListChromosomeDecoder;
import org.junit.Assert;
import org.junit.Test;

public class BinaryMutationTest {

    @Test
    public void testMutate() {
        BinaryMutation<String> mutation = new BinaryMutation<>();

        // stochastic testing for single gene mutation :)
        for (int i = 0; i < 20; i++) {
            BinaryChromosome<String> original = BinaryChromosome.<String>randomChromosome(10, chromosome -> 0,
                    new DummyListChromosomeDecoder<>("0"));
            BinaryChromosome<String> mutated = (BinaryChromosome<String>) mutation.mutate(original, .1);

            // one gene should be different
            int numDifferent = 0;
            for (int j = 0; j < original.getRepresentation().size(); j++) {
                if (original.getRepresentation().get(j) != mutated.getRepresentation().get(j)) {
                    numDifferent++;
                }
            }
            Assert.assertEquals(1, numDifferent);
        }

        // stochastic testing for two gene mutation :)
        for (int i = 0; i < 20; i++) {
            BinaryChromosome<String> original = BinaryChromosome.<String>randomChromosome(10, chromosome -> 0,
                    new DummyListChromosomeDecoder<>("0"));
            BinaryChromosome<String> mutated = (BinaryChromosome<String>) mutation.mutate(original, .2);

            // one gene should be different
            int numDifferent = 0;
            for (int j = 0; j < original.getRepresentation().size(); j++) {
                if (original.getRepresentation().get(j) != mutated.getRepresentation().get(j)) {
                    numDifferent++;
                }
            }
            Assert.assertEquals(2, numDifferent);
        }

        // stochastic testing for three gene mutation :)
        for (int i = 0; i < 20; i++) {
            BinaryChromosome<String> original = BinaryChromosome.<String>randomChromosome(10, chromosome -> 0,
                    new DummyListChromosomeDecoder<>("0"));
            BinaryChromosome<String> mutated = (BinaryChromosome<String>) mutation.mutate(original, .3);

            // one gene should be different
            int numDifferent = 0;
            for (int j = 0; j < original.getRepresentation().size(); j++) {
                if (original.getRepresentation().get(j) != mutated.getRepresentation().get(j)) {
                    numDifferent++;
                }
            }
            Assert.assertEquals(3, numDifferent);
        }
    }

}
