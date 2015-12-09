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

package org.apache.commons.math3.geometry.euclidean.threed;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.random.Well1024a;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;
import org.junit.Assert;
import org.junit.Test;

public class FieldVector3DTest {

    @Test
    public void testConstructors() throws DimensionMismatchException {
        double cosAlpha = 1 / 2.0;
        double sinAlpha = FastMath.sqrt(3) / 2.0;
        double cosDelta = FastMath.sqrt(2) / 2.0;
        double sinDelta = -FastMath.sqrt(2) / 2.0;
        FieldVector3D<DerivativeStructure> u = new FieldVector3D<DerivativeStructure>(2,
                new FieldVector3D<DerivativeStructure>(new DerivativeStructure(2, 1, 0,  FastMath.PI / 3),
                        new DerivativeStructure(2, 1, 1, -FastMath.PI / 4)));
        checkVector(u, 2 * cosAlpha * cosDelta, 2 * sinAlpha * cosDelta, 2 * sinDelta);
        Assert.assertEquals(-2 * sinAlpha * cosDelta, u.getX().getPartialDerivative(1, 0), 1.0e-12);
        Assert.assertEquals(+2 * cosAlpha * cosDelta, u.getY().getPartialDerivative(1, 0), 1.0e-12);
        Assert.assertEquals(0,                        u.getZ().getPartialDerivative(1, 0), 1.0e-12);
        Assert.assertEquals(-2 * cosAlpha * sinDelta, u.getX().getPartialDerivative(0, 1), 1.0e-12);
        Assert.assertEquals(-2 * sinAlpha * sinDelta, u.getY().getPartialDerivative(0, 1), 1.0e-12);
        Assert.assertEquals(2 * cosDelta,             u.getZ().getPartialDerivative(0, 1), 1.0e-12);

        checkVector(new FieldVector3D<DerivativeStructure>(2, createVector(1, 0,  0, 3)),
                                   2, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2);
        checkVector(new FieldVector3D<DerivativeStructure>(new DerivativeStructure(4, 1, 3,  2.0),
                                   createVector(1, 0,  0, 4)),
                                   2, 0, 0, 2, 0, 0, 1, 0, 2, 0, 0, 0, 0, 2, 0);
        checkVector(new FieldVector3D<DerivativeStructure>(new DerivativeStructure(4, 1, 3,  2.0),
                                   new Vector3D(1, 0,  0)),
                                   2, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0);

        checkVector(new FieldVector3D<DerivativeStructure>(2, createVector(1, 0,  0, 3),
                                   -3, createVector(0, 0, -1, 3)),
                                   2, 0, 3, -1, 0, 0, 0, -1, 0, 0, 0, -1);
        checkVector(new FieldVector3D<DerivativeStructure>(new DerivativeStructure(4, 1, 3,  2.0),
                                   createVector(1, 0,  0, 4),
                                   new DerivativeStructure(4, 1, 3, -3.0),
                                   createVector(0, 0, -1, 4)),
                                   2, 0, 3, -1, 0, 0, 1, 0, -1, 0, 0, 0, 0, -1, -1);
        checkVector(new FieldVector3D<DerivativeStructure>(new DerivativeStructure(4, 1, 3,  2.0),
                                   new Vector3D(1, 0,  0),
                                   new DerivativeStructure(4, 1, 3, -3.0),
                                   new Vector3D(0, 0, -1)),
                                   2, 0, 3, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, -1);

        checkVector(new FieldVector3D<DerivativeStructure>(2, createVector(1, 0, 0, 3),
                                   5, createVector(0, 1, 0, 3),
                                   -3, createVector(0, 0, -1, 3)),
                                   2, 5, 3, 4, 0, 0, 0, 4, 0, 0, 0, 4);
        checkVector(new FieldVector3D<DerivativeStructure>(new DerivativeStructure(4, 1, 3,  2.0),
                                   createVector(1, 0,  0, 4),
                                   new DerivativeStructure(4, 1, 3,  5.0),
                                   createVector(0, 1,  0, 4),
                                   new DerivativeStructure(4, 1, 3, -3.0),
                                   createVector(0, 0, -1, 4)),
                                   2, 5, 3, 4, 0, 0, 1, 0, 4, 0, 1, 0, 0, 4, -1);
        checkVector(new FieldVector3D<DerivativeStructure>(new DerivativeStructure(4, 1, 3,  2.0),
                                   new Vector3D(1, 0,  0),
                                   new DerivativeStructure(4, 1, 3,  5.0),
                                   new Vector3D(0, 1,  0),
                                   new DerivativeStructure(4, 1, 3, -3.0),
                                   new Vector3D(0, 0, -1)),
                                   2, 5, 3, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, -1);

        checkVector(new FieldVector3D<DerivativeStructure>(2, createVector(1, 0, 0, 3),
                                   5, createVector(0, 1, 0, 3),
                                   5, createVector(0, -1, 0, 3),
                                   -3, createVector(0, 0, -1, 3)),
                                   2, 0, 3, 9, 0, 0, 0, 9, 0, 0, 0, 9);
        checkVector(new FieldVector3D<DerivativeStructure>(new DerivativeStructure(4, 1, 3,  2.0),
                                   createVector(1, 0,  0, 4),
                                   new DerivativeStructure(4, 1, 3,  5.0),
                                   createVector(0, 1,  0, 4),
                                   new DerivativeStructure(4, 1, 3,  5.0),
                                   createVector(0, -1,  0, 4),
                                   new DerivativeStructure(4, 1, 3, -3.0),
                                   createVector(0, 0, -1, 4)),
                                   2, 0, 3, 9, 0, 0, 1, 0, 9, 0, 0, 0, 0, 9, -1);
        checkVector(new FieldVector3D<DerivativeStructure>(new DerivativeStructure(4, 1, 3,  2.0),
                                   new Vector3D(1, 0,  0),
                                   new DerivativeStructure(4, 1, 3,  5.0),
                                   new Vector3D(0, 1,  0),
                                   new DerivativeStructure(4, 1, 3,  5.0),
                                   new Vector3D(0, -1,  0),
                                   new DerivativeStructure(4, 1, 3, -3.0),
                                   new Vector3D(0, 0, -1)),
                                   2, 0, 3, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, -1);

        checkVector(new FieldVector3D<DerivativeStructure>(new DerivativeStructure[] {
            new DerivativeStructure(3, 1, 2,  2),
            new DerivativeStructure(3, 1, 1,  5),
            new DerivativeStructure(3, 1, 0, -3)
        }),
        2, 5, -3, 0, 0, 1, 0, 1, 0, 1, 0, 0);

    }

