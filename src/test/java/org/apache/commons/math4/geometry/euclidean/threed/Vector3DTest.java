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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import org.apache.commons.math4.TestUtils;
import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.MathArithmeticException;
import org.apache.commons.math4.geometry.Space;
import org.apache.commons.math4.geometry.euclidean.threed.Rotation;
import org.apache.commons.math4.geometry.euclidean.threed.Coordinates3D;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.math4.util.Precision;
import org.junit.Assert;
import org.junit.Test;

public class Vector3DTest {
    @Test
    public void testConstructors() throws DimensionMismatchException {
        double r = FastMath.sqrt(2) /2;
        checkVector(new Coordinates3D(2, new Coordinates3D(FastMath.PI / 3, -FastMath.PI / 4)),
                    r, r * FastMath.sqrt(3), -2 * r);
        checkVector(new Coordinates3D(2, Coordinates3D.PLUS_I,
                                 -3, Coordinates3D.MINUS_K),
                    2, 0, 3);
        checkVector(new Coordinates3D(2, Coordinates3D.PLUS_I,
                                 5, Coordinates3D.PLUS_J,
                                 -3, Coordinates3D.MINUS_K),
                    2, 5, 3);
        checkVector(new Coordinates3D(2, Coordinates3D.PLUS_I,
                                 5, Coordinates3D.PLUS_J,
                                 5, Coordinates3D.MINUS_J,
                                 -3, Coordinates3D.MINUS_K),
                    2, 0, 3);
        checkVector(new Coordinates3D(new double[] { 2,  5,  -3 }),
                    2, 5, -3);
    }

    @Test
    public void testSpace() {
        Space space = new Coordinates3D(1, 2, 2).getSpace();
        Assert.assertEquals(3, space.getDimension());
        Assert.assertEquals(2, space.getSubSpace().getDimension());
        Space deserialized = (Space) TestUtils.serializeAndRecover(space);
        Assert.assertTrue(space == deserialized);
    }

    @Test
    public void testZero() {
        Assert.assertEquals(0, new Coordinates3D(1, 2, 2).getZero().getNorm(), 1.0e-15);
    }

    @Test
    public void testEquals() {
        Coordinates3D u1 = new Coordinates3D(1, 2, 3);
        Coordinates3D u2 = new Coordinates3D(1, 2, 3);
        Assert.assertTrue(u1.equals(u1));
        Assert.assertTrue(u1.equals(u2));
        Assert.assertFalse(u1.equals(new Rotation(1, 0, 0, 0, false)));
        Assert.assertFalse(u1.equals(new Coordinates3D(1, 2, 3 + 10 * Precision.EPSILON)));
        Assert.assertFalse(u1.equals(new Coordinates3D(1, 2 + 10 * Precision.EPSILON, 3)));
        Assert.assertFalse(u1.equals(new Coordinates3D(1 + 10 * Precision.EPSILON, 2, 3)));
        Assert.assertTrue(new Coordinates3D(0, Double.NaN, 0).equals(new Coordinates3D(0, 0, Double.NaN)));
    }

    @Test
    public void testHash() {
        Assert.assertEquals(new Coordinates3D(0, Double.NaN, 0).hashCode(), new Coordinates3D(0, 0, Double.NaN).hashCode());
        Coordinates3D u = new Coordinates3D(1, 2, 3);
        Coordinates3D v = new Coordinates3D(1, 2, 3 + 10 * Precision.EPSILON);
        Assert.assertTrue(u.hashCode() != v.hashCode());
    }

    @Test
    public void testInfinite() {
        Assert.assertTrue(new Coordinates3D(1, 1, Double.NEGATIVE_INFINITY).isInfinite());
        Assert.assertTrue(new Coordinates3D(1, Double.NEGATIVE_INFINITY, 1).isInfinite());
        Assert.assertTrue(new Coordinates3D(Double.NEGATIVE_INFINITY, 1, 1).isInfinite());
        Assert.assertFalse(new Coordinates3D(1, 1, 2).isInfinite());
        Assert.assertFalse(new Coordinates3D(1, Double.NaN, Double.NEGATIVE_INFINITY).isInfinite());
    }

