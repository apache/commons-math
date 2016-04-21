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

package org.apache.commons.math4.rng.internal.source64;

import java.util.List;
import java.util.ArrayList;
import org.apache.commons.math4.exception.MathInternalError;
import org.apache.commons.math4.exception.OutOfRangeException;
import org.apache.commons.math4.exception.InsufficientDataException;
import org.apache.commons.math4.rng.internal.util.NumberFactory;

/**
 * Random number generator designed by Mark D. Overton.
 * <p>
 *  It is one of the many generators described by the author in the following article series:
 *  <ul>
 *   <li><a href="http://www.drdobbs.com/tools/fast-high-quality-parallel-random-number/229625477">Part one</a></li>
 *   <li><a href="http://www.drdobbs.com/tools/fast-high-quality-parallel-random-number/231000484">Part two</a></li>
 *  </ul>
 * </p>
 *
 * @since 4.0
 */
public class TwoCmres extends LongProvider {
    /** A small positive integer. */
    private static final byte SEED_GUARD = 9;
    /** Factory of instances of this class. Singleton. */
    private static final Cmres.Factory FACTORY = new Cmres.Factory();
    /** First subcycle generator. */
    private final Cmres x;
    /** Second subcycle generator. */
    private final Cmres y;
    /** State of first subcycle generator. */
    private long xx;
    /** State of second subcycle generator. */
    private long yy;

    /**
     * Creates a new instance.
     *
     * @param seed Initial seed.
     * @param x First subcycle generator.
     * @param y Second subcycle generator.
     * @throws InsufficientDataException if {@code x == y}.
     */
    private TwoCmres(int seed,
                     Cmres x,
                     Cmres y) {
        if (x == y) {
            throw new InsufficientDataException();
        }
        this.x = x;
        this.y = y;
        setSeedInternal(seed);
    }

    /**
     * Creates a new instance.
     *
     * @param seed Seed.
     */
    public TwoCmres(Integer seed) {
        this(seed, 0, 1);
    }

    /**
     * Creates a new instance.
     *
     * @param seed Seed.
     * @param i Table entry for first subcycle generator.
     * @param j Table entry for second subcycle generator.
     * @throws InsufficientDataException if {@code i == j}.
     * @throws OutOfRangeException if {@code i < 0} or
     * {@code i >= numberOfSubcycleGenerators()}.
     * @throws OutOfRangeException if {@code j < 0} or
     * {@code j >= numberOfSubcycleGenerators()}.
     */
    public TwoCmres(Integer seed,
                    int i,
                    int j) {
        this(seed, FACTORY.get(i), FACTORY.get(j));
    }

    /** {@inheritDoc} */
    @Override
    public long next() {
        xx = x.transform(xx);
        yy = y.transform(yy);

        return xx + yy;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return super.toString() + " (" + x + " + " + y + ")";
    }

    /**
     * @return the number of subcycle generators.
     */
    public static int numberOfSubcycleGenerators() {
        return FACTORY.numberOfSubcycleGenerators();
    }

    /** {@inheritDoc} */
    @Override
    protected byte[] getStateInternal() {
        return NumberFactory.makeByteArray(new long[] { xx, yy });
    }

    /** {@inheritDoc} */
    @Override
    protected void setStateInternal(byte[] s) {
        if (s.length != 16) {
            throw new InsufficientDataException();
        }

        final long[] state = NumberFactory.makeLongArray(s);
        xx = state[0];
        yy = state[1];
    }

    /**
     * @param seed Seed.
     */
    private void setSeedInternal(int seed) {
        // The seeding procedure consists in going away from some
        // point known to be in the cycle.
        // The total number of calls to the "transform" method will
        // not exceed about 130,000 (which is negligible as seeding
        // will not occur more than once in normal usage).

        // Make two positive 16-bits integers.
        final long s = NumberFactory.makeLong(0, seed); // s >= 0
        final int xMax = (int) (s & 0xffff + SEED_GUARD);
        final int yMax = (int) ((s >> 16) + SEED_GUARD);

        if (xMax < 0 ||
            yMax < 0) {
            throw new MathInternalError();
        }

        xx = x.getStart();
        for (int i = xMax; i > 0; i--) {
            xx = x.transform(xx);
        }

        yy = y.getStart();
        for (int i = yMax; i > 0; i--) {
            yy = y.transform(yy);
        }
    }

