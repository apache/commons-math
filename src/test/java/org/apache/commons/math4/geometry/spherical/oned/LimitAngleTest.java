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
package org.apache.commons.math4.geometry.spherical.oned;

import org.apache.commons.math4.geometry.spherical.oned.LimitAngle;
import org.apache.commons.math4.geometry.spherical.oned.S1Point;
import org.apache.commons.math4.util.MathUtils;
import org.junit.Assert;
import org.junit.Test;

public class LimitAngleTest {

    @Test
    public void testReversedLimit() {
        for (int k = -2; k < 3; ++k) {
            LimitAngle l  = new LimitAngle(new S1Point(1.0 + k * MathUtils.TWO_PI), false, 1.0e-10);
            Assert.assertEquals(l.getLocation().getAlpha(), l.getReverse().getLocation().getAlpha(), 1.0e-10);
            Assert.assertEquals(l.getTolerance(), l.getReverse().getTolerance(), 1.0e-10);
            Assert.assertTrue(l.sameOrientationAs(l));
            Assert.assertFalse(l.sameOrientationAs(l.getReverse()));
            Assert.assertEquals(MathUtils.TWO_PI, l.wholeSpace().getSize(), 1.0e-10);
            Assert.assertEquals(MathUtils.TWO_PI, l.getReverse().wholeSpace().getSize(), 1.0e-10);
        }
    }

}
