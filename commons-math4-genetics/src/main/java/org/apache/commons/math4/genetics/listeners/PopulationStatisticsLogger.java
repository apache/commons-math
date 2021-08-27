package org.apache.commons.math4.genetics.listeners;

import org.apache.commons.math4.genetics.model.Population;
import org.apache.commons.math4.genetics.stats.PopulationStatisticalSummary;
import org.apache.commons.math4.genetics.stats.internal.PopulationStatisticalSummaryImpl;

public class PopulationStatisticsLogger implements ConvergenceListener {

	@Override
	public void notify(Population population) {
		PopulationStatisticalSummary populationStatisticalSummary = new PopulationStatisticalSummaryImpl(population);
		System.out.println("*******************Population statistics*******************");
		System.out.println("Mean Fitness : " + populationStatisticalSummary.getMeanFitness());
		System.out.println("Max Fitness : " + populationStatisticalSummary.getMaxFitness());
		System.out.println("Fitness Variance : " + populationStatisticalSummary.getFitnessVariance());
	}

}
