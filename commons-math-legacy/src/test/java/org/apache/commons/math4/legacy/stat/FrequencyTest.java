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
package org.apache.commons.math4.legacy.stat;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math4.legacy.TestUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the {@link Frequency} class.
 *
 */
public final class FrequencyTest {
    private static final long ONE_LONG = 1L;
    private static final long TWO_LONG = 2L;
    private static final long THREE_LONG = 3L;
    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int THREE = 3 ;
    private static final double TOLERANCE = 10E-15d;

    /** test freq counts */
    @Test
    public void testCounts() {
        Frequency<Long> fLong = new Frequency<>();
        Assert.assertEquals("total count",0,fLong.getSumFreq());
        fLong.addValue(ONE_LONG);
        fLong.addValue(TWO_LONG);
        fLong.addValue(1L);
        fLong.addValue(ONE_LONG);
        Assert.assertEquals("one frequency count",3,fLong.getCount(1L));
        Assert.assertEquals("two frequency count",1,fLong.getCount(2L));
        Assert.assertEquals("three frequency count",0,fLong.getCount(3L));
        Assert.assertEquals("total count",4,fLong.getSumFreq());
        Assert.assertEquals("zero cumulative frequency", 0, fLong.getCumFreq(0L));
        Assert.assertEquals("one cumulative frequency", 3,  fLong.getCumFreq(1L));
        Assert.assertEquals("two cumulative frequency", 4,  fLong.getCumFreq(2L));
        Assert.assertEquals("Integer argument cum freq",4, fLong.getCumFreq(Long.valueOf(2)));
        Assert.assertEquals("five cumulative frequency", 4,  fLong.getCumFreq(5L));
        Assert.assertEquals("foo cumulative frequency", 0,  fLong.getCumFreq(-1L));

        fLong.clear();
        Assert.assertEquals("total count",0,fLong.getSumFreq());

        // userguide examples -------------------------------------------------------------------
        Frequency<String> fString = new Frequency<>();
        fString.addValue("one");
        fString.addValue("One");
        fString.addValue("oNe");
        fString.addValue("Z");
        Assert.assertEquals("one cumulative frequency", 1 , fString.getCount("one"));
        Assert.assertEquals("Z cumulative pct", 0.5,  fString.getCumPct("Z"), TOLERANCE);
        Assert.assertEquals("z cumulative pct", 1.0,  fString.getCumPct("z"), TOLERANCE);
        Assert.assertEquals("Ot cumulative pct", 0.25,  fString.getCumPct("Ot"), TOLERANCE);

        Frequency<Integer> fInteger = new Frequency<>();
        fInteger.addValue(1);
        fInteger.addValue(Integer.valueOf(1));
        fInteger.addValue(ONE);
        fInteger.addValue(2);
        fInteger.addValue(Integer.valueOf(-1));
        Assert.assertEquals("1 count", 3, fInteger.getCount(1));
        Assert.assertEquals("1 count", 3, fInteger.getCount(Integer.valueOf(1)));
        Assert.assertEquals("0 cum pct", 0.2, fInteger.getCumPct(0), TOLERANCE);
        Assert.assertEquals("1 pct", 0.6, fInteger.getPct(Integer.valueOf(1)), TOLERANCE);
        Assert.assertEquals("-2 cum pct", 0, fInteger.getCumPct(-2), TOLERANCE);
        Assert.assertEquals("10 cum pct", 1, fInteger.getCumPct(10), TOLERANCE);

        fString = new Frequency<>(String.CASE_INSENSITIVE_ORDER);
        fString.addValue("one");
        fString.addValue("One");
        fString.addValue("oNe");
        fString.addValue("Z");
        Assert.assertEquals("one count", 3 ,  fString.getCount("one"));
        Assert.assertEquals("Z cumulative pct -- case insensitive", 1 ,  fString.getCumPct("Z"), TOLERANCE);
        Assert.assertEquals("z cumulative pct -- case insensitive", 1 ,  fString.getCumPct("z"), TOLERANCE);

        Frequency<Character> fChar = new Frequency<>();
        Assert.assertEquals(0L, fChar.getCount('a'));
        Assert.assertEquals(0L, fChar.getCumFreq('b'));
        TestUtils.assertEquals(Double.NaN, fChar.getPct('a'), 0.0);
        TestUtils.assertEquals(Double.NaN, fChar.getCumPct('b'), 0.0);
        fChar.addValue('a');
        fChar.addValue('b');
        fChar.addValue('c');
        fChar.addValue('d');
        Assert.assertEquals(1L, fChar.getCount('a'));
        Assert.assertEquals(2L, fChar.getCumFreq('b'));
        Assert.assertEquals(0.25, fChar.getPct('a'), 0.0);
        Assert.assertEquals(0.5, fChar.getCumPct('b'), 0.0);
        Assert.assertEquals(1.0, fChar.getCumPct('e'), 0.0);
    }

