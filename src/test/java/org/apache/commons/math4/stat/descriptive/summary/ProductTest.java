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
package org.apache.commons.math4.stat.descriptive.summary;

import org.apache.commons.math4.stat.descriptive.StorelessUnivariateStatistic;
import org.apache.commons.math4.stat.descriptive.StorelessUnivariateStatisticAbstractTest;
import org.apache.commons.math4.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math4.stat.descriptive.summary.Product;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the {@link UnivariateStatistic} class.
 */
public class ProductTest extends StorelessUnivariateStatisticAbstractTest{

    protected Product stat;

    /**
     * {@inheritDoc}
     */
    @Override
    public UnivariateStatistic getUnivariateStatistic() {
        return new Product();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getTolerance() {
        return 10E8;    //sic -- big absolute error due to only 15 digits of accuracy in double
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double expectedValue() {
        return this.product;
    }

    /** Expected value for  the testArray defined in UnivariateStatisticAbstractTest */
    public double expectedWeightedValue() {
        return this.weightedProduct;
    }

    @Test
    public void testSpecialValues() {
        Product product = new Product();
        Assert.assertEquals(1, product.getResult(), 0);
        product.increment(1);
        Assert.assertEquals(1, product.getResult(), 0);
        product.increment(Double.POSITIVE_INFINITY);
        Assert.assertEquals(Double.POSITIVE_INFINITY, product.getResult(), 0);
        product.increment(Double.NEGATIVE_INFINITY);
        Assert.assertEquals(Double.NEGATIVE_INFINITY, product.getResult(), 0);
        product.increment(Double.NaN);
        Assert.assertTrue(Double.isNaN(product.getResult()));
        product.increment(1);
        Assert.assertTrue(Double.isNaN(product.getResult()));
    }

    @Test
    public void testWeightedProduct() {
        Product product = new Product();
        Assert.assertEquals(expectedWeightedValue(),
                            product.evaluate(testArray, testWeightsArray, 0, testArray.length),getTolerance());
        Assert.assertEquals(expectedValue(),
                            product.evaluate(testArray, unitWeightsArray, 0, testArray.length), getTolerance());
    }

    @Override
    protected void checkClearValue(StorelessUnivariateStatistic statistic){
        Assert.assertEquals(1, statistic.getResult(), 0);
    }

}
