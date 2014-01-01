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
import java.util.List;

import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.partitioning.AbstractRegion;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
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

    /** Get the node corresponding to the first arc start.
     * @return smallest internal node (i.e. first after 0.0 radians, in trigonometric direction),
     * or null if there are no internal nodes (i.e. the set is either empty or covers the full circle)
     */
    private BSPTree<Sphere1D> getFirstArcStart() {

        // start search at the tree root
        BSPTree<Sphere1D> node = getTree(false);
        if (node.getCut() == null) {
            return null;
        }

        // walk tree until we find the smallest internal node
        BSPTree<Sphere1D> previous = previousNode(node);
        while (previous != null) {
            node = previous;
            previous = previousNode(node);
        }

        // walk tree until we find an arc start
        while (node != null && !isArcStart(node)) {
            node = nextNode(node);
        }

        return node;

    }

    /** Check if a node corresponds to the start angle of an arc.
     * @param node node to check
     * @return true if the node corresponds to the start angle of an arc
     */
    private boolean isArcStart(final BSPTree<Sphere1D> node) {

        if (node.getCut() == null) {
            // it's not even a limit angle, it cannot start an arc!
            return false;
        }

        if ((Boolean) leafBefore(node).getAttribute()) {
            // it has an inside cell before it, it may end an arc but not start it
            return false;
        }

        if (!(Boolean) leafAfter(node).getAttribute()) {
            // it has an outside cell after it, it is a dummy cut away from real arcs
            return false;
        }

        // the cell defines a limit angle, with an outside before and an inside after
        // it is the start of an arc
        return true;

    }

    /** Check if a node corresponds to the end angle of an arc.
     * @param node node to check
     * @return true if the node corresponds to the end angle of an arc
     */
    private boolean isArcEnd(final BSPTree<Sphere1D> node) {

        if (node.getCut() == null) {
            // it's not even a limit angle, it cannot start an arc!
            return false;
        }

        if (!(Boolean) leafBefore(node).getAttribute()) {
            // it has an outside cell before it, it may start an arc but not end it
            return false;
        }

        if ((Boolean) leafAfter(node).getAttribute()) {
            // it has an inside cell after it, it is a dummy cut in the middle of an arc
            return false;
        }

        // the cell defines a limit angle, with an inside before and an outside after
        // it is the end of an arc
        return true;

    }

    /** Get the next internal node.
     * @param node current node
     * @return next internal node in trigonometric order, or null
     * if this is the last internal node
     */
    private BSPTree<Sphere1D> nextNode(BSPTree<Sphere1D> node) {

        if (childAfter(node).getCut() != null) {
            // the next node is in the sub-tree
            return leafAfter(node).getParent();
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

        if (childBefore(node).getCut() != null) {
            // the next node is in the sub-tree
            return leafBefore(node).getParent();
        }

        // there is nothing left deeper in the tree, we backtrack
        while (isBeforeParent(node)) {
            node = node.getParent();
        }
        return node.getParent();

    }

    /** Find the leaf node just before an internal node.
     * @param node node at which the sub-tree starts
     * @return leaf node just before the internal node
     */
    private BSPTree<Sphere1D> leafBefore(BSPTree<Sphere1D> node) {

        if (node.getCut() != null) {
            node = childBefore(node);
        }

        while (node.getCut() != null) {
            node = childAfter(node);
        }

        return node;

    }

    /** Find the leaf node just after an internal node.
     * @param node node at which the sub-tree starts
     * @return leaf node just after the internal node
     */
    private BSPTree<Sphere1D> leafAfter(BSPTree<Sphere1D> node) {

        if (node.getCut() != null) {
            node = childAfter(node);
        }

        while (node.getCut() != null) {
            node = childBefore(node);
        }

        return node;

    }

    /** Check if a node is the child before its parent in trigonometric order.
     * @param node child node considered
     * @return true is the node has a parent end is before it in trigonometric order
     */
    private boolean isBeforeParent(final BSPTree<Sphere1D> node) {
        final BSPTree<Sphere1D> parent = node.getParent();
        if (parent == null) {
            return false;
        } else {
            return node == childBefore(parent);
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
        } else {
            return node == childAfter(parent);
        }
    }

    /** Find the child node just before an internal node.
     * @param node node at which the sub-tree starts
     * @return child node just before the internal node
     */
    private BSPTree<Sphere1D> childBefore(BSPTree<Sphere1D> node) {
        if (isDirect(node)) {
            // smaller angles are on minus side, larger angles are on plus side
            return node.getMinus();
        } else {
            // smaller angles are on plus side, larger angles are on minus side
            return node.getPlus();
        }
    }

    /** Find the child node just after an internal node.
     * @param node node at which the sub-tree starts
     * @return child node just after the internal node
     */
    private BSPTree<Sphere1D> childAfter(BSPTree<Sphere1D> node) {
        if (isDirect(node)) {
            // smaller angles are on minus side, larger angles are on plus side
            return node.getPlus();
        } else {
            // smaller angles are on plus side, larger angles are on minus side
            return node.getMinus();
        }
    }

    /** Check if an internal node has a direct limit angle.
     * @param node node to check
     * @return true if the limit angle is direct
     */
    private boolean isDirect(final BSPTree<Sphere1D> node) {
        return ((LimitAngle) node.getCut().getHyperplane()).isDirect();
    }

    /** Get the limit angle of a node.
     * @param node node to check
     * @return true if the limit angle is direct
     */
    private double getAngle(final BSPTree<Sphere1D> node) {
        return ((LimitAngle) node.getCut().getHyperplane()).getLocation().getAlpha();
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
        final BSPTree<Sphere1D> firstStart = getFirstArcStart();

        if (firstStart == null) {
                // the tree has a single node
                if ((Boolean) getTree(false).getAttribute()) {
                    // it is an inside node, it represents the full circle
                    list.add(new Arc(0.0, 0.0, tolerance)); // since lower == upper, the arc covers the full circle
                }
        } else if (previousNode(firstStart) == null && nextNode(firstStart) == null) {
            // the tree is a degenerate tree (probably build from a custom collection of hyperplanes) with a single cut
            // we ignore the cut and consider the tree represents the full circle
            list.add(new Arc(0.0, 0.0, tolerance)); // since lower == upper, the arc covers the full circle
        } else {

            // find all arcs
            BSPTree<Sphere1D> start = firstStart;
            while (start != null) {

                // look for the end of the current arc
                BSPTree<Sphere1D> end = start;
                while (end != null && !isArcEnd(end)) {
                    end = nextNode(end);
                }

                if (end != null) {

                    // we have identified the current arc
                    list.add(new Arc(getAngle(start), getAngle(end), tolerance));

                    // prepare search for next arc
                    start = end;
                    while (start != null && !isArcStart(start)) {
                        start = nextNode(start);
                    }

                } else {
                    // the final arc wraps around 2\pi, its end is before the first start
                    end = firstStart;
                    while (end != null && !isArcEnd(end)) {
                        end = previousNode(end);
                    }
                    if (end == null) {
                        // this should never happen
                        throw new MathInternalError();
                    }

                    // we have identified the last arc
                    list.add(new Arc(getAngle(start), getAngle(end) + MathUtils.TWO_PI, tolerance));

                    // stop the loop
                    start = null;

                }

            }

        }

        return list;

    }

}
