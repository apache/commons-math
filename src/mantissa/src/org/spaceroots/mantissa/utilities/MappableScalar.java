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

package org.spaceroots.mantissa.utilities;

/**
 * Wrapper class around a scalar in order to have it implement the
 * {@link ArraySliceMappable} interface.

 * @version $Id$
 * @author L. Maisonobe

 */

public class MappableScalar
  implements ArraySliceMappable {

  /** Simple constructor.
   * Build a mappable scalar
   */
  public MappableScalar() {
    value = 0;
  }

  /** Simple constructor.
   * Build a mappable scalar from its initial value
   * @param value initial value of the scalar
   */
  public MappableScalar(double value) {
    this.value = value;
  }

  /** Get the value stored in the instance.
   * @return value stored in the instance
   */
  public double getValue () {
    return value;
  }

  /** Set the value stored in the instance.
   * @param value value to store in the instance
   */
  public void setValue (double value) {
    this.value = value;
  }

  /** Get the dimension of the internal array.
   * @return dimension of the array (always 1 for this class)
   */
  public int getStateDimension() {
    return 1;
  }
    
  /** Reinitialize internal state from the specified array slice data.
   * @param start start index in the array
   * @param array array holding the data to extract
   */
  public void mapStateFromArray(int start, double[] array) {
    value = array[start];
  }
    
  /** Store internal state data into the specified array slice.
   * @param start start index in the array
   * @param array array where data should be stored
   */
  public void mapStateToArray(int start, double[] array) {
    array[start] = value;
  }

  /** Internal scalar.
   */
  double value;

}
