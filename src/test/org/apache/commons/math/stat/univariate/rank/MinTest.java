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
package org.apache.commons.math.stat.univariate.rank;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.math.stat.univariate.StorelessUnivariateStatisticAbstractTest;
import org.apache.commons.math.stat.univariate.UnivariateStatistic;

/**
 * Test cases for the {@link UnivariateStatistic} class.
 * @version $Revision: 1.12 $ $Date: 2004/06/18 22:10:35 $
 */
public class MinTest extends StorelessUnivariateStatisticAbstractTest{

    protected Min stat;
    
    /**
     * @param name
     */
    public MinTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(MinTest.class);
        suite.setName("Min  Tests");
        return suite;
    }
    
    /* (non-Javadoc)
     * @see org.apache.commons.math.stat.univariate.UnivariateStatisticAbstractTest#getUnivariateStatistic()
     */
    public UnivariateStatistic getUnivariateStatistic() {
        return new Min();
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.stat.univariate.UnivariateStatisticAbstractTest#expectedValue()
     */
    public double expectedValue() {
        return this.min;
    }
    
    public void testSpecialValues() {
        double[] testArray = {0d, Double.NaN, Double.POSITIVE_INFINITY, 
                Double.NEGATIVE_INFINITY};
        Min min = new Min();
        assertTrue(Double.isNaN(min.getResult()));
        min.increment(testArray[0]);
        assertEquals(0d, min.getResult(), 0);
        min.increment(testArray[1]);
        assertEquals(0d, min.getResult(), 0);
        min.increment(testArray[2]);
        assertEquals(0d, min.getResult(), 0);
        min.increment(testArray[3]);
        assertEquals(Double.NEGATIVE_INFINITY, min.getResult(), 0);
        assertEquals(Double.NEGATIVE_INFINITY, min.evaluate(testArray), 0);     
    }

}
