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
import java.util.Comparator;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the {@link Combinations} class.
 *
 */
public class CombinationsTest {
    @Test
    public void testAccessor1() {
        final int n = 5;
        final int k = 3;
        Assert.assertEquals(n, new Combinations(n, k).getN());
    }
    @Test
    public void testAccessor2() {
        final int n = 5;
        final int k = 3;
        Assert.assertEquals(k, new Combinations(n, k).getK());
    }

    @Test
    public void testLexicographicIterator() {
        checkLexicographicIterator(new Combinations(5, 3));
        checkLexicographicIterator(new Combinations(6, 4));
        checkLexicographicIterator(new Combinations(8, 2));
        checkLexicographicIterator(new Combinations(6, 1));
        checkLexicographicIterator(new Combinations(3, 3));
        checkLexicographicIterator(new Combinations(1, 1));
        checkLexicographicIterator(new Combinations(1, 0));
        checkLexicographicIterator(new Combinations(0, 0));
        checkLexicographicIterator(new Combinations(4, 2));
        checkLexicographicIterator(new Combinations(123, 2));
    }

    @Test(expected=DimensionMismatchException.class)
    public void testLexicographicComparatorWrongIterate1() {
        final int n = 5;
        final int k = 3;
        final Comparator<int[]> comp = new Combinations(n, k).comparator();
        comp.compare(new int[] {1}, new int[] {0, 1, 2});
    }

    @Test(expected=DimensionMismatchException.class)
    public void testLexicographicComparatorWrongIterate2() {
        final int n = 5;
        final int k = 3;
        final Comparator<int[]> comp = new Combinations(n, k).comparator();
        comp.compare(new int[] {0, 1, 2}, new int[] {0, 1, 2, 3});
    }

    @Test(expected=OutOfRangeException.class)
    public void testLexicographicComparatorWrongIterate3() {
        final int n = 5;
        final int k = 3;
        final Comparator<int[]> comp = new Combinations(n, k).comparator();
        comp.compare(new int[] {1, 2, 5}, new int[] {0, 1, 2});
    }

    @Test(expected=OutOfRangeException.class)
    public void testLexicographicComparatorWrongIterate4() {
        final int n = 5;
        final int k = 3;
        final Comparator<int[]> comp = new Combinations(n, k).comparator();
        comp.compare(new int[] {1, 2, 4}, new int[] {-1, 1, 2});
    }

    @Test
    public void testLexicographicComparator() {
        final int n = 5;
        final int k = 3;
        final Comparator<int[]> comp = new Combinations(n, k).comparator();
        Assert.assertEquals(1, comp.compare(new int[] {1, 2, 4},
                                            new int[] {1, 2, 3}));
        Assert.assertEquals(-1, comp.compare(new int[] {0, 1, 4},
                                             new int[] {0, 2, 4}));
        Assert.assertEquals(0, comp.compare(new int[] {1, 3, 4},
                                            new int[] {1, 3, 4}));
    }

    /**
     * Check that iterates can be passed unsorted.
     */
    @Test
    public void testLexicographicComparatorUnsorted() {
        final int n = 5;
        final int k = 3;
        final Comparator<int[]> comp = new Combinations(n, k).comparator();
        Assert.assertEquals(1, comp.compare(new int[] {1, 4, 2},
                                            new int[] {1, 3, 2}));
        Assert.assertEquals(-1, comp.compare(new int[] {0, 4, 1},
                                             new int[] {0, 4, 2}));
        Assert.assertEquals(0, comp.compare(new int[] {1, 4, 3},
                                            new int[] {1, 3, 4}));
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
     */
    private void checkLexicographicIterator(Combinations c) {
        final Comparator<int[]> comp = c.comparator();
        final int n = c.getN();
        final int k = c.getK();

        int[] lastIterate = null;

        long numIterates = 0;
        for (int[] iterate : c) {
            Assert.assertEquals(k, iterate.length);

            // Check that the sequence of iterates is ordered.
            if (lastIterate != null) {
                Assert.assertTrue(comp.compare(iterate, lastIterate) == 1);
            }

            // Check that each iterate is ordered.
            for (int i = 1; i < iterate.length; i++) {
                Assert.assertTrue(iterate[i] > iterate[i - 1]);
            }

            lastIterate = iterate;
            ++numIterates;
        }

        // Check the number of iterates.
        Assert.assertEquals(CombinatoricsUtils.binomialCoefficient(n, k),
                            numIterates);
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
}
