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

package org.apache.commons.math4.examples.genetics.tsp.legacy;

import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.StoppingCondition;

public class UnchangedBestFitness implements StoppingCondition {

	private double lastBestFitness = Double.MIN_VALUE;

	private final int maxGenerationsWithUnchangedBestFitness;

	private int generationsHavingUnchangedBestFitness;

	public UnchangedBestFitness(final int maxGenerationsWithUnchangedAverageFitness) {
		this.maxGenerationsWithUnchangedBestFitness = maxGenerationsWithUnchangedAverageFitness;
	}

	@Override
	public boolean isSatisfied(Population population) {
		double currentBestFitness = population.getFittestChromosome().getFitness();

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
