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

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.IOException;

/** This class is a step interpolator that does nothing.
 *
 * <p>This class is used when the {@link StepHandler "step handler"}
 * set up by the user does not need step interpolation. It does not
 * recompute the state when {@link AbstractStepInterpolator#setInterpolatedTime
 * setInterpolatedTime} is called. This implies the interpolated state
 * is always the state at the end of the current step.</p>
 *
 * @see StepHandler
 *
 * @version $Id: DummyStepInterpolator.java 1705 2006-09-17 19:57:39Z luc $
 *
 */

public class DummyStepInterpolator
  extends AbstractStepInterpolator {

  /** Simple constructor.
   * This constructor builds an instance that is not usable yet, the
   * <code>AbstractStepInterpolator.reinitialize</code> protected method
   * should be called before using the instance in order to initialize
   * the internal arrays. This constructor is used only in order to delay
   * the initialization in some cases. As an example, the {@link
   * RungeKuttaFehlbergIntegrator} uses the prototyping design pattern
   * to create the step interpolators by cloning an uninitialized
   * model and latter initializing the copy.
   */
  public DummyStepInterpolator() {
    super();
  }

  /** Simple constructor.
   * @param y reference to the integrator array holding the state at
   * the end of the step
   * @param forward integration direction indicator
   */
  protected DummyStepInterpolator(double[] y, boolean forward) {
    super(y, forward);
  }

  /** Copy constructor.

   * <p>The copied interpolator should have been finalized before the
   * copy, otherwise the copy will not be able to perform correctly
   * any interpolation and will throw a {@link NullPointerException}
   * later. Since we don't want this constructor to throw the
   * exceptions finalization may involve and since we don't want this
   * method to modify the state of the copied interpolator,
   * finalization is <strong>not</strong> done automatically, it
   * remains under user control.</p>

   * <p>The copy is a deep copy: its arrays are separated from the
   * original arrays of the instance.</p>

   * @param interpolator interpolator to copy from.

   */
  protected DummyStepInterpolator(DummyStepInterpolator interpolator) {
    super(interpolator);
  }

  /** Compute the state at the interpolated time.
   * In this class, this method does nothing: the interpolated state
   * is always the state at the end of the current step.
   * @param theta normalized interpolation abscissa within the step
   * (theta is zero at the previous time step and one at the current time step)
   * @param oneMinusThetaH time gap between the interpolated time and
   * the current time
   * @throws DerivativeException this exception is propagated to the caller if the
   * underlying user function triggers one
   */
  protected void computeInterpolatedState(double theta, double oneMinusThetaH)
    throws DerivativeException {
  }
    
  public void writeExternal(ObjectOutput out)
    throws IOException {
    // save the state of the base class
    writeBaseExternal(out);
  }

  public void readExternal(ObjectInput in)
    throws IOException {

    // read the base class 
    double t = readBaseExternal(in);

    try {
      // we can now set the interpolated time and state
      setInterpolatedTime(t);
    } catch (DerivativeException e) {
      throw new IOException(e.getMessage());
    }

  }

  private static final long serialVersionUID = 1708010296707839488L;

}
