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

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.random.UnitSphereRandomVectorGenerator;
import org.apache.commons.math3.random.Well1024a;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.junit.Assert;
import org.junit.Test;


public class RotationDSTest {

    @Test
    public void testIdentity() {

        RotationDS r = createRotation(1, 0, 0, 0, false);
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

        RotationDS r = new RotationDS(createAxis(10, 10, 10), createAngle(2 * FastMath.PI / 3));
        checkVector(r.applyTo(createVector(1, 0, 0)), createVector(0, 1, 0));
        checkVector(r.applyTo(createVector(0, 1, 0)), createVector(0, 0, 1));
        checkVector(r.applyTo(createVector(0, 0, 1)), createVector(1, 0, 0));
        double s = 1 / FastMath.sqrt(3);
        checkVector(r.getAxis(), createVector(s, s, s));
        checkAngle(r.getAngle(), 2 * FastMath.PI / 3);

        try {
            new RotationDS(createAxis(0, 0, 0), createAngle(2 * FastMath.PI / 3));
            Assert.fail("an exception should have been thrown");
        } catch (MathIllegalArgumentException e) {
        }

        r = new RotationDS(createAxis(0, 0, 1), createAngle(1.5 * FastMath.PI));
        checkVector(r.getAxis(), createVector(0, 0, -1));
        checkAngle(r.getAngle(), 0.5 * FastMath.PI);

        r = new RotationDS(createAxis(0, 1, 0), createAngle(FastMath.PI));
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
        RotationDS r = createRotation(a, b, c, d, true);
        double a2 = a * a;
        double b2 = b * b;
        double c2 = c * c;
        double d2 = d * d;
        double den = (a2 + b2 + c2 + d2) * FastMath.sqrt(a2 + b2 + c2 + d2);
        Assert.assertEquals((b2 + c2 + d2) / den, r.getQ0().getPartialDerivative(1, 0, 0, 0), 1.0e-15);
        Assert.assertEquals(-a * b / den, r.getQ0().getPartialDerivative(0, 1, 0, 0), 1.0e-15);
        Assert.assertEquals(-a * c / den, r.getQ0().getPartialDerivative(0, 0, 1, 0), 1.0e-15);
        Assert.assertEquals(-a * d / den, r.getQ0().getPartialDerivative(0, 0, 0, 1), 1.0e-15);
        Assert.assertEquals(-b * a / den, r.getQ1().getPartialDerivative(1, 0, 0, 0), 1.0e-15);
        Assert.assertEquals((a2 + c2 + d2) / den, r.getQ1().getPartialDerivative(0, 1, 0, 0), 1.0e-15);
        Assert.assertEquals(-b * c / den, r.getQ1().getPartialDerivative(0, 0, 1, 0), 1.0e-15);
        Assert.assertEquals(-b * d / den, r.getQ1().getPartialDerivative(0, 0, 0, 1), 1.0e-15);
        Assert.assertEquals(-c * a / den, r.getQ2().getPartialDerivative(1, 0, 0, 0), 1.0e-15);
        Assert.assertEquals(-c * b / den, r.getQ2().getPartialDerivative(0, 1, 0, 0), 1.0e-15);
        Assert.assertEquals((a2 + b2 + d2) / den, r.getQ2().getPartialDerivative(0, 0, 1, 0), 1.0e-15);
        Assert.assertEquals(-c * d / den, r.getQ2().getPartialDerivative(0, 0, 0, 1), 1.0e-15);
        Assert.assertEquals(-d * a / den, r.getQ3().getPartialDerivative(1, 0, 0, 0), 1.0e-15);
        Assert.assertEquals(-d * b / den, r.getQ3().getPartialDerivative(0, 1, 0, 0), 1.0e-15);
        Assert.assertEquals(-d * c / den, r.getQ3().getPartialDerivative(0, 0, 1, 0), 1.0e-15);
        Assert.assertEquals((a2 + b2 + c2) / den, r.getQ3().getPartialDerivative(0, 0, 0, 1), 1.0e-15);
        RotationDS reverted = r.revert();
        RotationDS rrT = r.applyTo(reverted);
        checkRotationDS(rrT, 1, 0, 0, 0);
        Assert.assertEquals(0, rrT.getQ0().getPartialDerivative(1, 0, 0, 0), 1.0e-15);
        Assert.assertEquals(0, rrT.getQ0().getPartialDerivative(0, 1, 0, 0), 1.0e-15);
        Assert.assertEquals(0, rrT.getQ0().getPartialDerivative(0, 0, 1, 0), 1.0e-15);
        Assert.assertEquals(0, rrT.getQ0().getPartialDerivative(0, 0, 0, 1), 1.0e-15);
        Assert.assertEquals(0, rrT.getQ1().getPartialDerivative(1, 0, 0, 0), 1.0e-15);
        Assert.assertEquals(0, rrT.getQ1().getPartialDerivative(0, 1, 0, 0), 1.0e-15);
        Assert.assertEquals(0, rrT.getQ1().getPartialDerivative(0, 0, 1, 0), 1.0e-15);
        Assert.assertEquals(0, rrT.getQ1().getPartialDerivative(0, 0, 0, 1), 1.0e-15);
        Assert.assertEquals(0, rrT.getQ2().getPartialDerivative(1, 0, 0, 0), 1.0e-15);
        Assert.assertEquals(0, rrT.getQ2().getPartialDerivative(0, 1, 0, 0), 1.0e-15);
        Assert.assertEquals(0, rrT.getQ2().getPartialDerivative(0, 0, 1, 0), 1.0e-15);
        Assert.assertEquals(0, rrT.getQ2().getPartialDerivative(0, 0, 0, 1), 1.0e-15);
        Assert.assertEquals(0, rrT.getQ3().getPartialDerivative(1, 0, 0, 0), 1.0e-15);
        Assert.assertEquals(0, rrT.getQ3().getPartialDerivative(0, 1, 0, 0), 1.0e-15);
        Assert.assertEquals(0, rrT.getQ3().getPartialDerivative(0, 0, 1, 0), 1.0e-15);
        Assert.assertEquals(0, rrT.getQ3().getPartialDerivative(0, 0, 0, 1), 1.0e-15);
        RotationDS rTr = reverted.applyTo(r);
        checkRotationDS(rTr, 1, 0, 0, 0);
        Assert.assertEquals(0, rTr.getQ0().getPartialDerivative(1, 0, 0, 0), 1.0e-15);
        Assert.assertEquals(0, rTr.getQ0().getPartialDerivative(0, 1, 0, 0), 1.0e-15);
        Assert.assertEquals(0, rTr.getQ0().getPartialDerivative(0, 0, 1, 0), 1.0e-15);
        Assert.assertEquals(0, rTr.getQ0().getPartialDerivative(0, 0, 0, 1), 1.0e-15);
        Assert.assertEquals(0, rTr.getQ1().getPartialDerivative(1, 0, 0, 0), 1.0e-15);
        Assert.assertEquals(0, rTr.getQ1().getPartialDerivative(0, 1, 0, 0), 1.0e-15);
        Assert.assertEquals(0, rTr.getQ1().getPartialDerivative(0, 0, 1, 0), 1.0e-15);
        Assert.assertEquals(0, rTr.getQ1().getPartialDerivative(0, 0, 0, 1), 1.0e-15);
        Assert.assertEquals(0, rTr.getQ2().getPartialDerivative(1, 0, 0, 0), 1.0e-15);
        Assert.assertEquals(0, rTr.getQ2().getPartialDerivative(0, 1, 0, 0), 1.0e-15);
        Assert.assertEquals(0, rTr.getQ2().getPartialDerivative(0, 0, 1, 0), 1.0e-15);
        Assert.assertEquals(0, rTr.getQ2().getPartialDerivative(0, 0, 0, 1), 1.0e-15);
        Assert.assertEquals(0, rTr.getQ3().getPartialDerivative(1, 0, 0, 0), 1.0e-15);
        Assert.assertEquals(0, rTr.getQ3().getPartialDerivative(0, 1, 0, 0), 1.0e-15);
        Assert.assertEquals(0, rTr.getQ3().getPartialDerivative(0, 0, 1, 0), 1.0e-15);
        Assert.assertEquals(0, rTr.getQ3().getPartialDerivative(0, 0, 0, 1), 1.0e-15);
        Assert.assertEquals(r.getAngle().getValue(), reverted.getAngle().getValue(), 1.0e-15);
        Assert.assertEquals(-1, Vector3DDS.dotProduct(r.getAxis(), reverted.getAxis()).getValue(), 1.0e-15);
    }