    @Test
    public void testNaN() {
        Assert.assertTrue(new Coordinates3D(1, 1, Double.NaN).isNaN());
        Assert.assertTrue(new Coordinates3D(1, Double.NaN, 1).isNaN());
        Assert.assertTrue(new Coordinates3D(Double.NaN, 1, 1).isNaN());
        Assert.assertFalse(new Coordinates3D(1, 1, 2).isNaN());
        Assert.assertFalse(new Coordinates3D(1, 1, Double.NEGATIVE_INFINITY).isNaN());
    }

    @Test
    public void testToString() {
        Assert.assertEquals("{3; 2; 1}", new Coordinates3D(3, 2, 1).toString());
        NumberFormat format = new DecimalFormat("0.000", new DecimalFormatSymbols(Locale.US));
        Assert.assertEquals("{3.000; 2.000; 1.000}", new Coordinates3D(3, 2, 1).toString(format));
    }

    @Test(expected=DimensionMismatchException.class)
    public void testWrongDimension() throws DimensionMismatchException {
        new Coordinates3D(new double[] { 2,  5 });
    }

    @Test
    public void testCoordinates() {
        Coordinates3D v = new Coordinates3D(1, 2, 3);
        Assert.assertTrue(FastMath.abs(v.getX() - 1) < 1.0e-12);
        Assert.assertTrue(FastMath.abs(v.getY() - 2) < 1.0e-12);
        Assert.assertTrue(FastMath.abs(v.getZ() - 3) < 1.0e-12);
        double[] coordinates = v.toArray();
        Assert.assertTrue(FastMath.abs(coordinates[0] - 1) < 1.0e-12);
        Assert.assertTrue(FastMath.abs(coordinates[1] - 2) < 1.0e-12);
        Assert.assertTrue(FastMath.abs(coordinates[2] - 3) < 1.0e-12);
    }

    @Test
    public void testNorm1() {
        Assert.assertEquals(0.0, Coordinates3D.ZERO.getNorm1(), 0);
        Assert.assertEquals(6.0, new Coordinates3D(1, -2, 3).getNorm1(), 0);
    }

    @Test
    public void testNorm() {
        Assert.assertEquals(0.0, Coordinates3D.ZERO.getNorm(), 0);
        Assert.assertEquals(FastMath.sqrt(14), new Coordinates3D(1, 2, 3).getNorm(), 1.0e-12);
    }

    @Test
    public void testNormSq() {
        Assert.assertEquals(0.0, new Coordinates3D(0, 0, 0).getNormSq(), 0);
        Assert.assertEquals(14, new Coordinates3D(1, 2, 3).getNormSq(), 1.0e-12);
    }

    @Test
    public void testNormInf() {
        Assert.assertEquals(0.0, Coordinates3D.ZERO.getNormInf(), 0);
        Assert.assertEquals(3.0, new Coordinates3D(1, -2, 3).getNormInf(), 0);
    }

    @Test
    public void testDistance1() {
        Coordinates3D v1 = new Coordinates3D(1, -2, 3);
        Coordinates3D v2 = new Coordinates3D(-4, 2, 0);
        Assert.assertEquals(0.0, Coordinates3D.distance1(Coordinates3D.MINUS_I, Coordinates3D.MINUS_I), 0);
        Assert.assertEquals(12.0, Coordinates3D.distance1(v1, v2), 1.0e-12);
        Assert.assertEquals(v1.subtract(v2).getNorm1(), Coordinates3D.distance1(v1, v2), 1.0e-12);
    }

    @Test
    public void testDistance() {
        Coordinates3D v1 = new Coordinates3D(1, -2, 3);
        Coordinates3D v2 = new Coordinates3D(-4, 2, 0);
        Assert.assertEquals(0.0, Coordinates3D.distance(Coordinates3D.MINUS_I, Coordinates3D.MINUS_I), 0);
        Assert.assertEquals(FastMath.sqrt(50), Coordinates3D.distance(v1, v2), 1.0e-12);
        Assert.assertEquals(v1.subtract(v2).getNorm(), Coordinates3D.distance(v1, v2), 1.0e-12);
    }

