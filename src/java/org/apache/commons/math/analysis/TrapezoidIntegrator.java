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
 * Implements the <a href="http://mathworld.wolfram.com/TrapezoidalRule.html">
 * Trapezoidal Rule</a> for integration of real univariate functions. For
 * reference, see <b>Introduction to Numerical Analysis</b>, ISBN 038795452X,
 * chapter 3.
 * <p>
 * The function should be integrable.</p>
 *  
 * @version $Revision$ $Date$
 * @since 1.2
 */
public class TrapezoidIntegrator extends UnivariateRealIntegratorImpl {

    /** serializable version identifier */
    private static final long serialVersionUID = 4978222553983172543L;

    /** intermediate result */
    private double s;

    /**
     * Construct an integrator for the given function.
     * 
     * @param f function to integrate
     */
    public TrapezoidIntegrator(UnivariateRealFunction f) {
        super(f, 64);
    }

    /**
     * Compute the n-th stage integral of trapezoid rule. This function
     * should only be called by API <code>integrate()</code> in the package.
     * To save time it does not verify arguments - caller does.
     * <p>
     * The interval is divided equally into 2^n sections rather than an
     * arbitrary m sections because this configuration can best utilize the
     * alrealy computed values.</p>
     *
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @param n the stage of 1/2 refinement, n = 0 is no refinement
     * @return the value of n-th stage integral
     * @throws FunctionEvaluationException if an error occurs evaluating the
     * function
     */
    double stage(double min, double max, int n) throws
        FunctionEvaluationException {
        
        long i, np;
        double x, spacing, sum = 0;
        
        if (n == 0) {
            s = 0.5 * (max - min) * (f.value(min) + f.value(max));
            return s;
        } else {
            np = 1L << (n-1);           // number of new points in this stage
            spacing = (max - min) / np; // spacing between adjacent new points
            x = min + 0.5 * spacing;    // the first new point
            for (i = 0; i < np; i++) {
                sum += f.value(x);
                x += spacing;
            }
            // add the new sum to previously calculated result
            s = 0.5 * (s + sum * spacing);
            return s;
        }
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
        double t, oldt;
        
        clearResult();
        verifyInterval(min, max);
        verifyIterationCount();

        oldt = stage(min, max, 0);
        while (i <= maximalIterationCount) {
            t = stage(min, max, i);
            if (i >= minimalIterationCount) {
                if (Math.abs(t - oldt) <= Math.abs(relativeAccuracy * oldt)) {
                    setResult(t, i);
                    return result;
                }
            }
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
