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
package org.apache.commons.math.stat.descriptive.summary;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.math.stat.descriptive.StorelessUnivariateStatisticAbstractTest;
import org.apache.commons.math.stat.descriptive.UnivariateStatistic;

/**
 * Test cases for the {@link UnivariateStatistic} class.
 * @version $Revision$ $Date$
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
     * @see org.apache.commons.math.stat.descriptive.UnivariateStatisticAbstractTest#getUnivariateStatistic()
     */
    public UnivariateStatistic getUnivariateStatistic() {
        return new Product();
    }

    public double getTolerance() {
        return 10E8;    //sic -- big absolute error due to only 15 digits of accuracy in double
    }
    
    /* (non-Javadoc)
     * @see org.apache.commons.math.stat.descriptive.UnivariateStatisticAbstractTest#expectedValue()
     */
    public double expectedValue() {
        return this.product;
    }
    
    public void testSpecialValues() {
        Product product = new Product();
        assertTrue(Double.isNaN(product.getResult()));
        product.increment(1);
        assertEquals(1, product.getResult(), 0);
        product.increment(Double.POSITIVE_INFINITY);
        assertEquals(Double.POSITIVE_INFINITY, product.getResult(), 0);
        product.increment(Double.NEGATIVE_INFINITY);
        assertEquals(Double.NEGATIVE_INFINITY, product.getResult(), 0);
        product.increment(Double.NaN);
        assertTrue(Double.isNaN(product.getResult())); 
        product.increment(1);
        assertTrue(Double.isNaN(product.getResult())); 
    }

}
