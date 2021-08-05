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
package org.apache.commons.math4.legacy.fitting;

import java.util.Collections;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.math4.legacy.exception.ZeroException;
import org.apache.commons.math4.legacy.exception.OutOfRangeException;
import org.apache.commons.math4.legacy.analysis.ParametricUnivariateFunction;
import org.apache.commons.math4.legacy.fitting.leastsquares.LeastSquaresBuilder;
import org.apache.commons.math4.legacy.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math4.legacy.linear.DiagonalMatrix;

/**
 * Fits points to a user-defined {@link ParametricUnivariateFunction function}.
 *
 * @since 3.4
 */
public class SimpleCurveFitter extends AbstractCurveFitter {
    /** Function to fit. */
    private final ParametricUnivariateFunction function;
    /** Initial guess for the parameters. */
    private final double[] initialGuess;
    /** Parameter guesser. */
    private final ParameterGuesser guesser;
    /** Maximum number of iterations of the optimization algorithm. */
    private final int maxIter;

    /**
     * Constructor used by the factory methods.
     *
     * @param function Function to fit.
     * @param initialGuess Initial guess. Cannot be {@code null}. Its length must
     * be consistent with the number of parameters of the {@code function} to fit.
     * @param guesser Method for providing an initial guess (if {@code initialGuess}
     * is {@code null}).
     * @param maxIter Maximum number of iterations of the optimization algorithm.
     */
    protected SimpleCurveFitter(ParametricUnivariateFunction function,
                                double[] initialGuess,
                                ParameterGuesser guesser,
                                int maxIter) {
        this.function = function;
        this.initialGuess = initialGuess;
        this.guesser = guesser;
        this.maxIter = maxIter;
    }

    /**
     * Creates a curve fitter.
     * The maximum number of iterations of the optimization algorithm is set
     * to {@link Integer#MAX_VALUE}.
     *
     * @param f Function to fit.
     * @param start Initial guess for the parameters.  Cannot be {@code null}.
     * Its length must be consistent with the number of parameters of the
     * function to fit.
     * @return a curve fitter.
     *
     * @see #withStartPoint(double[])
     * @see #withMaxIterations(int)
     */
    public static SimpleCurveFitter create(ParametricUnivariateFunction f,
                                           double[] start) {
        return new SimpleCurveFitter(f, start, null, Integer.MAX_VALUE);
    }

    /**
     * Creates a curve fitter.
     * The maximum number of iterations of the optimization algorithm is set
     * to {@link Integer#MAX_VALUE}.
     *
     * @param f Function to fit.
     * @param guesser Method for providing an initial guess.
     * @return a curve fitter.
     *
     * @see #withStartPoint(double[])
     * @see #withMaxIterations(int)
     */
    public static SimpleCurveFitter create(ParametricUnivariateFunction f,
                                           ParameterGuesser guesser) {
        return new SimpleCurveFitter(f, null, guesser, Integer.MAX_VALUE);
    }

    /**
     * Configure the start point (initial guess).
     * @param newStart new start point (initial guess)
     * @return a new instance.
     */
    public SimpleCurveFitter withStartPoint(double[] newStart) {
        return new SimpleCurveFitter(function,
                                     newStart.clone(),
                                     null,
                                     maxIter);
    }

    /**
     * Configure the maximum number of iterations.
     * @param newMaxIter maximum number of iterations
     * @return a new instance.
     */
    public SimpleCurveFitter withMaxIterations(int newMaxIter) {
        return new SimpleCurveFitter(function,
                                     initialGuess,
                                     guesser,
                                     newMaxIter);
    }

    /** {@inheritDoc} */
    @Override
    protected LeastSquaresProblem getProblem(Collection<WeightedObservedPoint> observations) {
        // Prepare least-squares problem.
        final int len = observations.size();
        final double[] target  = new double[len];
        final double[] weights = new double[len];

        int count = 0;
        for (WeightedObservedPoint obs : observations) {
            target[count]  = obs.getY();
            weights[count] = obs.getWeight();
            ++count;
        }

        final AbstractCurveFitter.TheoreticalValuesFunction model
            = new AbstractCurveFitter.TheoreticalValuesFunction(function,
                                                                observations);

        final double[] startPoint = initialGuess != null ?
            initialGuess :
            // Compute estimation.
            guesser.guess(observations);

        // Create an optimizer for fitting the curve to the observed points.
        return new LeastSquaresBuilder().
                maxEvaluations(Integer.MAX_VALUE).
                maxIterations(maxIter).
                start(startPoint).
                target(target).
                weight(new DiagonalMatrix(weights)).
                model(model.getModelFunction(), model.getModelFunctionJacobian()).
                build();
    }

