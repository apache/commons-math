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

import java.util.List;

import java.util.Objects;

import org.apache.commons.math4.ga.decoder.Decoder;
import org.apache.commons.math4.ga.fitness.FitnessFunction;
import org.apache.commons.math4.ga.internal.exception.GeneticException;
import org.apache.commons.math4.ga.utils.ChromosomeRepresentationUtils;

/**
 * BinaryChromosome represented by a vector of 0s and 1s.
 * @param <P> phenotype of chromosome
 * @since 4.0
 */
public class BinaryChromosome<P> extends AbstractChromosome<P> {

    /**
     * maximum allowed length of binary chromosome.
     */
    public static final long MAX_LENGTH = Integer.MAX_VALUE;

    /**
     * length of binary chromosome.
     */
    private final long length;

    /**
     * binary representation of chromosome.
     */
    private final long[] representation;

    /**
     * @param representation  Internal representation of chromosome.
     * @param length          length of chromosome
     * @param fitnessFunction The {@link FitnessFunction}
     * @param decoder         The {@link Decoder}
     */
    public BinaryChromosome(List<Long> representation,
            long length,
            FitnessFunction<P> fitnessFunction,
            Decoder<P> decoder) {
        super(fitnessFunction, decoder);
        Objects.requireNonNull(representation);
        checkMaximumLength(length);
        this.length = length;
        this.representation = new long[representation.size()];
        for (int i = 0; i < representation.size(); i++) {
            this.representation[i] = representation.get(i);
        }
    }

    /**
     * @param representation  Internal representation of chromosome.
     * @param length          length of chromosome
     * @param fitnessFunction The {@link FitnessFunction}
     * @param decoder         The {@link Decoder}
     */
    public BinaryChromosome(Long[] representation,
            long length,
            FitnessFunction<P> fitnessFunction,
            Decoder<P> decoder) {
        super(fitnessFunction, decoder);
        Objects.requireNonNull(representation);
        checkMaximumLength(length);
        this.length = length;
        this.representation = new long[representation.length];
        for (int i = 0; i < representation.length; i++) {
            this.representation[i] = representation[i];
        }
    }

    /**
     * @param inputRepresentation Internal representation of chromosome.
     * @param length              length of chromosome
     * @param fitnessFunction     The {@link FitnessFunction}
     * @param decoder             The {@link Decoder}
     */
    public BinaryChromosome(long[] inputRepresentation,
            long length,
            FitnessFunction<P> fitnessFunction,
            Decoder<P> decoder) {
        super(fitnessFunction, decoder);
        Objects.requireNonNull(inputRepresentation);
        checkMaximumLength(length);
        if (length <= (inputRepresentation.length - 1) * Long.SIZE || length > inputRepresentation.length * Long.SIZE) {
            throw new GeneticException(GeneticException.ILLEGAL_ARGUMENT,
                    "provided length does not match expected representation");
        }
        this.length = length;
        this.representation = new long[inputRepresentation.length];
        System.arraycopy(inputRepresentation, 0, representation, 0, inputRepresentation.length);
    }

    /**
     * @param representation  Internal representation of chromosome.
     * @param fitnessFunction The {@link FitnessFunction}
     * @param decoder         The {@link Decoder}
     */
    public BinaryChromosome(String representation, FitnessFunction<P> fitnessFunction, Decoder<P> decoder) {
        super(fitnessFunction, decoder);
        Objects.requireNonNull(representation);
        this.length = representation.length();
        this.representation = encode(representation);
    }

    /**
     * Checks the input chromosome length against predefined maximum length.
     * @param chromosomeLength input chromsome length
     */
    protected void checkMaximumLength(long chromosomeLength) {
        if (chromosomeLength > MAX_LENGTH) {
            throw new GeneticException(GeneticException.ILLEGAL_ARGUMENT,
                    "length exceeded the max length " + MAX_LENGTH);
        }
    }

    /**
     * Validates the string representation.
     * @param stringRepresentation binary string representation of chromosome
     */
    private void validateStringRepresentation(String stringRepresentation) {
        char allele = '\0';
        final int chromosomeLength = stringRepresentation.length();
        for (int i = 0; i < chromosomeLength; i++) {
            if ((allele = stringRepresentation.charAt(i)) != '0' && allele != '1') {
                throw new GeneticException(GeneticException.ILLEGAL_ARGUMENT,
                        "Only 0 or 1 are acceptable as characters.");
            }
        }
    }

    /**
     * encodes the binary string representation as an array of long.
     * @param stringRepresentation binary string
     * @return encoded representation
     */
    private long[] encode(String stringRepresentation) {
        validateStringRepresentation(stringRepresentation);
        final int chromosomeLength = stringRepresentation.length();
        final int arraySize = (int) Math.ceil(chromosomeLength / (double) Long.SIZE);
        final long[] encodedRepresentation = new long[arraySize];
        final int offset = (int) (chromosomeLength % Long.SIZE == 0 ? 0 : Long.SIZE - chromosomeLength % Long.SIZE);
        encodedRepresentation[0] = Long.parseUnsignedLong(stringRepresentation.substring(0, Long.SIZE - offset), 2);
        for (int i = Long.SIZE - offset, j = 1; i < chromosomeLength; i += Long.SIZE, j++) {
            encodedRepresentation[j] = Long.parseUnsignedLong(stringRepresentation.substring(i, i + Long.SIZE), 2);
        }
        return encodedRepresentation;
    }

