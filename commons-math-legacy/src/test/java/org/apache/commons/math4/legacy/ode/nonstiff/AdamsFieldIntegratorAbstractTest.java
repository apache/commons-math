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


import org.apache.commons.math4.legacy.core.Field;
import org.apache.commons.math4.legacy.core.RealFieldElement;
import org.apache.commons.math4.legacy.exception.MathIllegalStateException;
import org.apache.commons.math4.legacy.exception.MaxCountExceededException;
import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;
import org.apache.commons.math4.legacy.ode.AbstractFieldIntegrator;
import org.apache.commons.math4.legacy.ode.FieldExpandableODE;
import org.apache.commons.math4.legacy.ode.FieldODEState;
import org.apache.commons.math4.legacy.ode.FieldODEStateAndDerivative;
import org.apache.commons.math4.legacy.ode.FirstOrderFieldIntegrator;
import org.apache.commons.math4.legacy.ode.MultistepFieldIntegrator;
import org.apache.commons.math4.legacy.ode.TestFieldProblem1;
import org.apache.commons.math4.legacy.ode.TestFieldProblem5;
import org.apache.commons.math4.legacy.ode.TestFieldProblem6;
import org.apache.commons.math4.legacy.ode.TestFieldProblemAbstract;
import org.apache.commons.math4.legacy.ode.TestFieldProblemHandler;
import org.apache.commons.math4.legacy.ode.sampling.FieldStepHandler;
import org.apache.commons.math4.legacy.ode.sampling.FieldStepInterpolator;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.junit.Assert;
import org.junit.Test;

public abstract class AdamsFieldIntegratorAbstractTest {

    protected abstract <T extends RealFieldElement<T>> AdamsFieldIntegrator<T>
    createIntegrator(Field<T> field, int nSteps, double minStep, double maxStep,
                     double scalAbsoluteTolerance, double scalRelativeTolerance);

    protected abstract <T extends RealFieldElement<T>> AdamsFieldIntegrator<T>
    createIntegrator(Field<T> field, int nSteps, double minStep, double maxStep,
                     double[] vecAbsoluteTolerance, double[] vecRelativeTolerance);

    @Test(expected=NumberIsTooSmallException.class)
    public abstract void testMinStep();

