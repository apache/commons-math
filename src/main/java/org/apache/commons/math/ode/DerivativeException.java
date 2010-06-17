/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.math.ode;

import org.apache.commons.math.MathException;
import org.apache.commons.math.util.DummyLocalizable;
import org.apache.commons.math.util.Localizable;

/**
 * This exception is made available to users to report
 * the error conditions that are triggered while computing
 * the differential equations.
 * @version $Revision$ $Date$
 * @since 1.2
 */
public class DerivativeException
  extends MathException {

  /** Serializable version identifier */
  private static final long serialVersionUID = 5666710788967425123L;

  /** Simple constructor.
   * Build an exception by translating and formating a message
   * @param specifier format specifier (to be translated)
   * @param parts to insert in the format (no translation)
   * @deprecated as of 2.2 replaced by {@link #DerivativeException(Localizable, Object...)}
   */
  @Deprecated
  public DerivativeException(final String specifier, final Object ... parts) {
    this(new DummyLocalizable(specifier), parts);
  }

  /** Simple constructor.
   * Build an exception by translating and formating a message
   * @param specifier format specifier (to be translated)
   * @param parts to insert in the format (no translation)
   * @since 2.2
   */
  public DerivativeException(final Localizable specifier, final Object ... parts) {
    super(specifier, parts);
  }

 /** Build an instance from an underlying cause.
   * @param cause cause for the exception
   */
  public DerivativeException(final Throwable cause) {
    super(cause);
  }

}