    @Test
    public void testEquals() {
        FieldVector3D<DerivativeStructure> u1 = createVector(1, 2, 3, 3);
        FieldVector3D<DerivativeStructure> v  = createVector(1, 2, 3 + 10 * Precision.EPSILON, 3);
        Assert.assertTrue(u1.equals(u1));
        Assert.assertTrue(u1.equals(new FieldVector3D<DerivativeStructure>(new DerivativeStructure(3, 1, 0, 1.0),
                                                   new DerivativeStructure(3, 1, 1, 2.0),
                                                   new DerivativeStructure(3, 1, 2, 3.0))));
        Assert.assertFalse(u1.equals(new FieldVector3D<DerivativeStructure>(new DerivativeStructure(3, 1, 1.0),
                                                    new DerivativeStructure(3, 1, 1, 2.0),
                                                    new DerivativeStructure(3, 1, 2, 3.0))));
        Assert.assertFalse(u1.equals(new FieldVector3D<DerivativeStructure>(new DerivativeStructure(3, 1, 0, 1.0),
                                                    new DerivativeStructure(3, 1, 2.0),
                                                    new DerivativeStructure(3, 1, 2, 3.0))));
        Assert.assertFalse(u1.equals(new FieldVector3D<DerivativeStructure>(new DerivativeStructure(3, 1, 0, 1.0),
                                                    new DerivativeStructure(3, 1, 1, 2.0),
                                                    new DerivativeStructure(3, 1, 3.0))));
        Assert.assertFalse(u1.equals(v));
        Assert.assertFalse(u1.equals(u1.toVector3D()));
        Assert.assertTrue(createVector(0, Double.NaN, 0, 3).equals(createVector(0, 0, Double.NaN, 3)));
    }

    @Test
    public void testHash() {
        Assert.assertEquals(createVector(0, Double.NaN, 0, 3).hashCode(), createVector(0, 0, Double.NaN, 3).hashCode());
        FieldVector3D<DerivativeStructure> u = createVector(1, 2, 3, 3);
        FieldVector3D<DerivativeStructure> v = createVector(1, 2, 3 + 10 * Precision.EPSILON, 3);
        Assert.assertTrue(u.hashCode() != v.hashCode());
    }

    @Test
    public void testInfinite() {
        Assert.assertTrue(createVector(1, 1, Double.NEGATIVE_INFINITY, 3).isInfinite());
        Assert.assertTrue(createVector(1, Double.NEGATIVE_INFINITY, 1, 3).isInfinite());
        Assert.assertTrue(createVector(Double.NEGATIVE_INFINITY, 1, 1, 3).isInfinite());
        Assert.assertFalse(createVector(1, 1, 2, 3).isInfinite());
        Assert.assertFalse(createVector(1, Double.NaN, Double.NEGATIVE_INFINITY, 3).isInfinite());
    }

    @Test
    public void testNaN() {
        Assert.assertTrue(createVector(1, 1, Double.NaN, 3).isNaN());
        Assert.assertTrue(createVector(1, Double.NaN, 1, 3).isNaN());
        Assert.assertTrue(createVector(Double.NaN, 1, 1, 3).isNaN());
        Assert.assertFalse(createVector(1, 1, 2, 3).isNaN());
        Assert.assertFalse(createVector(1, 1, Double.NEGATIVE_INFINITY, 3).isNaN());
    }

    @Test
    public void testToString() {
        Assert.assertEquals("{3; 2; 1}", createVector(3, 2, 1, 3).toString());
        NumberFormat format = new DecimalFormat("0.000", new DecimalFormatSymbols(Locale.US));
        Assert.assertEquals("{3.000; 2.000; 1.000}", createVector(3, 2, 1, 3).toString(format));
    }

    @Test(expected=DimensionMismatchException.class)
    public void testWrongDimension() throws DimensionMismatchException {
        new FieldVector3D<DerivativeStructure>(new DerivativeStructure[] {
            new DerivativeStructure(3, 1, 0, 2),
            new DerivativeStructure(3, 1, 0, 5)
        });
    }

    @Test
    public void testCoordinates() {
        FieldVector3D<DerivativeStructure> v = createVector(1, 2, 3, 3);
        Assert.assertTrue(FastMath.abs(v.getX().getReal() - 1) < 1.0e-12);
        Assert.assertTrue(FastMath.abs(v.getY().getReal() - 2) < 1.0e-12);
        Assert.assertTrue(FastMath.abs(v.getZ().getReal() - 3) < 1.0e-12);
        DerivativeStructure[] coordinates = v.toArray();
        Assert.assertTrue(FastMath.abs(coordinates[0].getReal() - 1) < 1.0e-12);
        Assert.assertTrue(FastMath.abs(coordinates[1].getReal() - 2) < 1.0e-12);
        Assert.assertTrue(FastMath.abs(coordinates[2].getReal() - 3) < 1.0e-12);
    }

    @Test
    public void testNorm1() {
        Assert.assertEquals( 0.0, createVector(0, 0, 0, 3).getNorm1().getReal(), 0);
        Assert.assertEquals( 6.0, createVector(1, -2, 3, 3).getNorm1().getReal(), 0);
        Assert.assertEquals( 1.0, createVector(1, -2, 3, 3).getNorm1().getPartialDerivative(1, 0, 0), 0);
        Assert.assertEquals(-1.0, createVector(1, -2, 3, 3).getNorm1().getPartialDerivative(0, 1, 0), 0);
        Assert.assertEquals( 1.0, createVector(1, -2, 3, 3).getNorm1().getPartialDerivative(0, 0, 1), 0);
    }

