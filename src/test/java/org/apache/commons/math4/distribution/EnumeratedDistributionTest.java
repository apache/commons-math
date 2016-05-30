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
package org.apache.commons.math4.distribution;

import static org.junit.Assert.assertEquals;

import org.apache.commons.math4.util.Pair;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;


/**
 * Test class for {@link EnumeratedDistribution}.
 *
 */
public class EnumeratedDistributionTest {

    @Test
    public void testCreateFromPMF() {
        Integer[] values = {0, 1, null};
        List<Pair<Integer, Double>> pmf = Arrays.asList(
            new Pair<Integer, Double>(values[0], 0.1),
            new Pair<Integer, Double>(values[1], 0.3),
            new Pair<Integer, Double>(values[1], 0.2),
            new Pair<Integer, Double>(values[2], 0.2),
            new Pair<Integer, Double>(values[2], 0.2)
        );
        EnumeratedDistribution<Integer> distribution = new EnumeratedDistribution<Integer>(pmf);
        assertEquals(0.1, distribution.probability(values[0]), 0);
        assertEquals(0.5, distribution.probability(values[1]), 0);
        assertEquals(0.4, distribution.probability(values[2]), 0);
        assertEquals(pmf, distribution.getPmf());
    }
}
