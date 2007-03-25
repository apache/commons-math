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

/**
 * Provide a default implementation for several functions useful to generic
 * integrators.
 *  
 * @version $Revision: 480440 $ $Date: 2006-11-29 08:14:12 +0100 (mer., 29 nov. 2006) $
 */
public class FirstOrderIntegratorFactoryImpl extends
        FirstOrderIntegratorFactory {

    /**
     * Create a new fixed stepsize {@link FirstOrderIntegrator}.
     * This factory buid a {@link ClassicalRungeKuttaIntegrator
     * classical Runge-Kutta} integrator by default.
     * @param step the fixed stepsize.
     * @return the new fixed step integrator
     */
    public FirstOrderIntegrator newDefaultFixedStepsizeIntegrator(double step) {
        return newClassicalRungeKuttaIntegrator(step);
    }

    /**
     * Create a new {@link AdaptiveStepsizeIntegrator}.
     * This factory buid a {@link DormandPrince853Integrator
     * Dormand-Prince 8(5,3)} integrator by default.
     * @param minStep minimal step (must be positive even for backward
     * integration), the last step can be smaller than this
     * @param maxStep maximal step (must be positive even for backward
     * integration)
     * @param scalAbsoluteTolerance allowed absolute error
     * @param scalRelativeTolerance allowed relative error
     * @return the new adaptive stepsize integrator
     */
    public AdaptiveStepsizeIntegrator newDefaultAdaptiveStepsizeIntegrator(
            double minStep, double maxStep, double scalAbsoluteTolerance,
            double scalRelativeTolerance) {
        return newDormandPrince853Integrator(minStep, maxStep,
                scalAbsoluteTolerance, scalRelativeTolerance);
    }

    /**
     * Create a new {@link AdaptiveStepsizeIntegrator}.
     * This factory buid a {@link DormandPrince853Integrator
     * Dormand-Prince 8(5,3)} integrator by default.
     * @param minStep minimal step (must be positive even for backward
     * integration), the last step can be smaller than this
     * @param maxStep maximal step (must be positive even for backward
     * integration)
     * @param vecAbsoluteTolerance allowed absolute error
     * @param vecRelativeTolerance allowed relative error
     * @return the new adaptive stepsize integrator
     */
    public AdaptiveStepsizeIntegrator newDefaultAdaptiveStepsizeIntegrator(
            double minStep, double maxStep, double[] vecAbsoluteTolerance,
            double[] vecRelativeTolerance) {
        return newDormandPrince853Integrator(minStep, maxStep,
                vecAbsoluteTolerance, vecRelativeTolerance);
    }

    /**
     * Create a new fixed stepsize {@link FirstOrderIntegrator}.
     * This factory buid a {@link EulerIntegrator Euler} integrator.
     * @param step the fixed stepsize.
     * @return the new fixed step integrator
     */
    public FirstOrderIntegrator newEulerIntegrator(double step) {
        return new EulerIntegrator(step);
    }

    /**
     * Create a new fixed stepsize {@link FirstOrderIntegrator}.
     * This factory buid a {@link MidpointIntegrator midpoint} integrator.
     * @param step the fixed stepsize.
     * @return the new fixed step integrator
     */
    public FirstOrderIntegrator newMidpointIntegrator(double step) {
        return new MidpointIntegrator(step);
    }
    /**
     * Create a new fixed stepsize {@link FirstOrderIntegrator}.
     * This factory buid a {@link ClassicalRungeKuttaIntegrator
     * classical Runge-Kutta} integrator.
     * @param step the fixed stepsize.
     * @return the new fixed step integrator
     */

    public FirstOrderIntegrator newClassicalRungeKuttaIntegrator(double step) {
        return new ClassicalRungeKuttaIntegrator(step);
    }

    /**
     * Create a new fixed stepsize {@link FirstOrderIntegrator}.
     * This factory buid a {@link GillIntegrator Gill} integrator.
     * @param step the fixed stepsize.
     * @return the new fixed step integrator
     */
    public FirstOrderIntegrator newGillIntegrator(double step) {
        return new GillIntegrator(step);
    }

    /**
     * Create a new fixed stepsize {@link FirstOrderIntegrator}.
     * This factory buid a {@link ThreeEighthesIntegrator 3/8} integrator.
     * @param step the fixed stepsize.
     * @return the new fixed step integrator
     */
    public FirstOrderIntegrator newThreeEighthesIntegrator(double step) {
        return new ThreeEighthesIntegrator(step);
    }

    /**
     * Create a new {@link AdaptiveStepsizeIntegrator}.
     * This factory buid a {@link HighamHall54Integrator Higham-Hall} integrator.
     * @param minStep minimal step (must be positive even for backward
     * integration), the last step can be smaller than this
     * @param maxStep maximal step (must be positive even for backward
     * integration)
     * @param scalAbsoluteTolerance allowed absolute error
     * @param scalRelativeTolerance allowed relative error
     * @return the new adaptive stepsize integrator
     */
    public AdaptiveStepsizeIntegrator newHighamHall54Integrator(double minStep,
            double maxStep, double scalAbsoluteTolerance,
            double scalRelativeTolerance) {
        return new HighamHall54Integrator(minStep, maxStep,
                scalAbsoluteTolerance, scalRelativeTolerance);
    }

    /**
     * Create a new {@link AdaptiveStepsizeIntegrator}.
     * This factory buid a {@link HighamHall54Integrator Higham-Hall} integrator.
     * @param minStep minimal step (must be positive even for backward
     * integration), the last step can be smaller than this
     * @param maxStep maximal step (must be positive even for backward
     * integration)
     * @param vecAbsoluteTolerance allowed absolute error
     * @param vecRelativeTolerance allowed relative error
     * @return the new adaptive stepsize integrator
     */
    public AdaptiveStepsizeIntegrator newHighamHall54Integrator(double minStep,
            double maxStep, double[] vecAbsoluteTolerance,
            double[] vecRelativeTolerance) {
        return new HighamHall54Integrator(minStep, maxStep,
                vecAbsoluteTolerance, vecRelativeTolerance);
    }

    /**
     * Create a new {@link AdaptiveStepsizeIntegrator}.
     * This factory buid a {@link DormandPrince54Integrator
     * Dormand-Prince 5(4)} integrator.
     * @param minStep minimal step (must be positive even for backward
     * integration), the last step can be smaller than this
     * @param maxStep maximal step (must be positive even for backward
     * integration)
     * @param scalAbsoluteTolerance allowed absolute error
     * @param scalRelativeTolerance allowed relative error
     * @return the new adaptive stepsize integrator
     */
    public AdaptiveStepsizeIntegrator newDormandPrince54Integrator(
            double minStep, double maxStep, double scalAbsoluteTolerance,
            double scalRelativeTolerance) {
        return new DormandPrince54Integrator(minStep, maxStep,
                scalAbsoluteTolerance, scalRelativeTolerance);
    }

    /**
     * Create a new {@link AdaptiveStepsizeIntegrator}.
     * This factory buid a {@link DormandPrince54Integrator
     * Dormand-Prince 5(4)} integrator.
     * @param minStep minimal step (must be positive even for backward
     * integration), the last step can be smaller than this
     * @param maxStep maximal step (must be positive even for backward
     * integration)
     * @param vecAbsoluteTolerance allowed absolute error
     * @param vecRelativeTolerance allowed relative error
     * @return the new adaptive stepsize integrator
     */
    public AdaptiveStepsizeIntegrator newDormandPrince54Integrator(double minStep,
            double maxStep, double[] vecAbsoluteTolerance,
            double[] vecRelativeTolerance) {
        return new DormandPrince54Integrator(minStep, maxStep,
                vecAbsoluteTolerance, vecRelativeTolerance);
    }

    /**
     * Create a new {@link AdaptiveStepsizeIntegrator}.
     * This factory buid a {@link DormandPrince853Integrator
     * Dormand-Prince 8(5,3)} integrator.
     * @param minStep minimal step (must be positive even for backward
     * integration), the last step can be smaller than this
     * @param maxStep maximal step (must be positive even for backward
     * integration)
     * @param scalAbsoluteTolerance allowed absolute error
     * @param scalRelativeTolerance allowed relative error
     * @return the new adaptive stepsize integrator
     */
    public AdaptiveStepsizeIntegrator newDormandPrince853Integrator(
            double minStep, double maxStep, double scalAbsoluteTolerance,
            double scalRelativeTolerance) {
        return new DormandPrince853Integrator(minStep, maxStep,
                scalAbsoluteTolerance, scalRelativeTolerance);
    }

    /**
     * Create a new {@link AdaptiveStepsizeIntegrator}.
     * This factory buid a {@link DormandPrince853Integrator
     * Dormand-Prince 8(5,3)} integrator.
     * @param minStep minimal step (must be positive even for backward
     * integration), the last step can be smaller than this
     * @param maxStep maximal step (must be positive even for backward
     * integration)
     * @param vecAbsoluteTolerance allowed absolute error
     * @param vecRelativeTolerance allowed relative error
     * @return the new adaptive stepsize integrator
     */
    public AdaptiveStepsizeIntegrator newDormandPrince853Integrator(
            double minStep, double maxStep, double[] vecAbsoluteTolerance,
            double[] vecRelativeTolerance) {
        return new DormandPrince853Integrator(minStep, maxStep,
                vecAbsoluteTolerance, vecRelativeTolerance);
    }

    /**
     * Create a new {@link AdaptiveStepsizeIntegrator}.
     * This factory buid a {@link GraggBulirschStoerIntegrator
     * Gragg-Bulirsch-Stoer} integrator.
     * @param minStep minimal step (must be positive even for backward
     * integration), the last step can be smaller than this
     * @param maxStep maximal step (must be positive even for backward
     * integration)
     * @param scalAbsoluteTolerance allowed absolute error
     * @param scalRelativeTolerance allowed relative error
     * @return the new adaptive stepsize integrator
     */
    public AdaptiveStepsizeIntegrator newGraggBulirschStoerIntegrator(
            double minStep, double maxStep, double scalAbsoluteTolerance,
            double scalRelativeTolerance) {
        return new GraggBulirschStoerIntegrator(minStep, maxStep,
                scalAbsoluteTolerance, scalRelativeTolerance);
    }

    /**
     * Create a new {@link AdaptiveStepsizeIntegrator}.
     * This factory buid a {@link GraggBulirschStoerIntegrator
     * Gragg-Bulirsch-Stoer} integrator.
     * @param minStep minimal step (must be positive even for backward
     * integration), the last step can be smaller than this
     * @param maxStep maximal step (must be positive even for backward
     * integration)
     * @param vecAbsoluteTolerance allowed absolute error
     * @param vecRelativeTolerance allowed relative error
     * @return the new adaptive stepsize integrator
     */
    public AdaptiveStepsizeIntegrator newGraggBulirschStoerIntegrator(
            double minStep, double maxStep, double[] vecAbsoluteTolerance,
            double[] vecRelativeTolerance) {
        return new GraggBulirschStoerIntegrator(minStep, maxStep,
                vecAbsoluteTolerance, vecRelativeTolerance);
    }

}
