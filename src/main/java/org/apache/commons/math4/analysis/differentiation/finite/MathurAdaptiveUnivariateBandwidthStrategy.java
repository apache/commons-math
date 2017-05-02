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
package org.apache.commons.math4.analysis.differentiation.finite;

import org.apache.commons.math4.analysis.UnivariateFunction;
import org.apache.commons.math4.exception.NotPositiveException;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.math4.util.Precision;

/**
 * A rule-of-thumb bandwidth selection strategy.
 * <p>
 * The optimal bandwidth for a finite difference derivative is given by:<br>
 * \[[\frac{d}{n}\frac{1}{|C_{n}|}(\epsilon |F_{\epsilon}| + \delta |F_{\delta}|)]^\frac{1}{n + d}\]<br>
 * where
 * <table border="0">
 *  <tr>
 *   <td>\(d\)</td>
 *   <td>The derivative order.</td>
 *  </tr>
 *  <tr>
 *   <td>\(n\)</td>
 *   <td>The error order.</td>
 *  </tr>
 *  <tr>
 *   <td>\(\epsilon\)</td>
 *   <td>The condition error.</td>
 *  </tr>
 *   <tr>
 *   <td>\(F_\epsilon\)</td>
 *   <td>The condition error coefficient.</td>
 *  </tr>
 *  <tr>
 *   <td>\(\delta\)</td>
 *   <td>The cancellation error.</td>
 *  </tr>
 *  <tr>
 *   <td>\(F_\delta\)</td>
 *   <td>The cancellation error coefficient.</td>
 *  </tr>
 *  <tr>
 *   <td>\(C_{n}\)</td>
 *   <td>The truncation error.</td>
 *  </tr>
 * </table>
 * </p>
 * <p>
 * All of these parameters are either trivially given by the stencil, supplied
 * exogenously by the user, or easy compute; except for the truncation error
 * \(C_{n}\).
 * </p>
 * <p>
 * The truncation takes the following form:<br>
 * \[f^{(d)}(x) = D^{(d)}_{n}\{f\}(x, h) + C(n, h) \cdot h^{n}]<br>
 * where the operator \(D\) represents the finite difference operation.
 * </p>
 * <p>
 * We can estimate \(C(n, h)\) by assuming that, as h becomes "small" it loses
 * its dependence on the bandwidth. We now choose two trial bandwidths that are
 * sufficiently small, and evaluating equation above (removing the dependence on
 * the bandwidth) we have:<br> 
 * \[f^{(d)}(x) = D^{(d)}_{n}\{f\}(x, h_{1}) + C(n) \cdot h_{1}^{n}\]<br>
 * \[f^{(d)}(x) = D^{(d)}_{n}\{f\}(x, h_{2}) + C(n) \cdot h_{2}^{n}\]
 * </p>
 * <p>
 * Assuming (\h_{1} > h_{2}\) we can solve for \(C(n)\) easily:<br>
 * \[C(n) = \frac{D^{(d)}_{n}\{f\}(x, h_{2}) - D^{(d)}_{n}\{f\}(x, h_{1})}{h_{1}^{n} - h_{2}^{n}}\]
 * </p>
 * <p>
 * Note that this is only an <i>estimate</i> of the truncation error. We are
 * also left with the (simpler) problem of choosing the trial bandwidth(s). This
 * class allows the user to either specify the trial bandwidth \(h_{2}\)
 * exogenously; or have it computed automatically using a rule-of-thumb.
 * </p>
 * <p>
 * Note that this strategy is quite expensive compared to the rule-of-thumb or
 * fixed strategies, as it will involve many more function executions. A common
 * pattern of this class is to use it to determine a suitable bandwidth for
 * "normal" parameter ranges offline; and then use said fixed value for all live
 * finite difference derivative calculations.
 * </p>
 * 
 * @since 4.0
 */
public class MathurAdaptiveUnivariateBandwidthStrategy implements UnivariateBandwidthStrategy {

    /**
     * The condition error.
     */
    private final double epsilon;

    /**
     * The trial bandwidth; or <code>null</code> to use the rule-of-thumb.
     */
    private final Double trialBandwidth;

    /**
     * Default constructor.
     * <p>
     * This function assumes that error levels are the
     * {@linkplain Precision#EPSILON machine epsilon}; and will use a
     * rule-of-thumb to generate a trial bandwidth when estimating the curvature
     * scale.
     * </p>
     */
    public MathurAdaptiveUnivariateBandwidthStrategy() {
	this(Precision.EPSILON, null);
    }

    /**
     * Constructor.
     * 
     * @param epsilon The condition error.
     * @throws NotPositiveException If either <code>epsilon</code> is not
     *             strictly positive.
     */
    public MathurAdaptiveUnivariateBandwidthStrategy(final double epsilon)
	    throws NotPositiveException {
	this(epsilon, null);
    }

    /**
     * Full constructor.
     * 
     * @param epsilon The condition error.
     * @param trialBandwidth The trial bandwidth to use when estimating the
     *            scale.
     * @throws NotPositiveException If either <code>epsilon</code> or
     *             <code>trialBandwidth</code> is not strictly positive.
     */
    public MathurAdaptiveUnivariateBandwidthStrategy(final double epsilon,
	    final double trialBandwidth) throws NotPositiveException {
	this(epsilon, new Double(trialBandwidth));
    }

