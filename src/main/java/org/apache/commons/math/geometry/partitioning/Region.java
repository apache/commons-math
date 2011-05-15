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
package org.apache.commons.math.geometry.partitioning;

import java.util.Collection;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ArrayList;

/** This class represent a region of a space as a partition.

 * <p>Region are subsets of a space, they can be infinite (whole
 * space, half space, infinite stripe ...) or finite (polygons in 2D,
 * polyhedrons in 3D ...). Their main characteristic is to separate
 * points that are considered to be <em>inside</em> the region from
 * points considered to be <em>outside</em> of it. In between, there
 * may be points on the <em>boundary</em> of the region.</p>

 * <p>This implementation is limited to regions for which the boundary
 * is composed of several {@link SubHyperplane sub-hyperplanes},
 * including regions with no boundary at all: the whole space and the
 * empty region. They are not necessarily finite and not necessarily
 * path-connected. They can contain holes.</p>

 * <p>Regions can be combined using the traditional sets operations :
 * union, intersection, difference and symetric difference (exclusive
 * or) for the binary operations, complement for the unary
 * operation.</p>

 * @version $Revision$ $Date$
 */
public abstract class Region {

    /** Enumerate for the location of a point with respect to the region. */
    public static enum Location {
        /** Code for points inside the partition. */
        INSIDE,

        /** Code for points outside of the partition. */
        OUTSIDE,

        /** Code for points on the partition boundary. */
        BOUNDARY;
    }

    /** Inside/Outside BSP tree. */
    private BSPTree tree;

    /** Size of the instance. */
    private double size;

    /** Barycenter. */
    private Point barycenter;