    @Test
    public void testNorm() {
        double r = FastMath.sqrt(14);
        Assert.assertEquals(0.0, createVector(0, 0, 0, 3).getNorm().getReal(), 0);
        Assert.assertEquals(r, createVector(1, 2, 3, 3).getNorm().getReal(), 1.0e-12);
        Assert.assertEquals( 1.0 / r, createVector(1, 2, 3, 3).getNorm().getPartialDerivative(1, 0, 0), 0);
        Assert.assertEquals( 2.0 / r, createVector(1, 2, 3, 3).getNorm().getPartialDerivative(0, 1, 0), 0);
        Assert.assertEquals( 3.0 / r, createVector(1, 2, 3, 3).getNorm().getPartialDerivative(0, 0, 1), 0);
    }

    @Test
    public void testNormSq() {
        Assert.assertEquals(0.0, createVector(0, 0, 0, 3).getNormSq().getReal(), 0);
        Assert.assertEquals(14, createVector(1, 2, 3, 3).getNormSq().getReal(), 1.0e-12);
        Assert.assertEquals( 2, createVector(1, 2, 3, 3).getNormSq().getPartialDerivative(1, 0, 0), 0);
        Assert.assertEquals( 4, createVector(1, 2, 3, 3).getNormSq().getPartialDerivative(0, 1, 0), 0);
        Assert.assertEquals( 6, createVector(1, 2, 3, 3).getNormSq().getPartialDerivative(0, 0, 1), 0);
    }

    @Test
    public void testNormInf() {
        Assert.assertEquals( 0.0, createVector(0, 0, 0, 3).getNormInf().getReal(), 0);
        Assert.assertEquals( 3.0, createVector(1, -2, 3, 3).getNormInf().getReal(), 0);
        Assert.assertEquals( 0.0, createVector(1, -2, 3, 3).getNormInf().getPartialDerivative(1, 0, 0), 0);
        Assert.assertEquals( 0.0, createVector(1, -2, 3, 3).getNormInf().getPartialDerivative(0, 1, 0), 0);
        Assert.assertEquals( 1.0, createVector(1, -2, 3, 3).getNormInf().getPartialDerivative(0, 0, 1), 0);
        Assert.assertEquals( 3.0, createVector(2, -1, 3, 3).getNormInf().getReal(), 0);
        Assert.assertEquals( 0.0, createVector(2, -1, 3, 3).getNormInf().getPartialDerivative(1, 0, 0), 0);
        Assert.assertEquals( 0.0, createVector(2, -1, 3, 3).getNormInf().getPartialDerivative(0, 1, 0), 0);
        Assert.assertEquals( 1.0, createVector(2, -1, 3, 3).getNormInf().getPartialDerivative(0, 0, 1), 0);
        Assert.assertEquals( 3.0, createVector(1, -3, 2, 3).getNormInf().getReal(), 0);
        Assert.assertEquals( 0.0, createVector(1, -3, 2, 3).getNormInf().getPartialDerivative(1, 0, 0), 0);
        Assert.assertEquals(-1.0, createVector(1, -3, 2, 3).getNormInf().getPartialDerivative(0, 1, 0), 0);
        Assert.assertEquals( 0.0, createVector(1, -3, 2, 3).getNormInf().getPartialDerivative(0, 0, 1), 0);
        Assert.assertEquals( 3.0, createVector(2, -3, 1, 3).getNormInf().getReal(), 0);
        Assert.assertEquals( 0.0, createVector(2, -3, 1, 3).getNormInf().getPartialDerivative(1, 0, 0), 0);
        Assert.assertEquals(-1.0, createVector(2, -3, 1, 3).getNormInf().getPartialDerivative(0, 1, 0), 0);
        Assert.assertEquals( 0.0, createVector(2, -3, 1, 3).getNormInf().getPartialDerivative(0, 0, 1), 0);
        Assert.assertEquals( 3.0, createVector(3, -1, 2, 3).getNormInf().getReal(), 0);
        Assert.assertEquals( 1.0, createVector(3, -1, 2, 3).getNormInf().getPartialDerivative(1, 0, 0), 0);
        Assert.assertEquals( 0.0, createVector(3, -1, 2, 3).getNormInf().getPartialDerivative(0, 1, 0), 0);
        Assert.assertEquals( 0.0, createVector(3, -1, 2, 3).getNormInf().getPartialDerivative(0, 0, 1), 0);
        Assert.assertEquals( 3.0, createVector(3, -2, 1, 3).getNormInf().getReal(), 0);
        Assert.assertEquals( 1.0, createVector(3, -2, 1, 3).getNormInf().getPartialDerivative(1, 0, 0), 0);
        Assert.assertEquals( 0.0, createVector(3, -2, 1, 3).getNormInf().getPartialDerivative(0, 1, 0), 0);
        Assert.assertEquals( 0.0, createVector(3, -2, 1, 3).getNormInf().getPartialDerivative(0, 0, 1), 0);
    }

    @Test
    public void testDistance1() {
        FieldVector3D<DerivativeStructure> v1 = createVector(1, -2, 3, 3);
        FieldVector3D<DerivativeStructure> v2 = createVector(-4, 2, 0, 3);
        Assert.assertEquals(0.0, FieldVector3D.distance1(createVector(-1, 0, 0, 3), createVector(-1, 0, 0, 3)).getReal(), 0);
        DerivativeStructure distance = FieldVector3D.distance1(v1, v2);
        Assert.assertEquals(12.0, distance.getReal(), 1.0e-12);
        Assert.assertEquals(0, distance.getPartialDerivative(1, 0, 0), 1.0e-12);
        Assert.assertEquals(0, distance.getPartialDerivative(0, 1, 0), 1.0e-12);
        Assert.assertEquals(0, distance.getPartialDerivative(0, 0, 1), 1.0e-12);
        distance = FieldVector3D.distance1(v1, new Vector3D(-4, 2, 0));
        Assert.assertEquals(12.0, distance.getReal(), 1.0e-12);
        Assert.assertEquals( 1, distance.getPartialDerivative(1, 0, 0), 1.0e-12);
        Assert.assertEquals(-1, distance.getPartialDerivative(0, 1, 0), 1.0e-12);
        Assert.assertEquals( 1, distance.getPartialDerivative(0, 0, 1), 1.0e-12);
        distance = FieldVector3D.distance1(new Vector3D(-4, 2, 0), v1);
        Assert.assertEquals(12.0, distance.getReal(), 1.0e-12);
        Assert.assertEquals( 1, distance.getPartialDerivative(1, 0, 0), 1.0e-12);
        Assert.assertEquals(-1, distance.getPartialDerivative(0, 1, 0), 1.0e-12);
        Assert.assertEquals( 1, distance.getPartialDerivative(0, 0, 1), 1.0e-12);
    }

