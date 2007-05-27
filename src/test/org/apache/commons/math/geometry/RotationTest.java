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

import org.apache.commons.math.geometry.CardanEulerSingularityException;
import org.apache.commons.math.geometry.NotARotationMatrixException;
import org.apache.commons.math.geometry.Rotation;
import org.apache.commons.math.geometry.RotationOrder;
import org.apache.commons.math.geometry.Vector3D;

import junit.framework.*;

public class RotationTest
  extends TestCase {

  public RotationTest(String name) {
    super(name);
  }

  public void testIdentity() {

    Rotation r = new Rotation();
    checkVector(r.applyTo(Vector3D.plusI), Vector3D.plusI);
    checkVector(r.applyTo(Vector3D.plusJ), Vector3D.plusJ);
    checkVector(r.applyTo(Vector3D.plusK), Vector3D.plusK);
    checkAngle(r.getAngle(), 0);

    r = new Rotation(-1, 0, 0, 0, false);
    checkVector(r.applyTo(Vector3D.plusI), Vector3D.plusI);
    checkVector(r.applyTo(Vector3D.plusJ), Vector3D.plusJ);
    checkVector(r.applyTo(Vector3D.plusK), Vector3D.plusK);
    checkAngle(r.getAngle(), 0);

    r = new Rotation(42, 0, 0, 0, true);
    checkVector(r.applyTo(Vector3D.plusI), Vector3D.plusI);
    checkVector(r.applyTo(Vector3D.plusJ), Vector3D.plusJ);
    checkVector(r.applyTo(Vector3D.plusK), Vector3D.plusK);
    checkAngle(r.getAngle(), 0);

  }

  public void testAxisAngle() {

    Rotation r = new Rotation(new Vector3D(10, 10, 10), 2 * Math.PI / 3);
    checkVector(r.applyTo(Vector3D.plusI), Vector3D.plusJ);
    checkVector(r.applyTo(Vector3D.plusJ), Vector3D.plusK);
    checkVector(r.applyTo(Vector3D.plusK), Vector3D.plusI);
    double s = 1 / Math.sqrt(3);
    checkVector(r.getAxis(), new Vector3D(s, s, s));
    checkAngle(r.getAngle(), 2 * Math.PI / 3);

    try {
      new Rotation(new Vector3D(0, 0, 0), 2 * Math.PI / 3);
      fail("an exception should have been thrown");
    } catch (ArithmeticException e) {
    } catch (Exception e) {
      fail("unexpected exception");
    }

    r = new Rotation(Vector3D.plusK, 1.5 * Math.PI);
    checkVector(r.getAxis(), new Vector3D(0, 0, -1));
    checkAngle(r.getAngle(), 0.5 * Math.PI);

    r = new Rotation(Vector3D.plusJ, Math.PI);
    checkVector(r.getAxis(), Vector3D.plusJ);
    checkAngle(r.getAngle(), Math.PI);

  }

  public void testVectorOnePair() {

    Vector3D u = new Vector3D(3, 2, 1);
    Vector3D v = new Vector3D(-4, 2, 2);
    Rotation r = new Rotation(u, v);
    checkVector(r.applyTo(u.multiply(v.getNorm())), v.multiply(u.getNorm()));

    checkAngle(new Rotation(u, u.negate()).getAngle(), Math.PI);

  }

  public void testVectorTwoPairs() {

    Vector3D u1 = new Vector3D(3, 0, 0);
    Vector3D u2 = new Vector3D(0, 5, 0);
    Vector3D v1 = new Vector3D(0, 0, 2);
    Vector3D v2 = new Vector3D(-2, 0, 2);
    Rotation r = new Rotation(u1, u2, v1, v2);
    checkVector(r.applyTo(Vector3D.plusI), Vector3D.plusK);
    checkVector(r.applyTo(Vector3D.plusJ), Vector3D.minusI);

    r = new Rotation(u1, u2, u1.negate(), u2.negate());
    Vector3D axis = r.getAxis();
    if (Vector3D.dotProduct(axis, Vector3D.plusK) > 0) {
      checkVector(axis, Vector3D.plusK);
    } else {
      checkVector(axis, Vector3D.minusK);
    }
    checkAngle(r.getAngle(), Math.PI);

  }

  public void testMatrix()
    throws NotARotationMatrixException {

    double[][] m1 = { { 0.0, 1.0, 0.0 },
                      { 0.0, 0.0, 1.0 },
                      { 1.0, 0.0, 0.0 } };
    Rotation r = new Rotation(m1, 1.0e-7);
    checkVector(r.applyTo(Vector3D.plusI), Vector3D.plusK);
    checkVector(r.applyTo(Vector3D.plusJ), Vector3D.plusI);
    checkVector(r.applyTo(Vector3D.plusK), Vector3D.plusJ);

    double[][] m2 = { { 0.83203, -0.55012, -0.07139 },
                      { 0.48293,  0.78164, -0.39474 },
                      { 0.27296,  0.29396,  0.91602 } };
    r = new Rotation(m2, 1.0e-12);

    double[][] m3 = r.getMatrix();
    double d00 = m2[0][0] - m3[0][0];
    double d01 = m2[0][1] - m3[0][1];
    double d02 = m2[0][2] - m3[0][2];
    double d10 = m2[1][0] - m3[1][0];
    double d11 = m2[1][1] - m3[1][1];
    double d12 = m2[1][2] - m3[1][2];
    double d20 = m2[2][0] - m3[2][0];
    double d21 = m2[2][1] - m3[2][1];
    double d22 = m2[2][2] - m3[2][2];

    assertTrue(Math.abs(d00) < 6.0e-6);
    assertTrue(Math.abs(d01) < 6.0e-6);
    assertTrue(Math.abs(d02) < 6.0e-6);
    assertTrue(Math.abs(d10) < 6.0e-6);
    assertTrue(Math.abs(d11) < 6.0e-6);
    assertTrue(Math.abs(d12) < 6.0e-6);
    assertTrue(Math.abs(d20) < 6.0e-6);
    assertTrue(Math.abs(d21) < 6.0e-6);
    assertTrue(Math.abs(d22) < 6.0e-6);

    assertTrue(Math.abs(d00) > 4.0e-7);
    assertTrue(Math.abs(d01) > 4.0e-7);
    assertTrue(Math.abs(d02) > 4.0e-7);
    assertTrue(Math.abs(d10) > 4.0e-7);
    assertTrue(Math.abs(d11) > 4.0e-7);
    assertTrue(Math.abs(d12) > 4.0e-7);
    assertTrue(Math.abs(d20) > 4.0e-7);
    assertTrue(Math.abs(d21) > 4.0e-7);
    assertTrue(Math.abs(d22) > 4.0e-7);

    for (int i = 0; i < 3; ++i) {
      for (int j = 0; j < 3; ++j) {
        double m3tm3 = m3[i][0] * m3[j][0]
                     + m3[i][1] * m3[j][1]
                     + m3[i][2] * m3[j][2];
        if (i == j) {
          assertTrue(Math.abs(m3tm3 - 1.0) < 1.0e-10);
        } else {
          assertTrue(Math.abs(m3tm3) < 1.0e-10);
        }
      }
    }

    checkVector(r.applyTo(Vector3D.plusI),
                new Vector3D(m3[0][0], m3[1][0], m3[2][0]));
    checkVector(r.applyTo(Vector3D.plusJ),
                new Vector3D(m3[0][1], m3[1][1], m3[2][1]));
    checkVector(r.applyTo(Vector3D.plusK),
                new Vector3D(m3[0][2], m3[1][2], m3[2][2]));

    double[][] m4 = { { 1.0,  0.0,  0.0 },
                      { 0.0, -1.0,  0.0 },
                      { 0.0,  0.0, -1.0 } };
    r = new Rotation(m4, 1.0e-7);
    checkAngle(r.getAngle(), Math.PI);

    try {
      double[][] m5 = { { 0.0, 0.0, 1.0 },
                        { 0.0, 1.0, 0.0 },
                        { 1.0, 0.0, 0.0 } };
      r = new Rotation(m5, 1.0e-7);
      fail("got " + r + ", should have caught an exception");
    } catch (NotARotationMatrixException e) {
      // expected
    } catch (Exception e) {
      fail("wrong exception caught");
    }

  }

  public void testAngles()
    throws CardanEulerSingularityException {

    RotationOrder[] CardanOrders = {
      RotationOrder.XYZ, RotationOrder.XZY, RotationOrder.YXZ,
      RotationOrder.YZX, RotationOrder.ZXY, RotationOrder.ZYX
    };

    RotationOrder[] EulerOrders = {
      RotationOrder.XYX, RotationOrder.XZX, RotationOrder.YXY,
      RotationOrder.YZY, RotationOrder.ZXZ, RotationOrder.ZYZ
    };

    for (int i = 0; i < CardanOrders.length; ++i) {
      for (double alpha1 = 0.1; alpha1 < 6.2; alpha1 += 0.3) {
        for (double alpha2 = -1.55; alpha2 < 1.55; alpha2 += 0.3) {
          for (double alpha3 = 0.1; alpha3 < 6.2; alpha3 += 0.3) {
            Rotation r = new Rotation(CardanOrders[i],
                                      alpha1, alpha2, alpha3);
            double[] angles = r.getAngles(CardanOrders[i]);
            checkAngle(angles[0], alpha1);
            checkAngle(angles[1], alpha2);
            checkAngle(angles[2], alpha3);
          }
        }
      }
    }

    for (int i = 0; i < EulerOrders.length; ++i) {
      for (double alpha1 = 0.1; alpha1 < 6.2; alpha1 += 0.3) {
        for (double alpha2 = 0.05; alpha2 < 3.1; alpha2 += 0.3) {
          for (double alpha3 = 0.1; alpha3 < 6.2; alpha3 += 0.3) {
            Rotation r = new Rotation(EulerOrders[i],
                                      alpha1, alpha2, alpha3);
            double[] angles = r.getAngles(EulerOrders[i]);
            checkAngle(angles[0], alpha1);
            checkAngle(angles[1], alpha2);
            checkAngle(angles[2], alpha3);
          }
        }
      }
    }

  }

  public void testQuaternion() {
    Rotation r1 = new Rotation(new Vector3D(2, -3, 5), 1.7);
    double n = 23.5;
    Rotation r2 = new Rotation(n * r1.getQ0(), n * r1.getQ1(),
                               n * r1.getQ2(), n * r1.getQ3(),
                               true);
    for (double x = -0.9; x < 0.9; x += 0.2) {
      for (double y = -0.9; y < 0.9; y += 0.2) {
        for (double z = -0.9; z < 0.9; z += 0.2) {
          Vector3D u = new Vector3D(x, y, z);
          checkVector(r2.applyTo(u), r1.applyTo(u));
        }
      }
    }
  }

  public void testCompose() {

    Rotation r1 = new Rotation(new Vector3D(2, -3, 5), 1.7);
    Rotation r2 = new Rotation(new Vector3D(-1, 3, 2), 0.3);
    Rotation r3 = r2.applyTo(r1);

    for (double x = -0.9; x < 0.9; x += 0.2) {
      for (double y = -0.9; y < 0.9; y += 0.2) {
        for (double z = -0.9; z < 0.9; z += 0.2) {
          Vector3D u = new Vector3D(x, y, z);
          checkVector(r2.applyTo(r1.applyTo(u)), r3.applyTo(u));
        }
      }
    }

  }

  public void testComposeInverse() {

    Rotation r1 = new Rotation(new Vector3D(2, -3, 5), 1.7);
    Rotation r2 = new Rotation(new Vector3D(-1, 3, 2), 0.3);
    Rotation r3 = r2.applyInverseTo(r1);

    for (double x = -0.9; x < 0.9; x += 0.2) {
      for (double y = -0.9; y < 0.9; y += 0.2) {
        for (double z = -0.9; z < 0.9; z += 0.2) {
          Vector3D u = new Vector3D(x, y, z);
          checkVector(r2.applyInverseTo(r1.applyTo(u)), r3.applyTo(u));
        }
      }
    }

  }

  public void testApplyInverseTo() {

    Rotation r = new Rotation(new Vector3D(2, -3, 5), 1.7);
    for (double lambda = 0; lambda < 6.2; lambda += 0.2) {
      for (double phi = -1.55; phi < 1.55; phi += 0.2) {
          Vector3D u = new Vector3D(Math.cos(lambda) * Math.cos(phi),
                                    Math.sin(lambda) * Math.cos(phi),
                                    Math.sin(phi));
          r.applyInverseTo(r.applyTo(u));
          checkVector(u, r.applyInverseTo(r.applyTo(u)));
          checkVector(u, r.applyTo(r.applyInverseTo(u)));
      }
    }

    r = new Rotation();
    for (double lambda = 0; lambda < 6.2; lambda += 0.2) {
      for (double phi = -1.55; phi < 1.55; phi += 0.2) {
          Vector3D u = new Vector3D(Math.cos(lambda) * Math.cos(phi),
                                    Math.sin(lambda) * Math.cos(phi),
                                    Math.sin(phi));
          checkVector(u, r.applyInverseTo(r.applyTo(u)));
          checkVector(u, r.applyTo(r.applyInverseTo(u)));
      }
    }

    r = new Rotation(Vector3D.plusK, Math.PI);
    for (double lambda = 0; lambda < 6.2; lambda += 0.2) {
      for (double phi = -1.55; phi < 1.55; phi += 0.2) {
          Vector3D u = new Vector3D(Math.cos(lambda) * Math.cos(phi),
                                    Math.sin(lambda) * Math.cos(phi),
                                    Math.sin(phi));
          checkVector(u, r.applyInverseTo(r.applyTo(u)));
          checkVector(u, r.applyTo(r.applyInverseTo(u)));
      }
    }

  }

  private void checkVector(Vector3D v1, Vector3D v2) {
    assertTrue(v1.subtract(v2).getNorm() < 1.0e-10);
  }

  private void checkAngle(double a1, double a2) {
    a2 -= 2 * Math.PI * Math.floor((a2 + Math.PI - a1) / (2 * Math.PI));
    assertTrue(Math.abs(a1 - a2) < 1.0e-10);
  }

  public static Test suite() {
    return new TestSuite(RotationTest.class);
  }

}
