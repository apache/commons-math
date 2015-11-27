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
package org.apache.commons.math4.optim.nonlinear.scalar.noderiv;

import java.util.Arrays;
import java.util.Random;

import org.apache.commons.math4.analysis.MultivariateFunction;
import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.NumberIsTooLargeException;
import org.apache.commons.math4.exception.NumberIsTooSmallException;
import org.apache.commons.math4.exception.TooManyEvaluationsException;
import org.apache.commons.math4.optim.InitialGuess;
import org.apache.commons.math4.optim.MaxEval;
import org.apache.commons.math4.optim.PointValuePair;
import org.apache.commons.math4.optim.SimpleBounds;
import org.apache.commons.math4.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math4.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math4.optim.nonlinear.scalar.noderiv.BOBYQAOptimizer;
import org.apache.commons.math4.util.FastMath;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test for {@link BOBYQAOptimizer}.
 */
public class BOBYQAOptimizerTest {

    static final int DIM = 13;

    @Test(expected=NumberIsTooLargeException.class)
    public void testInitOutOfBounds() {
        double[] startPoint = point(DIM, 3);
        double[][] boundaries = boundaries(DIM, -1, 2);
        doTest(new Rosen(), startPoint, boundaries,
                GoalType.MINIMIZE,
                1e-13, 1e-6, 2000, null);
    }

    @Test(expected=DimensionMismatchException.class)
    public void testBoundariesDimensionMismatch() {
        double[] startPoint = point(DIM, 0.5);
        double[][] boundaries = boundaries(DIM + 1, -1, 2);
        doTest(new Rosen(), startPoint, boundaries,
               GoalType.MINIMIZE,
               1e-13, 1e-6, 2000, null);
    }

    @Test(expected=NumberIsTooSmallException.class)
    public void testProblemDimensionTooSmall() {
        double[] startPoint = point(1, 0.5);
        doTest(new Rosen(), startPoint, null,
               GoalType.MINIMIZE,
               1e-13, 1e-6, 2000, null);
    }

    @Test(expected=TooManyEvaluationsException.class)
    public void testMaxEvaluations() {
        final int lowMaxEval = 2;
        double[] startPoint = point(DIM, 0.1);
        double[][] boundaries = null;
        doTest(new Rosen(), startPoint, boundaries,
               GoalType.MINIMIZE,
               1e-13, 1e-6, lowMaxEval, null);
     }

    @Test
    public void testRosen() {
        double[] startPoint = point(DIM,0.1);
        double[][] boundaries = null;
        PointValuePair expected = new PointValuePair(point(DIM,1.0),0.0);
        doTest(new Rosen(), startPoint, boundaries,
                GoalType.MINIMIZE,
                1e-13, 1e-6, 2000, expected);
     }

    @Test
    public void testMaximize() {
        double[] startPoint = point(DIM,1.0);
        double[][] boundaries = null;
        PointValuePair expected = new PointValuePair(point(DIM,0.0),1.0);
        doTest(new MinusElli(), startPoint, boundaries,
                GoalType.MAXIMIZE,
                2e-10, 5e-6, 1000, expected);
        boundaries = boundaries(DIM,-0.3,0.3);
        startPoint = point(DIM,0.1);
        doTest(new MinusElli(), startPoint, boundaries,
                GoalType.MAXIMIZE,
                2e-10, 5e-6, 1000, expected);
    }

    @Test
    public void testEllipse() {
        double[] startPoint = point(DIM,1.0);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(point(DIM,0.0),0.0);
        doTest(new Elli(), startPoint, boundaries,
                GoalType.MINIMIZE,
                1e-13, 1e-6, 1000, expected);
     }

    @Test
    public void testElliRotated() {
        double[] startPoint = point(DIM,1.0);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(point(DIM,0.0),0.0);
        doTest(new ElliRotated(), startPoint, boundaries,
                GoalType.MINIMIZE,
                1e-12, 1e-6, 10000, expected);
    }

    @Test
    public void testCigar() {
        double[] startPoint = point(DIM,1.0);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(point(DIM,0.0),0.0);
        doTest(new Cigar(), startPoint, boundaries,
                GoalType.MINIMIZE,
                1e-13, 1e-6, 100, expected);
    }

    @Test
    public void testTwoAxes() {
        double[] startPoint = point(DIM,1.0);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(point(DIM,0.0),0.0);
        doTest(new TwoAxes(), startPoint, boundaries,
                GoalType.MINIMIZE, 2*
                1e-13, 1e-6, 100, expected);
     }

