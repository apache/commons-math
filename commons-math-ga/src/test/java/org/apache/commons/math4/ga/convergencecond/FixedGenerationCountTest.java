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
package org.apache.commons.math4.ga.convergencecond;

import org.apache.commons.math4.ga.convergence.FixedGenerationCount;

import org.apache.commons.math4.ga.internal.exception.GeneticException;
import org.apache.commons.math4.ga.population.ListPopulation;
import org.apache.commons.math4.ga.population.Population;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FixedGenerationCountTest {

    @Test
    public void testIsSatisfied() {
        FixedGenerationCount<String> fgc = new FixedGenerationCount<String>(20);

        int cnt = 0;
        Population<String> pop = new ListPopulation<>(10);

        while (!fgc.isSatisfied(pop)) {
            cnt++;
        }
        Assertions.assertEquals(cnt, fgc.getNumGenerations());
    }

    @Test
    public void testNegativeGenerationCount() {
        Assertions.assertThrows(GeneticException.class, () -> {
            new FixedGenerationCount<String>(-1);
        });
    }
}
