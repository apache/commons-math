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
package org.apache.commons.math4.linear;

import java.util.Arrays;

import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.MathUnsupportedOperationException;
import org.apache.commons.math4.exception.MaxCountExceededException;
import org.apache.commons.math4.linear.Array2DRowRealMatrix;
import org.apache.commons.math4.linear.ArrayRealVector;
import org.apache.commons.math4.linear.ConjugateGradient;
import org.apache.commons.math4.linear.IterativeLinearSolver;
import org.apache.commons.math4.linear.IterativeLinearSolverEvent;
import org.apache.commons.math4.linear.JacobiPreconditioner;
import org.apache.commons.math4.linear.NonPositiveDefiniteOperatorException;
import org.apache.commons.math4.linear.NonSquareOperatorException;
import org.apache.commons.math4.linear.PreconditionedIterativeLinearSolver;
import org.apache.commons.math4.linear.RealLinearOperator;
import org.apache.commons.math4.linear.RealVector;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.math4.util.IterationEvent;
import org.apache.commons.math4.util.IterationListener;
import org.junit.Assert;
import org.junit.Test;

public class ConjugateGradientTest {

    @Test(expected = NonSquareOperatorException.class)
    public void testNonSquareOperator() {
        final Array2DRowRealMatrix a = new Array2DRowRealMatrix(2, 3);
        final IterativeLinearSolver solver;
        solver = new ConjugateGradient(10, 0., false);
        final ArrayRealVector b = new ArrayRealVector(a.getRowDimension());
        final ArrayRealVector x = new ArrayRealVector(a.getColumnDimension());
        solver.solve(a, b, x);
    }

    @Test(expected = DimensionMismatchException.class)
    public void testDimensionMismatchRightHandSide() {
        final Array2DRowRealMatrix a = new Array2DRowRealMatrix(3, 3);
        final IterativeLinearSolver solver;
        solver = new ConjugateGradient(10, 0., false);
        final ArrayRealVector b = new ArrayRealVector(2);
        final ArrayRealVector x = new ArrayRealVector(3);
        solver.solve(a, b, x);
    }

    @Test(expected = DimensionMismatchException.class)
    public void testDimensionMismatchSolution() {
        final Array2DRowRealMatrix a = new Array2DRowRealMatrix(3, 3);
        final IterativeLinearSolver solver;
        solver = new ConjugateGradient(10, 0., false);
        final ArrayRealVector b = new ArrayRealVector(3);
        final ArrayRealVector x = new ArrayRealVector(2);
        solver.solve(a, b, x);
    }

    @Test(expected = NonPositiveDefiniteOperatorException.class)
    public void testNonPositiveDefiniteLinearOperator() {
        final Array2DRowRealMatrix a = new Array2DRowRealMatrix(2, 2);
        a.setEntry(0, 0, -1.);
        a.setEntry(0, 1, 2.);
        a.setEntry(1, 0, 3.);
        a.setEntry(1, 1, 4.);
        final IterativeLinearSolver solver;
        solver = new ConjugateGradient(10, 0., true);
        final ArrayRealVector b = new ArrayRealVector(2);
        b.setEntry(0, -1.);
        b.setEntry(1, -1.);
        final ArrayRealVector x = new ArrayRealVector(2);
        solver.solve(a, b, x);
    }

    @Test
    public void testUnpreconditionedSolution() {
        final int n = 5;
        final int maxIterations = 100;
        final RealLinearOperator a = new HilbertMatrix(n);
        final InverseHilbertMatrix ainv = new InverseHilbertMatrix(n);
        final IterativeLinearSolver solver;
        solver = new ConjugateGradient(maxIterations, 1E-10, true);
        final RealVector b = new ArrayRealVector(n);
        for (int j = 0; j < n; j++) {
            b.set(0.);
            b.setEntry(j, 1.);
            final RealVector x = solver.solve(a, b);
            for (int i = 0; i < n; i++) {
                final double actual = x.getEntry(i);
                final double expected = ainv.getEntry(i, j);
                final double delta = 1E-10 * FastMath.abs(expected);
                final String msg = String.format("entry[%d][%d]", i, j);
                Assert.assertEquals(msg, expected, actual, delta);
            }
        }
    }

