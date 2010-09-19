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


/** This class implements the WELL19937a pseudo-random number generator
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
public class Well19937a extends AbstractWell {

    /** Serializable version identifier. */
    private static final long serialVersionUID = -8052371714518610855L;

    /** Number of bits in the pool. */
    private static final int K = 19937;

    /** First parameter of the algorithm. */
    private static final int M1 = 70;

    /** Second parameter of the algorithm. */
    private static final int M2 = 179;

    /** Third parameter of the algorithm. */
    private static final int M3 = 449;

    /** Creates a new random number generator.
     * <p>The instance is initialized using the current time as the
     * seed.</p>
     */
    public Well19937a() {
        super(K, M1, M2, M3);
    }

    /** Creates a new random number generator using a single int seed.
     * @param seed the initial seed (32 bits integer)
     */
    public Well19937a(int seed) {
        super(K, M1, M2, M3, seed);
    }

    /** Creates a new random number generator using an int array seed.
     * @param seed the initial seed (32 bits integers array), if null
     * the seed of the generator will be related to the current time
     */
    public Well19937a(int[] seed) {
        super(K, M1, M2, M3, seed);
    }

    /** Creates a new random number generator using a single long seed.
     * @param seed the initial seed (64 bits integer)
     */
    public Well19937a(long seed) {
        super(K, M1, M2, M3, seed);
    }

    /** {@inheritDoc} */
    protected int t0(final int vi0) {
        return m3(-25, vi0);
    }

    /** {@inheritDoc} */
    protected int t1(final int vim1) {
        return m3(27, vim1);
    }

    /** {@inheritDoc} */
    protected int t2(final int vim2) {
        return m2(9, vim2);
    }

    /** {@inheritDoc} */
    protected int t3(final int vim3) {
        return m3(1, vim3);
    }

    /** {@inheritDoc} */
    protected int t4(final int z0) {
        return m1(z0);
    }

    /** {@inheritDoc} */
    protected int t5(final int z1) {
        return m3(-9, z1);
    }

    /** {@inheritDoc} */
    protected int t6(final int z2) {
        return m3(-21, z2);
    }

    /** {@inheritDoc} */
    protected int t7(final int z3) {
        return m3(21, z3);
    }

}
