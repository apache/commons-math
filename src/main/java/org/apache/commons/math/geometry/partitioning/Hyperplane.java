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

/** This interface represents an hyperplane of a space.

 * <p>The most prominent place where hyperplane appears in space
 * partitioning is as cutters. Each partitioning node in a {@link
 * BSPTree BSP tree} has a cut {@link SubHyperplane sub-hyperplane}
 * which is either an hyperplane or a part of an hyperplane. In an
 * n-dimensions euclidean space, an hyperplane is an (n-1)-dimensions
 * hyperplane (for example a traditional plane in the 3D euclidean
 * space). They can be more exotic objects in specific fields, for
 * example a circle on the surface of the unit sphere.</p>

 * @version $Revision$ $Date$
 */
public interface Hyperplane extends SubSpace {

    /** Enumerate for specifying sides of the hyperplane. */
    enum Side {

        /** Code for the plus side of the hyperplane. */
        PLUS,

        /** Code for the minus side of the hyperplane. */
        MINUS,

        /** Code for elements crossing the hyperplane from plus to minus side. */
        BOTH,

        /** Code for the hyperplane itself. */
        HYPER;

    }

    /** Copy the instance.
     * <p>The instance created is completely independant of the original
     * one. A deep copy is used, none of the underlying objects are
     * shared (except for immutable objects).</p>
     * @return a new hyperplane, copy of the instance
     */
    Hyperplane copySelf();

    /** Get the offset (oriented distance) of a point.
     * <p>The offset is 0 if the point is on the underlying hyperplane,
     * it is positive if the point is on one particular side of the
     * hyperplane, and it is negative if the point is on the other side,
     * according to the hyperplane natural orientation.</p>
     * @param point point to check
     * @return offset of the point
     */
    double getOffset(Point point);

    /** Check if the instance has the same orientation as another hyperplane.
     * <p>This method is expected to be called on parallel hyperplanes
     * (i.e. when the {@link #side side} method would return {@link
     * Side#HYPER} for some sub-hyperplane having the specified hyperplane
     * as its underlying hyperplane). The method should <em>not</em>
     * re-check for parallelism, only for orientation, typically by
     * testing something like the sign of the dot-products of
     * normals.</p>
     * @param other other hyperplane to check against the instance
     * @return true if the instance and the other hyperplane have
     * the same orientation
     */
    boolean sameOrientationAs(Hyperplane other);

    /** Build the sub-space shared by the instance and another hyperplane.
     * @param other other hyperplane
     * @return a sub-space at the intersection of the instance and the
     * other sub-space (it has a dimension one unit less than the
     * instance)
     */
    SubSpace intersection(Hyperplane other);

    /** Build a region covering the whole hyperplane.
     * <p>The region build is restricted to the sub-space defined by the
     * hyperplane. This means that the regions points are consistent
     * with the argument of the {@link SubSpace#toSpace toSpace} method
     * and with the return value of the {@link SubSpace#toSubSpace
     * toSubSpace} method.<p>
     * @return a region covering the whole hyperplane
     */
    Region wholeHyperplane();

    /** Build a region covering the whole space.
     * @return a region containing the instance
     */
    Region wholeSpace();

    /** Compute the relative position of a sub-hyperplane with respect
     * to the instance.
     * @param sub sub-hyperplane to check
     * @return one of {@link Side#PLUS}, {@link Side#MINUS}, {@link Side#BOTH},
     * {@link Side#HYPER}
     */
    Side side(SubHyperplane sub);

    /** Split a sub-hyperplane in two parts by the instance.
     * @param sub sub-hyperplane to split
     * @return an object containing both the part of the sub-hyperplane
     * on the plus side of the instance and the part of the
     * sub-hyperplane on the minus side of the instance
     */
    SplitSubHyperplane split(SubHyperplane sub);

    /** Class holding the results of the {@link Hyperplane#split Hyperplane.split}
     * method. */
    class SplitSubHyperplane {

        /** Part of the sub-hyperplane on the plus side of the splitting hyperplane. */
        private final SubHyperplane plus;

        /** Part of the sub-hyperplane on the minus side of the splitting hyperplane. */
        private final SubHyperplane minus;

        /** Build a SplitSubHyperplane from its parts.
         * @param plus part of the sub-hyperplane on the plus side of the
         * splitting hyperplane
         * @param minus part of the sub-hyperplane on the minus side of the
         * splitting hyperplane
         */
        public SplitSubHyperplane(final SubHyperplane plus, final SubHyperplane minus) {
            this.plus  = plus;
            this.minus = minus;
        }

        /** Get the part of the sub-hyperplane on the plus side of the splitting hyperplane.
         * @return part of the sub-hyperplane on the plus side of the splitting hyperplane
         */
        public SubHyperplane getPlus() {
            return plus;
        }

        /** Get the part of the sub-hyperplane on the minus side of the splitting hyperplane.
         * @return part of the sub-hyperplane on the minus side of the splitting hyperplane
         */
        public SubHyperplane getMinus() {
            return minus;
        }

    }

}
