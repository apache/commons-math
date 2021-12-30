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

import org.apache.commons.math4.legacy.core.Pair;

/**
 * This class holds a point and the vectorial value of an objective function at
 * that point.
 *
 * @see PointValuePair
 * @see org.apache.commons.math4.legacy.analysis.MultivariateVectorFunction
 * @since 3.0
 */
public class PointVectorValuePair extends Pair<double[], double[]> {
    /**
     * Builds a point/objective function value pair.
     *
     * @param point Point coordinates. This instance will store
     * a copy of the array, not the array passed as argument.
     * @param value Value of the objective function at the point.
     */
    public PointVectorValuePair(final double[] point,
                                final double[] value) {
        this(point, value, true);
    }

    /**
     * Build a point/objective function value pair.
     *
     * @param point Point coordinates.
     * @param value Value of the objective function at the point.
     * @param copyArray if {@code true}, the input arrays will be copied,
     * otherwise they will be referenced.
     */
    public PointVectorValuePair(final double[] point,
                                final double[] value,
                                final boolean copyArray) {
        super(copyArray ?
              ((point == null) ? null :
               point.clone()) :
              point,
              copyArray ?
              ((value == null) ? null :
               value.clone()) :
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

    /**
     * Gets the value of the objective function.
     *
     * @return a copy of the stored value of the objective function.
     */
    @Override
    public double[] getValue() {
        final double[] v = super.getValue();
        return v == null ? null : v.clone();
    }

    /**
     * Gets a reference to the value of the objective function.
     *
     * @return a reference to the internal array storing the value of
     * the objective function.
     */
    public double[] getValueRef() {
        return super.getValue();
    }
}
