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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.math4.exception.NumberIsTooLargeException;
import org.apache.commons.math4.geometry.partitioning.BSPTree;
import org.apache.commons.math4.geometry.partitioning.Region;
import org.apache.commons.math4.geometry.partitioning.RegionFactory;
import org.apache.commons.math4.geometry.partitioning.Side;
import org.apache.commons.math4.geometry.partitioning.SubHyperplane;
import org.apache.commons.math4.geometry.partitioning.Region.Location;
import org.apache.commons.math4.geometry.spherical.oned.Arc;
import org.apache.commons.math4.geometry.spherical.oned.ArcsSet;
import org.apache.commons.math4.geometry.spherical.oned.LimitAngle;
import org.apache.commons.math4.geometry.spherical.oned.S1Point;
import org.apache.commons.math4.geometry.spherical.oned.Sphere1D;
import org.apache.commons.math4.geometry.spherical.oned.SubLimitAngle;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.math4.util.MathUtils;
import org.apache.commons.math4.util.Precision;
import org.junit.Assert;
import org.junit.Test;

public class ArcsSetTest {

    @Test
    public void testArc() {
        ArcsSet set = new ArcsSet(2.3, 5.7, 1.0e-10);
        Assert.assertEquals(3.4, set.getSize(), 1.0e-10);
        Assert.assertEquals(1.0e-10, set.getTolerance(), 1.0e-20);
        Assert.assertEquals(Region.Location.BOUNDARY, set.checkPoint(new S1Point(2.3)));
        Assert.assertEquals(Region.Location.BOUNDARY, set.checkPoint(new S1Point(5.7)));
        Assert.assertEquals(Region.Location.OUTSIDE,  set.checkPoint(new S1Point(1.2)));
        Assert.assertEquals(Region.Location.OUTSIDE,  set.checkPoint(new S1Point(8.5)));
        Assert.assertEquals(Region.Location.INSIDE,   set.checkPoint(new S1Point(8.7)));
        Assert.assertEquals(Region.Location.INSIDE,   set.checkPoint(new S1Point(3.0)));
        Assert.assertEquals(1, set.asList().size());
        Assert.assertEquals(2.3, set.asList().get(0).getInf(), 1.0e-10);
        Assert.assertEquals(5.7, set.asList().get(0).getSup(), 1.0e-10);
    }

    @Test
    public void testWrapAround2PiArc() {
        ArcsSet set = new ArcsSet(5.7 - MathUtils.TWO_PI, 2.3, 1.0e-10);
        Assert.assertEquals(MathUtils.TWO_PI - 3.4, set.getSize(), 1.0e-10);
        Assert.assertEquals(1.0e-10, set.getTolerance(), 1.0e-20);
        Assert.assertEquals(Region.Location.BOUNDARY, set.checkPoint(new S1Point(2.3)));
        Assert.assertEquals(Region.Location.BOUNDARY, set.checkPoint(new S1Point(5.7)));
        Assert.assertEquals(Region.Location.INSIDE,   set.checkPoint(new S1Point(1.2)));
        Assert.assertEquals(Region.Location.INSIDE,   set.checkPoint(new S1Point(8.5)));
        Assert.assertEquals(Region.Location.OUTSIDE,  set.checkPoint(new S1Point(8.7)));
        Assert.assertEquals(Region.Location.OUTSIDE,  set.checkPoint(new S1Point(3.0)));
        Assert.assertEquals(1, set.asList().size());
        Assert.assertEquals(5.7, set.asList().get(0).getInf(), 1.0e-10);
        Assert.assertEquals(2.3 + MathUtils.TWO_PI, set.asList().get(0).getSup(), 1.0e-10);
    }

    @Test
    public void testSplitOver2Pi() {
        ArcsSet set = new ArcsSet(1.0e-10);
        Arc     arc = new Arc(1.5 * FastMath.PI, 2.5 * FastMath.PI, 1.0e-10);
        ArcsSet.Split split = set.split(arc);
        for (double alpha = 0.0; alpha <= MathUtils.TWO_PI; alpha += 0.01) {
            S1Point p = new S1Point(alpha);
            if (alpha < 0.5 * FastMath.PI || alpha > 1.5 * FastMath.PI) {
                Assert.assertEquals(Location.OUTSIDE, split.getPlus().checkPoint(p));
                Assert.assertEquals(Location.INSIDE,  split.getMinus().checkPoint(p));
            } else {
                Assert.assertEquals(Location.INSIDE,  split.getPlus().checkPoint(p));
                Assert.assertEquals(Location.OUTSIDE, split.getMinus().checkPoint(p));
            }
        }
    }

