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
 * @version $Revision$ $Date$
 */
public class KurtosisTest extends StorelessUnivariateStatisticAbstractTest{

    protected Kurtosis stat;
    
    /**
     * @param name
     */
    public KurtosisTest(String name) {
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(KurtosisTest.class);
        suite.setName("Kurtosis  Tests");
        return suite;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.stat.descriptive.UnivariateStatisticAbstractTest#getUnivariateStatistic()
     */
    public UnivariateStatistic getUnivariateStatistic() {
        return new Kurtosis();
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.stat.descriptive.UnivariateStatisticAbstractTest#expectedValue()
     */
    public double expectedValue() {
        return this.kurt;
    }
    
    /**
     * Make sure Double.NaN is returned iff n < 4
     *
     */
    public void testNaN() {
        Kurtosis kurt = new Kurtosis();
        assertTrue(Double.isNaN(kurt.getResult()));
        kurt.increment(1d);
        assertTrue(Double.isNaN(kurt.getResult()));
        kurt.increment(1d);
        assertTrue(Double.isNaN(kurt.getResult()));
        kurt.increment(1d);
        assertTrue(Double.isNaN(kurt.getResult()));
        kurt.increment(1d);
        assertFalse(Double.isNaN(kurt.getResult()));      
    }

}
