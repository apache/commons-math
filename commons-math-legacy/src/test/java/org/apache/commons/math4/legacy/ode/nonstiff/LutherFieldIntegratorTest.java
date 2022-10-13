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
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.MaxCountExceededException;
import org.apache.commons.math4.legacy.exception.NoBracketingException;
import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;

public class LutherFieldIntegratorTest extends RungeKuttaFieldIntegratorAbstractTest {

    @Override
    protected <T extends RealFieldElement<T>> RungeKuttaFieldIntegrator<T>
    createIntegrator(Field<T> field, T step) {
        return new LutherFieldIntegrator<>(field, step);
    }

    @Override
    public void testNonFieldIntegratorConsistency() {
        doTestNonFieldIntegratorConsistency(Decimal64Field.getInstance());
    }

    @Override
    public void testMissedEndEvent()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        doTestMissedEndEvent(Decimal64Field.getInstance(), 1.0e-15, 1.0e-15);
    }

    @Override
    public void testSanityChecks()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        doTestSanityChecks(Decimal64Field.getInstance());
    }

    @Override
    public void testDecreasingSteps()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        doTestDecreasingSteps(Decimal64Field.getInstance(), 1.0, 1.0, 1.0e-10);
    }

    @Override
    public void testSmallStep()
         throws DimensionMismatchException, NumberIsTooSmallException,
                MaxCountExceededException, NoBracketingException {
        doTestSmallStep(Decimal64Field.getInstance(), 8.7e-17, 3.6e-15, 1.0e-12, "Luther");
    }

    @Override
    public void testBigStep()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        doTestBigStep(Decimal64Field.getInstance(), 2.7e-5, 1.7e-3, 1.0e-12, "Luther");
    }

    @Override
    public void testBackward()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        doTestBackward(Decimal64Field.getInstance(), 2.4e-13, 4.3e-13, 1.0e-12, "Luther");
    }

    @Override
    public void testKepler()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        doTestKepler(Decimal64Field.getInstance(), 2.18e-7, 4.0e-10);
    }

    @Override
    public void testStepSize()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        doTestStepSize(Decimal64Field.getInstance(), 1.0e-22);
    }

    @Override
    public void testSingleStep() {
        doTestSingleStep(Decimal64Field.getInstance(), 6.0e-12);
    }

    @Override
    public void testTooLargeFirstStep() {
        doTestTooLargeFirstStep(Decimal64Field.getInstance());
    }

    @Override
    public void testUnstableDerivative() {
        doTestUnstableDerivative(Decimal64Field.getInstance(), 4.0e-15);
    }

    @Override
    public void testDerivativesConsistency() {
        doTestDerivativesConsistency(Decimal64Field.getInstance(), 1.0e-20);
    }

    @Override
    public void testPartialDerivatives() {
        doTestPartialDerivatives(4.3e-13, new double[] { 2.2e-12, 5.6e-13, 9.4e-14, 9.4e-14, 5.6e-13 });
    }
}