    @Test
    public void testDistance() {
        FieldVector3D<DerivativeStructure> v1 = createVector(1, -2, 3, 3);
        FieldVector3D<DerivativeStructure> v2 = createVector(-4, 2, 0, 3);
        Assert.assertEquals(0.0, FieldVector3D.distance(createVector(-1, 0, 0, 3), createVector(-1, 0, 0, 3)).getReal(), 0);
        DerivativeStructure distance = FieldVector3D.distance(v1, v2);
        Assert.assertEquals(FastMath.sqrt(50), distance.getReal(), 1.0e-12);
        Assert.assertEquals(0, distance.getPartialDerivative(1, 0, 0), 1.0e-12);
        Assert.assertEquals(0, distance.getPartialDerivative(0, 1, 0), 1.0e-12);
        Assert.assertEquals(0, distance.getPartialDerivative(0, 0, 1), 1.0e-12);
        distance = FieldVector3D.distance(v1, new Vector3D(-4, 2, 0));
        Assert.assertEquals(FastMath.sqrt(50), distance.getReal(), 1.0e-12);
        Assert.assertEquals( 5 / FastMath.sqrt(50), distance.getPartialDerivative(1, 0, 0), 1.0e-12);
        Assert.assertEquals(-4 / FastMath.sqrt(50), distance.getPartialDerivative(0, 1, 0), 1.0e-12);
        Assert.assertEquals( 3 / FastMath.sqrt(50), distance.getPartialDerivative(0, 0, 1), 1.0e-12);
        distance = FieldVector3D.distance(new Vector3D(-4, 2, 0), v1);
        Assert.assertEquals(FastMath.sqrt(50), distance.getReal(), 1.0e-12);
        Assert.assertEquals( 5 / FastMath.sqrt(50), distance.getPartialDerivative(1, 0, 0), 1.0e-12);
        Assert.assertEquals(-4 / FastMath.sqrt(50), distance.getPartialDerivative(0, 1, 0), 1.0e-12);
        Assert.assertEquals( 3 / FastMath.sqrt(50), distance.getPartialDerivative(0, 0, 1), 1.0e-12);
    }

    @Test
    public void testDistanceSq() {
        FieldVector3D<DerivativeStructure> v1 = createVector(1, -2, 3, 3);
        FieldVector3D<DerivativeStructure> v2 = createVector(-4, 2, 0, 3);
        Assert.assertEquals(0.0, FieldVector3D.distanceSq(createVector(-1, 0, 0, 3), createVector(-1, 0, 0, 3)).getReal(), 0);
        DerivativeStructure distanceSq = FieldVector3D.distanceSq(v1, v2);
        Assert.assertEquals(50.0, distanceSq.getReal(), 1.0e-12);
        Assert.assertEquals(0, distanceSq.getPartialDerivative(1, 0, 0), 1.0e-12);
        Assert.assertEquals(0, distanceSq.getPartialDerivative(0, 1, 0), 1.0e-12);
        Assert.assertEquals(0, distanceSq.getPartialDerivative(0, 0, 1), 1.0e-12);
        distanceSq = FieldVector3D.distanceSq(v1, new Vector3D(-4, 2, 0));
        Assert.assertEquals(50.0, distanceSq.getReal(), 1.0e-12);
        Assert.assertEquals(10, distanceSq.getPartialDerivative(1, 0, 0), 1.0e-12);
        Assert.assertEquals(-8, distanceSq.getPartialDerivative(0, 1, 0), 1.0e-12);
        Assert.assertEquals( 6, distanceSq.getPartialDerivative(0, 0, 1), 1.0e-12);
        distanceSq = FieldVector3D.distanceSq(new Vector3D(-4, 2, 0), v1);
        Assert.assertEquals(50.0, distanceSq.getReal(), 1.0e-12);
        Assert.assertEquals(10, distanceSq.getPartialDerivative(1, 0, 0), 1.0e-12);
        Assert.assertEquals(-8, distanceSq.getPartialDerivative(0, 1, 0), 1.0e-12);
        Assert.assertEquals( 6, distanceSq.getPartialDerivative(0, 0, 1), 1.0e-12);
  }

