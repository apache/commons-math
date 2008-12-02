// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
// 
//   http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.spaceroots.mantissa.random;

import org.spaceroots.mantissa.linalg.SymetricalMatrix;

/** This class compute basic statistics on a scalar sample.
 * @version $Id$
 * @author L. Maisonobe
 */
public class VectorialSampleStatistics {

  /** Dimension of the vectors to handle. */
  private int dimension;

  /** Number of sample points. */
  private int n;

  /** Indices of the minimal values occurrence in the sample. */
  private int[] minIndices;

  /** Minimal value in the sample. */
  private double[] min;

  /** Maximal value in the sample. */
  private double[] max;

  /** Indices of the maximal values occurrence in the sample. */
  private int[] maxIndices;

  /** Sum of the sample values. */
  private double[] sum;

  /** Sum of the squares of the sample values. */
  private double[] sum2;

  /** Simple constructor.
   * Build a new empty instance
   */
  public VectorialSampleStatistics() {
    dimension  = -1;
    n          = 0;
    min        = null;
    minIndices = null;
    max        = null;
    maxIndices = null;
    sum        = null;
    sum2       = null;
  }

  /** Add one point to the instance.
   * @param x value of the sample point
   * @exception IllegalArgumentException if there is a dimension
   * mismatch between this point and the ones already added (this
   * cannot happen when the instance is empty)
   */
  public void add(double[] x) {

    if (n == 0) {

      dimension  = x.length;
      minIndices = new int[dimension];
      maxIndices = new int[dimension];
      min        = (double[]) x.clone();
      max        = (double[]) x.clone();
      sum        = (double[]) x.clone();
      sum2       = new double[dimension * (dimension + 1) / 2];

      int k = 0;
      for (int i = 0; i < dimension; ++i) {
        for (int j = 0; j <= i; ++j) {
          sum2[k++] = x[i] * x[j];
        }
      }

    } else {
      int k = 0;
      for (int i = 0; i < dimension; ++i) {

        if (x[i] < min[i]) {
          min[i]        = x[i];
          minIndices[i] = n;
        } else if (x[i] > max[i]) {
          max[i]        = x[i];
          maxIndices[i] = n;
        }

        sum[i] += x[i];
        for (int j = 0; j <= i; ++j) {
          sum2[k++] += x[i] * x[j];
        }

      }
    }

    ++n;

  }

  /** Add all points of an array to the instance.
   * @param points array of points
   * @exception IllegalArgumentException if there is a dimension
   * mismatch between these points and the ones already added (this
   * cannot happen when the instance is empty)
   */
  public void add(double[][] points) {
    for (int i = 0; i < points.length; ++i) {
      add(points[i]);
    }
  }

  /** Add all the points of another sample to the instance.
   * @param s samples to add
   * @exception IllegalArgumentException if there is a dimension
   * mismatch between this sample points and the ones already added
   * (this cannot happen when the instance is empty)
   */
  public void add(VectorialSampleStatistics s) {

    if (s.n == 0) {
      // nothing to add
      return;
    }

    if (n == 0) {

      dimension = s.dimension;
      min        = (double[]) s.min.clone();
      minIndices = (int[])    s.minIndices.clone();
      max        = (double[]) s.max.clone();
      maxIndices = (int[])    s.maxIndices.clone();
      sum        = (double[]) s.sum.clone();
      sum2       = (double[]) s.sum2.clone();

    } else {
      int k = 0;

      for (int i = 0; i < dimension; ++i) {

        if (s.min[i] < min[i]) {
          min[i]        = s.min[i];
          minIndices[i] = n;
        } else if (s.max[i] > max[i]) {
          max[i]        = s.max[i];
          maxIndices[i] = n;
        }

        sum[i] += s.sum[i];
        for (int j = 0; j <= i; ++j) {
          sum2[k] += s.sum2[k];
          ++k;
        }

      }

    }

    n += s.n;

  }

  /** Get the number of points in the sample.
   * @return number of points in the sample
   */
  public int size() {
    return n;
  }

  /** Get the minimal value in the sample.
   * <p>Since all components of the sample vector can reach their
   * minimal value at different times, this vector should be
   * considered as gathering all minimas of all components. The index
   * of the sample at which the minimum was encountered can be
   * retrieved with the {@link #getMinIndices getMinIndices}
   * method.</p>
   * @return minimal value in the sample (a new array is created
   * at each call, the caller may do what it wants to with it)
   * @see #getMinIndices
   */
  public double[] getMin() {
    return (double[]) min.clone();
  }

  /** Get the indices at which the minimal value occurred in the sample.
   * @return a vector reporting at which occurrence each component of
   * the sample reached its minimal value (a new array is created
   * at each call, the caller may do what it wants to with it)
   * @see #getMin
   */
  public int[] getMinIndices() {
    return (int[]) minIndices.clone();
  }

  /** Get the maximal value in the sample.
   * <p>Since all components of the sample vector can reach their
   * maximal value at different times, this vector should be
   * considered as gathering all maximas of all components. The index
   * of the sample at which the maximum was encountered can be
   * retrieved with the {@link #getMaxIndices getMaxIndices}
   * method.</p>
   * @return maximal value in the sample (a new array is created
   * at each call, the caller may do what it wants to with it)
   * @see #getMaxIndices
   */
  public double[] getMax() {
    return (double[]) max.clone();
  }

  /** Get the indices at which the maximal value occurred in the sample.
   * @return a vector reporting at which occurrence each component of
   * the sample reached its maximal value (a new array is created
   * at each call, the caller may do what it wants to with it)
   * @see #getMax
   */
  public int[] getMaxIndices() {
    return (int[]) maxIndices.clone();
  }

  /** Get the mean value of the sample.
   * @return mean value of the sample or an empty array
   * if the sample is empty (a new array is created
   * at each call, the caller may do what it wants to with it)
   */
  public double[] getMean() {
    if (n == 0) {
      return new double[0];
    }
    double[] mean = new double[dimension];
    for (int i = 0; i < dimension; ++i) {
      mean[i] = sum[i] / n;
    }
    return mean;
  }

  /** Get the covariance matrix of the underlying law.
   * This method estimate the covariance matrix considering that the
   * data available are only a <em>sample</em> of all possible
   * values. This value is the sample covariance matrix (as opposed
   * to the population covariance matrix).
   * @param covariance placeholder where to store the matrix, if null
   * a new matrix will be allocated
   * @return covariance matrix of the underlying or null if the
   * sample has less than 2 points (reference to covariance if it was
   * non-null, reference to a new matrix otherwise)
   */
  public SymetricalMatrix getCovarianceMatrix(SymetricalMatrix covariance) {

    if (n < 2) {
      return null;
    }

    if (covariance == null) {
      covariance = new SymetricalMatrix(dimension);
    }

    double c = 1.0 / (n * (n - 1));
    int k = 0;
    for (int i = 0; i < dimension; ++i) {
      for (int j = 0; j <= i; ++j) {
        double e = c * (n * sum2[k] - sum[i] * sum[j]);
        covariance.setElementAndSymetricalElement(i, j, e);
        ++k;
      }
    }

    return covariance;

    }

}
