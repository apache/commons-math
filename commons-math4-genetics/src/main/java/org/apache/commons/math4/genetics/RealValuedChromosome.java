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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.math4.genetics.decoder.AbstractListChromosomeDecoder;
import org.apache.commons.math4.genetics.utils.ChromosomeRepresentationUtils;

/**
 * DoubleEncodedChromosome is used for representing chromosome encoded as
 * Double. It is a vector of a fixed length of real numbers.
 * <p>
 *
 * @param <P> phenotype of chromosome
 * @since 4.0
 */
public class RealValuedChromosome<P> extends AbstractListChromosome<Double, P> {

    /**
     * constructor.
     * @param representation  an array of real values
     * @param fitnessFunction the fitness function
     * @param decoder         the decoder
     */
    public RealValuedChromosome(final List<Double> representation, FitnessFunction<P> fitnessFunction,
            AbstractListChromosomeDecoder<Double, P> decoder) {
        super(representation, fitnessFunction, decoder);
    }

    /**
     * constructor.
     * @param representation
     * @param fitnessFunction
     */
    public RealValuedChromosome(final Double[] representation, FitnessFunction<P> fitnessFunction,
            AbstractListChromosomeDecoder<Double, P> decoder) {
        this(Arrays.asList(representation), fitnessFunction, decoder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkValidity(final List<Double> chromosomeRepresentation) {
        // No need to validate
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RealValuedChromosome<P> newChromosome(List<Double> chromosomeRepresentation) {
        return new RealValuedChromosome<P>(chromosomeRepresentation, getFitnessFunction(), getDecoder());
    }

    /**
     * Creates an instance of RealValued chromosome with randomly generated
     * representation.
     * @param <P>             phenotype of chromosome
     * @param length
     * @param fitnessFunction
     * @param decoder
     * @param minValue
     * @param maxValue
     * @return chromosome phenotype
     */
    public static <P> RealValuedChromosome<P> randomChromosome(int length, FitnessFunction<P> fitnessFunction,
            AbstractListChromosomeDecoder<Double, P> decoder, double minValue, double maxValue) {
        return new RealValuedChromosome<P>(
                ChromosomeRepresentationUtils.randomDoubleRepresentation(length, minValue, maxValue), fitnessFunction,
                decoder);
    }

}
