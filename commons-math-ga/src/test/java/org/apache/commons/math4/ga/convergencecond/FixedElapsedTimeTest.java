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

import java.util.concurrent.TimeUnit;

import org.apache.commons.math4.ga.convergence.FixedElapsedTime;
import org.apache.commons.math4.ga.internal.exception.GeneticException;
import org.apache.commons.math4.ga.population.ListPopulation;
import org.apache.commons.math4.ga.population.Population;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FixedElapsedTimeTest {

    @Test
    public void testIsSatisfied() {
        final Population<String> pop = new ListPopulation<>(10);

        final long start = System.nanoTime();
        final long duration = 3;
        final FixedElapsedTime<String> tec = new FixedElapsedTime<String>(duration);

        while (!tec.isSatisfied(pop)) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // ignore
            }
        }

        final long end = System.nanoTime();
        final long elapsedTime = end - start;
        final long diff = Math.abs(elapsedTime - TimeUnit.SECONDS.toNanos(duration));

        Assertions.assertTrue(diff < TimeUnit.MILLISECONDS.toNanos(100));
    }

    @Test
    public void testNegativeTime() {
        Assertions.assertThrows(GeneticException.class, () -> {
            new FixedElapsedTime<>(-10);
        });
    }
}
