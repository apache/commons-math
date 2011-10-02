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
package org.apache.commons.math.stat.correlation;

import org.apache.commons.math.exception.MathIllegalArgumentException;
import org.apache.commons.math.exception.MathUnsupportedOperationException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.RealMatrix;

/**
 * Covariance implementation that does not require input data to be
 * stored in memory.
 *
 * @version $Id$
 * @since 3.0
 */
public class StorelessCovariance extends Covariance {

    private StorelessBivariateCovariance[][] covMatrix = null;

    private int rowDimension = 1;

    private int colDimension = 1;

    private boolean biasCorrected = true;

    public StorelessCovariance(int rowDimension, int colDimension){
        this(rowDimension, colDimension, true);
    }

    public StorelessCovariance(int rowDimension, int colDimension, boolean biasCorrected){
        this.rowDimension = rowDimension;
        this.colDimension = colDimension;
        this.biasCorrected = biasCorrected;
        covMatrix = new StorelessBivariateCovariance[rowDimension][colDimension];
        initializeMatrix();
    }

    private void initializeMatrix(){
        for(int i=0;i<rowDimension;i++){
            for(int j=0;j<colDimension;j++){
                covMatrix[i][j] = new StorelessBivariateCovariance(biasCorrected);
            }
        }
    }

    public StorelessBivariateCovariance getCovariance(int xIndex, int yIndex){
        return covMatrix[xIndex][yIndex];
    }

    public void setCovariance(int xIndex, int yIndex, StorelessBivariateCovariance cov){
        covMatrix[xIndex][yIndex] = cov;
    }

    public void incrementCovariance(int xIndex, int yIndex, double x, double y){
        covMatrix[xIndex][yIndex].increment(x, y);
    }

    public void incrementRow(double[] rowData)throws IllegalArgumentException{
        int length = rowData.length;
        if (length != colDimension) {
            throw new MathIllegalArgumentException(
                  LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, length, colDimension);
        }
        for(int i=0;i<length;i++){
            for(int j=0;j<length;j++){
                covMatrix[i][j].increment(rowData[i], rowData[j]);
            }
        }
    }

    @Override
    public RealMatrix getCovarianceMatrix() throws IllegalArgumentException {
        RealMatrix matrix = new Array2DRowRealMatrix(rowDimension, colDimension);
        for(int i=0;i<rowDimension;i++){
            for(int j=0;j<colDimension;j++){
                matrix.setEntry(i, j, covMatrix[i][j].getResult());
            }
        }
        return matrix;
    }

    public double[][] getData() throws IllegalArgumentException {
        double[][] data = new double[rowDimension][rowDimension];
        for(int i=0;i<rowDimension;i++){
            for(int j=0;j<colDimension;j++){
                data[i][j] = covMatrix[i][j].getResult();
            }
        }
        return data;
    }

    /**
     * This {@link Covariance} method is not supported by StorelessCovariance, since
     * the number of bivariate observations does not have to be the same for different
     * pairs of covariates - i.e., N as defined in {@link Covariance#getN()} is undefined.
     * @return nothing as this implementation always throws a {@link MathUnsupportedOperationException}
     * @throws MathUnsupportedOperationException in all cases
     */
    @Override
    public int getN()
        throws MathUnsupportedOperationException {
        throw new MathUnsupportedOperationException();
    }

}
