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
package org.apache.commons.math3.genetics;


import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.junit.Assert;
import org.junit.Test;

public class ElitisticListPopulationTest {

    private static int counter = 0;

    @Test
    public void testNextGeneration() {
        ElitisticListPopulation pop = new ElitisticListPopulation(100, 0.203);

        for (int i=0; i<pop.getPopulationLimit(); i++) {
            pop.addChromosome(new DummyChromosome());
        }

        Population nextGeneration = pop.nextGeneration();

        Assert.assertEquals(20, nextGeneration.getPopulationSize());
    }

    @Test
    public void testSetElitismRate() {
        final double rate = 0.25;
        final ElitisticListPopulation pop = new ElitisticListPopulation(100, 0.203);
        pop.setElitismRate(rate);
        Assert.assertEquals(rate, pop.getElitismRate(), 1e-6);
    }

    @Test(expected = OutOfRangeException.class)
    public void testSetElitismRateTooLow() {
        final double rate = -0.25;
        final ElitisticListPopulation pop = new ElitisticListPopulation(100, 0.203);
        pop.setElitismRate(rate);
    }

    @Test(expected = OutOfRangeException.class)
    public void testSetElitismRateTooHigh() {
        final double rate = 1.25;
        final ElitisticListPopulation pop = new ElitisticListPopulation(100, 0.203);
        pop.setElitismRate(rate);
    }

    @Test(expected = OutOfRangeException.class)
    public void testConstructorTooLow() {
        final double rate = -0.25;
        new ElitisticListPopulation(100, rate);
    }

    @Test(expected = OutOfRangeException.class)
    public void testConstructorTooHigh() {
        final double rate = 1.25;
        new ElitisticListPopulation(100, rate);
    }

    @Test(expected = OutOfRangeException.class)
    public void testChromosomeListConstructorTooLow() {
        final List<Chromosome> chromosomes = Collections.emptyList();
        final double rate = -0.25;
        new ElitisticListPopulation(chromosomes, 100, rate);
    }

    @Test(expected = OutOfRangeException.class)
    public void testChromosomeListConstructorTooHigh() {
        final List<Chromosome> chromosomes = Collections.emptyList();
        final double rate = 1.25;
        new ElitisticListPopulation(chromosomes, 100, rate);
    }

    private static class DummyChromosome extends Chromosome {
        private final int fitness;

        public DummyChromosome() {
            this.fitness = counter;
            counter++;
        }

        public double fitness() {
            return this.fitness;
        }
    }

}
