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

package org.apache.commons.math4.legacy.ode;


import org.apache.commons.math4.legacy.core.Field;
import org.apache.commons.math4.legacy.core.RealFieldElement;
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.apache.commons.math4.legacy.ode.nonstiff.Decimal64Field;
import org.apache.commons.math4.legacy.core.MathArrays;
import org.junit.Assert;
import org.junit.Test;

public class FieldExpandableODETest {

    @Test
    public void testOnlyMainEquation() {
        doTestOnlyMainEquation(Decimal64Field.getInstance());
    }

    private <T extends RealFieldElement<T>> void doTestOnlyMainEquation(final Field<T> field) {
        FirstOrderFieldDifferentialEquations<T> main = new Linear<>(field, 3, 0);
        FieldExpandableODE<T> equation = new FieldExpandableODE<>(main);
        Assert.assertEquals(main.getDimension(), equation.getMapper().getTotalDimension());
        Assert.assertEquals(1, equation.getMapper().getNumberOfEquations());
        T t0 = field.getZero().add(10);
        T t  = field.getZero().add(100);
        T[] complete    = MathArrays.buildArray(field, equation.getMapper().getTotalDimension());
        for (int i = 0; i < complete.length; ++i) {
            complete[i] = field.getZero().add(i);
        }
        equation.init(t0, complete, t);
        T[] completeDot = equation.computeDerivatives(t0, complete);
        FieldODEStateAndDerivative<T> state = equation.getMapper().mapStateAndDerivative(t0, complete, completeDot);
        Assert.assertEquals(0, state.getNumberOfSecondaryStates());
        T[] mainState    = state.getState();
        T[] mainStateDot = state.getDerivative();
        Assert.assertEquals(main.getDimension(), mainState.length);
        for (int i = 0; i < main.getDimension(); ++i) {
            Assert.assertEquals(i, mainState[i].getReal(),   1.0e-15);
            Assert.assertEquals(i, mainStateDot[i].getReal(), 1.0e-15);
            Assert.assertEquals(i, completeDot[i].getReal(),  1.0e-15);
        }
    }

    @Test
    public void testMainAndSecondary() {
        doTestMainAndSecondary(Decimal64Field.getInstance());
    }

    private <T extends RealFieldElement<T>> void doTestMainAndSecondary(final Field<T> field) {

        FirstOrderFieldDifferentialEquations<T> main = new Linear<>(field, 3, 0);
        FieldExpandableODE<T> equation = new FieldExpandableODE<>(main);
        FieldSecondaryEquations<T> secondary1 = new Linear<>(field, 3, main.getDimension());
        int i1 = equation.addSecondaryEquations(secondary1);
        FieldSecondaryEquations<T> secondary2 = new Linear<>(field, 5, main.getDimension() + secondary1.getDimension());
        int i2 = equation.addSecondaryEquations(secondary2);
        Assert.assertEquals(main.getDimension() + secondary1.getDimension() + secondary2.getDimension(),
                            equation.getMapper().getTotalDimension());
        Assert.assertEquals(3, equation.getMapper().getNumberOfEquations());
        Assert.assertEquals(1, i1);
        Assert.assertEquals(2, i2);

        T t0 = field.getZero().add(10);
        T t  = field.getZero().add(100);
        T[] complete    = MathArrays.buildArray(field, equation.getMapper().getTotalDimension());
        for (int i = 0; i < complete.length; ++i) {
            complete[i] = field.getZero().add(i);
        }
        equation.init(t0, complete, t);
        T[] completeDot = equation.computeDerivatives(t0, complete);

        T[] mainState    = equation.getMapper().extractEquationData(0,  complete);
        T[] mainStateDot = equation.getMapper().extractEquationData(0,  completeDot);
        Assert.assertEquals(main.getDimension(), mainState.length);
        for (int i = 0; i < main.getDimension(); ++i) {
            Assert.assertEquals(i, mainState[i].getReal(),   1.0e-15);
            Assert.assertEquals(i, mainStateDot[i].getReal(), 1.0e-15);
            Assert.assertEquals(i, completeDot[i].getReal(),  1.0e-15);
        }

        T[] secondaryState1    = equation.getMapper().extractEquationData(i1,  complete);
        T[] secondaryState1Dot = equation.getMapper().extractEquationData(i1,  completeDot);
        Assert.assertEquals(secondary1.getDimension(), secondaryState1.length);
        for (int i = 0; i < secondary1.getDimension(); ++i) {
            Assert.assertEquals(i + main.getDimension(), secondaryState1[i].getReal(),   1.0e-15);
            Assert.assertEquals(-i, secondaryState1Dot[i].getReal(), 1.0e-15);
            Assert.assertEquals(-i, completeDot[i + main.getDimension()].getReal(),  1.0e-15);
        }

        T[] secondaryState2    = equation.getMapper().extractEquationData(i2,  complete);
        T[] secondaryState2Dot = equation.getMapper().extractEquationData(i2,  completeDot);
        Assert.assertEquals(secondary2.getDimension(), secondaryState2.length);
        for (int i = 0; i < secondary2.getDimension(); ++i) {
            Assert.assertEquals(i + main.getDimension() + secondary1.getDimension(), secondaryState2[i].getReal(),   1.0e-15);
            Assert.assertEquals(-i, secondaryState2Dot[i].getReal(), 1.0e-15);
            Assert.assertEquals(-i, completeDot[i + main.getDimension() + secondary1.getDimension()].getReal(),  1.0e-15);
        }
    }

