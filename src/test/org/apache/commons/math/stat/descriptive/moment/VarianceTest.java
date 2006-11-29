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
package org.apache.commons.math.stat.descriptive.moment;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.math.stat.descriptive.StorelessUnivariateStatisticAbstractTest;
import org.apache.commons.math.stat.descriptive.UnivariateStatistic;

/**
 * Test cases for the {@link UnivariateStatistic} class.
 * 
 * @version $Revision$ $Date$
 */
public class VarianceTest extends StorelessUnivariateStatisticAbstractTest{

    protected Variance stat;
    
    /**
     * @param name
     */
    public VarianceTest(String name) {
        super(name);
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.stat.descriptive.UnivariateStatisticAbstractTest#getUnivariateStatistic()
     */
    public UnivariateStatistic getUnivariateStatistic() {
        return new Variance();
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(VarianceTest.class);
        suite.setName("Variance Tests");
        return suite;
    }
    
    /* (non-Javadoc)
     * @see org.apache.commons.math.stat.descriptive.UnivariateStatisticAbstractTest#expectedValue()
     */
    public double expectedValue() {
        return this.var;
    }
    
    /**
     * Make sure Double.NaN is returned iff n = 0
     *
     */
    public void testNaN() {
        StandardDeviation std = new StandardDeviation();
        assertTrue(Double.isNaN(std.getResult()));
        std.increment(1d);
        assertEquals(0d, std.getResult(), 0);
    }
    
    /**
     * Test population version of variance
     */ 
    public void testPopulation() {
        double[] values = {-1.0d, 3.1d, 4.0d, -2.1d, 22d, 11.7d, 3d, 14d};
        SecondMoment m = new SecondMoment();
        m.evaluate(values);  // side effect is to add values
        Variance v1 = new Variance();
        v1.setBiasCorrected(false);
        assertEquals(populationVariance(values), v1.evaluate(values), 1E-14);
        v1.incrementAll(values);
        assertEquals(populationVariance(values), v1.getResult(), 1E-14);
        v1 = new Variance(false, m);
        assertEquals(populationVariance(values), v1.getResult(), 1E-14);     
        v1 = new Variance(false);
        assertEquals(populationVariance(values), v1.evaluate(values), 1E-14);
        v1.incrementAll(values);
        assertEquals(populationVariance(values), v1.getResult(), 1E-14);     
    }
    
    /**
     * Definitional formula for population variance
     */
    protected double populationVariance(double[] v) {
        double mean = new Mean().evaluate(v);
        double sum = 0;
        for (int i = 0; i < v.length; i++) {
           sum += (v[i] - mean) * (v[i] - mean); 
        }
        return sum / (double) v.length;
    }

}
