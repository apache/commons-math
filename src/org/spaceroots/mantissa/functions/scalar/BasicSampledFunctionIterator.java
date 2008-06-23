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

package org.spaceroots.mantissa.functions.scalar;

import java.io.Serializable;

import org.spaceroots.mantissa.functions.FunctionException;
import org.spaceroots.mantissa.functions.ExhaustedSampleException;

/** This class is a simple wrapper allowing to iterate over a
 * SampledFunction.

 * <p>The basic implementation of the iteration interface does not
 * perform any transformation on the sample, it only handles a loop
 * over the underlying sampled function.</p>

 * @see SampledFunction

 * @version $Id$
 * @author L. Maisonobe

 */
public class BasicSampledFunctionIterator
  implements SampledFunctionIterator, Serializable {

  /** Underlying sampled function. */
  private final SampledFunction function;

  /** Next sample element. */
  private int next;

  /** Simple constructor.
   * Build an instance from a SampledFunction
   * @param function smapled function over which we want to iterate
   */
  public BasicSampledFunctionIterator(SampledFunction function) {
    this.function = function;
    next          = 0;
  }

  public boolean hasNext() {
    return next < function.size();
  }

  public ScalarValuedPair nextSamplePoint()
    throws ExhaustedSampleException, FunctionException {
    if (next >= function.size()) {
      throw new ExhaustedSampleException(function.size());
    }

    int current = next++;
    return function.samplePointAt(current);

  }

  private static final long serialVersionUID = -9106690005598356403L;

}
