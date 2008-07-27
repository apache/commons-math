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

import org.apache.commons.math.linear.QRDecomposition;
import org.apache.commons.math.linear.QRDecompositionImpl;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealMatrixImpl;

/**
 * <p>Implements ordinary least squares (OLS) to estimate the parameters of a 
 * multiple linear regression model.</p>
 * 
 * <p>OLS assumes the covariance matrix of the error to be diagonal and with
 * equal variance.
 * <pre>
 * u ~ N(0, sigma^2*I)
 * </pre></p>
 * 
 * <p>The regression coefficients, b, satisfy the normal equations:
 * <pre>
 * X^T X b = X^T y
 * </pre></p>
 * 
 * <p>To solve the normal equations, this implementation uses QR decomposition
 * of the X matrix. (See {@link QRDecompositionImpl} for details on the
 * decomposition algorithm.)
 * <pre>
 * X^T X b = X^T y
 * (QR)^T (QR) b = (QR)^T y
 * R^T (Q^T Q) R b = R^T Q^T y
 * R^T R b = R^T Q^T y
 * (R^T)^{-1} R^T R b = (R^T)^{-1} R^T Q^T y
 * R b = Q^T y
 * </pre>
 * Given Q and R, the last equation is solved by back-subsitution.</p>
 * 
 * @version $Revision$ $Date$
 * @since 2.0
 */
public class OLSMultipleLinearRegression extends AbstractMultipleLinearRegression {
    
    /** Cached QR decomposition of X matrix */
    private QRDecomposition qr = null;

    /*
     * {@inheritDoc}
     * 
     * Computes and caches QR decomposition of the X matrix.
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
    public void newSampleData(double[] data, int nobs, int nvars) {
        super.newSampleData(data, nobs, nvars);
        qr = new QRDecompositionImpl(X);
    }
    
    /**
     * Loads new x sample data, overriding any previous sample
     * 
     * @param x the [n,k] array representing the x sample
     */
    protected void newXSampleData(double[][] x) {
        this.X = new RealMatrixImpl(x);
        qr = new QRDecompositionImpl(X);
    }
    
    /**
     * Calculates regression coefficients using OLS.
     * 
     * @return beta
     */
    protected RealMatrix calculateBeta() {
        return solveUpperTriangular((RealMatrixImpl) qr.getR(),
                (RealMatrixImpl) qr.getQ().transpose().multiply(Y));
    }

    /**
     * Calculates the variance on the beta by OLS.
     * <pre>
     *  Var(b)=(X'X)^-1
     * </pre>
     * @return The beta variance
     */
    protected RealMatrix calculateBetaVariance() {
        RealMatrix XTX = X.transpose().multiply(X);
        return XTX.inverse();
    }
    

    /**
     * Calculates the variance on the Y by OLS.
     * <pre>
     *  Var(y)=Tr(u'u)/(n-k)
     * </pre>
     * @return The Y variance
     */
    protected double calculateYVariance() {
        RealMatrix u = calculateResiduals();
        RealMatrix sse = u.transpose().multiply(u);
        return sse.getTrace()/(X.getRowDimension()-X.getColumnDimension());
    }
    
    /** TODO:  Find a home for the following methods in the linear package */   
    
    /**
     * <p>Uses back substitution to solve the system</p>
     * 
     * <p>coefficients X = constants</p>
     * 
     * <p>coefficients must upper-triangular and constants must be a column 
     * matrix.  The solution is returned as a column matrix.</p>
     * 
     * <p>The number of columns in coefficients determines the length
     * of the returned solution vector (column matrix).  If constants
     * has more rows than coefficients has columns, excess rows are ignored.
     * Similarly, extra (zero) rows in coefficients are ignored</p>
     * 
     * @param coefficients upper-triangular coefficients matrix
     * @param constants column RHS constants matrix
     * @return solution matrix as a column matrix
     * 
     */
    private static RealMatrix solveUpperTriangular(RealMatrixImpl coefficients,
            RealMatrixImpl constants) {
        if (!isUpperTriangular(coefficients, 1E-12)) {
            throw new IllegalArgumentException(
                   "Coefficients is not upper-triangular");
        }
        if (constants.getColumnDimension() != 1) {
            throw new IllegalArgumentException(
                    "Constants not a column matrix.");
        }
        int length = coefficients.getColumnDimension();
        double[][] cons = constants.getDataRef();
        double[][] coef = coefficients.getDataRef();
        double x[] = new double[length];
        for (int i = 0; i < length; i++) {
            int index = length - 1 - i;
            double sum = 0;
            for (int j = index + 1; j < length; j++) {
                sum += coef[index][j] * x[j];
            }
            x[index] = (cons[index][0] - sum) / coef[index][index];
        } 
        return new RealMatrixImpl(x);
    }
    
    /**
     * <p>Returns true iff m is an upper-triangular matrix.</p>
     * 
     * <p>Makes sure all below-diagonal elements are within epsilon of 0.</p>
     * 
     * @param m matrix to check
     * @param epsilon maximum allowable absolute value for elements below
     * the main diagonal
     * 
     * @return true if m is upper-triangular; false otherwise
     * @throws NullPointerException if m is null
     */
    private static boolean isUpperTriangular(RealMatrixImpl m, double epsilon) {
        double[][] data = m.getDataRef();
        int nCols = m.getColumnDimension();
        int nRows = m.getRowDimension();
        for (int r = 0; r < nRows; r++) {
            int bound = Math.min(r, nCols);
            for (int c = 0; c < bound; c++) {
                if (Math.abs(data[r][c]) > epsilon) {
                    return false;
                }
            }
        }
        return true;
    }
}
