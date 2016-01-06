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
package org.apache.commons.math4.ode;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math4.RealFieldElement;
import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.MaxCountExceededException;
import org.apache.commons.math4.util.MathArrays;


/**
 * This class represents a combined set of first order differential equations,
 * with at least a primary set of equations expandable by some sets of secondary
 * equations.
 * <p>
 * One typical use case is the computation of the Jacobian matrix for some ODE.
 * In this case, the primary set of equations corresponds to the raw ODE, and we
 * add to this set another bunch of secondary equations which represent the Jacobian
 * matrix of the primary set.
 * </p>
 * <p>
 * We want the integrator to use <em>only</em> the primary set to estimate the
 * errors and hence the step sizes. It should <em>not</em> use the secondary
 * equations in this computation. The {@link FieldFirstOrderIntegrator integrator} will
 * be able to know where the primary set ends and so where the secondary sets begin.
 * </p>
 *
 * @see FieldFirstOrderDifferentialEquations
 * @see FieldSecondaryEquations
 *
 * @param <T> the type of the field elements
 * @since 3.6
 */

public class FieldExpandableODE<T extends RealFieldElement<T>> {

    /** Total dimension. */
    private int dimension;

    /** Primary differential equation. */
    private final FieldFirstOrderDifferentialEquations<T> primary;

    /** Mapper for primary equation. */
    private final FieldEquationsMapper<T> primaryMapper;

    /** Components of the expandable ODE. */
    private List<FieldSecondaryComponent<T>> components;

    /** Build an expandable set from its primary ODE set.
     * @param primary the primary set of differential equations to be integrated.
     */
    public FieldExpandableODE(final FieldFirstOrderDifferentialEquations<T> primary) {
        this.dimension     = primary.getDimension();
        this.primary       = primary;
        this.primaryMapper = new FieldEquationsMapper<T>(0, primary.getDimension());
        this.components    = new ArrayList<FieldExpandableODE.FieldSecondaryComponent<T>>();
    }

    /** Get the primary set of differential equations.
     * @return primary set of differential equations
     */
    public FieldFirstOrderDifferentialEquations<T> getPrimary() {
        return primary;
    }

    /** Return the dimension of the complete set of equations.
     * <p>
     * The complete set of equations correspond to the primary set plus all secondary sets.
     * </p>
     * @return dimension of the complete set of equations
     */
    public int getTotalDimension() {
        return dimension;
    }

    /** Add a set of secondary equations to be integrated along with the primary set.
     * @param secondary secondary equations set
     * @return index of the secondary equation in the expanded state, to be used
     * as the parameter to {@link FieldODEState#getSecondaryState(int)} and
     * {@link FieldODEStateAndDerivative#getSecondaryDerivative(int)}
     */
    public int addSecondaryEquations(final FieldSecondaryEquations<T> secondary) {

        final int firstIndex;
        if (components.isEmpty()) {
            // lazy creation of the components list
            components = new ArrayList<FieldExpandableODE.FieldSecondaryComponent<T>>();
            firstIndex = primary.getDimension();
        } else {
            final FieldSecondaryComponent<T> last = components.get(components.size() - 1);
            firstIndex = last.mapper.getFirstIndex() + last.mapper.getDimension();
        }

        final FieldSecondaryComponent<T> component = new FieldSecondaryComponent<T>(secondary, firstIndex);
        components.add(component);

        // update total dimension
        dimension = component.mapper.getFirstIndex() + component.mapper.getDimension();

        return components.size() - 1;

    }

    /** Map a state to a complete flat array.
     * @param state state to map
     * @return flat array containing the mapped state, including primary and secondary components
     */
    public T[] mapState(final FieldODEState<T> state) {
        final T[] y = MathArrays.buildArray(state.getTime().getField(), getTotalDimension());
        primaryMapper.insertEquationData(state.getState(), y);
        for (int i = 0; i < components.size(); ++i) {
            components.get(i).mapper.insertEquationData(state.getSecondaryState(i), y);
        }
        return y;
    }

