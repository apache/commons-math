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
package org.apache.commons.math3.geometry.spherical.oned;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.partitioning.AbstractRegion;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.Precision;

/** This class represents a region of a circle: a set of arcs.
 * <p>
 * Note that due to the wrapping around \(2 \pi\), barycenter is
 * ill-defined here. It was defined only in order to fulfill
 * the requirements of the {@link
 * org.apache.commons.math3.geometry.partitioning.Region Region}
 * interface, but its use is discouraged.
 * </p>
 * @version $Id$
 * @since 3.3
 */
public class ArcsSet extends AbstractRegion<Sphere1D, Sphere1D> {

    /** Tolerance below which close sub-arcs are merged together. */
    private final double tolerance;

    /** Build an arcs set representing the whole circle.
     * @param tolerance tolerance below which close sub-arcs are merged together
     */
    public ArcsSet(final double tolerance) {
        this.tolerance = tolerance;
    }

    /** Build an arcs set corresponding to a single arc.
     * <p>
     * If either {@code lower} is equals to {@code upper} or
     * the interval exceeds \( 2 \pi \), the arc is considered
     * to be the full circle and its initial defining boundaries
     * will be forgotten. {@code lower} is not allowed to be greater
     * than {@code upper} (an exception is thrown in this case).
     * </p>
     * @param lower lower bound of the arc
     * @param upper upper bound of the arc
     * @param tolerance tolerance below which close sub-arcs are merged together
     * @exception NumberIsTooLargeException if lower is greater than upper
     */
    public ArcsSet(final double lower, final double upper, final double tolerance)
        throws NumberIsTooLargeException {
        super(buildTree(lower, upper, tolerance));
        this.tolerance = tolerance;
    }

    /** Build an arcs set from an inside/outside BSP tree.
     * <p>The leaf nodes of the BSP tree <em>must</em> have a
     * {@code Boolean} attribute representing the inside status of
     * the corresponding cell (true for inside cells, false for outside
     * cells). In order to avoid building too many small objects, it is
     * recommended to use the predefined constants
     * {@code Boolean.TRUE} and {@code Boolean.FALSE}</p>
     * @param tree inside/outside BSP tree representing the arcs set
     * @param tolerance tolerance below which close sub-arcs are merged together
     */
    public ArcsSet(final BSPTree<Sphere1D> tree, final double tolerance) {
        super(tree);
        this.tolerance = tolerance;
    }

    /** Build an arcs set from a Boundary REPresentation (B-rep).
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
     * org.apache.commons.math3.geometry.partitioning.Region#checkPoint(org.apache.commons.math3.geometry.Point)
     * checkPoint} method will not be meaningful anymore.</p>
     * <p>If the boundary is empty, the region will represent the whole
     * space.</p>
     * @param boundary collection of boundary elements
     * @param tolerance tolerance below which close sub-arcs are merged together
     */
    public ArcsSet(final Collection<SubHyperplane<Sphere1D>> boundary, final double tolerance) {
        super(boundary);
        this.tolerance = tolerance;
    }

    /** Build an inside/outside tree representing a single arc.
     * @param lower lower angular bound of the arc
     * @param upper upper angular bound of the arc
     * @param tolerance tolerance below which close sub-arcs are merged together
     * @return the built tree
     * @exception NumberIsTooLargeException if lower is greater than upper
     */
    private static BSPTree<Sphere1D> buildTree(final double lower, final double upper,
                                               final double tolerance)
        throws NumberIsTooLargeException {

        if (Precision.equals(lower, upper, 0) || (upper - lower) >= MathUtils.TWO_PI) {
            // the tree must cover the whole circle
            return new BSPTree<Sphere1D>(Boolean.TRUE);
        } else  if (lower > upper) {
            throw new NumberIsTooLargeException(LocalizedFormats.ENDPOINTS_NOT_AN_INTERVAL,
                                                lower, upper, true);
        }

        // this is a regular arc, covering only part of the circle
        final double normalizedLower = MathUtils.normalizeAngle(lower, FastMath.PI);
        final double normalizedUpper = normalizedLower + (upper - lower);
        final SubHyperplane<Sphere1D> lowerCut =
                new LimitAngle(new S1Point(normalizedLower), false, tolerance).wholeHyperplane();

        if (normalizedUpper <= MathUtils.TWO_PI) {
            // simple arc starting after 0 and ending before 2 \pi
            final SubHyperplane<Sphere1D> upperCut =
                    new LimitAngle(new S1Point(normalizedUpper), true, tolerance).wholeHyperplane();
            return new BSPTree<Sphere1D>(lowerCut,
                                         new BSPTree<Sphere1D>(Boolean.FALSE),
                                         new BSPTree<Sphere1D>(upperCut,
                                                               new BSPTree<Sphere1D>(Boolean.FALSE),
                                                               new BSPTree<Sphere1D>(Boolean.TRUE),
                                                               null),
                                         null);
        } else {
            // arc wrapping around 2 \pi
            final SubHyperplane<Sphere1D> upperCut =
                    new LimitAngle(new S1Point(normalizedUpper - MathUtils.TWO_PI), true, tolerance).wholeHyperplane();
            return new BSPTree<Sphere1D>(lowerCut,
                                         new BSPTree<Sphere1D>(upperCut,
                                                               new BSPTree<Sphere1D>(Boolean.FALSE),
                                                               new BSPTree<Sphere1D>(Boolean.TRUE),
                                                               null),
                                         new BSPTree<Sphere1D>(Boolean.TRUE),
                                         null);
        }

    }

