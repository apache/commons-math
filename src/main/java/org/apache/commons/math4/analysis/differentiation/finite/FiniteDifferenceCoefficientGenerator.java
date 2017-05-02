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

import java.util.Arrays;

import org.apache.commons.math4.exception.NullArgumentException;
import org.apache.commons.math4.fraction.BigFraction;
import org.apache.commons.math4.fraction.BigFractionField;
import org.apache.commons.math4.linear.ArrayFieldVector;
import org.apache.commons.math4.linear.BlockFieldMatrix;
import org.apache.commons.math4.linear.FieldLUDecomposition;
import org.apache.commons.math4.linear.FieldMatrix;
import org.apache.commons.math4.linear.FieldVector;
import org.apache.commons.math4.util.CombinatoricsUtils;

/**
 * This class generates finite difference coefficients by generating the system
 * of linear equations (based on repeated Taylor expansion) that provide finite
 * difference of the desired derivative and error orders.
 * <p>
 * Finite difference coefficients are always rational numbers, and this class
 * solves the aforementioned system <i>exactly</i> using arbitrary-precision
 * rational numbers. Even though this is (vastly) more expensive than using
 * floating point arithmetic, it has several advantages. First, there is no
 * round-off error in the resulting coefficients - a central assumption of just
 * about every finite difference bandwidth selection method. Second, we are able
 * to easily tell if a coefficient is exactly zero, allowing us to short-circuit
 * certain function evaluations.
 * </p>
 * <p>
 * In general, this class is not intended for use by end-users directly.
 * Instead, you should obtain finite difference coefficients
 * {@linkplain FiniteDifference#getCoefficients() directly from the descriptor}.
 * </p>
 * 
 * @see <a href="http://www.geometrictools.com/Documentation/FiniteDifferences.pdf">Derivative Approximation by Finite Differences</a>
 * @since 4.0
 */
public class FiniteDifferenceCoefficientGenerator {

    /**
     * The finite difference.
     */
    private final FiniteDifference finiteDifference;

    /**
     * Constructor.
     * 
     * @param finiteDifference The finite difference for which to generate
     *            coefficients.
     * @throws NullArgumentException If <cod>finiteDifference</code> is
     *             <code>null</code>.
     */
    public FiniteDifferenceCoefficientGenerator(final FiniteDifference finiteDifference)
	throws NullArgumentException {
	if(finiteDifference == null) {
	    throw new NullArgumentException();
	}
	
	this.finiteDifference = finiteDifference;
    }

    /**
     * Get the coefficients.
     * 
     * @return The coefficients.
     */
    public FieldVector<BigFraction> getCoefficients() {
	FieldMatrix<BigFraction> a = getCoefficientMatrix();
	FieldVector<BigFraction> b = getConstantVector();

	// solve the system.
	FieldLUDecomposition<BigFraction> luDecomposition = new FieldLUDecomposition<BigFraction>(
		a);
	FieldVector<BigFraction> x = luDecomposition.getSolver().solve(b);

	// multiply by d!
	long factorial = CombinatoricsUtils.factorial(finiteDifference.getDerivativeOrder());
	x.mapMultiplyToSelf(new BigFraction(factorial));

	return x;
    }

    /**
     * Generates the coefficient matrix.
     * 
     * @return The coefficient matrix.
     */
    private FieldMatrix<BigFraction> getCoefficientMatrix() {
	int size = finiteDifference.getLength();

	FieldMatrix<BigFraction> matrix = new BlockFieldMatrix<BigFraction>(
		BigFractionField.getInstance(), size, size);
	
	BigFraction[] one = new BigFraction[size];
	Arrays.fill(one, BigFraction.ONE);
	matrix.setRow(0, one);
	
	for (int row = 1; row < size; row += 1) {
	    for (int col = 0, multiplier = finiteDifference.getLeftMultiplier(); col < size; col += 1, multiplier += 1) {
		BigFraction value = matrix.getEntry(row - 1, col).multiply(multiplier);
		matrix.setEntry(row, col, value);
	    }
	}

	return matrix;
    }

    /**
     * Get the constant vector.
     * 
     * @return The constant vector.
     */
    private FieldVector<BigFraction> getConstantVector() {
	int size = finiteDifference.getLength();

	FieldVector<BigFraction> b = new ArrayFieldVector<BigFraction>(
		BigFractionField.getInstance(), size);
	b.set(BigFraction.ZERO);

	// the only entry that should be one is of course the derivative
	// index.
	b.setEntry(finiteDifference.getDerivativeOrder(), BigFraction.ONE);

	return b;
    }

}
 