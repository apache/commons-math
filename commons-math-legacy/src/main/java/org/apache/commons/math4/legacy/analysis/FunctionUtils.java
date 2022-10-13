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

package org.apache.commons.math4.legacy.analysis;

import org.apache.commons.numbers.core.Sum;
import org.apache.commons.math4.legacy.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math4.legacy.analysis.differentiation.MultivariateDifferentiableFunction;
import org.apache.commons.math4.legacy.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math4.legacy.analysis.function.Identity;
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.NumberIsTooLargeException;

/**
 * Utilities for manipulating function objects.
 *
 * @since 3.0
 */
public final class FunctionUtils {
    /**
     * Class only contains static methods.
     */
    private FunctionUtils() {}

    /**
     * Composes functions.
     * <p>
     * The functions in the argument list are composed sequentially, in the
     * given order.  For example, compose(f1,f2,f3) acts like f1(f2(f3(x))).</p>
     *
     * @param f List of functions.
     * @return the composite function.
     */
    public static UnivariateFunction compose(final UnivariateFunction ... f) {
        return new UnivariateFunction() {
            /** {@inheritDoc} */
            @Override
            public double value(double x) {
                double r = x;
                for (int i = f.length - 1; i >= 0; i--) {
                    r = f[i].value(r);
                }
                return r;
            }
        };
    }

    /**
     * Composes functions.
     * <p>
     * The functions in the argument list are composed sequentially, in the
     * given order.  For example, compose(f1,f2,f3) acts like f1(f2(f3(x))).</p>
     *
     * @param f List of functions.
     * @return the composite function.
     * @since 3.1
     */
    public static UnivariateDifferentiableFunction compose(final UnivariateDifferentiableFunction ... f) {
        return new UnivariateDifferentiableFunction() {

            /** {@inheritDoc} */
            @Override
            public double value(final double t) {
                double r = t;
                for (int i = f.length - 1; i >= 0; i--) {
                    r = f[i].value(r);
                }
                return r;
            }

            /** {@inheritDoc} */
            @Override
            public DerivativeStructure value(final DerivativeStructure t) {
                DerivativeStructure r = t;
                for (int i = f.length - 1; i >= 0; i--) {
                    r = f[i].value(r);
                }
                return r;
            }
        };
    }

    /**
     * Adds functions.
     *
     * @param f List of functions.
     * @return a function that computes the sum of the functions.
     */
    public static UnivariateFunction add(final UnivariateFunction ... f) {
        return new UnivariateFunction() {
            /** {@inheritDoc} */
            @Override
            public double value(double x) {
                double r = f[0].value(x);
                for (int i = 1; i < f.length; i++) {
                    r += f[i].value(x);
                }
                return r;
            }
        };
    }

    /**
     * Adds functions.
     *
     * @param f List of functions.
     * @return a function that computes the sum of the functions.
     * @since 3.1
     */
    public static UnivariateDifferentiableFunction add(final UnivariateDifferentiableFunction ... f) {
        return new UnivariateDifferentiableFunction() {

            /** {@inheritDoc} */
            @Override
            public double value(final double t) {
                double r = f[0].value(t);
                for (int i = 1; i < f.length; i++) {
                    r += f[i].value(t);
                }
                return r;
            }

            /** {@inheritDoc}
             * @throws DimensionMismatchException if functions are not consistent with each other
             */
            @Override
            public DerivativeStructure value(final DerivativeStructure t)
                throws DimensionMismatchException {
                DerivativeStructure r = f[0].value(t);
                for (int i = 1; i < f.length; i++) {
                    r = r.add(f[i].value(t));
                }
                return r;
            }
        };
    }

    /**
     * Multiplies functions.
     *
     * @param f List of functions.
     * @return a function that computes the product of the functions.
     */
    public static UnivariateFunction multiply(final UnivariateFunction ... f) {
        return new UnivariateFunction() {
            /** {@inheritDoc} */
            @Override
            public double value(double x) {
                double r = f[0].value(x);
                for (int i = 1; i < f.length; i++) {
                    r *= f[i].value(x);
                }
                return r;
            }
        };
    }

