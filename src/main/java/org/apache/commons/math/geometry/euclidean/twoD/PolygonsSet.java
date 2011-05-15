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
package org.apache.commons.math.geometry.euclidean.twoD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.math.geometry.euclidean.oneD.Point1D;
import org.apache.commons.math.geometry.partitioning.BSPTree;
import org.apache.commons.math.geometry.partitioning.Hyperplane;
import org.apache.commons.math.geometry.partitioning.Region;
import org.apache.commons.math.geometry.partitioning.SubHyperplane;
import org.apache.commons.math.geometry.partitioning.utilities.AVLTree;
import org.apache.commons.math.util.FastMath;

/** This class represents a 2D region: a set of polygons.
 * @version $Revision$ $Date$
 */
public class PolygonsSet extends Region {

    /** Vertices organized as boundary loops. */
    private Point2D[][] vertices;

    /** Build a polygons set representing the whole real line.
     */
    public PolygonsSet() {
        super();
    }

    /** Build a polygons set from a BSP tree.
     * <p>The leaf nodes of the BSP tree <em>must</em> have a
     * {@code Boolean} attribute representing the inside status of
     * the corresponding cell (true for inside cells, false for outside
     * cells). In order to avoid building too many small objects, it is
     * recommended to use the predefined constants
     * {@code Boolean.TRUE} and {@code Boolean.FALSE}</p>
     * @param tree inside/outside BSP tree representing the region
     */
    public PolygonsSet(final BSPTree tree) {
        super(tree);
    }

    /** Build a polygons set from a Boundary REPresentation (B-rep).
     * <p>The boundary is provided as a collection of {@link
     * SubHyperplane sub-hyperplanes}. Each sub-hyperplane has the
     * interior part of the region on its minus side and the exterior on
     * its plus side.</p>
     * <p>The boundary elements can be in any order, and can form
     * several non-connected sets (like for example polygons with holes
     * or a set of disjoints polyhedrons considered as a whole). In
     * fact, the elements do not even need to be connected together
     * (their topological connections are not used here). However, if the
     * boundary does not really separate an inside open from an outside
     * open (open having here its topological meaning), then subsequent
     * calls to the {@link
     * Region#checkPoint(org.apache.commons.math.geometry.partitioning.Point)
     * checkPoint} method will not be meaningful anymore.</p>
     * <p>If the boundary is empty, the region will represent the whole
     * space.</p>
     * @param boundary collection of boundary elements, as a
     * collection of {@link SubHyperplane SubHyperplane} objects
     */
    public PolygonsSet(final Collection<SubHyperplane> boundary) {
        super(boundary);
    }

    /** Build a parallellepipedic box.
     * @param xMin low bound along the x direction
     * @param xMax high bound along the x direction
     * @param yMin low bound along the y direction
     * @param yMax high bound along the y direction
     */
    public PolygonsSet(final double xMin, final double xMax,
                       final double yMin, final double yMax) {
        this(buildConvex(boxBoundary(xMin, xMax, yMin, yMax)).getTree(false));
    }

    /** Create a list of hyperplanes representing the boundary of a box.
     * @param xMin low bound along the x direction
     * @param xMax high bound along the x direction
     * @param yMin low bound along the y direction
     * @param yMax high bound along the y direction
     * @return boundary of the box
     */
    private static List<Hyperplane> boxBoundary(final double xMin, final double xMax,
                                                final double yMin, final double yMax) {
        final Point2D minMin = new Point2D(xMin, yMin);
        final Point2D minMax = new Point2D(xMin, yMax);
        final Point2D maxMin = new Point2D(xMax, yMin);
        final Point2D maxMax = new Point2D(xMax, yMax);
        return Arrays.asList(new Hyperplane[] {
            new Line(minMin, maxMin),
            new Line(maxMin, maxMax),
            new Line(maxMax, minMax),
            new Line(minMax, minMin)
        });
    }

