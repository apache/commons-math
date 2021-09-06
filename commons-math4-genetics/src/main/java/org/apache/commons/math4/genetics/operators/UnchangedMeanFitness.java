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
