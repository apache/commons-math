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

package org.apache.commons.math3.optimization;


import org.apache.commons.math3.TestUtils;
import org.junit.Assert;
import org.junit.Test;

@Deprecated
public class PointValuePairTest {

    @Test
    public void testSerial() {
        PointValuePair pv1 = new PointValuePair(new double[] { 1.0, 2.0, 3.0 }, 4.0);
        PointValuePair pv2 = (PointValuePair) TestUtils.serializeAndRecover(pv1);
        Assert.assertEquals(pv1.getKey().length, pv2.getKey().length);
        for (int i = 0; i < pv1.getKey().length; ++i) {
            Assert.assertEquals(pv1.getKey()[i], pv2.getKey()[i], 1.0e-15);
        }
        Assert.assertEquals(pv1.getValue(), pv2.getValue(), 1.0e-15);
    }

}
