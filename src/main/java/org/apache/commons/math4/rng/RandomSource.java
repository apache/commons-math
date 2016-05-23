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

import org.apache.commons.math4.exception.MathUnsupportedOperationException;
import org.apache.commons.math4.rng.internal.ProviderBuilder;
import org.apache.commons.math4.rng.internal.BaseProvider;
import org.apache.commons.math4.rng.internal.util.SeedFactory;
import org.apache.commons.math4.rng.internal.source64.TwoCmres;

/**
 * This class provides the API for creating generators of random numbers.
 * <p>
 * Usage examples:
 * <pre><code>
 *  UniformRandomProvider rng = RandomSource.create(RandomSource.MT);
 * </code></pre>
 * or
 * <pre><code>
 *  final int[] seed = new int[] { 196, 9, 0, 226 };
 *  UniformRandomProvider rng = RandomSource.create(RandomSource.MT, seed);
 * </code></pre>
 * or
 * <pre><code>
 *  final int[] seed = RandomSource.createIntArray(256);
 *  UniformRandomProvider rng = RandomSource.create(RandomSource.MT, seed);
 * </code></pre>
 * where the first argument to method {@code create} is the identifier
 * of the generator's concrete implementation, and the second the is the
 * (optional) seed.
 * <br>
 * In the first form, a random seed will be {@link SeedFactory generated
 * automatically}; in the second form, a fixed seed is used; a random seed
 * is explicitly generated in the third form.
 * </p>
 *
 * <p>
 * Seeding is the procedure by which a value (or set of values) is
 * used to <i>initialize</i> a generator instance.
 * The requirement that a given seed will always result in the same
 * internal state allows to create different instances of a generator
 * that will produce the same sequence of pseudo-random numbers.
 * </p>
 *
 * <p>
 * The type of data used as a seed depends on the concrete implementation
 * as some types may not provide enough information to fully initialize
 * the generator's internal state.
 * <br>
 * The reference algorithm's seeding procedure (if provided) operates
 * on a value of a (single) <i>native</i> type:
 * Each concrete implementation's constructor creates an instance using
 * the native type whose information contents is used to set the
 * internal state.
 * <br>
 * When the seed value passed by the caller is of the native type, it is
 * expected that the sequences produced will be identical to those
 * produced by other implementations of the same reference algorithm.
 * <br>
 * However, when the seed value passed by the caller is not of the native
 * type, a transformation is performed by this library and the resulting
 * native type value will <i>not</i> contain more information than the
 * original seed value.
 * If the algorithm's native type is "simpler" than the type passed by
 * the caller, then some (unused) information will even be lost.
 * <br>
 * The transformation from non-native to native seed type is arbitrary,
 * as long as it does not reduce the amount of information required by
 * the algorithm to initialize its state.
 * The consequence of the transformation is that sequences produced
 * by this library may <i>not</i> be the same as the sequences produced
 * by other implementations of the same algorithm!
 * </p>
 *
 * <p>
 * This class provides methods to generate random seeds (single values
 * or arrays of values, of {@code int} or {@code long} types) that can
 * be passed to the {@link RandomSource#create(RandomSource,Object,Object[])
 * generators factory method}.
 * <br>
 * Although the seed-generating methods defined in this class will likely
 * return different values each time they are called, there is no guarantee
 * that the resulting "seed" will always generate a <i>good</i> (i.e.
 * sufficiently uniformly random for the intended purpose) sequence of
 * numbers, even if the generator is good!
 * The only way to ensure that the selected seed will make the generator
 * produce a good sequence is to submit that sequence to a series of
 * stringent tests, as provided by tools such as
 * <a href="http://www.phy.duke.edu/~rgb/General/dieharder.php">dieharder</a>
 * or <a href="http://simul.iro.umontreal.ca/testu01/tu01.html">TestU01</a>.
 * </p>
 *
 * <p>
 * The current implementations have no provision for producing non-overlapping
 * sequences.
 * For parallel applications, a possible workaround is that each thread uses
 * a generator of a different type (see {@link #TWO_CMRES_SELECT}).
 * </p>
 *
 * <p>
 * <b>Note:</b>
 * Seeding is not equivalent to restoring the internal state of an
 * <i>already initialized</i> generator.
 * Indeed, generators can have a state that is more complex than the
 * seed, and seeding is thus a transformation (from seed to state).
 * Implementations do not provide the inverse transformation (from
 * state to seed), hence it is not generally possible to know the seed
 * that would initialize a new generator instance to the current state
 * of another instance.
 * Reseeding is also inefficient if the purpose is to continue the
 * same sequence where another instance left off, as it would require
 * to "replay" all the calls performed by that other instance (and it
 * would require to know the number of calls to the primary source of
 * randomness, which is also not usually accessible).
 * <br>
 * This factory thus provides a method for
 * {@link #saveState(UniformRandomProvider) saving} the internal
 * state of a generator.
 * The state is encapsulated in an {@link State "opaque object"} to be
 * used for {@link #restoreState(UniformRandomProvider,State) restoring}
 * a generator (of the same type) to an identical state (e.g. to allow
 * persistent storage, or to continue a sequence from where the original
 * instance left off).
 * </p>
 *
 * @since 4.0
 */
