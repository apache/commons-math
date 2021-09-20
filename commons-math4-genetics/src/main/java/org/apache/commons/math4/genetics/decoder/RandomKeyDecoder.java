package org.apache.commons.math4.genetics.decoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math4.genetics.AbstractListChromosome;
import org.apache.commons.math4.genetics.exception.GeneticException;
import org.apache.commons.math4.genetics.utils.ValidationUtils;

/**
 * A concrete implementation of RandomKey decoder. This class is responsible for
 * decoding permutation chromosome encoded with random key.
 * 
 * @param <U> type of the permutation element
 */
public final class RandomKeyDecoder<U> extends AbstractListChromosomeDecoder<Double, List<U>> {

    private List<U> baseSequence;

    public RandomKeyDecoder(List<U> baseSequence) {
        ValidationUtils.checkForNull("baseSequence", baseSequence);
        this.baseSequence = Collections.unmodifiableList(baseSequence);
    }

    @Override
    protected List<U> decode(AbstractListChromosome<Double, List<U>> chromosome) {
        List<Double> representation = chromosome.getRepresentation();
        List<Double> sortedRepresentation = new ArrayList<>(representation);
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
