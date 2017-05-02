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

import org.apache.commons.math4.exception.NullArgumentException;
import org.apache.commons.math4.fraction.BigFraction;
import org.apache.commons.math4.fraction.BigFractionField;
import org.apache.commons.math4.linear.ArrayFieldVector;
import org.apache.commons.math4.linear.FieldVector;
import org.apache.commons.math4.util.Pair;

/**
 * Generates multivariate finite difference coefficients.
 * 
 * @since 4.0
 */
public class MultivariateFiniteDifferenceCoefficientGenerator {

    /**
     * The descriptor.
     */
    private final MultivariateFiniteDifference multivariateFiniteDifference;

    /**
     * Constructor.
     * 
     * @param multivariateFiniteDifference The multivariate finite difference
     *            descriptor.
     * @throws NullArgumentException If
     *             <code>multivariateFiniteDifference</code> is
     *             <code>null</code>.
     */
    public MultivariateFiniteDifferenceCoefficientGenerator(
	    final MultivariateFiniteDifference multivariateFiniteDifference)
	    throws NullArgumentException {
	if (multivariateFiniteDifference == null) {
	    throw new NullArgumentException();
	}

	this.multivariateFiniteDifference = multivariateFiniteDifference;
    }

    /**
     * Get the coefficient tensor in row-major order.
     * 
     * @return The coefficient tensor.
     */
    public FieldVector<BigFraction> getCoefficients() {

	// first, compute the size.
	int size = 1;
	for(int length : multivariateFiniteDifference.getFiniteDifferenceLengths()) {
	    size *= length;
	}
	
	// the vector we'll be filling in.
	FieldVector<BigFraction> rowMajor = new ArrayFieldVector<>(BigFractionField.getInstance(), size);

	// we'll of course be needing the univariate coefficents.
	FiniteDifferenceCoefficients univariate = FiniteDifferenceCoefficients.getInstance();

	// use the multi-index helper.
	RowMajorIteration iteration = new RowMajorIteration(
		multivariateFiniteDifference.getFiniteDifferenceLengths());
	for (Pair<int[], Integer> multiIndex : iteration) {

	    // construct multivariate stencil as tensor product.
	    int[] tensorIndex = multiIndex.getFirst();
	    BigFraction coefficent = BigFraction.ONE;
	    for (int index = 0; index < tensorIndex.length; index++) {

		// get coefficients for the specific univariate stencil in
		// arbitrary precision.
		FiniteDifference univariateFiniteDifference = multivariateFiniteDifference
			.getUnivariateFiniteDifference(index);
		FieldVector<BigFraction> bigUnivariateCoefficients = univariate
			.getBigCoefficients(univariateFiniteDifference);

		// and now it's easy to update the multivariate stencil.
		int univariateIndex = tensorIndex[index];
		BigFraction univariateCoefficient = bigUnivariateCoefficients
			.getEntry(univariateIndex);
		coefficent = coefficent.multiply(univariateCoefficient);
	    }

	    // our multi-index iterator also supplies the row-major index at
	    // which to store the result.
	    rowMajor.setEntry(multiIndex.getSecond(), coefficent);
	}

	return rowMajor;
    }

}
