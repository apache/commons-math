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
import org.apache.commons.math3.ode.FieldFirstOrderIntegrator;
import org.apache.commons.math3.ode.FieldODEState;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
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
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;
import org.apache.commons.math3.util.Decimal64Field;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;
import org.junit.Assert;
import org.junit.Test;

public class GillFieldIntegratorTest {

    @Test(expected=DimensionMismatchException.class)
    public void testDimensionCheck()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        doTestDimensionCheck(Decimal64Field.getInstance());
    }

    private <T extends RealFieldElement<T>>void doTestDimensionCheck(Field<T> field)
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        TestFieldProblem1<T> pb = new TestFieldProblem1<T>(field);
        new GillFieldIntegrator<T>(field, field.getZero().add(0.01)).integrate(new FieldExpandableODE<>(pb),
                                                                               new FieldODEState<T>(field.getZero(), MathArrays.buildArray(field, pb.getDimension() + 10)),
                                                                               field.getOne());
          Assert.fail("an exception should have been thrown");
    }

    @Test
    public void testDecreasingSteps()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        doTestDecreasingSteps(Decimal64Field.getInstance());
    }

    private <T extends RealFieldElement<T>>void doTestDecreasingSteps(Field<T> field)
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
            for (int i = 5; i < 10; ++i) {

                T step = pb.getFinalTime().subtract(pb.getInitialState().getTime()).multiply(FastMath.pow(2.0, -i));

                FieldFirstOrderIntegrator<T> integ = new GillFieldIntegrator<T>(field, step);
                TestFieldProblemHandler<T> handler = new TestFieldProblemHandler<T>(pb, integ);
                integ.addStepHandler(handler);
                FieldEventHandler<T>[] functions = pb.getEventsHandlers();
                for (int l = 0; l < functions.length; ++l) {
                    integ.addEventHandler(functions[l],
                                          Double.POSITIVE_INFINITY, 1.0e-6 * step.getReal(), 1000);
                }
                Assert.assertEquals(functions.length, integ.getEventHandlers().size());
                FieldODEStateAndDerivative<T> stop = integ.integrate(new FieldExpandableODE<T>(pb),
                                                                     pb.getInitialState(),
                                                                     pb.getFinalTime());
                if (functions.length == 0) {
                    Assert.assertEquals(pb.getFinalTime().getReal(), stop.getTime().getReal(), 1.0e-10);
                }

                T error = handler.getMaximalValueError();
                if (i > 5) {
                    Assert.assertTrue(error.subtract(previousValueError.abs().multiply(1.01)).getReal() < 0);
                }
                previousValueError = error;

                T timeError = handler.getMaximalTimeError();
                if (i > 5) {
                    Assert.assertTrue(timeError.subtract(previousTimeError.abs()).getReal() <= 0);
                }
                previousTimeError = timeError;

                integ.clearEventHandlers();
                Assert.assertEquals(0, integ.getEventHandlers().size());
            }
        }
    }

    @Test
    public void testSmallStep()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        doTestSmallStep(Decimal64Field.getInstance());
    }

    private <T extends RealFieldElement<T>>void doTestSmallStep(Field<T> field)
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {

        TestFieldProblem1<T> pb = new TestFieldProblem1<T>(field);
        T step = pb.getFinalTime().subtract(pb.getInitialState().getTime()).multiply(0.001);

        FieldFirstOrderIntegrator<T> integ = new GillFieldIntegrator<T>(field, step);
        TestFieldProblemHandler<T> handler = new TestFieldProblemHandler<T>(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(new FieldExpandableODE<T>(pb), pb.getInitialState(), pb.getFinalTime());

        Assert.assertTrue(handler.getLastError().getReal() < 2.0e-13);
        Assert.assertTrue(handler.getMaximalValueError().getReal() < 4.0e-12);
        Assert.assertEquals(0, handler.getMaximalTimeError().getReal(), 1.0e-12);
        Assert.assertEquals("Gill", integ.getName());
    }

    @Test
    public void testBigStep()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        doTestBigStep(Decimal64Field.getInstance());
    }

    private <T extends RealFieldElement<T>>void doTestBigStep(Field<T> field)
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {

        TestFieldProblem1<T> pb = new TestFieldProblem1<T>(field);
        T step = pb.getFinalTime().subtract(pb.getInitialState().getTime()).multiply(0.2);

        FieldFirstOrderIntegrator<T> integ = new GillFieldIntegrator<T>(field, step);
        TestFieldProblemHandler<T> handler = new TestFieldProblemHandler<T>(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(new FieldExpandableODE<T>(pb), pb.getInitialState(), pb.getFinalTime());

        Assert.assertTrue(handler.getLastError().getReal() > 0.0004);
        Assert.assertTrue(handler.getMaximalValueError().getReal() > 0.005);
        Assert.assertEquals(0, handler.getMaximalTimeError().getReal(), 1.0e-12);

    }

    @Test
    public void testBackward()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        doTestBackward(Decimal64Field.getInstance());
    }

    private <T extends RealFieldElement<T>>void doTestBackward(Field<T> field)
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {

        TestFieldProblem5<T> pb = new TestFieldProblem5<T>(field);
        T step = pb.getFinalTime().subtract(pb.getInitialState().getTime()).multiply(0.001).abs();

        FieldFirstOrderIntegrator<T> integ = new GillFieldIntegrator<T>(field, step);
        TestFieldProblemHandler<T> handler = new TestFieldProblemHandler<T>(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(new FieldExpandableODE<>(pb), pb.getInitialState(), pb.getFinalTime());

        Assert.assertTrue(handler.getLastError().getReal() < 5.0e-10);
        Assert.assertTrue(handler.getMaximalValueError().getReal() < 7.0e-10);
        Assert.assertEquals(0, handler.getMaximalTimeError().getReal(), 1.0e-12);
        Assert.assertEquals("Gill", integ.getName());
    }

    @Test
    public void testKepler()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        doTestKepler(Decimal64Field.getInstance());
    }

    private <T extends RealFieldElement<T>>void doTestKepler(Field<T> field)
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {

        final TestFieldProblem3<T> pb  = new TestFieldProblem3<T>(field, field.getZero().add(0.9));
        T step = pb.getFinalTime().subtract(pb.getInitialState().getTime()).multiply(0.0003);

        FieldFirstOrderIntegrator<T> integ = new GillFieldIntegrator<T>(field, step);
        integ.addStepHandler(new KeplerHandler<T>(pb));
        integ.integrate(new FieldExpandableODE<>(pb), pb.getInitialState(), pb.getFinalTime());
    }

    @Test
    public void testUnstableDerivative()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        doTestUnstableDerivative(Decimal64Field.getInstance());
    }

    private <T extends RealFieldElement<T>>void doTestUnstableDerivative(Field<T> field)
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
      final StepFieldProblem<T> stepProblem = new StepFieldProblem<T>(field,
                                                                      field.getZero().add(0.0),
                                                                      field.getZero().add(1.0),
                                                                      field.getZero().add(2.0));
      FieldFirstOrderIntegrator<T> integ = new GillFieldIntegrator<T>(field, field.getZero().add(0.3));
      integ.addEventHandler(stepProblem, 1.0, 1.0e-12, 1000);
      FieldODEStateAndDerivative<T> result = integ.integrate(new FieldExpandableODE<>(stepProblem),
                                                             new FieldODEState<>(field.getZero(), MathArrays.buildArray(field, 1)),
                                                             field.getZero().add(10.0));
      Assert.assertEquals(8.0, result.getState()[0].getReal(), 1.0e-12);
    }

    private static class KeplerHandler<T extends RealFieldElement<T>> implements FieldStepHandler<T> {
        public KeplerHandler(TestFieldProblem3<T> pb) {
            this.pb = pb;
            maxError = pb.getField().getZero();
        }
        public void init(FieldODEStateAndDerivative<T> state0, T t) {
            maxError = pb.getField().getZero();
        }
        public void handleStep(FieldStepInterpolator<T> interpolator, boolean isLast) {

            FieldODEStateAndDerivative<T> current = interpolator.getCurrentState();
            T[] theoreticalY  = pb.computeTheoreticalState(current.getTime());
            T dx = current.getState()[0].subtract(theoreticalY[0]);
            T dy = current.getState()[1].subtract(theoreticalY[1]);
            T error = dx.multiply(dx).add(dy.multiply(dy));
            if (error.subtract(maxError).getReal() > 0) {
                maxError = error;
            }
            if (isLast) {
                // even with more than 1000 evaluations per period,
                // Gill is not able to integrate such an eccentric
                // orbit with a good accuracy
                Assert.assertTrue(maxError.getReal() > 0.001);
            }
        }
        private T maxError;
        private TestFieldProblem3<T> pb;
    }

    @Test
    public void testStepSize()
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        doTestStepSize(Decimal64Field.getInstance());
    }

    private <T extends RealFieldElement<T>>void doTestStepSize(Field<T> field)
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        final double step = 1.23456;
        FirstOrderIntegrator integ = new GillIntegrator(step);
        integ.addStepHandler(new StepHandler() {
            public void handleStep(StepInterpolator interpolator, boolean isLast) {
                if (! isLast) {
                    Assert.assertEquals(step,
                                 interpolator.getCurrentTime() - interpolator.getPreviousTime(),
                                 1.0e-12);
                }
            }
            public void init(double t0, double[] y0, double t) {
            }
        });
        integ.integrate(new FirstOrderDifferentialEquations() {
            public void computeDerivatives(double t, double[] y, double[] dot) {
                dot[0] = 1.0;
            }
            public int getDimension() {
                return 1;
            }
        }, 0.0, new double[] { 0.0 }, 5.0, new double[1]);
    }

}