    /**
     * Multiplies functions.
     *
     * @param f List of functions.
     * @return a function that computes the product of the functions.
     * @since 3.1
     */
    public static UnivariateDifferentiableFunction multiply(final UnivariateDifferentiableFunction ... f) {
        return new UnivariateDifferentiableFunction() {

            /** {@inheritDoc} */
            @Override
            public double value(final double t) {
                double r = f[0].value(t);
                for (int i = 1; i < f.length; i++) {
                    r  *= f[i].value(t);
                }
                return r;
            }

            /** {@inheritDoc} */
            @Override
            public DerivativeStructure value(final DerivativeStructure t) {
                DerivativeStructure r = f[0].value(t);
                for (int i = 1; i < f.length; i++) {
                    r = r.multiply(f[i].value(t));
                }
                return r;
            }
        };
    }

    /**
     * Returns the univariate function
     * {@code h(x) = combiner(f(x), g(x))}.
     *
     * @param combiner Combiner function.
     * @param f Function.
     * @param g Function.
     * @return the composite function.
     */
    public static UnivariateFunction combine(final BivariateFunction combiner,
                                             final UnivariateFunction f,
                                             final UnivariateFunction g) {
        return new UnivariateFunction() {
            /** {@inheritDoc} */
            @Override
            public double value(double x) {
                return combiner.value(f.value(x), g.value(x));
            }
        };
    }

    /**
     * Returns a MultivariateFunction h(x[]). Defined by:
     * <pre> <code>
     * h(x[]) = combiner(...combiner(combiner(initialValue,f(x[0])),f(x[1]))...),f(x[x.length-1]))
     * </code></pre>
     *
     * @param combiner Combiner function.
     * @param f Function.
     * @param initialValue Initial value.
     * @return a collector function.
     */
    public static MultivariateFunction collector(final BivariateFunction combiner,
                                                 final UnivariateFunction f,
                                                 final double initialValue) {
        return new MultivariateFunction() {
            /** {@inheritDoc} */
            @Override
            public double value(double[] point) {
                double result = combiner.value(initialValue, f.value(point[0]));
                for (int i = 1; i < point.length; i++) {
                    result = combiner.value(result, f.value(point[i]));
                }
                return result;
            }
        };
    }

    /**
     * Returns a MultivariateFunction h(x[]). Defined by:
     * <pre> <code>
     * h(x[]) = combiner(...combiner(combiner(initialValue,x[0]),x[1])...),x[x.length-1])
     * </code></pre>
     *
     * @param combiner Combiner function.
     * @param initialValue Initial value.
     * @return a collector function.
     */
    public static MultivariateFunction collector(final BivariateFunction combiner,
                                                 final double initialValue) {
        return collector(combiner, new Identity(), initialValue);
    }

    /**
     * Creates a unary function by fixing the first argument of a binary function.
     *
     * @param f Binary function.
     * @param fixed value to which the first argument of {@code f} is set.
     * @return the unary function h(x) = f(fixed, x)
     */
    public static UnivariateFunction fix1stArgument(final BivariateFunction f,
                                                    final double fixed) {
        return new UnivariateFunction() {
            /** {@inheritDoc} */
            @Override
            public double value(double x) {
                return f.value(fixed, x);
            }
        };
    }
    /**
     * Creates a unary function by fixing the second argument of a binary function.
     *
     * @param f Binary function.
     * @param fixed value to which the second argument of {@code f} is set.
     * @return the unary function h(x) = f(x, fixed)
     */
    public static UnivariateFunction fix2ndArgument(final BivariateFunction f,
                                                    final double fixed) {
        return new UnivariateFunction() {
            /** {@inheritDoc} */
            @Override
            public double value(double x) {
                return f.value(x, fixed);
            }
        };
    }

