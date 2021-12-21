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

public class BinaryChromosomeTest {

    @Test
    public void testInvalidConstructor() {
        Assertions.assertThrows(GeneticException.class, () -> {
            new BinaryChromosome<String>(ChromosomeRepresentationUtils.randomBinaryRepresentation(10), Long.MAX_VALUE,
                c -> 0, c -> "0");
        });
        Assertions.assertThrows(GeneticException.class, () -> {
            new BinaryChromosome<String>(ChromosomeRepresentationUtils.randomBinaryRepresentation(10), 100, c -> 0,
                c -> "0");
        });
    }

    @Test
    public void testRandomConstructor() {
        for (int i = 0; i < 20; i++) {
            BinaryChromosome.<String>randomChromosome(10, c -> 1, new DummyListChromosomeDecoder<>("1"));
        }
    }

    @Test
    public void testGetStringRepresentation() {
        int length = 10;
        int startToEndGap = 1;
        testStringRepresentationWithRanges(length, startToEndGap);

        length = 64;
        startToEndGap = 10;
        testStringRepresentationWithRanges(length, startToEndGap);

        length = 100;
        startToEndGap = 50;
        testStringRepresentationWithRanges(length, startToEndGap);

        length = 128;
        startToEndGap = 70;
        testStringRepresentationWithRanges(length, startToEndGap);

        length = 250;
        startToEndGap = 128;
        testStringRepresentationWithRanges(length, startToEndGap);

        length = 350;
        startToEndGap = 228;
        testStringRepresentationWithRanges(length, startToEndGap);

        length = 450;
        startToEndGap = 108;
        testStringRepresentationWithRanges(length, startToEndGap);

    }

    private void testStringRepresentationWithRanges(int length, int startToEndGap) {
        for (int i = 0; i < 50; i++) {
            String representationStr = ChromosomeRepresentationUtils.randomStringRepresentation(new char[] {'0', '1'},
                    length);
            BinaryChromosome<String> chromosome = new BinaryChromosome<>(representationStr, c -> 0, c -> "0");
            Assertions.assertEquals(representationStr, chromosome.getStringRepresentation());
            for (int j = 0; j < length - startToEndGap; j++) {
                int index = (int) ((length + 1 - startToEndGap) * Math.random());
                Assertions.assertEquals(representationStr.substring(index, index + startToEndGap),
                        chromosome.getStringRepresentation(index, index + startToEndGap));
            }
        }
    }

}
