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

import java.util.Arrays;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.ConvergenceChecker;
import org.apache.commons.math.optimization.univariate.AbstractUnivariateRealOptimizer;
import org.apache.commons.math.optimization.univariate.BracketFinder;
import org.apache.commons.math.optimization.univariate.BrentOptimizer;
import org.apache.commons.math.optimization.univariate.UnivariateRealPointValuePair;

/**
 * Powell algorithm.
 * This code is translated and adapted from the Python version of this
 * algorithm (as implemented in module {@code optimize.py} v0.5 of
 * <em>SciPy</em>).
 * <br/>
 * The user is responsible for calling {@link
 * #setConvergenceChecker(ConvergenceChecker) ConvergenceChecker}
 * prior to using the optimizer.
 *
 * @version $Revision$ $Date$
 * @since 2.2
 */
public class PowellOptimizer
    extends AbstractScalarOptimizer {
    /**
     * Line search.
     */
    private LineSearch line = new LineSearch();

    /**
     * Set the convergence checker.
     * It also indirectly sets the line search tolerances to the square-root
     * of the correponding tolerances in the checker.
     *
     * @param checker Convergence checker.
     */
    public void setConvergenceChecker(ConvergenceChecker<RealPointValuePair> checker) {
        super.setConvergenceChecker(checker);

        // Line search tolerances can be much lower than the tolerances
        // required for the optimizer itself.
        final double minTol = 1e-4;
        final double rel = Math.min(Math.sqrt(checker.getRelativeThreshold()), minTol);
        final double abs = Math.min(Math.sqrt(checker.getAbsoluteThreshold()), minTol);
        line.setConvergenceChecker(new BrentOptimizer.BrentConvergenceChecker(rel, abs));
    }

    /** {@inheritDoc} */
    @Override
    public void setMaxEvaluations(int maxEvaluations) {
        super.setMaxEvaluations(maxEvaluations);

        // We must allow at least as many iterations to the underlying line
        // search optimizer. Because the line search inner class will call 
        // "computeObjectiveValue" in this class, we ensure that this class
        // will be the first to eventually throw "TooManyEvaluationsException".
        line.setMaxEvaluations(maxEvaluations);
    }

    /** {@inheritDoc} */
    @Override
    protected RealPointValuePair doOptimize()
        throws FunctionEvaluationException {
        final GoalType goal = getGoalType();
        final double[] guess = getStartPoint();
        final int n = guess.length;

        final double[][] direc = new double[n][n];
        for (int i = 0; i < n; i++) {
            direc[i][i] = 1;
        }

        double[] x = guess;
        double fVal = computeObjectiveValue(x);
        double[] x1 = x.clone();
        int iter = 0;
        while (true) {
            ++iter;

            double fX = fVal;
            double fX2 = 0;
            double delta = 0;
            int bigInd = 0;
            double alphaMin = 0;

            for (int i = 0; i < n; i++) {
                final double[] d = Arrays.copyOf(direc[i], n);

                fX2 = fVal;

                final UnivariateRealPointValuePair optimum = line.search(x, d);
                fVal = optimum.getValue();
                alphaMin = optimum.getPoint();
                final double[][] result = newPointAndDirection(x, d, alphaMin);
                x = result[0];

                if ((fX2 - fVal) > delta) {
                    delta = fX2 - fVal;
                    bigInd = i;
                }
            }

            final RealPointValuePair previous = new RealPointValuePair(x1, fX);
            final RealPointValuePair current = new RealPointValuePair(x, fVal);
            if (getConvergenceChecker().converged(iter, previous, current)) {
                if (goal == GoalType.MINIMIZE) {
                    return (fVal < fX) ? current : previous;
                } else {
                    return (fVal > fX) ? current : previous;
                }
            }

            final double[] d = new double[n];
            final double[] x2 = new double[n];
            for (int i = 0; i < n; i++) {
                d[i] = x[i] - x1[i];
                x2[i] = 2 * x[i] - x1[i];
            }

            x1 = x.clone();
            fX2 = computeObjectiveValue(x2);

            if (fX > fX2) {
                double t = 2 * (fX + fX2 - 2 * fVal);
                double temp = fX - fVal - delta;
                t *= temp * temp;
                temp = fX - fX2;
                t -= delta * temp * temp;

                if (t < 0.0) {
                    final UnivariateRealPointValuePair optimum = line.search(x, d);
                    fVal = optimum.getValue();
                    alphaMin = optimum.getPoint();
                    final double[][] result = newPointAndDirection(x, d, alphaMin);
                    x = result[0];

                    final int lastInd = n - 1;
                    direc[bigInd] = direc[lastInd];
                    direc[lastInd] = result[1];
                }
            }
        }
    }

    /**
     * Compute a new point (in the original space) and a new direction
     * vector, resulting from the line search.
     * The parameters {@code p} and {@code d} will be changed in-place.
     *
     * @param p Point used in the line search.
     * @param d Direction used in the line search.
     * @param optimum Optimum found by the line search.
     * @return a 2-element array containing the new point (at index 0) and
     * the new direction (at index 1).
     */
    private double[][] newPointAndDirection(double[] p,
                                            double[] d,
                                            double optimum) {
        final int n = p.length;
        final double[][] result = new double[2][n];
        final double[] nP = result[0];
        final double[] nD = result[1];
        for (int i = 0; i < n; i++) {
            nD[i] = d[i] * optimum;
            nP[i] = p[i] + nD[i];
        }
        return result;
    }

    /**
     * Class for finding the minimum of the objective function along a given
     * direction.
     */
    private class LineSearch extends BrentOptimizer {
        /**
         * Automatic bracketing.
         */
        private final BracketFinder bracket = new BracketFinder();
        /**
         * Value of the optimum.
         */
        private UnivariateRealPointValuePair optimum;

        /**
         * Find the minimum of the function {@code f(p + alpha * d)}.
         *
         * @param p Starting point.
         * @param d Search direction.
         * @return the optimum.
         * @throws FunctionEvaluationException if the function evaluation
         * fails.
         * @throws TooManyEvaluationsException if the number of evaluations is
         * exceeded.
         */
        public UnivariateRealPointValuePair search(final double[] p,
                                                   final double[] d)
            throws FunctionEvaluationException {

            final int n = p.length;
            final UnivariateRealFunction f = new UnivariateRealFunction() {
                    public double value(double alpha)
                        throws FunctionEvaluationException {
                        
                        final double[] x = new double[n];
                        for (int i = 0; i < n; i++) {
                            x[i] = p[i] + alpha * d[i];
                        }
                        final double obj = PowellOptimizer.this.computeObjectiveValue(x);
                        return obj;
                    }
                };
            
            final GoalType goal = PowellOptimizer.this.getGoalType();
            bracket.search(f, goal, 0, 1);
            return optimize(f, goal, bracket.getLo(), bracket.getHi(),
                            bracket.getMid());
        }
    }
}
