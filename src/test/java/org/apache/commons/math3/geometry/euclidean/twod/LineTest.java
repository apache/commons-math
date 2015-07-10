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
package org.apache.commons.math3.geometry.euclidean.twod;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math3.geometry.euclidean.oned.Vector1D;
import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.partitioning.Transform;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

public class LineTest {

    @Test
    public void testContains() {
        Line l = new Line(new Vector2D(0, 1), new Vector2D(1, 2), 1.0e-10);
        Assert.assertTrue(l.contains(new Vector2D(0, 1)));
        Assert.assertTrue(l.contains(new Vector2D(1, 2)));
        Assert.assertTrue(l.contains(new Vector2D(7, 8)));
        Assert.assertTrue(! l.contains(new Vector2D(8, 7)));
    }

    @Test
    public void testAbscissa() {
        Line l = new Line(new Vector2D(2, 1), new Vector2D(-2, -2), 1.0e-10);
        Assert.assertEquals(0.0,
                            (l.toSubSpace(new Vector2D(-3,  4))).getX(),
                            1.0e-10);
        Assert.assertEquals(0.0,
                            (l.toSubSpace(new Vector2D( 3, -4))).getX(),
                            1.0e-10);
        Assert.assertEquals(-5.0,
                            (l.toSubSpace(new Vector2D( 7, -1))).getX(),
                            1.0e-10);
        Assert.assertEquals( 5.0,
                             (l.toSubSpace(new Vector2D(-1, -7))).getX(),
                             1.0e-10);
    }

    @Test
    public void testOffset() {
        Line l = new Line(new Vector2D(2, 1), new Vector2D(-2, -2), 1.0e-10);
        Assert.assertEquals(-5.0, l.getOffset(new Vector2D(5, -3)), 1.0e-10);
        Assert.assertEquals(+5.0, l.getOffset(new Vector2D(-5, 2)), 1.0e-10);
    }

    @Test
    public void testDistance() {
        Line l = new Line(new Vector2D(2, 1), new Vector2D(-2, -2), 1.0e-10);
        Assert.assertEquals(+5.0, l.distance(new Vector2D(5, -3)), 1.0e-10);
        Assert.assertEquals(+5.0, l.distance(new Vector2D(-5, 2)), 1.0e-10);
    }

    @Test
    public void testPointAt() {
        Line l = new Line(new Vector2D(2, 1), new Vector2D(-2, -2), 1.0e-10);
        for (double a = -2.0; a < 2.0; a += 0.2) {
            Point<Euclidean1D> pA = new Vector1D(a);
            Point<Euclidean2D> point = l.toSpace(pA);
            Assert.assertEquals(a, (l.toSubSpace(point)).getX(), 1.0e-10);
            Assert.assertEquals(0.0, l.getOffset(point),   1.0e-10);
            for (double o = -2.0; o < 2.0; o += 0.2) {
                point = l.getPointAt((Vector1D) pA, o);
                Assert.assertEquals(a, (l.toSubSpace(point)).getX(), 1.0e-10);
                Assert.assertEquals(o, l.getOffset(point),   1.0e-10);
            }
        }
    }

    @Test
    public void testOriginOffset() {
        Line l1 = new Line(new Vector2D(0, 1), new Vector2D(1, 2), 1.0e-10);
        Assert.assertEquals(FastMath.sqrt(0.5), l1.getOriginOffset(), 1.0e-10);
        Line l2 = new Line(new Vector2D(1, 2), new Vector2D(0, 1), 1.0e-10);
        Assert.assertEquals(-FastMath.sqrt(0.5), l2.getOriginOffset(), 1.0e-10);
    }

    @Test
    public void testParallel() {
        Line l1 = new Line(new Vector2D(0, 1), new Vector2D(1, 2), 1.0e-10);
        Line l2 = new Line(new Vector2D(2, 2), new Vector2D(3, 3), 1.0e-10);
        Assert.assertTrue(l1.isParallelTo(l2));
        Line l3 = new Line(new Vector2D(1, 0), new Vector2D(0.5, -0.5), 1.0e-10);
        Assert.assertTrue(l1.isParallelTo(l3));
        Line l4 = new Line(new Vector2D(1, 0), new Vector2D(0.5, -0.51), 1.0e-10);
        Assert.assertTrue(! l1.isParallelTo(l4));
    }

    @Test
    public void testTransform() throws MathIllegalArgumentException {

        Line l1 = new Line(new Vector2D(1.0 ,1.0), new Vector2D(4.0 ,1.0), 1.0e-10);
        Transform<Euclidean2D, Euclidean1D> t1 =
            Line.getTransform(0.0, 0.5, -1.0, 0.0, 1.0, 1.5);
        Assert.assertEquals(0.5 * FastMath.PI,
                            ((Line) t1.apply(l1)).getAngle(),
                            1.0e-10);

        Line l2 = new Line(new Vector2D(0.0, 0.0), new Vector2D(1.0, 1.0), 1.0e-10);
        Transform<Euclidean2D, Euclidean1D> t2 =
            Line.getTransform(0.0, 0.5, -1.0, 0.0, 1.0, 1.5);
        Assert.assertEquals(FastMath.atan2(1.0, -2.0),
                            ((Line) t2.apply(l2)).getAngle(),
                            1.0e-10);

    }

    @Test
    public void testIntersection() {
        Line    l1 = new Line(new Vector2D( 0, 1), new Vector2D(1, 2), 1.0e-10);
        Line    l2 = new Line(new Vector2D(-1, 2), new Vector2D(2, 1), 1.0e-10);
        Vector2D p  = l1.intersection(l2);
        Assert.assertEquals(0.5, p.getX(), 1.0e-10);
        Assert.assertEquals(1.5, p.getY(), 1.0e-10);
    }

}
