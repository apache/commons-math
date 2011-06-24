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

/**
 * Base class for <em>Secant</em> methods that guarantee convergence
 * by maintaining a {@link BracketedSolution bracketed solution}.
 *
 * @since 3.0
 * @version $Id$
 */
public class BaseBracketedSecantSolver extends BaseSecantSolver
    implements BracketedSolution {
    /**
     * Construct a solver with default accuracy (1e-6).
     *
     * @param method Method.
     */
    protected BaseBracketedSecantSolver(Method method) {
        super(DEFAULT_ABSOLUTE_ACCURACY, method);
    }

    /**
     * Construct a solver.
     *
     * @param absoluteAccuracy absolute accuracy
     * @param method Method.
     */
    protected BaseBracketedSecantSolver(final double absoluteAccuracy,
                                        Method method) {
        super(absoluteAccuracy, method);
    }

    /**
     * Construct a solver.
     *
     * @param relativeAccuracy relative accuracy
     * @param absoluteAccuracy absolute accuracy
     * @param method Method.
     */
    protected BaseBracketedSecantSolver(final double relativeAccuracy,
                                        final double absoluteAccuracy,
                                        Method method) {
        super(relativeAccuracy, absoluteAccuracy, method);
    }

    /** {@inheritDoc} */
    public AllowedSolutions getAllowedSolutions() {
        return allowedSolutions;
    }

    /** {@inheritDoc} */
    public void setAllowedSolutions(final AllowedSolutions allowedSolutions) {
        this.allowedSolutions = allowedSolutions;
    }
}
