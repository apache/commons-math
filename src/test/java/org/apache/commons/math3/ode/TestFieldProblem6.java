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
 *    y' = 3x^5 - y
 * </pre>
 * when the initial condition is y(0) = -360, the solution of this
 * equation degenerates to a simple quintic polynomial function :
 * <pre>
 *   y (t) = 3x^5 - 15x^4 + 60x^3 - 180x^2 + 360x - 360
 * </pre>
 * </p>

 * @param <T> the type of the field elements
 */
public class TestFieldProblem6<T extends RealFieldElement<T>>
    extends TestFieldProblemAbstract<T> {

    /**
     * Simple constructor.
     * @param field field to which elements belong
     */
    public TestFieldProblem6(Field<T> field) {
        super(field);
        setInitialConditions(convert(0.0), convert( new double[] { -360.0 }));
        setFinalConditions(convert(1.0));
        setErrorScale(convert( new double[] { 1.0 }));
    }

    @Override
    public T[] doComputeDerivatives(T t, T[] y) {

        final T[] yDot = MathArrays.buildArray(getField(), getDimension());

        // compute the derivatives
        T t2 = t.multiply(t);
        T t4 = t2.multiply(t2);
        T t5 = t4.multiply(t);
        for (int i = 0; i < getDimension(); ++i) {
            yDot[i] = t5.multiply(3).subtract(y[i]);
        }

        return yDot;

    }

    @Override
    public T[] computeTheoreticalState(T t) {

        final T[] y = MathArrays.buildArray(getField(), getDimension());

        for (int i = 0; i < getDimension(); ++i) {
            y[i] = t.multiply(3).subtract(15).multiply(t).add(60).multiply(t).subtract(180).multiply(t).add(360).multiply(t).subtract(360);
        }

        return y;

    }

}
