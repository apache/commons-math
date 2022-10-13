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

public class DormandPrince54FieldIntegratorTest extends EmbeddedRungeKuttaFieldIntegratorAbstractTest {

    @Override
    protected <T extends RealFieldElement<T>> EmbeddedRungeKuttaFieldIntegrator<T>
    createIntegrator(Field<T> field, final double minStep, final double maxStep,
                     final double scalAbsoluteTolerance, final double scalRelativeTolerance) {
        return new DormandPrince54FieldIntegrator<>(field, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
    }

    @Override
    protected <T extends RealFieldElement<T>> EmbeddedRungeKuttaFieldIntegrator<T>
    createIntegrator(Field<T> field, final double minStep, final double maxStep,
                     final double[] vecAbsoluteTolerance, final double[] vecRelativeTolerance) {
        return new DormandPrince54FieldIntegrator<>(field, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
    }

    @Override
    public void testNonFieldIntegratorConsistency() {
        doTestNonFieldIntegratorConsistency(Decimal64Field.getInstance());
    }

    @Override
    public void testSanityChecks() {
        doTestSanityChecks(Decimal64Field.getInstance());
    }

    @Override
    public void testBackward() {
        doTestBackward(Decimal64Field.getInstance(), 1.6e-7, 1.6e-7, 1.0e-22, "Dormand-Prince 5(4)");
    }

    @Override
    public void testKepler() {
        doTestKepler(Decimal64Field.getInstance(), 3.1e-10);
    }

    @Override
    public void testForwardBackwardExceptions() {
        doTestForwardBackwardExceptions(Decimal64Field.getInstance());
    }

    @Override
    public void testMinStep() {
        doTestMinStep(Decimal64Field.getInstance());
    }

    @Override
    public void testIncreasingTolerance() {
        // the 0.7 factor is only valid for this test
        // and has been obtained from trial and error
        // there is no general relation between local and global errors
        doTestIncreasingTolerance(Decimal64Field.getInstance(), 0.7, 1.0e-12);
    }

    @Override
    public void testEvents() {
        doTestEvents(Decimal64Field.getInstance(), 1.7e-7, "Dormand-Prince 5(4)");
    }

    @Override
    public void testEventsErrors() {
        doTestEventsErrors(Decimal64Field.getInstance());
    }

    @Override
    public void testEventsNoConvergence() {
        doTestEventsNoConvergence(Decimal64Field.getInstance());
    }

    @Override
    public void testPartialDerivatives() {
        doTestPartialDerivatives(4.8e-12, new double[] { 2.3e-11, 6.3e-12, 9.0e-13, 7.4e-13, 6.3e-12 });
    }
}
