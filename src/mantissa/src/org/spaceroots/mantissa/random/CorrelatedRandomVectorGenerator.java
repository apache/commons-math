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

import org.spaceroots.mantissa.MantissaException;
import org.spaceroots.mantissa.linalg.Matrix;
import org.spaceroots.mantissa.linalg.GeneralMatrix;
import org.spaceroots.mantissa.linalg.SymetricalMatrix;

import java.io.Serializable;

/** This class allows to generate random vectors with correlated components.

 * <p>Random vectors with correlated components are built by combining
 * the uncorrelated components of another random vector in such a way
 * the resulting correlations are the ones specified by a positive
 * definite covariance matrix.</p>

 * <p>Sometimes, the covariance matrix for a given simulation is not
 * strictly positive definite. This means that the correlations are
 * not all independant from each other. In this case, however, the non
 * strictly positive elements found during the Cholesky decomposition
 * of the covariance matrix should not be negative either, they
 * should be null. This implies that rather than computing <code>C =
 * L.Lt</code> where <code>C</code> is the covariance matrix and
 * <code>L</code> is a lower-triangular matrix, we compute <code>C =
 * B.Bt</code> where <code>B</code> is a rectangular matrix having
 * more rows than columns. The number of columns of <code>B</code> is
 * the rank of the covariance matrix, and it is the dimension of the
 * uncorrelated random vector that is needed to compute the component
 * of the correlated vector. This class does handle this situation
 * automatically.</p>

 * @version $Id: CorrelatedRandomVectorGenerator.java 1705 2006-09-17 19:57:39Z luc $
 * @author L. Maisonobe

 */

