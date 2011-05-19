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
package org.apache.commons.math.geometry.euclidean.threed;

import org.apache.commons.math.geometry.euclidean.twod.Point2D;
import org.apache.commons.math.geometry.euclidean.twod.PolygonsSet;
import org.apache.commons.math.geometry.partitioning.BSPTree;
import org.apache.commons.math.geometry.partitioning.BSPTreeVisitor;
import org.apache.commons.math.geometry.partitioning.Region;
import org.apache.commons.math.geometry.partitioning.SubHyperplane;
import org.apache.commons.math.util.FastMath;

import java.util.ArrayList;

/** Extractor for {@link PolygonsSet polyhedrons sets} outlines.
 * <p>This class extracts the 2D outlines from {{@link PolygonsSet
 * polyhedrons sets} in a specified projection plane.</p>
 * @version $Revision$ $Date$
 */
public class OutlineExtractor {

    /** Abscissa axis of the projection plane. */
    private Vector3D u;

    /** Ordinate axis of the projection plane. */
    private Vector3D v;

    /** Normal of the projection plane (viewing direction). */
    private Vector3D w;

    /** Build an extractor for a specific projection plane.
     * @param u abscissa axis of the projection point
     * @param v ordinate axis of the projection point
     */
    public OutlineExtractor(final Vector3D u, final Vector3D v) {
        this.u = u;
        this.v = v;
        w = Vector3D.crossProduct(u, v);
    }

    /** Extract the outline of a polyhedrons set.
     * @param polyhedronsSet polyhedrons set whose outline must be extracted
     * @return an outline, as an array of loops.
     */
    public Point2D[][] getOutline(final PolyhedronsSet polyhedronsSet) {

        // project all boundary facets into one polygons set
        final BoundaryProjector projector = new BoundaryProjector();
        polyhedronsSet.getTree(true).visit(projector);
        final PolygonsSet projected = projector.getProjected();

        // Remove the spurious intermediate vertices from the outline
        final Point2D[][] outline = projected.getVertices();
        for (int i = 0; i < outline.length; ++i) {
            final Point2D[] rawLoop = outline[i];
            int end = rawLoop.length;
            int j = 0;
            while (j < end) {
                if (pointIsBetween(rawLoop, end, j)) {
                    // the point should be removed
                    for (int k = j; k < (end - 1); ++k) {
                        rawLoop[k] = rawLoop[k + 1];
                    }
                    --end;
                } else {
                    // the point remains in the loop
                    ++j;
                }
            }
            if (end != rawLoop.length) {
                // resize the array
                outline[i] = new Point2D[end];
                System.arraycopy(rawLoop, 0, outline[i], 0, end);
            }
        }

        return outline;

    }

