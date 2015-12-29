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

package org.apache.commons.math3.ode.nonstiff;


import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.AbstractIntegrator;
import org.apache.commons.math3.ode.ExpandableStatefulODE;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.MultistepIntegrator;
import org.apache.commons.math3.ode.TestProblem1;
import org.apache.commons.math3.ode.TestProblem5;
import org.apache.commons.math3.ode.TestProblem6;
import org.apache.commons.math3.ode.TestProblemAbstract;
import org.apache.commons.math3.ode.TestProblemHandler;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

public class AdamsBashforthIntegratorTest {

    @Test(expected=DimensionMismatchException.class)
    public void dimensionCheck() throws NumberIsTooSmallException, DimensionMismatchException, MaxCountExceededException, NoBracketingException {
        TestProblem1 pb = new TestProblem1();
        FirstOrderIntegrator integ =
            new AdamsBashforthIntegrator(2, 0.0, 1.0, 1.0e-10, 1.0e-10);
        integ.integrate(pb,
                        0.0, new double[pb.getDimension()+10],
                        1.0, new double[pb.getDimension()+10]);
    }

    @Test(expected=NumberIsTooSmallException.class)
    public void testMinStep() throws DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException, NoBracketingException {

          TestProblem1 pb = new TestProblem1();
          double minStep = 0.1 * (pb.getFinalTime() - pb.getInitialTime());
          double maxStep = pb.getFinalTime() - pb.getInitialTime();
          double[] vecAbsoluteTolerance = { 1.0e-15, 1.0e-16 };
          double[] vecRelativeTolerance = { 1.0e-15, 1.0e-16 };

          FirstOrderIntegrator integ = new AdamsBashforthIntegrator(4, minStep, maxStep,
                                                                    vecAbsoluteTolerance,
                                                                    vecRelativeTolerance);
          TestProblemHandler handler = new TestProblemHandler(pb, integ);
          integ.addStepHandler(handler);
          integ.integrate(pb,
                          pb.getInitialTime(), pb.getInitialState(),
                          pb.getFinalTime(), new double[pb.getDimension()]);

    }

    @Test
    public void testIncreasingTolerance() throws DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException, NoBracketingException {

        int previousCalls = Integer.MAX_VALUE;
        for (int i = -12; i < -2; ++i) {
            TestProblem1 pb = new TestProblem1();
            double minStep = 0;
            double maxStep = pb.getFinalTime() - pb.getInitialTime();
            double scalAbsoluteTolerance = FastMath.pow(10.0, i);
            double scalRelativeTolerance = 0.01 * scalAbsoluteTolerance;

            FirstOrderIntegrator integ = new AdamsBashforthIntegrator(4, minStep, maxStep,
                                                                      scalAbsoluteTolerance,
                                                                      scalRelativeTolerance);
            TestProblemHandler handler = new TestProblemHandler(pb, integ);
            integ.addStepHandler(handler);
            integ.integrate(pb,
                            pb.getInitialTime(), pb.getInitialState(),
                            pb.getFinalTime(), new double[pb.getDimension()]);

            // the 2.6 and 122 factors are only valid for this test
            // and has been obtained from trial and error
            // there are no general relationship between local and global errors
            Assert.assertTrue(handler.getMaximalValueError() > (2.6 * scalAbsoluteTolerance));
            Assert.assertTrue(handler.getMaximalValueError() < (122 * scalAbsoluteTolerance));

            int calls = pb.getCalls();
            Assert.assertEquals(integ.getEvaluations(), calls);
            Assert.assertTrue(calls <= previousCalls);
            previousCalls = calls;

        }

    }

    @Test(expected = MaxCountExceededException.class)
    public void exceedMaxEvaluations() throws DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException, NoBracketingException {

        TestProblem1 pb  = new TestProblem1();
        double range = pb.getFinalTime() - pb.getInitialTime();

        AdamsBashforthIntegrator integ = new AdamsBashforthIntegrator(2, 0, range, 1.0e-12, 1.0e-12);
        TestProblemHandler handler = new TestProblemHandler(pb, integ);
        integ.addStepHandler(handler);
        integ.setMaxEvaluations(650);
        integ.integrate(pb,
                        pb.getInitialTime(), pb.getInitialState(),
                        pb.getFinalTime(), new double[pb.getDimension()]);

    }

    @Test
    public void backward() throws DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException, NoBracketingException {

        TestProblem5 pb = new TestProblem5();
        double range = FastMath.abs(pb.getFinalTime() - pb.getInitialTime());

        AdamsBashforthIntegrator integ = new AdamsBashforthIntegrator(4, 0, range, 1.0e-12, 1.0e-12);
        integ.setStarterIntegrator(new PerfectStarter(pb, (integ.getNSteps() + 5) / 2));
        TestProblemHandler handler = new TestProblemHandler(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(),
                        pb.getFinalTime(), new double[pb.getDimension()]);

        Assert.assertEquals(0.0, handler.getLastError(), 4.3e-8);
        Assert.assertEquals(0.0, handler.getMaximalValueError(), 4.3e-8);
        Assert.assertEquals(0, handler.getMaximalTimeError(), 1.0e-16);
        Assert.assertEquals("Adams-Bashforth", integ.getName());
    }

