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
package org.apache.commons.math4.rng;

import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests which all 64-bits based generators must pass.
 */
@RunWith(value=Parameterized.class)
public class Providers64ParametricTest {
    /** RNG under test. */
    private final UniformRandomProvider generator;

    /**
     * Initializes generator instance.
     *
     * @param rng RNG to be tested.
     */
    public Providers64ParametricTest(ProvidersList.Data data) {
        final RandomSource source = data.getSource();
        final Object seed = data.getSeed();
        final Object[] args = data.getArgs();
        generator = RandomSource.create(source, seed, args);
    }

    @Parameters(name = "{index}: data={0}")
    public static Iterable<ProvidersList.Data[]> getList() {
        return ProvidersList.list64();
    }


    @Test
    public void testNextBytesChunks() {
        final int[] chunkSizes = { 8, 16, 24 };
        final int[] chunks = { 1, 2, 3, 4, 5 };
        for (int chunkSize : chunkSizes) {
            for (int numChunks : chunks) {
                ProvidersCommonParametricTest.checkNextBytesChunks(generator,
                                                                   chunkSize,
                                                                   numChunks);
            }
        }
    }
}
