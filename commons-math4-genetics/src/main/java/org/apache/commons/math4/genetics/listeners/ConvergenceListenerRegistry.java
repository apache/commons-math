package org.apache.commons.math4.genetics.listeners;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math4.genetics.model.Population;

/**
 * This class is the default implementation of ConvergenceListenerRegistry. It
 * will be responsible for registering the interested listeners and notifying
 * all when required.
 *
 */
public class ConvergenceListenerRegistry {

	private static final ConvergenceListenerRegistry instance = new ConvergenceListenerRegistry();

	private List<ConvergenceListener> listeners = new ArrayList<>();

	private ConvergenceListenerRegistry() {

	}

	/**
	 * Registers the interested ConvergenceListener passed as an argument.
	 * 
	 * @param the convergence listener.
	 */
	public void addConvergenceListener(ConvergenceListener convergenceListener) {
		this.listeners.add(convergenceListener);
	}

	/**
	 * Notifies all registered ConvergenceListeners about the population statistics.
	 * 
	 * @param population statistics
	 */
	public void notifyAll(Population population) {
		for (ConvergenceListener convergenceListener : listeners) {
			convergenceListener.notify(population);
		}
	}

	public void addConvergenceListeners(List<ConvergenceListener> convergenceListeners) {
		if (convergenceListeners != null) {
			for (ConvergenceListener convergenceListener : convergenceListeners) {
				this.listeners.add(convergenceListener);
			}
		}
	}

	public static ConvergenceListenerRegistry getInstance() {
		return instance;
	}

}