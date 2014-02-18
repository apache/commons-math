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

import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.fitting.leastsquares.GaussNewtonOptimizer.Decomposition;
import org.apache.commons.math3.optim.SimpleVectorValueChecker;
import org.junit.Test;
import org.junit.Assert;

import java.io.IOException;

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
    extends AbstractLeastSquaresOptimizerAbstractTest {

    @Override
    public int getMaxIterations() {
        return 1000;
    }

    @Test
    public void testGaussNewtonLU() throws Exception {
        check(new GaussNewtonOptimizer(Decomposition.LU));
    }

    @Test
    public void testGaussNewtonQR() throws Exception {
        check(new GaussNewtonOptimizer(Decomposition.QR));
    }

    @Override
    public void check(LeastSquaresOptimizer optimizer) throws Exception {
        super.check(optimizer);
        //add an additional test
        testMaxEvaluations(optimizer);
    }

    @Override
    public void testMoreEstimatedParametersSimple(LeastSquaresOptimizer optimizer) {
        /*
         * Exception is expected with this optimizer
         */
        try {
            super.testMoreEstimatedParametersSimple(optimizer);
            fail(optimizer);
        } catch (ConvergenceException e) {
            //expected
        }
    }

    @Override
    public void testMoreEstimatedParametersUnsorted(LeastSquaresOptimizer optimizer) {
        /*
         * Exception is expected with this optimizer
         */
        try{
            super.testMoreEstimatedParametersUnsorted(optimizer);
            fail(optimizer);
        }catch (ConvergenceException e){
            //expected
        }
    }

    public void testMaxEvaluations(LeastSquaresOptimizer optimizer) throws Exception {
        try{
        CircleVectorial circle = new CircleVectorial();
        circle.addPoint( 30.0,  68.0);
        circle.addPoint( 50.0,  -6.0);
        circle.addPoint(110.0, -20.0);
        circle.addPoint( 35.0,  15.0);
        circle.addPoint( 45.0,  97.0);

        LeastSquaresProblem lsp = builder(circle)
                .checker(new SimpleVectorValueChecker(1e-30, 1e-30))
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
    public void testCircleFittingBadInit(LeastSquaresOptimizer optimizer) {
        /*
         * This test does not converge with this optimizer.
         */
        try{
            super.testCircleFittingBadInit(optimizer);
            fail(optimizer);
        }catch (ConvergenceException e){
            //expected
        }
    }

    @Override
    public void testHahn1(LeastSquaresOptimizer optimizer)
        throws IOException {
        /*
         * TODO This test leads to a singular problem with the Gauss-Newton
         * optimizer. This should be inquired.
         */
        try{
            super.testHahn1(optimizer);
            fail(optimizer);
        } catch (ConvergenceException e){
            //expected for LU
        } catch (TooManyEvaluationsException e){
            //expected for QR
        }
    }

}
