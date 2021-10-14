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
package org.apache.commons.math4.legacy.optim.univariate;

import org.apache.commons.math4.legacy.analysis.UnivariateFunction;
import org.apache.commons.math4.legacy.exception.MaxCountExceededException;
import org.apache.commons.math4.legacy.exception.NotStrictlyPositiveException;
import org.apache.commons.math4.legacy.exception.TooManyEvaluationsException;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.apache.commons.math4.legacy.core.IntegerSequence.Incrementor;

/**
 * Provide an interval that brackets a local optimum of a function.
 * This code is based on a Python implementation (from <em>SciPy</em>,
 * module {@code optimize.py} v0.5).
 *
 * @since 2.2
 */
public class BracketFinder {
    /** Tolerance to avoid division by zero. */
    private static final double EPS_MIN = 1e-21;
    /**
     * Golden section.
     */
    private static final double GOLD = 1.618034;
    /**
     * Factor for expanding the interval.
     */
    private final double growLimit;
    /**
     * Number of allowed function evaluations.
     */
    private final int maxEvaluations;
    /**
     * Number of function evaluations performed in the last search.
     */
    private int evaluations;
    /**
     * Lower bound of the bracket.
     */
    private double lo;
    /**
     * Higher bound of the bracket.
     */
    private double hi;
    /**
     * Point inside the bracket.
     */
    private double mid;
    /**
     * Function value at {@link #lo}.
     */
    private double fLo;
    /**
     * Function value at {@link #hi}.
     */
    private double fHi;
    /**
     * Function value at {@link #mid}.
     */
    private double fMid;

    /**
     * Constructor with default values {@code 100, 500} (see the
     * {@link #BracketFinder(double,int) other constructor}).
     */
    public BracketFinder() {
        this(100, 500);
    }

    /**
     * Create a bracketing interval finder.
     *
     * @param growLimit Expanding factor.
     * @param maxEvaluations Maximum number of evaluations allowed for finding
     * a bracketing interval.
     */
    public BracketFinder(double growLimit,
                         int maxEvaluations) {
        if (growLimit <= 0) {
            throw new NotStrictlyPositiveException(growLimit);
        }
        if (maxEvaluations <= 0) {
            throw new NotStrictlyPositiveException(maxEvaluations);
        }

        this.growLimit = growLimit;
        this.maxEvaluations = maxEvaluations;
    }

    /**
     * Search new points that bracket a local optimum of the function.
     *
     * @param func Function whose optimum should be bracketed.
     * @param goal {@link GoalType Goal type}.
     * @param xA Initial point.
     * @param xB Initial point.
     * @throws TooManyEvaluationsException if the maximum number of evaluations
     * is exceeded.
     */
    public void search(UnivariateFunction func,
                       GoalType goal,
                       double xA,
                       double xB) {
        final FunctionEvaluator eval = new FunctionEvaluator(func);
        final boolean isMinim = goal == GoalType.MINIMIZE;

        double fA = eval.value(xA);
        double fB = eval.value(xB);
        if (isMinim ?
            fA < fB :
            fA > fB) {

            double tmp = xA;
            xA = xB;
            xB = tmp;

            tmp = fA;
            fA = fB;
            fB = tmp;
        }

        double xC = xB + GOLD * (xB - xA);
        double fC = eval.value(xC);

        while (isMinim ? fC < fB : fC > fB) {
            double tmp1 = (xB - xA) * (fB - fC);
            double tmp2 = (xB - xC) * (fB - fA);

            double val = tmp2 - tmp1;
            double denom = JdkMath.abs(val) < EPS_MIN ? 2 * EPS_MIN : 2 * val;

            double w = xB - ((xB - xC) * tmp2 - (xB - xA) * tmp1) / denom;
            double wLim = xB + growLimit * (xC - xB);

            double fW;
            if ((w - xC) * (xB - w) > 0) {
                fW = eval.value(w);
                if (isMinim ?
                    fW < fC :
                    fW > fC) {
                    xA = xB;
                    xB = w;
                    fA = fB;
                    fB = fW;
                    break;
                } else if (isMinim ?
                           fW > fB :
                           fW < fB) {
                    xC = w;
                    fC = fW;
                    break;
                }
                w = xC + GOLD * (xC - xB);
                fW = eval.value(w);
            } else if ((w - wLim) * (wLim - xC) >= 0) {
                w = wLim;
                fW = eval.value(w);
            } else if ((w - wLim) * (xC - w) > 0) {
                fW = eval.value(w);
                if (isMinim ?
                    fW < fC :
                    fW > fC) {
                    xB = xC;
                    xC = w;
                    w = xC + GOLD * (xC - xB);
                    fB = fC;
                    fC =fW;
                    fW = eval.value(w);
                }
            } else {
                w = xC + GOLD * (xC - xB);
                fW = eval.value(w);
            }

            xA = xB;
            fA = fB;
            xB = xC;
            fB = fC;
            xC = w;
            fC = fW;
        }

        lo = xA;
        fLo = fA;
        mid = xB;
        fMid = fB;
        hi = xC;
        fHi = fC;

        if (lo > hi) {
            double tmp = lo;
            lo = hi;
            hi = tmp;

            tmp = fLo;
            fLo = fHi;
            fHi = tmp;
        }
    }

    /**
     * @return the number of evaluations.
     */
    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    /**
     * @return the number of evaluations.
     */
    public int getEvaluations() {
        return evaluations;
    }

    /**
     * @return the lower bound of the bracket.
     * @see #getFLo()
     */
    public double getLo() {
        return lo;
    }

    /**
     * Get function value at {@link #getLo()}.
     * @return function value at {@link #getLo()}
     */
    public double getFLo() {
        return fLo;
    }

    /**
     * @return the higher bound of the bracket.
     * @see #getFHi()
     */
    public double getHi() {
        return hi;
    }

    /**
     * Get function value at {@link #getHi()}.
     * @return function value at {@link #getHi()}
     */
    public double getFHi() {
        return fHi;
    }

    /**
     * @return a point in the middle of the bracket.
     * @see #getFMid()
     */
    public double getMid() {
        return mid;
    }

    /**
     * Get function value at {@link #getMid()}.
     * @return function value at {@link #getMid()}
     */
    public double getFMid() {
        return fMid;
    }

    /**
     * Utility for incrementing a counter at each function evaluation.
     */
    private class FunctionEvaluator {
        /** Function. */
        private final UnivariateFunction func;
        /** Counter. */
        private final Incrementor inc;

        /**
         * @param func Function.
         */
        FunctionEvaluator(UnivariateFunction func) {
            this.func = func;
            inc = Incrementor.create().withMaximalCount(maxEvaluations);
            evaluations = 0;
        }

        /**
         * @param x Argument.
         * @return {@code f(x)}
         * @throws TooManyEvaluationsException if the maximal number of evaluations is
         * exceeded.
         */
        double value(double x) {
            try {
                inc.increment();
                evaluations = inc.getCount();
            } catch (MaxCountExceededException e) {
                throw new TooManyEvaluationsException(e.getMax());
            }

            return func.value(x);
        }
    }
}
