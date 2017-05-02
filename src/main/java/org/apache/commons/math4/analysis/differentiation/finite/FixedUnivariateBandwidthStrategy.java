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
import org.apache.commons.math4.exception.util.LocalizedFormats;

/**
 * Fixed bandwidth strategy for univariate functions.
 */
public class FixedUnivariateBandwidthStrategy implements UnivariateBandwidthStrategy {

    /**
     * The bandwidth to always use.
     */
    private final double bandwidth;

    /**
     * Constructor.
     * 
     * @param bandwidth The bandwidth value.
     * @throws MathIllegalArgumentException If <code>bandwidth</code> is not
     *             strictly positive.
     */
    public FixedUnivariateBandwidthStrategy(final double bandwidth) {
	if (bandwidth <= 0) {
	    throw new MathIllegalArgumentException(LocalizedFormats.BANDWIDTH, bandwidth);
	}

	this.bandwidth = bandwidth;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getBandwidth(final UnivariateFunction function,
	    final FiniteDifference finiteDifference, final double x) {
	return bandwidth;
    }

}
