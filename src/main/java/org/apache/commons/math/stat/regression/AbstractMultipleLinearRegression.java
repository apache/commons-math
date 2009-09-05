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
package org.apache.commons.math.stat.regression;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.RealVector;
import org.apache.commons.math.linear.ArrayRealVector;

/**
 * Abstract base class for implementations of MultipleLinearRegression.
 * @version $Revision$ $Date$
 * @since 2.0
 */
public abstract class AbstractMultipleLinearRegression implements
        MultipleLinearRegression {

    /** X sample data. */
    protected RealMatrix X;

    /** Y sample data. */
    protected RealVector Y;

    /**
     * Loads model x and y sample data from a flat array of data, overriding any previous sample.
     * Assumes that rows are concatenated with y values first in each row.
     *
     * @param data input data array
     * @param nobs number of observations (rows)
     * @param nvars number of independent variables (columns, not counting y)
     */
    public void newSampleData(double[] data, int nobs, int nvars) {
        double[] y = new double[nobs];
        double[][] x = new double[nobs][nvars + 1];
        int pointer = 0;
        for (int i = 0; i < nobs; i++) {
            y[i] = data[pointer++];
            x[i][0] = 1.0d;
            for (int j = 1; j < nvars + 1; j++) {
                x[i][j] = data[pointer++];
            }
        }
        this.X = new Array2DRowRealMatrix(x);
        this.Y = new ArrayRealVector(y);
    }

    /**
     * Loads new y sample data, overriding any previous sample
     *
     * @param y the [n,1] array representing the y sample
     */
    protected void newYSampleData(double[] y) {
        this.Y = new ArrayRealVector(y);
    }

    /**
     * Loads new x sample data, overriding any previous sample
     *
     * @param x the [n,k] array representing the x sample
     */
    protected void newXSampleData(double[][] x) {
        this.X = new Array2DRowRealMatrix(x);
    }

    /**
     * Validates sample data.
     *
     * @param x the [n,k] array representing the x sample
     * @param y the [n,1] array representing the y sample
     * @throws IllegalArgumentException if the x and y array data are not
     *             compatible for the regression
     */
    protected void validateSampleData(double[][] x, double[] y) {
        if ((x == null) || (y == null) || (x.length != y.length)) {
            throw MathRuntimeException.createIllegalArgumentException(
                  "dimension mismatch {0} != {1}",
                  (x == null) ? 0 : x.length,
                  (y == null) ? 0 : y.length);
        } else if ((x.length > 0) && (x[0].length > x.length)) {
            throw MathRuntimeException.createIllegalArgumentException(
                  "not enough data ({0} rows) for this many predictors ({1} predictors)",
                  x.length, x[0].length);
        }
    }

    /**
     * Validates sample data.
     *
     * @param x the [n,k] array representing the x sample
     * @param covariance the [n,n] array representing the covariance matrix
     * @throws IllegalArgumentException if the x sample data or covariance
     *             matrix are not compatible for the regression
     */
    protected void validateCovarianceData(double[][] x, double[][] covariance) {
        if (x.length != covariance.length) {
            throw MathRuntimeException.createIllegalArgumentException(
                 "dimension mismatch {0} != {1}", x.length, covariance.length);
        }
        if (covariance.length > 0 && covariance.length != covariance[0].length) {
            throw MathRuntimeException.createIllegalArgumentException(
                  "a {0}x{1} matrix was provided instead of a square matrix",
                  covariance.length, covariance[0].length);
        }
    }

    /**
     * {@inheritDoc}
     */
    public double[] estimateRegressionParameters() {
        RealVector b = calculateBeta();
        return b.getData();
    }

    /**
     * {@inheritDoc}
     */
    public double[] estimateResiduals() {
        RealVector b = calculateBeta();
        RealVector e = Y.subtract(X.operate(b));
        return e.getData();
    }

    /**
     * {@inheritDoc}
     */
    public double[][] estimateRegressionParametersVariance() {
        return calculateBetaVariance().getData();
    }

    /**
     * {@inheritDoc}
     */
    public double[] estimateRegressionParametersStandardErrors() {
        double[][] betaVariance = estimateRegressionParametersVariance();
        double sigma = calculateYVariance();
        int length = betaVariance[0].length;
        double[] result = new double[length];
        for (int i = 0; i < length; i++) {
            result[i] = Math.sqrt(sigma * betaVariance[i][i]);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public double estimateRegressandVariance() {
        return calculateYVariance();
    }

    /**
     * Calculates the beta of multiple linear regression in matrix notation.
     *
     * @return beta
     */
    protected abstract RealVector calculateBeta();

    /**
     * Calculates the beta variance of multiple linear regression in matrix
     * notation.
     *
     * @return beta variance
     */
    protected abstract RealMatrix calculateBetaVariance();

    /**
     * Calculates the Y variance of multiple linear regression.
     *
     * @return Y variance
     */
    protected abstract double calculateYVariance();

    /**
     * Calculates the residuals of multiple linear regression in matrix
     * notation.
     *
     * <pre>
     * u = y - X * b
     * </pre>
     *
     * @return The residuals [n,1] matrix
     */
    protected RealVector calculateResiduals() {
        RealVector b = calculateBeta();
        return Y.subtract(X.operate(b));
    }

}
