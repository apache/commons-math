/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.commons.math3.fitting.leastsquares;

import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.math3.optim.PointVectorValuePair;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.util.FastMath;
import org.junit.Test;
import org.junit.Assert;
/**
 * The only features tested here are utility methods defined
 * in {@link AbstractLeastSquaresOptimizer} that compute the
 * chi-square and parameters standard-deviations.
 */
public class AbstractLeastSquaresOptimizerTest {
    @Test
    public void testComputeCost() throws IOException {
        final StatisticalReferenceDataset dataset
            = StatisticalReferenceDatasetFactory.createKirby2();
        final double[] a = dataset.getParameters();
        final double[] y = dataset.getData()[1];
        final double[] w = new double[y.length];
        Arrays.fill(w, 1d);

        StatisticalReferenceDataset.LeastSquaresProblem problem
            = dataset.getLeastSquaresProblem();

        final LevenbergMarquardtOptimizer optim = LevenbergMarquardtOptimizer.create()
            .withModelAndJacobian(problem.getModelFunction(),
                                  problem.getModelFunctionJacobian())
            .withTarget(y)
            .withWeight(new DiagonalMatrix(w))
            .withStartPoint(a);

        final double expected = dataset.getResidualSumOfSquares();
        final double cost = optim.computeCost(optim.computeResiduals(optim.getModel().value(optim.getStart())));
        final double actual = cost * cost;
        Assert.assertEquals(dataset.getName(), expected, actual, 1e-11 * expected);
    }

    @Test
    public void testComputeRMS() throws IOException {
        final StatisticalReferenceDataset dataset
            = StatisticalReferenceDatasetFactory.createKirby2();
        final double[] a = dataset.getParameters();
        final double[] y = dataset.getData()[1];
        final double[] w = new double[y.length];
        Arrays.fill(w, 1d);

        StatisticalReferenceDataset.LeastSquaresProblem problem
            = dataset.getLeastSquaresProblem();

        final LevenbergMarquardtOptimizer optim = LevenbergMarquardtOptimizer.create()
            .withModelAndJacobian(problem.getModelFunction(),
                                  problem.getModelFunctionJacobian())
            .withTarget(y)
            .withWeight(new DiagonalMatrix(w))
            .withStartPoint(a);

        final double expected = FastMath.sqrt(dataset.getResidualSumOfSquares() /
                                              dataset.getNumObservations());
        final double actual = optim.computeRMS(optim.getStart());
        Assert.assertEquals(dataset.getName(), expected, actual, 1e-11 * expected);
    }

    @Test
    public void testComputeSigma() throws IOException {
        final StatisticalReferenceDataset dataset
            = StatisticalReferenceDatasetFactory.createKirby2();
        final double[] a = dataset.getParameters();
        final double[] y = dataset.getData()[1];
        final double[] w = new double[y.length];
        Arrays.fill(w, 1d);

        StatisticalReferenceDataset.LeastSquaresProblem problem
            = dataset.getLeastSquaresProblem();

        final LevenbergMarquardtOptimizer optim = LevenbergMarquardtOptimizer.create()
            .withModelAndJacobian(problem.getModelFunction(),
                                  problem.getModelFunctionJacobian())
            .withTarget(y)
            .withWeight(new DiagonalMatrix(w))
            .withStartPoint(a);

        final double[] expected = dataset.getParametersStandardDeviations();

        final double cost = optim.computeCost(optim.computeResiduals(optim.getModel().value(optim.getStart())));
        final double[] sig = optim.computeSigma(optim.getStart(), 1e-14);
        final int dof = y.length - a.length;
        for (int i = 0; i < sig.length; i++) {
            final double actual = FastMath.sqrt(cost * cost / dof) * sig[i];
            Assert.assertEquals(dataset.getName() + ", parameter #" + i,
                                expected[i], actual, 1e-6 * expected[i]);
        }
    }
}
