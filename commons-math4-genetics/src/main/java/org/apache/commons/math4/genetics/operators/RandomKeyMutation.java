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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math4.genetics.exception.GeneticException;
import org.apache.commons.math4.genetics.model.Chromosome;
import org.apache.commons.math4.genetics.model.RandomKey;
import org.apache.commons.math4.genetics.utils.RandomGenerator;

/**
 * Mutation operator for {@link RandomKey}s. Changes a randomly chosen element
 * of the array representation to a random value uniformly distributed in [0,1].
 *
 * @since 2.0
 */
public class RandomKeyMutation implements MutationPolicy {

	/**
	 * {@inheritDoc}
	 *
	 * @throws GeneticException if <code>original</code> is not a
	 *                                      {@link RandomKey} instance
	 */
	@Override
	public Chromosome mutate(final Chromosome original, double mutationRate) {
		if (!(original instanceof RandomKey<?>)) {
			throw new GeneticException(GeneticException.ILLEGAL_ARGUMENT, original.getClass().getSimpleName());
		}

		RandomKey<?> originalRk = (RandomKey<?>) original;
		List<Double> repr = originalRk.getRepresentation();
		int rInd = RandomGenerator.getRandomGenerator().nextInt(repr.size());

		List<Double> newRepr = new ArrayList<>(repr);
		newRepr.set(rInd, RandomGenerator.getRandomGenerator().nextDouble());

		return originalRk.newFixedLengthChromosome(newRepr);
	}

}
