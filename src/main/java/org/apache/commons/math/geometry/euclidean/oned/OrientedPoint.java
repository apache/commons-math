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
package org.apache.commons.math.geometry.euclidean.oned;

import org.apache.commons.math.exception.MathUnsupportedOperationException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.geometry.partitioning.BSPTree;
import org.apache.commons.math.geometry.partitioning.Hyperplane;
import org.apache.commons.math.geometry.partitioning.Point;
import org.apache.commons.math.geometry.partitioning.Region;
import org.apache.commons.math.geometry.partitioning.SubHyperplane;
import org.apache.commons.math.geometry.partitioning.SubSpace;

/** This class represents a 1D oriented hyperplane.
 * <p>An hyperplane in 1D is a simple point, its orientation being a
 * boolean.</p>
 * <p>Instances of this class are guaranteed to be immutable.</p>
 * @version $Revision$ $Date$
 */
public class OrientedPoint implements Hyperplane {

    /** Dummy region returned by the {@link #wholeHyperplane} method. */
    private static final Region DUMMY_REGION = new DummyRegion();

    /** Point location. */
    private Point1D location;

    /** Orientation. */
    private boolean direct;

    /** Simple constructor.
     * @param location location of the hyperplane
     * @param direct if true, the plus side of the hyperplane is towards
     * abscissae greater than {@code location}
     */
    public OrientedPoint(final Point1D location, final boolean direct) {
        this.location = location;
        this.direct   = direct;
    }

    /** Copy the instance.
     * <p>Since instances are immutable, this method directly returns
     * the instance.</p>
     * @return the instance itself
     */
    public Hyperplane copySelf() {
        return this;
    }

    /** Get the offset (oriented distance) of a point to the hyperplane.
     * @param point point to check
     * @return offset of the point
     */
    public double getOffset(final Point point) {
        final double delta = ((Point1D) point).getAbscissa() - location.getAbscissa();
        return direct ? delta : -delta;
    }

    /** Transform a space point into a sub-space point.
     * <p>Since this class represent zero dimension spaces which does
     * not have lower dimension sub-spaces, this method cannot be
     * supported here. It always throws a {@code RuntimeException}
     * when called.</p>
     * @param point n-dimension point of the space
     * @return (n-1)-dimension point of the sub-space corresponding to
     * the specified space point
     * @see #toSpace
     */
    public Point toSubSpace(final Point point) {
        throw new MathUnsupportedOperationException(LocalizedFormats.NOT_SUPPORTED_IN_DIMENSION_N, 1);
    }

    /** Transform a sub-space point into a space point.
     * <p>Since this class represent zero dimension spaces which does
     * not have lower dimension sub-spaces, this method cannot be
     * supported here. It always throws a {@code RuntimeException}
     * when called.</p>
     * @param point (n-1)-dimension point of the sub-space
     * @return n-dimension point of the space corresponding to the
     * specified sub-space point
     * @see #toSubSpace
     */
    public Point toSpace(final Point point) {
        throw new MathUnsupportedOperationException(LocalizedFormats.NOT_SUPPORTED_IN_DIMENSION_N, 1);
    }

    /** Build the sub-space shared by the instance and another hyperplane.
     * <p>Since this class represent zero dimension spaces which does
     * not have lower dimension sub-spaces, this method cannot be
     * supported here. It always throws a {@code RuntimeException}
     * when called.</p>
     * @param other other sub-space (must have the same dimension as the
     * instance)
     * @return a sub-space at the intersection of the instance and the
     * other sub-space (it has a dimension one unit less than the
     * instance)
     */
    public SubSpace intersection(final Hyperplane other) {
        throw new MathUnsupportedOperationException(LocalizedFormats.NOT_SUPPORTED_IN_DIMENSION_N, 1);
    }

    /** Build a region covering the whole hyperplane.
     * <p>Since this class represent zero dimension spaces which does
     * not have lower dimension sub-spaces, this method returns a dummy
     * implementation of a {@link Region Region} (always the same
     * instance). This implementation is only used to allow the {@link
     * SubHyperplane SubHyperplane} class implementation to work
     * properly, it should <em>not</em> be used otherwise.</p>
     * @return a dummy region
     */
    public Region wholeHyperplane() {
        return DUMMY_REGION;
    }

    /** Build a region covering the whole space.
     * @return a region containing the instance (really an {@link
     * IntervalsSet IntervalsSet} instance)
     */
    public Region wholeSpace() {
        return new IntervalsSet();
    }

    /** Check if the instance has the same orientation as another hyperplane.
     * <p>This method is expected to be called on parallel hyperplanes
     * (i.e. when the {@link #side side} method would return {@link
     * org.apache.commons.math.geometry.partitioning.Hyperplane.Side#HYPER}
     * for some sub-hyperplane having the specified hyperplane
     * as its underlying hyperplane). The method should <em>not</em>
     * re-check for parallelism, only for orientation, typically by
     * testing something like the sign of the dot-products of
     * normals.</p>
     * @param other other hyperplane to check against the instance
     * @return true if the instance and the other hyperplane have
     * the same orientation
     */
    public boolean sameOrientationAs(final Hyperplane other) {
        return !(direct ^ ((OrientedPoint) other).direct);
    }

    /** Compute the relative position of a sub-hyperplane with respect
     * to the instance.
     * @param sub sub-hyperplane to check
     * @return one of {@link org.apache.commons.math.geometry.partitioning.Hyperplane.Side#PLUS PLUS},
     * {@link org.apache.commons.math.geometry.partitioning.Hyperplane.Side#MINUS MINUS}
     * or {@link org.apache.commons.math.geometry.partitioning.Hyperplane.Side#HYPER HYPER}
     * (in dimension 1, this method <em>never</em> returns {@link
     * org.apache.commons.math.geometry.partitioning.Hyperplane.Side#BOTH BOTH})
     *
     */
    public Side side(final SubHyperplane sub) {
        final double global = getOffset(((OrientedPoint) sub.getHyperplane()).location);
        return (global < -1.0e-10) ? Side.MINUS : ((global > 1.0e-10) ? Side.PLUS : Side.HYPER);
    }

    /** Split a sub-hyperplane in two parts by the instance.
     * @param sub sub-hyperplane to split
     * @return an object containing both the part of the sub-hyperplane
     * on the plus side of the instance and the part of the
     * sub-hyperplane on the minus side of the instance
     */
    public SplitSubHyperplane split(final SubHyperplane sub) {
        final double global = getOffset(((OrientedPoint) sub.getHyperplane()).location);
        return (global < -1.0e-10) ? new SplitSubHyperplane(null, sub) : new SplitSubHyperplane(sub, null);
    }

    /** Get the hyperplane location on the real line.
     * @return the hyperplane location
     */
    public Point1D getLocation() {
        return location;
    }

    /** Check if the hyperplane orientation is direct.
     * @return true if the plus side of the hyperplane is towards
     * abscissae greater than hyperplane location
     */
    public boolean isDirect() {
        return direct;
    }

    /** Revert the instance.
     */
    public void revertSelf() {
        direct = !direct;
    }

    /** Dummy region representing the whole set of reals. */
    private static class DummyRegion extends Region {

        /** Simple constructor.
         */
        public DummyRegion() {
            super();
        }

        /** {@inheritDoc} */
        public Region buildNew(final BSPTree tree) {
            return this;
        }

        /** {@inheritDoc} */
        protected void computeGeometricalProperties() {
            setSize(0);
            setBarycenter(Point1D.ZERO);
        }
    }

}
