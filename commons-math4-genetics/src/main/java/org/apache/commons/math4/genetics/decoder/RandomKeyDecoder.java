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
package org.apache.commons.math4.genetics.decoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math4.genetics.chromosome.AbstractListChromosome;
import org.apache.commons.math4.genetics.exception.GeneticException;
import org.apache.commons.math4.genetics.utils.ValidationUtils;

/**
 * A concrete implementation of RandomKey decoder. This class is responsible for
 * decoding permutation chromosome encoded with random key.
 * @param <U> type of the permutation element
 */
public final class RandomKeyDecoder<U> extends AbstractListChromosomeDecoder<Double, List<U>> {

    /** base sequence for decoding chromosome. **/
    private final List<U> baseSequence;

    /**
     * constructor.
     * @param baseSequence the unpermuted sequence
     */
    public RandomKeyDecoder(List<U> baseSequence) {
        ValidationUtils.checkForNull("baseSequence", baseSequence);
        this.baseSequence = Collections.unmodifiableList(baseSequence);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<U> decode(AbstractListChromosome<Double, List<U>> chromosome) {
        final List<Double> representation = chromosome.getRepresentation();
        final List<Double> sortedRepresentation = new ArrayList<>(representation);
        Collections.sort(sortedRepresentation);

        final int l = baseSequence.size();

        // the size of the three lists must be equal
        if (representation.size() != l) {
            throw new GeneticException(GeneticException.SIZE_MISMATCH, representation.size(), l);
        }

        // do not modify the original representation
        final List<Double> representationCopy = new ArrayList<>(representation);

        // now find the indices in the original repr and use them for permuting
        final List<U> res = new ArrayList<>(l);
        for (int i = 0; i < l; i++) {
            final int index = representationCopy.indexOf(sortedRepresentation.get(i));
            res.add(baseSequence.get(index));
            representationCopy.set(index, null);
        }

        return res;
    }

}