    @Test
    public void testMap() {
        doTestMap(Decimal64Field.getInstance());
    }

    private <T extends RealFieldElement<T>> void doTestMap(final Field<T> field) {

        FirstOrderFieldDifferentialEquations<T> main = new Linear<>(field, 3, 0);
        FieldExpandableODE<T> equation = new FieldExpandableODE<>(main);
        FieldSecondaryEquations<T> secondary1 = new Linear<>(field, 3, main.getDimension());
        int i1 = equation.addSecondaryEquations(secondary1);
        FieldSecondaryEquations<T> secondary2 = new Linear<>(field, 5, main.getDimension() + secondary1.getDimension());
        int i2 = equation.addSecondaryEquations(secondary2);
        Assert.assertEquals(main.getDimension() + secondary1.getDimension() + secondary2.getDimension(),
                            equation.getMapper().getTotalDimension());
        Assert.assertEquals(3, equation.getMapper().getNumberOfEquations());
        Assert.assertEquals(1, i1);
        Assert.assertEquals(2, i2);

        T t0 = field.getZero().add(10);
        T t  = field.getZero().add(100);
        T[] complete    = MathArrays.buildArray(field, equation.getMapper().getTotalDimension());
        for (int i = 0; i < complete.length; ++i) {
            complete[i] = field.getZero().add(i);
        }
        equation.init(t0, complete, t);
        T[] completeDot = equation.computeDerivatives(t0, complete);

        try {
            equation.getMapper().mapStateAndDerivative(t0, MathArrays.buildArray(field, complete.length + 1), completeDot);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException dme) {
            // expected
        }
        try {
            equation.getMapper().mapStateAndDerivative(t0, complete, MathArrays.buildArray(field, completeDot.length + 1));
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException dme) {
            // expected
        }
        FieldODEStateAndDerivative<T> state = equation.getMapper().mapStateAndDerivative(t0, complete, completeDot);
        Assert.assertEquals(2, state.getNumberOfSecondaryStates());
        Assert.assertEquals(main.getDimension(),       state.getSecondaryStateDimension(0));
        Assert.assertEquals(secondary1.getDimension(), state.getSecondaryStateDimension(i1));
        Assert.assertEquals(secondary2.getDimension(), state.getSecondaryStateDimension(i2));

        T[] mainState             = state.getState();
        T[] mainStateDot          = state.getDerivative();
        T[] mainStateAlternate    = state.getSecondaryState(0);
        T[] mainStateDotAlternate = state.getSecondaryDerivative(0);
        Assert.assertEquals(main.getDimension(), mainState.length);
        for (int i = 0; i < main.getDimension(); ++i) {
            Assert.assertEquals(i, mainState[i].getReal(),             1.0e-15);
            Assert.assertEquals(i, mainStateDot[i].getReal(),          1.0e-15);
            Assert.assertEquals(i, mainStateAlternate[i].getReal(),    1.0e-15);
            Assert.assertEquals(i, mainStateDotAlternate[i].getReal(), 1.0e-15);
            Assert.assertEquals(i, completeDot[i].getReal(),           1.0e-15);
        }

        T[] secondaryState1    = state.getSecondaryState(i1);
        T[] secondaryState1Dot = state.getSecondaryDerivative(i1);
        Assert.assertEquals(secondary1.getDimension(), secondaryState1.length);
        for (int i = 0; i < secondary1.getDimension(); ++i) {
            Assert.assertEquals(i + main.getDimension(), secondaryState1[i].getReal(),   1.0e-15);
            Assert.assertEquals(-i, secondaryState1Dot[i].getReal(), 1.0e-15);
            Assert.assertEquals(-i, completeDot[i + main.getDimension()].getReal(),  1.0e-15);
        }

        T[] secondaryState2    = state.getSecondaryState(i2);
        T[] secondaryState2Dot = state.getSecondaryDerivative(i2);
        Assert.assertEquals(secondary2.getDimension(), secondaryState2.length);
        for (int i = 0; i < secondary2.getDimension(); ++i) {
            Assert.assertEquals(i + main.getDimension() + secondary1.getDimension(), secondaryState2[i].getReal(),   1.0e-15);
            Assert.assertEquals(-i, secondaryState2Dot[i].getReal(), 1.0e-15);
            Assert.assertEquals(-i, completeDot[i + main.getDimension() + secondary1.getDimension()].getReal(),  1.0e-15);
        }

        T[] remappedState = equation.getMapper().mapState(state);
        T[] remappedDerivative = equation.getMapper().mapDerivative(state);
        Assert.assertEquals(equation.getMapper().getTotalDimension(), remappedState.length);
        Assert.assertEquals(equation.getMapper().getTotalDimension(), remappedDerivative.length);
        for (int i = 0; i < remappedState.length; ++i) {
            Assert.assertEquals(complete[i].getReal(),    remappedState[i].getReal(),      1.0e-15);
            Assert.assertEquals(completeDot[i].getReal(), remappedDerivative[i].getReal(), 1.0e-15);
        }
    }

