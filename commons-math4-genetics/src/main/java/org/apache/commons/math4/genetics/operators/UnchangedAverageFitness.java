package org.apache.commons.math4.genetics.operators;

import org.apache.commons.math4.genetics.model.Population;

public class UnchangedAverageFitness implements StoppingCondition {

	private double lastBestFitness = Double.MIN_VALUE;

	private final int maxGenerationsWithUnchangedAverageFitness;

	private int noOfGenerationsHavingUnchangedAverageFitness;

	public UnchangedAverageFitness(final int maxGenerationsWithUnchangedAverageFitness) {
		this.maxGenerationsWithUnchangedAverageFitness = maxGenerationsWithUnchangedAverageFitness;
	}

	@Override
	public boolean isSatisfied(Population population) {

//		double currentBestFitness = population.getFittestChromosome().getFitness();
//
//		if (lastBestFitness == currentBestFitness) {
//			if (noOfGenerationsHavingUnchangedAverageFitness == maxGenerationsWithUnchangedAverageFitness) {
//				return true;
//			} else {
//				this.noOfGenerationsHavingUnchangedAverageFitness++;
//			}
//		} else {
//			this.noOfGenerationsHavingUnchangedAverageFitness = 0;
//			lastBestFitness = currentBestFitness;
//		}

		return false;
	}

}
