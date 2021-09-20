package org.apache.commons.math4.genetics;

/**
 * This abstraction represents a chromosome.
 *
 * @param <P>   phenotype of chromosome
 */
public interface Chromosome<P> extends Comparable<Chromosome<P>> {

    /**
     * Access the fitness of this chromosome. The bigger the fitness, the better the
     * chromosome.
     * <p>
     * Computation of fitness is usually very time-consuming task, therefore the
     * fitness is cached.
     * @return the fitness
     */
    public double evaluate();

    /**
     * Decodes the chromosome genotype and returns the phenotype.
     * @return phenotype
     */
    public P decode();

}
