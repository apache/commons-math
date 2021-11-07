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
package org.apache.commons.math4.ga.population;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.math4.ga.chromosome.AbstractChromosome;
import org.apache.commons.math4.ga.chromosome.Chromosome;
import org.apache.commons.math4.ga.chromosome.IntegralValuedChromosome;
import org.apache.commons.math4.ga.dummy.DummyListChromosomeDecoder;
import org.apache.commons.math4.ga.internal.exception.GeneticException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ListPopulationTest {

    @Test
    public void testGetFittestChromosome() {
        AbstractChromosome<String> c1 = new AbstractChromosome<String>(chromosome -> 0, chromosome -> "0") {
        };
        AbstractChromosome<String> c2 = new AbstractChromosome<String>(chromosome -> 10, chromosome -> "10") {
        };
        AbstractChromosome<String> c3 = new AbstractChromosome<String>(chromosome -> 15, chromosome -> "15") {
        };

        ArrayList<Chromosome<String>> chromosomes = new ArrayList<>();
        chromosomes.add(c1);
        chromosomes.add(c2);
        chromosomes.add(c3);

        ListPopulation<String> population = new ListPopulation<String>(chromosomes, 10);

        Assertions.assertEquals(c3, population.getFittestChromosome());
        Assertions.assertNotNull(population.toString());
    }

    @Test
    public void testChromosomes() {
        final ArrayList<Chromosome<String>> chromosomes = new ArrayList<>();
        chromosomes.add(IntegralValuedChromosome.<String>randomChromosome(3, chromosome -> 0,
                new DummyListChromosomeDecoder<>("0"), 0, 2));
        chromosomes.add(IntegralValuedChromosome.<String>randomChromosome(3, chromosome -> 0,
                new DummyListChromosomeDecoder<>("0"), 0, 2));
        chromosomes.add(IntegralValuedChromosome.<String>randomChromosome(3, chromosome -> 0,
                new DummyListChromosomeDecoder<>("0"), 0, 2));

        final ListPopulation<String> population = new ListPopulation<>(10);

        population.addChromosomes(chromosomes);

        Assertions.assertEquals(chromosomes, population.getChromosomes());

        population.setPopulationLimit(50);
        Assertions.assertEquals(50, population.getPopulationLimit());
    }

    @Test
    public void testSetPopulationLimit() {
        final ListPopulation<String> population = new ListPopulation<>(10);

        Assertions.assertThrows(GeneticException.class, () -> {
            population.setPopulationLimit(-50);
        });

    }

    @Test
    public void testConstructorPopulationLimitNotPositive() {
        Assertions.assertThrows(GeneticException.class, () -> {
            new ListPopulation<String>(-10);
        });
    }

    @Test
    public void testChromosomeListConstructorPopulationLimitNotPositive() {
        final ArrayList<Chromosome<String>> chromosomes = new ArrayList<>();
        chromosomes.add(IntegralValuedChromosome.<String>randomChromosome(3, chromosome -> 0,
                new DummyListChromosomeDecoder<>("0"), 0, 2));
        Assertions.assertThrows(GeneticException.class, () -> {
            new ListPopulation<String>(chromosomes, -10);
        });
    }

    @Test
    public void testConstructorListOfChromosomesBiggerThanPopulationSize() {
        final ArrayList<Chromosome<String>> chromosomes = new ArrayList<>();
        chromosomes.add(IntegralValuedChromosome.<String>randomChromosome(3, chromosome -> 0,
                new DummyListChromosomeDecoder<>("0"), 0, 2));
        chromosomes.add(IntegralValuedChromosome.<String>randomChromosome(3, chromosome -> 0,
                new DummyListChromosomeDecoder<>("0"), 0, 2));
        chromosomes.add(IntegralValuedChromosome.<String>randomChromosome(3, chromosome -> 0,
                new DummyListChromosomeDecoder<>("0"), 0, 2));

        Assertions.assertThrows(GeneticException.class, () -> {
            new ListPopulation<String>(chromosomes, 1);
        });

    }

    @Test
    public void testAddTooManyChromosomes() {
        final ArrayList<Chromosome<String>> chromosomes = new ArrayList<>();
        chromosomes.add(IntegralValuedChromosome.<String>randomChromosome(3, chromosome -> 0,
                new DummyListChromosomeDecoder<>("0"), 0, 2));
        chromosomes.add(IntegralValuedChromosome.<String>randomChromosome(3, chromosome -> 0,
                new DummyListChromosomeDecoder<>("0"), 0, 2));
        chromosomes.add(IntegralValuedChromosome.<String>randomChromosome(3, chromosome -> 0,
                new DummyListChromosomeDecoder<>("0"), 0, 2));

        final ListPopulation<String> population = new ListPopulation<>(2);

        Assertions.assertThrows(GeneticException.class, () -> {
            population.addChromosomes(chromosomes);
        });

    }

    @Test
    public void testAddTooManyChromosomesSingleCall() {

        final ListPopulation<String> population = new ListPopulation<>(2);

        Assertions.assertThrows(GeneticException.class, () -> {
            for (int i = 0; i <= population.getPopulationLimit(); i++) {
                population.addChromosome(IntegralValuedChromosome.<String>randomChromosome(3, chromosome -> 0,
                        new DummyListChromosomeDecoder<>("0"), 0, 2));
            }
        });
    }

    @Test
    public void testIterator() {
        final ArrayList<Chromosome<String>> chromosomes = new ArrayList<>();
        chromosomes.add(IntegralValuedChromosome.<String>randomChromosome(3, chromosome -> 0,
                new DummyListChromosomeDecoder<>("0"), 0, 2));
        chromosomes.add(IntegralValuedChromosome.<String>randomChromosome(3, chromosome -> 0,
                new DummyListChromosomeDecoder<>("0"), 0, 2));
        chromosomes.add(IntegralValuedChromosome.<String>randomChromosome(3, chromosome -> 0,
                new DummyListChromosomeDecoder<>("0"), 0, 2));

        final ListPopulation<String> population = new ListPopulation<>(10);

        population.addChromosomes(chromosomes);

        final Iterator<Chromosome<String>> iter = population.iterator();
        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            while (iter.hasNext()) {
                iter.next();
                iter.remove();
            }
        });
    }

    @Test
    public void testSetPopulationLimitTooSmall() {
        final ArrayList<Chromosome<String>> chromosomes = new ArrayList<>();
        chromosomes.add(IntegralValuedChromosome.<String>randomChromosome(3, chromosome -> 0,
                new DummyListChromosomeDecoder<>("0"), 0, 2));
        chromosomes.add(IntegralValuedChromosome.<String>randomChromosome(3, chromosome -> 0,
                new DummyListChromosomeDecoder<>("0"), 0, 2));
        chromosomes.add(IntegralValuedChromosome.<String>randomChromosome(3, chromosome -> 0,
                new DummyListChromosomeDecoder<>("0"), 0, 2));

        final ListPopulation<String> population = new ListPopulation<>(chromosomes, 3);

        Assertions.assertThrows(GeneticException.class, () -> {
            population.setPopulationLimit(2);
        });

    }

    @Test
    public void testNextGeneration() {
        final ArrayList<Chromosome<String>> chromosomes = new ArrayList<>();
        chromosomes.add(IntegralValuedChromosome.<String>randomChromosome(3, chromosome -> 0,
                new DummyListChromosomeDecoder<>("0"), 0, 2));
        chromosomes.add(IntegralValuedChromosome.<String>randomChromosome(3, chromosome -> 0,
                new DummyListChromosomeDecoder<>("0"), 0, 2));
        chromosomes.add(IntegralValuedChromosome.<String>randomChromosome(3, chromosome -> 0,
                new DummyListChromosomeDecoder<>("0"), 0, 2));

        final ListPopulation<String> population = new ListPopulation<>(chromosomes, 3);

        Assertions.assertEquals(1, population.nextGeneration(.4).getPopulationSize());
        Assertions.assertEquals(0, population.nextGeneration(.1).getPopulationSize());
    }
}