    @Test
    public void testSplitAtEnd() {
        ArcsSet set = new ArcsSet(1.0e-10);
        Arc     arc = new Arc(FastMath.PI, MathUtils.TWO_PI, 1.0e-10);
        ArcsSet.Split split = set.split(arc);
        for (double alpha = 0.01; alpha < MathUtils.TWO_PI; alpha += 0.01) {
            S1Point p = new S1Point(alpha);
            if (alpha > FastMath.PI) {
                Assert.assertEquals(Location.OUTSIDE, split.getPlus().checkPoint(p));
                Assert.assertEquals(Location.INSIDE,  split.getMinus().checkPoint(p));
            } else {
                Assert.assertEquals(Location.INSIDE,  split.getPlus().checkPoint(p));
                Assert.assertEquals(Location.OUTSIDE, split.getMinus().checkPoint(p));
            }
        }

        S1Point zero = new S1Point(0.0);
        Assert.assertEquals(Location.BOUNDARY,  split.getPlus().checkPoint(zero));
        Assert.assertEquals(Location.BOUNDARY,  split.getMinus().checkPoint(zero));

        S1Point pi = new S1Point(FastMath.PI);
        Assert.assertEquals(Location.BOUNDARY,  split.getPlus().checkPoint(pi));
        Assert.assertEquals(Location.BOUNDARY,  split.getMinus().checkPoint(pi));

    }

    @Test(expected=NumberIsTooLargeException.class)
    public void testWrongInterval() {
        new ArcsSet(1.2, 0.0, 1.0e-10);
    }

    @Test
    public void testFullEqualEndPoints() {
        ArcsSet set = new ArcsSet(1.0, 1.0, 1.0e-10);
        Assert.assertEquals(1.0e-10, set.getTolerance(), 1.0e-20);
        Assert.assertEquals(Region.Location.INSIDE, set.checkPoint(new S1Point(9.0)));
        for (double alpha = -20.0; alpha <= 20.0; alpha += 0.1) {
            Assert.assertEquals(Region.Location.INSIDE, set.checkPoint(new S1Point(alpha)));
        }
        Assert.assertEquals(1, set.asList().size());
        Assert.assertEquals(0.0, set.asList().get(0).getInf(), 1.0e-10);
        Assert.assertEquals(2 * FastMath.PI, set.asList().get(0).getSup(), 1.0e-10);
        Assert.assertEquals(2 * FastMath.PI, set.getSize(), 1.0e-10);
    }

    @Test
    public void testFullCircle() {
        ArcsSet set = new ArcsSet(1.0e-10);
        Assert.assertEquals(1.0e-10, set.getTolerance(), 1.0e-20);
        Assert.assertEquals(Region.Location.INSIDE, set.checkPoint(new S1Point(9.0)));
        for (double alpha = -20.0; alpha <= 20.0; alpha += 0.1) {
            Assert.assertEquals(Region.Location.INSIDE, set.checkPoint(new S1Point(alpha)));
        }
        Assert.assertEquals(1, set.asList().size());
        Assert.assertEquals(0.0, set.asList().get(0).getInf(), 1.0e-10);
        Assert.assertEquals(2 * FastMath.PI, set.asList().get(0).getSup(), 1.0e-10);
        Assert.assertEquals(2 * FastMath.PI, set.getSize(), 1.0e-10);
    }

    @Test
    public void testEmpty() {
        ArcsSet empty = (ArcsSet) new RegionFactory<Sphere1D>().getComplement(new ArcsSet(1.0e-10));
        Assert.assertEquals(1.0e-10, empty.getTolerance(), 1.0e-20);
        Assert.assertEquals(0.0, empty.getSize(), 1.0e-10);
        Assert.assertTrue(empty.asList().isEmpty());
    }

    @Test
    public void testTiny() {
        ArcsSet tiny = new ArcsSet(0.0, Precision.SAFE_MIN / 2, 1.0e-10);
        Assert.assertEquals(1.0e-10, tiny.getTolerance(), 1.0e-20);
        Assert.assertEquals(Precision.SAFE_MIN / 2, tiny.getSize(), 1.0e-10);
        Assert.assertEquals(1, tiny.asList().size());
        Assert.assertEquals(0.0, tiny.asList().get(0).getInf(), 1.0e-10);
        Assert.assertEquals(Precision.SAFE_MIN / 2, tiny.asList().get(0).getSup(), 1.0e-10);
    }

