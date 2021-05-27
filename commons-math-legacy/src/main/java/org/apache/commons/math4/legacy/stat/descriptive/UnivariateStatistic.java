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

import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.apache.commons.math4.legacy.exception.NullArgumentException;
import org.apache.commons.math4.legacy.exception.util.LocalizedFormats;
import org.apache.commons.math4.legacy.util.MathArrays;

import java.io.Serializable;


/**
 * Base interface implemented by all statistics.
 */
public interface UnivariateStatistic extends MathArrays.Function {
    /**
     * Returns the result of evaluating the statistic over the input array.
     *
     * @param values input array
     * @return the value of the statistic applied to the input array
     * @throws MathIllegalArgumentException  if values is null
     */
    @Override
    double evaluate(double[] values) throws MathIllegalArgumentException;

    /**
     * Returns the result of evaluating the statistic over the specified entries
     * in the input array.
     *
     * @param values the input array
     * @param begin the index of the first element to include
     * @param length the number of elements to include
     * @return the value of the statistic applied to the included array entries
     * @throws MathIllegalArgumentException if values is null or the indices are invalid
     */
    @Override
    double evaluate(double[] values, int begin, int length) throws MathIllegalArgumentException;

    /**
     * Returns a copy of the statistic with the same internal state.
     *
     * @return a copy of the statistic
     */
    UnivariateStatistic copy();

    /**
     * Subclasses implementing this interface can transform Objects to doubles.
     *
     * No longer extends Serializable since 2.0
     *
     */
    interface NumberTransformer {

        /**
         * Implementing this interface provides a facility to transform
         * from Object to Double.
         *
         * @param o the Object to be transformed.
         * @return the double value of the Object.
         * @throws MathIllegalArgumentException if the Object can not be transformed into a Double.
         */
        double transform(Object o) throws MathIllegalArgumentException;
    }

    /**
     * A Default NumberTransformer for java.lang.Numbers and Numeric Strings. This
     * provides some simple conversion capabilities to turn any java.lang.Number
     * into a primitive double or to turn a String representation of a Number into
     * a double.
     */
    class DefaultTransformer implements NumberTransformer, Serializable {

        /** Serializable version identifier */
        private static final long serialVersionUID = 4019938025047800455L;

        /**
         * @param o  the object that gets transformed.
         * @return a double primitive representation of the Object o.
         * @throws NullArgumentException if Object <code>o</code> is {@code null}.
         * @throws MathIllegalArgumentException if Object <code>o</code>
         * cannot successfully be transformed
         * @see <a href="http://commons.apache.org/collections/api-release/org/apache/commons/collections/Transformer.html">Commons Collections Transformer</a>
         */
        @Override
        public double transform(Object o)
            throws NullArgumentException, MathIllegalArgumentException {

            if (o == null) {
                throw new NullArgumentException(LocalizedFormats.OBJECT_TRANSFORMATION);
            }

            if (o instanceof Number) {
                return ((Number)o).doubleValue();
            }

            try {
                return Double.parseDouble(o.toString());
            } catch (NumberFormatException e) {
                throw new MathIllegalArgumentException(LocalizedFormats.CANNOT_TRANSFORM_TO_DOUBLE,
                                                       o.toString());
            }
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            return other instanceof DefaultTransformer;
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            // some arbitrary number ...
            return 401993047;
        }

    }
}