    /** {@inheritDoc} */
    public Region buildNew(final BSPTree tree) {
        return new PolygonsSet(tree);
    }

    /** {@inheritDoc} */
    protected void computeGeometricalProperties() {

        final Point2D[][] v = getVertices();

        if (v.length == 0) {
            if ((Boolean) getTree(false).getAttribute()) {
                setSize(Double.POSITIVE_INFINITY);
                setBarycenter(Point2D.UNDEFINED);
            } else {
                setSize(0);
                setBarycenter(new Point2D(0, 0));
            }
        } else if (v[0][0] == null) {
            // there is at least one open-loop: the polygon is infinite
            setSize(Double.POSITIVE_INFINITY);
            setBarycenter(Point2D.UNDEFINED);
        } else {
            // all loops are closed, we compute some integrals around the shape

            double sum  = 0;
            double sumX = 0;
            double sumY = 0;

            for (Point2D[] loop : v) {
                double x1 = loop[loop.length - 1].x;
                double y1 = loop[loop.length - 1].y;
                for (final Point2D point : loop) {
                    final double x0 = x1;
                    final double y0 = y1;
                    x1 = point.x;
                    y1 = point.y;
                    final double factor = x0 * y1 - y0 * x1;
                    sum  += factor;
                    sumX += factor * (x0 + x1);
                    sumY += factor * (y0 + y1);
                }
            }

            if (sum < 0) {
                // the polygon as a finite outside surrounded by an infinite inside
                setSize(Double.POSITIVE_INFINITY);
                setBarycenter(Point2D.UNDEFINED);
            } else {
                setSize(sum / 2);
                setBarycenter(new Point2D(sumX / (3 * sum), sumY / (3 * sum)));
            }

        }

    }

    /** Get the vertices of the polygon.
     * <p>The polygon boundary can be represented as an array of loops,
     * each loop being itself an array of vertices.</p>
     * <p>In order to identify open loops which start and end by
     * infinite edges, the open loops arrays start with a null point. In
     * this case, the first non null point and the last point of the
     * array do not represent real vertices, they are dummy points
     * intended only to get the direction of the first and last edge. An
     * open loop consisting of a single infinite line will therefore be
     * represented by a three elements array with one null point
     * followed by two dummy points. The open loops are always the first
     * ones in the loops array.</p>
     * <p>If the polygon has no boundary at all, a zero length loop
     * array will be returned.</p>
     * <p>All line segments in the various loops have the inside of the
     * region on their left side and the outside on their right side
     * when moving in the underlying line direction. This means that
     * closed loops surrounding finite areas obey the direct
     * trigonometric orientation.</p>
     * @return vertices of the polygon, organized as oriented boundary
     * loops with the open loops first (the returned value is guaranteed
     * to be non-null)
     */
    public Point2D[][] getVertices() {
        if (vertices == null) {
            if (getTree(false).getCut() == null) {
                vertices = new Point2D[0][];
            } else {

                // sort the segmfinal ents according to their start point
                final SegmentsBuilder visitor = new SegmentsBuilder();
                getTree(true).visit(visitor);
                final AVLTree<Segment> sorted = visitor.getSorted();

                // identify the loops, starting from the open ones
                // (their start segments final are naturally at the sorted set beginning)
                final ArrayList<List<Segment>> loops = new ArrayList<List<Segment>>();
                while (!sorted.isEmpty()) {
                    final AVLTree<Segment>.Node node = sorted.getSmallest();
                    final List<Segment> loop = followLoop(node, sorted);
                    if (loop != null) {
                        loops.add(loop);
                    }
                }

                // tranform the loops in an array of arrays of points
                vertices = new Point2D[loops.size()][];
                int i = 0;

                for (final List<Segment> loop : loops) {
                    if (loop.size() < 2) {
                        // sifinal ngle infinite line
                        final Line line = ((Segment) loop.get(0)).getLine();
                        vertices[i++] = new Point2D[] {
                            null,
                            (Point2D) line.toSpace(new Point1D(-Float.MAX_VALUE)),
                            (Point2D) line.toSpace(new Point1D(+Float.MAX_VALUE))
                        };
                    } else if (((Segment) loop.get(0)).getStart() == null) {
                        // open lofinal op with at least one real point
                        final Point2D[] array = new Point2D[loop.size() + 2];
                        int j = 0;
                        for (Segment segment : loop) {

                            if (j == 0) {
                                // null point and first dummy point
                                double x =
                                    ((Point1D) segment.getLine().toSubSpace(segment.getEnd())).getAbscissa();
                                x -= FastMath.max(1.0, FastMath.abs(x / 2));
                                array[j++] = null;
                                array[j++] = (Point2D) segment.getLine().toSpace(new Point1D(x));
                            }

                            if (j < (array.length - 1)) {
                                // current point
                                array[j++] = segment.getEnd();
                            }

                            if (j == (array.length - 1)) {
                                // last dummy point
                                double x =
                                    ((Point1D) segment.getLine().toSubSpace(segment.getStart())).getAbscissa();
                                x += FastMath.max(1.0, FastMath.abs(x / 2));
                                array[j++] = (Point2D) segment.getLine().toSpace(new Point1D(x));
                            }

                        }
                        vertices[i++] = array;
                    } else {
                        final Point2D[] array = new Point2D[loop.size()];
                        int j = 0;
                        for (Segment segment : loop) {
                            array[j++] = segment.getStart();
                        }
                        vertices[i++] = array;
                    }
                }

            }
        }

        return vertices.clone();

    }

