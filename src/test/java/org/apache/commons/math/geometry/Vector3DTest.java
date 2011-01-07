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

package org.apache.commons.math.geometry;

import org.apache.commons.math.geometry.Vector3D;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.exception.MathArithmeticException;

import org.junit.Assert;
import org.junit.Test;

public class Vector3DTest {
    @Test
    public void testConstructors() {
        double r = FastMath.sqrt(2) /2;
        checkVector(new Vector3D(2, new Vector3D(FastMath.PI / 3, -FastMath.PI / 4)),
                    r, r * FastMath.sqrt(3), -2 * r);
        checkVector(new Vector3D(2, Vector3D.PLUS_I,
                                 -3, Vector3D.MINUS_K),
                    2, 0, 3);
        checkVector(new Vector3D(2, Vector3D.PLUS_I,
                                 5, Vector3D.PLUS_J,
                                 -3, Vector3D.MINUS_K),
                    2, 5, 3);
        checkVector(new Vector3D(2, Vector3D.PLUS_I,
                                 5, Vector3D.PLUS_J,
                                 5, Vector3D.MINUS_J,
                                 -3, Vector3D.MINUS_K),
                    2, 0, 3);
    }

    @Test
    public void testCoordinates() {
        Vector3D v = new Vector3D(1, 2, 3);
        Assert.assertTrue(FastMath.abs(v.getX() - 1) < 1.0e-12);
        Assert.assertTrue(FastMath.abs(v.getY() - 2) < 1.0e-12);
        Assert.assertTrue(FastMath.abs(v.getZ() - 3) < 1.0e-12);
    }

    @Test
    public void testNorm1() {
        Assert.assertEquals(0.0, Vector3D.ZERO.getNorm1(), 0);
        Assert.assertEquals(6.0, new Vector3D(1, -2, 3).getNorm1(), 0);
    }

    @Test
    public void testNorm() {
        Assert.assertEquals(0.0, Vector3D.ZERO.getNorm(), 0);
        Assert.assertEquals(FastMath.sqrt(14), new Vector3D(1, 2, 3).getNorm(), 1.0e-12);
    }

    @Test
    public void testNormInf() {
        Assert.assertEquals(0.0, Vector3D.ZERO.getNormInf(), 0);
        Assert.assertEquals(3.0, new Vector3D(1, -2, 3).getNormInf(), 0);
    }

    @Test
    public void testDistance1() {
        Vector3D v1 = new Vector3D(1, -2, 3);
        Vector3D v2 = new Vector3D(-4, 2, 0);
        Assert.assertEquals(0.0, Vector3D.distance1(Vector3D.MINUS_I, Vector3D.MINUS_I), 0);
        Assert.assertEquals(12.0, Vector3D.distance1(v1, v2), 1.0e-12);
        Assert.assertEquals(v1.subtract(v2).getNorm1(), Vector3D.distance1(v1, v2), 1.0e-12);
    }

    @Test
    public void testDistance() {
        Vector3D v1 = new Vector3D(1, -2, 3);
        Vector3D v2 = new Vector3D(-4, 2, 0);
        Assert.assertEquals(0.0, Vector3D.distance(Vector3D.MINUS_I, Vector3D.MINUS_I), 0);
        Assert.assertEquals(FastMath.sqrt(50), Vector3D.distance(v1, v2), 1.0e-12);
        Assert.assertEquals(v1.subtract(v2).getNorm(), Vector3D.distance(v1, v2), 1.0e-12);
    }

    @Test
    public void testDistanceSq() {
        Vector3D v1 = new Vector3D(1, -2, 3);
        Vector3D v2 = new Vector3D(-4, 2, 0);
        Assert.assertEquals(0.0, Vector3D.distanceSq(Vector3D.MINUS_I, Vector3D.MINUS_I), 0);
        Assert.assertEquals(50.0, Vector3D.distanceSq(v1, v2), 1.0e-12);
        Assert.assertEquals(Vector3D.distance(v1, v2) * Vector3D.distance(v1, v2),
                            Vector3D.distanceSq(v1, v2), 1.0e-12);
  }

    @Test
    public void testDistanceInf() {
        Vector3D v1 = new Vector3D(1, -2, 3);
        Vector3D v2 = new Vector3D(-4, 2, 0);
        Assert.assertEquals(0.0, Vector3D.distanceInf(Vector3D.MINUS_I, Vector3D.MINUS_I), 0);
        Assert.assertEquals(5.0, Vector3D.distanceInf(v1, v2), 1.0e-12);
        Assert.assertEquals(v1.subtract(v2).getNormInf(), Vector3D.distanceInf(v1, v2), 1.0e-12);
    }

    @Test
    public void testSubtract() {
        Vector3D v1 = new Vector3D(1, 2, 3);
        Vector3D v2 = new Vector3D(-3, -2, -1);
        v1 = v1.subtract(v2);
        checkVector(v1, 4, 4, 4);

        checkVector(v2.subtract(v1), -7, -6, -5);
        checkVector(v2.subtract(3, v1), -15, -14, -13);
    }

