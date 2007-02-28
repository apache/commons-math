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
package org.apache.commons.math.stat.descriptive.moment;

import java.io.Serializable;

import org.apache.commons.math.DimensionMismatchException;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealMatrixImpl;

/**
 * Returns the covariance matrix of the available vectors.
 * @version $Revision:$
 */
public class VectorialCovariance implements Serializable {

    /** Serializable version identifier */
    private static final long serialVersionUID = 4118372414238930270L;

    /** Sums for each component. */
    private double[] sums;

    /** Sums of products for each component. */
    private double[] productsSums;

    /** Number of vectors in the sample. */
    private long n;

    /** Constructs a VectorialMean.
     * @param dimension vectors dimension
     */
    public VectorialCovariance(int dimension) {
        sums         = new double[dimension];
        productsSums = new double[dimension * (dimension + 1) / 2];
        n            = 0;
    }

    /**
     * Add a new vector to the sample.
     * @param v vector to add
     * @exception DimensionMismatchException if the vector does not have the right dimension
     */
    public void increment(double[] v) throws DimensionMismatchException {
        if (v.length != sums.length) {
            throw new DimensionMismatchException(v.length, sums.length);
        }
        int k = 0;
        for (int i = 0; i < v.length; ++i) {
            sums[i] += v[i];
            for (int j = 0; j <= i; ++j) {
                productsSums[k++] += v[i] * v[j];
            }
        }
        n++;
    }

    /**
     * Get the covariance matrix.
     * @return covariance matrix
     */
    public RealMatrix getResult() {

        int dimension = sums.length;
        RealMatrixImpl result = new RealMatrixImpl(dimension, dimension);

        if (n > 1) {
            double[][] resultData = result.getDataRef();
            double c = 1.0 / (n * (n - 1));
            int k = 0;
            for (int i = 0; i < dimension; ++i) {
                for (int j = 0; j <= i; ++j) {
                    double e = c * (n * productsSums[k++] - sums[i] * sums[j]);
                    resultData[i][j] = e;
                    resultData[j][i] = e;
                }
            }
        }

        return result;

    }

    /**
     * Get the number of vectors in the sample.
     * @return number of vectors in the sample
     */
    public long getN() {
        return n;
    }

}