    @Test
    public void testDistanceInf() {
        FieldVector3D<DerivativeStructure> v1 = createVector(1, -2, 3, 3);
        FieldVector3D<DerivativeStructure> v2 = createVector(-4, 2, 0, 3);
        Assert.assertEquals(0.0, FieldVector3D.distanceInf(createVector(-1, 0, 0, 3), createVector(-1, 0, 0, 3)).getReal(), 0);
        DerivativeStructure distance = FieldVector3D.distanceInf(v1, v2);
        Assert.assertEquals(5.0, distance.getReal(), 1.0e-12);
        Assert.assertEquals(0, distance.getPartialDerivative(1, 0, 0), 1.0e-12);
        Assert.assertEquals(0, distance.getPartialDerivative(0, 1, 0), 1.0e-12);
        Assert.assertEquals(0, distance.getPartialDerivative(0, 0, 1), 1.0e-12);
        distance = FieldVector3D.distanceInf(v1, new Vector3D(-4, 2, 0));
        Assert.assertEquals(5.0, distance.getReal(), 1.0e-12);
        Assert.assertEquals(1, distance.getPartialDerivative(1, 0, 0), 1.0e-12);
        Assert.assertEquals(0, distance.getPartialDerivative(0, 1, 0), 1.0e-12);
        Assert.assertEquals(0, distance.getPartialDerivative(0, 0, 1), 1.0e-12);
        distance = FieldVector3D.distanceInf(new Vector3D(-4, 2, 0), v1);
        Assert.assertEquals(5.0, distance.getReal(), 1.0e-12);
        Assert.assertEquals(1, distance.getPartialDerivative(1, 0, 0), 1.0e-12);
        Assert.assertEquals(0, distance.getPartialDerivative(0, 1, 0), 1.0e-12);
        Assert.assertEquals(0, distance.getPartialDerivative(0, 0, 1), 1.0e-12);
        Assert.assertEquals(v1.subtract(v2).getNormInf().getReal(), FieldVector3D.distanceInf(v1, v2).getReal(), 1.0e-12);

        Assert.assertEquals(5.0,
                            FieldVector3D.distanceInf(createVector( 1, -2, 3, 3), createVector(-4,  2, 0, 3)).getReal(),
                            1.0e-12);
        Assert.assertEquals(5.0,
                            FieldVector3D.distanceInf(createVector( 1, 3, -2, 3), createVector(-4, 0,  2, 3)).getReal(),
                            1.0e-12);
        Assert.assertEquals(5.0,
                            FieldVector3D.distanceInf(createVector(-2,  1, 3, 3), createVector( 2, -4, 0, 3)).getReal(),
                            1.0e-12);
        Assert.assertEquals(5.0,
                            FieldVector3D.distanceInf(createVector(-2, 3,  1, 3), createVector( 2, 0, -4, 3)).getReal(),
                            1.0e-12);
        Assert.assertEquals(5.0,
                            FieldVector3D.distanceInf(createVector(3, -2,  1, 3), createVector(0,  2, -4, 3)).getReal(),
                            1.0e-12);
        Assert.assertEquals(5.0,
                            FieldVector3D.distanceInf(createVector(3,  1, -2, 3), createVector(0, -4,  2, 3)).getReal(),
                            1.0e-12);

        Assert.assertEquals(5.0,
                            FieldVector3D.distanceInf(createVector( 1, -2, 3, 3), new Vector3D(-4,  2, 0)).getReal(),
                            1.0e-12);
        Assert.assertEquals(5.0,
                            FieldVector3D.distanceInf(createVector( 1, 3, -2, 3), new Vector3D(-4, 0,  2)).getReal(),
                            1.0e-12);
        Assert.assertEquals(5.0,
                            FieldVector3D.distanceInf(createVector(-2,  1, 3, 3), new Vector3D( 2, -4, 0)).getReal(),
                            1.0e-12);
        Assert.assertEquals(5.0,
                            FieldVector3D.distanceInf(createVector(-2, 3,  1, 3), new Vector3D( 2, 0, -4)).getReal(),
                            1.0e-12);
        Assert.assertEquals(5.0,
                            FieldVector3D.distanceInf(createVector(3, -2,  1, 3), new Vector3D(0,  2, -4)).getReal(),
                            1.0e-12);
        Assert.assertEquals(5.0,
                            FieldVector3D.distanceInf(createVector(3,  1, -2, 3), new Vector3D(0, -4,  2)).getReal(),
                            1.0e-12);

    }

    @Test
    public void testSubtract() {
        FieldVector3D<DerivativeStructure> v1 = createVector(1, 2, 3, 3);
        FieldVector3D<DerivativeStructure> v2 = createVector(-3, -2, -1, 3);
        v1 = v1.subtract(v2);
        checkVector(v1, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0);

        checkVector(v2.subtract(v1), -7, -6, -5, 1, 0, 0, 0, 1, 0, 0, 0, 1);
        checkVector(v2.subtract(new Vector3D(4, 4, 4)), -7, -6, -5, 1, 0, 0, 0, 1, 0, 0, 0, 1);
        checkVector(v2.subtract(3, v1), -15, -14, -13, 1, 0, 0, 0, 1, 0, 0, 0, 1);
        checkVector(v2.subtract(3, new Vector3D(4, 4, 4)), -15, -14, -13, 1, 0, 0, 0, 1, 0, 0, 0, 1);
        checkVector(v2.subtract(new DerivativeStructure(3, 1, 2, 3), new Vector3D(4, 4, 4)),
                    -15, -14, -13, 1, 0, -4, 0, 1, -4, 0, 0, -3);

        checkVector(createVector(1, 2, 3, 4).subtract(new DerivativeStructure(4, 1, 3, 5.0),
                                                      createVector(3, -2, 1, 4)),
                    -14, 12, -2,
                     -4,  0,  0, -3,
                      0, -4,  0,  2,
                      0,  0, -4, -1);

    }

    @Test
    public void testAdd() {
        FieldVector3D<DerivativeStructure> v1 = createVector(1, 2, 3, 3);
        FieldVector3D<DerivativeStructure> v2 = createVector(-3, -2, -1, 3);
        v1 = v1.add(v2);
        checkVector(v1, -2, 0, 2, 2, 0, 0, 0, 2, 0, 0, 0, 2);

        checkVector(v2.add(v1), -5, -2, 1, 3, 0, 0, 0, 3, 0, 0, 0, 3);
        checkVector(v2.add(new Vector3D(-2, 0, 2)), -5, -2, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1);
        checkVector(v2.add(3, v1), -9, -2, 5, 7, 0, 0, 0, 7, 0, 0, 0, 7);
        checkVector(v2.add(3, new Vector3D(-2, 0, 2)), -9, -2, 5, 1, 0, 0, 0, 1, 0, 0, 0, 1);
        checkVector(v2.add(new DerivativeStructure(3, 1, 2, 3), new Vector3D(-2, 0, 2)),
                    -9, -2, 5, 1, 0, -2, 0, 1, 0, 0, 0, 3);

        checkVector(createVector(1, 2, 3, 4).add(new DerivativeStructure(4, 1, 3, 5.0),
                                                 createVector(3, -2, 1, 4)),
                    16, -8,  8,
                     6,  0,  0,  3,
                     0,  6,  0, -2,
                     0,  0,  6,  1);

    }

    @Test
    public void testScalarProduct() {
        FieldVector3D<DerivativeStructure> v = createVector(1, 2, 3, 3);
        v = v.scalarMultiply(3);
        checkVector(v, 3, 6, 9);

        checkVector(v.scalarMultiply(0.5), 1.5, 3, 4.5);
    }