public enum RandomSource {
    /**
     * Source of randomness is {@link org.apache.commons.math4.rng.internal.source32.JDKRandom}.
     * Native seed type: {@code Long}.
     */
    JDK(ProviderBuilder.RandomSourceInternal.JDK),
    /**
     * Source of randomness is {@link org.apache.commons.math4.rng.internal.source32.Well512a}.
     * Native seed type: {@code int[]}.
     */
    WELL_512_A(ProviderBuilder.RandomSourceInternal.WELL_512_A),
    /**
     * Source of randomness is {@link org.apache.commons.math4.rng.internal.source32.Well1024a}.
     * Native seed type: {@code int[]}.
     */
    WELL_1024_A(ProviderBuilder.RandomSourceInternal.WELL_1024_A),
    /**
     * Source of randomness is {@link org.apache.commons.math4.rng.internal.source32.Well19937a}.
     * Native seed type: {@code int[]}.
     */
    WELL_19937_A(ProviderBuilder.RandomSourceInternal.WELL_19937_A),
    /**
     * Source of randomness is {@link org.apache.commons.math4.rng.internal.source32.Well19937c}.
     * Native seed type: {@code int[]}.
     */
    WELL_19937_C(ProviderBuilder.RandomSourceInternal.WELL_19937_C),
    /**
     * Source of randomness is {@link org.apache.commons.math4.rng.internal.source32.Well44497a}.
     * Native seed type: {@code int[]}.
     */
    WELL_44497_A(ProviderBuilder.RandomSourceInternal.WELL_44497_A),
    /**
     * Source of randomness is {@link org.apache.commons.math4.rng.internal.source32.Well44497b}.
     * Native seed type: {@code int[]}.
     */
    WELL_44497_B(ProviderBuilder.RandomSourceInternal.WELL_44497_B),
    /**
     * Source of randomness is {@link org.apache.commons.math4.rng.internal.source32.MersenneTwister}.
     * Native seed type: {@code int[]}.
     */
    MT(ProviderBuilder.RandomSourceInternal.MT),
    /**
     * Source of randomness is {@link org.apache.commons.math4.rng.internal.source32.ISAACRandom}.
     * Native seed type: {@code int[]}.
     */
    ISAAC(ProviderBuilder.RandomSourceInternal.ISAAC),
    /**
     * Source of randomness is {@link org.apache.commons.math4.rng.internal.source64.SplitMix64}.
     * Native seed type: {@code Long}.
     */
    SPLIT_MIX_64(ProviderBuilder.RandomSourceInternal.SPLIT_MIX_64),
    /**
     * Source of randomness is {@link org.apache.commons.math4.rng.internal.source64.XorShift1024Star}.
     * Native seed type: {@code long[]}.
     */
    XOR_SHIFT_1024_S(ProviderBuilder.RandomSourceInternal.XOR_SHIFT_1024_S),
    /**
     * Source of randomness is {@link org.apache.commons.math4.rng.internal.source64.TwoCmres}.
     * Native seed type: {@code Integer}.
     */
    TWO_CMRES(ProviderBuilder.RandomSourceInternal.TWO_CMRES),
    /**
     * Source of randomness is {@link org.apache.commons.math4.rng.internal.source64.TwoCmres},
     * with explicit selection of the two subcycle generators.
     * Native seed type: {@code Integer}.
     */
    TWO_CMRES_SELECT(ProviderBuilder.RandomSourceInternal.TWO_CMRES_SELECT),
    /**
     * Source of randomness is {@link org.apache.commons.math4.rng.internal.source64.MersenneTwister64}.
     * Native seed type: {@code long[]}.
     */
    MT_64(ProviderBuilder.RandomSourceInternal.MT_64);

    /** Internal identifier. */
    private final ProviderBuilder.RandomSourceInternal internalIdentifier;

    /**
     * @param id Internal identifier.
     */
    RandomSource(ProviderBuilder.RandomSourceInternal id) {
        internalIdentifier = id;
    }

    /**
     * @return the internal identifier.
     */
    ProviderBuilder.RandomSourceInternal getInternalIdentifier() {
        return internalIdentifier;
    }

    /**
     * Checks whether the type of given {@code seed} is the native type
     * of the implementation.
     *
     * @param seed Seed value.
     * @return {@code true} if the seed can be passed to the builder
     * for this RNG type.
     */
    public boolean isNativeSeed(Object seed) {
        return internalIdentifier.isNativeSeed(seed);
    }

    /**
     * Marker interface used to define the "save" and "restore"
     * functionality of the generators.
     */
    public interface State {}

