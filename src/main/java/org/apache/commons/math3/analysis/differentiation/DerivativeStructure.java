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
package org.apache.commons.math3.analysis.differentiation;

import java.io.Serializable;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.util.FastMath;

/** Class representing both the value and the differentials of a function.
 * <p>This class is the workhorse of the differentiation package.</p>
 * <p>This class is an implementation of the extension to Rall's
 * numbers described in Dan Kalman's paper <a
 * href="http://www.math.american.edu/People/kalman/pdffiles/mmgautodiff.pdf">Doubly
 * Recursive Multivariate Automatic Differentiation</a>, Mathematics Magazine, vol. 75,
 * no. 3, June 2002.</p>. Rall's numbers are an extension to the real numbers used
 * throughout mathematical expressions; they hold the derivative together with the
 * value of a function. Dan Kalman's derivative structures hold all partial derivatives
 * up to any specified order, with respect to any number of free parameters. Rall's
 * numbers therefore can be seen as derivative structures for order one derivative and
 * one free parameter, and real numbers can be seen as derivative structures with zero
 * order derivative and no free parameters.</p>
 * <p>{@link DerivativeStructure} instances can be used directly thanks to
 * the arithmetic operators to the mathematical functions provided as static
 * methods by this class (+, -, *, /, %, sin, cos ...).</p>
 * <p>Implementing complex expressions by hand using these classes is
 * a tedious and error-prone task but has the advantage of having no limitation
 * on the derivation order despite no requiring users to compute the derivatives by
 * themselves. Implementing complex expression can also be done by developing computation
 * code using standard primitive double values and to use {@link
 * UnivariateFunctionDifferentiator differentiators} to create the {@link
 * DerivativeStructure}-based instances. This method is simpler but may be limited in
 * the accuracy and derivation orders and may be computationally intensive (this is
 * typically the case for {@link FiniteDifferencesDifferentiator finite differences
 * differentiator).</p>
 * <p>Instances of this class are guaranteed to be immutable.</p>
 * @see DSCompiler
 * @version $Id$
 * @since 3.1
 */
public class DerivativeStructure implements FieldElement<DerivativeStructure>, Serializable {

    /** Serializable UID. */
    private static final long serialVersionUID = 20120730L;

    /** Compiler for the current dimensions. */
    private transient DSCompiler compiler;

    /** Combined array holding all values. */
    private final double[] data;

    /** Build an instance with all values and derivatives set to 0.
     * @param compiler compiler to use for computation
     */
    private DerivativeStructure(final DSCompiler compiler) {
        this.compiler = compiler;
        this.data     = new double[compiler.getSize()];
    }

    /** Build an instance with all values and derivatives set to 0.
     * @param parameters number of free parameters
     * @param order derivation order
     */
    public DerivativeStructure(final int parameters, final int order) {
        this(DSCompiler.getCompiler(parameters, order));
    }

    /** Build an instance representing a constant value.
     * @param parameters number of free parameters
     * @param order derivation order
     * @param value value of the constant
     * @see #DerivativeStructure(int, int, int, double)
     */
    public DerivativeStructure(final int parameters, final int order, final double value) {
        this(parameters, order);
        this.data[0] = value;
    }

    /** Build an instance representing a variable.
     * <p>Instances built using this constructor are considered
     * to be the free variables with respect to which differentials
     * are computed. As such, their differential with respect to
     * themselves is +1.</p>
     * @param parameters number of free parameters
     * @param order derivation order
     * @param index index of the variable (from 0 to {@code variables - 1})
     * @param value value of the variable
     * @exception NumberIsTooLargeException if index is equal to variables or larger
     * @see #DerivativeStructure(int, int, double)
     */
    public DerivativeStructure(final int parameters, final int order,
                               final int index, final double value)
        throws NumberIsTooLargeException {
        this(parameters, order, value);

        if (index >= parameters) {
            throw new NumberIsTooLargeException(index, parameters, false);
        }

        if (order > 0) {
            // the derivative of the variable with respect to itself is 1.0
            data[DSCompiler.getCompiler(index, order).getSize()] = 1.0;
        }

    }

