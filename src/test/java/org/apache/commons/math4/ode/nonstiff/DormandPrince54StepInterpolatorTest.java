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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.MaxCountExceededException;
import org.apache.commons.math4.exception.NoBracketingException;
import org.apache.commons.math4.exception.NumberIsTooSmallException;
import org.apache.commons.math4.ode.ContinuousOutputModel;
import org.apache.commons.math4.ode.TestProblem3;
import org.apache.commons.math4.ode.nonstiff.DormandPrince54Integrator;
import org.apache.commons.math4.ode.sampling.StepHandler;
import org.apache.commons.math4.ode.sampling.StepInterpolator;
import org.apache.commons.math4.ode.sampling.StepInterpolatorTestUtils;
import org.apache.commons.math4.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

public class DormandPrince54StepInterpolatorTest {

  @Test
  public void derivativesConsistency()
      throws DimensionMismatchException, NumberIsTooSmallException,
             MaxCountExceededException, NoBracketingException {
    TestProblem3 pb = new TestProblem3(0.1);
    double minStep = 0;
    double maxStep = pb.getFinalTime() - pb.getInitialTime();
    double scalAbsoluteTolerance = 1.0e-8;
    double scalRelativeTolerance = scalAbsoluteTolerance;
    DormandPrince54Integrator integ = new DormandPrince54Integrator(minStep, maxStep,
                                                                    scalAbsoluteTolerance,
                                                                    scalRelativeTolerance);
    StepInterpolatorTestUtils.checkDerivativesConsistency(integ, pb, 1.0e-10);
  }

  @Test
  public void serialization()
    throws IOException, ClassNotFoundException,
           DimensionMismatchException, NumberIsTooSmallException,
           MaxCountExceededException, NoBracketingException  {

    TestProblem3 pb = new TestProblem3(0.9);
    double minStep = 0;
    double maxStep = pb.getFinalTime() - pb.getInitialTime();
    double scalAbsoluteTolerance = 1.0e-8;
    double scalRelativeTolerance = scalAbsoluteTolerance;
    DormandPrince54Integrator integ = new DormandPrince54Integrator(minStep, maxStep,
                                                                    scalAbsoluteTolerance,
                                                                    scalRelativeTolerance);
    integ.addStepHandler(new ContinuousOutputModel());
    integ.integrate(pb,
                    pb.getInitialTime(), pb.getInitialState(),
                    pb.getFinalTime(), new double[pb.getDimension()]);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream    oos = new ObjectOutputStream(bos);
    for (StepHandler handler : integ.getStepHandlers()) {
        oos.writeObject(handler);
    }

    Assert.assertTrue(bos.size () > 135000);
    Assert.assertTrue(bos.size () < 145000);

    ByteArrayInputStream  bis = new ByteArrayInputStream(bos.toByteArray());
    ObjectInputStream     ois = new ObjectInputStream(bis);
    ContinuousOutputModel cm  = (ContinuousOutputModel) ois.readObject();

    Random random = new Random(347588535632l);
    double maxError = 0.0;
    for (int i = 0; i < 1000; ++i) {
      double r = random.nextDouble();
      double time = r * pb.getInitialTime() + (1.0 - r) * pb.getFinalTime();
      cm.setInterpolatedTime(time);
      double[] interpolatedY = cm.getInterpolatedState ();
      double[] theoreticalY  = pb.computeTheoreticalState(time);
      double dx = interpolatedY[0] - theoreticalY[0];
      double dy = interpolatedY[1] - theoreticalY[1];
      double error = dx * dx + dy * dy;
      if (error > maxError) {
        maxError = error;
      }
    }

    Assert.assertTrue(maxError < 7.0e-10);

  }

  @Test
  public void checkClone()
      throws DimensionMismatchException, NumberIsTooSmallException,
             MaxCountExceededException, NoBracketingException {
      TestProblem3 pb = new TestProblem3(0.9);
      double minStep = 0;
      double maxStep = pb.getFinalTime() - pb.getInitialTime();
      double scalAbsoluteTolerance = 1.0e-8;
      double scalRelativeTolerance = scalAbsoluteTolerance;
      DormandPrince54Integrator integ = new DormandPrince54Integrator(minStep, maxStep,
                                                                      scalAbsoluteTolerance,
                                                                      scalRelativeTolerance);
      integ.addStepHandler(new StepHandler() {
          public void handleStep(StepInterpolator interpolator, boolean isLast)
              throws MaxCountExceededException {
              StepInterpolator cloned = interpolator.copy();
              double tA = cloned.getPreviousTime();
              double tB = cloned.getCurrentTime();
              double halfStep = FastMath.abs(tB - tA) / 2;
              Assert.assertEquals(interpolator.getPreviousTime(), tA, 1.0e-12);
              Assert.assertEquals(interpolator.getCurrentTime(), tB, 1.0e-12);
              for (int i = 0; i < 10; ++i) {
                  double t = (i * tB + (9 - i) * tA) / 9;
                  interpolator.setInterpolatedTime(t);
                  Assert.assertTrue(FastMath.abs(cloned.getInterpolatedTime() - t) > (halfStep / 10));
                  cloned.setInterpolatedTime(t);
                  Assert.assertEquals(t, cloned.getInterpolatedTime(), 1.0e-12);
                  double[] referenceState = interpolator.getInterpolatedState();
                  double[] cloneState     = cloned.getInterpolatedState();
                  for (int j = 0; j < referenceState.length; ++j) {
                      Assert.assertEquals(referenceState[j], cloneState[j], 1.0e-12);
                  }
              }
          }
          public void init(double t0, double[] y0, double t) {
          }
      });
      integ.integrate(pb,
              pb.getInitialTime(), pb.getInitialState(),
              pb.getFinalTime(), new double[pb.getDimension()]);

  }

}
