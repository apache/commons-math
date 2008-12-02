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
 * This class is a simple container for an offset and an
 * {@link ArraySliceMappable} object.

 * @version $Id$
 * @author L. Maisonobe

 */

class ArrayMapperEntry {

  /** Mappable object. */
  public final ArraySliceMappable object;

  /** Offset from start of array. */
  public final int offset;

  /** Simple constructor.
   * @param object mappable object
   * @param offset offset from start of array
   */
  public ArrayMapperEntry(ArraySliceMappable object, int offset) {
    this.object = object;
    this.offset = offset;
  }

}
