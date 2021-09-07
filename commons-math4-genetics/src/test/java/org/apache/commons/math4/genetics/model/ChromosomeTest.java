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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math4.genetics.model.Chromosome;
import org.apache.commons.math4.genetics.model.FitnessFunction;
import org.apache.commons.math4.genetics.model.ListPopulation;
import org.apache.commons.math4.genetics.model.Population;
import org.junit.Assert;
import org.junit.Test;

public class ChromosomeTest {

	@Test
	public void testCompareTo() {
		Chromosome c1 = new Chromosome((c) -> {
			return 0;
		}) {
		};
		Chromosome c2 = new Chromosome((c) -> {
			return 10;
		}) {
		};
		Chromosome c3 = new Chromosome((c) -> {
			return 10;
		}) {
		};

		Assert.assertTrue(c1.compareTo(c2) < 0);
		Assert.assertTrue(c2.compareTo(c1) > 0);
		Assert.assertEquals(0, c3.compareTo(c2));
		Assert.assertEquals(0, c2.compareTo(c3));
	}

	private abstract static class DummyChromosome extends Chromosome {
		private final int repr;

		DummyChromosome(final int repr, FitnessFunction f) {
			super(f);
			this.repr = repr;
		}

		@Override
		protected boolean isSame(Chromosome another) {
			return ((DummyChromosome) another).repr == repr;
		}

		@Override
		public Chromosome findSameChromosome(Population population) {
			return super.findSameChromosome(population);
		}
	}

	@Test
	public void testFindSameChromosome() {

		DummyChromosome c1 = new DummyChromosome(1, (c) -> {
			return 1;
		}) {
		};
		DummyChromosome c2 = new DummyChromosome(2, (c) -> {
			return 2;
		}) {
		};
		DummyChromosome c3 = new DummyChromosome(3, (c) -> {
			return 3;
		}) {
		};
		DummyChromosome c4 = new DummyChromosome(1, (c) -> {
			return 5;
		}) {
		};
		DummyChromosome c5 = new DummyChromosome(15, (c) -> {
			return 15;
		}) {
		};

		List<Chromosome> popChr = new ArrayList<>();
		popChr.add(c1);
		popChr.add(c2);
		popChr.add(c3);
		Population pop = new ListPopulation(popChr, 3);

		Assert.assertNull(c5.findSameChromosome(pop));
		Assert.assertEquals(c1, c4.findSameChromosome(pop));

		c4.searchForFitnessUpdate(pop);
		Assert.assertEquals(1, c4.getFitness(), 0);
	}

}
