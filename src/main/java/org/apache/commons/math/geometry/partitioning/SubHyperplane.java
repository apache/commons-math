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

/** This interface represents the remaining parts of an hyperplane after
 * other parts have been chopped off.

 * <p>sub-hyperplanes are obtained when parts of an {@link
 * Hyperplane hyperplane} are chopped off by other hyperplanes that
 * intersect it. The remaining part is a convex region. Such objects
 * appear in {@link BSPTree BSP trees} as the intersection of a cut
 * hyperplane with the convex region which it splits, the chopping
 * hyperplanes are the cut hyperplanes closer to the tree root.</p>

 * @version $Revision$ $Date$
 */
public class SubHyperplane {

    /** Underlying hyperplane. */
    private final Hyperplane hyperplane;

    /** Remaining region of the hyperplane. */
    private final Region remainingRegion;

    /** Build a chopped hyperplane that is not chopped at all.
     * @param hyperplane underlying hyperplane
     */
    public SubHyperplane(final Hyperplane hyperplane) {
        this.hyperplane = hyperplane;
        remainingRegion = hyperplane.wholeHyperplane();
    }

    /** Build a sub-hyperplane from an hyperplane and a region.
     * @param hyperplane underlying hyperplane
     * @param remainingRegion remaining region of the hyperplane
     */
    public SubHyperplane(final Hyperplane hyperplane, final Region remainingRegion) {
        this.hyperplane      = hyperplane;
        this.remainingRegion = remainingRegion;
    }

    /** Copy the instance.
     * <p>The instance created is completely independant of the original
     * one. A deep copy is used, none of the underlying objects are
     * shared (except for the nodes attributes and immutable
     * objects).</p>
     * @return a new sub-hyperplane, copy of the instance
     */
    public SubHyperplane copySelf() {
        return new SubHyperplane(hyperplane.copySelf(), remainingRegion.copySelf());
    }

    /** Get the underlying hyperplane.
     * @return underlying hyperplane
     */
    public Hyperplane getHyperplane() {
        return hyperplane;
    }

    /** Get the remaining region of the hyperplane.
     * <p>The returned region is expressed in the canonical hyperplane
     * frame and has the hyperplane dimension. For example a chopped
     * hyperplane in the 3D euclidean is a 2D plane and the
     * corresponding region is a convex 2D polygon.</p>
     * @return remaining region of the hyperplane
     */
    public Region getRemainingRegion() {
        return remainingRegion;
    }

    /** Apply a transform to the instance.
     * <p>The instance must be a (D-1)-dimension sub-hyperplane with
     * respect to the transform <em>not</em> a (D-2)-dimension
     * sub-hyperplane the transform knows how to transform by
     * itself. The transform will consist in transforming first the
     * hyperplane and then the all region using the various methods
     * provided by the transform.</p>
     * @param transform D-dimension transform to apply
     * @return the transformed instance
     */
    public SubHyperplane applyTransform(final Transform transform) {
        final Hyperplane tHyperplane = transform.apply(hyperplane);
        final BSPTree tTree =
            recurseTransform(remainingRegion.getTree(false), tHyperplane, transform);
        return new SubHyperplane(tHyperplane, remainingRegion.buildNew(tTree));
    }

    /** Recursively transform a BSP-tree from a sub-hyperplane.
     * @param node current BSP tree node
     * @param transformed image of the instance hyperplane by the transform
     * @param transform transform to apply
     * @return a new tree
     */
    private BSPTree recurseTransform(final BSPTree node, final Hyperplane transformed,
                                     final Transform transform) {
        if (node.getCut() == null) {
            return new BSPTree(node.getAttribute());
        }

        Region.BoundaryAttribute attribute =
            (Region.BoundaryAttribute) node.getAttribute();
        if (attribute != null) {
            final SubHyperplane tPO = (attribute.getPlusOutside() == null) ?
                                      null :
                                      transform.apply(attribute.getPlusOutside(),
                                                      hyperplane, transformed);
            final SubHyperplane tPI = (attribute.getPlusInside() == null) ?
                                      null :
                                      transform.apply(attribute.getPlusInside(),
                                                      hyperplane, transformed);
            attribute = new Region.BoundaryAttribute(tPO, tPI);
        }

        return new BSPTree(transform.apply(node.getCut(),
                                           hyperplane, transformed),
                                           recurseTransform(node.getPlus(), transformed,
                                                            transform),
                                                            recurseTransform(node.getMinus(), transformed,
                                                                             transform),
                                                                             attribute);

    }

}
