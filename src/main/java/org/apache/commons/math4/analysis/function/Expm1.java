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

package org.apache.commons.math4.analysis.function;

import org.apache.commons.math4.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math4.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math4.util.FastMath;

/**
 * <code>e<sup>x</sup>-1</code> function.
 *
 * @since 3.0
 */
public class Expm1 implements UnivariateDifferentiableFunction {
    /** {@inheritDoc} */
    @Override
    public double value(double x) {
        return FastMath.expm1(x);
    }

    /** {@inheritDoc}
     * @since 3.1
     */
    @Override
    public DerivativeStructure value(final DerivativeStructure t) {
        return t.expm1();
    }

}
