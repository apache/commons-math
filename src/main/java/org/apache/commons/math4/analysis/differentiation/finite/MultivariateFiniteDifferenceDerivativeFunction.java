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
import org.apache.commons.math4.util.Pair;

/**
 * A multivariate finite difference derivative function.
 * 
 * @since 4.0
 */
public class MultivariateFiniteDifferenceDerivativeFunction implements MultivariateFunction {

    /**
     * The function to numerically derive.
     */
    private final MultivariateFunction function;

    /**
     * The stencil descriptor.
     */
    private final MultivariateFiniteDifference finiteDifference;
    
    /**
     * The bandwidth.
     */
    private final MultivariateBandwidthStrategy bandwidthStrategy;

    /**
     * Constructor.
     * 
     * @param function The function.
     * @param finiteDifference The finite difference.
     * @param bandwidthStrategy The bandwidth function.
     */
    public MultivariateFiniteDifferenceDerivativeFunction(
	    final MultivariateFunction function,
	    final MultivariateFiniteDifference finiteDifference,
	    final MultivariateBandwidthStrategy bandwidthStrategy) {
	this.function = function;
	this.finiteDifference = finiteDifference;
	this.bandwidthStrategy = bandwidthStrategy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double value(final double[] point) {
	
	// get the bandwidth and value tensor.
	double[] bandwidth = bandwidthStrategy.getBandwidthVector(function, finiteDifference, point);
	double[] valueTensor = getFunctionValueTensor(point, bandwidth);
	
	double derivative = finiteDifference.evaluate(valueTensor, bandwidth);
	
	return derivative;
    }

    /**
     * Get the function value tensor.
     * 
     * @param point The point.
     * @param bandwidth The bandwidth grid.
     * @return The function value tensor.
     */
    private double[] getFunctionValueTensor(final double[] point, final double[] bandwidth) {
	
	double[] tensor = new double[finiteDifference.getCoefficients().length];

	// use the iteration helper.
	RowMajorIteration iteration = new RowMajorIteration(finiteDifference.getFiniteDifferenceLengths());
	
	for(Pair<int[], Integer> index : iteration) {
	    
	    int[] multi = index.getFirst();	    
	   
	    // get the point with bandwidth shifts applied.
	    double[] indexPoint = getPointForIndex(point, bandwidth, multi);
	    double functionValue = function.value(indexPoint);
	    
	    // and store in the proper place in the tensor.
	    int arrayIndex = index.getSecond();	    
	    tensor[arrayIndex] = functionValue;
	}
	
	return tensor;
    }

    /**
     * @param point
     * @param multi
     * @return
     */
    private double[] getPointForIndex(double[] point, double[] h, int[] multi) {
	double[] indexPoint = point.clone();
	
	for(int index = 0; index < indexPoint.length; index++) {
	    FiniteDifference univariate = finiteDifference.getUnivariateFiniteDifference(index);
	    double mh = h[index] * (univariate.getLeftMultiplier() + multi[index]);
	    indexPoint[index] += mh;
	}
	
	return indexPoint;
    }
    
}
