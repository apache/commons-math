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
import java.util.Iterator;

import org.apache.commons.math4.genetics.exception.GeneticException;
import org.junit.Assert;
import org.junit.Test;

public class ListPopulationTest {

	@Test
	public void testGetFittestChromosome() {
		Chromosome c1 = new Chromosome((c) -> {
			return 0;
		}) {
		};
		Chromosome c2 = new Chromosome((c) -> {
			return 10;
		}) {
		};
		Chromosome c3 = new Chromosome((c) -> {
			return 15;
		}) {
		};

		ArrayList<Chromosome> chromosomes = new ArrayList<>();
		chromosomes.add(c1);
		chromosomes.add(c2);
		chromosomes.add(c3);

		ListPopulation population = new ListPopulation(chromosomes, 10);

		Assert.assertEquals(c3, population.getFittestChromosome());
	}

	@Test
	public void testChromosomes() {
		final ArrayList<Chromosome> chromosomes = new ArrayList<>();
		chromosomes.add(BinaryChromosome.randomChromosome(3, (c) -> {
			return 0;
		}));
		chromosomes.add(BinaryChromosome.randomChromosome(3, (c) -> {
			return 0;
		}));
		chromosomes.add(BinaryChromosome.randomChromosome(3, (c) -> {
			return 0;
		}));

		final ListPopulation population = new ListPopulation(10);

		population.addChromosomes(chromosomes);

		Assert.assertEquals(chromosomes, population.getChromosomes());
		Assert.assertEquals(chromosomes.toString(), population.toString());

		population.setPopulationLimit(50);
		Assert.assertEquals(50, population.getPopulationLimit());
	}

	@Test(expected = GeneticException.class)
	public void testSetPopulationLimit() {
		final ListPopulation population = new ListPopulation(10);

		population.setPopulationLimit(-50);
	}

	@Test(expected = GeneticException.class)
	public void testConstructorPopulationLimitNotPositive() {
		new ListPopulation(-10);
	}

	@Test(expected = GeneticException.class)
	public void testChromosomeListConstructorPopulationLimitNotPositive() {
		final ArrayList<Chromosome> chromosomes = new ArrayList<>();
		chromosomes.add(BinaryChromosome.randomChromosome(3, (c) -> {
			return 0;
		}));
		new ListPopulation(chromosomes, -10);
	}

	@Test(expected = GeneticException.class)
	public void testConstructorListOfChromosomesBiggerThanPopulationSize() {
		final ArrayList<Chromosome> chromosomes = new ArrayList<>();
		chromosomes.add(BinaryChromosome.randomChromosome(3, (c) -> {
			return 0;
		}));
		chromosomes.add(BinaryChromosome.randomChromosome(3, (c) -> {
			return 0;
		}));
		chromosomes.add(BinaryChromosome.randomChromosome(3, (c) -> {
			return 0;
		}));
		new ListPopulation(chromosomes, 1);
	}

	@Test(expected = GeneticException.class)
	public void testAddTooManyChromosomes() {
		final ArrayList<Chromosome> chromosomes = new ArrayList<>();
		chromosomes.add(BinaryChromosome.randomChromosome(3, (c) -> {
			return 0;
		}));
		chromosomes.add(BinaryChromosome.randomChromosome(3, (c) -> {
			return 0;
		}));
		chromosomes.add(BinaryChromosome.randomChromosome(3, (c) -> {
			return 0;
		}));

		final ListPopulation population = new ListPopulation(2);

		population.addChromosomes(chromosomes);
	}

	@Test(expected = GeneticException.class)
	public void testAddTooManyChromosomesSingleCall() {

		final ListPopulation population = new ListPopulation(2);

		for (int i = 0; i <= population.getPopulationLimit(); i++) {
			population.addChromosome(BinaryChromosome.randomChromosome(3, (c) -> {
				return 0;
			}));
		}
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testIterator() {
		final ArrayList<Chromosome> chromosomes = new ArrayList<>();
		chromosomes.add(BinaryChromosome.randomChromosome(3, (c) -> {
			return 0;
		}));
		chromosomes.add(BinaryChromosome.randomChromosome(3, (c) -> {
			return 0;
		}));
		chromosomes.add(BinaryChromosome.randomChromosome(3, (c) -> {
			return 0;
		}));

		final ListPopulation population = new ListPopulation(10);

		population.addChromosomes(chromosomes);

		final Iterator<Chromosome> iter = population.iterator();
		while (iter.hasNext()) {
			iter.next();
			iter.remove();
		}
	}

	@Test(expected = GeneticException.class)
	public void testSetPopulationLimitTooSmall() {
		final ArrayList<Chromosome> chromosomes = new ArrayList<>();
		chromosomes.add(BinaryChromosome.randomChromosome(3, (c) -> {
			return 0;
		}));
		chromosomes.add(BinaryChromosome.randomChromosome(3, (c) -> {
			return 0;
		}));
		chromosomes.add(BinaryChromosome.randomChromosome(3, (c) -> {
			return 0;
		}));

		final ListPopulation population = new ListPopulation(chromosomes, 3);

		population.setPopulationLimit(2);
	}

}