    /** Convert regular functions to {@link UnivariateDifferentiableFunction}.
     * <p>
     * This method handle the case with one free parameter and several derivatives.
     * For the case with several free parameters and only first order derivatives,
     * see {@link #toDifferentiable(MultivariateFunction, MultivariateVectorFunction)}.
     * There are no direct support for intermediate cases, with several free parameters
     * and order 2 or more derivatives, as is would be difficult to specify all the
     * cross derivatives.
     * </p>
     * <p>
     * Note that the derivatives are expected to be computed only with respect to the
     * raw parameter x of the base function, i.e. they are df/dx, df<sup>2</sup>/dx<sup>2</sup>, ...
     * Even if the built function is later used in a composition like f(sin(t)), the provided
     * derivatives should <em>not</em> apply the composition with sine and its derivatives by
     * themselves. The composition will be done automatically here and the result will properly
     * contain f(sin(t)), df(sin(t))/dt, df<sup>2</sup>(sin(t))/dt<sup>2</sup> despite the
     * provided derivatives functions know nothing about the sine function.
     * </p>
     * @param f base function f(x)
     * @param derivatives derivatives of the base function, in increasing differentiation order
     * @return a differentiable function with value and all specified derivatives
     * @see #toDifferentiable(MultivariateFunction, MultivariateVectorFunction)
     * @see #derivative(UnivariateDifferentiableFunction, int)
     */
    public static UnivariateDifferentiableFunction toDifferentiable(final UnivariateFunction f,
                                                                       final UnivariateFunction ... derivatives) {

        return new UnivariateDifferentiableFunction() {

            /** {@inheritDoc} */
            @Override
            public double value(final double x) {
                return f.value(x);
            }

            /** {@inheritDoc} */
            @Override
            public DerivativeStructure value(final DerivativeStructure x) {
                if (x.getOrder() > derivatives.length) {
                    throw new NumberIsTooLargeException(x.getOrder(), derivatives.length, true);
                }
                final double[] packed = new double[x.getOrder() + 1];
                packed[0] = f.value(x.getValue());
                for (int i = 0; i < x.getOrder(); ++i) {
                    packed[i + 1] = derivatives[i].value(x.getValue());
                }
                return x.compose(packed);
            }
        };
    }

    /** Convert regular functions to {@link MultivariateDifferentiableFunction}.
     * <p>
     * This method handle the case with several free parameters and only first order derivatives.
     * For the case with one free parameter and several derivatives,
     * see {@link #toDifferentiable(UnivariateFunction, UnivariateFunction...)}.
     * There are no direct support for intermediate cases, with several free parameters
     * and order 2 or more derivatives, as is would be difficult to specify all the
     * cross derivatives.
     * </p>
     * <p>
     * Note that the gradient is expected to be computed only with respect to the
     * raw parameter x of the base function, i.e. it is df/dx<sub>1</sub>, df/dx<sub>2</sub>, ...
     * Even if the built function is later used in a composition like f(sin(t), cos(t)), the provided
     * gradient should <em>not</em> apply the composition with sine or cosine and their derivative by
     * itself. The composition will be done automatically here and the result will properly
     * contain f(sin(t), cos(t)), df(sin(t), cos(t))/dt despite the provided derivatives functions
     * know nothing about the sine or cosine functions.
     * </p>
     * @param f base function f(x)
     * @param gradient gradient of the base function
     * @return a differentiable function with value and gradient
     * @see #toDifferentiable(UnivariateFunction, UnivariateFunction...)
     * @see #derivative(MultivariateDifferentiableFunction, int[])
     */
    public static MultivariateDifferentiableFunction toDifferentiable(final MultivariateFunction f,
                                                                      final MultivariateVectorFunction gradient) {

        return new MultivariateDifferentiableFunction() {

            /** {@inheritDoc} */
            @Override
            public double value(final double[] point) {
                return f.value(point);
            }

            /** {@inheritDoc} */
            @Override
            public DerivativeStructure value(final DerivativeStructure[] point) {

                // set up the input parameters
                final double[] dPoint = new double[point.length];
                for (int i = 0; i < point.length; ++i) {
                    dPoint[i] = point[i].getValue();
                    if (point[i].getOrder() > 1) {
                        throw new NumberIsTooLargeException(point[i].getOrder(), 1, true);
                    }
                }

                // evaluate regular functions
                final double    v = f.value(dPoint);
                final double[] dv = gradient.value(dPoint);
                if (dv.length != point.length) {
                    // the gradient function is inconsistent
                    throw new DimensionMismatchException(dv.length, point.length);
                }

                // build the combined derivative
                final int parameters = point[0].getFreeParameters();
                final double[] partials = new double[point.length];
                final double[] packed = new double[parameters + 1];
                packed[0] = v;
                final int[] orders = new int[parameters];
                for (int i = 0; i < parameters; ++i) {

                    // we differentiate once with respect to parameter i
                    orders[i] = 1;
                    for (int j = 0; j < point.length; ++j) {
                        partials[j] = point[j].getPartialDerivative(orders);
                    }
                    orders[i] = 0;

                    // compose partial derivatives
                    packed[i + 1] = Sum.ofProducts(dv, partials).getAsDouble();
                }

                return new DerivativeStructure(parameters, 1, packed);
            }
        };
    }

