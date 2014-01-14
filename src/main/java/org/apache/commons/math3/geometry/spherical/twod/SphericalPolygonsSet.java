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
package org.apache.commons.math3.geometry.spherical.twod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.partitioning.AbstractRegion;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor;
import org.apache.commons.math3.geometry.partitioning.BoundaryAttribute;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.geometry.spherical.oned.Arc;
import org.apache.commons.math3.geometry.spherical.oned.ArcsSet;
import org.apache.commons.math3.geometry.spherical.oned.S1Point;
import org.apache.commons.math3.geometry.spherical.oned.Sphere1D;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

/** This class represents a region on the 2-sphere: a set of spherical polygons.
 * @version $Id$
 * @since 3.3
 */
public class SphericalPolygonsSet extends AbstractRegion<Sphere2D, Sphere1D> {

    /** Boundary defined as an array of closed loops start vertices. */
    private List<Vertex> loops;

    /** Build a polygons set representing the whole real 2-sphere.
     * @param tolerance below which points are consider to be identical
     */
    public SphericalPolygonsSet(final double tolerance) {
        super(tolerance);
    }

    /** Build a polygons set representing a hemisphere.
     * @param pole pole of the hemisphere (the pole is in the inside half)
     * @param tolerance below which points are consider to be identical
     */
    public SphericalPolygonsSet(final Vector3D pole, final double tolerance) {
        super(new BSPTree<Sphere2D>(new Circle(pole, tolerance).wholeHyperplane(),
                                    new BSPTree<Sphere2D>(Boolean.FALSE),
                                    new BSPTree<Sphere2D>(Boolean.TRUE),
                                    null),
              tolerance);
    }

    /** Build a polygons set representing a regular polygon.
     * @param center center of the polygon (the center is in the inside half)
     * @param meridian point defining the reference meridian for first polygon vertex
     * @param outsideRadius distance of the vertices to the center
     * @param n number of sides of the polygon
     * @param tolerance below which points are consider to be identical
     */
    public SphericalPolygonsSet(final Vector3D center, final Vector3D meridian,
                                final double outsideRadius, final int n,
                                final double tolerance) {
        this(tolerance, createRegularPolygonVertices(center, meridian, outsideRadius, n));
    }

    /** Build a polygons set from a BSP tree.
     * <p>The leaf nodes of the BSP tree <em>must</em> have a
     * {@code Boolean} attribute representing the inside status of
     * the corresponding cell (true for inside cells, false for outside
     * cells). In order to avoid building too many small objects, it is
     * recommended to use the predefined constants
     * {@code Boolean.TRUE} and {@code Boolean.FALSE}</p>
     * @param tree inside/outside BSP tree representing the region
     * @param tolerance below which points are consider to be identical
     */
    public SphericalPolygonsSet(final BSPTree<Sphere2D> tree, final double tolerance) {
        super(tree, tolerance);
    }

    /** Build a polygons set from a Boundary REPresentation (B-rep).
     * <p>The boundary is provided as a collection of {@link
     * SubHyperplane sub-hyperplanes}. Each sub-hyperplane has the
     * interior part of the region on its minus side and the exterior on
     * its plus side.</p>
     * <p>The boundary elements can be in any order, and can form
     * several non-connected sets (like for example polygons with holes
     * or a set of disjoint polygons considered as a whole). In
     * fact, the elements do not even need to be connected together
     * (their topological connections are not used here). However, if the
     * boundary does not really separate an inside open from an outside
     * open (open having here its topological meaning), then subsequent
     * calls to the {@link
     * org.apache.commons.math3.geometry.partitioning.Region#checkPoint(org.apache.commons.math3.geometry.Point)
     * checkPoint} method will not be meaningful anymore.</p>
     * <p>If the boundary is empty, the region will represent the whole
     * space.</p>
     * @param boundary collection of boundary elements, as a
     * collection of {@link SubHyperplane SubHyperplane} objects
     * @param tolerance below which points are consider to be identical
     */
    public SphericalPolygonsSet(final Collection<SubHyperplane<Sphere2D>> boundary, final double tolerance) {
        super(boundary, tolerance);
    }

