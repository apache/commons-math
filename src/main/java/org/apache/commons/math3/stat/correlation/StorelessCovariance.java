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
package org.apache.commons.math3.stat.correlation;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * Covariance implementation that does not require input data to be
 * stored in memory. The size of the covariance matrix is specified in the
 * constructor. Specific elements of the matrix are incrementally updated with
 * calls to incrementRow() or increment Covariance().
 *
 * <p>This class is based on a paper written by Philippe Pébay:
 * <a href="http://prod.sandia.gov/techlib/access-control.cgi/2008/086212.pdf">
 * Formulas for Robust, One-Pass Parallel Computation of Covariances and
 * Arbitrary-Order Statistical Moments</a>, 2008, Technical Report SAND2008-6212,
 * Sandia National Laboratories.</p>
 *
 * @version $Id$
 * @since 3.0
 */
public class StorelessCovariance extends Covariance {

    /** the two-dimensional covariance matrix */
    private StorelessBivariateCovariance[][] covMatrix;

    /** row dimension of the covariance matrix */
    private int rowDimension;

    /** column dimension of the covariance matrix */
    private int colDimension;

    /** flag for bias correction */
    private boolean biasCorrected;

    /**
     * Create a bias corrected covariance matrix with a given number of rows and columns.
     *
     * @param rows number of rows
     * @param cols number of columns
     */
    public StorelessCovariance(final int rows, final int cols) {
        this(rows, cols, true);
    }

    /**
     * Create a covariance matrix with a given number of rows and columns and the
     * indicated bias correction.
     *
     * @param rows number of variables in the rows
     * @param cols number of variables in the columns
     * @param biasCorrection if <code>true</code> the covariance estimate is corrected
     * for bias, i.e. n-1 in the denominator, otherwise there is no bias correction,
     * i.e. n in the denominator.
     */
    public StorelessCovariance(final int rows, final int cols,
                               final boolean biasCorrection) {
        rowDimension = rows;
        colDimension = cols;
        biasCorrected = biasCorrection;
        covMatrix = new StorelessBivariateCovariance[rowDimension][colDimension];
        initializeMatrix();
    }

    /**
     * Initialize the internal two-dimensional array of
     * {@link StorelessBivariateCovariance} instances.
     */
    private void initializeMatrix() {
        for(int i=0;i<rowDimension;i++){
            for(int j=0;j<colDimension;j++){
                covMatrix[i][j] = new StorelessBivariateCovariance(biasCorrected);
            }
        }
    }

    /**
     * Get the covariance for an individual element of the covariance matrix.
     *
     * @param xIndex row index in the covariance matrix
     * @param yIndex column index in the covariance matrix
     * @return the covariance of the given element
     */
    public StorelessBivariateCovariance getCovariance(final int xIndex,
                                                      final int yIndex) {
        return covMatrix[xIndex][yIndex];
    }

    /**
     * Set the covariance for an individual element of the covariance matrix.
     *
     * @param xIndex row index in the covariance matrix
     * @param yIndex column index in the covariance matrix
     * @param cov the covariance to be set
     */
    public void setCovariance(final int xIndex, final int yIndex,
                              final StorelessBivariateCovariance cov) {
        covMatrix[xIndex][yIndex] = cov;
    }

    /**
     * Increment one individual element of the covariance matrix.
     *
     * <p>The element is specified by the xIndex and yIndex and incremented with the
     * corresponding values of x and y.</p>
     *
     * @param xIndex row index in the covariance matrix
     * @param yIndex column index in the covariance matrix
     * @param x value of x
     * @param y value of y
     */
    public void incrementCovariance(final int xIndex, final int yIndex,
                                    final double x, final double y) {
        covMatrix[xIndex][yIndex].increment(x, y);
    }

    /**
     * Increment the covariance matrix with one row of data.
     *
     * @param rowData array representing one row of data.
     * @throws DimensionMismatchException if the length of <code>rowData</code>
     * does not match with the covariance matrix
     */
    public void incrementRow(final double[] rowData)
        throws DimensionMismatchException {

        int length = rowData.length;
        if (length != colDimension) {
            throw new DimensionMismatchException(length, colDimension);
        }
        for (int i = 0; i < length; i++){
            for (int j = 0; j < length; j++){
                covMatrix[i][j].increment(rowData[i], rowData[j]);
            }
        }

    }

    /**
     * {@inheritDoc}
     * @throws NumberIsTooSmallException if the number of observations
     * in a cell is &lt; 2
     */
    public RealMatrix getCovarianceMatrix() throws NumberIsTooSmallException {
        return MatrixUtils.createRealMatrix(getData());
    }

    /**
     * Return the covariance matrix as two-dimensional array.
     *
     * @return a two-dimensional double array of covariance values
     * @throws NumberIsTooSmallException if the number of observations
     * for a cell is &lt; 2
     */
    public double[][] getData() throws NumberIsTooSmallException {
        final double[][] data = new double[rowDimension][rowDimension];
        for (int i = 0; i < rowDimension; i++) {
            for (int j = 0; j < colDimension; j++) {
                data[i][j] = covMatrix[i][j].getResult();
            }
        }
        return data;
    }

    /**
     * This {@link Covariance} method is not supported by a {@link StorelessCovariance},
     * since the number of bivariate observations does not have to be the same for different
     * pairs of covariates - i.e., N as defined in {@link Covariance#getN()} is undefined.
     *
     * @return nothing as this implementation always throws a
     * {@link MathUnsupportedOperationException}
     * @throws MathUnsupportedOperationException in all cases
     */
    public int getN()
        throws MathUnsupportedOperationException {
        throw new MathUnsupportedOperationException();
    }
}
