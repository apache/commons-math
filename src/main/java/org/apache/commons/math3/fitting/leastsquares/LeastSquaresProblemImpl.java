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

import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.optim.AbstractOptimizationProblem;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.PointVectorValuePair;

/**
 * A private, "field" immutable (not "real" immutable) implementation of {@link
 * LeastSquaresProblem}.
 *
 * @version $Id$
 * @since 3.3
 */
class LeastSquaresProblemImpl
        extends AbstractOptimizationProblem<PointVectorValuePair>
        implements LeastSquaresProblem {

    /** Target values for the model function at optimum. */
    private double[] target;
    /** Model function. */
    private MultivariateVectorFunction model;
    /** Jacobian of the model function. */
    private MultivariateMatrixFunction jacobian;
    /** Initial guess. */
    private double[] start;

    LeastSquaresProblemImpl(final MultivariateVectorFunction model,
                            final MultivariateMatrixFunction jacobian,
                            final double[] target,
                            final double[] start,
                            final ConvergenceChecker<PointVectorValuePair> checker,
                            final int maxEvaluations,
                            final int maxIterations) {
        super(maxEvaluations, maxIterations, checker);
        this.target = target;
        this.model = model;
        this.jacobian = jacobian;
        this.start = start;
    }

    public int getObservationSize() {
        return target.length;
    }

    public int getParameterSize() {
        return start.length;
    }

    public double[] getStart() {
        return start == null ? null : start.clone();
    }

    public Evaluation evaluate(final double[] point) {
        //TODO evaluate value and jacobian in one function call
        return new UnweightedEvaluation(
                this.model.value(point),
                this.jacobian.value(point),
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
        private final double[] point;
        /** value at point */
        private final double[] values;
        /** deriviative at point */
        private final double[][] jacobian;
        /** reference to the observed values */
        private final double[] target;

        private UnweightedEvaluation(final double[] values,
                                     final double[][] jacobian,
                                     final double[] target,
                                     final double[] point) {
            super(target.length);
            this.values = values;
            this.jacobian = jacobian;
            this.target = target;
            this.point = point;
        }


        public double[] computeValue() {
            return this.values;
        }

        public RealMatrix computeJacobian() {
            return MatrixUtils.createRealMatrix(this.jacobian);
        }


        public double[] getPoint() {
            return this.point;
        }

        public double[] computeResiduals() {
            final double[] objectiveValue = this.computeValue();
            if (objectiveValue.length != target.length) {
                throw new DimensionMismatchException(target.length,
                        objectiveValue.length);
            }

            final double[] residuals = new double[target.length];
            for (int i = 0; i < target.length; i++) {
                residuals[i] = target[i] - objectiveValue[i];
            }

            return residuals;
        }

    }

}