    @Test
    public void testDistanceSq() {
        Coordinates3D v1 = new Coordinates3D(1, -2, 3);
        Coordinates3D v2 = new Coordinates3D(-4, 2, 0);
        Assert.assertEquals(0.0, Coordinates3D.distanceSq(Coordinates3D.MINUS_I, Coordinates3D.MINUS_I), 0);
        Assert.assertEquals(50.0, Coordinates3D.distanceSq(v1, v2), 1.0e-12);
        Assert.assertEquals(Coordinates3D.distance(v1, v2) * Coordinates3D.distance(v1, v2),
                            Coordinates3D.distanceSq(v1, v2), 1.0e-12);
  }

    @Test
    public void testDistanceInf() {
        Coordinates3D v1 = new Coordinates3D(1, -2, 3);
        Coordinates3D v2 = new Coordinates3D(-4, 2, 0);
        Assert.assertEquals(0.0, Coordinates3D.distanceInf(Coordinates3D.MINUS_I, Coordinates3D.MINUS_I), 0);
        Assert.assertEquals(5.0, Coordinates3D.distanceInf(v1, v2), 1.0e-12);
        Assert.assertEquals(v1.subtract(v2).getNormInf(), Coordinates3D.distanceInf(v1, v2), 1.0e-12);
    }

    @Test
    public void testSubtract() {
        Coordinates3D v1 = new Coordinates3D(1, 2, 3);
        Coordinates3D v2 = new Coordinates3D(-3, -2, -1);
        v1 = v1.subtract(v2);
        checkVector(v1, 4, 4, 4);

        checkVector(v2.subtract(v1), -7, -6, -5);
        checkVector(v2.subtract(3, v1), -15, -14, -13);
    }

    @Test
    public void testAdd() {
        Coordinates3D v1 = new Coordinates3D(1, 2, 3);
        Coordinates3D v2 = new Coordinates3D(-3, -2, -1);
        v1 = v1.add(v2);
        checkVector(v1, -2, 0, 2);

        checkVector(v2.add(v1), -5, -2, 1);
        checkVector(v2.add(3, v1), -9, -2, 5);
    }

    @Test
    public void testScalarProduct() {
        Coordinates3D v = new Coordinates3D(1, 2, 3);
        v = v.scalarMultiply(3);
        checkVector(v, 3, 6, 9);

        checkVector(v.scalarMultiply(0.5), 1.5, 3, 4.5);
    }

    @Test
    public void testVectorialProducts() {
        Coordinates3D v1 = new Coordinates3D(2, 1, -4);
        Coordinates3D v2 = new Coordinates3D(3, 1, -1);

        Assert.assertTrue(FastMath.abs(Coordinates3D.dotProduct(v1, v2) - 11) < 1.0e-12);

        Coordinates3D v3 = Coordinates3D.crossProduct(v1, v2);
        checkVector(v3, 3, -10, -1);

        Assert.assertTrue(FastMath.abs(Coordinates3D.dotProduct(v1, v3)) < 1.0e-12);
        Assert.assertTrue(FastMath.abs(Coordinates3D.dotProduct(v2, v3)) < 1.0e-12);
    }

    @Test
    public void testCrossProductCancellation() {
        Coordinates3D v1 = new Coordinates3D(9070467121.0, 4535233560.0, 1);
        Coordinates3D v2 = new Coordinates3D(9070467123.0, 4535233561.0, 1);
        checkVector(Coordinates3D.crossProduct(v1, v2), -1, 2, 1);

        double scale    = FastMath.scalb(1.0, 100);
        Coordinates3D big1   = new Coordinates3D(scale, v1);
        Coordinates3D small2 = new Coordinates3D(1 / scale, v2);
        checkVector(Coordinates3D.crossProduct(big1, small2), -1, 2, 1);

    }

    @Test
    public void testAngular() {
        Assert.assertEquals(0,           Coordinates3D.PLUS_I.getAlpha(), 1.0e-10);
        Assert.assertEquals(0,           Coordinates3D.PLUS_I.getDelta(), 1.0e-10);
        Assert.assertEquals(FastMath.PI / 2, Coordinates3D.PLUS_J.getAlpha(), 1.0e-10);
        Assert.assertEquals(0,           Coordinates3D.PLUS_J.getDelta(), 1.0e-10);
        Assert.assertEquals(0,           Coordinates3D.PLUS_K.getAlpha(), 1.0e-10);
        Assert.assertEquals(FastMath.PI / 2, Coordinates3D.PLUS_K.getDelta(), 1.0e-10);

        Coordinates3D u = new Coordinates3D(-1, 1, -1);
        Assert.assertEquals(3 * FastMath.PI /4, u.getAlpha(), 1.0e-10);
        Assert.assertEquals(-1.0 / FastMath.sqrt(3), FastMath.sin(u.getDelta()), 1.0e-10);
    }

