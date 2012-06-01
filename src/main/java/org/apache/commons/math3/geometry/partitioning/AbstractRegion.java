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
package org.apache.commons.math3.geometry.partitioning;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.geometry.Space;
import org.apache.commons.math3.geometry.Vector;

/** Abstract class for all regions, independently of geometry type or dimension.

 * @param <S> Type of the space.
 * @param <T> Type of the sub-space.

 * @version $Id$
 * @since 3.0
 */
public abstract class AbstractRegion<S extends Space, T extends Space> implements Region<S> {

    /** Inside/Outside BSP tree. */
    private BSPTree<S> tree;

    /** Size of the instance. */
    private double size;

    /** Barycenter. */
    private Vector<S> barycenter;

    /** Build a region representing the whole space.
     */
    protected AbstractRegion() {
        tree = new BSPTree<S>(Boolean.TRUE);
    }

    /** Build a region from an inside/outside BSP tree.
     * <p>The leaf nodes of the BSP tree <em>must</em> have a
     * {@code Boolean} attribute representing the inside status of
     * the corresponding cell (true for inside cells, false for outside
     * cells). In order to avoid building too many small objects, it is
     * recommended to use the predefined constants
     * {@code Boolean.TRUE} and {@code Boolean.FALSE}. The
     * tree also <em>must</em> have either null internal nodes or
     * internal nodes representing the boundary as specified in the
     * {@link #getTree getTree} method).</p>
     * @param tree inside/outside BSP tree representing the region
     */
    protected AbstractRegion(final BSPTree<S> tree) {
        this.tree = tree;
    }

    /** Build a Region from a Boundary REPresentation (B-rep).
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
     * calls to the {@link #checkPoint(Vector) checkPoint} method will not be
     * meaningful anymore.</p>
     * <p>If the boundary is empty, the region will represent the whole
     * space.</p>
     * @param boundary collection of boundary elements, as a
     * collection of {@link SubHyperplane SubHyperplane} objects
     */
    protected AbstractRegion(final Collection<SubHyperplane<S>> boundary) {

        if (boundary.size() == 0) {

            // the tree represents the whole space
            tree = new BSPTree<S>(Boolean.TRUE);

        } else {

            // sort the boundary elements in decreasing size order
            // (we don't want equal size elements to be removed, so
            // we use a trick to fool the TreeSet)
            final TreeSet<SubHyperplane<S>> ordered = new TreeSet<SubHyperplane<S>>(new Comparator<SubHyperplane<S>>() {
                public int compare(final SubHyperplane<S> o1, final SubHyperplane<S> o2) {
                    final double size1 = o1.getSize();
                    final double size2 = o2.getSize();
                    return (size2 < size1) ? -1 : ((o1 == o2) ? 0 : +1);
                }
            });
            ordered.addAll(boundary);

            // build the tree top-down
            tree = new BSPTree<S>();
            insertCuts(tree, ordered);

            // set up the inside/outside flags
            tree.visit(new BSPTreeVisitor<S>() {

                /** {@inheritDoc} */
                public Order visitOrder(final BSPTree<S> node) {
                    return Order.PLUS_SUB_MINUS;
                }

                /** {@inheritDoc} */
                public void visitInternalNode(final BSPTree<S> node) {
                }

                /** {@inheritDoc} */
                public void visitLeafNode(final BSPTree<S> node) {
                    node.setAttribute((node == node.getParent().getPlus()) ?
                                                                            Boolean.FALSE : Boolean.TRUE);
                }
            });

        }

    }

    /** Build a convex region from an array of bounding hyperplanes.
     * @param hyperplanes array of bounding hyperplanes (if null, an
     * empty region will be built)
     */
    public AbstractRegion(final Hyperplane<S>[] hyperplanes) {
        if ((hyperplanes == null) || (hyperplanes.length == 0)) {
            tree = new BSPTree<S>(Boolean.FALSE);
        } else {

            // use the first hyperplane to build the right class
            tree = hyperplanes[0].wholeSpace().getTree(false);

            // chop off parts of the space
            BSPTree<S> node = tree;
            node.setAttribute(Boolean.TRUE);
            for (final Hyperplane<S> hyperplane : hyperplanes) {
                if (node.insertCut(hyperplane)) {
                    node.setAttribute(null);
                    node.getPlus().setAttribute(Boolean.FALSE);
                    node = node.getMinus();
                    node.setAttribute(Boolean.TRUE);
                }
            }

        }

    }

