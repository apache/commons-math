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

public class EulerFieldIntegratorTest extends RungeKuttaFieldIntegratorAbstractTest {

    @Override
    protected <T extends RealFieldElement<T>> RungeKuttaFieldIntegrator<T>
    createIntegrator(Field<T> field, T step) {
        return new EulerFieldIntegrator<>(field, step);
    }

    @Override
    public void testNonFieldIntegratorConsistency() {
        doTestNonFieldIntegratorConsistency(Decimal64Field.getInstance());
    }

    @Override
    public void testMissedEndEvent() {
        doTestMissedEndEvent(Decimal64Field.getInstance(), 1.0e-15, 6.0e-5);
    }

    @Override
    public void testSanityChecks() {
        doTestSanityChecks(Decimal64Field.getInstance());
    }

    @Override
    public void testDecreasingSteps() {
        doTestDecreasingSteps(Decimal64Field.getInstance(), 1.0, 1.5, 1.0e-10);
    }

    @Override
    public void testSmallStep() {
        doTestSmallStep(Decimal64Field.getInstance(), 2.0e-4, 1.0e-3, 1.0e-12, "Euler");
    }

    @Override
    public void testBigStep() {
        doTestBigStep(Decimal64Field.getInstance(), 0.01, 0.2, 1.0e-12, "Euler");
    }

    @Override
    public void testBackward() {
        doTestBackward(Decimal64Field.getInstance(),0.45, 0.45, 1.0e-12, "Euler");
    }

    @Override
    public void testKepler() {
        // Euler integrator is clearly not able to solve this problem
        doTestKepler(Decimal64Field.getInstance(), 881.176, 0.001);
    }

    @Override
    public void testStepSize() {
        doTestStepSize(Decimal64Field.getInstance(), 1.0e-12);
    }

    @Override
    public void testSingleStep() {
        doTestSingleStep(Decimal64Field.getInstance(), 0.21);
    }

    @Override
    public void testTooLargeFirstStep() {
        doTestTooLargeFirstStep(Decimal64Field.getInstance());
    }

    @Override
    public void testUnstableDerivative() {
        doTestUnstableDerivative(Decimal64Field.getInstance(), 1.0e-12);
    }

    @Override
    public void testDerivativesConsistency() {
        doTestDerivativesConsistency(Decimal64Field.getInstance(), 1.0e-10);
    }

    @Override
    public void testPartialDerivatives() {
        doTestPartialDerivatives(0.085, new double[] { 0.47, 0.13, 0.019, 0.019, 0.13 });
    }
}