    @Test
    public void testCigTab() {
        double[] startPoint = point(DIM,1.0);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(point(DIM,0.0),0.0);
        doTest(new CigTab(), startPoint, boundaries,
                GoalType.MINIMIZE,
                1e-13, 5e-5, 100, expected);
     }

    @Test
    public void testSphere() {
        double[] startPoint = point(DIM,1.0);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(point(DIM,0.0),0.0);
        doTest(new Sphere(), startPoint, boundaries,
                GoalType.MINIMIZE,
                1e-13, 1e-6, 100, expected);
    }

    @Test
    public void testTablet() {
        double[] startPoint = point(DIM,1.0);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(point(DIM,0.0),0.0);
        doTest(new Tablet(), startPoint, boundaries,
                GoalType.MINIMIZE,
                1e-13, 1e-6, 100, expected);
    }

    @Test
    public void testDiffPow() {
        double[] startPoint = point(DIM/2,1.0);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(point(DIM/2,0.0),0.0);
        doTest(new DiffPow(), startPoint, boundaries,
                GoalType.MINIMIZE,
                1e-8, 1e-1, 21000, expected);
    }

    @Test
    public void testSsDiffPow() {
        double[] startPoint = point(DIM/2,1.0);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(point(DIM/2,0.0),0.0);
        doTest(new SsDiffPow(), startPoint, boundaries,
                GoalType.MINIMIZE,
                1e-2, 1.3e-1, 50000, expected);
    }

    @Test
    public void testAckley() {
        double[] startPoint = point(DIM,0.1);
        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(point(DIM,0.0),0.0);
        doTest(new Ackley(), startPoint, boundaries,
                GoalType.MINIMIZE,
                1e-7, 1e-5, 1000, expected);
    }

    @Test
    public void testRastrigin() {
        double[] startPoint = point(DIM,1.0);

        double[][] boundaries = null;
        PointValuePair expected =
            new PointValuePair(point(DIM,0.0),0.0);
        doTest(new Rastrigin(), startPoint, boundaries,
                GoalType.MINIMIZE,
                1e-13, 1e-6, 1000, expected);
    }

    @Test
    public void testConstrainedRosen() {
        double[] startPoint = point(DIM,0.1);

        double[][] boundaries = boundaries(DIM,-1,2);
        PointValuePair expected =
            new PointValuePair(point(DIM,1.0),0.0);
        doTest(new Rosen(), startPoint, boundaries,
                GoalType.MINIMIZE,
                1e-13, 1e-6, 2000, expected);
    }

    // See MATH-728
    // TODO: this test is temporarily disabled for 3.2 release as a bug in Cobertura
    //       makes it run for several hours before completing
    @Ignore @Test
    public void testConstrainedRosenWithMoreInterpolationPoints() {
        final double[] startPoint = point(DIM, 0.1);
        final double[][] boundaries = boundaries(DIM, -1, 2);
        final PointValuePair expected = new PointValuePair(point(DIM, 1.0), 0.0);

        // This should have been 78 because in the code the hard limit is
        // said to be
        //   ((DIM + 1) * (DIM + 2)) / 2 - (2 * DIM + 1)
        // i.e. 78 in this case, but the test fails for 48, 59, 62, 63, 64,
        // 65, 66, ...
        final int maxAdditionalPoints = 47;

        for (int num = 1; num <= maxAdditionalPoints; num++) {
            doTest(new Rosen(), startPoint, boundaries,
                   GoalType.MINIMIZE,
                   1e-12, 1e-6, 2000,
                   num,
                   expected,
                   "num=" + num);
        }
    }

    /**
     * @param func Function to optimize.
     * @param startPoint Starting point.
     * @param boundaries Upper / lower point limit.
     * @param goal Minimization or maximization.
     * @param fTol Tolerance relative error on the objective function.
     * @param pointTol Tolerance for checking that the optimum is correct.
     * @param maxEvaluations Maximum number of evaluations.
     * @param expected Expected point / value.
     */
    private void doTest(MultivariateFunction func,
                        double[] startPoint,
                        double[][] boundaries,
                        GoalType goal,
                        double fTol,
                        double pointTol,
                        int maxEvaluations,
                        PointValuePair expected) {
        doTest(func,
               startPoint,
               boundaries,
               goal,
               fTol,
               pointTol,
               maxEvaluations,
               0,
               expected,
               "");
    }

