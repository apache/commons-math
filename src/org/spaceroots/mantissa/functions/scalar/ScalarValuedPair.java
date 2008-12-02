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

/** This class represents an (x, f(x)) pair for scalar functions.

 * <p>A scalar function is a function of one scalar parameter x whose
 * value is a scalar. This class is used has a simple placeholder to
 * contain both an abscissa and the value of the function at this
 * abscissa.</p>

 * @see SampledFunction
 * @see org.spaceroots.mantissa.functions.vectorial.VectorialValuedPair

 * @version $Id$
 * @author L. Maisonobe

 */
public class ScalarValuedPair
  implements Serializable {

  /** Simple constructor.
   * Build an instance from its coordinates
   * @param x abscissa
   * @param y ordinate (value of the function)
   */
  public ScalarValuedPair(double x, double y) {
    this.x = x;
    this.y = y;
  }

  /** Copy-constructor.
   * @param p point to copy
   */
  public ScalarValuedPair(ScalarValuedPair p) {
    x = p.x;
    y = p.y;
  }

  /**
   * Getter for the abscissa.
   * @return value of the abscissa
   */
  public double getX() {
    return x;
  }

  /**
   * Getter for the ordinate.
   * @return value of the ordinate
   */
  public double getY() {
    return y;
  }

  /**
   * Setter for the abscissa.
   * @param x new value for the abscissa
   */
  public void setX(double x) {
    this.x = x;
  }

  /**
   * Setter for the ordinate.
   * @param y new value for the ordinate
   */
  public void setY(double y) {
    this.y = y;
  }

  /** Abscissa of the point. */
  private double x;

  /** Scalar ordinate of the point, y = f (x). */
  private double y;

  private static final long serialVersionUID = 1884346552569300794L;

}
