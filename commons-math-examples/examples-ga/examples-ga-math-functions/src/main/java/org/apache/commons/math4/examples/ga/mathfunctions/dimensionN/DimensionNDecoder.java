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

import org.apache.commons.math4.ga.chromosome.AbstractListChromosome;
import org.apache.commons.math4.ga.chromosome.BinaryChromosome;
import org.apache.commons.math4.ga.decoder.AbstractListChromosomeDecoder;

/**
 * Decoder to convert chromosome's binary genotype to phenotype
 * {@link DimensionNCoordinate}.
 */
public class DimensionNDecoder extends AbstractListChromosomeDecoder<Integer, DimensionNCoordinate> {

    /**
     * decode the binary representation of chromosome to {@link DimensionNCoordinate}.
     * @param chromosome The {@link AbstractListChromosome}
     */
    @Override
    protected DimensionNCoordinate decode(AbstractListChromosome<Integer, DimensionNCoordinate> chromosome) {
        final BinaryChromosome<DimensionNCoordinate> binaryChromosome =
                (BinaryChromosome<DimensionNCoordinate>) chromosome;
        final List<Integer> alleles = binaryChromosome.getRepresentation();

        final StringBuilder allelesStr = new StringBuilder();
        for (Integer allele : alleles) {
            allelesStr.append(Integer.toBinaryString(allele));
        }

        List<Double> values = new ArrayList<>();
        for (int i = 0; i < allelesStr.length(); i += 12) {
            values.add(Integer.parseInt(allelesStr.substring(i, i + 12), 2) / 100.0);
        }

        return new DimensionNCoordinate(values);
    }

}
