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

package org.spaceroots.mantissa.quadrature.vectorial;

import org.spaceroots.mantissa.functions.vectorial.*;
import org.spaceroots.mantissa.functions.FunctionException;
import org.spaceroots.mantissa.functions.ExhaustedSampleException;

/** This class implements a trapezoid integrator as a sample.

 * <p>A trapezoid integrator is a very simple one that assumes the
 * function is constant over the integration step. Since it is very
 * simple, this algorithm needs very small steps to achieve high
 * accuracy, and small steps lead to numerical errors and
 * instabilities.</p>

 * <p>This algorithm is almost never used and has been included in
 * this package only as a simple template for more useful
 * integrators.</p>

 * @see TrapezoidIntegrator

 * @version $Id$
 * @author L. Maisonobe

 */

public class TrapezoidIntegratorSampler
  implements SampledFunctionIterator {

  /** Underlying sample iterator. */
  private SampledFunctionIterator iter;

  /** Current point. */
  private VectorialValuedPair current;

  /** Current running sum. */
  private double[] sum;

  /** Constructor.
   * Build an integrator from an underlying sample iterator.
   * @param iter iterator over the base function
   */
  public TrapezoidIntegratorSampler(SampledFunctionIterator iter)
    throws ExhaustedSampleException, FunctionException {

    this.iter = iter;

    // get the first point
    current = iter.nextSamplePoint();

    // initialize the sum
    sum = new double[iter.getDimension()];
    for (int i = 0; i < sum.length; ++i) {
      sum[i] = 0.0;
    }

  }

  public boolean hasNext() {
    return iter.hasNext();
  }

  public int getDimension() {
    return iter.getDimension();
  }

  public VectorialValuedPair nextSamplePoint()
    throws ExhaustedSampleException, FunctionException {

    // performs one step of a trapezoid scheme
    VectorialValuedPair previous = current;
    current = iter.nextSamplePoint();

    double halfDx = 0.5 * (current.x - previous.x);
    double[] pY = previous.y;
    double[] cY = current.y;
    for (int i = 0; i < sum.length; ++i) {
      sum[i] += halfDx * (pY[i] + cY[i]);
    }

    return new VectorialValuedPair (current.x, (double[]) sum.clone());

  }

}
