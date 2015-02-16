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
package org.apache.commons.math4.fitting.leastsquares;

import org.apache.commons.math4.analysis.MultivariateMatrixFunction;
import org.apache.commons.math4.analysis.MultivariateVectorFunction;
import org.apache.commons.math4.exception.ConvergenceException;
import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.fitting.leastsquares.LeastSquaresBuilder;
import org.apache.commons.math4.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math4.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math4.fitting.leastsquares.MultivariateJacobianFunction;
import org.apache.commons.math4.fitting.leastsquares.LeastSquaresOptimizer.Optimum;
import org.apache.commons.math4.fitting.leastsquares.LeastSquaresProblem.Evaluation;
import org.apache.commons.math4.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math4.linear.Array2DRowRealMatrix;
import org.apache.commons.math4.linear.ArrayRealVector;
import org.apache.commons.math4.linear.BlockRealMatrix;
import org.apache.commons.math4.linear.DiagonalMatrix;
import org.apache.commons.math4.linear.RealMatrix;
import org.apache.commons.math4.linear.RealVector;
import org.apache.commons.math4.optim.ConvergenceChecker;
import org.apache.commons.math4.optim.SimpleVectorValueChecker;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.math4.util.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;

/**
 * Some of the unit tests are re-implementations of the MINPACK <a
 * href="http://www.netlib.org/minpack/ex/file17">file17</a> and <a
 * href="http://www.netlib.org/minpack/ex/file22">file22</a> test files. The
 * redistribution policy for MINPACK is available <a href="http://www.netlib.org/minpack/disclaimer">here</a>.
 * <p/>
 * <T> Concrete implementation of an optimizer.
 *
 */
public abstract class AbstractLeastSquaresOptimizerAbstractTest {

    /** default absolute tolerance of comparisons */
    public static final double TOl = 1e-10;

    public LeastSquaresBuilder base() {
        return new LeastSquaresBuilder()
                .checkerPair(new SimpleVectorValueChecker(1e-6, 1e-6))
                .maxEvaluations(100)
                .maxIterations(getMaxIterations());
    }

    public LeastSquaresBuilder builder(CircleVectorial c) {
        final double[] weights = new double[c.getN()];
        Arrays.fill(weights, 1.0);
        return base()
                .model(c.getModelFunction(), c.getModelFunctionJacobian())
                .target(new double[c.getN()])
                .weight(new DiagonalMatrix(weights));
    }

    public LeastSquaresBuilder builder(StatisticalReferenceDataset dataset) {
        StatisticalReferenceDataset.LeastSquaresProblem problem
                = dataset.getLeastSquaresProblem();
        final double[] weights = new double[dataset.getNumObservations()];
        Arrays.fill(weights, 1.0);
        return base()
                .model(problem.getModelFunction(), problem.getModelFunctionJacobian())
                .target(dataset.getData()[1])
                .weight(new DiagonalMatrix(weights))
                .start(dataset.getStartingPoint(0));
    }

    public void fail(LeastSquaresOptimizer optimizer) {
        Assert.fail("Expected Exception from: " + optimizer.toString());
    }

    /**
     * Check the value of a vector.
     * @param tolerance the absolute tolerance of comparisons
     * @param actual the vector to test
     * @param expected the expected values
     */
    public void assertEquals(double tolerance, RealVector actual, double... expected){
        for (int i = 0; i < expected.length; i++) {
            Assert.assertEquals(expected[i], actual.getEntry(i), tolerance);
        }
        Assert.assertEquals(expected.length, actual.getDimension());
    }

    /**
     * @return the default number of allowed iterations (which will be used when not
     *         specified otherwise).
     */
    public abstract int getMaxIterations();

    /**
     * Get an instance of the optimizer under test.
     *
     * @return the subject under test.
     */
    public abstract LeastSquaresOptimizer getOptimizer();

    /**
     * The subject under test.
     */
    public final LeastSquaresOptimizer optimizer = this.getOptimizer();

