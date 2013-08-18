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

package org.apache.commons.math3.fitting.leastsquares;

import java.io.IOException;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.optim.SimpleVectorValueChecker;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.junit.Test;
import org.junit.Assert;

/**
 * <p>Some of the unit tests are re-implementations of the MINPACK <a
 * href="http://www.netlib.org/minpack/ex/file17">file17</a> and <a
 * href="http://www.netlib.org/minpack/ex/file22">file22</a> test files.
 * The redistribution policy for MINPACK is available <a
 * href="http://www.netlib.org/minpack/disclaimer">here</a>/
 *
 * @version $Id$
 */
public class GaussNewtonOptimizerTest
    extends AbstractLeastSquaresOptimizerAbstractTest<GaussNewtonOptimizer> {
    @Override
    public GaussNewtonOptimizer createOptimizer() {
        return GaussNewtonOptimizer.create()
            .withConvergenceChecker(new SimpleVectorValueChecker(1e-6, 1e-6));
    }

    @Override
    public int getMaxIterations() {
        return 1000;
    }

    @Override
    @Test
    public void testShallowCopy() {
        super.testShallowCopy(); // Test copy of parent.

        final boolean useLU1 = false;
        final GaussNewtonOptimizer optim1 = createOptimizer()
            .withLU(useLU1);

        final GaussNewtonOptimizer optim2 = optim1.shallowCopy();

        // Check that all fields have the same values.
        Assert.assertTrue(optim1.getLU() == optim2.getLU());

        // Change "optim2".
        final boolean useLU2 = true;
        optim2.withLU(useLU2);

        // Check that all fields now have different values.
        Assert.assertFalse(optim1.getLU() == optim2.getLU());
    }

    @Override
    @Test(expected=ConvergenceException.class)
    public void testMoreEstimatedParametersSimple() {
        /*
         * Exception is expected with this optimizer
         */
        super.testMoreEstimatedParametersSimple();
    }

    @Override
    @Test(expected=ConvergenceException.class)
    public void testMoreEstimatedParametersUnsorted() {
        /*
         * Exception is expected with this optimizer
         */
        super.testMoreEstimatedParametersUnsorted();
    }

    @Test(expected=TooManyEvaluationsException.class)
    public void testMaxEvaluations() throws Exception {
        CircleVectorial circle = new CircleVectorial();
        circle.addPoint( 30.0,  68.0);
        circle.addPoint( 50.0,  -6.0);
        circle.addPoint(110.0, -20.0);
        circle.addPoint( 35.0,  15.0);
        circle.addPoint( 45.0,  97.0);

        GaussNewtonOptimizer optimizer = createOptimizer()
            .withConvergenceChecker(new SimpleVectorValueChecker(1e-30, 1e-30))
            .withMaxIterations(Integer.MAX_VALUE)
            .withMaxEvaluations(100)
            .withModelAndJacobian(circle.getModelFunction(),
                                  circle.getModelFunctionJacobian())
            .withTarget(new double[] { 0, 0, 0, 0, 0 })
            .withWeight(new DiagonalMatrix(new double[] { 1, 1, 1, 1, 1 }))
            .withStartPoint(new double[] { 98.680, 47.345 });

        optimizer.optimize();
    }

    @Override
    @Test(expected=ConvergenceException.class)
    public void testCircleFittingBadInit() {
        /*
         * This test does not converge with this optimizer.
         */
        super.testCircleFittingBadInit();
    }

    @Override
    @Test(expected=ConvergenceException.class)
    public void testHahn1()
        throws IOException {
        /*
         * TODO This test leads to a singular problem with the Gauss-Newton
         * optimizer. This should be inquired.
         */
        super.testHahn1();
    }
}