    /**
     * Guesses the parameters.
     */
    public abstract static class ParameterGuesser {
        /** Comparator. */
        private static final Comparator<WeightedObservedPoint> CMP = new Comparator<WeightedObservedPoint>() {
                /** {@inheritDoc} */
                @Override
                public int compare(WeightedObservedPoint p1,
                                   WeightedObservedPoint p2) {
                    if (p1 == null && p2 == null) {
                        return 0;
                    }
                    if (p1 == null) {
                        return -1;
                    }
                    if (p2 == null) {
                        return 1;
                    }
                    int comp = Double.compare(p1.getX(), p2.getX());
                    if (comp != 0) {
                        return comp;
                    }
                    comp = Double.compare(p1.getY(), p2.getY());
                    if (comp != 0) {
                        return comp;
                    }
                    return Double.compare(p1.getWeight(), p2.getWeight());
                }
            };

        /**
         * Computes an estimation of the parameters.
         *
         * @param obs Observations.
         * @return the guessed parameters.
         */
        public abstract double[] guess(Collection<WeightedObservedPoint> obs);

        /**
         * Sort the observations.
         *
         * @param unsorted Input observations.
         * @return the input observations, sorted.
         */
        protected List<WeightedObservedPoint> sortObservations(Collection<WeightedObservedPoint> unsorted) {
            final List<WeightedObservedPoint> observations = new ArrayList<>(unsorted);
            Collections.sort(observations, CMP);
            return observations;
        }

        /**
         * Finds index of point in specified points with the largest Y.
         *
         * @param points Points to search.
         * @return the index in specified points array.
         */
        protected int findMaxY(WeightedObservedPoint[] points) {
            int maxYIdx = 0;
            for (int i = 1; i < points.length; i++) {
                if (points[i].getY() > points[maxYIdx].getY()) {
                    maxYIdx = i;
                }
            }
            return maxYIdx;
        }

        /**
         * Interpolates using the specified points to determine X at the
         * specified Y.
         *
         * @param points Points to use for interpolation.
         * @param startIdx Index within points from which to start the search for
         * interpolation bounds points.
         * @param idxStep Index step for searching interpolation bounds points.
         * @param y Y value for which X should be determined.
         * @return the value of X for the specified Y.
         * @throws ZeroException if {@code idxStep} is 0.
         * @throws OutOfRangeException if specified {@code y} is not within the
         * range of the specified {@code points}.
         */
        protected double interpolateXAtY(WeightedObservedPoint[] points,
                                         int startIdx,
                                         int idxStep,
                                         double y) {
            if (idxStep == 0) {
                throw new ZeroException();
            }
            final WeightedObservedPoint[] twoPoints
                = getInterpolationPointsForY(points, startIdx, idxStep, y);
            final WeightedObservedPoint p1 = twoPoints[0];
            final WeightedObservedPoint p2 = twoPoints[1];
            if (p1.getY() == y) {
                return p1.getX();
            }
            if (p2.getY() == y) {
                return p2.getX();
            }
            return p1.getX() + (((y - p1.getY()) * (p2.getX() - p1.getX())) /
                                (p2.getY() - p1.getY()));
        }

        /**
         * Gets the two bounding interpolation points from the specified points
         * suitable for determining X at the specified Y.
         *
         * @param points Points to use for interpolation.
         * @param startIdx Index within points from which to start search for
         * interpolation bounds points.
         * @param idxStep Index step for search for interpolation bounds points.
         * @param y Y value for which X should be determined.
         * @return the array containing two points suitable for determining X at
         * the specified Y.
         * @throws ZeroException if {@code idxStep} is 0.
         * @throws OutOfRangeException if specified {@code y} is not within the
         * range of the specified {@code points}.
         */
        private WeightedObservedPoint[] getInterpolationPointsForY(WeightedObservedPoint[] points,
                                                                   int startIdx,
                                                                   int idxStep,
                                                                   double y) {
            if (idxStep == 0) {
                throw new ZeroException();
            }
            for (int i = startIdx;
                 idxStep < 0 ? i + idxStep >= 0 : i + idxStep < points.length;
                 i += idxStep) {
                final WeightedObservedPoint p1 = points[i];
                final WeightedObservedPoint p2 = points[i + idxStep];
                if (isBetween(y, p1.getY(), p2.getY())) {
                    if (idxStep < 0) {
                        return new WeightedObservedPoint[] { p2, p1 };
                    } else {
                        return new WeightedObservedPoint[] { p1, p2 };
                    }
                }
            }

            // Boundaries are replaced by dummy values because the raised
            // exception is caught and the message never displayed.
            // TODO: Exceptions should not be used for flow control.
            throw new OutOfRangeException(y,
                                          Double.NEGATIVE_INFINITY,
                                          Double.POSITIVE_INFINITY);
        }

        /**
         * Determines whether a value is between two other values.
         *
         * @param value Value to test whether it is between {@code boundary1}
         * and {@code boundary2}.
         * @param boundary1 One end of the range.
         * @param boundary2 Other end of the range.
         * @return {@code true} if {@code value} is between {@code boundary1} and
         * {@code boundary2} (inclusive), {@code false} otherwise.
         */
        private boolean isBetween(double value,
                                  double boundary1,
                                  double boundary2) {
            return (value >= boundary1 && value <= boundary2) ||
                (value >= boundary2 && value <= boundary1);
        }
    }
}
