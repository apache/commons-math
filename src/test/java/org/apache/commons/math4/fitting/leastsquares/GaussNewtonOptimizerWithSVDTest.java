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

import org.apache.commons.math4.exception.ConvergenceException;
import org.apache.commons.math4.exception.TooManyEvaluationsException;
import org.apache.commons.math4.fitting.leastsquares.GaussNewtonOptimizer;
import org.apache.commons.math4.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math4.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math4.fitting.leastsquares.GaussNewtonOptimizer.Decomposition;
import org.apache.commons.math4.fitting.leastsquares.LeastSquaresOptimizer.Optimum;
import org.apache.commons.math4.geometry.euclidean.threed.Plane;
import org.apache.commons.math4.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math4.optim.SimpleVectorValueChecker;
import org.apache.commons.math4.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * <p>Some of the unit tests are re-implementations of the MINPACK <a
 * href="http://www.netlib.org/minpack/ex/file17">file17</a> and <a
 * href="http://www.netlib.org/minpack/ex/file22">file22</a> test files.
 * The redistribution policy for MINPACK is available <a
 * href="http://www.netlib.org/minpack/disclaimer">here</a>/
 *
 */
public class GaussNewtonOptimizerWithSVDTest
    extends AbstractLeastSquaresOptimizerAbstractTest {

    @Override
    public int getMaxIterations() {
        return 1000;
    }

    @Override
    public LeastSquaresOptimizer getOptimizer() {
        return new GaussNewtonOptimizer(Decomposition.SVD);
    }

    @Test
    public void testMaxEvaluations() throws Exception {
        try{
        CircleVectorial circle = new CircleVectorial();
        circle.addPoint( 30.0,  68.0);
        circle.addPoint( 50.0,  -6.0);
        circle.addPoint(110.0, -20.0);
        circle.addPoint( 35.0,  15.0);
        circle.addPoint( 45.0,  97.0);

        LeastSquaresProblem lsp = builder(circle)
                .checkerPair(new SimpleVectorValueChecker(1e-30, 1e-30))
                .maxIterations(Integer.MAX_VALUE)
                .start(new double[]{98.680, 47.345})
                .build();

        optimizer.optimize(lsp);

            fail(optimizer);
        }catch (TooManyEvaluationsException e){
            //expected
        }
    }

    @Override
    @Test
    public void testCircleFittingBadInit() {
        /*
         * This test converged to the wrong solution with this optimizer.
         * It seems that the state becomes so large that the convergence
         * checker's relative tolerance test passes.
         */
        try {
            super.testCircleFittingBadInit();
            fail(optimizer);
        } catch (AssertionError e) {
            //expected
        }
    }

    @Override
    @Test
    public void testHahn1()
        throws IOException {
        /*
         * TODO This test leads to a singular problem with the Gauss-Newton
         * optimizer. This should be inquired.
         */
        try{
            super.testHahn1();
            fail(optimizer);
        } catch (ConvergenceException e){
            //expected for LU
        } catch (TooManyEvaluationsException e){
            //expected for QR
        }
    }

    @Test
    @Override
    public void testGetIterations() {
        /* this diverges with SVD */
        try {
            super.testGetIterations();
            fail(optimizer);
        } catch (TooManyEvaluationsException e) {
            //expected
        }
    }

    @Test
    @Override
    public void testNonInvertible() throws Exception {
        /*  SVD can compute a solution to singular problems.
         *  In this case the target vector, b, is not in the
         *  span of the jacobian matrix, A. The closes point
         *  to b on the plane spanned by A is computed.
         */
        LinearProblem problem = new LinearProblem(new double[][]{
                {1, 2, -3},
                {2, 1, 3},
                {-3, 0, -9}
        }, new double[]{1, 1, 1});

        Optimum optimum = optimizer.optimize(problem.getBuilder().build());

        Plane span = new Plane(Vector3D.ZERO, new Vector3D(1, 2, -3), new Vector3D(2, 1, 0), TOl);
        double expected = FastMath.abs(span.getOffset(new Vector3D(1, 1, 1)));
        double actual = optimum.getResiduals().getNorm();

        //verify
        Assert.assertEquals(expected, actual, TOl);
    }

}
