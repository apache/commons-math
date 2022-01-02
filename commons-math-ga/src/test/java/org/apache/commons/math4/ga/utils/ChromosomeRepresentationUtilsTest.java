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
package org.apache.commons.math4.ga.utils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.math4.ga.chromosome.RealValuedChromosome;
import org.apache.commons.math4.ga.decoder.RandomKeyDecoder;
import org.apache.commons.math4.ga.internal.exception.GeneticException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ChromosomeRepresentationUtilsTest {

    @Test
    public void testRandomPermutation() {
        // never generate an invalid one
        for (int i = 0; i < 10; i++) {
            List<Double> representation = ChromosomeRepresentationUtils.randomPermutation(10);
            Assertions.assertNotNull(representation);
        }
    }

    @Test
    public void testIdentityPermutation() {
        List<Double> identityPermutation = ChromosomeRepresentationUtils.identityPermutation(5);
        List<String> sequence = Arrays.asList(new String[] {"a", "b", "c", "d", "e"});
        RandomKeyDecoder<String> decoder = new RandomKeyDecoder<>(sequence);
        RealValuedChromosome<List<String>> chromosome = new RealValuedChromosome<>(identityPermutation, c -> 0,
                decoder);
        List<String> decoded = decoder.decode(chromosome);

        Assertions.assertEquals("a", decoded.get(0));
        Assertions.assertEquals("b", decoded.get(1));
        Assertions.assertEquals("c", decoded.get(2));
        Assertions.assertEquals("d", decoded.get(3));
        Assertions.assertEquals("e", decoded.get(4));
    }

    @Test
    public void testComparatorPermutation() {
        List<String> sequence = Arrays.asList(new String[] {"x", "b", "c", "z", "b"});

        List<Double> permutation = ChromosomeRepresentationUtils.comparatorPermutation(sequence,
                new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareTo(o2);
                    }
                });
        Double[] permArr = new Double[sequence.size()];
        permArr = permutation.toArray(permArr);

        Assertions.assertArrayEquals(new Double[] {0.6, 0.0, 0.4, 0.8, 0.2}, permArr);

        RandomKeyDecoder<String> decoder = new RandomKeyDecoder<>(sequence);
        List<String> decodedData = decoder.decode(new RealValuedChromosome<>(permutation, c -> 0, decoder));

        Assertions.assertEquals("b", decodedData.get(0));
        Assertions.assertEquals("b", decodedData.get(1));
        Assertions.assertEquals("c", decodedData.get(2));
        Assertions.assertEquals("x", decodedData.get(3));
        Assertions.assertEquals("z", decodedData.get(4));

        permutation = ChromosomeRepresentationUtils.comparatorPermutation(sequence, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o2.compareTo(o1);
            }
        });
        permArr = new Double[sequence.size()];
        permArr = permutation.toArray(permArr);

        Assertions.assertArrayEquals(new Double[] {0.2, 0.6, 0.4, 0.0, 0.8}, permArr);

        decodedData = decoder.decode(new RealValuedChromosome<>(permutation, c -> 0, decoder));

        Assertions.assertEquals("z", decodedData.get(0));
        Assertions.assertEquals("x", decodedData.get(1));
        Assertions.assertEquals("c", decodedData.get(2));
        Assertions.assertEquals("b", decodedData.get(3));
        Assertions.assertEquals("b", decodedData.get(4));
    }

    @Test
    public void testInducedPermutation() {
        List<String> origData = Arrays.asList(new String[] {"a", "b", "c", "d", "d"});
        List<String> permutedData = Arrays.asList(new String[] {"d", "b", "c", "a", "d"});

        RandomKeyDecoder<String> decoder = new RandomKeyDecoder<>(origData);
        RealValuedChromosome<List<String>> chromosome = new RealValuedChromosome<>(
                ChromosomeRepresentationUtils.inducedPermutation(origData, permutedData), c -> 0, decoder);
        List<String> decoded = decoder.decode(chromosome);

        Assertions.assertEquals("d", decoded.get(0));
        Assertions.assertEquals("b", decoded.get(1));
        Assertions.assertEquals("c", decoded.get(2));
        Assertions.assertEquals("a", decoded.get(3));
        Assertions.assertEquals("d", decoded.get(4));

        try {
            ChromosomeRepresentationUtils.inducedPermutation(Arrays.asList(new String[] {"a", "b", "c", "d", "d"}),
                    Arrays.asList(new String[] {"a", "b", "c", "d"}));
            Assertions.fail("Uncaught exception");
        } catch (GeneticException e) {
            // no-op
        }
        try {
            ChromosomeRepresentationUtils.inducedPermutation(Arrays.asList(new String[] {"a", "b", "c", "d", "d"}),
                    Arrays.asList(new String[] {"a", "b", "c", "d", "f"}));
            Assertions.fail("Uncaught exception");
        } catch (GeneticException e) {
            // no-op
        }
    }

    @Test
    public void testEqualRepr() {
        RandomKeyDecoder<String> decoder = new RandomKeyDecoder<>(Arrays.asList(new String[] {"a", "b", "c"}));
        RealValuedChromosome<List<String>> chromosome = new RealValuedChromosome<>(new Double[] {0.2, 0.2, 0.5}, c -> 0,
                decoder);

        List<String> decodedData = decoder.decode(chromosome);
        Assertions.assertEquals("a", decodedData.get(0));
        Assertions.assertEquals("b", decodedData.get(1));
        Assertions.assertEquals("c", decodedData.get(2));
    }

    @Test
    public void testIntegralRepresentation() {
        int min = 0;
        int max = 10;
        List<Integer> values = ChromosomeRepresentationUtils.randomIntegralRepresentation(100, min, max);
        for (Integer value : values) {
            Assertions.assertTrue(min <= value && value < max);
        }
    }

    @Test
    public void testNormalizedDoubleRepresentation() {
        List<Double> values = ChromosomeRepresentationUtils.randomNormalizedDoubleRepresentation(100);
        for (Double value : values) {
            Assertions.assertTrue(0 <= value && value < 1);
        }
    }

}