    @Test
    public void testSpecialConstruction() {
        List<SubHyperplane<Sphere1D>> boundary = new ArrayList<SubHyperplane<Sphere1D>>();
        boundary.add(new LimitAngle(new S1Point(0.0), false, 1.0e-10).wholeHyperplane());
        boundary.add(new LimitAngle(new S1Point(MathUtils.TWO_PI - 1.0e-11), true, 1.0e-10).wholeHyperplane());
        ArcsSet set = new ArcsSet(boundary, 1.0e-10);
        Assert.assertEquals(MathUtils.TWO_PI, set.getSize(), 1.0e-10);
        Assert.assertEquals(1.0e-10, set.getTolerance(), 1.0e-20);
        Assert.assertEquals(1, set.asList().size());
        Assert.assertEquals(0.0, set.asList().get(0).getInf(), 1.0e-10);
        Assert.assertEquals(MathUtils.TWO_PI, set.asList().get(0).getSup(), 1.0e-10);
    }

    @Test
    public void testDifference() {

        ArcsSet a   = new ArcsSet(1.0, 6.0, 1.0e-10);
        List<Arc> aList = a.asList();
        Assert.assertEquals(1,   aList.size());
        Assert.assertEquals(1.0, aList.get(0).getInf(), 1.0e-10);
        Assert.assertEquals(6.0, aList.get(0).getSup(), 1.0e-10);

        ArcsSet b   = new ArcsSet(3.0, 5.0, 1.0e-10);
        List<Arc> bList = b.asList();
        Assert.assertEquals(1,   bList.size());
        Assert.assertEquals(3.0, bList.get(0).getInf(), 1.0e-10);
        Assert.assertEquals(5.0, bList.get(0).getSup(), 1.0e-10);

        ArcsSet aMb = (ArcsSet) new RegionFactory<Sphere1D>().difference(a, b);
        for (int k = -2; k < 3; ++k) {
            Assert.assertEquals(Location.OUTSIDE,  aMb.checkPoint(new S1Point(0.0 + k * MathUtils.TWO_PI)));
            Assert.assertEquals(Location.OUTSIDE,  aMb.checkPoint(new S1Point(0.9 + k * MathUtils.TWO_PI)));
            Assert.assertEquals(Location.BOUNDARY, aMb.checkPoint(new S1Point(1.0 + k * MathUtils.TWO_PI)));
            Assert.assertEquals(Location.INSIDE,   aMb.checkPoint(new S1Point(1.1 + k * MathUtils.TWO_PI)));
            Assert.assertEquals(Location.INSIDE,   aMb.checkPoint(new S1Point(2.9 + k * MathUtils.TWO_PI)));
            Assert.assertEquals(Location.BOUNDARY, aMb.checkPoint(new S1Point(3.0 + k * MathUtils.TWO_PI)));
            Assert.assertEquals(Location.OUTSIDE,  aMb.checkPoint(new S1Point(3.1 + k * MathUtils.TWO_PI)));
            Assert.assertEquals(Location.OUTSIDE,  aMb.checkPoint(new S1Point(4.9 + k * MathUtils.TWO_PI)));
            Assert.assertEquals(Location.BOUNDARY, aMb.checkPoint(new S1Point(5.0 + k * MathUtils.TWO_PI)));
            Assert.assertEquals(Location.INSIDE,   aMb.checkPoint(new S1Point(5.1 + k * MathUtils.TWO_PI)));
            Assert.assertEquals(Location.INSIDE,   aMb.checkPoint(new S1Point(5.9 + k * MathUtils.TWO_PI)));
            Assert.assertEquals(Location.BOUNDARY, aMb.checkPoint(new S1Point(6.0 + k * MathUtils.TWO_PI)));
            Assert.assertEquals(Location.OUTSIDE,  aMb.checkPoint(new S1Point(6.1 + k * MathUtils.TWO_PI)));
            Assert.assertEquals(Location.OUTSIDE,  aMb.checkPoint(new S1Point(6.2 + k * MathUtils.TWO_PI)));
        }

        List<Arc> aMbList = aMb.asList();
        Assert.assertEquals(2,   aMbList.size());
        Assert.assertEquals(1.0, aMbList.get(0).getInf(), 1.0e-10);
        Assert.assertEquals(3.0, aMbList.get(0).getSup(), 1.0e-10);
        Assert.assertEquals(5.0, aMbList.get(1).getInf(), 1.0e-10);
        Assert.assertEquals(6.0, aMbList.get(1).getSup(), 1.0e-10);


    }

