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

import org.apache.commons.math4.legacy.linear.RealVector;
import org.apache.commons.math4.legacy.optim.ConvergenceChecker;
import org.apache.commons.math4.legacy.core.IntegerSequence;

/**
 * An adapter that delegates to another implementation of {@link LeastSquaresProblem}.
 *
 * @since 3.3
 */
public class LeastSquaresAdapter implements LeastSquaresProblem {

    /** the delegate problem. */
    private final LeastSquaresProblem problem;

    /**
     * Delegate the {@link LeastSquaresProblem} interface to the given implementation.
     *
     * @param problem the delegate
     */
    public LeastSquaresAdapter(final LeastSquaresProblem problem) {
        this.problem = problem;
    }

    /** {@inheritDoc} */
    @Override
    public RealVector getStart() {
        return problem.getStart();
    }

    /** {@inheritDoc} */
    @Override
    public int getObservationSize() {
        return problem.getObservationSize();
    }

    /** {@inheritDoc} */
    @Override
    public int getParameterSize() {
        return problem.getParameterSize();
    }

    /** {@inheritDoc}
     */
    @Override
    public Evaluation evaluate(final RealVector point) {
        return problem.evaluate(point);
    }

    /** {@inheritDoc} */
    @Override
    public IntegerSequence.Incrementor getEvaluationCounter() {
        return problem.getEvaluationCounter();
    }

    /** {@inheritDoc} */
    @Override
    public IntegerSequence.Incrementor getIterationCounter() {
        return problem.getIterationCounter();
    }

    /** {@inheritDoc} */
    @Override
    public ConvergenceChecker<Evaluation> getConvergenceChecker() {
        return problem.getConvergenceChecker();
    }
}
