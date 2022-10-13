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
package org.apache.commons.math4.legacy.ode.sampling;


import org.apache.commons.math4.legacy.core.RealFieldElement;
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.MaxCountExceededException;
import org.apache.commons.math4.legacy.exception.NoBracketingException;
import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;
import org.apache.commons.math4.legacy.ode.FieldExpandableODE;
import org.apache.commons.math4.legacy.ode.FirstOrderFieldIntegrator;
import org.apache.commons.math4.legacy.ode.FieldODEStateAndDerivative;
import org.apache.commons.math4.legacy.ode.FirstOrderIntegrator;
import org.apache.commons.math4.legacy.ode.TestFieldProblemAbstract;
import org.apache.commons.math4.legacy.ode.TestProblemAbstract;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.junit.Assert;

public final class StepInterpolatorTestUtils {

    /** No instances. */
    private StepInterpolatorTestUtils() {}

    public static void checkDerivativesConsistency(final FirstOrderIntegrator integrator,
                                                   final TestProblemAbstract problem,
                                                   final double finiteDifferencesRatio,
                                                   final double threshold)
        throws DimensionMismatchException, NumberIsTooSmallException,
               MaxCountExceededException, NoBracketingException {
        integrator.addStepHandler(new StepHandler() {

            @Override
            public void handleStep(StepInterpolator interpolator, boolean isLast)
                throws MaxCountExceededException {

                final double dt = interpolator.getCurrentTime() - interpolator.getPreviousTime();
                final double h  = finiteDifferencesRatio * dt;
                final double t  = interpolator.getCurrentTime() - 0.3 * dt;

                if (JdkMath.abs(h) < 10 * JdkMath.ulp(t)) {
                    return;
                }

                interpolator.setInterpolatedTime(t - 4 * h);
                final double[] yM4h = interpolator.getInterpolatedState().clone();
                interpolator.setInterpolatedTime(t - 3 * h);
                final double[] yM3h = interpolator.getInterpolatedState().clone();
                interpolator.setInterpolatedTime(t - 2 * h);
                final double[] yM2h = interpolator.getInterpolatedState().clone();
                interpolator.setInterpolatedTime(t - h);
                final double[] yM1h = interpolator.getInterpolatedState().clone();
                interpolator.setInterpolatedTime(t + h);
                final double[] yP1h = interpolator.getInterpolatedState().clone();
                interpolator.setInterpolatedTime(t + 2 * h);
                final double[] yP2h = interpolator.getInterpolatedState().clone();
                interpolator.setInterpolatedTime(t + 3 * h);
                final double[] yP3h = interpolator.getInterpolatedState().clone();
                interpolator.setInterpolatedTime(t + 4 * h);
                final double[] yP4h = interpolator.getInterpolatedState().clone();

                interpolator.setInterpolatedTime(t);
                final double[] yDot = interpolator.getInterpolatedDerivatives();

                for (int i = 0; i < yDot.length; ++i) {
                    final double approYDot = ( -3 * (yP4h[i] - yM4h[i]) +
                                               32 * (yP3h[i] - yM3h[i]) +
                                             -168 * (yP2h[i] - yM2h[i]) +
                                              672 * (yP1h[i] - yM1h[i])) / (840 * h);
                    Assert.assertEquals("" + (approYDot - yDot[i]), approYDot, yDot[i], threshold);
                }
            }

            @Override
            public void init(double t0, double[] y0, double t) {
            }
        });

        integrator.integrate(problem,
                             problem.getInitialTime(), problem.getInitialState(),
                             problem.getFinalTime(), new double[problem.getDimension()]);
    }

    public static <T extends RealFieldElement<T>> void checkDerivativesConsistency(final FirstOrderFieldIntegrator<T> integrator,
                                                                                   final TestFieldProblemAbstract<T> problem,
                                                                                   final double threshold) {
        integrator.addStepHandler(new FieldStepHandler<T>() {

            @Override
            public void handleStep(FieldStepInterpolator<T> interpolator, boolean isLast)
                throws MaxCountExceededException {

                final T h = interpolator.getCurrentState().getTime().subtract(interpolator.getPreviousState().getTime()).multiply(0.001);
                final T t = interpolator.getCurrentState().getTime().subtract(h.multiply(300));

                if (h.abs().subtract(JdkMath.ulp(t.getReal()) * 10).getReal() < 0) {
                    return;
                }

                final T[] yM4h = interpolator.getInterpolatedState(t.add(h.multiply(-4))).getState();
                final T[] yM3h = interpolator.getInterpolatedState(t.add(h.multiply(-3))).getState();
                final T[] yM2h = interpolator.getInterpolatedState(t.add(h.multiply(-2))).getState();
                final T[] yM1h = interpolator.getInterpolatedState(t.add(h.multiply(-1))).getState();
                final T[] yP1h = interpolator.getInterpolatedState(t.add(h.multiply( 1))).getState();
                final T[] yP2h = interpolator.getInterpolatedState(t.add(h.multiply( 2))).getState();
                final T[] yP3h = interpolator.getInterpolatedState(t.add(h.multiply( 3))).getState();
                final T[] yP4h = interpolator.getInterpolatedState(t.add(h.multiply( 4))).getState();

                final T[] yDot = interpolator.getInterpolatedState(t).getDerivative();

                for (int i = 0; i < yDot.length; ++i) {
                    final T approYDot =     yP4h[i].subtract(yM4h[i]).multiply(  -3).
                                        add(yP3h[i].subtract(yM3h[i]).multiply(  32)).
                                        add(yP2h[i].subtract(yM2h[i]).multiply(-168)).
                                        add(yP1h[i].subtract(yM1h[i]).multiply( 672)).
                                        divide(h.multiply(840));
                    Assert.assertEquals(approYDot.getReal(), yDot[i].getReal(), threshold);
                }
            }

            @Override
            public void init(FieldODEStateAndDerivative<T> state0, T t) {
            }
        });

        integrator.integrate(new FieldExpandableODE<>(problem), problem.getInitialState(), problem.getFinalTime());
    }
}