    @Test
    public void testIntersection() {

        ArcsSet a   = (ArcsSet) new RegionFactory<Sphere1D>().union(new ArcsSet(1.0, 3.0, 1.0e-10),
                                                                    new ArcsSet(5.0, 6.0, 1.0e-10));
        List<Arc> aList = a.asList();
        Assert.assertEquals(2,   aList.size());
        Assert.assertEquals(1.0, aList.get(0).getInf(), 1.0e-10);
        Assert.assertEquals(3.0, aList.get(0).getSup(), 1.0e-10);
        Assert.assertEquals(5.0, aList.get(1).getInf(), 1.0e-10);
        Assert.assertEquals(6.0, aList.get(1).getSup(), 1.0e-10);

        ArcsSet b   = new ArcsSet(0.0, 5.5, 1.0e-10);
        List<Arc> bList = b.asList();
        Assert.assertEquals(1,   bList.size());
        Assert.assertEquals(0.0, bList.get(0).getInf(), 1.0e-10);
        Assert.assertEquals(5.5, bList.get(0).getSup(), 1.0e-10);

        ArcsSet aMb = (ArcsSet) new RegionFactory<Sphere1D>().intersection(a, b);
        for (int k = -2; k < 3; ++k) {
            Assert.assertEquals(Location.OUTSIDE,  aMb.checkPoint(new S1Point(0.0 + k * MathUtils.TWO_PI)));
            Assert.assertEquals(Location.BOUNDARY, aMb.checkPoint(new S1Point(1.0 + k * MathUtils.TWO_PI)));
            Assert.assertEquals(Location.INSIDE,   aMb.checkPoint(new S1Point(1.1 + k * MathUtils.TWO_PI)));
            Assert.assertEquals(Location.INSIDE,   aMb.checkPoint(new S1Point(2.9 + k * MathUtils.TWO_PI)));
            Assert.assertEquals(Location.BOUNDARY, aMb.checkPoint(new S1Point(3.0 + k * MathUtils.TWO_PI)));
            Assert.assertEquals(Location.OUTSIDE,  aMb.checkPoint(new S1Point(3.1 + k * MathUtils.TWO_PI)));
            Assert.assertEquals(Location.OUTSIDE,  aMb.checkPoint(new S1Point(4.9 + k * MathUtils.TWO_PI)));
            Assert.assertEquals(Location.BOUNDARY, aMb.checkPoint(new S1Point(5.0 + k * MathUtils.TWO_PI)));
            Assert.assertEquals(Location.INSIDE,   aMb.checkPoint(new S1Point(5.1 + k * MathUtils.TWO_PI)));
            Assert.assertEquals(Location.INSIDE,   aMb.checkPoint(new S1Point(5.4 + k * MathUtils.TWO_PI)));
            Assert.assertEquals(Location.BOUNDARY, aMb.checkPoint(new S1Point(5.5 + k * MathUtils.TWO_PI)));
            Assert.assertEquals(Location.OUTSIDE,  aMb.checkPoint(new S1Point(5.6 + k * MathUtils.TWO_PI)));
            Assert.assertEquals(Location.OUTSIDE,  aMb.checkPoint(new S1Point(6.2 + k * MathUtils.TWO_PI)));
        }

        List<Arc> aMbList = aMb.asList();
        Assert.assertEquals(2,   aMbList.size());
        Assert.assertEquals(1.0, aMbList.get(0).getInf(), 1.0e-10);
        Assert.assertEquals(3.0, aMbList.get(0).getSup(), 1.0e-10);
        Assert.assertEquals(5.0, aMbList.get(1).getInf(), 1.0e-10);
        Assert.assertEquals(5.5, aMbList.get(1).getSup(), 1.0e-10);


    }

    @Test
    public void testMultiple() {
        RegionFactory<Sphere1D> factory = new RegionFactory<Sphere1D>();
        ArcsSet set = (ArcsSet)
        factory.intersection(factory.union(factory.difference(new ArcsSet(1.0, 6.0, 1.0e-10),
                                                              new ArcsSet(3.0, 5.0, 1.0e-10)),
                                                              new ArcsSet(0.5, 2.0, 1.0e-10)),
                                                              new ArcsSet(0.0, 5.5, 1.0e-10));
        Assert.assertEquals(3.0, set.getSize(), 1.0e-10);
        Assert.assertEquals(Region.Location.OUTSIDE,  set.checkPoint(new S1Point(0.0)));
        Assert.assertEquals(Region.Location.OUTSIDE,  set.checkPoint(new S1Point(4.0)));
        Assert.assertEquals(Region.Location.OUTSIDE,  set.checkPoint(new S1Point(6.0)));
        Assert.assertEquals(Region.Location.INSIDE,   set.checkPoint(new S1Point(1.2)));
        Assert.assertEquals(Region.Location.INSIDE,   set.checkPoint(new S1Point(5.25)));
        Assert.assertEquals(Region.Location.BOUNDARY, set.checkPoint(new S1Point(0.5)));
        Assert.assertEquals(Region.Location.BOUNDARY, set.checkPoint(new S1Point(3.0)));
        Assert.assertEquals(Region.Location.BOUNDARY, set.checkPoint(new S1Point(5.0)));
        Assert.assertEquals(Region.Location.BOUNDARY, set.checkPoint(new S1Point(5.5)));

        List<Arc> list = set.asList();
        Assert.assertEquals(2, list.size());
        Assert.assertEquals( 0.5, list.get(0).getInf(), 1.0e-10);
        Assert.assertEquals( 3.0, list.get(0).getSup(), 1.0e-10);
        Assert.assertEquals( 5.0, list.get(1).getInf(), 1.0e-10);
        Assert.assertEquals( 5.5, list.get(1).getSup(), 1.0e-10);

    }

