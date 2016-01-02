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
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.util.Decimal64Field;
import org.junit.Test;

public class DormandPrince54FieldStepInterpolatorTest extends RungeKuttaFieldStepInterpolatorAbstractTest {

    protected <T extends RealFieldElement<T>> RungeKuttaFieldStepInterpolator<T>
    createInterpolator(Field<T> field, boolean forward, T[][] yDotK,
                       FieldODEStateAndDerivative<T> globalPreviousState,
                       FieldODEStateAndDerivative<T> globalCurrentState,
                       FieldODEStateAndDerivative<T> softPreviousState,
                       FieldODEStateAndDerivative<T> softCurrentState,
                       FieldEquationsMapper<T> mapper) {
        return new DormandPrince54FieldStepInterpolator<T>(field, forward, yDotK,
                                                           globalPreviousState, globalCurrentState,
                                                           softPreviousState, softCurrentState,
                                                           mapper);
    }

    protected <T extends RealFieldElement<T>> FieldButcherArrayProvider<T>
    createButcherArrayProvider(final Field<T> field) {
        return new DormandPrince54FieldIntegrator<T>(field, 0, 1, 1, 1);
    }

    @Test
    public void interpolationAtBounds() {
        doInterpolationAtBounds(Decimal64Field.getInstance(), 1.0e-50);
    }

    @Test
    public void interpolationInside() {
        doInterpolationInside(Decimal64Field.getInstance(), 9.5e-14, 1.8e-15);
    }

    @Test
    public void nonFieldInterpolatorConsistency() {
        doNonFieldInterpolatorConsistency(Decimal64Field.getInstance(), 2.8e-17, 2.3e-16, 5.6e-16, 5.6e-17);
    }

}
