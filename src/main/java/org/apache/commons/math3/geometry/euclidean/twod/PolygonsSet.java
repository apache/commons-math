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
package org.apache.commons.math3.geometry.euclidean.twod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math3.geometry.euclidean.oned.Interval;
import org.apache.commons.math3.geometry.euclidean.oned.IntervalsSet;
import org.apache.commons.math3.geometry.euclidean.oned.Vector1D;
import org.apache.commons.math3.geometry.partitioning.AbstractRegion;
import org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor;
import org.apache.commons.math3.geometry.partitioning.BoundaryAttribute;
import org.apache.commons.math3.geometry.partitioning.Side;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.geometry.partitioning.utilities.AVLTree;
import org.apache.commons.math3.geometry.partitioning.utilities.OrderedTuple;
import org.apache.commons.math3.util.FastMath;

/** This class represents a 2D region: a set of polygons.
 * @version $Id$
 * @since 3.0
 */
public class PolygonsSet extends AbstractRegion<Euclidean2D, Euclidean1D> {

    /** Vertices organized as boundary loops. */
    private Vector2D[][] vertices;

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
    public PolygonsSet(final BSPTree<Euclidean2D> tree) {
        super(tree);
    }

    /** Build a polygons set from a Boundary REPresentation (B-rep).
     * <p>The boundary is provided as a collection of {@link
     * SubHyperplane sub-hyperplanes}. Each sub-hyperplane has the
     * interior part of the region on its minus side and the exterior on
     * its plus side.</p>
     * <p>The boundary elements can be in any order, and can form
     * several non-connected sets (like for example polygons with holes
     * or a set of disjoint polyhedrons considered as a whole). In
     * fact, the elements do not even need to be connected together
     * (their topological connections are not used here). However, if the
     * boundary does not really separate an inside open from an outside
     * open (open having here its topological meaning), then subsequent
     * calls to the {@link
     * org.apache.commons.math3.geometry.partitioning.Region#checkPoint(org.apache.commons.math3.geometry.Vector)
     * checkPoint} method will not be meaningful anymore.</p>
     * <p>If the boundary is empty, the region will represent the whole
     * space.</p>
     * @param boundary collection of boundary elements, as a
     * collection of {@link SubHyperplane SubHyperplane} objects
     */
    public PolygonsSet(final Collection<SubHyperplane<Euclidean2D>> boundary) {
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
        super(boxBoundary(xMin, xMax, yMin, yMax));
    }

    /** Build a polygon from a simple list of vertices.
     * <p>The boundary is provided as a list of points considering to
     * represent the vertices of a simple loop. The interior part of the
     * region is on the left side of this path and the exterior is on its
     * right side.</p>
     * <p>This constructor does not handle polygons with a boundary
     * forming several disconnected paths (such as polygons with holes).</p>
     * <p>For cases where this simple constructor applies, it is expected to
     * be numerically more robust than the {@link #PolygonsSet(Collection) general
     * constructor} using {@link SubHyperplane subhyperplanes}.</p>
     * <p>If the list is empty, the region will represent the whole
     * space.</p>
     * <p>
     * Polygons with thin pikes or dents are inherently difficult to handle because
     * they involve lines with almost opposite directions at some vertices. Polygons
     * whose vertices come from some physical measurement with noise are also
     * difficult because an edge that should be straight may be broken in lots of
     * different pieces with almost equal directions. In both cases, computing the
     * lines intersections is not numerically robust due to the almost 0 or almost
     * &pi; angle. Such cases need to carefully adjust the {@code hyperplaneThickness}
     * parameter. A too small value would often lead to completely wrong polygons
     * with large area wrongly identified as inside or outside. Large values are
     * often much safer. As a rule of thumb, a value slightly below the size of the
     * most accurate detail needed is a good value for the {@code hyperplaneThickness}
     * parameter.
     * </p>
     * @param hyperplaneThickness tolerance below which points are considered to
     * belong to the hyperplane (which is therefore more a slab)
     * @param vertices vertices of the simple loop boundary
     * @since 3.1
     */
    public PolygonsSet(final double hyperplaneThickness, final Vector2D ... vertices) {
        super(verticesToTree(hyperplaneThickness, vertices));
    }

