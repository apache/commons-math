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
package org.apache.commons.math3.geometry.spherical.oned;

import org.apache.commons.math3.geometry.partitioning.Region.Location;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.Precision;


/** This class represents an arc on a circle.
 * @see ArcsSet
 * @version $Id$
 * @since 3.3
 */
public class Arc {

    /** The lower angular bound of the arc. */
    private final double lower;

    /** The upper angular bound of the arc. */
    private final double upper;

    /** Middle point of the arc. */
    private final double middle;

    /** Simple constructor.
     * <p>
     * As the circle is a closed curve, {@code lower} is
     * allowed to be greater than {@code upper}, and will
     * be automatically canonicalized so the arc wraps
     * around \( 2\pi \), but without exceeding a total
     * length of \( 2\pi \). If {@code lower} is equals
     * to {@code upper}, the arc is considered to be the full
     * circle.
     * </p>
     * @param lower lower angular bound of the arc
     * @param upper upper angular bound of the arc
     */
    public Arc(final double lower, final double upper) {
        this.lower = lower;
        if (Precision.equals(lower, upper, 0)) {
            this.upper = 2 * FastMath.PI + lower;
        } else {
            this.upper = MathUtils.normalizeAngle(upper, lower + FastMath.PI);
        }
        this.middle = 0.5 * (this.lower + this.upper);
    }

    /** Get the lower angular bound of the arc.
     * @return lower angular bound of the arc
     */
    public double getInf() {
        return lower;
    }

    /** Get the upper angular bound of the arc.
     * @return upper angular bound of the arc
     */
    public double getSup() {
        return upper;
    }

    /** Get the angular size of the arc.
     * @return angular size of the arc
     */
    public double getSize() {
        return upper - lower;
    }

    /** Get the barycenter of the arc.
     * @return barycenter of the arc
     */
    public double getBarycenter() {
        return middle;
    }

    /** Check a point with respect to the arc.
     * @param point point to check
     * @param tolerance tolerance below which points are considered to
     * belong to the boundary
     * @return a code representing the point status: either {@link
     * Location#INSIDE}, {@link Location#OUTSIDE} or {@link Location#BOUNDARY}
     */
    public Location checkPoint(final double point, final double tolerance) {
        final double normalizedPoint = MathUtils.normalizeAngle(point, middle);
        if (normalizedPoint < lower - tolerance || normalizedPoint > upper + tolerance) {
            return Location.OUTSIDE;
        } else if (normalizedPoint > lower + tolerance && normalizedPoint < upper - tolerance) {
            return Location.INSIDE;
        } else {
            return Location.BOUNDARY;
        }
    }

}
