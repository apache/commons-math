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

import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

public class SphericalCoordinatesTest {

    @Test
    public void testCoordinatesStoC() throws DimensionMismatchException {
        double piO2 = 0.5 * FastMath.PI;
        SphericalCoordinates sc1 = new SphericalCoordinates(2.0, 0, piO2);
        Assert.assertEquals(0, sc1.getCartesian().distance(new Vector3D(2, 0, 0)), 1.0e-10);
        SphericalCoordinates sc2 = new SphericalCoordinates(2.0, piO2, piO2);
        Assert.assertEquals(0, sc2.getCartesian().distance(new Vector3D(0, 2, 0)), 1.0e-10);
        SphericalCoordinates sc3 = new SphericalCoordinates(2.0, FastMath.PI, piO2);
        Assert.assertEquals(0, sc3.getCartesian().distance(new Vector3D(-2, 0, 0)), 1.0e-10);
        SphericalCoordinates sc4 = new SphericalCoordinates(2.0, -piO2, piO2);
        Assert.assertEquals(0, sc4.getCartesian().distance(new Vector3D(0, -2, 0)), 1.0e-10);
        SphericalCoordinates sc5 = new SphericalCoordinates(2.0, 1.23456, 0);
        Assert.assertEquals(0, sc5.getCartesian().distance(new Vector3D(0, 0, 2)), 1.0e-10);
        SphericalCoordinates sc6 = new SphericalCoordinates(2.0, 6.54321, FastMath.PI);
        Assert.assertEquals(0, sc6.getCartesian().distance(new Vector3D(0, 0, -2)), 1.0e-10);
    }

    @Test
    public void testCoordinatesCtoS() throws DimensionMismatchException {
        double piO2 = 0.5 * FastMath.PI;
        SphericalCoordinates sc1 = new SphericalCoordinates(new Vector3D(2, 0, 0));
        Assert.assertEquals(2,           sc1.getR(),     1.0e-10);
        Assert.assertEquals(0,           sc1.getTheta(), 1.0e-10);
        Assert.assertEquals(piO2,        sc1.getPhi(),   1.0e-10);
        SphericalCoordinates sc2 = new SphericalCoordinates(new Vector3D(0, 2, 0));
        Assert.assertEquals(2,           sc2.getR(),     1.0e-10);
        Assert.assertEquals(piO2,        sc2.getTheta(), 1.0e-10);
        Assert.assertEquals(piO2,        sc2.getPhi(),   1.0e-10);
        SphericalCoordinates sc3 = new SphericalCoordinates(new Vector3D(-2, 0, 0));
        Assert.assertEquals(2,           sc3.getR(),     1.0e-10);
        Assert.assertEquals(FastMath.PI, sc3.getTheta(), 1.0e-10);
        Assert.assertEquals(piO2,        sc3.getPhi(),   1.0e-10);
        SphericalCoordinates sc4 = new SphericalCoordinates(new Vector3D(0, -2, 0));
        Assert.assertEquals(2,           sc4.getR(),     1.0e-10);
        Assert.assertEquals(-piO2,       sc4.getTheta(), 1.0e-10);
        Assert.assertEquals(piO2,        sc4.getPhi(),   1.0e-10);
        SphericalCoordinates sc5 = new SphericalCoordinates(new Vector3D(0, 0, 2));
        Assert.assertEquals(2,           sc5.getR(),     1.0e-10);
        //  don't check theta on poles, as it is singular
        Assert.assertEquals(0,           sc5.getPhi(),   1.0e-10);
        SphericalCoordinates sc6 = new SphericalCoordinates(new Vector3D(0, 0, -2));
        Assert.assertEquals(2,           sc6.getR(),     1.0e-10);
        //  don't check theta on poles, as it is singular
        Assert.assertEquals(FastMath.PI, sc6.getPhi(),   1.0e-10);
    }

    @Test
    public void testGradient() {
        for (double r = 0.2; r < 10; r += 0.5) {
            for (double theta = 0; theta < 2 * FastMath.PI; theta += 0.1) {
                for (double phi = 0.1; phi < FastMath.PI; phi += 0.1) {
                    SphericalCoordinates sc = new SphericalCoordinates(r, theta, phi);

                    DerivativeStructure svalue = valueSpherical(new DerivativeStructure(3, 1, 0, r),
                                                                new DerivativeStructure(3, 1, 1, theta),
                                                                new DerivativeStructure(3, 1, 2, phi));
                    double[] sGradient = new double[] {
                        svalue.getPartialDerivative(1, 0, 0),
                        svalue.getPartialDerivative(0, 1, 0),
                        svalue.getPartialDerivative(0, 0, 1),
                    };

                    DerivativeStructure cvalue = valueCartesian(new DerivativeStructure(3, 1, 0, sc.getCartesian().getX()),
                                                                new DerivativeStructure(3, 1, 1, sc.getCartesian().getY()),
                                                                new DerivativeStructure(3, 1, 2, sc.getCartesian().getZ()));
                    Vector3D refCGradient = new Vector3D(cvalue.getPartialDerivative(1, 0, 0),
                                                         cvalue.getPartialDerivative(0, 1, 0),
                                                         cvalue.getPartialDerivative(0, 0, 1));

                    Vector3D testCGradient = new Vector3D(sc.toCartesianGradient(sGradient));

                    Assert.assertEquals(0, testCGradient.distance(refCGradient) / refCGradient.getNorm(), 5.0e-14);

                }
            }
        }
    }

