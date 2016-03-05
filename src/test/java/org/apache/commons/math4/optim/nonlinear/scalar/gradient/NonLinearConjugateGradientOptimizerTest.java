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

package org.apache.commons.math4.optim.nonlinear.scalar.gradient;

import org.apache.commons.math4.analysis.MultivariateFunction;
import org.apache.commons.math4.analysis.MultivariateVectorFunction;
import org.apache.commons.math4.exception.MathUnsupportedOperationException;
import org.apache.commons.math4.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math4.linear.BlockRealMatrix;
import org.apache.commons.math4.linear.RealMatrix;
import org.apache.commons.math4.optim.InitialGuess;
import org.apache.commons.math4.optim.MaxEval;
import org.apache.commons.math4.optim.PointValuePair;
import org.apache.commons.math4.optim.SimpleBounds;
import org.apache.commons.math4.optim.SimpleValueChecker;
import org.apache.commons.math4.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math4.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math4.optim.nonlinear.scalar.ObjectiveFunctionGradient;
import org.apache.commons.math4.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer;
import org.apache.commons.math4.optim.nonlinear.scalar.gradient.Preconditioner;
import org.junit.Assert;
import org.junit.Test;

/**
 * <p>Some of the unit tests are re-implementations of the MINPACK <a
 * href="http://www.netlib.org/minpack/ex/file17">file17</a> and <a
 * href="http://www.netlib.org/minpack/ex/file22">file22</a> test files.
 * The redistribution policy for MINPACK is available <a
 * href="http://www.netlib.org/minpack/disclaimer">here</a>, for
 * convenience, it is reproduced below.</p>
 *
 * <table border="0" width="80%" cellpadding="10" align="center" bgcolor="#E0E0E0">
 * <tr><td>
 *    Minpack Copyright Notice (1999) University of Chicago.
 *    All rights reserved
 * </td></tr>
 * <tr><td>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * <ol>
 *  <li>Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.</li>
 * <li>Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials provided
 *     with the distribution.</li>
 * <li>The end-user documentation included with the redistribution, if any,
 *     must include the following acknowledgment:
 *     <code>This product includes software developed by the University of
 *           Chicago, as Operator of Argonne National Laboratory.</code>
 *     Alternately, this acknowledgment may appear in the software itself,
 *     if and wherever such third-party acknowledgments normally appear.</li>
 * <li><strong>WARRANTY DISCLAIMER. THE SOFTWARE IS SUPPLIED "AS IS"
 *     WITHOUT WARRANTY OF ANY KIND. THE COPYRIGHT HOLDER, THE
 *     UNITED STATES, THE UNITED STATES DEPARTMENT OF ENERGY, AND
 *     THEIR EMPLOYEES: (1) DISCLAIM ANY WARRANTIES, EXPRESS OR
 *     IMPLIED, INCLUDING BUT NOT LIMITED TO ANY IMPLIED WARRANTIES
 *     OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE
 *     OR NON-INFRINGEMENT, (2) DO NOT ASSUME ANY LEGAL LIABILITY
 *     OR RESPONSIBILITY FOR THE ACCURACY, COMPLETENESS, OR
 *     USEFULNESS OF THE SOFTWARE, (3) DO NOT REPRESENT THAT USE OF
 *     THE SOFTWARE WOULD NOT INFRINGE PRIVATELY OWNED RIGHTS, (4)
 *     DO NOT WARRANT THAT THE SOFTWARE WILL FUNCTION
 *     UNINTERRUPTED, THAT IT IS ERROR-FREE OR THAT ANY ERRORS WILL
 *     BE CORRECTED.</strong></li>
 * <li><strong>LIMITATION OF LIABILITY. IN NO EVENT WILL THE COPYRIGHT
 *     HOLDER, THE UNITED STATES, THE UNITED STATES DEPARTMENT OF
 *     ENERGY, OR THEIR EMPLOYEES: BE LIABLE FOR ANY INDIRECT,
 *     INCIDENTAL, CONSEQUENTIAL, SPECIAL OR PUNITIVE DAMAGES OF
 *     ANY KIND OR NATURE, INCLUDING BUT NOT LIMITED TO LOSS OF
 *     PROFITS OR LOSS OF DATA, FOR ANY REASON WHATSOEVER, WHETHER
 *     SUCH LIABILITY IS ASSERTED ON THE BASIS OF CONTRACT, TORT
 *     (INCLUDING NEGLIGENCE OR STRICT LIABILITY), OR OTHERWISE,
 *     EVEN IF ANY OF SAID PARTIES HAS BEEN WARNED OF THE
 *     POSSIBILITY OF SUCH LOSS OR DAMAGES.</strong></li>
 * <ol></td></tr>
 * </table>
 *
 * @author Argonne National Laboratory. MINPACK project. March 1980 (original fortran minpack tests)
 * @author Burton S. Garbow (original fortran minpack tests)
 * @author Kenneth E. Hillstrom (original fortran minpack tests)
 * @author Jorge J. More (original fortran minpack tests)
 * @author Luc Maisonobe (non-minpack tests and minpack tests Java translation)
 */
