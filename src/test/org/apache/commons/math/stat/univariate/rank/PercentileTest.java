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
 * @version $Revision: 1.11 $ $Date: 2004/03/13 20:03:16 $
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
    
    public void testPercentile() {
        double[] d = new double[] {1, 3, 2, 4};
        Percentile p = new Percentile(30);
        assertEquals(1.5, p.evaluate(d), 1.0e-5);
        p.setQuantile(25);
        assertEquals(1.25, p.evaluate(d), 1.0e-5);
        p.setQuantile(75);
        assertEquals(3.75, p.evaluate(d), 1.0e-5);
        p.setQuantile(50);
        assertEquals(2.5, p.evaluate(d), 1.0e-5);
    }
    
    public void testNISTExample() {
        double[] d = new double[] {95.1772, 95.1567, 95.1937, 95.1959, 
                95.1442, 95.0610,  95.1591, 95.1195, 95.1772, 95.0925, 95.1990, 95.1682
        };
        Percentile p = new Percentile(90); 
        assertEquals(95.1981, p.evaluate(d), 1.0e-4);
    }
}
