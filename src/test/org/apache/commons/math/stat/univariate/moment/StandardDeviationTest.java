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
package org.apache.commons.math.stat.univariate.moment;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.math.stat.univariate.StorelessUnivariateStatisticAbstractTest;
import org.apache.commons.math.stat.univariate.UnivariateStatistic;

/**
 * Test cases for the {@link UnivariateStatistic} class.
 * 
 * @version $Revision: 1.10 $ $Date: 2004/02/21 21:35:18 $
 */
public class StandardDeviationTest extends StorelessUnivariateStatisticAbstractTest{

    protected StandardDeviation stat;
    
    /**
     * @param name
     */
    public StandardDeviationTest(String name) {
        super(name);
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.stat.univariate.UnivariateStatisticAbstractTest#getUnivariateStatistic()
     */
    public UnivariateStatistic getUnivariateStatistic() {
       
        if(stat == null)
            stat = new StandardDeviation();
            
        return stat;
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(StandardDeviationTest.class);
        suite.setName("StandardDeviation Tests");
        return suite;
    }
    
    /* (non-Javadoc)
     * @see org.apache.commons.math.stat.univariate.UnivariateStatisticAbstractTest#expectedValue()
     */
    public double expectedValue() {
        return this.std;
    }

}
