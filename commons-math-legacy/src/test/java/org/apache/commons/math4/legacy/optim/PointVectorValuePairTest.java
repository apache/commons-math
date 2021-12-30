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

public class PointVectorValuePairTest {
    @Test
    public void testAccessors1() {
        final double[] k = new double[] { 1.0, 2.0, 3.0 };
        final double[] v = new double[] { 4.0, 5.0 };

        final PointVectorValuePair pv = new PointVectorValuePair(k, v);

        final double[] kC = pv.getPoint();
        kC[0] = 1 - kC[0];
        final double[] vC = pv.getValue();
        vC[0] = 1 - vC[0];

        // Check that "pv" has not been changed.
        TestUtils.assertEquals("k", k, pv.getPoint(), 0d);
        TestUtils.assertEquals("v", v, pv.getValue(), 0d);
    }

    @Test
    public void testAccessors2() {
        final double[] k = new double[] { 1.0, 2.0, 3.0 };
        final double[] v = new double[] { 4.0, 5.0 };

        final PointVectorValuePair pv = new PointVectorValuePair(k, v);

        final double[] kC = pv.getPointRef();
        kC[0] = 1 - kC[0];
        final double[] vC = pv.getValueRef();
        vC[0] = 1 - vC[0];

        // Check that "pv" has been changed.
        Assert.assertEquals(kC[0], pv.getPoint()[0], 0d);
        Assert.assertEquals(vC[0], pv.getValue()[0], 0d);
    }
}