    /** Check if a point is geometrically between its neighbour in an array.
     * <p>The neighbours are computed considering the array is a loop
     * (i.e. point at index (n-1) is before point at index 0)</p>
     * @param loop points array
     * @param n number of points to consider in the array
     * @param i index of the point to check (must be between 0 and n-1)
     * @return true if the point is exactly between its neighbours
     */
    private boolean pointIsBetween(final Point2D[] loop, final int n, final int i) {
        final Point2D previous = loop[(i + n - 1) % n];
        final Point2D current  = loop[i];
        final Point2D next     = loop[(i + 1) % n];
        final double dx1       = current.x - previous.x;
        final double dy1       = current.y - previous.y;
        final double dx2       = next.x    - current.x;
        final double dy2       = next.y    - current.y;
        final double cross     = dx1 * dy2 - dx2 * dy1;
        final double dot       = dx1 * dx2 + dy1 * dy2;
        final double d1d2      = FastMath.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2));
        return (FastMath.abs(cross) <= (1.0e-6 * d1d2)) && (dot >= 0.0);
    }

    /** Visitor projecting the boundary facets on a plane. */
    private class BoundaryProjector implements BSPTreeVisitor {

        /** Projection of the polyhedrons set on the plane. */
        private PolygonsSet projected;

        /** Simple constructor.
         */
        public BoundaryProjector() {
            projected = new PolygonsSet(new BSPTree(Boolean.FALSE));
        }

        /** {@inheritDoc} */
        public Order visitOrder(final BSPTree node) {
            return Order.MINUS_SUB_PLUS;
        }

        /** {@inheritDoc} */
        public void visitInternalNode(final BSPTree node) {
            final Region.BoundaryAttribute attribute =
                (Region.BoundaryAttribute) node.getAttribute();
            if (attribute.getPlusOutside() != null) {
                addContribution(attribute.getPlusOutside(), false);
            }
            if (attribute.getPlusInside() != null) {
                addContribution(attribute.getPlusInside(), true);
            }
        }

        /** {@inheritDoc} */
        public void visitLeafNode(final BSPTree node) {
        }

        /** Add he contribution of a boundary facet.
         * @param facet boundary facet
         * @param reversed if true, the facet has the inside on its plus side
         */
        private void addContribution(final SubHyperplane facet, final boolean reversed) {

            // extract the vertices of the facet
            final Plane plane    = (Plane) facet.getHyperplane();
            Point2D[][] vertices =
                ((PolygonsSet) facet.getRemainingRegion()).getVertices();

            final double scal = Vector3D.dotProduct(plane.getNormal(), w);
            if (FastMath.abs(scal) > 1.0e-3) {

                if ((scal < 0) ^ reversed) {
                    // the facet is seen from the inside,
                    // we need to invert its boundary orientation
                    final Point2D[][] newVertices = new Point2D[vertices.length][];
                    for (int i = 0; i < vertices.length; ++i) {
                        final Point2D[] loop = vertices[i];
                        final Point2D[] newLoop = new Point2D[loop.length];
                        if (loop[0] == null) {
                            newLoop[0] = null;
                            for (int j = 1; j < loop.length; ++j) {
                                newLoop[j] = loop[loop.length - j];
                            }
                        } else {
                            for (int j = 0; j < loop.length; ++j) {
                                newLoop[j] = loop[loop.length - (j + 1)];
                            }
                        }
                        newVertices[i] = newLoop;
                    }

                    // use the reverted vertices
                    vertices = newVertices;

                }

                // compute the projection of the facet in the outline plane
                final ArrayList<SubHyperplane> edges = new ArrayList<SubHyperplane>();
                for (Point2D[] loop : vertices) {
                    final boolean closed = loop[0] != null;
                    int previous         = closed ? (loop.length - 1) : 1;
                    Vector3D previous3D  = (Vector3D) plane.toSpace(loop[previous]);
                    int current          = (previous + 1) % loop.length;
                    Point2D pPoint       = new Point2D(Vector3D.dotProduct(previous3D, u),
                                                       Vector3D.dotProduct(previous3D, v));
                    while (current < loop.length) {

                        final Vector3D current3D = (Vector3D) plane.toSpace(loop[current]);
                        final Point2D  cPoint    = new Point2D(Vector3D.dotProduct(current3D, u),
                                                               Vector3D.dotProduct(current3D, v));
                        final org.apache.commons.math.geometry.euclidean.twod.Line line =
                            new org.apache.commons.math.geometry.euclidean.twod.Line(pPoint, cPoint);
                        SubHyperplane edge = new SubHyperplane(line);

                        if (closed || (previous != 1)) {
                            // the previous point is a real vertex
                            // it defines one bounding point of the edge
                            final double angle = line.getAngle() + 0.5 * FastMath.PI;
                            final org.apache.commons.math.geometry.euclidean.twod.Line l =
                                new org.apache.commons.math.geometry.euclidean.twod.Line(pPoint, angle);
                            edge = l.split(edge).getPlus();
                        }

                        if (closed || (current != (loop.length - 1))) {
                            // the current point is a real vertex
                            // it defines one bounding point of the edge
                            final double angle = line.getAngle() + 0.5 * FastMath.PI;
                            final org.apache.commons.math.geometry.euclidean.twod.Line l =
                                new org.apache.commons.math.geometry.euclidean.twod.Line(cPoint, angle);
                            edge = l.split(edge).getMinus();
                        }

                        edges.add(edge);

                        previous   = current++;
                        previous3D = current3D;
                        pPoint     = cPoint;

                    }
                }
                final PolygonsSet projectedFacet = new PolygonsSet(edges);

                // add the contribution of the facet to the global outline
                projected = (PolygonsSet) Region.union(projected, projectedFacet);

            }
        }

        /** Get the projecion of the polyhedrons set on the plane.
         * @return projecion of the polyhedrons set on the plane
         */
        public PolygonsSet getProjected() {
            return projected;
        }

    }

}
