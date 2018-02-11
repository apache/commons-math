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

import org.junit.Test;
import org.apache.commons.math3.util.Precision;
import org.apache.commons.math4.geometry.GeometryTestUtils;
import org.apache.commons.math4.geometry.Point;
import org.apache.commons.math4.geometry.Vector;
import org.junit.Assert;

public class OrientedPointTest {

    @Test
    public void testConstructor() {
        // act
        OrientedPoint pt = new OrientedPoint(new Cartesian1D(2.0), true, 1e-5);

        // assert
        Assert.assertEquals(2.0, pt.getLocation().getX(), Precision.EPSILON);
        Assert.assertTrue(pt.isDirect());
        Assert.assertEquals(1e-5, pt.getTolerance(), Precision.EPSILON);
    }

    @Test
    public void testCopySelf() {
        // arrange
        OrientedPoint orig = new OrientedPoint(new Cartesian1D(2.0), true, 1e-5);

        // act
        OrientedPoint copy = orig.copySelf();

        // assert
        Assert.assertSame(orig, copy);
        Assert.assertEquals(2.0, copy.getLocation().getX(), Precision.EPSILON);
        Assert.assertTrue(copy.isDirect());
        Assert.assertEquals(1e-5, copy.getTolerance(), Precision.EPSILON);
    }

    @Test
    public void testGetOffset_direct_point() {
        // arrange
        OrientedPoint pt = new OrientedPoint(new Cartesian1D(-1.0), true, 1e-5);

        // act/assert
        Assert.assertEquals(-99, pt.getOffset((Point<Euclidean1D>) new Cartesian1D(-100)), Precision.EPSILON);
        Assert.assertEquals(-1, pt.getOffset((Point<Euclidean1D>) new Cartesian1D(-2)), Precision.EPSILON);
        Assert.assertEquals(-0.01, pt.getOffset((Point<Euclidean1D>) new Cartesian1D(-1.01)), Precision.EPSILON);
        Assert.assertEquals(0.0, pt.getOffset((Point<Euclidean1D>) new Cartesian1D(-1.0)), Precision.EPSILON);
        Assert.assertEquals(0.01, pt.getOffset((Point<Euclidean1D>) new Cartesian1D(-0.99)), Precision.EPSILON);
        Assert.assertEquals(1, pt.getOffset((Point<Euclidean1D>) new Cartesian1D(0)), Precision.EPSILON);
        Assert.assertEquals(101, pt.getOffset((Point<Euclidean1D>) new Cartesian1D(100)), Precision.EPSILON);
    }

    @Test
    public void testGetOffset_notDirect_point() {
        // arrange
        OrientedPoint pt = new OrientedPoint(new Cartesian1D(-1.0), false, 1e-5);

        // act/assert
        Assert.assertEquals(99, pt.getOffset((Point<Euclidean1D>) new Cartesian1D(-100)), Precision.EPSILON);
        Assert.assertEquals(1, pt.getOffset((Point<Euclidean1D>) new Cartesian1D(-2)), Precision.EPSILON);
        Assert.assertEquals(0.01, pt.getOffset((Point<Euclidean1D>) new Cartesian1D(-1.01)), Precision.EPSILON);
        Assert.assertEquals(0.0, pt.getOffset((Point<Euclidean1D>) new Cartesian1D(-1.0)), Precision.EPSILON);
        Assert.assertEquals(-0.01, pt.getOffset((Point<Euclidean1D>) new Cartesian1D(-0.99)), Precision.EPSILON);
        Assert.assertEquals(-1, pt.getOffset((Point<Euclidean1D>) new Cartesian1D(0)), Precision.EPSILON);
        Assert.assertEquals(-101, pt.getOffset((Point<Euclidean1D>) new Cartesian1D(100)), Precision.EPSILON);
    }

    @Test
    public void testGetOffset_direct_vector() {
        // arrange
        OrientedPoint pt = new OrientedPoint(new Cartesian1D(-1.0), true, 1e-5);

        // act/assert
        Assert.assertEquals(-99, pt.getOffset((Vector<Euclidean1D>) new Cartesian1D(-100)), Precision.EPSILON);
        Assert.assertEquals(-1, pt.getOffset((Vector<Euclidean1D>) new Cartesian1D(-2)), Precision.EPSILON);
        Assert.assertEquals(-0.01, pt.getOffset((Vector<Euclidean1D>) new Cartesian1D(-1.01)), Precision.EPSILON);
        Assert.assertEquals(-0.0, pt.getOffset((Vector<Euclidean1D>) new Cartesian1D(-1.0)), Precision.EPSILON);
        Assert.assertEquals(0.01, pt.getOffset((Vector<Euclidean1D>) new Cartesian1D(-0.99)), Precision.EPSILON);
        Assert.assertEquals(1, pt.getOffset((Vector<Euclidean1D>) new Cartesian1D(0)), Precision.EPSILON);
        Assert.assertEquals(101, pt.getOffset((Vector<Euclidean1D>) new Cartesian1D(100)), Precision.EPSILON);
    }

