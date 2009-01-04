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

package org.apache.commons.math.estimation;

import java.io.Serializable;

import org.apache.commons.math.linear.InvalidMatrixException;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;
import org.apache.commons.math.linear.RealVectorImpl;

/** 
 * This class implements a solver for estimation problems.
 *
 * <p>This class solves estimation problems using a weighted least
 * squares criterion on the measurement residuals. It uses a
 * Gauss-Newton algorithm.</p>
 *
 * @version $Revision$ $Date$
 * @since 1.2
 *
 */

public class GaussNewtonEstimator extends AbstractEstimator implements Serializable {

    /** 
     * Simple constructor.
     *
     * <p>This constructor builds an estimator and stores its convergence
     * characteristics.</p>
     *
     * <p>An estimator is considered to have converged whenever either
     * the criterion goes below a physical threshold under which
     * improvements are considered useless or when the algorithm is
     * unable to improve it (even if it is still high). The first
     * condition that is met stops the iterations.</p>
     *
     * <p>The fact an estimator has converged does not mean that the
     * model accurately fits the measurements. It only means no better
     * solution can be found, it does not mean this one is good. Such an
     * analysis is left to the caller.</p>
     *
     * <p>If neither conditions are fulfilled before a given number of
     * iterations, the algorithm is considered to have failed and an
     * {@link EstimationException} is thrown.</p>
     *
     * @param maxCostEval maximal number of cost evaluations allowed
     * @param convergence criterion threshold below which we do not need
     * to improve the criterion anymore
     * @param steadyStateThreshold steady state detection threshold, the
     * problem has converged has reached a steady state if
     * <code>Math.abs (Jn - Jn-1) < Jn * convergence</code>, where
     * <code>Jn</code> and <code>Jn-1</code> are the current and
     * preceding criterion value (square sum of the weighted residuals
     * of considered measurements).
     */
    public GaussNewtonEstimator(int maxCostEval,
            double convergence,
            double steadyStateThreshold) {
        setMaxCostEval(maxCostEval);
        this.steadyStateThreshold = steadyStateThreshold;
        this.convergence          = convergence;
    }

    /** 
     * Solve an estimation problem using a least squares criterion.
     *
     * <p>This method set the unbound parameters of the given problem
     * starting from their current values through several iterations. At
     * each step, the unbound parameters are changed in order to
     * minimize a weighted least square criterion based on the
     * measurements of the problem.</p>
     *
     * <p>The iterations are stopped either when the criterion goes
     * below a physical threshold under which improvement are considered
     * useless or when the algorithm is unable to improve it (even if it
     * is still high). The first condition that is met stops the
     * iterations. If the convergence it nos reached before the maximum
     * number of iterations, an {@link EstimationException} is
     * thrown.</p>
     *
     * @param problem estimation problem to solve
     * @exception EstimationException if the problem cannot be solved
     *
     * @see EstimationProblem
     *
     */
    public void estimate(EstimationProblem problem)
    throws EstimationException {

        initializeEstimate(problem);

        // work matrices
        double[] grad             = new double[parameters.length];
        RealVectorImpl bDecrement = new RealVectorImpl(parameters.length);
        double[] bDecrementData   = bDecrement.getDataRef();
        RealMatrix wGradGradT     = MatrixUtils.createRealMatrix(parameters.length, parameters.length);

        // iterate until convergence is reached
        double previous = Double.POSITIVE_INFINITY;
        do {

            // build the linear problem
            incrementJacobianEvaluationsCounter();
            RealVector b = new RealVectorImpl(parameters.length);
            RealMatrix a = MatrixUtils.createRealMatrix(parameters.length, parameters.length);
            for (int i = 0; i < measurements.length; ++i) {
                if (! measurements [i].isIgnored()) {

                    double weight   = measurements[i].getWeight();
                    double residual = measurements[i].getResidual();

                    // compute the normal equation
                    for (int j = 0; j < parameters.length; ++j) {
                        grad[j] = measurements[i].getPartial(parameters[j]);
                        bDecrementData[j] = weight * residual * grad[j];
                    }

                    // build the contribution matrix for measurement i
                    for (int k = 0; k < parameters.length; ++k) {
                        double gk = grad[k];
                        for (int l = 0; l < parameters.length; ++l) {
                            wGradGradT.setEntry(k, l, weight * gk * grad[l]);
                        }
                    }

                    // update the matrices
                    a = a.add(wGradGradT);
                    b = b.add(bDecrement);

                }
            }

            try {

                // solve the linearized least squares problem
                RealVector dX = new LUDecompositionImpl(a).getSolver().solve(b);

                // update the estimated parameters
                for (int i = 0; i < parameters.length; ++i) {
                    parameters[i].setEstimate(parameters[i].getEstimate() + dX.getEntry(i));
                }

            } catch(InvalidMatrixException e) {
                throw new EstimationException("unable to solve: singular problem", null);
            }


            previous = cost;
            updateResidualsAndCost();

        } while ((getCostEvaluations() < 2) ||
                 (Math.abs(previous - cost) > (cost * steadyStateThreshold) &&
                  (Math.abs(cost) > convergence)));

    }

    /** Threshold for cost steady state detection. */
    private double steadyStateThreshold;

    /** Threshold for cost convergence. */
    private double convergence;

    /** Serializable version identifier */
     private static final long serialVersionUID = 5485001826076289109L;

}
