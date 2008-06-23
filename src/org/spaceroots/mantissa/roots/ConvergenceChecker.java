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

package org.spaceroots.mantissa.roots;

/** This interface specifies methods to check if a root-finding
 * algorithm has converged.

 * Deciding if convergence has been reached is a problem-dependent
 * issue. The user should provide a class implementing this interface
 * to allow the root-finding algorithm to stop its search according to
 * the problem at hand.

 * @version $Id$
 * @author L. Maisonobe

 */

public interface ConvergenceChecker {

  /** Indicator for no convergence. */
  public static final int NONE = 0;

  /** Indicator for convergence on the lower bound of the interval. */
  public static final int LOW  = 1;

  /** Indicator for convergence on the higher bound of the interval. */
  public static final int HIGH = 2;

  /** Check if the root-finding algorithm has converged on the interval.
   * The interval defined by the arguments contains one root (if there
   * was at least one in the initial interval given by the user to the
   * root-finding algorithm, of course)
   * @param xLow abscissa of the lower bound of the interval
   * @param fLow value of the function the lower bound of the interval
   * @param xHigh abscissa of the higher bound of the interval
   * @param fHigh value of the function the higher bound of the interval
   * @return convergence indicator, must be one of {@link #NONE},
   * {@link #LOW} or {@link #HIGH}
   */
  public int converged (double xLow, double fLow, double xHigh, double fHigh);

}
