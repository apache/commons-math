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
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.util.Decimal64Field;
import org.junit.Test;

public class LutherFieldIntegratorTest extends AbstractRungeKuttaFieldIntegratorTest {

    protected <T extends RealFieldElement<T>> RungeKuttaFieldIntegrator<T>
    createIntegrator(Field<T> field, T step) {
        return new LutherFieldIntegrator<T>(field, step);
    }

    @Test
    public void testNonFieldIntegratorConsistency() {
        doTestNonFieldIntegratorConsistency(Decimal64Field.getInstance());
    }

    @Test
    public void testMissedEndEvent()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        doTestMissedEndEvent(Decimal64Field.getInstance(), 1.0e-15, 1.0e-15);
    }

    @Test
    public void testSanityChecks()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        doTestSanityChecks(Decimal64Field.getInstance());
    }

    @Test
    public void testDecreasingSteps()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        doTestDecreasingSteps(Decimal64Field.getInstance(), 1.0, 1.0, 1.0e-10);
    }

    @Test
    public void testSmallStep()
         throws DimensionMismatchException, NumberIsTooSmallException,
                MaxCountExceededException, NoBracketingException {
        doTestSmallStep(Decimal64Field.getInstance(), 9.0e-17, 4.0e-15, 1.0e-12, "Luther");
    }

    @Test
    public void testBigStep()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        doTestBigStep(Decimal64Field.getInstance(), 0.00002, 0.001, 1.0e-12, "Luther");
    }

    @Test
    public void testBackward()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        doTestBackward(Decimal64Field.getInstance(), 3.0e-13, 5.0e-13, 1.0e-12, "Luther");
    }

    @Test
    public void testKepler()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        doTestKepler(Decimal64Field.getInstance(), 2.2e-7, 1.0e-8);
    }

    @Test
    public void testStepSize()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        doTestStepSize(Decimal64Field.getInstance(), 1.0e-12);
    }

    @Test
    public void testSingleStep() {
        doTestSingleStep(Decimal64Field.getInstance(), 1.0e-11);
    }

    @Test
    public void testTooLargeFirstStep() {
        doTestTooLargeFirstStep(Decimal64Field.getInstance());
    }

    @Test
    public void testUnstableDerivative() {
        doTestUnstableDerivative(Decimal64Field.getInstance(), 1.0e-12);
    }

    @Test
    public void testDerivativesConsistency() {
        doTestDerivativesConsistency(Decimal64Field.getInstance(), 1.0e-10);
    }

}
