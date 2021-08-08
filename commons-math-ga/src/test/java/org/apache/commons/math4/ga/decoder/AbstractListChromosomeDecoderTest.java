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

import org.apache.commons.math4.ga.chromosome.AbstractListChromosome;
import org.apache.commons.math4.ga.chromosome.Chromosome;
import org.apache.commons.math4.ga.dummy.DummyChromosome;
import org.apache.commons.math4.ga.internal.exception.GeneticException;
import org.junit.Test;

public class AbstractListChromosomeDecoderTest {

    @Test(expected = GeneticException.class)
    public void testDecodeWithInvalidChromosomeInstance() {
        Decoder<String> decoder = new AbstractListChromosomeDecoder<Integer, String>() {

            @Override
            protected String decode(AbstractListChromosome<Integer, String> chromosome) {
                return null;
            }
        };
        Chromosome<String> ch = new DummyChromosome();
        decoder.decode(ch);
    }

}