    /** test pcts */
    @Test
    public void testPcts() {
        Frequency<Long> f = new Frequency<>();
        f.addValue(ONE_LONG);
        f.addValue(TWO_LONG);
        f.addValue(THREE_LONG);
        f.addValue(THREE_LONG);
        Assert.assertEquals("two pct",0.25,f.getPct(Long.valueOf(2)),TOLERANCE);
        Assert.assertEquals("two cum pct",0.50,f.getCumPct(Long.valueOf(2)),TOLERANCE);
        Assert.assertEquals("three cum pct",1.0,f.getCumPct(THREE_LONG),TOLERANCE);
    }

    /** test adding incomparable values */
    @Test
    public void testAdd() {
        Frequency<Character> f = new Frequency<>();
        char aChar = 'a';
        char bChar = 'b';
        f.addValue(aChar);
        f.addValue(bChar);
        Assert.assertEquals("a pct",0.5,f.getPct(aChar),TOLERANCE);
        Assert.assertEquals("b cum pct",1.0,f.getCumPct(bChar),TOLERANCE);
    }

    /** test empty table */
    @Test
    public void testEmptyTable() {
        Frequency<Integer> f = new Frequency<>();
        Assert.assertEquals("freq sum, empty table", 0, f.getSumFreq());
        Assert.assertEquals("count, empty table", 0, f.getCount(0));
        Assert.assertEquals("count, empty table",0, f.getCount(Integer.valueOf(0)));
        Assert.assertEquals("cum freq, empty table", 0, f.getCumFreq(0));
        Assert.assertTrue("pct, empty table", Double.isNaN(f.getPct(0)));
        Assert.assertTrue("pct, empty table", Double.isNaN(f.getPct(Integer.valueOf(0))));
        Assert.assertTrue("cum pct, empty table", Double.isNaN(f.getCumPct(0)));
        Assert.assertTrue("cum pct, empty table", Double.isNaN(f.getCumPct(Integer.valueOf(0))));
    }

    /**
     * Tests toString()
     */
    @Test
    public void testToString() throws Exception {
        Frequency<Long> f = new Frequency<>();
        f.addValue(ONE_LONG);
        f.addValue(TWO_LONG);

        String s = f.toString();
        //System.out.println(s);
        Assert.assertNotNull(s);
        BufferedReader reader = new BufferedReader(new StringReader(s));
        String line = reader.readLine(); // header line
        Assert.assertNotNull(line);

        line = reader.readLine(); // one's or two's line
        Assert.assertNotNull(line);
    }

    @Test
    public void testIntegerValues() {
        Frequency<Integer> f = new Frequency<>();
        f.addValue(Integer.valueOf(1));
        f.addValue(1);
        f.addValue(2);
        f.addValue(Integer.valueOf(2));
        Assert.assertEquals("Integer 1 count", 2, f.getCount(1));
        Assert.assertEquals("Integer 1 count", 2, f.getCount(Integer.valueOf(1)));
        Assert.assertEquals("Integer 1 cumPct", 0.5, f.getCumPct(1), TOLERANCE);
        Assert.assertEquals("Integer 1 cumPct", 0.5, f.getCumPct(Integer.valueOf(1)), TOLERANCE);

        f.incrementValue(ONE, -2);
        f.incrementValue(THREE, 5);

        Assert.assertEquals("Integer 1 count", 0, f.getCount(1));
        Assert.assertEquals("Integer 3 count", 5, f.getCount(3));

        Iterator<?> it = f.valuesIterator();
        while (it.hasNext()) {
            Assert.assertTrue(it.next() instanceof Integer);
        }
    }

    @Test
    public void testGetUniqueCount() {
        Frequency<Long> f = new Frequency<>();
        Assert.assertEquals(0, f.getUniqueCount());
        f.addValue(ONE_LONG);
        Assert.assertEquals(1, f.getUniqueCount());
        f.addValue(ONE_LONG);
        Assert.assertEquals(1, f.getUniqueCount());
        f.addValue(TWO_LONG);
        Assert.assertEquals(2, f.getUniqueCount());
    }

    @Test
    public void testIncrement() {
        Frequency<Long> f = new Frequency<>();
        Assert.assertEquals(0, f.getUniqueCount());
        f.incrementValue(ONE_LONG, 1);
        Assert.assertEquals(1, f.getCount(ONE_LONG));

        f.incrementValue(ONE_LONG, 4);
        Assert.assertEquals(5, f.getCount(ONE_LONG));

        f.incrementValue(ONE_LONG, -5);
        Assert.assertEquals(0, f.getCount(ONE_LONG));
    }