    @Test
    public void testGetIterations() {
        LeastSquaresProblem lsp = base()
                .target(new double[]{1})
                .weight(new DiagonalMatrix(new double[]{1}))
                .start(new double[]{3})
                .model(new MultivariateJacobianFunction() {
                    public Pair<RealVector, RealMatrix> value(final RealVector point) {
                        return new Pair<RealVector, RealMatrix>(
                                new ArrayRealVector(
                                        new double[]{
                                                FastMath.pow(point.getEntry(0), 4)
                                        },
                                        false),
                                new Array2DRowRealMatrix(
                                        new double[][]{
                                                {0.25 * FastMath.pow(point.getEntry(0), 3)}
                                        },
                                        false)
                        );
                    }
                })
                .build();

        Optimum optimum = optimizer.optimize(lsp);

        //TODO more specific test? could pass with 'return 1;'
        Assert.assertTrue(optimum.getIterations() > 0);
    }

    @Test
    public void testTrivial() {
        LinearProblem problem
                = new LinearProblem(new double[][]{{2}},
                new double[]{3});
        LeastSquaresProblem ls = problem.getBuilder().build();

        Optimum optimum = optimizer.optimize(ls);

        Assert.assertEquals(0, optimum.getRMS(), TOl);
        assertEquals(TOl, optimum.getPoint(), 1.5);
        Assert.assertEquals(0.0, optimum.getResiduals().getEntry(0), TOl);
    }

    @Test
    public void testQRColumnsPermutation() {
        LinearProblem problem
                = new LinearProblem(new double[][]{{1, -1}, {0, 2}, {1, -2}},
                new double[]{4, 6, 1});

        Optimum optimum = optimizer.optimize(problem.getBuilder().build());

        Assert.assertEquals(0, optimum.getRMS(), TOl);
        assertEquals(TOl, optimum.getPoint(), 7, 3);
        assertEquals(TOl, optimum.getResiduals(), 0, 0, 0);
    }

    @Test
    public void testNoDependency() {
        LinearProblem problem = new LinearProblem(new double[][]{
                {2, 0, 0, 0, 0, 0},
                {0, 2, 0, 0, 0, 0},
                {0, 0, 2, 0, 0, 0},
                {0, 0, 0, 2, 0, 0},
                {0, 0, 0, 0, 2, 0},
                {0, 0, 0, 0, 0, 2}
        }, new double[]{0, 1.1, 2.2, 3.3, 4.4, 5.5});

        Optimum optimum = optimizer.optimize(problem.getBuilder().build());

        Assert.assertEquals(0, optimum.getRMS(), TOl);
        for (int i = 0; i < problem.target.length; ++i) {
            Assert.assertEquals(0.55 * i, optimum.getPoint().getEntry(i), TOl);
        }
    }

    @Test
    public void testOneSet() {
        LinearProblem problem = new LinearProblem(new double[][]{
                {1, 0, 0},
                {-1, 1, 0},
                {0, -1, 1}
        }, new double[]{1, 1, 1});

        Optimum optimum = optimizer.optimize(problem.getBuilder().build());

        Assert.assertEquals(0, optimum.getRMS(), TOl);
        assertEquals(TOl, optimum.getPoint(), 1, 2, 3);
    }

    @Test
    public void testTwoSets() {
        double epsilon = 1e-7;
        LinearProblem problem = new LinearProblem(new double[][]{
                {2, 1, 0, 4, 0, 0},
                {-4, -2, 3, -7, 0, 0},
                {4, 1, -2, 8, 0, 0},
                {0, -3, -12, -1, 0, 0},
                {0, 0, 0, 0, epsilon, 1},
                {0, 0, 0, 0, 1, 1}
        }, new double[]{2, -9, 2, 2, 1 + epsilon * epsilon, 2});

        Optimum optimum = optimizer.optimize(problem.getBuilder().build());

        Assert.assertEquals(0, optimum.getRMS(), TOl);
        assertEquals(TOl, optimum.getPoint(), 3, 4, -1, -2, 1 + epsilon, 1 - epsilon);
    }

