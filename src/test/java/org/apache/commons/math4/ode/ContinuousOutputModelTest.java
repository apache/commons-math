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

package org.apache.commons.math4.ode;

import java.util.Random;

import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.MathIllegalArgumentException;
import org.apache.commons.math4.exception.MaxCountExceededException;
import org.apache.commons.math4.exception.NoBracketingException;
import org.apache.commons.math4.exception.NumberIsTooSmallException;
import org.apache.commons.math4.ode.ContinuousOutputModel;
import org.apache.commons.math4.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math4.ode.FirstOrderIntegrator;
import org.apache.commons.math4.ode.nonstiff.DormandPrince54Integrator;
import org.apache.commons.math4.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math4.ode.sampling.DummyStepInterpolator;
import org.apache.commons.math4.ode.sampling.StepInterpolator;
import org.apache.commons.math4.util.FastMath;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ContinuousOutputModelTest {

  public ContinuousOutputModelTest() {
    pb    = null;
    integ = null;
  }

  @Test
  public void testBoundaries() throws DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException, NoBracketingException {
    integ.addStepHandler(new ContinuousOutputModel());
    integ.integrate(pb,
                    pb.getInitialTime(), pb.getInitialState(),
                    pb.getFinalTime(), new double[pb.getDimension()]);
    ContinuousOutputModel cm = (ContinuousOutputModel) integ.getStepHandlers().iterator().next();
    cm.setInterpolatedTime(2.0 * pb.getInitialTime() - pb.getFinalTime());
    cm.setInterpolatedTime(2.0 * pb.getFinalTime() - pb.getInitialTime());
    cm.setInterpolatedTime(0.5 * (pb.getFinalTime() + pb.getInitialTime()));
  }

  @Test
  public void testRandomAccess() throws DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException, NoBracketingException {

    ContinuousOutputModel cm = new ContinuousOutputModel();
    integ.addStepHandler(cm);
    integ.integrate(pb,
                    pb.getInitialTime(), pb.getInitialState(),
                    pb.getFinalTime(), new double[pb.getDimension()]);

    Random random = new Random(347588535632l);
    double maxError    = 0.0;
    double maxErrorDot = 0.0;
    for (int i = 0; i < 1000; ++i) {
      double r = random.nextDouble();
      double time = r * pb.getInitialTime() + (1.0 - r) * pb.getFinalTime();
      cm.setInterpolatedTime(time);
      double[] interpolatedY    = cm.getInterpolatedState();
      double[] interpolatedYDot = cm.getInterpolatedDerivatives();
      double[] theoreticalY     = pb.computeTheoreticalState(time);
      double[] theoreticalYDot  = new double[pb.getDimension()];
      pb.doComputeDerivatives(time, theoreticalY, theoreticalYDot);
      double dx = interpolatedY[0] - theoreticalY[0];
      double dy = interpolatedY[1] - theoreticalY[1];
      double error = dx * dx + dy * dy;
      maxError = FastMath.max(maxError, error);
      double dxDot = interpolatedYDot[0] - theoreticalYDot[0];
      double dyDot = interpolatedYDot[1] - theoreticalYDot[1];
      double errorDot = dxDot * dxDot + dyDot * dyDot;
      maxErrorDot = FastMath.max(maxErrorDot, errorDot);
    }

    Assert.assertEquals(0.0, maxError,    1.0e-9);
    Assert.assertEquals(0.0, maxErrorDot, 4.0e-7);

  }

  @Test
  public void testModelsMerging() throws MaxCountExceededException, MathIllegalArgumentException {

      // theoretical solution: y[0] = cos(t), y[1] = sin(t)
      FirstOrderDifferentialEquations problem =
          new FirstOrderDifferentialEquations() {
              @Override
            public void computeDerivatives(double t, double[] y, double[] dot) {
                  dot[0] = -y[1];
                  dot[1] =  y[0];
              }
              @Override
            public int getDimension() {
                  return 2;
              }
          };

      // integrate backward from &pi; to 0;
      ContinuousOutputModel cm1 = new ContinuousOutputModel();
      FirstOrderIntegrator integ1 =
          new DormandPrince853Integrator(0, 1.0, 1.0e-8, 1.0e-8);
      integ1.addStepHandler(cm1);
      integ1.integrate(problem, FastMath.PI, new double[] { -1.0, 0.0 },
                       0, new double[2]);

      // integrate backward from 2&pi; to &pi;
      ContinuousOutputModel cm2 = new ContinuousOutputModel();
      FirstOrderIntegrator integ2 =
          new DormandPrince853Integrator(0, 0.1, 1.0e-12, 1.0e-12);
      integ2.addStepHandler(cm2);
      integ2.integrate(problem, 2.0 * FastMath.PI, new double[] { 1.0, 0.0 },
                       FastMath.PI, new double[2]);

      // merge the two half circles
      ContinuousOutputModel cm = new ContinuousOutputModel();
      cm.append(cm2);
      cm.append(new ContinuousOutputModel());
      cm.append(cm1);

      // check circle
      Assert.assertEquals(2.0 * FastMath.PI, cm.getInitialTime(), 1.0e-12);
      Assert.assertEquals(0, cm.getFinalTime(), 1.0e-12);
      Assert.assertEquals(cm.getFinalTime(), cm.getInterpolatedTime(), 1.0e-12);
      for (double t = 0; t < 2.0 * FastMath.PI; t += 0.1) {
          cm.setInterpolatedTime(t);
          double[] y = cm.getInterpolatedState();
          Assert.assertEquals(FastMath.cos(t), y[0], 1.0e-7);
          Assert.assertEquals(FastMath.sin(t), y[1], 1.0e-7);
      }

  }

  @Test
  public void testErrorConditions() throws MaxCountExceededException, MathIllegalArgumentException {

      ContinuousOutputModel cm = new ContinuousOutputModel();
      cm.handleStep(buildInterpolator(0, new double[] { 0.0, 1.0, -2.0 }, 1), true);

      // dimension mismatch
      Assert.assertTrue(checkAppendError(cm, 1.0, new double[] { 0.0, 1.0 }, 2.0));

      // hole between time ranges
      Assert.assertTrue(checkAppendError(cm, 10.0, new double[] { 0.0, 1.0, -2.0 }, 20.0));

      // propagation direction mismatch
      Assert.assertTrue(checkAppendError(cm, 1.0, new double[] { 0.0, 1.0, -2.0 }, 0.0));

      // no errors
      Assert.assertFalse(checkAppendError(cm, 1.0, new double[] { 0.0, 1.0, -2.0 }, 2.0));

  }

  private boolean checkAppendError(ContinuousOutputModel cm,
                                   double t0, double[] y0, double t1)
      throws MaxCountExceededException, MathIllegalArgumentException {
      try {
          ContinuousOutputModel otherCm = new ContinuousOutputModel();
          otherCm.handleStep(buildInterpolator(t0, y0, t1), true);
          cm.append(otherCm);
      } catch(MathIllegalArgumentException iae) {
          return true; // there was an allowable error
      }
      return false; // no allowable error
  }

  private StepInterpolator buildInterpolator(double t0, double[] y0, double t1) {
      DummyStepInterpolator interpolator  = new DummyStepInterpolator(y0, new double[y0.length], t1 >= t0);
      interpolator.storeTime(t0);
      interpolator.shift();
      interpolator.storeTime(t1);
      return interpolator;
  }

  public void checkValue(double value, double reference) {
    Assert.assertTrue(FastMath.abs(value - reference) < 1.0e-10);
  }

  @Before
  public void setUp() {
    pb = new TestProblem3(0.9);
    double minStep = 0;
    double maxStep = pb.getFinalTime() - pb.getInitialTime();
    integ = new DormandPrince54Integrator(minStep, maxStep, 1.0e-8, 1.0e-8);
  }

  @After
  public void tearDown() {
    pb    = null;
    integ = null;
  }

  TestProblem3 pb;
  FirstOrderIntegrator integ;

}
