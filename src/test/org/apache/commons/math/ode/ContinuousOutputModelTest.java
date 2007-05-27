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

import junit.framework.*;
import java.util.Random;

import org.apache.commons.math.ode.ContinuousOutputModel;
import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.DormandPrince54Integrator;
import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.IntegratorException;

public class ContinuousOutputModelTest
  extends TestCase {

  public ContinuousOutputModelTest(String name) {
    super(name);
    pb    = null;
    integ = null;
  }

  public void testBoundaries()
    throws DerivativeException, IntegratorException {
    integ.setStepHandler(new ContinuousOutputModel());
    integ.integrate(pb,
                    pb.getInitialTime(), pb.getInitialState(),
                    pb.getFinalTime(), new double[pb.getDimension()]);
    ContinuousOutputModel cm = (ContinuousOutputModel) integ.getStepHandler();
    cm.setInterpolatedTime(2.0 * pb.getInitialTime() - pb.getFinalTime());
    cm.setInterpolatedTime(2.0 * pb.getFinalTime() - pb.getInitialTime());
    cm.setInterpolatedTime(0.5 * (pb.getFinalTime() + pb.getInitialTime()));
  }

  public void testRandomAccess()
    throws DerivativeException, IntegratorException {

    ContinuousOutputModel cm = new ContinuousOutputModel();
    integ.setStepHandler(cm);
    integ.integrate(pb,
                    pb.getInitialTime(), pb.getInitialState(),
                    pb.getFinalTime(), new double[pb.getDimension()]);

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

    assertTrue(maxError < 1.0e-9);

  }

  public void checkValue(double value, double reference) {
    assertTrue(Math.abs(value - reference) < 1.0e-10);
  }

  public static Test suite() {
    return new TestSuite(ContinuousOutputModelTest.class);
  }

  public void setUp() {
    pb = new TestProblem3(0.9);
    double minStep = 0;
    double maxStep = pb.getFinalTime() - pb.getInitialTime();
    integ = new DormandPrince54Integrator(minStep, maxStep, 1.0e-8, 1.0e-8);
  }

  public void tearDown() {
    pb    = null;
    integ = null;
  }

  TestProblem3 pb;
  FirstOrderIntegrator integ;

}
