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

package org.apache.commons.math.analysis;

import org.apache.commons.math.exception.MathUserException;
import org.apache.commons.math.util.FastMath;


/**
 * Base class for {@link UnivariateRealFunction} that can be composed with other functions.
 *
 * @since 2.1
 * @version $Revision$ $Date$
 * @deprecated in 2.2 (to be removed in 3.0). Please use the function classes
 * in the {@link org.apache.commons.math.analysis.function} package and the
 * methods in {@link FunctionUtils}.
 */
@Deprecated
public abstract class ComposableFunction implements UnivariateRealFunction {
    public static ComposableFunction make(final UnivariateRealFunction f) {
        return new ComposableFunction() {
            /** {@inheritDoc} */
            @Override
            public double value(double x) {
                return f.value(x);
            }
        };
    }

    /** The constant function always returning 0. */
    public static final ComposableFunction ZERO =
        make(new org.apache.commons.math.analysis.function.Constant(0));
    
    /** The constant function always returning 1. */
    public static final ComposableFunction ONE = 
        make(new org.apache.commons.math.analysis.function.Constant(1));

    /** The identity function. */
    public static final ComposableFunction IDENTITY =
        make(new org.apache.commons.math.analysis.function.Identity());

    /** The {@code FastMath.abs} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction ABS =
        make(new org.apache.commons.math.analysis.function.Abs());

    /** The - operator wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction NEGATE = 
        make(new org.apache.commons.math.analysis.function.Minus());

    /** The invert operator wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction INVERT =
        make(new org.apache.commons.math.analysis.function.Inverse());

    /** The {@code FastMath.sin} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction SIN =
        make(new org.apache.commons.math.analysis.function.Sin());

    /** The {@code FastMath.sqrt} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction SQRT =
        make(new org.apache.commons.math.analysis.function.Sqrt());

    /** The {@code FastMath.sinh} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction SINH =
        make(new org.apache.commons.math.analysis.function.Sinh());

    /** The {@code FastMath.exp} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction EXP =
        make(new org.apache.commons.math.analysis.function.Exp());

    /** The {@code FastMath.expm1} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction EXPM1 =
        make(new org.apache.commons.math.analysis.function.Expm1());

    /** The {@code FastMath.asin} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction ASIN =
        make(new org.apache.commons.math.analysis.function.Asin());

    /** The {@code FastMath.atan} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction ATAN =
        make(new org.apache.commons.math.analysis.function.Atan());

    /** The {@code FastMath.tan} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction TAN =
        make(new org.apache.commons.math.analysis.function.Tan());

    /** The {@code FastMath.tanh} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction TANH =
        make(new org.apache.commons.math.analysis.function.Tanh());

    /** The {@code FastMath.cbrt} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction CBRT =
        make(new org.apache.commons.math.analysis.function.Cbrt());

    /** The {@code FastMath.ceil} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction CEIL = new ComposableFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double d) {
            return FastMath.ceil(d);
        }
    };

    /** The {@code FastMath.floor} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction FLOOR = new ComposableFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double d) {
            return FastMath.floor(d);
        }
    };

    /** The {@code FastMath.log} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction LOG =
        make(new org.apache.commons.math.analysis.function.Log());

    /** The {@code FastMath.log10} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction LOG10 =
        make(new org.apache.commons.math.analysis.function.Log10());

    /** The {@code FastMath.log1p} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction LOG1P =
        make(new org.apache.commons.math.analysis.function.Log1p());

    /** The {@code FastMath.cos} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction COS =
        make(new org.apache.commons.math.analysis.function.Cos());

    /** The {@code FastMath.abs} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction ACOS =
        make(new org.apache.commons.math.analysis.function.Acos());

    /** The {@code FastMath.cosh} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction COSH =
        make(new org.apache.commons.math.analysis.function.Cosh());

    /** The {@code FastMath.rint} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction RINT = new ComposableFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double d) {
            return FastMath.rint(d);
        }
    };

    /** The {@code FastMath.signum} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction SIGNUM = new ComposableFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double d) {
            return FastMath.signum(d);
        }
    };

    /** The {@code FastMath.ulp} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction ULP = new ComposableFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double d) {
            return FastMath.ulp(d);
        }
    };

    /** Precompose the instance with another function.
     * <p>
     * The composed function h created by {@code h = g.of(f)} is such
     * that {@code h.value(x) == g.value(f.value(x))} for all x.
     * </p>
     * @param f function to compose with
     * @return a new function which computes {@code this.value(f.value(x))}
     * @see #postCompose(UnivariateRealFunction)
     */
    public ComposableFunction of(final UnivariateRealFunction f) {
        return new ComposableFunction() {
            @Override
            /** {@inheritDoc} */
            public double value(double x) throws MathUserException {
                return ComposableFunction.this.value(f.value(x));
            }
        };
    }

