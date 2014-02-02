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

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.math3.exception.InsufficientDataException;
import org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Segment;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.hull.ConvexHull;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.RegionFactory;

/**
 * This class represents a convex hull in an two-dimensional euclidean space.
 *
 * @version $Id$
 * @since 3.3
 */
public class ConvexHull2D implements ConvexHull<Euclidean2D, Vector2D>, Serializable {

    /** Serializable UID. */
    private static final long serialVersionUID = 20140129L;

    /** Vertices of the hull. */
    private final Vector2D[] vertices;

    /** Line segments of the hull. */
    private final Segment[] lineSegments;

    /**
     * Simple constructor.
     * @param vertices the vertices of the convex hull, must be ordered in CCW winding
     * @param tolerance tolerance below which points are considered identical
     */
    ConvexHull2D(final Collection<Vector2D> vertices, final double tolerance) {
        this.vertices = vertices.toArray(new Vector2D[vertices.size()]);

        // construct the line segments - handle special cases of 1 or 2 points
        final int size = vertices.size();
        if (size <= 1) {
            this.lineSegments = new Segment[0];
        } else if (size == 2) {
            this.lineSegments = new Segment[1];
            final Iterator<Vector2D> it = vertices.iterator();
            final Vector2D p1 = it.next();
            final Vector2D p2 = it.next();
            this.lineSegments[0] = new Segment(p1, p2, new Line(p1, p2, tolerance));
        } else {
            this.lineSegments = new Segment[size];
            Vector2D firstPoint = null;
            Vector2D lastPoint = null;
            int index = 0;
            for (Vector2D point : vertices) {
                if (lastPoint == null) {
                    firstPoint = point;
                    lastPoint = point;
                } else {
                    this.lineSegments[index++] =
                            new Segment(lastPoint, point, new Line(lastPoint, point, tolerance));
                    lastPoint = point;
                }
            }
            this.lineSegments[index] =
                    new Segment(lastPoint, firstPoint, new Line(lastPoint, firstPoint, tolerance));
        }
    }

    /** {@inheritDoc} */
    public Vector2D[] getVertices() {
        return vertices.clone();
    }

    /**
     * Get the line segments of the convex hull, ordered in CCW winding.
     * @return the line segments of the convex hull
     */
    public Segment[] getLineSegments() {
        return lineSegments.clone();
    }

    /** {@inheritDoc} */
    public Region<Euclidean2D> createRegion() throws InsufficientDataException {
        if (vertices.length < 3) {
            throw new InsufficientDataException();
        }
        final RegionFactory<Euclidean2D> factory = new RegionFactory<Euclidean2D>();
        final Line[] lineArray = new Line[lineSegments.length];
        for (int i = 0; i < lineSegments.length; i++) {
            lineArray[i] = lineSegments[i].getLine();
        }
        return factory.buildConvex(lineArray);
    }
}
