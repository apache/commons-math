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

package org.apache.commons.math.linear;

import org.junit.Assert;
import org.junit.Test;

public class SingularValueSolverTest {

    private double[][] testSquare = {
            { 24.0 / 25.0, 43.0 / 25.0 },
            { 57.0 / 25.0, 24.0 / 25.0 }
    };

    private static final double normTolerance = 10e-14;

    /** test solve dimension errors */
    @Test
    public void testSolveDimensionErrors() {
        DecompositionSolver solver =
            new SingularValueDecompositionImpl(MatrixUtils.createRealMatrix(testSquare)).getSolver();
        RealMatrix b = MatrixUtils.createRealMatrix(new double[3][2]);
        try {
            solver.solve(b);
            Assert.fail("an exception should have been thrown");
        } catch (IllegalArgumentException iae) {
            // expected behavior
        } catch (Exception e) {
            Assert.fail("wrong exception caught");
        }
        try {
            solver.solve(b.getColumn(0));
            Assert.fail("an exception should have been thrown");
        } catch (IllegalArgumentException iae) {
            // expected behavior
        } catch (Exception e) {
            Assert.fail("wrong exception caught");
        }
        try {
            solver.solve(new ArrayRealVectorTest.RealVectorTestImpl(b.getColumn(0)));
            Assert.fail("an exception should have been thrown");
        } catch (IllegalArgumentException iae) {
            // expected behavior
        } catch (Exception e) {
            Assert.fail("wrong exception caught");
        }
    }

    /** test least square solve */
    @Test
    public void testLeastSquareSolve() {
        RealMatrix m =
            MatrixUtils.createRealMatrix(new double[][] {
                                   { 1.0, 0.0 },
                                   { 0.0, 0.0 }
                               });
        DecompositionSolver solver = new SingularValueDecompositionImpl(m).getSolver();
        RealMatrix b = MatrixUtils.createRealMatrix(new double[][] {
            { 11, 12 }, { 21, 22 } 
        });
        RealMatrix xMatrix = solver.solve(b);
        Assert.assertEquals(11, xMatrix.getEntry(0, 0), 1.0e-15);
        Assert.assertEquals(12, xMatrix.getEntry(0, 1), 1.0e-15);
        Assert.assertEquals(0, xMatrix.getEntry(1, 0), 1.0e-15);
        Assert.assertEquals(0, xMatrix.getEntry(1, 1), 1.0e-15);
        double[] xCol = solver.solve(b.getColumn(0));
        Assert.assertEquals(11, xCol[0], 1.0e-15);
        Assert.assertEquals(0, xCol[1], 1.0e-15);
        RealVector xColVec = solver.solve(b.getColumnVector(0));
        Assert.assertEquals(11, xColVec.getEntry(0), 1.0e-15);
        Assert.assertEquals(0, xColVec.getEntry(1), 1.0e-15);
        RealVector xColOtherVec = solver.solve(new ArrayRealVectorTest.RealVectorTestImpl(b.getColumn(0)));
        Assert.assertEquals(11, xColOtherVec.getEntry(0), 1.0e-15);
        Assert.assertEquals(0, xColOtherVec.getEntry(1), 1.0e-15);
    }

    /** test solve */
    @Test
    public void testSolve() {
        DecompositionSolver solver =
            new SingularValueDecompositionImpl(MatrixUtils.createRealMatrix(testSquare)).getSolver();
        RealMatrix b = MatrixUtils.createRealMatrix(new double[][] {
                { 1, 2, 3 }, { 0, -5, 1 }
        });
        RealMatrix xRef = MatrixUtils.createRealMatrix(new double[][] {
                { -8.0 / 25.0, -263.0 / 75.0, -29.0 / 75.0 },
                { 19.0 / 25.0,   78.0 / 25.0,  49.0 / 25.0 }
        });

        // using RealMatrix
        Assert.assertEquals(0, solver.solve(b).subtract(xRef).getNorm(), normTolerance);

        // using double[]
        for (int i = 0; i < b.getColumnDimension(); ++i) {
            Assert.assertEquals(0,
                         new ArrayRealVector(solver.solve(b.getColumn(i))).subtract(xRef.getColumnVector(i)).getNorm(),
                         1.0e-13);
        }

        // using Array2DRowRealMatrix
        for (int i = 0; i < b.getColumnDimension(); ++i) {
            Assert.assertEquals(0,
                         solver.solve(b.getColumnVector(i)).subtract(xRef.getColumnVector(i)).getNorm(),
                         1.0e-13);
        }

        // using RealMatrix with an alternate implementation
        for (int i = 0; i < b.getColumnDimension(); ++i) {
            ArrayRealVectorTest.RealVectorTestImpl v =
                new ArrayRealVectorTest.RealVectorTestImpl(b.getColumn(i));
            Assert.assertEquals(0,
                         solver.solve(v).subtract(xRef.getColumnVector(i)).getNorm(),
                         1.0e-13);
        }

    }