    @Test
    public void testAngularSeparation() throws MathArithmeticException {
        Coordinates3D v1 = new Coordinates3D(2, -1, 4);

        Coordinates3D  k = v1.normalize();
        Coordinates3D  i = k.orthogonal();
        Coordinates3D v2 = k.scalarMultiply(FastMath.cos(1.2)).add(i.scalarMultiply(FastMath.sin(1.2)));

        Assert.assertTrue(FastMath.abs(Coordinates3D.angle(v1, v2) - 1.2) < 1.0e-12);
  }

    @Test
    public void testNormalize() throws MathArithmeticException {
        Assert.assertEquals(1.0, new Coordinates3D(5, -4, 2).normalize().getNorm(), 1.0e-12);
        try {
            Coordinates3D.ZERO.normalize();
            Assert.fail("an exception should have been thrown");
        } catch (MathArithmeticException ae) {
            // expected behavior
        }
    }

    @Test
    public void testNegate() {
        checkVector(new Coordinates3D(0.1, 2.5, 1.3).negate(), -0.1, -2.5, -1.3);
    }

    @Test
    public void testOrthogonal() throws MathArithmeticException {
        Coordinates3D v1 = new Coordinates3D(0.1, 2.5, 1.3);
        Assert.assertEquals(0.0, Coordinates3D.dotProduct(v1, v1.orthogonal()), 1.0e-12);
        Coordinates3D v2 = new Coordinates3D(2.3, -0.003, 7.6);
        Assert.assertEquals(0.0, Coordinates3D.dotProduct(v2, v2.orthogonal()), 1.0e-12);
        Coordinates3D v3 = new Coordinates3D(-1.7, 1.4, 0.2);
        Assert.assertEquals(0.0, Coordinates3D.dotProduct(v3, v3.orthogonal()), 1.0e-12);
        Coordinates3D v4 = new Coordinates3D(4.2, 0.1, -1.8);
        Assert.assertEquals(0.0, Coordinates3D.dotProduct(v4, v4.orthogonal()), 1.0e-12);
        try {
            new Coordinates3D(0, 0, 0).orthogonal();
            Assert.fail("an exception should have been thrown");
        } catch (MathArithmeticException ae) {
            // expected behavior
        }
    }
    @Test
    public void testAngle() throws MathArithmeticException {
        Assert.assertEquals(0.22572612855273393616,
                            Coordinates3D.angle(new Coordinates3D(1, 2, 3), new Coordinates3D(4, 5, 6)),
                            1.0e-12);
        Assert.assertEquals(7.98595620686106654517199e-8,
                            Coordinates3D.angle(new Coordinates3D(1, 2, 3), new Coordinates3D(2, 4, 6.000001)),
                            1.0e-12);
        Assert.assertEquals(3.14159257373023116985197793156,
                            Coordinates3D.angle(new Coordinates3D(1, 2, 3), new Coordinates3D(-2, -4, -6.000001)),
                            1.0e-12);
        try {
            Coordinates3D.angle(Coordinates3D.ZERO, Coordinates3D.PLUS_I);
            Assert.fail("an exception should have been thrown");
        } catch (MathArithmeticException ae) {
            // expected behavior
        }
    }

    @Test
    public void testAccurateDotProduct() {
        // the following two vectors are nearly but not exactly orthogonal
        // naive dot product (i.e. computing u1.x * u2.x + u1.y * u2.y + u1.z * u2.z
        // leads to a result of 0.0, instead of the correct -1.855129...
        Coordinates3D u1 = new Coordinates3D(-1321008684645961.0 /  268435456.0,
                                   -5774608829631843.0 /  268435456.0,
                                   -7645843051051357.0 / 8589934592.0);
        Coordinates3D u2 = new Coordinates3D(-5712344449280879.0 /    2097152.0,
                                   -4550117129121957.0 /    2097152.0,
                                    8846951984510141.0 /     131072.0);
        double sNaive = u1.getX() * u2.getX() + u1.getY() * u2.getY() + u1.getZ() * u2.getZ();
        double sAccurate = u1.dotProduct(u2);
        Assert.assertEquals(0.0, sNaive, 1.0e-30);
        Assert.assertEquals(-2088690039198397.0 / 1125899906842624.0, sAccurate, 1.0e-15);
    }

