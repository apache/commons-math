/*
 * Copyright 2017 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
 * {@code log(1 + x) / x }
 *
 * @since
 */
public class Log1pOverIdentity implements UnivariateDifferentiableFunction {

    @Override
    public double value(double x) {
        if (FastMath.abs(x) > 1e-8) {
            return FastMath.log1p(x) / x;
        } else {
            return 1.-x*((1./2.)-x*((1./3.)-x*(1./4.)));
        }
    }

    @Override
    public DerivativeStructure value(DerivativeStructure t) {
        return t.log1p().multiply(t.reciprocal());
    }

}
