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
package org.apache.commons.math.analysis.minimization;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.ConvergingAlgorithm;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;


/**
 * Interface for (univariate real) minimization algorithms.
 *  
 * @version $Revision$ $Date$
 * @since 2.0
 */
public interface UnivariateRealMinimizer extends ConvergingAlgorithm {

    /**
     * Find a minimum in the given interval.
     * <p>
     * A minimizer may require that the interval brackets a single minimum.
     * </p>
     * @param f the function to minimize.
     * @param min the lower bound for the interval.
     * @param max the upper bound for the interval.
     * @return a value where the function is minimum
     * @throws ConvergenceException if the maximum iteration count is exceeded
     * or the minimizer detects convergence problems otherwise.
     * @throws FunctionEvaluationException if an error occurs evaluating the
     * function
     * @throws IllegalArgumentException if min > max or the endpoints do not
     * satisfy the requirements specified by the minimizer
     */
    double minimize(UnivariateRealFunction f, double min, double max)
        throws ConvergenceException, 
        FunctionEvaluationException;

    /**
     * Find a minimum in the given interval, start at startValue.
     * <p>
     * A minimizer may require that the interval brackets a single minimum.
     * </p>
     * @param f the function to minimize.
     * @param min the lower bound for the interval.
     * @param max the upper bound for the interval.
     * @param startValue the start value to use
     * @return a value where the function is minimum
     * @throws ConvergenceException if the maximum iteration count is exceeded
     * or the minimizer detects convergence problems otherwise.
     * @throws FunctionEvaluationException if an error occurs evaluating the
     * function
     * @throws IllegalArgumentException if min > max or the arguments do not
     * satisfy the requirements specified by the minimizer
     */
    double minimize(UnivariateRealFunction f, double min, double max, double startValue)
        throws ConvergenceException, FunctionEvaluationException;

    /**
     * Get the result of the last run of the minimizer.
     * 
     * @return the last result.
     * @throws IllegalStateException if there is no result available, either
     * because no result was yet computed or the last attempt failed.
     */
    double getResult();

    /**
     * Get the result of the last run of the minimizer.
     * 
     * @return the value of the function at the last result.
     * @throws IllegalStateException if there is no result available, either
     * because no result was yet computed or the last attempt failed.
     */
    double getFunctionValue();

}