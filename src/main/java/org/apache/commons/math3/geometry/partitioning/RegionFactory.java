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

import org.apache.commons.math3.geometry.Space;

/** This class is a factory for {@link Region}.

 * @param <S> Type of the space.

 * @version $Id$
 * @since 3.0
 */
public class RegionFactory<S extends Space> {

    /** Visitor removing internal nodes attributes. */
    private final NodesCleaner nodeCleaner;

    /** Simple constructor.
     */
    public RegionFactory() {
        nodeCleaner = new NodesCleaner();
    }

    /** Build a convex region from a collection of bounding hyperplanes.
     * @param hyperplanes collection of bounding hyperplanes
     * @return a new convex region, or null if the collection is empty
     */
    public Region<S> buildConvex(final Hyperplane<S> ... hyperplanes) {
        if ((hyperplanes == null) || (hyperplanes.length == 0)) {
            return null;
        }

        // use the first hyperplane to build the right class
        final Region<S> region = hyperplanes[0].wholeSpace();

        // chop off parts of the space
        BSPTree<S> node = region.getTree(false);
        node.setAttribute(Boolean.TRUE);
        for (final Hyperplane<S> hyperplane : hyperplanes) {
            if (node.insertCut(hyperplane)) {
                node.setAttribute(null);
                node.getPlus().setAttribute(Boolean.FALSE);
                node = node.getMinus();
                node.setAttribute(Boolean.TRUE);
            }
        }

        return region;

    }

    /** Compute the union of two regions.
     * @param region1 first region (will be unusable after the operation as
     * parts of it will be reused in the new region)
     * @param region2 second region (will be unusable after the operation as
     * parts of it will be reused in the new region)
     * @return a new region, result of {@code region1 union region2}
     */
    public Region<S> union(final Region<S> region1, final Region<S> region2) {
        final BSPTree<S> tree =
            region1.getTree(false).merge(region2.getTree(false), new UnionMerger());
        tree.visit(nodeCleaner);
        return region1.buildNew(tree);
    }

    /** Compute the intersection of two regions.
     * @param region1 first region (will be unusable after the operation as
     * parts of it will be reused in the new region)
     * @param region2 second region (will be unusable after the operation as
     * parts of it will be reused in the new region)
     * @return a new region, result of {@code region1 intersection region2}
     */
    public Region<S> intersection(final Region<S> region1, final Region<S> region2) {
        final BSPTree<S> tree =
            region1.getTree(false).merge(region2.getTree(false), new IntersectionMerger());
        tree.visit(nodeCleaner);
        return region1.buildNew(tree);
    }

    /** Compute the symmetric difference (exclusive or) of two regions.
     * @param region1 first region (will be unusable after the operation as
     * parts of it will be reused in the new region)
     * @param region2 second region (will be unusable after the operation as
     * parts of it will be reused in the new region)
     * @return a new region, result of {@code region1 xor region2}
     */
    public Region<S> xor(final Region<S> region1, final Region<S> region2) {
        final BSPTree<S> tree =
            region1.getTree(false).merge(region2.getTree(false), new XorMerger());
        tree.visit(nodeCleaner);
        return region1.buildNew(tree);
    }

    /** Compute the difference of two regions.
     * @param region1 first region (will be unusable after the operation as
     * parts of it will be reused in the new region)
     * @param region2 second region (will be unusable after the operation as
     * parts of it will be reused in the new region)
     * @return a new region, result of {@code region1 minus region2}
     */
    public Region<S> difference(final Region<S> region1, final Region<S> region2) {
        final BSPTree<S> tree =
            region1.getTree(false).merge(region2.getTree(false), new DifferenceMerger());
        tree.visit(nodeCleaner);
        return region1.buildNew(tree);
    }

    /** Get the complement of the region (exchanged interior/exterior).
     * @param region region to complement, it will not modified, a new
     * region independent region will be built
     * @return a new region, complement of the specified one
     */
    public Region<S> getComplement(final Region<S> region) {
        return region.buildNew(recurseComplement(region.getTree(false)));
    }

