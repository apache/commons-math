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


/**
 * Set of {@link UnivariateRealFunction} classes wrapping methods from
 * the standard Math class.
 *
 * @version $Revision$ $Date$
 */
public class UnivariateRealFunctions {

    /** The {@code Math.abs} method wrapped as a {@link UnivariateRealFunction}. */
    public static final UnivariateRealFunction ABS = new UnivariateRealFunction() {
        /** {@inheritDoc} */
        public double value(double d) {
            return Math.abs(d);
        }
    };

    /** The - operator wrapped as a {@link UnivariateRealFunction}. */
    public static final UnivariateRealFunction NEGATE = new UnivariateRealFunction() {
        /** {@inheritDoc} */
        public double value(double d) {
            return -d;
        }
    };

    /** The {@code Math.sin} method wrapped as a {@link UnivariateRealFunction}. */
    public static final UnivariateRealFunction SIN = new UnivariateRealFunction() {
        /** {@inheritDoc} */
        public double value(double d) {
            return Math.sin(d);
        }
    };

    /** The {@code Math.sqrt} method wrapped as a {@link UnivariateRealFunction}. */
    public static final UnivariateRealFunction SQRT = new UnivariateRealFunction() {
        /** {@inheritDoc} */
        public double value(double d) {
            return Math.sqrt(d);
        }
    };

    /** The {@code Math.sinh} method wrapped as a {@link UnivariateRealFunction}. */
    public static final UnivariateRealFunction SINH = new UnivariateRealFunction() {
        /** {@inheritDoc} */
        public double value(double d) {
            return Math.sinh(d);
        }
    };

    /** The {@code Math.exp} method wrapped as a {@link UnivariateRealFunction}. */
    public static final UnivariateRealFunction EXP = new UnivariateRealFunction() {
        /** {@inheritDoc} */
        public double value(double d) {
            return Math.exp(d);
        }
    };

    /** The {@code Math.expm1} method wrapped as a {@link UnivariateRealFunction}. */
    public static final UnivariateRealFunction EXP1M = new UnivariateRealFunction() {
        /** {@inheritDoc} */
        public double value(double d) {
            return Math.expm1(d);
        }
    };

    /** The {@code Math.asin} method wrapped as a {@link UnivariateRealFunction}. */
    public static final UnivariateRealFunction ASIN = new UnivariateRealFunction() {
        /** {@inheritDoc} */
        public double value(double d) {
            return Math.asin(d);
        }
    };

    /** The {@code Math.atan} method wrapped as a {@link UnivariateRealFunction}. */
    public static final UnivariateRealFunction ATAN = new UnivariateRealFunction() {
        /** {@inheritDoc} */
        public double value(double d) {
            return Math.atan(d);
        }
    };

    /** The {@code Math.tan} method wrapped as a {@link UnivariateRealFunction}. */
    public static final UnivariateRealFunction TAN = new UnivariateRealFunction() {
        /** {@inheritDoc} */
        public double value(double d) {
            return Math.tan(d);
        }
    };

    /** The {@code Math.tanh} method wrapped as a {@link UnivariateRealFunction}. */
    public static final UnivariateRealFunction TANH = new UnivariateRealFunction() {
        /** {@inheritDoc} */
        public double value(double d) {
            return Math.tanh(d);
        }
    };

    /** The {@code Math.cbrt} method wrapped as a {@link UnivariateRealFunction}. */
    public static final UnivariateRealFunction CBRT = new UnivariateRealFunction() {
        /** {@inheritDoc} */
        public double value(double d) {
            return Math.cbrt(d);
        }
    };

    /** The {@code Math.ceil} method wrapped as a {@link UnivariateRealFunction}. */
    public static final UnivariateRealFunction CEIL = new UnivariateRealFunction() {
        /** {@inheritDoc} */
        public double value(double d) {
            return Math.ceil(d);
        }
    };

    /** The {@code Math.floor} method wrapped as a {@link UnivariateRealFunction}. */
    public static final UnivariateRealFunction FLOOR = new UnivariateRealFunction() {
        /** {@inheritDoc} */
        public double value(double d) {
            return Math.floor(d);
        }
    };

    /** The {@code Math.log} method wrapped as a {@link UnivariateRealFunction}. */
    public static final UnivariateRealFunction LOG = new UnivariateRealFunction() {
        /** {@inheritDoc} */
        public double value(double d) {
            return Math.log(d);
        }
    };

    /** The {@code Math.log10} method wrapped as a {@link UnivariateRealFunction}. */
    public static final UnivariateRealFunction LOG10 = new UnivariateRealFunction() {
        /** {@inheritDoc} */
        public double value(double d) {
            return Math.log10(d);
        }
    };

    /** The {@code Math.cos} method wrapped as a {@link UnivariateRealFunction}. */
    public static final UnivariateRealFunction COS = new UnivariateRealFunction() {
        /** {@inheritDoc} */
        public double value(double d) {
            return Math.cos(d);
        }
    };

    /** The {@code Math.abs} method wrapped as a {@link UnivariateRealFunction}. */
    public static final UnivariateRealFunction ACOS = new UnivariateRealFunction() {
        /** {@inheritDoc} */
        public double value(double d) {
            return Math.acos(d);
        }
    };

    /** The {@code Math.cosh} method wrapped as a {@link UnivariateRealFunction}. */
    public static final UnivariateRealFunction COSH = new UnivariateRealFunction() {
        /** {@inheritDoc} */
        public double value(double d) {
            return Math.cosh(d);
        }
    };

    /** The {@code Math.rint} method wrapped as a {@link UnivariateRealFunction}. */
    public static final UnivariateRealFunction RINT = new UnivariateRealFunction() {
        /** {@inheritDoc} */
        public double value(double d) {
            return Math.rint(d);
        }
    };

    /** The {@code Math.signum} method wrapped as a {@link UnivariateRealFunction}. */
    public static final UnivariateRealFunction SIGNUM = new UnivariateRealFunction() {
        /** {@inheritDoc} */
        public double value(double d) {
            return Math.signum(d);
        }
    };

    /** The {@code Math.ulp} method wrapped as a {@link UnivariateRealFunction}. */
    public static final UnivariateRealFunction ULP = new UnivariateRealFunction() {
        /** {@inheritDoc} */
        public double value(double d) {
            return Math.ulp(d);
        }
    };

    /** The {@code Math.pow} method wrapped as a {@link UnivariateRealFunction}. */
    public static class Pow implements UnivariateRealFunction {

        /** The power to which the value should be raised. */
        private final double pow;

        /** Simple constructor.
         * @param pow the power to which the value should be raised
         */
        public Pow(double pow) {
            this.pow = pow;
        }

        /** {@inheritDoc} */
        public double value(double d) {
            return Math.pow(d, pow);
        }

    }

}
