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
package org.apache.commons.math.random;

import java.io.Serializable;


/** This abstract class implements the WELL class of pseudo-random number generator
 * from Fran&ccedil;ois Panneton, Pierre L'Ecuyer and Makoto Matsumoto.

 * <p>This generator is described in a paper by Fran&ccedil;ois Panneton,
 * Pierre L'Ecuyer and Makoto Matsumoto <a
 * href="http://www.iro.umontreal.ca/~lecuyer/myftp/papers/wellrng.pdf">Improved
 * Long-Period Generators Based on Linear Recurrences Modulo 2</a> ACM
 * Transactions on Mathematical Software, 32, 1 (2006).</p>

 * @see <a href="http://www.iro.umontreal.ca/~panneton/WELLRNG.html">WELL Random number generator</a>
 * @version $Revision$ $Date$
 * @since 2.2

 */
public abstract class AbstractWell extends BitsStreamGenerator implements Serializable {

    /** Serializable version identifier. */
    private static final long serialVersionUID = -8068371019303673353L;

    /** Number of bits blocks in the pool. */
    private final int r;

    /** Bit mask preserving the first w - p bits in a w bits block. */
    private final int mp;

    /** Bit mask preserving the last p bits in a w bits block. */
    private final int mpTilde;

    /** First parameter of the algorithm. */
    private final int m1;

    /** Second parameter of the algorithm. */
    private final int m2;

    /** Third parameter of the algorithm. */
    private final int m3;

    /** Current index in the bytes pool. */
    private int index;

    /** Bytes pool. */
    private final int[] v;

    /** Creates a new random number generator.
     * <p>The instance is initialized using the current time as the
     * seed.</p>
     * @param k number of bits in the pool (not necessarily a multiple of 32)
     * @param m1 first parameter of the algorithm
     * @param m2 second parameter of the algorithm
     * @param m3 third parameter of the algorithm
     */
    protected AbstractWell(final int k, final int m1, final int m2, final int m3) {
        this(k, m1, m2, m3, System.currentTimeMillis());
    }

    /** Creates a new random number generator using a single int seed.
     * @param k number of bits in the pool (not necessarily a multiple of 32)
     * @param m1 first parameter of the algorithm
     * @param m2 second parameter of the algorithm
     * @param m3 third parameter of the algorithm
     * @param seed the initial seed (32 bits integer)
     */
    protected AbstractWell(final int k, final int m1, final int m2, final int m3, final int seed) {
        this(k, m1, m2, m3, new int[] { seed });
    }

    /** Creates a new random number generator using an int array seed.
     * @param k number of bits in the pool (not necessarily a multiple of 32)
     * @param m1 first parameter of the algorithm
     * @param m2 second parameter of the algorithm
     * @param m3 third parameter of the algorithm
     * @param seed the initial seed (32 bits integers array), if null
     * the seed of the generator will be related to the current time
     */
    protected AbstractWell(final int k, final int m1, final int m2, final int m3, final int[] seed) {

        // the bits pool contains k bits, k = r w - p where r is the number
        // of w bits blocks, w is the block size (always 32 in the original paper)
        // and p is the number of unused bits in the last block
        final int w = 32;
        this.r      = (k + w - 1) / w;
        final int p = r * w - k;

        // set up  generator parameters
        this.mp      = (-1) << p;
        this.mpTilde = ~mp;
        this.m1      = m1;
        this.m2      = m2;
        this.m3      = m3;
        this.v       = new int[r];
        this.index   = 0;

        // initialize the pool content
        setSeed(seed);

    }

    /** Creates a new random number generator using a single long seed.
     * @param k number of bits in the pool (not necessarily a multiple of 32)
     * @param m1 first parameter of the algorithm
     * @param m2 second parameter of the algorithm
     * @param m3 third parameter of the algorithm
     * @param seed the initial seed (64 bits integer)
     */
    protected AbstractWell(final int k, final int m1, final int m2, final int m3, final long seed) {
        this(k, m1, m2, m3, new int[] { (int) (seed >>> 32), (int) (seed & 0xffffffffl) });
    }