    @Test
    public void testVectorOnePair() throws MathArithmeticException {

        Vector3DDS u = createVector(3, 2, 1);
        Vector3DDS v = createVector(-4, 2, 2);
        RotationDS r = new RotationDS(u, v);
        checkVector(r.applyTo(u.scalarMultiply(v.getNorm())), v.scalarMultiply(u.getNorm()));

        checkAngle(new RotationDS(u, u.negate()).getAngle(), FastMath.PI);

        try {
            new RotationDS(u, createVector(0, 0, 0));
            Assert.fail("an exception should have been thrown");
        } catch (MathArithmeticException e) {
            // expected behavior
        }

    }

    @Test
    public void testVectorTwoPairs() throws MathArithmeticException {

        Vector3DDS u1 = createVector(3, 0, 0);
        Vector3DDS u2 = createVector(0, 5, 0);
        Vector3DDS v1 = createVector(0, 0, 2);
        Vector3DDS v2 = createVector(-2, 0, 2);
        RotationDS r = new RotationDS(u1, u2, v1, v2);
        checkVector(r.applyTo(createVector(1, 0, 0)), createVector(0, 0, 1));
        checkVector(r.applyTo(createVector(0, 1, 0)), createVector(-1, 0, 0));

        r = new RotationDS(u1, u2, u1.negate(), u2.negate());
        Vector3DDS axis = r.getAxis();
        if (Vector3DDS.dotProduct(axis, createVector(0, 0, 1)).getValue() > 0) {
            checkVector(axis, createVector(0, 0, 1));
        } else {
            checkVector(axis, createVector(0, 0, -1));
        }
        checkAngle(r.getAngle(), FastMath.PI);

        double sqrt = FastMath.sqrt(2) / 2;
        r = new RotationDS(createVector(1, 0, 0),  createVector(0, 1, 0),
                           createVector(0.5, 0.5,  sqrt),
                           createVector(0.5, 0.5, -sqrt));
        checkRotationDS(r, sqrt, 0.5, 0.5, 0);

        r = new RotationDS(u1, u2, u1, Vector3DDS.crossProduct(u1, u2));
        checkRotationDS(r, sqrt, -sqrt, 0, 0);

        checkRotationDS(new RotationDS(u1, u2, u1, u2), 1, 0, 0, 0);

        try {
            new RotationDS(u1, u2, createVector(0, 0, 0), v2);
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
        RotationDS r = createRotation(m1, 1.0e-7);
        checkVector(r.applyTo(createVector(1, 0, 0)), createVector(0, 0, 1));
        checkVector(r.applyTo(createVector(0, 1, 0)), createVector(1, 0, 0));
        checkVector(r.applyTo(createVector(0, 0, 1)), createVector(0, 1, 0));

        double[][] m2 = { { 0.83203, -0.55012, -0.07139 },
            { 0.48293,  0.78164, -0.39474 },
            { 0.27296,  0.29396,  0.91602 } };
        r = createRotation(m2, 1.0e-12);

        DerivativeStructure[][] m3 = r.getMatrix();
        double d00 = m2[0][0] - m3[0][0].getValue();
        double d01 = m2[0][1] - m3[0][1].getValue();
        double d02 = m2[0][2] - m3[0][2].getValue();
        double d10 = m2[1][0] - m3[1][0].getValue();
        double d11 = m2[1][1] - m3[1][1].getValue();
        double d12 = m2[1][2] - m3[1][2].getValue();
        double d20 = m2[2][0] - m3[2][0].getValue();
        double d21 = m2[2][1] - m3[2][1].getValue();
        double d22 = m2[2][2] - m3[2][2].getValue();

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
                double m3tm3 = m3[i][0].getValue() * m3[j][0].getValue() +
                               m3[i][1].getValue() * m3[j][1].getValue() +
                               m3[i][2].getValue() * m3[j][2].getValue();
                if (i == j) {
                    Assert.assertTrue(FastMath.abs(m3tm3 - 1.0) < 1.0e-10);
                } else {
                    Assert.assertTrue(FastMath.abs(m3tm3) < 1.0e-10);
                }
            }
        }