    @Test
    public void testNonInvertible() throws Exception {
        try {
            LinearProblem problem = new LinearProblem(new double[][]{
                    {1, 2, -3},
                    {2, 1, 3},
                    {-3, 0, -9}
            }, new double[]{1, 1, 1});

            optimizer.optimize(problem.getBuilder().build());

            fail(optimizer);
        } catch (ConvergenceException e) {
            //expected
        }
    }

    @Test
    public void testIllConditioned() {
        LinearProblem problem1 = new LinearProblem(new double[][]{
                {10, 7, 8, 7},
                {7, 5, 6, 5},
                {8, 6, 10, 9},
                {7, 5, 9, 10}
        }, new double[]{32, 23, 33, 31});
        final double[] start = {0, 1, 2, 3};

        Optimum optimum = optimizer
                .optimize(problem1.getBuilder().start(start).build());

        Assert.assertEquals(0, optimum.getRMS(), TOl);
        assertEquals(TOl, optimum.getPoint(), 1, 1, 1, 1);

        LinearProblem problem2 = new LinearProblem(new double[][]{
                {10.00, 7.00, 8.10, 7.20},
                {7.08, 5.04, 6.00, 5.00},
                {8.00, 5.98, 9.89, 9.00},
                {6.99, 4.99, 9.00, 9.98}
        }, new double[]{32, 23, 33, 31});

        optimum = optimizer.optimize(problem2.getBuilder().start(start).build());

        Assert.assertEquals(0, optimum.getRMS(), TOl);
        assertEquals(1e-8, optimum.getPoint(), -81, 137, -34, 22);
    }

    @Test
    public void testMoreEstimatedParametersSimple() {
        LinearProblem problem = new LinearProblem(new double[][]{
                {3, 2, 0, 0},
                {0, 1, -1, 1},
                {2, 0, 1, 0}
        }, new double[]{7, 3, 5});

        Optimum optimum = optimizer
                .optimize(problem.getBuilder().start(new double[]{7, 6, 5, 4}).build());

        Assert.assertEquals(0, optimum.getRMS(), TOl);
    }

    @Test
    public void testMoreEstimatedParametersUnsorted() {
        LinearProblem problem = new LinearProblem(new double[][]{
                {1, 1, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 0},
                {0, 0, 0, 0, 1, -1},
                {0, 0, -1, 1, 0, 1},
                {0, 0, 0, -1, 1, 0}
        }, new double[]{3, 12, -1, 7, 1});

        Optimum optimum = optimizer.optimize(
                problem.getBuilder().start(new double[]{2, 2, 2, 2, 2, 2}).build());

        Assert.assertEquals(0, optimum.getRMS(), TOl);
        RealVector point = optimum.getPoint();
        //the first two elements are under constrained
        //check first two elements obey the constraint: sum to 3
        Assert.assertEquals(3, point.getEntry(0) + point.getEntry(1), TOl);
        //#constrains = #states fro the last 4 elements
        assertEquals(TOl, point.getSubVector(2, 4), 3, 4, 5, 6);
    }

    @Test
    public void testRedundantEquations() {
        LinearProblem problem = new LinearProblem(new double[][]{
                {1, 1},
                {1, -1},
                {1, 3}
        }, new double[]{3, 1, 5});

        Optimum optimum = optimizer
                .optimize(problem.getBuilder().start(new double[]{1, 1}).build());

        Assert.assertEquals(0, optimum.getRMS(), TOl);
        assertEquals(TOl, optimum.getPoint(), 2, 1);
    }