    /** Reinitialize the generator as if just built with the given int seed.
     * <p>The state of the generator is exactly the same as a new
     * generator built with the same seed.</p>
     * @param seed the initial seed (32 bits integer)
     */
    public void setSeed(final int seed) {
        setSeed(new int[] { seed });
    }

    /** Reinitialize the generator as if just built with the given int array seed.
     * <p>The state of the generator is exactly the same as a new
     * generator built with the same seed.</p>
     * @param seed the initial seed (32 bits integers array), if null
     * the seed of the generator will be related to the current time
     */
    public void setSeed(final int[] seed) {

        if (seed == null) {
            setSeed(System.currentTimeMillis());
            return;
        }

        System.arraycopy(seed, 0, v, 0, Math.min(seed.length, v.length));

        if (seed.length < v.length) {
            for (int i = seed.length; i < v.length; ++i) {
                final long l = v[i - seed.length];
                v[i] = (int) ((1812433253l * (l ^ (l >> 30)) + i) & 0xffffffffL);
            }
        }

        index = 0;

    }

    /** Reinitialize the generator as if just built with the given long seed.
     * <p>The state of the generator is exactly the same as a new
     * generator built with the same seed.</p>
     * @param seed the initial seed (64 bits integer)
     */
    public void setSeed(final long seed) {
        setSeed(new int[] { (int) (seed >>> 32), (int) (seed & 0xffffffffl) });
    }

    /** Generate next pseudorandom number.
     * <p>This method is the core generation algorithm. It is used by all the
     * public generation methods for the various primitive types {@link
     * #nextBoolean()}, {@link #nextBytes(byte[])}, {@link #nextDouble()},
     * {@link #nextFloat()}, {@link #nextGaussian()}, {@link #nextInt()},
     * {@link #next(int)} and {@link #nextLong()}.</p>
     * <p>This implementation is the general WELL algorithm, described in
     * a paper by Fran&ccedil;ois Panneton, Pierre L'Ecuyer and Makoto Matsumoto
     * <a href="http://www.iro.umontreal.ca/~lecuyer/myftp/papers/wellrng.pdf">Improved
     *  Long-Period Generators Based on Linear Recurrences Modulo 2</a> ACM
     *  Transactions on Mathematical Software, 32, 1 (2006).</p>
     * @param bits number of random bits to produce
     * @return random bits generated
     */
    protected int next(final int bits) {

        final int iRm1   = (index + r - 1) % r;
        final int iRm2   = (index + r - 2) % r;
        final int i1     = (index + m1) % r;
        final int i2     = (index + m2) % r;
        final int i3     = (index + m3) % r;

        final int z0 = (mp & v[iRm1]) ^ (mpTilde & v[iRm2]);
        final int z1 = t0(v[index])   ^ t1(v[i1]);
        final int z2 = t2(v[i2])      ^ t3(v[i3]);
        final int z3 = z1 ^ z2;
        final int z4 = t4(z0) ^ t5(z1) ^ t6(z2) ^ t7(z3);

        v[index] = z3;
        v[iRm1]  = z4;
        v[iRm2] &= mp;
        index    = iRm1;

        return z4 >>> (32 - bits);

    }

    /** Apply transform M<sub>0</sub> to a bits block.
     * @param x bits block to apply transform to
     * @return M<sub>0</sub>(x)
     */
    protected int m0(final int x) {
        return 0;
    }

    /** Apply transform M<sub>1</sub> to a bits block.
     * @param x bits block to apply transform to
     * @return M<sub>1</sub>(x)
     */
    protected int m1(final int x) {
        return x;
    }

    /** Apply transform M<sub>2</sub> to a bits block.
     * @param t parameter of the transform
     * @param x bits block to apply transform to
     * @return M<sub>2, t</sub>(x)
     */
    protected int m2(final int t, final int x) {
        return (t >= 0) ? (x >>> t) : (x << -t);
    }