    @Test
    public void testSinglePoint() {
        ArcsSet set = new ArcsSet(1.0, FastMath.nextAfter(1.0, Double.POSITIVE_INFINITY), 1.0e-10);
        Assert.assertEquals(2 * Precision.EPSILON, set.getSize(), Precision.SAFE_MIN);
    }

    @Test
    public void testIteration() {
        ArcsSet set = (ArcsSet) new RegionFactory<Sphere1D>().difference(new ArcsSet(1.0, 6.0, 1.0e-10),
                                                                         new ArcsSet(3.0, 5.0, 1.0e-10));
        Iterator<double[]> iterator = set.iterator();
        try {
            iterator.remove();
            Assert.fail("an exception should have been thrown");
        } catch (UnsupportedOperationException uoe) {
            // expected
        }

        Assert.assertTrue(iterator.hasNext());
        double[] a0 = iterator.next();
        Assert.assertEquals(2, a0.length);
        Assert.assertEquals(1.0, a0[0], 1.0e-10);
        Assert.assertEquals(3.0, a0[1], 1.0e-10);

        Assert.assertTrue(iterator.hasNext());
        double[] a1 = iterator.next();
        Assert.assertEquals(2, a1.length);
        Assert.assertEquals(5.0, a1[0], 1.0e-10);
        Assert.assertEquals(6.0, a1[1], 1.0e-10);

        Assert.assertFalse(iterator.hasNext());
        try {
            iterator.next();
            Assert.fail("an exception should have been thrown");
        } catch (NoSuchElementException nsee) {
            // expected
        }

    }

    @Test
    public void testEmptyTree() {
        Assert.assertEquals(MathUtils.TWO_PI, new ArcsSet(new BSPTree<Sphere1D>(Boolean.TRUE), 1.0e-10).getSize(), 1.0e-10);
    }

    @Test
    public void testShiftedAngles() {
        for (int k = -2; k < 3; ++k) {
            SubLimitAngle l1  = new LimitAngle(new S1Point(1.0 + k * MathUtils.TWO_PI), false, 1.0e-10).wholeHyperplane();
            SubLimitAngle l2  = new LimitAngle(new S1Point(1.5 + k * MathUtils.TWO_PI), true,  1.0e-10).wholeHyperplane();
            ArcsSet set = new ArcsSet(new BSPTree<Sphere1D>(l1,
                                                            new BSPTree<Sphere1D>(Boolean.FALSE),
                                                            new BSPTree<Sphere1D>(l2,
                                                                                  new BSPTree<Sphere1D>(Boolean.FALSE),
                                                                                  new BSPTree<Sphere1D>(Boolean.TRUE),
                                                                                  null),
                                                            null),
                                      1.0e-10);
            for (double alpha = 1.0e-6; alpha < MathUtils.TWO_PI; alpha += 0.001) {
                if (alpha < 1 || alpha > 1.5) {
                    Assert.assertEquals(Location.OUTSIDE, set.checkPoint(new S1Point(alpha)));
                } else {
                    Assert.assertEquals(Location.INSIDE,  set.checkPoint(new S1Point(alpha)));
                }
            }
        }

    }

    @Test(expected=ArcsSet.InconsistentStateAt2PiWrapping.class)
    public void testInconsistentState() {
        SubLimitAngle l1 = new LimitAngle(new S1Point(1.0), false, 1.0e-10).wholeHyperplane();
        SubLimitAngle l2 = new LimitAngle(new S1Point(2.0), true,  1.0e-10).wholeHyperplane();
        SubLimitAngle l3 = new LimitAngle(new S1Point(3.0), false, 1.0e-10).wholeHyperplane();
        new ArcsSet(new BSPTree<Sphere1D>(l1,
                                          new BSPTree<Sphere1D>(Boolean.FALSE),
                                          new BSPTree<Sphere1D>(l2,
                                                                new BSPTree<Sphere1D>(l3,
                                                                                      new BSPTree<Sphere1D>(Boolean.FALSE),
                                                                                      new BSPTree<Sphere1D>(Boolean.TRUE),
                                                                                      null),
                                                                new BSPTree<Sphere1D>(Boolean.TRUE),
                                                                null),
                                          null),
                                          1.0e-10);
    }

