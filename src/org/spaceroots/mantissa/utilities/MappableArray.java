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
 * Wrapper class around an array in order to have it implement the
 * {@link ArraySliceMappable} interface.

 * @version $Id$
 * @author L. Maisonobe

 */

public class MappableArray
  implements ArraySliceMappable {

  /** Simple constructor.
   * Build a mappable array from its dimension
   * @param dimension dimension of the array
   */
  public MappableArray(int dimension) {
    internalArray = new double[dimension];
    for (int i = 0; i < dimension; ++i) {
      internalArray[i] = 0;
    }
  }

  /** Simple constructor.
   * Build a mappable array from an existing array
   * @param array array to use
   */
  public MappableArray(double[] array) {
    internalArray = (double[]) array.clone();
  }

  /** Get the array stored in the instance.
   * @return array stored in the instance
   */
  public double[] getArray () {
    return (double[]) internalArray.clone();
  }

  /** Get the dimension of the internal array.
   * @return dimension of the array
   */
  public int getStateDimension() {
    return internalArray.length;
  }
    
  /** Reinitialize internal state from the specified array slice data.
   * @param start start index in the array
   * @param array array holding the data to extract
   */
  public void mapStateFromArray(int start, double[] array) {
    System.arraycopy(array, start, internalArray, 0, internalArray.length);
  }
    
  /** Store internal state data into the specified array slice.
   * @param start start index in the array
   * @param array array where data should be stored
   */
  public void mapStateToArray(int start, double[] array) {
    System.arraycopy(internalArray, 0, array, start, internalArray.length);
  }

  /** Internal array holding all data.
   */
  double[] internalArray;

}
