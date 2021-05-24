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

package org.apache.commons.math4.utils.random;

import org.apache.commons.math4.utils.TestUtils;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.Assert;
import org.junit.Test;


public class GaussianRandomGeneratorTest {

    @Test
    public void testMeanAndStandardDeviation() {
        GaussianRandomGenerator generator = new GaussianRandomGenerator(RandomSource.create(RandomSource.MT, 17399225432l));
        double[] sample = new double[10000];
        for (int i = 0; i < sample.length; ++i) {
            sample[i] = generator.nextNormalizedDouble();
        }
        Assert.assertEquals(0.0, TestUtils.mean(sample), 0.012);
        Assert.assertEquals(1.0, TestUtils.variance(sample), 0.01);
    }
}
