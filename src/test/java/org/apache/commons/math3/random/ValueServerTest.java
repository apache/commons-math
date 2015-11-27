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
package org.apache.commons.math3.random;

import java.net.URL;
import java.util.Arrays;

import org.apache.commons.math3.RetryRunner;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test cases for the ValueServer class.
 *
 */

@RunWith(RetryRunner.class)
public final class ValueServerTest {

    private ValueServer vs = new ValueServer(new Well19937c(100));

    @Before
    public void setUp() {
        vs.setMode(ValueServer.DIGEST_MODE);
        URL url = getClass().getResource("testData.txt");
        vs.setValuesFileURL(url);
    }

    /**
      * Generate 1000 random values and make sure they look OK.<br>
      * Note that there is a non-zero (but very small) probability that
      * these tests will fail even if the code is working as designed.
      */
    @Test
    public void testNextDigest() throws Exception {
        double next = 0.0;
        double tolerance = 0.1;
        vs.computeDistribution();
        Assert.assertTrue("empirical distribution property",
            vs.getEmpiricalDistribution() != null);
        SummaryStatistics stats = new SummaryStatistics();
        for (int i = 1; i < 1000; i++) {
            next = vs.getNext();
            stats.addValue(next);
        }
        Assert.assertEquals("mean", 5.069831575018909, stats.getMean(), tolerance);
        Assert.assertEquals("std dev", 1.0173699343977738, stats.getStandardDeviation(),
            tolerance);

        vs.computeDistribution(500);
        stats = new SummaryStatistics();
        for (int i = 1; i < 1000; i++) {
            next = vs.getNext();
            stats.addValue(next);
        }
        Assert.assertEquals("mean", 5.069831575018909, stats.getMean(), tolerance);
        Assert.assertEquals("std dev", 1.0173699343977738, stats.getStandardDeviation(),
            tolerance);
    }

    /**
     * Verify that when provided with fixed seeds, stochastic modes
     * generate fixed sequences.  Verifies the fix for MATH-654.
     */
    @Test
    public void testFixedSeed() throws Exception {
        ValueServer valueServer = new ValueServer();
        URL url = getClass().getResource("testData.txt");
        valueServer.setValuesFileURL(url);
        valueServer.computeDistribution();
        checkFixedSeed(valueServer, ValueServer.DIGEST_MODE);
        checkFixedSeed(valueServer, ValueServer.EXPONENTIAL_MODE);
        checkFixedSeed(valueServer, ValueServer.GAUSSIAN_MODE);
        checkFixedSeed(valueServer, ValueServer.UNIFORM_MODE);
    }

    /**
     * Do the check for {@link #testFixedSeed()}
     * @param mode ValueServer mode
     */
    private void checkFixedSeed(ValueServer valueServer, int mode) throws Exception {
        valueServer.reSeed(1000);
        valueServer.setMode(mode);
        double[][] values = new double[2][100];
        for (int i = 0; i < 100; i++) {
            values[0][i] = valueServer.getNext();
        }
        valueServer.reSeed(1000);
        for (int i = 0; i < 100; i++) {
            values[1][i] = valueServer.getNext();
        }
        Assert.assertTrue(Arrays.equals(values[0], values[1]));
    }

    /**
      * Make sure exception thrown if digest getNext is attempted
      * before loading empiricalDistribution.
      */
    @Test
    public void testNextDigestFail() throws Exception {
        try {
            vs.getNext();
            Assert.fail("Expecting IllegalStateException");
        } catch (IllegalStateException ex) {}
    }

    @Test
    public void testEmptyReplayFile() throws Exception {
        try {
            URL url = getClass().getResource("emptyFile.txt");
            vs.setMode(ValueServer.REPLAY_MODE);
            vs.setValuesFileURL(url);
            vs.getNext();
            Assert.fail("an exception should have been thrown");
        } catch (MathIllegalStateException mise) {
            // expected behavior
        }
    }

    @Test
    public void testEmptyDigestFile() throws Exception {
        try {
            URL url = getClass().getResource("emptyFile.txt");
            vs.setMode(ValueServer.DIGEST_MODE);
            vs.setValuesFileURL(url);
            vs.computeDistribution();
            Assert.fail("an exception should have been thrown");
        } catch (ZeroException ze) {
            // expected behavior
        }
    }

    /**
     * Test ValueServer REPLAY_MODE using values in testData file.<br>
     * Check that the values 1,2,1001,1002 match data file values 1 and 2.
     * the sample data file.
     */
    @Test
    public void testReplay() throws Exception {
        double firstDataValue = 4.038625496201205;
        double secondDataValue = 3.6485326248346936;
        double tolerance = 10E-15;
        double compareValue = 0.0d;
        vs.setMode(ValueServer.REPLAY_MODE);
        vs.resetReplayFile();
        compareValue = vs.getNext();
        Assert.assertEquals(compareValue,firstDataValue,tolerance);
        compareValue = vs.getNext();
        Assert.assertEquals(compareValue,secondDataValue,tolerance);
        for (int i = 3; i < 1001; i++) {
           compareValue = vs.getNext();
        }
        compareValue = vs.getNext();
        Assert.assertEquals(compareValue,firstDataValue,tolerance);
        compareValue = vs.getNext();
        Assert.assertEquals(compareValue,secondDataValue,tolerance);
        vs.closeReplayFile();
        // make sure no NPE
        vs.closeReplayFile();
    }

    /**
     * Test other ValueServer modes
     */
    @Test
    public void testModes() throws Exception {
        vs.setMode(ValueServer.CONSTANT_MODE);
        vs.setMu(0);
        Assert.assertEquals("constant mode test",vs.getMu(),vs.getNext(),Double.MIN_VALUE);
        vs.setMode(ValueServer.UNIFORM_MODE);
        vs.setMu(2);
        double val = vs.getNext();
        Assert.assertTrue(val > 0 && val < 4);
        vs.setSigma(1);
        vs.setMode(ValueServer.GAUSSIAN_MODE);
        val = vs.getNext();
        Assert.assertTrue("gaussian value close enough to mean",
            val < vs.getMu() + 100*vs.getSigma());
        vs.setMode(ValueServer.EXPONENTIAL_MODE);
        val = vs.getNext();
        Assert.assertTrue(val > 0);
        try {
            vs.setMode(1000);
            vs.getNext();
            Assert.fail("bad mode, expecting IllegalStateException");
        } catch (IllegalStateException ex) {
            // ignored
        }
    }

    /**
     * Test fill
     */
    @Test
    public void testFill() throws Exception {
        vs.setMode(ValueServer.CONSTANT_MODE);
        vs.setMu(2);
        double[] val = new double[5];
        vs.fill(val);
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals("fill test in place",2,val[i],Double.MIN_VALUE);
        }
        double v2[] = vs.fill(3);
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals("fill test in place",2,v2[i],Double.MIN_VALUE);
        }
    }

    /**
     * Test getters to make Clover happy
     */
    @Test
    public void testProperties() throws Exception {
        vs.setMode(ValueServer.CONSTANT_MODE);
        Assert.assertEquals("mode test",ValueServer.CONSTANT_MODE,vs.getMode());
        vs.setValuesFileURL("http://www.apache.org");
        URL url = vs.getValuesFileURL();
        Assert.assertEquals("valuesFileURL test","http://www.apache.org",url.toString());
    }

}
