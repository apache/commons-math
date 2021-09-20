package org.apache.commons.math4.genetics.decoder;

import org.apache.commons.math4.genetics.Chromosome;

/**
 * Decoder is responsible for converting chromosome genotype to phenotype.
 * 
 * @param <P> phenotype of chromosome
 */
public interface Decoder<P> {

    /**
     * Converts genotype to phenotype.
     * @param chromosome
     * @return phenotype
     */
    P decode(Chromosome<P> chromosome);

}