    /** Convert an {@link UnivariateDifferentiableFunction} to an
     * {@link UnivariateFunction} computing n<sup>th</sup> order derivative.
     * <p>
     * This converter is only a convenience method. Beware computing only one derivative does
     * not save any computation as the original function will really be called under the hood.
     * The derivative will be extracted from the full {@link DerivativeStructure} result.
     * </p>
     * @param f original function, with value and all its derivatives
     * @param order of the derivative to extract
     * @return function computing the derivative at required order
     * @see #derivative(MultivariateDifferentiableFunction, int[])
     * @see #toDifferentiable(UnivariateFunction, UnivariateFunction...)
     */
    public static UnivariateFunction derivative(final UnivariateDifferentiableFunction f, final int order) {
        return new UnivariateFunction() {

            /** {@inheritDoc} */
            @Override
            public double value(final double x) {
                final DerivativeStructure dsX = new DerivativeStructure(1, order, 0, x);
                return f.value(dsX).getPartialDerivative(order);
            }
        };
    }

    /** Convert an {@link MultivariateDifferentiableFunction} to an
     * {@link MultivariateFunction} computing n<sup>th</sup> order derivative.
     * <p>
     * This converter is only a convenience method. Beware computing only one derivative does
     * not save any computation as the original function will really be called under the hood.
     * The derivative will be extracted from the full {@link DerivativeStructure} result.
     * </p>
     * @param f original function, with value and all its derivatives
     * @param orders of the derivative to extract, for each free parameters
     * @return function computing the derivative at required order
     * @see #derivative(UnivariateDifferentiableFunction, int)
     * @see #toDifferentiable(MultivariateFunction, MultivariateVectorFunction)
     */
    public static MultivariateFunction derivative(final MultivariateDifferentiableFunction f, final int[] orders) {
        return new MultivariateFunction() {

            /** {@inheritDoc} */
            @Override
            public double value(final double[] point) {

                // the maximum differentiation order is the sum of all orders
                int sumOrders = 0;
                for (final int order : orders) {
                    sumOrders += order;
                }

                // set up the input parameters
                final DerivativeStructure[] dsPoint = new DerivativeStructure[point.length];
                for (int i = 0; i < point.length; ++i) {
                    dsPoint[i] = new DerivativeStructure(point.length, sumOrders, i, point[i]);
                }

                return f.value(dsPoint).getPartialDerivative(orders);
            }
        };
    }
}
