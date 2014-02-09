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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.geometry.enclosing.EnclosingBall;
import org.apache.commons.math3.geometry.enclosing.WelzlEncloser;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.partitioning.AbstractRegion;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
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
     * @see Vertex
     * @see Edge
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

    /** Get a spherical cap enclosing the polygon.
     * <p>
     * This method is intended as a first test to quickly identify points
     * that are guaranteed to be outside of the region, hence performing a full
     * {@link #checkPoint(org.apache.commons.math3.geometry.Vector) checkPoint}
     * only if the point status remains undecided after the quick check. It is
     * is therefore mostly useful to speed up computation for small polygons with
     * complex shapes (say a country boundary on Earth), as the spherical cap will
     * be small and hence will reliably identify a large part of the sphere as outside,
     * whereas the full check can be more computing intensive. A typical use case is
     * therefore:
     * </p>
     * <pre>
     *   // compute region, plus an enclosing spherical cap
     *   SphericalPolygonsSet complexShape = ...;
     *   EnclosingBall<Sphere2D, S2Point> cap = complexShape.getEnclosingCap();
     *
     *   // check lots of points
     *   for (Vector3D p : points) {
     *
     *     final Location l;
     *     if (cap.contains(p)) {
     *       // we cannot be sure where the point is
     *       // we need to perform the full computation
     *       l = complexShape.checkPoint(v);
     *     } else {
     *       // no need to do further computation,
     *       // we already know the point is outside
     *       l = Location.OUTSIDE;
     *     }
     *
     *     // use l ...
     *
     *   }
     * </pre>
     * <p>
     * In the special cases of empty or whole sphere polygons, special
     * spherical caps are returned, with angular radius set to negative
     * or positive infinity so the {@link
     * EnclosingBall#contains(org.apache.commons.math3.geometry.Point) ball.contains(point)}
     * method return always false or true.
     * </p>
     * <p>
     * This method is <em>not</em> guaranteed to return the smallest enclosing cap. It
     * will do so for small polygons without holes, but may select a very large spherical
     * cap in other cases.
     * </p>
     * @return a spherical cap enclosing the polygon
     */
    public EnclosingBall<Sphere2D, S2Point> getEnclosingCap() {

        // handle special cases first
        if (isEmpty()) {
            return new EnclosingBall<Sphere2D, S2Point>(S2Point.PLUS_K, Double.NEGATIVE_INFINITY);
        }
        if (isFull()) {
            return new EnclosingBall<Sphere2D, S2Point>(S2Point.PLUS_K, Double.POSITIVE_INFINITY);
        }

        // as the polygons is neither empty nor full, it has some boundaries and cut hyperplanes
        final BSPTree<Sphere2D> root = getTree(false);
        if (isEmpty(root.getMinus()) && isFull(root.getPlus())) {
            // the polygon covers an hemisphere, and its boundary is one 2π long edge
            final Circle circle = (Circle) root.getCut().getHyperplane();
            return new EnclosingBall<Sphere2D, S2Point>(new S2Point(circle.getPole()).negate(),
                                                        0.5 * FastMath.PI);
        }
        if (isFull(root.getMinus()) && isEmpty(root.getPlus())) {
            // the polygon covers an hemisphere, and its boundary is one 2π long edge
            final Circle circle = (Circle) root.getCut().getHyperplane();
            return new EnclosingBall<Sphere2D, S2Point>(new S2Point(circle.getPole()),
                                                        0.5 * FastMath.PI);
        }

        // extract boundary
        final List<Vertex> boundary = getBoundaryLoops();

        final List<S2Point> points = new ArrayList<S2Point>();
        for (final Vertex loopStart : boundary) {
            // extract points from the boundary loops, to be used by the encloser
            for (Vertex v = loopStart; points.isEmpty() || v != loopStart; v = v.getOutgoing().getEnd()) {
                points.add(v.getLocation());
            }
        }

        // find the smallest enclosing spherical cap
        final SphericalCapGenerator capGenerator = new SphericalCapGenerator(getInsidePoint(boundary));
        final WelzlEncloser<Sphere2D, S2Point> encloser =
                new WelzlEncloser<Sphere2D, S2Point>(getTolerance(), capGenerator);
        EnclosingBall<Sphere2D, S2Point> enclosing = encloser.enclose(points);

        // ensure that the spherical cap (which was defined using only a few points)
        // does not cross any edges
        if (enclosing.getRadius() >= 0.5 * FastMath.PI) {
            final List<Edge> notEnclosed = notEnclosed(boundary, enclosing);
            if (!notEnclosed.isEmpty()) {
                // the vertex-based spherical cap is too small
                // it does not fully enclose some edges

                // add the edges converging to the support points if any
                for (final S2Point point : enclosing.getSupport()) {
                    final Vertex vertex = associatedVertex(point, boundary);
                    if (vertex != null) {
                        addEdge(vertex.getIncoming(), notEnclosed);
                        addEdge(vertex.getOutgoing(), notEnclosed);
                    }
                }
                if (notEnclosed.size() < 3) {
                    // this should never happen
                    throw new MathInternalError();
                }

                // enlarge the spherical cap so it encloses even the three farthest edges
                Collections.sort(notEnclosed, new DistanceComparator(enclosing.getCenter().getVector()));
                enclosing = capGenerator.ballOnSupport(notEnclosed.get(notEnclosed.size() - 1).getCircle(),
                                                       notEnclosed.get(notEnclosed.size() - 2).getCircle(),
                                                       notEnclosed.get(notEnclosed.size() - 3).getCircle());

            }
        }

        // return the spherical cap found
        return enclosing;

    }

    /** Get an inside point.
     * @param boundary boundary loops (known to be non-empty)
     * @return an inside point
     */
    private Vector3D getInsidePoint(final List<Vertex> boundary) {

        // search for a leaf inside cell bounded by the loop
        final BSPTree<Sphere2D> leaf = getInsideLeaf(boundary.get(0).getOutgoing());

        // build a convex region from this inside leaf
        final SphericalPolygonsSet convex =
            new SphericalPolygonsSet(leaf.pruneAroundConvexCell(Boolean.TRUE, Boolean.FALSE, null),
                                     getTolerance());

        // select the convex region barycenter as the inside point
        return ((S2Point) convex.getBarycenter()).getVector();

    }

    /** Get an inside leaf cell relative to a single boundary loop.
     * @param edge edge belonging to the loop
     * @return an inside leaf cell
     */
    private BSPTree<Sphere2D> getInsideLeaf(final Edge edge) {

        // search for the node whose cut sub-hyperplane contains the edge
        final S2Point middlePoint = new S2Point(edge.getPointAt(0.5 * edge.getLength()));
        final BSPTree<Sphere2D> cuttingNode = getTree(true).getCell(middlePoint, getTolerance());
        if (cuttingNode.getCut() == null) {
            // this should never happen
            throw new MathInternalError();
        }

        final Vector3D cutPole  = ((Circle) cuttingNode.getCut().getHyperplane()).getPole();
        final Vector3D edgePole = edge.getCircle().getPole();

        if (Vector3D.dotProduct(cutPole, edgePole) > 0) {
            // the inside part is on the minus part of the cut
            return cuttingNode.getMinus().getCell(middlePoint, getTolerance());
        } else {
            // the inside part is on the plus part of the cut
            return cuttingNode.getPlus().getCell(middlePoint, getTolerance());
        }

    }

    /** Get the edges that fails to be in an enclosing spherical cap.
     * <p>
     * This method must be called <em>only</em> when the edges endpoints
     * are already known to be all inside of the spherical cap.
     * </p>
     * @param boundary boundary loops
     * @param cap spherical cap that already encloses all vertices
     * @return the edges that are not fully in the cap, despite their endpoints are
     */
    private List<Edge> notEnclosed(final List<Vertex> boundary,
                                    final EnclosingBall<Sphere2D, S2Point> cap) {

        final List<Edge> edges = new ArrayList<Edge>();

        for (final Vertex loopStart : boundary) {
            int count = 0;
            Edge edge = null;
            for (Vertex vertex = loopStart; count == 0 || vertex != loopStart; vertex = edge.getEnd()) {

                ++count;
                edge = vertex.getOutgoing();

                // find the intersection of the circle containing the edge and the cap boundary
                // let (u, α) and (v, β) be the poles and angular radii of the two circles
                // the intersection is computed as p = a u + b v + c u^v, with ||p|| = 1, so:
                // a = (cos α - u.v cos β) / (1 - (u.v)²)
                // b = (cos β - u.v cos α) / (1 - (u.v)²)
                // c = ±√[(1 - (a u + b v)²) / (1 - (u.v)²)]
                // here, α = π/2, hence cos α = 0
                // a u + b v is the part of the p vector in the (u, v) plane
                // c u^v is the part of the p vector orthogonal to the (u, v) plane
                final Vector3D u       = edge.getCircle().getPole();
                final Vector3D v       = cap.getCenter().getVector();
                final double cosBeta   = FastMath.cos(cap.getRadius());
                final double s         = Vector3D.dotProduct(u, v);
                final double f         = 1.0 / ((1 - s) * (1 + s));
                final double b         = cosBeta * f;
                final double a         = -s * b;
                final Vector3D inPlane = new Vector3D(a, u, b, v);
                final double aubv2     = inPlane.getNormSq();
                if (aubv2 < 1.0) {

                    // the two circles have two intersection points
                    final double   c          = FastMath.sqrt((1 - aubv2) * f);
                    final Vector3D crossPlane = Vector3D.crossProduct(u, v);
                    final Vector3D pPlus      = new Vector3D(1, inPlane, +c, crossPlane);
                    final Vector3D pMinus     = new Vector3D(1, inPlane, -c, crossPlane);
                    if (Vector3D.angle(pPlus, edge.getStart().getLocation().getVector()) <= getTolerance() ||
                        Vector3D.angle(pPlus, edge.getStart().getLocation().getVector()) <= getTolerance() ||
                        Vector3D.angle(pPlus, edge.getStart().getLocation().getVector()) <= getTolerance() ||
                        Vector3D.angle(pPlus, edge.getStart().getLocation().getVector()) <= getTolerance()) {
                        // the edge endpoints are really close, we select the edge
                        edges.add(edge);
                    } else {
                        // the edge is limited to a part of the circle, check its extension
                        final double startPhase          = edge.getCircle().getPhase(edge.getStart().getLocation().getVector());
                        final double pPlusRelativePhase  = MathUtils.normalizeAngle(edge.getCircle().getPhase(pPlus),
                                                                                    startPhase + FastMath.PI);
                        final double pMinusRelativePhase = MathUtils.normalizeAngle(edge.getCircle().getPhase(pMinus),
                                                                                    startPhase + FastMath.PI);
                        if (FastMath.min(pPlusRelativePhase, pMinusRelativePhase) < edge.getLength()) {
                            // the edge really goes out of the cap and enter it again,
                            // it is not entirely contained in the cap
                            edges.add(edge);
                        }
                    }

                }

            }
        }

        return edges;

    }

    /** Find the vertex associated to a point.
     * @param point point to associate to a vertex
     * @param boundary boundary loops
     * @return vertex associated to point, or null if the point is not a vertex
     */
    private Vertex associatedVertex(final S2Point point, final List<Vertex> boundary) {
        for (final Vertex loopStart : boundary) {
            int count = 0;
            for (Vertex vertex = loopStart; count == 0 || vertex != loopStart; vertex = vertex.getOutgoing().getEnd()) {
                ++count;
                if (point == vertex.getLocation()) {
                    return vertex;
                }
            }
        }
        return null;
    }

    /** Add an edge to a list if not already present.
     * @param edge edge to add
     * @param list to which edge must be added
     */
    private void addEdge(final Edge edge, final List<Edge> list) {
        for (final Edge present : list) {
            if (present == edge) {
                // the edge was already in the list
                return;
            }
        }
        list.add(edge);
    }

    /** Comparator for sorting edges according to distance wrt a spherical cap pole. */
    private static class DistanceComparator implements Comparator<Edge> {

        /** Pole of the spherical cap. */
        private final Vector3D pole;

        /** Simple constructor.
         * @param pole pole of the spherical cap
         */
        public DistanceComparator(final Vector3D pole) {
            this.pole = pole;
        }

        /** {@inheritDoc} */
        public int compare(final Edge o1, final Edge o2) {
            return Double.compare(distance(o1), distance(o2));
        }

        /** Compute distance between edge and pole.
         * @param edge edge
         * @return distance between adged and pole
         */
        private double distance(final Edge edge) {
            final double alpha = edge.getCircle().getPole().distance(pole);
            return FastMath.max(alpha + 0.5 * FastMath.PI, 1.5 * FastMath.PI - alpha);
        }

    }

}
