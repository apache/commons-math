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

package org.apache.commons.math.analysis.solvers;

/** Interface for {@link UnivariateRealSolver (univariate real) root-finding
 * algorithms} that maintain a bracketed solution. There are several advantages
 * to having such root-finding algorithms:
 * <ul>
 *  <li>The bracketed solution guarantees that the root is kept within the
 *      interval. As such, these algorithms generally also guarantee
 *      convergence.</li>
 *  <li>The bracketed solution means that we have the opportunity to only
 *      return roots that are greater than or equal to the actual root, or
 *      are less than or equal to the actual root. That is, we can control
 *      whether under-approximations and over-approximations are
 *      {@link AllowedSolutions allowed solutions}. Other root-finding
 *      algorithms can usually only guarantee that the solution (the root that
 *      was found) is around the actual root.</li>
 * </ul>
 *
 * <p>For backwards compatibility, all root-finding algorithms must have
 * {@link AllowedSolutions#EITHER_SIDE EITHER_SIDE} as default for the allowed
 * solutions.</p>
 *
 * @see AllowedSolutions
 * @since 3.0
 * @version $Id$
 */
public interface BracketedUnivariateRealSolver extends UnivariateRealSolver {
    /** Returns the kind of solutions that the root-finding algorithm may
     * accept as solutions.
     *
     * @return the kind of solutions that the root-finding algorithm may
     * accept as solutions
     */
    AllowedSolutions getAllowedSolutions();

    /** Sets the kind of solutions that the root-finding algorithm may accept
     * as solutions.
     *
     * @param allowedSolutions the kind of solutions that the root-finding
     * algorithm may accept as solutions
     */
    void setAllowedSolutions(AllowedSolutions allowedSolutions);
}
