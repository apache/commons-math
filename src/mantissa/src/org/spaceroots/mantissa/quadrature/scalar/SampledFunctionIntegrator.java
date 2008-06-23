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

import org.spaceroots.mantissa.functions.scalar.SampledFunctionIterator;
import org.spaceroots.mantissa.functions.ExhaustedSampleException;
import org.spaceroots.mantissa.functions.FunctionException;

/** This interface represents an integrator for scalar samples.

 * <p>The classes which are devoted to integrate scalar samples
 * should implement this interface.</p>

 * @see org.spaceroots.mantissa.functions.scalar.SampledFunctionIterator
 * @see ComputableFunctionIntegrator

 * @version $Id$
 * @author L. Maisonobe

 */

public interface SampledFunctionIntegrator {
  /** Integrate a sample over its overall range
   * @param iter iterator over the sample to integrate
   * @return value of the integral over the sample range
   * @exception ExhaustedSampleException if the sample does not have
   * enough points for the integration scheme
   * @exception FunctionException if the underlying sampled function throws one
   */
  public double integrate(SampledFunctionIterator iter)
    throws ExhaustedSampleException, FunctionException;

}