    @Test
    public void testSide() {
        ArcsSet set = (ArcsSet) new RegionFactory<Sphere1D>().difference(new ArcsSet(1.0, 6.0, 1.0e-10),
                                                                         new ArcsSet(3.0, 5.0, 1.0e-10));
        for (int k = -2; k < 3; ++k) {
            Assert.assertEquals(Side.MINUS, set.split(new Arc(0.5 + k * MathUtils.TWO_PI,
                                                              6.1 + k * MathUtils.TWO_PI,
                                                              set.getTolerance())).getSide());
            Assert.assertEquals(Side.PLUS,  set.split(new Arc(0.5 + k * MathUtils.TWO_PI,
                                                              0.8 + k * MathUtils.TWO_PI,
                                                              set.getTolerance())).getSide());
            Assert.assertEquals(Side.PLUS,  set.split(new Arc(6.2 + k * MathUtils.TWO_PI,
                                                              6.3 + k * MathUtils.TWO_PI,
                                                              set.getTolerance())).getSide());
            Assert.assertEquals(Side.PLUS,  set.split(new Arc(3.5 + k * MathUtils.TWO_PI,
                                                              4.5 + k * MathUtils.TWO_PI,
                                                              set.getTolerance())).getSide());
            Assert.assertEquals(Side.BOTH,  set.split(new Arc(2.9 + k * MathUtils.TWO_PI,
                                                              4.5 + k * MathUtils.TWO_PI,
                                                              set.getTolerance())).getSide());
            Assert.assertEquals(Side.BOTH,  set.split(new Arc(0.5 + k * MathUtils.TWO_PI,
                                                              1.2 + k * MathUtils.TWO_PI,
                                                              set.getTolerance())).getSide());
            Assert.assertEquals(Side.BOTH,  set.split(new Arc(0.5 + k * MathUtils.TWO_PI,
                                                              5.9 + k * MathUtils.TWO_PI,
                                                              set.getTolerance())).getSide());
        }
    }

    @Test
    public void testSideEmbedded() {

        ArcsSet s35 = new ArcsSet(3.0, 5.0, 1.0e-10);
        ArcsSet s16 = new ArcsSet(1.0, 6.0, 1.0e-10);

        Assert.assertEquals(Side.BOTH,  s16.split(new Arc(3.0, 5.0, 1.0e-10)).getSide());
        Assert.assertEquals(Side.BOTH,  s16.split(new Arc(5.0, 3.0 + MathUtils.TWO_PI, 1.0e-10)).getSide());
        Assert.assertEquals(Side.MINUS, s35.split(new Arc(1.0, 6.0, 1.0e-10)).getSide());
        Assert.assertEquals(Side.PLUS,  s35.split(new Arc(6.0, 1.0 + MathUtils.TWO_PI, 1.0e-10)).getSide());

    }

    @Test
    public void testSideOverlapping() {
        ArcsSet s35 = new ArcsSet(3.0, 5.0, 1.0e-10);
        ArcsSet s46 = new ArcsSet(4.0, 6.0, 1.0e-10);

        Assert.assertEquals(Side.BOTH,  s46.split(new Arc(3.0, 5.0, 1.0e-10)).getSide());
        Assert.assertEquals(Side.BOTH,  s46.split(new Arc(5.0, 3.0 + MathUtils.TWO_PI, 1.0e-10)).getSide());
        Assert.assertEquals(Side.BOTH, s35.split(new Arc(4.0, 6.0, 1.0e-10)).getSide());
        Assert.assertEquals(Side.BOTH,  s35.split(new Arc(6.0, 4.0 + MathUtils.TWO_PI, 1.0e-10)).getSide());
    }

    @Test
    public void testSideHyper() {
        ArcsSet sub = (ArcsSet) new RegionFactory<Sphere1D>().getComplement(new ArcsSet(1.0e-10));
        Assert.assertTrue(sub.isEmpty());
        Assert.assertEquals(Side.HYPER,  sub.split(new Arc(2.0, 3.0, 1.0e-10)).getSide());
    }

