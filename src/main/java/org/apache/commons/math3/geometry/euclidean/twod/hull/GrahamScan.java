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

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.FastMath;

/**
 * Implements Graham's scan method to generate the convex hull of a finite set of
 * points in the two-dimensional euclidean space.
 * <p>
 * The implementation is not sensitive to collinear points. The runtime complexity
 * is O(n log n), with n being the number of input points.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Graham_scan">Graham's scan algorithm (Wikipedia)</a>
 * @since 3.3
 * @version $Id$
 */
public class GrahamScan extends AbstractConvexHullGenerator2D {

    /** Vector representing the x-axis. */
    private static final Vector2D X_AXIS = new Vector2D(1.0, 0.0);

    /**
     * Create a new GrahamScan instance.
     * <p>
     * Collinear points on the hull will not be added to the hull vertices and
     * {@code 1e-10} will be used as tolerance criteria for identical points.
     */
    public GrahamScan() {
        super();
    }

    /**
     * Create a new GrahamScan instance.
     * <p>
     * The default tolerance (1e-10) will be used to determine identical points.
     *
     * @param includeCollinearPoints indicates if collinear points on the hull shall be
     * added as hull vertices
     */
    public GrahamScan(final boolean includeCollinearPoints) {
        super(includeCollinearPoints);
    }

    /**
     * Create a new GrahamScan instance.
     *
     * @param includeCollinearPoints indicates if collinear points on the hull shall be
     * added as hull vertices
     * @param tolerance tolerance below which points are considered identical
     */
    public GrahamScan(final boolean includeCollinearPoints, final double tolerance) {
        super(includeCollinearPoints, tolerance);
    }

    @Override
    protected Collection<Vector2D> generateHull(final Collection<Vector2D> points) {

        final Vector2D referencePoint = getReferencePoint(points);

        final List<Vertex> pointsSortedByAngle = new ArrayList<Vertex>(points.size());
        for (final Vector2D p : points) {
            pointsSortedByAngle.add(new Vertex(p, getAngleBetweenPoints(p, referencePoint)));
        }

        // sort the points in increasing order of their angles
        Collections.sort(pointsSortedByAngle, new Comparator<Vertex>() {
            public int compare(final Vertex o1, final Vertex o2) {
                return (int) FastMath.signum(o2.angle - o1.angle);
            }
        });

        // list containing the vertices of the hull in CCW direction
        final List<Vector2D> hullVertices = new ArrayList<Vector2D>();

        // push the first two points on the stack
        final Iterator<Vertex> it = pointsSortedByAngle.iterator();
        final Vector2D firstPoint = it.next().point;
        hullVertices.add(firstPoint);

        final double tolerance = getTolerance();
        // ensure that we do not add an identical point
        while(hullVertices.size() < 2 && it.hasNext()) {
            final Vector2D p = it.next().point;
            if (firstPoint.distance(p) >= tolerance) {
                hullVertices.add(p);
            }
        }

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

            if (currentPoint == null) {
                currentPoint = it.next().point;
            }

            // test if the current point is to the left of the line
            final double offset = currentPoint.crossProduct(p1, p2);

            if (FastMath.abs(offset) < tolerance) {
                // the point is collinear to the line (p1, p2)

                final double distanceToCurrent = p1.distance(currentPoint);
                if (distanceToCurrent < tolerance || p2.distance(currentPoint) < tolerance) {
                    // the point is assumed to be identical to either p1 or p2
                    currentPoint = null;
                    continue;
                }

                final double distanceToLast = p1.distance(p2);
                if (isIncludeCollinearPoints()) {
                    final int index = distanceToCurrent < distanceToLast ? size - 1 : size;
                    hullVertices.add(index, currentPoint);
                    currentPoint = null;
                } else {
                    if (distanceToCurrent > distanceToLast) {
                        hullVertices.remove(size - 1);
                    } else {
                        currentPoint = null;
                    }
                }
            } else if (offset > 0.0) {
                // the current point forms a convex polygon
                hullVertices.add(currentPoint);
                currentPoint = null;
            } else {
                // the current point creates a concave polygon, remove the last point
                hullVertices.remove(size - 1);
            }
        }

        return hullVertices;
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
    private Vector2D getReferencePoint(final Iterable<Vector2D> points) {
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
