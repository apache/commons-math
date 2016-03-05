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
package org.apache.commons.math4.geometry.euclidean.threed;

import org.apache.commons.math4.exception.MathArithmeticException;
import org.apache.commons.math4.exception.MathIllegalArgumentException;
import org.apache.commons.math4.geometry.euclidean.threed.Line;
import org.apache.commons.math4.geometry.euclidean.threed.Plane;
import org.apache.commons.math4.geometry.euclidean.threed.Rotation;
import org.apache.commons.math4.geometry.euclidean.threed.Vector3D;
import org.junit.Assert;
import org.junit.Test;

public class PlaneTest {

    @Test
    public void testContains() throws MathArithmeticException {
        Plane p = new Plane(new Vector3D(0, 0, 1), new Vector3D(0, 0, 1), 1.0e-10);
        Assert.assertTrue(p.contains(new Vector3D(0, 0, 1)));
        Assert.assertTrue(p.contains(new Vector3D(17, -32, 1)));
        Assert.assertTrue(! p.contains(new Vector3D(17, -32, 1.001)));
    }

    @Test
    public void testOffset() throws MathArithmeticException {
        Vector3D p1 = new Vector3D(1, 1, 1);
        Plane p = new Plane(p1, new Vector3D(0.2, 0, 0), 1.0e-10);
        Assert.assertEquals(-5.0, p.getOffset(new Vector3D(-4, 0, 0)), 1.0e-10);
        Assert.assertEquals(+5.0, p.getOffset(new Vector3D(6, 10, -12)), 1.0e-10);
        Assert.assertEquals(0.3,
                            p.getOffset(new Vector3D(1.0, p1, 0.3, p.getNormal())),
                            1.0e-10);
        Assert.assertEquals(-0.3,
                            p.getOffset(new Vector3D(1.0, p1, -0.3, p.getNormal())),
                            1.0e-10);
    }

    @Test
    public void testPoint() throws MathArithmeticException {
        Plane p = new Plane(new Vector3D(2, -3, 1), new Vector3D(1, 4, 9), 1.0e-10);
        Assert.assertTrue(p.contains(p.getOrigin()));
    }

    @Test
    public void testThreePoints() throws MathArithmeticException {
        Vector3D p1 = new Vector3D(1.2, 3.4, -5.8);
        Vector3D p2 = new Vector3D(3.4, -5.8, 1.2);
        Vector3D p3 = new Vector3D(-2.0, 4.3, 0.7);
        Plane    p  = new Plane(p1, p2, p3, 1.0e-10);
        Assert.assertTrue(p.contains(p1));
        Assert.assertTrue(p.contains(p2));
        Assert.assertTrue(p.contains(p3));
    }

    @Test
    public void testRotate() throws MathArithmeticException, MathIllegalArgumentException {
        Vector3D p1 = new Vector3D(1.2, 3.4, -5.8);
        Vector3D p2 = new Vector3D(3.4, -5.8, 1.2);
        Vector3D p3 = new Vector3D(-2.0, 4.3, 0.7);
        Plane    p  = new Plane(p1, p2, p3, 1.0e-10);
        Vector3D oldNormal = p.getNormal();

        p = p.rotate(p2, new Rotation(p2.subtract(p1), 1.7));
        Assert.assertTrue(p.contains(p1));
        Assert.assertTrue(p.contains(p2));
        Assert.assertTrue(! p.contains(p3));

        p = p.rotate(p2, new Rotation(oldNormal, 0.1));
        Assert.assertTrue(! p.contains(p1));
        Assert.assertTrue(p.contains(p2));
        Assert.assertTrue(! p.contains(p3));

        p = p.rotate(p1, new Rotation(oldNormal, 0.1));
        Assert.assertTrue(! p.contains(p1));
        Assert.assertTrue(! p.contains(p2));
        Assert.assertTrue(! p.contains(p3));

    }

