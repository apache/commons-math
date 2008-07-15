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

package org.apache.commons.math.ode.nonstiff;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.apache.commons.math.fraction.Fraction;
import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.sampling.AbstractStepInterpolator;
import org.apache.commons.math.ode.sampling.StepInterpolator;

/**
 * This class implements an interpolator for Adams-Moulton multiple steps.
 *
 * <p>This interpolator computes dense output inside the last few
 * steps computed. The interpolation equation is consistent with the
 * integration scheme, it is based on a kind of <em>rollback</em> of the
 * integration from step end to interpolation date:
 * <pre>
 *   y(t<sub>n</sub> + theta h) = y (t<sub>n</sub> + h) - &int;<sub>t<sub>n</sub> + theta h</sub><sup>t<sub>n</sub> + h</sup>p(t)dt
 * </pre>
 * where theta belongs to [0 ; 1] and p(t) is the interpolation polynomial based on
 * the derivatives at previous steps f<sub>n-k+1</sub>, f<sub>n-k+2</sub> ...
 * f<sub>n</sub>, f<sub>n</sub> and f<sub>n+1</sub>.</p>
 *
 * @see AdamsMoultonIntegrator
 * @version $Revision$ $Date$
 * @since 2.0
 */

class AdamsMoultonStepInterpolator extends MultistepStepInterpolator {

    /** Serializable version identifier */
    private static final long serialVersionUID = 735568489801241899L;

    /** Neville's interpolation array. */
    private double[] neville;

    /** Integration rollback array. */
    private double[] rollback;

    /** &gamma; star array. */
    private double[] gammaStar;

    /** Backward differences array. */
    private int[][] bdArray;

    /** Original non-truncated step end time. */
    private double nonTruncatedEnd;

    /** Original non-truncated step size. */
    private double nonTruncatedH;

    /** Simple constructor.
     * This constructor builds an instance that is not usable yet, the
     * {@link AbstractStepInterpolator#reinitialize} method should be called
     * before using the instance in order to initialize the internal arrays. This
     * constructor is used only in order to delay the initialization in
     * some cases.
     */
    public AdamsMoultonStepInterpolator() {
    }

    /** Copy constructor.
     * @param interpolator interpolator to copy from. The copy is a deep
     * copy: its arrays are separated from the original arrays of the
     * instance
     */
    public AdamsMoultonStepInterpolator(final AdamsMoultonStepInterpolator interpolator) {
        super(interpolator);
        nonTruncatedEnd = interpolator.nonTruncatedEnd;
        nonTruncatedH   = interpolator.nonTruncatedH;
    }

    /** {@inheritDoc} */
    protected StepInterpolator doCopy() {
        return new AdamsMoultonStepInterpolator(this);
    }

    /** {@inheritDoc} */
    protected void initializeCoefficients() {

        neville  = new double[previousF.length];
        rollback = new double[previousF.length];

        bdArray = AdamsBashforthIntegrator.computeBackwardDifferencesArray(previousF.length);

        Fraction[] fGammaStar = AdamsMoultonIntegrator.computeGammaStarArray(previousF.length);
        gammaStar = new double[fGammaStar.length];
        for (int i = 0; i < fGammaStar.length; ++i) {
            gammaStar[i] = fGammaStar[i].doubleValue();
        }

    }

    /** {@inheritDoc} */
    public void storeTime(final double t) {
        nonTruncatedEnd = t;
        nonTruncatedH   = nonTruncatedEnd - previousTime;
        super.storeTime(t);
    }

    /** Truncate a step.
     * <p>Truncating a step is necessary when an event is triggered
     * before the nominal end of the step.</p>
     * @param truncatedEndTime end time of truncated step
     */
    void truncateStep(final double truncatedEndTime) {
        currentTime = truncatedEndTime;
        h = currentTime - previousTime;
    }

    /** {@inheritDoc} */
    public void setInterpolatedTime(final double time)
        throws DerivativeException {
        interpolatedTime = time;
        final double oneMinusThetaH = nonTruncatedEnd - interpolatedTime;
        final double theta = (nonTruncatedH == 0) ?
                             0 : (nonTruncatedH - oneMinusThetaH) / nonTruncatedH;
        computeInterpolatedState(theta, oneMinusThetaH);
    }

    /** {@inheritDoc} */
    protected void computeInterpolatedState(final double theta, final double oneMinusThetaH) {
        interpolateDerivatives();
        interpolateState(theta);
    }

    /** Interpolate the derivatives.
     * <p>The Adams method is based on a polynomial interpolation of the
     * derivatives based on the preceding steps. So the interpolation of
     * the derivatives here is strictly equivalent: it is a simple polynomial
     * interpolation.</p>
     */
    private void interpolateDerivatives() {

        for (int i = 0; i < interpolatedDerivatives.length; ++i) {

            // initialize the Neville's interpolation algorithm
            for (int k = 0; k < previousF.length; ++k) {
                neville[k] = previousF[k][i];
            }

            // combine the contributions of each points
            for (int l = 1; l < neville.length; ++l) {
                for (int m = neville.length - 1; m >= l; --m) {
                    final double xm   = previousT[m];
                    final double xmMl = previousT[m - l];
                    neville[m] = ((interpolatedTime - xm) * neville[m-1] +
                                  (xmMl - interpolatedTime) * neville[m]) / (xmMl - xm);
                }
            }

            // the interpolation polynomial value is in the array last element
            interpolatedDerivatives[i] = neville[neville.length - 1];

        }

    }

