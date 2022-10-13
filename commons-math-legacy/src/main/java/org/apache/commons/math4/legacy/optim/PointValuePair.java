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
package org.apache.commons.math4.legacy.optim;

import java.util.Arrays;

import org.apache.commons.math4.legacy.core.Pair;

/**
 * This class holds a point and the value of an objective function at
 * that point.
 *
 * @see PointVectorValuePair
 * @see org.apache.commons.math4.legacy.analysis.MultivariateFunction
 * @since 3.0
 */
public final class PointValuePair extends Pair<double[], Double> {
    /**
     * Builds a point/objective function value pair.
     *
     * @param point Point coordinates. This instance will store
     * a copy of the array, not the array passed as argument.
     * @param value Value of the objective function at the point.
     */
    public PointValuePair(final double[] point,
                          final double value) {
        this(point, value, true);
    }

    /**
     * Builds a point/objective function value pair.
     *
     * @param point Point coordinates.
     * @param value Value of the objective function at the point.
     * @param copyArray if {@code true}, the input array will be copied,
     * otherwise it will be referenced.
     */
    public PointValuePair(final double[] point,
                          final double value,
                          final boolean copyArray) {
        super(copyArray ? ((point == null) ? null :
                           point.clone()) :
              point,
              value);
    }

    /**
     * Gets the point.
     *
     * @return a copy of the stored point.
     */
    public double[] getPoint() {
        final double[] p = getKey();
        return p == null ? null : p.clone();
    }

    /**
     * Gets a reference to the point.
     *
     * @return a reference to the internal array storing the point.
     */
    public double[] getPointRef() {
        return getKey();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PointValuePair) {
            final PointValuePair other = (PointValuePair) o;

            return getValue().equals(other.getValue()) &&
                Arrays.equals(getPointRef(),
                              other.getPointRef());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getKey()) + 31 * Double.hashCode(getValue());
    }

    @Override
    public String toString() {
        return "[" + Arrays.toString(getPointRef()) + ", " + getValue() + "]";
    }
}
