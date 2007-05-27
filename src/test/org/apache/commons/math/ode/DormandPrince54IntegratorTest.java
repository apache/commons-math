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

package org.apache.commons.math.ode;

import org.apache.commons.math.ode.AdaptiveStepsizeIntegrator;
import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.DormandPrince54Integrator;
import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.StepHandler;
import org.apache.commons.math.ode.StepInterpolator;
import org.apache.commons.math.ode.SwitchingFunction;

import junit.framework.*;

public class DormandPrince54IntegratorTest
  extends TestCase {

  public DormandPrince54IntegratorTest(String name) {
    super(name);
  }

  public void testDimensionCheck() {
    try  {
      TestProblem1 pb = new TestProblem1();
      DormandPrince54Integrator integrator = new DormandPrince54Integrator(0.0, 1.0,
                                                                           1.0e-10, 1.0e-10);
      integrator.integrate(pb,
                           0.0, new double[pb.getDimension()+10],
                           1.0, new double[pb.getDimension()+10]);
      fail("an exception should have been thrown");
    } catch(DerivativeException de) {
      fail("wrong exception caught");
    } catch(IntegratorException ie) {
    }
  }

  public void testMinStep()
    throws DerivativeException, IntegratorException {

    try {
      TestProblem1 pb = new TestProblem1();
      double minStep = 0.1 * (pb.getFinalTime() - pb.getInitialTime());
      double maxStep = pb.getFinalTime() - pb.getInitialTime();
      double scalAbsoluteTolerance = 1.0e-15;
      double scalRelativeTolerance = 1.0e-15;

      FirstOrderIntegrator integ = new DormandPrince54Integrator(minStep, maxStep,
                                                                 scalAbsoluteTolerance,
                                                                 scalRelativeTolerance);
      TestProblemHandler handler = new TestProblemHandler(pb);
      integ.setStepHandler(handler);
      integ.integrate(pb,
                      pb.getInitialTime(), pb.getInitialState(),
                      pb.getFinalTime(), new double[pb.getDimension()]);
      fail("an exception should have been thrown");
    } catch(DerivativeException de) {
      fail("wrong exception caught");
    } catch(IntegratorException ie) {
    }

  }

  public void testSmallLastStep()
    throws DerivativeException, IntegratorException {

    TestProblemAbstract pb = new TestProblem5();
    double minStep = 1.25;
    double maxStep = Math.abs(pb.getFinalTime() - pb.getInitialTime());
    double scalAbsoluteTolerance = 6.0e-4;
    double scalRelativeTolerance = 6.0e-4;

    AdaptiveStepsizeIntegrator integ =
      new DormandPrince54Integrator(minStep, maxStep,
                                    scalAbsoluteTolerance,
                                    scalRelativeTolerance);

    DP54SmallLastHandler handler = new DP54SmallLastHandler(minStep);
    integ.setStepHandler(handler);
    integ.setInitialStepSize(1.7);
    integ.integrate(pb,
                    pb.getInitialTime(), pb.getInitialState(),
                    pb.getFinalTime(), new double[pb.getDimension()]);
    assertTrue(handler.wasLastSeen());

  }

  private static class DP54SmallLastHandler implements StepHandler {

    public DP54SmallLastHandler(double minStep) {
      lastSeen = false;
      this.minStep = minStep;
    }

    public boolean requiresDenseOutput() {
      return false;
    }

    public void reset() {
    }

    public void handleStep(StepInterpolator interpolator, boolean isLast) {
      if (isLast) {
        lastSeen = true;
        double h = interpolator.getCurrentTime() - interpolator.getPreviousTime();
        assertTrue(Math.abs(h) < minStep);
      }
    }

    public boolean wasLastSeen() {
      return lastSeen;
    }

    private boolean lastSeen;
    private double  minStep;

  }

  public void testIncreasingTolerance()
    throws DerivativeException, IntegratorException {

    int previousCalls = Integer.MAX_VALUE;
    for (int i = -12; i < -2; ++i) {
      TestProblem1 pb = new TestProblem1();
      double minStep = 0;
      double maxStep = pb.getFinalTime() - pb.getInitialTime();
      double scalAbsoluteTolerance = Math.pow(10.0, i);
      double scalRelativeTolerance = 0.01 * scalAbsoluteTolerance;

      FirstOrderIntegrator integ = new DormandPrince54Integrator(minStep, maxStep,
                                                                 scalAbsoluteTolerance,
                                                                 scalRelativeTolerance);
      TestProblemHandler handler = new TestProblemHandler(pb);
      integ.setStepHandler(handler);
      integ.integrate(pb,
                      pb.getInitialTime(), pb.getInitialState(),
                      pb.getFinalTime(), new double[pb.getDimension()]);

      // the 0.7 factor is only valid for this test
      // and has been obtained from trial and error
      // there is no general relation between local and global errors
      assertTrue(handler.getMaximalError() < (0.7 * scalAbsoluteTolerance));

      int calls = pb.getCalls();
      assertTrue(calls <= previousCalls);
      previousCalls = calls;

    }

  }

  public void testSwitchingFunctions()
    throws DerivativeException, IntegratorException {

    TestProblem4 pb = new TestProblem4();
    double minStep = 0;
    double maxStep = pb.getFinalTime() - pb.getInitialTime();
    double scalAbsoluteTolerance = 1.0e-8;
    double scalRelativeTolerance = 0.01 * scalAbsoluteTolerance;

    FirstOrderIntegrator integ = new DormandPrince54Integrator(minStep, maxStep,
                                                               scalAbsoluteTolerance,
                                                               scalRelativeTolerance);
    TestProblemHandler handler = new TestProblemHandler(pb);
    integ.setStepHandler(handler);
    SwitchingFunction[] functions = pb.getSwitchingFunctions();
    for (int l = 0; l < functions.length; ++l) {
      integ.addSwitchingFunction(functions[l],
                                 Double.POSITIVE_INFINITY, 1.0e-8 * maxStep);
    }
    integ.integrate(pb,
                    pb.getInitialTime(), pb.getInitialState(),
                    pb.getFinalTime(), new double[pb.getDimension()]);

    assertTrue(handler.getMaximalError() < 5.0e-6);
    assertEquals(12.0, handler.getLastTime(), 1.0e-8 * maxStep);

  }

  public void testKepler()
    throws DerivativeException, IntegratorException {

    final TestProblem3 pb  = new TestProblem3(0.9);
    double minStep = 0;
    double maxStep = pb.getFinalTime() - pb.getInitialTime();
    double scalAbsoluteTolerance = 1.0e-8;
    double scalRelativeTolerance = scalAbsoluteTolerance;

    FirstOrderIntegrator integ = new DormandPrince54Integrator(minStep, maxStep,
                                                               scalAbsoluteTolerance,
                                                               scalRelativeTolerance);
    integ.setStepHandler(new KeplerHandler(pb));
    integ.integrate(pb,
                    pb.getInitialTime(), pb.getInitialState(),
                    pb.getFinalTime(), new double[pb.getDimension()]);

    assertTrue(pb.getCalls() < 2800);

  }

  public void testVariableSteps()
    throws DerivativeException, IntegratorException {

    final TestProblem3 pb  = new TestProblem3(0.9);
    double minStep = 0;
    double maxStep = pb.getFinalTime() - pb.getInitialTime();
    double scalAbsoluteTolerance = 1.0e-8;
    double scalRelativeTolerance = scalAbsoluteTolerance;

    FirstOrderIntegrator integ = new DormandPrince54Integrator(minStep, maxStep,
                                                               scalAbsoluteTolerance,
                                                               scalRelativeTolerance);
    integ.setStepHandler(new VariableHandler());
    integ.integrate(pb,
                    pb.getInitialTime(), pb.getInitialState(),
                    pb.getFinalTime(), new double[pb.getDimension()]);
  }

  private static class KeplerHandler implements StepHandler {
    public KeplerHandler(TestProblem3 pb) {
      this.pb = pb;
      reset();
    }
    public boolean requiresDenseOutput() {
      return true;
    }
    public void reset() {
      nbSteps = 0;
      maxError = 0;
    }
    public void handleStep(StepInterpolator interpolator,
                           boolean isLast)
    throws DerivativeException {

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
        assertTrue(maxError < 7.0e-10);
        assertTrue(nbSteps < 400);
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
    public boolean requiresDenseOutput() {
      return false;
    }
    public void reset() {
      firstTime = true;
      minStep = 0;
      maxStep = 0;
    }
    public void handleStep(StepInterpolator interpolator,
                           boolean isLast) {

      double step = Math.abs(interpolator.getCurrentTime()
                             - interpolator.getPreviousTime());
      if (firstTime) {
        minStep   = Math.abs(step);
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
        assertTrue(minStep < (1.0 / 450.0));
        assertTrue(maxStep > (1.0 / 4.2));
      }
    }  
    private boolean firstTime;
    private double  minStep;
    private double  maxStep;
  }

  public static Test suite() {
    return new TestSuite(DormandPrince54IntegratorTest.class);
  }

}
