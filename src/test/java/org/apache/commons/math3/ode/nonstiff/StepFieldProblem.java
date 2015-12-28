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

package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.ode.FirstOrderFieldDifferentialEquations;
import org.apache.commons.math3.ode.FieldODEState;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.ode.events.Action;
import org.apache.commons.math3.ode.events.FieldEventHandler;
import org.apache.commons.math3.util.MathArrays;


public class StepFieldProblem<T extends RealFieldElement<T>>
    implements FirstOrderFieldDifferentialEquations<T>, FieldEventHandler<T> {

    public StepFieldProblem(Field<T> field, T rateBefore, T rateAfter, T switchTime) {
        this.field      = field;
        this.rateAfter  = rateAfter;
        this.switchTime = switchTime;
        setRate(rateBefore);
    }

    public T[] computeDerivatives(T t, T[] y) {
        T[] yDot = MathArrays.buildArray(field, 1);
        yDot[0] = rate;
        return yDot;
    }

    public int getDimension() {
        return 1;
    }

    public void setRate(T rate) {
        this.rate = rate;
    }

    public void init(T t0, T[] y0, T t) {
    }

    public void init(FieldODEStateAndDerivative<T> state0, T t) {
    }

    public Action eventOccurred(FieldODEStateAndDerivative<T> state, boolean increasing) {
        setRate(rateAfter);
        return Action.RESET_DERIVATIVES;
    }

    public T g(FieldODEStateAndDerivative<T> state) {
        return state.getTime().subtract(switchTime);
    }

    public FieldODEState<T> resetState(FieldODEStateAndDerivative<T> state) {
        return state;
    }

    private Field<T> field;
    private T        rate;
    private T        rateAfter;
    private T        switchTime;

}
