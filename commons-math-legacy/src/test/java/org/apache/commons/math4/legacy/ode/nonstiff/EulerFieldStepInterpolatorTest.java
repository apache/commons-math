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

package org.apache.commons.math4.legacy.ode.nonstiff;


import org.apache.commons.math4.legacy.core.Field;
import org.apache.commons.math4.legacy.core.RealFieldElement;
import org.apache.commons.math4.legacy.ode.FieldEquationsMapper;
import org.apache.commons.math4.legacy.ode.FieldODEStateAndDerivative;
import org.junit.Test;

public class EulerFieldStepInterpolatorTest extends RungeKuttaFieldStepInterpolatorAbstractTest {

    @Override
    protected <T extends RealFieldElement<T>> RungeKuttaFieldStepInterpolator<T>
    createInterpolator(Field<T> field, boolean forward, T[][] yDotK,
                       FieldODEStateAndDerivative<T> globalPreviousState,
                       FieldODEStateAndDerivative<T> globalCurrentState,
                       FieldODEStateAndDerivative<T> softPreviousState,
                       FieldODEStateAndDerivative<T> softCurrentState, FieldEquationsMapper<T> mapper) {
        return new EulerFieldStepInterpolator<>(field, forward, yDotK,
                                                 globalPreviousState, globalCurrentState,
                                                 softPreviousState, softCurrentState,
                                                 mapper);
    }

    @Override
    protected <T extends RealFieldElement<T>> FieldButcherArrayProvider<T>
    createButcherArrayProvider(final Field<T> field) {
        return new EulerFieldIntegrator<>(field, field.getOne());
    }

    @Override
    @Test
    public void interpolationAtBounds() {
        doInterpolationAtBounds(Decimal64Field.getInstance(), 1.0e-15);
    }

    @Override
    @Test
    public void interpolationInside() {
        doInterpolationInside(Decimal64Field.getInstance(), 3.3e-4, 7.9e-3);
    }

    @Override
    @Test
    public void nonFieldInterpolatorConsistency() {
        doNonFieldInterpolatorConsistency(Decimal64Field.getInstance(), 7.0e-18, 1.0e-50, 1.0e-50, 1.0e-50);
    }
}
