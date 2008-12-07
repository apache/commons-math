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
package org.apache.commons.math.analysis;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;


/**
 * Interface for (univariate real) rootfinding algorithms.
 * <p>
 * Implementations will search for only one zero in the given interval.</p>
 *  
 * @version $Revision$ $Date$
 */
public interface UnivariateRealSolver {

    /**
     * Set the upper limit for the number of iterations.
     * <p>
     * Usually a high iteration count indicates convergence problems. However,
     * the "reasonable value" varies widely for different solvers.  Users are
     * advised to use the default value supplied by the solver.</p>
     * <p>
     * A <code>ConvergenceException</code> will be thrown if this number
     * is exceeded.</p>
     *  
     * @param count maximum number of iterations
     */
    void setMaximalIterationCount(int count);

    /**
     * Get the upper limit for the number of iterations.
     * 
     * @return the actual upper limit
     */
    int getMaximalIterationCount();

    /**
     * Reset the upper limit for the number of iterations to the default.
     * <p>
     * The default value is supplied by the solver implementation.</p>
     * 
     * @see #setMaximalIterationCount(int)
     */
    void resetMaximalIterationCount();

    /**
     * Set the absolute accuracy.
     * <p>
     * The default is usually choosen so that roots in the interval
     * -10..-0.1 and +0.1..+10 can be found with a reasonable accuracy. If the
     * expected absolute value of your roots is of much smaller magnitude, set
     * this to a smaller value.</p>
     * <p>
     * Solvers are advised to do a plausibility check with the relative
     * accuracy, but clients should not rely on this.</p>
     *  
     * @param accuracy the accuracy.
     * @throws IllegalArgumentException if the accuracy can't be achieved by
     * the solver or is otherwise deemed unreasonable. 
     */
    void setAbsoluteAccuracy(double accuracy);

    /**
     * Get the actual absolute accuracy.
     * 
     * @return the accuracy
     */
    double getAbsoluteAccuracy();

    /**
     * Reset the absolute accuracy to the default.
     * <p>
     * The default value is provided by the solver implementation.</p>
     */
    void resetAbsoluteAccuracy();

    /**
     * Set the relative accuracy.
     * <p>
     * This is used to stop iterations if the absolute accuracy can't be
     * achieved due to large values or short mantissa length.</p>
     * <p>
     * If this should be the primary criterion for convergence rather then a
     * safety measure, set the absolute accuracy to a ridiculously small value,
     * like 1E-1000.</p>
     * 
     * @param accuracy the relative accuracy.
     * @throws IllegalArgumentException if the accuracy can't be achieved by
     *  the solver or is otherwise deemed unreasonable. 
     */
    void setRelativeAccuracy(double accuracy);

    /**
     * Get the actual relative accuracy.
     * @return the accuracy
     */
    double getRelativeAccuracy();

    /**
     * Reset the relative accuracy to the default.
     * The default value is provided by the solver implementation.
     */
    void resetRelativeAccuracy();

    /**
     * Set the function value accuracy.
     * <p>
     * This is used to determine when an evaluated function value or some other
     * value which is used as divisor is zero.</p>
     * <p>
     * This is a safety guard and it shouldn't be necessary to change this in
     * general.</p>
     * 
     * @param accuracy the accuracy.
     * @throws IllegalArgumentException if the accuracy can't be achieved by
     * the solver or is otherwise deemed unreasonable. 
     */
    void setFunctionValueAccuracy(double accuracy);

    /**
     * Get the actual function value accuracy.
     * @return the accuracy
     */
    double getFunctionValueAccuracy();

    /**
     * Reset the actual function accuracy to the default.
     * The default value is provided by the solver implementation.
     */
    void resetFunctionValueAccuracy();

    /**
     * Solve for a zero root in the given interval.
     * A solver may require that the interval brackets a single zero root.
     * 
     * @param min the lower bound for the interval.
     * @param max the upper bound for the interval.
     * @return a value where the function is zero
     * @throws ConvergenceException if the maximum iteration count is exceeded
     * or the solver detects convergence problems otherwise.
     * @throws FunctionEvaluationException if an error occurs evaluating the
     * function
     * @throws IllegalArgumentException if min > max or the endpoints do not
     * satisfy the requirements specified by the solver
     * @deprecated replaced by {@link #solve(UnivariateRealFunction, double, double)
     * since 2.0
     */
    @Deprecated
    double solve(double min, double max) throws ConvergenceException, 
        FunctionEvaluationException;

    /**
     * Solve for a zero root in the given interval.
     * A solver may require that the interval brackets a single zero root.
     * 
     * @param f the function to solve.
     * @param min the lower bound for the interval.
     * @param max the upper bound for the interval.
     * @return a value where the function is zero
     * @throws ConvergenceException if the maximum iteration count is exceeded
     * or the solver detects convergence problems otherwise.
     * @throws FunctionEvaluationException if an error occurs evaluating the
     * function
     * @throws IllegalArgumentException if min > max or the endpoints do not
     * satisfy the requirements specified by the solver
     * @since 2.0
     */
    double solve(UnivariateRealFunction f, double min, double max)
        throws ConvergenceException, 
        FunctionEvaluationException;

    /**
     * Solve for a zero in the given interval, start at startValue.
     * A solver may require that the interval brackets a single zero root.
     * 
     * @param min the lower bound for the interval.
     * @param max the upper bound for the interval.
     * @param startValue the start value to use
     * @return a value where the function is zero
     * @throws ConvergenceException if the maximum iteration count is exceeded
     * or the solver detects convergence problems otherwise.
     * @throws FunctionEvaluationException if an error occurs evaluating the
     * function
     * @throws IllegalArgumentException if min > max or the arguments do not
     * satisfy the requirements specified by the solver
     * @deprecated replaced by {@link #solve(UnivariateRealFunction, double, double, double)
     * since 2.0
     */
    @Deprecated
    double solve(double min, double max, double startValue)
        throws ConvergenceException, FunctionEvaluationException;

    /**
     * Solve for a zero in the given interval, start at startValue.
     * A solver may require that the interval brackets a single zero root.
     * 
     * @param f the function to solve.
     * @param min the lower bound for the interval.
     * @param max the upper bound for the interval.
     * @param startValue the start value to use
     * @return a value where the function is zero
     * @throws ConvergenceException if the maximum iteration count is exceeded
     * or the solver detects convergence problems otherwise.
     * @throws FunctionEvaluationException if an error occurs evaluating the
     * function
     * @throws IllegalArgumentException if min > max or the arguments do not
     * satisfy the requirements specified by the solver
     * @since 2.0
     */
    double solve(UnivariateRealFunction f, double min, double max, double startValue)
        throws ConvergenceException, FunctionEvaluationException;

    /**
     * Get the result of the last run of the solver.
     * 
     * @return the last result.
     * @throws IllegalStateException if there is no result available, either
     * because no result was yet computed or the last attempt failed.
     */
    double getResult();

    /**
     * Get the number of iterations in the last run of the solver.
     * <p>
     * This is mainly meant for testing purposes. It may occasionally
     * help track down performance problems: if the iteration count
     * is notoriously high, check whether the function is evaluated
     * properly, and whether another solver is more amenable to the
     * problem.</p>
     * 
     * @return the last iteration count.
     * @throws IllegalStateException if there is no result available, either
     * because no result was yet computed or the last attempt failed.
     */
    int getIterationCount();
}