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

package org.apache.commons.math4.analysis.solvers;

import org.apache.commons.math4.analysis.solvers.SecantSolver;
import org.apache.commons.math4.analysis.solvers.UnivariateSolver;

/**
 * Test case for {@link SecantSolver Secant} solver.
 *
 */
public final class SecantSolverTest extends BaseSecantSolverAbstractTest {
    /** {@inheritDoc} */
    @Override
    protected UnivariateSolver getSolver() {
        return new SecantSolver();
    }

    /** {@inheritDoc} */
    @Override
    protected int[] getQuinticEvalCounts() {
        // As the Secant method does not maintain a bracketed solution,
        // convergence is not guaranteed. Two test cases are disabled (-1) due
        // to bad solutions.
        return new int[] {3, 7, -1, 8, 9, 8, 11, 12, 14, -1, 16};
    }
}
