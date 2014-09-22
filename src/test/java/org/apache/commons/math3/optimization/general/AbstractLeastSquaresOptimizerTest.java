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
package org.apache.commons.math3.optimization.general;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Assert;
import org.apache.commons.math3.optimization.PointVectorValuePair;
import org.apache.commons.math3.util.FastMath;
import org.junit.Test;

@Deprecated
public class AbstractLeastSquaresOptimizerTest {

    public static AbstractLeastSquaresOptimizer createOptimizer() {
        return new AbstractLeastSquaresOptimizer(null) {

            @Override
            protected PointVectorValuePair doOptimize() {
                final double[] params = getStartPoint();
                final double[] res = computeResiduals(computeObjectiveValue(params));
                setCost(computeCost(res));
                return new PointVectorValuePair(params, null);
            }
        };
    }

    @Test
    public void testGetChiSquare() throws IOException {
        final StatisticalReferenceDataset dataset;
        dataset = StatisticalReferenceDatasetFactory.createKirby2();
        final AbstractLeastSquaresOptimizer optimizer;
        optimizer = createOptimizer();
        final double[] a = dataset.getParameters();
        final double[] y = dataset.getData()[1];
        final double[] w = new double[y.length];
        Arrays.fill(w, 1.0);

        optimizer.optimize(1, dataset.getLeastSquaresProblem(), y, w, a);
        final double expected = dataset.getResidualSumOfSquares();
        final double actual = optimizer.getChiSquare();
        Assert.assertEquals(dataset.getName(), expected, actual,
                            1E-11 * expected);
    }

    @Test
    public void testGetRMS() throws IOException {
        final StatisticalReferenceDataset dataset;
        dataset = StatisticalReferenceDatasetFactory.createKirby2();
        final AbstractLeastSquaresOptimizer optimizer;
        optimizer = createOptimizer();
        final double[] a = dataset.getParameters();
        final double[] y = dataset.getData()[1];
        final double[] w = new double[y.length];
        Arrays.fill(w, 1.0);

        optimizer.optimize(1, dataset.getLeastSquaresProblem(), y, w, a);
        final double expected = FastMath
            .sqrt(dataset.getResidualSumOfSquares() /
                  dataset.getNumObservations());
        final double actual = optimizer.getRMS();
        Assert.assertEquals(dataset.getName(), expected, actual,
                            1E-11 * expected);
    }

    @Test
    public void testComputeSigma() throws IOException {
        final StatisticalReferenceDataset dataset;
        dataset = StatisticalReferenceDatasetFactory.createKirby2();
        final AbstractLeastSquaresOptimizer optimizer;
        optimizer = createOptimizer();
        final double[] a = dataset.getParameters();
        final double[] y = dataset.getData()[1];
        final double[] w = new double[y.length];
        Arrays.fill(w, 1.0);

        final int dof = y.length - a.length;
        final PointVectorValuePair optimum = optimizer.optimize(1, dataset.getLeastSquaresProblem(), y, w, a);
        final double[] sig = optimizer.computeSigma(optimum.getPoint(), 1e-14);
        final double[] expected = dataset.getParametersStandardDeviations();
        for (int i = 0; i < sig.length; i++) {
            final double actual = FastMath.sqrt(optimizer.getChiSquare() / dof) * sig[i];
            Assert.assertEquals(dataset.getName() + ", parameter #" + i,
                                expected[i], actual, 1e-7 * expected[i]);
        }
    }
}
