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
package org.apache.commons.math4.genetics;

import java.util.List;

import org.apache.commons.math4.genetics.decoder.AbstractListChromosomeDecoder;
import org.apache.commons.math4.genetics.exception.GeneticException;
import org.apache.commons.math4.genetics.utils.ChromosomeRepresentationUtils;

/**
 * Chromosome represented by a vector of 0s and 1s.
 * @param <P> phenotype of chromosome
 * @since 2.0
 */
public class BinaryChromosome<P> extends AbstractListChromosome<Integer, P> {

    /**
     * constructor.
     * @param representation
     * @param fitnessFunction
     * @param decoder
     */
    public BinaryChromosome(List<Integer> representation, FitnessFunction<P> fitnessFunction,
            AbstractListChromosomeDecoder<Integer, P> decoder) {
        super(representation, fitnessFunction, decoder);
    }

    /**
     * constructor.
     * @param representation
     * @param fitnessFunction
     * @param decoder
     */
    public BinaryChromosome(Integer[] representation, FitnessFunction<P> fitnessFunction,
            AbstractListChromosomeDecoder<Integer, P> decoder) {
        super(representation, fitnessFunction, decoder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkValidity(List<Integer> chromosomeRepresentation) {
        for (int i : chromosomeRepresentation) {
            if (i < 0 || i > 1) {
                throw new GeneticException(GeneticException.ILLEGAL_ARGUMENT, i);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BinaryChromosome<P> newChromosome(List<Integer> chromosomeRepresentation) {
        return new BinaryChromosome<P>(chromosomeRepresentation, getFitnessFunction(), getDecoder());
    }

    /**
     * Creates an instance of Binary Chromosome with random binary representation.
     * @param <P>             phenotype fo chromosome
     * @param length
     * @param fitnessFunction
     * @param decoder
     * @return a binary chromosome
     */
    public static <P> BinaryChromosome<P> randomChromosome(int length, FitnessFunction<P> fitnessFunction,
            AbstractListChromosomeDecoder<Integer, P> decoder) {
        return new BinaryChromosome<>(ChromosomeRepresentationUtils.randomBinaryRepresentation(length), fitnessFunction,
                decoder);
    }

}
