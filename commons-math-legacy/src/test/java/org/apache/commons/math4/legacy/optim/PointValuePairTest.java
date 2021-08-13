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
package org.apache.commons.math4.legacy.optim;

import org.apache.commons.math4.legacy.TestUtils;
import org.junit.Assert;
import org.junit.Test;

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

    @Test
    public void testEquals() {
        final double[] p1 = new double[] { 1 };
        final PointValuePair pv1 = new PointValuePair(p1, 2);
        Assert.assertNotEquals(pv1, null);

        final PointValuePair pv2 = new PointValuePair(pv1.getPointRef(), 3);
        // Same array reference, different objective values.
        Assert.assertNotEquals(pv1, pv2);

        final PointValuePair pv3 = new PointValuePair(pv2.getPoint(), pv2.getValue());
        // Different array reference, same array values, same objective values.
        Assert.assertEquals(pv2, pv3);

        final double[] p2 = new double[] { p1[0] + 1 };
        final PointValuePair pv4 = new PointValuePair(p2, pv2.getValue());
        // Different array values, same objective values.
        Assert.assertNotEquals(pv2, pv4);
    }
}
