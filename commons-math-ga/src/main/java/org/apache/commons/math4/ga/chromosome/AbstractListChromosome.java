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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.math4.ga.decoder.AbstractListChromosomeDecoder;
import org.apache.commons.math4.ga.fitness.FitnessFunction;

/**
 * This class represents an abstract chromosome containing an immutable list of
 * allele/genes.
 * @param <T> type of the allele/gene in the representation list. T should be
 *            immutable.
 * @param <P> phenotype of chromosome
 * @since 2.0
 */
public abstract class AbstractListChromosome<T, P> extends AbstractChromosome<P> {

    /** List of allele/genes. */
    private final List<T> representation;

    /**
     * @param representation  The representation of chromosome genotype as
     *                        {@link List} of generic T
     * @param fitnessFunction The {@link FitnessFunction}
     * @param decoder         An instance of {@link AbstractListChromosomeDecoder},
     *                        to decode list chromosome.
     */
    protected AbstractListChromosome(final List<T> representation,
            final FitnessFunction<P> fitnessFunction,
            final AbstractListChromosomeDecoder<T, P> decoder) {
        this(representation, true, fitnessFunction, decoder);
    }

    /**
     * @param representation  The representation of chromosome genotype as an array
     *                        of generic T
     * @param fitnessFunction The {@link FitnessFunction}
     * @param decoder         An instance of {@link AbstractListChromosomeDecoder},
     *                        to decode list chromosome.
     */
    protected AbstractListChromosome(final T[] representation,
            FitnessFunction<P> fitnessFunction,
            AbstractListChromosomeDecoder<T, P> decoder) {
        this(Arrays.asList(representation), fitnessFunction, decoder);
    }

    /**
     * @param representation  Internal representation of chromosome genotype as an
     *                        array of generic T
     * @param copyList        if {@code true}, the representation will be copied,
     *                        otherwise it will be referenced.
     * @param fitnessFunction The {@link FitnessFunction}
     * @param decoder         The instance of {@link AbstractListChromosomeDecoder}
     */
    protected AbstractListChromosome(final List<T> representation,
            final boolean copyList,
            final FitnessFunction<P> fitnessFunction,
            final AbstractListChromosomeDecoder<T, P> decoder) {
        super(fitnessFunction, decoder);
        Objects.requireNonNull(representation);
        this.representation = Collections.unmodifiableList(copyList ? new ArrayList<>(representation) : representation);
    }

    /**
     * Returns the (immutable) inner representation of the chromosome.
     * @return the representation of the chromosome
     */
    public List<T> getRepresentation() {
        return representation;
    }

    /**
     * Returns the length of the chromosome.
     * @return the length of the chromosome
     */
    public int getLength() {
        return getRepresentation().size();
    }

    /**
     * returns the decoder.
     * @return decoder
     */
    @SuppressWarnings("unchecked")
    @Override
    protected AbstractListChromosomeDecoder<T, P> getDecoder() {
        return (AbstractListChromosomeDecoder<T, P>) super.getDecoder();
    }

    /**
     * Creates a new instance of the same class as <code>this</code> is, with a
     * given <code>arrayRepresentation</code>. This is needed in crossover and
     * mutation operators, where we need a new instance of the same class, but with
     * different array representation.
     * <p>
     * Usually, this method just calls a constructor of the class.
     *
     * @param chromosomeRepresentation the inner array representation of the new
     *                                 chromosome.
     * @return new instance extended from FixedLengthChromosome with the given
     *         arrayRepresentation
     */
    public abstract AbstractListChromosome<T, P> newChromosome(List<T> chromosomeRepresentation);

}