    /** Recursively build the complement of a BSP tree.
     * @param node current node of the original tree
     * @return new tree, complement of the node
     */
    private BSPTree<S> recurseComplement(final BSPTree<S> node) {
        if (node.getCut() == null) {
            return new BSPTree<S>(((Boolean) node.getAttribute()) ? Boolean.FALSE : Boolean.TRUE);
        }

        @SuppressWarnings("unchecked")
        BoundaryAttribute<S> attribute = (BoundaryAttribute<S>) node.getAttribute();
        if (attribute != null) {
            final SubHyperplane<S> plusOutside =
                (attribute.getPlusInside() == null) ? null : attribute.getPlusInside().copySelf();
            final SubHyperplane<S> plusInside  =
                (attribute.getPlusOutside() == null) ? null : attribute.getPlusOutside().copySelf();
            attribute = new BoundaryAttribute<S>(plusOutside, plusInside);
        }

        return new BSPTree<S>(node.getCut().copySelf(),
                              recurseComplement(node.getPlus()),
                              recurseComplement(node.getMinus()),
                              attribute);

    }

    /** BSP tree leaf merger computing union of two regions. */
    private class UnionMerger implements BSPTree.LeafMerger<S> {
        /** {@inheritDoc} */
        public BSPTree<S> merge(final BSPTree<S> leaf, final BSPTree<S> tree,
                                final BSPTree<S> parentTree,
                                final boolean isPlusChild, final boolean leafFromInstance) {
            if ((Boolean) leaf.getAttribute()) {
                // the leaf node represents an inside cell
                leaf.insertInTree(parentTree, isPlusChild);
                return leaf;
            }
            // the leaf node represents an outside cell
            tree.insertInTree(parentTree, isPlusChild);
            return tree;
        }
    }

    /** BSP tree leaf merger computing union of two regions. */
    private class IntersectionMerger implements BSPTree.LeafMerger<S> {
        /** {@inheritDoc} */
        public BSPTree<S> merge(final BSPTree<S> leaf, final BSPTree<S> tree,
                                final BSPTree<S> parentTree,
                                final boolean isPlusChild, final boolean leafFromInstance) {
            if ((Boolean) leaf.getAttribute()) {
                // the leaf node represents an inside cell
                tree.insertInTree(parentTree, isPlusChild);
                return tree;
            }
            // the leaf node represents an outside cell
            leaf.insertInTree(parentTree, isPlusChild);
            return leaf;
        }
    }

    /** BSP tree leaf merger computing union of two regions. */
    private class XorMerger implements BSPTree.LeafMerger<S> {
        /** {@inheritDoc} */
        public BSPTree<S> merge(final BSPTree<S> leaf, final BSPTree<S> tree,
                                final BSPTree<S> parentTree, final boolean isPlusChild,
                                final boolean leafFromInstance) {
            BSPTree<S> t = tree;
            if ((Boolean) leaf.getAttribute()) {
                // the leaf node represents an inside cell
                t = recurseComplement(t);
            }
            t.insertInTree(parentTree, isPlusChild);
            return t;
        }
    }

    /** BSP tree leaf merger computing union of two regions. */
    private class DifferenceMerger implements BSPTree.LeafMerger<S> {
        /** {@inheritDoc} */
        public BSPTree<S> merge(final BSPTree<S> leaf, final BSPTree<S> tree,
                                final BSPTree<S> parentTree, final boolean isPlusChild,
                                final boolean leafFromInstance) {
            if ((Boolean) leaf.getAttribute()) {
                // the leaf node represents an inside cell
                final BSPTree<S> argTree =
                    recurseComplement(leafFromInstance ? tree : leaf);
                argTree.insertInTree(parentTree, isPlusChild);
                return argTree;
            }
            // the leaf node represents an outside cell
            final BSPTree<S> instanceTree =
                leafFromInstance ? leaf : tree;
            instanceTree.insertInTree(parentTree, isPlusChild);
            return instanceTree;
        }
    }

    /** Visitor removing internal nodes attributes. */
    private class NodesCleaner implements  BSPTreeVisitor<S> {

        /** {@inheritDoc} */
        public Order visitOrder(final BSPTree<S> node) {
            return Order.PLUS_SUB_MINUS;
        }

        /** {@inheritDoc} */
        public void visitInternalNode(final BSPTree<S> node) {
            node.setAttribute(null);
        }

        /** {@inheritDoc} */
        public void visitLeafNode(final BSPTree<S> node) {
        }

    }

}