        checkVector(r.applyTo(createVector(1, 0, 0)),
                    new Vector3DDS(m3[0][0], m3[1][0], m3[2][0]));
        checkVector(r.applyTo(createVector(0, 1, 0)),
                    new Vector3DDS(m3[0][1], m3[1][1], m3[2][1]));
        checkVector(r.applyTo(createVector(0, 0, 1)),
                    new Vector3DDS(m3[0][2], m3[1][2], m3[2][2]));

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

        RotationOrder[] CardanOrders = {
            RotationOrder.XYZ, RotationOrder.XZY, RotationOrder.YXZ,
            RotationOrder.YZX, RotationOrder.ZXY, RotationOrder.ZYX
        };

        for (int i = 0; i < CardanOrders.length; ++i) {
            for (double alpha1 = 0.1; alpha1 < 6.2; alpha1 += 0.3) {
                for (double alpha2 = -1.55; alpha2 < 1.55; alpha2 += 0.3) {
                    for (double alpha3 = 0.1; alpha3 < 6.2; alpha3 += 0.3) {
                        RotationDS r = new RotationDS(CardanOrders[i],
                                                      new DerivativeStructure(3, 1, 0, alpha1),
                                                      new DerivativeStructure(3, 1, 1, alpha2),
                                                      new DerivativeStructure(3, 1, 2, alpha3));
                        DerivativeStructure[] angles = r.getAngles(CardanOrders[i]);
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
            for (double alpha1 = 0.1; alpha1 < 6.2; alpha1 += 0.3) {
                for (double alpha2 = 0.05; alpha2 < 3.1; alpha2 += 0.3) {
                    for (double alpha3 = 0.1; alpha3 < 6.2; alpha3 += 0.3) {
                        RotationDS r = new RotationDS(EulerOrders[i],
                                                      new DerivativeStructure(3, 1, 0, alpha1),
                                                      new DerivativeStructure(3, 1, 1, alpha2),
                                                      new DerivativeStructure(3, 1, 2, alpha3));
                        DerivativeStructure[] angles = r.getAngles(EulerOrders[i]);
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

        RotationOrder[] CardanOrders = {
            RotationOrder.XYZ, RotationOrder.XZY, RotationOrder.YXZ,
            RotationOrder.YZX, RotationOrder.ZXY, RotationOrder.ZYX
        };

        double[] singularCardanAngle = { FastMath.PI / 2, -FastMath.PI / 2 };
        for (int i = 0; i < CardanOrders.length; ++i) {
            for (int j = 0; j < singularCardanAngle.length; ++j) {
                RotationDS r = new RotationDS(CardanOrders[i],
                                              new DerivativeStructure(3, 1, 0, 0.1),
                                              new DerivativeStructure(3, 1, 1, singularCardanAngle[j]),
                                              new DerivativeStructure(3, 1, 2, 0.3));
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
                RotationDS r = new RotationDS(EulerOrders[i],
                                              new DerivativeStructure(3, 1, 0, 0.1),
                                              new DerivativeStructure(3, 1, 1, singularEulerAngle[j]),
                                              new DerivativeStructure(3, 1, 2, 0.3));
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

        RotationDS r1 = new RotationDS(createVector(2, -3, 5), createAngle(1.7));
        double n = 23.5;
        RotationDS r2 = new RotationDS(r1.getQ0().multiply(n), r1.getQ1().multiply(n),
                                       r1.getQ2().multiply(n), r1.getQ3().multiply(n),
                                       true);
        for (double x = -0.9; x < 0.9; x += 0.2) {
            for (double y = -0.9; y < 0.9; y += 0.2) {
                for (double z = -0.9; z < 0.9; z += 0.2) {
                    Vector3DDS u = createVector(x, y, z);
                    checkVector(r2.applyTo(u), r1.applyTo(u));
                }
            }
        }

        r1 = createRotation(0.288,  0.384,  0.36,  0.8, false);
        checkRotationDS(r1,
                        -r1.getQ0().getValue(), -r1.getQ1().getValue(),
                        -r1.getQ2().getValue(), -r1.getQ3().getValue());

    }

    @Test
    public void testCompose() throws MathIllegalArgumentException {

        RotationDS r1       = new RotationDS(createVector(2, -3, 5), createAngle(1.7));
        RotationDS r2       = new RotationDS(createVector(-1, 3, 2), createAngle(0.3));
        RotationDS r3       = r2.applyTo(r1);
        RotationDS r3Double = r2.applyTo(new Rotation(r1.getQ0().getValue(),
                                                      r1.getQ1().getValue(),
                                                      r1.getQ2().getValue(),
                                                      r1.getQ3().getValue(),
                                                      false));

        for (double x = -0.9; x < 0.9; x += 0.2) {
            for (double y = -0.9; y < 0.9; y += 0.2) {
                for (double z = -0.9; z < 0.9; z += 0.2) {
                    Vector3DDS u = createVector(x, y, z);
                    checkVector(r2.applyTo(r1.applyTo(u)), r3.applyTo(u));
                    checkVector(r2.applyTo(r1.applyTo(u)), r3Double.applyTo(u));
                }
            }
        }

    }

    @Test
    public void testComposeInverse() throws MathIllegalArgumentException {

        RotationDS r1 = new RotationDS(createVector(2, -3, 5), createAngle(1.7));
        RotationDS r2 = new RotationDS(createVector(-1, 3, 2), createAngle(0.3));
        RotationDS r3 = r2.applyInverseTo(r1);
        RotationDS r3Double = r2.applyInverseTo(new Rotation(r1.getQ0().getValue(),
                                                             r1.getQ1().getValue(),
                                                             r1.getQ2().getValue(),
                                                             r1.getQ3().getValue(),
                                                             false));

        for (double x = -0.9; x < 0.9; x += 0.2) {
            for (double y = -0.9; y < 0.9; y += 0.2) {
                for (double z = -0.9; z < 0.9; z += 0.2) {
                    Vector3DDS u = createVector(x, y, z);
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
            RotationDS r = new RotationDS(createVector(unit[0], unit[1], unit[2]),
                                          createAngle(random.nextDouble()));

            for (double x = -0.9; x < 0.9; x += 0.2) {
                for (double y = -0.9; y < 0.9; y += 0.2) {
                    for (double z = -0.9; z < 0.9; z += 0.2) {
                        Vector3DDS uds   = createVector(x, y, z);
                        Vector3DDS ruds  = r.applyTo(uds);
                        Vector3DDS rIuds = r.applyInverseTo(uds);
                        Vector3D   u     = new Vector3D(x, y, z);
                        Vector3DDS ru    = r.applyTo(u);
                        Vector3DDS rIu   = r.applyInverseTo(u);
                        DerivativeStructure[] ruArray = new DerivativeStructure[3];
                        r.applyTo(new double[] { x, y, z}, ruArray);
                        DerivativeStructure[] rIuArray = new DerivativeStructure[3];
                        r.applyInverseTo(new double[] { x, y, z}, rIuArray);
                        checkVector(ruds, ru);
                        checkVector(ruds, new Vector3DDS(ruArray));
                        checkVector(rIuds, rIu);
                        checkVector(rIuds, new Vector3DDS(rIuArray));
                    }
                }
            }
        }

    }

    @Test
    public void testDoubleRotations() throws MathIllegalArgumentException {

        Well1024a random = new Well1024a(0x180b41cfeeffaf67l);
        UnitSphereRandomVectorGenerator g = new UnitSphereRandomVectorGenerator(3, random);
        for (int i = 0; i < 10; ++i) {
            double[] unit1 = g.nextVector();
            Rotation r1 = new Rotation(new Vector3D(unit1[0], unit1[1], unit1[2]),
                                      random.nextDouble());
            RotationDS r1Prime = new RotationDS(new DerivativeStructure(4, 1, 0, r1.getQ0()),
                                                new DerivativeStructure(4, 1, 1, r1.getQ1()),
                                                new DerivativeStructure(4, 1, 2, r1.getQ2()),
                                                new DerivativeStructure(4, 1, 3, r1.getQ3()),
                                                false);
            double[] unit2 = g.nextVector();
            RotationDS r2 = new RotationDS(createVector(unit2[0], unit2[1], unit2[2]),
                                           createAngle(random.nextDouble()));

            RotationDS rA = RotationDS.applyTo(r1, r2);
            RotationDS rB = r1Prime.applyTo(r2);
            RotationDS rC = RotationDS.applyInverseTo(r1, r2);
            RotationDS rD = r1Prime.applyInverseTo(r2);

            for (double x = -0.9; x < 0.9; x += 0.2) {
                for (double y = -0.9; y < 0.9; y += 0.2) {
                    for (double z = -0.9; z < 0.9; z += 0.2) {

                        Vector3DDS uds   = createVector(x, y, z);
                        checkVector(r1Prime.applyTo(uds), RotationDS.applyTo(r1, uds));
                        checkVector(r1Prime.applyInverseTo(uds), RotationDS.applyInverseTo(r1, uds));
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
    public void testDerivatives() {

        double eps      = 5.0e-16;
        double kx       = 2;
        double ky       = -3;
        double kz       = 5;
        double n2       = kx * kx + ky * ky + kz * kz;
        double n        = FastMath.sqrt(n2);
        double theta    = 1.7;
        double cosTheta = FastMath.cos(theta);
        double sinTheta = FastMath.sin(theta);
        RotationDS r    = new RotationDS(createAxis(kx, ky, kz), createAngle(theta));
        Vector3D a      = new Vector3D(kx / n, ky / n, kz / n);

        // Jacobian of the normalized rotation axis a with respect to the Cartesian vector k
        RealMatrix dadk = MatrixUtils.createRealMatrix(new double[][] {
            { (ky * ky + kz * kz) / ( n * n2),            -kx * ky / ( n * n2),            -kx * kz / ( n * n2) },
            {            -kx * ky / ( n * n2), (kx * kx + kz * kz) / ( n * n2),            -ky * kz / ( n * n2) },
            {            -kx * kz / ( n * n2),            -ky * kz / ( n * n2), (kx * kx + ky * ky) / ( n * n2) }
        });

        for (double x = -0.9; x < 0.9; x += 0.2) {
            for (double y = -0.9; y < 0.9; y += 0.2) {
                for (double z = -0.9; z < 0.9; z += 0.2) {
                    Vector3D   u = new Vector3D(x, y, z);
                    Vector3DDS v = r.applyTo(createVector(x, y, z));

                    // explicit formula for rotation of vector u around axis a with angle theta
                    double dot     = Vector3D.dotProduct(u, a);
                    Vector3D cross = Vector3D.crossProduct(a, u);
                    double c1      = 1 - cosTheta;
                    double c2      = c1 * dot;
                    Vector3D rt    = new Vector3D(cosTheta, u, c2, a, sinTheta, cross);
                    Assert.assertEquals(rt.getX(), v.getX().getValue(), eps);
                    Assert.assertEquals(rt.getY(), v.getY().getValue(), eps);
                    Assert.assertEquals(rt.getZ(), v.getZ().getValue(), eps);

                    // Jacobian of the image v = r(u) with respect to rotation axis a
                    // (analytical differentiation of the explicit formula)
                    RealMatrix dvda = MatrixUtils.createRealMatrix(new double[][] {
                        { c1 * x * a.getX() + c2,           c1 * y * a.getX() + sinTheta * z, c1 * z * a.getX() - sinTheta * y },
                        { c1 * x * a.getY() - sinTheta * z, c1 * y * a.getY() + c2,           c1 * z * a.getY() + sinTheta * x },
                        { c1 * x * a.getZ() + sinTheta * y, c1 * y * a.getZ() - sinTheta * x, c1 * z * a.getZ() + c2           }
                    });

                    // compose Jacobians
                    RealMatrix dvdk = dvda.multiply(dadk);

                    // derivatives with respect to un-normalized axis
                    Assert.assertEquals(dvdk.getEntry(0, 0), v.getX().getPartialDerivative(1, 0, 0, 0), eps);
                    Assert.assertEquals(dvdk.getEntry(0, 1), v.getX().getPartialDerivative(0, 1, 0, 0), eps);
                    Assert.assertEquals(dvdk.getEntry(0, 2), v.getX().getPartialDerivative(0, 0, 1, 0), eps);
                    Assert.assertEquals(dvdk.getEntry(1, 0), v.getY().getPartialDerivative(1, 0, 0, 0), eps);
                    Assert.assertEquals(dvdk.getEntry(1, 1), v.getY().getPartialDerivative(0, 1, 0, 0), eps);
                    Assert.assertEquals(dvdk.getEntry(1, 2), v.getY().getPartialDerivative(0, 0, 1, 0), eps);
                    Assert.assertEquals(dvdk.getEntry(2, 0), v.getZ().getPartialDerivative(1, 0, 0, 0), eps);
                    Assert.assertEquals(dvdk.getEntry(2, 1), v.getZ().getPartialDerivative(0, 1, 0, 0), eps);
                    Assert.assertEquals(dvdk.getEntry(2, 2), v.getZ().getPartialDerivative(0, 0, 1, 0), eps);

                    // derivative with respect to rotation angle
                    // (analytical differentiation of the explicit formula)
                    Vector3D dvdTheta =
                            new Vector3D(-sinTheta, u, sinTheta * dot, a, cosTheta, cross);
                    Assert.assertEquals(dvdTheta.getX(), v.getX().getPartialDerivative(0, 0, 0, 1), eps);
                    Assert.assertEquals(dvdTheta.getY(), v.getY().getPartialDerivative(0, 0, 0, 1), eps);
                    Assert.assertEquals(dvdTheta.getZ(), v.getZ().getPartialDerivative(0, 0, 0, 1), eps);

                }
            }
        }
     }

    @Test
    public void testArray() throws MathIllegalArgumentException {

        RotationDS r = new RotationDS(createAxis(2, -3, 5), createAngle(1.7));

        for (double x = -0.9; x < 0.9; x += 0.2) {
            for (double y = -0.9; y < 0.9; y += 0.2) {
                for (double z = -0.9; z < 0.9; z += 0.2) {
                    Vector3DDS u = createVector(x, y, z);
                    Vector3DDS v = r.applyTo(u);
                    DerivativeStructure[] out = new DerivativeStructure[3];
                    r.applyTo(new DerivativeStructure[] { u.getX(), u.getY(), u.getZ() }, out);
                    Assert.assertEquals(v.getX().getValue(), out[0].getValue(), 1.0e-10);
                    Assert.assertEquals(v.getY().getValue(), out[1].getValue(), 1.0e-10);
                    Assert.assertEquals(v.getZ().getValue(), out[2].getValue(), 1.0e-10);
                    r.applyInverseTo(out, out);
                    Assert.assertEquals(u.getX().getValue(), out[0].getValue(), 1.0e-10);
                    Assert.assertEquals(u.getY().getValue(), out[1].getValue(), 1.0e-10);
                    Assert.assertEquals(u.getZ().getValue(), out[2].getValue(), 1.0e-10);
                }
            }
        }

    }

    @Test
    public void testApplyInverseTo() throws MathIllegalArgumentException {

        DerivativeStructure[] in      = new DerivativeStructure[3];
        DerivativeStructure[] out     = new DerivativeStructure[3];
        DerivativeStructure[] rebuilt = new DerivativeStructure[3];
        RotationDS r = new RotationDS(createVector(2, -3, 5), createAngle(1.7));
        for (double lambda = 0; lambda < 6.2; lambda += 0.2) {
            for (double phi = -1.55; phi < 1.55; phi += 0.2) {
                Vector3DDS u = createVector(FastMath.cos(lambda) * FastMath.cos(phi),
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
                Assert.assertEquals(in[0].getValue(), rebuilt[0].getValue(), 1.0e-12);
                Assert.assertEquals(in[1].getValue(), rebuilt[1].getValue(), 1.0e-12);
                Assert.assertEquals(in[2].getValue(), rebuilt[2].getValue(), 1.0e-12);
            }
        }

        r = createRotation(1, 0, 0, 0, false);
        for (double lambda = 0; lambda < 6.2; lambda += 0.2) {
            for (double phi = -1.55; phi < 1.55; phi += 0.2) {
                Vector3DDS u = createVector(FastMath.cos(lambda) * FastMath.cos(phi),
                                          FastMath.sin(lambda) * FastMath.cos(phi),
                                          FastMath.sin(phi));
                checkVector(u, r.applyInverseTo(r.applyTo(u)));
                checkVector(u, r.applyTo(r.applyInverseTo(u)));
            }
        }

        r = new RotationDS(createVector(0, 0, 1), createAngle(FastMath.PI));
        for (double lambda = 0; lambda < 6.2; lambda += 0.2) {
            for (double phi = -1.55; phi < 1.55; phi += 0.2) {
                Vector3DDS u = createVector(FastMath.cos(lambda) * FastMath.cos(phi),
                                          FastMath.sin(lambda) * FastMath.cos(phi),
                                          FastMath.sin(phi));
                checkVector(u, r.applyInverseTo(r.applyTo(u)));
                checkVector(u, r.applyTo(r.applyInverseTo(u)));
            }
        }

    }

    @Test
    public void testIssue639() throws MathArithmeticException{
        Vector3DDS u1 = createVector(-1321008684645961.0 /  268435456.0,
                                   -5774608829631843.0 /  268435456.0,
                                   -3822921525525679.0 / 4294967296.0);
        Vector3DDS u2 =createVector( -5712344449280879.0 /    2097152.0,
                                   -2275058564560979.0 /    1048576.0,
                                   4423475992255071.0 /      65536.0);
        RotationDS rot = new RotationDS(u1, u2, createVector(1, 0, 0),createVector(0, 0, 1));
        Assert.assertEquals( 0.6228370359608200639829222, rot.getQ0().getValue(), 1.0e-15);
        Assert.assertEquals( 0.0257707621456498790029987, rot.getQ1().getValue(), 1.0e-15);
        Assert.assertEquals(-0.0000000002503012255839931, rot.getQ2().getValue(), 1.0e-15);
        Assert.assertEquals(-0.7819270390861109450724902, rot.getQ3().getValue(), 1.0e-15);
    }

    @Test
    public void testIssue801() throws MathArithmeticException {
        Vector3DDS u1 = createVector(0.9999988431610581, -0.0015210774290851095, 0.0);
        Vector3DDS u2 = createVector(0.0, 0.0, 1.0);

        Vector3DDS v1 = createVector(0.9999999999999999, 0.0, 0.0);
        Vector3DDS v2 = createVector(0.0, 0.0, -1.0);

        RotationDS quat = new RotationDS(u1, u2, v1, v2);
        double q2 = quat.getQ0().getValue() * quat.getQ0().getValue() +
                    quat.getQ1().getValue() * quat.getQ1().getValue() +
                    quat.getQ2().getValue() * quat.getQ2().getValue() +
                    quat.getQ3().getValue() * quat.getQ3().getValue();
        Assert.assertEquals(1.0, q2, 1.0e-14);
        Assert.assertEquals(0.0, Vector3DDS.angle(v1, quat.applyTo(u1)).getValue(), 1.0e-14);
        Assert.assertEquals(0.0, Vector3DDS.angle(v2, quat.applyTo(u2)).getValue(), 1.0e-14);

    }

    private void checkAngle(DerivativeStructure a1, double a2) {
        Assert.assertEquals(a1.getValue(), MathUtils.normalizeAngle(a2, a1.getValue()), 1.0e-10);
    }

    private void checkRotationDS(RotationDS r, double q0, double q1, double q2, double q3) {
        RotationDS rPrime = createRotation(q0, q1, q2, q3, false);
        Assert.assertEquals(0, RotationDS.distance(r, rPrime).getValue(), 1.0e-12);
    }

    private RotationDS createRotation(double q0, double q1, double q2, double q3,
                                      boolean needsNormalization) {
        return new RotationDS(new DerivativeStructure(4, 1, 0, q0),
                              new DerivativeStructure(4, 1, 1, q1),
                              new DerivativeStructure(4, 1, 2, q2),
                              new DerivativeStructure(4, 1, 3, q3),
                              needsNormalization);
    }

    private RotationDS createRotation(double[][] m, double threshold) {
        DerivativeStructure[][] mds = new DerivativeStructure[m.length][m[0].length];
        int index = 0;
        for (int i = 0; i < m.length; ++i) {
            for (int j = 0; j < m[i].length; ++j) {
                mds[i][j] = new DerivativeStructure(4, 1, index, m[i][j]);
                index = (index + 1) % 4;
            }
        }
        return new RotationDS(mds, threshold);
    }

    private Vector3DDS createVector(double x, double y, double z) {
        return new Vector3DDS(new DerivativeStructure(4, 1, x),
                              new DerivativeStructure(4, 1, y),
                              new DerivativeStructure(4, 1, z));
    }

    private Vector3DDS createAxis(double x, double y, double z) {
        return new Vector3DDS(new DerivativeStructure(4, 1, 0, x),
                              new DerivativeStructure(4, 1, 1, y),
                              new DerivativeStructure(4, 1, 2, z));
    }

    private DerivativeStructure createAngle(double alpha) {
        return new DerivativeStructure(4, 1, 3, alpha);
    }

    private void checkVector(Vector3DDS u, Vector3DDS v) {
        Assert.assertEquals(u.getX().getValue(), v.getX().getValue(), 1.0e-12);
        Assert.assertEquals(u.getY().getValue(), v.getY().getValue(), 1.0e-12);
        Assert.assertEquals(u.getZ().getValue(), v.getZ().getValue(), 1.0e-12);
    }

}
