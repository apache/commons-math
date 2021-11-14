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
package org.apache.commons.math4.ga.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.math4.ga.internal.exception.GeneticException;
import org.apache.commons.rng.UniformRandomProvider;

/**
 * This interface generates all random representations for chromosomes.
 * @since 4.0
 */
public interface ChromosomeRepresentationUtils {

    /**
     * Generates a representation corresponding to a random permutation of length l
     * which can be passed to the RandomKey constructor.
     *
     * @param l length of the permutation
     * @return representation of a random permutation
     */
    static List<Double> randomPermutation(final int l) {
        final UniformRandomProvider randomProvider = RandomProviderManager.getRandomProvider();
        final List<Double> repr = new ArrayList<>(l);
        for (int i = 0; i < l; i++) {
            repr.add(randomProvider.nextDouble());
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
    static List<Double> identityPermutation(final int l) {
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
    static <S> List<Double> comparatorPermutation(final List<S> data, final Comparator<S> comparator) {
        final List<S> sortedData = new ArrayList<>(data);
        Collections.sort(sortedData, comparator);

        return inducedPermutation(data, sortedData);
    }

    /**
     * Generates a representation of a permutation corresponding to a permutation
     * which yields <code>permutedData</code> when applied to
     * <code>originalData</code>.
     *
     * This method can be viewed as an inverse to decode().
     *
     * @param <S>          type of the data
     * @param originalData the original, unpermuted data
     * @param permutedData the data, somehow permuted
     * @return representation of a permutation corresponding to the permutation
     *         {@code originalData -> permutedData}
     */
    static <S> List<Double> inducedPermutation(final List<S> originalData, final List<S> permutedData) {

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

    /**
     * Returns a representation of a random binary array of length
     * <code>length</code>.
     * @param length length of the array
     * @param min    minimum inclusive value of allele
     * @param max    maximum exclusive value of allele
     * @return a random binary array of length <code>length</code>
     */
    static List<Integer> randomIntegralRepresentation(final int length, final int min, final int max) {
        final UniformRandomProvider randomProvider = RandomProviderManager.getRandomProvider();
        final List<Integer> rList = new ArrayList<>(length);
        for (int j = 0; j < length; j++) {
            rList.add(min + randomProvider.nextInt(max - min));
        }
        return rList;
    }

    /**
     * Returns a representation of a random binary array of length
     * <code>length</code>.
     * @param length length of the array
     * @return a random binary array of length <code>length</code>
     */
    static List<Integer> randomBinaryRepresentation(final int length) {
        final UniformRandomProvider randomProvider = RandomProviderManager.getRandomProvider();
        // random binary list
        final List<Integer> rList = new ArrayList<>(length);
        for (int j = 0; j < length; j++) {
            rList.add(randomProvider.nextInt(2));
        }
        return rList;
    }

    /**
     * Generates a representation corresponding to a random double values[0..1] of
     * length l.
     * @param l length of the permutation
     * @return representation of a random permutation
     */
    static List<Double> randomNormalizedDoubleRepresentation(final int l) {
        return randomDoubleRepresentation(l, 0, 1);
    }

    /**
     * Generates a representation corresponding to a random double values of length
     * l.
     * @param l   length of representation
     * @param min minimum inclusive value of chromosome gene
     * @param max maximum exclusive value of chromosome gene
     * @return representation as List of Double
     */
    static List<Double> randomDoubleRepresentation(final int l, double min, double max) {
        if (min >= max) {
            throw new GeneticException(GeneticException.TOO_LARGE, min, max);
        }
        final double range = max - min;
        final UniformRandomProvider randomProvider = RandomProviderManager.getRandomProvider();
        final List<Double> repr = new ArrayList<>(l);
        for (int i = 0; i < l; i++) {
            repr.add(min + randomProvider.nextDouble() * range);
        }
        return repr;
    }

}
