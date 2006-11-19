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

import java.util.Random;

/** This class is a gaussian normalized random generator
 * for scalars.

 * <p>This class is a simple interface adaptor around the {@link
 * java.util.Random#nextGaussian nextGaussian} method.</p>

 * @version $Id: GaussianRandomGenerator.java 1705 2006-09-17 19:57:39Z luc $
 * @author L. Maisonobe

 */

public class GaussianRandomGenerator
  implements NormalizedRandomGenerator {

  /** Underlying generator. */
  Random generator;

  /** Create a new generator.
   * The seed of the generator is related to the current time.
   */
  public GaussianRandomGenerator() {
    generator = new Random();
  }

  /** Creates a new random number generator using a single int seed.
   * @param seed the initial seed (32 bits integer)
   */
  public GaussianRandomGenerator(int seed) {
    generator = new Random(seed);
  }

  /** Create a new generator initialized with a single long seed.
   * @param seed seed for the generator (64 bits integer)
   */
  public GaussianRandomGenerator(long seed) {
    generator = new Random(seed);
  }

  /** Generate a random scalar with null mean and unit standard deviation.
   * @return a random scalar with null mean and unit standard deviation
   */
  public double nextDouble() {
    return generator.nextGaussian();
  }

}
