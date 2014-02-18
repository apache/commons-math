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

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem.Evaluation;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
/**
 * The only features tested here are utility methods defined
 * in {@link LeastSquaresProblem.Evaluation} that compute the
 * chi-square and parameters standard-deviations.
 */
public class EvaluationTest {

    /**
     * Create a {@link LeastSquaresBuilder} from a {@link StatisticalReferenceDataset}.
     *
     * @param dataset the source data
     * @return a builder for further customization.
     */
    public LeastSquaresBuilder builder(StatisticalReferenceDataset dataset) {
        StatisticalReferenceDataset.LeastSquaresProblem problem
                = dataset.getLeastSquaresProblem();
        final double[] start = dataset.getParameters();
        final double[] observed = dataset.getData()[1];
        final double[] weights = new double[observed.length];
        Arrays.fill(weights, 1d);

        return new LeastSquaresBuilder()
                .model(problem.getModelFunction(), problem.getModelFunctionJacobian())
                .target(observed)
                .weight(new DiagonalMatrix(weights))
                .start(start);
    }

    @Test
    public void testComputeCost() throws IOException {
        final StatisticalReferenceDataset dataset
            = StatisticalReferenceDatasetFactory.createKirby2();

        final LeastSquaresProblem lsp = builder(dataset).build();

        final double expected = dataset.getResidualSumOfSquares();
        final double cost = lsp.evaluate(lsp.getStart()).computeCost();
        final double actual = cost * cost;
        Assert.assertEquals(dataset.getName(), expected, actual, 1e-11 * expected);
    }

    @Test
    public void testComputeRMS() throws IOException {
        final StatisticalReferenceDataset dataset
            = StatisticalReferenceDatasetFactory.createKirby2();

        final LeastSquaresProblem lsp = builder(dataset).build();

        final double expected = FastMath.sqrt(dataset.getResidualSumOfSquares() /
                                              dataset.getNumObservations());
        final double actual = lsp.evaluate(lsp.getStart()).computeRMS();
        Assert.assertEquals(dataset.getName(), expected, actual, 1e-11 * expected);
    }

    @Test
    public void testComputeSigma() throws IOException {
        final StatisticalReferenceDataset dataset
            = StatisticalReferenceDatasetFactory.createKirby2();

        final LeastSquaresProblem lsp = builder(dataset).build();

        final double[] expected = dataset.getParametersStandardDeviations();

        final Evaluation evaluation = lsp.evaluate(lsp.getStart());
        final double cost = evaluation.computeCost();
        final RealVector sig = evaluation.computeSigma(1e-14);
        final int dof = lsp.getObservationSize() - lsp.getParameterSize();
        for (int i = 0; i < sig.getDimension(); i++) {
            final double actual = FastMath.sqrt(cost * cost / dof) * sig.getEntry(i);
            Assert.assertEquals(dataset.getName() + ", parameter #" + i,
                                expected[i], actual, 1e-6 * expected[i]);
        }
    }
}