    /** {@inheritDoc} */
    public abstract AbstractRegion<S, T> buildNew(BSPTree<S> newTree);

    /** Recursively build a tree by inserting cut sub-hyperplanes.
     * @param node current tree node (it is a leaf node at the beginning
     * of the call)
     * @param boundary collection of edges belonging to the cell defined
     * by the node
     */
    private void insertCuts(final BSPTree<S> node, final Collection<SubHyperplane<S>> boundary) {

        final Iterator<SubHyperplane<S>> iterator = boundary.iterator();

        // build the current level
        Hyperplane<S> inserted = null;
        while ((inserted == null) && iterator.hasNext()) {
            inserted = iterator.next().getHyperplane();
            if (!node.insertCut(inserted.copySelf())) {
                inserted = null;
            }
        }

        if (!iterator.hasNext()) {
            return;
        }

        // distribute the remaining edges in the two sub-trees
        final ArrayList<SubHyperplane<S>> plusList  = new ArrayList<SubHyperplane<S>>();
        final ArrayList<SubHyperplane<S>> minusList = new ArrayList<SubHyperplane<S>>();
        while (iterator.hasNext()) {
            final SubHyperplane<S> other = iterator.next();
            switch (other.side(inserted)) {
            case PLUS:
                plusList.add(other);
                break;
            case MINUS:
                minusList.add(other);
                break;
            case BOTH:
                final SubHyperplane.SplitSubHyperplane<S> split = other.split(inserted);
                plusList.add(split.getPlus());
                minusList.add(split.getMinus());
                break;
            default:
                // ignore the sub-hyperplanes belonging to the cut hyperplane
            }
        }

        // recurse through lower levels
        insertCuts(node.getPlus(),  plusList);
        insertCuts(node.getMinus(), minusList);

    }

    /** {@inheritDoc} */
    public AbstractRegion<S, T> copySelf() {
        return buildNew(tree.copySelf());
    }

    /** {@inheritDoc} */
    public boolean isEmpty() {
        return isEmpty(tree);
    }

    /** {@inheritDoc} */
    public boolean isEmpty(final BSPTree<S> node) {

        // we use a recursive function rather than the BSPTreeVisitor
        // interface because we can stop visiting the tree as soon as we
        // have found an inside cell

        if (node.getCut() == null) {
            // if we find an inside node, the region is not empty
            return !((Boolean) node.getAttribute());
        }

        // check both sides of the sub-tree
        return isEmpty(node.getMinus()) && isEmpty(node.getPlus());

    }

    /** {@inheritDoc} */
    public boolean contains(final Region<S> region) {
        return new RegionFactory<S>().difference(region, this).isEmpty();
    }

    /** {@inheritDoc} */
    public Location checkPoint(final Vector<S> point) {
        return checkPoint(tree, point);
    }

    /** Check a point with respect to the region starting at a given node.
     * @param node root node of the region
     * @param point point to check
     * @return a code representing the point status: either {@link
     * Region.Location#INSIDE INSIDE}, {@link Region.Location#OUTSIDE
     * OUTSIDE} or {@link Region.Location#BOUNDARY BOUNDARY}
     */
    protected Location checkPoint(final BSPTree<S> node, final Vector<S> point) {
        final BSPTree<S> cell = node.getCell(point);
        if (cell.getCut() == null) {
            // the point is in the interior of a cell, just check the attribute
            return ((Boolean) cell.getAttribute()) ? Location.INSIDE : Location.OUTSIDE;
        }

        // the point is on a cut-sub-hyperplane, is it on a boundary ?
        final Location minusCode = checkPoint(cell.getMinus(), point);
        final Location plusCode  = checkPoint(cell.getPlus(),  point);
        return (minusCode == plusCode) ? minusCode : Location.BOUNDARY;

    }

    /** {@inheritDoc} */
    public BSPTree<S> getTree(final boolean includeBoundaryAttributes) {
        if (includeBoundaryAttributes && (tree.getCut() != null) && (tree.getAttribute() == null)) {
            // we need to compute the boundary attributes
            tree.visit(new BoundaryBuilder<S>());
        }
        return tree;
    }

    /** Visitor building boundary shell tree.
     * <p>
     * The boundary shell is represented as {@link BoundaryAttribute boundary attributes}
     * at each internal node.
     * </p>
     */
    private static class BoundaryBuilder<S extends Space> implements BSPTreeVisitor<S> {

        /** {@inheritDoc} */
        public Order visitOrder(BSPTree<S> node) {
            return Order.PLUS_MINUS_SUB;
        }

