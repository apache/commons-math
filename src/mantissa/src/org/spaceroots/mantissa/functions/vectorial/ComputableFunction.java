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

/** This interface represents vectorial functions of one real variable.

 * <p>This interface should be implemented by all vectorial functions
 * that can be evaluated at any point. This does not imply that an
 * explicit definition is available, a function given by an implicit
 * function that should be numerically solved for each point for
 * example is considered a computable function.</p>

 * <p>The {@link ComputableFunctionSampler} class can be used to
 * transform classes implementing this interface into classes
 * implementing the {@link SampledFunction} interface.</p>

 * <p>Several numerical algorithms (Gauss-Legendre integrators for
 * example) need to choose themselves the evaluation points, so they
 * can handle only objects that implement this interface.</p>

 * @see org.spaceroots.mantissa.quadrature.vectorial.ComputableFunctionIntegrator
 * @see SampledFunction

 * @version $Id$
 * @author L. Maisonobe

 */
public interface ComputableFunction extends Serializable {
  /** Get the dimension of the vectorial values of the function.
   * @return dimension
   */
  public int getDimension();

  /** Get the value of the function at the specified abscissa.
   * @param x current abscissa
   * @return function value
   * @exception FunctionException if something goes wrong
   */
  public double[] valueAt(double x)
    throws FunctionException;

}
