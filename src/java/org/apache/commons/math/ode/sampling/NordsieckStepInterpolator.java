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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealMatrixImpl;
import org.apache.commons.math.linear.RealMatrixPreservingVisitor;
import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.nonstiff.AdamsIntegrator;

/**
 * This class implements an interpolator for integrators using Nordsieck representation.
 *
 * <p>This interpolator computes dense output around the current point.
 * The interpolation equation is based on Taylor series formulas.
 *
 * @see AdamsIntegrator
 * @version $Revision$ $Date$
 * @since 2.0
 */

public class NordsieckStepInterpolator extends AbstractStepInterpolator {

    /** Serializable version identifier */
    private static final long serialVersionUID = -7179861704951334960L;

    /** Step size used in the first scaled derivative and Nordsieck vector. */
    private double scalingH;

    /** First scaled derivative. */
    private double[] scaled;

    /** Nordsieck vector. */
    private RealMatrix nordsieck;

    /** Simple constructor.
     * This constructor builds an instance that is not usable yet, the
     * {@link AbstractStepInterpolator#reinitialize} method should be called
     * before using the instance in order to initialize the internal arrays. This
     * constructor is used only in order to delay the initialization in
     * some cases.
     */
    public NordsieckStepInterpolator() {
    }

    /** Copy constructor.
     * @param interpolator interpolator to copy from. The copy is a deep
     * copy: its arrays are separated from the original arrays of the
     * instance
     */
    public NordsieckStepInterpolator(final NordsieckStepInterpolator interpolator) {
        super(interpolator);
        scalingH = interpolator.scalingH;
        if (interpolator.scaled != null) {
            scaled = interpolator.scaled.clone();
        }
        if (interpolator.nordsieck != null) {
            nordsieck = interpolator.nordsieck.copy();
        }
    }

    /** {@inheritDoc} */
    @Override
    protected StepInterpolator doCopy() {
        return new NordsieckStepInterpolator(this);
    }

    /** Reinitialize the instance
     * <p>Beware that all arrays <em>must</em> be references to integrator
     * arrays, in order to ensure proper update without copy.</p>
     * @param y reference to the integrator array holding the state at
     * the end of the step
     * @param forward integration direction indicator
     */
    @Override
    public void reinitialize(final double[] y, final boolean forward) {
        super.reinitialize(y, forward);
    }

    /** Reinitialize the instance
     * <p>Beware that all arrays <em>must</em> be references to integrator
     * arrays, in order to ensure proper update without copy.</p>
     * @param scalingH step size used in the scaled and nordsieck arrays
     * @param scaled reference to the integrator array holding the first
     * scaled derivative
     * @param nordsieck reference to the integrator matrix holding the
     * nordsieck vector
     */
    public void reinitialize(final double scalingH, final double[] scaled,
                             final RealMatrix nordsieck) {
        this.scalingH  = scalingH;
        this.scaled    = scaled;
        this.nordsieck = nordsieck;
    }

    /** Store the current step time.
     * @param t current time
     */
    @Override
    public void storeTime(final double t) {
      currentTime      = t;
      h                = currentTime - previousTime;
      interpolatedTime = t;
      computeInterpolatedState(1.0, 0.0);
    }

    /** {@inheritDoc} */
    @Override
    protected void computeInterpolatedState(final double theta, final double oneMinusThetaH) {
        final double x = theta * h;
        nordsieck.walkInOptimizedOrder(new StateEstimator(x, x / scalingH));
    }

    /** State estimator. */
    private class StateEstimator implements RealMatrixPreservingVisitor {

        /** Scaling factor for derivative. */
        private final double scale;

        /** First order power. */
        private final double lowPower;

        /** High order powers. */
        private final double[] highPowers;

        /** Simple constructor.
         * @param scale scaling factor for derivative
         * @param theta normalized interpolation abscissa within the step
         */
        public StateEstimator(final double scale, final double theta) {
            this.scale  = scale;
            lowPower   = theta;
            highPowers = new double[nordsieck.getRowDimension()];
            double thetaN = theta;
            for (int i = 0; i < highPowers.length; ++i) {
                thetaN *= theta;
                highPowers[i] = thetaN;
            }
        }

        /** {@inheritDoc} */
        public void start(int rows, int columns,
                          int startRow, int endRow, int startColumn, int endColumn) {
            Arrays.fill(interpolatedState, 0.0);
            Arrays.fill(interpolatedDerivatives, 0.0);
        }

        /** {@inheritDoc} */
        public void visit(int row, int column, double value) {
            final double d = value * highPowers[row];
            interpolatedState[column]       += d;
            interpolatedDerivatives[column] += (row + 2) * d;
        }

        /** {@inheritDoc} */
        public double end() {
            for (int j = 0; j < currentState.length; ++j) {
                interpolatedState[j] += currentState[j] + scaled[j] * lowPower;
                interpolatedDerivatives[j] =
                    (interpolatedDerivatives[j] + scaled[j] * lowPower) / scale;
            }
            return 0;
        }

    }

    /** {@inheritDoc} */
    @Override
    public void writeExternal(final ObjectOutput out)
        throws IOException {

        // save the state of the base class
        writeBaseExternal(out);

        // save the local attributes
        final int n = (currentState == null) ? -1 : currentState.length;
        if (scaled == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            for (int j = 0; j < n; ++j) {
                out.writeDouble(scaled[j]);
            }
        }

        if (nordsieck == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            final int rows = nordsieck.getRowDimension();
            out.writeInt(rows);
            for (int i = 0; i < rows; ++i) {
                for (int j = 0; j < n; ++j) {
                    out.writeDouble(nordsieck.getEntry(i, j));
                }
            }
        }

    }

    /** {@inheritDoc} */
    @Override
    public void readExternal(final ObjectInput in)
        throws IOException {

        // read the base class 
        final double t = readBaseExternal(in);

        // read the local attributes
        final int n = (currentState == null) ? -1 : currentState.length;
        final boolean hasScaled = in.readBoolean();
        if (hasScaled) {
            scaled = new double[n];
            for (int j = 0; j < n; ++j) {
                scaled[j] = in.readDouble();
            }
        } else {
            scaled = null;
        }

        final boolean hasNordsieck = in.readBoolean();
        if (hasNordsieck) {
            final int rows = in.readInt();
            final double[][] nData = new double[rows][n];
            for (int i = 0; i < rows; ++i) {
                final double[] nI = nData[i];
                for (int j = 0; j < n; ++j) {
                    nI[j] = in.readDouble();
                }
            }
            nordsieck = new RealMatrixImpl(nData, false);
        } else {
            nordsieck = null;
        }

        try {
            if (hasScaled && hasNordsieck) {
                // we can now set the interpolated time and state
                setInterpolatedTime(t);
            }
        } catch (DerivativeException e) {
            throw MathRuntimeException.createIOException(e);
        }

    }

}
