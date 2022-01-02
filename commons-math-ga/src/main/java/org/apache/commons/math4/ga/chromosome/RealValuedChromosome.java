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
package org.apache.commons.math4.ga.chromosome;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.math4.ga.decoder.AbstractListChromosomeDecoder;
import org.apache.commons.math4.ga.fitness.FitnessFunction;
import org.apache.commons.math4.ga.internal.exception.GeneticException;
import org.apache.commons.math4.ga.utils.ChromosomeRepresentationUtils;

/**
 * DoubleEncodedChromosome is used for representing chromosome encoded as
 * Double. It is a vector of a fixed length of real numbers.The acceptable real
 * values should belong to the range min(inclusive) to max(exclusive).
 * <p>
 * @param <P> phenotype of chromosome
 * @since 4.0
 */
public class RealValuedChromosome<P> extends AbstractListChromosome<Double, P> {

    /** minimum acceptable value of allele. **/
    private final double min;

    /** maximum acceptable value of allele. **/
    private final double max;

    /**
     * @param representation  an array of real values
     * @param fitnessFunction the fitness function
     * @param decoder         the {@link AbstractListChromosomeDecoder}
     */
    public RealValuedChromosome(final List<Double> representation,
            FitnessFunction<P> fitnessFunction,
            AbstractListChromosomeDecoder<Double, P> decoder) {
        super(representation, fitnessFunction, decoder);
        this.min = 0;
        this.max = 1d;
        checkValidity();
    }

    /**
     * @param representation  an array of real values
     * @param fitnessFunction the fitness function
     * @param decoder         the {@link AbstractListChromosomeDecoder}
     * @param min             minimum inclusive value of allele
     * @param max             maximum exclusive value of allele
     */
    public RealValuedChromosome(final List<Double> representation,
            FitnessFunction<P> fitnessFunction,
            AbstractListChromosomeDecoder<Double, P> decoder,
            double min,
            double max) {
        super(representation, fitnessFunction, decoder);
        this.min = min;
        this.max = max;
        checkValidity();
    }

    /**
     * @param representation  Internal representation of chromosome as genotype
     * @param fitnessFunction The {@link FitnessFunction}
     * @param decoder         The {@link AbstractListChromosomeDecoder}
     */
    public RealValuedChromosome(final Double[] representation,
            FitnessFunction<P> fitnessFunction,
            AbstractListChromosomeDecoder<Double, P> decoder) {
        this(Arrays.asList(representation), fitnessFunction, decoder);
    }

    /**
     * @param representation  Internal representation of chromosome as genotype
     * @param fitnessFunction The {@link FitnessFunction}
     * @param decoder         The {@link AbstractListChromosomeDecoder}
     * @param min             minimum inclusive value of allele
     * @param max             maximum exclusive value of allele
     */
    public RealValuedChromosome(final Double[] representation,
            FitnessFunction<P> fitnessFunction,
            AbstractListChromosomeDecoder<Double, P> decoder,
            double min,
            double max) {
        this(Arrays.asList(representation), fitnessFunction, decoder, min, max);
    }

    /**
     * Return the minimum allele value.
     * @return minimum
     */
    public double getMin() {
        return min;
    }

    /**
     * Returns the maximum allele value.
     * @return maximum
     */
    public double getMax() {
        return max;
    }

    /**
     * Asserts that <code>representation</code> can represent a valid chromosome.
     */
    private void checkValidity() {
        if (min >= max) {
            throw new GeneticException(GeneticException.TOO_LARGE, min, max);
        }
        for (double i : getRepresentation()) {
            if (i < min || i >= max) {
                throw new GeneticException(GeneticException.ILLEGAL_ARGUMENT, i);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RealValuedChromosome<P> newChromosome(List<Double> chromosomeRepresentation) {
        return new RealValuedChromosome<>(chromosomeRepresentation, getFitnessFunction(), getDecoder(), this.min,
                this.max);
    }

    /**
     * Creates an instance of RealValued chromosome with randomly generated
     * representation.
     * @param <P>             phenotype of chromosome
     * @param length          length of chromosome genotype
     * @param fitnessFunction The {@link FitnessFunction}
     * @param decoder         The {@link AbstractListChromosomeDecoder}
     * @param min             minimum inclusive value generated as allele
     * @param max             maximum exclusive value generated as allele
     * @return A real-valued chromosome
     */
    public static <P> RealValuedChromosome<P> randomChromosome(int length,
            FitnessFunction<P> fitnessFunction,
            AbstractListChromosomeDecoder<Double, P> decoder,
            double min,
            double max) {
        return new RealValuedChromosome<>(ChromosomeRepresentationUtils.randomDoubleRepresentation(length, min, max),
                fitnessFunction, decoder, min, max);
    }

}
