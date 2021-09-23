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

package org.apache.commons.math4.examples.genetics.mathfunctions.legacy;

import java.util.List;

import org.apache.commons.math3.genetics.AbstractListChromosome;
import org.apache.commons.math3.genetics.BinaryChromosome;

/**
 * A representation of concrete binary chromosome.
 */
public class LegacyBinaryChromosome extends BinaryChromosome {

    /**
     * constructor.
     * @param representation the internal representation
     */
    public LegacyBinaryChromosome(List<Integer> representation) {
        super(representation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double fitness() {
        final List<Integer> alleles = getRepresentation();

        final StringBuilder allelesStr = new StringBuilder();
        for (Integer allele : alleles) {
            allelesStr.append(Integer.toBinaryString(allele));
        }

        final double x = Integer.parseInt(allelesStr.substring(0, 12), 2) / 100.0;
        final double y = Integer.parseInt(allelesStr.substring(12, 24), 2) / 100.0;

        return -Math.pow(Math.pow(x, 2) + Math.pow(y, 2), .25) *
                (Math.pow(Math.sin(50 * Math.pow(Math.pow(x, 2) + Math.pow(y, 2), .1)), 2) + 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractListChromosome<Integer> newFixedLengthChromosome(List<Integer> chromosomeRepresentation) {
        return new LegacyBinaryChromosome(chromosomeRepresentation);
    }

}
