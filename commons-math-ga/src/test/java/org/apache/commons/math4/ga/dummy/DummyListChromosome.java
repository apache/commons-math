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
package org.apache.commons.math4.ga.dummy;

import java.util.List;

import org.apache.commons.math4.ga.chromosome.AbstractListChromosome;
import org.apache.commons.math4.ga.utils.ChromosomeRepresentationUtils;

/**
 * Implementation of ListChromosome for testing purposes
 */
public class DummyListChromosome extends AbstractListChromosome<Integer, String> {

    public DummyListChromosome(final Integer[] representation) {
        super(representation, chromosome -> 0, new DummyListChromosomeDecoder<>("0"));
    }

    public DummyListChromosome() {
        super(ChromosomeRepresentationUtils.randomIntegralRepresentation(10, 0, 2), chromosome -> 0,
                new DummyListChromosomeDecoder<>("0"));
    }

    public DummyListChromosome(final List<Integer> representation) {
        super(representation, chromosome -> 0, new DummyListChromosomeDecoder<>("0"));
    }

    @Override
    public DummyListChromosome newChromosome(final List<Integer> chromosomeRepresentation) {
        return new DummyListChromosome(chromosomeRepresentation);
    }

}
