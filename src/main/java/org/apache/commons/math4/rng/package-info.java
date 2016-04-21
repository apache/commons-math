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

/**
 * <h3>Randomness Providers</h3>
 *
 * <p>
 * This package contains the public API for generating sequences of
 * pseudo-random numbers that are <i>uniformly distributed</i> in a
 * specified range.
 * <br>
 * All implemented generators can be instantiated through
 * {@link org.apache.commons.math4.rng.RandomSource factory methods}.
 * The low-level classes, that define how the randomness is produced,
 * are implemented in package {@link org.apache.commons.math4.rng.internal}
 * and its sub-packages, but should not be used directly.
 * <br>
 * The generators are <i>not</i> thread-safe: Parallel applications must
 * use different generator instances in different threads.
 * </p>
 *
 * <p>
 * In the case of pseudo-random generators, the source of randomness is
 * usually a set of numbers whose bits representation are scrambled in such
 * a way as to produce a random-looking sequence.
 * <br>
 * The main property of the sequence is that the numbers must be uniformly
 * distributed within their allowed range.
 * <br>
 * Classes in this package do not provide any further processing of the
 * number generation such as to match other types of distribution.
 * </p>
 *
 * <p>
 * Which source of randomness to choose may depend on which properties
 * are more important.
 * Considerations can include speed of generation, memory usage, period
 * size, equidistribution, correlation, etc.
 * <br>
 * For some of the generators, interesting properties (of the reference
 * implementations) are proven in scientific papers.
 * Some generators can also suffer from potential weaknesses.
 * </p>
 *
 * <p>
 * For simple sampling, any of the generators implemented in this library
 * may be sufficient.
 * <br>
 * For Monte-Carlo simulations that require generating high-dimensional
 * vectors), equidistribution and non-correlation are crucial.
 * The <i>Mersenne Twister</i> and <i>Well</i> generators have
 * equidistribution properties proven according to their bits pool size
 * which is directly related to their period (all of them have maximal
 * period, i.e. a generator with size {@code n} pool has a period
 * <code>2<sup>n</sup>-1</code>).
 * They also have equidistribution properties for 32 bits blocks up to
 * {@code s/32} dimension where {@code s} is their pool size.
 * <br>
 * For example, {@code Well19937c} is equidistributed up to dimension 623
 * (i.e. 19937 divided by 32).
 * It means that a Monte-Carlo simulation generating vectors of {@code n}
 * (32-bits integer) variables at each iteration has some guarantee on the
 * properties of its components as long as {@code n < 623}.
 * Note that if the variables are of type {@code double}, the limit is
 * divided by two (since 64 bits are needed to create a {@code double}).
 * <br>
 * Reference to the relevant publications are listed in the specific
 * documentation of each class.
 * </p>
 *
 * <p>
 * Memory usage can vary a lot between providers.
 * The state of {@code MersenneTwister} is composed of 624 integers,
 * using about 2.5 kB.
 * The <i>Well</i> generators use 6 integer arrays, the length of each
 * being equal to the pool size; thus, for example, {@code Well44497b}
 * uses about 33 kB.
 * </p>
 */

package org.apache.commons.math4.rng;
