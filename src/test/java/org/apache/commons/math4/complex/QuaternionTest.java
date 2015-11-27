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
package org.apache.commons.math4.complex;

import java.util.Random;

import org.apache.commons.math4.complex.Quaternion;
import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.ZeroException;
import org.apache.commons.math4.geometry.euclidean.threed.Rotation;
import org.apache.commons.math4.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math4.util.FastMath;
import org.junit.Test;
import org.junit.Assert;

public class QuaternionTest {
    /** Epsilon for double comparison. */
    private static final double EPS = Math.ulp(1d);
    /** Epsilon for double comparison. */
    private static final double COMPARISON_EPS = 1e-14;

    @Test
    public final void testAccessors1() {
        final double q0 = 2;
        final double q1 = 5.4;
        final double q2 = 17;
        final double q3 = 0.0005;
        final Quaternion q = new Quaternion(q0, q1, q2, q3);

        Assert.assertEquals(q0, q.getQ0(), 0);
        Assert.assertEquals(q1, q.getQ1(), 0);
        Assert.assertEquals(q2, q.getQ2(), 0);
        Assert.assertEquals(q3, q.getQ3(), 0);
    }

    @Test
    public final void testAccessors2() {
        final double q0 = 2;
        final double q1 = 5.4;
        final double q2 = 17;
        final double q3 = 0.0005;
        final Quaternion q = new Quaternion(q0, q1, q2, q3);

        final double sP = q.getScalarPart();
        final double[] vP = q.getVectorPart();

        Assert.assertEquals(q0, sP, 0);
        Assert.assertEquals(q1, vP[0], 0);
        Assert.assertEquals(q2, vP[1], 0);
        Assert.assertEquals(q3, vP[2], 0);
    }

    @Test
    public final void testAccessors3() {
        final double q0 = 2;
        final double q1 = 5.4;
        final double q2 = 17;
        final double q3 = 0.0005;
        final Quaternion q = new Quaternion(q0, new double[] { q1, q2, q3 });

        final double sP = q.getScalarPart();
        final double[] vP = q.getVectorPart();

        Assert.assertEquals(q0, sP, 0);
        Assert.assertEquals(q1, vP[0], 0);
        Assert.assertEquals(q2, vP[1], 0);
        Assert.assertEquals(q3, vP[2], 0);
    }

    @Test(expected=DimensionMismatchException.class)
    public void testWrongDimension() {
        new Quaternion(new double[] { 1, 2 });
    }

    @Test
    public final void testConjugate() {
        final double q0 = 2;
        final double q1 = 5.4;
        final double q2 = 17;
        final double q3 = 0.0005;
        final Quaternion q = new Quaternion(q0, q1, q2, q3);

        final Quaternion qConjugate = q.getConjugate();

        Assert.assertEquals(q0, qConjugate.getQ0(), 0);
        Assert.assertEquals(-q1, qConjugate.getQ1(), 0);
        Assert.assertEquals(-q2, qConjugate.getQ2(), 0);
        Assert.assertEquals(-q3, qConjugate.getQ3(), 0);
    }

