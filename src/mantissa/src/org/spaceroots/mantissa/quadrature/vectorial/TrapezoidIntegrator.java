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

/** This class implements a trapezoid integrator.

 * <p>A trapezoid integrator is a very simple one that assumes the
 * function is linear over the integration step.</p>

 * @version $Id$
 * @author L. Maisonobe

 */

public class TrapezoidIntegrator
  implements SampledFunctionIntegrator {
  public double[] integrate(SampledFunctionIterator iter)
    throws ExhaustedSampleException, FunctionException {

    TrapezoidIntegratorSampler sampler =
      new TrapezoidIntegratorSampler(iter);
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
