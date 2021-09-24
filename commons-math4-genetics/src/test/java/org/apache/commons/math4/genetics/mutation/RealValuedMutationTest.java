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

import org.apache.commons.math4.genetics.chromosome.AbstractChromosome;
import org.apache.commons.math4.genetics.chromosome.RealValuedChromosome;
import org.apache.commons.math4.genetics.utils.DummyListChromosomeDecoder;
import org.junit.Assert;
import org.junit.Test;

public class RealValuedMutationTest {

    @Test
    public void testMutate() {
        MutationPolicy<String> mutation = new RealValuedMutation<>();
        int l = 10;
        for (int i = 0; i < 20; i++) {
            RealValuedChromosome<String> origRk = RealValuedChromosome.<String>randomChromosome(l, chromosome -> 0,
                    new DummyListChromosomeDecoder<>("0"), 0d, 1d);
            AbstractChromosome<String> mutated = (AbstractChromosome<String>) mutation.mutate(origRk, .1);
            RealValuedChromosome<String> mutatedRk = (RealValuedChromosome<String>) mutated;

            int changes = 0;
            for (int j = 0; j < origRk.getLength(); j++) {
                if (origRk.getRepresentation().get(j) != mutatedRk.getRepresentation().get(j)) {
                    changes++;
                }
            }
            Assert.assertEquals(1, changes);
        }
    }

}
