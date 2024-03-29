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

package org.apache.commons.math4.legacy.optim.nonlinear.scalar.gradient;

import org.apache.commons.math4.legacy.analysis.MultivariateFunction;
import org.apache.commons.math4.legacy.exception.MathInternalError;
import org.apache.commons.math4.legacy.exception.MathUnsupportedOperationException;
import org.apache.commons.math4.legacy.exception.TooManyEvaluationsException;
import org.apache.commons.math4.legacy.exception.util.LocalizedFormats;
import org.apache.commons.math4.legacy.optim.ConvergenceChecker;
import org.apache.commons.math4.legacy.optim.OptimizationData;
import org.apache.commons.math4.legacy.optim.PointValuePair;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.GradientMultivariateOptimizer;

/**
 * Non-linear conjugate gradient optimizer.
 * <br>
 * This class supports both the Fletcher-Reeves and the Polak-Ribière
 * update formulas for the conjugate search directions.
 * It also supports optional preconditioning.
 * <br>
 * Line search must be setup via {@link org.apache.commons.math4.legacy.optim.nonlinear.scalar.LineSearchTolerance}.
 * <br>
 * Constraints are not supported: the call to
 * {@link #optimize(OptimizationData[]) optimize} will throw
 * {@link MathUnsupportedOperationException} if bounds are passed to it.
 *
 * @since 2.0
 */
