/*
 * Copyright 2003-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.stat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.apache.commons.math.stat.descriptive.SummaryStatisticsImpl;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 * Certified data test cases.
 * @version $Revision$ $Date$
 */
public class CertifiedDataTest extends TestCase  {

    protected double mean = Double.NaN;

    protected double std = Double.NaN;

    /**
     * Certified Data Test Constructor
     * @param name
     */
    public CertifiedDataTest(String name) {
        super(name);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() {
    }

    /**
     * @return The test suite
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(CertifiedDataTest.class);
        suite.setName("Certified Tests");
        return suite;
    }

    /**
     * Test StorelessDescriptiveStatistics
    */
    public void testUnivariateImpl() throws Exception {
        SummaryStatistics u = SummaryStatistics.newInstance(SummaryStatisticsImpl.class);
        loadStats("data/PiDigits.txt", u);
        assertEquals("PiDigits: std", std, u.getStandardDeviation(), .0000000000001);
        assertEquals("PiDigits: mean", mean, u.getMean(), .0000000000001);  

        loadStats("data/Mavro.txt", u);
        assertEquals("Mavro: std", std, u.getStandardDeviation(), .00000000000001);
        assertEquals("Mavro: mean", mean, u.getMean(), .00000000000001);
        
        //loadStats("data/Michelso.txt");
        //assertEquals("Michelso: std", std, u.getStandardDeviation(), .00000000000001);
        //assertEquals("Michelso: mean", mean, u.getMean(), .00000000000001);   
                                        
        loadStats("data/NumAcc1.txt", u);
        assertEquals("NumAcc1: std", std, u.getStandardDeviation(), .00000000000001);
        assertEquals("NumAcc1: mean", mean, u.getMean(), .00000000000001);
        
        //loadStats("data/NumAcc2.txt");
        //assertEquals("NumAcc2: std", std, u.getStandardDeviation(), .000000001);
        //assertEquals("NumAcc2: mean", mean, u.getMean(), .00000000000001);
    }

    /**
     * Test StorelessDescriptiveStatistics
     */
    public void testStoredUnivariateImpl() throws Exception {

        DescriptiveStatistics u = DescriptiveStatistics.newInstance();
        
        loadStats("data/PiDigits.txt", u);
        assertEquals("PiDigits: std", std, u.getStandardDeviation(), .0000000000001);
        assertEquals("PiDigits: mean", mean, u.getMean(), .0000000000001);
        
        loadStats("data/Mavro.txt", u);
        assertEquals("Mavro: std", std, u.getStandardDeviation(), .00000000000001);
        assertEquals("Mavro: mean", mean, u.getMean(), .00000000000001);        
        
        //loadStats("data/Michelso.txt");
        //assertEquals("Michelso: std", std, u.getStandardDeviation(), .00000000000001);
        //assertEquals("Michelso: mean", mean, u.getMean(), .00000000000001);   

        loadStats("data/NumAcc1.txt", u);
        assertEquals("NumAcc1: std", std, u.getStandardDeviation(), .00000000000001);
        assertEquals("NumAcc1: mean", mean, u.getMean(), .00000000000001);
        
        //loadStats("data/NumAcc2.txt");
        //assertEquals("NumAcc2: std", std, u.getStandardDeviation(), .000000001);
        //assertEquals("NumAcc2: mean", mean, u.getMean(), .00000000000001);
    }

    /**
     * loads a DescriptiveStatistics off of a test file
     * @param file
     * @param statistical summary
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
        
        BufferedReader in =
            new BufferedReader(
                    new InputStreamReader(
                            getClass().getResourceAsStream(resource)));
        
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
        
        in.close();
    }
}
