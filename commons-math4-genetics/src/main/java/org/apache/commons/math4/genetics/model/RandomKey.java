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
package org.apache.commons.math4.genetics.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.math4.genetics.exception.GeneticException;
import org.apache.commons.math4.genetics.utils.Constants;
import org.apache.commons.math4.genetics.utils.RandomGenerator;

/**
 * Random Key chromosome is used for permutation representation. It is a vector
 * of a fixed length of real numbers in [0,1] interval. The index of the i-th
 * smallest value in the vector represents an i-th member of the permutation.
 * <p>
 * For example, the random key [0.2, 0.3, 0.8, 0.1] corresponds to the
 * permutation of indices (3,0,1,2). If the original (unpermuted) sequence would
 * be (a,b,c,d), this would mean the sequence (d,a,b,c).
 * <p>
 * With this representation, common operators like n-point crossover can be
 * used, because any such chromosome represents a valid permutation.
 * <p>
 * Since the chromosome (and thus its arrayRepresentation) is immutable, the
 * array representation is sorted only once in the constructor.
 * <p>
 * For details, see:
 * <ul>
 * <li>Bean, J.C.: Genetic algorithms and random keys for sequencing and
 * optimization. ORSA Journal on Computing 6 (1994) 154-160</li>
 * <li>Rothlauf, F.: Representations for Genetic and Evolutionary Algorithms.
 * Volume 104 of Studies in Fuzziness and Soft Computing. Physica-Verlag,
 * Heidelberg (2002)</li>
 * </ul>
 *
 * @param <T> type of the permuted objects
 * @since 2.0
 */
public class RandomKey<T> extends AbstractListChromosome<Double> implements PermutationChromosome<T> {

    /** Cache of sorted representation (unmodifiable). */
    private final List<Double> sortedRepresentation;

    /**
     * Base sequence [0,1,...,n-1], permuted according to the representation
     * (unmodifiable).
     */
    private final List<Integer> baseSeqPermutation;

    /**
     * Constructor.
     * @param representation list of [0,1] values representing the permutation
     * @param fitnessFunction
     * @throws GeneticException iff the <code>representation</code> can not
     *                          represent a valid chromosome
     */
    public RandomKey(final List<Double> representation, FitnessFunction fitnessFunction) {
        super(representation, fitnessFunction);
        // store the sorted representation
        final List<Double> sortedRepr = new ArrayList<>(getRepresentation());
        Collections.sort(sortedRepr);
        sortedRepresentation = Collections.unmodifiableList(sortedRepr);
        // store the permutation of [0,1,...,n-1] list for toString() and isSame()
        // methods
        baseSeqPermutation = Collections
                .unmodifiableList(decodeGeneric(baseSequence(getLength()), getRepresentation(), sortedRepresentation));
    }

