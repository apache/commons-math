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

package org.apache.commons.math.optimization.fitting;

import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.math.analysis.function.Gaussian;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.exception.NumberIsTooSmallException;
import org.apache.commons.math.exception.OutOfRangeException;
import org.apache.commons.math.exception.ZeroException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.optimization.DifferentiableMultivariateVectorialOptimizer;
import org.apache.commons.math.optimization.fitting.CurveFitter;
import org.apache.commons.math.optimization.fitting.WeightedObservedPoint;

/**
 * Fits points to a {@link
 * org.apache.commons.math.analysis.function.Gaussian.Parametric Gaussian} function.
 * <p>
 * Usage example:
 * <pre>
 *   GaussianFitter fitter = new GaussianFitter(
 *     new LevenbergMarquardtOptimizer());
 *   fitter.addObservedPoint(4.0254623,  531026.0);
 *   fitter.addObservedPoint(4.03128248, 984167.0);
 *   fitter.addObservedPoint(4.03839603, 1887233.0);
 *   fitter.addObservedPoint(4.04421621, 2687152.0);
 *   fitter.addObservedPoint(4.05132976, 3461228.0);
 *   fitter.addObservedPoint(4.05326982, 3580526.0);
 *   fitter.addObservedPoint(4.05779662, 3439750.0);
 *   fitter.addObservedPoint(4.0636168,  2877648.0);
 *   fitter.addObservedPoint(4.06943698, 2175960.0);
 *   fitter.addObservedPoint(4.07525716, 1447024.0);
 *   fitter.addObservedPoint(4.08237071, 717104.0);
 *   fitter.addObservedPoint(4.08366408, 620014.0);
 *   double[] parameters = fitter.fit();
 * </pre>
 *
 * @since 2.2
 * @version $Revision$ $Date$
 */
public class GaussianFitter extends CurveFitter {

    /**
     * Constructs an instance using the specified optimizer.
     *
     * @param optimizer optimizer to use for the fitting
     */
    public GaussianFitter(DifferentiableMultivariateVectorialOptimizer optimizer) {
        super(optimizer);;
    }


    /**
     * Fits Gaussian function to the observed points.
     * It will call the base class
     * {@link CurveFitter#fit(
     * org.apache.commons.math.analysis.ParametricUnivariateRealFunction,
     * double[]) fit} method.
     *
     * @return the Gaussian function that best fits the observed points.
     */
    public double[] fit() {
        return fit(new Gaussian.Parametric(),
                   (new ParameterGuesser(getObservations())).guess());
    }

    /**
     * Guesses the parameters {@code norm}, {@code mean}, and {@code sigma}
     * of a {@link org.apache.commons.math.analysis.function.Gaussian.Parametric}
     * based on the specified observed points.
     */
    public static class ParameterGuesser {
        /** Observed points. */
        private final WeightedObservedPoint[] observations;

        /** Resulting guessed parameters. */
        private double[] parameters;

        /**
         * Constructs instance with the specified observed points.
         *
         * @param observations observed points upon which should base guess
         */
        public ParameterGuesser(WeightedObservedPoint[] observations) {
            if (observations == null) {
                throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY);
            }
            if (observations.length < 3) {
                throw new NumberIsTooSmallException(observations.length, 3, true);
            }
            this.observations = observations.clone();
        }

        /**
         * Guesses the parameters based on the observed points.
         *
         * @return guessed parameters array <code>{norm, mean, sigma}</code>
         */
        public double[] guess() {
            if (parameters == null) {
                parameters = basicGuess(observations);
            }
            return parameters.clone();
        }

        /**
         * Guesses the parameters based on the specified observed points.
         *
         * @param points observed points upon which should base guess
         *
         * @return guessed parameters array <code>{norm, mean, sigma}</code>
         */
        private double[] basicGuess(WeightedObservedPoint[] points) {
            Arrays.sort(points, createWeightedObservedPointComparator());
            double[] params = new double[3];


            int maxYIdx = findMaxY(points);
            params[0] = points[maxYIdx].getY();
            params[1] = points[maxYIdx].getX();

            double fwhmApprox;
            try {
                double halfY = params[0] + ((params[1] - params[0]) / 2.0);
                double fwhmX1 = interpolateXAtY(points, maxYIdx, -1, halfY);
                double fwhmX2 = interpolateXAtY(points, maxYIdx, +1, halfY);
                fwhmApprox = fwhmX2 - fwhmX1;
            } catch (OutOfRangeException e) {
                fwhmApprox = points[points.length - 1].getX() - points[0].getX();
            }
            params[2] = fwhmApprox / (2.0 * Math.sqrt(2.0 * Math.log(2.0)));

            return params;
        }