    @Test
    public void testUnpreconditionedInPlaceSolutionWithInitialGuess() {
        final int n = 5;
        final int maxIterations = 100;
        final RealLinearOperator a = new HilbertMatrix(n);
        final InverseHilbertMatrix ainv = new InverseHilbertMatrix(n);
        final IterativeLinearSolver solver;
        solver = new ConjugateGradient(maxIterations, 1E-10, true);
        final RealVector b = new ArrayRealVector(n);
        for (int j = 0; j < n; j++) {
            b.set(0.);
            b.setEntry(j, 1.);
            final RealVector x0 = new ArrayRealVector(n);
            x0.set(1.);
            final RealVector x = solver.solveInPlace(a, b, x0);
            Assert.assertSame("x should be a reference to x0", x0, x);
            for (int i = 0; i < n; i++) {
                final double actual = x.getEntry(i);
                final double expected = ainv.getEntry(i, j);
                final double delta = 1E-10 * FastMath.abs(expected);
                final String msg = String.format("entry[%d][%d)", i, j);
                Assert.assertEquals(msg, expected, actual, delta);
            }
        }
    }

    @Test
    public void testUnpreconditionedSolutionWithInitialGuess() {
        final int n = 5;
        final int maxIterations = 100;
        final RealLinearOperator a = new HilbertMatrix(n);
        final InverseHilbertMatrix ainv = new InverseHilbertMatrix(n);
        final IterativeLinearSolver solver;
        solver = new ConjugateGradient(maxIterations, 1E-10, true);
        final RealVector b = new ArrayRealVector(n);
        for (int j = 0; j < n; j++) {
            b.set(0.);
            b.setEntry(j, 1.);
            final RealVector x0 = new ArrayRealVector(n);
            x0.set(1.);
            final RealVector x = solver.solve(a, b, x0);
            Assert.assertNotSame("x should not be a reference to x0", x0, x);
            for (int i = 0; i < n; i++) {
                final double actual = x.getEntry(i);
                final double expected = ainv.getEntry(i, j);
                final double delta = 1E-10 * FastMath.abs(expected);
                final String msg = String.format("entry[%d][%d]", i, j);
                Assert.assertEquals(msg, expected, actual, delta);
                Assert.assertEquals(msg, x0.getEntry(i), 1., Math.ulp(1.));
            }
        }
    }

    /**
     * Check whether the estimate of the (updated) residual corresponds to the
     * exact residual. This fails to be true for a large number of iterations,
     * due to the loss of orthogonality of the successive search directions.
     * Therefore, in the present test, the number of iterations is limited.
     */
    @Test
    public void testUnpreconditionedResidual() {
        final int n = 10;
        final int maxIterations = n;
        final RealLinearOperator a = new HilbertMatrix(n);
        final ConjugateGradient solver;
        solver = new ConjugateGradient(maxIterations, 1E-15, true);
        final RealVector r = new ArrayRealVector(n);
        final RealVector x = new ArrayRealVector(n);
        final IterationListener listener = new IterationListener() {

            public void terminationPerformed(final IterationEvent e) {
                // Do nothing
            }

            public void iterationStarted(final IterationEvent e) {
                // Do nothing
            }

            public void iterationPerformed(final IterationEvent e) {
                final IterativeLinearSolverEvent evt;
                evt = (IterativeLinearSolverEvent) e;
                RealVector v = evt.getResidual();
                r.setSubVector(0, v);
                v = evt.getSolution();
                x.setSubVector(0, v);
            }

            public void initializationPerformed(final IterationEvent e) {
                // Do nothing
            }
        };
        solver.getIterationManager().addIterationListener(listener);
        final RealVector b = new ArrayRealVector(n);
        for (int j = 0; j < n; j++) {
            b.set(0.);
            b.setEntry(j, 1.);

            boolean caught = false;
            try {
                solver.solve(a, b);
            } catch (MaxCountExceededException e) {
                caught = true;
                final RealVector y = a.operate(x);
                for (int i = 0; i < n; i++) {
                    final double actual = b.getEntry(i) - y.getEntry(i);
                    final double expected = r.getEntry(i);
                    final double delta = 1E-6 * FastMath.abs(expected);
                    final String msg = String
                        .format("column %d, residual %d", i, j);
                    Assert.assertEquals(msg, expected, actual, delta);
                }
            }
            Assert
                .assertTrue("MaxCountExceededException should have been caught",
                            caught);
        }
    }