    /** Get the tolerance below which angles are considered identical.
     * @return tolerance below which angles are considered identical
     */
    public double getTolerance() {
        return tolerance;
    }

    /** Get the smallest internal node.
     * @return smallest internal node (i.e. first after 0.0 radians, in trigonometric direction),
     * or null if there are no internal nodes (i.e. the set is either empty or covers the full circle)
     */
    public LimitAngle getSmallestLimit() {

        // start search at the tree root
        BSPTree<Sphere1D> node = getTree(false);
        if (node.getCut() == null) {
            return null;
        }

        BSPTree<Sphere1D> previous = previousNode(node);
        while (previous != null) {
            node = previous;
            previous = previousNode(node);
        }

        return (LimitAngle) node.getCut().getHyperplane();

    }

    /** Get the largest limit angle in the set.
     * @return largest limit angle (i.e. last before or at \(2 \pi) radians, in trigonometric direction),
     * or null if there are no limits (i.e. the set is either empty or covers the full circle)
     */
    public LimitAngle getLargestLimit() {

        // start search at the tree root
        BSPTree<Sphere1D> node = getTree(false);
        if (node.getCut() == null) {
            return null;
        }

        BSPTree<Sphere1D> next = nextNode(node);
        while (next != null) {
            node = next;
            next = nextNode(node);
        }

        return (LimitAngle) node.getCut().getHyperplane();

    }

    /** Get the next internal node.
     * @param node current node
     * @return next internal node in trigonometric order, or null
     * if this is the last internal node
     */
    private BSPTree<Sphere1D> nextNode(BSPTree<Sphere1D> node) {

        final BSPTree<Sphere1D> nextDeeper =
                ((LimitAngle) node.getCut().getHyperplane()).isDirect() ?
                node.getPlus() : node.getMinus();

        if (nextDeeper.getCut() != null) {
            // the next node is in the sub-tree
            return findSmallest(nextDeeper);
        }

        // there is nothing left deeper in the tree, we backtrack
        while (isAfterParent(node)) {
            node = node.getParent();
        }
        return node.getParent();

    }

    /** Get the previous internal node.
     * @param node current node
     * @return previous internal node in trigonometric order, or null
     * if this is the first internal node
     */
    private BSPTree<Sphere1D> previousNode(BSPTree<Sphere1D> node) {

        final BSPTree<Sphere1D> nextDeeper =
                ((LimitAngle) node.getCut().getHyperplane()).isDirect() ?
                node.getMinus() : node.getPlus();

        if (nextDeeper.getCut() != null) {
            // the next node is in the sub-tree
            return findLargest(nextDeeper);
        }

        // there is nothing left deeper in the tree, we backtrack
        while (isBeforeParent(node)) {
            node = node.getParent();
        }
        return node.getParent();

    }

    /** Check if a node is the child before its parent in trigonometric order.
     * @param node child node considered
     * @return true is the node has a parent end is before it in trigonometric order
     */
    private boolean isBeforeParent(final BSPTree<Sphere1D> node) {
        final BSPTree<Sphere1D> parent = node.getParent();
        if (parent == null) {
            return false;
        }
        if (((LimitAngle) parent.getCut().getHyperplane()).isDirect()) {
            // smaller angles are on minus side, larger angles are on plus side
            return node == parent.getMinus();
        } else {
            // smaller angles are on plus side, larger angles are on minus side
            return node == parent.getPlus();
        }
    }

    /** Check if a node is the child after its parent in trigonometric order.
     * @param node child node considered
     * @return true is the node has a parent end is after it in trigonometric order
     */
    private boolean isAfterParent(final BSPTree<Sphere1D> node) {
        final BSPTree<Sphere1D> parent = node.getParent();
        if (parent == null) {
            return false;
        }
        if (((LimitAngle) parent.getCut().getHyperplane()).isDirect()) {
            // smaller angles are on minus side, larger angles are on plus side
            return node == parent.getPlus();
        } else {
            // smaller angles are on plus side, larger angles are on minus side
            return node == parent.getMinus();
        }
    }

    /** Find the smallest internal node in a sub-tree.
     * @param node node at which the sub-tree starts
     * @return smallest internal node (in trigonometric order), may be the
     * provided node if no smaller internal node exist
     */
    private BSPTree<Sphere1D> findSmallest(BSPTree<Sphere1D> node) {

        BSPTree<Sphere1D> internal = null;

        while (node.getCut() != null) {
            internal = node;
            if (((LimitAngle) node.getCut().getHyperplane()).isDirect()) {
                // smaller angles are on minus side, larger angles are on plus side
                node = node.getMinus();
            } else {
                // smaller angles are on plus side, larger angles are on minus side
                node = node.getPlus();
            }
        }

        return internal;

    }

