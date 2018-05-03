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

import org.apache.commons.math4.analysis.differentiation.FiniteDifferencesDifferentiator;
import org.apache.commons.math4.analysis.differentiation.UnivariateVectorFunctionDifferentiator;

public class DifferentiatorVectorMultivariateJacobianFunctionTest
        extends LevenbergMarquardtOptimizerTest {

    protected static final int POINTS = 20;
    protected static final double STEP_SIZE = 0.2;
    protected static final UnivariateVectorFunctionDifferentiator differentiator = new FiniteDifferencesDifferentiator(POINTS, STEP_SIZE);

    @Override
    public LeastSquaresBuilder builder(BevingtonProblem problem){
        return super.builder(problem)
            .model(new DifferentiatorVectorMultivariateJacobianFunction(problem.getModelFunction(), differentiator));
    }

    @Override
    public LeastSquaresBuilder builder(CircleProblem problem){
        return super.builder(problem)
                .model(new DifferentiatorVectorMultivariateJacobianFunction(problem.getModelFunction(), differentiator));
    }
}