    /**
     * @param func Function to optimize.
     * @param startPoint Starting point.
     * @param boundaries Upper / lower point limit.
     * @param goal Minimization or maximization.
     * @param fTol Tolerance relative error on the objective function.
     * @param pointTol Tolerance for checking that the optimum is correct.
     * @param maxEvaluations Maximum number of evaluations.
     * @param additionalInterpolationPoints Number of interpolation to used
     * in addition to the default (2 * dim + 1).
     * @param expected Expected point / value.
     */
    private void doTest(MultivariateFunction func,
                        double[] startPoint,
                        double[][] boundaries,
                        GoalType goal,
                        double fTol,
                        double pointTol,
                        int maxEvaluations,
                        int additionalInterpolationPoints,
                        PointValuePair expected,
                        String assertMsg) {

//         System.out.println(func.getClass().getName() + " BEGIN"); // XXX

        int dim = startPoint.length;
        final int numIterpolationPoints = 2 * dim + 1 + additionalInterpolationPoints;
        BOBYQAOptimizer optim = new BOBYQAOptimizer(numIterpolationPoints);
        PointValuePair result = boundaries == null ?
            optim.optimize(new MaxEval(maxEvaluations),
                           new ObjectiveFunction(func),
                           goal,
                           SimpleBounds.unbounded(dim),
                           new InitialGuess(startPoint)) :
            optim.optimize(new MaxEval(maxEvaluations),
                           new ObjectiveFunction(func),
                           goal,
                           new InitialGuess(startPoint),
                           new SimpleBounds(boundaries[0],
                                            boundaries[1]));
//        System.out.println(func.getClass().getName() + " = "
//              + optim.getEvaluations() + " f(");
//        for (double x: result.getPoint())  System.out.print(x + " ");
//        System.out.println(") = " +  result.getValue());
        Assert.assertEquals(assertMsg, expected.getValue(), result.getValue(), fTol);
        for (int i = 0; i < dim; i++) {
            Assert.assertEquals(expected.getPoint()[i],
                                result.getPoint()[i], pointTol);
        }

//         System.out.println(func.getClass().getName() + " END"); // XXX
    }

    private static double[] point(int n, double value) {
        double[] ds = new double[n];
        Arrays.fill(ds, value);
        return ds;
    }

    private static double[][] boundaries(int dim,
            double lower, double upper) {
        double[][] boundaries = new double[2][dim];
        for (int i = 0; i < dim; i++)
            boundaries[0][i] = lower;
        for (int i = 0; i < dim; i++)
            boundaries[1][i] = upper;
        return boundaries;
    }

    private static class Sphere implements MultivariateFunction {

        public double value(double[] x) {
            double f = 0;
            for (int i = 0; i < x.length; ++i)
                f += x[i] * x[i];
            return f;
        }
    }

    private static class Cigar implements MultivariateFunction {
        private double factor;

        Cigar() {
            this(1e3);
        }

        Cigar(double axisratio) {
            factor = axisratio * axisratio;
        }

        public double value(double[] x) {
            double f = x[0] * x[0];
            for (int i = 1; i < x.length; ++i)
                f += factor * x[i] * x[i];
            return f;
        }
    }

    private static class Tablet implements MultivariateFunction {
        private double factor;

        Tablet() {
            this(1e3);
        }

        Tablet(double axisratio) {
            factor = axisratio * axisratio;
        }

        public double value(double[] x) {
            double f = factor * x[0] * x[0];
            for (int i = 1; i < x.length; ++i)
                f += x[i] * x[i];
            return f;
        }
    }

    private static class CigTab implements MultivariateFunction {
        private double factor;

        CigTab() {
            this(1e4);
        }

        CigTab(double axisratio) {
            factor = axisratio;
        }

        public double value(double[] x) {
            int end = x.length - 1;
            double f = x[0] * x[0] / factor + factor * x[end] * x[end];
            for (int i = 1; i < end; ++i)
                f += x[i] * x[i];
            return f;
        }
    }

    private static class TwoAxes implements MultivariateFunction {

        private double factor;

        TwoAxes() {
            this(1e6);
        }

        TwoAxes(double axisratio) {
            factor = axisratio * axisratio;
        }

        public double value(double[] x) {
            double f = 0;
            for (int i = 0; i < x.length; ++i)
                f += (i < x.length / 2 ? factor : 1) * x[i] * x[i];
            return f;
        }
    }

    private static class ElliRotated implements MultivariateFunction {
        private Basis B = new Basis();
        private double factor;

        ElliRotated() {
            this(1e3);
        }

        ElliRotated(double axisratio) {
            factor = axisratio * axisratio;
        }

        public double value(double[] x) {
            double f = 0;
            x = B.Rotate(x);
            for (int i = 0; i < x.length; ++i)
                f += FastMath.pow(factor, i / (x.length - 1.)) * x[i] * x[i];
            return f;
        }
    }

    private static class Elli implements MultivariateFunction {

