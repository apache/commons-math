package org.apache.commons.math4.genetics.operators;

import org.apache.commons.math4.genetics.stats.PopulationStatisticalSummary;

public class UnchangedMeanFitness implements StoppingCondition {

	private double lastMeanFitness = Double.MIN_VALUE;

	private final int maxGenerationsWithUnchangedMeanFitness;

	private int generationsHavingUnchangedMeanFitness;

	public UnchangedMeanFitness(final int maxGenerationsWithUnchangedMeanFitness) {
		this.maxGenerationsWithUnchangedMeanFitness = maxGenerationsWithUnchangedMeanFitness;
	}

	@Override
	public boolean isSatisfied(PopulationStatisticalSummary populationStats) {

		double currentMeanFitness = populationStats.getMeanFitness();

		if (lastMeanFitness == currentMeanFitness) {
			if (generationsHavingUnchangedMeanFitness == maxGenerationsWithUnchangedMeanFitness) {
				return true;
			} else {
				this.generationsHavingUnchangedMeanFitness++;
			}
		} else {
			this.generationsHavingUnchangedMeanFitness = 0;
			lastMeanFitness = currentMeanFitness;
		}

		return false;
	}

}
