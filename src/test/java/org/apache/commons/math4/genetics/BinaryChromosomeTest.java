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
package org.apache.commons.math4.genetics;


import org.apache.commons.math4.exception.MathIllegalArgumentException;
import org.apache.commons.math4.genetics.BinaryChromosome;
import org.apache.commons.math4.genetics.Chromosome;
import org.junit.Assert;
import org.junit.Test;

public class BinaryChromosomeTest {

    @Test
    public void testInvalidConstructor() {
        Integer[][] reprs = new Integer[][] {
                new Integer[] {0,1,0,1,2},
                new Integer[] {0,1,0,1,-1}
        };

        for (Integer[] repr : reprs) {
            try {
                new DummyBinaryChromosome(repr);
                Assert.fail("Exception not caught");
            } catch (MathIllegalArgumentException e) {
                // Expected
            }
        }
    }

    @Test
    public void testRandomConstructor() {
        for (int i=0; i<20; i++) {
            new DummyBinaryChromosome(BinaryChromosome.randomBinaryRepresentation(10));
        }
    }

    @Test
    public void testIsSame() {
        Chromosome c1 = new DummyBinaryChromosome(new Integer[] {0,1,0,1,0,1});
        Chromosome c2 = new DummyBinaryChromosome(new Integer[] {0,1,1,0,1});
        Chromosome c3 = new DummyBinaryChromosome(new Integer[] {0,1,0,1,0,1,1});
        Chromosome c4 = new DummyBinaryChromosome(new Integer[] {1,1,0,1,0,1});
        Chromosome c5 = new DummyBinaryChromosome(new Integer[] {0,1,0,1,0,0});
        Chromosome c6 = new DummyBinaryChromosome(new Integer[] {0,1,0,1,0,1});

        Assert.assertFalse(c1.isSame(c2));
        Assert.assertFalse(c1.isSame(c3));
        Assert.assertFalse(c1.isSame(c4));
        Assert.assertFalse(c1.isSame(c5));
        Assert.assertTrue(c1.isSame(c6));
    }

}