public class NonLinearConjugateGradientOptimizerTest {
    @Test(expected=MathUnsupportedOperationException.class)
    public void testBoundsUnsupported() {
        LinearProblem problem
            = new LinearProblem(new double[][] { { 2 } }, new double[] { 3 });
        NonLinearConjugateGradientOptimizer optimizer
            = new NonLinearConjugateGradientOptimizer(NonLinearConjugateGradientOptimizer.Formula.POLAK_RIBIERE,
                                                      new SimpleValueChecker(1e-6, 1e-6),
                                                      1e-3, 1e-3, 1);
        optimizer.optimize(new MaxEval(100),
                           problem.getObjectiveFunction(),
                           problem.getObjectiveFunctionGradient(),
                           GoalType.MINIMIZE,
                           new InitialGuess(new double[] { 0 }),
                           new SimpleBounds(new double[] { -1 },
                                            new double[] { 1 }));
    }

    @Test
    public void testTrivial() {
        LinearProblem problem
            = new LinearProblem(new double[][] { { 2 } }, new double[] { 3 });
        NonLinearConjugateGradientOptimizer optimizer
            = new NonLinearConjugateGradientOptimizer(NonLinearConjugateGradientOptimizer.Formula.POLAK_RIBIERE,
                                                      new SimpleValueChecker(1e-6, 1e-6),
                                                      1e-3, 1e-3, 1);
        PointValuePair optimum
            = optimizer.optimize(new MaxEval(100),
                                 problem.getObjectiveFunction(),
                                 problem.getObjectiveFunctionGradient(),
                                 GoalType.MINIMIZE,
                                 new InitialGuess(new double[] { 0 }));
        Assert.assertEquals(1.5, optimum.getPoint()[0], 1.0e-10);
        Assert.assertEquals(0.0, optimum.getValue(), 1.0e-10);

        // Check that the number of iterations is updated (MATH-949).
        Assert.assertTrue(optimizer.getIterations() > 0);
    }

    @Test
    public void testColumnsPermutation() {
        LinearProblem problem
            = new LinearProblem(new double[][] { { 1.0, -1.0 }, { 0.0, 2.0 }, { 1.0, -2.0 } },
                                new double[] { 4.0, 6.0, 1.0 });

        NonLinearConjugateGradientOptimizer optimizer
            = new NonLinearConjugateGradientOptimizer(NonLinearConjugateGradientOptimizer.Formula.POLAK_RIBIERE,
                                                      new SimpleValueChecker(1e-6, 1e-6),
                                                      1e-3, 1e-3, 1);
        PointValuePair optimum
            = optimizer.optimize(new MaxEval(100),
                                 problem.getObjectiveFunction(),
                                 problem.getObjectiveFunctionGradient(),
                                 GoalType.MINIMIZE,
                                 new InitialGuess(new double[] { 0, 0 }));
        Assert.assertEquals(7.0, optimum.getPoint()[0], 1.0e-10);
        Assert.assertEquals(3.0, optimum.getPoint()[1], 1.0e-10);
        Assert.assertEquals(0.0, optimum.getValue(), 1.0e-10);

    }

