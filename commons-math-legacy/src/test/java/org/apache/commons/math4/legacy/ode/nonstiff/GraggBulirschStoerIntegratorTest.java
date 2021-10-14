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

import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.MaxCountExceededException;
import org.apache.commons.math4.legacy.exception.NoBracketingException;
import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;
import org.apache.commons.math4.legacy.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math4.legacy.ode.FirstOrderIntegrator;
import org.apache.commons.math4.legacy.ode.TestProblem1;
import org.apache.commons.math4.legacy.ode.TestProblem3;
import org.apache.commons.math4.legacy.ode.TestProblem4;
import org.apache.commons.math4.legacy.ode.TestProblem5;
import org.apache.commons.math4.legacy.ode.TestProblemAbstract;
import org.apache.commons.math4.legacy.ode.TestProblemHandler;
import org.apache.commons.math4.legacy.ode.events.EventHandler;
import org.apache.commons.math4.legacy.ode.sampling.StepHandler;
import org.apache.commons.math4.legacy.ode.sampling.StepInterpolator;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.junit.Assert;
import org.junit.Test;


public class GraggBulirschStoerIntegratorTest {

  @Test(expected=DimensionMismatchException.class)
  public void testDimensionCheck()
      throws DimensionMismatchException, NumberIsTooSmallException,
             MaxCountExceededException, NoBracketingException {
      TestProblem1 pb = new TestProblem1();
      AdaptiveStepsizeIntegrator integrator =
        new GraggBulirschStoerIntegrator(0.0, 1.0, 1.0e-10, 1.0e-10);
      integrator.integrate(pb,
                           0.0, new double[pb.getDimension()+10],
                           1.0, new double[pb.getDimension()+10]);
  }

  @Test(expected=NumberIsTooSmallException.class)
  public void testNullIntervalCheck()
      throws DimensionMismatchException, NumberIsTooSmallException,
             MaxCountExceededException, NoBracketingException {
      TestProblem1 pb = new TestProblem1();
      GraggBulirschStoerIntegrator integrator =
        new GraggBulirschStoerIntegrator(0.0, 1.0, 1.0e-10, 1.0e-10);
      integrator.integrate(pb,
                           0.0, new double[pb.getDimension()],
                           0.0, new double[pb.getDimension()]);
  }

  @Test(expected=NumberIsTooSmallException.class)
  public void testMinStep()
      throws DimensionMismatchException, NumberIsTooSmallException,
             MaxCountExceededException, NoBracketingException {

      TestProblem5 pb  = new TestProblem5();
      double minStep   = 0.1 * JdkMath.abs(pb.getFinalTime() - pb.getInitialTime());
      double maxStep   = JdkMath.abs(pb.getFinalTime() - pb.getInitialTime());
      double[] vecAbsoluteTolerance = { 1.0e-20, 1.0e-21 };
      double[] vecRelativeTolerance = { 1.0e-20, 1.0e-21 };

      FirstOrderIntegrator integ =
        new GraggBulirschStoerIntegrator(minStep, maxStep,
                                         vecAbsoluteTolerance, vecRelativeTolerance);
      TestProblemHandler handler = new TestProblemHandler(pb, integ);
      integ.addStepHandler(handler);
      integ.integrate(pb,
                      pb.getInitialTime(), pb.getInitialState(),
                      pb.getFinalTime(), new double[pb.getDimension()]);

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

      FirstOrderIntegrator integ = new GraggBulirschStoerIntegrator(minStep, maxStep,
                                                                    scalAbsoluteTolerance,
                                                                    scalRelativeTolerance);
      TestProblemHandler handler = new TestProblemHandler(pb, integ);
      integ.addStepHandler(handler);
      integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(),
                      pb.getFinalTime(), new double[pb.getDimension()]);