    @Test(expected = NonSquareOperatorException.class)
    public void testNonSquarePreconditioner() {
        final Array2DRowRealMatrix a = new Array2DRowRealMatrix(2, 2);
        final RealLinearOperator m = new RealLinearOperator() {

            @Override
            public RealVector operate(final RealVector x) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int getRowDimension() {
                return 2;
            }

            @Override
            public int getColumnDimension() {
                return 3;
            }
        };
        final PreconditionedIterativeLinearSolver solver;
        solver = new ConjugateGradient(10, 0d, false);
        final ArrayRealVector b = new ArrayRealVector(a.getRowDimension());
        solver.solve(a, m, b);
    }

    @Test(expected = DimensionMismatchException.class)
    public void testMismatchedOperatorDimensions() {
        final Array2DRowRealMatrix a = new Array2DRowRealMatrix(2, 2);
        final RealLinearOperator m = new RealLinearOperator() {

            @Override
            public RealVector operate(final RealVector x) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int getRowDimension() {
                return 3;
            }

            @Override
            public int getColumnDimension() {
                return 3;
            }
        };
        final PreconditionedIterativeLinearSolver solver;
        solver = new ConjugateGradient(10, 0d, false);
        final ArrayRealVector b = new ArrayRealVector(a.getRowDimension());
        solver.solve(a, m, b);
    }

    @Test(expected = NonPositiveDefiniteOperatorException.class)
    public void testNonPositiveDefinitePreconditioner() {
        final Array2DRowRealMatrix a = new Array2DRowRealMatrix(2, 2);
        a.setEntry(0, 0, 1d);
        a.setEntry(0, 1, 2d);
        a.setEntry(1, 0, 3d);
        a.setEntry(1, 1, 4d);
        final RealLinearOperator m = new RealLinearOperator() {

            @Override
            public RealVector operate(final RealVector x) {
                final ArrayRealVector y = new ArrayRealVector(2);
                y.setEntry(0, -x.getEntry(0));
                y.setEntry(1, x.getEntry(1));
                return y;
            }

            @Override
            public int getRowDimension() {
                return 2;
            }

            @Override
            public int getColumnDimension() {
                return 2;
            }
        };
        final PreconditionedIterativeLinearSolver solver;
        solver = new ConjugateGradient(10, 0d, true);
        final ArrayRealVector b = new ArrayRealVector(2);
        b.setEntry(0, -1d);
        b.setEntry(1, -1d);
        solver.solve(a, m, b);
    }

    @Test
    public void testPreconditionedSolution() {
        final int n = 8;
        final int maxIterations = 100;
        final RealLinearOperator a = new HilbertMatrix(n);
        final InverseHilbertMatrix ainv = new InverseHilbertMatrix(n);
        final RealLinearOperator m = JacobiPreconditioner.create(a);
        final PreconditionedIterativeLinearSolver solver;
        solver = new ConjugateGradient(maxIterations, 1E-15, true);
        final RealVector b = new ArrayRealVector(n);
        for (int j = 0; j < n; j++) {
            b.set(0.);
            b.setEntry(j, 1.);
            final RealVector x = solver.solve(a, m, b);
            for (int i = 0; i < n; i++) {
                final double actual = x.getEntry(i);
                final double expected = ainv.getEntry(i, j);
                final double delta = 1E-6 * FastMath.abs(expected);
                final String msg = String.format("coefficient (%d, %d)", i, j);
                Assert.assertEquals(msg, expected, actual, delta);
            }
        }
    }