    @Test
    public void testTranslate() throws MathArithmeticException {
        Vector3D p1 = new Vector3D(1.2, 3.4, -5.8);
        Vector3D p2 = new Vector3D(3.4, -5.8, 1.2);
        Vector3D p3 = new Vector3D(-2.0, 4.3, 0.7);
        Plane    p  = new Plane(p1, p2, p3, 1.0e-10);

        p = p.translate(new Vector3D(2.0, p.getU(), -1.5, p.getV()));
        Assert.assertTrue(p.contains(p1));
        Assert.assertTrue(p.contains(p2));
        Assert.assertTrue(p.contains(p3));

        p = p.translate(new Vector3D(-1.2, p.getNormal()));
        Assert.assertTrue(! p.contains(p1));
        Assert.assertTrue(! p.contains(p2));
        Assert.assertTrue(! p.contains(p3));

        p = p.translate(new Vector3D(+1.2, p.getNormal()));
        Assert.assertTrue(p.contains(p1));
        Assert.assertTrue(p.contains(p2));
        Assert.assertTrue(p.contains(p3));

    }

    @Test
    public void testIntersection() throws MathArithmeticException, MathIllegalArgumentException {
        Plane p = new Plane(new Vector3D(1, 2, 3), new Vector3D(-4, 1, -5), 1.0e-10);
        Line  l = new Line(new Vector3D(0.2, -3.5, 0.7), new Vector3D(1.2, -2.5, -0.3), 1.0e-10);
        Vector3D point = p.intersection(l);
        Assert.assertTrue(p.contains(point));
        Assert.assertTrue(l.contains(point));
        Assert.assertNull(p.intersection(new Line(new Vector3D(10, 10, 10),
                                                  new Vector3D(10, 10, 10).add(p.getNormal().orthogonal()),
                                                  1.0e-10)));
    }

    @Test
    public void testIntersection2() throws MathArithmeticException {
        Vector3D p1  = new Vector3D (1.2, 3.4, -5.8);
        Vector3D p2  = new Vector3D (3.4, -5.8, 1.2);
        Plane    pA  = new Plane(p1, p2, new Vector3D (-2.0, 4.3, 0.7), 1.0e-10);
        Plane    pB  = new Plane(p1, new Vector3D (11.4, -3.8, 5.1), p2, 1.0e-10);
        Line     l   = pA.intersection(pB);
        Assert.assertTrue(l.contains(p1));
        Assert.assertTrue(l.contains(p2));
        Assert.assertNull(pA.intersection(pA));
    }

    @Test
    public void testIntersection3() throws MathArithmeticException {
        Vector3D reference = new Vector3D (1.2, 3.4, -5.8);
        Plane p1 = new Plane(reference, new Vector3D(1, 3, 3), 1.0e-10);
        Plane p2 = new Plane(reference, new Vector3D(-2, 4, 0), 1.0e-10);
        Plane p3 = new Plane(reference, new Vector3D(7, 0, -4), 1.0e-10);
        Vector3D p = Plane.intersection(p1, p2, p3);
        Assert.assertEquals(reference.getX(), p.getX(), 1.0e-10);
        Assert.assertEquals(reference.getY(), p.getY(), 1.0e-10);
        Assert.assertEquals(reference.getZ(), p.getZ(), 1.0e-10);
    }

    @Test
    public void testSimilar() throws MathArithmeticException {
        Vector3D p1  = new Vector3D (1.2, 3.4, -5.8);
        Vector3D p2  = new Vector3D (3.4, -5.8, 1.2);
        Vector3D p3  = new Vector3D (-2.0, 4.3, 0.7);
        Plane    pA  = new Plane(p1, p2, p3, 1.0e-10);
        Plane    pB  = new Plane(p1, new Vector3D (11.4, -3.8, 5.1), p2, 1.0e-10);
        Assert.assertTrue(! pA.isSimilarTo(pB));
        Assert.assertTrue(pA.isSimilarTo(pA));
        Assert.assertTrue(pA.isSimilarTo(new Plane(p1, p3, p2, 1.0e-10)));
        Vector3D shift = new Vector3D(0.3, pA.getNormal());
        Assert.assertTrue(! pA.isSimilarTo(new Plane(p1.add(shift),
                                                     p3.add(shift),
                                                     p2.add(shift),
                                                     1.0e-10)));
    }

}