    /** Build a polygon from a simple list of vertices.
     * <p>The boundary is provided as a list of points considering to
     * represent the vertices of a simple loop. The interior part of the
     * region is on the left side of this path and the exterior is on its
     * right side.</p>
     * <p>This constructor does not handle polygons with a boundary
     * forming several disconnected paths (such as polygons with holes).</p>
     * <p>For cases where this simple constructor applies, it is expected to
     * be numerically more robust than the {@link #SphericalPolygonsSet(Collection,
     * double) general constructor} using {@link SubHyperplane subhyperplanes}.</p>
     * <p>If the list is empty, the region will represent the whole
     * space.</p>
     * <p>
     * Polygons with thin pikes or dents are inherently difficult to handle because
     * they involve circles with almost opposite directions at some vertices. Polygons
     * whose vertices come from some physical measurement with noise are also
     * difficult because an edge that should be straight may be broken in lots of
     * different pieces with almost equal directions. In both cases, computing the
     * circles intersections is not numerically robust due to the almost 0 or almost
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
     */
    public SphericalPolygonsSet(final double hyperplaneThickness, final S2Point ... vertices) {
        super(verticesToTree(hyperplaneThickness, vertices), hyperplaneThickness);
    }

    /** Build the vertices representing a regular polygon.
     * @param center center of the polygon (the center is in the inside half)
     * @param meridian point defining the reference meridian for first polygon vertex
     * @param outsideRadius distance of the vertices to the center
     * @param n number of sides of the polygon
     * @return vertices array
     */
    private static S2Point[] createRegularPolygonVertices(final Vector3D center, final Vector3D meridian,
                                                          final double outsideRadius, final int n) {
        final S2Point[] array = new S2Point[n];
        final Rotation r0 = new Rotation(Vector3D.crossProduct(center, meridian), outsideRadius);
        array[0] = new S2Point(r0.applyTo(center));

        final Rotation r = new Rotation(center, MathUtils.TWO_PI / n);
        for (int i = 1; i < n; ++i) {
            array[i] = new S2Point(r.applyTo(array[i - 1].getVector()));
        }

        return array;
    }

    /** Build the BSP tree of a polygons set from a simple list of vertices.
     * <p>The boundary is provided as a list of points considering to
     * represent the vertices of a simple loop. The interior part of the
     * region is on the left side of this path and the exterior is on its
     * right side.</p>
     * <p>This constructor does not handle polygons with a boundary
     * forming several disconnected paths (such as polygons with holes).</p>
     * <p>This constructor handles only polygons with edges strictly shorter
     * than \( \pi \). If longer edges are needed, they need to be broken up
     * in smaller sub-edges so this constraint holds.</p>
     * <p>For cases where this simple constructor applies, it is expected to
     * be numerically more robust than the {@link #PolygonsSet(Collection) general
     * constructor} using {@link SubHyperplane subhyperplanes}.</p>
     * @param hyperplaneThickness tolerance below which points are consider to
     * belong to the hyperplane (which is therefore more a slab)
     * @param vertices vertices of the simple loop boundary
     * @return the BSP tree of the input vertices
     */
    private static BSPTree<Sphere2D> verticesToTree(final double hyperplaneThickness,
                                                    final S2Point ... vertices) {

        final int n = vertices.length;
        if (n == 0) {
            // the tree represents the whole space
            return new BSPTree<Sphere2D>(Boolean.TRUE);
        }

        // build the vertices
        final Vertex[] vArray = new Vertex[n];
        for (int i = 0; i < n; ++i) {
            vArray[i] = new Vertex(vertices[i]);
        }

        // build the edges
        List<Edge> edges = new ArrayList<Edge>(n);
        Vertex end = vArray[n - 1];
        for (int i = 0; i < n; ++i) {

            // get the endpoints of the edge
            final Vertex start = end;
            end = vArray[i];

            // get the circle supporting the edge, taking care not to recreate it
            // if it was already created earlier due to another edge being aligned
            // with the current one
            Circle circle = start.sharedCircleWith(end);
            if (circle == null) {
                circle = new Circle(start.getLocation(), end.getLocation(), hyperplaneThickness);
            }

            // create the edge and store it
            edges.add(new Edge(start, end,
                               Vector3D.angle(start.getLocation().getVector(),
                                              end.getLocation().getVector()),
                               circle));

            // check if another vertex also happens to be on this circle
            for (final Vertex vertex : vArray) {
                if (vertex != start && vertex != end &&
                    FastMath.abs(circle.getOffset(vertex.getLocation())) <= hyperplaneThickness) {
                    vertex.bindWith(circle);
                }
            }

        }

        // build the tree top-down
        final BSPTree<Sphere2D> tree = new BSPTree<Sphere2D>();
        insertEdges(hyperplaneThickness, tree, edges);

        return tree;

    }