    @Test
    public void testVectorialProducts() {
        FieldVector3D<DerivativeStructure> v1 = createVector(2, 1, -4, 3);
        FieldVector3D<DerivativeStructure> v2 = createVector(3, 1, -1, 3);

        Assert.assertTrue(FastMath.abs(FieldVector3D.dotProduct(v1, v2).getReal() - 11) < 1.0e-12);
        Assert.assertTrue(FastMath.abs(FieldVector3D.dotProduct(v1, v2.toVector3D()).getReal() - 11) < 1.0e-12);
        Assert.assertTrue(FastMath.abs(FieldVector3D.dotProduct(v1.toVector3D(), v2).getReal() - 11) < 1.0e-12);

        FieldVector3D<DerivativeStructure> v3 = FieldVector3D.crossProduct(v1, v2);
        checkVector(v3, 3, -10, -1);
        Assert.assertTrue(FastMath.abs(FieldVector3D.dotProduct(v1, v3).getReal()) < 1.0e-12);
        Assert.assertTrue(FastMath.abs(FieldVector3D.dotProduct(v2, v3).getReal()) < 1.0e-12);

        v3 = FieldVector3D.crossProduct(v1, v2.toVector3D());
        checkVector(v3, 3, -10, -1);
        Assert.assertTrue(FastMath.abs(FieldVector3D.dotProduct(v1, v3).getReal()) < 1.0e-12);
        Assert.assertTrue(FastMath.abs(FieldVector3D.dotProduct(v2, v3).getReal()) < 1.0e-12);

        v3 = FieldVector3D.crossProduct(v1.toVector3D(), v2);
        checkVector(v3, 3, -10, -1);
        Assert.assertTrue(FastMath.abs(FieldVector3D.dotProduct(v1, v3).getReal()) < 1.0e-12);
        Assert.assertTrue(FastMath.abs(FieldVector3D.dotProduct(v2, v3).getReal()) < 1.0e-12);

    }

    @Test
    public void testCrossProductCancellation() {
        FieldVector3D<DerivativeStructure> v1 = createVector(9070467121.0, 4535233560.0, 1, 3);
        FieldVector3D<DerivativeStructure> v2 = createVector(9070467123.0, 4535233561.0, 1, 3);
        checkVector(FieldVector3D.crossProduct(v1, v2), -1, 2, 1);

        double scale    = FastMath.scalb(1.0, 100);
        FieldVector3D<DerivativeStructure> big1   = new FieldVector3D<DerivativeStructure>(scale, v1);
        FieldVector3D<DerivativeStructure> small2 = new FieldVector3D<DerivativeStructure>(1 / scale, v2);
        checkVector(FieldVector3D.crossProduct(big1, small2), -1, 2, 1);

    }

    @Test
    public void testAngular() {
        Assert.assertEquals(0,           createVector(1, 0, 0, 3).getAlpha().getReal(), 1.0e-10);
        Assert.assertEquals(0,           createVector(1, 0, 0, 3).getDelta().getReal(), 1.0e-10);
        Assert.assertEquals(FastMath.PI / 2, createVector(0, 1, 0, 3).getAlpha().getReal(), 1.0e-10);
        Assert.assertEquals(0,           createVector(0, 1, 0, 3).getDelta().getReal(), 1.0e-10);
        Assert.assertEquals(FastMath.PI / 2, createVector(0, 0, 1, 3).getDelta().getReal(), 1.0e-10);

        FieldVector3D<DerivativeStructure> u = createVector(-1, 1, -1, 3);
        Assert.assertEquals(3 * FastMath.PI /4, u.getAlpha().getReal(), 1.0e-10);
        Assert.assertEquals(-1.0 / FastMath.sqrt(3), u.getDelta().sin().getReal(), 1.0e-10);
    }

    @Test
    public void testAngularSeparation() throws MathArithmeticException {
        FieldVector3D<DerivativeStructure> v1 = createVector(2, -1, 4, 3);

        FieldVector3D<DerivativeStructure>  k = v1.normalize();
        FieldVector3D<DerivativeStructure>  i = k.orthogonal();
        FieldVector3D<DerivativeStructure> v2 = k.scalarMultiply(FastMath.cos(1.2)).add(i.scalarMultiply(FastMath.sin(1.2)));

        Assert.assertTrue(FastMath.abs(FieldVector3D.angle(v1, v2).getReal() - 1.2) < 1.0e-12);
        Assert.assertTrue(FastMath.abs(FieldVector3D.angle(v1, v2.toVector3D()).getReal() - 1.2) < 1.0e-12);
        Assert.assertTrue(FastMath.abs(FieldVector3D.angle(v1.toVector3D(), v2).getReal() - 1.2) < 1.0e-12);

        try {
            FieldVector3D.angle(v1, Vector3D.ZERO);
            Assert.fail("an exception should have been thrown");
        } catch (MathArithmeticException mae) {
            // expected
        }
        Assert.assertEquals(0.0, FieldVector3D.angle(v1, v1.toVector3D()).getReal(), 1.0e-15);
        Assert.assertEquals(FastMath.PI, FieldVector3D.angle(v1, v1.negate().toVector3D()).getReal(), 1.0e-15);

    }

    @Test
    public void testNormalize() throws MathArithmeticException {
        Assert.assertEquals(1.0, createVector(5, -4, 2, 3).normalize().getNorm().getReal(), 1.0e-12);
        try {
            createVector(0, 0, 0, 3).normalize();
            Assert.fail("an exception should have been thrown");
        } catch (MathArithmeticException ae) {
            // expected behavior
        }
    }

    @Test
    public void testNegate() {
        checkVector(createVector(0.1, 2.5, 1.3, 3).negate(),
                    -0.1, -2.5, -1.3, -1, 0, 0, 0, -1, 0, 0, 0, -1);
    }

