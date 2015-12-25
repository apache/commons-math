/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.commons.math3.util;

import java.util.List;
import java.util.ArrayList;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.ZeroException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link IntegerSequence} and {@link IntegerSequence#Incrementor}.
 */
public class IntegerSequenceTest {
    @Test
    public void testRangeMultipleIterations() {
        // Check that we can iterate several times using the same instance.
        final int start = 1;
        final int max = 7;
        final int step = 2;

        final List<Integer> seq = new ArrayList<Integer>();
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

        final List<Integer> seq = new ArrayList<Integer>();
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

        final List<Integer> seq = new ArrayList<Integer>();
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

        final List<Integer> seq = new ArrayList<Integer>();
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

        final List<Integer> seq = new ArrayList<Integer>();
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

        final List<Integer> seq = new ArrayList<Integer>();
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

        final List<Integer> seq = new ArrayList<Integer>();
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

        final List<Integer> seq = new ArrayList<Integer>();
        final IntegerSequence.Range r = IntegerSequence.range(start, max, step);
        for (Integer i : r) {
            seq.add(i);
        }

        Assert.assertEquals(0, seq.size());
        Assert.assertEquals(seq.size(), r.size());
    }

    @Test(expected=MaxCountExceededException.class)
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

    @Test(expected=NotStrictlyPositiveException.class)
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

    @Test(expected=ZeroException.class)
    public void testIncrementZeroStep() {
        final int step = 0;
        IntegerSequence.Incrementor.create().withIncrement(step);
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

    @Test(expected=TooManyEvaluationsException.class)
    public void testIncrementorAlternateException() {
        final int start = 1;
        final int max = 2;
        final int step = 1;

        final IntegerSequence.Incrementor.MaxCountExceededCallback cb
            = new IntegerSequence.Incrementor.MaxCountExceededCallback() {
                    /** {@inheritDoc} */
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

        try {
            // One call must succeed.
            inc.increment();
        } catch (RuntimeException e) {
            Assert.fail("unexpected exception");
        }

        // Second call must fail.
        inc.increment();
    }
}
