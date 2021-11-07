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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BinaryChromosomeTest {

    @Test
    public void testInvalidConstructor() {
        Integer[][] reprs = new Integer[][] {new Integer[] {0, 1, 0, 1, 2}, new Integer[] {0, 1, 0, 1, -1}};

        for (Integer[] repr : reprs) {
            Assertions.assertThrows(GeneticException.class, () -> {
                new BinaryChromosome<>(repr, c -> 0, new DummyListChromosomeDecoder<>("0"));
            });
        }
    }

    @Test
    public void testRandomConstructor() {
        for (int i = 0; i < 20; i++) {
            BinaryChromosome.<String>randomChromosome(10, c -> 1, new DummyListChromosomeDecoder<>("1"));
        }
    }

}
