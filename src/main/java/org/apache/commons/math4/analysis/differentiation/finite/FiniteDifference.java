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

import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.MathIllegalArgumentException;
import org.apache.commons.math4.exception.NotPositiveException;
import org.apache.commons.math4.exception.NullArgumentException;
import org.apache.commons.math4.exception.NumberIsTooLargeException;
import org.apache.commons.math4.exception.NumberIsTooSmallException;
import org.apache.commons.math4.exception.util.LocalizedFormats;
import org.apache.commons.math4.util.FastMath;

/**
 * A finite difference descriptor.
 * 
 * @since 4.0
 */
public final class FiniteDifference implements Cloneable, Serializable {

    // a few "canonical" stencil.

    /**
     * The three-point central finite difference.
     */
    public static final FiniteDifference THREE_POINT_CENTRAL = new FiniteDifference(
	    FiniteDifferenceType.CENTRAL, 1, 2);

    /**
     * The two-point forward finite difference.
     */
    public static final FiniteDifference TWO_POINT_FORWARD = new FiniteDifference(
	    FiniteDifferenceType.FORWARD, 1, 1);

    /**
     * The five-point central finite difference.
     */
    public static final FiniteDifference FIVE_POINT_CENTRAL = new FiniteDifference(
	    FiniteDifferenceType.CENTRAL, 1, 4);

    /**
     * The four-point forward finite difference.
     */
    public static final FiniteDifference FOUR_POINT_FORWARD = new FiniteDifference(
	    FiniteDifferenceType.FORWARD, 1, 3);
    
    /**
     * A semi-degenerate finite difference that simply returns the value of a
     * univariate function.
     * <p>
     * This stencil has both derivative and error order 0.
     */
    public static final FiniteDifference VALUE = new FiniteDifference(
	    FiniteDifferenceType.CENTRAL, 0, 0);

    private static final long serialVersionUID = 715034958256394843L;

    /**
     * The finite difference type.
     */
    private final FiniteDifferenceType finiteDifferenceType;

    /**
     * The derivative order.
     */
    private final int derivativeOrder;

    /**
     * The error order.
     */
    private final int errorOrder;

    /**
     * The left multiplier.
     */
    private final int leftMultiplier;

    /**
     * The right multiplier.
     */
    private final int rightMultiplier;

    /**
     * The stencil length.
     */
    private final int length;

    /**
     * The stencil coefficients.
     */
    private transient volatile double[] coefficients;

    /**
     * Constructor.
     * 
     * @param finiteDifferenceType The stencil type.
     * @param derivativeOrder The derivative order.
     * @param errorOrder The error order.
     * @throws NullArgumentException If the type is <code>null</code>.
     * @throws NumberIsTooSmallException If <code>derivativeOrder</code> is
     *             negative; or if <code>derivativeOrder</code> is non-zero and
     *             <code>errorOrder</code> is not strictly positive.
     * @throws NumberIsTooLargeException If <code>derivativeOrder</code> is zero
     *             and <code>errorOrder</code> is not 0.
     * @throws MathIllegalArgumentException If <code>finiteDifferenceType</code>
     *             is <code>CENTRAL</code> and the error is not an even number.
     */
    public FiniteDifference(final FiniteDifferenceType finiteDifferenceType,
	    final int derivativeOrder, final int errorOrder)
	    throws NullArgumentException, NumberIsTooSmallException,
	    MathIllegalArgumentException {
	if (finiteDifferenceType == null) {
	    throw new NullArgumentException();
	}

	if (derivativeOrder < 0) {
	    throw new NumberIsTooSmallException(derivativeOrder, 0, true);
	}
 
	if ((derivativeOrder != 0) && (errorOrder <= 0)) {
	    throw new NumberIsTooSmallException(derivativeOrder, 0, false);
	}

	if ((derivativeOrder == 0) && (errorOrder != 0)) {
	    throw new NumberIsTooLargeException(errorOrder, 0, true);
	}

	if ((finiteDifferenceType == FiniteDifferenceType.CENTRAL)
		&& ((errorOrder % 2) != 0)) {
	    throw new MathIllegalArgumentException(LocalizedFormats.NUMBER_ODD,
		    errorOrder);
	}

	this.finiteDifferenceType = finiteDifferenceType;
	this.derivativeOrder = derivativeOrder;
	this.errorOrder = errorOrder;

	switch (finiteDifferenceType) {
	case BACKWARD:
	    leftMultiplier = -(derivativeOrder + errorOrder);
	    rightMultiplier = 0;
	    length = derivativeOrder + errorOrder;
	    break;
	case CENTRAL:
	    leftMultiplier = -((derivativeOrder + errorOrder - 1) / 2);
	    rightMultiplier = (derivativeOrder + errorOrder - 1) / 2;
	    length = rightMultiplier - leftMultiplier + 1;
	    break;
	case FORWARD:
	    leftMultiplier = 0;
	    rightMultiplier = derivativeOrder + errorOrder;
	    length = derivativeOrder + errorOrder;
	    break;
	default:
	    throw new IllegalArgumentException("finiteDifferenceType");
	}
    }

