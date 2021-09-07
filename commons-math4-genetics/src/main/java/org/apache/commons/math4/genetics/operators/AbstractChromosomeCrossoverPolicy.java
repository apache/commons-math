package org.apache.commons.math4.genetics.operators;

import org.apache.commons.math4.genetics.model.Chromosome;
import org.apache.commons.math4.genetics.model.ChromosomePair;
import org.apache.commons.math4.genetics.utils.RandomGenerator;

public abstract class AbstractChromosomeCrossoverPolicy implements CrossoverPolicy {

	@Override
	public ChromosomePair crossover(Chromosome first, Chromosome second, double crossoverRate) {
		if (RandomGenerator.getRandomGenerator().nextDouble() < crossoverRate) {
			return crossover(first, second);
		} else {
			return new ChromosomePair(first, second);
		}
	}

	/**
	 * Performs crossover of two chromosomes.
	 * 
	 * @param first
	 * @param second
	 * @return chromosome pair
	 */
	protected abstract ChromosomePair crossover(Chromosome first, Chromosome second);

}
