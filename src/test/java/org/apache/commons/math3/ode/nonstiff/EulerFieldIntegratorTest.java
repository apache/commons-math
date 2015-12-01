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


import java.lang.reflect.Array;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.FieldExpandableODE;
import org.apache.commons.math3.ode.FieldFirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FieldFirstOrderIntegrator;
import org.apache.commons.math3.ode.FieldODEState;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.ode.TestFieldProblem1;
import org.apache.commons.math3.ode.TestFieldProblem2;
import org.apache.commons.math3.ode.TestFieldProblem3;
import org.apache.commons.math3.ode.TestFieldProblem4;
import org.apache.commons.math3.ode.TestFieldProblem5;
import org.apache.commons.math3.ode.TestFieldProblem6;
import org.apache.commons.math3.ode.TestFieldProblemAbstract;
import org.apache.commons.math3.ode.TestFieldProblemHandler;
import org.apache.commons.math3.ode.events.FieldEventHandler;
import org.apache.commons.math3.ode.sampling.FieldStepHandler;
import org.apache.commons.math3.ode.sampling.FieldStepInterpolator;
import org.apache.commons.math3.util.Decimal64Field;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;
import org.junit.Assert;
import org.junit.Test;

public class EulerFieldIntegratorTest {

    @Test(expected=DimensionMismatchException.class)
    public void testDimensionCheck()
        throws DimensionMismatchException, NumberIsTooSmallException,
        MaxCountExceededException, NoBracketingException {
        doTestDimensionCheck(Decimal64Field.getInstance());
    }