    @Test
    public final void testProductQuaternionQuaternion() {

        // Case : analytic test case

        final Quaternion qA = new Quaternion(1, 0.5, -3, 4);
        final Quaternion qB = new Quaternion(6, 2, 1, -9);
        final Quaternion qResult = Quaternion.multiply(qA, qB);

        Assert.assertEquals(44, qResult.getQ0(), EPS);
        Assert.assertEquals(28, qResult.getQ1(), EPS);
        Assert.assertEquals(-4.5, qResult.getQ2(), EPS);
        Assert.assertEquals(21.5, qResult.getQ3(), EPS);

        // comparison with the result given by the formula :
        // qResult = (scalarA * scalarB - vectorA . vectorB) + (scalarA * vectorB + scalarB * vectorA + vectorA ^
        // vectorB)

        final Vector3D vectorA = new Vector3D(qA.getVectorPart());
        final Vector3D vectorB = new Vector3D(qB.getVectorPart());
        final Vector3D vectorResult = new Vector3D(qResult.getVectorPart());

        final double scalarPartRef = qA.getScalarPart() * qB.getScalarPart() - Vector3D.dotProduct(vectorA, vectorB);

        Assert.assertEquals(scalarPartRef, qResult.getScalarPart(), EPS);

        final Vector3D vectorPartRef = ((vectorA.scalarMultiply(qB.getScalarPart())).add(vectorB.scalarMultiply(qA
                .getScalarPart()))).add(Vector3D.crossProduct(vectorA, vectorB));
        final double norm = (vectorResult.subtract(vectorPartRef)).getNorm();

        Assert.assertEquals(0, norm, EPS);

        // Conjugate of the product of two quaternions and product of their conjugates :
        // Conj(qA * qB) = Conj(qB) * Conj(qA)

        final Quaternion conjugateOfProduct = qB.getConjugate().multiply(qA.getConjugate());
        final Quaternion productOfConjugate = (qA.multiply(qB)).getConjugate();

        Assert.assertEquals(conjugateOfProduct.getQ0(), productOfConjugate.getQ0(), EPS);
        Assert.assertEquals(conjugateOfProduct.getQ1(), productOfConjugate.getQ1(), EPS);
        Assert.assertEquals(conjugateOfProduct.getQ2(), productOfConjugate.getQ2(), EPS);
        Assert.assertEquals(conjugateOfProduct.getQ3(), productOfConjugate.getQ3(), EPS);
    }

    @Test
    public final void testProductQuaternionVector() {

        // Case : Product between a vector and a quaternion : QxV

        final Quaternion quaternion = new Quaternion(4, 7, -1, 2);
        final double[] vector = {2.0, 1.0, 3.0};
        final Quaternion qResultQxV = Quaternion.multiply(quaternion, new Quaternion(vector));

        Assert.assertEquals(-19, qResultQxV.getQ0(), EPS);
        Assert.assertEquals(3, qResultQxV.getQ1(), EPS);
        Assert.assertEquals(-13, qResultQxV.getQ2(), EPS);
        Assert.assertEquals(21, qResultQxV.getQ3(), EPS);

        // comparison with the result given by the formula :
        // qResult = (- vectorQ . vector) + (scalarQ * vector + vectorQ ^ vector)

        final double[] vectorQ = quaternion.getVectorPart();
        final double[] vectorResultQxV = qResultQxV.getVectorPart();

        final double scalarPartRefQxV = -Vector3D.dotProduct(new Vector3D(vectorQ), new Vector3D(vector));
        Assert.assertEquals(scalarPartRefQxV, qResultQxV.getScalarPart(), EPS);

        final Vector3D vectorPartRefQxV = (new Vector3D(vector).scalarMultiply(quaternion.getScalarPart())).add(Vector3D
                .crossProduct(new Vector3D(vectorQ), new Vector3D(vector)));
        final double normQxV = (new Vector3D(vectorResultQxV).subtract(vectorPartRefQxV)).getNorm();
        Assert.assertEquals(0, normQxV, EPS);

        // Case : Product between a vector and a quaternion : VxQ

        final Quaternion qResultVxQ = Quaternion.multiply(new Quaternion(vector), quaternion);

        Assert.assertEquals(-19, qResultVxQ.getQ0(), EPS);
        Assert.assertEquals(13, qResultVxQ.getQ1(), EPS);
        Assert.assertEquals(21, qResultVxQ.getQ2(), EPS);
        Assert.assertEquals(3, qResultVxQ.getQ3(), EPS);

        final double[] vectorResultVxQ = qResultVxQ.getVectorPart();

        // comparison with the result given by the formula :
        // qResult = (- vector . vectorQ) + (scalarQ * vector + vector ^ vectorQ)

        final double scalarPartRefVxQ = -Vector3D.dotProduct(new Vector3D(vectorQ), new Vector3D(vector));
        Assert.assertEquals(scalarPartRefVxQ, qResultVxQ.getScalarPart(), EPS);

        final Vector3D vectorPartRefVxQ = (new Vector3D(vector).scalarMultiply(quaternion.getScalarPart())).add(Vector3D
                .crossProduct(new Vector3D(vector), new Vector3D(vectorQ)));
        final double normVxQ = (new Vector3D(vectorResultVxQ).subtract(vectorPartRefVxQ)).getNorm();
        Assert.assertEquals(0, normVxQ, EPS);
    }

