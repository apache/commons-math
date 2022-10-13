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
import org.apache.commons.math4.legacy.ode.events.FieldEventHandler;
import org.apache.commons.math4.legacy.core.MathArrays;

/**
 * This class is used as the base class of the problems that are
 * integrated during the junit tests for the ODE integrators.
 * @param <T> the type of the field elements
 */
public abstract class TestFieldProblemAbstract<T extends RealFieldElement<T>>
    implements FirstOrderFieldDifferentialEquations<T> {

    /** Field to which elements belong. */
    private Field<T> field;

    /** Dimension of the problem. */
    private int n;

    /** Number of functions calls. */
    private int calls;

    /** Initial time */
    private T t0;

    /** Initial state */
    private T[] y0;

    /** Final time */
    private T t1;

    /** Error scale */
    private T[] errorScale;

    /**
     * Simple constructor.
     * @param field field to which elements belong
     */
    protected TestFieldProblemAbstract(Field<T> field) {
        this.field = field;
        n          = 0;
        calls      = 0;
        t0         = field.getZero();
        y0         = null;
        t1         = field.getZero();
        errorScale = null;
    }

    /**
     * Set the initial conditions
     * @param t0 initial time
     * @param y0 initial state vector
     */
    protected void setInitialConditions(T t0, T[] y0) {
        calls     = 0;
        n         = y0.length;
        this.t0   = t0;
        this.y0   = y0.clone();
    }

    /**
     * Set the final conditions.
     * @param t1 final time
     */
    protected void setFinalConditions(T t1) {
        this.t1 = t1;
    }

    /**
     * Set the error scale
     * @param errorScale error scale
     */
    protected void setErrorScale(T[] errorScale) {
        this.errorScale = errorScale.clone();
    }

    /** get the filed to which elements belong.
     * @return field to which elements belong
     */
    public Field<T> getField() {
        return field;
    }

    /** Get the problem dimension.
     * @return problem dimension
     */
    @Override
    public int getDimension() {
        return n;
    }

   /**
     * Get the initial state.
     * @return initial state
     */
    public FieldODEState<T> getInitialState() {
        return new FieldODEState<>(t0, y0);
    }

    /**
     * Get the final time.
     * @return final time
     */
    public T getFinalTime() {
        return t1;
    }

    /**
     * Get the error scale.
     * @return error scale
     */
    public T[] getErrorScale() {
        return errorScale;
    }

    /**
     * Get the events handlers.
     * @return events handlers   */
    public FieldEventHandler<T>[] getEventsHandlers() {
        @SuppressWarnings("unchecked")
        final FieldEventHandler<T>[] empty =
                        (FieldEventHandler<T>[]) Array.newInstance(FieldEventHandler.class, 0);
        return empty;
    }

    /**
     * Get the theoretical events times.
     * @return theoretical events times
     */
    public T[] getTheoreticalEventsTimes() {
        return MathArrays.buildArray(field, 0);
    }

    /**
     * Get the number of calls.
     * @return number of calls
     */
    public int getCalls() {
        return calls;
    }

    /** {@inheritDoc} */
    @Override
    public void init(T t0, T[] y0, T t) {
    }

    /** {@inheritDoc} */
    @Override
    public T[] computeDerivatives(T t, T[] y) {
        ++calls;
        return doComputeDerivatives(t, y);
    }

    public abstract T[] doComputeDerivatives(T t, T[] y);

    /**
     * Compute the theoretical state at the specified time.
     * @param t time at which the state is required
     * @return state vector at time t
     */
    public abstract T[] computeTheoreticalState(T t);

    /** Convert a double.
     * @param d double to convert
     * @return converted double
     */
    protected T convert(double d) {
        return field.getZero().add(d);
    }

    /** Convert a one dimension array.
     * @param elements array elements
     * @return converted array
     */
    protected T[] convert(double ... elements) {
        T[] array = MathArrays.buildArray(field, elements.length);
        for (int i = 0; i < elements.length; ++i) {
            array[i] = convert(elements[i]);
        }
        return array;
    }
}
