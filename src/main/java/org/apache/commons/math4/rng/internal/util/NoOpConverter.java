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
package org.apache.commons.math4.rng.internal.util;


/**
 * Dummy converter that simply passes on its input.
 * It can be useful to avoid "unchecked" compiler warnings.
 *
 * @param <SEED> Seed type.
 *
 * @since 4.0
 */
public class NoOpConverter<SEED> implements SeedConverter<SEED, SEED> {
    /** {@inheritDoc} */
    @Override
    public SEED convert(SEED seed) {
        return seed;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Pass-through";
    }
}
