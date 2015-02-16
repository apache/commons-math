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

package org.apache.commons.math3.ode.sampling;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.TestProblem3;
import org.apache.commons.math3.ode.nonstiff.DormandPrince54Integrator;
import org.apache.commons.math3.util.FastMath;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class StepNormalizerTest {

  public StepNormalizerTest() {
    pb    = null;
    integ = null;
  }

  @Test
  public void testBoundaries()
      throws DimensionMismatchException, NumberIsTooSmallException,
             MaxCountExceededException, NoBracketingException {
    double range = pb.getFinalTime() - pb.getInitialTime();
    setLastSeen(false);
    integ.addStepHandler(new StepNormalizer(range / 10.0,
                                       new FixedStepHandler() {
                                         private boolean firstCall = true;
                                         public void init(double t0, double[] y0, double t) {
                                         }
                                         public void handleStep(double t,
                                                                double[] y,
                                                                double[] yDot,
                                                                boolean isLast) {
                                           if (firstCall) {
                                             checkValue(t, pb.getInitialTime());
                                             firstCall = false;
                                           }
                                           if (isLast) {
                                             setLastSeen(true);
                                             checkValue(t, pb.getFinalTime());
                                           }
                                         }
                                       }));
    integ.integrate(pb,
                    pb.getInitialTime(), pb.getInitialState(),
                    pb.getFinalTime(), new double[pb.getDimension()]);
    Assert.assertTrue(lastSeen);
  }

  @Test
  public void testBeforeEnd()
      throws DimensionMismatchException, NumberIsTooSmallException,
             MaxCountExceededException, NoBracketingException {
    final double range = pb.getFinalTime() - pb.getInitialTime();
    setLastSeen(false);
    integ.addStepHandler(new StepNormalizer(range / 10.5,
                                       new FixedStepHandler() {
                                         public void init(double t0, double[] y0, double t) {
                                         }
                                         public void handleStep(double t,
                                                                double[] y,
                                                                double[] yDot,
                                                                boolean isLast) {
                                           if (isLast) {
                                             setLastSeen(true);
                                             checkValue(t,
                                                        pb.getFinalTime() - range / 21.0);
                                           }
                                         }
                                       }));
    integ.integrate(pb,
                    pb.getInitialTime(), pb.getInitialState(),
                    pb.getFinalTime(), new double[pb.getDimension()]);
    Assert.assertTrue(lastSeen);
  }

  public void checkValue(double value, double reference) {
    Assert.assertTrue(FastMath.abs(value - reference) < 1.0e-10);
  }

  public void setLastSeen(boolean lastSeen) {
    this.lastSeen = lastSeen;
  }

  @Before
  public void setUp() {
    pb = new TestProblem3(0.9);
    double minStep = 0;
    double maxStep = pb.getFinalTime() - pb.getInitialTime();
    integ = new DormandPrince54Integrator(minStep, maxStep, 10.e-8, 1.0e-8);
    lastSeen = false;
  }

  @After
  public void tearDown() {
    pb    = null;
    integ = null;
  }

  TestProblem3 pb;
  FirstOrderIntegrator integ;
  boolean lastSeen;

}