    /**
     * Subcycle generator.
     * Class is immutable.
     */
    static class Cmres {
        /** Cycle start. */
        private final int start;
        /** Multiplier. */
        private final long multiply;
        /** Rotation. */
        private final int rotate;

        /**
         * @param multiply Multiplier.
         * @param rotate Positive number. Must be in {@code [0, 64]}.
         * @param start Cycle start.
         */
        Cmres(long multiply,
              int rotate,
              int start) {
            this.multiply = multiply;
            this.rotate = rotate;
            this.start = start;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            final String sep = ", ";
            // Use hexadecimal for "multiplier" field.
            final String m = String.format((java.util.Locale) null, "0x%016xL", multiply);
            return "Cmres: [" + m + sep + rotate + sep + start + "]";
        }

        /**
         * @return the multiplier.
         */
        public long getMultiply() {
            return multiply;
        }

        /**
         * @return the cycle start.
         */
        public int getStart() {
            return start;
        }

        /**
         * @param state Current state.
         * @return the new state.
         */
        long transform(long state) {
            long s = state;
            s *= multiply;
            s = rotl(s);
            s -= state;
            return s;
        }

        /**
         * @param state State.
         * @return the rotated state.
         */
        private long rotl(long state) {
            return (state << rotate) | (state >>> (64 - rotate));
        }

        /** Factory. */
        static class Factory {
            /** List of good "Cmres" subcycle generators. */
            private static final List<Cmres> TABLE = new ArrayList<Cmres>();

            /**
             * Populates the table.
             * It lists parameters known to be good (provided in
             * the article referred to above).
             * To maintain compatibility, new entries must be added
             * only at the end of the table.
             */
            static {
                add(0xedce446814d3b3d9L, 33, 0x13b572e7);
                add(0xc5b3cf786c806df7L, 33, 0x13c8e18a);
                add(0xdd91bbb8ab9e0e65L, 31, 0x06dd03a6);
                add(0x7b69342c0790221dL, 31, 0x1646bb8b);
                add(0x0c72c0d18614c32bL, 33, 0x06014a3d);
                add(0xd8d98c13bebe26c9L, 33, 0x014e8475);
                add(0xcb039dc328bbc40fL, 31, 0x008684bd);
                add(0x858c5ef3c021ed2fL, 32, 0x0dc8d622);
                add(0x4c8be96bfc23b127L, 33, 0x0b6b20cc);
                add(0x11eab77f808cf641L, 32, 0x06534421);
                add(0xbc9bd78810fd28fdL, 31, 0x1d9ba40d);
                add(0x0f1505c780688cb5L, 33, 0x0b7b7b67);
                add(0xadc174babc2053afL, 31, 0x267f4197);
                add(0x900b6b82b31686d9L, 31, 0x023c6985);
                // Add new entries here.
            }

            /**
             * @return the number of subcycle generators.
             */
            int numberOfSubcycleGenerators() {
                return TABLE.size();
            }

            /**
             * @param index Index into the list of available generators.
             * @return the subcycle generator entry at index {@code index}.
             */
            Cmres get(int index) {
                if (index < 0 ||
                    index >= TABLE.size()) {
                    throw new OutOfRangeException(index, 0, TABLE.size());
                }

                return TABLE.get(index);
            }

            /**
             * Adds an entry to the {@link Factory#TABLE}.
             *
             * @param multiply Multiplier.
             * @param rotate Rotate.
             * @param start Cycle start.
             */
            private static void add(long multiply,
                                    int rotate,
                                    int start) {
                // Sanity check: if there are duplicates, the class initialization
                // will fail (and the JVM will report "NoClassDefFoundError").
                for (Cmres sg : TABLE) {
                    if (multiply == sg.getMultiply()) {
                        throw new MathInternalError();
                    }
                }

                TABLE.add(new Cmres(multiply, rotate, start));
            }
        }
    }
}
