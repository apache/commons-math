package org.apache.commons.math4.genetics.listeners;

import org.apache.commons.math4.genetics.stats.PopulationStatisticalSummary;

/**
 * This interface represents the convergence listener registry. It will be
 * responsible for registering the interested listeners and notifying all when
 * required.
 *
 */
public interface ConvergenceListenerRegistry {

	/**
	 * Registers the interested ConvergenceListener passed as an argument.
	 * 
	 * @param the convergence listener.
	 */
	public void addConvergenceListener(ConvergenceListener convergenceListener);

	/**
	 * Notifies all registered ConvergenceListeners about the population statistics.
	 * 
	 * @param population statistics
	 */
	public void notifyAll(PopulationStatisticalSummary populationStatisticalSummary);

}
