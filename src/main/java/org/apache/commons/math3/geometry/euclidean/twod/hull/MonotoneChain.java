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
package org.apache.commons.math3.geometry.euclidean.twod.hull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

/**
 * Implements Andrew's monotone chain method to generate the convex hull of a finite set of
 * points in the two-dimensional euclidean space.
 * <p>
 * The implementation is not sensitive to colinear points. The runtime complexity
 * is O(n log n), with n being the number of input points. If the point set is already
 * sorted (by x-coordinate), the runtime complexity is O(n).
 *
 * @see <a href="http://en.wikibooks.org/wiki/Algorithm_Implementation/Geometry/Convex_hull/Monotone_chain">
 * Andrew's monotone chain algorithm (Wikibooks)</a>
 * @since 3.3
 * @version $Id$
 */
public class MonotoneChain implements ConvexHullGenerator2D {

    /** Default value for tolerance. */
    private static final double DEFAULT_TOLERANCE = 1e-10;

    /** Tolerance below which points are considered identical. */
    private final double tolerance;

    /**
     * Creates a new instance.
     * <p>
     * The default tolerance (1e-10) will be used to determine identical points.
     */
    public MonotoneChain() {
        this(DEFAULT_TOLERANCE);
    }

    /**
     * Creates a new instance with the given tolerance for determining identical points.
     * @param tolerance tolerance below which points are considered identical
     */
    public MonotoneChain(final double tolerance) {
        this.tolerance = tolerance;
    }

    /** {@inheritDoc} */
    public ConvexHull2D generate(final Collection<Vector2D> points) throws NullArgumentException {

        // check for null points
        MathUtils.checkNotNull(points);

        if (points.size() < 3) {
            return new ConvexHull2D(points, tolerance);
        }

        final List<Vector2D> pointsSortedByXAxis = new ArrayList<Vector2D>(points);

        // sort the points in increasing order on the x-axis
        Collections.sort(pointsSortedByXAxis, new Comparator<Vector2D>() {
            public int compare(final Vector2D o1, final Vector2D o2) {
                final int diff = (int) FastMath.signum(o1.getX() - o2.getX());
                if (diff == 0) {
                    return (int) FastMath.signum(o1.getY() - o2.getY());
                } else {
                    return diff;
                }
            }
        });

        // build lower hull
        final List<Vector2D> lowerHull = new ArrayList<Vector2D>();
        for (Vector2D p : pointsSortedByXAxis) {
            while (lowerHull.size() >= 2) {
                final int size = lowerHull.size();
                final Vector2D p1 = lowerHull.get(size - 2);
                final Vector2D p2 = lowerHull.get(size - 1);

                if (getLocation(p, p1, p2) <= 0) {
                    lowerHull.remove(size - 1);
                } else {
                    break;
                }
            }
            lowerHull.add(p);
        }

        // build upper hull
        final List<Vector2D> upperHull = new ArrayList<Vector2D>();
        for (int idx = pointsSortedByXAxis.size() - 1; idx >= 0; idx--) {
            final Vector2D p = pointsSortedByXAxis.get(idx);
            while (upperHull.size() >= 2) {
                final int size = upperHull.size();
                final Vector2D p1 = upperHull.get(size - 2);
                final Vector2D p2 = upperHull.get(size - 1);

                if (getLocation(p, p1, p2) <= 0) {
                    upperHull.remove(size - 1);
                } else {
                    break;
                }
            }
            upperHull.add(p);
        }

        // concatenate the lower and upper hulls
        // the last point of each list is omitted as it is repeated at the beginning of the other list
        List<Vector2D> hullVertices = new ArrayList<Vector2D>(lowerHull.size() + upperHull.size() - 2);
        for (int idx = 0; idx < lowerHull.size() - 1; idx++) {
            hullVertices.add(lowerHull.get(idx));
        }
        for (int idx = 0; idx < upperHull.size() - 1; idx++) {
            hullVertices.add(upperHull.get(idx));
        }

        return new ConvexHull2D(hullVertices, tolerance);
    }

    /**
     * Get the location of a point with regard to the given line.
     * <p>
     * Note: this method does the same as {@link Line#getOffset(Vector)} but is
     * faster, thus preferred for this heuristic.
     *
     * @param point the point to check
     * @param linePoint1 the first point of the line
     * @param linePoint2 the second point of the line
     * @return the location of the point with regard to the line
     */
    private double getLocation(final Vector2D point,
                               final Vector2D linePoint1,
                               final Vector2D linePoint2) {
        return (linePoint2.getX() - linePoint1.getX()) * (point.getY() - linePoint1.getY()) -
               (point.getX() - linePoint1.getX()) * (linePoint2.getY() - linePoint1.getY());
    }

}
