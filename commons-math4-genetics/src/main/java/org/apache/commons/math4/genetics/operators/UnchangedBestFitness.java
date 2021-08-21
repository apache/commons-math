package org.apache.commons.math4.genetics.operators;

import org.apache.commons.math4.genetics.stats.PopulationStatisticalSummary;

public class UnchangedBestFitness implements StoppingCondition {

	private double lastBestFitness = Double.MIN_VALUE;

	private final int maxGenerationsWithUnchangedBestFitness;

	private int generationsHavingUnchangedBestFitness;

	public UnchangedBestFitness(final int maxGenerationsWithUnchangedAverageFitness) {
		this.maxGenerationsWithUnchangedBestFitness = maxGenerationsWithUnchangedAverageFitness;
	}

	@Override
	public boolean isSatisfied(PopulationStatisticalSummary populationStats) {
		double currentBestFitness = populationStats.getMaxFitness();

		if (lastBestFitness == currentBestFitness) {
			if (generationsHavingUnchangedBestFitness == maxGenerationsWithUnchangedBestFitness) {
				return true;
			} else {
				this.generationsHavingUnchangedBestFitness++;
			}
		} else {
			this.generationsHavingUnchangedBestFitness = 0;
			lastBestFitness = currentBestFitness;
		}

		return false;
	}

}
