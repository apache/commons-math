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


import java.lang.reflect.Array;

import org.apache.commons.math4.legacy.core.Field;
import org.apache.commons.math4.legacy.core.RealFieldElement;
import org.apache.commons.math4.legacy.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.MaxCountExceededException;
import org.apache.commons.math4.legacy.exception.NoBracketingException;
import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;
import org.apache.commons.math4.legacy.ode.FieldExpandableODE;
import org.apache.commons.math4.legacy.ode.FirstOrderFieldDifferentialEquations;
import org.apache.commons.math4.legacy.ode.FieldODEState;
import org.apache.commons.math4.legacy.ode.FieldODEStateAndDerivative;
import org.apache.commons.math4.legacy.ode.TestFieldProblem1;
import org.apache.commons.math4.legacy.ode.TestFieldProblem2;
import org.apache.commons.math4.legacy.ode.TestFieldProblem3;
import org.apache.commons.math4.legacy.ode.TestFieldProblem4;
import org.apache.commons.math4.legacy.ode.TestFieldProblem5;
import org.apache.commons.math4.legacy.ode.TestFieldProblem6;
import org.apache.commons.math4.legacy.ode.TestFieldProblemAbstract;
import org.apache.commons.math4.legacy.ode.TestFieldProblemHandler;
import org.apache.commons.math4.legacy.ode.events.Action;
import org.apache.commons.math4.legacy.ode.events.FieldEventHandler;
import org.apache.commons.math4.legacy.ode.sampling.FieldStepHandler;
import org.apache.commons.math4.legacy.ode.sampling.FieldStepInterpolator;
import org.apache.commons.math4.legacy.ode.sampling.StepInterpolatorTestUtils;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.apache.commons.math4.legacy.core.MathArrays;
import org.junit.Assert;
import org.junit.Test;