    @Test
    public final void testDotProductQuaternionQuaternion() {
        // expected output
        final double expected = -6.;
        // inputs
        final Quaternion q1 = new Quaternion(1, 2, 2, 1);
        final Quaternion q2 = new Quaternion(3, -2, -1, -3);

        final double actual1 = Quaternion.dotProduct(q1, q2);
        final double actual2 = q1.dotProduct(q2);

        Assert.assertEquals(expected, actual1, EPS);
        Assert.assertEquals(expected, actual2, EPS);
    }

    @Test
    public final void testScalarMultiplyDouble() {
        // expected outputs
        final double w = 1.6;
        final double x = -4.8;
        final double y = 11.20;
        final double z = 2.56;
        // inputs
        final Quaternion q1 = new Quaternion(0.5, -1.5, 3.5, 0.8);
        final double a = 3.2;

        final Quaternion q = q1.multiply(a);

        Assert.assertEquals(w, q.getQ0(), COMPARISON_EPS);
        Assert.assertEquals(x, q.getQ1(), COMPARISON_EPS);
        Assert.assertEquals(y, q.getQ2(), COMPARISON_EPS);
        Assert.assertEquals(z, q.getQ3(), COMPARISON_EPS);
    }

    @Test
    public final void testAddQuaternionQuaternion() {
        // expected outputs
        final double w = 4;
        final double x = -1;
        final double y = 2;
        final double z = -4;
        // inputs
        final Quaternion q1 = new Quaternion(1., 2., -2., -1.);
        final Quaternion q2 = new Quaternion(3., -3., 4., -3.);

        final Quaternion qa = Quaternion.add(q1, q2);
        final Quaternion qb = q1.add(q2);

        Assert.assertEquals(w, qa.getQ0(), EPS);
        Assert.assertEquals(x, qa.getQ1(), EPS);
        Assert.assertEquals(y, qa.getQ2(), EPS);
        Assert.assertEquals(z, qa.getQ3(), EPS);

        Assert.assertEquals(w, qb.getQ0(), EPS);
        Assert.assertEquals(x, qb.getQ1(), EPS);
        Assert.assertEquals(y, qb.getQ2(), EPS);
        Assert.assertEquals(z, qb.getQ3(), EPS);
    }

    @Test
    public final void testSubtractQuaternionQuaternion() {
        // expected outputs
        final double w = -2.;
        final double x = 5.;
        final double y = -6.;
        final double z = 2.;
        // inputs
        final Quaternion q1 = new Quaternion(1., 2., -2., -1.);
        final Quaternion q2 = new Quaternion(3., -3., 4., -3.);

        final Quaternion qa = Quaternion.subtract(q1, q2);
        final Quaternion qb = q1.subtract(q2);

        Assert.assertEquals(w, qa.getQ0(), EPS);
        Assert.assertEquals(x, qa.getQ1(), EPS);
        Assert.assertEquals(y, qa.getQ2(), EPS);
        Assert.assertEquals(z, qa.getQ3(), EPS);

        Assert.assertEquals(w, qb.getQ0(), EPS);
        Assert.assertEquals(x, qb.getQ1(), EPS);
        Assert.assertEquals(y, qb.getQ2(), EPS);
        Assert.assertEquals(z, qb.getQ3(), EPS);
}