        /**
         * Finds index of point in specified points with the largest Y.
         *
         * @param points points to search
         *
         * @return index in specified points array
         */
        private int findMaxY(WeightedObservedPoint[] points) {
            int maxYIdx = 0;
            for (int i = 1; i < points.length; i++) {
                if (points[i].getY() > points[maxYIdx].getY()) {
                    maxYIdx = i;
                }
            }
            return maxYIdx;
        }

        /**
         * Interpolates using the specified points to determine X at the specified
         * Y.
         *
         * @param points points to use for interpolation
         * @param startIdx index within points from which to start search for
         *        interpolation bounds points
         * @param idxStep index step for search for interpolation bounds points
         * @param y Y value for which X should be determined
         *
         * @return value of X at the specified Y
         *
         * @throws IllegalArgumentException if idxStep is 0
         * @throws OutOfRangeException if specified <code>y</code> is not within the
         *         range of the specified <code>points</code>
         */
        private double interpolateXAtY(WeightedObservedPoint[] points,
                                       int startIdx, int idxStep, double y)
            throws OutOfRangeException {
            if (idxStep == 0) {
                throw new ZeroException();
            }
            WeightedObservedPoint[] twoPoints = getInterpolationPointsForY(points, startIdx, idxStep, y);
            WeightedObservedPoint pointA = twoPoints[0];
            WeightedObservedPoint pointB = twoPoints[1];
            if (pointA.getY() == y) {
                return pointA.getX();
            }
            if (pointB.getY() == y) {
                return pointB.getX();
            }
            return pointA.getX() +
                   (((y - pointA.getY()) * (pointB.getX() - pointA.getX())) /
                    (pointB.getY() - pointA.getY()));
        }

        /**
         * Gets the two bounding interpolation points from the specified points
         * suitable for determining X at the specified Y.
         *
         * @param points points to use for interpolation
         * @param startIdx index within points from which to start search for
         *        interpolation bounds points
         * @param idxStep index step for search for interpolation bounds points
         * @param y Y value for which X should be determined
         *
         * @return array containing two points suitable for determining X at the
         *         specified Y
         *
         * @throws IllegalArgumentException if idxStep is 0
         * @throws OutOfRangeException if specified <code>y</code> is not within the
         *         range of the specified <code>points</code>
         */
        private WeightedObservedPoint[] getInterpolationPointsForY(WeightedObservedPoint[] points,
                                                                   int startIdx, int idxStep, double y)
            throws OutOfRangeException {
            if (idxStep == 0) {
                throw new ZeroException();
            }
            for (int i = startIdx;
                 (idxStep < 0) ? (i + idxStep >= 0) : (i + idxStep < points.length);
                 i += idxStep) {
                if (isBetween(y, points[i].getY(), points[i + idxStep].getY())) {
                    return (idxStep < 0) ?
                           new WeightedObservedPoint[] { points[i + idxStep], points[i] } :
                           new WeightedObservedPoint[] { points[i], points[i + idxStep] };
                }
            }

            double minY = Double.POSITIVE_INFINITY;
            double maxY = Double.NEGATIVE_INFINITY;
            for (final WeightedObservedPoint point : points) {
                minY = Math.min(minY, point.getY());
                maxY = Math.max(maxY, point.getY());
            }
            throw new OutOfRangeException(y, minY, maxY);

        }

        /**
         * Determines whether a value is between two other values.
         *
         * @param value value to determine whether is between <code>boundary1</code>
         *        and <code>boundary2</code>
         * @param boundary1 one end of the range
         * @param boundary2 other end of the range
         *
         * @return true if <code>value</code> is between <code>boundary1</code> and
         *         <code>boundary2</code> (inclusive); false otherwise
         */
        private boolean isBetween(double value, double boundary1, double boundary2) {
            return (value >= boundary1 && value <= boundary2) ||
                   (value >= boundary2 && value <= boundary1);
        }

        /**
         * Factory method creating <code>Comparator</code> for comparing
         * <code>WeightedObservedPoint</code> instances.
         *
         * @return new <code>Comparator</code> instance
         */
        private Comparator<WeightedObservedPoint> createWeightedObservedPointComparator() {
            return new Comparator<WeightedObservedPoint>() {
                public int compare(WeightedObservedPoint p1, WeightedObservedPoint p2) {
                    if (p1 == null && p2 == null) {
                        return 0;
                    }
                    if (p1 == null) {
                        return -1;
                    }
                    if (p2 == null) {
                        return 1;
                    }
                    if (p1.getX() < p2.getX()) {
                        return -1;
                    }
                    if (p1.getX() > p2.getX()) {
                        return 1;
                    }
                    if (p1.getY() < p2.getY()) {
                        return -1;
                    }
                    if (p1.getY() > p2.getY()) {
                        return 1;
                    }
                    if (p1.getWeight() < p2.getWeight()) {
                        return -1;
                    }
                    if (p1.getWeight() > p2.getWeight()) {
                        return 1;
                    }
                    return 0;
                }
            };
        }
    }
}
