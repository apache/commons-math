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
package org.apache.commons.math4.legacy.stat.descriptive;

import org.apache.commons.math4.legacy.core.MathArrays;
import org.apache.commons.math4.legacy.exception.OutOfRangeException;
import org.apache.commons.math4.legacy.exception.util.LocalizedFormats;
import org.apache.commons.statistics.descriptive.DoubleStatistic;
import org.apache.commons.statistics.descriptive.Quantile;
import org.apache.commons.statistics.descriptive.Quantile.EstimationMethod;

/**
 * Utility class delegating computations to Commons Statistics.
 */
final class Statistics {

    /**
     * Represents a function that accepts a range of {@code double[]} values and produces a result.
     *
     * @param <R> the type of the result of the function
     */
    @FunctionalInterface
    private interface RangeFunction<R> {
        /**
         * Applies this function to the given arguments.
         *
         * @param t the function argument
         * @param from Inclusive start of the range.
         * @param to Exclusive end of the range.
         * @return the function result
         */
        R apply(double[] t, int from, int to);
    }

    /**
     * Delegating univariate statistic implementation.
     * This is the base class for descriptive statistics computed using an array range.
     *
     * @param <R> the statistic type
     */
    private static class StatImp<R extends DoubleStatistic> implements UnivariateStatistic {
        /** Statistic function. */
        private RangeFunction<R> function;

        /**
         * Create an instance.
         *
         * @param function the function
         */
        StatImp(RangeFunction<R> function) {
            this.function = function;
        }

        @Override
        public double evaluate(double[] values) {
            // This method is not used
            throw new IllegalStateException();
        }

        @Override
        public double evaluate(double[] values, int begin, int length) {
            // Support legacy exception behaviour
            MathArrays.verifyValues(values, begin, length);
            return function.apply(values, begin, begin + length).getAsDouble();
        }

        @Override
        public UnivariateStatistic copy() {
            // Return this is safe if the RangeFunction is thread safe
            return this;
        }
    }

    /**
     * Mean implementation.
     */
    static final class Mean extends StatImp<org.apache.commons.statistics.descriptive.Mean> {
        /** Default instance. */
        private static final Mean INSTANCE = new Mean();

        /** Create an instance. */
        private Mean() {
            super(org.apache.commons.statistics.descriptive.Mean::ofRange);
        }

        /**
         * Gets an instance.
         *
         * @return instance
         */
        static Mean getInstance() {
            return INSTANCE;
        }
    }

    /**
     * GeometricMean implementation.
     */
    static final class GeometricMean extends StatImp<org.apache.commons.statistics.descriptive.GeometricMean> {
        /** Default instance. */
        private static final GeometricMean INSTANCE = new GeometricMean();

        /** Create an instance. */
        private GeometricMean() {
            super(org.apache.commons.statistics.descriptive.GeometricMean::ofRange);
        }

        /**
         * Gets an instance.
         *
         * @return instance
         */
        static GeometricMean getInstance() {
            return INSTANCE;
        }
    }

    /**
     * Kurtosis implementation.
     */
    static final class Kurtosis extends StatImp<org.apache.commons.statistics.descriptive.Kurtosis> {
        /** Default instance. */
        private static final Kurtosis INSTANCE = new Kurtosis();

        /** Create an instance. */
        private Kurtosis() {
            super(org.apache.commons.statistics.descriptive.Kurtosis::ofRange);
        }

        /**
         * Gets an instance.
         *
         * @return instance
         */
        static Kurtosis getInstance() {
            return INSTANCE;
        }
    }

    /**
     * Max implementation.
     */
    static final class Max extends StatImp<org.apache.commons.statistics.descriptive.Max> {
        /** Default instance. */
        private static final Max INSTANCE = new Max();

        /** Create an instance. */
        private Max() {
            super(org.apache.commons.statistics.descriptive.Max::ofRange);
        }

        /**
         * Gets an instance.
         *
         * @return instance
         */
        static Max getInstance() {
            return INSTANCE;
        }
    }

    /**
     * Min implementation.
     */
    static final class Min extends StatImp<org.apache.commons.statistics.descriptive.Min> {
        /** Default instance. */
        private static final Min INSTANCE = new Min();

