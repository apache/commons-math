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
package org.apache.commons.math3.fitting;

import java.util.Collection;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.fitting.leastsquares.WithStartPoint;
import org.apache.commons.math3.fitting.leastsquares.WithMaxIterations;
import org.apache.commons.math3.linear.DiagonalMatrix;

/**
 * Fits points to a {@link
 * org.apache.commons.math3.analysis.polynomials.PolynomialFunction.Parametric polynomial}
 * function.
 * <br/>
 * The size of the {@link #withStartPoint(double[]) initial guess} array defines the
 * degree of the polynomial to be fitted.
 * They must be sorted in increasing order of the polynomial's degree.
 * The optimal values of the coefficients will be returned in the same order.
 *
 * @version $Id$
 * @since 3.3
 */
public class PolynomialCurveFitter extends AbstractCurveFitter<LevenbergMarquardtOptimizer>
    implements WithStartPoint<PolynomialCurveFitter>,
               WithMaxIterations<PolynomialCurveFitter> {
    /** Parametric function to be fitted. */
    private static final PolynomialFunction.Parametric FUNCTION = new PolynomialFunction.Parametric();
    /** Initial guess. */
    private final double[] initialGuess;
    /** Maximum number of iterations of the optimization algorithm. */
    private final int maxIter;

    /**
     * Contructor used by the factory methods.
     *
     * @param initialGuess Initial guess.
     * @param maxIter Maximum number of iterations of the optimization algorithm.
     * @throws MathInternalError if {@code initialGuess} is {@code null}.
     */
    private PolynomialCurveFitter(double[] initialGuess,
                                  int maxIter) {
        this.initialGuess = initialGuess;
        this.maxIter = maxIter;
    }

    /**
     * Creates a default curve fitter.
     * Zero will be used as initial guess for the coefficients, and the maximum
     * number of iterations of the optimization algorithm is set to
     * {@link Integer#MAX_VALUE}.
     *
     * @param degree Degree of the polynomial to be fitted.
     * @return a curve fitter.
     *
     * @see #withStartPoint(double[])
     * @see #withMaxIterations(int)
     */
    public static PolynomialCurveFitter create(int degree) {
        return new PolynomialCurveFitter(new double[degree + 1], Integer.MAX_VALUE);
    }

    /** {@inheritDoc} */
    public PolynomialCurveFitter withStartPoint(double[] start) {
        return new PolynomialCurveFitter(start.clone(),
                                         maxIter);
    }

    /** {@inheritDoc} */
    public PolynomialCurveFitter withMaxIterations(int max) {
        return new PolynomialCurveFitter(initialGuess,
                                         max);
    }

    /** {@inheritDoc} */
    @Override
    protected LevenbergMarquardtOptimizer getOptimizer(Collection<WeightedObservedPoint> observations) {
        // Prepare least-squares problem.
        final int len = observations.size();
        final double[] target  = new double[len];
        final double[] weights = new double[len];

        int i = 0;
        for (WeightedObservedPoint obs : observations) {
            target[i]  = obs.getY();
            weights[i] = obs.getWeight();
            ++i;
        }

        final AbstractCurveFitter.TheoreticalValuesFunction model
            = new AbstractCurveFitter.TheoreticalValuesFunction(FUNCTION,
                                                                observations);

        if (initialGuess == null) {
            throw new MathInternalError();
        }

        // Return a new optimizer set up to fit a Gaussian curve to the
        // observed points.
        return LevenbergMarquardtOptimizer.create()
            .withMaxEvaluations(Integer.MAX_VALUE)
            .withMaxIterations(maxIter)
            .withStartPoint(initialGuess)
            .withTarget(target)
            .withWeight(new DiagonalMatrix(weights))
            .withModelAndJacobian(model.getModelFunction(),
                                  model.getModelFunctionJacobian());
    }
}