    @Test
    public void testGetOffset_notDirect_vector() {
        // arrange
        OrientedPoint pt = new OrientedPoint(new Cartesian1D(-1.0), false, 1e-5);

        // act/assert
        Assert.assertEquals(99, pt.getOffset((Vector<Euclidean1D>) new Cartesian1D(-100)), Precision.EPSILON);
        Assert.assertEquals(1, pt.getOffset((Vector<Euclidean1D>) new Cartesian1D(-2)), Precision.EPSILON);
        Assert.assertEquals(0.01, pt.getOffset((Vector<Euclidean1D>) new Cartesian1D(-1.01)), Precision.EPSILON);
        Assert.assertEquals(0.0, pt.getOffset((Vector<Euclidean1D>) new Cartesian1D(-1.0)), Precision.EPSILON);
        Assert.assertEquals(-0.01, pt.getOffset((Vector<Euclidean1D>) new Cartesian1D(-0.99)), Precision.EPSILON);
        Assert.assertEquals(-1, pt.getOffset((Vector<Euclidean1D>) new Cartesian1D(0)), Precision.EPSILON);
        Assert.assertEquals(-101, pt.getOffset((Vector<Euclidean1D>) new Cartesian1D(100)), Precision.EPSILON);
    }

    @Test
    public void testWholeHyperplane() {
        // arrange
        OrientedPoint pt = new OrientedPoint(new Cartesian1D(1.0), false, 1e-5);

        // act
        SubOrientedPoint subPt = pt.wholeHyperplane();

        // assert
        Assert.assertSame(pt, subPt.getHyperplane());
        Assert.assertNull(subPt.getRemainingRegion());
    }

    @Test
    public void testWholeSpace() {
        // arrange
        OrientedPoint pt = new OrientedPoint(new Cartesian1D(1.0), false, 1e-5);

        // act
        IntervalsSet set = pt.wholeSpace();

        // assert
        GeometryTestUtils.assertNegativeInfinity(set.getInf());
        GeometryTestUtils.assertPositiveInfinity(set.getSup());
    }

    @Test
    public void testSameOrientationAs() {
        // arrange
        OrientedPoint notDirect1 = new OrientedPoint(new Cartesian1D(1.0), false, 1e-5);
        OrientedPoint notDirect2 = new OrientedPoint(new Cartesian1D(1.0), false, 1e-5);
        OrientedPoint direct1 = new OrientedPoint(new Cartesian1D(1.0), true, 1e-5);
        OrientedPoint direct2 = new OrientedPoint(new Cartesian1D(1.0), true, 1e-5);

        // act/assert
        Assert.assertTrue(notDirect1.sameOrientationAs(notDirect1));
        Assert.assertTrue(notDirect1.sameOrientationAs(notDirect2));
        Assert.assertTrue(notDirect2.sameOrientationAs(notDirect1));

        Assert.assertTrue(direct1.sameOrientationAs(direct1));
        Assert.assertTrue(direct1.sameOrientationAs(direct2));
        Assert.assertTrue(direct2.sameOrientationAs(direct1));

        Assert.assertFalse(notDirect1.sameOrientationAs(direct1));
        Assert.assertFalse(direct1.sameOrientationAs(notDirect1));
    }

    @Test
    public void testProject() {
        // arrange
        OrientedPoint pt = new OrientedPoint(new Cartesian1D(1.0), true, 1e-5);

        // act/assert
        Assert.assertEquals(1.0, ((Cartesian1D) pt.project(new Cartesian1D(-1.0))).getX(), Precision.EPSILON);
        Assert.assertEquals(1.0, ((Cartesian1D) pt.project(new Cartesian1D(0.0))).getX(), Precision.EPSILON);
        Assert.assertEquals(1.0, ((Cartesian1D) pt.project(new Cartesian1D(1.0))).getX(), Precision.EPSILON);
        Assert.assertEquals(1.0, ((Cartesian1D) pt.project(new Cartesian1D(100.0))).getX(), Precision.EPSILON);
    }

    @Test
    public void testRevertSelf() {
        // arrange
        OrientedPoint pt = new OrientedPoint(new Cartesian1D(2.0), true, 1e-5);

        // act
        pt.revertSelf();

        // assert
        Assert.assertEquals(2.0, pt.getLocation().getX(), Precision.EPSILON);
        Assert.assertFalse(pt.isDirect());
        Assert.assertEquals(1e-5, pt.getTolerance(), Precision.EPSILON);

        Assert.assertEquals(1, pt.getOffset((Vector<Euclidean1D>) new Cartesian1D(1.0)), Precision.EPSILON);
        Assert.assertEquals(-1, pt.getOffset((Vector<Euclidean1D>) new Cartesian1D(3.0)), Precision.EPSILON);
    }
}