    /** Recursively build a tree by inserting cut sub-hyperplanes.
     * @param hyperplaneThickness tolerance below which points are considered to
     * belong to the hyperplane (which is therefore more a slab)
     * @param node current tree node (it is a leaf node at the beginning
     * of the call)
     * @param edges list of edges to insert in the cell defined by this node
     * (excluding edges not belonging to the cell defined by this node)
     */
    private static void insertEdges(final double hyperplaneThickness,
                                    final BSPTree<Sphere2D> node,
                                    final List<Edge> edges) {

        // find an edge with an hyperplane that can be inserted in the node
        int index = 0;
        Edge inserted = null;
        while (inserted == null && index < edges.size()) {
            inserted = edges.get(index++);
            if (!node.insertCut(inserted.getCircle())) {
                inserted = null;
            }
        }

        if (inserted == null) {
            // no suitable edge was found, the node remains a leaf node
            // we need to set its inside/outside boolean indicator
            final BSPTree<Sphere2D> parent = node.getParent();
            if (parent == null || node == parent.getMinus()) {
                node.setAttribute(Boolean.TRUE);
            } else {
                node.setAttribute(Boolean.FALSE);
            }
            return;
        }

        // we have split the node by inserting an edge as a cut sub-hyperplane
        // distribute the remaining edges in the two sub-trees
        final List<Edge> outsideList = new ArrayList<Edge>();
        final List<Edge> insideList  = new ArrayList<Edge>();
        for (final Edge edge : edges) {
            if (edge != inserted) {
                edge.split(inserted.getCircle(), outsideList, insideList);
            }
        }

        // recurse through lower levels
        if (!outsideList.isEmpty()) {
            insertEdges(hyperplaneThickness, node.getPlus(), outsideList);
        } else {
            node.getPlus().setAttribute(Boolean.FALSE);
        }
        if (!insideList.isEmpty()) {
            insertEdges(hyperplaneThickness, node.getMinus(),  insideList);
        } else {
            node.getMinus().setAttribute(Boolean.TRUE);
        }

    }

    /** Spherical polygons vertex.
     * @see Edge
     */
    public static class Vertex {

        /** Vertex location. */
        private final S2Point location;

        /** Incoming edge. */
        private Edge incoming;

        /** Outgoing edge. */
        private Edge outgoing;

        /** Circles bound with this vertex. */
        private final List<Circle> circles;

        /** Build a non-processed vertex not owned by any node yet.
         * @param location vertex location
         */
        private Vertex(final S2Point location) {
            this.location = location;
            this.incoming = null;
            this.outgoing = null;
            this.circles  = new ArrayList<Circle>();
        }

        /** Get Vertex location.
         * @return vertex location
         */
        public S2Point getLocation() {
            return location;
        }

        /** Bind a circle considered to contain this vertex.
         * @param circle circle to bind with this vertex
         */
        private void bindWith(final Circle circle) {
            circles.add(circle);
        }

