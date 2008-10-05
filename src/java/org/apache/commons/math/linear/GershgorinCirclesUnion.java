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

package org.apache.commons.math.linear;

/** Class representing a union of Gershgorin circles.
 * <p>Gershgorin circles are bounding areas where eigenvalues must lie.
 * They are used as starting values for eigen decomposition algorithms.
 * In the real case, Gershgorin circles are simple intervals.</p>
 * @see EigenDecompositionImpl
 * @version $Revision$ $Date$
 * @since 2.0
 */
class GershgorinCirclesUnion implements Comparable<GershgorinCirclesUnion> {

    /** Lower bound of the interval. */
    private double low;

    /** Higher bound of the interval. */
    private double high;

    /** Create a simple Gershgorin circle.
     * @param d diagonal element of the current row
     * @param sum sum of the absolute values of the off-diagonal elements
     * of the current row
     */
    public GershgorinCirclesUnion(final double d, final double sum) {
        low  = d - sum;
        high = d + sum;
    }

    /**
     * Get the lower bound of the interval.
     * @return lower bound of the interval
     */
    public double getLow() {
        return low;
    }

    /**
     * Get the higher bound of the interval.
     * @return higher bound of the interval
     */
    public double getHigh() {
        return high;
    }

    /**
     * Check if a Gershgorin circles union intersects instance.
     * @param other Gershgorin circles union to test against instance
     * @return true if the other Gershgorin circles union intersects instance
     */
    public boolean intersects(final GershgorinCirclesUnion other) {
        return (other.low <= this.high) && (other.high >= this.low);
    }

    /**
     * Swallow another Gershgorin circles union.
     * <p>Swallowing another Gershgorin circles union changes the
     * instance such that it contains everything that was formerly in
     * either circles union. It is mainly intended for circles unions
     * that {@link #intersects(GershgorinCirclesUnion) intersect}
     * each other beforehand.</p>
     */
    public void swallow(final GershgorinCirclesUnion other) {
        low  = Math.min(low,  other.low);
        high = Math.max(high, other.high);
    }

    /** Comparator class for sorting intervals. */
    public int compareTo(GershgorinCirclesUnion other) {
        return Double.compare(low, other.low);
    }

}