    @Test
    public void testNoDependency() {
        LinearProblem problem = new LinearProblem(new double[][] {
                { 2, 0, 0, 0, 0, 0 },
                { 0, 2, 0, 0, 0, 0 },
                { 0, 0, 2, 0, 0, 0 },
                { 0, 0, 0, 2, 0, 0 },
                { 0, 0, 0, 0, 2, 0 },
                { 0, 0, 0, 0, 0, 2 }
        }, new double[] { 0.0, 1.1, 2.2, 3.3, 4.4, 5.5 });
        NonLinearConjugateGradientOptimizer optimizer
            = new NonLinearConjugateGradientOptimizer(NonLinearConjugateGradientOptimizer.Formula.POLAK_RIBIERE,
                                                      new SimpleValueChecker(1e-6, 1e-6),
                                                      1e-3, 1e-3, 1);
        PointValuePair optimum
            = optimizer.optimize(new MaxEval(100),
                                 problem.getObjectiveFunction(),
                                 problem.getObjectiveFunctionGradient(),
                                 GoalType.MINIMIZE,
                                 new InitialGuess(new double[] { 0, 0, 0, 0, 0, 0 }));
        for (int i = 0; i < problem.target.length; ++i) {
            Assert.assertEquals(0.55 * i, optimum.getPoint()[i], 1.0e-10);
        }
    }

    @Test
    public void testOneSet() {
        LinearProblem problem = new LinearProblem(new double[][] {
                {  1,  0, 0 },
                { -1,  1, 0 },
                {  0, -1, 1 }
        }, new double[] { 1, 1, 1});
        NonLinearConjugateGradientOptimizer optimizer
            = new NonLinearConjugateGradientOptimizer(NonLinearConjugateGradientOptimizer.Formula.POLAK_RIBIERE,
                                                      new SimpleValueChecker(1e-6, 1e-6),
                                                      1e-3, 1e-3, 1);
        PointValuePair optimum
            = optimizer.optimize(new MaxEval(100),
                                 problem.getObjectiveFunction(),
                                 problem.getObjectiveFunctionGradient(),
                                 GoalType.MINIMIZE,
                                 new InitialGuess(new double[] { 0, 0, 0 }));
        Assert.assertEquals(1.0, optimum.getPoint()[0], 1.0e-10);
        Assert.assertEquals(2.0, optimum.getPoint()[1], 1.0e-10);
        Assert.assertEquals(3.0, optimum.getPoint()[2], 1.0e-10);

    }