        /** Create an instance. */
        private Min() {
            super(org.apache.commons.statistics.descriptive.Min::ofRange);
        }

        /**
         * Gets an instance.
         *
         * @return instance
         */
        static Min getInstance() {
            return INSTANCE;
        }
    }

    /**
     * Skewness implementation.
     */
    static final class Skewness extends StatImp<org.apache.commons.statistics.descriptive.Skewness> {
        /** Default instance. */
        private static final Skewness INSTANCE = new Skewness();

        /** Create an instance. */
        private Skewness() {
            super(org.apache.commons.statistics.descriptive.Skewness::ofRange);
        }

        /**
         * Gets an instance.
         *
         * @return instance
         */
        static Skewness getInstance() {
            return INSTANCE;
        }
    }

    /**
     * Variance implementation.
     */
    static final class Variance extends StatImp<org.apache.commons.statistics.descriptive.Variance> {
        /** Default instance. */
        private static final Variance INSTANCE = new Variance();

        /** Create an instance. */
        private Variance() {
            super(org.apache.commons.statistics.descriptive.Variance::ofRange);
        }

        /**
         * Gets an instance.
         *
         * @return instance
         */
        static Variance getInstance() {
            return INSTANCE;
        }
    }

    /**
     * SumOfSquares implementation.
     */
    static final class SumOfSquares extends StatImp<org.apache.commons.statistics.descriptive.SumOfSquares> {
        /** Default instance. */
        private static final SumOfSquares INSTANCE = new SumOfSquares();

        /** Create an instance. */
        private SumOfSquares() {
            super(org.apache.commons.statistics.descriptive.SumOfSquares::ofRange);
        }

        /**
         * Gets an instance.
         *
         * @return instance
         */
        static SumOfSquares getInstance() {
            return INSTANCE;
        }
    }

    /**
     * Sum implementation.
     */
    static final class Sum extends StatImp<org.apache.commons.statistics.descriptive.Sum> {
        /** Default instance. */
        private static final Sum INSTANCE = new Sum();

        /** Create an instance. */
        private Sum() {
            super(org.apache.commons.statistics.descriptive.Sum::ofRange);
        }

        /**
         * Gets an instance.
         *
         * @return instance
         */
        static Sum getInstance() {
            return INSTANCE;
        }
    }

    /**
     * Percentile implementation.
     */
    static final class Percentile implements UnivariateStatistic {
        /** Delegate percentile implementation. */
        private static final Quantile QUANTILE =
            Quantile.withDefaults().with(EstimationMethod.HF6).withCopy(false);

        /** Probability. */
        private double p;

        /**
         * Create an instance.
         *
         * @param p Probability in [0, 1].
         */
        private Percentile(double p) {
            this.p = p;
        }

        /**
         * Create an instance.
         *
         * @param percentile Percentile in [0, 100].
         * @return an instance
         * @throws OutOfRangeException if the percentile is invalid.
         */
        static Percentile create(double percentile) {
            return new Percentile(createProbability(percentile));
        }

        /**
         * Create the probability from the percentile.
         *
         * @param percentile Percentile in [0, 100].
         * @return probability in [0, 1]
         * @throws OutOfRangeException if the percentile is invalid.
         */
        static double createProbability(double percentile) {
            if (percentile <= 100 && percentile >= 0) {
                return percentile / 100;
            }
            // NaN or out of range
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_RANGE, percentile, 0, 100);
        }

        /**
         * Sets the quantile (in percent).
         * Note: This is named using the CM legacy method name
         * setQuantile rather than setPercentile.
         *
         * @param percentile Percentile in [0, 100].
         * @throws OutOfRangeException if the percentile is invalid.
         */
        void setQuantile(double percentile) {
            p = createProbability(percentile);
        }

        @Override
        public double evaluate(double[] values) {
            MathArrays.verifyValues(values, 0, 0);
            return QUANTILE.evaluate(values, p);
        }

        @Override
        public double evaluate(double[] values, int begin, int length) {
            MathArrays.verifyValues(values, begin, length);
            return QUANTILE.evaluateRange(values, begin, begin + length, p);
        }

        @Override
        public UnivariateStatistic copy() {
            return new Percentile(p);
        }
    }

    /** No instances. */
    private Statistics() {
        // Do nothing
    }
}
