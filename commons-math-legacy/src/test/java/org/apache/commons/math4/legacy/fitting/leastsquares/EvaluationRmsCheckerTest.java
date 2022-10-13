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
package org.apache.commons.math4.legacy.fitting.leastsquares;

import org.apache.commons.math4.legacy.fitting.leastsquares.LeastSquaresProblem.Evaluation;
import org.apache.commons.math4.legacy.linear.RealMatrix;
import org.apache.commons.math4.legacy.linear.RealVector;
import org.apache.commons.math4.legacy.optim.ConvergenceChecker;
import org.junit.Assert;
import org.junit.Test;

/** Unit tests for {@link EvaluationRmsChecker}. */
public class EvaluationRmsCheckerTest {

    /** check {@link ConvergenceChecker#converged(int, Object, Object)}. */
    @Test
    public void testConverged() {
        //setup
        ConvergenceChecker<Evaluation> checker = new EvaluationRmsChecker(0.1, 1);
        Evaluation e200 = mockEvaluation(200);
        Evaluation e1 = mockEvaluation(1);

        //action + verify
        //just matches rel tol
        Assert.assertTrue(checker.converged(0, e200, mockEvaluation(210)));
        //just matches abs tol
        Assert.assertTrue(checker.converged(0, e1, mockEvaluation(1.9)));
        //matches both
        Assert.assertTrue(checker.converged(0, e1, mockEvaluation(1.01)));
        //matches neither
        Assert.assertFalse(checker.converged(0, e200, mockEvaluation(300)));
    }

    /**
     * Create a mock {@link Evaluation}.
     *
     * @param rms the evaluation's rms.
     * @return a new mock evaluation.
     */
    private static Evaluation mockEvaluation(final double rms) {
        return new Evaluation() {
            @Override
            public RealMatrix getCovariances(double threshold) {
                return null;
            }

            @Override
            public RealVector getSigma(double covarianceSingularityThreshold) {
                return null;
            }

            @Override
            public double getRMS() {
                return rms;
            }

            @Override
            public RealMatrix getJacobian() {
                return null;
            }

            @Override
            public double getCost() {
                return 0;
            }

            @Override
            public double getChiSquare() {
                return 0;
            }

            @Override
            public double getReducedChiSquare(int n) {
                return 0;
            }

            @Override
            public RealVector getResiduals() {
                return null;
            }

            @Override
            public RealVector getPoint() {
                return null;
            }
        };
    }
}
