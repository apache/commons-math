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

import java.io.Externalizable;

/** This interface represents an interpolator over the last step
 * during an ODE integration.
 *
 * <p>The various ODE integrators provide objects implementing this
 * interface to the step handlers. These objects are often custom
 * objects tightly bound to the integrator internal algorithms. The
 * handlers can use these objects to retrieve the state vector at
 * intermediate times between the previous and the current grid points
 * (this feature is often called dense output).</p>
 *
 * @see FirstOrderIntegrator
 * @see SecondOrderIntegrator
 * @see StepHandler
 *
 * @version $Id: StepInterpolator.java 1705 2006-09-17 19:57:39Z luc $
 *
 */

public interface StepInterpolator
  extends Externalizable {

  /**
   * Get the previous grid point time.
   * @return previous grid point time
   */
  public double getPreviousTime();
    
  /**
   * Get the current grid point time.
   * @return current grid point time
   */
  public double getCurrentTime();
    
  /**
   * Get the time of the interpolated point.
   * If {@link #setInterpolatedTime} has not been called, it returns
   * the current grid point time.
   * @return interpolation point time
   */
  public double getInterpolatedTime();
    
  /**
   * Set the time of the interpolated point.
   * <p>Setting the time outside of the current step is now allowed
   * (it was not allowed up to version 5.4 of Mantissa), but should be
   * used with care since the accuracy of the interpolator will
   * probably be very poor far from this step. This allowance has been
   * added to simplify implementation of search algorithms near the
   * step endpoints.</p>
   * @param time time of the interpolated point
   * @throws DerivativeException if this call induces an automatic
   * step finalization that throws one
   */
  public void setInterpolatedTime(double time)
    throws DerivativeException;

  /**
   * Get the state vector of the interpolated point.
   * @return state vector at time {@link #getInterpolatedTime}
   */
  public double[] getInterpolatedState();

  /** Check if the natural integration direction is forward.
   * <p>This method provides the integration direction as specified by
   * the integrator itself, it avoid some nasty problems in
   * degenerated cases like null steps due to cancellation at step
   * initialization, step control or switching function
   * triggering.</p>
   * @return true if the integration variable (time) increases during
   * integration
   */
  public boolean isForward();

}