        private double factor;

        Elli() {
            this(1e3);
        }

        Elli(double axisratio) {
            factor = axisratio * axisratio;
        }

        public double value(double[] x) {
            double f = 0;
            for (int i = 0; i < x.length; ++i)
                f += FastMath.pow(factor, i / (x.length - 1.)) * x[i] * x[i];
            return f;
        }
    }

    private static class MinusElli implements MultivariateFunction {
        private final Elli elli = new Elli();
        public double value(double[] x) {
            return 1.0 - elli.value(x);
        }
    }

    private static class DiffPow implements MultivariateFunction {
//        private int fcount = 0;
        public double value(double[] x) {
            double f = 0;
            for (int i = 0; i < x.length; ++i)
                f += FastMath.pow(FastMath.abs(x[i]), 2. + 10 * (double) i
                        / (x.length - 1.));
//            System.out.print("" + (fcount++) + ") ");
//            for (int i = 0; i < x.length; i++)
//                System.out.print(x[i] +  " ");
//            System.out.println(" = " + f);
            return f;
        }
    }

    private static class SsDiffPow implements MultivariateFunction {

        public double value(double[] x) {
            double f = FastMath.pow(new DiffPow().value(x), 0.25);
            return f;
        }
    }

    private static class Rosen implements MultivariateFunction {

        public double value(double[] x) {
            double f = 0;
            for (int i = 0; i < x.length - 1; ++i)
                f += 1e2 * (x[i] * x[i] - x[i + 1]) * (x[i] * x[i] - x[i + 1])
                + (x[i] - 1.) * (x[i] - 1.);
            return f;
        }
    }

    private static class Ackley implements MultivariateFunction {
        private double axisratio;

        Ackley(double axra) {
            axisratio = axra;
        }

        public Ackley() {
            this(1);
        }

        public double value(double[] x) {
            double f = 0;
            double res2 = 0;
            double fac = 0;
            for (int i = 0; i < x.length; ++i) {
                fac = FastMath.pow(axisratio, (i - 1.) / (x.length - 1.));
                f += fac * fac * x[i] * x[i];
                res2 += FastMath.cos(2. * FastMath.PI * fac * x[i]);
            }
            f = (20. - 20. * FastMath.exp(-0.2 * FastMath.sqrt(f / x.length))
                    + FastMath.exp(1.) - FastMath.exp(res2 / x.length));
            return f;
        }
    }

    private static class Rastrigin implements MultivariateFunction {

        private double axisratio;
        private double amplitude;

        Rastrigin() {
            this(1, 10);
        }

        Rastrigin(double axisratio, double amplitude) {
            this.axisratio = axisratio;
            this.amplitude = amplitude;
        }

        public double value(double[] x) {
            double f = 0;
            double fac;
            for (int i = 0; i < x.length; ++i) {
                fac = FastMath.pow(axisratio, (i - 1.) / (x.length - 1.));
                if (i == 0 && x[i] < 0)
                    fac *= 1.;
                f += fac * fac * x[i] * x[i] + amplitude
                * (1. - FastMath.cos(2. * FastMath.PI * fac * x[i]));
            }
            return f;
        }
    }

    private static class Basis {
        double[][] basis;
        Random rand = new Random(2); // use not always the same basis

        double[] Rotate(double[] x) {
            GenBasis(x.length);
            double[] y = new double[x.length];
            for (int i = 0; i < x.length; ++i) {
                y[i] = 0;
                for (int j = 0; j < x.length; ++j)
                    y[i] += basis[i][j] * x[j];
            }
            return y;
        }

        void GenBasis(int DIM) {
            if (basis != null ? basis.length == DIM : false)
                return;

            double sp;
            int i, j, k;

            /* generate orthogonal basis */
            basis = new double[DIM][DIM];
            for (i = 0; i < DIM; ++i) {
                /* sample components gaussian */
                for (j = 0; j < DIM; ++j)
                    basis[i][j] = rand.nextGaussian();
                /* substract projection of previous vectors */
                for (j = i - 1; j >= 0; --j) {
                    for (sp = 0., k = 0; k < DIM; ++k)
                        sp += basis[i][k] * basis[j][k]; /* scalar product */
                    for (k = 0; k < DIM; ++k)
                        basis[i][k] -= sp * basis[j][k]; /* substract */
                }
                /* normalize */
                for (sp = 0., k = 0; k < DIM; ++k)
                    sp += basis[i][k] * basis[i][k]; /* squared norm */
                for (k = 0; k < DIM; ++k)
                    basis[i][k] /= FastMath.sqrt(sp);
            }
        }
    }
}