    /** Create a list of hyperplanes representing the boundary of a box.
     * @param xMin low bound along the x direction
     * @param xMax high bound along the x direction
     * @param yMin low bound along the y direction
     * @param yMax high bound along the y direction
     * @return boundary of the box
     */
    private static Line[] boxBoundary(final double xMin, final double xMax,
                                      final double yMin, final double yMax) {
        final Vector2D minMin = new Vector2D(xMin, yMin);
        final Vector2D minMax = new Vector2D(xMin, yMax);
        final Vector2D maxMin = new Vector2D(xMax, yMin);
        final Vector2D maxMax = new Vector2D(xMax, yMax);
        return new Line[] {
            new Line(minMin, maxMin),
            new Line(maxMin, maxMax),
            new Line(maxMax, minMax),
            new Line(minMax, minMin)
        };
    }

    /** Build the BSP tree of a polygons set from a simple list of vertices.
     * <p>The boundary is provided as a list of points considering to
     * represent the vertices of a simple loop. The interior part of the
     * region is on the left side of this path and the exterior is on its
     * right side.</p>
     * <p>This constructor does not handle polygons with a boundary
     * forming several disconnected paths (such as polygons with holes).</p>
     * <p>For cases where this simple constructor applies, it is expected to
     * be numerically more robust than the {@link #PolygonsSet(Collection) general
     * constructor} using {@link SubHyperplane subhyperplanes}.</p>
     * @param hyperplaneThickness tolerance below which points are consider to
     * belong to the hyperplane (which is therefore more a slab)
     * @param vertices vertices of the simple loop boundary
     * @return the BSP tree of the input vertices
     */
    private static BSPTree<Euclidean2D> verticesToTree(final double hyperplaneThickness,
                                                       final Vector2D ... vertices) {

        final int n = vertices.length;
        if (n == 0) {
            // the tree represents the whole space
            return new BSPTree<Euclidean2D>(Boolean.TRUE);
        }

        // build the vertices
        final Vertex[] vArray = new Vertex[n];
        for (int i = 0; i < n; ++i) {
            vArray[i] = new Vertex(vertices[i]);
        }

        // build the edges
        List<Edge> edges = new ArrayList<Edge>();
        for (int i = 0; i < n; ++i) {

            // get the endpoints of the edge
            final Vertex start = vArray[i];
            final Vertex end   = vArray[(i + 1) % n];

            // get the line supporting the edge, taking care not to recreate it
            // if it was already created earlier due to another edge being aligned
            // with the current one
            Line line = start.sharedLineWith(end);
            if (line == null) {
                line = new Line(start.getLocation(), end.getLocation());
            }

            // create the edge and store it
            edges.add(new Edge(start, end, line));

            // check if another vertex also happens to be on this line
            for (final Vertex vertex : vArray) {
                if (vertex != start && vertex != end &&
                    FastMath.abs(line.getOffset(vertex.getLocation())) <= hyperplaneThickness) {
                    vertex.bindWith(line);
                }
            }

        }

        // build the tree top-down
        final BSPTree<Euclidean2D> tree = new BSPTree<Euclidean2D>();
        insertEdges(hyperplaneThickness, tree, edges);

        return tree;

    }