    @Test
    public void testAdd() {
        Vector3D v1 = new Vector3D(1, 2, 3);
        Vector3D v2 = new Vector3D(-3, -2, -1);
        v1 = v1.add(v2);
        checkVector(v1, -2, 0, 2);

        checkVector(v2.add(v1), -5, -2, 1);
        checkVector(v2.add(3, v1), -9, -2, 5);
    }

    @Test
    public void testScalarProduct() {
        Vector3D v = new Vector3D(1, 2, 3);
        v = v.scalarMultiply(3);
        checkVector(v, 3, 6, 9);

        checkVector(v.scalarMultiply(0.5), 1.5, 3, 4.5);
    }

    @Test
    public void testVectorialProducts() {
        Vector3D v1 = new Vector3D(2, 1, -4);
        Vector3D v2 = new Vector3D(3, 1, -1);

        Assert.assertTrue(FastMath.abs(Vector3D.dotProduct(v1, v2) - 11) < 1.0e-12);

        Vector3D v3 = Vector3D.crossProduct(v1, v2);
        checkVector(v3, 3, -10, -1);

        Assert.assertTrue(FastMath.abs(Vector3D.dotProduct(v1, v3)) < 1.0e-12);
        Assert.assertTrue(FastMath.abs(Vector3D.dotProduct(v2, v3)) < 1.0e-12);
    }

    @Test
    public void testAngular() {
        Assert.assertEquals(0,           Vector3D.PLUS_I.getAlpha(), 1.0e-10);
        Assert.assertEquals(0,           Vector3D.PLUS_I.getDelta(), 1.0e-10);
        Assert.assertEquals(FastMath.PI / 2, Vector3D.PLUS_J.getAlpha(), 1.0e-10);
        Assert.assertEquals(0,           Vector3D.PLUS_J.getDelta(), 1.0e-10);
        Assert.assertEquals(0,           Vector3D.PLUS_K.getAlpha(), 1.0e-10);
        Assert.assertEquals(FastMath.PI / 2, Vector3D.PLUS_K.getDelta(), 1.0e-10);
      
        Vector3D u = new Vector3D(-1, 1, -1);
        Assert.assertEquals(3 * FastMath.PI /4, u.getAlpha(), 1.0e-10);
        Assert.assertEquals(-1.0 / FastMath.sqrt(3), FastMath.sin(u.getDelta()), 1.0e-10);
    }

    @Test
    public void testAngularSeparation() {
        Vector3D v1 = new Vector3D(2, -1, 4);

        Vector3D  k = v1.normalize();
        Vector3D  i = k.orthogonal();
        Vector3D v2 = k.scalarMultiply(FastMath.cos(1.2)).add(i.scalarMultiply(FastMath.sin(1.2)));

        Assert.assertTrue(FastMath.abs(Vector3D.angle(v1, v2) - 1.2) < 1.0e-12);
  }

    @Test
    public void testNormalize() {
        Assert.assertEquals(1.0, new Vector3D(5, -4, 2).normalize().getNorm(), 1.0e-12);
        try {
            Vector3D.ZERO.normalize();
            Assert.fail("an exception should have been thrown");
        } catch (MathArithmeticException ae) {
            // expected behavior
        }
    }

    @Test
    public void testOrthogonal() {
        Vector3D v1 = new Vector3D(0.1, 2.5, 1.3);
        Assert.assertEquals(0.0, Vector3D.dotProduct(v1, v1.orthogonal()), 1.0e-12);
        Vector3D v2 = new Vector3D(2.3, -0.003, 7.6);
        Assert.assertEquals(0.0, Vector3D.dotProduct(v2, v2.orthogonal()), 1.0e-12);
        Vector3D v3 = new Vector3D(-1.7, 1.4, 0.2);
        Assert.assertEquals(0.0, Vector3D.dotProduct(v3, v3.orthogonal()), 1.0e-12);
        try {
            new Vector3D(0, 0, 0).orthogonal();
            Assert.fail("an exception should have been thrown");
        } catch (MathArithmeticException ae) {
            // expected behavior
        }
    }

    @Test
    public void testAngle() {
        Assert.assertEquals(0.22572612855273393616,
                            Vector3D.angle(new Vector3D(1, 2, 3), new Vector3D(4, 5, 6)),
                            1.0e-12);
        Assert.assertEquals(7.98595620686106654517199e-8,
                            Vector3D.angle(new Vector3D(1, 2, 3), new Vector3D(2, 4, 6.000001)),
                            1.0e-12);
        Assert.assertEquals(3.14159257373023116985197793156,
                            Vector3D.angle(new Vector3D(1, 2, 3), new Vector3D(-2, -4, -6.000001)),
                            1.0e-12);
        try {
            Vector3D.angle(Vector3D.ZERO, Vector3D.PLUS_I);
            Assert.fail("an exception should have been thrown");
        } catch (MathArithmeticException ae) {
            // expected behavior
        }
    }

    private void checkVector(Vector3D v, double x, double y, double z) {
        Assert.assertEquals(x, v.getX(), 1.0e-12);
        Assert.assertEquals(y, v.getY(), 1.0e-12);
        Assert.assertEquals(z, v.getZ(), 1.0e-12);
    }
}
