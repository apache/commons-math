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
import org.apache.commons.math4.legacy.exception.MathIllegalStateException;
import org.apache.commons.math4.legacy.exception.MaxCountExceededException;
import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;
import org.junit.Test;

public class AdamsMoultonFieldIntegratorTest extends AdamsFieldIntegratorAbstractTest {

    @Override
    protected <T extends RealFieldElement<T>> AdamsFieldIntegrator<T>
    createIntegrator(Field<T> field, final int nSteps, final double minStep, final double maxStep,
                     final double scalAbsoluteTolerance, final double scalRelativeTolerance) {
        return new AdamsMoultonFieldIntegrator<>(field, nSteps, minStep, maxStep,
                        scalAbsoluteTolerance, scalRelativeTolerance);
    }

    @Override
    protected <T extends RealFieldElement<T>> AdamsFieldIntegrator<T>
    createIntegrator(Field<T> field, final int nSteps, final double minStep, final double maxStep,
                     final double[] vecAbsoluteTolerance, final double[] vecRelativeTolerance) {
        return new AdamsMoultonFieldIntegrator<>(field, nSteps, minStep, maxStep,
                        vecAbsoluteTolerance, vecRelativeTolerance);
    }

    @Override
    @Test(expected=NumberIsTooSmallException.class)
    public void testMinStep() {
        doDimensionCheck(Decimal64Field.getInstance());
    }

    @Override
    @Test
    public void testIncreasingTolerance() {
        // the 0.45 and 8.69 factors are only valid for this test
        // and has been obtained from trial and error
        // there are no general relationship between local and global errors
        doTestIncreasingTolerance(Decimal64Field.getInstance(), 0.45, 8.69);
    }

    @Override
    @Test(expected = MaxCountExceededException.class)
    public void exceedMaxEvaluations() {
        doExceedMaxEvaluations(Decimal64Field.getInstance(), 650);
    }

    @Override
    @Test
    public void backward() {
        doBackward(Decimal64Field.getInstance(), 3.0e-9, 3.0e-9, 1.0e-16, "Adams-Moulton");
    }

    @Override
    @Test
    public void polynomial() {
        doPolynomial(Decimal64Field.getInstance(), 5, 2.2e-05, 1.1e-11);
    }

    @Override
    @Test(expected=MathIllegalStateException.class)
    public void testStartFailure() {
        doTestStartFailure(Decimal64Field.getInstance());
    }
}