    @Test
    public void testSplitEmbedded() {

        ArcsSet s35 = new ArcsSet(3.0, 5.0, 1.0e-10);
        ArcsSet s16 = new ArcsSet(1.0, 6.0, 1.0e-10);

        ArcsSet.Split split1 = s16.split(new Arc(3.0, 5.0, 1.0e-10));
        ArcsSet split1Plus  = split1.getPlus();
        ArcsSet split1Minus = split1.getMinus();
        Assert.assertEquals(3.0, split1Plus.getSize(), 1.0e-10);
        Assert.assertEquals(2,   split1Plus.asList().size());
        Assert.assertEquals(1.0, split1Plus.asList().get(0).getInf(), 1.0e-10);
        Assert.assertEquals(3.0, split1Plus.asList().get(0).getSup(), 1.0e-10);
        Assert.assertEquals(5.0, split1Plus.asList().get(1).getInf(), 1.0e-10);
        Assert.assertEquals(6.0, split1Plus.asList().get(1).getSup(), 1.0e-10);
        Assert.assertEquals(2.0, split1Minus.getSize(), 1.0e-10);
        Assert.assertEquals(1,   split1Minus.asList().size());
        Assert.assertEquals(3.0, split1Minus.asList().get(0).getInf(), 1.0e-10);
        Assert.assertEquals(5.0, split1Minus.asList().get(0).getSup(), 1.0e-10);

        ArcsSet.Split split2 = s16.split(new Arc(5.0, 3.0 + MathUtils.TWO_PI, 1.0e-10));
        ArcsSet split2Plus  = split2.getPlus();
        ArcsSet split2Minus = split2.getMinus();
        Assert.assertEquals(2.0, split2Plus.getSize(), 1.0e-10);
        Assert.assertEquals(1,   split2Plus.asList().size());
        Assert.assertEquals(3.0, split2Plus.asList().get(0).getInf(), 1.0e-10);
        Assert.assertEquals(5.0, split2Plus.asList().get(0).getSup(), 1.0e-10);
        Assert.assertEquals(3.0, split2Minus.getSize(), 1.0e-10);
        Assert.assertEquals(2,   split2Minus.asList().size());
        Assert.assertEquals(1.0, split2Minus.asList().get(0).getInf(), 1.0e-10);
        Assert.assertEquals(3.0, split2Minus.asList().get(0).getSup(), 1.0e-10);
        Assert.assertEquals(5.0, split2Minus.asList().get(1).getInf(), 1.0e-10);
        Assert.assertEquals(6.0, split2Minus.asList().get(1).getSup(), 1.0e-10);

        ArcsSet.Split split3 = s35.split(new Arc(1.0, 6.0, 1.0e-10));
        ArcsSet split3Plus  = split3.getPlus();
        ArcsSet split3Minus = split3.getMinus();
        Assert.assertNull(split3Plus);
        Assert.assertEquals(2.0, split3Minus.getSize(), 1.0e-10);
        Assert.assertEquals(1,   split3Minus.asList().size());
        Assert.assertEquals(3.0, split3Minus.asList().get(0).getInf(), 1.0e-10);
        Assert.assertEquals(5.0, split3Minus.asList().get(0).getSup(), 1.0e-10);

        ArcsSet.Split split4 = s35.split(new Arc(6.0, 1.0 + MathUtils.TWO_PI, 1.0e-10));
        ArcsSet split4Plus  = split4.getPlus();
        ArcsSet split4Minus = split4.getMinus();
        Assert.assertEquals(2.0, split4Plus.getSize(), 1.0e-10);
        Assert.assertEquals(1,   split4Plus.asList().size());
        Assert.assertEquals(3.0, split4Plus.asList().get(0).getInf(), 1.0e-10);
        Assert.assertEquals(5.0, split4Plus.asList().get(0).getSup(), 1.0e-10);
        Assert.assertNull(split4Minus);

    }

