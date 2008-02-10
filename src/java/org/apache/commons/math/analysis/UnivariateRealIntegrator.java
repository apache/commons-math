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
 * Interface for univariate real integration algorithms.
 *  
 * @version $Revision$ $Date$
 * @since 1.2
 */
public interface UnivariateRealIntegrator {

    /**
     * Set the upper limit for the number of iterations.
     * <p>
     * Usually a high iteration count indicates convergence problem. However,
     * the "reasonable value" varies widely for different cases.  Users are
     * advised to use the default value.</p>
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
     * The default value is supplied by the implementation.</p>
     * 
     * @see #setMaximalIterationCount(int)
     */
    void resetMaximalIterationCount();

    /**
     * Set the lower limit for the number of iterations.
     * <p>
     * Minimal iteration is needed to avoid false early convergence, e.g.
     * the sample points happen to be zeroes of the function. Users can
     * use the default value or choose one that they see as appropriate.</p>
     * <p>
     * A <code>ConvergenceException</code> will be thrown if this number
     * is not met.</p>
     *
     * @param count minimum number of iterations
     */
    void setMinimalIterationCount(int count);

    /**
     * Get the lower limit for the number of iterations.
     * 
     * @return the actual lower limit
     */
    int getMinimalIterationCount();

    /**
     * Reset the lower limit for the number of iterations to the default.
     * <p>
     * The default value is supplied by the implementation.</p>
     * 
     * @see #setMinimalIterationCount(int)
     */
    void resetMinimalIterationCount();

    /**
     * Set the relative accuracy.
     * <p>
     * This is used to stop iterations.</p>
     * 
     * @param accuracy the relative accuracy
     * @throws IllegalArgumentException if the accuracy can't be achieved
     * or is otherwise deemed unreasonable
     */
    void setRelativeAccuracy(double accuracy);

    /**
     * Get the actual relative accuracy.
     *
     * @return the accuracy
     */
    double getRelativeAccuracy();

    /**
     * Reset the relative accuracy to the default.
     * <p>
     * The default value is provided by the implementation.</p>
     *
     * @see #setRelativeAccuracy(double)
     */
    void resetRelativeAccuracy();

    /**
     * Integrate the function in the given interval.
     * 
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @return the value of integral
     * @throws ConvergenceException if the maximum iteration count is exceeded
     * or the integrator detects convergence problems otherwise
     * @throws FunctionEvaluationException if an error occurs evaluating the
     * function
     * @throws IllegalArgumentException if min > max or the endpoints do not
     * satisfy the requirements specified by the integrator
     */
    double integrate(double min, double max) throws ConvergenceException, 
        FunctionEvaluationException, IllegalArgumentException;

    /**
     * Get the result of the last run of the integrator.
     * 
     * @return the last result
     * @throws IllegalStateException if there is no result available, either
     * because no result was yet computed or the last attempt failed
     */
    double getResult() throws IllegalStateException;

    /**
     * Get the number of iterations in the last run of the integrator.
     * <p>
     * This is mainly meant for testing purposes. It may occasionally
     * help track down performance problems: if the iteration count
     * is notoriously high, check whether the function is evaluated
     * properly, and whether another integrator is more amenable to the
     * problem.</p>
     * 
     * @return the last iteration count
     * @throws IllegalStateException if there is no result available, either
     * because no result was yet computed or the last attempt failed
     */
    int getIterationCount() throws IllegalStateException;
}