    @Test
    public void testInconsistentEquations() {
        LinearProblem problem = new LinearProblem(new double[][]{
                {1, 1},
                {1, -1},
                {1, 3}
        }, new double[]{3, 1, 4});

        Optimum optimum = optimizer
                .optimize(problem.getBuilder().start(new double[]{1, 1}).build());

        //TODO what is this actually testing?
        Assert.assertTrue(optimum.getRMS() > 0.1);
    }

    @Test
    public void testInconsistentSizes1() {
        try {
            LinearProblem problem
                    = new LinearProblem(new double[][]{{1, 0},
                    {0, 1}},
                    new double[]{-1, 1});

            //TODO why is this part here? hasn't it been tested already?
            Optimum optimum = optimizer.optimize(problem.getBuilder().build());

            Assert.assertEquals(0, optimum.getRMS(), TOl);
            assertEquals(TOl, optimum.getPoint(), -1, 1);

            //TODO move to builder test
            optimizer.optimize(
                    problem.getBuilder().weight(new DiagonalMatrix(new double[]{1})).build());

            fail(optimizer);
        } catch (DimensionMismatchException e) {
            //expected
        }
    }

    @Test
    public void testInconsistentSizes2() {
        try {
            LinearProblem problem
                    = new LinearProblem(new double[][]{{1, 0}, {0, 1}},
                    new double[]{-1, 1});

            Optimum optimum = optimizer.optimize(problem.getBuilder().build());

            Assert.assertEquals(0, optimum.getRMS(), TOl);
            assertEquals(TOl, optimum.getPoint(), -1, 1);

            //TODO move to builder test
            optimizer.optimize(
                    problem.getBuilder()
                            .target(new double[]{1})
                            .weight(new DiagonalMatrix(new double[]{1}))
                            .build()
            );

            fail(optimizer);
        } catch (DimensionMismatchException e) {
            //expected
        }
    }

    @Test
    public void testCircleFitting() {
        CircleVectorial circle = new CircleVectorial();
        circle.addPoint(30, 68);
        circle.addPoint(50, -6);
        circle.addPoint(110, -20);
        circle.addPoint(35, 15);
        circle.addPoint(45, 97);
        final double[] start = {98.680, 47.345};

        Optimum optimum = optimizer.optimize(builder(circle).start(start).build());

        Assert.assertTrue(optimum.getEvaluations() < 10);

        double rms = optimum.getRMS();
        Assert.assertEquals(1.768262623567235, FastMath.sqrt(circle.getN()) * rms, TOl);

        Vector2D center = new Vector2D(optimum.getPoint().getEntry(0), optimum.getPoint().getEntry(1));
        Assert.assertEquals(69.96016176931406, circle.getRadius(center), 1e-6);
        Assert.assertEquals(96.07590211815305, center.getX(), 1e-6);
        Assert.assertEquals(48.13516790438953, center.getY(), 1e-6);

        double[][] cov = optimum.getCovariances(1e-14).getData();
        Assert.assertEquals(1.839, cov[0][0], 0.001);
        Assert.assertEquals(0.731, cov[0][1], 0.001);
        Assert.assertEquals(cov[0][1], cov[1][0], 1e-14);
        Assert.assertEquals(0.786, cov[1][1], 0.001);

        // add perfect measurements and check formal errors are reduced
        double r = circle.getRadius(center);
        for (double d = 0; d < 2 * FastMath.PI; d += 0.01) {
            circle.addPoint(center.getX() + r * FastMath.cos(d), center.getY() + r * FastMath.sin(d));
        }

        double[] weights = new double[circle.getN()];
        Arrays.fill(weights, 2);

        optimum = optimizer.optimize(
                builder(circle).weight(new DiagonalMatrix(weights)).start(start).build());

        cov = optimum.getCovariances(1e-14).getData();
        Assert.assertEquals(0.0016, cov[0][0], 0.001);
        Assert.assertEquals(3.2e-7, cov[0][1], 1e-9);
        Assert.assertEquals(cov[0][1], cov[1][0], 1e-14);
        Assert.assertEquals(0.0016, cov[1][1], 0.001);
    }