    /**
     * Core constructor.
     * 
     * @param epsilon The condition error.
     * @param trialBandwidth The trial bandwidth.
     * @throws NotPositiveException If either <code>epsilon</code> or
     *             <code>trialBandwidth</code> is <code>null</code>.
     */
    private MathurAdaptiveUnivariateBandwidthStrategy(final double epsilon,
	    final Double trialBandwidth) throws NotPositiveException {

	if (epsilon <= 0) {
	    throw new NotPositiveException(epsilon);
	}

	if ((trialBandwidth != null) && (trialBandwidth <= 0)) {
	    throw new NotPositiveException(trialBandwidth);
	}

	this.epsilon = epsilon;
	this.trialBandwidth = trialBandwidth;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getBandwidth(final UnivariateFunction function,
	    final FiniteDifference finiteDifference, final double x) {

	// first, get the estimated trunction error.
	double trialBandwidth = getTrialBandwidth(function, finiteDifference, x);
	double cn = getEstimatedTruncationError(function, finiteDifference, x, trialBandwidth);
	cn = Math.abs(cn);

	if (cn < Precision.EPSILON) {
	    // estimated truncation error given the trial bandwidth is very
	    // small. In other words, our trial bandwidth is a good bandwidth to
	    // use!
	    return trialBandwidth;
	}

	// this is the roundoff error bound. We assume everything is in doubles,
	// so this value is not settable.
	double delta = Precision.EPSILON;

	// first, compute the condition and roundoff error coefficients.
	double absF = FastMath.abs(function.value(x));
	double l1 = finiteDifference.getL1NormOfCoefficients();

	// use the simplified forms of the error.
	double fe = absF * l1;
	double fd = absF * l1 / 2d;

	// and now it's easy to estimate the optimal bandwidth.
	double d = finiteDifference.getDerivativeOrder();
	double n = finiteDifference.getErrorOrder();

	double arg = (d / n) * (1d / cn) * (epsilon * fe + delta * fd);
	double h = FastMath.pow(arg, 1d / (n + d));

	return h;
    }

    /**
     * Gets a suitable trial bandwidth.
     * <p>
     * Returns the user-specified bandwidth, if it exists. Otherwise, returns a
     * bandwidth based on a rule-of-thumb.
     * </p>
     * 
     * @param function The function.
     * @param finiteDifference The finite difference.
     * @param x The point at which to take a derivative.
     * @return A suitable trial bandwidth.
     */
    private double getTrialBandwidth(final UnivariateFunction function,
	    final FiniteDifference finiteDifference, final double x) {

	if (trialBandwidth != null) {
	    return trialBandwidth;
	}

	double h = getRuleOfThumbBandwidth(function, finiteDifference, x);

	return h;
    }

    /**
     * Gets a rule-of-thumb bandwidth.
     * 
     * @param function The function.
     * @param finiteDifference The stencil descriptor.
     * @param x The point at which to take the derivative.
     * @return A rule-of-thumb bandwidth.
     */
    private double getRuleOfThumbBandwidth(final UnivariateFunction function,
	    final FiniteDifference finiteDifference, final double x) {

	RuleOfThumbUnivariateBandwidthStrategy ruleOfThumb = new RuleOfThumbUnivariateBandwidthStrategy(
		epsilon);
	PowerOfTwoUnivariateBandwidthStrategy powerOfTwo = new PowerOfTwoUnivariateBandwidthStrategy(
		ruleOfThumb);

	double h = powerOfTwo.getBandwidth(function, finiteDifference, x);
	
	// some "experiments" have shown that is better to use a bandwidth that
	// is slightly too large vs. one that is too small *in the case of
	// estimating the truncation error*.
	h *= 2d;

	return h;
    }

    /**
     * Gets the estimated truncation error.
     * 
     * @param function The function.
     * @param finiteDifference The finite difference descriptor.
     * @param x The point at which to estimate the error.
     * @param h2 The trial bandwidth.
     * @return The estimate truncation error.
     */
    private double getEstimatedTruncationError(final UnivariateFunction function,
	    final FiniteDifference finiteDifference, final double x, final double h2) {

	// we need a second bandwidth such that h1 > h2. Ideally, h2 is a
	// power-of-two, so it's natural to simply choose h1 = h2 * 2.
	double h1 = h2 * 2;

	double fd1 = getDerivative(function, finiteDifference, x, h1);
	double fd2 = getDerivative(function, finiteDifference, x, h2);

	double diff = fd2 - fd1;
	double n = finiteDifference.getErrorOrder();
	double cn = diff / (FastMath.pow(h1, n) - FastMath.pow(h2, n));

	return cn;
    }

    /**
     * Computes the specified derivative of the desired function using a fixed
     * bandwidth.
     * 
     * @param function The function.
     * @param finiteDifference The derivative descriptor.
     * @param x The point at which to evaluate.
     * @param h The bandwidth.
     * @return The numerical derivative of the aforementioned nature.
     */
    private double getDerivative(final UnivariateFunction function,
	    final FiniteDifference finiteDifference, final double x, final double h) {

	FixedUnivariateBandwidthStrategy bandwidth = new FixedUnivariateBandwidthStrategy(h);
	UnivariateFiniteDifferenceDerivativeFunction derivativeFunction = new UnivariateFiniteDifferenceDerivativeFunction(
		function, finiteDifference, bandwidth);
	double derivative = derivativeFunction.value(x);

	return derivative;
    }

}