    protected <T extends RealFieldElement<T>> void doDimensionCheck(final Field<T> field) {
        TestFieldProblem1<T> pb = new TestFieldProblem1<>(field);

        double minStep = pb.getFinalTime().subtract(pb.getInitialState().getTime()).multiply(0.1).getReal();
        double maxStep = pb.getFinalTime().subtract(pb.getInitialState().getTime()).getReal();
        double[] vecAbsoluteTolerance = { 1.0e-15, 1.0e-16 };
        double[] vecRelativeTolerance = { 1.0e-15, 1.0e-16 };

        FirstOrderFieldIntegrator<T> integ = createIntegrator(field, 4, minStep, maxStep,
                                                              vecAbsoluteTolerance,
                                                              vecRelativeTolerance);
        TestFieldProblemHandler<T> handler = new TestFieldProblemHandler<>(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(new FieldExpandableODE<>(pb), pb.getInitialState(), pb.getFinalTime());
    }

    @Test
    public abstract void testIncreasingTolerance();

    protected <T extends RealFieldElement<T>> void doTestIncreasingTolerance(final Field<T> field,
                                                                             double ratioMin, double ratioMax) {

        int previousCalls = Integer.MAX_VALUE;
        for (int i = -12; i < -2; ++i) {
            TestFieldProblem1<T> pb = new TestFieldProblem1<>(field);
            double minStep = 0;
            double maxStep = pb.getFinalTime().subtract(pb.getInitialState().getTime()).getReal();
            double scalAbsoluteTolerance = JdkMath.pow(10.0, i);
            double scalRelativeTolerance = 0.01 * scalAbsoluteTolerance;

            FirstOrderFieldIntegrator<T> integ = createIntegrator(field, 4, minStep, maxStep,
                                                                  scalAbsoluteTolerance,
                                                                  scalRelativeTolerance);
            TestFieldProblemHandler<T> handler = new TestFieldProblemHandler<>(pb, integ);
            integ.addStepHandler(handler);
            integ.integrate(new FieldExpandableODE<>(pb), pb.getInitialState(), pb.getFinalTime());

            Assert.assertTrue(handler.getMaximalValueError().getReal() > ratioMin * scalAbsoluteTolerance);
            Assert.assertTrue(handler.getMaximalValueError().getReal() < ratioMax * scalAbsoluteTolerance);

            int calls = pb.getCalls();
            Assert.assertEquals(integ.getEvaluations(), calls);
            Assert.assertTrue(calls <= previousCalls);
            previousCalls = calls;
        }
    }

    @Test(expected = MaxCountExceededException.class)
    public abstract void exceedMaxEvaluations();

    protected <T extends RealFieldElement<T>> void doExceedMaxEvaluations(final Field<T> field, final int max) {

        TestFieldProblem1<T> pb  = new TestFieldProblem1<>(field);
        double range = pb.getFinalTime().subtract(pb.getInitialState().getTime()).getReal();

        FirstOrderFieldIntegrator<T> integ = createIntegrator(field, 2, 0, range, 1.0e-12, 1.0e-12);
        TestFieldProblemHandler<T> handler = new TestFieldProblemHandler<>(pb, integ);
        integ.addStepHandler(handler);
        integ.setMaxEvaluations(max);
        integ.integrate(new FieldExpandableODE<>(pb), pb.getInitialState(), pb.getFinalTime());
    }

    @Test
    public abstract void backward();

    protected <T extends RealFieldElement<T>> void doBackward(final Field<T> field,
                                                              final double epsilonLast,
                                                              final double epsilonMaxValue,
                                                              final double epsilonMaxTime,
                                                              final String name) {

        TestFieldProblem5<T> pb = new TestFieldProblem5<>(field);
        double range = pb.getFinalTime().subtract(pb.getInitialState().getTime()).getReal();

        AdamsFieldIntegrator<T> integ = createIntegrator(field, 4, 0, range, 1.0e-12, 1.0e-12);
        TestFieldProblemHandler<T> handler = new TestFieldProblemHandler<>(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(new FieldExpandableODE<>(pb), pb.getInitialState(), pb.getFinalTime());

        Assert.assertEquals(0.0, handler.getLastError().getReal(), epsilonLast);
        Assert.assertEquals(0.0, handler.getMaximalValueError().getReal(), epsilonMaxValue);
        Assert.assertEquals(0, handler.getMaximalTimeError().getReal(), epsilonMaxTime);
        Assert.assertEquals(name, integ.getName());
    }

    @Test
    public abstract void polynomial();

    protected <T extends RealFieldElement<T>> void doPolynomial(final Field<T> field,
                                                                final int nLimit,
                                                                final double epsilonBad,
                                                                final double epsilonGood) {
        TestFieldProblem6<T> pb = new TestFieldProblem6<>(field);
        double range = pb.getFinalTime().subtract(pb.getInitialState().getTime()).abs().getReal();

        for (int nSteps = 2; nSteps < 8; ++nSteps) {
            AdamsFieldIntegrator<T> integ = createIntegrator(field, nSteps, 1.0e-6 * range, 0.1 * range, 1.0e-4, 1.0e-4);
            integ.setStarterIntegrator(new PerfectStarter<>(pb, nSteps));
            TestFieldProblemHandler<T> handler = new TestFieldProblemHandler<>(pb, integ);
            integ.addStepHandler(handler);
            integ.integrate(new FieldExpandableODE<>(pb), pb.getInitialState(), pb.getFinalTime());
            if (nSteps < nLimit) {
                Assert.assertTrue(handler.getMaximalValueError().getReal() > epsilonBad);
            } else {
                Assert.assertTrue(handler.getMaximalValueError().getReal() < epsilonGood);
            }
        }
    }

    @Test(expected=MathIllegalStateException.class)
    public abstract void testStartFailure();

    protected <T extends RealFieldElement<T>> void doTestStartFailure(final Field<T> field) {
        TestFieldProblem1<T> pb = new TestFieldProblem1<>(field);
        double minStep = pb.getFinalTime().subtract(pb.getInitialState().getTime()).multiply(0.0001).getReal();
        double maxStep = pb.getFinalTime().subtract(pb.getInitialState().getTime()).getReal();
        double scalAbsoluteTolerance = 1.0e-6;
        double scalRelativeTolerance = 1.0e-7;

        MultistepFieldIntegrator<T> integ = createIntegrator(field, 6, minStep, maxStep,
                                                             scalAbsoluteTolerance,
                                                             scalRelativeTolerance);
        integ.setStarterIntegrator(new DormandPrince853FieldIntegrator<>(field, maxStep * 0.5, maxStep, 0.1, 0.1));
        TestFieldProblemHandler<T> handler = new TestFieldProblemHandler<>(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(new FieldExpandableODE<>(pb), pb.getInitialState(), pb.getFinalTime());
    }

    private static class PerfectStarter<T extends RealFieldElement<T>> extends AbstractFieldIntegrator<T> {

        private final PerfectInterpolator<T> interpolator;
        private final int nbSteps;

        PerfectStarter(final TestFieldProblemAbstract<T> problem, final int nbSteps) {
            super(problem.getField(), "perfect-starter");
            this.interpolator = new PerfectInterpolator<>(problem);
            this.nbSteps      = nbSteps;
        }

        @Override
        public FieldODEStateAndDerivative<T> integrate(FieldExpandableODE<T> equations,
                                                       FieldODEState<T> initialState, T finalTime) {
            T tStart = initialState.getTime().add(finalTime.subtract(initialState.getTime()).multiply(0.01));
            getEvaluationsCounter().increment(nbSteps);
            interpolator.setCurrentTime(initialState.getTime());
            for (int i = 0; i < nbSteps; ++i) {
                T tK = initialState.getTime().multiply(nbSteps - 1 - (i + 1)).add(tStart.multiply(i + 1)).divide(nbSteps - 1);
                interpolator.setPreviousTime(interpolator.getCurrentTime());
                interpolator.setCurrentTime(tK);
                for (FieldStepHandler<T> handler : getStepHandlers()) {
                    handler.handleStep(interpolator, i == nbSteps - 1);
                }
            }
            return interpolator.getInterpolatedState(tStart);
        }
    }

    private static class PerfectInterpolator<T extends RealFieldElement<T>> implements FieldStepInterpolator<T> {
        private final TestFieldProblemAbstract<T> problem;
        private T previousTime;
        private T currentTime;

        PerfectInterpolator(final TestFieldProblemAbstract<T> problem) {
            this.problem = problem;
        }

        public void setPreviousTime(T previousTime) {
            this.previousTime = previousTime;
        }

        public void setCurrentTime(T currentTime) {
            this.currentTime = currentTime;
        }

        public T getCurrentTime() {
            return currentTime;
        }

        @Override
        public boolean isForward() {
            return problem.getFinalTime().subtract(problem.getInitialState().getTime()).getReal() >= 0;
        }

        @Override
        public FieldODEStateAndDerivative<T> getPreviousState() {
            return getInterpolatedState(previousTime);
        }

        @Override
        public FieldODEStateAndDerivative<T> getCurrentState() {
            return getInterpolatedState(currentTime);
        }

        @Override
        public FieldODEStateAndDerivative<T> getInterpolatedState(T time) {
            T[] y    = problem.computeTheoreticalState(time);
            T[] yDot = problem.computeDerivatives(time, y);
            return new FieldODEStateAndDerivative<>(time, y, yDot);
        }
    }
}
