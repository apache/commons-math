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

import java.util.Arrays;
import org.apache.commons.math4.exception.DimensionMismatchException;

/**
 * Utility for creating number types from one or two {@code int} values
 * or one {@code long} value, or a sequence of bytes.
 */
public final class NumberFactory {
    /** See {@link #makeDouble(long)} */
    private static final long DOUBLE_HIGH_BITS = 0x3ffL << 52;
    /** See {@link #makeFloat(int)} */
    private static final float FLOAT_MULTIPLIER = 0x1.0p-23f;
    /** See {@link #makeDouble(int, int)} */
    private static final double DOUBLE_MULTIPLIER = 0x1.0p-52d;
    /** Lowest byte mask. */
    private static final long LONG_LOWEST_BYTE_MASK = 0xffL;
    /** Number of bytes in a {@code long} */
    private static final int LONG_SIZE = 8;
    /** Lowest byte mask. */
    private static final int INT_LOWEST_BYTE_MASK = 0xff;
    /** Number of bytes in a {@code int} */
    private static final int INT_SIZE = 4;

    /**
     * Class contains only static methods.
     */
    private NumberFactory() {}

    /**
     * @param v Number.
     * @return a boolean.
     */
    public static boolean makeBoolean(int v) {
        return (v >>> 31) != 0;
    }

    /**
     * @param v Number.
     * @return a boolean.
     */
    public static boolean makeBoolean(long v) {
        return (v >>> 63) != 0;
    }

    /**
     * @param v Number.
     * @return a {@code double} value in the interval {@code [0, 1]}.
     */
    public static double makeDouble(long v) {
        // http://xorshift.di.unimi.it
        return Double.longBitsToDouble(DOUBLE_HIGH_BITS | v >>> 12) - 1d;
    }

    /**
     * @param v Number (high order bits).
     * @param w Number (low order bits).
     * @return a {@code double} value in the interval {@code [0, 1]}.
     */
    public static double makeDouble(int v,
                                    int w) {
        final long high = ((long) (v >>> 6)) << 26;
        final int low = w >>> 6;
        return (high | low) * DOUBLE_MULTIPLIER;
    }

    /**
     * @param v Number.
     * @return a {@code float} value in the interval {@code [0, 1]}.
     */
    public static float makeFloat(int v) {
        return (v >>> 9) * FLOAT_MULTIPLIER;
    }

    /**
     * @param v Number (high order bits).
     * @param w Number (low order bits).
     * @return a {@code long} value.
     */
    public static long makeLong(int v,
                                int w) {
        return (((long) v) << 32) | (w & 0xffffffffL);
    }

    /**
     * Creates an {@code int} from a {@code long}.
     *
     * @param v Number.
     * @return an {@code int} value made from the "xor" of the
     * {@link #extractHi(long) high order bits} and
     * {@link #extractLo(long) low order bits} of {@code v}.
     */
    public static int makeInt(long v) {
        return extractHi(v) ^ extractLo(v);
    }

    /**
     * Creates an {@code int} from a {@code long}, using the high order bits.
     * <p>
     * The returned value is such that if
     * <pre><code>
     *  vL = extractLo(v);
     *  vH = extractHi(v);
     * </code></pre>
     * then {@code v} is equal to {@link #makeLong(int,int) makeLong(vH, vL)}.
     * </p>
     *
     * @param v Number.
     * @return an {@code int} value made from the most significant bits
     * of {@code v}.
     */
    public static int extractHi(long v) {
        return (int) (v >>> 32);
    }

    /**
     * Creates an {@code int} from a {@code long}, using the low order bits.
     * <p>
     * The returned value is such that if
     * <pre><code>
     *  vL = extractLo(v);
     *  vH = extractHi(v);
     * </code></pre>
     * then {@code v} is equal to {@link #makeLong(int,int) makeLong(vH, vL)}.
     * </p>
     *
     * @param v Number.
     * @return an {@code int} value made from the least significant bits
     * of {@code v}.
     */
    public static int extractLo(long v) {
        return (int) v;
    }

    /**
     * Splits a {@code long} into 8 bytes.
     *
     * @param v Value.
     * @return the bytes that compose the given value (least-significant
     * byte first).
     */
    public static byte[] makeByteArray(long v) {
        final byte[] b = new byte[LONG_SIZE];

        for (int i = 0; i < LONG_SIZE; i++) {
            final int shift = i * 8;
            b[i] = (byte) ((v >>> shift) & LONG_LOWEST_BYTE_MASK);
        }

        return b;
    }