    @Test
    public void testHessian() {
        for (double r = 0.2; r < 10; r += 0.5) {
            for (double theta = 0; theta < 2 * FastMath.PI; theta += 0.2) {
                for (double phi = 0.1; phi < FastMath.PI; phi += 0.2) {
                    SphericalCoordinates sc = new SphericalCoordinates(r, theta, phi);

                    DerivativeStructure svalue = valueSpherical(new DerivativeStructure(3, 2, 0, r),
                                                                new DerivativeStructure(3, 2, 1, theta),
                                                                new DerivativeStructure(3, 2, 2, phi));
                    double[] sGradient = new double[] {
                        svalue.getPartialDerivative(1, 0, 0),
                        svalue.getPartialDerivative(0, 1, 0),
                        svalue.getPartialDerivative(0, 0, 1),
                    };
                    double[][] sHessian = new double[3][3];
                    sHessian[0][0] = svalue.getPartialDerivative(2, 0, 0); // d2F/dR2
                    sHessian[1][0] = svalue.getPartialDerivative(1, 1, 0); // d2F/dRdTheta
                    sHessian[2][0] = svalue.getPartialDerivative(1, 0, 1); // d2F/dRdPhi
                    sHessian[0][1] = Double.NaN; // just to check upper-right part is not used
                    sHessian[1][1] = svalue.getPartialDerivative(0, 2, 0); // d2F/dTheta2
                    sHessian[2][1] = svalue.getPartialDerivative(0, 1, 1); // d2F/dThetadPhi
                    sHessian[0][2] = Double.NaN; // just to check upper-right part is not used
                    sHessian[1][2] = Double.NaN; // just to check upper-right part is not used
                    sHessian[2][2] = svalue.getPartialDerivative(0, 0, 2); // d2F/dPhi2

                    DerivativeStructure cvalue = valueCartesian(new DerivativeStructure(3, 2, 0, sc.getCartesian().getX()),
                                                                new DerivativeStructure(3, 2, 1, sc.getCartesian().getY()),
                                                                new DerivativeStructure(3, 2, 2, sc.getCartesian().getZ()));
                    double[][] refCHessian = new double[3][3];
                    refCHessian[0][0] = cvalue.getPartialDerivative(2, 0, 0); // d2F/dX2
                    refCHessian[1][0] = cvalue.getPartialDerivative(1, 1, 0); // d2F/dXdY
                    refCHessian[2][0] = cvalue.getPartialDerivative(1, 0, 1); // d2F/dXdZ
                    refCHessian[0][1] = refCHessian[1][0];
                    refCHessian[1][1] = cvalue.getPartialDerivative(0, 2, 0); // d2F/dY2
                    refCHessian[2][1] = cvalue.getPartialDerivative(0, 1, 1); // d2F/dYdZ
                    refCHessian[0][2] = refCHessian[2][0];
                    refCHessian[1][2] = refCHessian[2][1];
                    refCHessian[2][2] = cvalue.getPartialDerivative(0, 0, 2); // d2F/dZ2
                    double norm =  0;
                    for (int i = 0; i < 3; ++i) {
                        for (int j = 0; j < 3; ++j) {
                            norm = FastMath.max(norm, FastMath.abs(refCHessian[i][j]));
                        }
                    }

                    double[][] testCHessian = sc.toCartesianHessian(sHessian, sGradient);
                    for (int i = 0; i < 3; ++i) {
                        for (int j = 0; j < 3; ++j) {
                            Assert.assertEquals("" + FastMath.abs((refCHessian[i][j] - testCHessian[i][j]) / norm),
                                                refCHessian[i][j], testCHessian[i][j], 1.0e-14 * norm);
                        }
                    }

                }
            }
        }
    }

    public DerivativeStructure valueCartesian(DerivativeStructure x, DerivativeStructure y, DerivativeStructure z) {
        return x.divide(y.multiply(5).add(10)).multiply(z.pow(3));
    }

    public DerivativeStructure valueSpherical(DerivativeStructure r, DerivativeStructure theta, DerivativeStructure phi) {
        return valueCartesian(r.multiply(theta.cos()).multiply(phi.sin()),
                              r.multiply(theta.sin()).multiply(phi.sin()),
                              r.multiply(phi.cos()));
    }

    @Test
    public void testSerialization() {
        SphericalCoordinates a = new SphericalCoordinates(3, 2, 1);
        SphericalCoordinates b = (SphericalCoordinates) TestUtils.serializeAndRecover(a);
        Assert.assertEquals(0, a.getCartesian().distance(b.getCartesian()), 1.0e-10);
        Assert.assertEquals(a.getR(),     b.getR(),     1.0e-10);
        Assert.assertEquals(a.getTheta(), b.getTheta(), 1.0e-10);
        Assert.assertEquals(a.getPhi(),   b.getPhi(),   1.0e-10);
    }

}
