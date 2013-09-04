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
package org.apache.commons.math3.util;

import java.util.Iterator;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the {@link Combinations} class.
 *
 * @version $Id$
 */
public class CombinationsTest {    
    @Test
    public void testLexicographicIterator() {
        checkLexicographicIterator(new Combinations(5, 3), 5, 3);
        checkLexicographicIterator(new Combinations(6, 4), 6, 4);
        checkLexicographicIterator(new Combinations(8, 2), 8, 2);
        checkLexicographicIterator(new Combinations(6, 1), 6, 1);
        checkLexicographicIterator(new Combinations(3, 3), 3, 3);
        checkLexicographicIterator(new Combinations(1, 1), 1, 1);
        checkLexicographicIterator(new Combinations(1, 0), 1, 0);
        checkLexicographicIterator(new Combinations(0, 0), 0, 0);
        checkLexicographicIterator(new Combinations(4, 2), 4, 2);
        checkLexicographicIterator(new Combinations(123, 2), 123, 2);
    }

    @Test
    public void testEmptyCombination() {
        final Iterator<int[]> iter = new Combinations(12345, 0).iterator();
        Assert.assertTrue(iter.hasNext());
        final int[] c = iter.next();
        Assert.assertEquals(0, c.length);
        Assert.assertFalse(iter.hasNext());
    }

    @Test
    public void testFullSetCombination() {
        final int n = 67;
        final Iterator<int[]> iter = new Combinations(n, n).iterator();
        Assert.assertTrue(iter.hasNext());
        final int[] c = iter.next();
        Assert.assertEquals(n, c.length);

        for (int i = 0; i < n; i++) {
            Assert.assertEquals(i, c[i]);
        }

        Assert.assertFalse(iter.hasNext());
    }

    /**
     * Verifies that the iterator generates a lexicographically
     * increasing sequence of b(n,k) arrays, each having length k
     * and each array itself increasing.
     * 
     * @param c Combinations.
     * @param n Size of universe.
     * @param k Size of subsets.
     */
    private void checkLexicographicIterator(Iterable<int[]> c,
                                            int n,
                                            int k) {
        long lastLex = -1;
        long length = 0;
        for (int[] iterate : c) {
            Assert.assertEquals(k, iterate.length);
            final long curLex = lexNorm(iterate, n);
            Assert.assertTrue(curLex > lastLex);
            lastLex = curLex;
            length++;
            for (int i = 1; i < iterate.length; i++) {
                Assert.assertTrue(iterate[i] > iterate[i - 1]);
            }
        }
        Assert.assertEquals(CombinatoricsUtils.binomialCoefficient(n, k), length);
    }
    
    @Test
    public void testCombinationsIteratorFail() {
        try {
            new Combinations(4, 5).iterator();
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }

        try {
            new Combinations(-1, -2).iterator();
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }
    }
    
    /**
     * Returns the value represented by the digits in the input array in reverse order.
     * For example [3,2,1] returns 123.
     * 
     * @param iterate input array
     * @param n size of universe
     * @return lexicographic norm
     */
    private long lexNorm(int[] iterate, int n) {
        long ret = 0;
        for (int i = iterate.length - 1; i >= 0; i--) {
            ret += iterate[i] * ArithmeticUtils.pow(n, (long) i);
        }
        return ret;
    }
}