        /** Get the common circle bound with both the instance and another vertex, if any.
         * <p>
         * When two vertices are both bound to the same circle, this means they are
         * already handled by node associated with this circle, so there is no need
         * to create a cut hyperplane for them.
         * </p>
         * @param vertex other vertex to check instance against
         * @return circle bound with both the instance and another vertex, or null if the
         * two vertices do not share a circle yet
         */
        private Circle sharedCircleWith(final Vertex vertex) {
            for (final Circle circle1 : circles) {
                for (final Circle circle2 : vertex.circles) {
                    if (circle1 == circle2) {
                        return circle1;
                    }
                }
            }
            return null;
        }

        /** Set incoming edge.
         * <p>
         * The circle supporting the incoming edge is automatically bound
         * with the instance.
         * </p>
         * @param incoming incoming edge
         */
        private void setIncoming(final Edge incoming) {
            this.incoming = incoming;
            bindWith(incoming.getCircle());
        }

        /** Get incoming edge.
         * @return incoming edge
         */
        public Edge getIncoming() {
            return incoming;
        }

        /** Set outgoing edge.
         * <p>
         * The circle supporting the outgoing edge is automatically bound
         * with the instance.
         * </p>
         * @param outgoing outgoing edge
         */
        private void setOutgoing(final Edge outgoing) {
            this.outgoing = outgoing;
            bindWith(outgoing.getCircle());
        }

        /** Get outgoing edge.
         * @return outgoing edge
         */
        public Edge getOutgoing() {
            return outgoing;
        }

    }

    /** Spherical polygons edge.
     * @see Vertex
     */
    public static class Edge {

        /** Start vertex. */
        private final Vertex start;

        /** End vertex. */
        private Vertex end;

        /** Length of the arc. */
        private final double length;

        /** Circle supporting the edge. */
        private final Circle circle;

