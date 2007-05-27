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

import java.util.ArrayList;
import java.util.Iterator;
import java.io.Serializable;

/**
 * This class stores all information provided by an ODE integrator
 * during the integration process and build a continuous model of the
 * solution from this.

 * <p>This class act as a step handler from the integrator point of
 * view. It is called iteratively during the integration process and
 * stores a copy of all steps information in a sorted collection for
 * later use. Once the integration process is over, the user can use
 * the {@link #setInterpolatedTime setInterpolatedTime} and {@link
 * #getInterpolatedState getInterpolatedState} to retrieve this
 * information at any time. It is important to wait for the
 * integration to be over before attempting to call {@link
 * #setInterpolatedTime setInterpolatedTime} because some internal
 * variables are set only once the last step has been handled.</p>

 * <p>This is useful for example if the main loop of the user
 * application should remain independant from the integration process
 * or if one needs to mimic the behaviour of an analytical model
 * despite a numerical model is used (i.e. one needs the ability to
 * get the model value at any time or to navigate through the
 * data).</p>

 * <p>If problem modelization is done with several separate
 * integration phases for contiguous intervals, the same
 * ContinuousOutputModel can be used as step handler for all
 * integration phases as long as they are performed in order and in
 * the same direction. As an example, one can extrapolate the
 * trajectory of a satellite with one model (i.e. one set of
 * differential equations) up to the beginning of a maneuver, use
 * another more complex model including thrusters modelization and
 * accurate attitude control during the maneuver, and revert to the
 * first model after the end of the maneuver. If the same continuous
 * output model handles the steps of all integration phases, the user
 * do not need to bother when the maneuver begins or ends, he has all
 * the data available in a transparent manner.</p>

 * <p>An important feature of this class is that it implements the
 * <code>Serializable</code> interface. This means that the result of
 * an integration can be serialized and reused later (if stored into a
 * persistent medium like a filesystem or a database) or elsewhere (if
 * sent to another application). Only the result of the integration is
 * stored, there is no reference to the integrated problem by
 * itself.</p>

 * <p>One should be aware that the amount of data stored in a
 * ContinuousOutputModel instance can be important if the state vector
 * is large, if the integration interval is long or if the steps are
 * small (which can result from small tolerance settings in {@link
 * AdaptiveStepsizeIntegrator adaptive step size integrators}).</p>

 * @see StepHandler
 * @see StepInterpolator

 * @version $Id: ContinuousOutputModel.java 1705 2006-09-17 19:57:39Z luc $

 */