    /** Recursively build a tree by inserting cut sub-hyperplanes.
     * @param hyperplaneThickness tolerance below which points are consider to
     * belong to the hyperplane (which is therefore more a slab)
     * @param node current tree node (it is a leaf node at the beginning
     * of the call)
     * @param edges list of edges to insert in the cell defined by this node
     * (excluding edges not belonging to the cell defined by this node)
     */
    private static void insertEdges(final double hyperplaneThickness,
                                    final BSPTree<Euclidean2D> node,
                                    final List<Edge> edges) {

        // find an edge with an hyperplane that can be inserted in the node
        int index = 0;
        Edge inserted =null;
        while (inserted == null && index < edges.size()) {
            inserted = edges.get(index++);
            if (inserted.getNode() == null) {
                if (node.insertCut(inserted.getLine())) {
                    inserted.setNode(node);
                } else {
                    inserted = null;
                }
            } else {
                inserted = null;
            }
        }

        if (inserted == null) {
            // no suitable edge was found, the node remains a leaf node
            // we need to set its inside/outside boolean indicator
            final BSPTree<Euclidean2D> parent = node.getParent();
            if (parent == null || node == parent.getMinus()) {
                node.setAttribute(Boolean.TRUE);
            } else {
                node.setAttribute(Boolean.FALSE);
            }
            return;
        }

        // we have split the node by inserted an edge as a cut sub-hyperplane
        // distribute the remaining edges in the two sub-trees
        final List<Edge> plusList  = new ArrayList<Edge>();
        final List<Edge> minusList = new ArrayList<Edge>();
        for (final Edge edge : edges) {
            if (edge != inserted) {
                final double startOffset = inserted.getLine().getOffset(edge.getStart().getLocation());
                final double endOffset   = inserted.getLine().getOffset(edge.getEnd().getLocation());
                Side startSide = (FastMath.abs(startOffset) <= hyperplaneThickness) ?
                                 Side.HYPER : ((startOffset < 0) ? Side.MINUS : Side.PLUS);
                Side endSide   = (FastMath.abs(endOffset) <= hyperplaneThickness) ?
                                 Side.HYPER : ((endOffset < 0) ? Side.MINUS : Side.PLUS);
                switch (startSide) {
                    case PLUS:
                        if (endSide == Side.MINUS) {
                            // we need to insert a split point on the hyperplane
                            final Vertex splitPoint = edge.split(inserted.getLine());
                            minusList.add(splitPoint.getOutgoing());
                            plusList.add(splitPoint.getIncoming());
                        } else {
                            plusList.add(edge);
                        }
                        break;
                    case MINUS:
                        if (endSide == Side.PLUS) {
                            // we need to insert a split point on the hyperplane
                            final Vertex splitPoint = edge.split(inserted.getLine());
                            minusList.add(splitPoint.getIncoming());
                            plusList.add(splitPoint.getOutgoing());
                        } else {
                            minusList.add(edge);
                        }
                        break;
                    default:
                        if (endSide == Side.PLUS) {
                            plusList.add(edge);
                        } else if (endSide == Side.MINUS) {
                            minusList.add(edge);
                        }
                        break;
                }
            }
        }

        // recurse through lower levels
        if (!plusList.isEmpty()) {
            insertEdges(hyperplaneThickness, node.getPlus(),  plusList);
        } else {
            node.getPlus().setAttribute(Boolean.FALSE);
        }
        if (!minusList.isEmpty()) {
            insertEdges(hyperplaneThickness, node.getMinus(), minusList);
        } else {
            node.getMinus().setAttribute(Boolean.TRUE);
        }

    }

    /** Internal class for holding vertices while they are processed to build a BSP tree. */
    private static class Vertex {

        /** Vertex location. */
        private final Vector2D location;

        /** Incoming edge. */
        private Edge incoming;

        /** Outgoing edge. */
        private Edge outgoing;

        /** Lines bound with this vertex. */
        private final List<Line> lines;

        /** Build a non-processed vertex not owned by any node yet.
         * @param location vertex location
         */
        public Vertex(final Vector2D location) {
            this.location = location;
            this.incoming = null;
            this.outgoing = null;
            this.lines    = new ArrayList<Line>();
        }

        /** Get Vertex location.
         * @return vertex location
         */
        public Vector2D getLocation() {
            return location;
        }

        /** Bind a line considered to contain this vertex.
         * @param line line to bind with this vertex
         */
        public void bindWith(final Line line) {
            lines.add(line);
        }

        /** Get the common line bound with both the instance and another vertex, if any.
         * <p>
         * When two vertices are both bound to the same line, this means they are
         * already handled by node associated with this line, so there is no need
         * to create a cut hyperplane for them.
         * </p>
         * @param vertex other vertex to check instance against
         * @return line bound with both the instance and another vertex, or null if the
         * two vertices do not share a line yet
         */
        public Line sharedLineWith(final Vertex vertex) {
            for (final Line line1 : lines) {
                for (final Line line2 : vertex.lines) {
                    if (line1 == line2) {
                        return line1;
                    }
                }
            }
            return null;
        }

        /** Set incoming edge.
         * <p>
         * The line supporting the incoming edge is automatically bound
         * with the instance.
         * </p>
         * @param incoming incoming edge
         */
        public void setIncoming(final Edge incoming) {
            this.incoming = incoming;
            bindWith(incoming.getLine());
        }