    private <T extends RealFieldElement<T>> void doTestDimensionCheck(Field<T> field)
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        TestFieldProblemAbstract<T> pb = new TestFieldProblem1<T>(field);
        FieldExpandableODE<T> ode = new FieldExpandableODE<T>(pb);
        FieldFirstOrderIntegrator<T> integ =
                        new EulerFieldIntegrator<T>(field, field.getZero().add(0.01));
        FieldODEState<T> start =
                        new FieldODEState<T>(field.getZero().add(0.0),
                                             MathArrays.buildArray(field, pb.getDimension() + 10));
        integ.integrate(ode, start, field.getZero().add(1.0));
    }

    @Test
    public void testDecreasingSteps()
        throws DimensionMismatchException, NumberIsTooSmallException,
        MaxCountExceededException, NoBracketingException {
        dotTestDecreasingSteps(Decimal64Field.getInstance());
    }

    private <T extends RealFieldElement<T>> void dotTestDecreasingSteps(Field<T> field)
        throws DimensionMismatchException, NumberIsTooSmallException,
        MaxCountExceededException, NoBracketingException {

        @SuppressWarnings("unchecked")
        TestFieldProblemAbstract<T>[] allProblems =
                        (TestFieldProblemAbstract<T>[]) Array.newInstance(TestFieldProblemAbstract.class, 6);
        allProblems[0] = new TestFieldProblem1<T>(field);
        allProblems[1] = new TestFieldProblem2<T>(field);
        allProblems[2] = new TestFieldProblem3<T>(field);
        allProblems[3] = new TestFieldProblem4<T>(field);
        allProblems[4] = new TestFieldProblem5<T>(field);
        allProblems[5] = new TestFieldProblem6<T>(field);
        for (TestFieldProblemAbstract<T> pb :  allProblems) {

            T previousValueError = null;
            T previousTimeError  = null;
            for (int i = 4; i < 8; ++i) {

                T step = pb.getFinalTime().subtract(pb.getInitialState().getTime()).multiply(FastMath.pow(2.0, -i));

                FieldFirstOrderIntegrator<T> integ = new EulerFieldIntegrator<T>(field, step);
                TestFieldProblemHandler<T> handler = new TestFieldProblemHandler<T>(pb, integ);
                integ.addStepHandler(handler);
                FieldEventHandler<T>[] functions = pb.getEventsHandlers();
                for (int l = 0; l < functions.length; ++l) {
                    integ.addEventHandler(functions[l],
                                          Double.POSITIVE_INFINITY, 1.0e-6 * step.getReal(), 1000);
                }
                FieldODEStateAndDerivative<T> stop = integ.integrate(new FieldExpandableODE<T>(pb),
                                                                     pb.getInitialState(),
                                                                     pb.getFinalTime());
                if (functions.length == 0) {
                    Assert.assertEquals(pb.getFinalTime().getReal(), stop.getTime().getReal(), 1.0e-10);
                }

                T valueError = handler.getMaximalValueError();
                if (i > 4) {
                    Assert.assertTrue(valueError.subtract(previousValueError.abs()).getReal() < 0);
                }
                previousValueError = valueError;

                T timeError = handler.getMaximalTimeError();
                if (i > 4) {
                    Assert.assertTrue(timeError.subtract(previousTimeError.abs()).getReal() <= 0);
                }
                previousTimeError = timeError;

            }

        }

    }

    @Test
    public void testSmallStep()
        throws DimensionMismatchException, NumberIsTooSmallException,
        MaxCountExceededException, NoBracketingException {
        doTestSmallStep(Decimal64Field.getInstance());
    }

    private <T extends RealFieldElement<T>> void doTestSmallStep(Field<T> field)
        throws DimensionMismatchException, NumberIsTooSmallException,
        MaxCountExceededException, NoBracketingException {

        TestFieldProblem1<T> pb  = new TestFieldProblem1<T>(field);
        T step = pb.getFinalTime().subtract(pb.getInitialState().getTime()).multiply(0.001);

        FieldFirstOrderIntegrator<T> integ = new EulerFieldIntegrator<T>(field, step);
        TestFieldProblemHandler<T> handler = new TestFieldProblemHandler<T>(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(new FieldExpandableODE<T>(pb), pb.getInitialState(), pb.getFinalTime());

        Assert.assertTrue(handler.getLastError().getReal() < 2.0e-4);
        Assert.assertTrue(handler.getMaximalValueError().getReal() < 1.0e-3);
        Assert.assertEquals(0, handler.getMaximalTimeError().getReal(), 1.0e-12);
        Assert.assertEquals("Euler", integ.getName());

    }

    @Test
    public void testBigStep()
        throws DimensionMismatchException, NumberIsTooSmallException,
        MaxCountExceededException, NoBracketingException {
        doTestBigStep(Decimal64Field.getInstance());
    }

    private <T extends RealFieldElement<T>> void doTestBigStep(Field<T> field)
        throws DimensionMismatchException, NumberIsTooSmallException,
        MaxCountExceededException, NoBracketingException {

        TestFieldProblem1<T> pb  = new TestFieldProblem1<T>(field);
        T step = pb.getFinalTime().subtract(pb.getInitialState().getTime()).multiply(0.2);

        FieldFirstOrderIntegrator<T> integ = new EulerFieldIntegrator<T>(field, step);
        TestFieldProblemHandler<T> handler = new TestFieldProblemHandler<T>(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(new FieldExpandableODE<T>(pb), pb.getInitialState(), pb.getFinalTime());

        Assert.assertTrue(handler.getLastError().getReal() > 0.01);
        Assert.assertTrue(handler.getMaximalValueError().getReal() > 0.2);
        Assert.assertEquals(0, handler.getMaximalTimeError().getReal(), 1.0e-12);

    }

    @Test
    public void testBackward()
        throws DimensionMismatchException, NumberIsTooSmallException,
        MaxCountExceededException, NoBracketingException {
        doTestBackward(Decimal64Field.getInstance());
    }

    private <T extends RealFieldElement<T>> void doTestBackward(Field<T> field)
        throws DimensionMismatchException, NumberIsTooSmallException,
        MaxCountExceededException, NoBracketingException {

        TestFieldProblem5<T> pb = new TestFieldProblem5<T>(field);
        T step = pb.getFinalTime().subtract(pb.getInitialState().getTime()).multiply(0.001).abs();

        FieldFirstOrderIntegrator<T> integ = new EulerFieldIntegrator<T>(field, step);
        TestFieldProblemHandler<T> handler = new TestFieldProblemHandler<T>(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(new FieldExpandableODE<>(pb), pb.getInitialState(), pb.getFinalTime());

        Assert.assertTrue(handler.getLastError().getReal() < 0.45);
        Assert.assertTrue(handler.getMaximalValueError().getReal() < 0.45);
        Assert.assertEquals(0, handler.getMaximalTimeError().getReal(), 1.0e-12);
        Assert.assertEquals("Euler", integ.getName());
    }

    @Test
    public void testStepSize()
        throws DimensionMismatchException, NumberIsTooSmallException,
        MaxCountExceededException, NoBracketingException {
        doTestStepSize(Decimal64Field.getInstance());
    }

    private <T extends RealFieldElement<T>> void doTestStepSize(Field<T> field)
        throws DimensionMismatchException, NumberIsTooSmallException,
        MaxCountExceededException, NoBracketingException {
        final T step = field.getZero().add(1.23456);
        FieldFirstOrderIntegrator<T> integ = new EulerFieldIntegrator<T>(field, step);
        integ.addStepHandler(new FieldStepHandler<T>() {
            public void handleStep(FieldStepInterpolator<T> interpolator, boolean isLast) {
                if (! isLast) {
                    Assert.assertEquals(step.getReal(),
                                        interpolator.getCurrentState().getTime().subtract(interpolator.getPreviousState().getTime()).getReal(),
                                        1.0e-12);
                }
            }
            public void init(FieldODEStateAndDerivative<T> s0, T t) {
            }
        });
        integ.integrate(new FieldExpandableODE<T>(new FieldFirstOrderDifferentialEquations<T>() {
            public void init(T t0, T[] y0, T t) {
            }
            public T[] computeDerivatives(T t, T[] y) {
                T[] dot = MathArrays.buildArray(t.getField(), 1);
                dot[0] = t.getField().getOne();
                return dot;
            }
            public int getDimension() {
                return 1;
            }
        }), new FieldODEState<T>(field.getZero(), MathArrays.buildArray(field, 1)), field.getZero().add(5.0));
    }

}