        /** Build an edge not contained in any node yet.
         * @param start start vertex
         * @param end end vertex
         * @param length length of the arc (it can be greater than \( \pi \))
         * @param circle circle supporting the edge
         */
        private Edge(final Vertex start, final Vertex end, final double length, final Circle circle) {

            this.start  = start;
            this.end    = end;
            this.length = length;
            this.circle = circle;

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

        /** Get the length of the arc.
         * @return length of the arc (can be greater than \( \pi \))
         */
        public double getLength() {
            return length;
        }

        /** Get the circle supporting this edge.
         * @return circle supporting this edge
         */
        public Circle getCircle() {
            return circle;
        }

        /** Get an intermediate point.
         * <p>
         * The angle along the edge should normally be between 0 and {@link #getLength()}
         * in order to remain within edge limits. However, there are no checks on the
         * value of the angle, so user can rebuild the full circle on which an edge is
         * defined if they want.
         * </p>
         * @param alpha angle along the edge, counted from {@link #getStart()}
         * @return an intermediate point
         */
        public Vector3D getPointAt(final double alpha) {
            return circle.getPointAt(alpha + circle.getPhase(start.getLocation().getVector()));
        }

        /** Connect the instance with a following edge.
         * @param next edge following the instance
         */
        private void setNextEdge(final Edge next) {
            end = next.getStart();
            end.setIncoming(this);
            end.bindWith(getCircle());
        }

        /** Split the edge.
         * <p>
         * Once split, this edge is not referenced anymore by the vertices,
         * it is replaced by the two or three sub-edges and intermediate splitting
         * vertices are introduced to connect these sub-edges together.
         * </p>
         * @param splitCircle circle splitting the edge in several parts
         * @param outsideList list where to put parts that are outside of the split circle
         * @param insideList list where to put parts that are inside the split circle
         */
        private void split(final Circle splitCircle,
                           final List<Edge> outsideList, final List<Edge> insideList) {

            // get the inside arc, synchronizing its phase with the edge itself
            final double edgeStart        = circle.getPhase(start.getLocation().getVector());
            final Arc    arc              = circle.getInsideArc(splitCircle);
            final double arcRelativeStart = MathUtils.normalizeAngle(arc.getInf(), edgeStart + FastMath.PI) - edgeStart;
            final double arcRelativeEnd   = arcRelativeStart + arc.getSize();
            final double unwrappedEnd     = arcRelativeEnd - MathUtils.TWO_PI;

            // build the sub-edges
            final double tolerance = circle.getTolerance();
            Vertex previousVertex = start;
            if (unwrappedEnd >= length - tolerance) {

                // the edge is entirely contained inside the circle
                // we don't split anything
                insideList.add(this);

            } else {

                // there are at least some parts of the edge that should be outside
                // (even is they are later be filtered out as being too small)
                double alreadyManagedLength = 0;
                if (unwrappedEnd >= 0) {
                    // the start of the edge is inside the circle
                    previousVertex = addSubEdge(previousVertex,
                                                new Vertex(new S2Point(circle.getPointAt(edgeStart + unwrappedEnd))),
                                                unwrappedEnd, insideList, splitCircle);
                    alreadyManagedLength = unwrappedEnd;
                }

                if (arcRelativeStart >= length - tolerance) {
                    // the edge ends while still outside of the circle
                    if (unwrappedEnd >= 0) {
                        previousVertex = addSubEdge(previousVertex, end,
                                                    length - alreadyManagedLength, outsideList, splitCircle);
                    } else {
                        // the edge is entirely outside of the circle
                        // we don't split anything
                        outsideList.add(this);
                    }
                } else {
                    // the edge is long enough to enter inside the circle
                    previousVertex = addSubEdge(previousVertex,
                                                new Vertex(new S2Point(circle.getPointAt(edgeStart + arcRelativeStart))),
                                                arcRelativeStart - alreadyManagedLength, outsideList, splitCircle);
                    alreadyManagedLength = arcRelativeStart;

                    if (arcRelativeEnd >= length - tolerance) {
                        // the edge ends while still inside of the circle
                        previousVertex = addSubEdge(previousVertex, end,
                                                    length - alreadyManagedLength, insideList, splitCircle);
                    } else {
                        // the edge is long enough to exit outside of the circle
                        previousVertex = addSubEdge(previousVertex,
                                                    new Vertex(new S2Point(circle.getPointAt(edgeStart + arcRelativeStart))),
                                                    arcRelativeStart - alreadyManagedLength, insideList, splitCircle);
                        alreadyManagedLength = arcRelativeStart;
                        previousVertex = addSubEdge(previousVertex, end,
                                                    length - alreadyManagedLength, outsideList, splitCircle);
                    }
                }

            }

        }

        /** Add a sub-edge to a list if long enough.
         * <p>
         * If the length of the sub-edge to add is smaller than the {@link Circle#getTolerance()}
         * tolerance of the support circle, it will be ignored.
         * </p>
         * @param subStart start of the sub-edge
         * @param subEnd end of the sub-edge
         * @param subLength length of the sub-edge
         * @param splitCircle circle splitting the edge in several parts
         * @param list list where to put the sub-edge
         * @return end vertex of the edge ({@code subEnd} if the edge was long enough and really
         * added, {@code subStart} if the edge was too small and therefore ignored)
         */
        private Vertex addSubEdge(final Vertex subStart, final Vertex subEnd, final double subLength,
                                  final List<Edge> list, final Circle splitCircle) {

            if (subLength <= circle.getTolerance()) {
                // the edge is too short, we ignore it
                return subStart;
            }

            // really add the edge
            subEnd.bindWith(splitCircle);
            final Edge edge = new Edge(subStart, subEnd, subLength, circle);
            list.add(edge);
            return subEnd;

        }

    }

    /** {@inheritDoc} */
    @Override
    public SphericalPolygonsSet buildNew(final BSPTree<Sphere2D> tree) {
        return new SphericalPolygonsSet(tree, getTolerance());
    }

