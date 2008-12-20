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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class QRSolverTest extends TestCase {
    double[][] testData3x3NonSingular = { 
            { 12, -51, 4 }, 
            { 6, 167, -68 },
            { -4, 24, -41 }, };

    double[][] testData3x3Singular = { 
            { 1, 4, 7, }, 
            { 2, 5, 8, },
            { 3, 6, 9, }, };

    double[][] testData3x4 = { 
            { 12, -51, 4, 1 }, 
            { 6, 167, -68, 2 },
            { -4, 24, -41, 3 }, };

    double[][] testData4x3 = { 
            { 12, -51, 4, }, 
            { 6, 167, -68, },
            { -4, 24, -41, }, 
            { -5, 34, 7, }, };

    public QRSolverTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(QRSolverTest.class);
        suite.setName("QRSolver Tests");
        return suite;
    }

    /** test rank */
    public void testRank() {
        QRSolver solver =
            new QRSolver(new QRDecompositionImpl(MatrixUtils.createRealMatrix(testData3x3NonSingular)));
        assertTrue(solver.isNonSingular());

        solver = new QRSolver(new QRDecompositionImpl(MatrixUtils.createRealMatrix(testData3x3Singular)));
        assertFalse(solver.isNonSingular());

        solver = new QRSolver(new QRDecompositionImpl(MatrixUtils.createRealMatrix(testData3x4)));
        assertTrue(solver.isNonSingular());

        solver = new QRSolver(new QRDecompositionImpl(MatrixUtils.createRealMatrix(testData4x3)));
        assertTrue(solver.isNonSingular());

    }

    /** test solve dimension errors */
    public void testSolveDimensionErrors() {
        QRSolver solver =
            new QRSolver(new QRDecompositionImpl(MatrixUtils.createRealMatrix(testData3x3NonSingular)));
        RealMatrix b = MatrixUtils.createRealMatrix(new double[2][2]);
        try {
            solver.solve(b);
            fail("an exception should have been thrown");
        } catch (IllegalArgumentException iae) {
            // expected behavior
        } catch (Exception e) {
            fail("wrong exception caught");
        }
        try {
            solver.solve(b.getColumn(0));
            fail("an exception should have been thrown");
        } catch (IllegalArgumentException iae) {
            // expected behavior
        } catch (Exception e) {
            fail("wrong exception caught");
        }
        try {
            solver.solve(b.getColumnVector(0));
            fail("an exception should have been thrown");
        } catch (IllegalArgumentException iae) {
            // expected behavior
        } catch (Exception e) {
            fail("wrong exception caught");
        }
    }

    /** test solve rank errors */
    public void testSolveRankErrors() {
        QRSolver solver =
            new QRSolver(new QRDecompositionImpl(MatrixUtils.createRealMatrix(testData3x3Singular)));
        RealMatrix b = MatrixUtils.createRealMatrix(new double[3][2]);
        try {
            solver.solve(b);
            fail("an exception should have been thrown");
        } catch (InvalidMatrixException iae) {
            // expected behavior
        } catch (Exception e) {
            fail("wrong exception caught");
        }
        try {
            solver.solve(b.getColumn(0));
            fail("an exception should have been thrown");
        } catch (InvalidMatrixException iae) {
            // expected behavior
        } catch (Exception e) {
            fail("wrong exception caught");
        }
        try {
            solver.solve(b.getColumnVector(0));
            fail("an exception should have been thrown");
        } catch (InvalidMatrixException iae) {
            // expected behavior
        } catch (Exception e) {
            fail("wrong exception caught");
        }
    }

    /** test solve */
    public void testSolve() {
        QRSolver solver =
            new QRSolver(new QRDecompositionImpl(MatrixUtils.createRealMatrix(testData3x3NonSingular)));
        RealMatrix b = MatrixUtils.createRealMatrix(new double[][] {
                { -102, 12250 }, { 544, 24500 }, { 167, -36750 }
        });
        RealMatrix xRef = MatrixUtils.createRealMatrix(new double[][] {
                { 1, 2515 }, { 2, 422 }, { -3, 898 }
        });

        // using RealMatrix
        assertEquals(0, solver.solve(b).subtract(xRef).getNorm(), 1.0e-13);

        // using double[]
        for (int i = 0; i < b.getColumnDimension(); ++i) {
            assertEquals(0,
                         new RealVectorImpl(solver.solve(b.getColumn(i))).subtract(xRef.getColumnVector(i)).getNorm(),
                         1.0e-13);
        }

        // using RealVectorImpl
        for (int i = 0; i < b.getColumnDimension(); ++i) {
            assertEquals(0,
                         solver.solve(b.getColumnVector(i)).subtract(xRef.getColumnVector(i)).getNorm(),
                         1.0e-13);
        }

        // using RealVector with an alternate implementation
        for (int i = 0; i < b.getColumnDimension(); ++i) {
            RealVectorImplTest.RealVectorTestImpl v =
                new RealVectorImplTest.RealVectorTestImpl(b.getColumn(i));
            assertEquals(0,
                         solver.solve(v).subtract(xRef.getColumnVector(i)).getNorm(),
                         1.0e-13);
        }

    }

}