    @Test
    public void testPreconditionedResidual() {
        final int n = 10;
        final int maxIterations = n;
        final RealLinearOperator a = new HilbertMatrix(n);
        final RealLinearOperator m = JacobiPreconditioner.create(a);
        final ConjugateGradient solver;
        solver = new ConjugateGradient(maxIterations, 1E-15, true);
        final RealVector r = new ArrayRealVector(n);
        final RealVector x = new ArrayRealVector(n);
        final IterationListener listener = new IterationListener() {

            public void terminationPerformed(final IterationEvent e) {
                // Do nothing
            }

            public void iterationStarted(final IterationEvent e) {
                // Do nothing
            }

            public void iterationPerformed(final IterationEvent e) {
                final IterativeLinearSolverEvent evt;
                evt = (IterativeLinearSolverEvent) e;
                RealVector v = evt.getResidual();
                r.setSubVector(0, v);
                v = evt.getSolution();
                x.setSubVector(0, v);
            }

            public void initializationPerformed(final IterationEvent e) {
                // Do nothing
            }
        };
        solver.getIterationManager().addIterationListener(listener);
        final RealVector b = new ArrayRealVector(n);

        for (int j = 0; j < n; j++) {
            b.set(0.);
            b.setEntry(j, 1.);

            boolean caught = false;
            try {
                solver.solve(a, m, b);
            } catch (MaxCountExceededException e) {
                caught = true;
                final RealVector y = a.operate(x);
                for (int i = 0; i < n; i++) {
                    final double actual = b.getEntry(i) - y.getEntry(i);
                    final double expected = r.getEntry(i);
                    final double delta = 1E-6 * FastMath.abs(expected);
                    final String msg = String.format("column %d, residual %d", i, j);
                    Assert.assertEquals(msg, expected, actual, delta);
                }
            }
            Assert.assertTrue("MaxCountExceededException should have been caught", caught);
        }
    }

    @Test
    public void testPreconditionedSolution2() {
        final int n = 100;
        final int maxIterations = 100000;
        final Array2DRowRealMatrix a = new Array2DRowRealMatrix(n, n);
        double daux = 1.;
        for (int i = 0; i < n; i++) {
            a.setEntry(i, i, daux);
            daux *= 1.2;
            for (int j = i + 1; j < n; j++) {
                if (i == j) {
                } else {
                    final double value = 1.0;
                    a.setEntry(i, j, value);
                    a.setEntry(j, i, value);
                }
            }
        }
        final RealLinearOperator m = JacobiPreconditioner.create(a);
        final PreconditionedIterativeLinearSolver pcg;
        final IterativeLinearSolver cg;
        pcg = new ConjugateGradient(maxIterations, 1E-6, true);
        cg = new ConjugateGradient(maxIterations, 1E-6, true);
        final RealVector b = new ArrayRealVector(n);
        final String pattern = "preconditioned gradient (%d iterations) should"
                               + " have been faster than unpreconditioned (%d iterations)";
        String msg;
        for (int j = 0; j < 1; j++) {
            b.set(0.);
            b.setEntry(j, 1.);
            final RealVector px = pcg.solve(a, m, b);
            final RealVector x = cg.solve(a, b);
            final int npcg = pcg.getIterationManager().getIterations();
            final int ncg = cg.getIterationManager().getIterations();
            msg = String.format(pattern, npcg, ncg);
            Assert.assertTrue(msg, npcg < ncg);
            for (int i = 0; i < n; i++) {
                msg = String.format("row %d, column %d", i, j);
                final double expected = x.getEntry(i);
                final double actual = px.getEntry(i);
                final double delta = 1E-6 * FastMath.abs(expected);
                Assert.assertEquals(msg, expected, actual, delta);
            }
        }
    }

    @Test
    public void testEventManagement() {
        final int n = 5;
        final int maxIterations = 100;
        final RealLinearOperator a = new HilbertMatrix(n);
        final IterativeLinearSolver solver;
        /*
         * count[0] = number of calls to initializationPerformed
         * count[1] = number of calls to iterationStarted
         * count[2] = number of calls to iterationPerformed
         * count[3] = number of calls to terminationPerformed
         */
        final int[] count = new int[] {0, 0, 0, 0};
        final IterationListener listener = new IterationListener() {
            private void doTestVectorsAreUnmodifiable(final IterationEvent e) {
                final IterativeLinearSolverEvent evt;
                evt = (IterativeLinearSolverEvent) e;
                try {
                    evt.getResidual().set(0.0);
                    Assert.fail("r is modifiable");
                } catch (MathUnsupportedOperationException exc){
                    // Expected behavior
                }
                try {
                    evt.getRightHandSideVector().set(0.0);
                    Assert.fail("b is modifiable");
                } catch (MathUnsupportedOperationException exc){
                    // Expected behavior
                }
                try {
                    evt.getSolution().set(0.0);
                    Assert.fail("x is modifiable");
                } catch (MathUnsupportedOperationException exc){
                    // Expected behavior
                }
            }

            public void initializationPerformed(final IterationEvent e) {
                ++count[0];
                doTestVectorsAreUnmodifiable(e);
            }

            public void iterationPerformed(final IterationEvent e) {
                ++count[2];
                Assert.assertEquals("iteration performed",
                    count[2], e.getIterations() - 1);
                doTestVectorsAreUnmodifiable(e);
            }

            public void iterationStarted(final IterationEvent e) {
                ++count[1];
                Assert.assertEquals("iteration started",
                    count[1], e.getIterations() - 1);
                doTestVectorsAreUnmodifiable(e);
            }

            public void terminationPerformed(final IterationEvent e) {
                ++count[3];
                doTestVectorsAreUnmodifiable(e);
            }
        };
        solver = new ConjugateGradient(maxIterations, 1E-10, true);
        solver.getIterationManager().addIterationListener(listener);
        final RealVector b = new ArrayRealVector(n);
        for (int j = 0; j < n; j++) {
            Arrays.fill(count, 0);
            b.set(0.);
            b.setEntry(j, 1.);
            solver.solve(a, b);
            String msg = String.format("column %d (initialization)", j);
            Assert.assertEquals(msg, 1, count[0]);
            msg = String.format("column %d (finalization)", j);
            Assert.assertEquals(msg, 1, count[3]);
        }
    }

