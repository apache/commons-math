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

import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.MaxCountExceededException;
import org.apache.commons.math4.exception.NoBracketingException;
import org.apache.commons.math4.exception.NumberIsTooSmallException;
import org.apache.commons.math4.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math4.ode.FirstOrderIntegrator;
import org.apache.commons.math4.ode.TestProblem1;
import org.apache.commons.math4.ode.TestProblem3;
import org.apache.commons.math4.ode.TestProblem4;
import org.apache.commons.math4.ode.TestProblem5;
import org.apache.commons.math4.ode.TestProblemHandler;
import org.apache.commons.math4.ode.events.EventHandler;
import org.apache.commons.math4.ode.nonstiff.AdaptiveStepsizeIntegrator;
import org.apache.commons.math4.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math4.ode.sampling.StepHandler;
import org.apache.commons.math4.ode.sampling.StepInterpolator;
import org.apache.commons.math4.util.FastMath;
import org.junit.Assert;
import org.junit.Test;


public class DormandPrince853IntegratorTest {

  @Test
  public void testMissedEndEvent()
      throws DimensionMismatchException, NumberIsTooSmallException,
             MaxCountExceededException, NoBracketingException {
      final double   t0     = 1878250320.0000029;
      final double   tEvent = 1878250379.9999986;
      final double[] k  = { 1.0e-4, 1.0e-5, 1.0e-6 };
      FirstOrderDifferentialEquations ode = new FirstOrderDifferentialEquations() {

          public int getDimension() {
              return k.length;
          }

          public void computeDerivatives(double t, double[] y, double[] yDot) {
              for (int i = 0; i < y.length; ++i) {
                  yDot[i] = k[i] * y[i];
              }
          }
      };

      DormandPrince853Integrator integrator = new DormandPrince853Integrator(0.0, 100.0,
                                                                             1.0e-10, 1.0e-10);

      double[] y0   = new double[k.length];
      for (int i = 0; i < y0.length; ++i) {
          y0[i] = i + 1;
      }
      double[] y    = new double[k.length];

      integrator.setInitialStepSize(60.0);
      double finalT = integrator.integrate(ode, t0, y0, tEvent, y);
      Assert.assertEquals(tEvent, finalT, 5.0e-6);
      for (int i = 0; i < y.length; ++i) {
          Assert.assertEquals(y0[i] * FastMath.exp(k[i] * (finalT - t0)), y[i], 1.0e-9);
      }

      integrator.setInitialStepSize(60.0);
      integrator.addEventHandler(new EventHandler() {

          public void init(double t0, double[] y0, double t) {
          }

          public void resetState(double t, double[] y) {
          }

          public double g(double t, double[] y) {
              return t - tEvent;
          }

          public Action eventOccurred(double t, double[] y, boolean increasing) {
              Assert.assertEquals(tEvent, t, 5.0e-6);
              return Action.CONTINUE;
          }
      }, Double.POSITIVE_INFINITY, 1.0e-20, 100);
      finalT = integrator.integrate(ode, t0, y0, tEvent + 120, y);
      Assert.assertEquals(tEvent + 120, finalT, 5.0e-6);
      for (int i = 0; i < y.length; ++i) {
          Assert.assertEquals(y0[i] * FastMath.exp(k[i] * (finalT - t0)), y[i], 1.0e-9);
      }

  }

  @Test(expected=DimensionMismatchException.class)
  public void testDimensionCheck()
      throws DimensionMismatchException, NumberIsTooSmallException,
             MaxCountExceededException, NoBracketingException {
      TestProblem1 pb = new TestProblem1();
      DormandPrince853Integrator integrator = new DormandPrince853Integrator(0.0, 1.0,
                                                                             1.0e-10, 1.0e-10);
      integrator.integrate(pb,
                           0.0, new double[pb.getDimension()+10],
                           1.0, new double[pb.getDimension()+10]);
      Assert.fail("an exception should have been thrown");
  }

  @Test(expected=NumberIsTooSmallException.class)
  public void testNullIntervalCheck()
      throws DimensionMismatchException, NumberIsTooSmallException,
             MaxCountExceededException, NoBracketingException {
      TestProblem1 pb = new TestProblem1();
      DormandPrince853Integrator integrator = new DormandPrince853Integrator(0.0, 1.0,
                                                                             1.0e-10, 1.0e-10);
      integrator.integrate(pb,
                           0.0, new double[pb.getDimension()],
                           0.0, new double[pb.getDimension()]);
      Assert.fail("an exception should have been thrown");
  }