public class ContinuousOutputModel
  implements StepHandler, Serializable {

  /** Simple constructor.
   * Build an empty continuous output model.
   */
  public ContinuousOutputModel() {
    steps = new ArrayList();
    reset();
  }

  /** Append another model at the end of the instance.
   * @param model model to add at the end of the instance
   * @exception IllegalArgumentException if the model to append is not
   * compatible with the instance (dimension of the state vector,
   * propagation direction, hole between the dates)
   */
  public void append(ContinuousOutputModel model) {

    if (model.steps.size() == 0) {
      return;
    }

    if (steps.size() == 0) {
      initialTime = model.initialTime;
      forward     = model.forward;
    } else {

      if (getInterpolatedState().length != model.getInterpolatedState().length) {
        throw new IllegalArgumentException("state vector dimension mismatch");
      }

      if (forward ^ model.forward) {
        throw new IllegalArgumentException("propagation direction mismatch");
      }

      StepInterpolator lastInterpolator = (StepInterpolator) steps.get(index);
      double current  = lastInterpolator.getCurrentTime();
      double previous = lastInterpolator.getPreviousTime();
      double step = current - previous;
      double gap = model.getInitialTime() - current;
      if (Math.abs(gap) > 1.0e-3 * Math.abs(step)) {
        throw new IllegalArgumentException("hole between time ranges");
      }

    }

    for (Iterator iter = model.steps.iterator(); iter.hasNext(); ) {
      AbstractStepInterpolator ai = (AbstractStepInterpolator) iter.next();
      steps.add(ai.clone());
    }

    index = steps.size() - 1;
    finalTime = ((StepInterpolator) steps.get(index)).getCurrentTime();

  }

  /** Determines whether this handler needs dense output.
   * <p>The essence of this class is to provide dense output over all
   * steps, hence it requires the internal steps to provide themselves
   * dense output. The method therefore returns always true.</p>
   * @return always true
   */
  public boolean requiresDenseOutput() {
    return true;
  }

  /** Reset the step handler.
   * Initialize the internal data as required before the first step is
   * handled.
   */
  public void reset() {
    initialTime = Double.NaN;
    finalTime   = Double.NaN;
    forward     = true;
    index       = 0;
    steps.clear();
   }

  /** Handle the last accepted step.
   * A copy of the information provided by the last step is stored in
   * the instance for later use.
   * @param interpolator interpolator for the last accepted step.
   * @param isLast true if the step is the last one
   * @throws DerivativeException this exception is propagated to the
   * caller if the underlying user function triggers one
   */
  public void handleStep(StepInterpolator interpolator, boolean isLast)
    throws DerivativeException {

    AbstractStepInterpolator ai = (AbstractStepInterpolator) interpolator;

    if (steps.size() == 0) {
      initialTime = interpolator.getPreviousTime();
      forward     = interpolator.isForward();
    }

    ai.finalizeStep();
    steps.add(ai.clone());

    if (isLast) {
      finalTime = ai.getCurrentTime();
      index     = steps.size() - 1;
    }

  }

  /**
   * Get the initial integration time.
   * @return initial integration time
   */
  public double getInitialTime() {
    return initialTime;
  }
    
  /**
   * Get the final integration time.
   * @return final integration time
   */
  public double getFinalTime() {
    return finalTime;
  }

  /**
   * Get the time of the interpolated point.
   * If {@link #setInterpolatedTime} has not been called, it returns
   * the final integration time.
   * @return interpolation point time
   */
  public double getInterpolatedTime() {
    return ((StepInterpolator) steps.get(index)).getInterpolatedTime();
  }
    
  /** Set the time of the interpolated point.
   * <p>This method should <strong>not</strong> be called before the
   * integration is over because some internal variables are set only
   * once the last step has been handled.</p>
   * <p>Setting the time outside of the integration interval is now
   * allowed (it was not allowed up to version 5.9 of Mantissa), but
   * should be used with care since the accuracy of the interpolator
   * will probably be very poor far from this interval. This allowance
   * has been added to simplify implementation of search algorithms
   * near the interval endpoints.</p>
   * @param time time of the interpolated point
   */
  public void setInterpolatedTime(double time) {

    try {
      // initialize the search with the complete steps table
      int iMin = 0;
      StepInterpolator sMin = (StepInterpolator) steps.get(iMin);
      double tMin = 0.5 * (sMin.getPreviousTime() + sMin.getCurrentTime());

      int iMax = steps.size() - 1;
      StepInterpolator sMax = (StepInterpolator) steps.get(iMax);
      double tMax = 0.5 * (sMax.getPreviousTime() + sMax.getCurrentTime());

      // handle points outside of the integration interval
      // or in the first and last step
      if (locatePoint(time, sMin) <= 0) {
        index = iMin;
        sMin.setInterpolatedTime(time);
        return;
      }
      if (locatePoint(time, sMax) >= 0) {
        index = iMax;
        sMax.setInterpolatedTime(time);
        return;
      }

      // reduction of the table slice size
      while (iMax - iMin > 5) {

        // use the last estimated index as the splitting index
        StepInterpolator si = (StepInterpolator) steps.get(index);
        int location = locatePoint(time, si);
        if (location < 0) {
          iMax = index;
          tMax = 0.5 * (si.getPreviousTime() + si.getCurrentTime());
        } else if (location > 0) {
          iMin = index;
          tMin = 0.5 * (si.getPreviousTime() + si.getCurrentTime());
        } else {
          // we have found the target step, no need to continue searching
          si.setInterpolatedTime(time);
          return;
        }

        // compute a new estimate of the index in the reduced table slice
        int iMed = (iMin + iMax) / 2;
        StepInterpolator sMed = (StepInterpolator) steps.get(iMed);
        double tMed = 0.5 * (sMed.getPreviousTime() + sMed.getCurrentTime());

        if ((Math.abs(tMed - tMin) < 1e-6) || (Math.abs(tMax - tMed) < 1e-6)) {
          // too close to the bounds, we estimate using a simple dichotomy
          index = iMed;
        } else {
          // estimate the index using a reverse quadratic polynom
          // (reverse means we have i = P(t), thus allowing to simply
          // compute index = P(time) rather than solving a quadratic equation)
          double d12 = tMax - tMed;
          double d23 = tMed - tMin;
          double d13 = tMax - tMin;
          double dt1 = time - tMax;
          double dt2 = time - tMed;
          double dt3 = time - tMin;
          double iLagrange = (  (dt2 * dt3 * d23) * iMax
                              - (dt1 * dt3 * d13) * iMed
                              + (dt1 * dt2 * d12) * iMin)
                           / (d12 * d23 * d13);
          index = (int) Math.rint(iLagrange);
        }

        // force the next size reduction to be at least one tenth
        int low  = Math.max(iMin + 1, (9 * iMin + iMax) / 10);
        int high = Math.min(iMax - 1, (iMin + 9 * iMax) / 10);
        if (index < low) {
          index = low;
        } else if (index > high) {
          index = high;
        }

      }

      // now the table slice is very small, we perform an iterative search
      index = iMin;
      while ((index <= iMax)
             && (locatePoint(time, (StepInterpolator) steps.get(index)) > 0)) {
        ++index;
      }

      StepInterpolator si = (StepInterpolator) steps.get(index);

      si.setInterpolatedTime(time);

    } catch (DerivativeException de) {
      throw new RuntimeException("unexpected DerivativeException caught: "
                                 + de.getMessage());
    }

  }

  /**
   * Get the state vector of the interpolated point.
   * @return state vector at time {@link #getInterpolatedTime}
   */
  public double[] getInterpolatedState() {
    return ((StepInterpolator) steps.get(index)).getInterpolatedState();
  }

  /** Compare a step interval and a double. 
   * @param time point to locate
   * @param interval step interval
   * @return -1 if the double is before the interval, 0 if it is in
   * the interval, and +1 if it is after the interval, according to
   * the interval direction
   */
  private int locatePoint(double time, StepInterpolator interval) {
    if (forward) {
      if (time < interval.getPreviousTime()) {
        return -1;
      } else if (time > interval.getCurrentTime()) {
        return +1;
      } else {
        return 0;
      }
    }
    if (time > interval.getPreviousTime()) {
      return -1;
    } else if (time < interval.getCurrentTime()) {
      return +1;
    } else {
      return 0;
    }
  }

  /** Initial integration time. */
  private double initialTime;

  /** Final integration time. */
  private double finalTime;

  /** Integration direction indicator. */
  private boolean forward;

  /** Current interpolator index. */
  private int index;

  /** Steps table. */
  private ArrayList steps;

  private static final long serialVersionUID = 2259286184268533249L;

}
