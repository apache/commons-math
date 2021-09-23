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
package org.apache.commons.math4.genetics.convergencecond;

import java.util.concurrent.TimeUnit;

import org.apache.commons.math4.genetics.population.ListPopulation;
import org.apache.commons.math4.genetics.population.Population;
import org.junit.Assert;
import org.junit.Test;

public class FixedElapsedTimeTest {

    @Test
    public void testIsSatisfied() {
        final Population<String> pop = new ListPopulation<>(10);

        final long start = System.nanoTime();
        final long duration = 3;
        final FixedElapsedTime<String> tec = new FixedElapsedTime<String>(duration, TimeUnit.SECONDS);

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

        Assert.assertTrue(diff < TimeUnit.MILLISECONDS.toNanos(100));
    }
}
