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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.MathArithmeticException;
import org.apache.commons.math4.geometry.Space;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.numbers.core.Precision;
import org.junit.Assert;
import org.junit.Test;

public class Cartesian1DTest {
    @Test
    public void testConstructors() throws DimensionMismatchException {
        checkVector(new Cartesian1D(3, new Cartesian1D(FastMath.PI / 3)),
                    FastMath.PI);
        checkVector(new Cartesian1D(2, Cartesian1D.ONE, -3, new Cartesian1D(2)),
                    -4);
        checkVector(new Cartesian1D(2, Cartesian1D.ONE,
                                 5, new Cartesian1D(2),
                                 -3, new Cartesian1D(3)),
                    3);
        checkVector(new Cartesian1D(2, Cartesian1D.ONE,
                                 5, new Cartesian1D(2),
                                 5, new Cartesian1D(-2),
                                 -3, new Cartesian1D(-3)),
                    11);
    }

    @Test
    public void testSpace() {
        Space space = new Cartesian1D(1).getSpace();
        Assert.assertEquals(1, space.getDimension());
    }

    @Test
    public void testZero() {
        Assert.assertEquals(0, new Cartesian1D(1).getZero().getNorm(), 1.0e-15);
    }

    @Test
    public void testEquals() {
        Cartesian1D u1 = new Cartesian1D(1);
        Cartesian1D u2 = new Cartesian1D(1);
        Assert.assertTrue(u1.equals(u1));
        Assert.assertTrue(u1.equals(u2));
        Assert.assertFalse(u1.equals(new Cartesian1D(1 + 10 * Precision.EPSILON)));
        Assert.assertTrue(new Cartesian1D(Double.NaN).equals(new Cartesian1D(Double.NaN)));
    }

    @Test
    public void testHash() {
        Assert.assertEquals(new Cartesian1D(Double.NaN).hashCode(), new Cartesian1D(Double.NaN).hashCode());
        Cartesian1D u = new Cartesian1D(1);
        Cartesian1D v = new Cartesian1D(1 + 10 * Precision.EPSILON);
        Assert.assertTrue(u.hashCode() != v.hashCode());
    }

    @Test
    public void testInfinite() {
        Assert.assertTrue(new Cartesian1D(Double.NEGATIVE_INFINITY).isInfinite());
        Assert.assertTrue(new Cartesian1D(Double.POSITIVE_INFINITY).isInfinite());
        Assert.assertFalse(new Cartesian1D(1).isInfinite());
        Assert.assertFalse(new Cartesian1D(Double.NaN).isInfinite());
    }

    @Test
    public void testNaN() {
        Assert.assertTrue(new Cartesian1D(Double.NaN).isNaN());
        Assert.assertFalse(new Cartesian1D(1).isNaN());
        Assert.assertFalse(new Cartesian1D(Double.NEGATIVE_INFINITY).isNaN());
    }

    @Test
    public void testToString() {
        Assert.assertEquals("{3}", new Cartesian1D(3).toString());
        NumberFormat format = new DecimalFormat("0.000", new DecimalFormatSymbols(Locale.US));
        Assert.assertEquals("{3.000}", new Cartesian1D(3).toString(format));
    }

    @Test
    public void testCoordinates() {
        Cartesian1D v = new Cartesian1D(1);
        Assert.assertTrue(FastMath.abs(v.getX() - 1) < 1.0e-12);
    }

    @Test
    public void testNorm1() {
        Assert.assertEquals(0.0, Cartesian1D.ZERO.getNorm1(), 0);
        Assert.assertEquals(6.0, new Cartesian1D(6).getNorm1(), 0);
    }

    @Test
    public void testNorm() {
        Assert.assertEquals(0.0, Cartesian1D.ZERO.getNorm(), 0);
        Assert.assertEquals(3.0, new Cartesian1D(-3).getNorm(), 1.0e-12);
    }

    @Test
    public void testNormSq() {
        Assert.assertEquals(0.0, new Cartesian1D(0).getNormSq(), 0);
        Assert.assertEquals(9.0, new Cartesian1D(-3).getNormSq(), 1.0e-12);
    }

