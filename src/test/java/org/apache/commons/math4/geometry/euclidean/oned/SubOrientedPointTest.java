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

import java.util.List;

import org.apache.commons.math4.geometry.partitioning.Side;
import org.apache.commons.math4.geometry.partitioning.SubHyperplane;
import org.apache.commons.math4.geometry.partitioning.SubHyperplane.SplitSubHyperplane;
import org.junit.Assert;
import org.junit.Test;

public class SubOrientedPointTest {
    private static final double TEST_TOLERANCE = 1e-10;

    @Test
    public void testGetSize() {
        // arrange
        OrientedPoint hyperplane = new OrientedPoint(new Cartesian1D(1), true, TEST_TOLERANCE);
        SubOrientedPoint pt = (SubOrientedPoint) hyperplane.wholeHyperplane();

        // act/assert
        Assert.assertEquals(0.0, pt.getSize(), TEST_TOLERANCE);
    }

    @Test
    public void testIsEmpty() {
        // arrange
        OrientedPoint hyperplane = new OrientedPoint(new Cartesian1D(1), true, TEST_TOLERANCE);
        SubOrientedPoint pt = (SubOrientedPoint) hyperplane.wholeHyperplane();

        // act/assert
        Assert.assertFalse(pt.isEmpty());
    }

    @Test
    public void testBuildNew() {
        // arrange
        OrientedPoint originalHyperplane = new OrientedPoint(new Cartesian1D(1), true, TEST_TOLERANCE);
        SubOrientedPoint pt = (SubOrientedPoint) originalHyperplane.wholeHyperplane();

        OrientedPoint hyperplane = new OrientedPoint(new Cartesian1D(2), true, TEST_TOLERANCE);
        IntervalsSet intervals = new IntervalsSet(2, 3, TEST_TOLERANCE);

        // act
        SubHyperplane<Euclidean1D> result = pt.buildNew(hyperplane, intervals);

        // assert
        Assert.assertTrue(result instanceof SubOrientedPoint);
        Assert.assertSame(hyperplane, result.getHyperplane());
        Assert.assertSame(intervals, ((SubOrientedPoint) result).getRemainingRegion());
    }

    @Test
    public void testSplit_resultOnMinusSide() {
        // arrange
        OrientedPoint hyperplane = new OrientedPoint(new Cartesian1D(1), true, TEST_TOLERANCE);
        IntervalsSet interval = new IntervalsSet(4, 5, TEST_TOLERANCE);
        SubOrientedPoint pt = new SubOrientedPoint(hyperplane, interval);

        OrientedPoint splitter = new OrientedPoint(new Cartesian1D(2), true, TEST_TOLERANCE);

        // act
        SplitSubHyperplane<Euclidean1D> split = pt.split(splitter);

        // assert
        Assert.assertEquals(Side.MINUS, split.getSide());

        SubOrientedPoint minusSub = ((SubOrientedPoint) split.getMinus());
        Assert.assertNotNull(minusSub);

        OrientedPoint minusHyper = (OrientedPoint) minusSub.getHyperplane();
        Assert.assertEquals(1, minusHyper.getLocation().getX(), TEST_TOLERANCE);

        List<Interval> minusIntervals = ((IntervalsSet) minusSub.getRemainingRegion()).asList();
        Assert.assertEquals(1, minusIntervals.size());
        Assert.assertEquals(4, minusIntervals.get(0).getInf(), TEST_TOLERANCE);
        Assert.assertEquals(5, minusIntervals.get(0).getSup(), TEST_TOLERANCE);

        Assert.assertNull(split.getPlus());
    }

    @Test
    public void testSplit_resultOnPlusSide() {
        // arrange
        OrientedPoint hyperplane = new OrientedPoint(new Cartesian1D(1), true, TEST_TOLERANCE);
        IntervalsSet interval = new IntervalsSet(4, 5, TEST_TOLERANCE);
        SubOrientedPoint pt = new SubOrientedPoint(hyperplane, interval);

        OrientedPoint splitter = new OrientedPoint(new Cartesian1D(0), true, TEST_TOLERANCE);

        // act
        SplitSubHyperplane<Euclidean1D> split = pt.split(splitter);

        // assert
        Assert.assertEquals(Side.PLUS, split.getSide());

        Assert.assertNull(split.getMinus());

        SubOrientedPoint plusSub = ((SubOrientedPoint) split.getPlus());
        Assert.assertNotNull(plusSub);

        OrientedPoint plusHyper = (OrientedPoint) plusSub.getHyperplane();
        Assert.assertEquals(1, plusHyper.getLocation().getX(), TEST_TOLERANCE);

        List<Interval> plusIntervals = ((IntervalsSet) plusSub.getRemainingRegion()).asList();
        Assert.assertEquals(1, plusIntervals.size());
        Assert.assertEquals(4, plusIntervals.get(0).getInf(), TEST_TOLERANCE);
        Assert.assertEquals(5, plusIntervals.get(0).getSup(), TEST_TOLERANCE);
    }

    @Test
    public void testSplit_equivalentHyperplanes() {
        // arrange
        OrientedPoint hyperplane = new OrientedPoint(new Cartesian1D(1), true, TEST_TOLERANCE);
        IntervalsSet interval = new IntervalsSet(4, 5, TEST_TOLERANCE);
        SubOrientedPoint pt = new SubOrientedPoint(hyperplane, interval);

        OrientedPoint splitter = new OrientedPoint(new Cartesian1D(1), true, TEST_TOLERANCE);

        // act
        SplitSubHyperplane<Euclidean1D> split = pt.split(splitter);

        // assert
        Assert.assertEquals(Side.HYPER, split.getSide());

        Assert.assertNull(split.getMinus());
        Assert.assertNull(split.getPlus());
    }

    @Test
    public void testSplit_usesToleranceFromParentHyperplane() {
        // arrange
        OrientedPoint hyperplane = new OrientedPoint(new Cartesian1D(1), true, 0.1);
        SubOrientedPoint pt = (SubOrientedPoint) hyperplane.wholeHyperplane();

        // act/assert
        SplitSubHyperplane<Euclidean1D> plusSplit = pt.split(new OrientedPoint(new Cartesian1D(0.899), true, 1e-10));
        Assert.assertNull(plusSplit.getMinus());
        Assert.assertNotNull(plusSplit.getPlus());

        SplitSubHyperplane<Euclidean1D> lowWithinTolerance = pt.split(new OrientedPoint(new Cartesian1D(0.901), true, 1e-10));
        Assert.assertNull(lowWithinTolerance.getMinus());
        Assert.assertNull(lowWithinTolerance.getPlus());

        SplitSubHyperplane<Euclidean1D> highWithinTolerance = pt.split(new OrientedPoint(new Cartesian1D(1.09), true, 1e-10));
        Assert.assertNull(highWithinTolerance.getMinus());
        Assert.assertNull(highWithinTolerance.getPlus());

        SplitSubHyperplane<Euclidean1D> minusSplit = pt.split(new OrientedPoint(new Cartesian1D(1.101), true, 1e-10));
        Assert.assertNotNull(minusSplit.getMinus());
        Assert.assertNull(minusSplit.getPlus());
    }
}