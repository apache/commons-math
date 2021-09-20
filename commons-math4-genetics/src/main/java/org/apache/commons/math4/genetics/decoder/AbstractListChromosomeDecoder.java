package org.apache.commons.math4.genetics.decoder;

import org.apache.commons.math4.genetics.AbstractListChromosome;
import org.apache.commons.math4.genetics.Chromosome;
import org.apache.commons.math4.genetics.exception.GeneticException;

/**
 * An abstract Decoder of ListChromosome.
 * 
 * @param <T>   genotype fo chromosome
 * @param <P>   phenotype of chromosome
 */
public abstract class AbstractListChromosomeDecoder<T, P> implements Decoder<P> {

    public P decode(Chromosome<P> chromosome) {
        if (!AbstractListChromosome.class.isAssignableFrom(chromosome.getClass())) {
            throw new GeneticException(GeneticException.ILLEGAL_ARGUMENT, chromosome.getClass().getSimpleName());
        }
        @SuppressWarnings("unchecked")
        AbstractListChromosome<T, P> listChromosome = (AbstractListChromosome<T, P>) chromosome;

        return decode(listChromosome);
    }

    protected abstract P decode(AbstractListChromosome<T, P> chromosome);

}
