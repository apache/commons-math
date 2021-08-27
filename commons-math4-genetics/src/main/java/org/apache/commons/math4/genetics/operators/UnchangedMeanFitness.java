package org.apache.commons.math4.genetics.operators;

import org.apache.commons.math4.genetics.model.Chromosome;
import org.apache.commons.math4.genetics.model.Population;

public class UnchangedMeanFitness implements StoppingCondition {

	private double lastMeanFitness = Double.MIN_VALUE;

	private final int maxGenerationsWithUnchangedMeanFitness;

	private int generationsHavingUnchangedMeanFitness;

	public UnchangedMeanFitness(final int maxGenerationsWithUnchangedMeanFitness) {
		this.maxGenerationsWithUnchangedMeanFitness = maxGenerationsWithUnchangedMeanFitness;
	}

	@Override
	public boolean isSatisfied(Population population) {

		double currentMeanFitness = calculateMeanFitness(population);

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

	private double calculateMeanFitness(Population population) {
		double totalFitness = 0.0;
		for (Chromosome chromosome : population) {
			totalFitness += chromosome.getFitness();
		}
		return totalFitness / population.getPopulationSize();
	}
}
