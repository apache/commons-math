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

package org.apache.commons.math.optimization.general;

import org.apache.commons.math.linear.InvalidMatrixException;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.decomposition.LUDecompositionImpl;
import org.apache.commons.math.optimization.ObjectiveException;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.SimpleVectorialValueChecker;
import org.apache.commons.math.optimization.VectorialConvergenceChecker;
import org.apache.commons.math.optimization.VectorialDifferentiableObjectiveFunction;
import org.apache.commons.math.optimization.VectorialDifferentiableOptimizer;
import org.apache.commons.math.optimization.VectorialPointValuePair;

/**
 * Base class for implementing least squares optimizers.
 * <p>This base class handles the boilerplate methods associated to thresholds
 * settings, jacobian and error estimation.</p>
 * @version $Revision$ $Date$
 * @since 1.2
 *
 */
public abstract class AbstractLeastSquaresOptimizer implements VectorialDifferentiableOptimizer {

    /** Serializable version identifier */
    private static final long serialVersionUID = -3080152374642370722L;

    /** Default maximal number of objective function evaluations allowed. */
    public static final int DEFAULT_MAX_EVALUATIONS = 100;

    /** Number of evaluations already performed for the current start. */
    private int objectiveEvaluations;

    /** Number of jacobian evaluations. */
    private int jacobianEvaluations;

    /** Maximal number of evaluations allowed. */
    private int maxEvaluations;

    /** Convergence checker. */
    protected VectorialConvergenceChecker checker;

    /** 
     * Jacobian matrix.
     * <p>This matrix is in canonical form just after the calls to
     * {@link #updateJacobian()}, but may be modified by the solver
     * in the derived class (the {@link LevenbergMarquardtOptimizer
     * Levenberg-Marquardt optimizer} does this).</p>
     */
    protected double[][] jacobian;

    /** Number of columns of the jacobian matrix. */
    protected int cols;

    /** Number of rows of the jacobian matrix. */
    protected int rows;

    /** Objective function. */
    private VectorialDifferentiableObjectiveFunction f;

    /** Target value for the objective functions at optimum. */
    protected double[] target;

    /** Weight for the least squares cost computation. */
    protected double[] weights;

    /** Current variables set. */
    protected double[] variables;

    /** Current objective function value. */
    protected double[] objective;

    /** Current residuals. */
    protected double[] residuals;

    /** Cost value (square root of the sum of the residuals). */
    protected double cost;

    /** Simple constructor with default settings.
     * <p>The convergence check is set to a {@link SimpleVectorialValueChecker}
     * and the maximal number of evaluation is set to its default value.</p>
     */
    protected AbstractLeastSquaresOptimizer() {
        setConvergenceChecker(new SimpleVectorialValueChecker());
        setMaxEvaluations(DEFAULT_MAX_EVALUATIONS);
    }

    /** {@inheritDoc} */
    public void setMaxEvaluations(int maxEvaluations) {
        this.maxEvaluations = maxEvaluations;
    }

    /** {@inheritDoc} */
    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    /** {@inheritDoc} */
    public int getEvaluations() {
        return objectiveEvaluations;
    }

    /** {@inheritDoc} */
    public int getJacobianEvaluations() {
        return jacobianEvaluations;
    }

    /** {@inheritDoc} */
    public void setConvergenceChecker(VectorialConvergenceChecker checker) {
        this.checker = checker;
    }

    /** {@inheritDoc} */
    public VectorialConvergenceChecker getConvergenceChecker() {
        return checker;
    }

    /** 
     * Update the jacobian matrix.
     * @exception ObjectiveException if the function jacobian
     * cannot be evaluated or its dimension doesn't match problem dimension
     */
    protected void updateJacobian() throws ObjectiveException {
        incrementJacobianEvaluationsCounter();
        jacobian = f.jacobian(variables, objective);
        if (jacobian.length != rows) {
            throw new ObjectiveException("dimension mismatch {0} != {1}",
                                         jacobian.length, rows);
        }
        for (int i = 0; i < rows; i++) {
            final double[] ji = jacobian[i];
            final double factor = -Math.sqrt(weights[i]);
            for (int j = 0; j < cols; ++j) {
                ji[j] *= factor;
            }
        }
    }

    /**
     * Increment the jacobian evaluations counter.
     */
    protected final void incrementJacobianEvaluationsCounter() {
        ++jacobianEvaluations;
    }

