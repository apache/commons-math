package org.apache.commons.math4.genetics.operators;

import org.apache.commons.math4.genetics.model.Population;

public class UnchangedBestFitness implements StoppingCondition {

	private double lastBestFitness = Double.MIN_VALUE;

	private final int maxGenerationsWithUnchangedBestFitness;

	private int noOfGenerationsHavingUnchangedBestFitness;

	public UnchangedBestFitness(final int maxGenerationsWithUnchangedAverageFitness) {
		this.maxGenerationsWithUnchangedBestFitness = maxGenerationsWithUnchangedAverageFitness;
	}

	@Override
	public boolean isSatisfied(Population population) {
		double currentBestFitness = population.getFittestChromosome().getFitness();

		if (lastBestFitness == currentBestFitness) {
			if (noOfGenerationsHavingUnchangedBestFitness == maxGenerationsWithUnchangedBestFitness) {
				return true;
			} else {
				this.noOfGenerationsHavingUnchangedBestFitness++;
			}
		} else {
			this.noOfGenerationsHavingUnchangedBestFitness = 0;
			lastBestFitness = currentBestFitness;
		}

		return false;
	}

}