    @Test
    public void testTwoSets() {
        final double epsilon = 1.0e-7;
        LinearProblem problem = new LinearProblem(new double[][] {
                {  2,  1,   0,  4,       0, 0 },
                { -4, -2,   3, -7,       0, 0 },
                {  4,  1,  -2,  8,       0, 0 },
                {  0, -3, -12, -1,       0, 0 },
                {  0,  0,   0,  0, epsilon, 1 },
                {  0,  0,   0,  0,       1, 1 }
        }, new double[] { 2, -9, 2, 2, 1 + epsilon * epsilon, 2});

        final Preconditioner preconditioner
            = new Preconditioner() {
                    public double[] precondition(double[] point, double[] r) {
                        double[] d = r.clone();
                        d[0] /=  72.0;
                        d[1] /=  30.0;
                        d[2] /= 314.0;
                        d[3] /= 260.0;
                        d[4] /= 2 * (1 + epsilon * epsilon);
                        d[5] /= 4.0;
                        return d;
                    }
                };

        NonLinearConjugateGradientOptimizer optimizer
           = new NonLinearConjugateGradientOptimizer(NonLinearConjugateGradientOptimizer.Formula.POLAK_RIBIERE,
                                                     new SimpleValueChecker(1e-13, 1e-13),
                                                     1e-7, 1e-7, 1,
                                                     preconditioner);

        PointValuePair optimum
            = optimizer.optimize(new MaxEval(100),
                                 problem.getObjectiveFunction(),
                                 problem.getObjectiveFunctionGradient(),
                                 GoalType.MINIMIZE,
                                 new InitialGuess(new double[] { 0, 0, 0, 0, 0, 0 }));

        final double[] result = optimum.getPoint();
        final double[] expected = {3, 4, -1, -2, 1 + epsilon, 1 - epsilon};

        Assert.assertEquals(expected[0], result[0], 1.0e-7);
        Assert.assertEquals(expected[1], result[1], 1.0e-7);
        Assert.assertEquals(expected[2], result[2], 1.0e-9);
        Assert.assertEquals(expected[3], result[3], 1.0e-8);
        Assert.assertEquals(expected[4] + epsilon, result[4], 1.0e-6);
        Assert.assertEquals(expected[5] - epsilon, result[5], 1.0e-6);

    }

    @Test
    public void testNonInversible() {
        LinearProblem problem = new LinearProblem(new double[][] {
                {  1, 2, -3 },
                {  2, 1,  3 },
                { -3, 0, -9 }
        }, new double[] { 1, 1, 1 });
        NonLinearConjugateGradientOptimizer optimizer
            = new NonLinearConjugateGradientOptimizer(NonLinearConjugateGradientOptimizer.Formula.POLAK_RIBIERE,
                                                      new SimpleValueChecker(1e-6, 1e-6),
                                                      1e-3, 1e-3, 1);
        PointValuePair optimum
            = optimizer.optimize(new MaxEval(100),
                                 problem.getObjectiveFunction(),
                                 problem.getObjectiveFunctionGradient(),
                                 GoalType.MINIMIZE,
                                 new InitialGuess(new double[] { 0, 0, 0 }));
        Assert.assertTrue(optimum.getValue() > 0.5);
    }

    @Test
    public void testIllConditioned() {
        LinearProblem problem1 = new LinearProblem(new double[][] {
                { 10.0, 7.0,  8.0,  7.0 },
                {  7.0, 5.0,  6.0,  5.0 },
                {  8.0, 6.0, 10.0,  9.0 },
                {  7.0, 5.0,  9.0, 10.0 }
        }, new double[] { 32, 23, 33, 31 });
        NonLinearConjugateGradientOptimizer optimizer
            = new NonLinearConjugateGradientOptimizer(NonLinearConjugateGradientOptimizer.Formula.POLAK_RIBIERE,
                                                      new SimpleValueChecker(1e-13, 1e-13),
                                                      1e-15, 1e-15, 1);
        PointValuePair optimum1
            = optimizer.optimize(new MaxEval(200),
                                 problem1.getObjectiveFunction(),
                                 problem1.getObjectiveFunctionGradient(),
                                 GoalType.MINIMIZE,
                                 new InitialGuess(new double[] { 0, 1, 2, 3 }));
        Assert.assertEquals(1.0, optimum1.getPoint()[0], 1.0e-4);
        Assert.assertEquals(1.0, optimum1.getPoint()[1], 1.0e-3);
        Assert.assertEquals(1.0, optimum1.getPoint()[2], 1.0e-4);
        Assert.assertEquals(1.0, optimum1.getPoint()[3], 1.0e-4);

        LinearProblem problem2 = new LinearProblem(new double[][] {
                { 10.00, 7.00, 8.10, 7.20 },
                {  7.08, 5.04, 6.00, 5.00 },
                {  8.00, 5.98, 9.89, 9.00 },
                {  6.99, 4.99, 9.00, 9.98 }
        }, new double[] { 32, 23, 33, 31 });
        PointValuePair optimum2
            = optimizer.optimize(new MaxEval(200),
                                 problem2.getObjectiveFunction(),
                                 problem2.getObjectiveFunctionGradient(),
                                 GoalType.MINIMIZE,
                                 new InitialGuess(new double[] { 0, 1, 2, 3 }));

        final double[] result2 = optimum2.getPoint();
        final double[] expected2 = {-81, 137, -34, 22};

        Assert.assertEquals(expected2[0], result2[0], 2);
        Assert.assertEquals(expected2[1], result2[1], 4);
        Assert.assertEquals(expected2[2], result2[2], 1);
        Assert.assertEquals(expected2[3], result2[3], 1);
    }

