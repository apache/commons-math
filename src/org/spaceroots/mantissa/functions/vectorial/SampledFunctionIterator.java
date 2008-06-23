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

package org.spaceroots.mantissa.functions.vectorial;

import org.spaceroots.mantissa.functions.FunctionException;
import org.spaceroots.mantissa.functions.ExhaustedSampleException;

/** This interface provides iteration services over vectorial functions
 * samples.

 * @see SampledFunction

 * @version $Id$
 * @author L. Maisonobe

 */
public interface SampledFunctionIterator {

  /** Get the dimension of the vectorial values of the function.
   * @return dimension
   */
  public int getDimension();

  /** Check if the iterator can provide another point.
   * @return true if the iterator can provide another point.
   */
  public boolean hasNext();

  /** Get the next point of a sampled function.
   * @return the next point of the sampled function
   * @exception ExhaustedSampleException if the sample has been exhausted
   * @exception FunctionException if the underlying function throws one
   */
  public VectorialValuedPair nextSamplePoint()
    throws ExhaustedSampleException, FunctionException;

}
