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

public class ThreeEighthesFieldIntegratorTest extends RungeKuttaFieldIntegratorAbstractTest {

    @Override
    protected <T extends RealFieldElement<T>> RungeKuttaFieldIntegrator<T>
    createIntegrator(Field<T> field, T step) {
        return new ThreeEighthesFieldIntegrator<>(field, step);
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
        doTestDecreasingSteps(Decimal64Field.getInstance(), 1.0, 1.0, 1.0e-10);
    }

    @Override
    public void testSmallStep() {
        doTestSmallStep(Decimal64Field.getInstance(), 2.0e-13, 4.0e-12, 1.0e-12, "3/8");
    }

    @Override
    public void testBigStep() {
        doTestBigStep(Decimal64Field.getInstance(), 0.0004, 0.005, 1.0e-12, "3/8");
    }

    @Override
    public void testBackward() {
        doTestBackward(Decimal64Field.getInstance(), 5.0e-10, 7.0e-10, 1.0e-12, "3/8");
    }

    @Override
    public void testKepler() {
        doTestKepler(Decimal64Field.getInstance(), 0.0348, 1.0e-4);
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
        doTestPartialDerivatives(3.2e-10, new double[] { 2.1e-9, 5.9e-10, 7.0e-11, 5.9e-10, 5.9e-10 });
    }
}