    /** Build a region representing the whole space.
     */
    protected Region() {
        tree = new BSPTree(Boolean.TRUE);
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
    protected Region(final BSPTree tree) {
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
     * calls to the {@link #checkPoint(Point) checkPoint} method will not be
     * meaningful anymore.</p>
     * <p>If the boundary is empty, the region will represent the whole
     * space.</p>
     * @param boundary collection of boundary elements, as a
     * collection of {@link SubHyperplane SubHyperplane} objects
     */
    protected Region(final Collection<SubHyperplane> boundary) {

        if (boundary.size() == 0) {

            // the tree represents the whole space
            tree = new BSPTree(Boolean.TRUE);

        } else {

            // sort the boundary elements in decreasing size order
            // (we don't want equal size elements to be removed, so
            // we use a trick to fool the TreeSet)
            final TreeSet<SubHyperplane> ordered = new TreeSet<SubHyperplane>(new Comparator<SubHyperplane>() {
                public int compare(final SubHyperplane o1, final SubHyperplane o2) {
                    final double size1 = o1.getRemainingRegion().getSize();
                    final double size2 = o2.getRemainingRegion().getSize();
                    return (size2 < size1) ? -1 : ((o1 == o2) ? 0 : +1);
                }
            });
            ordered.addAll(boundary);

            // build the tree top-down
            tree = new BSPTree();
            insertCuts(tree, ordered);

            // set up the inside/outside flags
            tree.visit(new BSPTreeVisitor() {

                /** {@inheritDoc} */
                public Order visitOrder(final BSPTree node) {
                    return Order.PLUS_SUB_MINUS;
                }

                /** {@inheritDoc} */
                public void visitInternalNode(final BSPTree node) {
                }

                /** {@inheritDoc} */
                public void visitLeafNode(final BSPTree node) {
                    node.setAttribute((node == node.getParent().getPlus()) ?
                                                                            Boolean.FALSE : Boolean.TRUE);
                }
            });

        }

    }

    /** Build a region using the instance as a prototype.
     * <p>This method allow to create new instances without knowing
     * exactly the type of the region. It is an application of the
     * prototype design pattern.</p>
     * <p>The leaf nodes of the BSP tree <em>must</em> have a
     * {@code Boolean} attribute representing the inside status of
     * the corresponding cell (true for inside cells, false for outside
     * cells). In order to avoid building too many small objects, it is
     * recommended to use the predefined constants
     * {@code Boolean.TRUE} and {@code Boolean.FALSE}. The
     * tree also <em>must</em> have either null internal nodes or
     * internal nodes representing the boundary as specified in the
     * {@link #getTree getTree} method).</p>
     * @param newTree inside/outside BSP tree representing the new region
     * @return the built region
     */
    public abstract Region buildNew(BSPTree newTree);

    /** Recursively build a tree by inserting cut sub-hyperplanes.
     * @param node current tree node (it is a leaf node at the beginning
     * of the call)
     * @param boundary collection of edges belonging to the cell defined
     * by the node
     */
    private void insertCuts(final BSPTree node, final Collection<SubHyperplane> boundary) {

        final Iterator<SubHyperplane> iterator = boundary.iterator();

        // build the current level
        Hyperplane inserted = null;
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
        final ArrayList<SubHyperplane> plusList  = new ArrayList<SubHyperplane>();
        final ArrayList<SubHyperplane> minusList = new ArrayList<SubHyperplane>();
        while (iterator.hasNext()) {
            final SubHyperplane other = iterator.next();
            switch (inserted.side(other)) {
            case PLUS:
                plusList.add(other);
                break;
            case MINUS:
                minusList.add(other);
                break;
            case BOTH:
                final Hyperplane.SplitSubHyperplane split = inserted.split(other);
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

    /** Build a convex region from a collection of bounding hyperplanes.
     * @param hyperplanes collection of bounding hyperplanes
     * @return a new convex region, or null if the collection is empty
     */
    public static Region buildConvex(final Collection<Hyperplane> hyperplanes) {
        if (hyperplanes.isEmpty()) {
            return null;
        }

        // use the first hyperplane to build the right class
        final Region region = hyperplanes.iterator().next().wholeSpace();

        // chop off parts of the space
        BSPTree node = region.tree;
        node.setAttribute(Boolean.TRUE);
        for (final Hyperplane hyperplane : hyperplanes) {
            if (node.insertCut(hyperplane)) {
                node.setAttribute(null);
                node.getPlus().setAttribute(Boolean.FALSE);
                node = node.getMinus();
                node.setAttribute(Boolean.TRUE);
            }
        }

        return region;

    }

    /** Copy the instance.
     * <p>The instance created is completely independant of the original
     * one. A deep copy is used, none of the underlying objects are
     * shared (except for the underlying tree {@code Boolean}
     * attributes and immutable objects).</p>
     * @return a new region, copy of the instance
     */
    public Region copySelf() {
        return buildNew(tree.copySelf());
    }

    /** Check if the instance is empty.
     * @return true if the instance is empty
     */
    public boolean isEmpty() {
        return isEmpty(tree);
    }

    /** Check if the sub-tree starting at a given node is empty.
     * @param node root node of the sub-tree (<em>must</em> have {@link
     * Region Region} tree semantics, i.e. the leaf nodes must have
     * {@code Boolean} attributes representing an inside/outside
     * property)
     * @return true if the sub-tree starting at the given node is empty
     */
    public static boolean isEmpty(final BSPTree node) {

        // we use a recursive function rather than the BSPTreeVisitor
        // interface because we can stop visiting the tree as soon as we
        // have found an inside cell

        if (node.getCut() == null) {
            // if we find an inside node, the region is not empty
            return !isInside(node);
        }

        // check both sides of the sub-tree
        return isEmpty(node.getMinus()) && isEmpty(node.getPlus());

    }

    /** Check a leaf node inside attribute.
     * @param node leaf node to check
     * @return true if the leaf node is an inside node
     */
    private static boolean isInside(final BSPTree node) {
        return (Boolean) node.getAttribute();
    }

    /** Check if the instance entirely contains another region.
     * @param region region to check against the instance
     * @return true if the instance contains the specified tree
     */
    public boolean contains(final Region region) {
        return difference(region, this).isEmpty();
    }

    /** Check a point with respect to the region.
     * @param point point to check
     * @return a code representing the point status: either {@link
     * Location#INSIDE}, {@link Location#OUTSIDE} or {@link Location#BOUNDARY}
     */
    public Location checkPoint(final Point point) {
        return checkPoint(tree, point);
    }

    /** Check a point with respect to the region starting at a given node.
     * @param node root node of the region
     * @param point point to check
     * @return a code representing the point status: either {@link
     * Location#INSIDE}, {@link Location#OUTSIDE} or {@link Location#BOUNDARY}
     */
    protected Location checkPoint(final BSPTree node, final Point point) {
        final BSPTree cell = node.getCell(point);
        if (cell.getCut() == null) {
            // the point is in the interior of a cell, just check the attribute
            return isInside(cell) ? Location.INSIDE : Location.OUTSIDE;
        }

        // the point is on a cut-sub-hyperplane, is it on a boundary ?
        final Location minusCode = checkPoint(cell.getMinus(), point);
        final Location plusCode  = checkPoint(cell.getPlus(),  point);
        return (minusCode == plusCode) ? minusCode : Location.BOUNDARY;

    }

    /** Get the complement of the region (exchanged interior/exterior).
     * <p>The instance is not modified, a new region is built.</p>
     * @return a new region, complement of the instance
     */
    public Region getComplement() {
        return buildNew(recurseComplement(tree));
    }

    /** Recursively build the complement of a BSP tree.
     * @param node current node of the original tree
     * @return new tree, complement of the node
     */
    private static BSPTree recurseComplement(final BSPTree node) {
        if (node.getCut() == null) {
            return new BSPTree(isInside(node) ? Boolean.FALSE : Boolean.TRUE);
        }

        BoundaryAttribute attribute = (BoundaryAttribute) node.getAttribute();
        if (attribute != null) {
            final SubHyperplane plusOutside =
                (attribute.plusInside == null) ? null : attribute.plusInside.copySelf();
            final SubHyperplane plusInside  =
                (attribute.plusOutside == null) ? null : attribute.plusOutside.copySelf();
            attribute = new BoundaryAttribute(plusOutside, plusInside);
        }

        return new BSPTree(node.getCut().copySelf(),
                           recurseComplement(node.getPlus()),
                           recurseComplement(node.getMinus()),
                           attribute);

    }

    /** Get the underlying BSP tree.

     * <p>Regions are represented by an underlying inside/outside BSP
     * tree whose leaf attributes are {@code Boolean} instances
     * representing inside leaf cells if the attribute value is
     * {@code true} and outside leaf cells if the attribute is
     * {@code false}. These leaf attributes are always present and
     * guaranteed to be non null.</p>

     * <p>In addition to the leaf attributes, the internal nodes which
     * correspond to cells split by cut sub-hyperplanes may contain
     * {@link BoundaryAttribute BoundaryAttribute} objects representing
     * the parts of the corresponding cut sub-hyperplane that belong to
     * the boundary. When the boundary attributes have been computed,
     * all internal nodes are guaranteed to have non-null
     * attributes, however some {@link BoundaryAttribute
     * BoundaryAttribute} instances may have their {@link
     * BoundaryAttribute#plusInside plusInside} and {@link
     * BoundaryAttribute#plusOutside plusOutside} fields both null if
     * the corresponding cut sub-hyperplane does not have any parts
     * belonging to the boundary.</p>

     * <p>Since computing the boundary is not always required and can be
     * time-consuming for large trees, these internal nodes attributes
     * are computed using lazy evaluation only when required by setting
     * the {@code includeBoundaryAttributes} argument to
     * {@code true}. Once computed, these attributes remain in the
     * tree, which implies that in this case, further calls to the
     * method for the same region will always include these attributes
     * regardless of the value of the
     * {@code includeBoundaryAttributes} argument.</p>

     * @param includeBoundaryAttributes if true, the boundary attributes
     * at internal nodes are guaranteed to be included (they may be
     * included even if the argument is false, if they have already been
     * computed due to a previous call)
     * @return underlying BSP tree
     * @see BoundaryAttribute
     */
    public BSPTree getTree(final boolean includeBoundaryAttributes) {
        if (includeBoundaryAttributes && (tree.getCut() != null) && (tree.getAttribute() == null)) {
            // we need to compute the boundary attributes
            recurseBuildBoundary(tree);
        }
        return tree;
    }

    /** Class holding boundary attributes.
     * <p>This class is used for the attributes associated with the
     * nodes of region boundary shell trees returned by the {@link
     * Region#getTree Region.getTree}. It contains the
     * parts of the node cut sub-hyperplane that belong to the
     * boundary.</p>
     * <p>This class is a simple placeholder, it does not provide any
     * processing methods.</p>
     * @see Region#getTree
     */
    public static class BoundaryAttribute {

        /** Part of the node cut sub-hyperplane that belongs to the
         * boundary and has the outside of the region on the plus side of
         * its underlying hyperplane (may be null).
         */
        private final SubHyperplane plusOutside;

        /** Part of the node cut sub-hyperplane that belongs to the
         * boundary and has the inside of the region on the plus side of
         * its underlying hyperplane (may be null).
         */
        private final SubHyperplane plusInside;

        /** Simple constructor.
         * @param plusOutside part of the node cut sub-hyperplane that
         * belongs to the boundary and has the outside of the region on
         * the plus side of its underlying hyperplane (may be null)
         * @param plusInside part of the node cut sub-hyperplane that
         * belongs to the boundary and has the inside of the region on the
         * plus side of its underlying hyperplane (may be null)
         */
        public BoundaryAttribute(final SubHyperplane plusOutside,
                                 final SubHyperplane plusInside) {
            this.plusOutside = plusOutside;
            this.plusInside  = plusInside;
        }

        /** Get the part of the node cut sub-hyperplane that belongs to the
         * boundary and has the outside of the region on the plus side of
         * its underlying hyperplane.
         * @return part of the node cut sub-hyperplane that belongs to the
         * boundary and has the outside of the region on the plus side of
         * its underlying hyperplane
         */
        public SubHyperplane getPlusOutside() {
            return plusOutside;
        }

        /** Get the part of the node cut sub-hyperplane that belongs to the
         * boundary and has the inside of the region on the plus side of
         * its underlying hyperplane.
         * @return part of the node cut sub-hyperplane that belongs to the
         * boundary and has the inside of the region on the plus side of
         * its underlying hyperplane
         */
        public SubHyperplane getPlusInside() {
            return plusInside;
        }


    }

    /** Recursively build the boundary shell tree.
     * @param node current node in the inout tree
     */
    private void recurseBuildBoundary(final BSPTree node) {
        if (node.getCut() != null) {

            SubHyperplane plusOutside = null;
            SubHyperplane plusInside  = null;

            // characterize the cut sub-hyperplane,
            // first with respect to the plus sub-tree
            final Characterization plusChar = new Characterization();
            characterize(node.getPlus(), node.getCut().copySelf(), plusChar);

            if (plusChar.hasOut()) {
                // plusChar.out corresponds to a subset of the cut
                // sub-hyperplane known to have outside cells on its plus
                // side, we want to check if parts of this subset do have
                // inside cells on their minus side
                final Characterization minusChar = new Characterization();
                characterize(node.getMinus(), plusChar.getOut(), minusChar);
                if (minusChar.hasIn()) {
                    plusOutside = minusChar.getIn();
                }
            }

            if (plusChar.hasIn()) {
                // plusChar.in corresponds to a subset of the cut
                // sub-hyperplane known to have inside cells on its plus
                // side, we want to check if parts of this subset do have
                // outside cells on their minus side
                final Characterization minusChar = new Characterization();
                characterize(node.getMinus(), plusChar.getIn(), minusChar);
                if (minusChar.hasOut()) {
                    plusInside = minusChar.getOut();
                }
            }

            node.setAttribute(new BoundaryAttribute(plusOutside, plusInside));
            recurseBuildBoundary(node.getPlus());
            recurseBuildBoundary(node.getMinus());

        }
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
    private static void characterize(final BSPTree node, final SubHyperplane sub,
                                     final Characterization characterization) {
        if (node.getCut() == null) {
            // we have reached a leaf node
            final boolean inside = (Boolean) node.getAttribute();
            characterization.add(sub, inside);
        } else {
            final Hyperplane hyperplane = node.getCut().getHyperplane();
            switch (hyperplane.side(sub)) {
            case PLUS:
                characterize(node.getPlus(), sub, characterization);
                break;
            case MINUS:
                characterize(node.getMinus(), sub, characterization);
                break;
            case BOTH:
                final Hyperplane.SplitSubHyperplane split = hyperplane.split(sub);
                characterize(node.getPlus(),  split.getPlus(),  characterization);
                characterize(node.getMinus(), split.getMinus(), characterization);
                break;
            default:
                // this should not happen
                throw new RuntimeException("internal error");
            }
        }
    }

    /** Get the size of the boundary.
     * @return the size of the boundary (this is 0 in 1D, a length in
     * 2D, an area in 3D ...)
     */
    public double getBoundarySize() {
        final BoundarySizeVisitor visitor = new BoundarySizeVisitor();
        getTree(true).visit(visitor);
        return visitor.getSize();
    }

    /** Visitor computing the boundary size. */
    private static class BoundarySizeVisitor implements BSPTreeVisitor {

        /** Size of the boundary. */
        private double boundarySize;

        /** Simple constructor.
         */
        public BoundarySizeVisitor() {
            boundarySize = 0;
        }

        /** {@inheritDoc}*/
        public Order visitOrder(final BSPTree node) {
            return Order.MINUS_SUB_PLUS;
        }

        /** {@inheritDoc}*/
        public void visitInternalNode(final BSPTree node) {
            final BoundaryAttribute attribute = (BoundaryAttribute) node.getAttribute();
            if (attribute.plusOutside != null) {
                boundarySize += attribute.plusOutside.getRemainingRegion().getSize();
            }
            if (attribute.plusInside != null) {
                boundarySize += attribute.plusInside.getRemainingRegion().getSize();
            }
        }

        /** {@inheritDoc}*/
        public void visitLeafNode(final BSPTree node) {
        }

        /** Get the size of the boundary.
         * @return size of the boundary
         */
        public double getSize() {
            return boundarySize;
        }

    }

    /** Get the size of the instance.
     * @return the size of the instance (this is a length in 1D, an area
     * in 2D, a volume in 3D ...)
     */
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

    /** Get the barycenter of the instance.
     * @return an object representing the barycenter
     */
    public Point getBarycenter() {
        if (barycenter == null) {
            computeGeometricalProperties();
        }
        return barycenter;
    }

    /** Set the barycenter of the instance.
     * @param barycenter barycenter of the instance
     */
    protected void setBarycenter(final Point barycenter) {
        this.barycenter = barycenter;
    }

    /** Compute some geometrical properties.
     * <p>The properties to compute are the barycenter and the size.</p>
     */
    protected abstract void computeGeometricalProperties();

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
    public Region applyTransform(final Transform transform) {

        // transform the BSP tree
        final Region tRegion = buildNew(recurseTransform(tree, transform));

        // transform the barycenter
        if (barycenter != null) {
            tRegion.size = size;
            tRegion.barycenter = transform.apply(barycenter);
        }

        return tRegion;

    }

    /** Recursively transform an inside/outside BSP-tree.
     * @param node current BSP tree node
     * @param transform transform to apply
     * @return a new tree
     */
    private BSPTree recurseTransform(final BSPTree node, final Transform transform) {

        if (node.getCut() == null) {
            return new BSPTree(node.getAttribute());
        }

        final SubHyperplane  sub = node.getCut();
        final SubHyperplane tSub = sub.applyTransform(transform);
        BoundaryAttribute attribute = (BoundaryAttribute) node.getAttribute();
        if (attribute != null) {
            final SubHyperplane tPO =
                (attribute.getPlusOutside() == null) ? null : attribute.getPlusOutside().applyTransform(transform);
            final SubHyperplane tPI =
                (attribute.getPlusInside()  == null) ? null  : attribute.getPlusInside().applyTransform(transform);
            attribute = new BoundaryAttribute(tPO, tPI);
        }

        return new BSPTree(tSub,
                           recurseTransform(node.getPlus(),  transform),
                           recurseTransform(node.getMinus(), transform),
                           attribute);

    }

    /** Compute the relative position of the instance with respect to an
     * hyperplane.
     * @param hyperplane reference hyperplane
     * @return one of {@link Hyperplane.Side#PLUS Hyperplane.Side.PLUS}, {@link
     * Hyperplane.Side#MINUS Hyperplane.Side.MINUS}, {@link Hyperplane.Side#BOTH
     * Hyperplane.Side.BOTH} or {@link Hyperplane.Side#HYPER Hyperplane.Side.HYPER}
     * (the latter result can occur only if the tree contains only one
     * cut hyperplane)
     */
    public Hyperplane.Side side(final Hyperplane hyperplane) {
        final Sides sides = new Sides();
        recurseSides(tree, new SubHyperplane(hyperplane), sides);
        return sides.plusFound() ?
              (sides.minusFound() ? Hyperplane.Side.BOTH  : Hyperplane.Side.PLUS) :
              (sides.minusFound() ? Hyperplane.Side.MINUS : Hyperplane.Side.HYPER);
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
    private void recurseSides(final BSPTree node, final SubHyperplane sub, final Sides sides) {

        if (node.getCut() == null) {
            if (isInside(node)) {
                // this is an inside cell expanding across the hyperplane
                sides.rememberPlusFound();
                sides.rememberMinusFound();
            }
            return;
        }

        final Hyperplane hyperplane = node.getCut().getHyperplane();
        switch (hyperplane.side(sub)) {
        case PLUS :
            // the sub-hyperplane is entirely in the plus sub-tree
            if (sub.getHyperplane().side(node.getCut()) == Hyperplane.Side.PLUS) {
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
            if (sub.getHyperplane().side(node.getCut()) == Hyperplane.Side.PLUS) {
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
            final Hyperplane.SplitSubHyperplane split = hyperplane.split(sub);

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
                if ((node.getPlus().getCut() != null) || isInside(node.getPlus())) {
                    sides.rememberPlusFound();
                }
                if ((node.getMinus().getCut() != null) || isInside(node.getMinus())) {
                    sides.rememberMinusFound();
                }
            } else {
                if ((node.getPlus().getCut() != null) || isInside(node.getPlus())) {
                    sides.rememberMinusFound();
                }
                if ((node.getMinus().getCut() != null) || isInside(node.getMinus())) {
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

    /** Get the parts of a sub-hyperplane that are contained in the region.
     * <p>The parts of the sub-hyperplane that belong to the boundary are
     * <em>not</em> included in the resulting parts.</p>
     * @param sub sub-hyperplane traversing the region
     * @return filtered sub-hyperplane
     */
    public SubHyperplane intersection(final SubHyperplane sub) {
        return recurseIntersection(tree, sub);
    }

    /** Recursively compute the parts of a sub-hyperplane that are
     * contained in the region.
     * @param node current BSP tree node
     * @param sub sub-hyperplane traversing the region
     * @return filtered sub-hyperplane
     */
    private SubHyperplane recurseIntersection(final BSPTree node, final SubHyperplane sub) {

        if (node.getCut() == null) {
            return isInside(node) ? sub.copySelf() : null;
        }

        final Hyperplane hyperplane = node.getCut().getHyperplane();
        switch (hyperplane.side(sub)) {
        case PLUS :
            return recurseIntersection(node.getPlus(), sub);
        case MINUS :
            return recurseIntersection(node.getMinus(), sub);
        case BOTH :
            final Hyperplane.SplitSubHyperplane split = hyperplane.split(sub);
            final SubHyperplane plus  = recurseIntersection(node.getPlus(),  split.getPlus());
            final SubHyperplane minus = recurseIntersection(node.getMinus(), split.getMinus());
            if (plus == null) {
                return minus;
            } else if (minus == null) {
                return plus;
            } else {
                return new SubHyperplane(plus.getHyperplane(),
                                         Region.union(plus.getRemainingRegion(),
                                                      minus.getRemainingRegion()));
            }
        default :
            return recurseIntersection(node.getPlus(),
                                       recurseIntersection(node.getMinus(), sub));
        }

    }

    /** Compute the union of two regions.
     * @param region1 first region (will be unusable after the operation as
     * parts of it will be reused in the new region)
     * @param region2 second region (will be unusable after the operation as
     * parts of it will be reused in the new region)
     * @return a new region, result of {@code region1 union region2}
     */
    public static Region union(final Region region1, final Region region2) {
        final BSPTree tree = region1.tree.merge(region2.tree, new UnionMerger());
        tree.visit(new InternalNodesCleaner());
        return region1.buildNew(tree);
    }

    /** Compute the intersection of two regions.
     * @param region1 first region (will be unusable after the operation as
     * parts of it will be reused in the new region)
     * @param region2 second region (will be unusable after the operation as
     * parts of it will be reused in the new region)
     * @return a new region, result of {@code region1 intersection region2}
     */
    public static Region intersection(final Region region1, final Region region2) {
        final BSPTree tree = region1.tree.merge(region2.tree, new IntersectionMerger());
        tree.visit(new InternalNodesCleaner());
        return region1.buildNew(tree);
    }

    /** Compute the symmetric difference (exclusive or) of two regions.
     * @param region1 first region (will be unusable after the operation as
     * parts of it will be reused in the new region)
     * @param region2 second region (will be unusable after the operation as
     * parts of it will be reused in the new region)
     * @return a new region, result of {@code region1 xor region2}
     */
    public static Region xor(final Region region1, final Region region2) {
        final BSPTree tree = region1.tree.merge(region2.tree, new XORMerger());
        tree.visit(new InternalNodesCleaner());
        return region1.buildNew(tree);
    }

    /** Compute the difference of two regions.
     * @param region1 first region (will be unusable after the operation as
     * parts of it will be reused in the new region)
     * @param region2 second region (will be unusable after the operation as
     * parts of it will be reused in the new region)
     * @return a new region, result of {@code region1 minus region2}
     */
    public static Region difference(final Region region1, final Region region2) {
        final BSPTree tree = region1.tree.merge(region2.tree, new DifferenceMerger());
        tree.visit(new InternalNodesCleaner());
        return region1.buildNew(tree);
    }

    /** Leaf node / tree merger for union operation. */
    private static final class UnionMerger implements BSPTree.LeafMerger {
        /** {@inheritDoc} */
        public BSPTree merge(final BSPTree leaf, final BSPTree tree,
                             final BSPTree parentTree, final boolean isPlusChild,
                             final boolean leafFromInstance) {
            if (isInside(leaf)) {
                // the leaf node represents an inside cell
                leaf.insertInTree(parentTree, isPlusChild);
                return leaf;
            }
            // the leaf node represents an outside cell
            tree.insertInTree(parentTree, isPlusChild);
            return tree;
        }
    };

    /** Leaf node / tree merger for intersection operation. */
    private static final class IntersectionMerger implements BSPTree.LeafMerger {
        /** {@inheritDoc} */
        public BSPTree merge(final BSPTree leaf, final BSPTree tree,
                             final BSPTree parentTree, final boolean isPlusChild,
                             final boolean leafFromInstance) {
            if (isInside(leaf)) {
                // the leaf node represents an inside cell
                tree.insertInTree(parentTree, isPlusChild);
                return tree;
            }
            // the leaf node represents an outside cell
            leaf.insertInTree(parentTree, isPlusChild);
            return leaf;
        }
    };

    /** Leaf node / tree merger for xor operation. */
    private static final class XORMerger implements BSPTree.LeafMerger {
        /** {@inheritDoc} */
        public BSPTree merge(final BSPTree leaf, final BSPTree tree,
                             final BSPTree parentTree, final boolean isPlusChild,
                             final boolean leafFromInstance) {
            BSPTree t = tree;
            if (isInside(leaf)) {
                // the leaf node represents an inside cell
                t = recurseComplement(t);
            }
            t.insertInTree(parentTree, isPlusChild);
            return t;
        }
    };

    /** Leaf node / tree merger for difference operation.
     * <p>The algorithm used here is directly derived from the one
     * described in section III (<i>Binary Partitioning of a BSP
     * Tree</i>) of the Naylor, Amanatides and Thibault paper. An error
     * was detected and corrected in the figure 5.1 of the article for
     * merging leaf nodes with complete trees. Contrary to what is said
     * in the figure, the {@code ELSE} part of if is not the same
     * as the first part with {@code T1} and {@codeT2}
     * swapped. {@code T1} and {@codeT2} must be swapped
     * everywhere <em>except</em> in the {@code RETURN} part of the
     * {@code DIFFERENCE} operation: if {@codeT2} is an
     * in-cell, we must return {@code Complement_Bspt(T2)}, not
     * {@code Complement_Bspt(T1)}, and if {@codeT2} is an
     * out-cell, we must return {@code T1}, not {@codeT2}</p>
     */
    private static final class DifferenceMerger implements BSPTree.LeafMerger {
        /** {@inheritDoc} */
        public BSPTree merge(final BSPTree leaf, final BSPTree tree,
                             final BSPTree parentTree, final boolean isPlusChild,
                             final boolean leafFromInstance) {
            if (isInside(leaf)) {
                // the leaf node represents an inside cell
                final BSPTree argTree = recurseComplement(leafFromInstance ? tree : leaf);
                argTree.insertInTree(parentTree, isPlusChild);
                return argTree;
            }
            // the leaf node represents an outside cell
            final BSPTree instanceTree = leafFromInstance ? leaf : tree;
            instanceTree.insertInTree(parentTree, isPlusChild);
            return instanceTree;
        }
    };

    /** Visitor removing internal nodes attributes. */
    private static final class InternalNodesCleaner implements BSPTreeVisitor {

        /** {@inheritDoc} */
        public Order visitOrder(final BSPTree node) {
            return Order.PLUS_SUB_MINUS;
        }

        /** {@inheritDoc} */
        public void visitInternalNode(final BSPTree node) {
            node.setAttribute(null);
        }

        /** {@inheritDoc} */
        public void visitLeafNode(final BSPTree node) {
        }

    }

}
