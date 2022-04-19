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
package org.apache.commons.math4.ga.chromosome;

import org.apache.commons.math4.ga.dummy.DummyListChromosome;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AbstractListChromosomeTest {

    @Test
    public void testCompareTo() {
        final Integer[] repr = new Integer[] {1, 0, 1};
        Chromosome<String> c1 = new DummyListChromosome(repr, c -> 0);
        Chromosome<String> c2 = new DummyListChromosome(repr, c -> 10);
        Chromosome<String> c3 = new DummyListChromosome(repr, c -> 10);

        Assertions.assertTrue(c1.compareTo(c2) < 0);
        Assertions.assertTrue(c2.compareTo(c1) > 0);
        Assertions.assertEquals(0, c3.compareTo(c2));
        Assertions.assertEquals(0, c2.compareTo(c3));
    }

    @Test
    public void testGetFitness() {
        final Integer[] repr = new Integer[] {1, 0, 1};
        Chromosome<String> ch = new DummyListChromosome(repr, c -> .001);
        Assertions.assertEquals(.001, ch.getFitness(), .00001);
    }
}