public class NonLinearConjugateGradientOptimizer
    extends GradientMultivariateOptimizer {
    /** Update formula for the beta parameter. */
    private final Formula updateFormula;
    /** Preconditioner (may be null). */
    private final Preconditioner preconditioner;

    /**
     * Available choices of update formulas for the updating the parameter
     * that is used to compute the successive conjugate search directions.
     * For non-linear conjugate gradients, there are
     * two formulas:
     * <ul>
     *   <li>Fletcher-Reeves formula</li>
     *   <li>Polak-Ribière formula</li>
     * </ul>
     *
     * On the one hand, the Fletcher-Reeves formula is guaranteed to converge
     * if the start point is close enough of the optimum whether the
     * Polak-Ribière formula may not converge in rare cases. On the
     * other hand, the Polak-Ribière formula is often faster when it
     * does converge. Polak-Ribière is often used.
     *
     * @since 2.0
     */
    public enum Formula {
        /** Fletcher-Reeves formula. */
        FLETCHER_REEVES,
        /** Polak-Ribière formula. */
        POLAK_RIBIERE
    }

    /**
     * Constructor with default {@link IdentityPreconditioner preconditioner}.
     *
     * @param updateFormula formula to use for updating the &beta; parameter,
     * must be one of {@link Formula#FLETCHER_REEVES} or
     * {@link Formula#POLAK_RIBIERE}.
     * @param checker Convergence checker.
     *
     * @since 3.3
     */
    public NonLinearConjugateGradientOptimizer(final Formula updateFormula,
                                               ConvergenceChecker<PointValuePair> checker) {
        this(updateFormula,
             checker,
             new IdentityPreconditioner());
    }

    /**
     * @param updateFormula formula to use for updating the &beta; parameter,
     * must be one of {@link Formula#FLETCHER_REEVES} or
     * {@link Formula#POLAK_RIBIERE}.
     * @param checker Convergence checker.
     * @param preconditioner Preconditioner.
     *
     * @since 3.3
     */
    public NonLinearConjugateGradientOptimizer(final Formula updateFormula,
                                               ConvergenceChecker<PointValuePair> checker,
                                               final Preconditioner preconditioner) {
        super(checker);

        this.updateFormula = updateFormula;
        this.preconditioner = preconditioner;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PointValuePair optimize(OptimizationData... optData)
        throws TooManyEvaluationsException {
        // Set up base class and perform computation.
        return super.optimize(optData);
    }

    /** {@inheritDoc} */
    @Override
    protected PointValuePair doOptimize() {
        final ConvergenceChecker<PointValuePair> checker = getConvergenceChecker();
        final double[] point = getStartPoint();
        final GoalType goal = getGoalType();
        final MultivariateFunction func = getObjectiveFunction();
        final int n = point.length;
        double[] r = computeObjectiveGradient(point);
        if (goal == GoalType.MINIMIZE) {
            for (int i = 0; i < n; i++) {
                r[i] = -r[i];
            }
        }

        // Initial search direction.
        double[] steepestDescent = preconditioner.precondition(point, r);
        double[] searchDirection = steepestDescent.clone();

        double delta = 0;
        for (int i = 0; i < n; ++i) {
            delta += r[i] * searchDirection[i];
        }

        createLineSearch();

        PointValuePair current = null;
        while (true) {
            incrementIterationCount();

            final double objective = func.value(point);
            PointValuePair previous = current;
            current = new PointValuePair(point, objective);
            if (previous != null &&
                checker.converged(getIterations(), previous, current)) {
                // We have found an optimum.
                return current;
            }

            final double step = lineSearch(point, searchDirection).getPoint();

            // Validate new point.
            for (int i = 0; i < point.length; ++i) {
                point[i] += step * searchDirection[i];
            }

            r = computeObjectiveGradient(point);
            if (goal == GoalType.MINIMIZE) {
                for (int i = 0; i < n; ++i) {
                    r[i] = -r[i];
                }
            }

            // Compute beta.
            final double deltaOld = delta;
            final double[] newSteepestDescent = preconditioner.precondition(point, r);
            delta = 0;
            for (int i = 0; i < n; ++i) {
                delta += r[i] * newSteepestDescent[i];
            }

            final double beta;
            switch (updateFormula) {
            case FLETCHER_REEVES:
                beta = delta / deltaOld;
                break;
            case POLAK_RIBIERE:
                double deltaMid = 0;
                for (int i = 0; i < r.length; ++i) {
                    deltaMid += r[i] * steepestDescent[i];
                }
                beta = (delta - deltaMid) / deltaOld;
                break;
            default:
                // Should never happen.
                throw new MathInternalError();
            }
            steepestDescent = newSteepestDescent;

            // Compute conjugate search direction.
            if (getIterations() % n == 0 ||
                beta < 0) {
                // Break conjugation: reset search direction.
                searchDirection = steepestDescent.clone();
            } else {
                // Compute new conjugate search direction.
                for (int i = 0; i < n; ++i) {
                    searchDirection[i] = steepestDescent[i] + beta * searchDirection[i];
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void parseOptimizationData(OptimizationData... optData) {
        // Allow base class to register its own data.
        super.parseOptimizationData(optData);

        checkParameters();
    }

    /** Default identity preconditioner. */
    public static class IdentityPreconditioner implements Preconditioner {
        /** {@inheritDoc} */
        @Override
        public double[] precondition(double[] variables, double[] r) {
            return r.clone();
        }
    }

    // Class is not used anymore (cf. MATH-1092). However, it might
    // be interesting to create a class similar to "LineSearch", but
    // that will take advantage that the model's gradient is available.
//     /**
//      * Internal class for line search.
//      * <p>
//      * The function represented by this class is the dot product of
//      * the objective function gradient and the search direction. Its
//      * value is zero when the gradient is orthogonal to the search
//      * direction, i.e. when the objective function value is a local
//      * extremum along the search direction.
//      * </p>
//      */
//     private class LineSearchFunction implements UnivariateFunction {
//         /** Current point. */
//         private final double[] currentPoint;
//         /** Search direction. */
//         private final double[] searchDirection;

//         /**
//          * @param point Current point.
//          * @param direction Search direction.
//          */
//         public LineSearchFunction(double[] point,
//                                   double[] direction) {
//             currentPoint = point.clone();
//             searchDirection = direction.clone();
//         }

//         /** {@inheritDoc} */
//         public double value(double x) {
//             // current point in the search direction
//             final double[] shiftedPoint = currentPoint.clone();
//             for (int i = 0; i < shiftedPoint.length; ++i) {
//                 shiftedPoint[i] += x * searchDirection[i];
//             }

//             // gradient of the objective function
//             final double[] gradient = computeObjectiveGradient(shiftedPoint);

//             // dot product with the search direction
//             double dotProduct = 0;
//             for (int i = 0; i < gradient.length; ++i) {
//                 dotProduct += gradient[i] * searchDirection[i];
//             }

//             return dotProduct;
//         }
//     }

    /**
     * @throws MathUnsupportedOperationException if bounds were passed to the
     * {@link #optimize(OptimizationData[]) optimize} method.
     */
    private void checkParameters() {
        if (getLowerBound() != null ||
            getUpperBound() != null) {
            throw new MathUnsupportedOperationException(LocalizedFormats.CONSTRAINT);
        }
    }
}
