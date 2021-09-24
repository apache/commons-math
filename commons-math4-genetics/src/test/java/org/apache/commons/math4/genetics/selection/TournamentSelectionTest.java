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
package org.apache.commons.math4.genetics.selection;

import org.apache.commons.math4.genetics.chromosome.AbstractChromosome;
import org.apache.commons.math4.genetics.chromosome.ChromosomePair;
import org.apache.commons.math4.genetics.population.ListPopulation;
import org.junit.Assert;
import org.junit.Test;

public class TournamentSelectionTest {

    private static int counter;

    @Test
    public void testSelect() {
        TournamentSelection<String> ts = new TournamentSelection<>(2);
        ListPopulation<String> pop = new ListPopulation<>(100);

        for (int i = 0; i < pop.getPopulationLimit(); i++) {
            pop.addChromosome(new DummyChromosome());
        }
        // how to write a test for stochastic method?
        for (int i = 0; i < 20; i++) {
            ChromosomePair<String> pair = ts.select(pop);
            // the worst chromosome should NEVER be selected
            Assert.assertTrue(pair.getFirst().evaluate() > 0);
            Assert.assertTrue(pair.getSecond().evaluate() > 0);
        }
    }

    private static class DummyChromosome extends AbstractChromosome<String> {
        DummyChromosome() {
            super(c -> counter++, c -> "0");
        }

    }

}