    /** Interpolate the state.
     * <p>The Adams method is based on a polynomial interpolation of the
     * derivatives based on the preceding steps. The polynomial model is
     * integrated analytically throughout the last step. Using the notations
     * found in the second edition of the first volume (Nonstiff Problems)
     * of the reference book by Hairer, Norsett and Wanner: <i>Solving Ordinary
     * Differential Equations</i> (Springer-Verlag, ISBN 3-540-56670-8), this
     * process leads to the following expression:</p>
     * <pre>
     * y<sub>n+1</sub> = y<sub>n</sub> +
     * h &times; &sum;<sub>j=0</sub><sup>j=k</sup> &gamma;<sub>j</sub><sup>*</sup>&nabla;<sup>j</sup>f<sub>n+1</sub>
     * </pre>
     * <p>In the previous expression, the &gamma;<sub>j</sub><sup>*</sup> terms are the
     * ones that result from the analytical integration, and can be computed form
     * the binomial coefficients C<sub>j</sub><sup>-s</sup>:</p>
     * <p>
     * &gamma;<sub>j</sub><sup>*</sup> = (-1)<sup>j</sup>&int;<sub>0</sub><sup>1</sup>C<sub>j</sub><sup>1-s</sup>ds
     * </p>
     * <p>In order to interpolate the state in a manner that is consistent with the
     * integration scheme, we simply subtract from the current state (at the end of the step)
     * the integral computed from interpolation time to step end time.</p>
     * <p>
     * &eta;<sub>j</sub><sup>*</sup>(&theta;)=
     * (-1)<sup>j</sup>&int;<sub>&theta;</sub><sup>1</sup>C<sub>j</sub><sup>1-s</sup>ds
     * </p>
     * The method described in the Hairer, Norsett and Wanner book to compute &gamma;<sub>j</sub><sup>*</sup>
     * is easily extended to compute &gamma;<sub>j</sub><sup>*</sup>(&theta;)=
     * (-1)<sup>j</sup>&int;<sub>0</sub><sup>&theta;</sup>C<sub>j</sub><sup>1-s</sup>ds. From this,
     * we can compute &eta;<sub>j</sub><sup>*</sup>(&theta;) =
     * &gamma;<sub>j</sub><sup>*</sup>-&gamma;<sub>j</sub><sup>*</sup>(&theta;).
     * The first few values are:</p>
     * <table>
     * <tr><td>j</td><td>&gamma;<sub>j</sub><sup>*</sup></td><td>&gamma;<sub>j</sub><sup>*</sup>(&theta;)</td><td>&eta;<sub>j</sub><sup>*</sup>(&theta;)</td></tr>
     * <tr><td>0</td><td>1</td><td>&theta;</td><td>1-&theta;</td></tr>
     * <tr><td>1</td><td>-1/2</td><td>(&theta;<sup>2</sup>-2&theta;)/2</td><td>(-1+2&theta;-&theta;<sup>2</sup>)/2</td></tr>
     * <tr><td>2</td><td>-1/12</td><td>(2&theta;<sup>3</sup>-3&theta;<sup>2</sup>)/12</td><td>(-1+3&theta;<sup>2</sup>-2&theta;<sup>3</sup>)/12</td></tr>
     * </table>
     * <p>
     * The &eta;<sub>j</sub>(&theta;) functions appear to be polynomial ones. As expected,
     * we see that &eta;<sub>j</sub>(1)= 0. The recurrence relation derived for
     * &gamma;<sub>j</sub>(&theta;) is:
     * </p>
     * <p>
     * &sum<sub>j=0</sub><sup>j=m</sup>&gamma;<sub>j</sub><sup>*</sup>(&theta;)/(m+1-j) =
     * 1/(m+1)! &prod;<sub>k=0</sub><sup>k=m</sup>(&theta;+k-1)
     * </p>
     * @param theta location of the interpolation point within the last step
     */
    private void interpolateState(final double theta) {

        // compute the integrals to remove from the final state
        computeRollback(previousT.length - 1, theta);

        // remove these integrals from the final state
        for (int j = 0; j < interpolatedState.length; ++j) {
            double sum = 0;
            for (int l = 0; l < previousT.length; ++l) {
                sum += rollback[l] * previousF[l][j];
            }
            interpolatedState[j] = currentState[j] - h * sum;
        }

    }

    /** Compute the rollback coefficients.
     * @param order order of the integration method
     * @param theta current value for theta
     */
    private void computeRollback(final int order, final double theta) {

        // compute the gamma star(theta) values from the recurrence relation
        double product = theta - 1;
        rollback[0]  = theta;
        for (int i = 1; i <= order; ++i) {
            product *= (i - 1 + theta) / (i + 1);
            double gStar = product;
            for (int j = 1; j <= i; ++j) {
                gStar -= rollback[i - j] / (j + 1);
            }
            rollback[i] = gStar;
        }

        // subtract it from gamma star to get eta star(theta)
        for (int i = 0; i <= order; ++i) {
            rollback[i] -= gammaStar[i];
        }

        // combine the eta star integrals with the backward differences array
        // to get the rollback coefficients
        for (int i = 0; i <= order; ++i) {
            double f = 0;
            for (int j = i; j <= order; ++j) {
                f -= rollback[j] * bdArray[j][i];
            }
            rollback[i] = f;
        }

    }

    /** {@inheritDoc} */
    public void writeExternal(final ObjectOutput out)
        throws IOException {
        super.writeExternal(out);
        out.writeDouble(nonTruncatedEnd);
    }

    /** {@inheritDoc} */
    public void readExternal(final ObjectInput in)
        throws IOException {
        nonTruncatedEnd = in.readDouble();
    }

}
