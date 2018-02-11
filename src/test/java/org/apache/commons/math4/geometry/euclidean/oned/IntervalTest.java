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

import org.apache.commons.math4.geometry.GeometryTestUtils;
import org.apache.commons.math4.geometry.euclidean.oned.Interval;
import org.apache.commons.math4.geometry.partitioning.Region;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.numbers.core.Precision;
import org.apache.commons.math4.exception.NumberIsTooSmallException;
import org.junit.Assert;
import org.junit.Test;

public class IntervalTest {

    private static final double TEST_TOLERANCE = 1e-10;

    @Test
    public void testBasicProperties() {
        // arrange
        Interval interval = new Interval(2.3, 5.7);

        // act/assert
        Assert.assertEquals(3.4, interval.getSize(), TEST_TOLERANCE);
        Assert.assertEquals(4.0, interval.getBarycenter(), TEST_TOLERANCE);
        Assert.assertEquals(2.3, interval.getInf(), TEST_TOLERANCE);
        Assert.assertEquals(5.7, interval.getSup(), TEST_TOLERANCE);
    }

    @Test
    public void testBasicProperties_negativeValues() {
        // arrange
        Interval interval = new Interval(-5.7, -2.3);

        // act/assert
        Assert.assertEquals(3.4, interval.getSize(), TEST_TOLERANCE);
        Assert.assertEquals(-4.0, interval.getBarycenter(), TEST_TOLERANCE);
        Assert.assertEquals(-5.7, interval.getInf(), TEST_TOLERANCE);
        Assert.assertEquals(-2.3, interval.getSup(), TEST_TOLERANCE);
    }

    // MATH-1256
    @Test(expected = NumberIsTooSmallException.class)
    public void testStrictOrdering() {
        new Interval(0, -1);
    }

    @Test
    public void testCheckPoint() {
        // arrange
        Interval interval = new Interval(2.3, 5.7);

        // act/assert
        Assert.assertEquals(Region.Location.OUTSIDE,  interval.checkPoint(1.2, TEST_TOLERANCE));

        Assert.assertEquals(Region.Location.OUTSIDE, interval.checkPoint(2.2, TEST_TOLERANCE));
        Assert.assertEquals(Region.Location.BOUNDARY, interval.checkPoint(2.3, TEST_TOLERANCE));
        Assert.assertEquals(Region.Location.INSIDE, interval.checkPoint(2.4, TEST_TOLERANCE));

        Assert.assertEquals(Region.Location.INSIDE,   interval.checkPoint(3.0, TEST_TOLERANCE));

        Assert.assertEquals(Region.Location.INSIDE, interval.checkPoint(5.6, TEST_TOLERANCE));
        Assert.assertEquals(Region.Location.BOUNDARY, interval.checkPoint(5.7, TEST_TOLERANCE));
        Assert.assertEquals(Region.Location.OUTSIDE, interval.checkPoint(5.8, TEST_TOLERANCE));

        Assert.assertEquals(Region.Location.OUTSIDE,  interval.checkPoint(8.7, TEST_TOLERANCE));

        Assert.assertEquals(Region.Location.OUTSIDE, interval.checkPoint(Double.NEGATIVE_INFINITY, TEST_TOLERANCE));
        Assert.assertEquals(Region.Location.OUTSIDE, interval.checkPoint(Double.POSITIVE_INFINITY, TEST_TOLERANCE));
        Assert.assertEquals(Region.Location.BOUNDARY, interval.checkPoint(Double.NaN, TEST_TOLERANCE));
    }