    /**
     * Creates a random number generator with a random seed.
     *
     * <p>
     * Example of usage:
     * <pre><code>
     *  UniformRandomProvider rng = RandomSource.create(Source.MT);
     * </code></pre>
     * </p>
     *
     * @param source {@link RandomSource RNG type}.
     * @return the RNG.
     */
    public static UniformRandomProvider create(RandomSource source) {
        return create(source, null);
    }

    /**
     * Creates a random number generator with the given {@code seed}.
     *
     * <p>
     * Example of usage:
     * <pre><code>
     *  UniformRandomProvider rng = RandomSource.create(Source.TWO_CMRES_SELECT, 26219, 6, 9);
     * </code></pre>
     * </p>
     *
     * <p>
     * Valid types for the {@code seed} are:
     *  <ul>
     *   <li>{@code Integer} (or {@code int})</li>
     *   <li>{@code Long} (or {@code long})</li>
     *   <li>{@code int[]}</li>
     *   <li>{@code long[]}</li>
     *  </ul>
     * </p>
     *
     * <p>
     * Notes:
     * <ul>
     *  <li>
     *   When the seed type passed as argument is more complex (i.e. more
     *   bits can be independently chosen) than the generator's
     *   {@link #isNativeSeed(Object) native type}, the conversion of a
     *   set of different seeds will necessarily result in the same value
     *   of the native seed type.
     *  </li>
     *  <li>
     *   When the native seed type is an array, the same remark applies
     *   when the array contains more bits than the state of the generator.
     *  </li>
     *  <li>
     *   When the native seed type is an array and the {@code seed} is
     *   {@code null}, the size of the generated array will be 128.
     *  </li>
     * </p>
     *
     * @param source {@link RandomSource RNG type}.
     * @param seed Seed value.  It can be {@code null} (in which case a
     * random value will be used).
     * @param data Additional arguments to the implementation's constructor.
     * Please refer to the documentation of each specific implementation.
     * @return the RNG.
     * @throws MathUnsupportedOperationException if the type of the
     * {@code seed} is invalid.
     * @throws org.apache.commons.math4.exception.InsufficientDataException
     * if data is missing to initialize the generator implemented by the
     * given {@code source}.
     */
    public static UniformRandomProvider create(RandomSource source,
                                               Object seed,
                                               Object ... data) {
        return ProviderBuilder.create(source.getInternalIdentifier(), seed, data);
    }

    /**
     * Gets the number of elements of the set of "subcycle" generators from
     * which two can be selected in order to create a {@link TwoCmres} RNG.
     *
     * @return the number of implemented subcycle generators.
     */
    public static int numberOfCmresGenerators() {
        return TwoCmres.numberOfSubcycleGenerators();
    }

    /**
     * Saves the state of a RNG.
     *
     * @param provider Provider.
     * @return the current state of the given {@code provider}.
     * @throws MathUnsupportedOperationException if the {@code provider} is
     * not an object created by this factory or the underlying source of
     * randomness does not support this functionality.
     *
     * @see #restoreState(UniformRandomProvider,RandomSource.State)
     */
    public static State saveState(UniformRandomProvider provider) {
        if (!(provider instanceof BaseProvider)) {
            throw new MathUnsupportedOperationException();
        } else {
            return ((BaseProvider) provider).getState();
        }
    }

    /**
     * Restores the state of a RNG.
     *
     * @param provider Provider.
     * @param state State which the {@code provider} will be set to.
     * This parameter must have been obtained by a call to
     * {@link #saveState(UniformRandomProvider) saveState(rng)}
     * where {@code rng} is either the same object as {@code provider},
     * or an object of the same concrete type.
     * @throws MathUnsupportedOperationException if the {@code provider} is
     * not an object created by this factory or the underlying source of
     * randomness does not support this functionality.
     * @throws org.apache.commons.math4.exception.InsufficientDataException
     * if it was detected that the {@code state} is incompatible with the
     * given {@code provider}.
     *
     * @see #saveState(UniformRandomProvider)
     */
    public static void restoreState(UniformRandomProvider provider,
                                    State state) {
        if (!(provider instanceof BaseProvider)) {
            throw new MathUnsupportedOperationException();
        } else {
            ((BaseProvider) provider).setState(state);
        }
    }

    /**
     * Creates a number for use as a seed.
     *
     * @return a random number.
     */
    public static int createInt() {
        return SeedFactory.createInt();
    }

    /**
     * Creates a number for use as a seed.
     *
     * @return a random number.
     */
    public static long createLong() {
        return SeedFactory.createLong();
    }

    /**
     * Creates an array of numbers for use as a seed.
     *
     * @param n Size of the array to create.
     * @return an array of {@code n} random numbers.
     */
    public static int[] createIntArray(int n) {
        return SeedFactory.createIntArray(n);
    }

    /**
     * Creates an array of numbers for use as a seed.
     *
     * @param n Size of the array to create.
     * @return an array of {@code n} random numbers.
     */
    public static long[] createLongArray(int n) {
        return SeedFactory.createLongArray(n);
    }
}