    @Test
    public void testMoreEstimatedParametersSimple() {
        LinearProblem problem = new LinearProblem(new double[][] {
                { 3.0, 2.0,  0.0, 0.0 },
                { 0.0, 1.0, -1.0, 1.0 },
                { 2.0, 0.0,  1.0, 0.0 }
        }, new double[] { 7.0, 3.0, 5.0 });

        NonLinearConjugateGradientOptimizer optimizer
            = new NonLinearConjugateGradientOptimizer(NonLinearConjugateGradientOptimizer.Formula.POLAK_RIBIERE,
                                                      new SimpleValueChecker(1e-6, 1e-6),
                                                      1e-3, 1e-3, 1);
        PointValuePair optimum
            = optimizer.optimize(new MaxEval(100),
                                 problem.getObjectiveFunction(),
                                 problem.getObjectiveFunctionGradient(),
                                 GoalType.MINIMIZE,
                                 new InitialGuess(new double[] { 7, 6, 5, 4 }));
        Assert.assertEquals(0, optimum.getValue(), 1.0e-10);

    }

    @Test
    public void testMoreEstimatedParametersUnsorted() {
        LinearProblem problem = new LinearProblem(new double[][] {
                 { 1.0, 1.0,  0.0,  0.0, 0.0,  0.0 },
                 { 0.0, 0.0,  1.0,  1.0, 1.0,  0.0 },
                 { 0.0, 0.0,  0.0,  0.0, 1.0, -1.0 },
                 { 0.0, 0.0, -1.0,  1.0, 0.0,  1.0 },
                 { 0.0, 0.0,  0.0, -1.0, 1.0,  0.0 }
        }, new double[] { 3.0, 12.0, -1.0, 7.0, 1.0 });
        NonLinearConjugateGradientOptimizer optimizer
           = new NonLinearConjugateGradientOptimizer(NonLinearConjugateGradientOptimizer.Formula.POLAK_RIBIERE,
                                                     new SimpleValueChecker(1e-6, 1e-6),
                                                     1e-3, 1e-3, 1);
        PointValuePair optimum
            = optimizer.optimize(new MaxEval(100),
                                 problem.getObjectiveFunction(),
                                 problem.getObjectiveFunctionGradient(),
                                 GoalType.MINIMIZE,
                                 new InitialGuess(new double[] { 2, 2, 2, 2, 2, 2 }));
        Assert.assertEquals(0, optimum.getValue(), 1.0e-10);
    }

    @Test
    public void testRedundantEquations() {
        LinearProblem problem = new LinearProblem(new double[][] {
                { 1.0,  1.0 },
                { 1.0, -1.0 },
                { 1.0,  3.0 }
        }, new double[] { 3.0, 1.0, 5.0 });

        NonLinearConjugateGradientOptimizer optimizer
            = new NonLinearConjugateGradientOptimizer(NonLinearConjugateGradientOptimizer.Formula.POLAK_RIBIERE,
                                                      new SimpleValueChecker(1e-6, 1e-6),
                                                      1e-3, 1e-3, 1);
        PointValuePair optimum
            = optimizer.optimize(new MaxEval(100),
                                 problem.getObjectiveFunction(),
                                 problem.getObjectiveFunctionGradient(),
                                 GoalType.MINIMIZE,
                                 new InitialGuess(new double[] { 1, 1 }));
        Assert.assertEquals(2.0, optimum.getPoint()[0], 1.0e-8);
        Assert.assertEquals(1.0, optimum.getPoint()[1], 1.0e-8);

    }

