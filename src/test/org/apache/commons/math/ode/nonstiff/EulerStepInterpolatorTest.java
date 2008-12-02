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

package org.apache.commons.math.ode.nonstiff;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.math.ode.ContinuousOutputModel;
import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.sampling.StepHandler;

public class EulerStepInterpolatorTest
  extends StepInterpolatorAbstractTest {

  public EulerStepInterpolatorTest(String name) {
    super(name);
  }

  public void testNoReset() {

    double[]   y    =   { 0.0, 1.0, -2.0 };
    double[][] yDot = { { 1.0, 2.0, -2.0 } };
    EulerStepInterpolator interpolator = new EulerStepInterpolator();
    interpolator.reinitialize(new DummyEquations(), y, yDot, true);
    interpolator.storeTime(0);
    interpolator.shift();
    interpolator.storeTime(1);

    double[] result = interpolator.getInterpolatedState();
    for (int i = 0; i < result.length; ++i) {
      assertTrue(Math.abs(result[i] - y[i]) < 1.0e-10);
    }

  }

  public void testInterpolationAtBounds()
    throws DerivativeException {

    double   t0 = 0;
    double[] y0 = {0.0, 1.0, -2.0};

    double[] y = (double[]) y0.clone();
    double[][] yDot = { new double[y0.length] };
    EulerStepInterpolator interpolator = new EulerStepInterpolator();
    interpolator.reinitialize(new DummyEquations(), y, yDot, true);
    interpolator.storeTime(t0);

    double dt = 1.0;
    y[0] =  1.0;
    y[1] =  3.0;
    y[2] = -4.0;
    yDot[0][0] = (y[0] - y0[0]) / dt;
    yDot[0][1] = (y[1] - y0[1]) / dt;
    yDot[0][2] = (y[2] - y0[2]) / dt;
    interpolator.shift();
    interpolator.storeTime(t0 + dt);

    interpolator.setInterpolatedTime(interpolator.getPreviousTime());
    double[] result = interpolator.getInterpolatedState();
    for (int i = 0; i < result.length; ++i) {
      assertTrue(Math.abs(result[i] - y0[i]) < 1.0e-10);
    }

    interpolator.setInterpolatedTime(interpolator.getCurrentTime());
    result = interpolator.getInterpolatedState();
    for (int i = 0; i < result.length; ++i) {
      assertTrue(Math.abs(result[i] - y[i]) < 1.0e-10);
    }

  }

  public void testInterpolationInside()
  throws DerivativeException {

    double[]   y    =   { 1.0, 3.0, -4.0 };
    double[][] yDot = { { 1.0, 2.0, -2.0 } };
    EulerStepInterpolator interpolator = new EulerStepInterpolator();
    interpolator.reinitialize(new DummyEquations(), y, yDot, true);
    interpolator.storeTime(0);
    interpolator.shift();
    interpolator.storeTime(1);

    interpolator.setInterpolatedTime(0.1);
    double[] result = interpolator.getInterpolatedState();
    assertTrue(Math.abs(result[0] - 0.1) < 1.0e-10);
    assertTrue(Math.abs(result[1] - 1.2) < 1.0e-10);
    assertTrue(Math.abs(result[2] + 2.2) < 1.0e-10);

    interpolator.setInterpolatedTime(0.5);
    result = interpolator.getInterpolatedState();
    assertTrue(Math.abs(result[0] - 0.5) < 1.0e-10);
    assertTrue(Math.abs(result[1] - 2.0) < 1.0e-10);
    assertTrue(Math.abs(result[2] + 3.0) < 1.0e-10);

  }

  public void testDerivativesConsistency()
  throws DerivativeException, IntegratorException {
    TestProblem3 pb = new TestProblem3();
    double step = (pb.getFinalTime() - pb.getInitialTime()) * 0.001;
    EulerIntegrator integ = new EulerIntegrator(step);
    checkDerivativesConsistency(integ, pb, 1.0e-10);
  }

  public void testSerialization()
    throws DerivativeException, IntegratorException,
           IOException, ClassNotFoundException {

    TestProblem1 pb = new TestProblem1();
    double step = (pb.getFinalTime() - pb.getInitialTime()) * 0.001;
    EulerIntegrator integ = new EulerIntegrator(step);
    integ.addStepHandler(new ContinuousOutputModel());
    integ.integrate(pb,
                    pb.getInitialTime(), pb.getInitialState(),
                    pb.getFinalTime(), new double[pb.getDimension()]);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream    oos = new ObjectOutputStream(bos);
    for (StepHandler handler : integ.getStepHandlers()) {
        oos.writeObject(handler);
    }

    assertTrue(bos.size () > 82000);
    assertTrue(bos.size () < 83000);

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

    assertTrue(maxError < 0.001);

  }

  private static class DummyEquations
    implements FirstOrderDifferentialEquations {
    private static final long serialVersionUID = 291437140744677100L;
    public int getDimension() {
      return 0;
    }
    public void computeDerivatives(double t, double[] y, double[] yDot) {
    }
  }

  public static Test suite() {
    return new TestSuite(EulerStepInterpolatorTest.class);
  }

}
