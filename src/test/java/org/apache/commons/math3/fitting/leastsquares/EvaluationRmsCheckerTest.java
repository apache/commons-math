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

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem.Evaluation;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.optim.ConvergenceChecker;
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
        Assert.assertEquals(true, checker.converged(0, e200, mockEvaluation(210)));
        //just matches abs tol
        Assert.assertEquals(true, checker.converged(0, e1, mockEvaluation(1.9)));
        //matches both
        Assert.assertEquals(true, checker.converged(0, e1, mockEvaluation(1.01)));
        //matches neither
        Assert.assertEquals(false, checker.converged(0, e200, mockEvaluation(300)));
    }

    /**
     * Create a mock {@link Evaluation}.
     *
     * @param rms the evaluation's rms.
     * @return a new mock evaluation.
     */
    private static Evaluation mockEvaluation(final double rms) {
        return new Evaluation() {
            public RealMatrix getCovariances(double threshold) {
                return null;
            }

            public RealVector getSigma(double covarianceSingularityThreshold) {
                return null;
            }

            public double getRMS() {
                return rms;
            }

            public RealMatrix getJacobian() {
                return null;
            }

            public double getCost() {
                return 0;
            }

            public RealVector getResiduals() {
                return null;
            }

            public RealVector getPoint() {
                return null;
            }
        };
    }

}