    /** Follow a boundary loop.
     * @param node node containing the segment starting the loop
     * @param sorted set of segments belonging to the boundary, sorted by
     * start points (contains {@code node})
     * @return a list of connected sub-hyperplanes starting at
     * {@code node}
     */
    private List<Segment> followLoop(final AVLTree<Segment>.Node node,
                                     final AVLTree<Segment> sorted) {

        final ArrayList<Segment> loop = new ArrayList<Segment>();
        Segment segment = (Segment) node.getElement();
        loop.add(segment);
        final Point2D globalStart = segment.getStart();
        Point2D end = segment.getEnd();
        node.delete();

        // is this an open or a closed loop ?
        final boolean open = segment.getStart() == null;

        while ((end != null) && (open || (globalStart.distance(end) > 1.0e-10))) {

            // search the sub-hyperplane starting where the previous one ended
            AVLTree<Segment>.Node selectedNode = null;
            Segment       selectedSegment  = null;
            double        selectedDistance = Double.POSITIVE_INFINITY;
            final Segment lowerLeft        = new Segment(end, -1.0e-10, -1.0e-10);
            final Segment upperRight       = new Segment(end, +1.0e-10, +1.0e-10);
            for (AVLTree<Segment>.Node n = sorted.getNotSmaller(lowerLeft);
                 (n != null) && (n.getElement().compareTo(upperRight) <= 0);
                 n = n.getNext()) {
                segment = (Segment) n.getElement();
                final double distance = end.distance(segment.getStart());
                if (distance < selectedDistance) {
                    selectedNode     = n;
                    selectedSegment  = segment;
                    selectedDistance = distance;
                }
            }

            if (selectedDistance > 1.0e-10) {
                // this is a degenerated loop, it probably comes from a very
                // tiny region with some segments smaller than the threshold, we
                // simply ignore it
                return null;
            }

            end = selectedSegment.getEnd();
            loop.add(selectedSegment);
            selectedNode.delete();

        }

        if ((loop.size() == 2) && !open) {
            // this is a degenerated infinitely thin loop, we simply ignore it
            return null;
        }

        if ((end == null) && !open) {
            throw new RuntimeException("internal error");
        }

        return loop;

    }

}
