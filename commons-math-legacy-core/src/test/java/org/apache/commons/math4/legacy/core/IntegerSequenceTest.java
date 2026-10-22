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
package org.apache.commons.math4.legacy.core;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.math4.legacy.exception.MaxCountExceededException;
import org.apache.commons.math4.legacy.exception.TooManyEvaluationsException;
import org.apache.commons.math4.legacy.exception.NotStrictlyPositiveException;
import org.apache.commons.math4.legacy.exception.ZeroException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link IntegerSequence} and {@link IntegerSequence.Incrementor}.
 */
public class IntegerSequenceTest {
    @Test
    public void testRangeMultipleIterations() {
        // Check that we can iterate several times using the same instance.
        final int start = 1;
        final int max = 7;
        final int step = 2;

        final List<Integer> seq = new ArrayList<>();
        final IntegerSequence.Range r = IntegerSequence.range(start, max, step);

        final int numTimes = 3;
        for (int n = 0; n < numTimes; n++) {
            seq.clear();
            for (Integer i : r) {
                seq.add(i);
            }
            Assert.assertEquals(4, seq.size());
            Assert.assertEquals(seq.size(), r.size());
        }
    }

    @Test
    public void testIncreasingRange() {
        final int start = 1;
        final int max = 7;
        final int step = 2;

        final List<Integer> seq = new ArrayList<>();
        final IntegerSequence.Range r = IntegerSequence.range(start, max, step);
        for (Integer i : r) {
            seq.add(i);
        }

        Assert.assertEquals(4, seq.size());
        Assert.assertEquals(seq.size(), r.size());
        for (int i = 0; i < seq.size(); i++) {
            Assert.assertEquals(start + i * step, seq.get(i).intValue());
        }
    }

    @Test
    public void testIncreasingRangeNegativeEnd() {
        final int start = -10;
        final int max = -1;
        final int step = 2;

        final List<Integer> seq = new ArrayList<>();
        final IntegerSequence.Range r = IntegerSequence.range(start, max, step);
        for (Integer i : r) {
            seq.add(i);
        }

        Assert.assertEquals(5, seq.size());
        Assert.assertEquals(seq.size(), r.size());
        for (int i = 0; i < seq.size(); i++) {
            Assert.assertEquals(start + i * step, seq.get(i).intValue());
        }
    }

    @Test
    public void testDecreasingRange() {
        final int start = 10;
        final int max = -8;
        final int step = -3;

        final List<Integer> seq = new ArrayList<>();
        final IntegerSequence.Range r = IntegerSequence.range(start, max, step);
        for (Integer i : r) {
            seq.add(i);
        }

        Assert.assertEquals(7, seq.size());
        Assert.assertEquals(seq.size(), r.size());
        for (int i = 0; i < seq.size(); i++) {
            Assert.assertEquals(start + i * step, seq.get(i).intValue());
        }
    }

    @Test
    public void testSingleElementRange() {
        final int start = 1;
        final int max = 1;
        final int step = -1;

        final List<Integer> seq = new ArrayList<>();
        final IntegerSequence.Range r = IntegerSequence.range(start, max, step);
        for (Integer i : r) {
            seq.add(i);
        }

        Assert.assertEquals(1, seq.size());
        Assert.assertEquals(seq.size(), r.size());
        Assert.assertEquals(start, seq.get(0).intValue());
    }

    @Test
    public void testBasicRange() {
        final int start = -2;
        final int end = 4;

        final List<Integer> seq = new ArrayList<>();
        for (Integer i : IntegerSequence.range(start, end)) {
            seq.add(i);
        }

        for (int i = start; i <= end; i++) {
            Assert.assertEquals(i, seq.get(i - start).intValue());
        }
    }

    @Test
    public void testEmptyRange() {
        final int start = 2;
        final int end = 0;

        final List<Integer> seq = new ArrayList<>();
        final IntegerSequence.Range r = IntegerSequence.range(start, end);
        for (Integer i : r) {
            seq.add(i);
        }

        Assert.assertEquals(0, seq.size());
        Assert.assertEquals(seq.size(), r.size());
    }

    @Test
    public void testEmptyRangeNegativeStart() {
        final int start = -2;
        final int max = -1;
        final int step = -1;

        final List<Integer> seq = new ArrayList<>();
        final IntegerSequence.Range r = IntegerSequence.range(start, max, step);
        for (Integer i : r) {
            seq.add(i);
        }

        Assert.assertEquals(0, seq.size());
        Assert.assertEquals(seq.size(), r.size());
    }

