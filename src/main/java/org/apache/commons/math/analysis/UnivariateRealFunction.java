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
package org.apache.commons.math.analysis;

import org.apache.commons.math.exception.MathUserException;

/**
 * An interface representing a univariate real function.
 *
 * @version $Revision$ $Date$
 */
public interface UnivariateRealFunction {
    /**
     * Compute the value of the function.
     *
     * @param x Point at which the function value should be computed.
     * @return the value.
     * @throws IllegalArgumentException when the activated method itself can
     * ascertain that preconditions, specified in the API expressed at the
     * level of the activated method, have been violated.  In the vast
     * majority of cases where Commons-Math throws IllegalArgumentException,
     * it is the result of argument checking of actual parameters immediately
     * passed to a method.
     * @throws MathUserException when the method may encounter errors during evaluation.
     * This should be thrown only in circumstances where, at the level of the
     * activated function, IllegalArgumentException is not appropriate and it
     * should indicate that while formal preconditions of the method have not
     * been violated, an irrecoverable error has occurred evaluating a
     * function at some (usually lower) level of the call stack.
     * Convergence failures, runtime exceptions (even IllegalArgumentException)
     * in user code or lower level methods can cause (and should be wrapped in)
     * a MathUserException.
     */
    double value(double x) throws MathUserException;
}
