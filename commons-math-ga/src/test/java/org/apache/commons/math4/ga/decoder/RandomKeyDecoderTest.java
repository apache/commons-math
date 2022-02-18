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
package org.apache.commons.math4.ga.decoder;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.math4.ga.chromosome.RealValuedChromosome;
import org.apache.commons.math4.ga.internal.exception.GeneticIllegalArgumentException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RandomKeyDecoderTest {

    @Test
    public void testDecodeChromosome() {

        List<String> sequence = Arrays.asList(new String[] {"a", "b", "c", "d", "e"});
        Double[] keys = new Double[] {0.4, 0.1, 0.5, 0.8, 0.2};

        RandomKeyDecoder<String> decoder = new RandomKeyDecoder<>(sequence);
        RealValuedChromosome<List<String>> chromosome = new RealValuedChromosome<>(keys, c -> 0, decoder);
        List<String> decodedSequence = chromosome.decode();

        Assertions.assertEquals("b", decodedSequence.get(0));
        Assertions.assertEquals("e", decodedSequence.get(1));
        Assertions.assertEquals("a", decodedSequence.get(2));
        Assertions.assertEquals("c", decodedSequence.get(3));
        Assertions.assertEquals("d", decodedSequence.get(4));

    }

    @Test
    public void testSequenceLength() {
        List<String> sequence = Arrays.asList(new String[] {"a", "b", "c", "d", "e", "f"});
        Double[] keys = new Double[] {0.4, 0.1, 0.5, 0.8, 0.2};

        RandomKeyDecoder<String> decoder = new RandomKeyDecoder<>(sequence);
        RealValuedChromosome<List<String>> chromosome = new RealValuedChromosome<>(keys, c -> 0, decoder);
        Assertions.assertThrows(GeneticIllegalArgumentException.class, () -> {
            chromosome.decode();
        });
    }
}
