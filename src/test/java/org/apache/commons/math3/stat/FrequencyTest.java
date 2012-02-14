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
package org.apache.commons.math3.stat;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Iterator;


import org.apache.commons.math3.TestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for the {@link Frequency} class.
 *
 * @version $Id$
 */

public final class FrequencyTest {
    private long oneL = 1;
    private long twoL = 2;
    private long threeL = 3;
    private int oneI = 1;
    private int twoI = 2;
    private int threeI=3;
    private double tolerance = 10E-15;
    private Frequency f = null;

    @Before
    public void setUp() {
        f = new Frequency();
    }

    /** test freq counts */
    @Test
    public void testCounts() {
        Assert.assertEquals("total count",0,f.getSumFreq());
        f.addValue(oneL);
        f.addValue(twoL);
        f.addValue(1);
        f.addValue(oneI);
        Assert.assertEquals("one frequency count",3,f.getCount(1));
        Assert.assertEquals("two frequency count",1,f.getCount(2));
        Assert.assertEquals("three frequency count",0,f.getCount(3));
        Assert.assertEquals("total count",4,f.getSumFreq());
        Assert.assertEquals("zero cumulative frequency", 0, f.getCumFreq(0));
        Assert.assertEquals("one cumulative frequency", 3,  f.getCumFreq(1));
        Assert.assertEquals("two cumulative frequency", 4,  f.getCumFreq(2));
        Assert.assertEquals("Integer argument cum freq",4, f.getCumFreq(Integer.valueOf(2)));
        Assert.assertEquals("five cumulative frequency", 4,  f.getCumFreq(5));
        Assert.assertEquals("foo cumulative frequency", 0,  f.getCumFreq("foo"));

        f.clear();
        Assert.assertEquals("total count",0,f.getSumFreq());

        // userguide examples -------------------------------------------------------------------
        f.addValue("one");
        f.addValue("One");
        f.addValue("oNe");
        f.addValue("Z");
        Assert.assertEquals("one cumulative frequency", 1 ,  f.getCount("one"));
        Assert.assertEquals("Z cumulative pct", 0.5,  f.getCumPct("Z"), tolerance);
        Assert.assertEquals("z cumulative pct", 1.0,  f.getCumPct("z"), tolerance);
        Assert.assertEquals("Ot cumulative pct", 0.25,  f.getCumPct("Ot"), tolerance);
        f.clear();

        f = null;
        Frequency f = new Frequency();
        f.addValue(1);
        f.addValue(Integer.valueOf(1));
        f.addValue(Long.valueOf(1));
        f.addValue(2);
        f.addValue(Integer.valueOf(-1));
        Assert.assertEquals("1 count", 3, f.getCount(1));
        Assert.assertEquals("1 count", 3, f.getCount(Integer.valueOf(1)));
        Assert.assertEquals("0 cum pct", 0.2, f.getCumPct(0), tolerance);
        Assert.assertEquals("1 pct", 0.6, f.getPct(Integer.valueOf(1)), tolerance);
        Assert.assertEquals("-2 cum pct", 0, f.getCumPct(-2), tolerance);
        Assert.assertEquals("10 cum pct", 1, f.getCumPct(10), tolerance);

        f = null;
        f = new Frequency(String.CASE_INSENSITIVE_ORDER);
        f.addValue("one");
        f.addValue("One");
        f.addValue("oNe");
        f.addValue("Z");
        Assert.assertEquals("one count", 3 ,  f.getCount("one"));
        Assert.assertEquals("Z cumulative pct -- case insensitive", 1 ,  f.getCumPct("Z"), tolerance);
        Assert.assertEquals("z cumulative pct -- case insensitive", 1 ,  f.getCumPct("z"), tolerance);

        f = null;
        f = new Frequency();
        Assert.assertEquals(0L, f.getCount('a'));
        Assert.assertEquals(0L, f.getCumFreq('b'));
        TestUtils.assertEquals(Double.NaN, f.getPct('a'), 0.0);
        TestUtils.assertEquals(Double.NaN, f.getCumPct('b'), 0.0);
        f.addValue('a');
        f.addValue('b');
        f.addValue('c');
        f.addValue('d');
        Assert.assertEquals(1L, f.getCount('a'));
        Assert.assertEquals(2L, f.getCumFreq('b'));
        Assert.assertEquals(0.25, f.getPct('a'), 0.0);
        Assert.assertEquals(0.5, f.getCumPct('b'), 0.0);
        Assert.assertEquals(1.0, f.getCumPct('e'), 0.0);
    }