    /** Postcompose the instance with another function.
     * <p>
     * The composed function h created by {@code h = g.postCompose(f)} is such
     * that {@code h.value(x) == f.value(g.value(x))} for all x.
     * </p>
     * @param f function to compose with
     * @return a new function which computes {@code f.value(this.value(x))}
     * @see #of(UnivariateRealFunction)
     */
    public ComposableFunction postCompose(final UnivariateRealFunction f) {
        return new ComposableFunction() {
            @Override
            /** {@inheritDoc} */
            public double value(double x) throws MathUserException {
                return f.value(ComposableFunction.this.value(x));
            }
        };
    }

    /**
     * Return a function combining the instance and another function.
     * <p>
     * The function h created by {@code h = g.combine(f, combiner)} is such that
     * {@code h.value(x) == combiner.value(g.value(x), f.value(x))} for all x.
     * </p>
     * @param f function to combine with the instance
     * @param combiner bivariate function used for combining
     * @return a new function which computes {@code combine.value(this.value(x), f.value(x))}
     */
    public ComposableFunction combine(final UnivariateRealFunction f,
                                      final BivariateRealFunction combiner) {
        return new ComposableFunction() {
            @Override
            /** {@inheritDoc} */
            public double value(double x) throws MathUserException {
                return combiner.value(ComposableFunction.this.value(x), f.value(x));
            }
        };
    }

    /**
     * Return a function adding the instance and another function.
     * @param f function to combine with the instance
     * @return a new function which computes {@code this.value(x) + f.value(x)}
     */
    public ComposableFunction add(final UnivariateRealFunction f) {
        return new ComposableFunction() {
            @Override
            /** {@inheritDoc} */
            public double value(double x) throws MathUserException {
                return ComposableFunction.this.value(x) + f.value(x);
            }
        };
    }

    /**
     * Return a function adding a constant term to the instance.
     * @param a term to add
     * @return a new function which computes {@code this.value(x) + a}
     */
    public ComposableFunction add(final double a) {
        return new ComposableFunction() {
            @Override
            /** {@inheritDoc} */
            public double value(double x) throws MathUserException {
                return ComposableFunction.this.value(x) + a;
            }
        };
    }

    /**
     * Return a function subtracting another function from the instance.
     * @param f function to combine with the instance
     * @return a new function which computes {@code this.value(x) - f.value(x)}
     */
    public ComposableFunction subtract(final UnivariateRealFunction f) {
        return new ComposableFunction() {
            @Override
            /** {@inheritDoc} */
            public double value(double x) throws MathUserException {
                return ComposableFunction.this.value(x) - f.value(x);
            }
        };
    }

    /**
     * Return a function multiplying the instance and another function.
     * @param f function to combine with the instance
     * @return a new function which computes {@code this.value(x) * f.value(x)}
     */
    public ComposableFunction multiply(final UnivariateRealFunction f) {
        return new ComposableFunction() {
            @Override
            /** {@inheritDoc} */
            public double value(double x) throws MathUserException {
                return ComposableFunction.this.value(x) * f.value(x);
            }
        };
    }