    @Test
    public void testOrthogonal() throws MathArithmeticException {
        FieldVector3D<DerivativeStructure> v1 = createVector(0.1, 2.5, 1.3, 3);
        Assert.assertEquals(0.0, FieldVector3D.dotProduct(v1, v1.orthogonal()).getReal(), 1.0e-12);
        FieldVector3D<DerivativeStructure> v2 = createVector(2.3, -0.003, 7.6, 3);
        Assert.assertEquals(0.0, FieldVector3D.dotProduct(v2, v2.orthogonal()).getReal(), 1.0e-12);
        FieldVector3D<DerivativeStructure> v3 = createVector(-1.7, 1.4, 0.2, 3);
        Assert.assertEquals(0.0, FieldVector3D.dotProduct(v3, v3.orthogonal()).getReal(), 1.0e-12);
        FieldVector3D<DerivativeStructure> v4 = createVector(4.2, 0.1, -1.8, 3);
        Assert.assertEquals(0.0, FieldVector3D.dotProduct(v4, v4.orthogonal()).getReal(), 1.0e-12);
        try {
            createVector(0, 0, 0, 3).orthogonal();
            Assert.fail("an exception should have been thrown");
        } catch (MathArithmeticException ae) {
            // expected behavior
        }
    }

    @Test
    public void testAngle() throws MathArithmeticException {
        Assert.assertEquals(0.22572612855273393616,
                            FieldVector3D.angle(createVector(1, 2, 3, 3), createVector(4, 5, 6, 3)).getReal(),
                            1.0e-12);
        Assert.assertEquals(7.98595620686106654517199e-8,
                            FieldVector3D.angle(createVector(1, 2, 3, 3), createVector(2, 4, 6.000001, 3)).getReal(),
                            1.0e-12);
        Assert.assertEquals(3.14159257373023116985197793156,
                            FieldVector3D.angle(createVector(1, 2, 3, 3), createVector(-2, -4, -6.000001, 3)).getReal(),
                            1.0e-12);
        try {
            FieldVector3D.angle(createVector(0, 0, 0, 3), createVector(1, 0, 0, 3));
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
        FieldVector3D<DerivativeStructure> u1 = createVector(-1321008684645961.0 /  268435456.0,
                                   -5774608829631843.0 /  268435456.0,
                                   -7645843051051357.0 / 8589934592.0, 3);
        FieldVector3D<DerivativeStructure> u2 = createVector(-5712344449280879.0 /    2097152.0,
                                   -4550117129121957.0 /    2097152.0,
                                    8846951984510141.0 /     131072.0, 3);
        DerivativeStructure sNaive = u1.getX().multiply(u2.getX()).add(u1.getY().multiply(u2.getY())).add(u1.getZ().multiply(u2.getZ()));
        DerivativeStructure sAccurate = FieldVector3D.dotProduct(u1, u2);
        Assert.assertEquals(0.0, sNaive.getReal(), 1.0e-30);
        Assert.assertEquals(-2088690039198397.0 / 1125899906842624.0, sAccurate.getReal(), 1.0e-15);
    }

    @Test
    public void testDotProduct() {
        // we compare accurate versus naive dot product implementations
        // on regular vectors (i.e. not extreme cases like in the previous test)
        Well1024a random = new Well1024a(553267312521321234l);
        for (int i = 0; i < 10000; ++i) {
            double ux = 10000 * random.nextDouble();
            double uy = 10000 * random.nextDouble();
            double uz = 10000 * random.nextDouble();
            double vx = 10000 * random.nextDouble();
            double vy = 10000 * random.nextDouble();
            double vz = 10000 * random.nextDouble();
            double sNaive = ux * vx + uy * vy + uz * vz;

            FieldVector3D<DerivativeStructure> uds = createVector(ux, uy, uz, 3);
            FieldVector3D<DerivativeStructure> vds = createVector(vx, vy, vz, 3);
            Vector3D v = new Vector3D(vx, vy, vz);

            DerivativeStructure sAccurate = FieldVector3D.dotProduct(uds, vds);
            Assert.assertEquals(sNaive, sAccurate.getReal(), 2.5e-16 * sNaive);
            Assert.assertEquals(ux + vx, sAccurate.getPartialDerivative(1, 0, 0), 2.5e-16 * sNaive);
            Assert.assertEquals(uy + vy, sAccurate.getPartialDerivative(0, 1, 0), 2.5e-16 * sNaive);
            Assert.assertEquals(uz + vz, sAccurate.getPartialDerivative(0, 0, 1), 2.5e-16 * sNaive);

            sAccurate = FieldVector3D.dotProduct(uds, v);
            Assert.assertEquals(sNaive, sAccurate.getReal(), 2.5e-16 * sNaive);
            Assert.assertEquals(vx, sAccurate.getPartialDerivative(1, 0, 0), 2.5e-16 * sNaive);
            Assert.assertEquals(vy, sAccurate.getPartialDerivative(0, 1, 0), 2.5e-16 * sNaive);
            Assert.assertEquals(vz, sAccurate.getPartialDerivative(0, 0, 1), 2.5e-16 * sNaive);

        }
    }

    @Test
    public void testAccurateCrossProduct() {
        // the vectors u1 and u2 are nearly but not exactly anti-parallel
        // (7.31e-16 degrees from 180 degrees) naive cross product (i.e.
        // computing u1.x * u2.x + u1.y * u2.y + u1.z * u2.z
        // leads to a result of   [0.0009765, -0.0001220, -0.0039062],
        // instead of the correct [0.0006913, -0.0001254, -0.0007909]
        final FieldVector3D<DerivativeStructure> u1 = createVector(-1321008684645961.0 /   268435456.0,
                                         -5774608829631843.0 /   268435456.0,
                                         -7645843051051357.0 /  8589934592.0, 3);
        final FieldVector3D<DerivativeStructure> u2 = createVector( 1796571811118507.0 /  2147483648.0,
                                          7853468008299307.0 /  2147483648.0,
                                          2599586637357461.0 / 17179869184.0, 3);
        final FieldVector3D<DerivativeStructure> u3 = createVector(12753243807587107.0 / 18446744073709551616.0,
                                         -2313766922703915.0 / 18446744073709551616.0,
                                          -227970081415313.0 /   288230376151711744.0, 3);
        FieldVector3D<DerivativeStructure> cNaive = new FieldVector3D<DerivativeStructure>(u1.getY().multiply(u2.getZ()).subtract(u1.getZ().multiply(u2.getY())),
                                       u1.getZ().multiply(u2.getX()).subtract(u1.getX().multiply(u2.getZ())),
                                       u1.getX().multiply(u2.getY()).subtract(u1.getY().multiply(u2.getX())));
        FieldVector3D<DerivativeStructure> cAccurate = FieldVector3D.crossProduct(u1, u2);
        Assert.assertTrue(FieldVector3D.distance(u3, cNaive).getReal() > 2.9 * u3.getNorm().getReal());
        Assert.assertEquals(0.0, FieldVector3D.distance(u3, cAccurate).getReal(), 1.0e-30 * cAccurate.getNorm().getReal());
    }

    @Test
    public void testCrossProduct() {
        // we compare accurate versus naive cross product implementations
        // on regular vectors (i.e. not extreme cases like in the previous test)
        Well1024a random = new Well1024a(885362227452043214l);
        for (int i = 0; i < 10000; ++i) {
            double ux = random.nextDouble();
            double uy = random.nextDouble();
            double uz = random.nextDouble();
            double vx = random.nextDouble();
            double vy = random.nextDouble();
            double vz = random.nextDouble();
            Vector3D cNaive = new Vector3D(uy * vz - uz * vy, uz * vx - ux * vz, ux * vy - uy * vx);

            FieldVector3D<DerivativeStructure> uds = createVector(ux, uy, uz, 3);
            FieldVector3D<DerivativeStructure> vds = createVector(vx, vy, vz, 3);
            Vector3D v = new Vector3D(vx, vy, vz);

            checkVector(FieldVector3D.crossProduct(uds, vds),
                        cNaive.getX(), cNaive.getY(), cNaive.getZ(),
                        0, vz - uz, uy - vy,
                        uz - vz, 0, vx - ux,
                        vy - uy, ux - vx, 0);

            checkVector(FieldVector3D.crossProduct(uds, v),
                        cNaive.getX(), cNaive.getY(), cNaive.getZ(),
                          0,  vz, -vy,
                        -vz,   0,  vx,
                         vy, -vx,   0);

        }
    }

    private FieldVector3D<DerivativeStructure> createVector(double x, double y, double z, int params) {
        return new FieldVector3D<DerivativeStructure>(new DerivativeStructure(params, 1, 0, x),
                              new DerivativeStructure(params, 1, 1, y),
                              new DerivativeStructure(params, 1, 2, z));
    }

    private void checkVector(FieldVector3D<DerivativeStructure> v, double x, double y, double z) {
        Assert.assertEquals(x, v.getX().getReal(), 1.0e-12);
        Assert.assertEquals(y, v.getY().getReal(), 1.0e-12);
        Assert.assertEquals(z, v.getZ().getReal(), 1.0e-12);
    }

    private void checkVector(FieldVector3D<DerivativeStructure> v, double x, double y, double z,
                             double dxdx, double dxdy, double dxdz,
                             double dydx, double dydy, double dydz,
                             double dzdx, double dzdy, double dzdz) {
        Assert.assertEquals(x, v.getX().getReal(), 1.0e-12);
        Assert.assertEquals(y, v.getY().getReal(), 1.0e-12);
        Assert.assertEquals(z, v.getZ().getReal(), 1.0e-12);
        Assert.assertEquals(dxdx, v.getX().getPartialDerivative(1, 0, 0), 1.0e-12);
        Assert.assertEquals(dxdy, v.getX().getPartialDerivative(0, 1, 0), 1.0e-12);
        Assert.assertEquals(dxdz, v.getX().getPartialDerivative(0, 0, 1), 1.0e-12);
        Assert.assertEquals(dydx, v.getY().getPartialDerivative(1, 0, 0), 1.0e-12);
        Assert.assertEquals(dydy, v.getY().getPartialDerivative(0, 1, 0), 1.0e-12);
        Assert.assertEquals(dydz, v.getY().getPartialDerivative(0, 0, 1), 1.0e-12);
        Assert.assertEquals(dzdx, v.getZ().getPartialDerivative(1, 0, 0), 1.0e-12);
        Assert.assertEquals(dzdy, v.getZ().getPartialDerivative(0, 1, 0), 1.0e-12);
        Assert.assertEquals(dzdz, v.getZ().getPartialDerivative(0, 0, 1), 1.0e-12);
    }

    private void checkVector(FieldVector3D<DerivativeStructure> v, double x, double y, double z,
                             double dxdx, double dxdy, double dxdz, double dxdt,
                             double dydx, double dydy, double dydz, double dydt,
                             double dzdx, double dzdy, double dzdz, double dzdt) {
        Assert.assertEquals(x, v.getX().getReal(), 1.0e-12);
        Assert.assertEquals(y, v.getY().getReal(), 1.0e-12);
        Assert.assertEquals(z, v.getZ().getReal(), 1.0e-12);
        Assert.assertEquals(dxdx, v.getX().getPartialDerivative(1, 0, 0, 0), 1.0e-12);
        Assert.assertEquals(dxdy, v.getX().getPartialDerivative(0, 1, 0, 0), 1.0e-12);
        Assert.assertEquals(dxdz, v.getX().getPartialDerivative(0, 0, 1, 0), 1.0e-12);
        Assert.assertEquals(dxdt, v.getX().getPartialDerivative(0, 0, 0, 1), 1.0e-12);
        Assert.assertEquals(dydx, v.getY().getPartialDerivative(1, 0, 0, 0), 1.0e-12);
        Assert.assertEquals(dydy, v.getY().getPartialDerivative(0, 1, 0, 0), 1.0e-12);
        Assert.assertEquals(dydz, v.getY().getPartialDerivative(0, 0, 1, 0), 1.0e-12);
        Assert.assertEquals(dydt, v.getY().getPartialDerivative(0, 0, 0, 1), 1.0e-12);
        Assert.assertEquals(dzdx, v.getZ().getPartialDerivative(1, 0, 0, 0), 1.0e-12);
        Assert.assertEquals(dzdy, v.getZ().getPartialDerivative(0, 1, 0, 0), 1.0e-12);
        Assert.assertEquals(dzdz, v.getZ().getPartialDerivative(0, 0, 1, 0), 1.0e-12);
        Assert.assertEquals(dzdt, v.getZ().getPartialDerivative(0, 0, 0, 1), 1.0e-12);
    }

}
