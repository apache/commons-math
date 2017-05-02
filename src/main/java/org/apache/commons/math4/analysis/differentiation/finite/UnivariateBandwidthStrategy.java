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
import org.apache.commons.math4.exception.NullArgumentException;

/**
 * Strategy for getting a bandwidth or grid width for computing a finite
 * difference derivative of a univariate function.
 * 
 * @since 4.0
 */
public interface UnivariateBandwidthStrategy {

    /**
     * Returns the bandwidth to use to evaluate the desired numerical
     * derivative.
     * <p>
     * The returned bandwidth <u>must</u> be positive.
     * </p>
     * 
     * @param function The function to derive numerically.
     * @param finiteDifference The finite difference descriptor.
     * @param x The point at which we wish to evaluate the derivative.
     * @return The bandwidth.
     * @throws NullArgumentException If <code>function</code> or
     *             <code>finiteDifference</code> are <code>null</code>.
     */
    public double getBandwidth(UnivariateFunction function, FiniteDifference finiteDifference,
	    double x) throws NullArgumentException;

}
