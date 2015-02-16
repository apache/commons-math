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

import org.apache.commons.math4.dfp.Dfp;
import org.apache.commons.math4.dfp.DfpField;
import org.apache.commons.math4.exception.MathArithmeticException;
import org.apache.commons.math4.exception.MathIllegalArgumentException;
import org.apache.commons.math4.geometry.euclidean.threed.CardanEulerSingularityException;
import org.apache.commons.math4.geometry.euclidean.threed.FieldRotation;
import org.apache.commons.math4.geometry.euclidean.threed.FieldVector3D;
import org.apache.commons.math4.geometry.euclidean.threed.NotARotationMatrixException;
import org.apache.commons.math4.geometry.euclidean.threed.Rotation;
import org.apache.commons.math4.geometry.euclidean.threed.RotationOrder;
import org.apache.commons.math4.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math4.random.UnitSphereRandomVectorGenerator;
import org.apache.commons.math4.random.Well1024a;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.math4.util.MathUtils;
import org.junit.Assert;
import org.junit.Test;


public class FieldRotationDfpTest {

    @Test
    public void testIdentity() {

        FieldRotation<Dfp> r = createRotation(1, 0, 0, 0, false);
        checkVector(r.applyTo(createVector(1, 0, 0)), createVector(1, 0, 0));
        checkVector(r.applyTo(createVector(0, 1, 0)), createVector(0, 1, 0));
        checkVector(r.applyTo(createVector(0, 0, 1)), createVector(0, 0, 1));
        checkAngle(r.getAngle(), 0);

        r = createRotation(-1, 0, 0, 0, false);
        checkVector(r.applyTo(createVector(1, 0, 0)), createVector(1, 0, 0));
        checkVector(r.applyTo(createVector(0, 1, 0)), createVector(0, 1, 0));
        checkVector(r.applyTo(createVector(0, 0, 1)), createVector(0, 0, 1));
        checkAngle(r.getAngle(), 0);

        r = createRotation(42, 0, 0, 0, true);
        checkVector(r.applyTo(createVector(1, 0, 0)), createVector(1, 0, 0));
        checkVector(r.applyTo(createVector(0, 1, 0)), createVector(0, 1, 0));
        checkVector(r.applyTo(createVector(0, 0, 1)), createVector(0, 0, 1));
        checkAngle(r.getAngle(), 0);

    }

    @Test
    public void testAxisAngle() throws MathIllegalArgumentException {

        FieldRotation<Dfp> r = new FieldRotation<Dfp>(createAxis(10, 10, 10), createAngle(2 * FastMath.PI / 3));
        checkVector(r.applyTo(createVector(1, 0, 0)), createVector(0, 1, 0));
        checkVector(r.applyTo(createVector(0, 1, 0)), createVector(0, 0, 1));
        checkVector(r.applyTo(createVector(0, 0, 1)), createVector(1, 0, 0));
        double s = 1 / FastMath.sqrt(3);
        checkVector(r.getAxis(), createVector(s, s, s));
        checkAngle(r.getAngle(), 2 * FastMath.PI / 3);

        try {
            new FieldRotation<Dfp>(createAxis(0, 0, 0), createAngle(2 * FastMath.PI / 3));
            Assert.fail("an exception should have been thrown");
        } catch (MathIllegalArgumentException e) {
        }

        r = new FieldRotation<Dfp>(createAxis(0, 0, 1), createAngle(1.5 * FastMath.PI));
        checkVector(r.getAxis(), createVector(0, 0, -1));
        checkAngle(r.getAngle(), 0.5 * FastMath.PI);

        r = new FieldRotation<Dfp>(createAxis(0, 1, 0), createAngle(FastMath.PI));
        checkVector(r.getAxis(), createVector(0, 1, 0));
        checkAngle(r.getAngle(), FastMath.PI);

        checkVector(createRotation(1, 0, 0, 0, false).getAxis(), createVector(1, 0, 0));

    }

    @Test
    public void testRevert() {
        double a = 0.001;
        double b = 0.36;
        double c = 0.48;
        double d = 0.8;
        FieldRotation<Dfp> r = createRotation(a, b, c, d, true);
        FieldRotation<Dfp> reverted = r.revert();
        FieldRotation<Dfp> rrT = r.applyTo(reverted);
        checkRotationDS(rrT, 1, 0, 0, 0);
        FieldRotation<Dfp> rTr = reverted.applyTo(r);
        checkRotationDS(rTr, 1, 0, 0, 0);
        Assert.assertEquals(r.getAngle().getReal(), reverted.getAngle().getReal(), 1.0e-15);
        Assert.assertEquals(-1, FieldVector3D.dotProduct(r.getAxis(), reverted.getAxis()).getReal(), 1.0e-15);
    }