  @Test(expected=NumberIsTooSmallException.class)
  public void testMinStep()
      throws DimensionMismatchException, NumberIsTooSmallException,
             MaxCountExceededException, NoBracketingException {

      TestProblem1 pb = new TestProblem1();
      double minStep = 0.1 * (pb.getFinalTime() - pb.getInitialTime());
      double maxStep = pb.getFinalTime() - pb.getInitialTime();
      double[] vecAbsoluteTolerance = { 1.0e-15, 1.0e-16 };
      double[] vecRelativeTolerance = { 1.0e-15, 1.0e-16 };

      FirstOrderIntegrator integ = new DormandPrince853Integrator(minStep, maxStep,
                                                                  vecAbsoluteTolerance,
                                                                  vecRelativeTolerance);
      TestProblemHandler handler = new TestProblemHandler(pb, integ);
      integ.addStepHandler(handler);
      integ.integrate(pb,
                      pb.getInitialTime(), pb.getInitialState(),
                      pb.getFinalTime(), new double[pb.getDimension()]);
      Assert.fail("an exception should have been thrown");

  }

  @Test
  public void testIncreasingTolerance()
      throws DimensionMismatchException, NumberIsTooSmallException,
             MaxCountExceededException, NoBracketingException {

    int previousCalls = Integer.MAX_VALUE;
    AdaptiveStepsizeIntegrator integ =
        new DormandPrince853Integrator(0, Double.POSITIVE_INFINITY,
                                       Double.NaN, Double.NaN);
    for (int i = -12; i < -2; ++i) {
      TestProblem1 pb = new TestProblem1();
      double minStep = 0;
      double maxStep = pb.getFinalTime() - pb.getInitialTime();
      double scalAbsoluteTolerance = FastMath.pow(10.0, i);
      double scalRelativeTolerance = 0.01 * scalAbsoluteTolerance;
      integ.setStepSizeControl(minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);

      TestProblemHandler handler = new TestProblemHandler(pb, integ);
      integ.addStepHandler(handler);
      integ.integrate(pb,
                      pb.getInitialTime(), pb.getInitialState(),
                      pb.getFinalTime(), new double[pb.getDimension()]);

      // the 1.3 factor is only valid for this test
      // and has been obtained from trial and error
      // there is no general relation between local and global errors
      Assert.assertTrue(handler.getMaximalValueError() < (1.3 * scalAbsoluteTolerance));
      Assert.assertEquals(0, handler.getMaximalTimeError(), 1.0e-12);

      int calls = pb.getCalls();
      Assert.assertEquals(integ.getEvaluations(), calls);
      Assert.assertTrue(calls <= previousCalls);
      previousCalls = calls;

    }

  }

  @Test
  public void testTooLargeFirstStep()
      throws DimensionMismatchException, NumberIsTooSmallException,
             MaxCountExceededException, NoBracketingException {

      AdaptiveStepsizeIntegrator integ =
              new DormandPrince853Integrator(0, Double.POSITIVE_INFINITY, Double.NaN, Double.NaN);
      final double start = 0.0;
      final double end   = 0.001;
      FirstOrderDifferentialEquations equations = new FirstOrderDifferentialEquations() {

          public int getDimension() {
              return 1;
          }

          public void computeDerivatives(double t, double[] y, double[] yDot) {
              Assert.assertTrue(t >= FastMath.nextAfter(start, Double.NEGATIVE_INFINITY));
              Assert.assertTrue(t <= FastMath.nextAfter(end,   Double.POSITIVE_INFINITY));
              yDot[0] = -100.0 * y[0];
          }

      };

      integ.setStepSizeControl(0, 1.0, 1.0e-6, 1.0e-8);
      integ.integrate(equations, start, new double[] { 1.0 }, end, new double[1]);

  }

  @Test
  public void testBackward()
      throws DimensionMismatchException, NumberIsTooSmallException,
             MaxCountExceededException, NoBracketingException {

      TestProblem5 pb = new TestProblem5();
      double minStep = 0;
      double maxStep = pb.getFinalTime() - pb.getInitialTime();
      double scalAbsoluteTolerance = 1.0e-8;
      double scalRelativeTolerance = 0.01 * scalAbsoluteTolerance;

      FirstOrderIntegrator integ = new DormandPrince853Integrator(minStep, maxStep,
                                                                  scalAbsoluteTolerance,
                                                                  scalRelativeTolerance);
      TestProblemHandler handler = new TestProblemHandler(pb, integ);
      integ.addStepHandler(handler);
      integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(),
                      pb.getFinalTime(), new double[pb.getDimension()]);

