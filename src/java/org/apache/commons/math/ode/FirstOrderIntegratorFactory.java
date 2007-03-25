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

import org.apache.commons.discovery.tools.DiscoverClass;

/**
 * Abstract factory class used to create {@link FirstOrderIntegrator} instances.
 * <p>
 * Integrators implementing the following fixed stepsize algorithms are supported:
 * <ul>
 * <li>Euler</li>
 * <li>midpoint</li>
 * <li>classical Runge-Kutta</li>
 * <li>Gill</li>
 * <li>3/8</li>
 * </ul>
 * Concrete factories extending this class also specify a default fixed stepsize integrator,
 * instances of which are returned by <code>newDefaultFixedStepsizeIntegrator</code>.
 * <p>
 * <p>
 * Integrators implementing the following adaptive stepsize algorithms are supported:
 * <ul>
 * <li>Higham-Hall</li>
 * <li>Dormand-Prince 5(4)</li>
 * <li>Dormand-Pince 8(5,3)</li>
 * <li>Gragg-Bulirsch-Stoer</li>
 * </ul>
 * Concrete factories extending this class also specify default adaptive stepsize integrators,
 * instances of which are returned by the two <code>newDefaultAdaptiveStepsizeIntegrator</code>
 * methods.
 * <p>
 * Common usage:<pre>
 * FirstOrderIntegratorFactory factory = FirstOrderIntegratorFactory.newInstance();
 *
 * // create a Dormand-Prince 8(5,3) integrator to use with some step control parameters
 * AdaptiveStepsizeIntegrator integrator =
 *   factory.newDormandPrince853Integrator(minStep, maxStep,
 *                                         scalAbsoluteTolerance,
 *                                         scalRelativeTolerance);
 * </pre>
 *
 * <a href="http://jakarta.apache.org/commons/discovery/">Jakarta Commons Discovery</a>
 * is used to determine the concrete factory returned by 
 * <code>FirstOrderIntegratorFactory.newInstance().</code>  The default is
 * {@link FirstOrderIntegratorFactoryImpl}
 *
 * @version $Revision: 480440 $ $Date: 2006-11-29 08:14:12 +0100 (mer., 29 nov. 2006) $
 */
public abstract class FirstOrderIntegratorFactory {

    /**
     * Default constructor.
     */
    protected FirstOrderIntegratorFactory() {
    }

    /**
     * Create a new factory.
     * @return a new factory
     */
    public static FirstOrderIntegratorFactory newInstance() {
        FirstOrderIntegratorFactory factory = null;
        try {
            DiscoverClass dc = new DiscoverClass();
            factory = (FirstOrderIntegratorFactory) dc.newInstance(
                FirstOrderIntegratorFactory.class,
                "org.apache.commons.math.analysis.FirstOrderIntegratorFactoryImpl");
        } catch(Throwable t) {
            return new FirstOrderIntegratorFactoryImpl();
        }
        return factory;
    }
    
    /**
     * Create a new fixed stepsize {@link FirstOrderIntegrator}.
     * The actual integrator returned is determined by the underlying factory.
     * @param step the fixed stepsize.
     * @return the new fixed step integrator
     */
    public abstract FirstOrderIntegrator newDefaultFixedStepsizeIntegrator(double step);
    
    /**
     * Create a new {@link AdaptiveStepsizeIntegrator}.
     * The actual integrator returned is determined by the underlying factory.
     * @param minStep minimal step (must be positive even for backward
     * integration), the last step can be smaller than this
     * @param maxStep maximal step (must be positive even for backward
     * integration)
     * @param scalAbsoluteTolerance allowed absolute error
     * @param scalRelativeTolerance allowed relative error
     * @return the new adaptive stepsize integrator
     */
    public abstract AdaptiveStepsizeIntegrator newDefaultAdaptiveStepsizeIntegrator(
            double minStep, double maxStep,
            double scalAbsoluteTolerance,
            double scalRelativeTolerance);
    
    /**
     * Create a new {@link AdaptiveStepsizeIntegrator}.
     * The actual integrator returned is determined by the underlying factory.
     * @param minStep minimal step (must be positive even for backward
     * integration), the last step can be smaller than this
     * @param maxStep maximal step (must be positive even for backward
     * integration)
     * @param vecAbsoluteTolerance allowed absolute error
     * @param vecRelativeTolerance allowed relative error
     * @return the new adaptive stepsize integrator
     */
    public abstract AdaptiveStepsizeIntegrator newDefaultAdaptiveStepsizeIntegrator(
            double minStep, double maxStep,
            double[] vecAbsoluteTolerance,
            double[] vecRelativeTolerance);
    
    /**
     * Create a new {@link FirstOrderIntegrator}.
     * The integrator is an implementation of the Euler method.
     * @param step the fixed stepsize.
     * @return the new fixed step integrator
     */
    public abstract FirstOrderIntegrator newEulerIntegrator(double step);

    /**
     * Create a new {@link FirstOrderIntegrator}.
     * The integrator is an implementation of the midpoint method.
     * @param step the fixed stepsize.
     * @return the new fixed step integrator
     */
    public abstract FirstOrderIntegrator newMidpointIntegrator(double step);

    /**
     * Create a new {@link FirstOrderIntegrator}.
     * The integrator is an implementation of the classical Runge-Kutta method.
     * @param step the fixed stepsize.
     * @return the new fixed step integrator
     */
    public abstract FirstOrderIntegrator newClassicalRungeKuttaIntegrator(double step);

    /**
     * Create a new {@link FirstOrderIntegrator}.
     * The integrator is an implementation of the Gill method.
     * @param step the fixed stepsize.
     * @return the new fixed step integrator
     */
    public abstract FirstOrderIntegrator newGillIntegrator(double step);

