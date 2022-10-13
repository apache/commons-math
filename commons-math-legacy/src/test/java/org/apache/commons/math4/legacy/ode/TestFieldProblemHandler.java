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

package org.apache.commons.math4.legacy.ode;

import org.apache.commons.math4.legacy.core.RealFieldElement;
import org.apache.commons.math4.legacy.exception.MaxCountExceededException;
import org.apache.commons.math4.legacy.ode.sampling.FieldStepHandler;
import org.apache.commons.math4.legacy.ode.sampling.FieldStepInterpolator;

/**
 * This class is used to handle steps for the test problems
 * integrated during the junit tests for the ODE integrators.
 * @param <T> the type of the field elements
 */
public class TestFieldProblemHandler<T extends RealFieldElement<T>>
    implements FieldStepHandler<T> {

    /** Associated problem. */
    private TestFieldProblemAbstract<T> problem;

    /** Maximal errors encountered during the integration. */
    private T maxValueError;
    private T maxTimeError;

    /** Error at the end of the integration. */
    private T lastError;

    /** Time at the end of integration. */
    private T lastTime;

    /** ODE solver used. */
    private FirstOrderFieldIntegrator<T> integrator;

    /** Expected start for step. */
    private T expectedStepStart;

    /**
     * Simple constructor.
     * @param problem problem for which steps should be handled
     * @param integrator ODE solver used
     */
    public TestFieldProblemHandler(TestFieldProblemAbstract<T> problem, FirstOrderFieldIntegrator<T> integrator) {
        this.problem      = problem;
        this.integrator   = integrator;
        maxValueError     = problem.getField().getZero();
        maxTimeError      = problem.getField().getZero();
        lastError         = problem.getField().getZero();
        expectedStepStart = null;
    }

    @Override
    public void init(FieldODEStateAndDerivative<T> state0, T t) {
        maxValueError     = problem.getField().getZero();
        maxTimeError      = problem.getField().getZero();
        lastError         = problem.getField().getZero();
        expectedStepStart = null;
    }

    @Override
    public void handleStep(FieldStepInterpolator<T> interpolator, boolean isLast) throws MaxCountExceededException {

        T start = integrator.getCurrentStepStart().getTime();
        if (start.subtract(problem.getInitialState().getTime()).divide(integrator.getCurrentSignedStepsize()).abs().getReal() > 0.001) {
            // multistep integrators do not handle the first steps themselves
            // so we have to make sure the integrator we look at has really started its work
            if (expectedStepStart != null) {
                // the step should either start at the end of the integrator step
                // or at an event if the step is split into several substeps
                T stepError = RealFieldElement.max(maxTimeError, start.subtract(expectedStepStart).abs());
                for (T eventTime : problem.getTheoreticalEventsTimes()) {
                    stepError = RealFieldElement.min(stepError, start.subtract(eventTime).abs());
                }
                maxTimeError = RealFieldElement.max(maxTimeError, stepError);
            }
            expectedStepStart = start.add(integrator.getCurrentSignedStepsize());
        }

        T pT = interpolator.getPreviousState().getTime();
        T cT = interpolator.getCurrentState().getTime();
        T[] errorScale = problem.getErrorScale();

        // store the error at the last step
        if (isLast) {
            T[] interpolatedY = interpolator.getInterpolatedState(cT).getState();
            T[] theoreticalY  = problem.computeTheoreticalState(cT);
            for (int i = 0; i < interpolatedY.length; ++i) {
                T error = interpolatedY[i].subtract(theoreticalY[i]).abs();
                lastError = RealFieldElement.max(error, lastError);
            }
            lastTime = cT;
        }

        // walk through the step
        for (int k = 0; k <= 20; ++k) {

            T time = pT.add(cT.subtract(pT).multiply(k).divide(20));
            T[] interpolatedY = interpolator.getInterpolatedState(time).getState();
            T[] theoreticalY  = problem.computeTheoreticalState(time);

            // update the errors
            for (int i = 0; i < interpolatedY.length; ++i) {
                T error = errorScale[i].multiply(interpolatedY[i].subtract(theoreticalY[i]).abs());
                maxValueError = RealFieldElement.max(error, maxValueError);
            }
        }
    }

    /**
     * Get the maximal value error encountered during integration.
     * @return maximal value error
     */
    public T getMaximalValueError() {
        return maxValueError;
    }

    /**
     * Get the maximal time error encountered during integration.
     * @return maximal time error
     */
    public T getMaximalTimeError() {
        return maxTimeError;
    }

    /**
     * Get the error at the end of the integration.
     * @return error at the end of the integration
     */
    public T getLastError() {
        return lastError;
    }

    /**
     * Get the time at the end of the integration.
     * @return time at the end of the integration.
     */
    public T getLastTime() {
        return lastTime;
    }
}
