/*
 * Copyright 2003-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import org.apache.commons.math.MathException;

/**
 * Utility routines for {@link UnivariateRealSolver} objects.
 * @version $Revision: 1.7 $ $Date: 2004/02/21 21:35:14 $
 */
public class UnivariateRealSolverUtils {
    /**
     * Default constructor.
     */
    private UnivariateRealSolverUtils() {
        super();
    }

    /**
     * Method to solve for zeros of real univariate functions.  A
     * default solver is created and used for solving. 
     * @param f the function.
     * @param x0 the lower bound for the interval.
     * @param x1 the upper bound for the interval.
     * @return a value where the function is zero.
     * @throws MathException if the iteration count was exceeded or the
     *         solver detects convergence problems otherwise.
     */
    public static double solve(UnivariateRealFunction f, double x0, double x1)
        throws MathException
    {
        if(f == null){
            throw new IllegalArgumentException("f can not be null.");    
        }
        
        return UnivariateRealSolverFactory.newInstance().newDefaultSolver(f)
            .solve(x0, x1);
    }

    /**
     * Convience method to solve for zeros of real univariate functions.  A
     * default solver is created and used for solving. 
     * @param f the function.
     * @param x0 the lower bound for the interval.
     * @param x1 the upper bound for the interval.
     * @param absoluteAccuracy the accuracy to be used by the solver.
     * @return a value where the function is zero.
     * @throws MathException if the iteration count was exceeded or the
     *         solver detects convergence problems otherwise.
     */
    public static double solve(
        UnivariateRealFunction f,
        double x0,
        double x1,
        double absoluteAccuracy)
        throws MathException
    {
        if(f == null){
            throw new IllegalArgumentException("f can not be null.");    
        }
            
        UnivariateRealSolver solver = UnivariateRealSolverFactory.newInstance()
            .newDefaultSolver(f);
        solver.setAbsoluteAccuracy(absoluteAccuracy);
        return solver.solve(x0, x1);
    }

    /**
     * For a function, f, this method returns two values, a and b that bracket
     * a root of f.  That is to say, there exists a value c between a and b
     * such that f(c) = 0.
     *
     * @param function the function
     * @param initial midpoint of the returned range.
     * @param lowerBound for numerical safety, a never is less than this value.
     * @param upperBound for numerical safety, b never is greater than this
     *                   value.
     * @return a two element array holding {a, b}.
     * @throws MathException if a root can not be bracketted.
     */
    public static double[] bracket(UnivariateRealFunction function,
                                   double initial,
                                   double lowerBound,
                                   double upperBound) throws MathException {
        return bracket( function, initial, lowerBound, upperBound,
            Integer.MAX_VALUE ) ;
    }

    /**
     * For a function, f, this method returns two values, a and b that bracket
     * a root of f.  That is to say, there exists a value c between a and b
     * such that f(c) = 0.
     *
     * @param function the function
     * @param initial midpoint of the returned range.
     * @param lowerBound for numerical safety, a never is less than this value.
     * @param upperBound for numerical safety, b never is greater than this
     *                   value.
     * @param maximumIterations to guard against infinite looping, maximum
     *                          number of iterations to perform
     * @return a two element array holding {a, b}.
     * @throws MathException if a root can not be bracketted.
     */
    public static double[] bracket(UnivariateRealFunction function,
                                   double initial,
                                   double lowerBound,
                                   double upperBound,
                                   int maximumIterations) throws MathException {
        double a = initial;
        double b = initial;
        double fa;
        double fb;
        int numIterations = 0 ;
    
        do {
            a = Math.max(a - 1.0, lowerBound);
            b = Math.min(b + 1.0, upperBound);
            fa = function.value(a);
            fb = function.value(b);
            numIterations += 1 ;
        } while ( (fa * fb > 0.0) && ( numIterations < maximumIterations ) );
    
        return new double[]{a, b};
    }
}
