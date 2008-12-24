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
package org.apache.commons.math.stat.descriptive;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.math.stat.descriptive.rank.Percentile;
import org.apache.commons.math.util.MathUtils;

/**
 * Test cases for the DescriptiveStatistics class.
 * 
 * @version $Revision$ $Date: 2007-08-16 15:36:33 -0500 (Thu, 16 Aug
 *          2007) $
 */
public class DescriptiveStatisticsTest extends TestCase {

    public DescriptiveStatisticsTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(DescriptiveStatisticsTest.class);
        suite.setName("DescriptiveStatistics Tests");
        return suite;
    }
    
    public void testSetterInjection() throws Exception {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        stats.addValue(1);
        stats.addValue(3);
        assertEquals(2, stats.getMean(), 1E-10);
        // Now lets try some new math
        stats.setMeanImpl(new deepMean());
        assertEquals(42, stats.getMean(), 1E-10);
    }
    
    public void testPercentileSetter() throws Exception {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        stats.addValue(1);
        stats.addValue(2);
        stats.addValue(3);
        assertEquals(2, stats.getPercentile(50.0), 1E-10);
        
        // Inject wrapped Percentile impl
        stats.setPercentileImpl(new goodPercentile());
        assertEquals(2, stats.getPercentile(50.0), 1E-10);
        
        // Try "new math" impl
        stats.setPercentileImpl(new subPercentile());
        assertEquals(10.0, stats.getPercentile(10.0), 1E-10);
        
        // Try to set bad impl
        try {
            stats.setPercentileImpl(new badPercentile()); 
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }
    public void testRemoval() {

        final DescriptiveStatistics dstat = new DescriptiveStatistics();

        checkremoval(dstat, 1, 6.0, 0.0, Double.NaN);
        checkremoval(dstat, 3, 5.0, 3.0, 4.5);
        checkremoval(dstat, 6, 3.5, 2.5, 3.0);
        checkremoval(dstat, 9, 3.5, 2.5, 3.0);
        checkremoval(dstat, DescriptiveStatistics.INFINITE_WINDOW, 3.5, 2.5, 3.0);

    }

    public void checkremoval(DescriptiveStatistics dstat, int wsize,
                             double mean1, double mean2, double mean3) {

        dstat.setWindowSize(wsize);
        dstat.clear();

        for (int i = 1 ; i <= 6 ; ++i) {
            dstat.addValue(i);
        }

        assertTrue(MathUtils.equals(mean1, dstat.getMean()));
        dstat.replaceMostRecentValue(0);
        assertTrue(MathUtils.equals(mean2, dstat.getMean()));
        dstat.removeMostRecentValue();
        assertTrue(MathUtils.equals(mean3, dstat.getMean()));

    }
    
    // Test UnivariateStatistics impls for setter injection tests
    
    /**
     * A new way to compute the mean 
     */
    static class deepMean implements UnivariateStatistic {
        private static final long serialVersionUID = 9108665370122541953L;

        public double evaluate(double[] values, int begin, int length) {
            return 42;
        }

        public double evaluate(double[] values) {
            return 42;
        }  
        public UnivariateStatistic copy() {
            return new deepMean();
        }
    }
    
    /**
     * Test percentile implementation - wraps a Percentile
     */
    static class goodPercentile implements UnivariateStatistic {
        private static final long serialVersionUID = 801005145532790795L;
        private Percentile percentile = new Percentile();
        public void setQuantile(double quantile) {
            percentile.setQuantile(quantile);
        }
        public double evaluate(double[] values, int begin, int length) {
            return percentile.evaluate(values, begin, length);
        }
        public double evaluate(double[] values) {
            return percentile.evaluate(values);
        }  
        public UnivariateStatistic copy() {
            goodPercentile result = new goodPercentile();
            result.setQuantile(percentile.getQuantile());
            return result;
        }
    }
    
    /**
     * Test percentile subclass - another "new math" impl
     * Always returns currently set quantile
     */
    static class subPercentile extends Percentile {
        public double evaluate(double[] values, int begin, int length) {
            return getQuantile();
        }
        public double evaluate(double[] values) {
            return getQuantile();
        }  
        private static final long serialVersionUID = 8040701391045914979L;
        public Percentile copy() {
            subPercentile result = new subPercentile();
            return result;
        }
    }
    
    /**
     * "Bad" test percentile implementation - no setQuantile
     */
    static class badPercentile implements UnivariateStatistic {
        private static final long serialVersionUID = -707437653388052183L;
        private Percentile percentile = new Percentile();
        public double evaluate(double[] values, int begin, int length) {
            return percentile.evaluate(values, begin, length);
        }
        public double evaluate(double[] values) {
            return percentile.evaluate(values);
        }
        public UnivariateStatistic copy() {
            return new badPercentile();
        }
    }
    
}