    /**
     * Create a new {@link FirstOrderIntegrator}.
     * The integrator is an implementation of the 3/8 method.
     * @param step the fixed stepsize.
     * @return the new fixed step integrator
     */
    public abstract FirstOrderIntegrator newThreeEighthesIntegrator(double step);

    /**
     * Create a new {@link AdaptiveStepsizeIntegrator}.
     * The integrator is an implementation of the Higham-Hall method.
     * @param minStep minimal step (must be positive even for backward
     * integration), the last step can be smaller than this
     * @param maxStep maximal step (must be positive even for backward
     * integration)
     * @param scalAbsoluteTolerance allowed absolute error
     * @param scalRelativeTolerance allowed relative error
     * @return the new adaptive stepsize integrator
     */
    public abstract AdaptiveStepsizeIntegrator newHighamHall54Integrator(
            double minStep, double maxStep,
            double scalAbsoluteTolerance,
            double scalRelativeTolerance);

    /**
     * Create a new {@link AdaptiveStepsizeIntegrator}.
     * The integrator is an implementation of the Higham-Hall method.
     * @param minStep minimal step (must be positive even for backward
     * integration), the last step can be smaller than this
     * @param maxStep maximal step (must be positive even for backward
     * integration)
     * @param vecAbsoluteTolerance allowed absolute error
     * @param vecRelativeTolerance allowed relative error
     * @return the new adaptive stepsize integrator
     */
    public abstract AdaptiveStepsizeIntegrator newHighamHall54Integrator(
            double minStep, double maxStep,
            double[] vecAbsoluteTolerance,
            double[] vecRelativeTolerance);

    /**
     * Create a new {@link AdaptiveStepsizeIntegrator}.
     * The integrator is an implementation of the Dormand-Prince 5(4) method.
     * @param minStep minimal step (must be positive even for backward
     * integration), the last step can be smaller than this
     * @param maxStep maximal step (must be positive even for backward
     * integration)
     * @param scalAbsoluteTolerance allowed absolute error
     * @param scalRelativeTolerance allowed relative error
     * @return the new adaptive stepsize integrator
     */
    public abstract AdaptiveStepsizeIntegrator newDormandPrince54Integrator(
            double minStep, double maxStep,
            double scalAbsoluteTolerance,
            double scalRelativeTolerance);

    /**
     * Create a new {@link AdaptiveStepsizeIntegrator}.
     * The integrator is an implementation of the Dormand-Prince 5(4) method.
     * @param minStep minimal step (must be positive even for backward
     * integration), the last step can be smaller than this
     * @param maxStep maximal step (must be positive even for backward
     * integration)
     * @param vecAbsoluteTolerance allowed absolute error
     * @param vecRelativeTolerance allowed relative error
     * @return the new adaptive stepsize integrator
     */
    public abstract AdaptiveStepsizeIntegrator newDormandPrince54Integrator(
            double minStep, double maxStep,
            double[] vecAbsoluteTolerance,
            double[] vecRelativeTolerance);

    /**
     * Create a new {@link AdaptiveStepsizeIntegrator}.
     * The integrator is an implementation of the Dormand-Prince 8(5,3) method.
     * @param minStep minimal step (must be positive even for backward
     * integration), the last step can be smaller than this
     * @param maxStep maximal step (must be positive even for backward
     * integration)
     * @param scalAbsoluteTolerance allowed absolute error
     * @param scalRelativeTolerance allowed relative error
     * @return the new adaptive stepsize integrator
     */
    public abstract AdaptiveStepsizeIntegrator newDormandPrince853Integrator(
            double minStep, double maxStep,
            double scalAbsoluteTolerance,
            double scalRelativeTolerance);

    /**
     * Create a new {@link AdaptiveStepsizeIntegrator}.
     * The integrator is an implementation of the Dormand-Prince 8(5,3) method.
     * @param minStep minimal step (must be positive even for backward
     * integration), the last step can be smaller than this
     * @param maxStep maximal step (must be positive even for backward
     * integration)
     * @param vecAbsoluteTolerance allowed absolute error
     * @param vecRelativeTolerance allowed relative error
     * @return the new adaptive stepsize integrator
     */
    public abstract AdaptiveStepsizeIntegrator newDormandPrince853Integrator(
            double minStep, double maxStep,
            double[] vecAbsoluteTolerance,
            double[] vecRelativeTolerance);

    /**
     * Create a new {@link AdaptiveStepsizeIntegrator}.
     * The integrator is an implementation of the Gragg-Burlisch-Stoer method.
     * @param minStep minimal step (must be positive even for backward
     * integration), the last step can be smaller than this
     * @param maxStep maximal step (must be positive even for backward
     * integration)
     * @param scalAbsoluteTolerance allowed absolute error
     * @param scalRelativeTolerance allowed relative error
     * @return the new adaptive stepsize integrator
     */
    public abstract AdaptiveStepsizeIntegrator newGraggBulirschStoerIntegrator(
            double minStep, double maxStep,
            double scalAbsoluteTolerance,
            double scalRelativeTolerance);

    /**
     * Create a new {@link AdaptiveStepsizeIntegrator}.
     * The integrator is an implementation of the Gragg-Burlisch-Stoer method.
     * @param minStep minimal step (must be positive even for backward
     * integration), the last step can be smaller than this
     * @param maxStep maximal step (must be positive even for backward
     * integration)
     * @param vecAbsoluteTolerance allowed absolute error
     * @param vecRelativeTolerance allowed relative error
     * @return the new adaptive stepsize integrator
     */
    public abstract AdaptiveStepsizeIntegrator newGraggBulirschStoerIntegrator(
            double minStep, double maxStep,
            double[] vecAbsoluteTolerance,
            double[] vecRelativeTolerance);

}