    @Test
    public void testMerge() {
        Frequency<Long> f = new Frequency<>();
        Assert.assertEquals(0, f.getUniqueCount());
        f.addValue(ONE_LONG);
        f.addValue(TWO_LONG);
        f.addValue(ONE_LONG);
        f.addValue(TWO_LONG);

        Assert.assertEquals(2, f.getUniqueCount());
        Assert.assertEquals(2, f.getCount(ONE_LONG));
        Assert.assertEquals(2, f.getCount(TWO_LONG));

        Frequency<Long> g = new Frequency<>();
        g.addValue(ONE_LONG);
        g.addValue(THREE_LONG);
        g.addValue(THREE_LONG);

        Assert.assertEquals(2, g.getUniqueCount());
        Assert.assertEquals(1, g.getCount(ONE_LONG));
        Assert.assertEquals(2, g.getCount(THREE_LONG));

        f.merge(g);

        Assert.assertEquals(3, f.getUniqueCount());
        Assert.assertEquals(3, f.getCount(ONE_LONG));
        Assert.assertEquals(2, f.getCount(TWO_LONG));
        Assert.assertEquals(2, f.getCount(THREE_LONG));
    }

    @Test
    public void testMergeCollection() {
        Frequency<Long> f = new Frequency<>();
        Assert.assertEquals(0, f.getUniqueCount());
        f.addValue(ONE_LONG);

        Assert.assertEquals(1, f.getUniqueCount());
        Assert.assertEquals(1, f.getCount(ONE_LONG));
        Assert.assertEquals(0, f.getCount(TWO_LONG));

        Frequency<Long> g = new Frequency<Long>();
        g.addValue(TWO_LONG);

        Frequency<Long> h = new Frequency<Long>();
        h.addValue(THREE_LONG);

        List<Frequency<Long>> coll = new ArrayList<>();
        coll.add(g);
        coll.add(h);
        f.merge(coll);

        Assert.assertEquals(3, f.getUniqueCount());
        Assert.assertEquals(1, f.getCount(ONE_LONG));
        Assert.assertEquals(1, f.getCount(TWO_LONG));
        Assert.assertEquals(1, f.getCount(THREE_LONG));
    }

    @Test
    public void testMode() {
        Frequency<String> f = new Frequency<>();
        List<String> mode;
        mode = f.getMode();
        Assert.assertEquals(0, mode.size());

        f.addValue("3");
        mode = f.getMode();
        Assert.assertEquals(1, mode.size());
        Assert.assertEquals("3", mode.get(0));

        f.addValue("2");
        mode = f.getMode();
        Assert.assertEquals(2, mode.size());
        Assert.assertEquals("2", mode.get(0));
        Assert.assertEquals("3",mode.get(1));

        f.addValue("2");
        mode = f.getMode();
        Assert.assertEquals(1, mode.size());
        Assert.assertEquals("2", mode.get(0));
        Assert.assertFalse(mode.contains("1"));
        Assert.assertTrue(mode.contains("2"));
    }

    @Test
    public void testModeDoubleNan() {
        Frequency<Double> f = new Frequency<>();
        List<Double> mode;
        f.addValue(Double.valueOf(Double.NaN));
        f.addValue(Double.valueOf(Double.NaN));
        f.addValue(Double.valueOf(Double.NaN));
        f.addValue(Double.valueOf(Double.NEGATIVE_INFINITY));
        f.addValue(Double.valueOf(Double.POSITIVE_INFINITY));
        f.addValue(Double.valueOf(Double.NEGATIVE_INFINITY));
        f.addValue(Double.valueOf(Double.POSITIVE_INFINITY));
        f.addValue(Double.valueOf(Double.NEGATIVE_INFINITY));
        f.addValue(Double.valueOf(Double.POSITIVE_INFINITY));
        mode = f.getMode();
        Assert.assertEquals(3, mode.size());
        Assert.assertEquals(Double.valueOf(Double.NEGATIVE_INFINITY), mode.get(0));
        Assert.assertEquals(Double.valueOf(Double.POSITIVE_INFINITY), mode.get(1));
        Assert.assertEquals(Double.valueOf(Double.NaN), mode.get(2));
    }

    @Test
    public void testModeFloatNan() {
        Frequency<Float> f = new Frequency<>();
        List<Float> mode;
        f.addValue(Float.valueOf(Float.NaN));
        f.addValue(Float.valueOf(Float.NaN));
        f.addValue(Float.valueOf(Float.NaN));
        f.addValue(Float.valueOf(Float.NEGATIVE_INFINITY));
        f.addValue(Float.valueOf(Float.POSITIVE_INFINITY));
        f.addValue(Float.valueOf(Float.NEGATIVE_INFINITY));
        f.addValue(Float.valueOf(Float.POSITIVE_INFINITY));
        f.addValue(Float.valueOf(Float.NEGATIVE_INFINITY));
        f.addValue(Float.valueOf(Float.POSITIVE_INFINITY));
        mode = f.getMode();
        Assert.assertEquals(3, mode.size());
        Assert.assertEquals(Float.valueOf(Float.NEGATIVE_INFINITY), mode.get(0));
        Assert.assertEquals(Float.valueOf(Float.POSITIVE_INFINITY), mode.get(1));
        Assert.assertEquals(Float.valueOf(Float.NaN), mode.get(2));
    }
}