    @Test
    public void testCircleFittingBadInit() {
        CircleVectorial circle = new CircleVectorial();
        double[][] points = circlePoints;
        double[] weights = new double[points.length];
        final double[] start = {-12, -12};
        Arrays.fill(weights, 2);
        for (int i = 0; i < points.length; ++i) {
            circle.addPoint(points[i][0], points[i][1]);
        }

        Optimum optimum = optimizer.optimize(builder(circle).weight(new DiagonalMatrix(weights)).start(start).build());

        Vector2D center = new Vector2D(optimum.getPoint().getEntry(0), optimum.getPoint().getEntry(1));
        Assert.assertTrue(optimum.getEvaluations() < 25);
        Assert.assertEquals(0.043, optimum.getRMS(), 1e-3);
        Assert.assertEquals(0.292235, circle.getRadius(center), 1e-6);
        Assert.assertEquals(-0.151738, center.getX(), 1e-6);
        Assert.assertEquals(0.2075001, center.getY(), 1e-6);
    }

    @Test
    public void testCircleFittingGoodInit() {
        CircleVectorial circle = new CircleVectorial();
        double[][] points = circlePoints;
        double[] weights = new double[points.length];
        Arrays.fill(weights, 2);
        for (int i = 0; i < points.length; ++i) {
            circle.addPoint(points[i][0], points[i][1]);
        }
        final double[] start = {0, 0};

        Optimum optimum = optimizer.optimize(
                builder(circle).weight(new DiagonalMatrix(weights)).start(start).build());

        assertEquals(1e-6, optimum.getPoint(), -0.1517383071957963, 0.2074999736353867);
        Assert.assertEquals(0.04268731682389561, optimum.getRMS(), 1e-8);
    }

    private final double[][] circlePoints = new double[][]{
            {-0.312967, 0.072366}, {-0.339248, 0.132965}, {-0.379780, 0.202724},
            {-0.390426, 0.260487}, {-0.361212, 0.328325}, {-0.346039, 0.392619},
            {-0.280579, 0.444306}, {-0.216035, 0.470009}, {-0.149127, 0.493832},
            {-0.075133, 0.483271}, {-0.007759, 0.452680}, {0.060071, 0.410235},
            {0.103037, 0.341076}, {0.118438, 0.273884}, {0.131293, 0.192201},
            {0.115869, 0.129797}, {0.072223, 0.058396}, {0.022884, 0.000718},
            {-0.053355, -0.020405}, {-0.123584, -0.032451}, {-0.216248, -0.032862},
            {-0.278592, -0.005008}, {-0.337655, 0.056658}, {-0.385899, 0.112526},
            {-0.405517, 0.186957}, {-0.415374, 0.262071}, {-0.387482, 0.343398},
            {-0.347322, 0.397943}, {-0.287623, 0.458425}, {-0.223502, 0.475513},
            {-0.135352, 0.478186}, {-0.061221, 0.483371}, {0.003711, 0.422737},
            {0.065054, 0.375830}, {0.108108, 0.297099}, {0.123882, 0.222850},
            {0.117729, 0.134382}, {0.085195, 0.056820}, {0.029800, -0.019138},
            {-0.027520, -0.072374}, {-0.102268, -0.091555}, {-0.200299, -0.106578},
            {-0.292731, -0.091473}, {-0.356288, -0.051108}, {-0.420561, 0.014926},
            {-0.471036, 0.074716}, {-0.488638, 0.182508}, {-0.485990, 0.254068},
            {-0.463943, 0.338438}, {-0.406453, 0.404704}, {-0.334287, 0.466119},
            {-0.254244, 0.503188}, {-0.161548, 0.495769}, {-0.075733, 0.495560},
            {0.001375, 0.434937}, {0.082787, 0.385806}, {0.115490, 0.323807},
            {0.141089, 0.223450}, {0.138693, 0.131703}, {0.126415, 0.049174},
            {0.066518, -0.010217}, {-0.005184, -0.070647}, {-0.080985, -0.103635},
            {-0.177377, -0.116887}, {-0.260628, -0.100258}, {-0.335756, -0.056251},
            {-0.405195, -0.000895}, {-0.444937, 0.085456}, {-0.484357, 0.175597},
            {-0.472453, 0.248681}, {-0.438580, 0.347463}, {-0.402304, 0.422428},
            {-0.326777, 0.479438}, {-0.247797, 0.505581}, {-0.152676, 0.519380},
            {-0.071754, 0.516264}, {0.015942, 0.472802}, {0.076608, 0.419077},
            {0.127673, 0.330264}, {0.159951, 0.262150}, {0.153530, 0.172681},
            {0.140653, 0.089229}, {0.078666, 0.024981}, {0.023807, -0.037022},
            {-0.048837, -0.077056}, {-0.127729, -0.075338}, {-0.221271, -0.067526}
    };

