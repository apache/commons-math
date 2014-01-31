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
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

/**
 * Implements Graham's scan method to generate the convex hull of a finite set of
 * points in the two-dimensional euclidean space.
 * <p>
 * The implementation is not sensitive to colinear points. The runtime complexity
 * is O(n log n), with n being the number of input points.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Graham_scan">Graham's scan algorithm (Wikipedia)</a>
 * @since 3.3
 * @version $Id$
 */
public class GrahamScan implements ConvexHullGenerator2D {

    /** Default value for tolerance. */
    private static final double DEFAULT_TOLERANCE = 1e-10;

    /** Vector representing the x-axis. */
    private static final Vector2D X_AXIS = new Vector2D(1.0, 0.0);

    /** Tolerance below which points are considered identical. */
    private final double tolerance;

    /**
     * Creates a new instance.
     * <p>
     * The default tolerance (1e-10) will be used to determine identical points.
     */
    public GrahamScan() {
        this(DEFAULT_TOLERANCE);
    }

    /**
     * Creates a new instance with the given tolerance for determining identical points.
     * @param tolerance tolerance below which points are considered identical
     */
    public GrahamScan(final double tolerance) {
        this.tolerance = tolerance;
    }

    /** {@inheritDoc} */
    public ConvexHull2D generate(final Collection<Vector2D> points) throws NullArgumentException {

        // check for null points
        MathUtils.checkNotNull(points);

        if (points.size() < 3) {
            return new ConvexHull2D(points, tolerance);
        }

        final Vector2D referencePoint = getReferencePoint(points);

        final List<Vertex> pointsSortedByAngle = new ArrayList<Vertex>();
        for (final Vector2D p : points) {
            pointsSortedByAngle.add(new Vertex(p, getAngleBetweenPoints(p, referencePoint)));
        }

        // sort the points in increasing order of their angles
        Collections.sort(pointsSortedByAngle, new Comparator<Vertex>() {
            public int compare(final Vertex o1, final Vertex o2) {
                return (int) FastMath.signum(o2.angle - o1.angle);
            }
        });

        // list containing the vertices of the hull in ccw direction
        final List<Vector2D> hullVertices = new ArrayList<Vector2D>(points.size());

        // push the first two points on the stack
        final Iterator<Vertex> it = pointsSortedByAngle.iterator();
        hullVertices.add(it.next().point);
        hullVertices.add(it.next().point);

        Vector2D currentPoint = null;
        while (it.hasNext() || currentPoint != null) {
            // push the current point to form a line segment if there is only one element
            final int size = hullVertices.size();
            if (size == 1) {
                hullVertices.add(currentPoint != null ? currentPoint : it.next().point);
                currentPoint = null;
                continue;
            }

            // get the last line segment of the current convex hull
            final Vector2D p1 = hullVertices.get(size - 2);
            final Vector2D p2 = hullVertices.get(size - 1);
            final Line line = new Line(p1, p2, tolerance);

            if (currentPoint == null) {
                currentPoint = it.next().point;
            }

            // test if the current point is to the left of the line
            final double offset = line.getOffset(currentPoint);

            if (offset < 0.0) {
                // the current point forms a convex section
                hullVertices.add(currentPoint);
                currentPoint = null;
            } else {
                // otherwise, the point is either colinear or will create
                // a concave section, thus we need to remove the last point.
                hullVertices.remove(size - 1);
            }
        }

        return new ConvexHull2D(hullVertices, tolerance);
    }

    /**
     * Returns the point with the lowest y-coordinate.
     * <p>
     * In case of a tie, the point with the lowest x-coordinate from the set of
     * candidates is chosen.
     *
     * @param points the point set
     * @return the point with the lowest y-coordinate
     */
    private Vector2D getReferencePoint(final Collection<Vector2D> points) {
        Vector2D minY = null;
        for (final Vector2D p : points) {
            if (minY == null) {
                minY = p;
            } else if (p.getY() < minY.getY()) {
                minY = p;
            } else if (p.getY() == minY.getY() && p.getX() < minY.getX()) {
                minY = p;
            }
        }
        return minY;
    }

    /**
     * Returns the smallest angle between the given two vectors along the x-axis.
     * @param v1 the first vector
     * @param v2 the second vector
     * @return angle in radians in the range [-pi, pi]
     */
    private double getAngleBetweenPoints(final Vector2D v1, final Vector2D v2) {
        final Vector2D p1 = v1.subtract(v2);
        final Vector2D p2 = X_AXIS;
        return FastMath.atan2(p2.getY(), p2.getX()) - FastMath.atan2(p1.getY(), p1.getX());
    }

    /**
     * A helper class used for sorting the points.
     */
    private static class Vertex {

        /** the point */
        private final Vector2D point;
        /** the angle */
        private final double angle;

        /**
         * Create a new Vertex.
         * @param point the point
         * @param angle the angle
         */
        public Vertex(final Vector2D point, final double angle) {
            this.point = point;
            this.angle = angle;
        }

    }

}
