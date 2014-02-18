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
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.optim.AbstractOptimizationProblem;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.PointVectorValuePair;
import org.apache.commons.math3.util.FastMath;

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
    /** Weight matrix. */
    private RealMatrix weight;
    /** Model function. */
    private MultivariateVectorFunction model;
    /** Jacobian of the model function. */
    private MultivariateMatrixFunction jacobian;
    /** Square-root of the weight matrix. */
    private RealMatrix weightSqrt;
    /** Initial guess. */
    private double[] start;

    LeastSquaresProblemImpl(final int maxEvaluations,
                            final int maxIterations,
                            final ConvergenceChecker<PointVectorValuePair> checker,
                            final double[] target,
                            final RealMatrix weight,
                            final MultivariateVectorFunction model,
                            final MultivariateMatrixFunction jacobian,
                            final double[] start) {
        super(maxEvaluations, maxIterations, checker);
        this.target = target;
        this.weight = weight;
        this.model = model;
        this.jacobian = jacobian;
        this.weightSqrt = squareRoot(weight);
        this.start = start;
    }

    public int getObservationSize() {
        return target.length;
    }

    public int getParameterSize() {
        return start.length;
    }

    /**
     * Gets the target values.
     *
     * @return the target values.
     */
    public double[] getTarget() {
        return target == null ? null : target.clone();
    }

    public double[] getStart() {
        return start == null ? null : start.clone();
    }

    /**
     * Gets the square-root of the weight matrix.
     *
     * @return the square-root of the weight matrix.
     */
    public RealMatrix getWeightSquareRoot() {
        return weightSqrt == null ? null : weightSqrt.copy();
    }

    /**
     * Gets the model function.
     *
     * @return the model function.
     */
    public MultivariateVectorFunction getModel() {
        return model;
    }

    /**
     * Gets the model function's Jacobian.
     *
     * @return the Jacobian.
     */
    public MultivariateMatrixFunction getJacobian() {
        return jacobian;
    }

    public RealMatrix getWeight() {
        return weight.copy();
    }

    public Evaluation evaluate(final double[] point) {
        //TODO evaluate value and jacobian in one function call
        return new EvaluationImpl(
                this.model.value(point),
                this.jacobian.value(point),
                this.weight,
                this.weightSqrt,
                this.target,
                point);
    }

    /**
     * Container with the model evaluation at a particular point.
     * <p/>
     * TODO revisit lazy evaluation
     */
    private static class EvaluationImpl implements Evaluation {

        /** the point of evaluation */
        private final double[] point;
        /** value at point */
        private final double[] values;
        /** deriviative at point */
        private final double[][] jacobian;
        /* references to data defined by the least squares problem. Not modified.
         * Could be a reference to the problem.
         */
        private final RealMatrix weight;
        private final RealMatrix weightSqrt;
        private final double[] target;

        private EvaluationImpl(final double[] values,
                               final double[][] jacobian,
                               final RealMatrix weight,
                               final RealMatrix weightSqrt,
                               final double[] target,
                               final double[] point) {
            this.values = values;
            this.jacobian = jacobian;
            this.weight = weight;
            this.weightSqrt = weightSqrt;
            this.target = target;
            this.point = point;
        }

        public double[][] computeCovariances(double threshold) {
            // Set up the Jacobian.
            final RealMatrix j = computeWeightedJacobian();

            // Compute transpose(J)J.
            final RealMatrix jTj = j.transpose().multiply(j);

            // Compute the covariances matrix.
            final DecompositionSolver solver
                    = new QRDecomposition(jTj, threshold).getSolver();
            return solver.getInverse().getData();
        }

        public double[] computeSigma(double covarianceSingularityThreshold) {
            final double[][] cov = computeCovariances(covarianceSingularityThreshold);
            final int nC = cov.length;
            final double[] sig = new double[nC];
            for (int i = 0; i < nC; ++i) {
                sig[i] = FastMath.sqrt(cov[i][i]);
            }
            return sig;
        }

        public double computeRMS() {
            final double cost = computeCost();
            return FastMath.sqrt(cost * cost / target.length);
        }

        public double[] computeValue() {
            return this.values;
        }

        public RealMatrix computeWeightedJacobian() {
            return weightSqrt.multiply(MatrixUtils.createRealMatrix(computeJacobian()));
        }

        public double[][] computeJacobian() {
            return this.jacobian;
        }

        public double computeCost() {
            final ArrayRealVector r = new ArrayRealVector(computeResiduals());
            return FastMath.sqrt(r.dotProduct(weight.operate(r)));
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

        public double[] getPoint() {
            //TODO copy?
            return this.point;
        }
    }

    /**
     * Computes the square-root of the weight matrix.
     *
     * @param m Symmetric, positive-definite (weight) matrix.
     * @return the square-root of the weight matrix.
     */
    private RealMatrix squareRoot(RealMatrix m) {
        if (m instanceof DiagonalMatrix) {
            final int dim = m.getRowDimension();
            final RealMatrix sqrtM = new DiagonalMatrix(dim);
            for (int i = 0; i < dim; i++) {
                sqrtM.setEntry(i, i, FastMath.sqrt(m.getEntry(i, i)));
            }
            return sqrtM;
        } else {
            final EigenDecomposition dec = new EigenDecomposition(m);
            return dec.getSquareRoot();
        }
    }
}
