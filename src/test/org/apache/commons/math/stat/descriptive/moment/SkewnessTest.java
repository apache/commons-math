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
public class SkewnessTest extends StorelessUnivariateStatisticAbstractTest{

    protected Skewness stat;
    
    /**
     * @param name
     */
    public SkewnessTest(String name) {
        super(name);
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.stat.descriptive.UnivariateStatisticAbstractTest#getUnivariateStatistic()
     */
    public UnivariateStatistic getUnivariateStatistic() {
        return new Skewness();
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(SkewnessTest.class);
        suite.setName("Skewness Tests");
        return suite;
    }
    
    /* (non-Javadoc)
     * @see org.apache.commons.math.stat.descriptive.UnivariateStatisticAbstractTest#expectedValue()
     */
    public double expectedValue() {
        return this.skew;
    }
    
    /**
     * Make sure Double.NaN is returned iff n < 3
     *
     */
    public void testNaN() {
        Skewness skew = new Skewness();
        assertTrue(Double.isNaN(skew.getResult()));
        skew.increment(1d);
        assertTrue(Double.isNaN(skew.getResult()));
        skew.increment(1d);
        assertTrue(Double.isNaN(skew.getResult()));
        skew.increment(1d);
        assertFalse(Double.isNaN(skew.getResult()));      
    }

}