    /**
     * Creates a {@code long} from 8 bytes.
     *
     * @param input Input.
     * @return the value that correspond to the given bytes assuming
     * that the is ordered in increasing byte significance (i.e. the
     * first byte in the array is the least-siginficant).
     * @throws DimensionMismatchException if {@code input.length != 8}.
     */
    public static long makeLong(byte[] input) {
        if (input.length != LONG_SIZE) {
            throw new DimensionMismatchException(input.length, LONG_SIZE);
        }

        long v = 0;
        for (int i = 0; i < LONG_SIZE; i++) {
            final int shift = i * 8;
            v |= (((long) input[i]) & LONG_LOWEST_BYTE_MASK) << shift;
        }

        return v;
    }

    /**
     * Splits an array of {@code long} values into a sequence of bytes.
     * This method calls {@link #makeByteArray(long)} for each element of
     * the {@code input}.
     *
     * @param input Input.
     * @return an array of bytes.
     */
    public static byte[] makeByteArray(long[] input) {
        final int size = input.length * LONG_SIZE;
        final byte[] b = new byte[size];

        for (int i = 0; i < input.length; i++) {
            final byte[] current = makeByteArray(input[i]);
            System.arraycopy(current, 0, b, i * LONG_SIZE, LONG_SIZE);
        }

        return b;
    }

    /**
     * Creates an array of {@code long} values from a sequence of bytes.
     * This method calls {@link #makeLong(byte[])} for each subsequence
     * of 8 bytes.
     *
     * @param input Input.
     * @return an array of {@code long}.
     * @throws DimensionMismatchException if {@code input.length} is not
     * a multiple of 8.
     */
    public static long[] makeLongArray(byte[] input) {
        final int size = input.length;
        final int num = size / LONG_SIZE;
        if (num * LONG_SIZE != size) {
            throw new DimensionMismatchException(size, num * LONG_SIZE);
        }

        final long[] output = new long[num];
        for (int i = 0; i < num; i++) {
            final int from = i * LONG_SIZE;
            final byte[] current = Arrays.copyOfRange(input, from, from + LONG_SIZE);
            output[i] = makeLong(current);
        }

        return output;
    }

    /**
     * Splits an {@code int} into 4 bytes.
     *
     * @param v Value.
     * @return the bytes that compose the given value (least-significant
     * byte first).
     */
    public static byte[] makeByteArray(int v) {
        final byte[] b = new byte[INT_SIZE];

        for (int i = 0; i < INT_SIZE; i++) {
            final int shift = i * 8;
            b[i] = (byte) ((v >>> shift) & INT_LOWEST_BYTE_MASK);
        }

        return b;
    }

    /**
     * Creates an {@code int} from 4 bytes.
     *
     * @param input Input.
     * @return the value that correspond to the given bytes assuming
     * that the is ordered in increasing byte significance (i.e. the
     * first byte in the array is the least-siginficant).
     * @throws DimensionMismatchException if {@code input.length != 4}.
     */
    public static int makeInt(byte[] input) {
        if (input.length != INT_SIZE) {
            throw new DimensionMismatchException(input.length, INT_SIZE);
        }

        int v = 0;
        for (int i = 0; i < INT_SIZE; i++) {
            final int shift = i * 8;
            v |= (((int) input[i]) & INT_LOWEST_BYTE_MASK) << shift;
        }

        return v;
    }

    /**
     * Splits an array of {@code int} values into a sequence of bytes.
     * This method calls {@link #makeByteArray(int)} for each element of
     * the {@code input}.
     *
     * @param input Input.
     * @return an array of bytes.
     */
    public static byte[] makeByteArray(int[] input) {
        final int size = input.length * INT_SIZE;
        final byte[] b = new byte[size];

        for (int i = 0; i < input.length; i++) {
            final byte[] current = makeByteArray(input[i]);
            System.arraycopy(current, 0, b, i * INT_SIZE, INT_SIZE);
        }

        return b;
    }

    /**
     * Creates an array of {@code int} values from a sequence of bytes.
     * This method calls {@link #makeInt(byte[])} for each subsequence
     * of 4 bytes.
     *
     * @param input Input. Length must be a multiple of 4.
     * @return an array of {@code int}.
     * @throws DimensionMismatchException if {@code input.length} is not
     * a multiple of 4.
     */
    public static int[] makeIntArray(byte[] input) {
        final int size = input.length;
        final int num = size / INT_SIZE;
        if (num * INT_SIZE != size) {
            throw new DimensionMismatchException(size, num * INT_SIZE);
        }

        final int[] output = new int[num];
        for (int i = 0; i < num; i++) {
            final int from = i * INT_SIZE;
            final byte[] current = Arrays.copyOfRange(input, from, from + INT_SIZE);
            output[i] = makeInt(current);
        }

        return output;
    }
}