    /** test condition number */
    @Test
    public void testConditionNumber() {
        SingularValueDecompositionImpl svd =
            new SingularValueDecompositionImpl(MatrixUtils.createRealMatrix(testSquare));
        Assert.assertEquals(3.0, svd.getConditionNumber(), 1.0e-15);
    }

    @Test
    public void testTruncated() {

        RealMatrix rm = new Array2DRowRealMatrix(new double[][] {
            { 1.0, 2.0, 3.0 }, { 2.0, 3.0, 4.0 }, { 3.0, 5.0, 7.0 }
        });
        double s439  = Math.sqrt(439.0);
        double[] reference = new double[] {
            Math.sqrt(3.0 * (21.0 + s439))
        };
        SingularValueDecomposition svd =
            new SingularValueDecompositionImpl(rm, 1);

        // check we get the expected theoretical singular values
        double[] singularValues = svd.getSingularValues();
        Assert.assertEquals(reference.length, singularValues.length);
        for (int i = 0; i < reference.length; ++i) {
            Assert.assertEquals(reference[i], singularValues[i], 4.0e-13);
        }

        // check the truncated decomposition DON'T allows to recover the original matrix
        RealMatrix recomposed = svd.getU().multiply(svd.getS()).multiply(svd.getVT());
        Assert.assertTrue(recomposed.subtract(rm).getNorm() > 1.4);

    }

    @Test
    public void testMath320A() {
        RealMatrix rm = new Array2DRowRealMatrix(new double[][] {
            { 1.0, 2.0, 3.0 }, { 2.0, 3.0, 4.0 }, { 3.0, 5.0, 7.0 }
        });
        double s439  = Math.sqrt(439.0);
        double[] reference = new double[] {
            Math.sqrt(3.0 * (21.0 + s439)), Math.sqrt(3.0 * (21.0 - s439))
        };
        SingularValueDecomposition svd =
            new SingularValueDecompositionImpl(rm);

        // check we get the expected theoretical singular values
        double[] singularValues = svd.getSingularValues();
        Assert.assertEquals(reference.length, singularValues.length);
        for (int i = 0; i < reference.length; ++i) {
            Assert.assertEquals(reference[i], singularValues[i], 4.0e-13);
        }

        // check the decomposition allows to recover the original matrix
        RealMatrix recomposed = svd.getU().multiply(svd.getS()).multiply(svd.getVT());
        Assert.assertEquals(0.0, recomposed.subtract(rm).getNorm(), 5.0e-13);

        // check we can solve a singular system
        double[] b = new double[] { 5.0, 6.0, 7.0 };
        double[] resSVD = svd.getSolver().solve(b);
        Assert.assertEquals(rm.getColumnDimension(), resSVD.length);

        // check the solution really minimizes the residuals
        double svdMinResidual = residual(rm, resSVD, b);
        double epsilon = 2 * Math.ulp(svdMinResidual);
        double h = 0.1;
        int    k = 3;
        for (double d0 = -k * h; d0 <= k * h; d0 += h) {
            for (double d1 = -k * h ; d1 <= k * h; d1 += h) {
                for (double d2 = -k * h; d2 <= k * h; d2 += h) {
                    double[] x = new double[] { resSVD[0] + d0, resSVD[1] + d1, resSVD[2] + d2 };
                    Assert.assertTrue((residual(rm, x, b) - svdMinResidual) > -epsilon);
                }
            }
        }

    }

    @Test
    public void testMath320B() {
        RealMatrix rm = new Array2DRowRealMatrix(new double[][] {
            { 1.0, 2.0 }, { 1.0, 2.0 }
        });
        SingularValueDecomposition svd =
            new SingularValueDecompositionImpl(rm);
        RealMatrix recomposed = svd.getU().multiply(svd.getS()).multiply(svd.getVT());
        Assert.assertEquals(0.0, recomposed.subtract(rm).getNorm(), 2.0e-15);
    }

    private double residual(RealMatrix a, double[] x, double[] b) {
        double[] ax = a.operate(x);
        double sum = 0;
        for (int i = 0; i < ax.length; ++i) {
            sum += (ax[i] - b[i]) * (ax[i] - b[i]);
        }
        return Math.sqrt(sum);
    }

}
