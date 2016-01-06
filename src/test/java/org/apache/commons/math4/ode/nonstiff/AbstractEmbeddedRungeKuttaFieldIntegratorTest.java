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

package org.apache.commons.math4.ode.nonstiff;


import org.apache.commons.math4.Field;
import org.apache.commons.math4.RealFieldElement;
import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.MaxCountExceededException;
import org.apache.commons.math4.exception.NoBracketingException;
import org.apache.commons.math4.exception.NumberIsTooSmallException;
import org.apache.commons.math4.ode.FieldExpandableODE;
import org.apache.commons.math4.ode.FieldFirstOrderDifferentialEquations;
import org.apache.commons.math4.ode.FieldFirstOrderIntegrator;
import org.apache.commons.math4.ode.FieldODEState;
import org.apache.commons.math4.ode.FieldODEStateAndDerivative;
import org.apache.commons.math4.ode.TestFieldProblem1;
import org.apache.commons.math4.ode.TestFieldProblem3;
import org.apache.commons.math4.ode.TestFieldProblem4;
import org.apache.commons.math4.ode.TestFieldProblem5;
import org.apache.commons.math4.ode.TestFieldProblemHandler;
import org.apache.commons.math4.ode.events.Action;
import org.apache.commons.math4.ode.events.FieldEventHandler;
import org.apache.commons.math4.ode.sampling.FieldStepHandler;
import org.apache.commons.math4.ode.sampling.FieldStepInterpolator;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.math4.util.MathArrays;
import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractEmbeddedRungeKuttaFieldIntegratorTest {

    protected abstract <T extends RealFieldElement<T>> EmbeddedRungeKuttaFieldIntegrator<T>
    createIntegrator(Field<T> field, final double minStep, final double maxStep,
                     final double scalAbsoluteTolerance, final double scalRelativeTolerance) ;

    protected abstract <T extends RealFieldElement<T>> EmbeddedRungeKuttaFieldIntegrator<T>
    createIntegrator(Field<T> field, final double minStep, final double maxStep,
                     final double[] vecAbsoluteTolerance, final double[] vecRelativeTolerance);

    @Test
    public abstract void testNonFieldIntegratorConsistency();

    protected <T extends RealFieldElement<T>> void doTestNonFieldIntegratorConsistency(final Field<T> field) {
        try {

            // get the Butcher arrays from the field integrator
            EmbeddedRungeKuttaFieldIntegrator<T> fieldIntegrator = createIntegrator(field, 0.001, 1.0, 1.0, 1.0);
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
    public abstract void testForwardBackwardExceptions();

    protected <T extends RealFieldElement<T>> void doTestForwardBackwardExceptions(final Field<T> field) {
        FieldFirstOrderDifferentialEquations<T> equations = new FieldFirstOrderDifferentialEquations<T>() {

            public int getDimension() {
                return 1;
            }

            public void init(T t0, T[] y0, T t) {
            }

            public T[] computeDerivatives(T t, T[] y) {
                if (t.getReal() < -0.5) {
                    throw new LocalException();
                } else {
                    throw new RuntimeException("oops");
                }
            }
        };

        EmbeddedRungeKuttaFieldIntegrator<T> integrator = createIntegrator(field, 0.0, 1.0, 1.0e-10, 1.0e-10);

        try  {
            integrator.integrate(new FieldExpandableODE<>(equations),
                                 new FieldODEState<T>(field.getOne().negate(),
                                                      MathArrays.buildArray(field, 1)),
                                 field.getZero());
            Assert.fail("an exception should have been thrown");
          } catch(LocalException de) {
            // expected behavior
          }

          try  {
              integrator.integrate(new FieldExpandableODE<>(equations),
                                   new FieldODEState<T>(field.getZero(),
                                                        MathArrays.buildArray(field, 1)),
                                   field.getOne());
               Assert.fail("an exception should have been thrown");
          } catch(RuntimeException de) {
            // expected behavior
          }
    }

    protected static class LocalException extends RuntimeException {
        private static final long serialVersionUID = 20151208L;
    }

    @Test(expected=NumberIsTooSmallException.class)
    public abstract void testMinStep();

    protected <T extends RealFieldElement<T>> void doTestMinStep(final Field<T> field)
        throws NumberIsTooSmallException {

        TestFieldProblem1<T> pb = new TestFieldProblem1<T>(field);
        double minStep = pb.getFinalTime().subtract(pb.getInitialState().getTime()).multiply(0.1).getReal();
        double maxStep = pb.getFinalTime().subtract(pb.getInitialState().getTime()).getReal();
        double[] vecAbsoluteTolerance = { 1.0e-15, 1.0e-16 };
        double[] vecRelativeTolerance = { 1.0e-15, 1.0e-16 };

        FieldFirstOrderIntegrator<T> integ = createIntegrator(field, minStep, maxStep,
                                                              vecAbsoluteTolerance, vecRelativeTolerance);
        TestFieldProblemHandler<T> handler = new TestFieldProblemHandler<T>(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(new FieldExpandableODE<>(pb), pb.getInitialState(), pb.getFinalTime());
        Assert.fail("an exception should have been thrown");

    }

    @Test
    public abstract void testIncreasingTolerance();

    protected <T extends RealFieldElement<T>> void doTestIncreasingTolerance(final Field<T> field,
                                                                             double factor,
                                                                             double epsilon) {

        int previousCalls = Integer.MAX_VALUE;
        for (int i = -12; i < -2; ++i) {
            TestFieldProblem1<T> pb = new TestFieldProblem1<T>(field);
            double minStep = 0;
            double maxStep = pb.getFinalTime().subtract(pb.getInitialState().getTime()).getReal();
            double scalAbsoluteTolerance = FastMath.pow(10.0, i);
            double scalRelativeTolerance = 0.01 * scalAbsoluteTolerance;

            FieldFirstOrderIntegrator<T> integ = createIntegrator(field, minStep, maxStep,
                                                                  scalAbsoluteTolerance, scalRelativeTolerance);
            TestFieldProblemHandler<T> handler = new TestFieldProblemHandler<T>(pb, integ);
            integ.addStepHandler(handler);
            integ.integrate(new FieldExpandableODE<T>(pb), pb.getInitialState(), pb.getFinalTime());

            Assert.assertTrue(handler.getMaximalValueError().getReal() < (factor * scalAbsoluteTolerance));
            Assert.assertEquals(0, handler.getMaximalTimeError().getReal(), epsilon);

            int calls = pb.getCalls();
            Assert.assertEquals(integ.getEvaluations(), calls);
            Assert.assertTrue(calls <= previousCalls);
            previousCalls = calls;

        }

    }

    @Test
    public abstract void testEvents();

    protected <T extends RealFieldElement<T>> void doTestEvents(final Field<T> field,
                                                                final double epsilonMaxValue,
                                                                final String name) {

      TestFieldProblem4<T> pb = new TestFieldProblem4<T>(field);
      double minStep = 0;
      double maxStep = pb.getFinalTime().subtract(pb.getInitialState().getTime()).getReal();
      double scalAbsoluteTolerance = 1.0e-8;
      double scalRelativeTolerance = 0.01 * scalAbsoluteTolerance;

      FieldFirstOrderIntegrator<T> integ = createIntegrator(field, minStep, maxStep,
                                                            scalAbsoluteTolerance, scalRelativeTolerance);
      TestFieldProblemHandler<T> handler = new TestFieldProblemHandler<T>(pb, integ);
      integ.addStepHandler(handler);
      FieldEventHandler<T>[] functions = pb.getEventsHandlers();
      double convergence = 1.0e-8 * maxStep;
      for (int l = 0; l < functions.length; ++l) {
          integ.addEventHandler(functions[l], Double.POSITIVE_INFINITY, convergence, 1000);
      }
      Assert.assertEquals(functions.length, integ.getEventHandlers().size());
      integ.integrate(new FieldExpandableODE<T>(pb), pb.getInitialState(), pb.getFinalTime());

      Assert.assertEquals(0, handler.getMaximalValueError().getReal(), epsilonMaxValue);
      Assert.assertEquals(0, handler.getMaximalTimeError().getReal(), convergence);
      Assert.assertEquals(12.0, handler.getLastTime().getReal(), convergence);
      Assert.assertEquals(name, integ.getName());
      integ.clearEventHandlers();
      Assert.assertEquals(0, integ.getEventHandlers().size());

    }

    @Test(expected=LocalException.class)
    public abstract void testEventsErrors();

    protected <T extends RealFieldElement<T>> void doTestEventsErrors(final Field<T> field)
        throws LocalException {
        final TestFieldProblem1<T> pb = new TestFieldProblem1<T>(field);
        double minStep = 0;
        double maxStep = pb.getFinalTime().subtract(pb.getInitialState().getTime()).getReal();
        double scalAbsoluteTolerance = 1.0e-8;
        double scalRelativeTolerance = 0.01 * scalAbsoluteTolerance;

        FieldFirstOrderIntegrator<T> integ = createIntegrator(field, minStep, maxStep,
                                                              scalAbsoluteTolerance, scalRelativeTolerance);
        TestFieldProblemHandler<T> handler = new TestFieldProblemHandler<T>(pb, integ);
        integ.addStepHandler(handler);

        integ.addEventHandler(new FieldEventHandler<T>() {
          public void init(FieldODEStateAndDerivative<T> state0, T t) {
          }
          public Action eventOccurred(FieldODEStateAndDerivative<T> state, boolean increasing) {
            return Action.CONTINUE;
          }
          public T g(FieldODEStateAndDerivative<T> state) {
            T middle = pb.getInitialState().getTime().add(pb.getFinalTime()).multiply(0.5);
            T offset = state.getTime().subtract(middle);
            if (offset.getReal() > 0) {
              throw new LocalException();
            }
            return offset;
          }
          public FieldODEState<T> resetState(FieldODEStateAndDerivative<T> state) {
              return state;
          }
        }, Double.POSITIVE_INFINITY, 1.0e-8 * maxStep, 1000);

        integ.integrate(new FieldExpandableODE<T>(pb), pb.getInitialState(), pb.getFinalTime());

    }

    @Test
    public abstract void testEventsNoConvergence();

    protected <T extends RealFieldElement<T>> void doTestEventsNoConvergence(final Field<T> field){

        final TestFieldProblem1<T> pb = new TestFieldProblem1<T>(field);
        double minStep = 0;
        double maxStep = pb.getFinalTime().subtract(pb.getInitialState().getTime()).getReal();
        double scalAbsoluteTolerance = 1.0e-8;
        double scalRelativeTolerance = 0.01 * scalAbsoluteTolerance;

        FieldFirstOrderIntegrator<T> integ = createIntegrator(field, minStep, maxStep,
                                                              scalAbsoluteTolerance, scalRelativeTolerance);
        TestFieldProblemHandler<T> handler = new TestFieldProblemHandler<T>(pb, integ);
        integ.addStepHandler(handler);

        integ.addEventHandler(new FieldEventHandler<T>() {
            public void init(FieldODEStateAndDerivative<T> state0, T t) {
            }
            public Action eventOccurred(FieldODEStateAndDerivative<T> state, boolean increasing) {
                return Action.CONTINUE;
            }
            public T g(FieldODEStateAndDerivative<T> state) {
                T middle = pb.getInitialState().getTime().add(pb.getFinalTime()).multiply(0.5);
                T offset = state.getTime().subtract(middle);
                return (offset.getReal() > 0) ? offset.add(0.5) : offset.subtract(0.5);
            }
            public FieldODEState<T> resetState(FieldODEStateAndDerivative<T> state) {
                return state;
            }
        }, Double.POSITIVE_INFINITY, 1.0e-8 * maxStep, 3);

        try {
            integ.integrate(new FieldExpandableODE<T>(pb), pb.getInitialState(), pb.getFinalTime());
            Assert.fail("an exception should have been thrown");
        } catch (MaxCountExceededException mcee) {
            // Expected.
        }

    }

    @Test
    public abstract void testSanityChecks();

    protected <T extends RealFieldElement<T>> void doTestSanityChecks(Field<T> field) {
        TestFieldProblem3<T> pb = new TestFieldProblem3<T>(field);
        try  {
            EmbeddedRungeKuttaFieldIntegrator<T> integrator = createIntegrator(field, 0,
                                                                               pb.getFinalTime().subtract(pb.getInitialState().getTime()).getReal(),
                                                                               new double[4], new double[4]);
            integrator.integrate(new FieldExpandableODE<>(pb),
                                 new FieldODEState<T>(pb.getInitialState().getTime(),
                                                      MathArrays.buildArray(field, 6)),
                                 pb.getFinalTime());
            Assert.fail("an exception should have been thrown");
        } catch(DimensionMismatchException ie) {
        }
        try  {
            EmbeddedRungeKuttaFieldIntegrator<T> integrator =
                            createIntegrator(field, 0,
                                             pb.getFinalTime().subtract(pb.getInitialState().getTime()).getReal(),
                                             new double[2], new double[4]);
            integrator.integrate(new FieldExpandableODE<>(pb), pb.getInitialState(), pb.getFinalTime());
            Assert.fail("an exception should have been thrown");
        } catch(DimensionMismatchException ie) {
        }
        try  {
            EmbeddedRungeKuttaFieldIntegrator<T> integrator =
                            createIntegrator(field, 0,
                                             pb.getFinalTime().subtract(pb.getInitialState().getTime()).getReal(),
                                             new double[4], new double[4]);
            integrator.integrate(new FieldExpandableODE<>(pb), pb.getInitialState(), pb.getInitialState().getTime());
            Assert.fail("an exception should have been thrown");
        } catch(NumberIsTooSmallException ie) {
        }
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
        double minStep = 0;
        double maxStep = pb.getFinalTime().subtract(pb.getInitialState().getTime()).abs().getReal();
        double scalAbsoluteTolerance = 1.0e-8;
        double scalRelativeTolerance = 0.01 * scalAbsoluteTolerance;

        EmbeddedRungeKuttaFieldIntegrator<T> integ = createIntegrator(field, minStep, maxStep,
                                                                      scalAbsoluteTolerance,
                                                                      scalRelativeTolerance);
        TestFieldProblemHandler<T> handler = new TestFieldProblemHandler<T>(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(new FieldExpandableODE<>(pb), pb.getInitialState(), pb.getFinalTime());

        Assert.assertEquals(0, handler.getLastError().getReal(),         espilonLast);
        Assert.assertEquals(0, handler.getMaximalValueError().getReal(), epsilonMaxValue);
        Assert.assertEquals(0, handler.getMaximalTimeError().getReal(),  epsilonMaxTime);
        Assert.assertEquals(name, integ.getName());

    }

    @Test
    public abstract void testKepler();

    protected <T extends RealFieldElement<T>> void doTestKepler(Field<T> field, double epsilon) {

        final TestFieldProblem3<T> pb  = new TestFieldProblem3<T>(field, field.getZero().add(0.9));
        double minStep = 0;
        double maxStep = pb.getFinalTime().subtract(pb.getInitialState().getTime()).getReal();
        double[] vecAbsoluteTolerance = { 1.0e-8, 1.0e-8, 1.0e-10, 1.0e-10 };
        double[] vecRelativeTolerance = { 1.0e-10, 1.0e-10, 1.0e-8, 1.0e-8 };

        FieldFirstOrderIntegrator<T> integ = createIntegrator(field, minStep, maxStep,
                                                              vecAbsoluteTolerance, vecRelativeTolerance);
        integ.addStepHandler(new KeplerHandler<T>(pb, epsilon));
        integ.integrate(new FieldExpandableODE<>(pb), pb.getInitialState(), pb.getFinalTime());
    }

    private static class KeplerHandler<T extends RealFieldElement<T>> implements FieldStepHandler<T> {
        private T maxError;
        private final TestFieldProblem3<T> pb;
        private final double epsilon;
        public KeplerHandler(TestFieldProblem3<T> pb, double epsilon) {
            this.pb      = pb;
            this.epsilon = epsilon;
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
                Assert.assertEquals(0.0, maxError.getReal(), epsilon);
            }
        }
    }

}