    /** Linear combination constructor.
     * The derivative structure built will be a1 * ds1 + a2 * ds2
     * @param a1 first scale factor
     * @param ds1 first base (unscaled) derivative structure
     * @param a2 second scale factor
     * @param ds2 second base (unscaled) derivative structure
     * @exception DimensionMismatchException if number of free parameters or orders are inconsistent
     */
    public DerivativeStructure(final double a1, final DerivativeStructure ds1,
                               final double a2, final DerivativeStructure ds2)
        throws DimensionMismatchException {
        this(ds1.compiler);
        compiler.checkCompatibility(ds2.compiler);
        compiler.linearCombination(a1, ds1.data, 0, a2, ds2.data, 0, data, 0);
    }

    /** Linear combination constructor.
     * The derivative structure built will be a1 * ds1 + a2 * ds2 + a3 * ds3
     * @param a1 first scale factor
     * @param ds1 first base (unscaled) derivative structure
     * @param a2 second scale factor
     * @param ds2 second base (unscaled) derivative structure
     * @param a3 third scale factor
     * @param ds3 third base (unscaled) derivative structure
     * @exception DimensionMismatchException if number of free parameters or orders are inconsistent
     */
    public DerivativeStructure(final double a1, final DerivativeStructure ds1,
                               final double a2, final DerivativeStructure ds2,
                               final double a3, final DerivativeStructure ds3)
        throws DimensionMismatchException {
        this(ds1.compiler);
        compiler.checkCompatibility(ds2.compiler);
        compiler.checkCompatibility(ds3.compiler);
        compiler.linearCombination(a1, ds1.data, 0, a2, ds2.data, 0, a3, ds3.data, 0, data, 0);
    }

    /** Linear combination constructor.
     * The derivative structure built will be a1 * ds1 + a2 * ds2 + a3 * ds3 + a4 * ds4
     * @param a1 first scale factor
     * @param ds1 first base (unscaled) derivative structure
     * @param a2 second scale factor
     * @param ds2 second base (unscaled) derivative structure
     * @param a3 third scale factor
     * @param ds3 third base (unscaled) derivative structure
     * @param a4 fourth scale factor
     * @param ds4 fourth base (unscaled) derivative structure
     * @exception DimensionMismatchException if number of free parameters or orders are inconsistent
     */
    public DerivativeStructure(final double a1, final DerivativeStructure ds1,
                               final double a2, final DerivativeStructure ds2,
                               final double a3, final DerivativeStructure ds3,
                               final double a4, final DerivativeStructure ds4)
        throws DimensionMismatchException {
        this(ds1.compiler);
        compiler.checkCompatibility(ds2.compiler);
        compiler.checkCompatibility(ds3.compiler);
        compiler.checkCompatibility(ds4.compiler);
        compiler.linearCombination(a1, ds1.data, 0, a2, ds2.data, 0,
                                   a3, ds3.data, 0, a4, ds4.data, 0,
                                   data, 0);
    }

    /** Build an instance from all its derivatives.
     * @param parameters number of free parameters
     * @param order derivation order
     * @param derivatives derivatives sorted according to
     * {@link DSCompiler#getPartialDerivativeIndex(int...)}
     * @exception DimensionMismatchException if derivatives array does not match the
     * {@link DSCompiler#getSize() size} expected by the compiler
     * @see #getAllDerivatives()
     */
    public DerivativeStructure(final int parameters, final int order, final double ... derivatives)
        throws DimensionMismatchException {
        this(parameters, order);
        if (derivatives.length != data.length) {
            throw new DimensionMismatchException(derivatives.length, data.length);
        }
        System.arraycopy(derivatives, 0, data, 0, data.length);
    }

    /** Copy constructor.
     * @param ds instance to copy
     */
    private DerivativeStructure(final DerivativeStructure ds) {
        this.compiler = ds.compiler;
        this.data     = ds.data.clone();
    }

    /** Get the number of free parameters.
     * @return number of free parameters
     */
    public int getFreeParameters() {
        return compiler.getFreeParameters();
    }

    /** Get the derivation order.
     * @return derivation order
     */
    public int getOrder() {
        return compiler.getOrder();
    }

    /** Get the value part of the derivative structure.
     * @return value part of the derivative structure
     * @see #getPartialDerivative(int...)
     */
    public double getValue() {
        return data[0];
    }

