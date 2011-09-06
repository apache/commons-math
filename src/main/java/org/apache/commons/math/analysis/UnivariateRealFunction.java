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

/**
 * An interface representing a univariate real function.
 *
 * @version $Id$
 */
public interface UnivariateRealFunction {
    /**
     * Compute the value of the function.
     *
     * <p>
     * For user-defined functions, when the method encounters an error
     * during evaluation, users must use their <em>own</em> unchecked exceptions.
     * The following example shows the recommended way to do that, using root
     * solving as the example (the same construct should be used for ODE
     * integrators or for optimizations).
     * </p>
     * <pre>
     * private static class LocalException extends RuntimeException {
     *
     *   // the x value that caused the problem
     *   private final double x;
     *
     *   public LocalException(double x) {
     *     this.x = x;
     *   }
     *
     *   public double getX() {
     *     return x;
     *   }
     *
     * }
     *
     * private static class MyFunction implements UnivariateRealFunction {
     *   public double value(double x) {
     *     double y = hugeFormula(x);
     *     if (somethingBadHappens) {
     *       throw new LocalException(x);
     *     }
     *     return y;
     *   }
     * }
     *
     * public void compute() {
     *   try {
     *     solver.solve(maxEval, new MyFunction(a, b, c), min, max);
     *   } catch (LocalException le) {
     *     // retrieve the x value
     *   }
     * }
     * </pre>
     *
     * <p>
     * As shown in this example the exception is really something local to user code
     * and there is a guarantee Apache Commons Math will not mess with it. The user is safe.
     * </p>
     * @param x Point at which the function value should be computed.
     * @return the value.
     * @throws IllegalArgumentException when the activated method itself can
     * ascertain that preconditions, specified in the API expressed at the
     * level of the activated method, have been violated.  In the vast
     * majority of cases where Commons-Math throws IllegalArgumentException,
     * it is the result of argument checking of actual parameters immediately
     * passed to a method.
     */
    double value(double x);
}
