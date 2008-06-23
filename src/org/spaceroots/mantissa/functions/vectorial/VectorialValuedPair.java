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

/** This class represents an (x, f(x)) pair for vectorial functions.

 * <p>A vectorial function is a function of one vectorial parameter x whose
 * value is a vector. This class is used has a simple immutable placeholder to
 * contain both an abscissa and the value of the function at this
 * abscissa.</p>

 * @see SampledFunction
 * @see org.spaceroots.mantissa.functions.vectorial.VectorialValuedPair

 * @version $Id$
 * @author L. Maisonobe

 */
public class VectorialValuedPair
  implements Serializable {

  /**
   * Simple constructor.
   * Build an instance from its coordinates
   * @param x abscissa
   * @param y ordinate (value of the function)
   */
  public VectorialValuedPair(double x, double[] y) {
    this.x = x;
    this.y = (double[]) y.clone();
  }

  /** Abscissa of the point. */
  public final double x;

  /** Vectorial ordinate of the point, y = f (x). */
  public final double[] y;

  private static final long serialVersionUID = -7397116933564410103L;

}
