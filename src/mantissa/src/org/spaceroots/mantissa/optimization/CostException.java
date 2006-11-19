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

package org.spaceroots.mantissa.optimization;

import org.spaceroots.mantissa.MantissaException;

/** This class represents exceptions thrown by cost functions.

 * @version $Id: CostException.java 1705 2006-09-17 19:57:39Z luc $
 * @author L. Maisonobe

 */

public class CostException
  extends MantissaException {

  /** Simple constructor.
   * Build an exception with a default message
   */
  public CostException() {
    super("cost exception");
  }

  /** Simple constructor.
   * Build an exception with the specified message
   * @param message exception message
   */
  public CostException(String message) {
    super(message);
  }

  /** Simple constructor.
   * Build an exception from a cause
   * @param cause cause of this exception
   */
  public CostException(Throwable cause) {
    super(cause);
  }

  /** Simple constructor.
   * Build an exception from a message and a cause
   * @param message exception message
   * @param cause cause of this exception
   */
  public CostException(String message, Throwable cause) {
    super(message, cause);
  }

  private static final long serialVersionUID = -6099968585593678071L;

}