    /** Map a state derivative to a complete flat array.
     * @param state state to map
     * @return flat array containing the mapped state derivative, including primary and secondary components
     */
    public T[] mapDerivative(final FieldODEStateAndDerivative<T> state) {
        final T[] yDot = MathArrays.buildArray(state.getTime().getField(), getTotalDimension());
        primaryMapper.insertEquationData(state.getDerivative(), yDot);
        for (int i = 0; i < components.size(); ++i) {
            components.get(i).mapper.insertEquationData(state.getSecondaryDerivative(i), yDot);
        }
        return yDot;
    }

    /** Map a flat array to a state.
     * @param t time
     * @param y array to map, including primary and secondary components
     * @return mapped state
     */
    public FieldODEState<T> mapState(final T t, final T[] y) {
        final T[] state = primaryMapper.extractEquationData(y);
        if (components.isEmpty()) {
            return new FieldODEState<T>(t, state);
        } else {
            @SuppressWarnings("unchecked")
            final T[][] secondaryState = (T[][]) Array.newInstance(t.getField().getRuntimeClass(), components.size());
            for (int i = 0; i < components.size(); ++i) {
                secondaryState[i] = components.get(i).mapper.extractEquationData(y);
            }
            return new FieldODEState<T>(t, state, secondaryState);
        }
    }

    /** Map flat arrays to a state and derivative.
     * @param t time
     * @param y state array to map, including primary and secondary components
     * @param yDot state derivative array to map, including primary and secondary components
     * @return mapped state
     */
    public FieldODEStateAndDerivative<T> mapStateAndDerivative(final T t, final T[] y, final T[] yDot) {
        final T[] state      = primaryMapper.extractEquationData(y);
        final T[] derivative = primaryMapper.extractEquationData(yDot);
        if (components.isEmpty()) {
            return new FieldODEStateAndDerivative<T>(t, state, derivative);
        } else {
            @SuppressWarnings("unchecked")
            final T[][] secondaryState      = (T[][]) Array.newInstance(t.getField().getRuntimeClass(), components.size());
            @SuppressWarnings("unchecked")
            final T[][] secondaryDerivative = (T[][]) Array.newInstance(t.getField().getRuntimeClass(), components.size());
            for (int i = 0; i < components.size(); ++i) {
                secondaryState[i]      = components.get(i).mapper.extractEquationData(y);
                secondaryDerivative[i] = components.get(i).mapper.extractEquationData(yDot);
            }
            return new FieldODEStateAndDerivative<T>(t, state, derivative, secondaryState, secondaryDerivative);
        }
    }

    /** Get the current time derivative of the complete state vector.
     * @param t current value of the independent <I>time</I> variable
     * @param y array containing the current value of the complete state vector
     * @return time derivative of the complete state vector
     * @exception MaxCountExceededException if the number of functions evaluations is exceeded
     * @exception DimensionMismatchException if arrays dimensions do not match equations settings
     */
    public T[] computeDerivatives(final T t, final T[] y)
        throws MaxCountExceededException, DimensionMismatchException {

        final T[] yDot = MathArrays.buildArray(t.getField(), getTotalDimension());

        // compute derivatives of the primary equations
        final T[] primaryState    = primaryMapper.extractEquationData(y);
        final T[] primaryStateDot = primary.computeDerivatives(t, primaryState);
        primaryMapper.insertEquationData(primaryStateDot, yDot);

        // Add contribution for secondary equations
        for (final FieldSecondaryComponent<T> component : components) {
            final T[] componentState    = component.mapper.extractEquationData(y);
            final T[] componentStateDot = component.equation.computeDerivatives(t, primaryState, primaryStateDot, componentState);
            component.mapper.insertEquationData(componentStateDot, yDot);
        }

        return yDot;

    }

    /** Components of the compound ODE.
     * @param <S> the type of the field elements
     */
    private static class FieldSecondaryComponent<S extends RealFieldElement<S>> {

        /** Secondary differential equation. */
        private final FieldSecondaryEquations<S> equation;

        /** Mapper between local and complete arrays. */
        private final FieldEquationsMapper<S> mapper;

        /** Simple constructor.
         * @param equation secondary differential equation
         * @param firstIndex index to use for the first element in the complete arrays
         */
        FieldSecondaryComponent(final FieldSecondaryEquations<S> equation, final int firstIndex) {
            this.equation = equation;
            this.mapper   = new FieldEquationsMapper<S>(firstIndex, equation.getDimension());
        }

    }

}
