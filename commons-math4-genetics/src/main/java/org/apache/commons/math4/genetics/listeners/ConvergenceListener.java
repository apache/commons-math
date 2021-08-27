package org.apache.commons.math4.genetics.listeners;

import org.apache.commons.math4.genetics.model.Population;

/**
 * This interface represents a convergence listener. Any implementation of the
 * same will be notified about the population statics.
 *
 */
public interface ConvergenceListener {

	/**
	 * Notifies about the population statistics.
	 * 
	 * @param population
	 */
	public void notify(Population population);

}
