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

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.QRDecomposition;
import org.apache.commons.math.linear.QRDecompositionImpl;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;

/**
 * <p>Implements ordinary least squares (OLS) to estimate the parameters of a
 * multiple linear regression model.</p>
 *
 * <p>OLS assumes the covariance matrix of the error to be diagonal and with
 * equal variance.</p>
 * <p>
 * u ~ N(0, &sigma;<sup>2</sup>I)
 * </p>
 *
 * <p>The regression coefficients, b, satisfy the normal equations:
 * <p>
 * X<sup>T</sup> X b = X<sup>T</sup> y
 * </p>
 *
 * <p>To solve the normal equations, this implementation uses QR decomposition
 * of the X matrix. (See {@link QRDecompositionImpl} for details on the
 * decomposition algorithm.)
 * </p>
 * <p>X<sup>T</sup>X b = X<sup>T</sup> y <br/>
 * (QR)<sup>T</sup> (QR) b = (QR)<sup>T</sup>y <br/>
 * R<sup>T</sup> (Q<sup>T</sup>Q) R b = R<sup>T</sup> Q<sup>T</sup> y <br/>
 * R<sup>T</sup> R b = R<sup>T</sup> Q<sup>T</sup> y <br/>
 * (R<sup>T</sup>)<sup>-1</sup> R<sup>T</sup> R b = (R<sup>T</sup>)<sup>-1</sup> R<sup>T</sup> Q<sup>T</sup> y <br/>
 * R b = Q<sup>T</sup> y
 * </p>
 * Given Q and R, the last equation is solved by back-subsitution.</p>
 *
 * @version $Revision$ $Date$
 * @since 2.0
 */
public class OLSMultipleLinearRegression extends AbstractMultipleLinearRegression {

    /** Cached QR decomposition of X matrix */
    private QRDecomposition qr = null;

    /**
     * Loads model x and y sample data, overriding any previous sample.
     *
     * Computes and caches QR decomposition of the X matrix.
     * @param y the [n,1] array representing the y sample
     * @param x the [n,k] array representing the x sample
     * @throws IllegalArgumentException if the x and y array data are not
     *             compatible for the regression
     */
    public void newSampleData(double[] y, double[][] x) {
        validateSampleData(x, y);
        newYSampleData(y);
        newXSampleData(x);
    }

    /**
     * {@inheritDoc}
     *
     * Computes and caches QR decomposition of the X matrix
     */
    @Override
    public void newSampleData(double[] data, int nobs, int nvars) {
        super.newSampleData(data, nobs, nvars);
        qr = new QRDecompositionImpl(X);
    }

    /**
     * <p>Compute the "hat" matrix.
     * </p>
     * <p>The hat matrix is defined in terms of the design matrix X
     *  by X(X<sup>T</sup>X)<sup>-1</sup>X<sup>T</sup>
     * </p>
     * <p>The implementation here uses the QR decomposition to compute the
     * hat matrix as Q I<sub>p</sub>Q<sup>T</sup> where I<sub>p</sub> is the
     * p-dimensional identity matrix augmented by 0's.  This computational
     * formula is from "The Hat Matrix in Regression and ANOVA",
     * David C. Hoaglin and Roy E. Welsch,
     * <i>The American Statistician</i>, Vol. 32, No. 1 (Feb., 1978), pp. 17-22.
     *
     * @return the hat matrix
     */
    public RealMatrix calculateHat() {
        // Create augmented identity matrix
        RealMatrix Q = qr.getQ();
        final int p = qr.getR().getColumnDimension();
        final int n = Q.getColumnDimension();
        Array2DRowRealMatrix augI = new Array2DRowRealMatrix(n, n);
        double[][] augIData = augI.getDataRef();
        for (int i = 0; i < n; i++) {
            for (int j =0; j < n; j++) {
                if (i == j && i < p) {
                    augIData[i][j] = 1d;
                } else {
                    augIData[i][j] = 0d;
                }
            }
        }

        // Compute and return Hat matrix
        return Q.multiply(augI).multiply(Q.transpose());
    }

    /**
     * Loads new x sample data, overriding any previous sample
     *
     * @param x the [n,k] array representing the x sample
     */
    @Override
    protected void newXSampleData(double[][] x) {
        this.X = new Array2DRowRealMatrix(x);
        qr = new QRDecompositionImpl(X);
    }

    /**
     * Calculates regression coefficients using OLS.
     *
     * @return beta
     */
    @Override
    protected RealVector calculateBeta() {
        return qr.getSolver().solve(Y);
    }

    /**
     * <p>Calculates the variance on the beta by OLS.
     * </p>
     * <p>Var(b) = (X<sup>T</sup>X)<sup>-1</sup>
     * </p>
     * <p>Uses QR decomposition to reduce (X<sup>T</sup>X)<sup>-1</sup>
     * to (R<sup>T</sup>R)<sup>-1</sup>, with only the top p rows of
     * R included, where p = the length of the beta vector.</p>
     *
     * @return The beta variance
     */
    @Override
    protected RealMatrix calculateBetaVariance() {
        int p = X.getColumnDimension();
        RealMatrix Raug = qr.getR().getSubMatrix(0, p - 1 , 0, p - 1);
        RealMatrix Rinv = new LUDecompositionImpl(Raug).getSolver().getInverse();
        return Rinv.multiply(Rinv.transpose());
    }


    /**
     * <p>Calculates the variance on the Y by OLS.
     * </p>
     * <p> Var(y) = Tr(u<sup>T</sup>u)/(n - k)
     * </p>
     * @return The Y variance
     */
    @Override
    protected double calculateYVariance() {
        RealVector residuals = calculateResiduals();
        return residuals.dotProduct(residuals) /
               (X.getRowDimension() - X.getColumnDimension());
    }

}
