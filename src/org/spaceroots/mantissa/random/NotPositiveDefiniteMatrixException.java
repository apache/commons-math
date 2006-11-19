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

package org.spaceroots.mantissa.random;

import org.spaceroots.mantissa.MantissaException;

/** This class represents exceptions thrown by the correlated random
 * vector generator.

 * @version $Id: NotPositiveDefiniteMatrixException.java 1705 2006-09-17 19:57:39Z luc $
 * @author L. Maisonobe

 */

public class NotPositiveDefiniteMatrixException
  extends MantissaException {

  /** Simple constructor.
   * build an exception with a default message.
   */
  public NotPositiveDefiniteMatrixException() {
    super("not positive definite matrix");
  }

  /** Simple constructor.
   * build an exception with the specified message.
   * @param message message to use to build the exception
   */
  public NotPositiveDefiniteMatrixException(String message) {
    super(message);
  }

  private static final long serialVersionUID = -6801349873804445905L;

}