    @Test(expected=DimensionMismatchException.class)
    public void testExtractDimensionMismatch() {
        doTestExtractDimensionMismatch(Decimal64Field.getInstance());
    }

    private <T extends RealFieldElement<T>> void doTestExtractDimensionMismatch(final Field<T> field)
        throws DimensionMismatchException {

        FirstOrderFieldDifferentialEquations<T> main = new Linear<>(field, 3, 0);
        FieldExpandableODE<T> equation = new FieldExpandableODE<>(main);
        FieldSecondaryEquations<T> secondary1 = new Linear<>(field, 3, main.getDimension());
        int i1 = equation.addSecondaryEquations(secondary1);
        T[] tooShort    = MathArrays.buildArray(field, main.getDimension());
        equation.getMapper().extractEquationData(i1, tooShort);
    }

    @Test(expected=DimensionMismatchException.class)
    public void testInsertTooShortComplete() {
        doTestInsertTooShortComplete(Decimal64Field.getInstance());
    }

    private <T extends RealFieldElement<T>> void doTestInsertTooShortComplete(final Field<T> field)
        throws DimensionMismatchException {

        FirstOrderFieldDifferentialEquations<T> main = new Linear<>(field, 3, 0);
        FieldExpandableODE<T> equation = new FieldExpandableODE<>(main);
        FieldSecondaryEquations<T> secondary1 = new Linear<>(field, 3, main.getDimension());
        int i1 = equation.addSecondaryEquations(secondary1);
        T[] equationData = MathArrays.buildArray(field, secondary1.getDimension());
        T[] tooShort     = MathArrays.buildArray(field, main.getDimension());
        equation.getMapper().insertEquationData(i1, equationData, tooShort);
    }

    @Test(expected=DimensionMismatchException.class)
    public void testInsertWrongEquationData() {
        doTestInsertWrongEquationData(Decimal64Field.getInstance());
    }

