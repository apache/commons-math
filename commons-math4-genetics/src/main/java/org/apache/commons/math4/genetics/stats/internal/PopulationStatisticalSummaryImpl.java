package org.apache.commons.math4.genetics.stats.internal;

import java.util.Arrays;

import org.apache.commons.math4.genetics.model.Chromosome;
import org.apache.commons.math4.genetics.model.Population;
import org.apache.commons.math4.genetics.stats.PopulationStatisticalSummary;

public class PopulationStatisticalSummaryImpl implements PopulationStatisticalSummary {

	private int size;

	private double maxFitness;

	private double minFitness;

	private double meanFitness;

	private double variance;

	public PopulationStatisticalSummaryImpl(Population population) {
		this.size = population.getPopulationSize();
		double[] fitnesses = getFitnesses(population);
		Arrays.sort(fitnesses);
		this.maxFitness = fitnesses[size - 1];
		this.minFitness = fitnesses[0];
		this.meanFitness = calculateMeanFitness(fitnesses);
		this.variance = calculateVariance(fitnesses);
	}

	private double[] getFitnesses(Population population) {
		double fitnesses[] = new double[population.getPopulationSize()];
		int index = 0;
		for (Chromosome chromosome : population) {
			fitnesses[index++] = chromosome.getFitness();
		}
		return fitnesses;
	}

	@Override
	public double getMeanFitness() {
		return this.meanFitness;
	}

	@Override
	public double getFitnessVariance() {
		return this.variance;
	}

	@Override
	public double getMaxFitness() {
		return this.maxFitness;
	}

	@Override
	public double getMinFitness() {
		return this.minFitness;
	}

	@Override
	public long getPopulationSize() {
		return this.size;
	}

	private double calculateMeanFitness(double[] fitnesses) {
		double sum = 0.0;
		for (double fitness : fitnesses) {
			sum += fitness;
		}
		return sum / fitnesses.length;
	}

	private double calculateVariance(double[] fitnesses) {
		if (this.meanFitness == 0) {
			this.meanFitness = calculateMeanFitness(fitnesses);
		}
		double sumOfSquare = 0.0;
		for (double fitness : fitnesses) {
			sumOfSquare += Math.pow(fitness, 2);
		}

		return (sumOfSquare / fitnesses.length) - Math.pow(this.meanFitness, 2);
	}

}
