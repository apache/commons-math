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

package org.apache.commons.math4.legacy.ode;

import org.apache.commons.math4.legacy.core.Field;
import org.apache.commons.math4.legacy.core.RealFieldElement;
import org.apache.commons.math4.legacy.core.MathArrays;

/**
 * This class is used in the junit tests for the ODE integrators.

 * <p>This specific problem is the following differential equation :
 * <pre>
 *    y' = t^3 - t y
 * </pre>
 * with the initial condition y (0) = 0. The solution of this equation
 * is the following function :
 * <pre>
 *   y (t) = t^2 + 2 (exp (- t^2 / 2) - 1)
 * </pre>
 * </p>

 * @param <T> the type of the field elements
 */
public class TestFieldProblem2<T extends RealFieldElement<T>>
    extends TestFieldProblemAbstract<T> {

    /**
     * Simple constructor.
     * @param field field to which elements belong
     */
    public TestFieldProblem2(Field<T> field) {
        super(field);
        setInitialConditions(convert(0.0), convert(new double[] { 0.0 }));
        setFinalConditions(convert(1.0));
        setErrorScale(convert(new double[] { 1.0 }));
    }

    @Override
    public T[] doComputeDerivatives(T t, T[] y) {

        final T[] yDot = MathArrays.buildArray(getField(), getDimension());
        // compute the derivatives
        for (int i = 0; i < getDimension(); ++i) {
            yDot[i] = t.multiply(t.multiply(t).subtract(y[i]));
        }

        return yDot;
    }

    @Override
    public T[] computeTheoreticalState(T t) {
        final T[] y = MathArrays.buildArray(getField(), getDimension());
        T t2 = t.multiply(t);
        T c = t2.add(t2.multiply(-0.5).exp().subtract(1).multiply(2));
        for (int i = 0; i < getDimension(); ++i) {
            y[i] = c;
        }
        return y;
    }
}
