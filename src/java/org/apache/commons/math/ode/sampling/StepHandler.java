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

package org.apache.commons.math.ode.sampling;

import java.io.Serializable;

import org.apache.commons.math.ode.ContinuousOutputModel;
import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.SecondOrderIntegrator;

/**
 * This interface represents a handler that should be called after
 * each successful step.
 *
 * <p>The ODE integrators compute the evolution of the state vector at
 * some grid points that depend on their own internal algorithm. Once
 * they have found a new grid point (possibly after having computed
 * several evaluation of the derivative at intermediate points), they
 * provide it to objects implementing this interface. These objects
 * typically either ignore the intermediate steps and wait for the
 * last one, store the points in an ephemeris, or forward them to
 * specialized processing or output methods.</p>
 *
 * @see FirstOrderIntegrator
 * @see SecondOrderIntegrator
 * @see StepInterpolator
 * @version $Revision$ $Date$
 * @since 1.2
 */

public interface StepHandler extends Serializable {

  /** Determines whether this handler needs dense output.
   * <p>This method allows the integrator to avoid performing extra
   * computation if the handler does not need dense output. If this
   * method returns false, the integrator will call the {@link
   * #handleStep} method with a {@link DummyStepInterpolator} rather
   * than a custom interpolator.</p>
   * @return true if the handler needs dense output
   */
  public boolean requiresDenseOutput();

  /** Reset the step handler.
   * Initialize the internal data as required before the first step is
   * handled.
   */
  public void reset();

  /**
   * Handle the last accepted step
   * @param interpolator interpolator for the last accepted step. For
   * efficiency purposes, the various integrators reuse the same
   * object on each call, so if the instance wants to keep it across
   * all calls (for example to provide at the end of the integration a
   * continuous model valid throughout the integration range, as the
   * {@link ContinuousOutputModel ContinuousOutputModel} class does),
   * it should build a local copy using the clone method of the
   * interpolator and store this copy. Keeping only a reference to the
   * interpolator and reusing it will result in unpredictable
   * behaviour (potentially crashing the application).
   * @param isLast true if the step is the last one
   * @throws DerivativeException this exception is propagated to the
   * caller if the underlying user function triggers one
   */
  public void handleStep(StepInterpolator interpolator, boolean isLast)
    throws DerivativeException;
    
}
