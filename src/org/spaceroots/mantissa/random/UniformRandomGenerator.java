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

/** This class implements a normalized uniform random generator.

 * <p>Since this is a normalized random generator, it has a null mean
 * and a unit standard deviation. Being also a uniform
 * generator, it produces numbers in the range [-sqrt(3) ;
 * sqrt(3)].</p>

 * @version $Id: UniformRandomGenerator.java 1705 2006-09-17 19:57:39Z luc $
 * @author L. Maisonobe

 */

public class UniformRandomGenerator
  implements NormalizedRandomGenerator {

  private static final double SQRT3 = Math.sqrt(3.0);

  private static final double TWOSQRT3 = 2.0 * Math.sqrt(3.0);

  /** Underlying generator. */
  Random generator;

  /** Create a new generator.
   * The seed of the generator is related to the current time.
   */
  public UniformRandomGenerator() {
    generator = new Random();
  }

  /** Creates a new random number generator using a single int seed.
   * @param seed the initial seed (32 bits integer)
   */
  public UniformRandomGenerator(int seed) {
    generator = new Random(seed);
  }

  /** Create a new generator initialized with a single long seed.
   * @param seed seed for the generator (64 bits integer)
   */
  public UniformRandomGenerator(long seed) {
    generator = new Random(seed);
  }

  /** Generate a random scalar with null mean and unit standard deviation.
   * <p>The number generated is uniformly distributed between -sqrt(3)
   * and sqrt(3).</p>
   * @return a random scalar with null mean and unit standard deviation
   */
  public double nextDouble() {
    return TWOSQRT3 * generator.nextDouble() - SQRT3;
  }

}