    @Test
    public void testUnpreconditionedNormOfResidual() {
        final int n = 5;
        final int maxIterations = 100;
        final RealLinearOperator a = new HilbertMatrix(n);
        final IterativeLinearSolver solver;
        final IterationListener listener = new IterationListener() {

            private void doTestNormOfResidual(final IterationEvent e) {
                final IterativeLinearSolverEvent evt;
                evt = (IterativeLinearSolverEvent) e;
                final RealVector x = evt.getSolution();
                final RealVector b = evt.getRightHandSideVector();
                final RealVector r = b.subtract(a.operate(x));
                final double rnorm = r.getNorm();
                Assert.assertEquals("iteration performed (residual)",
                    rnorm, evt.getNormOfResidual(),
                    FastMath.max(1E-5 * rnorm, 1E-10));
            }

            public void initializationPerformed(final IterationEvent e) {
                doTestNormOfResidual(e);
            }

            public void iterationPerformed(final IterationEvent e) {
                doTestNormOfResidual(e);
            }

            public void iterationStarted(final IterationEvent e) {
                doTestNormOfResidual(e);
            }

            public void terminationPerformed(final IterationEvent e) {
                doTestNormOfResidual(e);
            }
        };
        solver = new ConjugateGradient(maxIterations, 1E-10, true);
        solver.getIterationManager().addIterationListener(listener);
        final RealVector b = new ArrayRealVector(n);
        for (int j = 0; j < n; j++) {
            b.set(0.);
            b.setEntry(j, 1.);
            solver.solve(a, b);
        }
    }

    @Test
    public void testPreconditionedNormOfResidual() {
        final int n = 5;
        final int maxIterations = 100;
        final RealLinearOperator a = new HilbertMatrix(n);
        final RealLinearOperator m = JacobiPreconditioner.create(a);
        final PreconditionedIterativeLinearSolver solver;
        final IterationListener listener = new IterationListener() {

            private void doTestNormOfResidual(final IterationEvent e) {
                final IterativeLinearSolverEvent evt;
                evt = (IterativeLinearSolverEvent) e;
                final RealVector x = evt.getSolution();
                final RealVector b = evt.getRightHandSideVector();
                final RealVector r = b.subtract(a.operate(x));
                final double rnorm = r.getNorm();
                Assert.assertEquals("iteration performed (residual)",
                    rnorm, evt.getNormOfResidual(),
                    FastMath.max(1E-5 * rnorm, 1E-10));
            }

            public void initializationPerformed(final IterationEvent e) {
                doTestNormOfResidual(e);
            }

            public void iterationPerformed(final IterationEvent e) {
                doTestNormOfResidual(e);
            }

            public void iterationStarted(final IterationEvent e) {
                doTestNormOfResidual(e);
            }

            public void terminationPerformed(final IterationEvent e) {
                doTestNormOfResidual(e);
            }
        };
        solver = new ConjugateGradient(maxIterations, 1E-10, true);
        solver.getIterationManager().addIterationListener(listener);
        final RealVector b = new ArrayRealVector(n);
        for (int j = 0; j < n; j++) {
            b.set(0.);
            b.setEntry(j, 1.);
            solver.solve(a, m, b);
        }
    }
}
