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

import java.util.List;
import java.util.Collection;

import org.apache.commons.math4.legacy.analysis.function.Gaussian;
import org.apache.commons.math4.legacy.exception.NotStrictlyPositiveException;
import org.apache.commons.math4.legacy.exception.NullArgumentException;
import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;
import org.apache.commons.math4.legacy.exception.OutOfRangeException;
import org.apache.commons.math4.legacy.exception.util.LocalizedFormats;
import org.apache.commons.math4.core.jdkmath.JdkMath;

/**
 * Fits points to a {@link
 * org.apache.commons.math4.legacy.analysis.function.Gaussian.Parametric Gaussian}
 * function.
 * <br>
 * The {@link #withStartPoint(double[]) initial guess values} must be passed
 * in the following order:
 * <ul>
 *  <li>Normalization</li>
 *  <li>Mean</li>
 *  <li>Sigma</li>
 * </ul>
 * The optimal values will be returned in the same order.
 *
 * <p>
 * Usage example:
 * <pre>
 *   WeightedObservedPoints obs = new WeightedObservedPoints();
 *   obs.add(4.0254623,  531026.0);
 *   obs.add(4.03128248, 984167.0);
 *   obs.add(4.03839603, 1887233.0);
 *   obs.add(4.04421621, 2687152.0);
 *   obs.add(4.05132976, 3461228.0);
 *   obs.add(4.05326982, 3580526.0);
 *   obs.add(4.05779662, 3439750.0);
 *   obs.add(4.0636168,  2877648.0);
 *   obs.add(4.06943698, 2175960.0);
 *   obs.add(4.07525716, 1447024.0);
 *   obs.add(4.08237071, 717104.0);
 *   obs.add(4.08366408, 620014.0);
 *   double[] parameters = GaussianCurveFitter.create().fit(obs.toList());
 * </pre>
 *
 * @since 3.3
 */
public final class GaussianCurveFitter extends SimpleCurveFitter {
    /** Parametric function to be fitted. */
    private static final Gaussian.Parametric FUNCTION = new Gaussian.Parametric() {
            /** {@inheritDoc} */
            @Override
            public double value(double x, double ... p) {
                double v = Double.POSITIVE_INFINITY;
                try {
                    v = super.value(x, p);
                } catch (NotStrictlyPositiveException e) { // NOPMD
                    // Do nothing.
                }
                return v;
            }

            /** {@inheritDoc} */
            @Override
            public double[] gradient(double x, double ... p) {
                double[] v = { Double.POSITIVE_INFINITY,
                               Double.POSITIVE_INFINITY,
                               Double.POSITIVE_INFINITY };
                try {
                    v = super.gradient(x, p);
                } catch (NotStrictlyPositiveException e) { // NOPMD
                    // Do nothing.
                }
                return v;
            }
        };

    /**
     * Constructor used by the factory methods.
     *
     * @param initialGuess Initial guess. If set to {@code null}, the initial guess
     * will be estimated using the {@link ParameterGuesser}.
     * @param maxIter Maximum number of iterations of the optimization algorithm.
     */
    private GaussianCurveFitter(double[] initialGuess,
                                int maxIter) {
        super(FUNCTION, initialGuess, new ParameterGuesser(), maxIter);
    }

    /**
     * Creates a default curve fitter.
     * The initial guess for the parameters will be {@link ParameterGuesser}
     * computed automatically, and the maximum number of iterations of the
     * optimization algorithm is set to {@link Integer#MAX_VALUE}.
     *
     * @return a curve fitter.
     *
     * @see #withStartPoint(double[])
     * @see #withMaxIterations(int)
     */
    public static GaussianCurveFitter create() {
        return new GaussianCurveFitter(null, Integer.MAX_VALUE);
    }

    /**
     * Guesses the parameters {@code norm}, {@code mean}, and {@code sigma}
     * of a {@link org.apache.commons.math4.legacy.analysis.function.Gaussian.Parametric}
     * based on the specified observed points.
     */
    public static class ParameterGuesser extends SimpleCurveFitter.ParameterGuesser {
        /**
         * {@inheritDoc}
         *
         * @return the guessed parameters, in the following order:
         * <ul>
         *  <li>Normalization factor</li>
         *  <li>Mean</li>
         *  <li>Standard deviation</li>
         * </ul>
         * @throws NullArgumentException if {@code observations} is
         * {@code null}.
         * @throws NumberIsTooSmallException if there are less than 3
         * observations.
         */
        @Override
        public double[] guess(Collection<WeightedObservedPoint> observations) {
            if (observations == null) {
                throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY);
            }
            if (observations.size() < 3) {
                throw new NumberIsTooSmallException(observations.size(), 3, true);
            }

            final List<WeightedObservedPoint> sorted = sortObservations(observations);
            return basicGuess(sorted.toArray(new WeightedObservedPoint[0]));
        }

        /**
         * Guesses the parameters based on the specified observed points.
         *
         * @param points Observed points, sorted.
         * @return the guessed parameters (normalization factor, mean and
         * sigma).
         */
        private double[] basicGuess(WeightedObservedPoint[] points) {
            final int maxYIdx = findMaxY(points);
            final double n = points[maxYIdx].getY();

            double fwhmApprox;
            try {
                final double halfY = 0.5 * n;
                final double fwhmX1 = interpolateXAtY(points, maxYIdx, -1, halfY);
                final double fwhmX2 = interpolateXAtY(points, maxYIdx, 1, halfY);
                fwhmApprox = fwhmX2 - fwhmX1;
            } catch (OutOfRangeException e) {
                // TODO: Exceptions should not be used for flow control.
                fwhmApprox = points[points.length - 1].getX() - points[0].getX();
            }
            final double s = fwhmApprox / (2 * JdkMath.sqrt(2 * JdkMath.log(2)));

            return new double[] { n, points[maxYIdx].getX(), s };
        }
    }
}