        /** Get incoming edge.
         * @return incoming edge
         */
        public Edge getIncoming() {
            return incoming;
        }

        /** Set outgoing edge.
         * <p>
         * The line supporting the outgoing edge is automatically bound
         * with the instance.
         * </p>
         * @param outgoing outgoing edge
         */
        public void setOutgoing(final Edge outgoing) {
            this.outgoing = outgoing;
            bindWith(outgoing.getLine());
        }

        /** Get outgoing edge.
         * @return outgoing edge
         */
        public Edge getOutgoing() {
            return outgoing;
        }

    }

    /** Internal class for holding edges while they are processed to build a BSP tree. */
    private static class Edge {

        /** Start vertex. */
        private final Vertex start;

        /** End vertex. */
        private final Vertex end;

        /** Line supporting the edge. */
        private final Line line;

        /** Node whose cut hyperplane contains this edge. */
        private BSPTree<Euclidean2D> node;

        /** Build an edge not contained in any node yet.
         * @param start start vertex
         * @param end end vertex
         * @param line line supporting the edge
         */
        public Edge(final Vertex start, final Vertex end, final Line line) {

            this.start = start;
            this.end   = end;
            this.line  = line;
            this.node  = null;

            // connect the vertices back to the edge
            start.setOutgoing(this);
            end.setIncoming(this);

        }

        /** Get start vertex.
         * @return start vertex
         */
        public Vertex getStart() {
            return start;
        }

        /** Get end vertex.
         * @return end vertex
         */
        public Vertex getEnd() {
            return end;
        }

        /** Get the line supporting this edge.
         * @return line supporting this edge
         */
        public Line getLine() {
            return line;
        }

        /** Set the node whose cut hyperplane contains this edge.
         * @param node node whose cut hyperplane contains this edge
         */
        public void setNode(final BSPTree<Euclidean2D> node) {
            this.node = node;
        }

        /** Get the node whose cut hyperplane contains this edge.
         * @return node whose cut hyperplane contains this edge
         * (null if edge has not yet been inserted into the BSP tree)
         */
        public BSPTree<Euclidean2D> getNode() {
            return node;
        }

        /** Split the edge.
         * <p>
         * Once split, this edge is not referenced anymore by the vertices,
         * it is replaced by the two half-edges and an intermediate splitting
         * vertex is introduced to connect these two halves.
         * </p>
         * @param splitLine line splitting the edge in two halves
         * @return split vertex (its incoming and outgoing edges are the two halves)
         */
        public Vertex split(final Line splitLine) {
            final Vertex splitVertex = new Vertex(line.intersection(splitLine));
            splitVertex.bindWith(splitLine);
            final Edge startHalf = new Edge(start, splitVertex, line);
            final Edge endHalf   = new Edge(splitVertex, end, line);
            startHalf.node = node;
            endHalf.node   = node;
            return splitVertex;
        }

    }

    /** {@inheritDoc} */
    @Override
    public PolygonsSet buildNew(final BSPTree<Euclidean2D> tree) {
        return new PolygonsSet(tree);
    }

