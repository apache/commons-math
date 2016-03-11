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
 * Composes two {@link SeedConverter converters}.
 *
 * @param <IN> Input seed type.
 * @param <TRANS> Transitional seed type.
 * @param <OUT> Output seed type.
 *
 * @since 4.0
 */
public class SeedConverterComposer<IN, TRANS, OUT> implements SeedConverter<IN, OUT> {
    /** First conversion. */
    private SeedConverter<IN, TRANS> first;
    /** Second conversion. */
    private SeedConverter<TRANS, OUT> second;

    /**
     * @param first First conversion.
     * @param second second conversion.
     */
    public SeedConverterComposer(SeedConverter<IN, TRANS> first,
                                 SeedConverter<TRANS, OUT> second) {
        this.first = first;
        this.second = second;
    }

    /** {@inheritDoc} */
    @Override
    public OUT convert(IN seed) {
        final TRANS trans = first.convert(seed);
        return second.convert(trans);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return getClass().getSimpleName() + " (" + second + " o " + first + ")";
    }
}