    @Test
    public void polynomial() throws DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException, NoBracketingException {
        TestProblem6 pb = new TestProblem6();
        double range = FastMath.abs(pb.getFinalTime() - pb.getInitialTime());

        for (int nSteps = 2; nSteps < 8; ++nSteps) {
            AdamsBashforthIntegrator integ =
                new AdamsBashforthIntegrator(nSteps, 1.0e-6 * range, 0.1 * range, 1.0e-4, 1.0e-4);
            integ.setStarterIntegrator(new PerfectStarter(pb, nSteps));
            TestProblemHandler handler = new TestProblemHandler(pb, integ);
            integ.addStepHandler(handler);
            integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(),
                            pb.getFinalTime(), new double[pb.getDimension()]);
            if (nSteps < 5) {
                Assert.assertTrue(handler.getMaximalValueError() > 0.005);
            } else {
                Assert.assertTrue(handler.getMaximalValueError() < 5.0e-10);
            }
        }

    }

    @Test(expected=MathIllegalStateException.class)
    public void testStartFailure() {
        TestProblem1 pb = new TestProblem1();
        double minStep = 0.0001 * (pb.getFinalTime() - pb.getInitialTime());
        double maxStep = pb.getFinalTime() - pb.getInitialTime();
        double scalAbsoluteTolerance = 1.0e-6;
        double scalRelativeTolerance = 1.0e-7;

        MultistepIntegrator integ =
                        new AdamsBashforthIntegrator(6, minStep, maxStep,
                                                     scalAbsoluteTolerance,
                                                     scalRelativeTolerance);
        integ.setStarterIntegrator(new DormandPrince853Integrator(0.5 * (pb.getFinalTime() - pb.getInitialTime()),
                                                                  pb.getFinalTime() - pb.getInitialTime(),
                                                                  0.1, 0.1));
        TestProblemHandler handler = new TestProblemHandler(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(pb,
                        pb.getInitialTime(), pb.getInitialState(),
                        pb.getFinalTime(), new double[pb.getDimension()]);

    }

    private static class PerfectStarter extends AbstractIntegrator {

        private final PerfectInterpolator interpolator;
        private final int nbSteps;

        public PerfectStarter(final TestProblemAbstract problem, final int nbSteps) {
            this.interpolator = new PerfectInterpolator(problem);
            this.nbSteps      = nbSteps;
        }

        public void integrate(ExpandableStatefulODE equations, double t) {
            double tStart = equations.getTime() + 0.01 * (t - equations.getTime());
            getCounter().increment(nbSteps);
            for (int i = 0; i < nbSteps; ++i) {
                double tK = ((nbSteps - 1 - (i + 1)) * equations.getTime() + (i + 1) * tStart) / (nbSteps - 1);
                interpolator.setPreviousTime(interpolator.getCurrentTime());
                interpolator.setCurrentTime(tK);
                interpolator.setInterpolatedTime(tK);
                for (StepHandler handler : getStepHandlers()) {
                    handler.handleStep(interpolator, i == nbSteps - 1);
                }
            }
        }

    }

    private static class PerfectInterpolator implements StepInterpolator {
        private final TestProblemAbstract problem;
        private double previousTime;
        private double currentTime;
        private double interpolatedTime;

        public PerfectInterpolator(final TestProblemAbstract problem) {
            this.problem          = problem;
            this.previousTime     = problem.getInitialTime();
            this.currentTime      = problem.getInitialTime();
            this.interpolatedTime = problem.getInitialTime();
        }

        public void readExternal(ObjectInput arg0) {
        }

        public void writeExternal(ObjectOutput arg0) {
        }

        public double getPreviousTime() {
            return previousTime;
        }

        public void setPreviousTime(double time) {
            previousTime = time;
        }

        public double getCurrentTime() {
            return currentTime;
        }

        public void setCurrentTime(double time) {
            currentTime = time;
        }

        public double getInterpolatedTime() {
            return interpolatedTime;
        }

        public void setInterpolatedTime(double time) {
            interpolatedTime = time;
        }

        public double[] getInterpolatedState() {
            return problem.computeTheoreticalState(interpolatedTime);
        }

        public double[] getInterpolatedDerivatives() {
            double[] y = problem.computeTheoreticalState(interpolatedTime);
            double[] yDot = new double[y.length];
            problem.computeDerivatives(interpolatedTime, y, yDot);
            return yDot;
        }

        public double[] getInterpolatedSecondaryState(int index) {
            return null;
        }

        public double[] getInterpolatedSecondaryDerivatives(int index) {
            return null;
        }

        public boolean isForward() {
            return problem.getFinalTime() > problem.getInitialTime();
        }

        public StepInterpolator copy() {
            return this;
        }

    }

}