    @Test
    public void testRangeAtMaximumInteger() {
        final IntegerSequence.Range range = IntegerSequence.range(Integer.MAX_VALUE, Integer.MAX_VALUE);
        final Iterator<Integer> iterator = range.iterator();

        Assert.assertEquals(1, range.size());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(Integer.MAX_VALUE, iterator.next().intValue());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testDescendingRangeAtMinimumInteger() {
        final IntegerSequence.Range range = IntegerSequence.range(Integer.MIN_VALUE, Integer.MIN_VALUE, -1);
        final Iterator<Integer> iterator = range.iterator();

        Assert.assertEquals(1, range.size());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(Integer.MIN_VALUE, iterator.next().intValue());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testRangeUsesIncrementor() {
        Assert.assertTrue(IntegerSequence.range(0, 1).iterator() instanceof IntegerSequence.Incrementor);
    }

    @Test
    public void testRangeIteratorExhaustion() {
        assertIteratorSequence(IntegerSequence.range(Integer.MAX_VALUE - 2, Integer.MAX_VALUE, 2).iterator(),
                               Integer.MAX_VALUE - 2, Integer.MAX_VALUE);
        assertIteratorSequence(IntegerSequence.range(Integer.MIN_VALUE + 2, Integer.MIN_VALUE, -2).iterator(),
                               Integer.MIN_VALUE + 2, Integer.MIN_VALUE);
        assertIteratorSequence(IntegerSequence.range(Integer.MAX_VALUE - 1, Integer.MAX_VALUE, 2).iterator(),
                               Integer.MAX_VALUE - 1);
        assertIteratorSequence(IntegerSequence.range(Integer.MIN_VALUE + 1, Integer.MIN_VALUE, -2).iterator(),
                               Integer.MIN_VALUE + 1);
    }

    @Test
    public void testIncrementorIteratorOverflow() {
        assertIteratorSequence(IntegerSequence.Incrementor.create()
                               .withStart(Integer.MAX_VALUE - 1)
                               .withMaximalCount(Integer.MAX_VALUE)
                               .withIncrement(2), Integer.MAX_VALUE - 1);
        assertIteratorSequence(IntegerSequence.Incrementor.create()
                               .withStart(Integer.MIN_VALUE + 1)
                               .withMaximalCount(Integer.MIN_VALUE)
                               .withIncrement(-2), Integer.MIN_VALUE + 1);
    }

    @Test
    public void testIncrementorCanIncrementOverflow() {
        final IntegerSequence.Incrementor increasing = IntegerSequence.Incrementor.create()
            .withStart(Integer.MAX_VALUE - 1)
            .withMaximalCount(Integer.MAX_VALUE)
            .withIncrement(2);
        final IntegerSequence.Incrementor decreasing = IntegerSequence.Incrementor.create()
            .withStart(Integer.MIN_VALUE + 1)
            .withMaximalCount(Integer.MIN_VALUE)
            .withIncrement(-2);

        Assert.assertFalse(increasing.canIncrement());
        Assert.assertFalse(decreasing.canIncrement());
        Assert.assertFalse(increasing.canIncrement(Integer.MAX_VALUE));
        Assert.assertFalse(decreasing.canIncrement(Integer.MAX_VALUE));
    }

    @Test
    public void testIncrementorIncrementOverflow() {
        assertIncrementOverflow(Integer.MAX_VALUE - 1, Integer.MAX_VALUE, 2, 1);
        assertIncrementOverflow(Integer.MIN_VALUE + 1, Integer.MIN_VALUE, -2, 1);
        assertIncrementOverflow(0, Integer.MAX_VALUE, 2, Integer.MAX_VALUE);
        assertIncrementOverflow(0, Integer.MIN_VALUE, -2, Integer.MAX_VALUE);
    }

    @Test
    public void testIncrementorCallbackOverflow() {
        final List<Integer> limits = new ArrayList<>();
        final IntegerSequence.Incrementor inc = IntegerSequence.Incrementor.create()
            .withStart(Integer.MAX_VALUE - 1)
            .withMaximalCount(Integer.MAX_VALUE)
            .withIncrement(2)
            .withCallback(limits::add);

        inc.increment();
        Assert.assertEquals(1, limits.size());
        Assert.assertEquals(Integer.MAX_VALUE, limits.get(0).intValue());
        Assert.assertFalse(inc.hasNext());
        inc.increment();
        Assert.assertEquals(2, limits.size());
        Assert.assertFalse(inc.canIncrement());
    }

    private static void assertIteratorSequence(Iterator<Integer> iterator, int... expected) {
        for (int value : expected) {
            Assert.assertTrue(iterator.hasNext());
            Assert.assertEquals(value, iterator.next().intValue());
        }
        Assert.assertFalse(iterator.hasNext());
        try {
            iterator.next();
            Assert.fail("exception expected");
        } catch (NoSuchElementException e) {
            // Expected.
        }
        Assert.assertFalse(iterator.hasNext());
    }

    private static void assertIncrementOverflow(int start, int max, int step, int nTimes) {
        final IntegerSequence.Incrementor inc = IntegerSequence.Incrementor.create()
            .withStart(start)
            .withMaximalCount(max)
            .withIncrement(step);

        Assert.assertFalse(inc.canIncrement(nTimes));
        try {
            inc.increment(nTimes);
            Assert.fail("exception expected");
        } catch (MaxCountExceededException e) {
            // Expected.
        }
        Assert.assertFalse(inc.hasNext());
    }

    @Test(expected = ZeroException.class)
    public void testRangeZeroStep() {
        IntegerSequence.range(1, 10, 0);
    }

    @Test(expected = MaxCountExceededException.class)
    public void testIncrementorCountExceeded() {
        final int start = 1;
        final int max = 7;
        final int step = 2;

        final IntegerSequence.Incrementor inc =
            IntegerSequence.Incrementor.create()
            .withStart(start)
            .withMaximalCount(max)
            .withIncrement(step);

        Assert.assertTrue(inc.canIncrement(2));
        Assert.assertFalse(inc.canIncrement(3));

        while (true) {
            inc.increment();
        }
    }

    @Test
    public void testCanIncrementZeroTimes() {
        final int start = 1;
        final int max = 2;
        final int step = 1;

        final IntegerSequence.Incrementor inc
            = IntegerSequence.Incrementor.create()
            .withStart(start)
            .withMaximalCount(max)
            .withIncrement(step);

        Assert.assertTrue(inc.canIncrement(0));
    }

    @Test(expected = NotStrictlyPositiveException.class)
    public void testIncrementZeroTimes() {
        final int start = 1;
        final int max = 2;
        final int step = 1;

        final IntegerSequence.Incrementor inc
            = IntegerSequence.Incrementor.create()
            .withStart(start)
            .withMaximalCount(max)
            .withIncrement(step);

        inc.increment(0);
    }

    @Test
    public void testIncrementTooManyTimes() {
        final int start = 0;
        final int max = 3;
        final int step = 1;

        for (int i = 1; i <= max + 4; i++) {
            final IntegerSequence.Incrementor inc
                = IntegerSequence.Incrementor.create()
                .withStart(start)
                .withMaximalCount(max)
                .withIncrement(step);

            Assert.assertTrue(inc.canIncrement(max - 1));
            Assert.assertFalse(inc.canIncrement(max));

            try {
                inc.increment(i);
            } catch (MaxCountExceededException e) {
                if (i < max) {
                    Assert.fail("i=" + i);
                }
                // Otherwise, the exception is expected.
            }
        }
    }

    @Test(expected = ZeroException.class)
    public void testIncrementZeroStep() {
        final int step = 0;

        final IntegerSequence.Incrementor inc
            = IntegerSequence.Incrementor.create()
            .withIncrement(step);
    }

    @Test
    public void testIteratorZeroElement() {
        final int start = 1;
        final int max = 1;
        final int step = 1;

        final IntegerSequence.Incrementor inc
            = IntegerSequence.Incrementor.create()
            .withStart(start)
            .withMaximalCount(max)
            .withIncrement(step);

        Assert.assertFalse(inc.hasNext());
        try {
            inc.increment();
            Assert.fail("exception expected");
        } catch (MaxCountExceededException e) {
            // Expected.
        }
    }

    @Test
    public void testIteratorNext() {
        final int start = 1;
        final int max = 2;
        final int step = 1;

        final IntegerSequence.Incrementor inc
            = IntegerSequence.Incrementor.create()
            .withStart(start)
            .withMaximalCount(max)
            .withIncrement(step);

        Assert.assertTrue(inc.hasNext());
        Assert.assertEquals(1, inc.next().intValue());
        Assert.assertFalse(inc.hasNext());
        try {
            inc.next();
            Assert.fail("exception expected");
        } catch (NoSuchElementException e) {
            // Expected.
        }
    }

    @Test(expected = TooManyEvaluationsException.class)
    public void testIncrementorAlternateException() {
        final int start = 1;
        final int max = 2;
        final int step = 1;

        final IntegerSequence.Incrementor.MaxCountExceededCallback cb
            = new IntegerSequence.Incrementor.MaxCountExceededCallback() {
                    /** {@inheritDoc} */
                    @Override
                    public void trigger(int max) {
                        throw new TooManyEvaluationsException(max);
                    }
                };

        final IntegerSequence.Incrementor inc
            = IntegerSequence.Incrementor.create()
            .withStart(start)
            .withMaximalCount(max)
            .withIncrement(step)
            .withCallback(cb);

        Assert.assertTrue(inc.hasNext());
        Assert.assertEquals(start, inc.next().intValue());
        inc.increment(); // Must fail.
    }
}
