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

import org.apache.commons.math.stat.univariate.UnivariateStatistic;
import org.apache.commons.math.stat.univariate.UnivariateStatisticAbstractTest;

/**
 * Test cases for the {@link UnivariateStatistic} class.
 * @version $Revision: 1.10 $ $Date: 2004/02/21 21:35:18 $
 */
public class PercentileTest extends UnivariateStatisticAbstractTest{

    protected Percentile stat;
    
    /**
     * @param name
     */
    public PercentileTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(PercentileTest.class);
        suite.setName("Percentile Tests");
        return suite;
    }
    
    /* (non-Javadoc)
     * @see org.apache.commons.math.stat.univariate.UnivariateStatisticAbstractTest#getUnivariateStatistic()
     */
    public UnivariateStatistic getUnivariateStatistic() {
       
        if(stat == null)
            stat = new Percentile(95.0);
            
        return stat;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.stat.univariate.UnivariateStatisticAbstractTest#expectedValue()
     */
    public double expectedValue() {
        // TODO: fix this bad calculation in Percentile
        return 20.82;
    }

    public void testHighPercentile(){
        double[] d = new double[]{1, 2, 3};
        Percentile p = new Percentile(75);
        assertEquals(3.0, p.evaluate(d), 1.0e-5);
    }
}
