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
package org.apache.commons.math4.geometry.euclidean.oned;

import org.apache.commons.math4.geometry.euclidean.oned.Interval;
import org.apache.commons.math4.geometry.partitioning.Region;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.math4.util.Precision;
import org.apache.commons.math4.exception.NumberIsTooSmallException;
import org.junit.Assert;
import org.junit.Test;

public class IntervalTest {

    @Test
    public void testInterval() {
        Interval interval = new Interval(2.3, 5.7);
        Assert.assertEquals(3.4, interval.getSize(), 1.0e-10);
        Assert.assertEquals(4.0, interval.getBarycenter(), 1.0e-10);
        Assert.assertEquals(Region.Location.BOUNDARY, interval.checkPoint(2.3, 1.0e-10));
        Assert.assertEquals(Region.Location.BOUNDARY, interval.checkPoint(5.7, 1.0e-10));
        Assert.assertEquals(Region.Location.OUTSIDE,  interval.checkPoint(1.2, 1.0e-10));
        Assert.assertEquals(Region.Location.OUTSIDE,  interval.checkPoint(8.7, 1.0e-10));
        Assert.assertEquals(Region.Location.INSIDE,   interval.checkPoint(3.0, 1.0e-10));
        Assert.assertEquals(2.3, interval.getInf(), 1.0e-10);
        Assert.assertEquals(5.7, interval.getSup(), 1.0e-10);
    }

    @Test
    public void testTolerance() {
        Interval interval = new Interval(2.3, 5.7);
        Assert.assertEquals(Region.Location.OUTSIDE,  interval.checkPoint(1.2, 1.0));
        Assert.assertEquals(Region.Location.BOUNDARY, interval.checkPoint(1.2, 1.2));
        Assert.assertEquals(Region.Location.OUTSIDE,  interval.checkPoint(8.7, 2.9));
        Assert.assertEquals(Region.Location.BOUNDARY, interval.checkPoint(8.7, 3.1));
        Assert.assertEquals(Region.Location.INSIDE,   interval.checkPoint(3.0, 0.6));
        Assert.assertEquals(Region.Location.BOUNDARY, interval.checkPoint(3.0, 0.8));
    }

    @Test
    public void testInfinite() {
        Interval interval = new Interval(9.0, Double.POSITIVE_INFINITY);
        Assert.assertEquals(Region.Location.BOUNDARY, interval.checkPoint(9.0, 1.0e-10));
        Assert.assertEquals(Region.Location.OUTSIDE,  interval.checkPoint(8.4, 1.0e-10));
        for (double e = 1.0; e <= 6.0; e += 1.0) {
            Assert.assertEquals(Region.Location.INSIDE,
                                interval.checkPoint(FastMath.pow(10.0, e), 1.0e-10));
        }
        Assert.assertTrue(Double.isInfinite(interval.getSize()));
        Assert.assertEquals(9.0, interval.getInf(), 1.0e-10);
        Assert.assertTrue(Double.isInfinite(interval.getSup()));

    }

    @Test
    public void testSinglePoint() {
        Interval interval = new Interval(1.0, 1.0);
        Assert.assertEquals(0.0, interval.getSize(), Precision.SAFE_MIN);
        Assert.assertEquals(1.0, interval.getBarycenter(), Precision.EPSILON);
    }

    // MATH-1256
    @Test(expected=NumberIsTooSmallException.class)
    public void testStrictOrdering() {
        new Interval(0, -1);
    }
}
