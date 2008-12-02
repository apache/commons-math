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

import org.spaceroots.mantissa.functions.scalar.ComputableFunction;
import org.spaceroots.mantissa.functions.FunctionException;

/** This interface represents an integrator for scalar functions.

 * <p>The classes which are devoted to integrate scalar functions
 * should implement this interface. The functions which can be handled
 * should implement the {@link
 * org.spaceroots.mantissa.functions.scalar.ComputableFunction
 * ComputableFunction} interface.</p>

 * @see org.spaceroots.mantissa.functions.scalar.ComputableFunction

 * @version $Id$
 * @author L. Maisonobe

 */

public interface ComputableFunctionIntegrator {
  /** Integrate a function over a defined range.
   * @param f function to integrate
   * @param a first bound of the range (can be lesser or greater than b)
   * @param b second bound of the range (can be lesser or greater than a)
   * @return value of the integral over the range
   * @exception FunctionException if the underlying function throws one
   */
  public double integrate(ComputableFunction f, double a, double b)
    throws FunctionException;

}