    /** Get a partial derivative.
     * @param orders derivation orders with respect to each variable (if all orders are 0,
     * the value is returned)
     * @return partial derivative
     * @see #getValue()
     * @exception DimensionMismatchException if the numbers of variables does not
     * match the instance
     * @exception NumberIsTooLargeException if sum of derivation orders is larger
     * than the instance limits
     */
    public double getPartialDerivative(final int ... orders)
        throws DimensionMismatchException, NumberIsTooLargeException {
        return data[compiler.getPartialDerivativeIndex(orders)];
    }

    /** Get all partial derivatives.
     * @return a fresh copy of partial derivatives, in an array sorted according to
     * {@link DSCompiler#getPartialDerivativeIndex(int...)}
     */
    public double[] getAllDerivatives() {
        return data.clone();
    }

    /** '+' operator.
     * @param a right hand side parameter of the operator
     * @return this+a
     */
    public DerivativeStructure add(final double a) {
        final DerivativeStructure ds = new DerivativeStructure(this);
        ds.data[0] += a;
        return ds;
    }

    /** '+' operator.
     * @param a right hand side parameter of the operator
     * @return this+a
     * @exception DimensionMismatchException if number of free parameters or orders are inconsistent
     */
    public DerivativeStructure add(final DerivativeStructure a)
        throws DimensionMismatchException {
        compiler.checkCompatibility(a.compiler);
        final DerivativeStructure ds = new DerivativeStructure(this);
        compiler.add(data, 0, a.data, 0, ds.data, 0);
        return ds;
    }

    /** '-' operator.
     * @param a right hand side parameter of the operator
     * @return this-a
     */
    public DerivativeStructure subtract(final double a) {
        return add(-a);
    }

    /** '-' operator.
     * @param a right hand side parameter of the operator
     * @return this-a
     * @exception DimensionMismatchException if number of free parameters or orders are inconsistent
     */
    public DerivativeStructure subtract(final DerivativeStructure a)
        throws DimensionMismatchException {
        compiler.checkCompatibility(a.compiler);
        final DerivativeStructure ds = new DerivativeStructure(this);
        compiler.subtract(data, 0, a.data, 0, ds.data, 0);
        return ds;
    }

    /** {@inheritDoc} */
    public DerivativeStructure multiply(final int n) {
        return multiply((double) n);
    }

    /** '&times;' operator.
     * @param a right hand side parameter of the operator
     * @return this&times;a
     */
    public DerivativeStructure multiply(final double a) {
        final DerivativeStructure ds = new DerivativeStructure(this);
        for (int i = 0; i < ds.data.length; ++i) {
            ds.data[i] *= a;
        }
        return ds;
    }

    /** '&times;' operator.
     * @param a right hand side parameter of the operator
     * @return this&times;a
     * @exception DimensionMismatchException if number of free parameters or orders are inconsistent
     */
    public DerivativeStructure multiply(final DerivativeStructure a)
        throws DimensionMismatchException {
        compiler.checkCompatibility(a.compiler);
        final DerivativeStructure result = new DerivativeStructure(compiler);
        compiler.multiply(data, 0, a.data, 0, result.data, 0);
        return result;
    }

    /** '&divides;' operator.
     * @param a right hand side parameter of the operator
     * @return this&divides;a
     */
    public DerivativeStructure divide(final double a) {
        final DerivativeStructure ds = new DerivativeStructure(this);
        for (int i = 0; i < ds.data.length; ++i) {
            ds.data[i] /= a;
        }
        return ds;
    }

    /** '&divides;' operator.
     * @param a right hand side parameter of the operator
     * @return this&divides;a
     * @exception DimensionMismatchException if number of free parameters or orders are inconsistent
     */
    public DerivativeStructure divide(final DerivativeStructure a)
        throws DimensionMismatchException {
        compiler.checkCompatibility(a.compiler);
        final DerivativeStructure result = new DerivativeStructure(compiler);
        compiler.divide(data, 0, a.data, 0, result.data, 0);
        return result;
    }