    @Test
    public void testNormInf() {
        Assert.assertEquals(0.0, Cartesian1D.ZERO.getNormInf(), 0);
        Assert.assertEquals(3.0, new Cartesian1D(-3).getNormInf(), 0);
    }

    @Test
    public void testDistance1() {
        Cartesian1D v1 = new Cartesian1D(1);
        Cartesian1D v2 = new Cartesian1D(-4);
        Assert.assertEquals(0.0, new Cartesian1D(-1).distance1(new Cartesian1D(-1)), 0);
        Assert.assertEquals(5.0, v1.distance1(v2), 1.0e-12);
        Assert.assertEquals(v1.subtract(v2).getNorm1(), v1.distance1(v2), 1.0e-12);
    }

    @Test
    public void testDistance() {
        Cartesian1D v1 = new Cartesian1D(1);
        Cartesian1D v2 = new Cartesian1D(-4);
        Assert.assertEquals(0.0, Cartesian1D.distance(new Cartesian1D(-1), new Cartesian1D(-1)), 0);
        Assert.assertEquals(5.0, Cartesian1D.distance(v1, v2), 1.0e-12);
        Assert.assertEquals(v1.subtract(v2).getNorm(), Cartesian1D.distance(v1, v2), 1.0e-12);
    }

    @Test
    public void testDistanceSq() {
        Cartesian1D v1 = new Cartesian1D(1);
        Cartesian1D v2 = new Cartesian1D(-4);
        Assert.assertEquals(0.0, Cartesian1D.distanceSq(new Cartesian1D(-1), new Cartesian1D(-1)), 0);
        Assert.assertEquals(25.0, Cartesian1D.distanceSq(v1, v2), 1.0e-12);
        Assert.assertEquals(Cartesian1D.distance(v1, v2) * Cartesian1D.distance(v1, v2),
                            Cartesian1D.distanceSq(v1, v2), 1.0e-12);
  }

    @Test
    public void testDistanceInf() {
        Cartesian1D v1 = new Cartesian1D(1);
        Cartesian1D v2 = new Cartesian1D(-4);
        Assert.assertEquals(0.0, Cartesian1D.distanceInf(new Cartesian1D(-1), new Cartesian1D(-1)), 0);
        Assert.assertEquals(5.0, Cartesian1D.distanceInf(v1, v2), 1.0e-12);
        Assert.assertEquals(v1.subtract(v2).getNormInf(), Cartesian1D.distanceInf(v1, v2), 1.0e-12);
    }

    @Test
    public void testSubtract() {
        Cartesian1D v1 = new Cartesian1D(1);
        Cartesian1D v2 = new Cartesian1D(-3);
        v1 = v1.subtract(v2);
        checkVector(v1, 4);

        checkVector(v2.subtract(v1), -7);
        checkVector(v2.subtract(3, v1), -15);
    }

    @Test
    public void testAdd() {
        Cartesian1D v1 = new Cartesian1D(1);
        Cartesian1D v2 = new Cartesian1D(-3);
        v1 = v1.add(v2);
        checkVector(v1, -2);

        checkVector(v2.add(v1), -5);
        checkVector(v2.add(3, v1), -9);
    }

    @Test
    public void testScalarProduct() {
        Cartesian1D v = new Cartesian1D(1);
        v = v.scalarMultiply(3);
        checkVector(v, 3);

        checkVector(v.scalarMultiply(0.5), 1.5);
    }

    @Test
    public void testNormalize() throws MathArithmeticException {
        Assert.assertEquals(1.0, new Cartesian1D(5).normalize().getNorm(), 1.0e-12);
        try {
            Cartesian1D.ZERO.normalize();
            Assert.fail("an exception should have been thrown");
        } catch (MathArithmeticException ae) {
            // expected behavior
        }
    }

    @Test
    public void testNegate() {
        checkVector(new Cartesian1D(0.1).negate(), -0.1);
    }

    private void checkVector(Cartesian1D v, double x) {
        Assert.assertEquals(x, v.getX(), 1.0e-12);
    }
}
