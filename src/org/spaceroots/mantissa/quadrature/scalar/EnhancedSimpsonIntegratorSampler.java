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

package org.spaceroots.mantissa.quadrature.scalar;

import org.spaceroots.mantissa.functions.scalar.*;
import org.spaceroots.mantissa.functions.FunctionException;
import org.spaceroots.mantissa.functions.ExhaustedSampleException;

/** This class implements an enhanced Simpson integrator as a sample.

 * <p>A traditional Simpson integrator is based on a quadratic
 * approximation of the function on three equally spaced points. This
 * integrator does the same thing but can handle non-equally spaced
 * points. If it is used on a regular sample, it behaves exactly as a
 * traditional Simpson integrator.</p>

 * @see EnhancedSimpsonIntegrator

 * @version $Id$
 * @author L. Maisonobe

 */

public class EnhancedSimpsonIntegratorSampler
  implements SampledFunctionIterator {

  /** Underlying sampled function iterator. */
  private SampledFunctionIterator iter;

  /** Next point. */
  private ScalarValuedPair next;

  /** Current running sum. */
  private double sum;

  /** Constructor.
   * Build an integrator from an underlying sample iterator.
   * @param iter iterator over the base function
   */
  public EnhancedSimpsonIntegratorSampler(SampledFunctionIterator iter)
    throws ExhaustedSampleException, FunctionException {

    this.iter = iter;

    // get the first point
    next = iter.nextSamplePoint();

    // initialize the sum
    sum = 0.0;

  }

  public boolean hasNext() {
    return iter.hasNext();
  }

  public ScalarValuedPair nextSamplePoint()
    throws ExhaustedSampleException, FunctionException {
    // performs one step of an enhanced Simpson scheme
    ScalarValuedPair previous = next;
    ScalarValuedPair current  = iter.nextSamplePoint();

    try {
      next = iter.nextSamplePoint();

      double h1 = current.getX() - previous.getX();
      double h2 = next.getX()    - current.getX();
      double cP = (h1 + h2) * (2 * h1 - h2) / (6 * h1);
      double cC = (h1 + h2) * (h1 + h2) * (h1 + h2) / (6 * h1 * h2);
      double cN = (h1 + h2) * (2 * h2 - h1) / (6 * h2);

      sum += cP * previous.getY() + cC * current.getY() + cN * next.getY();

    } catch(ExhaustedSampleException e) {
      // we have an incomplete step at the end of the sample
      // we use a trapezoid scheme for this last step
      sum += 0.5 * (current.getX() - previous.getX()) * (previous.getY() + current.getY());
      return new ScalarValuedPair(current.getX(), sum);
    }

    return new ScalarValuedPair(next.getX(), sum);

  }

}
