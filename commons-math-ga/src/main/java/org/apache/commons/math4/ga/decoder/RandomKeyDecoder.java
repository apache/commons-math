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
package org.apache.commons.math4.ga.decoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.math4.ga.chromosome.AbstractListChromosome;
import org.apache.commons.math4.ga.internal.exception.GeneticException;

/**
 * A concrete implementation of RandomKey decoder. This class is responsible for
 * decoding permutation chromosome encoded with random key.
 * @param <U> type of the permutation element
 * @since 4.0
 */
public final class RandomKeyDecoder<U> extends AbstractListChromosomeDecoder<Double, List<U>> {

    /** base sequence for decoding chromosome. **/
    private final List<U> baseSequence;

    /**
     * @param baseSequence the unpermuted sequence
     */
    public RandomKeyDecoder(List<U> baseSequence) {
        this.baseSequence = Collections.unmodifiableList(Objects.requireNonNull(baseSequence));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<U> decode(AbstractListChromosome<Double, List<U>> chromosome) {
        final List<Double> representation = chromosome.getRepresentation();
        final List<Double> sortedRepresentation = new ArrayList<>(representation);
        Collections.sort(sortedRepresentation);

        final int sequenceLength = baseSequence.size();

        // the size of the three lists must be equal
        if (representation.size() != sequenceLength) {
            throw new GeneticException(GeneticException.SIZE_MISMATCH, representation.size(), sequenceLength);
        }

        // do not modify the original representation
        final List<Double> representationCopy = new ArrayList<>(representation);

        // now find the indices in the original repr and use them for permuting
        final List<U> res = new ArrayList<>(sequenceLength);
        for (int i = 0; i < sequenceLength; i++) {
            final int index = representationCopy.indexOf(sortedRepresentation.get(i));
            res.add(baseSequence.get(index));
            representationCopy.set(index, null);
        }

        return res;
    }

}