    /**
     * Returns the chromosome length.
     * @return length
     */
    public long getLength() {
        return length;
    }

    /**
     * Returns the binary representation.
     * @return representation
     */
    public long[] getRepresentation() {
        final long[] clonedRepresentation = new long[representation.length];
        System.arraycopy(representation, 0, clonedRepresentation, 0, representation.length);
        return clonedRepresentation;
    }

    /**
     * Returns the binary string representation of the chromosome.
     * @return the string representation
     */
    public String getStringRepresentation() {
        if (length > Integer.MAX_VALUE) {
            throw new GeneticException(GeneticException.LENGTH_TOO_LARGE, length);
        }
        return getStringRepresentation(0, length);
    }

    /**
     * Returns the binary string representation of the chromosome alleles from
     * start(inclusive) to end(exclusive) index.
     * @param start start allele/gene index(inclusive)
     * @param end   end allele/gene index(exclusive)
     * @return the string representation
     */
    public String getStringRepresentation(long start, long end) {
        if (start >= end) {
            throw new GeneticException(GeneticException.ILLEGAL_ARGUMENT,
                    "start " + start + " is greater than end " + end);
        }
        if (end - start > Integer.MAX_VALUE) {
            throw new GeneticException(GeneticException.LENGTH_TOO_LARGE, end - start);
        }
        final int offset = (int) (length % Long.SIZE == 0 ? 0 : Long.SIZE - length % Long.SIZE);
        final long offsettedStart = offset + start;
        final long offsettedEnd = offset + end;
        final int startAlleleBlockIndex = (int) (offsettedStart / Long.SIZE);
        final int endAlleleBlockIndex = (int) (offsettedEnd / Long.SIZE);
        final int startAlleleElementIndex = (int) (offsettedStart % Long.SIZE);
        final int endAlleleElementIndex = (int) (offsettedEnd % Long.SIZE);

        if (startAlleleBlockIndex == endAlleleBlockIndex) {
            return getAlleleBlockString(startAlleleBlockIndex).substring(startAlleleElementIndex,
                    endAlleleElementIndex);
        } else {
            final StringBuilder allelesStrRepresentation = new StringBuilder();

            // extract string representation of first allele block.
            allelesStrRepresentation
                    .append(getAlleleBlockString(startAlleleBlockIndex).substring(startAlleleElementIndex));

            // extract string representation of all allele blocks except first and last.
            for (int i = startAlleleBlockIndex + 1; i < endAlleleBlockIndex; i++) {
                allelesStrRepresentation.append(getAlleleBlockString(i));
            }

            // extract string representation from last allele block if end allele index !=
            // 0.
            if (endAlleleElementIndex != 0) {
                allelesStrRepresentation
                        .append(getAlleleBlockString(endAlleleBlockIndex).substring(0, endAlleleElementIndex));
            }

            return allelesStrRepresentation.toString();
        }
    }

    /**
     * Returns the allele block as binary string representation.
     * @param alleleBlockIndex index of allele block i.e. array element
     * @return the string representation
     */
    private String getAlleleBlockString(final int alleleBlockIndex) {
        return prepareZeroPrefix(representation[alleleBlockIndex] == 0 ? Long.SIZE - 1 :
                Long.numberOfLeadingZeros(representation[alleleBlockIndex])) +
                Long.toUnsignedString(representation[alleleBlockIndex], 2);
    }

    /**
     * Prepares zero prefix for binary chromosome.
     * @param count number of zeros
     * @return prefix
     */
    private String prepareZeroPrefix(int count) {
        final StringBuilder zeroPrefix = new StringBuilder();
        for (int i = 0; i < count; i++) {
            zeroPrefix.append('0');
        }
        return zeroPrefix.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FitnessFunction<P> getFitnessFunction() {
        return super.getFitnessFunction();
    }

    /**
     * Creates a new chromosome with provided parameters.
     * @param chromosomeRepresentation the representation
     * @param chromosomeLength         length of chromosome
     * @return chromosome
     */
    public BinaryChromosome<P> newChromosome(long[] chromosomeRepresentation, long chromosomeLength) {
        return new BinaryChromosome<P>(chromosomeRepresentation, chromosomeLength, getFitnessFunction(), getDecoder());
    }

    /**
     * Creates an instance of Binary Chromosome with random binary representation.
     * @param <P>             phenotype fo chromosome
     * @param length          length of chromosome
     * @param fitnessFunction The {@link FitnessFunction}
     * @param decoder         The {@link Decoder}
     * @return a binary chromosome
     */
    public static <P> BinaryChromosome<P> randomChromosome(int length,
            FitnessFunction<P> fitnessFunction,
            Decoder<P> decoder) {
        return new BinaryChromosome<P>(ChromosomeRepresentationUtils.randomBinaryRepresentation(length), length,
                fitnessFunction, decoder);
    }

}