    private <T extends RealFieldElement<T>> void doTestInsertWrongEquationData(final Field<T> field)
        throws DimensionMismatchException {

        FirstOrderFieldDifferentialEquations<T> main = new Linear<>(field, 3, 0);
        FieldExpandableODE<T> equation = new FieldExpandableODE<>(main);
        FieldSecondaryEquations<T> secondary1 = new Linear<>(field, 3, main.getDimension());
        int i1 = equation.addSecondaryEquations(secondary1);
        T[] wrongEquationData = MathArrays.buildArray(field, secondary1.getDimension() + 1);
        T[] complete          = MathArrays.buildArray(field, equation.getMapper().getTotalDimension());
        equation.getMapper().insertEquationData(i1, wrongEquationData, complete);
    }

    @Test(expected=MathIllegalArgumentException.class)
    public void testNegativeIndex() {
        doTestNegativeIndex(Decimal64Field.getInstance());
    }

    private <T extends RealFieldElement<T>> void doTestNegativeIndex(final Field<T> field)
        throws MathIllegalArgumentException {

        FirstOrderFieldDifferentialEquations<T> main = new Linear<>(field, 3, 0);
        FieldExpandableODE<T> equation = new FieldExpandableODE<>(main);
        T[] complete = MathArrays.buildArray(field, equation.getMapper().getTotalDimension());
        equation.getMapper().extractEquationData(-1, complete);
    }

    @Test(expected=MathIllegalArgumentException.class)
    public void testTooLargeIndex() {
        doTestTooLargeIndex(Decimal64Field.getInstance());
    }

    private <T extends RealFieldElement<T>> void doTestTooLargeIndex(final Field<T> field)
        throws MathIllegalArgumentException {

        FirstOrderFieldDifferentialEquations<T> main = new Linear<>(field, 3, 0);
        FieldExpandableODE<T> equation = new FieldExpandableODE<>(main);
        T[] complete = MathArrays.buildArray(field, equation.getMapper().getTotalDimension());
        equation.getMapper().extractEquationData(+1, complete);
    }

    private static final class  Linear<T extends RealFieldElement<T>>
        implements  FirstOrderFieldDifferentialEquations<T>, FieldSecondaryEquations<T> {

        private final Field<T> field;
        private final int dimension;
        private final int start;

        private Linear(final Field<T> field, final int dimension, final int start) {
            this.field     = field;
            this.dimension = dimension;
            this.start     = start;
        }

        @Override
        public int getDimension() {
            return dimension;
        }

        @Override
        public void init(final T t0, final T[] y0, final T finalTime) {
            Assert.assertEquals(dimension, y0.length);
            Assert.assertEquals(10.0,  t0.getReal(), 1.0e-15);
            Assert.assertEquals(100.0, finalTime.getReal(), 1.0e-15);
            for (int i = 0; i < y0.length; ++i) {
                Assert.assertEquals(i, y0[i].getReal(), 1.0e-15);
            }
        }

        @Override
        public T[] computeDerivatives(final T t, final T[] y) {
            final T[] yDot = MathArrays.buildArray(field, dimension);
            for (int i = 0; i < dimension; ++i) {
                yDot[i] = field.getZero().add(i);
            }
            return yDot;
        }

        @Override
        public void init(final T t0, final T[] primary0, final T[] secondary0, final T finalTime) {
            Assert.assertEquals(dimension, secondary0.length);
            Assert.assertEquals(10.0,  t0.getReal(), 1.0e-15);
            Assert.assertEquals(100.0, finalTime.getReal(), 1.0e-15);
            for (int i = 0; i < primary0.length; ++i) {
                Assert.assertEquals(i, primary0[i].getReal(), 1.0e-15);
            }
            for (int i = 0; i < secondary0.length; ++i) {
                Assert.assertEquals(start + i, secondary0[i].getReal(), 1.0e-15);
            }
        }

        @Override
        public T[] computeDerivatives(final T t, final T[] primary, final T[] primaryDot, final T[] secondary) {
            final T[] secondaryDot = MathArrays.buildArray(field, dimension);
            for (int i = 0; i < dimension; ++i) {
                secondaryDot[i] = field.getZero().subtract(i);
            }
            return secondaryDot;
        }
    }
}
