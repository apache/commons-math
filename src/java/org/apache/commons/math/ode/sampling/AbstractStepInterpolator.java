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

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.IOException;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.SecondOrderIntegrator;
import org.apache.commons.math.ode.nonstiff.EmbeddedRungeKuttaIntegrator;

/** This abstract class represents an interpolator over the last step
 * during an ODE integration.
 *
 * <p>The various ODE integrators provide objects extending this class
 * to the step handlers. The handlers can use these objects to
 * retrieve the state vector at intermediate times between the
 * previous and the current grid points (dense output).</p>
 *
 * @see FirstOrderIntegrator
 * @see SecondOrderIntegrator
 * @see StepHandler
 *
 * @version $Revision$ $Date$
 * @since 1.2
 *
 */

public abstract class AbstractStepInterpolator
  implements StepInterpolator {

  /** previous time */
  protected double previousTime;

  /** current time */
  protected double currentTime;

  /** current time step */
  protected double h;

  /** current state */
  protected double[] currentState;

  /** interpolated time */
  protected double interpolatedTime;

  /** interpolated state */
  protected double[] interpolatedState;

  /** indicate if the step has been finalized or not. */
  private boolean finalized;

  /** integration direction. */
  private boolean forward;

  /** Simple constructor.
   * This constructor builds an instance that is not usable yet, the
   * {@link #reinitialize} method should be called before using the
   * instance in order to initialize the internal arrays. This
   * constructor is used only in order to delay the initialization in
   * some cases. As an example, the {@link
   * EmbeddedRungeKuttaIntegrator} uses the prototyping design pattern
   * to create the step interpolators by cloning an uninitialized
   * model and latter initializing the copy.
   */
  protected AbstractStepInterpolator() {
    previousTime      = Double.NaN;
    currentTime       = Double.NaN;
    h                 = Double.NaN;
    interpolatedTime  = Double.NaN;
    currentState      = null;
    interpolatedState = null;
    finalized         = false;
    this.forward      = true;
  }

  /** Simple constructor.
   * @param y reference to the integrator array holding the state at
   * the end of the step
   * @param forward integration direction indicator
   */
  protected AbstractStepInterpolator(final double[] y, final boolean forward) {

    previousTime      = Double.NaN;
    currentTime       = Double.NaN;
    h                 = Double.NaN;
    interpolatedTime  = Double.NaN;

    currentState      = y;
    interpolatedState = new double[y.length];

    finalized         = false;
    this.forward      = forward;

  }

  /** Copy constructor.

   * <p>The copied interpolator should have been finalized before the
   * copy, otherwise the copy will not be able to perform correctly
   * any derivative computation and will throw a {@link
   * NullPointerException} later. Since we don't want this constructor
   * to throw the exceptions finalization may involve and since we
   * don't want this method to modify the state of the copied
   * interpolator, finalization is <strong>not</strong> done
   * automatically, it remains under user control.</p>

   * <p>The copy is a deep copy: its arrays are separated from the
   * original arrays of the instance.</p>

   * @param interpolator interpolator to copy from.

   */
  protected AbstractStepInterpolator(final AbstractStepInterpolator interpolator) {

    previousTime      = interpolator.previousTime;
    currentTime       = interpolator.currentTime;
    h                 = interpolator.h;
    interpolatedTime  = interpolator.interpolatedTime;

    if (interpolator.currentState != null) {
      currentState      = (double[]) interpolator.currentState.clone();
      interpolatedState = (double[]) interpolator.interpolatedState.clone();
    } else {
      currentState      = null;
      interpolatedState = null;
    }

    finalized = interpolator.finalized;
    forward   = interpolator.forward;

  }

  /** Reinitialize the instance
   * @param y reference to the integrator array holding the state at
   * the end of the step
   * @param forward integration direction indicator
   */
  protected void reinitialize(final double[] y, final boolean forward) {

    previousTime      = Double.NaN;
    currentTime       = Double.NaN;
    h                 = Double.NaN;
    interpolatedTime  = Double.NaN;

    currentState      = y;
    interpolatedState = new double[y.length];

    finalized         = false;
    this.forward      = forward;

  }

  /** Copy the instance.
   * <p>The copied instance is guaranteed to be independent from the
   * original one. Both can be used with different settings for
   * interpolated time without any side effect.</p>
   * @return a deep copy of the instance, which can be used independently.
   * @throws DerivativeException if this call induces an automatic
   * step finalization that throws one
   * @see #setInterpolatedTime(double)
   */
   public StepInterpolator copy() throws DerivativeException {

     // finalize the step before performing copy
     finalizeStep();

     // create the new independent instance
     return doCopy();

   }

   /** Really copy the finalized instance.
    * <p>This method is called by {@link #copy()} after the
    * step has been finalized. It must perform a deep copy
    * to have an new instance completely independent for the
    * original instance.
    * @return a copy of the finalized instance
    */
   protected abstract StepInterpolator doCopy();

  /** Shift one step forward.
   * Copy the current time into the previous time, hence preparing the
   * interpolator for future calls to {@link #storeTime storeTime}
   */
  public void shift() {
    previousTime = currentTime;
  }

  /** Store the current step time.
   * @param t current time
   */
  public void storeTime(final double t) {

    currentTime      = t;
    h                = currentTime - previousTime;
    interpolatedTime = t;
    System.arraycopy(currentState, 0, interpolatedState, 0,
                     currentState.length);

    // the step is not finalized anymore
    finalized = false;

  }

  /**
   * Get the previous grid point time.
   * @return previous grid point time
   */
  public double getPreviousTime() {
    return previousTime;
  }
    
  /**
   * Get the current grid point time.
   * @return current grid point time
   */
  public double getCurrentTime() {
    return currentTime;
  }
    
  /**
   * Get the time of the interpolated point.
   * If {@link #setInterpolatedTime} has not been called, it returns
   * the current grid point time.
   * @return interpolation point time
   */
  public double getInterpolatedTime() {
    return interpolatedTime;
  }
    
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
  public void setInterpolatedTime(final double time)
      throws DerivativeException {
      interpolatedTime = time;
      final double oneMinusThetaH = currentTime - interpolatedTime;
      computeInterpolatedState((h - oneMinusThetaH) / h, oneMinusThetaH);
  }

  /** Check if the natural integration direction is forward.
   * <p>This method provides the integration direction as specified by the
   * integrator itself, it avoid some nasty problems in degenerated
   * cases like null steps due to cancellation at step initialization,
   * step control or discrete events triggering.</p>
   * @return true if the integration variable (time) increases during
   * integration
   */
  public boolean isForward() {
    return forward;
  }

  /** Compute the state at the interpolated time.
   * This is the main processing method that should be implemented by
   * the derived classes to perform the interpolation.
   * @param theta normalized interpolation abscissa within the step
   * (theta is zero at the previous time step and one at the current time step)
   * @param oneMinusThetaH time gap between the interpolated time and
   * the current time
   * @throws DerivativeException this exception is propagated to the caller if the
   * underlying user function triggers one
   */
  protected abstract void computeInterpolatedState(double theta,
                                                   double oneMinusThetaH)
    throws DerivativeException;
    
  /**
   * Get the state vector of the interpolated point.
   * @return state vector at time {@link #getInterpolatedTime}
   */
  public double[] getInterpolatedState() {
    return (double[]) interpolatedState.clone();
  }


  /**
   * Finalize the step.

   * <p>Some embedded Runge-Kutta integrators need fewer functions
   * evaluations than their counterpart step interpolators. These
   * interpolators should perform the last evaluations they need by
   * themselves only if they need them. This method triggers these
   * extra evaluations. It can be called directly by the user step
   * handler and it is called automatically if {@link
   * #setInterpolatedTime} is called.</p>

   * <p>Once this method has been called, <strong>no</strong> other
   * evaluation will be performed on this step. If there is a need to
   * have some side effects between the step handler and the
   * differential equations (for example update some data in the
   * equations once the step has been done), it is advised to call
   * this method explicitly from the step handler before these side
   * effects are set up. If the step handler induces no side effect,
   * then this method can safely be ignored, it will be called
   * transparently as needed.</p>

   * <p><strong>Warning</strong>: since the step interpolator provided
   * to the step handler as a parameter of the {@link
   * StepHandler#handleStep handleStep} is valid only for the duration
   * of the {@link StepHandler#handleStep handleStep} call, one cannot
   * simply store a reference and reuse it later. One should first
   * finalize the instance, then copy this finalized instance into a
   * new object that can be kept.</p>

   * <p>This method calls the protected <code>doFinalize</code> method
   * if it has never been called during this step and set a flag
   * indicating that it has been called once. It is the <code>
   * doFinalize</code> method which should perform the evaluations.
   * This wrapping prevents from calling <code>doFinalize</code> several
   * times and hence evaluating the differential equations too often.
   * Therefore, subclasses are not allowed not reimplement it, they
   * should rather reimplement <code>doFinalize</code>.</p>

   * @throws DerivativeException this exception is propagated to the
   * caller if the underlying user function triggers one

   */
  public final void finalizeStep()
    throws DerivativeException {
    if (! finalized) {
      doFinalize();
      finalized = true;
    }
  }

  /**
   * Really finalize the step.
   * The default implementation of this method does nothing.
   * @throws DerivativeException this exception is propagated to the
   * caller if the underlying user function triggers one
   */
  protected void doFinalize()
    throws DerivativeException {
  }

  /** Write the instance to an output channel.
   * @param out output channel
   * @exception IOException if the instance cannot be written
   */
  public abstract void writeExternal(ObjectOutput out)
    throws IOException;

  /** Read the instance from an input channel.
   * @param in input channel
   * @exception IOException if the instance cannot be read
   */
  public abstract void readExternal(ObjectInput in)
    throws IOException;

  /** Save the base state of the instance.
   * This method performs step finalization if it has not been done
   * before.
   * @param out stream where to save the state
   * @exception IOException in case of write error
   */
  protected void writeBaseExternal(final ObjectOutput out)
    throws IOException {

    out.writeInt(currentState.length);
    out.writeDouble(previousTime);
    out.writeDouble(currentTime);
    out.writeDouble(h);
    out.writeBoolean(forward);

    for (int i = 0; i < currentState.length; ++i) {
      out.writeDouble(currentState[i]);
    }

    out.writeDouble(interpolatedTime);

    // we do not store the interpolated state,
    // it will be recomputed as needed after reading

    // finalize the step (and don't bother saving the now true flag)
    try {
      finalizeStep();
    } catch (DerivativeException e) {
      throw new IOException(e.getMessage());
    }

  }

  /** Read the base state of the instance.
   * This method does <strong>neither</strong> set the interpolated
   * time nor state. It is up to the derived class to reset it
   * properly calling the {@link #setInterpolatedTime} method later,
   * once all rest of the object state has been set up properly.
   * @param in stream where to read the state from
   * @return interpolated time be set later by the caller
   * @exception IOException in case of read error
   */
  protected double readBaseExternal(final ObjectInput in)
    throws IOException {

    final int dimension = in.readInt();
    previousTime  = in.readDouble();
    currentTime   = in.readDouble();
    h             = in.readDouble();
    forward       = in.readBoolean();

    currentState  = new double[dimension];
    for (int i = 0; i < currentState.length; ++i) {
      currentState[i] = in.readDouble();
    }

    // we do NOT handle the interpolated time and state here
    interpolatedTime  = Double.NaN;
    interpolatedState = new double[dimension];

    finalized = true;

    return in.readDouble();

  }

}
