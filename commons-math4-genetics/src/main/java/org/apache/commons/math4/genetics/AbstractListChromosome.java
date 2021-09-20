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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math4.genetics.decoder.AbstractListChromosomeDecoder;
import org.apache.commons.math4.genetics.utils.ValidationUtils;

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
     * constructor.
     * @param representation
     * @param fitnessFunction
     * @param decoder
     */
    public AbstractListChromosome(final List<T> representation, final FitnessFunction<P> fitnessFunction,
            final AbstractListChromosomeDecoder<T, P> decoder) {
        this(representation, true, fitnessFunction, decoder);
    }

    /**
     * constructor.
     * @param representation
     * @param fitnessFunction
     * @param decoder
     */
    public AbstractListChromosome(final T[] representation, FitnessFunction<P> fitnessFunction,
            AbstractListChromosomeDecoder<T, P> decoder) {
        this(Arrays.asList(representation), fitnessFunction, decoder);
    }

    /**
     * constructor.
     * @param representation
     * @param copyList
     * @param fitnessFunction
     * @param decoder
     */
    public AbstractListChromosome(final List<T> representation, final boolean copyList,
            final FitnessFunction<P> fitnessFunction, final AbstractListChromosomeDecoder<T, P> decoder) {
        super(fitnessFunction, decoder);
        ValidationUtils.checkForNull("representation", representation);
        checkValidity(representation);
        this.representation = Collections.unmodifiableList(copyList ? new ArrayList<>(representation) : representation);
    }

    /**
     * Asserts that <code>representation</code> can represent a valid chromosome.
     * @param chromosomeRepresentation representation of the chromosome
     */
    protected abstract void checkValidity(List<T> chromosomeRepresentation);

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
