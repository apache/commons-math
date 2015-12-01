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

package org.apache.commons.math3.ode;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.util.MathArrays;

/**
 * This class is used in the junit tests for the ODE integrators.

 * <p>This specific problem is the following differential equation :
 * <pre>
 *    y' = -y
 * </pre>
 * the solution of this equation is a simple exponential function :
 * <pre>
 *   y (t) = y (t0) exp (t0-t)
 * </pre>
 * </p>

 * @param <T> the type of the field elements
 */
public class TestFieldProblem1<T extends RealFieldElement<T>>
    extends TestFieldProblemAbstract<T> {

    /**
     * Simple constructor.
     * @param field field to which elements belong
     */
    public TestFieldProblem1(Field<T> field) {
        super(field);
        setInitialConditions(convert(0.0), convert(1.0, 0.1));
        setFinalConditions(convert(4.0));
        setErrorScale(convert(1.0, 1.0));
    }

    @Override
    public T[] doComputeDerivatives(T t, T[] y) {

        final T[] yDot = MathArrays.buildArray(getField(), getDimension());

        // compute the derivatives
        for (int i = 0; i < getDimension(); ++i) {
            yDot[i] = y[i].negate();
        }

        return yDot;

    }

    @Override
    public T[] computeTheoreticalState(T t) {
        final FieldODEState<T> s0 = getInitialState();
        final T[] y0 = s0.getState();
        final T[] y = MathArrays.buildArray(getField(), getDimension());
        T c = s0.getTime().subtract(t).exp();
        for (int i = 0; i < getDimension(); ++i) {
            y[i] = c.multiply(y0[i]);
        }
        return y;
    }

}
