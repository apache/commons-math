package org.apache.commons.math4.genetics;

import java.util.List;

import org.apache.commons.math4.genetics.listeners.ConvergenceListener;
import org.apache.commons.math4.genetics.listeners.ConvergenceListenerRegistry;
import org.apache.commons.math4.genetics.model.Population;
import org.apache.commons.math4.genetics.operators.CrossoverPolicy;
import org.apache.commons.math4.genetics.operators.MutationPolicy;
import org.apache.commons.math4.genetics.operators.SelectionPolicy;
import org.apache.commons.math4.genetics.operators.StoppingCondition;
import org.apache.commons.math4.genetics.stats.PopulationStatisticalSummary;
import org.apache.commons.math4.genetics.stats.internal.PopulationStatisticalSummaryImpl;

public abstract class AbstractGeneticAlgorithm {

	/** the crossover policy used by the algorithm. */
	private final CrossoverPolicy crossoverPolicy;

	/** the mutation policy used by the algorithm. */
	private final MutationPolicy mutationPolicy;

	/** the selection policy used by the algorithm. */
	private final SelectionPolicy selectionPolicy;

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
	 * Returns the number of generations evolved to reach {@link StoppingCondition}
	 * in the last run.
	 *
	 * @return number of generations evolved
	 * @since 2.1
	 */
	public int getGenerationsEvolved() {
		return generationsEvolved;
	}

	/**
	 * Evolve the given population. Evolution stops when the stopping condition is
	 * satisfied. Updates the {@link #getGenerationsEvolved() generationsEvolved}
	 * property with the number of generations evolved before the StoppingCondition
	 * is satisfied.
	 *
	 * @param initial   the initial, seed population.
	 * @param condition the stopping condition used to stop evolution.
	 * @return the population that satisfies the stopping condition.
	 */
	public Population evolve(final Population initial, final StoppingCondition condition) {
		Population current = initial;
		PopulationStatisticalSummary populationStats = new PopulationStatisticalSummaryImpl(current);
		ConvergenceListenerRegistry.getInstance().notifyAll(populationStats);

		while (!condition.isSatisfied(populationStats)) {
			current = nextGeneration(current, populationStats);
			this.generationsEvolved++;
		}

		return current;
	}

	/**
	 * Evolve the given population into the next generation.
	 * <ol>
	 * <li>Get nextGeneration population to fill from <code>current</code>
	 * generation, using its nextGeneration method</li>
	 * <li>Loop until new generation is filled:
	 * <ul>
	 * <li>Apply configured SelectionPolicy to select a pair of parents from
	 * <code>current</code></li>
	 * <li>With probability = {@link #getCrossoverRate()}, apply configured
	 * {@link CrossoverPolicy} to parents</li>
	 * <li>With probability = {@link #getMutationRate()}, apply configured
	 * {@link MutationPolicy} to each of the offspring</li>
	 * <li>Add offspring individually to nextGeneration, space permitting</li>
	 * </ul>
	 * </li>
	 * <li>Return nextGeneration</li>
	 * </ol>
	 *
	 * @param current         the current population
	 * @param populationStats the statistical summary of the population
	 * @return the population for the next generation.
	 */
	protected abstract Population nextGeneration(final Population current,
			PopulationStatisticalSummary populationStats);

}
