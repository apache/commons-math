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

package org.apache.commons.math4.legacy.analysis.function;

import org.apache.commons.math4.legacy.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math4.legacy.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math4.core.jdkmath.JdkMath;

/**
 * Sine function.
 *
 * @since 3.0
 */
public class Sin implements UnivariateDifferentiableFunction {
    /** {@inheritDoc} */
    @Override
    public double value(double x) {
        return JdkMath.sin(x);
    }

    /** {@inheritDoc}
     * @since 3.1
     */
    @Override
    public DerivativeStructure value(final DerivativeStructure t) {
        return t.sin();
    }
}
