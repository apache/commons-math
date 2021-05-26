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
package org.apache.commons.math4.transform;

import java.util.function.DoubleUnaryOperator;
import java.util.function.UnaryOperator;

import org.apache.commons.numbers.complex.Complex;

/**
 * {@link Complex} transform.
 * <p>
 * Such transforms include {@link FastSineTransform sine transform},
 * {@link FastCosineTransform cosine transform} or {@link
 * FastHadamardTransform Hadamard transform}.
 */
public interface ComplexTransform extends UnaryOperator<Complex[]> {
    /**
     * Returns the transform of the specified data set.
     *
     * @param f the data array to be transformed (signal).
     * @return the transformed array (spectrum).
     * @throws IllegalArgumentException if the transform cannot be performed.
     */
    Complex[] apply(Complex[] f);

    /**
     * Returns the transform of the specified data set.
     *
     * @param f the data array to be transformed (signal).
     * @return the transformed array (spectrum).
     * @throws IllegalArgumentException if the transform cannot be performed.
     */
    Complex[] apply(double[] f);

    /**
     * Returns the transform of the specified function.
     *
     * @param f Function to be sampled and transformed.
     * @param min Lower bound (inclusive) of the interval.
     * @param max Upper bound (exclusive) of the interval.
     * @param n Number of sample points.
     * @return the result.
     * @throws IllegalArgumentException if the transform cannot be performed.
     */
    default Complex[] apply(DoubleUnaryOperator f,
                            double min,
                            double max,
                            int n) {
        return apply(TransformUtils.sample(f, min, max, n));
    }
}