        /** {@inheritDoc} */
        public void visitInternalNode(BSPTree<S> node) {

            SubHyperplane<S> plusOutside = null;
            SubHyperplane<S> plusInside  = null;

            // characterize the cut sub-hyperplane,
            // first with respect to the plus sub-tree
            @SuppressWarnings("unchecked")
            final SubHyperplane<S>[] plusChar = (SubHyperplane<S>[]) Array.newInstance(SubHyperplane.class, 2);
            characterize(node.getPlus(), node.getCut().copySelf(), plusChar);

            if (plusChar[0] != null && !plusChar[0].isEmpty()) {
                // plusChar[0] corresponds to a subset of the cut sub-hyperplane known to have
                // outside cells on its plus side, we want to check if parts of this subset
                // do have inside cells on their minus side
                @SuppressWarnings("unchecked")
                final SubHyperplane<S>[] minusChar = (SubHyperplane<S>[]) Array.newInstance(SubHyperplane.class, 2);
                characterize(node.getMinus(), plusChar[0], minusChar);
                if (minusChar[1] != null && !minusChar[1].isEmpty()) {
                    // this part belongs to the boundary,
                    // it has the outside on its plus side and the inside on its minus side
                    plusOutside = minusChar[1];
                }
            }

            if (plusChar[1] != null && !plusChar[1].isEmpty()) {
                // plusChar[1] corresponds to a subset of the cut sub-hyperplane known to have
                // inside cells on its plus side, we want to check if parts of this subset
                // do have outside cells on their minus side
                @SuppressWarnings("unchecked")
                final SubHyperplane<S>[] minusChar = (SubHyperplane<S>[]) Array.newInstance(SubHyperplane.class, 2);
                characterize(node.getMinus(), plusChar[1], minusChar);
                if (minusChar[0] != null && !minusChar[0].isEmpty()) {
                    // this part belongs to the boundary,
                    // it has the inside on its plus side and the outside on its minus side
                    plusInside = minusChar[0];
                }
            }

            // set the boundary attribute at non-leaf nodes
            node.setAttribute(new BoundaryAttribute<S>(plusOutside, plusInside));

        }

        /** {@inheritDoc} */
        public void visitLeafNode(BSPTree<S> node) {
        }

        /** Filter the parts of an hyperplane belonging to the boundary.
         * <p>The filtering consist in splitting the specified
         * sub-hyperplane into several parts lying in inside and outside
         * cells of the tree. The principle is to call this method twice for
         * each cut sub-hyperplane in the tree, once one the plus node and
         * once on the minus node. The parts that have the same flag
         * (inside/inside or outside/outside) do not belong to the boundary
         * while parts that have different flags (inside/outside or
         * outside/inside) do belong to the boundary.</p>
         * @param node current BSP tree node
         * @param sub sub-hyperplane to characterize
         * @param characterization placeholder where to put the characterized parts
         */
        private void characterize(final BSPTree<S> node, final SubHyperplane<S> sub,
                                  final SubHyperplane<S>[] characterization) {
            if (node.getCut() == null) {
                // we have reached a leaf node
                final boolean inside = (Boolean) node.getAttribute();
                if (inside) {
                    if (characterization[1] == null) {
                        characterization[1] = sub;
                    } else {
                        characterization[1] = characterization[1].reunite(sub);
                    }
                } else {
                    if (characterization[0] == null) {
                        characterization[0] = sub;
                    } else {
                        characterization[0] = characterization[0].reunite(sub);
                    }
                }
            } else {
                final Hyperplane<S> hyperplane = node.getCut().getHyperplane();
                switch (sub.side(hyperplane)) {
                case PLUS:
                    characterize(node.getPlus(), sub, characterization);
                    break;
                case MINUS:
                    characterize(node.getMinus(), sub, characterization);
                    break;
                case BOTH:
                    final SubHyperplane.SplitSubHyperplane<S> split = sub.split(hyperplane);
                    characterize(node.getPlus(),  split.getPlus(),  characterization);
                    characterize(node.getMinus(), split.getMinus(), characterization);
                    break;
                default:
                    // this should not happen
                    throw new MathInternalError();
                }
            }
        }

    }

    /** {@inheritDoc} */
    public double getBoundarySize() {
        final BoundarySizeVisitor<S> visitor = new BoundarySizeVisitor<S>();
        getTree(true).visit(visitor);
        return visitor.getSize();
    }

    /** {@inheritDoc} */
    public double getSize() {
        if (barycenter == null) {
            computeGeometricalProperties();
        }
        return size;
    }