    @Test
    public void testVectorOnePair() throws MathArithmeticException {

        FieldVector3D<Dfp> u = createVector(3, 2, 1);
        FieldVector3D<Dfp> v = createVector(-4, 2, 2);
        FieldRotation<Dfp> r = new FieldRotation<Dfp>(u, v);
        checkVector(r.applyTo(u.scalarMultiply(v.getNorm())), v.scalarMultiply(u.getNorm()));

        checkAngle(new FieldRotation<Dfp>(u, u.negate()).getAngle(), FastMath.PI);

        try {
            new FieldRotation<Dfp>(u, createVector(0, 0, 0));
            Assert.fail("an exception should have been thrown");
        } catch (MathArithmeticException e) {
            // expected behavior
        }

    }

    @Test
    public void testVectorTwoPairs() throws MathArithmeticException {

        FieldVector3D<Dfp> u1 = createVector(3, 0, 0);
        FieldVector3D<Dfp> u2 = createVector(0, 5, 0);
        FieldVector3D<Dfp> v1 = createVector(0, 0, 2);
        FieldVector3D<Dfp> v2 = createVector(-2, 0, 2);
        FieldRotation<Dfp> r = new FieldRotation<Dfp>(u1, u2, v1, v2);
        checkVector(r.applyTo(createVector(1, 0, 0)), createVector(0, 0, 1));
        checkVector(r.applyTo(createVector(0, 1, 0)), createVector(-1, 0, 0));

        r = new FieldRotation<Dfp>(u1, u2, u1.negate(), u2.negate());
        FieldVector3D<Dfp> axis = r.getAxis();
        if (FieldVector3D.dotProduct(axis, createVector(0, 0, 1)).getReal() > 0) {
            checkVector(axis, createVector(0, 0, 1));
        } else {
            checkVector(axis, createVector(0, 0, -1));
        }
        checkAngle(r.getAngle(), FastMath.PI);

        double sqrt = FastMath.sqrt(2) / 2;
        r = new FieldRotation<Dfp>(createVector(1, 0, 0),  createVector(0, 1, 0),
                           createVector(0.5, 0.5,  sqrt),
                           createVector(0.5, 0.5, -sqrt));
        checkRotationDS(r, sqrt, 0.5, 0.5, 0);

        r = new FieldRotation<Dfp>(u1, u2, u1, FieldVector3D.crossProduct(u1, u2));
        checkRotationDS(r, sqrt, -sqrt, 0, 0);

        checkRotationDS(new FieldRotation<Dfp>(u1, u2, u1, u2), 1, 0, 0, 0);

        try {
            new FieldRotation<Dfp>(u1, u2, createVector(0, 0, 0), v2);
            Assert.fail("an exception should have been thrown");
        } catch (MathArithmeticException e) {
            // expected behavior
        }

    }

