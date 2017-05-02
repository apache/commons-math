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

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.NullArgumentException;
import org.apache.commons.math4.util.FastMath;

/**
 * A multivariate finite difference descriptor.
 * <p>
 * This is simply a composition of a univariate finite difference for each
 * dimension.
 */
public final class MultivariateFiniteDifference implements Cloneable, Serializable {
    
    private static final long serialVersionUID = 4835581111025007679L;

    /**
     * The univariate finite difference descriptors.
     */
    private final FiniteDifference[] finiteDifferences;
    
    /**
     * The coefficient tensor, in row-major order.
     */
    private transient volatile double[] coefficients;

    /**
     * Constructor.
     * 
     * @param finiteDifferences The univariate finite difference descriptors.
     */
    public MultivariateFiniteDifference(final FiniteDifference... finiteDifferences) {
	this.finiteDifferences = finiteDifferences.clone();
    }
    
    /**
     * Gets the "underlying" univariate finite differences.
     * 
     * @return The univariate finite difference.
     */
    public FiniteDifference[] getUnivariateFiniteDifferences() {
	return finiteDifferences.clone();
    }

    /**
     * Gets a reference to the univariate finite differences.
     * 
     * @return The univariate finite differences.
     */
    FiniteDifference[] getUnivariateFiniteDifferencesRef() {
	return finiteDifferences;
    }
    
    /**
     * Gets the lengths of the underlying univariate finite differences.
     * 
     * @return The lengths.
     */
    public int[] getFiniteDifferenceLengths() {
	int[] lengths = new int[finiteDifferences.length];
	for(int index = 0; index < lengths.length; index++) {
	    lengths[index] = finiteDifferences[index].getLength();
	}
	
	return lengths;
    }
    
    /**
     * Gets the index<sup>th</sup> underlying univariate finite difference.
     * 
     * @param index The index.
     * @return The desired univariate finite difference.
     */
    public FiniteDifference getUnivariateFiniteDifference(final int index) {
	return getUnivariateFiniteDifferencesRef()[index];
    }

    /**
     * Gets the coefficient tensor, in row-major order.
     * <p>
     * The returned vector is a clone, and so can be modified in place without
     * affecting the state of this object.
     * </p>
     * 
     * @return The coefficients.
     */
    public double[] getCoefficients() {
	return getCoefficientsRef().clone();
    }
    
    /**
     * Gets a reference to the coefficients, creating them if necessary.
     * 
     * @return The coefficient reference.
     */
    double[] getCoefficientsRef() {
	if(coefficients == null) {
	    FiniteDifferenceCoefficients cache = FiniteDifferenceCoefficients.getInstance();
	    coefficients = cache.getFiniteDifferenceCoefficients(this);
	}
	
	return coefficients;
    }
    
    /**
     * Evaluate the specified derivative.
     * 
     * @param valueTensor The function value tenor, in row-major order.
     * @param h The bandwidth array.
     * @return The numerical derivative.
     * @throws NullArgumentException If any arguments are <code>null</code>.
     * @throws DimensionMismatchException If <code>valueTensor</code> or
     *             <code>h</code> have incorrect lengths.
     */
    public double evaluate(final double[] valueTensor, final double[] h) 
    	throws NullArgumentException, DimensionMismatchException {
	
	double[] coefficients = getCoefficientsRef();
	
	// apply tensor-major derivative.
	double derivative = 0;
	for(int index = 0; index < valueTensor.length; index++) {
	    derivative += valueTensor[index] * coefficients[index];
	}
	
	for(int index = 0; index < h.length; index++) {
	    derivative /= FastMath.pow(h[index], finiteDifferences[index].getDerivativeOrder());
	}
	
	return derivative;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
	return Arrays.hashCode(finiteDifferences);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	
	if (obj == null) {
	    return false;
	}
	
	if (getClass() != obj.getClass()) {
	    return false;
	}
	
	MultivariateFiniteDifference other = (MultivariateFiniteDifference) obj;
	if (!Arrays.equals(finiteDifferences, other.finiteDifferences)) {
	    return false;
	}
	
	return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MultivariateFiniteDifference clone() {
	try {
	    return (MultivariateFiniteDifference)super.clone();
	} catch(final CloneNotSupportedException cnse) {
	    throw (InternalError)new InternalError().initCause(cnse);
	}
    }

}
