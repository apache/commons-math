/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.math4.genetics.stats.internal;

import java.util.Arrays;

import org.apache.commons.math4.genetics.model.Chromosome;
import org.apache.commons.math4.genetics.model.Population;
import org.apache.commons.math4.genetics.stats.PopulationStatisticalSummary;

public class PopulationStatisticalSummaryImpl implements PopulationStatisticalSummary {

	private double[] fitnesses;

	private double maxFitness;

	private double minFitness;

	private double meanFitness;

	private double variance;

	public PopulationStatisticalSummaryImpl(Population population) {
		double[] fitnesses = getFitnesses(population);
		Arrays.sort(fitnesses);
		this.fitnesses = fitnesses;
		this.maxFitness = fitnesses[fitnesses.length - 1];
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
		return this.fitnesses.length;
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

	@Override
	public int findRank(Chromosome chromosome) {
		return Arrays.binarySearch(fitnesses, chromosome.getFitness());
	}

}