    /**
     * Return a function scaling the instance by a constant factor.
     * @param scaleFactor constant scaling factor
     * @return a new function which computes {@code this.value(x) * scaleFactor}
     */
    public ComposableFunction multiply(final double scaleFactor) {
        return new ComposableFunction() {
            @Override
            /** {@inheritDoc} */
            public double value(double x) throws MathUserException {
                return ComposableFunction.this.value(x) * scaleFactor;
            }
        };
    }
    /**
     * Return a function dividing the instance by another function.
     * @param f function to combine with the instance
     * @return a new function which computes {@code this.value(x) / f.value(x)}
     */
    public ComposableFunction divide(final UnivariateRealFunction f) {
        return new ComposableFunction() {
            @Override
            /** {@inheritDoc} */
            public double value(double x) throws MathUserException {
                return ComposableFunction.this.value(x) / f.value(x);
            }
        };
    }

    /**
     * Generates a function that iteratively apply instance function on all
     * elements of an array.
     * <p>
     * The generated function behaves as follows:
     * <ul>
     *   <li>initialize result = initialValue</li>
     *   <li>iterate: {@code result = combiner.value(result,
     *   this.value(nextMultivariateEntry));}</li>
     *   <li>return result</li>
     * </ul>
     * </p>
     * @param combiner combiner to use between entries
     * @param initialValue initial value to use before first entry
     * @return a new function that iteratively applie instance function on all
     * elements of an array.
     */
    public MultivariateRealFunction asCollector(final BivariateRealFunction combiner,
                                                final double initialValue) {
        return new MultivariateRealFunction() {
            /** {@inheritDoc} */
            public double value(double[] point)
                throws MathUserException, IllegalArgumentException {
                double result = initialValue;
                for (final double entry : point) {
                    result = combiner.value(result, ComposableFunction.this.value(entry));
                }
                return result;
            }
        };
    }

    /**
     * Generates a function that iteratively apply instance function on all
     * elements of an array.
     * <p>
     * Calling this method is equivalent to call {@link
     * #asCollector(BivariateRealFunction, double) asCollector(BivariateRealFunction, 0.0)}.
     * </p>
     * @param combiner combiner to use between entries
     * @return a new function that iteratively applie instance function on all
     * elements of an array.
     * @see #asCollector(BivariateRealFunction, double)
     */
    public  MultivariateRealFunction asCollector(final BivariateRealFunction combiner) {
        return asCollector(combiner, 0.0);
    }

    /**
     * Generates a function that iteratively apply instance function on all
     * elements of an array.
     * <p>
     * Calling this method is equivalent to call {@link
     * #asCollector(BivariateRealFunction, double) asCollector(BinaryFunction.ADD, initialValue)}.
     * </p>
     * @param initialValue initial value to use before first entry
     * @return a new function that iteratively applie instance function on all
     * elements of an array.
     * @see #asCollector(BivariateRealFunction, double)
     * @see BinaryFunction#ADD
     */
    public  MultivariateRealFunction asCollector(final double initialValue) {
        return asCollector(BinaryFunction.ADD, initialValue);
    }

    /**
     * Generates a function that iteratively apply instance function on all
     * elements of an array.
     * <p>
     * Calling this method is equivalent to call {@link
     * #asCollector(BivariateRealFunction, double) asCollector(BinaryFunction.ADD, 0.0)}.
     * </p>
     * @return a new function that iteratively applie instance function on all
     * elements of an array.
     * @see #asCollector(BivariateRealFunction, double)
     * @see BinaryFunction#ADD
     */
    public  MultivariateRealFunction asCollector() {
        return asCollector(BinaryFunction.ADD, 0.0);
    }

    /** {@inheritDoc} */
    public abstract double value(double x) throws MathUserException;

}
