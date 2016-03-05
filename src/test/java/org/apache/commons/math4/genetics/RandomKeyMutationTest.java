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


import org.apache.commons.math4.genetics.Chromosome;
import org.apache.commons.math4.genetics.MutationPolicy;
import org.apache.commons.math4.genetics.RandomKey;
import org.apache.commons.math4.genetics.RandomKeyMutation;
import org.junit.Assert;
import org.junit.Test;

public class RandomKeyMutationTest {

    @Test
    public void testMutate() {
        MutationPolicy mutation = new RandomKeyMutation();
        int l=10;
        for (int i=0; i<20; i++) {
            DummyRandomKey origRk = new DummyRandomKey(RandomKey.randomPermutation(l));
            Chromosome mutated = mutation.mutate(origRk);
            DummyRandomKey mutatedRk = (DummyRandomKey) mutated;

            int changes = 0;
            for (int j=0; j<origRk.getLength(); j++) {
                if (origRk.getRepresentation().get(j) != mutatedRk.getRepresentation().get(j)) {
                    changes++;
                }
            }
            Assert.assertEquals(1,changes);
        }
    }

}