    @Test
    public void testDotProduct() {
        // we compare accurate versus naive dot product implementations
        // on regular vectors (i.e. not extreme cases like in the previous test)
        UniformRandomProvider random = RandomSource.create(RandomSource.WELL_1024_A, 553267312521321237l);
        for (int i = 0; i < 10000; ++i) {
            double ux = 10000 * random.nextDouble();
            double uy = 10000 * random.nextDouble();
            double uz = 10000 * random.nextDouble();
            double vx = 10000 * random.nextDouble();
            double vy = 10000 * random.nextDouble();
            double vz = 10000 * random.nextDouble();
            double sNaive = ux * vx + uy * vy + uz * vz;
            double sAccurate = new Coordinates3D(ux, uy, uz).dotProduct(new Coordinates3D(vx, vy, vz));
            Assert.assertEquals(sNaive, sAccurate, 2.5e-16 * sAccurate);
        }
    }

    @Test
    public void testAccurateCrossProduct() {
        // the vectors u1 and u2 are nearly but not exactly anti-parallel
        // (7.31e-16 degrees from 180 degrees) naive cross product (i.e.
        // computing u1.x * u2.x + u1.y * u2.y + u1.z * u2.z
        // leads to a result of   [0.0009765, -0.0001220, -0.0039062],
        // instead of the correct [0.0006913, -0.0001254, -0.0007909]
        final Coordinates3D u1 = new Coordinates3D(-1321008684645961.0 /   268435456.0,
                                         -5774608829631843.0 /   268435456.0,
                                         -7645843051051357.0 /  8589934592.0);
        final Coordinates3D u2 = new Coordinates3D( 1796571811118507.0 /  2147483648.0,
                                          7853468008299307.0 /  2147483648.0,
                                          2599586637357461.0 / 17179869184.0);
        final Coordinates3D u3 = new Coordinates3D(12753243807587107.0 / 18446744073709551616.0,
                                         -2313766922703915.0 / 18446744073709551616.0,
                                          -227970081415313.0 /   288230376151711744.0);
        Coordinates3D cNaive = new Coordinates3D(u1.getY() * u2.getZ() - u1.getZ() * u2.getY(),
                                       u1.getZ() * u2.getX() - u1.getX() * u2.getZ(),
                                       u1.getX() * u2.getY() - u1.getY() * u2.getX());
        Coordinates3D cAccurate = u1.crossProduct(u2);
        Assert.assertTrue(u3.distance(cNaive) > 2.9 * u3.getNorm());
        Assert.assertEquals(0.0, u3.distance(cAccurate), 1.0e-30 * cAccurate.getNorm());
    }

    @Test
    public void testCrossProduct() {
        // we compare accurate versus naive cross product implementations
        // on regular vectors (i.e. not extreme cases like in the previous test)
        UniformRandomProvider random = RandomSource.create(RandomSource.WELL_1024_A, 885362227452043215l);
        for (int i = 0; i < 10000; ++i) {
            double ux = 10000 * random.nextDouble();
            double uy = 10000 * random.nextDouble();
            double uz = 10000 * random.nextDouble();
            double vx = 10000 * random.nextDouble();
            double vy = 10000 * random.nextDouble();
            double vz = 10000 * random.nextDouble();
            Coordinates3D cNaive = new Coordinates3D(uy * vz - uz * vy, uz * vx - ux * vz, ux * vy - uy * vx);
            Coordinates3D cAccurate = new Coordinates3D(ux, uy, uz).crossProduct(new Coordinates3D(vx, vy, vz));
            Assert.assertEquals(0.0, cAccurate.distance(cNaive), 6.0e-15 * cAccurate.getNorm());
        }
    }

    private void checkVector(Coordinates3D v, double x, double y, double z) {
        Assert.assertEquals(x, v.getX(), 1.0e-12);
        Assert.assertEquals(y, v.getY(), 1.0e-12);
        Assert.assertEquals(z, v.getZ(), 1.0e-12);
    }
}
