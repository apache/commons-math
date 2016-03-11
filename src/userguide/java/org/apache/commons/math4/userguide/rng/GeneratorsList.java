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
package org.apache.commons.math4.userguide.rng;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.math4.rng.UniformRandomProvider;
import org.apache.commons.math4.rng.RandomSource;

/**
 * List of generators.
 */
public class GeneratorsList implements Iterable<UniformRandomProvider> {
    /** List. */
    private final List<UniformRandomProvider> list = new ArrayList<>();

    /**
     * Creates list.
     */
    public GeneratorsList() {
        list.add(RandomSource.create(RandomSource.JDK));
        list.add(RandomSource.create(RandomSource.MT));
        list.add(RandomSource.create(RandomSource.WELL_512_A));
        list.add(RandomSource.create(RandomSource.WELL_1024_A));
        list.add(RandomSource.create(RandomSource.WELL_19937_A));
        list.add(RandomSource.create(RandomSource.WELL_19937_C));
        list.add(RandomSource.create(RandomSource.WELL_44497_A));
        list.add(RandomSource.create(RandomSource.WELL_44497_B));
        list.add(RandomSource.create(RandomSource.ISAAC));
        list.add(RandomSource.create(RandomSource.MT_64));
        list.add(RandomSource.create(RandomSource.SPLIT_MIX_64));
        list.add(RandomSource.create(RandomSource.XOR_SHIFT_1024_S));
        list.add(RandomSource.create(RandomSource.TWO_CMRES));
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<UniformRandomProvider> iterator() {
        return list.iterator();
    }
}