    /** {@inheritDoc}
     * @exception MathIllegalStateException if the tolerance setting does not allow to build
     * a clean non-ambiguous boundary
     */
    @Override
    protected void computeGeometricalProperties() throws MathIllegalStateException {

        final BSPTree<Sphere2D> tree = getTree(true);

        if (tree.getCut() == null) {

            // the instance has a single cell without any boundaries

            if (tree.getCut() == null && (Boolean) tree.getAttribute()) {
                // the instance covers the whole space
                setSize(4 * FastMath.PI);
                setBarycenter(new S2Point(0, 0));
            } else {
                setSize(0);
                setBarycenter(S2Point.NaN);
            }

        } else {

            // the instance has a boundary
            final PropertiesComputer pc = new PropertiesComputer(getTolerance());
            tree.visit(pc);
            setSize(pc.getArea());
            setBarycenter(pc.getBarycenter());

        }

    }

    /** Get the boundary loops of the polygon.
     * <p>The polygon boundary can be represented as a list of closed loops,
     * each loop being given by exactly one of its vertices. From each loop
     * start vertex, one can follow the loop by finding the outgoing edge,
     * then the end vertex, then the next outgoing edge ... until the start
     * vertex of the loop (exactly the same instance) is found again once
     * the full loop has been visited.</p>
     * <p>If the polygon has no boundary at all, a zero length loop
     * array will be returned.</p>
     * <p>If the polygon is a simple one-piece polygon, then the returned
     * array will contain a single vertex.
     * </p>
     * <p>All edges in the various loops have the inside of the region on
     * their left side (i.e. toward their pole) and the outside on their
     * right side (i.e. away from their pole) when moving in the underlying
     * circle direction. This means that the closed loops obey the direct
     * trigonometric orientation.</p>
     * @return boundary of the polygon, organized as an unmodifiable list of loops start vertices.
     * @exception MathIllegalStateException if the tolerance setting does not allow to build
     * a clean non-ambiguous boundary
     */
    public List<Vertex> getBoundaryLoops() throws MathIllegalStateException {

        if (loops == null) {
            if (getTree(false).getCut() == null) {
                loops = Collections.emptyList();
            } else {

                // sort the arcs according to their start point
                final BSPTree<Sphere2D> root = getTree(true);
                final EdgesBuilder visitor = new EdgesBuilder(root, getTolerance());
                root.visit(visitor);
                final List<Edge> edges = visitor.getEdges();


                // convert the list of all edges into a list of start vertices
                loops = new ArrayList<Vertex>();
                while (!edges.isEmpty()) {

                    // this is an edge belonging to a new loop, store it
                    Edge edge = edges.get(0);
                    final Vertex startVertex = edge.getStart();
                    loops.add(startVertex);

                    // remove all remaining edges in the same loop
                    do {

                        // remove one edge
                        for (final Iterator<Edge> iterator = edges.iterator(); iterator.hasNext();) {
                            if (iterator.next() == edge) {
                                iterator.remove();
                                break;
                            }
                        }

                        // go to next edge following the boundary loop
                        edge = edge.getEnd().getOutgoing();

                    } while (edge.getStart() != startVertex);

                }

            }
        }

        return Collections.unmodifiableList(loops);

    }

    /** Visitor building edges. */
    private static class EdgesBuilder implements BSPTreeVisitor<Sphere2D> {

        /** Root of the tree. */
        private final BSPTree<Sphere2D> root;

        /** Tolerance below which points are consider to be identical. */
        private final double tolerance;

        /** Built edges and their associated nodes. */
        private final Map<Edge, BSPTree<Sphere2D>> edgeToNode;

        /** Reversed map. */
        private final Map<BSPTree<Sphere2D>, List<Edge>> nodeToEdgesList;

        /** Simple constructor.
         * @param root tree root
         * @param tolerance below which points are consider to be identical
         */
        public EdgesBuilder(final BSPTree<Sphere2D> root, final double tolerance) {
            this.root            = root;
            this.tolerance       = tolerance;
            this.edgeToNode      = new IdentityHashMap<Edge, BSPTree<Sphere2D>>();
            this.nodeToEdgesList = new IdentityHashMap<BSPTree<Sphere2D>, List<Edge>>();
        }

