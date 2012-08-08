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
package org.apache.commons.math3.analysis.differentiation;

import org.apache.commons.math3.analysis.UnivariateFunction;

/** Interface for univariate functions derivatives.
 * <p>This interface represents a simple function which computes
 * both the value and the first derivative of a mathematical function.
 * The derivative is computed with respect to the input variable.</p>
 * @see UnivariateDifferentiable
 * @see UnivariateDifferentiator
 * @since 3.1
 * @version $Id$
 */
public interface UnivariateDifferential {

    /** Get the primitive function associated with this differential.
     * <p>Each {@link UnivariateDifferential} instance is tightly bound
     * to an {@link UnivariateDifferentiable} instance. If the state of
     * the primitive instance changes in any way that affects the
     * differential computation, this binding allows this change to
     * be immediately seen by the derivative instance, there is no need
     * to differentiate the primitive again. The existing instance is aware
     * of the primitive changes.</p>
     * <p>In other words in the following code snippet, the three values
     * f1, f2 and f3 should be equal (at least at machine tolerance level)</p>
     * <pre>
     *    UnivariateDifferential derivative = differentiator.differentiate(derivable);
     *    derivable.someFunctionThatMutatesHeavilyTheInstance();
     *    double f1 = derivable.f(t);
     *    double f2 = derivative.getPrimitive().f(t);
     *    double f3 = derivative.f(new DerivativeStructure(variables, order, index, t)).getValue();
     * </pre>
     * @return primitive function bound to this derivative
     */
    UnivariateFunction getPrimitive();

    /** Simple mathematical function.
     * <p>{@link UnivariateDifferential} classes compute both the
     * value and the first derivative of the function.</p>
     * @param t function input value
     * @return function result
     */
    DerivativeStructure f(DerivativeStructure t);

}
