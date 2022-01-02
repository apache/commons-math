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

import java.util.List;
import java.util.Objects;

import org.apache.commons.math4.ga.chromosome.Chromosome;
import org.apache.commons.math4.ga.chromosome.IntegralValuedChromosome;
import org.apache.commons.math4.ga.utils.ChromosomeRepresentationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TransparentListChromosomeDecoderTest {

    @Test
    public void testDecode() {
        List<Integer> rp = ChromosomeRepresentationUtils.randomIntegralRepresentation(10, 0, 2);
        Chromosome<List<Integer>> chromosome = new IntegralValuedChromosome<>(rp, c -> 0,
                new TransparentListChromosomeDecoder<>(), 0, 2);
        List<Integer> decodedRp = chromosome.decode();
        Assertions.assertTrue(Objects.equals(rp, decodedRp));
    }

}