public abstract class RungeKuttaFieldIntegratorAbstractTest {

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
                Assert.assertEquals(0.0, fieldArray[i].getReal(), 0.0);
            } else {
                Assert.assertEquals(regularArray[i], fieldArray[i].getReal(), JdkMath.ulp(regularArray[i]));
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
        FirstOrderFieldDifferentialEquations<T> ode = new FirstOrderFieldDifferentialEquations<T>() {

            @Override
            public int getDimension() {
                return k.length;
            }

            @Override
            public void init(T t0, T[] y0, T t) {
            }

            @Override
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

        FieldODEStateAndDerivative<T> result = integrator.integrate(new FieldExpandableODE<>(ode),
                                                                    new FieldODEState<>(t0, y0),
                                                                    tEvent);
        Assert.assertEquals(tEvent.getReal(), result.getTime().getReal(), epsilonT);
        T[] y = result.getState();
        for (int i = 0; i < y.length; ++i) {
            Assert.assertEquals(y0[i].multiply(k[i].multiply(result.getTime().subtract(t0)).exp()).getReal(),
                                y[i].getReal(),
                                epsilonY);
        }

        integrator.addEventHandler(new FieldEventHandler<T>() {

            @Override
            public void init(FieldODEStateAndDerivative<T> state0, T t) {
            }

            @Override
            public FieldODEState<T> resetState(FieldODEStateAndDerivative<T> state) {
                return state;
            }

            @Override
            public T g(FieldODEStateAndDerivative<T> state) {
                return state.getTime().subtract(tEvent);
            }

            @Override
            public Action eventOccurred(FieldODEStateAndDerivative<T> state, boolean increasing) {
                Assert.assertEquals(tEvent.getReal(), state.getTime().getReal(), epsilonT);
                return Action.CONTINUE;
            }
        }, Double.POSITIVE_INFINITY, 1.0e-20, 100);
        result = integrator.integrate(new FieldExpandableODE<>(ode),
                                      new FieldODEState<>(t0, y0),
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
            TestFieldProblem1<T> pb = new TestFieldProblem1<>(field);
            integrator.integrate(new FieldExpandableODE<>(pb),
                                 new FieldODEState<>(field.getZero(), MathArrays.buildArray(field, pb.getDimension() + 10)),
                                 field.getOne());
            Assert.fail("an exception should have been thrown");
        } catch(DimensionMismatchException ie) {
        }
        try  {
            TestFieldProblem1<T> pb = new TestFieldProblem1<>(field);
            integrator.integrate(new FieldExpandableODE<>(pb),
                                 new FieldODEState<>(field.getZero(), MathArrays.buildArray(field, pb.getDimension())),
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
        allProblems[0] = new TestFieldProblem1<>(field);
        allProblems[1] = new TestFieldProblem2<>(field);
        allProblems[2] = new TestFieldProblem3<>(field);
        allProblems[3] = new TestFieldProblem4<>(field);
        allProblems[4] = new TestFieldProblem5<>(field);
        allProblems[5] = new TestFieldProblem6<>(field);
        for (TestFieldProblemAbstract<T> pb :  allProblems) {

            T previousValueError = null;
            T previousTimeError  = null;
            for (int i = 4; i < 10; ++i) {

                T step = pb.getFinalTime().subtract(pb.getInitialState().getTime()).multiply(JdkMath.pow(2.0, -i));

                RungeKuttaFieldIntegrator<T> integ = createIntegrator(field, step);
                TestFieldProblemHandler<T> handler = new TestFieldProblemHandler<>(pb, integ);
                integ.addStepHandler(handler);
                FieldEventHandler<T>[] functions = pb.getEventsHandlers();
                for (int l = 0; l < functions.length; ++l) {
                    integ.addEventHandler(functions[l],
                                          Double.POSITIVE_INFINITY, 1.0e-6 * step.getReal(), 1000);
                }
                Assert.assertEquals(functions.length, integ.getEventHandlers().size());
                FieldODEStateAndDerivative<T> stop = integ.integrate(new FieldExpandableODE<>(pb),
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
                                                                   final double epsilonLast,
                                                                   final double epsilonMaxValue,
                                                                   final double epsilonMaxTime,
                                                                   final String name)
         throws DimensionMismatchException, NumberIsTooSmallException,
                MaxCountExceededException, NoBracketingException {

        TestFieldProblem1<T> pb = new TestFieldProblem1<>(field);
        T step = pb.getFinalTime().subtract(pb.getInitialState().getTime()).multiply(0.001);

        RungeKuttaFieldIntegrator<T> integ = createIntegrator(field, step);
        TestFieldProblemHandler<T> handler = new TestFieldProblemHandler<>(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(new FieldExpandableODE<>(pb), pb.getInitialState(), pb.getFinalTime());

        Assert.assertEquals(0, handler.getLastError().getReal(),         epsilonLast);
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

        TestFieldProblem1<T> pb = new TestFieldProblem1<>(field);
        T step = pb.getFinalTime().subtract(pb.getInitialState().getTime()).multiply(0.2);

        RungeKuttaFieldIntegrator<T> integ = createIntegrator(field, step);
        TestFieldProblemHandler<T> handler = new TestFieldProblemHandler<>(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(new FieldExpandableODE<>(pb), pb.getInitialState(), pb.getFinalTime());

        Assert.assertTrue(handler.getLastError().getReal()         > belowLast);
        Assert.assertTrue(handler.getMaximalValueError().getReal() > belowMaxValue);
        Assert.assertEquals(0, handler.getMaximalTimeError().getReal(),  epsilonMaxTime);
        Assert.assertEquals(name, integ.getName());

    }

    @Test
    public abstract void testBackward();

    protected <T extends RealFieldElement<T>> void doTestBackward(Field<T> field,
                                                                  final double epsilonLast,
                                                                  final double epsilonMaxValue,
                                                                  final double epsilonMaxTime,
                                                                  final String name)
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {

        TestFieldProblem5<T> pb = new TestFieldProblem5<>(field);
        T step = pb.getFinalTime().subtract(pb.getInitialState().getTime()).multiply(0.001).abs();

        RungeKuttaFieldIntegrator<T> integ = createIntegrator(field, step);
        TestFieldProblemHandler<T> handler = new TestFieldProblemHandler<>(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(new FieldExpandableODE<>(pb), pb.getInitialState(), pb.getFinalTime());

        Assert.assertEquals(0, handler.getLastError().getReal(),         epsilonLast);
        Assert.assertEquals(0, handler.getMaximalValueError().getReal(), epsilonMaxValue);
        Assert.assertEquals(0, handler.getMaximalTimeError().getReal(),  epsilonMaxTime);
        Assert.assertEquals(name, integ.getName());

    }

    @Test
    public abstract void testKepler();

    protected <T extends RealFieldElement<T>> void doTestKepler(Field<T> field, double expectedMaxError, double epsilon)
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {

        final TestFieldProblem3<T> pb  = new TestFieldProblem3<>(field, field.getZero().add(0.9));
        T step = pb.getFinalTime().subtract(pb.getInitialState().getTime()).multiply(0.0003);

        RungeKuttaFieldIntegrator<T> integ = createIntegrator(field, step);
        integ.addStepHandler(new KeplerHandler<>(pb, expectedMaxError, epsilon));
        integ.integrate(new FieldExpandableODE<>(pb), pb.getInitialState(), pb.getFinalTime());
    }

    private static class KeplerHandler<T extends RealFieldElement<T>> implements FieldStepHandler<T> {
        private T maxError;
        private final TestFieldProblem3<T> pb;
        private final double expectedMaxError;
        private final double epsilon;
        KeplerHandler(TestFieldProblem3<T> pb, double expectedMaxError, double epsilon) {
            this.pb               = pb;
            this.expectedMaxError = expectedMaxError;
            this.epsilon          = epsilon;
            maxError = pb.getField().getZero();
        }
        @Override
        public void init(FieldODEStateAndDerivative<T> state0, T t) {
            maxError = pb.getField().getZero();
        }
        @Override
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
            @Override
            public void handleStep(FieldStepInterpolator<T> interpolator, boolean isLast) {
                if (! isLast) {
                    Assert.assertEquals(step.getReal(),
                                        interpolator.getCurrentState().getTime().subtract(interpolator.getPreviousState().getTime()).getReal(),
                                        epsilon);
                }
            }
            @Override
            public void init(FieldODEStateAndDerivative<T> s0, T t) {
            }
        });
        integ.integrate(new FieldExpandableODE<>(new FirstOrderFieldDifferentialEquations<T>() {
            @Override
            public void init(T t0, T[] y0, T t) {
            }
            @Override
            public T[] computeDerivatives(T t, T[] y) {
                T[] dot = MathArrays.buildArray(t.getField(), 1);
                dot[0] = t.getField().getOne();
                return dot;
            }
            @Override
            public int getDimension() {
                return 1;
            }
        }), new FieldODEState<>(field.getZero(), MathArrays.buildArray(field, 1)), field.getZero().add(5.0));
    }

    @Test
    public abstract void testSingleStep();

    protected <T extends RealFieldElement<T>> void doTestSingleStep(final Field<T> field, final double epsilon) {

        final TestFieldProblem3<T> pb  = new TestFieldProblem3<>(field, field.getZero().add(0.9));
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
        FirstOrderFieldDifferentialEquations<T> equations = new FirstOrderFieldDifferentialEquations<T>() {

            @Override
            public int getDimension() {
                return 1;
            }

            @Override
            public void init(T t0, T[] y0, T t) {
            }

            @Override
            public T[] computeDerivatives(T t, T[] y) {
                Assert.assertTrue(t.getReal() >= JdkMath.nextAfter(t0.getReal(), Double.NEGATIVE_INFINITY));
                Assert.assertTrue(t.getReal() <= JdkMath.nextAfter(t.getReal(),   Double.POSITIVE_INFINITY));
                T[] yDot = MathArrays.buildArray(field, 1);
                yDot[0] = y[0].multiply(-100.0);
                return yDot;
            }

        };

        integ.integrate(new FieldExpandableODE<>(equations), new FieldODEState<>(t0, y0), t);

    }

    @Test
    public abstract void testUnstableDerivative();

    protected <T extends RealFieldElement<T>> void doTestUnstableDerivative(Field<T> field, double epsilon) {
      final StepFieldProblem<T> stepProblem = new StepFieldProblem<>(field,
                                                                      field.getZero().add(0.0),
                                                                      field.getZero().add(1.0),
                                                                      field.getZero().add(2.0));
      RungeKuttaFieldIntegrator<T> integ = createIntegrator(field, field.getZero().add(0.3));
      integ.addEventHandler(stepProblem, 1.0, 1.0e-12, 1000);
      FieldODEStateAndDerivative<T> result = integ.integrate(new FieldExpandableODE<>(stepProblem),
                                                             new FieldODEState<>(field.getZero(), MathArrays.buildArray(field, 1)),
                                                             field.getZero().add(10.0));
      Assert.assertEquals(8.0, result.getState()[0].getReal(), epsilon);
    }

    @Test
    public abstract void testDerivativesConsistency();

    protected <T extends RealFieldElement<T>> void doTestDerivativesConsistency(final Field<T> field, double epsilon) {
        TestFieldProblem3<T> pb = new TestFieldProblem3<>(field);
        T step = pb.getFinalTime().subtract(pb.getInitialState().getTime()).multiply(0.001);
        RungeKuttaFieldIntegrator<T> integ = createIntegrator(field, step);
        StepInterpolatorTestUtils.checkDerivativesConsistency(integ, pb, 1.0e-10);
    }

    @Test
    public abstract void testPartialDerivatives();

    protected void doTestPartialDerivatives(final double epsilonY,
                                            final double[] epsilonPartials) {

        // parameters indices
        final int parameters = 5;
        final int order      = 1;
        final int parOmega   = 0;
        final int parTO      = 1;
        final int parY00     = 2;
        final int parY01     = 3;
        final int parT       = 4;

        DerivativeStructure omega = new DerivativeStructure(parameters, order, parOmega, 1.3);
        DerivativeStructure t0    = new DerivativeStructure(parameters, order, parTO, 1.3);
        DerivativeStructure[] y0  = new DerivativeStructure[] {
            new DerivativeStructure(parameters, order, parY00, 3.0),
            new DerivativeStructure(parameters, order, parY01, 4.0)
        };
        DerivativeStructure t     = new DerivativeStructure(parameters, order, parT, 6.0);
        SinCos sinCos = new SinCos(omega);

        RungeKuttaFieldIntegrator<DerivativeStructure> integrator =
                        createIntegrator(omega.getField(), t.subtract(t0).multiply(0.001));
        FieldODEStateAndDerivative<DerivativeStructure> result =
                        integrator.integrate(new FieldExpandableODE<>(sinCos),
                                             new FieldODEState<>(t0, y0),
                                             t);

        // check values
        for (int i = 0; i < sinCos.getDimension(); ++i) {
            Assert.assertEquals(sinCos.theoreticalY(t.getReal())[i], result.getState()[i].getValue(), epsilonY);
        }

        // check derivatives
        final double[][] derivatives = sinCos.getDerivatives(t.getReal());
        for (int i = 0; i < sinCos.getDimension(); ++i) {
            for (int parameter = 0; parameter < parameters; ++parameter) {
                Assert.assertEquals(derivatives[i][parameter],
                                    dYdP(result.getState()[i], parameter),
                                    epsilonPartials[parameter]);
            }
        }

    }

    private double dYdP(final DerivativeStructure y, final int parameter) {
        int[] orders = new int[y.getFreeParameters()];
        orders[parameter] = 1;
        return y.getPartialDerivative(orders);
    }

    private static class SinCos implements FirstOrderFieldDifferentialEquations<DerivativeStructure> {

        private final DerivativeStructure omega;
        private       DerivativeStructure r;
        private       DerivativeStructure alpha;

        private double dRdY00;
        private double dRdY01;
        private double dAlphadOmega;
        private double dAlphadT0;
        private double dAlphadY00;
        private double dAlphadY01;

        protected SinCos(final DerivativeStructure omega) {
            this.omega = omega;
        }

        @Override
        public int getDimension() {
            return 2;
        }

        @Override
        public void init(final DerivativeStructure t0, final DerivativeStructure[] y0,
                         final DerivativeStructure finalTime) {

            // theoretical solution is y(t) = { r * sin(omega * t + alpha), r * cos(omega * t + alpha) }
            // so we retrieve alpha by identification from the initial state
            final DerivativeStructure r2 = y0[0].multiply(y0[0]).add(y0[1].multiply(y0[1]));

            this.r            = r2.sqrt();
            this.dRdY00       = y0[0].divide(r).getReal();
            this.dRdY01       = y0[1].divide(r).getReal();

            this.alpha        = y0[0].atan2(y0[1]).subtract(t0.multiply(omega));
            this.dAlphadOmega = -t0.getReal();
            this.dAlphadT0    = -omega.getReal();
            this.dAlphadY00   = y0[1].divide(r2).getReal();
            this.dAlphadY01   = y0[0].negate().divide(r2).getReal();

        }

        @Override
        public DerivativeStructure[] computeDerivatives(final DerivativeStructure t, final DerivativeStructure[] y) {
            return new DerivativeStructure[] {
                omega.multiply(y[1]),
                omega.multiply(y[0]).negate()
            };
        }

        public double[] theoreticalY(final double t) {
            final double theta = omega.getReal() * t + alpha.getReal();
            return new double[] {
                r.getReal() * JdkMath.sin(theta), r.getReal() * JdkMath.cos(theta)
            };
        }

        public double[][] getDerivatives(final double t) {

            // intermediate angle and state
            final double theta        = omega.getReal() * t + alpha.getReal();
            final double sin          = JdkMath.sin(theta);
            final double cos          = JdkMath.cos(theta);
            final double y0           = r.getReal() * sin;
            final double y1           = r.getReal() * cos;

            // partial derivatives of the state first component
            final double dY0dOmega    =                y1 * (t + dAlphadOmega);
            final double dY0dT0       =                y1 * dAlphadT0;
            final double dY0dY00      = dRdY00 * sin + y1 * dAlphadY00;
            final double dY0dY01      = dRdY01 * sin + y1 * dAlphadY01;
            final double dY0dT        =                y1 * omega.getReal();

            // partial derivatives of the state second component
            final double dY1dOmega    =              - y0 * (t + dAlphadOmega);
            final double dY1dT0       =              - y0 * dAlphadT0;
            final double dY1dY00      = dRdY00 * cos - y0 * dAlphadY00;
            final double dY1dY01      = dRdY01 * cos - y0 * dAlphadY01;
            final double dY1dT        =              - y0 * omega.getReal();

            return new double[][] {
                { dY0dOmega, dY0dT0, dY0dY00, dY0dY01, dY0dT },
                { dY1dOmega, dY1dT0, dY1dY00, dY1dY01, dY1dT }
            };

        }

    }

}
