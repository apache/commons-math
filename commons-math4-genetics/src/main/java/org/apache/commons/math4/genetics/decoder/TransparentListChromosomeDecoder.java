package org.apache.commons.math4.genetics.decoder;

import java.util.List;

import org.apache.commons.math4.genetics.AbstractListChromosome;

/**
 * A concrete implementation of transparent decoder for List Chromosome. Treats
 * the gentype as phenotype.
 *
 * @param <T> the genotype of chromosome
 */
public final class TransparentListChromosomeDecoder<T> extends AbstractListChromosomeDecoder<T, List<T>> {

    @Override
    protected List<T> decode(AbstractListChromosome<T, List<T>> chromosome) {
        return chromosome.getRepresentation();
    }

}
