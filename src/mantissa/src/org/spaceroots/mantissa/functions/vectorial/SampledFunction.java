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

import java.io.Serializable;

import org.spaceroots.mantissa.functions.FunctionException;

/** This interface represent sampled vectorial functions.

 * <p>A function sample is an ordered set of points of the form (x, y)
 * where x is the abscissa of the point and y is the function value at
 * x. It is typically a function that has been computed by external
 * means or the result of measurements.</p>

 * <p>The {@link ComputableFunctionSampler} class can be used to
 * transform classes implementing the {@link ComputableFunction}
 * interface into classes implementing this interface.</p>

 * <p>Sampled functions cannot be directly handled by integrators
 * implementing the {@link
 * org.spaceroots.mantissa.quadrature.vectorial.SampledFunctionIntegrator
 * SampledFunctionIntegrator}. These integrators need a {@link
 * SampledFunctionIterator} object to iterate over the
 * sample.</p>

 * @see SampledFunctionIterator
 * @see ComputableFunctionSampler
 * @see ComputableFunction

 * @version $Id$
 * @author L. Maisonobe

 */
public interface SampledFunction extends Serializable {

  /** Get the number of points in the sample.
   * @return number of points in the sample
   */
  public int size();

  /** Get the dimension of the vectorial values of the function.
   * @return dimension
   */
  public int getDimension();

  /** Get the abscissa and value of the sample at the specified index.
   * @param index index in the sample, should be between 0 and
   * {@link #size} - 1
   * @return abscissa and value of the sample at the specified index
   * @exception ArrayIndexOutOfBoundsException if the index is wrong
   * @exception FunctionException if an eventual underlying function
   * throws one
   */
  public VectorialValuedPair samplePointAt(int index)
    throws ArrayIndexOutOfBoundsException, FunctionException;

}
