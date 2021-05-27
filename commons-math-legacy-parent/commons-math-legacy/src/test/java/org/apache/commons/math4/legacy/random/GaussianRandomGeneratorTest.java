//Licensed to the Apache Software Foundation (ASF) under one
//or more contributor license agreements.  See the NOTICE file
//distributed with this work for additional information
//regarding copyright ownership.  The ASF licenses this file
//to you under the Apache License, Version 2.0 (the
//"License"); you may not use this file except in compliance
//with the License.  You may obtain a copy of the License at

//http://www.apache.org/licenses/LICENSE-2.0

//Unless required by applicable law or agreed to in writing,
//software distributed under the License is distributed on an
//"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
//KIND, either express or implied.  See the License for the
//specific language governing permissions and limitations
//under the License.

package org.apache.commons.math4.legacy.random;

import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.math4.legacy.stat.StatUtils;
import org.junit.Assert;
import org.junit.Test;


public class GaussianRandomGeneratorTest {

    @Test
    public void testMeanAndStandardDeviation() {
        final GaussianRandomGenerator generator = new GaussianRandomGenerator(RandomSource.create(RandomSource.MT));
        final double[] sample = new double[10000];
        for (int i = 0; i < sample.length; ++i) {
            sample[i] = generator.nextNormalizedDouble();
        }
        final double mean = StatUtils.mean(sample);
        Assert.assertEquals("mean=" + mean, 0, mean, 1e-2);
        final double variance = StatUtils.variance(sample);
        Assert.assertEquals("variance=" + variance, 1, variance, 1e-2);
    }
}
