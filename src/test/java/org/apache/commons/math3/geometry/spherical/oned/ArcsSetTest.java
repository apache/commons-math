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
package org.apache.commons.math3.geometry.spherical.oned;

import java.util.List;

import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.Region.Location;
import org.apache.commons.math3.geometry.partitioning.RegionFactory;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.Precision;
import org.junit.Assert;
import org.junit.Test;

public class ArcsSetTest {

    @Test
    public void testArc() {
        ArcsSet set = new ArcsSet(2.3, 5.7, 1.0e-10);
        Assert.assertEquals(3.4, set.getSize(), 1.0e-10);
        Assert.assertEquals(4.0, ((S1Point) set.getBarycenter()).getAlpha(), 1.0e-10);
        Assert.assertEquals(Region.Location.BOUNDARY, set.checkPoint(new S1Point(2.3)));
        Assert.assertEquals(Region.Location.BOUNDARY, set.checkPoint(new S1Point(5.7)));
        Assert.assertEquals(Region.Location.OUTSIDE,  set.checkPoint(new S1Point(1.2)));
        Assert.assertEquals(Region.Location.OUTSIDE,  set.checkPoint(new S1Point(8.5)));
        Assert.assertEquals(Region.Location.INSIDE,   set.checkPoint(new S1Point(8.7)));
        Assert.assertEquals(Region.Location.INSIDE,   set.checkPoint(new S1Point(3.0)));
        Assert.assertEquals(1, set.asList().size());
        Assert.assertEquals(2.3, set.asList().get(0).getInf(), 1.0e-10);
        Assert.assertEquals(5.7, set.asList().get(0).getSup(), 1.0e-10);
        Assert.assertEquals(4.0, ((S1Point) set.getBarycenter()).getAlpha(), 1.0e-10);
        Assert.assertEquals(3.4, set.getSize(), 1.0e-10);
    }

    @Test
    public void testFullCircle() {
        ArcsSet set = new ArcsSet(9.0, 9.0, 1.0e-10);
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
        Assert.assertEquals(1,   aMbList.size());
        Assert.assertEquals(1.0, aMbList.get(0).getInf(), 1.0e-10);
        Assert.assertEquals(3.0, aMbList.get(0).getSup(), 1.0e-10);


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
        Assert.assertEquals(7.0 / 3.0, ((S1Point) set.getBarycenter()).getAlpha(), 1.0e-10);
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
        Assert.assertEquals(1.0, ((S1Point) set.getBarycenter()).getAlpha(), Precision.EPSILON);
    }

}
