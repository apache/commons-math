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
package org.apache.commons.math4.legacy.analysis.solvers;


/**
 * This class implements the <a href="http://mathworld.wolfram.com/BrentsMethod.html">
 * Brent algorithm</a> for finding zeros of real univariate functions.
 * The function should be continuous but not necessarily smooth.
 * The {@code solve} method returns a zero {@code x} of the function {@code f}
 * in the given interval {@code [a, b]} to within a tolerance
 * {@code 2 eps abs(x) + t} where {@code eps} is the relative accuracy and
 * {@code t} is the absolute accuracy.
 * <p>The given interval must bracket the root.</p>
 * <p>
 *  The reference implementation is given in chapter 4 of
 *  <blockquote>
 *   <b>Algorithms for Minimization Without Derivatives</b>,
 *   <em>Richard P. Brent</em>,
 *   Dover, 2002
 *  </blockquote>
 *
 * @see BaseAbstractUnivariateSolver
 */
public class BrentSolver extends AbstractUnivariateSolver {

    /** Default absolute accuracy. */
    private static final double DEFAULT_ABSOLUTE_ACCURACY = 1e-6;

    /**
     * Construct a solver with default absolute accuracy (1e-6).
     */
    public BrentSolver() {
        this(DEFAULT_ABSOLUTE_ACCURACY);
    }
    /**
     * Construct a solver.
     *
     * @param absoluteAccuracy Absolute accuracy.
     */
    public BrentSolver(double absoluteAccuracy) {
        super(absoluteAccuracy);
    }
    /**
     * Construct a solver.
     *
     * @param relativeAccuracy Relative accuracy.
     * @param absoluteAccuracy Absolute accuracy.
     */
    public BrentSolver(double relativeAccuracy,
                       double absoluteAccuracy) {
        super(relativeAccuracy, absoluteAccuracy);
    }
    /**
     * Construct a solver.
     *
     * @param relativeAccuracy Relative accuracy.
     * @param absoluteAccuracy Absolute accuracy.
     * @param functionValueAccuracy Function value accuracy.
     *
     * @see BaseAbstractUnivariateSolver#BaseAbstractUnivariateSolver(double,double,double)
     */
    public BrentSolver(double relativeAccuracy,
                       double absoluteAccuracy,
                       double functionValueAccuracy) {
        super(relativeAccuracy, absoluteAccuracy, functionValueAccuracy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double doSolve() {
        final double min = getMin();
        final double max = getMax();
        final double initial = getStartValue();

        final org.apache.commons.numbers.rootfinder.BrentSolver rf =
            new org.apache.commons.numbers.rootfinder.BrentSolver(getRelativeAccuracy(),
                                                                  getAbsoluteAccuracy(),
                                                                  getFunctionValueAccuracy());

        double root = Double.NaN;
        try {
            root = rf.findRoot(arg -> computeObjectiveValue(arg),
                               min, initial, max);
        } catch (IllegalArgumentException e) {
            // Redundant calls in order to throw the expected exceptions.
            verifySequence(min, initial, max);
            verifyBracketing(min, max);
        }

        return root;
    }
}