    /** {@inheritDoc} */
    @Override
    protected void computeGeometricalProperties() {

        final Vector2D[][] v = getVertices();

        if (v.length == 0) {
            final BSPTree<Euclidean2D> tree = getTree(false);
            if (tree.getCut() == null && (Boolean) tree.getAttribute()) {
                // the instance covers the whole space
                setSize(Double.POSITIVE_INFINITY);
                setBarycenter(Vector2D.NaN);
            } else {
                setSize(0);
                setBarycenter(new Vector2D(0, 0));
            }
        } else if (v[0][0] == null) {
            // there is at least one open-loop: the polygon is infinite
            setSize(Double.POSITIVE_INFINITY);
            setBarycenter(Vector2D.NaN);
        } else {
            // all loops are closed, we compute some integrals around the shape

            double sum  = 0;
            double sumX = 0;
            double sumY = 0;

            for (Vector2D[] loop : v) {
                double x1 = loop[loop.length - 1].getX();
                double y1 = loop[loop.length - 1].getY();
                for (final Vector2D point : loop) {
                    final double x0 = x1;
                    final double y0 = y1;
                    x1 = point.getX();
                    y1 = point.getY();
                    final double factor = x0 * y1 - y0 * x1;
                    sum  += factor;
                    sumX += factor * (x0 + x1);
                    sumY += factor * (y0 + y1);
                }
            }

            if (sum < 0) {
                // the polygon as a finite outside surrounded by an infinite inside
                setSize(Double.POSITIVE_INFINITY);
                setBarycenter(Vector2D.NaN);
            } else {
                setSize(sum / 2);
                setBarycenter(new Vector2D(sumX / (3 * sum), sumY / (3 * sum)));
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
    public Vector2D[][] getVertices() {
        if (vertices == null) {
            if (getTree(false).getCut() == null) {
                vertices = new Vector2D[0][];
            } else {

                // sort the segments according to their start point
                final SegmentsBuilder visitor = new SegmentsBuilder();
                getTree(true).visit(visitor);
                final AVLTree<ComparableSegment> sorted = visitor.getSorted();

                // identify the loops, starting from the open ones
                // (their start segments are naturally at the sorted set beginning)
                final ArrayList<List<ComparableSegment>> loops = new ArrayList<List<ComparableSegment>>();
                while (!sorted.isEmpty()) {
                    final AVLTree<ComparableSegment>.Node node = sorted.getSmallest();
                    final List<ComparableSegment> loop = followLoop(node, sorted);
                    if (loop != null) {
                        loops.add(loop);
                    }
                }

                // tranform the loops in an array of arrays of points
                vertices = new Vector2D[loops.size()][];
                int i = 0;

                for (final List<ComparableSegment> loop : loops) {
                    if (loop.size() < 2) {
                        // single infinite line
                        final Line line = loop.get(0).getLine();
                        vertices[i++] = new Vector2D[] {
                            null,
                            line.toSpace(new Vector1D(-Float.MAX_VALUE)),
                            line.toSpace(new Vector1D(+Float.MAX_VALUE))
                        };
                    } else if (loop.get(0).getStart() == null) {
                        // open loop with at least one real point
                        final Vector2D[] array = new Vector2D[loop.size() + 2];
                        int j = 0;
                        for (Segment segment : loop) {

                            if (j == 0) {
                                // null point and first dummy point
                                double x = segment.getLine().toSubSpace(segment.getEnd()).getX();
                                x -= FastMath.max(1.0, FastMath.abs(x / 2));
                                array[j++] = null;
                                array[j++] = segment.getLine().toSpace(new Vector1D(x));
                            }

                            if (j < (array.length - 1)) {
                                // current point
                                array[j++] = segment.getEnd();
                            }

                            if (j == (array.length - 1)) {
                                // last dummy point
                                double x = segment.getLine().toSubSpace(segment.getStart()).getX();
                                x += FastMath.max(1.0, FastMath.abs(x / 2));
                                array[j++] = segment.getLine().toSpace(new Vector1D(x));
                            }

                        }
                        vertices[i++] = array;
                    } else {
                        final Vector2D[] array = new Vector2D[loop.size()];
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
    private List<ComparableSegment> followLoop(final AVLTree<ComparableSegment>.Node node,
                                               final AVLTree<ComparableSegment> sorted) {

        final ArrayList<ComparableSegment> loop = new ArrayList<ComparableSegment>();
        ComparableSegment segment = node.getElement();
        loop.add(segment);
        final Vector2D globalStart = segment.getStart();
        Vector2D end = segment.getEnd();
        node.delete();

        // is this an open or a closed loop ?
        final boolean open = segment.getStart() == null;

        while ((end != null) && (open || (globalStart.distance(end) > 1.0e-10))) {

            // search the sub-hyperplane starting where the previous one ended
            AVLTree<ComparableSegment>.Node selectedNode = null;
            ComparableSegment       selectedSegment  = null;
            double                  selectedDistance = Double.POSITIVE_INFINITY;
            final ComparableSegment lowerLeft        = new ComparableSegment(end, -1.0e-10, -1.0e-10);
            final ComparableSegment upperRight       = new ComparableSegment(end, +1.0e-10, +1.0e-10);
            for (AVLTree<ComparableSegment>.Node n = sorted.getNotSmaller(lowerLeft);
                 (n != null) && (n.getElement().compareTo(upperRight) <= 0);
                 n = n.getNext()) {
                segment = n.getElement();
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
            throw new MathInternalError();
        }

        return loop;

    }

    /** Private extension of Segment allowing comparison. */
    private static class ComparableSegment extends Segment implements Comparable<ComparableSegment> {

        /** Sorting key. */
        private OrderedTuple sortingKey;

        /** Build a segment.
         * @param start start point of the segment
         * @param end end point of the segment
         * @param line line containing the segment
         */
        public ComparableSegment(final Vector2D start, final Vector2D end, final Line line) {
            super(start, end, line);
            sortingKey = (start == null) ?
                         new OrderedTuple(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY) :
                         new OrderedTuple(start.getX(), start.getY());
        }

        /** Build a dummy segment.
         * <p>
         * The object built is not a real segment, only the sorting key is used to
         * allow searching in the neighborhood of a point. This is an horrible hack ...
         * </p>
         * @param start start point of the segment
         * @param dx abscissa offset from the start point
         * @param dy ordinate offset from the start point
         */
        public ComparableSegment(final Vector2D start, final double dx, final double dy) {
            super(null, null, null);
            sortingKey = new OrderedTuple(start.getX() + dx, start.getY() + dy);
        }

        /** {@inheritDoc} */
        public int compareTo(final ComparableSegment o) {
            return sortingKey.compareTo(o.sortingKey);
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            } else if (other instanceof ComparableSegment) {
                return compareTo((ComparableSegment) other) == 0;
            } else {
                return false;
            }
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return getStart().hashCode() ^ getEnd().hashCode() ^
                   getLine().hashCode() ^ sortingKey.hashCode();
        }

    }

    /** Visitor building segments. */
    private static class SegmentsBuilder implements BSPTreeVisitor<Euclidean2D> {

        /** Sorted segments. */
        private AVLTree<ComparableSegment> sorted;

        /** Simple constructor. */
        public SegmentsBuilder() {
            sorted = new AVLTree<ComparableSegment>();
        }

        /** {@inheritDoc} */
        public Order visitOrder(final BSPTree<Euclidean2D> node) {
            return Order.MINUS_SUB_PLUS;
        }

        /** {@inheritDoc} */
        public void visitInternalNode(final BSPTree<Euclidean2D> node) {
            @SuppressWarnings("unchecked")
            final BoundaryAttribute<Euclidean2D> attribute = (BoundaryAttribute<Euclidean2D>) node.getAttribute();
            if (attribute.getPlusOutside() != null) {
                addContribution(attribute.getPlusOutside(), false);
            }
            if (attribute.getPlusInside() != null) {
                addContribution(attribute.getPlusInside(), true);
            }
        }

        /** {@inheritDoc} */
        public void visitLeafNode(final BSPTree<Euclidean2D> node) {
        }

        /** Add he contribution of a boundary facet.
         * @param sub boundary facet
         * @param reversed if true, the facet has the inside on its plus side
         */
        private void addContribution(final SubHyperplane<Euclidean2D> sub, final boolean reversed) {
            @SuppressWarnings("unchecked")
            final AbstractSubHyperplane<Euclidean2D, Euclidean1D> absSub =
                (AbstractSubHyperplane<Euclidean2D, Euclidean1D>) sub;
            final Line line      = (Line) sub.getHyperplane();
            final List<Interval> intervals = ((IntervalsSet) absSub.getRemainingRegion()).asList();
            for (final Interval i : intervals) {
                final Vector2D start = Double.isInfinite(i.getInf()) ?
                                      null : (Vector2D) line.toSpace(new Vector1D(i.getInf()));
                final Vector2D end   = Double.isInfinite(i.getSup()) ?
                                      null : (Vector2D) line.toSpace(new Vector1D(i.getSup()));
                if (reversed) {
                    sorted.insert(new ComparableSegment(end, start, line.getReverse()));
                } else {
                    sorted.insert(new ComparableSegment(start, end, line));
                }
            }
        }

        /** Get the sorted segments.
         * @return sorted segments
         */
        public AVLTree<ComparableSegment> getSorted() {
            return sorted;
        }

    }

}
