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
import org.apache.commons.math4.exception.NullArgumentException;

/**
 * Strategy for getting a bandwidth or grid width for computing a finite
 * difference derivative of a multivariate function.
 * 
 * @since 4.0
 */
public interface MultivariateBandwidthStrategy {

    /**
     * Returns the bandwidth vector to use to evaluate the desired numerical
     * derivative.
     * <p>
     * All elements of the returned vector <u>must</u> be positive.
     * </p>
     * 
     * @param function The function to derive numerically.
     * @param finiteDifference The finite difference descriptor.
     * @param x The point at which we wish to evaluate the derivative.
     * @return The bandwidth vector.
     * @throws NullArgumentException If any arguments are <code>null</code>.
     * @throws DimensionMismatchException If specified finite difference or
     *             point have an incorrect dimension.
     */
    public double[] getBandwidthVector(MultivariateFunction function,
	    MultivariateFiniteDifference finiteDifference, double[] x)
	    throws NullArgumentException, DimensionMismatchException;

}
