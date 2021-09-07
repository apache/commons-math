package org.apache.commons.math4.genetics.operators;

import org.apache.commons.math4.genetics.exception.GeneticException;
import org.apache.commons.math4.genetics.model.BinaryChromosome;
import org.apache.commons.math4.genetics.model.Chromosome;
import org.junit.Test;

public class AbstractListChromosomeCrossoverPolicyTest {

	@Test(expected = GeneticException.class)
	public void testCrossoverDimensionMismatchException() {
		final Integer[] p1 = new Integer[] { 1, 0, 1, 0, 0, 1, 0, 1, 1 };
		final Integer[] p2 = new Integer[] { 0, 1, 1, 0, 1 };

		final BinaryChromosome p1c = new BinaryChromosome(p1, (c) -> {
			return 0;
		});
		final BinaryChromosome p2c = new BinaryChromosome(p2, (c) -> {
			return 0;
		});

		final CrossoverPolicy cp = new CycleCrossover<Integer>();

		cp.crossover(p1c, p2c, 1.0);
	}

	@Test(expected = GeneticException.class)
	public void testCrossoverInvalidFixedLengthChromosomeFirst() {
		final Integer[] p1 = new Integer[] { 1, 0, 1, 0, 0, 1, 0, 1, 1 };
		final BinaryChromosome p1c = new BinaryChromosome(p1, (c) -> {
			return 0;
		});
		final Chromosome p2c = new Chromosome((c) -> {
			return 0;
		}) {
		};

		final CrossoverPolicy cp = new CycleCrossover<Integer>();
		cp.crossover(p1c, p2c, 1.0);
	}

	@Test(expected = GeneticException.class)
	public void testCrossoverInvalidFixedLengthChromosomeSecond() {
		final Integer[] p1 = new Integer[] { 1, 0, 1, 0, 0, 1, 0, 1, 1 };
		final BinaryChromosome p2c = new BinaryChromosome(p1, (c) -> {
			return 0;
		});
		final Chromosome p1c = new Chromosome((c) -> {
			return 0;
		}) {
		};

		final CrossoverPolicy cp = new CycleCrossover<Integer>();
		cp.crossover(p1c, p2c, 1.0);
	}

}