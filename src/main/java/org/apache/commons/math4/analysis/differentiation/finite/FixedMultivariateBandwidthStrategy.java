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

import org.apache.commons.math4.analysis.MultivariateFunction;
import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.NotPositiveException;
import org.apache.commons.math4.exception.NullArgumentException;

/**
 * Simple fixed bandwidth strategy for multivariate finite differences.
 * 
 * @since 4.0
 */
public class FixedMultivariateBandwidthStrategy implements
	MultivariateBandwidthStrategy {

    /**
     * The fixed bandwidth vector.
     */
    private final double[] vector;

    /**
     * Constructor.
     * 
     * @param vector The bandwidth vector.
     * @throws NullArgumentException If <code>vector</code> is <code>null</code>.
     * @throws NotPositiveException If any elements of <code>vector</code> are
     *             not positive.
     */
    public FixedMultivariateBandwidthStrategy(final double... vector)
	    throws NullArgumentException, NotPositiveException {

	if (vector == null) {
	    throw new NullArgumentException();
	}

	// ensure it can't be changed in place...
	this.vector = vector.clone();

	for (double x : this.vector) {
	    if (x < 0) {
		throw new NotPositiveException(x);
	    }
	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[] getBandwidthVector(final MultivariateFunction function,
	    final MultivariateFiniteDifference finiteDifference, final double[] x)
	    throws NullArgumentException, DimensionMismatchException {

	if (x.length != vector.length) {
	    throw new DimensionMismatchException(x.length, vector.length);
	}

	return vector;
    }

}
