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
package org.apache.commons.math4.genetics.model;

import org.apache.commons.math4.genetics.exception.GeneticException;
import org.apache.commons.math4.genetics.model.BinaryChromosome;
import org.apache.commons.math4.genetics.model.Chromosome;
import org.apache.commons.math4.genetics.model.FitnessFunction;
import org.junit.Assert;
import org.junit.Test;

public class BinaryChromosomeTest {

	@Test(expected = GeneticException.class)
	public void testInvalidConstructor() {
		Integer[][] reprs = new Integer[][] { new Integer[] { 0, 1, 0, 1, 2 }, new Integer[] { 0, 1, 0, 1, -1 } };

		for (Integer[] repr : reprs) {
			new BinaryChromosome(repr, (chromosome) -> {return 0;});
			Assert.fail("Exception not caught");
		}
	}

	@Test
	public void testRandomConstructor() {
		for (int i = 0; i < 20; i++) {
			BinaryChromosome.randomBinaryRepresentation(10);
		}
	}

	@Test
	public void testIsSame() {
		FitnessFunction fitnessFunction = (chromosome) -> {
			return 0;
		};
		BinaryChromosome c1 = new BinaryChromosome(new Integer[] { 0, 1, 0, 1, 0, 1 }, fitnessFunction);
		BinaryChromosome c2 = new BinaryChromosome(new Integer[] { 0, 1, 1, 0, 1 }, fitnessFunction);
		BinaryChromosome c3 = new BinaryChromosome(new Integer[] { 0, 1, 0, 1, 0, 1, 1 }, fitnessFunction);
		BinaryChromosome c4 = new BinaryChromosome(new Integer[] { 1, 1, 0, 1, 0, 1 }, fitnessFunction);
		BinaryChromosome c5 = new BinaryChromosome(new Integer[] { 0, 1, 0, 1, 0, 0 }, fitnessFunction);
		BinaryChromosome c6 = new BinaryChromosome(new Integer[] { 0, 1, 0, 1, 0, 1 }, fitnessFunction);

		Assert.assertFalse(c1.isSame(c2));
		Assert.assertFalse(c1.isSame(c3));
		Assert.assertFalse(c1.isSame(c4));
		Assert.assertFalse(c1.isSame(c5));
		Assert.assertTrue(c1.isSame(c6));
	}

}