    /** '%' operator.
     * @param a right hand side parameter of the operator
     * @return this%a
     */
    public DerivativeStructure remainder(final double a) {
        final DerivativeStructure ds = new DerivativeStructure(this);
        ds.data[0] = ds.data[0] % a;
        return ds;
    }

    /** '%' operator.
     * @param a right hand side parameter of the operator
     * @return this%a
     * @exception DimensionMismatchException if number of free parameters or orders are inconsistent
     */
    public DerivativeStructure remainder(final DerivativeStructure a)
        throws DimensionMismatchException {
        compiler.checkCompatibility(a.compiler);
        final DerivativeStructure result = new DerivativeStructure(compiler);
        compiler.remainder(data, 0, a.data, 0, result.data, 0);
        return result;
    }

    /** unary '-' operator.
     * @return -this
     */
    public DerivativeStructure negate() {
        final DerivativeStructure ds = new DerivativeStructure(compiler);
        for (int i = 0; i < ds.data.length; ++i) {
            ds.data[i] = -data[i];
        }
        return ds;
    }

    /** absolute value.
     * @return abs(this)
     */
    public DerivativeStructure abs() {
        if (Double.doubleToLongBits(data[0]) < 0) {
            // we use the bits representation to also handle -0.0
            return negate();
        } else {
            return this;
        }
    }

    /** Get the smallest whole number larger than instance.
     * @return ceil(this)
     */
    public DerivativeStructure ceil() {
        return new DerivativeStructure(compiler.getFreeParameters(),
                                       compiler.getOrder(),
                                       FastMath.ceil(data[0]));
    }

    /** Get the largest whole number smaller than instance.
     * @return floor(this)
     */
    public DerivativeStructure floor() {
        return new DerivativeStructure(compiler.getFreeParameters(),
                                       compiler.getOrder(),
                                       FastMath.floor(data[0]));
    }

    /** Get the whole number that is the nearest to the instance, or the even one if x is exactly half way between two integers.
     * @return a double number r such that r is an integer r - 0.5 <= this <= r + 0.5
     */
    public DerivativeStructure rint() {
        return new DerivativeStructure(compiler.getFreeParameters(),
                                       compiler.getOrder(),
                                       FastMath.rint(data[0]));
    }

    /** Get the closest long to instance value.
     * @return closest long to {@link #getValue()}
     */
    public long round() {
        return FastMath.round(data[0]);
    }

    /** Compute the signum of the instance.
     * The signum is -1 for negative numbers, +1 for positive numbers and 0 otherwise
     * @param a number on which evaluation is done
     * @return -1.0, -0.0, +0.0, +1.0 or NaN depending on sign of a
     */
    public DerivativeStructure signum() {
        return new DerivativeStructure(compiler.getFreeParameters(),
                                       compiler.getOrder(),
                                       FastMath.signum(data[0]));
    }

    /**
     * Returns the instance with the sign of the argument.
     * A NaN {@code sign} argument is treated as positive.
     *
     * @param sign the sign for the returned value
     * @return the instance with the same sign as the {@code sign} argument
     */
    public DerivativeStructure copySign(final double sign){
        long m = Double.doubleToLongBits(data[0]);
        long s = Double.doubleToLongBits(sign);
        if ((m >= 0 && s >= 0) || (m < 0 && s < 0)) { // Sign is currently OK
            return this;
        }
        return negate(); // flip sign
    }

    /**
     * Return the exponent of the instance value, removing the bias.
     * <p>
     * For double numbers of the form 2<sup>x</sup>, the unbiased
     * exponent is exactly x.
     * </p>
     * @return exponent for instance in IEEE754 representation, without bias
     */
    public int getExponent() {
        return FastMath.getExponent(data[0]);
    }

    /**
     * Multiply the instance by a power of 2.
     * @param n power of 2
     * @return this &times; 2<sup>n</sup>
     */
    public DerivativeStructure scalb(final int n) {
        final DerivativeStructure ds = new DerivativeStructure(compiler);
        for (int i = 0; i < ds.data.length; ++i) {
            ds.data[i] = FastMath.scalb(data[i], n);
        }
        return ds;
    }

