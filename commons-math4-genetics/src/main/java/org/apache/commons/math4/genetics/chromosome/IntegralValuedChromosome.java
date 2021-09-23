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
package org.apache.commons.math4.genetics.chromosome;

import java.util.List;

import org.apache.commons.math4.genetics.decoder.AbstractListChromosomeDecoder;
import org.apache.commons.math4.genetics.exception.GeneticException;
import org.apache.commons.math4.genetics.fitness.FitnessFunction;
import org.apache.commons.math4.genetics.utils.ChromosomeRepresentationUtils;

/**
 * Chromosome represented by a list of integral values. The acceptable integral
 * values should belong to the range min(inclusive) to max(exclusive).
 * @param <P> phenotype of chromosome
 * @since 4.0
 */
public class IntegralValuedChromosome<P> extends AbstractListChromosome<Integer, P> {

    /** minimum acceptable value of allele. **/
    private final int min;

    /** maximum acceptable value of allele. **/
    private final int max;

    /**
     * constructor.
     * @param representation  Internal representation of chromosome.
     * @param fitnessFunction The {@link FitnessFunction}
     * @param decoder         The {@link AbstractListChromosomeDecoder}
     * @param min             minimum inclusive value of allele
     * @param max             maximum exclusive value of allele
     */
    public IntegralValuedChromosome(List<Integer> representation, FitnessFunction<P> fitnessFunction,
            AbstractListChromosomeDecoder<Integer, P> decoder, int min, int max) {
        super(representation, fitnessFunction, decoder);
        this.min = min;
        this.max = max;
        checkValidity();
    }

    /**
     * constructor.
     * @param representation  Internal representation of chromosome.
     * @param fitnessFunction The {@link FitnessFunction}
     * @param decoder         The {@link AbstractListChromosomeDecoder}
     * @param min             minimum inclusive value of allele
     * @param max             maximum exclusive value of allele
     */
    public IntegralValuedChromosome(Integer[] representation, FitnessFunction<P> fitnessFunction,
            AbstractListChromosomeDecoder<Integer, P> decoder, int min, int max) {
        super(representation, fitnessFunction, decoder);
        this.min = min;
        this.max = max;
        checkValidity();
    }

    /**
     * Returns the minimum acceptable value of allele.
     * @return minimum value
     */
    public int getMin() {
        return min;
    }

    /**
     * Returns the maximum acceptable value of allele.
     * @return maximum value
     */
    public int getMax() {
        return max;
    }

    /**
     * Asserts that <code>representation</code> can represent a valid chromosome.
     */
    private void checkValidity() {
        if (min > max) {
            throw new GeneticException(GeneticException.TOO_LARGE, min, max);
        }
        for (int i : getRepresentation()) {
            if (i < min || i >= max) {
                throw new GeneticException(GeneticException.ILLEGAL_ARGUMENT, i);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntegralValuedChromosome<P> newChromosome(List<Integer> chromosomeRepresentation) {
        return new IntegralValuedChromosome<P>(chromosomeRepresentation, getFitnessFunction(), getDecoder(), this.min,
                this.max);
    }

    /**
     * Creates an instance of Integral valued Chromosome with random binary
     * representation.
     * @param <P>             phenotype fo chromosome
     * @param length          length of chromosome
     * @param fitnessFunction The {@link FitnessFunction}
     * @param decoder         The {@link AbstractListChromosomeDecoder}
     * @param min             minimum inclusive value of allele
     * @param max             maximum exclusive value of allele
     * @return a binary chromosome
     */
    public static <P> IntegralValuedChromosome<P> randomChromosome(int length, FitnessFunction<P> fitnessFunction,
            AbstractListChromosomeDecoder<Integer, P> decoder, int min, int max) {
        return new IntegralValuedChromosome<>(
                ChromosomeRepresentationUtils.randomIntegralRepresentation(length, min, max), fitnessFunction, decoder,
                min, max);
    }

}