        /** {@inheritDoc} */
        public Order visitOrder(final BSPTree<Sphere2D> node) {
            return Order.MINUS_SUB_PLUS;
        }

        /** {@inheritDoc} */
        public void visitInternalNode(final BSPTree<Sphere2D> node) {
            nodeToEdgesList.put(node, new ArrayList<Edge>());
            @SuppressWarnings("unchecked")
            final BoundaryAttribute<Sphere2D> attribute = (BoundaryAttribute<Sphere2D>) node.getAttribute();
            if (attribute.getPlusOutside() != null) {
                addContribution((SubCircle) attribute.getPlusOutside(), false, node);
            }
            if (attribute.getPlusInside() != null) {
                addContribution((SubCircle) attribute.getPlusInside(), true, node);
            }
        }

        /** {@inheritDoc} */
        public void visitLeafNode(final BSPTree<Sphere2D> node) {
        }

        /** Add the contribution of a boundary edge.
         * @param sub boundary facet
         * @param reversed if true, the facet has the inside on its plus side
         * @param node node to which the edge belongs
         */
        private void addContribution(final SubCircle sub, final boolean reversed,
                                     final BSPTree<Sphere2D> node) {
            final Circle circle  = (Circle) sub.getHyperplane();
            final List<Arc> arcs = ((ArcsSet) sub.getRemainingRegion()).asList();
            for (final Arc a : arcs) {
                final Vertex start = new Vertex((S2Point) circle.toSpace(new S1Point(a.getInf())));
                final Vertex end   = new Vertex((S2Point) circle.toSpace(new S1Point(a.getSup())));
                start.bindWith(circle);
                end.bindWith(circle);
                final Edge edge;
                if (reversed) {
                    edge = new Edge(end, start, a.getSize(), circle.getReverse());
                } else {
                    edge = new Edge(start, end, a.getSize(), circle);
                }
                edgeToNode.put(edge, node);
                nodeToEdgesList.get(node).add(edge);
            }
        }

        /** Get the edge that should naturally follow another one.
         * @param previous edge to be continued
         * @return other edge, starting where the previous one ends (they
         * have not been connected yet)
         * @exception MathIllegalStateException if there is not a single other edge
         */
        private Edge getFollowingEdge(final Edge previous)
            throws MathIllegalStateException {

            // get the candidate nodes
            final S2Point point = previous.getEnd().getLocation();
            final List<BSPTree<Sphere2D>> candidates = root.getCloseCuts(point, tolerance);

            // the following edge we are looking for must start from one of the candidates nodes
            double closest = tolerance;
            Edge following = null;
            for (final BSPTree<Sphere2D> node : candidates) {
                for (final Edge edge : nodeToEdgesList.get(node)) {
                    if (edge != previous && edge.getStart().getIncoming() == null) {
                        final Vector3D edgeStart = edge.getStart().getLocation().getVector();
                        final double gap         = Vector3D.angle(point.getVector(), edgeStart);
                        if (gap <= closest) {
                            closest   = gap;
                            following = edge;
                        }
                    }
                }
            }

            if (following == null) {
                // this should never happen
                throw new MathIllegalStateException(LocalizedFormats.OUTLINE_BOUNDARY_LOOP_OPEN);
            }

            return following;

        }

        /** Get the boundary edges.
         * @return boundary edges
         * @exception MathIllegalStateException if there is not a single other edge
         */
        public List<Edge> getEdges() throws MathIllegalStateException {

            // connect the edges
            for (final Edge previous : edgeToNode.keySet()) {
                previous.setNextEdge(getFollowingEdge(previous));
            }

            return new ArrayList<Edge>(edgeToNode.keySet());

        }

    }

    /** Visitor computing geometrical properties. */
    private static class PropertiesComputer implements BSPTreeVisitor<Sphere2D> {

        /** Tolerance below which points are consider to be identical. */
        private final double tolerance;