    /**
     * Constructor.
     * @param representation array of [0,1] values representing the permutation
     * @param fitnessFunction
     * @throws GeneticException iff the <code>representation</code> can not
     *                          represent a valid chromosome
     */
    public RandomKey(final Double[] representation, FitnessFunction fitnessFunction) {
        this(Arrays.asList(representation), fitnessFunction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> decode(final List<T> sequence) {
        return decodeGeneric(sequence, getRepresentation(), sortedRepresentation);
    }

    /**
     * Decodes a permutation represented by <code>representation</code> and returns
     * a (generic) list with the permuted values.
     *
     * @param <S>            generic type of the sequence values
     * @param sequence       the unpermuted sequence
     * @param representation representation of the permutation ([0,1] vector)
     * @param sortedRepr     sorted <code>representation</code>
     * @return list with the sequence values permuted according to the
     *         representation
     * @throws GeneticException iff the length of the <code>sequence</code>,
     *                          <code>representation</code> or
     *                          <code>sortedRepr</code> lists are not equal
     */
    private static <S> List<S> decodeGeneric(final List<S> sequence, List<Double> representation,
            final List<Double> sortedRepr) {

        final int l = sequence.size();

        // the size of the three lists must be equal
        if (representation.size() != l) {
            throw new GeneticException(GeneticException.SIZE_MISMATCH, representation.size(), l);
        }
        if (sortedRepr.size() != l) {
            throw new GeneticException(GeneticException.SIZE_MISMATCH, sortedRepr.size(), l);
        }

        // do not modify the original representation
        final List<Double> reprCopy = new ArrayList<>(representation);

        // now find the indices in the original repr and use them for permuting
        final List<S> res = new ArrayList<>(l);
        for (int i = 0; i < l; i++) {
            final int index = reprCopy.indexOf(sortedRepr.get(i));
            res.add(sequence.get(index));
            reprCopy.set(index, null);
        }
        return res;
    }

    /**
     * Returns <code>true</code> iff <code>another</code> is a RandomKey and encodes
     * the same permutation.
     *
     * @param another chromosome to compare
     * @return true iff chromosomes encode the same permutation
     */
    @Override
    public boolean isSame(final Chromosome another) {
        // type check
        if (!(another instanceof RandomKey<?>)) {
            return false;
        }
        final RandomKey<?> anotherRk = (RandomKey<?>) another;
        // size check
        if (getLength() != anotherRk.getLength()) {
            return false;
        }

        // two different representations can still encode the same permutation
        // the ordering is what counts
        final List<Integer> thisPerm = this.baseSeqPermutation;
        final List<Integer> anotherPerm = anotherRk.baseSeqPermutation;

        for (int i = 0; i < getLength(); i++) {
            if (thisPerm.get(i) != anotherPerm.get(i)) {
                return false;
            }
        }
        // the permutations are the same
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkValidity(final List<Double> chromosomeRepresentation) {

        for (double val : chromosomeRepresentation) {
            if (val < 0 || val > 1) {
                throw new GeneticException(GeneticException.OUT_OF_RANGE, val, Constants.ALLELE_VALUE, 0, 1);
            }
        }
    }

    /**
     * Generates a representation corresponding to a random permutation of length l
     * which can be passed to the RandomKey constructor.
     *
     * @param l length of the permutation
     * @return representation of a random permutation
     */
    public static final List<Double> randomPermutation(final int l) {
        final List<Double> repr = new ArrayList<>(l);
        for (int i = 0; i < l; i++) {
            repr.add(RandomGenerator.getRandomGenerator().nextDouble());
        }
        return repr;
    }

    /**
     * Generates a representation corresponding to an identity permutation of length
     * l which can be passed to the RandomKey constructor.
     *
     * @param l length of the permutation
     * @return representation of an identity permutation
     */
    public static final List<Double> identityPermutation(final int l) {
        final List<Double> repr = new ArrayList<>(l);
        for (int i = 0; i < l; i++) {
            repr.add((double) i / l);
        }
        return repr;
    }

    /**
     * Generates a representation of a permutation corresponding to the
     * <code>data</code> sorted by <code>comparator</code>. The <code>data</code> is
     * not modified during the process.
     *
     * This is useful if you want to inject some permutations to the initial
     * population.
     *
     * @param <S>        type of the data
     * @param data       list of data determining the order
     * @param comparator how the data will be compared
     * @return list representation of the permutation corresponding to the
     *         parameters
     */
    public static <S> List<Double> comparatorPermutation(final List<S> data, final Comparator<S> comparator) {
        final List<S> sortedData = new ArrayList<>(data);
        Collections.sort(sortedData, comparator);

        return inducedPermutation(data, sortedData);
    }

    /**
     * Generates a representation of a permutation corresponding to a permutation
     * which yields <code>permutedData</code> when applied to
     * <code>originalData</code>.
     *
     * This method can be viewed as an inverse to {@link #decode(List)}.
     *
     * @param <S>          type of the data
     * @param originalData the original, unpermuted data
     * @param permutedData the data, somehow permuted
     * @return representation of a permutation corresponding to the permutation
     *         {@code originalData -> permutedData}
     * @throws GeneticException iff the length of <code>originalData</code> and
     *                          <code>permutedData</code> lists are not equal
     * @throws GeneticException iff the <code>permutedData</code> and
     *                          <code>originalData</code> lists contain different
     *                          data
     */
    public static <S> List<Double> inducedPermutation(final List<S> originalData, final List<S> permutedData) {

        if (originalData.size() != permutedData.size()) {
            throw new GeneticException(GeneticException.SIZE_MISMATCH, permutedData.size(), originalData.size());
        }
        final int l = originalData.size();

        final List<S> origDataCopy = new ArrayList<>(originalData);

        final Double[] res = new Double[l];
        for (int i = 0; i < l; i++) {
            final int index = origDataCopy.indexOf(permutedData.get(i));
            if (index == -1) {
                throw new GeneticException(GeneticException.DIFFERENT_ORIG_AND_PERMUTED_DATA);
            }
            res[index] = (double) i / l;
            origDataCopy.set(index, null);
        }
        return Arrays.asList(res);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format("(f=%s pi=(%s))", getFitness(), baseSeqPermutation);
    }

    /**
     * Helper for constructor. Generates a list of natural numbers (0,1,...,l-1).
     *
     * @param l length of list to generate
     * @return list of integers from 0 to l-1
     */
    private static List<Integer> baseSequence(final int l) {
        final List<Integer> baseSequence = new ArrayList<>(l);
        for (int i = 0; i < l; i++) {
            baseSequence.add(i);
        }
        return baseSequence;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RandomKey<T> newChromosome(List<Double> chromosomeRepresentation) {
        return new RandomKey<T>(chromosomeRepresentation, getFitnessFunction());
    }

    /**
     * Creates an instance of RandomKey chromosome with randomly generated
     * representation.
     * @param <T> type
     * @param length
     * @param fitnessFunction
     * @return instance of RandomKey
     */
    public static <T> RandomKey<T> randomChromosome(int length, FitnessFunction fitnessFunction) {
        return new RandomKey<T>(randomPermutation(length), fitnessFunction);
    }

}
