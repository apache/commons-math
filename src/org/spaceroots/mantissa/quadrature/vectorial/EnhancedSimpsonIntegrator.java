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

import org.spaceroots.mantissa.functions.FunctionException;
import org.spaceroots.mantissa.functions.ExhaustedSampleException;
import org.spaceroots.mantissa.functions.vectorial.SampledFunctionIterator;

/** This class implements an enhanced Simpson-like integrator.

 * <p>A traditional Simpson integrator is based on a quadratic
 * approximation of the function on three equally spaced points. This
 * integrator does the same thing but can handle non-equally spaced
 * points. If it is used on a regular sample, it behaves exactly as a
 * traditional Simpson integrator.</p>

 * @version $Id$
 * @author L. Maisonobe

 */

public class EnhancedSimpsonIntegrator
  implements SampledFunctionIntegrator {
  public double[] integrate(SampledFunctionIterator iter)
    throws ExhaustedSampleException, FunctionException {

    EnhancedSimpsonIntegratorSampler sampler =
      new EnhancedSimpsonIntegratorSampler(iter);
    double[] sum = null;

    try {
      while (true) {
        sum = sampler.nextSamplePoint().y;
      }
    } catch(ExhaustedSampleException e) {
    }

    return sum;

  }

}
