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


import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.PrintStream;
import java.util.Locale;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.AbstractIntegrator;
import org.apache.commons.math3.ode.ExpandableStatefulODE;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.TestProblem1;
import org.apache.commons.math3.ode.TestProblem5;
import org.apache.commons.math3.ode.TestProblem6;
import org.apache.commons.math3.ode.TestProblemAbstract;
import org.apache.commons.math3.ode.TestProblemHandler;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;
import org.apache.commons.math3.util.CombinatoricsUtils;
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
        for (int i = -12; i < -5; ++i) {
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

            // the 8 and 122 factors are only valid for this test
            // and has been obtained from trial and error
            // there are no general relationship between local and global errors
            Assert.assertTrue(handler.getMaximalValueError() > (  8 * scalAbsoluteTolerance));
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

    private static class PerfectStarter extends AbstractIntegrator {

        private final PerfectInterpolator interpolator;
        private final int nbSteps;

        public PerfectStarter(final TestProblemAbstract problem, final int nbSteps) {
            this.interpolator = new PerfectInterpolator(problem);
            this.nbSteps      = nbSteps;
        }

        public void integrate(ExpandableStatefulODE equations, double t) {
            double tStart = equations.getTime() + 0.01 * (t - equations.getTime());
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

    @Test
    public void testTmp() throws IOException, DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException, NoBracketingException {
        final PrintStream out = new PrintStream(new File(new File(System.getProperty("user.home")), "x.dat"));
        final int n = 4;
        final AdamsBashforthIntegrator integ = new AdamsBashforthIntegrator(n, 1.0e-12, 1.0e3, 1.0e-10, 1.0e-12);
        final SinT4 sinT4 = new SinT4();
        integ.setStarterIntegrator(new PerfectStarter(sinT4, n));
        final StepHandler handler = new StepHandler() {
            double previousError = 0;
            public void init(double t0, double[] y0, double t) {
            }
            public void handleStep(StepInterpolator interpolator, boolean isLast) {
                final double t = interpolator.getCurrentTime();
                final double h = t - interpolator.getPreviousTime();
                final double y = interpolator.getInterpolatedState()[0];
                final double error = sinT4.computeTheoreticalState(t)[0] - y;
                out.format(Locale.US, "%10.8f %16.9e %10.8f %16.9e",
                           t, h, y, error - previousError);
                for (int i = 1; i < n + 1; ++i) {
                    out.format(Locale.US, " %16.9e", sinT4.scaledDerivative(t, h, i));
                }
                out.format(Locale.US, "%n");
                previousError = error;
            }
        };
        integ.addStepHandler(handler);
        final ExpandableStatefulODE expandableODE = new ExpandableStatefulODE(sinT4);
        final double t0 = 0.75;
        final double h0 = 1.0 / 128;
        expandableODE.setTime(t0);
        expandableODE.setPrimaryState(sinT4.computeTheoreticalState(t0));
        integ.setInitialStepSize(h0);
        integ.integrate(expandableODE, 0.9);
        out.close();
    }

    private static class SinT4 extends TestProblemAbstract {

        @Override
        public int getDimension() {
            return 1;
        }

        @Override
        public void doComputeDerivatives(double t, double[] y, double[] yDot) {
            // compute the derivatives
            double t3 = FastMath.pow(t,  3);
            yDot[0] = 4 * t3 * FastMath.cos(t3 * t);
        }

        public double[] computeTheoreticalState(final double t) {
          return new double[] { FastMath.sin(FastMath.pow(t,  4)) };
        }

        public DerivativeStructure valueDS(final double t, final int n) {
            return new DerivativeStructure(1, n, 0, t).pow(4).sin();
        }

        public double scaledDerivative(final double t, final double h, final int n) {
            return FastMath.pow(h, n) * valueDS(t, n).getPartialDerivative(n) / CombinatoricsUtils.factorial(n);
        }

    }

}