    /** Apply transform M<sub>3</sub> to a bits block.
     * @param t parameter of the transform
     * @param x bits block to apply transform to
     * @return M<sub>3, t</sub>(x)
     */
    protected int m3(final int t, final int x) {
        return x ^ ((t >= 0) ? (x >>> t) : (x << -t));
    }

    /** Apply transform M<sub>4</sub> to a bits block.
     * @param a parameter of the transform
     * @param x bits block to apply transform to
     * @return M<sub>4, a</sub>(x)
     */
    protected int m4(final int a, final int x) {
        final int shiftedX = x >>> 1;
        return ((x & 0x80000000) != 0) ? (shiftedX ^ a) : shiftedX;
    }

    /** Apply transform M<sub>5</sub> to a bits block.
     * @param t first parameter of the transform
     * @param b second parameter of the transform
     * @param x bits block to apply transform to
     * @return M<sub>5, t, b</sub>(x)
     */
    protected int m5(final int t, final int b, final int x) {
        // table I of the paper specifies that a left shift for positive t and
        // a right shift for negative t, however, reference implementation does
        // the opposite (and in fact this transform is used only by Well512a
        // with t = -28). Here, we follow the reference implementation with a
        // left shift for NEGATIVE t
        final int shiftedX = (t >= 0) ? (x >>> t) : (x << -t);
        return x ^ (shiftedX & b);
    }

    /** Apply transform M<sub>6</sub> to a bits block.
     * @param q first parameter of the transform
     * @param dsMask second parameter of the transform as a bit mask
     * @param tMask third parameter of the transform as a bit mask
     * @param a fourth parameter of the transform
     * @param x bits block to apply transform to
     * @return M<sub>6, q, s, t, a</sub>(x)
     */
    protected int m6(final int q, final int dsMask, final int tMask, final int a, final int x) {
        final int lShiftedX = x << q;
        final int rShiftedX = x >>> (32 - q);
        final int z         = (lShiftedX ^ rShiftedX) & dsMask;
        return ((x & tMask) != 0) ? (z ^ a) : z;
    }

    /** Apply transform T<sub>0</sub> to a bits block.
     * @param vi0 bits block to apply transform to
     * @return T<sub>0</sub> v<sub>i,0</sub>
     */
    protected abstract int t0(int vi0);

    /** Apply transform T<sub>1</sub> to a bits block.
     * @param vim1 bits block to apply transform to
     * @return T<sub>1</sub> v<sub>i,m1</sub>
     */
    protected abstract int t1(int vim1);

    /** Apply transform T<sub>2</sub> to a bits block.
     * @param vim2 bits block to apply transform to
     * @return T<sub>2</sub> v<sub>i,m2</sub>
     */
    protected abstract int t2(int vim2);

    /** Apply transform T<sub>3</sub> to a bits block.
     * @param vim3 bits block to apply transform to
     * @return T<sub>3</sub> v<sub>i,m3</sub>
     */
    protected abstract int t3(int vim3);

    /** Apply transform T<sub>4</sub> to a bits block.
     * @param z0 bits block to apply transform to
     * @return T<sub>4</sub> z<sub>0</sub>
     */
    protected abstract int t4(int z0);

    /** Apply transform T<sub>5</sub> to a bits block.
     * @param z1 bits block to apply transform to
     * @return T<sub>5</sub> z<sub>1</sub>
     */
    protected abstract int t5(int z1);

    /** Apply transform T<sub>6</sub> to a bits block.
     * @param z2 bits block to apply transform to
     * @return T<sub>6</sub> z<sub>2</sub>
     */
    protected abstract int t6(int z2);

    /** Apply transform T<sub>7</sub> to a bits block.
     * @param z3 bits block to apply transform to
     * @return T<sub>7</sub> z<sub>3</sub>
     */
    protected abstract int t7(int z3);

}