        /** Summed area. */
        private double summedArea;

        /** Summed barycenter. */
        private Vector3D summedBarycenter;

        /** Simple constructor.
     * @param tolerance below which points are consider to be identical
         */
        public PropertiesComputer(final double tolerance) {
            this.tolerance        = tolerance;
            this.summedArea       = 0;
            this.summedBarycenter = Vector3D.ZERO;
        }

        /** {@inheritDoc} */
        public Order visitOrder(final BSPTree<Sphere2D> node) {
            return Order.MINUS_SUB_PLUS;
        }

        /** {@inheritDoc} */
        public void visitInternalNode(final BSPTree<Sphere2D> node) {
            // nothing to do here
        }

        /** {@inheritDoc} */
        public void visitLeafNode(final BSPTree<Sphere2D> node) {
            if ((Boolean) node.getAttribute()) {

                // transform this inside leaf cell into a simple convex polygon
                final SphericalPolygonsSet convex =
                        new SphericalPolygonsSet(node.pruneAroundConvexCell(Boolean.TRUE,
                                                                            Boolean.FALSE,
                                                                            null),
                                                 tolerance);

                // extract the start of the single loop boundary of the convex cell
                final List<Vertex> boundary = convex.getBoundaryLoops();
                if (boundary.size() != 1) {
                    // this should never happen
                    throw new MathInternalError();
                }

                // compute the geometrical properties of the convex cell
                final double area  = convexCellArea(boundary.get(0));
                final Vector3D barycenter = convexCellBarycenter(boundary.get(0));

                // add the cell contribution to the global properties
                summedArea      += area;
                summedBarycenter = new Vector3D(1, summedBarycenter, area, barycenter);

            }
        }

        /** Compute convex cell area.
         * @param start start vertex of the convex cell boundary
         * @return area
         */
        private double convexCellArea(final Vertex start) {

            int n = 0;
            double sum = 0;

            // loop around the cell
            for (Edge e = start.getOutgoing(); n == 0 || e.getStart() != start; e = e.getEnd().getOutgoing()) {

                // find path interior angle at vertex
                final Vector3D previousPole = e.getCircle().getPole();
                final Vector3D nextPole     = e.getEnd().getOutgoing().getCircle().getPole();
                final Vector3D point        = e.getEnd().getLocation().getVector();
                double alpha = FastMath.atan2(Vector3D.dotProduct(nextPole, Vector3D.crossProduct(point, previousPole)),
                                              -Vector3D.dotProduct(nextPole, previousPole));
                if (alpha < 0) {
                    alpha += MathUtils.TWO_PI;
                }
                sum += alpha;
                n++;
            }

            // compute area using extended Girard theorem
            // see Spherical Trigonometry: For the Use of Colleges and Schools by I. Todhunter
            // article 99 in chapter VIII Area Of a Spherical Triangle. Spherical Excess.
            // book available from project Gutenberg at http://www.gutenberg.org/ebooks/19770
            return sum - (n - 2) * FastMath.PI;

        }

        /** Compute convex cell barycenter.
         * @param start start vertex of the convex cell boundary
         * @return barycenter
         */
        private Vector3D convexCellBarycenter(final Vertex start) {

            int n = 0;
            Vector3D sumB = Vector3D.ZERO;

            // loop around the cell
            for (Edge e = start.getOutgoing(); n == 0 || e.getStart() != start; e = e.getEnd().getOutgoing()) {
                final Vector3D middle = e.getPointAt(0.5 * e.getLength());
                sumB = new Vector3D(1, sumB, e.getLength(), middle);
                n++;
            }

            return sumB.normalize();

        }

        /** Get the area.
         * @return area
         */
        public double getArea() {
            return summedArea;
        }

        /** Get the barycenter.
         * @return barycenter
         */
        public S2Point getBarycenter() {
            if (summedBarycenter.getNormSq() == 0) {
                return S2Point.NaN;
            } else {
                return new S2Point(summedBarycenter);
            }
        }

    }

}