    /**
     * Returns the hypotenuse of a triangle with sides {@code x} and {@code y}
     * - sqrt(<i>x</i><sup>2</sup>&nbsp;+<i>y</i><sup>2</sup>)<br/>
     * avoiding intermediate overflow or underflow.
     *
     * <ul>
     * <li> If either argument is infinite, then the result is positive infinity.</li>
     * <li> else, if either argument is NaN then the result is NaN.</li>
     * </ul>
     *
     * @param x a value
     * @param y a value
     * @return sqrt(<i>x</i><sup>2</sup>&nbsp;+<i>y</i><sup>2</sup>)
     * @exception DimensionMismatchException if number of free parameters or orders are inconsistent
     */
    public static DerivativeStructure hypot(final DerivativeStructure x, final DerivativeStructure y)
        throws DimensionMismatchException {

        x.compiler.checkCompatibility(y.compiler);

        if (Double.isInfinite(x.data[0]) || Double.isInfinite(y.data[0])) {
            return new DerivativeStructure(x.compiler.getFreeParameters(),
                                           x.compiler.getFreeParameters(),
                                           Double.POSITIVE_INFINITY);
        } else if (Double.isNaN(x.data[0]) || Double.isNaN(y.data[0])) {
            return new DerivativeStructure(x.compiler.getFreeParameters(),
                                           x.compiler.getFreeParameters(),
                                           Double.NaN);
        } else {

            final int expX = x.getExponent();
            final int expY = y.getExponent();
            if (expX > expY + 27) {
                // y is neglectible with respect to x
                return x.abs();
            } else if (expY > expX + 27) {
                // x is neglectible with respect to y
                return y.abs();
            } else {

                // find an intermediate scale to avoid both overflow and underflow
                final int middleExp = (expX + expY) / 2;

                // scale parameters without losing precision
                final DerivativeStructure scaledX = x.scalb(-middleExp);
                final DerivativeStructure scaledY = y.scalb(-middleExp);

                // compute scaled hypotenuse
                final DerivativeStructure scaledH =
                        scaledX.multiply(scaledX).add(scaledY.multiply(scaledY)).sqrt();

                // remove scaling
                return scaledH.scalb(middleExp);

            }

        }
    }

    /** Compute composition of the instance by a univariate function.
     * @param f array of value and derivatives of the function at
     * the current point (i.e. [f({@link #getValue()}),
     * f'({@link #getValue()}), f''({@link #getValue()})...]).
     * @return f(this)
     * @exception DimensionMismatchException if the number of derivatives
     * in the array is not equal to {@link #getOrder() order} + 1
     */
    public DerivativeStructure compose(final double[] f) {
        if (f.length != getOrder() + 1) {
            throw new DimensionMismatchException(f.length, getOrder() + 1);
        }
        final DerivativeStructure result = new DerivativeStructure(compiler);
        compiler.compose(data, 0, f, result.data, 0);
        return result;
    }

    /** {@inheritDoc} */
    public DerivativeStructure reciprocal() {
        final DerivativeStructure result = new DerivativeStructure(compiler);
        compiler.pow(data, 0, -1, result.data, 0);
        return result;
    }

    /** Square root.
     * @return square root of the instance
     */
    public DerivativeStructure sqrt() {
        return rootN(2);
    }

    /** Cubic root.
     * @return cubic root of the instance
     */
    public DerivativeStructure cbrt() {
        return rootN(3);
    }

    /** N<sup>th</sup> root.
     * @param n order of the root
     * @return n<sup>th</sup> root of the instance
     */
    public DerivativeStructure rootN(final int n) {
        final DerivativeStructure result = new DerivativeStructure(compiler);
        compiler.rootN(data, 0, n, result.data, 0);
        return result;
    }

    /** {@inheritDoc} */
    public Field<DerivativeStructure> getField() {
        return new Field<DerivativeStructure>() {

            /** {@inheritDoc} */
            public DerivativeStructure getZero() {
                return new DerivativeStructure(compiler.getFreeParameters(), compiler.getOrder(), 0.0);
            }

            /** {@inheritDoc} */
            public DerivativeStructure getOne() {
                return new DerivativeStructure(compiler.getFreeParameters(), compiler.getOrder(), 1.0);
            }

            /** {@inheritDoc} */
            public Class<? extends FieldElement<DerivativeStructure>> getRuntimeClass() {
                return DerivativeStructure.class;
            }

        };
    }

