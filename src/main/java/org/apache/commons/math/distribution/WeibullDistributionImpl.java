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

package org.apache.commons.math.distribution;

import java.io.Serializable;

import org.apache.commons.math.MathRuntimeException;

/**
 * Default implementation of
 * {@link org.apache.commons.math.distribution.WeibullDistribution}.
 *
 * @since 1.1
 * @version $Revision$ $Date$
 */
public class WeibullDistributionImpl extends AbstractContinuousDistribution
        implements WeibullDistribution, Serializable {

    /** Serializable version identifier */
    private static final long serialVersionUID = 8589540077390120676L;

    /** The shape parameter. */
    private double shape;

    /** The scale parameter. */
    private double scale;

    /**
     * Creates weibull distribution with the given shape and scale and a
     * location equal to zero.
     * @param alpha the shape parameter.
     * @param beta the scale parameter.
     */
    public WeibullDistributionImpl(double alpha, double beta){
        super();
        setShape(alpha);
        setScale(beta);
    }

    /**
     * For this distribution, X, this method returns P(X &lt; <code>x</code>).
     * @param x the value at which the CDF is evaluated.
     * @return CDF evaluted at <code>x</code>.
     */
    public double cumulativeProbability(double x) {
        double ret;
        if (x <= 0.0) {
            ret = 0.0;
        } else {
            ret = 1.0 - Math.exp(-Math.pow(x / getScale(), getShape()));
        }
        return ret;
    }

    /**
     * Access the shape parameter.
     * @return the shape parameter.
     */
    public double getShape() {
        return shape;
    }

    /**
     * Access the scale parameter.
     * @return the scale parameter.
     */
    public double getScale() {
        return scale;
    }

    /**
     * For this distribution, X, this method returns the critical point x, such
     * that P(X &lt; x) = <code>p</code>.
     * <p>
     * Returns <code>Double.NEGATIVE_INFINITY</code> for p=0 and
     * <code>Double.POSITIVE_INFINITY</code> for p=1.</p>
     *
     * @param p the desired probability
     * @return x, such that P(X &lt; x) = <code>p</code>
     * @throws IllegalArgumentException if <code>p</code> is not a valid
     *         probability.
     */
    @Override
    public double inverseCumulativeProbability(double p) {
        double ret;
        if (p < 0.0 || p > 1.0) {
            throw MathRuntimeException.createIllegalArgumentException(
                  "{0} out of [{1}, {2}] range", p, 0.0, 1.0);
        } else if (p == 0) {
            ret = 0.0;
        } else  if (p == 1) {
            ret = Double.POSITIVE_INFINITY;
        } else {
            ret = getScale() * Math.pow(-Math.log(1.0 - p), 1.0 / getShape());
        }
        return ret;
    }

    /**
     * Modify the shape parameter.
     * @param alpha the new shape parameter value.
     */
    public void setShape(double alpha) {
        if (alpha <= 0.0) {
            throw MathRuntimeException.createIllegalArgumentException(
                  "shape must be positive ({0})",
                  alpha);
        }
        this.shape = alpha;
    }

    /**
     * Modify the scale parameter.
     * @param beta the new scale parameter value.
     */
    public void setScale(double beta) {
        if (beta <= 0.0) {
            throw MathRuntimeException.createIllegalArgumentException(
                  "scale must be positive ({0})",
                  beta);
        }
        this.scale = beta;
    }

    /**
     * Access the domain value lower bound, based on <code>p</code>, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p the desired probability for the critical value
     * @return domain value lower bound, i.e.
     *         P(X &lt; <i>lower bound</i>) &lt; <code>p</code>
     */
    @Override
    protected double getDomainLowerBound(double p) {
        return 0.0;
    }

    /**
     * Access the domain value upper bound, based on <code>p</code>, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p the desired probability for the critical value
     * @return domain value upper bound, i.e.
     *         P(X &lt; <i>upper bound</i>) &gt; <code>p</code>
     */
    @Override
    protected double getDomainUpperBound(double p) {
        return Double.MAX_VALUE;
    }

    /**
     * Access the initial domain value, based on <code>p</code>, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p the desired probability for the critical value
     * @return initial domain value
     */
    @Override
    protected double getInitialDomain(double p) {
        // use median
        return Math.pow(getScale() * Math.log(2.0), 1.0 / getShape());
    }
}