    /** 
     * Update the residuals array and cost function value.
     * @exception ObjectiveException if the function cannot be evaluated
     * or its dimension doesn't match problem dimension
     * @exception OptimizationException if the number of cost evaluations
     * exceeds the maximum allowed
     */
    protected void updateResidualsAndCost()
        throws ObjectiveException, OptimizationException {

        if (++objectiveEvaluations > maxEvaluations) {
            throw new OptimizationException(
                    "maximal number of evaluations exceeded ({0})",
                    objectiveEvaluations);
        }

        objective = f.objective(variables);
        if (objective.length != rows) {
            throw new ObjectiveException("dimension mismatch {0} != {1}",
                                         objective.length, rows);
        }
        cost = 0;
        for (int i = 0, index = 0; i < rows; i++, index += cols) {
            final double residual = target[i] - objective[i];
            residuals[i] = residual;
            cost += weights[i] * residual * residual;
        }
        cost = Math.sqrt(cost);

    }

    /** 
     * Get the Root Mean Square value.
     * Get the Root Mean Square value, i.e. the root of the arithmetic
     * mean of the square of all weighted residuals. This is related to the
     * criterion that is minimized by the optimizer as follows: if
     * <em>c</em> if the criterion, and <em>n</em> is the number of
     * measurements, then the RMS is <em>sqrt (c/n)</em>.
     * 
     * @return RMS value
     */
    public double getRMS() {
        double criterion = 0;
        for (int i = 0; i < rows; ++i) {
            final double residual = residuals[i];
            criterion += weights[i] * residual * residual;
        }
        return Math.sqrt(criterion / rows);
    }

    /**
     * Get the Chi-Square value.
     * @return chi-square value
     */
    public double getChiSquare() {
        double chiSquare = 0;
        for (int i = 0; i < rows; ++i) {
            final double residual = residuals[i];
            chiSquare += residual * residual / weights[i];
        }
        return chiSquare;
    }

    /**
     * Get the covariance matrix of optimized parameters.
     * @return covariance matrix
     * @exception ObjectiveException if the function jacobian cannot
     * be evaluated
     * @exception OptimizationException if the covariance matrix
     * cannot be computed (singular problem)
     */
    public double[][] getCovariances()
        throws ObjectiveException, OptimizationException {

        // set up the jacobian
        updateJacobian();

        // compute transpose(J).J, avoiding building big intermediate matrices
        double[][] jTj = new double[cols][cols];
        for (int i = 0; i < cols; ++i) {
            for (int j = i; j < cols; ++j) {
                double sum = 0;
                for (int k = 0; k < rows; ++k) {
                    sum += jacobian[k][i] * jacobian[k][j];
                }
                jTj[i][j] = sum;
                jTj[j][i] = sum;
            }
        }

        try {
            // compute the covariances matrix
            RealMatrix inverse =
                new LUDecompositionImpl(MatrixUtils.createRealMatrix(jTj)).getSolver().getInverse();
            return inverse.getData();
        } catch (InvalidMatrixException ime) {
            throw new OptimizationException("unable to compute covariances: singular problem");
        }

    }

    /**
     * Guess the errors in optimized parameters.
     * <p>Guessing is covariance-based, it only gives rough order of magnitude.</p>
     * @return errors in optimized parameters
     * @exception ObjectiveException if the function jacobian cannot b evaluated
     * @exception OptimizationException if the covariances matrix cannot be computed
     * or the number of degrees of freedom is not positive (number of measurements
     * lesser or equal to number of parameters)
     */
    public double[] guessParametersErrors()
        throws ObjectiveException, OptimizationException {
        if (rows <= cols) {
            throw new OptimizationException(
                    "no degrees of freedom ({0} measurements, {1} parameters)",
                    rows, cols);
        }
        double[] errors = new double[cols];
        final double c = Math.sqrt(getChiSquare() / (rows - cols));
        double[][] covar = getCovariances();
        for (int i = 0; i < errors.length; ++i) {
            errors[i] = Math.sqrt(covar[i][i]) * c;
        }
        return errors;
    }

    /** {@inheritDoc} */
    public VectorialPointValuePair optimize(final VectorialDifferentiableObjectiveFunction f,
                                            final double[] target, final double[] weights,
                                            final double[] startPoint)
        throws ObjectiveException, OptimizationException, IllegalArgumentException {

        if (target.length != weights.length) {
            throw new OptimizationException("dimension mismatch {0} != {1}",
                                            target.length, weights.length);
        }

        // reset counters
        objectiveEvaluations = 0;
        jacobianEvaluations  = 0;

        // store least squares problem characteristics
        this.f         = f;
        this.target    = target;
        this.weights   = weights;
        this.variables = startPoint.clone();
        this.residuals = new double[target.length];

        // arrays shared with the other private methods
        rows      = target.length;
        cols      = variables.length;
        jacobian  = new double[rows][cols];

        cost = Double.POSITIVE_INFINITY;

        return doOptimize();

    }

    /** Perform the bulk of optimization algorithm.
     */
    abstract protected VectorialPointValuePair doOptimize()
    throws ObjectiveException, OptimizationException, IllegalArgumentException;

}