    /** Set the size of the instance.
     * @param size size of the instance
     */
    protected void setSize(final double size) {
        this.size = size;
    }

    /** {@inheritDoc} */
    public Vector<S> getBarycenter() {
        if (barycenter == null) {
            computeGeometricalProperties();
        }
        return barycenter;
    }

    /** Set the barycenter of the instance.
     * @param barycenter barycenter of the instance
     */
    protected void setBarycenter(final Vector<S> barycenter) {
        this.barycenter = barycenter;
    }

    /** Compute some geometrical properties.
     * <p>The properties to compute are the barycenter and the size.</p>
     */
    protected abstract void computeGeometricalProperties();

    /** {@inheritDoc} */
    public Side side(final Hyperplane<S> hyperplane) {
        final Sides sides = new Sides();
        recurseSides(tree, hyperplane.wholeHyperplane(), sides);
        return sides.plusFound() ?
              (sides.minusFound() ? Side.BOTH  : Side.PLUS) :
              (sides.minusFound() ? Side.MINUS : Side.HYPER);
    }

    /** Search recursively for inside leaf nodes on each side of the given hyperplane.

     * <p>The algorithm used here is directly derived from the one
     * described in section III (<i>Binary Partitioning of a BSP
     * Tree</i>) of the Bruce Naylor, John Amanatides and William
     * Thibault paper <a
     * href="http://www.cs.yorku.ca/~amana/research/bsptSetOp.pdf">Merging
     * BSP Trees Yields Polyhedral Set Operations</a> Proc. Siggraph
     * '90, Computer Graphics 24(4), August 1990, pp 115-124, published
     * by the Association for Computing Machinery (ACM)..</p>

     * @param node current BSP tree node
     * @param sub sub-hyperplane
     * @param sides object holding the sides found
     */
    private void recurseSides(final BSPTree<S> node, final SubHyperplane<S> sub, final Sides sides) {

        if (node.getCut() == null) {
            if ((Boolean) node.getAttribute()) {
                // this is an inside cell expanding across the hyperplane
                sides.rememberPlusFound();
                sides.rememberMinusFound();
            }
            return;
        }

        final Hyperplane<S> hyperplane = node.getCut().getHyperplane();
        switch (sub.side(hyperplane)) {
        case PLUS :
            // the sub-hyperplane is entirely in the plus sub-tree
            if (node.getCut().side(sub.getHyperplane()) == Side.PLUS) {
                if (!isEmpty(node.getMinus())) {
                    sides.rememberPlusFound();
                }
            } else {
                if (!isEmpty(node.getMinus())) {
                    sides.rememberMinusFound();
                }
            }
            if (!(sides.plusFound() && sides.minusFound())) {
                recurseSides(node.getPlus(), sub, sides);
            }
            break;
        case MINUS :
            // the sub-hyperplane is entirely in the minus sub-tree
            if (node.getCut().side(sub.getHyperplane()) == Side.PLUS) {
                if (!isEmpty(node.getPlus())) {
                    sides.rememberPlusFound();
                }
            } else {
                if (!isEmpty(node.getPlus())) {
                    sides.rememberMinusFound();
                }
            }
            if (!(sides.plusFound() && sides.minusFound())) {
                recurseSides(node.getMinus(), sub, sides);
            }
            break;
        case BOTH :
            // the sub-hyperplane extends in both sub-trees
            final SubHyperplane.SplitSubHyperplane<S> split = sub.split(hyperplane);

            // explore first the plus sub-tree
            recurseSides(node.getPlus(), split.getPlus(), sides);

            // if needed, explore the minus sub-tree
            if (!(sides.plusFound() && sides.minusFound())) {
                recurseSides(node.getMinus(), split.getMinus(), sides);
            }
            break;
        default :
            // the sub-hyperplane and the cut sub-hyperplane share the same hyperplane
            if (node.getCut().getHyperplane().sameOrientationAs(sub.getHyperplane())) {
                if ((node.getPlus().getCut() != null) || ((Boolean) node.getPlus().getAttribute())) {
                    sides.rememberPlusFound();
                }
                if ((node.getMinus().getCut() != null) || ((Boolean) node.getMinus().getAttribute())) {
                    sides.rememberMinusFound();
                }
            } else {
                if ((node.getPlus().getCut() != null) || ((Boolean) node.getPlus().getAttribute())) {
                    sides.rememberMinusFound();
                }
                if ((node.getMinus().getCut() != null) || ((Boolean) node.getMinus().getAttribute())) {
                    sides.rememberPlusFound();
                }
            }
        }

    }

