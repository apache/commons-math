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
package org.apache.commons.math.linear;

import org.apache.commons.math.util.IterationEvent;

/**
 * This is the base class for all events occuring during the iterations of a
 * {@link IterativeLinearSolver}.
 *
 * @version $Id$
 * @since 3.0
 */
public abstract class IterativeLinearSolverEvent
    extends IterationEvent {

    /** */
    private static final long serialVersionUID = 283291016904748030L;

    /**
     * Creates a new instance of this class.
     *
     * @param source The iterative algorithm on which the event initially
     *        occurred.
     */
    public IterativeLinearSolverEvent(final Object source) {
        super(source);
    }

    /**
     * Returns the current right-hand side of the linear system to be solved.
     * This method should return an unmodifiable view, or a deep copy of the
     * actual right-hand side, in order not to compromise subsequent iterations
     * of the source {@link IterativeLinearSolver}.
     *
     * @return The right-hand side vector, b.
     */
    public abstract RealVector getRightHandSideVector();

    /**
     * Returns the current estimate of the solution to the linear system to be
     * solved. This method should return an unmodifiable view, or a deep copy of
     * the actual current solution, in order not to compromise subsequent
     * iterations of the source {@link IterativeLinearSolver}.
     *
     * @return The solution, x.
     */
    public abstract RealVector getSolution();
}
