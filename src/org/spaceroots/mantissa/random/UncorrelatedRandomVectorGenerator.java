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

import java.io.Serializable;

/** This class allows to generate random vectors with uncorrelated components.

 * @version $Id: UncorrelatedRandomVectorGenerator.java 1705 2006-09-17 19:57:39Z luc $
 * @author L. Maisonobe

 */

public class UncorrelatedRandomVectorGenerator
  implements Serializable, RandomVectorGenerator {

  /** Simple constructor.
   * <p>Build an uncorrelated random vector generator from its mean
   * and standard deviation vectors.</p>
   * @param mean expected mean values for all components
   * @param standardDeviation standard deviation for all components
   * @param generator underlying generator for uncorrelated normalized
   * components
   * @exception IllegalArgumentException if there is a dimension
   * mismatch between the mean and standard deviation vectors
   */
  public UncorrelatedRandomVectorGenerator(double[] mean,
                                           double[] standardDeviation,
                                           NormalizedRandomGenerator generator) {

    if (mean.length != standardDeviation.length) {
      throw new IllegalArgumentException("dimension mismatch");
    }
    this.mean              = mean;
    this.standardDeviation = standardDeviation;

    this.generator = generator;
    random = new double[mean.length];

  }

  /** Simple constructor.
   * <p>Build a null mean random and unit standard deviation
   * uncorrelated vector generator</p>
   * @param dimension dimension of the vectors to generate
   * @param generator underlying generator for uncorrelated normalized
   * components
   */
  public UncorrelatedRandomVectorGenerator(int dimension,
                                           NormalizedRandomGenerator generator) {

    mean              = new double[dimension];
    standardDeviation = new double[dimension];
    for (int i = 0; i < dimension; ++i) {
      mean[i]              = 0;
      standardDeviation[i] = 1;
    }

    this.generator = generator;
    random = new double[dimension];

  }

  /** Get the underlying normalized components generator.
   * @return underlying uncorrelated components generator
   */
  public NormalizedRandomGenerator getGenerator() {
    return generator;
  }

  /** Generate a correlated random vector.
   * @return a random vector as an array of double. The generator
   * <em>will</em> reuse the same array for each call, in order to
   * save the allocation time, so the user should keep a copy by
   * himself if he needs so.
   */
  public double[] nextVector() {

    for (int i = 0; i < random.length; ++i) {
      random[i] = mean[i] + standardDeviation[i] * generator.nextDouble();
    }

    return random;

  }

  /** Mean vector. */
  private double[] mean;

  /** Standard deviation vector. */
  private double[] standardDeviation;

  /** Underlying scalar generator. */
  NormalizedRandomGenerator generator;

  /** Storage for the random vector. */
  private double[] random;

  private static final long serialVersionUID = -3323293740860311151L;

}
