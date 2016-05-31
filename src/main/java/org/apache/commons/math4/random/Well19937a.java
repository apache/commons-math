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


/** This class implements the WELL19937a pseudo-random number generator
 * from Fran&ccedil;ois Panneton, Pierre L'Ecuyer and Makoto Matsumoto.
 * <p>
 * This generator is described in a paper by Fran&ccedil;ois Panneton,
 * Pierre L'Ecuyer and Makoto Matsumoto <a
 * href="http://www.iro.umontreal.ca/~lecuyer/myftp/papers/wellrng.pdf">Improved
 * Long-Period Generators Based on Linear Recurrences Modulo 2</a> ACM
 * Transactions on Mathematical Software, 32, 1 (2006). The errata for the paper
 * are in <a href="http://www.iro.umontreal.ca/~lecuyer/myftp/papers/wellrng-errata.txt">wellrng-errata.txt</a>.</p>
 *
 * @see <a href="http://www.iro.umontreal.ca/~panneton/WELLRNG.html">WELL Random number generator</a>
 * @since 2.2
 */
public class Well19937a extends AbstractWell {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 20150223L;

    /** Number of bits in the pool. */
    private static final int K = 19937;

    /** First parameter of the algorithm. */
    private static final int M1 = 70;

    /** Second parameter of the algorithm. */
    private static final int M2 = 179;

    /** Third parameter of the algorithm. */
    private static final int M3 = 449;

    /** The indirection index table. */
    private static final IndexTable TABLE = new IndexTable(K, M1, M2, M3);

    /** Creates a new random number generator.
     * <p>The instance is initialized using the current time as the
     * seed.</p>
     */
    public Well19937a() {
        super(K);
    }

    /** Creates a new random number generator using a single int seed.
     * @param seed the initial seed (32 bits integer)
     */
    public Well19937a(int seed) {
        super(K, seed);
    }

    /** Creates a new random number generator using an int array seed.
     * @param seed the initial seed (32 bits integers array), if null
     * the seed of the generator will be related to the current time
     */
    public Well19937a(int[] seed) {
        super(K, seed);
    }

    /** Creates a new random number generator using a single long seed.
     * @param seed the initial seed (64 bits integer)
     */
    public Well19937a(long seed) {
        super(K, seed);
    }

    /** {@inheritDoc} */
    @Override
    protected int next(final int bits) {

        final int indexRm1 = TABLE.getIndexPred(index);
        final int indexRm2 = TABLE.getIndexPred2(index);

        final int v0       = v[index];
        final int vM1      = v[TABLE.getIndexM1(index)];
        final int vM2      = v[TABLE.getIndexM2(index)];
        final int vM3      = v[TABLE.getIndexM3(index)];

        final int z0 = (0x80000000 & v[indexRm1]) ^ (0x7FFFFFFF & v[indexRm2]);
        final int z1 = (v0 ^ (v0 << 25))  ^ (vM1 ^ (vM1 >>> 27));
        final int z2 = (vM2 >>> 9) ^ (vM3 ^ (vM3 >>> 1));
        final int z3 = z1      ^ z2;
        final int z4 = z0 ^ (z1 ^ (z1 << 9)) ^ (z2 ^ (z2 << 21)) ^ (z3 ^ (z3 >>> 21));

        v[index]     = z3;
        v[indexRm1]  = z4;
        v[indexRm2] &= 0x80000000;
        index        = indexRm1;

        return z4 >>> (32 - bits);

    }

}
