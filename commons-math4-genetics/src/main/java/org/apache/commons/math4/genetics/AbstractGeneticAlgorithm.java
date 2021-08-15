package org.apache.commons.math4.genetics;

import org.apache.commons.math4.genetics.operators.CrossoverPolicy;
import org.apache.commons.math4.genetics.operators.MutationPolicy;
import org.apache.commons.math4.genetics.operators.SelectionPolicy;
import org.apache.commons.math4.genetics.operators.StoppingCondition;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;

public abstract class AbstractGeneticAlgorithm {

	/** the crossover policy used by the algorithm. */
	private final CrossoverPolicy crossoverPolicy;

	/** the mutation policy used by the algorithm. */
	private final MutationPolicy mutationPolicy;

	/** the selection policy used by the algorithm. */
	private final SelectionPolicy selectionPolicy;

	/**
	 * Static random number generator shared by GA implementation classes. Use
	 * {@link #setRandomGenerator(UniformRandomProvider)} to supply an alternative
	 * to the default PRNG, and/or select a specific seed.
	 */
	// @GuardedBy("this")
	private static UniformRandomProvider randomGenerator = RandomSource.create(RandomSource.WELL_19937_C);

	/**
	 * the number of generations evolved to reach {@link StoppingCondition} in the
	 * last run.
	 */
	private int generationsEvolved = 0;

	public AbstractGeneticAlgorithm(final CrossoverPolicy crossoverPolicy, final MutationPolicy mutationPolicy,
			final SelectionPolicy selectionPolicy) {
		this.crossoverPolicy = crossoverPolicy;
		this.mutationPolicy = mutationPolicy;
		this.selectionPolicy = selectionPolicy;
	}

	/**
	 * Returns the crossover policy.
	 * 
	 * @return crossover policy
	 */
	public CrossoverPolicy getCrossoverPolicy() {
		return crossoverPolicy;
	}

	/**
	 * Returns the mutation policy.
	 * 
	 * @return mutation policy
	 */
	public MutationPolicy getMutationPolicy() {
		return mutationPolicy;
	}

	/**
	 * Returns the selection policy.
	 * 
	 * @return selection policy
	 */
	public SelectionPolicy getSelectionPolicy() {
		return selectionPolicy;
	}

	/**
	 * Set the (static) random generator.
	 *
	 * @param random random generator
	 */
	public static synchronized void setRandomGenerator(final UniformRandomProvider random) {
		randomGenerator = random;
	}

	public void resetGenerationsEvolved() {
		this.generationsEvolved = 0;
	}

	public void increaseGenerationsEvolved() {
		this.generationsEvolved++;
	}

	/**
	 * Returns the number of generations evolved to reach {@link StoppingCondition}
	 * in the last run.
	 *
	 * @return number of generations evolved
	 * @since 2.1
	 */
	public int getGenerationsEvolved() {
		return generationsEvolved;
	}

}
