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
package org.apache.commons.math4.examples.ga.mathfunctions.dimension2;

import org.apache.commons.math4.ga.chromosome.BinaryChromosome;
import org.apache.commons.math4.ga.chromosome.Chromosome;
import org.apache.commons.math4.ga.decoder.Decoder;

/**
 * Decoder to convert chromosome's binary genotype to phenotype
 * {@link Dimension2Coordinate}.
 */
public class Dimension2Decoder implements Decoder<Dimension2Coordinate> {

    /**
     * Decode the binary representation of chromosome to
     * {@link Dimension2Coordinate}.
     * @param chromosome The {@link Chromosome}
     */
    @Override
    public Dimension2Coordinate decode(Chromosome<Dimension2Coordinate> chromosome) {
        final BinaryChromosome<Dimension2Coordinate> binaryChromosome =
                (BinaryChromosome<Dimension2Coordinate>) chromosome;
        final long alleles = binaryChromosome.getRepresentation()[0];

        long mask1 = ~(Long.MAX_VALUE << 12);
        long mask2 = ~(Long.MAX_VALUE << 24) ^ mask1;

        final double x = (alleles & mask1) / 100d;
        final double y = ((alleles & mask2) >> 12) / 100d;

        return new Dimension2Coordinate(x, y);
    }

}
