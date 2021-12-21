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
package org.apache.commons.math4.examples.ga.mathfunctions.dimensionN;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math4.ga.chromosome.BinaryChromosome;
import org.apache.commons.math4.ga.chromosome.Chromosome;
import org.apache.commons.math4.ga.decoder.Decoder;

/**
 * Decoder to convert chromosome's binary genotype to phenotype
 * {@link DimensionNCoordinate}.
 */
public class DimensionNDecoder implements Decoder<DimensionNCoordinate> {

    /**
     * decode the binary representation of chromosome to
     * {@link DimensionNCoordinate}.
     * @param chromosome The {@link Chromosome}
     */
    @Override
    public DimensionNCoordinate decode(Chromosome<DimensionNCoordinate> chromosome) {
        final BinaryChromosome<DimensionNCoordinate> binaryChromosome = (BinaryChromosome<DimensionNCoordinate>) chromosome;
        final long length = binaryChromosome.getLength();
        List<Double> coordinates = new ArrayList<>();

        for (int i = 0; i < length; i += 12) {
            final String dimensionStrValue = binaryChromosome.getStringRepresentation(i, i + 12);
            coordinates.add(Integer.parseUnsignedInt(dimensionStrValue, 2) / 100d);
        }

        return new DimensionNCoordinate(coordinates);
    }

}
