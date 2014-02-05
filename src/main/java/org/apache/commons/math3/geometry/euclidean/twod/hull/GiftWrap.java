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
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.FastMath;

/**
 * Implements the Gift wrapping algorithm to generate the convex hull of a finite set of
 * points in the two-dimensional euclidean space.
 * <p>
 * The implementation is not sensitive to collinear points. The runtime complexity is O(nh),
 * with n being the number of input points and h the number of points on the convex hull.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Gift_wrapping_algorithm">Gift wrapping algorithm (Wikipedia)</a>
 * @since 3.3
 * @version $Id$
 */
public class GiftWrap extends AbstractConvexHullGenerator2D {

    /**
     * Create a new GiftWrap instance.
     * <p>
     * Collinear points on the hull will not be added to the hull vertices and
     * {@code 1e-10} will be used as tolerance criteria for identical points.
     */
    public GiftWrap() {
        super();
    }

    /**
     * Create a new GiftWrap instance.
     * <p>
     * The default tolerance (1e-10) will be used to determine identical points.
     *
     * @param includeCollinearPoints indicates if collinear points on the hull shall be
     * added as hull vertices
     */
    public GiftWrap(final boolean includeCollinearPoints) {
        super(includeCollinearPoints);
    }

    /**
     * Create a new GiftWrap instance.
     *
     * @param includeCollinearPoints indicates if collinear points on the hull shall be
     * added as hull vertices
     * @param tolerance tolerance below which points are considered identical
     */
    public GiftWrap(final boolean includeCollinearPoints, final double tolerance) {
        super(includeCollinearPoints, tolerance);
    }

    @Override
    public Collection<Vector2D> generateHull(final Collection<Vector2D> points) {

        final double tolerance = getTolerance();
        final List<Vector2D> hullVertices = new ArrayList<Vector2D>();
        Vector2D pointOnHull = selectLeftMostPoint(points);
        Vector2D nextPoint;

        do {
            hullVertices.add(pointOnHull);
            final Iterator<Vector2D> it = points.iterator();

            nextPoint = null;
            while(it.hasNext() && nextPoint == null) {
                final Vector2D point = it.next();
                if (pointOnHull.distance(point) >= tolerance) {
                    nextPoint = point;
                }
            }

            while (it.hasNext()) {
                final Vector2D p = it.next();
                final double location = p.crossProduct(pointOnHull, nextPoint);
                if (FastMath.abs(location) < tolerance) {
                    final double distanceToCurrent = pointOnHull.distance(p);
                    if (distanceToCurrent < tolerance || nextPoint.distance(p) < tolerance) {
                        // the point is assumed to be identical to either of pointOnHull or nextPoint
                        continue;
                    }

                    final double distanceToNext = pointOnHull.distance(nextPoint);
                    if (isIncludeCollinearPoints()) {
                        if (distanceToCurrent < distanceToNext) {
                            nextPoint = p;
                        }
                    } else {
                        if (distanceToCurrent > distanceToNext) {
                            nextPoint = p;
                        }
                    }
                } else if (location < 0.0) {
                    nextPoint = p;
                }
            }
            pointOnHull = nextPoint;
        } while (nextPoint != hullVertices.get(0));

        return hullVertices;
    }

    /**
     * Selects the left-most point (minimum x-coordinate) from the set of points.
     * @param points the set of points
     * @return the left-most point
     */
    private Vector2D selectLeftMostPoint(final Collection<Vector2D> points) {
        Vector2D leftMost = null;
        for (final Vector2D p : points) {
            if (leftMost == null || p.getX() < leftMost.getX()) {
                leftMost = p;
            }
        }
        return leftMost;
    }

}
