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
package org.apache.commons.math3.stat.descriptive;


import java.util.Locale;


import org.apache.commons.math3.TestUtils;
import org.junit.Assert;
import org.junit.Test;
/**
 * Test cases for the {@link StatisticalSummaryValues} class.
 *
 */

public final class StatisticalSummaryValuesTest {

    @Test
    public void testSerialization() {
        StatisticalSummaryValues u = new StatisticalSummaryValues(1, 2, 3, 4, 5, 6);
        TestUtils.checkSerializedEquality(u);
        StatisticalSummaryValues t = (StatisticalSummaryValues) TestUtils.serializeAndRecover(u);
        verifyEquality(u, t);
    }

    @Test
    public void testEqualsAndHashCode() {
        StatisticalSummaryValues u  = new StatisticalSummaryValues(1, 2, 3, 4, 5, 6);
        StatisticalSummaryValues t = null;
        Assert.assertTrue("reflexive", u.equals(u));
        Assert.assertFalse("non-null compared to null", u.equals(t));
        Assert.assertFalse("wrong type", u.equals(Double.valueOf(0)));
        t = new StatisticalSummaryValues(1, 2, 3, 4, 5, 6);
        Assert.assertTrue("instances with same data should be equal", t.equals(u));
        Assert.assertEquals("hash code", u.hashCode(), t.hashCode());

        u = new StatisticalSummaryValues(Double.NaN, 2, 3, 4, 5, 6);
        t = new StatisticalSummaryValues(1, Double.NaN, 3, 4, 5, 6);
        Assert.assertFalse("instances based on different data should be different",
                (u.equals(t) ||t.equals(u)));
    }

    private void verifyEquality(StatisticalSummaryValues s, StatisticalSummaryValues u) {
        Assert.assertEquals("N",s.getN(),u.getN());
        TestUtils.assertEquals("sum",s.getSum(),u.getSum(), 0);
        TestUtils.assertEquals("var",s.getVariance(),u.getVariance(), 0);
        TestUtils.assertEquals("std",s.getStandardDeviation(),u.getStandardDeviation(), 0);
        TestUtils.assertEquals("mean",s.getMean(),u.getMean(), 0);
        TestUtils.assertEquals("min",s.getMin(),u.getMin(), 0);
        TestUtils.assertEquals("max",s.getMax(),u.getMax(), 0);
    }

    @Test
    public void testToString() {
        StatisticalSummaryValues u  = new StatisticalSummaryValues(4.5, 16, 10, 5, 4, 45);
        Locale d = Locale.getDefault();
        Locale.setDefault(Locale.US);
        Assert.assertEquals("StatisticalSummaryValues:\n" +
                     "n: 10\n" +
                     "min: 4.0\n" +
                     "max: 5.0\n" +
                     "mean: 4.5\n" +
                     "std dev: 4.0\n" +
                     "variance: 16.0\n" +
                     "sum: 45.0\n",  u.toString());
        Locale.setDefault(d);
    }
}
