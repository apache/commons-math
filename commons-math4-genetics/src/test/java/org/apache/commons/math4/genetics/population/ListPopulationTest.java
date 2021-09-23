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
package org.apache.commons.math4.genetics.population;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.math4.genetics.chromosome.AbstractChromosome;
import org.apache.commons.math4.genetics.chromosome.BinaryChromosome;
import org.apache.commons.math4.genetics.chromosome.Chromosome;
import org.apache.commons.math4.genetics.exception.GeneticException;
import org.apache.commons.math4.genetics.utils.DummyListChromosomeDecoder;
import org.junit.Assert;
import org.junit.Test;

public class ListPopulationTest {

    @Test
    public void testGetFittestChromosome() {
        AbstractChromosome<String> c1 = new AbstractChromosome<String>(chromosome -> {
            return 0;
        }, chromosome -> {
            return "0";
        }) {
        };
        AbstractChromosome<String> c2 = new AbstractChromosome<String>(chromosome -> {
            return 10;
        }, chromosome -> {
            return "10";
        }) {
        };
        AbstractChromosome<String> c3 = new AbstractChromosome<String>(chromosome -> {
            return 15;
        }, chromosome -> {
            return "15";
        }) {
        };

        ArrayList<Chromosome<String>> chromosomes = new ArrayList<>();
        chromosomes.add(c1);
        chromosomes.add(c2);
        chromosomes.add(c3);

        ListPopulation<String> population = new ListPopulation<String>(chromosomes, 10);

        Assert.assertEquals(c3, population.getFittestChromosome());
    }

    @Test
    public void testChromosomes() {
        final ArrayList<Chromosome<String>> chromosomes = new ArrayList<>();
        chromosomes.add(BinaryChromosome.randomChromosome(3, chromosome -> {
            return 0;
        }, new DummyListChromosomeDecoder<>("0")));
        chromosomes.add(BinaryChromosome.randomChromosome(3, chromosome -> {
            return 0;
        }, new DummyListChromosomeDecoder<>("0")));
        chromosomes.add(BinaryChromosome.randomChromosome(3, chromosome -> {
            return 0;
        }, new DummyListChromosomeDecoder<>("0")));

        final ListPopulation<String> population = new ListPopulation<>(10);

        population.addChromosomes(chromosomes);

        Assert.assertEquals(chromosomes, population.getChromosomes());

        population.setPopulationLimit(50);
        Assert.assertEquals(50, population.getPopulationLimit());
    }

    @Test(expected = GeneticException.class)
    public void testSetPopulationLimit() {
        final ListPopulation<String> population = new ListPopulation<>(10);

        population.setPopulationLimit(-50);
    }

    @Test(expected = GeneticException.class)
    public void testConstructorPopulationLimitNotPositive() {
        new ListPopulation<String>(-10);
    }

    @Test(expected = GeneticException.class)
    public void testChromosomeListConstructorPopulationLimitNotPositive() {
        final ArrayList<Chromosome<String>> chromosomes = new ArrayList<>();
        chromosomes.add(BinaryChromosome.randomChromosome(3, chromosome -> {
            return 0;
        }, new DummyListChromosomeDecoder<>("0")));
        new ListPopulation<String>(chromosomes, -10);
    }

    @Test(expected = GeneticException.class)
    public void testConstructorListOfChromosomesBiggerThanPopulationSize() {
        final ArrayList<Chromosome<String>> chromosomes = new ArrayList<>();
        chromosomes.add(BinaryChromosome.randomChromosome(3, chromosome -> {
            return 0;
        }, new DummyListChromosomeDecoder<>("0")));
        chromosomes.add(BinaryChromosome.randomChromosome(3, chromosome -> {
            return 0;
        }, new DummyListChromosomeDecoder<>("0")));
        chromosomes.add(BinaryChromosome.randomChromosome(3, chromosome -> {
            return 0;
        }, new DummyListChromosomeDecoder<>("0")));

        new ListPopulation<String>(chromosomes, 1);
    }

    @Test(expected = GeneticException.class)
    public void testAddTooManyChromosomes() {
        final ArrayList<Chromosome<String>> chromosomes = new ArrayList<>();
        chromosomes.add(BinaryChromosome.randomChromosome(3, chromosome -> {
            return 0;
        }, new DummyListChromosomeDecoder<>("0")));
        chromosomes.add(BinaryChromosome.randomChromosome(3, chromosome -> {
            return 0;
        }, new DummyListChromosomeDecoder<>("0")));
        chromosomes.add(BinaryChromosome.randomChromosome(3, chromosome -> {
            return 0;
        }, new DummyListChromosomeDecoder<>("0")));

        final ListPopulation<String> population = new ListPopulation<>(2);

        population.addChromosomes(chromosomes);
    }

    @Test(expected = GeneticException.class)
    public void testAddTooManyChromosomesSingleCall() {

        final ListPopulation<String> population = new ListPopulation<>(2);

        for (int i = 0; i <= population.getPopulationLimit(); i++) {
            population.addChromosome(BinaryChromosome.randomChromosome(3, chromosome -> {
                return 0;
            }, new DummyListChromosomeDecoder<>("0")));
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIterator() {
        final ArrayList<Chromosome<String>> chromosomes = new ArrayList<>();
        chromosomes.add(BinaryChromosome.randomChromosome(3, chromosome -> {
            return 0;
        }, new DummyListChromosomeDecoder<>("0")));
        chromosomes.add(BinaryChromosome.randomChromosome(3, chromosome -> {
            return 0;
        }, new DummyListChromosomeDecoder<>("0")));
        chromosomes.add(BinaryChromosome.randomChromosome(3, chromosome -> {
            return 0;
        }, new DummyListChromosomeDecoder<>("0")));

        final ListPopulation<String> population = new ListPopulation<>(10);

        population.addChromosomes(chromosomes);

        final Iterator<Chromosome<String>> iter = population.iterator();
        while (iter.hasNext()) {
            iter.next();
            iter.remove();
        }
    }

    @Test(expected = GeneticException.class)
    public void testSetPopulationLimitTooSmall() {
        final ArrayList<Chromosome<String>> chromosomes = new ArrayList<>();
        chromosomes.add(BinaryChromosome.randomChromosome(3, chromosome -> {
            return 0;
        }, new DummyListChromosomeDecoder<>("0")));
        chromosomes.add(BinaryChromosome.randomChromosome(3, chromosome -> {
            return 0;
        }, new DummyListChromosomeDecoder<>("0")));
        chromosomes.add(BinaryChromosome.randomChromosome(3, chromosome -> {
            return 0;
        }, new DummyListChromosomeDecoder<>("0")));

        final ListPopulation<String> population = new ListPopulation<>(chromosomes, 3);

        population.setPopulationLimit(2);
    }

}
