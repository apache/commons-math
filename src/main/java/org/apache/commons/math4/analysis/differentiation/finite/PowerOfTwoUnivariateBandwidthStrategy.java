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
import org.apache.commons.math4.exception.MathIllegalArgumentException;
import org.apache.commons.math4.exception.NullArgumentException;
import org.apache.commons.math4.exception.util.LocalizedFormats;
import org.apache.commons.math4.util.Precision;

/**
 * Wraps an underlying univariate bandwidth strategy and rounds the bandwidth to
 * the next highest power of two. Power of two bandwidths ensure that
 * <code>x +/- h</code> is representable <i>exactly</i> and can thus help to
 * reduce the roundoff error of the resulting numerical derivative.
 * <p>
 * This class uses the decorator design pattern.
 * </p>
 * 
 * @since 4.0
 */
public class PowerOfTwoUnivariateBandwidthStrategy implements UnivariateBandwidthStrategy {

    /**
     * The underlying strategy to wrap.
     */
    private UnivariateBandwidthStrategy underlyingStrategy;

    /**
     * Constructor.
     * 
     * @param underlyingStrategy The underlying strategy.
     * @throws NullArgumentException If <code>underlyingStrategy</code> is
     *             <code>null</code>.
     */
    public PowerOfTwoUnivariateBandwidthStrategy(
	    final UnivariateBandwidthStrategy underlyingStrategy) throws NullArgumentException {

	if (underlyingStrategy == null) {
	    throw new NullArgumentException();
	}

	this.underlyingStrategy = underlyingStrategy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getBandwidth(final UnivariateFunction function,
	    final FiniteDifference finiteDifference, final double x) {

	double underlyingBandwidth = underlyingStrategy.getBandwidth(function,
		finiteDifference, x);

	if (underlyingBandwidth <= 0) {
	    // bandwidths must be positive.
	    throw new MathIllegalArgumentException(LocalizedFormats.BANDWIDTH, x);
	}

	double powerOfTwo = Precision.roundToPowerOfTwoTowardZero(underlyingBandwidth);

	return powerOfTwo;
    }

}