    /** Power operation.
     * @param p power to apply
     * @return this<sup>p</sup>
     */
    public DerivativeStructure pow(final double p) {
        final DerivativeStructure result = new DerivativeStructure(compiler);
        compiler.pow(data, 0, p, result.data, 0);
        return result;
    }

    /** Integer power operation.
     * @param n power to apply
     * @return this<sup>n</sup>
     */
    public DerivativeStructure pow(final int n) {
        final DerivativeStructure result = new DerivativeStructure(compiler);
        compiler.pow(data, 0, n, result.data, 0);
        return result;
    }

    /** Power operation.
     * @param e exponent
     * @return this<sup>e</sup>
     * @exception DimensionMismatchException if number of free parameters or orders are inconsistent
     */
    public DerivativeStructure pow(final DerivativeStructure e)
        throws DimensionMismatchException {
        compiler.checkCompatibility(e.compiler);
        final DerivativeStructure result = new DerivativeStructure(compiler);
        compiler.pow(data, 0, e.data, 0, result.data, 0);
        return result;
    }

    /** Exponential.
     * @return exponential of the instance
     */
    public DerivativeStructure exp() {
        final DerivativeStructure result = new DerivativeStructure(compiler);
        compiler.exp(data, 0, result.data, 0);
        return result;
    }

    /** Exponential minus 1.
     * @return exponential minus one of the instance
     */
    public DerivativeStructure expm1() {
        final DerivativeStructure result = new DerivativeStructure(compiler);
        compiler.expm1(data, 0, result.data, 0);
        return result;
    }

    /** Natural logarithm.
     * @return logarithm of the instance
     */
    public DerivativeStructure log() {
        final DerivativeStructure result = new DerivativeStructure(compiler);
        compiler.log(data, 0, result.data, 0);
        return result;
    }

    /** Shifted natural logarithm.
     * @return logarithm of one plus the instance
     */
    public DerivativeStructure log1p() {
        final DerivativeStructure result = new DerivativeStructure(compiler);
        compiler.log1p(data, 0, result.data, 0);
        return result;
    }

    /** Base 10 logarithm.
     * @return base 10 logarithm of the instance
     */
    public DerivativeStructure log10() {
        final DerivativeStructure result = new DerivativeStructure(compiler);
        compiler.log10(data, 0, result.data, 0);
        return result;
    }

    /** Cosine operation.
     * @return cos(this)
     */
    public DerivativeStructure cos() {
        final DerivativeStructure result = new DerivativeStructure(compiler);
        compiler.cos(data, 0, result.data, 0);
        return result;
    }

    /** Sine operation.
     * @return sin(this)
     */
    public DerivativeStructure sin() {
        final DerivativeStructure result = new DerivativeStructure(compiler);
        compiler.sin(data, 0, result.data, 0);
        return result;
    }

    /** Tangent operation.
     * @return tan(this)
     */
    public DerivativeStructure tan() {
        final DerivativeStructure result = new DerivativeStructure(compiler);
        compiler.tan(data, 0, result.data, 0);
        return result;
    }

    /** Arc cosine operation.
     * @return acos(this)
     */
    public DerivativeStructure acos() {
        final DerivativeStructure result = new DerivativeStructure(compiler);
        compiler.acos(data, 0, result.data, 0);
        return result;
    }

    /** Arc sine operation.
     * @return asin(this)
     */
    public DerivativeStructure asin() {
        final DerivativeStructure result = new DerivativeStructure(compiler);
        compiler.asin(data, 0, result.data, 0);
        return result;
    }

    /** Arc tangent operation.
     * @return atan(this)
     */
    public DerivativeStructure atan() {
        final DerivativeStructure result = new DerivativeStructure(compiler);
        compiler.atan(data, 0, result.data, 0);
        return result;
    }

    /** Two arguments arc tangent operation.
     * @param y first argument of the arc tangent
     * @param x second argument of the arc tangent
     * @return atan2(y, x)
     * @exception DimensionMismatchException if number of free parameters or orders are inconsistent
     */
    public static DerivativeStructure atan2(final DerivativeStructure y, final DerivativeStructure x)
        throws DimensionMismatchException {
        y.compiler.checkCompatibility(x.compiler);
        final DerivativeStructure result = new DerivativeStructure(y.compiler);
        y.compiler.atan2(y.data, 0, x.data, 0, result.data, 0);
        return result;
    }