      Assert.assertTrue(handler.getLastError() < 7.5e-9);
      Assert.assertTrue(handler.getMaximalValueError() < 8.1e-9);
      Assert.assertEquals(0, handler.getMaximalTimeError(), 1.0e-12);
      Assert.assertEquals("Gragg-Bulirsch-Stoer", integ.getName());
  }

  @Test
  public void testIncreasingTolerance()
      throws DimensionMismatchException, NumberIsTooSmallException,
             MaxCountExceededException, NoBracketingException {

    int previousCalls = Integer.MAX_VALUE;
    for (int i = -12; i < -4; ++i) {
      TestProblem1 pb     = new TestProblem1();
      double minStep      = 0;
      double maxStep      = pb.getFinalTime() - pb.getInitialTime();
      double absTolerance = JdkMath.pow(10.0, i);
      double relTolerance = absTolerance;

      FirstOrderIntegrator integ =
        new GraggBulirschStoerIntegrator(minStep, maxStep,
                                         absTolerance, relTolerance);
      TestProblemHandler handler = new TestProblemHandler(pb, integ);
      integ.addStepHandler(handler);
      integ.integrate(pb,
                      pb.getInitialTime(), pb.getInitialState(),
                      pb.getFinalTime(), new double[pb.getDimension()]);

      // the coefficients are only valid for this test
      // and have been obtained from trial and error
      // there is no general relation between local and global errors
      double ratio =  handler.getMaximalValueError() / absTolerance;
      Assert.assertTrue(ratio < 2.4);
      Assert.assertTrue(ratio > 0.02);
      Assert.assertEquals(0, handler.getMaximalTimeError(), 1.0e-12);

      int calls = pb.getCalls();
      Assert.assertEquals(integ.getEvaluations(), calls);
      Assert.assertTrue(calls <= previousCalls);
      previousCalls = calls;

    }

  }

  @Test
  public void testIntegratorControls()
      throws DimensionMismatchException, NumberIsTooSmallException,
             MaxCountExceededException, NoBracketingException {

    TestProblem3 pb = new TestProblem3(0.999);
    GraggBulirschStoerIntegrator integ =
        new GraggBulirschStoerIntegrator(0, pb.getFinalTime() - pb.getInitialTime(),
                1.0e-8, 1.0e-10);

    double errorWithDefaultSettings = getMaxError(integ, pb);

    // stability control
    integ.setStabilityCheck(true, 2, 1, 0.99);
    Assert.assertTrue(errorWithDefaultSettings < getMaxError(integ, pb));
    integ.setStabilityCheck(true, -1, -1, -1);

    integ.setControlFactors(0.5, 0.99, 0.1, 2.5);
    Assert.assertTrue(errorWithDefaultSettings < getMaxError(integ, pb));
    integ.setControlFactors(-1, -1, -1, -1);

    integ.setOrderControl(10, 0.7, 0.95);
    Assert.assertTrue(errorWithDefaultSettings < getMaxError(integ, pb));
    integ.setOrderControl(-1, -1, -1);

    integ.setInterpolationControl(true, 3);
    Assert.assertTrue(errorWithDefaultSettings < getMaxError(integ, pb));
    integ.setInterpolationControl(true, -1);

  }

  private double getMaxError(FirstOrderIntegrator integrator, TestProblemAbstract pb)
      throws DimensionMismatchException, NumberIsTooSmallException,
             MaxCountExceededException, NoBracketingException {
      TestProblemHandler handler = new TestProblemHandler(pb, integrator);
      integrator.addStepHandler(handler);
      integrator.integrate(pb,
                           pb.getInitialTime(), pb.getInitialState(),
                           pb.getFinalTime(), new double[pb.getDimension()]);
      return handler.getMaximalValueError();
  }

  @Test
  public void testEvents()
      throws DimensionMismatchException, NumberIsTooSmallException,
             MaxCountExceededException, NoBracketingException {

    TestProblem4 pb = new TestProblem4();
    double minStep = 0;
    double maxStep = pb.getFinalTime() - pb.getInitialTime();
    double scalAbsoluteTolerance = 1.0e-10;
    double scalRelativeTolerance = 0.01 * scalAbsoluteTolerance;

    FirstOrderIntegrator integ = new GraggBulirschStoerIntegrator(minStep, maxStep,
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

    Assert.assertTrue(handler.getMaximalValueError() < 4.0e-7);
    Assert.assertEquals(0, handler.getMaximalTimeError(), convergence);
    Assert.assertEquals(12.0, handler.getLastTime(), convergence);
    integ.clearEventHandlers();
    Assert.assertEquals(0, integ.getEventHandlers().size());

  }

  @Test
  public void testKepler()
      throws DimensionMismatchException, NumberIsTooSmallException,
             MaxCountExceededException, NoBracketingException {

    final TestProblem3 pb = new TestProblem3(0.9);
    double minStep        = 0;
    double maxStep        = pb.getFinalTime() - pb.getInitialTime();
    double absTolerance   = 1.0e-6;
    double relTolerance   = 1.0e-6;

    FirstOrderIntegrator integ =
      new GraggBulirschStoerIntegrator(minStep, maxStep,
                                       absTolerance, relTolerance);
    integ.addStepHandler(new KeplerStepHandler(pb));
    integ.integrate(pb,
                    pb.getInitialTime(), pb.getInitialState(),
                    pb.getFinalTime(), new double[pb.getDimension()]);

    Assert.assertEquals(integ.getEvaluations(), pb.getCalls());
    Assert.assertTrue(pb.getCalls() < 2150);

  }

  @Test
  public void testVariableSteps()
      throws DimensionMismatchException, NumberIsTooSmallException,
             MaxCountExceededException, NoBracketingException {

    final TestProblem3 pb = new TestProblem3(0.9);
    double minStep        = 0;
    double maxStep        = pb.getFinalTime() - pb.getInitialTime();
    double absTolerance   = 1.0e-8;
    double relTolerance   = 1.0e-8;
    FirstOrderIntegrator integ =
      new GraggBulirschStoerIntegrator(minStep, maxStep,
                                       absTolerance, relTolerance);
    integ.addStepHandler(new VariableStepHandler());
    double stopTime = integ.integrate(pb,
                                      pb.getInitialTime(), pb.getInitialState(),
                                      pb.getFinalTime(), new double[pb.getDimension()]);
    Assert.assertEquals(pb.getFinalTime(), stopTime, 1.0e-10);
    Assert.assertEquals("Gragg-Bulirsch-Stoer", integ.getName());
  }

  @Test
  public void testTooLargeFirstStep()
      throws DimensionMismatchException, NumberIsTooSmallException,
             MaxCountExceededException, NoBracketingException {

      AdaptiveStepsizeIntegrator integ =
              new GraggBulirschStoerIntegrator(0, Double.POSITIVE_INFINITY, Double.NaN, Double.NaN);
      final double start = 0.0;
      final double end   = 0.001;
      FirstOrderDifferentialEquations equations = new FirstOrderDifferentialEquations() {

          @Override
        public int getDimension() {
              return 1;
          }

          @Override
        public void computeDerivatives(double t, double[] y, double[] yDot) {
              Assert.assertTrue(t >= JdkMath.nextAfter(start, Double.NEGATIVE_INFINITY));
              Assert.assertTrue(t <= JdkMath.nextAfter(end,   Double.POSITIVE_INFINITY));
              yDot[0] = -100.0 * y[0];
          }

      };

      integ.setStepSizeControl(0, 1.0, 1.0e-6, 1.0e-8);
      integ.integrate(equations, start, new double[] { 1.0 }, end, new double[1]);

  }

  @Test
  public void testUnstableDerivative()
      throws DimensionMismatchException, NumberIsTooSmallException,
             MaxCountExceededException, NoBracketingException {
    final StepProblem stepProblem = new StepProblem(0.0, 1.0, 2.0);
    FirstOrderIntegrator integ =
      new GraggBulirschStoerIntegrator(0.1, 10, 1.0e-12, 0.0);
    integ.addEventHandler(stepProblem, 1.0, 1.0e-12, 1000);
    double[] y = { Double.NaN };
    integ.integrate(stepProblem, 0.0, new double[] { 0.0 }, 10.0, y);
    Assert.assertEquals(8.0, y[0], 1.0e-12);
  }

  @Test
  public void testIssue596()
      throws DimensionMismatchException, NumberIsTooSmallException,
             MaxCountExceededException, NoBracketingException {
    FirstOrderIntegrator integ = new GraggBulirschStoerIntegrator(1e-10, 100.0, 1e-7, 1e-7);
      integ.addStepHandler(new StepHandler() {

          @Override
        public void init(double t0, double[] y0, double t) {
          }

          @Override
        public void handleStep(StepInterpolator interpolator, boolean isLast)
              throws MaxCountExceededException {
              double t = interpolator.getCurrentTime();
              interpolator.setInterpolatedTime(t);
              double[] y = interpolator.getInterpolatedState();
              double[] yDot = interpolator.getInterpolatedDerivatives();
              Assert.assertEquals(3.0 * t - 5.0, y[0], 1.0e-14);
              Assert.assertEquals(3.0, yDot[0], 1.0e-14);
          }
      });
      double[] y = {4.0};
      double t0 = 3.0;
      double tend = 10.0;
      integ.integrate(new FirstOrderDifferentialEquations() {
          @Override
        public int getDimension() {
              return 1;
          }

          @Override
        public void computeDerivatives(double t, double[] y, double[] yDot) {
              yDot[0] = 3.0;
          }
      }, t0, y, tend, y);

  }

  private static class KeplerStepHandler implements StepHandler {
    KeplerStepHandler(TestProblem3 pb) {
      this.pb = pb;
    }
    @Override
    public void init(double t0, double[] y0, double t) {
      nbSteps = 0;
      maxError = 0;
    }
    @Override
    public void handleStep(StepInterpolator interpolator, boolean isLast)
        throws MaxCountExceededException {

      ++nbSteps;
      for (int a = 1; a < 100; ++a) {

        double prev   = interpolator.getPreviousTime();
        double curr   = interpolator.getCurrentTime();
        double interp = ((100 - a) * prev + a * curr) / 100;
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
        Assert.assertTrue(maxError < 2.7e-6);
        Assert.assertTrue(nbSteps < 80);
      }
    }
    private int nbSteps;
    private double maxError;
    private TestProblem3 pb;
  }

  public static class VariableStepHandler implements StepHandler {
    public VariableStepHandler() {
        firstTime = true;
        minStep = 0;
        maxStep = 0;
    }
    @Override
    public void init(double t0, double[] y0, double t) {
      firstTime = true;
      minStep = 0;
      maxStep = 0;
    }
    @Override
    public void handleStep(StepInterpolator interpolator,
                           boolean isLast) {

      double step = JdkMath.abs(interpolator.getCurrentTime()
                             - interpolator.getPreviousTime());
      if (firstTime) {
        minStep   = JdkMath.abs(step);
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
        Assert.assertTrue(minStep < 8.2e-3);
        Assert.assertTrue(maxStep > 1.5);
      }
    }
    private boolean firstTime;
    private double  minStep;
    private double  maxStep;
  }

}
