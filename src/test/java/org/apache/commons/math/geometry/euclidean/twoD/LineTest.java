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
package org.apache.commons.math.geometry.euclidean.twoD;

import org.apache.commons.math.geometry.euclidean.oneD.Point1D;
import org.apache.commons.math.geometry.euclidean.twoD.Line;
import org.apache.commons.math.geometry.euclidean.twoD.Point2D;
import org.apache.commons.math.geometry.partitioning.Transform;
import org.apache.commons.math.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

import java.awt.geom.AffineTransform;

public class LineTest {

    @Test
    public void testContains() {
        Line l = new Line(new Point2D(0, 1), new Point2D(1, 2));
        Assert.assertTrue(l.contains(new Point2D(0, 1)));
        Assert.assertTrue(l.contains(new Point2D(1, 2)));
        Assert.assertTrue(l.contains(new Point2D(7, 8)));
        Assert.assertTrue(! l.contains(new Point2D(8, 7)));
    }

    @Test
    public void testAbscissa() {
        Line l = new Line(new Point2D(2, 1), new Point2D(-2, -2));
        Assert.assertEquals(0.0,
                            ((Point1D) l.toSubSpace(new Point2D(-3,  4))).getAbscissa(),
                            1.0e-10);
        Assert.assertEquals(0.0,
                            ((Point1D) l.toSubSpace(new Point2D( 3, -4))).getAbscissa(),
                            1.0e-10);
        Assert.assertEquals(-5.0,
                            ((Point1D) l.toSubSpace(new Point2D( 7, -1))).getAbscissa(),
                            1.0e-10);
        Assert.assertEquals( 5.0,
                             ((Point1D) l.toSubSpace(new Point2D(-1, -7))).getAbscissa(),
                             1.0e-10);
    }

    @Test
    public void testOffset() {
        Line l = new Line(new Point2D(2, 1), new Point2D(-2, -2));
        Assert.assertEquals(-5.0, l.getOffset(new Point2D(5, -3)), 1.0e-10);
        Assert.assertEquals(+5.0, l.getOffset(new Point2D(-5, 2)), 1.0e-10);
    }

    @Test
    public void testPointAt() {
        Line l = new Line(new Point2D(2, 1), new Point2D(-2, -2));
        for (double a = -2.0; a < 2.0; a += 0.2) {
            Point1D pA = new Point1D(a);
            Point2D point = (Point2D) l.toSpace(pA);
            Assert.assertEquals(a, ((Point1D) l.toSubSpace(point)).getAbscissa(), 1.0e-10);
            Assert.assertEquals(0.0, l.getOffset(point),   1.0e-10);
            for (double o = -2.0; o < 2.0; o += 0.2) {
                point = l.getPointAt(pA, o);
                Assert.assertEquals(a, ((Point1D) l.toSubSpace(point)).getAbscissa(), 1.0e-10);
                Assert.assertEquals(o, l.getOffset(point),   1.0e-10);
            }
        }
    }

    @Test
    public void testOriginOffset() {
        Line l1 = new Line(new Point2D(0, 1), new Point2D(1, 2));
        Assert.assertEquals(FastMath.sqrt(0.5), l1.getOriginOffset(), 1.0e-10);
        Line l2 = new Line(new Point2D(1, 2), new Point2D(0, 1));
        Assert.assertEquals(-FastMath.sqrt(0.5), l2.getOriginOffset(), 1.0e-10);
    }

    @Test
    public void testParallel() {
        Line l1 = new Line(new Point2D(0, 1), new Point2D(1, 2));
        Line l2 = new Line(new Point2D(2, 2), new Point2D(3, 3));
        Assert.assertTrue(l1.isParallelTo(l2));
        Line l3 = new Line(new Point2D(1, 0), new Point2D(0.5, -0.5));
        Assert.assertTrue(l1.isParallelTo(l3));
        Line l4 = new Line(new Point2D(1, 0), new Point2D(0.5, -0.51));
        Assert.assertTrue(! l1.isParallelTo(l4));
    }

    @Test
    public void testTransform() {

        Line l1 = new Line(new Point2D(1.0 ,1.0), new Point2D(4.0 ,1.0));
        Transform t1 = Line.getTransform(new AffineTransform(0.0, 0.5,
                                                             -1.0, 0.0,
                                                             1.0, 1.5));
        Assert.assertEquals(0.5 * FastMath.PI,
                            ((Line) t1.apply(l1)).getAngle(),
                            1.0e-10);

        Line l2 = new Line(new Point2D(0.0, 0.0), new Point2D(1.0, 1.0));
        Transform t2 = Line.getTransform(new AffineTransform(0.0, 0.5,
                                                             -1.0, 0.0,
                                                             1.0, 1.5));
        Assert.assertEquals(FastMath.atan2(1.0, -2.0),
                            ((Line) t2.apply(l2)).getAngle(),
                            1.0e-10);

    }

    @Test
    public void testIntersection() {
        Line    l1 = new Line(new Point2D( 0, 1), new Point2D(1, 2));
        Line    l2 = new Line(new Point2D(-1, 2), new Point2D(2, 1));
        Point2D p  = (Point2D) l1.intersection(l2);
        Assert.assertEquals(0.5, p.x, 1.0e-10);
        Assert.assertEquals(1.5, p.y, 1.0e-10);
    }

}