    @Test
    public final void testNorm() {

        final double q0 = 2;
        final double q1 = 1;
        final double q2 = -4;
        final double q3 = 3;
        final Quaternion q = new Quaternion(q0, q1, q2, q3);

        final double norm = q.getNorm();

        Assert.assertEquals(FastMath.sqrt(30), norm, 0);

        final double normSquareRef = Quaternion.multiply(q, q.getConjugate()).getScalarPart();
        Assert.assertEquals(FastMath.sqrt(normSquareRef), norm, 0);
    }

    @Test
    public final void testNormalize() {

        final Quaternion q = new Quaternion(2, 1, -4, -2);

        final Quaternion versor = q.normalize();

        Assert.assertEquals(2.0 / 5.0, versor.getQ0(), 0);
        Assert.assertEquals(1.0 / 5.0, versor.getQ1(), 0);
        Assert.assertEquals(-4.0 / 5.0, versor.getQ2(), 0);
        Assert.assertEquals(-2.0 / 5.0, versor.getQ3(), 0);

        Assert.assertEquals(1, versor.getNorm(), 0);
    }

    @Test(expected=ZeroException.class)
    public final void testNormalizeFail() {
        final Quaternion zeroQ = new Quaternion(0, 0, 0, 0);
        zeroQ.normalize();
    }

    @Test
    public final void testObjectEquals() {
        final double one = 1;
        final Quaternion q1 = new Quaternion(one, one, one, one);
        Assert.assertTrue(q1.equals(q1));

        final Quaternion q2 = new Quaternion(one, one, one, one);
        Assert.assertTrue(q2.equals(q1));

        final Quaternion q3 = new Quaternion(one, FastMath.nextUp(one), one, one);
        Assert.assertFalse(q3.equals(q1));
    }

    @Test
    public final void testQuaternionEquals() {
        final double inc = 1e-5;
        final Quaternion q1 = new Quaternion(2, 1, -4, -2);
        final Quaternion q2 = new Quaternion(q1.getQ0() + inc, q1.getQ1(), q1.getQ2(), q1.getQ3());
        final Quaternion q3 = new Quaternion(q1.getQ0(), q1.getQ1() + inc, q1.getQ2(), q1.getQ3());
        final Quaternion q4 = new Quaternion(q1.getQ0(), q1.getQ1(), q1.getQ2() + inc, q1.getQ3());
        final Quaternion q5 = new Quaternion(q1.getQ0(), q1.getQ1(), q1.getQ2(), q1.getQ3() + inc);

        Assert.assertFalse(q1.equals(q2, 0.9 * inc));
        Assert.assertFalse(q1.equals(q3, 0.9 * inc));
        Assert.assertFalse(q1.equals(q4, 0.9 * inc));
        Assert.assertFalse(q1.equals(q5, 0.9 * inc));

        Assert.assertTrue(q1.equals(q2, 1.1 * inc));
        Assert.assertTrue(q1.equals(q3, 1.1 * inc));
        Assert.assertTrue(q1.equals(q4, 1.1 * inc));
        Assert.assertTrue(q1.equals(q5, 1.1 * inc));
    }

    @Test
    public final void testQuaternionEquals2() {
        final Quaternion q1 = new Quaternion(1, 4, 2, 3);
        final double gap = 1e-5;
        final Quaternion q2 = new Quaternion(1 + gap, 4 + gap, 2 + gap, 3 + gap);

        Assert.assertTrue(q1.equals(q2, 10 * gap));
        Assert.assertFalse(q1.equals(q2, gap));
        Assert.assertFalse(q1.equals(q2, gap / 10));
    }

    @Test
    public final void testIsUnitQuaternion() {
        final Random r = new Random(48);
        final int numberOfTrials = 1000;
        for (int i = 0; i < numberOfTrials; i++) {
            final Quaternion q1 = new Quaternion(r.nextDouble(), r.nextDouble(), r.nextDouble(), r.nextDouble());
            final Quaternion q2 = q1.normalize();
            Assert.assertTrue(q2.isUnitQuaternion(COMPARISON_EPS));
        }

        final Quaternion q = new Quaternion(1, 1, 1, 1);
        Assert.assertFalse(q.isUnitQuaternion(COMPARISON_EPS));
    }