    @Test
    public void testInconsistentEquations() {
        LinearProblem problem = new LinearProblem(new double[][] {
                { 1.0,  1.0 },
                { 1.0, -1.0 },
                { 1.0,  3.0 }
        }, new double[] { 3.0, 1.0, 4.0 });

        NonLinearConjugateGradientOptimizer optimizer
            = new NonLinearConjugateGradientOptimizer(NonLinearConjugateGradientOptimizer.Formula.POLAK_RIBIERE,
                                                      new SimpleValueChecker(1e-6, 1e-6),
                                                      1e-3, 1e-3, 1);
        PointValuePair optimum
            = optimizer.optimize(new MaxEval(100),
                                 problem.getObjectiveFunction(),
                                 problem.getObjectiveFunctionGradient(),
                                 GoalType.MINIMIZE,
                                 new InitialGuess(new double[] { 1, 1 }));
        Assert.assertTrue(optimum.getValue() > 0.1);

    }

    @Test
    public void testCircleFitting() {
        CircleScalar problem = new CircleScalar();
        problem.addPoint( 30.0,  68.0);
        problem.addPoint( 50.0,  -6.0);
        problem.addPoint(110.0, -20.0);
        problem.addPoint( 35.0,  15.0);
        problem.addPoint( 45.0,  97.0);
        NonLinearConjugateGradientOptimizer optimizer
           = new NonLinearConjugateGradientOptimizer(NonLinearConjugateGradientOptimizer.Formula.POLAK_RIBIERE,
                                                     new SimpleValueChecker(1e-30, 1e-30),
                                                     1e-15, 1e-13, 1);
        PointValuePair optimum
            = optimizer.optimize(new MaxEval(100),
                                 problem.getObjectiveFunction(),
                                 problem.getObjectiveFunctionGradient(),
                                 GoalType.MINIMIZE,
                                 new InitialGuess(new double[] { 98.680, 47.345 }));
        Vector2D center = new Vector2D(optimum.getPointRef()[0], optimum.getPointRef()[1]);
        Assert.assertEquals(69.960161753, problem.getRadius(center), 1.0e-8);
        Assert.assertEquals(96.075902096, center.getX(), 1.0e-7);
        Assert.assertEquals(48.135167894, center.getY(), 1.0e-6);
    }

    private static class LinearProblem {
        final RealMatrix factors;
        final double[] target;

        public LinearProblem(double[][] factors,
                             double[] target) {
            this.factors = new BlockRealMatrix(factors);
            this.target  = target;
        }

        public ObjectiveFunction getObjectiveFunction() {
            return new ObjectiveFunction(new MultivariateFunction() {
                    public double value(double[] point) {
                        double[] y = factors.operate(point);
                        double sum = 0;
                        for (int i = 0; i < y.length; ++i) {
                            double ri = y[i] - target[i];
                            sum += ri * ri;
                        }
                        return sum;
                    }
                });
        }

        public ObjectiveFunctionGradient getObjectiveFunctionGradient() {
            return new ObjectiveFunctionGradient(new MultivariateVectorFunction() {
                    public double[] value(double[] point) {
                        double[] r = factors.operate(point);
                        for (int i = 0; i < r.length; ++i) {
                            r[i] -= target[i];
                        }
                        double[] p = factors.transpose().operate(r);
                        for (int i = 0; i < p.length; ++i) {
                            p[i] *= 2;
                        }
                        return p;
                    }
                });
        }
    }
}
