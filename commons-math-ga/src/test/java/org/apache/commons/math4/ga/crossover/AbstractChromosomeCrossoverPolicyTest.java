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
package org.apache.commons.math4.ga.crossover;

import org.apache.commons.math4.ga.chromosome.Chromosome;
import org.apache.commons.math4.ga.chromosome.ChromosomePair;
import org.apache.commons.math4.ga.dummy.DummyChromosome;
import org.junit.Assert;
import org.junit.Test;

public class AbstractChromosomeCrossoverPolicyTest {

    @Test
    public void testCrossoverProbability() {

        CrossoverPolicy<String> crossoverPolicy = new AbstractChromosomeCrossoverPolicy<String>() {
            @Override
            protected ChromosomePair<String> crossover(Chromosome<String> first, Chromosome<String> second) {
                return null;
            }
        };

        Chromosome<String> ch1 = new DummyChromosome();

        Chromosome<String> ch2 = new DummyChromosome();

        Assert.assertNull(crossoverPolicy.crossover(ch1, ch2, 1.0));
        Assert.assertNotNull(crossoverPolicy.crossover(ch1, ch2, 0.0));
    }

}
