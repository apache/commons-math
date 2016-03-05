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


import org.apache.commons.math4.genetics.BinaryChromosome;
import org.apache.commons.math4.genetics.BinaryMutation;
import org.junit.Assert;
import org.junit.Test;

public class BinaryMutationTest {

    @Test
    public void testMutate() {
        BinaryMutation mutation = new BinaryMutation();

        // stochastic testing :)
        for (int i=0; i<20; i++) {
            DummyBinaryChromosome original = new DummyBinaryChromosome(BinaryChromosome.randomBinaryRepresentation(10));
            DummyBinaryChromosome mutated = (DummyBinaryChromosome) mutation.mutate(original);

            // one gene should be different
            int numDifferent = 0;
            for (int j=0; j<original.getRepresentation().size(); j++) {
                if (original.getRepresentation().get(j) != mutated.getRepresentation().get(j))
                    numDifferent++;
            }
            Assert.assertEquals(1, numDifferent);
        }
    }

}
