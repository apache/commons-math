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
 * Our rule-of-thumb is derived by first using the "simplified" forms of the
 * condition and cancellation error. Specifically, this means that
 * (\F_{\epsilon} = |f_{0}| \sum |c_{i}|\) and (\F_{\delta} = \frac{|f_{0}| \sum |c_{i}|}{2}\)
 * </p>
 * <p>
 * Re-arranging, and absorbing \(|f_{0}|\) into the truncation error term:<br>
 * \[[(\frac{d}{n}) \cdot |x^{*}| \cdot \sum |c_{i}| \cdot (\epsilon + \frac{\delta}{2})]^\frac{1}{n + d}\]
 * </p>
 * <p>
 * Here, \(x^{*}\) essentially measures scale of the curvature of the function
 * near x. The rule-of-thumb then sets \(x^{*}\) equal to x itself (except near
 * zero). Although this may seem either trivial or entirely unrealistic, this
 * choice makes no assumptions about the particular form of the function and
 * works quite well in practice.
 * 
 * @since 4.0
 */
public class RuleOfThumbUnivariateBandwidthStrategy implements UnivariateBandwidthStrategy {

    /**
     * The condition error.
     */
    private final double epsilon;

    /**
     * Default constructor.
     * <p>
     * This sets the condition error to {@linkplain Precision#EPSILON the
     * machine epsilon}.
     */
    public RuleOfThumbUnivariateBandwidthStrategy() {
	this(Precision.EPSILON);
    }

    /**
     * Full constructor.
     * 
     * @param epsilon The function condition error.
     * @throws NotPositiveException If <code>epsilon</code> not positive.
     */
    public RuleOfThumbUnivariateBandwidthStrategy(final double epsilon)
    	throws NotPositiveException {
	if(epsilon <= 0d) {
	    throw new NotPositiveException(epsilon);
	}
	
	this.epsilon = epsilon;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getBandwidth(final UnivariateFunction function,
	    final FiniteDifference finiteDifference, final double x) {

	// this is fine since we're dealing with doubles.
	double delta = Precision.EPSILON;

	// first we need the sum of the absolute values of the coefficients.
	double sum = finiteDifference.getL1NormOfCoefficients();

	// this is really the key assumption of the rule-of-thumb - that the
	// modified curvature scale is proportional to x (except near 0).
	double cn = FastMath.max(1d, Math.abs(x));

	// and now we can compute the optimal bandwidth.
	double n = finiteDifference.getErrorOrder();
	double d = finiteDifference.getDerivativeOrder();
	double arg = (d / n) * cn * ((epsilon * sum) + (delta * sum / 2d));
	double h = FastMath.pow(arg, 1d / (n + d));
	
	return h;
    }

}
