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
package org.apache.commons.math4.random;

import java.io.Serializable;

import org.apache.commons.math4.exception.NotStrictlyPositiveException;
import org.apache.commons.math4.exception.OutOfRangeException;
import org.apache.commons.math4.util.FastMath;

/**
 * Abstract class implementing the methods of the {@link RandomGenerator}
 * interface in a generic way on the basis of abstract method {@link nextInt()}
 * to be defined in subclasses.
 *
 * It also provides additional utility methods that are not part of the
 * {@link RandomGenerator} API.
 *
 * @since 4.0
 */
public abstract class BaseRandomGenerator
    implements RandomGenerator,
               Serializable {
    /** Identifier for serialization. */
    private static final long serialVersionUID = 20151227L;
    /** Next Gaussian. */
    private double nextGaussian = Double.NaN;

    /**
     * {@inheritDoc}
     *
     * Basic building block for all the generic methods defined in this class.
     * It produces the next random number according to a specific algorithm to
     * be implemented by a subclass.
     */
    @Override
    public abstract int nextInt();

    /** {@inheritDoc} */
    @Override
    public boolean nextBoolean() {
        return (nextInt() >>> 31) != 0;
    }

    /** {@inheritDoc} */
    @Override
    public double nextDouble() {
        final long high = ((long) (nextInt() >>> 6)) << 26;
        final int low  = nextInt() >>> 6;
        return (high | low) * 0x1.0p-52d;
    }

    /** {@inheritDoc} */
    @Override
    public float nextFloat() {
        return (nextInt() >>> 9) * 0x1.0p-23f;
    }

    /** {@inheritDoc} */
    @Override
    public double nextGaussian() {
        final double random;
        if (Double.isNaN(nextGaussian)) {
            // Generate a new pair of gaussian numbers.
            final double x = nextDouble();
            final double y = nextDouble();
            final double alpha = 2 * FastMath.PI * x;
            final double r = FastMath.sqrt(-2 * FastMath.log(y));
            random = r * FastMath.cos(alpha);
            nextGaussian = r * FastMath.sin(alpha);
        } else {
            // Use the second element of the pair already generated.
            random = nextGaussian;
            nextGaussian = Double.NaN;
        }

        return random;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * This default implementation is copied from Apache Harmony
     * java.util.Random (r929253).
     * </p>
     *
     * <p>Implementation notes:
     *  <ul>
     *   <li>If n is a power of 2, this method returns
     *    {@code (int) ((n * (long) next(31)) >> 31)}.</li>
     *   <li>If n is not a power of 2, what is returned is {@code next(31) % n}
     *    with {@code next(31)} values rejected (i.e. regenerated) until a
     *    value that is larger than the remainder of {@code Integer.MAX_VALUE / n}
     *    is generated. Rejection of this initial segment is necessary to ensure
     *    a uniform distribution.</li>
     *  </ul>
     * </p>
     */
    @Override
    public int nextInt(int n) throws IllegalArgumentException {
        if (n > 0) {
            if ((n & -n) == n) {
                return (int) ((n * (long) (nextInt() >>> 1)) >> 31);
            }
            int bits;
            int val;
            do {
                bits = (nextInt() >>> 1);
                val = bits % n;
            } while (bits - val + (n - 1) < 0);
            return val;
        }

        throw new NotStrictlyPositiveException(n);
    }

    /** {@inheritDoc} */
    @Override
    public long nextLong() {
        final long high  = ((long) nextInt()) << 32;
        final long low  = nextInt() & 0xffffffffL;
        return high | low;
    }

    /**
     * Returns a pseudorandom, uniformly distributed {@code long} value
     * between 0 (inclusive) and the specified value (exclusive), drawn from
     * this random number generator's sequence.
     *
     * @param n the bound on the random number to be returned.  Must be
     * positive.
     * @return a pseudorandom, uniformly distributed {@code long} value
     * between 0 (inclusive) and n (exclusive).
     * @throws IllegalArgumentException if n is not positive.
     */
    public long nextLong(long n) {
        if (n > 0) {
            long bits;
            long val;
            do {
                bits = ((long) (nextInt() >>> 1)) << 32;
                bits |= ((long) nextInt()) & 0xffffffffL;
                val  = bits % n;
            } while (bits - val + (n - 1) < 0);
            return val;
        }

        throw new NotStrictlyPositiveException(n);
    }

    /**
     * Clears the cache used by the default implementation of
     * {@link #nextGaussian}.
     */
    public void clear() {
        nextGaussian = Double.NaN;
    }

    /**
     * Generates random bytes and places them into a user-supplied array.
     *
     * <p>
     * The array is filled with bytes extracted from random integers.
     * This implies that the number of random bytes generated may be larger than
     * the length of the byte array.
     * </p>
     *
     * @param bytes Array in which to put the generated bytes. Cannot be {@code null}.
     */
    @Override
    public void nextBytes(byte[] bytes) {
        nextBytesFill(bytes, 0, bytes.length);
    }

    /**
     * Generates random bytes and places them into a user-supplied array.
     *
     * <p>
     * The array is filled with bytes extracted from random integers.
     * This implies that the number of random bytes generated may be larger than
     * the length of the byte array.
     * </p>
     *
     * @param bytes Array in which to put the generated bytes. Cannot be {@code null}.
     * @param start Index at which to start inserting the generated bytes.
     * @param len Number of bytes to insert.
     * @throws OutOfRangeException if {@code start < 0} or {@code start >= bytes.length}.
     * @throws OutOfRangeException if {@code len <= 0} or {@code len > bytes.length - start}.
     */
    public void nextBytes(byte[] bytes,
                          int start,
                          int len) {
        if (start < 0 ||
            start >= bytes.length) {
            throw new OutOfRangeException(start, 0, bytes.length);
        }
        final int max = bytes.length - start;
        if (len <= 0 ||
            len > max) {
            throw new OutOfRangeException(len, 0, max);
        }

        nextBytesFill(bytes, start, len);
    }

    /**
     * Generates random bytes and places them into a user-supplied array.
     *
     * <p>
     * The array is filled with bytes extracted from random integers.
     * This implies that the number of random bytes generated may be larger than
     * the length of the byte array.
     * </p>
     *
     * @param bytes Array in which to put the generated bytes. Cannot be {@code null}.
     * @param position Index at which to start inserting the generated bytes.
     * @param length Number of bytes to insert.
     */
    private void nextBytesFill(byte[] bytes,
                               int position,
                               int length) {
        int index = position; // Index of first insertion.

        // Index of first insertion plus multiple 4 part of length (i.e. length
        // with two least significant bits unset).
        final int indexLoopLimit = index + (length & 0x7ffffffc);

        // Start filling in the byte array, 4 bytes at a time.
        while (index < indexLoopLimit) {
            final int random = nextInt();
            bytes[index++] = (byte) random;
            bytes[index++] = (byte) (random >>> 8);
            bytes[index++] = (byte) (random >>> 16);
            bytes[index++] = (byte) (random >>> 24);
        }

        final int indexLimit = position + length; // Index of last insertion + 1.

        // Fill in the remaining bytes.
        if (index < indexLimit) {
            int random = nextInt();
            while (true) {
                bytes[index++] = (byte) random;
                if (index < indexLimit) {
                    random >>>= 8;
                } else {
                    break;
                }
            }
        }
    }
}
