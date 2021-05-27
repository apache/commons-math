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
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.math4.legacy.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math4.legacy.stat.descriptive.SummaryStatistics;
import org.junit.Assert;
import org.junit.Test;

/**
 * Certified data test cases.
 */
public class CertifiedDataTest {

    protected double mean = Double.NaN;

    protected double std = Double.NaN;

    /**
     * Test SummaryStatistics - implementations that do not store the data
     * and use single pass algorithms to compute statistics
    */
    @Test
    public void testSummaryStatistics() throws Exception {
        SummaryStatistics u = new SummaryStatistics();
        loadStats("data/PiDigits.txt", u);
        Assert.assertEquals("PiDigits: std", std, u.getStandardDeviation(), 1E-13);
        Assert.assertEquals("PiDigits: mean", mean, u.getMean(), 1E-13);

        loadStats("data/Mavro.txt", u);
        Assert.assertEquals("Mavro: std", std, u.getStandardDeviation(), 1E-14);
        Assert.assertEquals("Mavro: mean", mean, u.getMean(), 1E-14);

        loadStats("data/Michelso.txt", u);
        Assert.assertEquals("Michelso: std", std, u.getStandardDeviation(), 1E-13);
        Assert.assertEquals("Michelso: mean", mean, u.getMean(), 1E-13);

        loadStats("data/NumAcc1.txt", u);
        Assert.assertEquals("NumAcc1: std", std, u.getStandardDeviation(), 1E-14);
        Assert.assertEquals("NumAcc1: mean", mean, u.getMean(), 1E-14);

        loadStats("data/NumAcc2.txt", u);
        Assert.assertEquals("NumAcc2: std", std, u.getStandardDeviation(), 1E-14);
        Assert.assertEquals("NumAcc2: mean", mean, u.getMean(), 1E-14);
    }

    /**
     * Test DescriptiveStatistics - implementations that store full array of
     * values and execute multi-pass algorithms
     */
    @Test
    public void testDescriptiveStatistics() throws Exception {

        DescriptiveStatistics u = new DescriptiveStatistics();

        loadStats("data/PiDigits.txt", u);
        Assert.assertEquals("PiDigits: std", std, u.getStandardDeviation(), 1E-14);
        Assert.assertEquals("PiDigits: mean", mean, u.getMean(), 1E-14);

        loadStats("data/Mavro.txt", u);
        Assert.assertEquals("Mavro: std", std, u.getStandardDeviation(), 1E-14);
        Assert.assertEquals("Mavro: mean", mean, u.getMean(), 1E-14);

        loadStats("data/Michelso.txt", u);
        Assert.assertEquals("Michelso: std", std, u.getStandardDeviation(), 1E-14);
        Assert.assertEquals("Michelso: mean", mean, u.getMean(), 1E-14);

        loadStats("data/NumAcc1.txt", u);
        Assert.assertEquals("NumAcc1: std", std, u.getStandardDeviation(), 1E-14);
        Assert.assertEquals("NumAcc1: mean", mean, u.getMean(), 1E-14);

        loadStats("data/NumAcc2.txt", u);
        Assert.assertEquals("NumAcc2: std", std, u.getStandardDeviation(), 1E-14);
        Assert.assertEquals("NumAcc2: mean", mean, u.getMean(), 1E-14);
    }

    /**
     * loads a DescriptiveStatistics off of a test file
     */
    private void loadStats(String resource, Object u) throws Exception {

        DescriptiveStatistics d = null;
        SummaryStatistics s = null;
        if (u instanceof DescriptiveStatistics) {
            d = (DescriptiveStatistics) u;
        } else {
            s = (SummaryStatistics) u;
        }
        u.getClass().getDeclaredMethod(
                "clear", new Class[]{}).invoke(u, new Object[]{});
        mean = Double.NaN;
        std = Double.NaN;

        InputStream resourceAsStream = CertifiedDataTest.class.getResourceAsStream(resource);
        Assert.assertNotNull("Could not find resource "+resource,resourceAsStream);
        BufferedReader in =
            new BufferedReader(
                    new InputStreamReader(
                            resourceAsStream));

        String line = null;

        for (int j = 0; j < 60; j++) {
            line = in.readLine();
            if (j == 40) {
                mean =
                    Double.parseDouble(
                            line.substring(line.lastIndexOf(":") + 1).trim());
            }
            if (j == 41) {
                std =
                    Double.parseDouble(
                            line.substring(line.lastIndexOf(":") + 1).trim());
            }
        }

        line = in.readLine();

        while (line != null) {
            if (d != null) {
                d.addValue(Double.parseDouble(line.trim()));
            }  else {
                s.addValue(Double.parseDouble(line.trim()));
            }
            line = in.readLine();
        }

        resourceAsStream.close();
        in.close();
    }
}
