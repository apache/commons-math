package org.apache.commons.math4.genetics.stats;

import org.apache.commons.math4.genetics.model.Chromosome;

/**
 * This interface represents the statistical summary for population fitness.
 */
public interface PopulationStatisticalSummary {

	/**
	 * Returns the arithmetic mean of population fitness.
	 * 
	 * @return The mean or Double.NaN if no values have been added.
	 */
	double getMeanFitness();

	/**
	 * Returns the variance of the population fitness.
	 * 
	 * @return The variance, Double.NaN if no values have been added or 0.0 for a
	 *         single value set.
	 */
	double getFitnessVariance();

	/**
	 * Returns the minimum fitness of the population.
	 * 
	 * @return The max or Double.NaN if no values have been added.
	 */
	double getMinFitness();

	/**
	 * Returns the maximum fitness of the population.
	 * 
	 * @return The max or Double.NaN if no values have been added.
	 */
	double getMaxFitness();

	/**
	 * Returns the population size.
	 * 
	 * @return The number of available values
	 */
	long getPopulationSize();

	/**
	 * Calculates the rank of chromosome in population based on its fitness.
	 * 
	 * @param chromosome
	 * @return the rank of chromosome
	 */
	public int findRank(Chromosome chromosome);

}