    /** test pcts */
    @Test
    public void testPcts() {
        f.addValue(oneL);
        f.addValue(twoL);
        f.addValue(oneI);
        f.addValue(twoI);
        f.addValue(threeL);
        f.addValue(threeL);
        f.addValue(3);
        f.addValue(threeI);
        Assert.assertEquals("one pct",0.25,f.getPct(1),tolerance);
        Assert.assertEquals("two pct",0.25,f.getPct(Long.valueOf(2)),tolerance);
        Assert.assertEquals("three pct",0.5,f.getPct(threeL),tolerance);
        Assert.assertEquals("five pct",0,f.getPct(5),tolerance);
        Assert.assertEquals("foo pct",0,f.getPct("foo"),tolerance);
        Assert.assertEquals("one cum pct",0.25,f.getCumPct(1),tolerance);
        Assert.assertEquals("two cum pct",0.50,f.getCumPct(Long.valueOf(2)),tolerance);
        Assert.assertEquals("Integer argument",0.50,f.getCumPct(Integer.valueOf(2)),tolerance);
        Assert.assertEquals("three cum pct",1.0,f.getCumPct(threeL),tolerance);
        Assert.assertEquals("five cum pct",1.0,f.getCumPct(5),tolerance);
        Assert.assertEquals("zero cum pct",0.0,f.getCumPct(0),tolerance);
        Assert.assertEquals("foo cum pct",0,f.getCumPct("foo"),tolerance);
    }

    /** test adding incomparable values */
    @Test
    public void testAdd() {
        char aChar = 'a';
        char bChar = 'b';
        String aString = "a";
        f.addValue(aChar);
        f.addValue(bChar);
        try {
            f.addValue(aString);
            Assert.fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            f.addValue(2);
            Assert.fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        Assert.assertEquals("a pct",0.5,f.getPct(aChar),tolerance);
        Assert.assertEquals("b cum pct",1.0,f.getCumPct(bChar),tolerance);
        Assert.assertEquals("a string pct",0.0,f.getPct(aString),tolerance);
        Assert.assertEquals("a string cum pct",0.0,f.getCumPct(aString),tolerance);

        f = new Frequency();
        f.addValue("One");
        try {
            f.addValue(new Integer("One"));
            Assert.fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    /** test empty table */
    @Test
    public void testEmptyTable() {
        Assert.assertEquals("freq sum, empty table", 0, f.getSumFreq());
        Assert.assertEquals("count, empty table", 0, f.getCount(0));
        Assert.assertEquals("count, empty table",0, f.getCount(Integer.valueOf(0)));
        Assert.assertEquals("cum freq, empty table", 0, f.getCumFreq(0));
        Assert.assertEquals("cum freq, empty table", 0, f.getCumFreq("x"));
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
        f.addValue(oneL);
        f.addValue(twoL);
        f.addValue(oneI);
        f.addValue(twoI);

        String s = f.toString();
        //System.out.println(s);
        Assert.assertNotNull(s);
        BufferedReader reader = new BufferedReader(new StringReader(s));
        String line = reader.readLine(); // header line
        Assert.assertNotNull(line);

        line = reader.readLine(); // one's or two's line
        Assert.assertNotNull(line);

        line = reader.readLine(); // one's or two's line
        Assert.assertNotNull(line);

        line = reader.readLine(); // no more elements
        Assert.assertNull(line);
    }

    @Test
    public void testIntegerValues() {
        Comparable<?> obj1 = null;
        obj1 = Integer.valueOf(1);
        Integer int1 = Integer.valueOf(1);
        f.addValue(obj1);
        f.addValue(int1);
        f.addValue(2);
        f.addValue(Long.valueOf(2));
        Assert.assertEquals("Integer 1 count", 2, f.getCount(1));
        Assert.assertEquals("Integer 1 count", 2, f.getCount(Integer.valueOf(1)));
        Assert.assertEquals("Integer 1 count", 2, f.getCount(Long.valueOf(1)));
        Assert.assertEquals("Integer 1 cumPct", 0.5, f.getCumPct(1), tolerance);
        Assert.assertEquals("Integer 1 cumPct", 0.5, f.getCumPct(Long.valueOf(1)), tolerance);
        Assert.assertEquals("Integer 1 cumPct", 0.5, f.getCumPct(Integer.valueOf(1)), tolerance);
        Iterator<?> it = f.valuesIterator();
        while (it.hasNext()) {
            Assert.assertTrue(it.next() instanceof Long);
        }
    }

    @Test
    public void testSerial() {
        f.addValue(oneL);
        f.addValue(twoL);
        f.addValue(oneI);
        f.addValue(twoI);
        Assert.assertEquals(f, TestUtils.serializeAndRecover(f));
    }
    
    @Test
    public void testGetUniqueCount() {
        Assert.assertEquals(0, f.getUniqueCount());
        f.addValue(oneL);
        Assert.assertEquals(1, f.getUniqueCount());
        f.addValue(oneL);
        Assert.assertEquals(1, f.getUniqueCount());
        f.addValue(twoI);
        Assert.assertEquals(2, f.getUniqueCount());
    }
}