    /**
     * Gets the left multiplier.
     * 
     * @return The left multiplier.
     */
    public int getLeftMultiplier() {
	return leftMultiplier;
    }

    /**
     * Gets the right multiplier.
     * 
     * @return The right multiplier.
     */
    public int getRightMultiplier() {
	return rightMultiplier;
    }

    /**
     * Gets the finite difference type.
     * 
     * @return The type.
     */
    public FiniteDifferenceType getFiniteDifferenceType() {
	return finiteDifferenceType;
    }

    /**
     * Gets the derivative order.
     * 
     * @return The derivative order.
     */
    public int getDerivativeOrder() {
	return derivativeOrder;
    }

    /**
     * Gets the error order.
     * 
     * @return The error order.
     */
    public int getErrorOrder() {
	return errorOrder;
    }

    /**
     * Gets the length of the coefficient vector.
     * 
     * @return The length.
     */
    public int getLength() {
	return length;
    }

    /**
     * Gets the coefficients, solving for them and storing in a local reference
     * if needed.
     * <p>
     * This method is package-protected for a reason.
     * 
     * @return The coefficients.
     */
    double[] getCoefficientsRef() {
	if (coefficients == null) {
	    coefficients = FiniteDifferenceCoefficients.getInstance()
		    .getFiniteDifferenceCoefficients(this);
	}

	return coefficients;
    }

    /**
     * Gets the coefficients.
     * <p>
     * The returned array is a clone and can be modified in place.
     * 
     * @return The coefficients.
     */
    public double[] getCoefficients() {
	return getCoefficientsRef().clone();
    }

    /**
     * Gets the &lscr;<sub>1</sub> norm of the coefficients.
     * 
     * @return The &lscr;<sub>1</sub> norm.
     */
    public double getL1NormOfCoefficients() {
	double[] coefficients = getCoefficientsRef();
	double norm = 0;
	for (double x : coefficients) {
	    norm += FastMath.abs(x);
	}

	return norm;
    }

    /**
     * Evaluates the finite difference derivative estimate described by
     * <code>this</code> given the specified grid of function values.
     * 
     * @param grid The grid of function values.
     * @param h The bandwidth.
     * @return The finite difference derivative value.
     * @throws NullArgumentException If <code>grid</code> is <code>null</code>.
     * @throws DimensionMismatchException If <code>grid</code> has an incorrect
     *             length.
     * @throws NotPositiveException If <code>h</code> is not strictly positive.
     */
    public double evaluate(final double[] grid, final double h)
	    throws NullArgumentException, DimensionMismatchException,
	    NotPositiveException {
	if (grid == null) {
	    throw new NullArgumentException();
	}

	double[] coefficients = getCoefficientsRef();
	if (grid.length != coefficients.length) {
	    throw new DimensionMismatchException(grid.length, coefficients.length);
	}

	if (h <= 0) {
	    throw new NotPositiveException(h);
	}

	double derivative = 0;
	for (int index = 0; index < grid.length; index++) {
	    derivative += coefficients[index] * grid[index];
	}

	int derivativeOrder = getDerivativeOrder();
	derivative /= FastMath.pow(h, derivativeOrder);

	return derivative;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
	int hashCode = finiteDifferenceType.hashCode();
	hashCode ^= derivativeOrder;
	hashCode ^= errorOrder;

	return hashCode;
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

	FiniteDifference that = (FiniteDifference) obj;

	return (finiteDifferenceType == that.finiteDifferenceType)
		&& (errorOrder == that.errorOrder)
		&& (derivativeOrder == that.derivativeOrder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append(getClass().getSimpleName());
	builder.append("[finiteDifferenceType=");
	builder.append(finiteDifferenceType);
	builder.append(", derivativeOrder=");
	builder.append(derivativeOrder);
	builder.append(", errorOrder=");
	builder.append(errorOrder);
	builder.append("]");

	return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FiniteDifference clone() {
	try {
	    FiniteDifference clone = (FiniteDifference) super.clone();
	    clone.coefficients = null;

	    return clone;
	} catch (final CloneNotSupportedException e) {
	    throw (InternalError) new InternalError().initCause(e);
	}
    }

}