    /** Find the largest internal node in a sub-tree.
     * @param node node at which the sub-tree starts
     * @return largest internal node (in trigonometric order), may be the
     * provided node if no larger internal node exist
     */
    private BSPTree<Sphere1D> findLargest(BSPTree<Sphere1D> node) {

        BSPTree<Sphere1D> internal = null;

        while (node.getCut() != null) {
            internal = node;
            if (((LimitAngle) node.getCut().getHyperplane()).isDirect()) {
                // smaller angles are on minus side, larger angles are on plus side
                node = node.getPlus();
            } else {
                // smaller angles are on plus side, larger angles are on minus side
                node = node.getMinus();
            }
        }

        return internal;

    }

    /** {@inheritDoc} */
    @Override
    public ArcsSet buildNew(final BSPTree<Sphere1D> tree) {
        return new ArcsSet(tree, tolerance);
    }

    /** {@inheritDoc} */
    @Override
    protected void computeGeometricalProperties() {
        if (getTree(false).getCut() == null) {
            setBarycenter(S1Point.NaN);
            setSize(((Boolean) getTree(false).getAttribute()) ? MathUtils.TWO_PI : 0);
        } else {
            double size = 0.0;
            double sum  = 0.0;
            for (final Arc arc : asList()) {
                size += arc.getSize();
                sum  += arc.getSize() * arc.getBarycenter();
            }
            setSize(size);
            if (Precision.equals(size, MathUtils.TWO_PI, 0)) {
                setBarycenter(S1Point.NaN);
            } else if (size >= Precision.SAFE_MIN) {
                setBarycenter(new S1Point(sum / size));
            } else {
                final LimitAngle limit = (LimitAngle) getTree(false).getCut().getHyperplane();
                setBarycenter(limit.getLocation());
            }
        }
    }

    /** Build an ordered list of arcs representing the instance.
     * <p>This method builds this arcs set as an ordered list of
     * {@link Arc Arc} elements. An empty tree will build an empty list
     * while a tree representing the whole circle will build a one
     * element list with bounds set to \( 0 and 2 \pi \).</p>
     * @return a new ordered list containing {@link Arc Arc} elements
     */
    public List<Arc> asList() {

        final List<Arc> list = new ArrayList<Arc>();
        final BSPTree<Sphere1D> root = getTree(false);

        if (root.getCut() == null) {
            // the tree has a single node
            if ((Boolean) root.getAttribute()) {
                // it is an inside node, it represents the full circle
                list.add(new Arc(0.0, 0.0, tolerance)); // since lower == upper, the arc covers the full circle
            }
        } else {

            // find all arcs limits
            final LimitsCollector finder = new LimitsCollector();
            root.visit(finder);
            final List<Double> limits = finder.getLimits();
            if (limits.size() < 2) {
                // the start and end angle collapsed to the same value, its the full circle again
                list.add(new Arc(0.0, 0.0, tolerance)); // since lower == upper, the arc covers the full circle
                return list;
            }

            // sort them so the first angle is an arc start
            Collections.sort(limits);
            if (checkPoint(new S1Point(0.5 * (limits.get(0) + limits.get(1)))) == Location.OUTSIDE) {
                // the first angle is not an arc start, its the last arc end
                // move it properly to the end
                limits.add(limits.remove(0) + MathUtils.TWO_PI);
            }

            // we can now build the list
            for (int i = 0; i < limits.size(); i += 2) {
                list.add(new Arc(limits.get(i), limits.get(i + 1), tolerance));
            }

        }

        return list;

    }

    /** Visitor looking for arc limits. */
    private final class LimitsCollector implements BSPTreeVisitor<Sphere1D> {

        /** Collected limits. */
        private List<Double> limits;

        /** Simple constructor. */
        public LimitsCollector() {
            this.limits = new ArrayList<Double>();
        }

        /** {@inheritDoc} */
        public BSPTreeVisitor.Order visitOrder(final BSPTree<Sphere1D> node) {
            return Order.MINUS_PLUS_SUB;
        }

        /** {@inheritDoc} */
        public void visitInternalNode(final BSPTree<Sphere1D> node) {
            // check if the chord end points are arc limits
            final LimitAngle limit = (LimitAngle) node.getCut().getHyperplane();
            if (checkPoint(limit.getLocation()) == Location.BOUNDARY) {
                limits.add(limit.getLocation().getAlpha());
            }
        }

        /** {@inheritDoc} */
        public void visitLeafNode(final BSPTree<Sphere1D> node) {
        }

        /** Get the collected limits.
         * @return collected limits
         */
        public List<Double> getLimits() {
            return limits;
        }

    }

}