    @Test
    public void testCheckPoint_tolerance() {
        // arrange
        Interval interval = new Interval(2.3, 5.7);

        // act/assert
        Assert.assertEquals(Region.Location.OUTSIDE, interval.checkPoint(2.29, 1e-3));
        Assert.assertEquals(Region.Location.BOUNDARY, interval.checkPoint(2.29, 1e-2));
        Assert.assertEquals(Region.Location.BOUNDARY, interval.checkPoint(2.29, 1e-1));
        Assert.assertEquals(Region.Location.BOUNDARY, interval.checkPoint(2.29, 1));
        Assert.assertEquals(Region.Location.BOUNDARY, interval.checkPoint(2.29, 2));

        Assert.assertEquals(Region.Location.INSIDE, interval.checkPoint(4.0, 1e-3));
        Assert.assertEquals(Region.Location.INSIDE, interval.checkPoint(4.0, 1e-2));
        Assert.assertEquals(Region.Location.INSIDE, interval.checkPoint(4.0, 1e-1));
        Assert.assertEquals(Region.Location.INSIDE, interval.checkPoint(4.0, 1));
        Assert.assertEquals(Region.Location.BOUNDARY, interval.checkPoint(4.0, 2));
    }

    @Test
    public void testInfinite_inf() {
        // act
        Interval interval = new Interval(Double.NEGATIVE_INFINITY, 9);

        // assert
        Assert.assertEquals(Region.Location.BOUNDARY, interval.checkPoint(9.0, TEST_TOLERANCE));
        Assert.assertEquals(Region.Location.OUTSIDE,  interval.checkPoint(9.4, TEST_TOLERANCE));
        for (double e = 1.0; e <= 6.0; e += 1.0) {
            Assert.assertEquals(Region.Location.INSIDE,
                                interval.checkPoint(-1 * FastMath.pow(10.0, e), TEST_TOLERANCE));
        }
        GeometryTestUtils.assertPositiveInfinity(interval.getSize());
        GeometryTestUtils.assertNegativeInfinity(interval.getInf());
        Assert.assertEquals(9.0, interval.getSup(), TEST_TOLERANCE);
    }

    @Test
    public void testInfinite_sup() {
        // act
        Interval interval = new Interval(9.0, Double.POSITIVE_INFINITY);

        // assert
        Assert.assertEquals(Region.Location.BOUNDARY, interval.checkPoint(9.0, TEST_TOLERANCE));
        Assert.assertEquals(Region.Location.OUTSIDE,  interval.checkPoint(8.4, TEST_TOLERANCE));
        for (double e = 1.0; e <= 6.0; e += 1.0) {
            Assert.assertEquals(Region.Location.INSIDE,
                                interval.checkPoint(FastMath.pow(10.0, e), TEST_TOLERANCE));
        }
        GeometryTestUtils.assertPositiveInfinity(interval.getSize());
        Assert.assertEquals(9.0, interval.getInf(), TEST_TOLERANCE);
        GeometryTestUtils.assertPositiveInfinity(interval.getSup());
    }

    @Test
    public void testInfinite_infAndSup() {
        // act
        Interval interval = new Interval(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

        // assert
        for (double e = 1.0; e <= 6.0; e += 1.0) {
            Assert.assertEquals(Region.Location.INSIDE,
                                interval.checkPoint(FastMath.pow(10.0, e), TEST_TOLERANCE));
        }
        GeometryTestUtils.assertPositiveInfinity(interval.getSize());
        GeometryTestUtils.assertNegativeInfinity(interval.getInf());
        GeometryTestUtils.assertPositiveInfinity(interval.getSup());
    }

    @Test
    public void testSinglePoint() {
        // act
        Interval interval = new Interval(1.0, 1.0);

        // assert
        Assert.assertEquals(0.0, interval.getSize(), Precision.SAFE_MIN);
        Assert.assertEquals(1.0, interval.getBarycenter(), Precision.EPSILON);
    }

    @Test
    public void testSingleInfinitePoint_positive() {
        // act
        Interval interval = new Interval(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

        // assert
        Assert.assertTrue(Double.isNaN(interval.getSize())); // inf - inf = NaN according to floating point spec
        GeometryTestUtils.assertPositiveInfinity(interval.getBarycenter());
    }

    @Test
    public void testSingleInfinitePoint_negative() {
        // act
        Interval interval = new Interval(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);

        // assert
        Assert.assertTrue(Double.isNaN(interval.getSize())); // inf - inf = NaN according to floating point spec
        GeometryTestUtils.assertNegativeInfinity(interval.getBarycenter());
    }
}
