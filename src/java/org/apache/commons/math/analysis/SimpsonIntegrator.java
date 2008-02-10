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
 * Implements the <a href="http://mathworld.wolfram.com/SimpsonsRule.html">
 * Simpson's Rule</a> for integration of real univariate functions. For
 * reference, see <b>Introduction to Numerical Analysis</b>, ISBN 038795452X,
 * chapter 3.
 * <p>
 * This implementation employs basic trapezoid rule as building blocks to
 * calculate the Simpson's rule of alternating 2/3 and 4/3.</p>
 *  
 * @version $Revision$ $Date$
 * @since 1.2
 */
public class SimpsonIntegrator extends UnivariateRealIntegratorImpl {

    /** serializable version identifier */
    private static final long serialVersionUID = 3405465123320678216L;

    /**
     * Construct an integrator for the given function.
     * 
     * @param f function to integrate
     */
    public SimpsonIntegrator(UnivariateRealFunction f) {
        super(f, 64);
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
        
        int i = 1;
        double s, olds, t, oldt;
        
        clearResult();
        verifyInterval(min, max);
        verifyIterationCount();

        TrapezoidIntegrator qtrap = new TrapezoidIntegrator(this.f);
        if (minimalIterationCount == 1) {
            s = (4 * qtrap.stage(min, max, 1) - qtrap.stage(min, max, 0)) / 3.0;
            setResult(s, 1);
            return result;
        }
        // Simpson's rule requires at least two trapezoid stages.
        olds = 0;
        oldt = qtrap.stage(min, max, 0);
        while (i <= maximalIterationCount) {
            t = qtrap.stage(min, max, i);
            s = (4 * t - oldt) / 3.0;
            if (i >= minimalIterationCount) {
                if (Math.abs(s - olds) <= Math.abs(relativeAccuracy * olds)) {
                    setResult(s, i);
                    return result;
                }
            }
            olds = s;
            oldt = t;
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
        // at most 64 bisection refinements
        if (maximalIterationCount > 64) {
            throw new IllegalArgumentException
                ("Iteration upper limit out of [0, 64] range: " +
                maximalIterationCount);
        }
    }
}
