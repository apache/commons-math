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
package org.apache.commons.math.stat.univariate.summary;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.math.stat.univariate.StorelessUnivariateStatisticAbstractTest;
import org.apache.commons.math.stat.univariate.UnivariateStatistic;

/**
 * Test cases for the {@link UnivariateStatistic} class.
 * @version $Revision: 1.12 $ $Date: 2004/06/17 21:37:05 $
 */
public class ProductTest extends StorelessUnivariateStatisticAbstractTest{

    protected Product stat;
    
    /**
     * @param name
     */
    public ProductTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(ProductTest.class);
        suite.setName("Product Tests");
        return suite;
    }
    
    /* (non-Javadoc)
     * @see org.apache.commons.math.stat.univariate.UnivariateStatisticAbstractTest#getUnivariateStatistic()
     */
    public UnivariateStatistic getUnivariateStatistic() {
        return new Product();
    }

    public double getTolerance() {
        return 10E8;    //sic -- big absolute error due to only 15 digits of accuracy in double
    }
    
    /* (non-Javadoc)
     * @see org.apache.commons.math.stat.univariate.UnivariateStatisticAbstractTest#expectedValue()
     */
    public double expectedValue() {
        return this.product;
    }

}