public class CorrelatedRandomVectorGenerator
  implements Serializable, RandomVectorGenerator {

  /** Simple constructor.
   * <p>Build a correlated random vector generator from its mean
   * vector and covariance matrix.</p>
   * @param mean expected mean values for all components
   * @param covariance covariance matrix
   * @param generator underlying generator for uncorrelated normalized
   * components
   * @exception IllegalArgumentException if there is a dimension
   * mismatch between the mean vector and the covariance matrix
   * @exception NotPositiveDefiniteMatrixException if the
   * covariance matrix is not strictly positive definite
   */
  public CorrelatedRandomVectorGenerator(double[] mean,
                                         SymetricalMatrix covariance,
                                         NormalizedRandomGenerator generator)
    throws NotPositiveDefiniteMatrixException {

    int order = covariance.getRows();
    if (mean.length != order) {
      String message =
        MantissaException.translate("dimension mismatch {0} != {1}",
                                    new String[] {
                                      Integer.toString(mean.length),
                                      Integer.toString(order)
                                    });
      throw new IllegalArgumentException(message);
    }
    this.mean = (double[]) mean.clone();

    factorize(covariance);

    this.generator = generator;
    normalized = new double[rank];

  }

  /** Simple constructor.
   * <p>Build a null mean random correlated vector generator from its
   * covariance matrix.</p>
   * @param covariance covariance matrix
   * @param generator underlying generator for uncorrelated normalized
   * components
   * @exception NotPositiveDefiniteMatrixException if the
   * covariance matrix is not strictly positive definite
   */
  public CorrelatedRandomVectorGenerator(SymetricalMatrix covariance,
                                         NormalizedRandomGenerator generator)
    throws NotPositiveDefiniteMatrixException {

    int order = covariance.getRows();
    mean = new double[order];
    for (int i = 0; i < order; ++i) {
      mean[i] = 0;
    }

    factorize(covariance);

    this.generator = generator;
    normalized = new double[rank];

  }

  /** Get the root of the covariance matrix.
   * The root is the matrix <code>B</code> such that <code>B.Bt</code>
   * is equal to the covariance matrix
   * @return root of the square matrix
   */
  public Matrix getRootMatrix() {
    return root;
  }

  /** Get the underlying normalized components generator.
   * @return underlying uncorrelated components generator
   */
  public NormalizedRandomGenerator getGenerator() {
    return generator;
  }

  /** Get the rank of the covariance matrix.
   * The rank is the number of independant rows in the covariance
   * matrix, it is also the number of columns of the rectangular
   * matrix of the factorization.
   * @return rank of the square matrix.
   */
  public int getRank() {
    return rank;
  }

  /** Factorize the original square matrix.
   * @param covariance covariance matrix
   * @exception NotPositiveDefiniteMatrixException if the
   * covariance matrix is not strictly positive definite
   */
  private void factorize(SymetricalMatrix covariance)
  throws NotPositiveDefiniteMatrixException {

    int order = covariance.getRows();
    SymetricalMatrix c = (SymetricalMatrix) covariance.duplicate();
    GeneralMatrix    b = new GeneralMatrix(order, order);

    int[] swap  = new int[order];
    int[] index = new int[order];
    for (int i = 0; i < order; ++i) {
      index[i] = i;
    }

    rank = 0;
    for (boolean loop = true; loop;) {

      // find maximal diagonal element
      swap[rank] = rank;
      for (int i = rank + 1; i < order; ++i) {
        if (c.getElement(index[i], index[i])
            > c.getElement(index[swap[i]], index[swap[i]])) {
          swap[rank] = i;
        }
      }


      // swap elements
      if (swap[rank] != rank) {
        int tmp = index[rank];
        index[rank] = index[swap[rank]];
        index[swap[rank]] = tmp;
      }

      // check diagonal element
      if (c.getElement(index[rank], index[rank]) < 1.0e-12) {

        if (rank == 0) {
          throw new NotPositiveDefiniteMatrixException();
        }

        // check remaining diagonal elements
        for (int i = rank; i < order; ++i) {
          if (c.getElement(index[rank], index[rank]) < -1.0e-12) {
            // there is at least one sufficiently negative diagonal element,
            // the covariance matrix is wrong
            throw new NotPositiveDefiniteMatrixException();
          }
        }

        // all remaining diagonal elements are close to zero,
        // we consider we have found the rank of the covariance matrix
        ++rank;
        loop = false;

      } else {

        // transform the matrix
        double sqrt = Math.sqrt(c.getElement(index[rank], index[rank]));
        b.setElement(rank, rank, sqrt);
        double inverse = 1 / sqrt;
        for (int i = rank + 1; i < order; ++i) {
          double e = inverse * c.getElement(index[i], index[rank]);
          b.setElement(i, rank, e);
          c.setElement(index[i], index[i],
                          c.getElement(index[i], index[i]) - e * e);
          for (int j = rank + 1; j < i; ++j) {
            double f = b.getElement(j, rank);
            c.setElementAndSymetricalElement(index[i], index[j],
                                             c.getElement(index[i], index[j])
                                             - e * f);
          }
        }

        // prepare next iteration
        loop = ++rank < order;

      }

    }

    // build the root matrix
    root = new GeneralMatrix(order, rank);
    for (int i = 0; i < order; ++i) {
      for (int j = 0; j < rank; ++j) {
        root.setElement(swap[i], j, b.getElement(i, j));
      }
    }

  }

  /** Generate a correlated random vector.
   * @return a random vector as an array of double. The returned array
   * is created at each call, the caller can do what it wants with it.
   */
  public double[] nextVector() {

    // generate uncorrelated vector
    for (int i = 0; i < rank; ++i) {
      normalized[i] = generator.nextDouble();
    }

    // compute correlated vector
    double[] correlated = new double[mean.length];
    for (int i = 0; i < correlated.length; ++i) {
      correlated[i] = mean[i];
      for (int j = 0; j < rank; ++j) {
        correlated[i] += root.getElement(i, j) * normalized[j];
      }
    }

    return correlated;

  }

  /** Mean vector. */
  private double[] mean;

  /** Permutated Cholesky root of the covariance matrix. */
  private Matrix root;

  /** Rank of the covariance matrix. */
  private int rank;

  /** Underlying generator. */
  NormalizedRandomGenerator generator;

  /** Storage for the normalized vector. */
  private double[] normalized;

  private static final long serialVersionUID = -88563624902398453L;

}