    /** Utility class holding the already found sides. */
    private static final class Sides {

        /** Indicator of inside leaf nodes found on the plus side. */
        private boolean plusFound;

        /** Indicator of inside leaf nodes found on the plus side. */
        private boolean minusFound;

        /** Simple constructor.
         */
        public Sides() {
            plusFound  = false;
            minusFound = false;
        }

        /** Remember the fact that inside leaf nodes have been found on the plus side.
         */
        public void rememberPlusFound() {
            plusFound = true;
        }

        /** Check if inside leaf nodes have been found on the plus side.
         * @return true if inside leaf nodes have been found on the plus side
         */
        public boolean plusFound() {
            return plusFound;
        }

        /** Remember the fact that inside leaf nodes have been found on the minus side.
         */
        public void rememberMinusFound() {
            minusFound = true;
        }

        /** Check if inside leaf nodes have been found on the minus side.
         * @return true if inside leaf nodes have been found on the minus side
         */
        public boolean minusFound() {
            return minusFound;
        }

    }

    /** {@inheritDoc} */
    public SubHyperplane<S> intersection(final SubHyperplane<S> sub) {
        return recurseIntersection(tree, sub);
    }

    /** Recursively compute the parts of a sub-hyperplane that are
     * contained in the region.
     * @param node current BSP tree node
     * @param sub sub-hyperplane traversing the region
     * @return filtered sub-hyperplane
     */
    private SubHyperplane<S> recurseIntersection(final BSPTree<S> node, final SubHyperplane<S> sub) {

        if (node.getCut() == null) {
            return (Boolean) node.getAttribute() ? sub.copySelf() : null;
        }

        final Hyperplane<S> hyperplane = node.getCut().getHyperplane();
        switch (sub.side(hyperplane)) {
        case PLUS :
            return recurseIntersection(node.getPlus(), sub);
        case MINUS :
            return recurseIntersection(node.getMinus(), sub);
        case BOTH :
            final SubHyperplane.SplitSubHyperplane<S> split = sub.split(hyperplane);
            final SubHyperplane<S> plus  = recurseIntersection(node.getPlus(),  split.getPlus());
            final SubHyperplane<S> minus = recurseIntersection(node.getMinus(), split.getMinus());
            if (plus == null) {
                return minus;
            } else if (minus == null) {
                return plus;
            } else {
                return plus.reunite(minus);
            }
        default :
            return recurseIntersection(node.getPlus(),
                                       recurseIntersection(node.getMinus(), sub));
        }

    }

    /** Transform a region.
     * <p>Applying a transform to a region consist in applying the
     * transform to all the hyperplanes of the underlying BSP tree and
     * of the boundary (and also to the sub-hyperplanes embedded in
     * these hyperplanes) and to the barycenter. The instance is not
     * modified, a new instance is built.</p>
     * @param transform transform to apply
     * @return a new region, resulting from the application of the
     * transform to the instance
     */
    public AbstractRegion<S, T> applyTransform(final Transform<S, T> transform) {
        return buildNew(recurseTransform(getTree(false), transform));
    }

    /** Recursively transform an inside/outside BSP-tree.
     * @param node current BSP tree node
     * @param transform transform to apply
     * @return a new tree
     */
    @SuppressWarnings("unchecked")
    private BSPTree<S> recurseTransform(final BSPTree<S> node, final Transform<S, T> transform) {

        if (node.getCut() == null) {
            return new BSPTree<S>(node.getAttribute());
        }

        final SubHyperplane<S>  sub = node.getCut();
        final SubHyperplane<S> tSub = ((AbstractSubHyperplane<S, T>) sub).applyTransform(transform);
        BoundaryAttribute<S> attribute = (BoundaryAttribute<S>) node.getAttribute();
        if (attribute != null) {
            final SubHyperplane<S> tPO = (attribute.getPlusOutside() == null) ?
                null : ((AbstractSubHyperplane<S, T>) attribute.getPlusOutside()).applyTransform(transform);
            final SubHyperplane<S> tPI = (attribute.getPlusInside()  == null) ?
                null  : ((AbstractSubHyperplane<S, T>) attribute.getPlusInside()).applyTransform(transform);
            attribute = new BoundaryAttribute<S>(tPO, tPI);
        }

        return new BSPTree<S>(tSub,
                                    recurseTransform(node.getPlus(),  transform),
                                    recurseTransform(node.getMinus(), transform),
                                    attribute);

    }

}