    @Test
    public void testSplitOverlapping() {

        ArcsSet s35 = new ArcsSet(3.0, 5.0, 1.0e-10);
        ArcsSet s46 = new ArcsSet(4.0, 6.0, 1.0e-10);

        ArcsSet.Split split1 = s46.split(new Arc(3.0, 5.0, 1.0e-10));
        ArcsSet split1Plus  = split1.getPlus();
        ArcsSet split1Minus = split1.getMinus();
        Assert.assertEquals(1.0, split1Plus.getSize(), 1.0e-10);
        Assert.assertEquals(1,   split1Plus.asList().size());
        Assert.assertEquals(5.0, split1Plus.asList().get(0).getInf(), 1.0e-10);
        Assert.assertEquals(6.0, split1Plus.asList().get(0).getSup(), 1.0e-10);
        Assert.assertEquals(1.0, split1Minus.getSize(), 1.0e-10);
        Assert.assertEquals(1,   split1Minus.asList().size());
        Assert.assertEquals(4.0, split1Minus.asList().get(0).getInf(), 1.0e-10);
        Assert.assertEquals(5.0, split1Minus.asList().get(0).getSup(), 1.0e-10);

        ArcsSet.Split split2 = s46.split(new Arc(5.0, 3.0 + MathUtils.TWO_PI, 1.0e-10));
        ArcsSet split2Plus  = split2.getPlus();
        ArcsSet split2Minus = split2.getMinus();
        Assert.assertEquals(1.0, split2Plus.getSize(), 1.0e-10);
        Assert.assertEquals(1,   split2Plus.asList().size());
        Assert.assertEquals(4.0, split2Plus.asList().get(0).getInf(), 1.0e-10);
        Assert.assertEquals(5.0, split2Plus.asList().get(0).getSup(), 1.0e-10);
        Assert.assertEquals(1.0, split2Minus.getSize(), 1.0e-10);
        Assert.assertEquals(1,   split2Minus.asList().size());
        Assert.assertEquals(5.0, split2Minus.asList().get(0).getInf(), 1.0e-10);
        Assert.assertEquals(6.0, split2Minus.asList().get(0).getSup(), 1.0e-10);

        ArcsSet.Split split3 = s35.split(new Arc(4.0, 6.0, 1.0e-10));
        ArcsSet split3Plus  = split3.getPlus();
        ArcsSet split3Minus = split3.getMinus();
        Assert.assertEquals(1.0, split3Plus.getSize(), 1.0e-10);
        Assert.assertEquals(1,   split3Plus.asList().size());
        Assert.assertEquals(3.0, split3Plus.asList().get(0).getInf(), 1.0e-10);
        Assert.assertEquals(4.0, split3Plus.asList().get(0).getSup(), 1.0e-10);
        Assert.assertEquals(1.0, split3Minus.getSize(), 1.0e-10);
        Assert.assertEquals(1,   split3Minus.asList().size());
        Assert.assertEquals(4.0, split3Minus.asList().get(0).getInf(), 1.0e-10);
        Assert.assertEquals(5.0, split3Minus.asList().get(0).getSup(), 1.0e-10);

        ArcsSet.Split split4 = s35.split(new Arc(6.0, 4.0 + MathUtils.TWO_PI, 1.0e-10));
        ArcsSet split4Plus  = split4.getPlus();
        ArcsSet split4Minus = split4.getMinus();
        Assert.assertEquals(1.0, split4Plus.getSize(), 1.0e-10);
        Assert.assertEquals(1,   split4Plus.asList().size());
        Assert.assertEquals(4.0, split4Plus.asList().get(0).getInf(), 1.0e-10);
        Assert.assertEquals(5.0, split4Plus.asList().get(0).getSup(), 1.0e-10);
        Assert.assertEquals(1.0, split4Minus.getSize(), 1.0e-10);
        Assert.assertEquals(1,   split4Minus.asList().size());
        Assert.assertEquals(3.0, split4Minus.asList().get(0).getInf(), 1.0e-10);
        Assert.assertEquals(4.0, split4Minus.asList().get(0).getSup(), 1.0e-10);

    }

    @Test
    public void testFarSplit() {
        ArcsSet set = new ArcsSet(FastMath.PI, 2.5 * FastMath.PI, 1.0e-10);
        ArcsSet.Split split = set.split(new Arc(0.5 * FastMath.PI, 1.5 * FastMath.PI, 1.0e-10));
        ArcsSet splitPlus  = split.getPlus();
        ArcsSet splitMinus = split.getMinus();
        Assert.assertEquals(1,   splitMinus.asList().size());
        Assert.assertEquals(1.0 * FastMath.PI, splitMinus.asList().get(0).getInf(), 1.0e-10);
        Assert.assertEquals(1.5 * FastMath.PI, splitMinus.asList().get(0).getSup(), 1.0e-10);
        Assert.assertEquals(0.5 * FastMath.PI, splitMinus.getSize(), 1.0e-10);
        Assert.assertEquals(1,   splitPlus.asList().size());
        Assert.assertEquals(1.5 * FastMath.PI, splitPlus.asList().get(0).getInf(), 1.0e-10);
        Assert.assertEquals(2.5 * FastMath.PI, splitPlus.asList().get(0).getSup(), 1.0e-10);
        Assert.assertEquals(1.0 * FastMath.PI, splitPlus.getSize(), 1.0e-10);

    }

    @Test
    public void testSplitWithinEpsilon() {
        double epsilon = 1.0e-10;
        double a = 6.25;
        double b = a - 0.5 * epsilon;
        ArcsSet set = new ArcsSet(a - 1, a, epsilon);
        Arc arc = new Arc(b, b + FastMath.PI, epsilon);
        ArcsSet.Split split = set.split(arc);
        Assert.assertEquals(set.getSize(), split.getPlus().getSize(),  epsilon);
        Assert.assertNull(split.getMinus());
    }

    @Test
    public void testSideSplitConsistency() {
        double  epsilon = 1.0e-6;
        double  a       = 4.725;
        ArcsSet set     = new ArcsSet(a, a + 0.5, epsilon);
        Arc     arc     = new Arc(a + 0.5 * epsilon, a + 1, epsilon);
        ArcsSet.Split split = set.split(arc);
        Assert.assertNotNull(split.getMinus());
        Assert.assertNull(split.getPlus());
        Assert.assertEquals(Side.MINUS, set.split(arc).getSide());
    }

}
