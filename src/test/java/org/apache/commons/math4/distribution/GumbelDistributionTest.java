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
package org.apache.commons.math4.distribution;

import org.apache.commons.math4.distribution.GumbelDistribution;
import org.apache.commons.math4.util.Precision;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for GumbelDistribution.
 */
public class GumbelDistributionTest extends RealDistributionAbstractTest {

    @Test
    public void testParameters() {
        GumbelDistribution d = makeDistribution();
        Assert.assertEquals(0.5, d.getLocation(), Precision.EPSILON);
        Assert.assertEquals(2, d.getScale(), Precision.EPSILON);
    }

    @Test
    public void testSupport() {
        GumbelDistribution d = makeDistribution();
        Assert.assertTrue(Double.isInfinite(d.getSupportLowerBound()));
        Assert.assertTrue(Double.isInfinite(d.getSupportUpperBound()));
        Assert.assertTrue(d.isSupportConnected());
    }

    @Override
    public GumbelDistribution makeDistribution() {
        return new GumbelDistribution(0.5, 2);
    }

    @Override
    public double[] makeCumulativeTestPoints() {
        return new double[] {
            -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5
        };
    }

    @Override
    public double[] makeDensityTestValues() {
        return new double[] {
            1.258262e-06, 3.594689e-04, 9.115766e-03, 5.321100e-02, 1.274352e-01, 1.777864e-01,
            1.787177e-01, 1.472662e-01, 1.075659e-01, 7.302736e-02, 4.742782e-02
        };
    }

    @Override
    public double[] makeCumulativeTestValues() {
        return new double[] {
            1.608760e-07, 7.577548e-05, 3.168165e-03, 3.049041e-02, 1.203923e-01, 2.769203e-01,
            4.589561e-01, 6.235249e-01, 7.508835e-01, 8.404869e-01, 8.999652e-01
        };
    }

}
