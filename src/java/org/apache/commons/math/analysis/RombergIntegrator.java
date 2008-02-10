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

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MaxIterationsExceededException;

/**
 * Implements the <a href="http://mathworld.wolfram.com/RombergIntegration.html">
 * Romberg Algorithm</a> for integration of real univariate functions. For
 * reference, see <b>Introduction to Numerical Analysis</b>, ISBN 038795452X,
 * chapter 3.
 * <p>
 * Romberg integration employs k successvie refinements of the trapezoid
 * rule to remove error terms less than order O(N^(-2k)). Simpson's rule
 * is a special case of k = 2.</p>
 *  
 * @version $Revision$ $Date$
 * @since 1.2
 */
public class RombergIntegrator extends UnivariateRealIntegratorImpl {

    /** serializable version identifier */
    private static final long serialVersionUID = -1058849527738180243L;

    /**
     * Construct an integrator for the given function.
     * 
     * @param f function to integrate
     */
    public RombergIntegrator(UnivariateRealFunction f) {
        super(f, 32);
    }

    /**
     * Integrate the function in the given interval.
     * 
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @return the value of integral
     * @throws MaxIterationsExceededException if the maximum iteration count is exceeded
     * or the integrator detects convergence problems otherwise
     * @throws FunctionEvaluationException if an error occurs evaluating the
     * function
     * @throws IllegalArgumentException if any parameters are invalid
     */
    public double integrate(double min, double max) throws MaxIterationsExceededException,
        FunctionEvaluationException, IllegalArgumentException {
        
        int i = 1, j, m = maximalIterationCount + 1;
        // Array strcture here can be improved for better space
        // efficiency because only the lower triangle is used.
        double r, t[][] = new double[m][m], s, olds;

        clearResult();
        verifyInterval(min, max);
        verifyIterationCount();

        TrapezoidIntegrator qtrap = new TrapezoidIntegrator(this.f);
        t[0][0] = qtrap.stage(min, max, 0);
        olds = t[0][0];
        while (i <= maximalIterationCount) {
            t[i][0] = qtrap.stage(min, max, i);
            for (j = 1; j <= i; j++) {
                // Richardson extrapolation coefficient
                r = (1L << (2 * j)) -1;
                t[i][j] = t[i][j-1] + (t[i][j-1] - t[i-1][j-1]) / r;
            }
            s = t[i][i];
            if (i >= minimalIterationCount) {
                if (Math.abs(s - olds) <= Math.abs(relativeAccuracy * olds)) {
                    setResult(s, i);
                    return result;
                }
            }
            olds = s;
            i++;
        }
        throw new MaxIterationsExceededException(maximalIterationCount);
    }

    /**
     * Verifies that the iteration limits are valid and within the range.
     * 
     * @throws IllegalArgumentException if not
     */
    protected void verifyIterationCount() throws IllegalArgumentException {
        super.verifyIterationCount();
        // at most 32 bisection refinements due to higher order divider
        if (maximalIterationCount > 32) {
            throw new IllegalArgumentException
                ("Iteration upper limit out of [0, 32] range: " +
                maximalIterationCount);
        }
    }
}