    /** Hyperbolic cosine operation.
     * @return cosh(this)
     */
    public DerivativeStructure cosh() {
        final DerivativeStructure result = new DerivativeStructure(compiler);
        compiler.cosh(data, 0, result.data, 0);
        return result;
    }

    /** Hyperbolic sine operation.
     * @return sinh(this)
     */
    public DerivativeStructure sinh() {
        final DerivativeStructure result = new DerivativeStructure(compiler);
        compiler.sinh(data, 0, result.data, 0);
        return result;
    }

    /** Hyperbolic tangent operation.
     * @return tanh(this)
     */
    public DerivativeStructure tanh() {
        final DerivativeStructure result = new DerivativeStructure(compiler);
        compiler.tanh(data, 0, result.data, 0);
        return result;
    }

    /** Inverse hyperbolic cosine operation.
     * @return acosh(this)
     */
    public DerivativeStructure acosh() {
        final DerivativeStructure result = new DerivativeStructure(compiler);
        compiler.acosh(data, 0, result.data, 0);
        return result;
    }

    /** Inverse hyperbolic sine operation.
     * @return asin(this)
     */
    public DerivativeStructure asinh() {
        final DerivativeStructure result = new DerivativeStructure(compiler);
        compiler.asinh(data, 0, result.data, 0);
        return result;
    }

    /** Inverse hyperbolic  tangent operation.
     * @return atanh(this)
     */
    public DerivativeStructure atanh() {
        final DerivativeStructure result = new DerivativeStructure(compiler);
        compiler.atanh(data, 0, result.data, 0);
        return result;
    }

    /** Convert radians to degrees, with error of less than 0.5 ULP
     *  @return instance converted into degrees
     */
    public DerivativeStructure toDegrees() {
        final DerivativeStructure ds = new DerivativeStructure(compiler);
        for (int i = 0; i < ds.data.length; ++i) {
            ds.data[i] = FastMath.toDegrees(data[i]);
        }
        return ds;
    }

    /** Convert degrees to radians, with error of less than 0.5 ULP
     *  @return instance converted into radians
     */
    public DerivativeStructure toRadians() {
        final DerivativeStructure ds = new DerivativeStructure(compiler);
        for (int i = 0; i < ds.data.length; ++i) {
            ds.data[i] = FastMath.toRadians(data[i]);
        }
        return ds;
    }

    /** Evaluate Taylor expansion a derivative structure.
     * @param delta parameters offsets (&Delta;x, &Delta;y, ...)
     * @return value of the Taylor expansion at x + &Delta;x, y + &Delta;y, ...
     */
    public double taylor(final double ... delta) {
        return compiler.taylor(data, 0, delta);
    }

    /**
     * Replace the instance with a data transfer object for serialization.
     * @return data transfer object that will be serialized
     */
    private Object writeReplace() {
        return new DataTransferObject(compiler.getFreeParameters(), compiler.getOrder(), data);
    }

    /** Internal class used only for serialization. */
    private static class DataTransferObject implements Serializable {

        /** Serializable UID. */
        private static final long serialVersionUID = 20120730L;

        /** Number of variables.
         * @Serial
         */
        private final int variables;

        /** Derivation order.
         * @Serial
         */
        private final int order;

        /** Partial derivatives.
         * @Serial
         */
        private final double[] data;

        /** Simple constructor.
         * @param variables number of variables
         * @param order derivation order
         * @param data partial derivatives
         */
        public DataTransferObject(final int variables, final int order, final double[] data) {
            this.variables = variables;
            this.order     = order;
            this.data      = data;
        }

        /** Replace the deserialized data transfer object with a {@link DerivativeStructure}.
         * @return replacement {@link DerivativeStructure}
         */
        private Object readResolve() {
            final DerivativeStructure ds = new DerivativeStructure(variables, order);
            System.arraycopy(data, 0, ds.data, 0, ds.data.length);
            return ds;
        }

    }

}
