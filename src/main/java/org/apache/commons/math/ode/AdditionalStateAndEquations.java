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

package org.apache.commons.math.ode;


/**
 * This class is a container for additional state parameters and their associated
 * evolution equation.
 * <p>
 * It is a container allowing the integrator to keep constant consistency between
 * additional states and the corresponding equations. It allows to set additional
 * state values, get current additional state value and derivatives by reference
 * on the associated additional equations.
 * </p>
 *
 * @see ExpandableFirstOrderDifferentialEquations
 * @see AdditionalEquations
 *
 * @version $Id$
 * @since 3.0
 */
class AdditionalStateAndEquations {

    /** Additional equations set. */
    private final AdditionalEquations addEquations;

    /** Current additional state. */
    private double[] addState;

    /** Current additional state derivatives. */
    private double[] addStateDot;

    /** Create a new instance based on one set of additional equations.
     * @param addEqu additional equations.
     */
    public AdditionalStateAndEquations(final AdditionalEquations addEqu) {
        this.addEquations = addEqu;
    }

    /** Get a reference to the current value of the additional state.
     * <p>The array returned is a true reference to the state array, so it may be
     * used to store data into it.</>
     * @return a reference current value of the additional state.
     */
    public double[] getAdditionalState() {
        return addState;
    }

    /** Get a reference to the current value of the additional state derivatives.
     * <p>The array returned is a true reference to the state array, so it may be
     * used to store data into it.</>
     * @return a reference current value of the additional state derivatives.
     */
    public double[] getAdditionalStateDot() {
        return addStateDot;
    }

    /** Get the instance of the current additional equations.
     * @return current value of the additional equations.
     */
    public AdditionalEquations getAdditionalEquations() {
        return addEquations;
    }

    /** Set a value to additional state.
     * @param state additional state value.
     */
    public void setAdditionalState(final double[] state) {
        this.addState    = state.clone();
        this.addStateDot = new double[state.length];
    }

}
