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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AbstractChromosomeTest {

    @Test
    public void testGetFitness() {
        Chromosome<String> c1 = new AbstractChromosome<String>(chromosome -> 1, chromosome -> "1") {
        };
        Assertions.assertEquals(1, c1.evaluate(), .001);
    }

    @Test
    public void testDecode() {
        Chromosome<String> c1 = new AbstractChromosome<String>(chromosome -> 1, chromosome -> "1") {
        };
        Assertions.assertEquals("1", c1.decode());
    }

    @Test
    public void testCompareTo() {
        Chromosome<String> c1 = new AbstractChromosome<String>(chromosome -> 0, chromosome -> "0") {
        };
        Chromosome<String> c2 = new AbstractChromosome<String>(chromosome -> 10, chromosome -> "10") {
        };
        Chromosome<String> c3 = new AbstractChromosome<String>(chromosome -> 10, chromosome -> "10") {
        };

        Assertions.assertTrue(c1.compareTo(c2) < 0);
        Assertions.assertTrue(c2.compareTo(c1) > 0);
        Assertions.assertEquals(0, c3.compareTo(c2));
        Assertions.assertEquals(0, c2.compareTo(c3));
    }

    @Test
    public void testIsSame() {
        AbstractChromosome<String> c1 = new AbstractChromosome<String>(chromosome -> 1, chromosome -> "1") {
        };
        AbstractChromosome<String> c2 = new AbstractChromosome<String>(chromosome -> 2, chromosome -> "2") {
        };
        AbstractChromosome<String> c3 = new AbstractChromosome<String>(chromosome -> 3, chromosome -> "1") {
        };
        Assertions.assertTrue(c1.isSame(c3));
        Assertions.assertFalse(c1.isSame(c2));
    }

}
