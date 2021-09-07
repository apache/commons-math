package org.apache.commons.math4.genetics.operators;

import org.apache.commons.math4.genetics.exception.GeneticException;
import org.apache.commons.math4.genetics.model.AbstractListChromosome;
import org.apache.commons.math4.genetics.model.Chromosome;
import org.apache.commons.math4.genetics.model.ChromosomePair;

public abstract class AbstractListChromosomeCrossoverPolicy<T> extends AbstractChromosomeCrossoverPolicy {

	/**
	 * Performs crossover of two chromosomes
	 *
	 * @throws GeneticException if the chromosomes are not an instance of
	 *                          {@link AbstractListChromosome}
	 * @throws GeneticException if the length of the two chromosomes is different
	 */
	@Override
	public ChromosomePair crossover(Chromosome first, Chromosome second) {

		if (!(first instanceof AbstractListChromosome<?> && second instanceof AbstractListChromosome<?>)) {
			throw new GeneticException(GeneticException.INVALID_FIXED_LENGTH_CHROMOSOME);
		}

		AbstractListChromosome<T> firstListChromosome = (AbstractListChromosome<T>) first;
		AbstractListChromosome<T> secondListChromosome = (AbstractListChromosome<T>) second;

		final int length = firstListChromosome.getLength();
		if (length != secondListChromosome.getLength()) {
			throw new GeneticException(GeneticException.SIZE_MISMATCH, secondListChromosome.getLength(), length);
		}

		return mate(firstListChromosome, secondListChromosome);
	}

	/**
	 * Performs mating between two chromosomes and returns the offspring pair.
	 * 
	 * @param first
	 * @param second
	 * @return chromosome pair
	 */
	protected abstract ChromosomePair mate(AbstractListChromosome<T> first, AbstractListChromosome<T> second);

}
