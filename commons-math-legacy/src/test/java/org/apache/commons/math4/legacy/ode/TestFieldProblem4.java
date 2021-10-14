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

import java.lang.reflect.Array;

import org.apache.commons.math4.legacy.core.Field;
import org.apache.commons.math4.legacy.core.RealFieldElement;
import org.apache.commons.math4.legacy.ode.events.Action;
import org.apache.commons.math4.legacy.ode.events.FieldEventHandler;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.apache.commons.math4.legacy.core.MathArrays;

/**
 * This class is used in the junit tests for the ODE integrators.

 * <p>This specific problem is the following differential equation :
 * <pre>
 *    x'' = -x
 * </pre>
 * And when x decreases down to 0, the state should be changed as follows :
 * <pre>
 *   x' -> -x'
 * </pre>
 * The theoretical solution of this problem is x = |sin(t+a)|
 * </p>

 * @param <T> the type of the field elements
 */
public class TestFieldProblem4<T extends RealFieldElement<T>>
    extends TestFieldProblemAbstract<T> {

    /** Time offset. */
    private T a;

    /** Simple constructor.
     * @param field field to which elements belong
     */
    public TestFieldProblem4(Field<T> field) {
        super(field);
        a = convert(1.2);
        T[] y0 = MathArrays.buildArray(field, 2);
        y0[0] = a.sin();
        y0[1] = a.cos();
        setInitialConditions(convert(0.0), y0);
        setFinalConditions(convert(15));
        setErrorScale(convert(1.0, 0.0));
    }

    @Override
    public FieldEventHandler<T>[] getEventsHandlers() {
        @SuppressWarnings("unchecked")
        FieldEventHandler<T>[] handlers =
                        (FieldEventHandler<T>[]) Array.newInstance(FieldEventHandler.class, 2);
        handlers[0] = new Bounce<>();
        handlers[1] = new Stop<>();
        return handlers;
    }

    /**
     * Get the theoretical events times.
     * @return theoretical events times
     */
    @Override
    public T[] getTheoreticalEventsTimes() {
        T[] array = MathArrays.buildArray(getField(), 5);
        array[0] = a.negate().add(1 * JdkMath.PI);
        array[1] = a.negate().add(2 * JdkMath.PI);
        array[2] = a.negate().add(3 * JdkMath.PI);
        array[3] = a.negate().add(4 * JdkMath.PI);
        array[4] = convert(120.0);
        return array;
    }

    @Override
    public T[] doComputeDerivatives(T t, T[] y) {
        final T[] yDot = MathArrays.buildArray(getField(), getDimension());
        yDot[0] = y[1];
        yDot[1] = y[0].negate();
        return yDot;
    }

    @Override
    public T[] computeTheoreticalState(T t) {
        T sin = t.add(a).sin();
        T cos = t.add(a).cos();
        final T[] y = MathArrays.buildArray(getField(), getDimension());
        y[0] = sin.abs();
        y[1] = (sin.getReal() >= 0) ? cos : cos.negate();
        return y;
    }

    private static class Bounce<T extends RealFieldElement<T>> implements FieldEventHandler<T> {

        private int sign;

        Bounce() {
            sign = +1;
        }

        @Override
        public void init(FieldODEStateAndDerivative<T> state0, T t) {
        }

        @Override
        public T g(FieldODEStateAndDerivative<T> state) {
            return state.getState()[0].multiply(sign);
        }

        @Override
        public Action eventOccurred(FieldODEStateAndDerivative<T> state, boolean increasing) {
            // this sign change is needed because the state will be reset soon
            sign = -sign;
            return Action.RESET_STATE;
        }

        @Override
        public FieldODEState<T> resetState(FieldODEStateAndDerivative<T> state) {
            T[] y = state.getState();
            y[0] = y[0].negate();
            y[1] = y[1].negate();
            return new FieldODEState<>(state.getTime(), y);
        }
    }

    private static class Stop<T extends RealFieldElement<T>> implements FieldEventHandler<T> {

        Stop() {
        }

        @Override
        public void init(FieldODEStateAndDerivative<T> state0, T t) {
        }

        @Override
        public T g(FieldODEStateAndDerivative<T> state) {
            return state.getTime().subtract(12.0);
        }

        @Override
        public Action eventOccurred(FieldODEStateAndDerivative<T> state, boolean increasing) {
            return Action.STOP;
        }

        @Override
        public FieldODEState<T> resetState(FieldODEStateAndDerivative<T> state) {
            return state;
        }
    }
}