      Assert.assertTrue(handler.getLastError() < 1.1e-7);
      Assert.assertTrue(handler.getMaximalValueError() < 1.1e-7);
      Assert.assertEquals(0, handler.getMaximalTimeError(), 1.0e-12);
      Assert.assertEquals("Dormand-Prince 8 (5, 3)", integ.getName());
  }

  @Test
  public void testEvents()
      throws DimensionMismatchException, NumberIsTooSmallException,
             MaxCountExceededException, NoBracketingException {

    TestProblem4 pb = new TestProblem4();
    double minStep = 0;
    double maxStep = pb.getFinalTime() - pb.getInitialTime();
    double scalAbsoluteTolerance = 1.0e-9;
    double scalRelativeTolerance = 0.01 * scalAbsoluteTolerance;

    FirstOrderIntegrator integ = new DormandPrince853Integrator(minStep, maxStep,
                                                                scalAbsoluteTolerance,
                                                                scalRelativeTolerance);
    TestProblemHandler handler = new TestProblemHandler(pb, integ);
    integ.addStepHandler(handler);
    EventHandler[] functions = pb.getEventsHandlers();
    double convergence = 1.0e-8 * maxStep;
    for (int l = 0; l < functions.length; ++l) {
      integ.addEventHandler(functions[l], Double.POSITIVE_INFINITY, convergence, 1000);
    }
    Assert.assertEquals(functions.length, integ.getEventHandlers().size());
    integ.integrate(pb,
                    pb.getInitialTime(), pb.getInitialState(),
                    pb.getFinalTime(), new double[pb.getDimension()]);

    Assert.assertEquals(0, handler.getMaximalValueError(), 2.1e-7);
    Assert.assertEquals(0, handler.getMaximalTimeError(), convergence);
    Assert.assertEquals(12.0, handler.getLastTime(), convergence);
    integ.clearEventHandlers();
    Assert.assertEquals(0, integ.getEventHandlers().size());

  }

  @Test
  public void testKepler()
      throws DimensionMismatchException, NumberIsTooSmallException,
             MaxCountExceededException, NoBracketingException {

    final TestProblem3 pb  = new TestProblem3(0.9);
    double minStep = 0;
    double maxStep = pb.getFinalTime() - pb.getInitialTime();
    double scalAbsoluteTolerance = 1.0e-8;
    double scalRelativeTolerance = scalAbsoluteTolerance;

    FirstOrderIntegrator integ = new DormandPrince853Integrator(minStep, maxStep,
                                                                scalAbsoluteTolerance,
                                                                scalRelativeTolerance);
    integ.addStepHandler(new KeplerHandler(pb));
    integ.integrate(pb,
                    pb.getInitialTime(), pb.getInitialState(),
                    pb.getFinalTime(), new double[pb.getDimension()]);

    Assert.assertEquals(integ.getEvaluations(), pb.getCalls());
    Assert.assertTrue(pb.getCalls() < 3300);

  }

  @Test
  public void testVariableSteps()
      throws DimensionMismatchException, NumberIsTooSmallException,
             MaxCountExceededException, NoBracketingException {

    final TestProblem3 pb  = new TestProblem3(0.9);
    double minStep = 0;
    double maxStep = pb.getFinalTime() - pb.getInitialTime();
    double scalAbsoluteTolerance = 1.0e-8;
    double scalRelativeTolerance = scalAbsoluteTolerance;

    FirstOrderIntegrator integ = new DormandPrince853Integrator(minStep, maxStep,
                                                               scalAbsoluteTolerance,
                                                               scalRelativeTolerance);
    integ.addStepHandler(new VariableHandler());
    double stopTime = integ.integrate(pb,
                                      pb.getInitialTime(), pb.getInitialState(),
                                      pb.getFinalTime(), new double[pb.getDimension()]);
    Assert.assertEquals(pb.getFinalTime(), stopTime, 1.0e-10);
    Assert.assertEquals("Dormand-Prince 8 (5, 3)", integ.getName());
  }

  @Test
  public void testUnstableDerivative()
      throws DimensionMismatchException, NumberIsTooSmallException,
             MaxCountExceededException, NoBracketingException {
    final StepProblem stepProblem = new StepProblem(0.0, 1.0, 2.0);
    FirstOrderIntegrator integ =
      new DormandPrince853Integrator(0.1, 10, 1.0e-12, 0.0);
    integ.addEventHandler(stepProblem, 1.0, 1.0e-12, 1000);
    double[] y = { Double.NaN };
    integ.integrate(stepProblem, 0.0, new double[] { 0.0 }, 10.0, y);
    Assert.assertEquals(8.0, y[0], 1.0e-12);
  }

  @Test
  public void testEventsScheduling() {

      FirstOrderDifferentialEquations sincos = new FirstOrderDifferentialEquations() {

          public int getDimension() {
              return 2;
          }

          public void computeDerivatives(double t, double[] y, double[] yDot) {
              yDot[0] =  y[1];
              yDot[1] = -y[0];
          }

      };

      SchedulingChecker sinChecker = new SchedulingChecker(0); // events at 0, PI, 2PI ...
      SchedulingChecker cosChecker = new SchedulingChecker(1); // events at PI/2, 3PI/2, 5PI/2 ...

      FirstOrderIntegrator integ =
              new DormandPrince853Integrator(0.001, 1.0, 1.0e-12, 0.0);
      integ.addEventHandler(sinChecker, 0.01, 1.0e-7, 100);
      integ.addStepHandler(sinChecker);
      integ.addEventHandler(cosChecker, 0.01, 1.0e-7, 100);
      integ.addStepHandler(cosChecker);
      double   t0 = 0.5;
      double[] y0 = new double[] { FastMath.sin(t0), FastMath.cos(t0) };
      double   t  = 10.0;
      double[] y  = new double[2];
      integ.integrate(sincos, t0, y0, t, y);

  }

  private static class SchedulingChecker implements StepHandler, EventHandler {

      int index;
      double tMin;

      public SchedulingChecker(int index) {
          this.index = index;
      }

      public void init(double t0, double[] y0, double t) {
          tMin = t0;
      }

      public void handleStep(StepInterpolator interpolator, boolean isLast) {
          tMin = interpolator.getCurrentTime();
      }

      public double g(double t, double[]  y) {
          // once a step has been handled by handleStep,
          // events checking should only refer to dates after the step
          Assert.assertTrue(t >= tMin);
          return y[index];
      }

      public Action eventOccurred(double t, double[] y, boolean increasing) {
          return Action.RESET_STATE;
      }

      public void resetState(double t, double[] y) {
          // in fact, we don't need to reset anything for the test
      }

  }

  private static class KeplerHandler implements StepHandler {
    public KeplerHandler(TestProblem3 pb) {
      this.pb = pb;
    }
    public void init(double t0, double[] y0, double t) {
      nbSteps = 0;
      maxError = 0;
    }
    public void handleStep(StepInterpolator interpolator, boolean isLast)
        throws MaxCountExceededException {

      ++nbSteps;
      for (int a = 1; a < 10; ++a) {

        double prev   = interpolator.getPreviousTime();
        double curr   = interpolator.getCurrentTime();
        double interp = ((10 - a) * prev + a * curr) / 10;
        interpolator.setInterpolatedTime(interp);

        double[] interpolatedY = interpolator.getInterpolatedState ();
        double[] theoreticalY  = pb.computeTheoreticalState(interpolator.getInterpolatedTime());
        double dx = interpolatedY[0] - theoreticalY[0];
        double dy = interpolatedY[1] - theoreticalY[1];
        double error = dx * dx + dy * dy;
        if (error > maxError) {
          maxError = error;
        }
      }
      if (isLast) {
        Assert.assertTrue(maxError < 2.4e-10);
        Assert.assertTrue(nbSteps < 150);
      }
    }
    private int nbSteps;
    private double maxError;
    private TestProblem3 pb;
  }

  private static class VariableHandler implements StepHandler {
    public VariableHandler() {
        firstTime = true;
        minStep = 0;
        maxStep = 0;
    }
    public void init(double t0, double[] y0, double t) {
      firstTime = true;
      minStep = 0;
      maxStep = 0;
    }
    public void handleStep(StepInterpolator interpolator,
                           boolean isLast) {

      double step = FastMath.abs(interpolator.getCurrentTime()
                             - interpolator.getPreviousTime());
      if (firstTime) {
        minStep   = FastMath.abs(step);
        maxStep   = minStep;
        firstTime = false;
      } else {
        if (step < minStep) {
          minStep = step;
        }
        if (step > maxStep) {
          maxStep = step;
        }
      }

      if (isLast) {
        Assert.assertTrue(minStep < (1.0 / 100.0));
        Assert.assertTrue(maxStep > (1.0 / 2.0));
      }
    }
    private boolean firstTime = true;
    private double  minStep = 0;
    private double  maxStep = 0;
  }

}
