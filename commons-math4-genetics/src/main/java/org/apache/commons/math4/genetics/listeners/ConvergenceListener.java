package org.apache.commons.math4.genetics.listeners;

import org.apache.commons.math4.genetics.stats.PopulationStatisticalSummary;

/**
 * This interface represents a convergence listener. Any implementation of the
 * same will be notified about the population statics.
 *
 */
public interface ConvergenceListener {

	/**
	 * Notifies about the population statistics.
	 * 
	 * @param populationStatisticalSummary
	 */
	public void notify(PopulationStatisticalSummary populationStatisticalSummary);

}