    @Test
    public void testMatrix()
            throws NotARotationMatrixException {

        try {
            createRotation(new double[][] {
                { 0.0, 1.0, 0.0 },
                { 1.0, 0.0, 0.0 }
            }, 1.0e-7);
            Assert.fail("Expecting NotARotationMatrixException");
        } catch (NotARotationMatrixException nrme) {
            // expected behavior
        }

        try {
            createRotation(new double[][] {
                {  0.445888,  0.797184, -0.407040 },
                {  0.821760, -0.184320,  0.539200 },
                { -0.354816,  0.574912,  0.737280 }
            }, 1.0e-7);
            Assert.fail("Expecting NotARotationMatrixException");
        } catch (NotARotationMatrixException nrme) {
            // expected behavior
        }

        try {
            createRotation(new double[][] {
                {  0.4,  0.8, -0.4 },
                { -0.4,  0.6,  0.7 },
                {  0.8, -0.2,  0.5 }
            }, 1.0e-15);
            Assert.fail("Expecting NotARotationMatrixException");
        } catch (NotARotationMatrixException nrme) {
            // expected behavior
        }

        checkRotationDS(createRotation(new double[][] {
            {  0.445888,  0.797184, -0.407040 },
            { -0.354816,  0.574912,  0.737280 },
            {  0.821760, -0.184320,  0.539200 }
        }, 1.0e-10),
        0.8, 0.288, 0.384, 0.36);

        checkRotationDS(createRotation(new double[][] {
            {  0.539200,  0.737280,  0.407040 },
            {  0.184320, -0.574912,  0.797184 },
            {  0.821760, -0.354816, -0.445888 }
        }, 1.0e-10),
        0.36, 0.8, 0.288, 0.384);

        checkRotationDS(createRotation(new double[][] {
            { -0.445888,  0.797184, -0.407040 },
            {  0.354816,  0.574912,  0.737280 },
            {  0.821760,  0.184320, -0.539200 }
        }, 1.0e-10),
        0.384, 0.36, 0.8, 0.288);

        checkRotationDS(createRotation(new double[][] {
            { -0.539200,  0.737280,  0.407040 },
            { -0.184320, -0.574912,  0.797184 },
            {  0.821760,  0.354816,  0.445888 }
        }, 1.0e-10),
        0.288, 0.384, 0.36, 0.8);

        double[][] m1 = { { 0.0, 1.0, 0.0 },
            { 0.0, 0.0, 1.0 },
            { 1.0, 0.0, 0.0 } };
        FieldRotation<Dfp> r = createRotation(m1, 1.0e-7);
        checkVector(r.applyTo(createVector(1, 0, 0)), createVector(0, 0, 1));
        checkVector(r.applyTo(createVector(0, 1, 0)), createVector(1, 0, 0));
        checkVector(r.applyTo(createVector(0, 0, 1)), createVector(0, 1, 0));

        double[][] m2 = { { 0.83203, -0.55012, -0.07139 },
            { 0.48293,  0.78164, -0.39474 },
            { 0.27296,  0.29396,  0.91602 } };
        r = createRotation(m2, 1.0e-12);

        Dfp[][] m3 = r.getMatrix();
        double d00 = m2[0][0] - m3[0][0].getReal();
        double d01 = m2[0][1] - m3[0][1].getReal();
        double d02 = m2[0][2] - m3[0][2].getReal();
        double d10 = m2[1][0] - m3[1][0].getReal();
        double d11 = m2[1][1] - m3[1][1].getReal();
        double d12 = m2[1][2] - m3[1][2].getReal();
        double d20 = m2[2][0] - m3[2][0].getReal();
        double d21 = m2[2][1] - m3[2][1].getReal();
        double d22 = m2[2][2] - m3[2][2].getReal();

        Assert.assertTrue(FastMath.abs(d00) < 6.0e-6);
        Assert.assertTrue(FastMath.abs(d01) < 6.0e-6);
        Assert.assertTrue(FastMath.abs(d02) < 6.0e-6);
        Assert.assertTrue(FastMath.abs(d10) < 6.0e-6);
        Assert.assertTrue(FastMath.abs(d11) < 6.0e-6);
        Assert.assertTrue(FastMath.abs(d12) < 6.0e-6);
        Assert.assertTrue(FastMath.abs(d20) < 6.0e-6);
        Assert.assertTrue(FastMath.abs(d21) < 6.0e-6);
        Assert.assertTrue(FastMath.abs(d22) < 6.0e-6);

        Assert.assertTrue(FastMath.abs(d00) > 4.0e-7);
        Assert.assertTrue(FastMath.abs(d01) > 4.0e-7);
        Assert.assertTrue(FastMath.abs(d02) > 4.0e-7);
        Assert.assertTrue(FastMath.abs(d10) > 4.0e-7);
        Assert.assertTrue(FastMath.abs(d11) > 4.0e-7);
        Assert.assertTrue(FastMath.abs(d12) > 4.0e-7);
        Assert.assertTrue(FastMath.abs(d20) > 4.0e-7);
        Assert.assertTrue(FastMath.abs(d21) > 4.0e-7);
        Assert.assertTrue(FastMath.abs(d22) > 4.0e-7);

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                double m3tm3 = m3[i][0].getReal() * m3[j][0].getReal() +
                               m3[i][1].getReal() * m3[j][1].getReal() +
                               m3[i][2].getReal() * m3[j][2].getReal();
                if (i == j) {
                    Assert.assertTrue(FastMath.abs(m3tm3 - 1.0) < 1.0e-10);
                } else {
                    Assert.assertTrue(FastMath.abs(m3tm3) < 1.0e-10);
                }
            }
        }

        checkVector(r.applyTo(createVector(1, 0, 0)),
                    new FieldVector3D<Dfp>(m3[0][0], m3[1][0], m3[2][0]));
        checkVector(r.applyTo(createVector(0, 1, 0)),
                    new FieldVector3D<Dfp>(m3[0][1], m3[1][1], m3[2][1]));
        checkVector(r.applyTo(createVector(0, 0, 1)),
                    new FieldVector3D<Dfp>(m3[0][2], m3[1][2], m3[2][2]));

        double[][] m4 = { { 1.0,  0.0,  0.0 },
            { 0.0, -1.0,  0.0 },
            { 0.0,  0.0, -1.0 } };
        r = createRotation(m4, 1.0e-7);
        checkAngle(r.getAngle(), FastMath.PI);

        try {
            double[][] m5 = { { 0.0, 0.0, 1.0 },
                { 0.0, 1.0, 0.0 },
                { 1.0, 0.0, 0.0 } };
            r = createRotation(m5, 1.0e-7);
            Assert.fail("got " + r + ", should have caught an exception");
        } catch (NotARotationMatrixException e) {
            // expected
        }

    }

    @Test
    public void testAngles()
            throws CardanEulerSingularityException {

        DfpField field = new DfpField(15);

        RotationOrder[] CardanOrders = {
            RotationOrder.XYZ, RotationOrder.XZY, RotationOrder.YXZ,
            RotationOrder.YZX, RotationOrder.ZXY, RotationOrder.ZYX
        };

        for (int i = 0; i < CardanOrders.length; ++i) {
            for (double alpha1 = 0.1; alpha1 < 6.2; alpha1 += 2.0) {
                for (double alpha2 = -1.55; alpha2 < 1.55; alpha2 += 0.8) {
                    for (double alpha3 = 0.1; alpha3 < 6.2; alpha3 += 2.0) {
                        FieldRotation<Dfp> r = new FieldRotation<Dfp>(CardanOrders[i],
                                                                      field.newDfp(alpha1),
                                                                      field.newDfp(alpha2),
                                                                      field.newDfp(alpha3));
                        Dfp[] angles = r.getAngles(CardanOrders[i]);
                        checkAngle(angles[0], alpha1);
                        checkAngle(angles[1], alpha2);
                        checkAngle(angles[2], alpha3);
                    }
                }
            }
        }

        RotationOrder[] EulerOrders = {
            RotationOrder.XYX, RotationOrder.XZX, RotationOrder.YXY,
            RotationOrder.YZY, RotationOrder.ZXZ, RotationOrder.ZYZ
        };

        for (int i = 0; i < EulerOrders.length; ++i) {
            for (double alpha1 = 0.1; alpha1 < 6.2; alpha1 += 2.0) {
                for (double alpha2 = 0.05; alpha2 < 3.1; alpha2 += 0.8) {
                    for (double alpha3 = 0.1; alpha3 < 6.2; alpha3 += 2.0) {
                        FieldRotation<Dfp> r = new FieldRotation<Dfp>(EulerOrders[i],
                                                                      field.newDfp(alpha1),
                                                                      field.newDfp(alpha2),
                                                                      field.newDfp(alpha3));
                        Dfp[] angles = r.getAngles(EulerOrders[i]);
                        checkAngle(angles[0], alpha1);
                        checkAngle(angles[1], alpha2);
                        checkAngle(angles[2], alpha3);
                    }
                }
            }
        }

    }

    @Test
    public void testSingularities() {

        DfpField field = new DfpField(20);
        RotationOrder[] CardanOrders = {
            RotationOrder.XYZ, RotationOrder.XZY, RotationOrder.YXZ,
            RotationOrder.YZX, RotationOrder.ZXY, RotationOrder.ZYX
        };

        double[] singularCardanAngle = { FastMath.PI / 2, -FastMath.PI / 2 };
        for (int i = 0; i < CardanOrders.length; ++i) {
            for (int j = 0; j < singularCardanAngle.length; ++j) {
                FieldRotation<Dfp> r = new FieldRotation<Dfp>(CardanOrders[i],
                                                              field.newDfp(0.1),
                                                              field.newDfp(singularCardanAngle[j]),
                                                              field.newDfp(0.3));
                try {
                    r.getAngles(CardanOrders[i]);
                    Assert.fail("an exception should have been caught");
                } catch (CardanEulerSingularityException cese) {
                    // expected behavior
                }
            }
        }

        RotationOrder[] EulerOrders = {
            RotationOrder.XYX, RotationOrder.XZX, RotationOrder.YXY,
            RotationOrder.YZY, RotationOrder.ZXZ, RotationOrder.ZYZ
        };

        double[] singularEulerAngle = { 0, FastMath.PI };
        for (int i = 0; i < EulerOrders.length; ++i) {
            for (int j = 0; j < singularEulerAngle.length; ++j) {
                FieldRotation<Dfp> r = new FieldRotation<Dfp>(EulerOrders[i],
                                                              field.newDfp(0.1),
                                                              field.newDfp(singularEulerAngle[j]),
                                                              field.newDfp(0.3));
                try {
                    r.getAngles(EulerOrders[i]);
                    Assert.fail("an exception should have been caught");
                } catch (CardanEulerSingularityException cese) {
                    // expected behavior
                }
            }
        }


    }

    @Test
    public void testQuaternion() throws MathIllegalArgumentException {

        FieldRotation<Dfp> r1 = new FieldRotation<Dfp>(createVector(2, -3, 5), createAngle(1.7));
        double n = 23.5;
        FieldRotation<Dfp> r2 = new FieldRotation<Dfp>(r1.getQ0().multiply(n), r1.getQ1().multiply(n),
                                       r1.getQ2().multiply(n), r1.getQ3().multiply(n),
                                       true);
        for (double x = -0.9; x < 0.9; x += 0.2) {
            for (double y = -0.9; y < 0.9; y += 0.2) {
                for (double z = -0.9; z < 0.9; z += 0.2) {
                    FieldVector3D<Dfp> u = createVector(x, y, z);
                    checkVector(r2.applyTo(u), r1.applyTo(u));
                }
            }
        }

        r1 = createRotation(0.288,  0.384,  0.36,  0.8, false);
        checkRotationDS(r1,
                        -r1.getQ0().getReal(), -r1.getQ1().getReal(),
                        -r1.getQ2().getReal(), -r1.getQ3().getReal());

    }

    @Test
    public void testCompose() throws MathIllegalArgumentException {

        FieldRotation<Dfp> r1       = new FieldRotation<Dfp>(createVector(2, -3, 5), createAngle(1.7));
        FieldRotation<Dfp> r2       = new FieldRotation<Dfp>(createVector(-1, 3, 2), createAngle(0.3));
        FieldRotation<Dfp> r3       = r2.applyTo(r1);
        FieldRotation<Dfp> r3Double = r2.applyTo(new Rotation(r1.getQ0().getReal(),
                                                      r1.getQ1().getReal(),
                                                      r1.getQ2().getReal(),
                                                      r1.getQ3().getReal(),
                                                      false));

        for (double x = -0.9; x < 0.9; x += 0.2) {
            for (double y = -0.9; y < 0.9; y += 0.2) {
                for (double z = -0.9; z < 0.9; z += 0.2) {
                    FieldVector3D<Dfp> u = createVector(x, y, z);
                    checkVector(r2.applyTo(r1.applyTo(u)), r3.applyTo(u));
                    checkVector(r2.applyTo(r1.applyTo(u)), r3Double.applyTo(u));
                }
            }
        }

    }

    @Test
    public void testComposeInverse() throws MathIllegalArgumentException {

        FieldRotation<Dfp> r1 = new FieldRotation<Dfp>(createVector(2, -3, 5), createAngle(1.7));
        FieldRotation<Dfp> r2 = new FieldRotation<Dfp>(createVector(-1, 3, 2), createAngle(0.3));
        FieldRotation<Dfp> r3 = r2.applyInverseTo(r1);
        FieldRotation<Dfp> r3Double = r2.applyInverseTo(new Rotation(r1.getQ0().getReal(),
                                                             r1.getQ1().getReal(),
                                                             r1.getQ2().getReal(),
                                                             r1.getQ3().getReal(),
                                                             false));

        for (double x = -0.9; x < 0.9; x += 0.2) {
            for (double y = -0.9; y < 0.9; y += 0.2) {
                for (double z = -0.9; z < 0.9; z += 0.2) {
                    FieldVector3D<Dfp> u = createVector(x, y, z);
                    checkVector(r2.applyInverseTo(r1.applyTo(u)), r3.applyTo(u));
                    checkVector(r2.applyInverseTo(r1.applyTo(u)), r3Double.applyTo(u));
                }
            }
        }

    }

    @Test
    public void testDoubleVectors() throws MathIllegalArgumentException {

        Well1024a random = new Well1024a(0x180b41cfeeffaf67l);
        UnitSphereRandomVectorGenerator g = new UnitSphereRandomVectorGenerator(3, random);
        for (int i = 0; i < 10; ++i) {
            double[] unit = g.nextVector();
            FieldRotation<Dfp> r = new FieldRotation<Dfp>(createVector(unit[0], unit[1], unit[2]),
                                          createAngle(random.nextDouble()));

            for (double x = -0.9; x < 0.9; x += 0.4) {
                for (double y = -0.9; y < 0.9; y += 0.4) {
                    for (double z = -0.9; z < 0.9; z += 0.4) {
                        FieldVector3D<Dfp> uds   = createVector(x, y, z);
                        FieldVector3D<Dfp> ruds  = r.applyTo(uds);
                        FieldVector3D<Dfp> rIuds = r.applyInverseTo(uds);
                        Vector3D   u     = new Vector3D(x, y, z);
                        FieldVector3D<Dfp> ru    = r.applyTo(u);
                        FieldVector3D<Dfp> rIu   = r.applyInverseTo(u);
                        Dfp[] ruArray = new Dfp[3];
                        r.applyTo(new double[] { x, y, z}, ruArray);
                        Dfp[] rIuArray = new Dfp[3];
                        r.applyInverseTo(new double[] { x, y, z}, rIuArray);
                        checkVector(ruds, ru);
                        checkVector(ruds, new FieldVector3D<Dfp>(ruArray));
                        checkVector(rIuds, rIu);
                        checkVector(rIuds, new FieldVector3D<Dfp>(rIuArray));
                    }
                }
            }
        }

    }

    @Test
    public void testDoubleRotations() throws MathIllegalArgumentException {

        DfpField field = new DfpField(20);
        Well1024a random = new Well1024a(0x180b41cfeeffaf67l);
        UnitSphereRandomVectorGenerator g = new UnitSphereRandomVectorGenerator(3, random);
        for (int i = 0; i < 10; ++i) {
            double[] unit1 = g.nextVector();
            Rotation r1 = new Rotation(new Vector3D(unit1[0], unit1[1], unit1[2]),
                                      random.nextDouble());
            FieldRotation<Dfp> r1Prime = new FieldRotation<Dfp>(field.newDfp(r1.getQ0()),
                                                                field.newDfp(r1.getQ1()),
                                                                field.newDfp(r1.getQ2()),
                                                                field.newDfp(r1.getQ3()),
                                                                false);
            double[] unit2 = g.nextVector();
            FieldRotation<Dfp> r2 = new FieldRotation<Dfp>(createVector(unit2[0], unit2[1], unit2[2]),
                                           createAngle(random.nextDouble()));

            FieldRotation<Dfp> rA = FieldRotation.applyTo(r1, r2);
            FieldRotation<Dfp> rB = r1Prime.applyTo(r2);
            FieldRotation<Dfp> rC = FieldRotation.applyInverseTo(r1, r2);
            FieldRotation<Dfp> rD = r1Prime.applyInverseTo(r2);

            for (double x = -0.9; x < 0.9; x += 0.4) {
                for (double y = -0.9; y < 0.9; y += 0.4) {
                    for (double z = -0.9; z < 0.9; z += 0.4) {

                        FieldVector3D<Dfp> uds   = createVector(x, y, z);
                        checkVector(r1Prime.applyTo(uds), FieldRotation.applyTo(r1, uds));
                        checkVector(r1Prime.applyInverseTo(uds), FieldRotation.applyInverseTo(r1, uds));
                        checkVector(rA.applyTo(uds), rB.applyTo(uds));
                        checkVector(rA.applyInverseTo(uds), rB.applyInverseTo(uds));
                        checkVector(rC.applyTo(uds), rD.applyTo(uds));
                        checkVector(rC.applyInverseTo(uds), rD.applyInverseTo(uds));

                    }
                }
            }
        }

    }

    @Test
    public void testArray() throws MathIllegalArgumentException {

        FieldRotation<Dfp> r = new FieldRotation<Dfp>(createAxis(2, -3, 5), createAngle(1.7));

        for (double x = -0.9; x < 0.9; x += 0.2) {
            for (double y = -0.9; y < 0.9; y += 0.2) {
                for (double z = -0.9; z < 0.9; z += 0.2) {
                    FieldVector3D<Dfp> u = createVector(x, y, z);
                    FieldVector3D<Dfp> v = r.applyTo(u);
                    Dfp[] out = new Dfp[3];
                    r.applyTo(new Dfp[] { u.getX(), u.getY(), u.getZ() }, out);
                    Assert.assertEquals(v.getX().getReal(), out[0].getReal(), 1.0e-10);
                    Assert.assertEquals(v.getY().getReal(), out[1].getReal(), 1.0e-10);
                    Assert.assertEquals(v.getZ().getReal(), out[2].getReal(), 1.0e-10);
                    r.applyInverseTo(out, out);
                    Assert.assertEquals(u.getX().getReal(), out[0].getReal(), 1.0e-10);
                    Assert.assertEquals(u.getY().getReal(), out[1].getReal(), 1.0e-10);
                    Assert.assertEquals(u.getZ().getReal(), out[2].getReal(), 1.0e-10);
                }
            }
        }

    }

    @Test
    public void testApplyInverseTo() throws MathIllegalArgumentException {

        Dfp[] in      = new Dfp[3];
        Dfp[] out     = new Dfp[3];
        Dfp[] rebuilt = new Dfp[3];
        FieldRotation<Dfp> r = new FieldRotation<Dfp>(createVector(2, -3, 5), createAngle(1.7));
        for (double lambda = 0; lambda < 6.2; lambda += 0.2) {
            for (double phi = -1.55; phi < 1.55; phi += 0.2) {
                FieldVector3D<Dfp> u = createVector(FastMath.cos(lambda) * FastMath.cos(phi),
                                          FastMath.sin(lambda) * FastMath.cos(phi),
                                          FastMath.sin(phi));
                r.applyInverseTo(r.applyTo(u));
                checkVector(u, r.applyInverseTo(r.applyTo(u)));
                checkVector(u, r.applyTo(r.applyInverseTo(u)));
                in[0] = u.getX();
                in[1] = u.getY();
                in[2] = u.getZ();
                r.applyTo(in, out);
                r.applyInverseTo(out, rebuilt);
                Assert.assertEquals(in[0].getReal(), rebuilt[0].getReal(), 1.0e-12);
                Assert.assertEquals(in[1].getReal(), rebuilt[1].getReal(), 1.0e-12);
                Assert.assertEquals(in[2].getReal(), rebuilt[2].getReal(), 1.0e-12);
            }
        }

        r = createRotation(1, 0, 0, 0, false);
        for (double lambda = 0; lambda < 6.2; lambda += 0.2) {
            for (double phi = -1.55; phi < 1.55; phi += 0.2) {
                FieldVector3D<Dfp> u = createVector(FastMath.cos(lambda) * FastMath.cos(phi),
                                          FastMath.sin(lambda) * FastMath.cos(phi),
                                          FastMath.sin(phi));
                checkVector(u, r.applyInverseTo(r.applyTo(u)));
                checkVector(u, r.applyTo(r.applyInverseTo(u)));
            }
        }

        r = new FieldRotation<Dfp>(createVector(0, 0, 1), createAngle(FastMath.PI));
        for (double lambda = 0; lambda < 6.2; lambda += 0.2) {
            for (double phi = -1.55; phi < 1.55; phi += 0.2) {
                FieldVector3D<Dfp> u = createVector(FastMath.cos(lambda) * FastMath.cos(phi),
                                          FastMath.sin(lambda) * FastMath.cos(phi),
                                          FastMath.sin(phi));
                checkVector(u, r.applyInverseTo(r.applyTo(u)));
                checkVector(u, r.applyTo(r.applyInverseTo(u)));
            }
        }

    }

    @Test
    public void testIssue639() throws MathArithmeticException{
        FieldVector3D<Dfp> u1 = createVector(-1321008684645961.0 /  268435456.0,
                                   -5774608829631843.0 /  268435456.0,
                                   -3822921525525679.0 / 4294967296.0);
        FieldVector3D<Dfp> u2 =createVector( -5712344449280879.0 /    2097152.0,
                                   -2275058564560979.0 /    1048576.0,
                                   4423475992255071.0 /      65536.0);
        FieldRotation<Dfp> rot = new FieldRotation<Dfp>(u1, u2, createVector(1, 0, 0),createVector(0, 0, 1));
        Assert.assertEquals( 0.6228370359608200639829222, rot.getQ0().getReal(), 1.0e-15);
        Assert.assertEquals( 0.0257707621456498790029987, rot.getQ1().getReal(), 1.0e-15);
        Assert.assertEquals(-0.0000000002503012255839931, rot.getQ2().getReal(), 1.0e-15);
        Assert.assertEquals(-0.7819270390861109450724902, rot.getQ3().getReal(), 1.0e-15);
    }

    @Test
    public void testIssue801() throws MathArithmeticException {
        FieldVector3D<Dfp> u1 = createVector(0.9999988431610581, -0.0015210774290851095, 0.0);
        FieldVector3D<Dfp> u2 = createVector(0.0, 0.0, 1.0);

        FieldVector3D<Dfp> v1 = createVector(0.9999999999999999, 0.0, 0.0);
        FieldVector3D<Dfp> v2 = createVector(0.0, 0.0, -1.0);

        FieldRotation<Dfp> quat = new FieldRotation<Dfp>(u1, u2, v1, v2);
        double q2 = quat.getQ0().getReal() * quat.getQ0().getReal() +
                    quat.getQ1().getReal() * quat.getQ1().getReal() +
                    quat.getQ2().getReal() * quat.getQ2().getReal() +
                    quat.getQ3().getReal() * quat.getQ3().getReal();
        Assert.assertEquals(1.0, q2, 1.0e-14);
        Assert.assertEquals(0.0, FieldVector3D.angle(v1, quat.applyTo(u1)).getReal(), 1.0e-14);
        Assert.assertEquals(0.0, FieldVector3D.angle(v2, quat.applyTo(u2)).getReal(), 1.0e-14);

    }

    private void checkAngle(Dfp a1, double a2) {
        Assert.assertEquals(a1.getReal(), MathUtils.normalizeAngle(a2, a1.getReal()), 1.0e-10);
    }

    private void checkRotationDS(FieldRotation<Dfp> r, double q0, double q1, double q2, double q3) {
        FieldRotation<Dfp> rPrime = createRotation(q0, q1, q2, q3, false);
        Assert.assertEquals(0, FieldRotation.distance(r, rPrime).getReal(), 1.0e-12);
    }

    private FieldRotation<Dfp> createRotation(double q0, double q1, double q2, double q3,
                                      boolean needsNormalization) {
        DfpField field = new DfpField(20);
        return new FieldRotation<Dfp>(field.newDfp(q0),
                                      field.newDfp(q1),
                                      field.newDfp(q2),
                                      field.newDfp(q3),
                                      needsNormalization);
    }

    private FieldRotation<Dfp> createRotation(double[][] m, double threshold) {
        DfpField field = new DfpField(20);
        Dfp[][] mds = new Dfp[m.length][m[0].length];
        for (int i = 0; i < m.length; ++i) {
            for (int j = 0; j < m[i].length; ++j) {
                mds[i][j] = field.newDfp(m[i][j]);
            }
        }
        return new FieldRotation<Dfp>(mds, threshold);
    }

    private FieldVector3D<Dfp> createVector(double x, double y, double z) {
        DfpField field = new DfpField(20);
        return new FieldVector3D<Dfp>(field.newDfp(x), field.newDfp(y), field.newDfp(z));
    }

    private FieldVector3D<Dfp> createAxis(double x, double y, double z) {
        DfpField field = new DfpField(20);
        return new FieldVector3D<Dfp>(field.newDfp(x), field.newDfp(y), field.newDfp(z));
    }

    private Dfp createAngle(double alpha) {
        return new DfpField(20).newDfp(alpha);
    }

    private void checkVector(FieldVector3D<Dfp> u, FieldVector3D<Dfp> v) {
        Assert.assertEquals(u.getX().getReal(), v.getX().getReal(), 1.0e-12);
        Assert.assertEquals(u.getY().getReal(), v.getY().getReal(), 1.0e-12);
        Assert.assertEquals(u.getZ().getReal(), v.getZ().getReal(), 1.0e-12);
    }

}
