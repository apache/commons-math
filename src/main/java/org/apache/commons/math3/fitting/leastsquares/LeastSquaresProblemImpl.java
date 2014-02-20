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
import org.apache.commons.math3.optim.AbstractOptimizationProblem;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.util.Pair;

/**
 * A private, "field" immutable (not "real" immutable) implementation of {@link
 * LeastSquaresProblem}.
 *
 * @version $Id$
 * @since 3.3
 */
class LeastSquaresProblemImpl
        extends AbstractOptimizationProblem<Evaluation>
        implements LeastSquaresProblem {

    /** Target values for the model function at optimum. */
    private RealVector target;
    /** Model function. */
    private MultivariateJacobianFunction model;
    /** Initial guess. */
    private RealVector start;

    /**
     * Create a {@link LeastSquaresProblem} from the given data.
     *
     * @param model          the model function
     * @param target         the observed data
     * @param start          the initial guess
     * @param checker        the convergence checker
     * @param maxEvaluations the allowed evaluations
     * @param maxIterations  the allowed iterations
     */
    LeastSquaresProblemImpl(final MultivariateJacobianFunction model,
                            final RealVector target,
                            final RealVector start,
                            final ConvergenceChecker<Evaluation> checker,
                            final int maxEvaluations,
                            final int maxIterations) {
        super(maxEvaluations, maxIterations, checker);
        this.target = target;
        this.model = model;
        this.start = start;
    }

    /** {@inheritDoc} */
    public int getObservationSize() {
        return target.getDimension();
    }

    /** {@inheritDoc} */
    public int getParameterSize() {
        return start.getDimension();
    }

    /** {@inheritDoc} */
    public RealVector getStart() {
        return start == null ? null : start.copy();
    }

    /** {@inheritDoc} */
    public Evaluation evaluate(final RealVector point) {
        //evaluate value and jacobian in one function call
        final Pair<RealVector, RealMatrix> value = this.model.value(point);
        return new UnweightedEvaluation(
                value.getFirst(),
                value.getSecond(),
                this.target,
                point);
    }

    /**
     * Container with the model evaluation at a particular point.
     * <p/>
     * TODO revisit lazy evaluation
     */
    private static class UnweightedEvaluation extends AbstractEvaluation {

        /** the point of evaluation */
        private final RealVector point;
        /** value at point */
        private final RealVector values;
        /** deriviative at point */
        private final RealMatrix jacobian;
        /** reference to the observed values */
        private final RealVector target;

        /**
         * Create an {@link Evaluation} with no weights.
         *
         * @param values   the computed function values
         * @param jacobian the computed function Jacobian
         * @param target   the observed values
         * @param point    the abscissa
         */
        private UnweightedEvaluation(final RealVector values,
                                     final RealMatrix jacobian,
                                     final RealVector target,
                                     final RealVector point) {
            super(target.getDimension());
            this.values = values;
            this.jacobian = jacobian;
            this.target = target;
            this.point = point;
        }

        /** {@inheritDoc} */
        public RealVector getValue() {
            return this.values;
        }

        /** {@inheritDoc} */
        public RealMatrix getJacobian() {
            return this.jacobian;
        }

        /** {@inheritDoc} */
        public RealVector getPoint() {
            return this.point;
        }

        /** {@inheritDoc} */
        public RealVector getResiduals() {
            return target.subtract(this.getValue());
        }

    }

}
