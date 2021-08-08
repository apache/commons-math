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

import java.util.List;

import org.apache.commons.math4.ga.chromosome.AbstractListChromosome;
import org.apache.commons.math4.ga.chromosome.BinaryChromosome;
import org.apache.commons.math4.ga.decoder.AbstractListChromosomeDecoder;

/**
 * Decoder to convert chromosome's binary genotype to phenotype
 * {@link Dimension2Coordinate}.
 */
public class Dimension2Decoder extends AbstractListChromosomeDecoder<Integer, Dimension2Coordinate> {

    /**
     * decode the binary representation of chromosome to
     * {@link Dimension2Coordinate}.
     * @param chromosome The {@link AbstractListChromosome}
     */
    @Override
    protected Dimension2Coordinate decode(AbstractListChromosome<Integer, Dimension2Coordinate> chromosome) {
        final BinaryChromosome<Dimension2Coordinate> binaryChromosome =
                (BinaryChromosome<Dimension2Coordinate>) chromosome;
        final List<Integer> alleles = binaryChromosome.getRepresentation();

        final StringBuilder allelesStr = new StringBuilder();
        for (Integer allele : alleles) {
            allelesStr.append(Integer.toBinaryString(allele));
        }

        final double x = Integer.parseInt(allelesStr.substring(0, 12), 2) / 100.0;
        final double y = Integer.parseInt(allelesStr.substring(12, 24), 2) / 100.0;

        return new Dimension2Coordinate(x, y);
    }

}