    @Test
    public final void testIsPureQuaternion() {
        final Quaternion q1 = new Quaternion(0, 5, 4, 8);
        Assert.assertTrue(q1.isPureQuaternion(EPS));

        final Quaternion q2 = new Quaternion(0 - EPS, 5, 4, 8);
        Assert.assertTrue(q2.isPureQuaternion(EPS));

        final Quaternion q3 = new Quaternion(0 - 1.1 * EPS, 5, 4, 8);
        Assert.assertFalse(q3.isPureQuaternion(EPS));

        final Random r = new Random(48);
        final double[] v = {r.nextDouble(), r.nextDouble(), r.nextDouble()};
        final Quaternion q4 = new Quaternion(v);
        Assert.assertTrue(q4.isPureQuaternion(0));

        final Quaternion q5 = new Quaternion(0, v);
        Assert.assertTrue(q5.isPureQuaternion(0));
    }

    @Test
    public final void testPolarForm() {
        final Random r = new Random(48);
        final int numberOfTrials = 1000;
        for (int i = 0; i < numberOfTrials; i++) {
            final Quaternion q = new Quaternion(2 * (r.nextDouble() - 0.5), 2 * (r.nextDouble() - 0.5),
                                                2 * (r.nextDouble() - 0.5), 2 * (r.nextDouble() - 0.5));
            final Quaternion qP = q.getPositivePolarForm();

            Assert.assertTrue(qP.isUnitQuaternion(COMPARISON_EPS));
            Assert.assertTrue(qP.getQ0() >= 0);

            final Rotation rot = new Rotation(q.getQ0(), q.getQ1(), q.getQ2(), q.getQ3(), true);
            final Rotation rotP = new Rotation(qP.getQ0(), qP.getQ1(), qP.getQ2(), qP.getQ3(), true);

            Assert.assertEquals(rot.getAngle(), rotP.getAngle(), COMPARISON_EPS);
            Assert.assertEquals(rot.getAxis().getX(), rot.getAxis().getX(), COMPARISON_EPS);
            Assert.assertEquals(rot.getAxis().getY(), rot.getAxis().getY(), COMPARISON_EPS);
            Assert.assertEquals(rot.getAxis().getZ(), rot.getAxis().getZ(), COMPARISON_EPS);
        }
    }

    @Test
    public final void testGetInverse() {
        final Quaternion q = new Quaternion(1.5, 4, 2, -2.5);

        final Quaternion inverseQ = q.getInverse();
        Assert.assertEquals(1.5 / 28.5, inverseQ.getQ0(), 0);
        Assert.assertEquals(-4.0 / 28.5, inverseQ.getQ1(), 0);
        Assert.assertEquals(-2.0 / 28.5, inverseQ.getQ2(), 0);
        Assert.assertEquals(2.5 / 28.5, inverseQ.getQ3(), 0);

        final Quaternion product = Quaternion.multiply(inverseQ, q);
        Assert.assertEquals(1, product.getQ0(), EPS);
        Assert.assertEquals(0, product.getQ1(), EPS);
        Assert.assertEquals(0, product.getQ2(), EPS);
        Assert.assertEquals(0, product.getQ3(), EPS);

        final Quaternion qNul = new Quaternion(0, 0, 0, 0);
        try {
            final Quaternion inverseQNul = qNul.getInverse();
            Assert.fail("expecting ZeroException but got : " + inverseQNul);
        } catch (ZeroException ex) {
            // expected
        }
    }

    @Test
    public final void testToString() {
        final Quaternion q = new Quaternion(1, 2, 3, 4);
        Assert.assertTrue(q.toString().equals("[1.0 2.0 3.0 4.0]"));
    }
}