    public void doTestStRD(final StatisticalReferenceDataset dataset,
                           final LeastSquaresOptimizer optimizer,
                           final double errParams,
                           final double errParamsSd) {

        final Optimum optimum = optimizer.optimize(builder(dataset).build());

        final RealVector actual = optimum.getPoint();
        for (int i = 0; i < actual.getDimension(); i++) {
            double expected = dataset.getParameter(i);
            double delta = FastMath.abs(errParams * expected);
            Assert.assertEquals(dataset.getName() + ", param #" + i,
                    expected, actual.getEntry(i), delta);
        }
    }

    @Test
    public void testKirby2() throws IOException {
        doTestStRD(StatisticalReferenceDatasetFactory.createKirby2(), optimizer, 1E-7, 1E-7);
    }

    @Test
    public void testHahn1() throws IOException {
        doTestStRD(StatisticalReferenceDatasetFactory.createHahn1(), optimizer, 1E-7, 1E-4);
    }

    @Test
    public void testPointCopy() {
        LinearProblem problem = new LinearProblem(new double[][]{
                {1, 0, 0},
                {-1, 1, 0},
                {0, -1, 1}
        }, new double[]{1, 1, 1});
        //mutable boolean
        final boolean[] checked = {false};

        final LeastSquaresBuilder builder = problem.getBuilder()
                .checker(new ConvergenceChecker<Evaluation>() {
                    public boolean converged(int iteration, Evaluation previous, Evaluation current) {
                        Assert.assertThat(
                                previous.getPoint(),
                                not(sameInstance(current.getPoint())));
                        Assert.assertArrayEquals(new double[3], previous.getPoint().toArray(), 0);
                        Assert.assertArrayEquals(new double[] {1, 2, 3}, current.getPoint().toArray(), TOl);
                        checked[0] = true;
                        return true;
                    }
                });
        optimizer.optimize(builder.build());

        Assert.assertThat(checked[0], is(true));
    }

    class LinearProblem {
        private final RealMatrix factors;
        private final double[] target;

        public LinearProblem(double[][] factors, double[] target) {
            this.factors = new BlockRealMatrix(factors);
            this.target = target;
        }

        public double[] getTarget() {
            return target;
        }

        public MultivariateVectorFunction getModelFunction() {
            return new MultivariateVectorFunction() {
                public double[] value(double[] params) {
                    return factors.operate(params);
                }
            };
        }

        public MultivariateMatrixFunction getModelFunctionJacobian() {
            return new MultivariateMatrixFunction() {
                public double[][] value(double[] params) {
                    return factors.getData();
                }
            };
        }

        public LeastSquaresBuilder getBuilder() {
            final double[] weights = new double[target.length];
            Arrays.fill(weights, 1.0);
            return base()
                    .model(getModelFunction(), getModelFunctionJacobian())
                    .target(target)
                    .weight(new DiagonalMatrix(weights))
                    .start(new double[factors.getColumnDimension()]);
        }
    }
}
