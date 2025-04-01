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
import org.apache.commons.statistics.descriptive.Quantile;
import org.apache.commons.statistics.descriptive.Quantile.EstimationMethod;

/**
 * Utility class delegating computations to Commons Statistics.
 */
final class Statistics {

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
