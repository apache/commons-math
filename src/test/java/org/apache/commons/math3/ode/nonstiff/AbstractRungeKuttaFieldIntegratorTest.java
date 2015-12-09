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
import org.apache.commons.math3.ode.events.Action;
import org.apache.commons.math3.ode.events.FieldEventHandler;
import org.apache.commons.math3.ode.sampling.FieldStepHandler;
import org.apache.commons.math3.ode.sampling.FieldStepInterpolator;
import org.apache.commons.math3.ode.sampling.StepInterpolatorTestUtils;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;
import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractRungeKuttaFieldIntegratorTest {

    protected abstract <T extends RealFieldElement<T>> RungeKuttaFieldIntegrator<T>
        createIntegrator(Field<T> field, T step);

    @Test
    public abstract void testNonFieldIntegratorConsistency();

    protected <T extends RealFieldElement<T>> void doTestNonFieldIntegratorConsistency(final Field<T> field) {
        try {

            // get the Butcher arrays from the field integrator
            RungeKuttaFieldIntegrator<T> fieldIntegrator = createIntegrator(field, field.getZero().add(1));
            T[][] fieldA = fieldIntegrator.getA();
            T[]   fieldB = fieldIntegrator.getB();
            T[]   fieldC = fieldIntegrator.getC();

            String fieldName   = fieldIntegrator.getClass().getName();
            String regularName = fieldName.replaceAll("Field", "");

            // get the Butcher arrays from the regular integrator
            @SuppressWarnings("unchecked")
            Class<RungeKuttaIntegrator> c = (Class<RungeKuttaIntegrator>) Class.forName(regularName);
            java.lang.reflect.Field jlrFieldA = c.getDeclaredField("STATIC_A");
            jlrFieldA.setAccessible(true);
            double[][] regularA = (double[][]) jlrFieldA.get(null);
            java.lang.reflect.Field jlrFieldB = c.getDeclaredField("STATIC_B");
            jlrFieldB.setAccessible(true);
            double[]   regularB = (double[])   jlrFieldB.get(null);
            java.lang.reflect.Field jlrFieldC = c.getDeclaredField("STATIC_C");
            jlrFieldC.setAccessible(true);
            double[]   regularC = (double[])   jlrFieldC.get(null);

            Assert.assertEquals(regularA.length, fieldA.length);
            for (int i = 0; i < regularA.length; ++i) {
                checkArray(regularA[i], fieldA[i]);
            }
            checkArray(regularB, fieldB);
            checkArray(regularC, fieldC);

        } catch (ClassNotFoundException cnfe) {
            Assert.fail(cnfe.getLocalizedMessage());
        } catch (IllegalAccessException iae) {
            Assert.fail(iae.getLocalizedMessage());
        } catch (IllegalArgumentException iae) {
            Assert.fail(iae.getLocalizedMessage());
        } catch (SecurityException se) {
            Assert.fail(se.getLocalizedMessage());
        } catch (NoSuchFieldException nsfe) {
            Assert.fail(nsfe.getLocalizedMessage());
        }
    }

    private <T extends RealFieldElement<T>> void checkArray(double[] regularArray, T[] fieldArray) {
        Assert.assertEquals(regularArray.length, fieldArray.length);
        for (int i = 0; i < regularArray.length; ++i) {
            if (regularArray[i] == 0) {
                Assert.assertTrue(0.0 == fieldArray[i].getReal());
            } else {
                Assert.assertEquals(regularArray[i], fieldArray[i].getReal(), FastMath.ulp(regularArray[i]));
            }
        }
    }

    @Test
    public abstract void testMissedEndEvent();

    protected <T extends RealFieldElement<T>> void doTestMissedEndEvent(final Field<T> field,
                                                                        final double epsilonT, final double epsilonY)
        throws DimensionMismatchException, NumberIsTooSmallException,
            MaxCountExceededException, NoBracketingException {
        final T   t0     = field.getZero().add(1878250320.0000029);
        final T   tEvent = field.getZero().add(1878250379.9999986);
        final T[] k      = MathArrays.buildArray(field, 3);
        k[0] = field.getZero().add(1.0e-4);
        k[1] = field.getZero().add(1.0e-5);
        k[2] = field.getZero().add(1.0e-6);
        FieldFirstOrderDifferentialEquations<T> ode = new FieldFirstOrderDifferentialEquations<T>() {

            public int getDimension() {
                return k.length;
            }

            public void init(T t0, T[] y0, T t) {
            }

            public T[] computeDerivatives(T t, T[] y) {
                T[] yDot = MathArrays.buildArray(field, k.length);
                for (int i = 0; i < y.length; ++i) {
                    yDot[i] = k[i].multiply(y[i]);
                }
                return yDot;
            }
        };

        RungeKuttaFieldIntegrator<T> integrator = createIntegrator(field, field.getZero().add(60.0));

        T[] y0   = MathArrays.buildArray(field, k.length);
        for (int i = 0; i < y0.length; ++i) {
            y0[i] = field.getOne().add(i);
        }

        FieldODEStateAndDerivative<T> result = integrator.integrate(new FieldExpandableODE<T>(ode),
                                                                    new FieldODEState<T>(t0, y0),
                                                                    tEvent);
        Assert.assertEquals(tEvent.getReal(), result.getTime().getReal(), epsilonT);
        T[] y = result.getState();
        for (int i = 0; i < y.length; ++i) {
            Assert.assertEquals(y0[i].multiply(k[i].multiply(result.getTime().subtract(t0)).exp()).getReal(),
                                y[i].getReal(),
                                epsilonY);
        }

        integrator.addEventHandler(new FieldEventHandler<T>() {

            public void init(FieldODEStateAndDerivative<T> state0, T t) {
            }

            public FieldODEState<T> resetState(FieldODEStateAndDerivative<T> state) {
                return state;
            }

            public T g(FieldODEStateAndDerivative<T> state) {
                return state.getTime().subtract(tEvent);
            }

            public Action eventOccurred(FieldODEStateAndDerivative<T> state, boolean increasing) {
                Assert.assertEquals(tEvent.getReal(), state.getTime().getReal(), epsilonT);
                return Action.CONTINUE;
            }
        }, Double.POSITIVE_INFINITY, 1.0e-20, 100);
        result = integrator.integrate(new FieldExpandableODE<T>(ode),
                                      new FieldODEState<T>(t0, y0),
                                      tEvent.add(120));
        Assert.assertEquals(tEvent.add(120).getReal(), result.getTime().getReal(), epsilonT);
        y = result.getState();
        for (int i = 0; i < y.length; ++i) {
            Assert.assertEquals(y0[i].multiply(k[i].multiply(result.getTime().subtract(t0)).exp()).getReal(),
                                y[i].getReal(),
                                epsilonY);
        }

    }

    @Test
    public abstract void testSanityChecks();

    protected <T extends RealFieldElement<T>> void doTestSanityChecks(Field<T> field)
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        RungeKuttaFieldIntegrator<T> integrator = createIntegrator(field, field.getZero().add(0.01));
        try  {
            TestFieldProblem1<T> pb = new TestFieldProblem1<T>(field);
            integrator.integrate(new FieldExpandableODE<T>(pb),
                                 new FieldODEState<T>(field.getZero(), MathArrays.buildArray(field, pb.getDimension() + 10)),
                                 field.getOne());
            Assert.fail("an exception should have been thrown");
        } catch(DimensionMismatchException ie) {
        }
        try  {
            TestFieldProblem1<T> pb = new TestFieldProblem1<T>(field);
            integrator.integrate(new FieldExpandableODE<T>(pb),
                                 new FieldODEState<T>(field.getZero(), MathArrays.buildArray(field, pb.getDimension())),
                                 field.getZero());
            Assert.fail("an exception should have been thrown");
        } catch(NumberIsTooSmallException ie) {
        }
    }

    @Test
    public abstract void testDecreasingSteps();

    protected <T extends RealFieldElement<T>> void doTestDecreasingSteps(Field<T> field,
                                                                         final double safetyValueFactor,
                                                                         final double safetyTimeFactor,
                                                                         final double epsilonT)
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
            for (int i = 4; i < 10; ++i) {

                T step = pb.getFinalTime().subtract(pb.getInitialState().getTime()).multiply(FastMath.pow(2.0, -i));

                RungeKuttaFieldIntegrator<T> integ = createIntegrator(field, step);
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
                    Assert.assertEquals(pb.getFinalTime().getReal(), stop.getTime().getReal(), epsilonT);
                }

                T error = handler.getMaximalValueError();
                if (i > 4) {
                    Assert.assertTrue(error.subtract(previousValueError.abs().multiply(safetyValueFactor)).getReal() < 0);
                }
                previousValueError = error;

                T timeError = handler.getMaximalTimeError();
                if (i > 4) {
                    Assert.assertTrue(timeError.subtract(previousTimeError.abs().multiply(safetyTimeFactor)).getReal() <= 0);
                }
                previousTimeError = timeError;

                integ.clearEventHandlers();
                Assert.assertEquals(0, integ.getEventHandlers().size());
            }

        }

    }

    @Test
    public abstract void testSmallStep();

    protected <T extends RealFieldElement<T>> void doTestSmallStep(Field<T> field,
                                                                   final double espilonLast,
                                                                   final double epsilonMaxValue,
                                                                   final double epsilonMaxTime,
                                                                   final String name)
         throws DimensionMismatchException, NumberIsTooSmallException,
                MaxCountExceededException, NoBracketingException {

        TestFieldProblem1<T> pb = new TestFieldProblem1<T>(field);
        T step = pb.getFinalTime().subtract(pb.getInitialState().getTime()).multiply(0.001);

        RungeKuttaFieldIntegrator<T> integ = createIntegrator(field, step);
        TestFieldProblemHandler<T> handler = new TestFieldProblemHandler<T>(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(new FieldExpandableODE<T>(pb), pb.getInitialState(), pb.getFinalTime());

        Assert.assertEquals(0, handler.getLastError().getReal(),         espilonLast);
        Assert.assertEquals(0, handler.getMaximalValueError().getReal(), epsilonMaxValue);
        Assert.assertEquals(0, handler.getMaximalTimeError().getReal(),  epsilonMaxTime);
        Assert.assertEquals(name, integ.getName());

    }

    @Test
    public abstract void testBigStep();

    protected <T extends RealFieldElement<T>> void doTestBigStep(Field<T> field,
                                                                 final double belowLast,
                                                                 final double belowMaxValue,
                                                                 final double epsilonMaxTime,
                                                                 final String name)
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {

        TestFieldProblem1<T> pb = new TestFieldProblem1<T>(field);
        T step = pb.getFinalTime().subtract(pb.getInitialState().getTime()).multiply(0.2);

        RungeKuttaFieldIntegrator<T> integ = createIntegrator(field, step);
        TestFieldProblemHandler<T> handler = new TestFieldProblemHandler<T>(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(new FieldExpandableODE<T>(pb), pb.getInitialState(), pb.getFinalTime());

        Assert.assertTrue(handler.getLastError().getReal()         > belowLast);
        Assert.assertTrue(handler.getMaximalValueError().getReal() > belowMaxValue);
        Assert.assertEquals(0, handler.getMaximalTimeError().getReal(),  epsilonMaxTime);
        Assert.assertEquals(name, integ.getName());

    }

    @Test
    public abstract void testBackward();

    protected <T extends RealFieldElement<T>> void doTestBackward(Field<T> field,
                                                                  final double espilonLast,
                                                                  final double epsilonMaxValue,
                                                                  final double epsilonMaxTime,
                                                                  final String name)
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {

        TestFieldProblem5<T> pb = new TestFieldProblem5<T>(field);
        T step = pb.getFinalTime().subtract(pb.getInitialState().getTime()).multiply(0.001).abs();

        RungeKuttaFieldIntegrator<T> integ = createIntegrator(field, step);
        TestFieldProblemHandler<T> handler = new TestFieldProblemHandler<T>(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(new FieldExpandableODE<T>(pb), pb.getInitialState(), pb.getFinalTime());

        Assert.assertEquals(0, handler.getLastError().getReal(),         espilonLast);
        Assert.assertEquals(0, handler.getMaximalValueError().getReal(), epsilonMaxValue);
        Assert.assertEquals(0, handler.getMaximalTimeError().getReal(),  epsilonMaxTime);
        Assert.assertEquals(name, integ.getName());

    }

    @Test
    public abstract void testKepler();

    protected <T extends RealFieldElement<T>> void doTestKepler(Field<T> field, double expectedMaxError, double epsilon)
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {

        final TestFieldProblem3<T> pb  = new TestFieldProblem3<T>(field, field.getZero().add(0.9));
        T step = pb.getFinalTime().subtract(pb.getInitialState().getTime()).multiply(0.0003);

        RungeKuttaFieldIntegrator<T> integ = createIntegrator(field, step);
        integ.addStepHandler(new KeplerHandler<T>(pb, expectedMaxError, epsilon));
        integ.integrate(new FieldExpandableODE<T>(pb), pb.getInitialState(), pb.getFinalTime());
    }

    private static class KeplerHandler<T extends RealFieldElement<T>> implements FieldStepHandler<T> {
        private T maxError;
        private final TestFieldProblem3<T> pb;
        private final double expectedMaxError;
        private final double epsilon;
        public KeplerHandler(TestFieldProblem3<T> pb, double expectedMaxError, double epsilon) {
            this.pb               = pb;
            this.expectedMaxError = expectedMaxError;
            this.epsilon          = epsilon;
            maxError = pb.getField().getZero();
        }
        public void init(FieldODEStateAndDerivative<T> state0, T t) {
            maxError = pb.getField().getZero();
        }
        public void handleStep(FieldStepInterpolator<T> interpolator, boolean isLast)
                        throws MaxCountExceededException {

            FieldODEStateAndDerivative<T> current = interpolator.getCurrentState();
            T[] theoreticalY  = pb.computeTheoreticalState(current.getTime());
            T dx = current.getState()[0].subtract(theoreticalY[0]);
            T dy = current.getState()[1].subtract(theoreticalY[1]);
            T error = dx.multiply(dx).add(dy.multiply(dy));
            if (error.subtract(maxError).getReal() > 0) {
                maxError = error;
            }
            if (isLast) {
                Assert.assertEquals(expectedMaxError, maxError.getReal(), epsilon);
            }
        }
    }

    @Test
    public abstract void testStepSize();

    protected <T extends RealFieldElement<T>> void doTestStepSize(final Field<T> field, final double epsilon)
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        final T step = field.getZero().add(1.23456);
        RungeKuttaFieldIntegrator<T> integ = createIntegrator(field, step);
        integ.addStepHandler(new FieldStepHandler<T>() {
            public void handleStep(FieldStepInterpolator<T> interpolator, boolean isLast) {
                if (! isLast) {
                    Assert.assertEquals(step.getReal(),
                                        interpolator.getCurrentState().getTime().subtract(interpolator.getPreviousState().getTime()).getReal(),
                                        epsilon);
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

    @Test
    public abstract void testSingleStep();

    protected <T extends RealFieldElement<T>> void doTestSingleStep(final Field<T> field, final double epsilon) {

        final TestFieldProblem3<T> pb  = new TestFieldProblem3<T>(field, field.getZero().add(0.9));
        T h = pb.getFinalTime().subtract(pb.getInitialState().getTime()).multiply(0.0003);

        RungeKuttaFieldIntegrator<T> integ = createIntegrator(field, field.getZero().add(Double.NaN));
        T   t = pb.getInitialState().getTime();
        T[] y = pb.getInitialState().getState();
        for (int i = 0; i < 100; ++i) {
            y = integ.singleStep(pb, t, y, t.add(h));
            t = t.add(h);
        }
        T[] yth = pb.computeTheoreticalState(t);
        T dx = y[0].subtract(yth[0]);
        T dy = y[1].subtract(yth[1]);
        T error = dx.multiply(dx).add(dy.multiply(dy));
        Assert.assertEquals(0.0, error.getReal(), epsilon);
    }

    @Test
    public abstract void testTooLargeFirstStep();

    protected <T extends RealFieldElement<T>> void doTestTooLargeFirstStep(final Field<T> field) {

        RungeKuttaFieldIntegrator<T> integ = createIntegrator(field, field.getZero().add(0.5));
        final T t0 = field.getZero();
        final T[] y0 = MathArrays.buildArray(field, 1);
        y0[0] = field.getOne();
        final T t   = field.getZero().add(0.001);
        FieldFirstOrderDifferentialEquations<T> equations = new FieldFirstOrderDifferentialEquations<T>() {

            public int getDimension() {
                return 1;
            }

            public void init(T t0, T[] y0, T t) {
            }

            public T[] computeDerivatives(T t, T[] y) {
                Assert.assertTrue(t.getReal() >= FastMath.nextAfter(t0.getReal(), Double.NEGATIVE_INFINITY));
                Assert.assertTrue(t.getReal() <= FastMath.nextAfter(t.getReal(),   Double.POSITIVE_INFINITY));
                T[] yDot = MathArrays.buildArray(field, 1);
                yDot[0] = y[0].multiply(-100.0);
                return yDot;
            }

        };

        integ.integrate(new FieldExpandableODE<T>(equations), new FieldODEState<T>(t0, y0), t);

    }

    @Test
    public abstract void testUnstableDerivative();

    protected <T extends RealFieldElement<T>> void doTestUnstableDerivative(Field<T> field, double epsilon) {
      final StepFieldProblem<T> stepProblem = new StepFieldProblem<T>(field,
                                                                      field.getZero().add(0.0),
                                                                      field.getZero().add(1.0),
                                                                      field.getZero().add(2.0));
      RungeKuttaFieldIntegrator<T> integ = createIntegrator(field, field.getZero().add(0.3));
      integ.addEventHandler(stepProblem, 1.0, 1.0e-12, 1000);
      FieldODEStateAndDerivative<T> result = integ.integrate(new FieldExpandableODE<T>(stepProblem),
                                                             new FieldODEState<T>(field.getZero(), MathArrays.buildArray(field, 1)),
                                                             field.getZero().add(10.0));
      Assert.assertEquals(8.0, result.getState()[0].getReal(), epsilon);
    }

    @Test
    public abstract void testDerivativesConsistency();

    protected <T extends RealFieldElement<T>> void doTestDerivativesConsistency(final Field<T> field, double epsilon) {
        TestFieldProblem3<T> pb = new TestFieldProblem3<T>(field);
        T step = pb.getFinalTime().subtract(pb.getInitialState().getTime()).multiply(0.001);
        RungeKuttaFieldIntegrator<T> integ = createIntegrator(field, step);
        StepInterpolatorTestUtils.checkDerivativesConsistency(integ, pb, 1.0e-10);
    }

}
