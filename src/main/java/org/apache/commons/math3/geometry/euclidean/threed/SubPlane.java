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
package org.apache.commons.math3.geometry.euclidean.threed;

import org.apache.commons.math3.geometry.euclidean.oned.Vector1D;
import org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.euclidean.twod.PolygonsSet;
import org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.Side;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;

/** This class represents a sub-hyperplane for {@link Plane}.
 * @version $Id$
 * @since 3.0
 */
public class SubPlane extends AbstractSubHyperplane<Euclidean3D, Euclidean2D> {

    /** Simple constructor.
     * @param hyperplane underlying hyperplane
     * @param remainingRegion remaining region of the hyperplane
     */
    public SubPlane(final Hyperplane<Euclidean3D> hyperplane,
                    final Region<Euclidean2D> remainingRegion) {
        super(hyperplane, remainingRegion);
    }

    /** {@inheritDoc} */
    @Override
    protected AbstractSubHyperplane<Euclidean3D, Euclidean2D> buildNew(final Hyperplane<Euclidean3D> hyperplane,
                                                                       final Region<Euclidean2D> remainingRegion) {
        return new SubPlane(hyperplane, remainingRegion);
    }

    /** {@inheritDoc} */
    @Override
    public Side side(Hyperplane<Euclidean3D> hyperplane) {

        final Plane otherPlane = (Plane) hyperplane;
        final Plane thisPlane  = (Plane) getHyperplane();
        final Line  inter      = otherPlane.intersection(thisPlane);

        if (inter == null) {
            // the hyperplanes are parallel,
            // any point can be used to check their relative position
            final double global = otherPlane.getOffset(thisPlane);
            return (global < -1.0e-10) ? Side.MINUS : ((global > 1.0e-10) ? Side.PLUS : Side.HYPER);
        }

        // create a 2D line in the otherPlane canonical 2D frame such that:
        //   - the line is the crossing line of the two planes in 3D
        //   - the line splits the otherPlane in two half planes with an
        //     orientation consistent with the orientation of the instance
        //     (i.e. the 3D half space on the plus side (resp. minus side)
        //      of the instance contains the 2D half plane on the plus side
        //      (resp. minus side) of the 2D line
        Vector2D p = thisPlane.toSubSpace(inter.toSpace(Vector1D.ZERO));
        Vector2D q = thisPlane.toSubSpace(inter.toSpace(Vector1D.ONE));
        Vector3D crossP = Vector3D.crossProduct(inter.getDirection(), thisPlane.getNormal());
        if (crossP.dotProduct(otherPlane.getNormal()) < 0) {
            final Vector2D tmp = p;
            p           = q;
            q           = tmp;
        }
        final org.apache.commons.math3.geometry.euclidean.twod.Line line2D =
            new org.apache.commons.math3.geometry.euclidean.twod.Line(p, q);

        // check the side on the 2D plane
        return getRemainingRegion().side(line2D);

    }

    /** Split the instance in two parts by an hyperplane.
     * @param hyperplane splitting hyperplane
     * @return an object containing both the part of the instance
     * on the plus side of the instance and the part of the
     * instance on the minus side of the instance
     */
    @Override
    public SplitSubHyperplane<Euclidean3D> split(Hyperplane<Euclidean3D> hyperplane) {

        final Plane otherPlane = (Plane) hyperplane;
        final Plane thisPlane  = (Plane) getHyperplane();
        final Line  inter      = otherPlane.intersection(thisPlane);

        if (inter == null) {
            // the hyperplanes are parallel
            final double global = otherPlane.getOffset(thisPlane);
            return (global < -1.0e-10) ?
                   new SplitSubHyperplane<Euclidean3D>(null, this) :
                   new SplitSubHyperplane<Euclidean3D>(this, null);
        }

        // the hyperplanes do intersect
        Vector2D p = thisPlane.toSubSpace(inter.toSpace(Vector1D.ZERO));
        Vector2D q = thisPlane.toSubSpace(inter.toSpace(Vector1D.ONE));
        Vector3D crossP = Vector3D.crossProduct(inter.getDirection(), thisPlane.getNormal());
        if (crossP.dotProduct(otherPlane.getNormal()) < 0) {
            final Vector2D tmp = p;
            p           = q;
            q           = tmp;
        }
        final SubHyperplane<Euclidean2D> l2DMinus =
            new org.apache.commons.math3.geometry.euclidean.twod.Line(p, q).wholeHyperplane();
        final SubHyperplane<Euclidean2D> l2DPlus =
            new org.apache.commons.math3.geometry.euclidean.twod.Line(q, p).wholeHyperplane();

        final BSPTree<Euclidean2D> splitTree = getRemainingRegion().getTree(false).split(l2DMinus);
        final BSPTree<Euclidean2D> plusTree  = getRemainingRegion().isEmpty(splitTree.getPlus()) ?
                                               new BSPTree<Euclidean2D>(Boolean.FALSE) :
                                               new BSPTree<Euclidean2D>(l2DPlus, new BSPTree<Euclidean2D>(Boolean.FALSE),
                                                                        splitTree.getPlus(), null);

        final BSPTree<Euclidean2D> minusTree = getRemainingRegion().isEmpty(splitTree.getMinus()) ?
                                               new BSPTree<Euclidean2D>(Boolean.FALSE) :
                                                   new BSPTree<Euclidean2D>(l2DMinus, new BSPTree<Euclidean2D>(Boolean.FALSE),
                                                                            splitTree.getMinus(), null);

        return new SplitSubHyperplane<Euclidean3D>(new SubPlane(thisPlane.copySelf(), new PolygonsSet(plusTree)),
                                                   new SubPlane(thisPlane.copySelf(), new PolygonsSet(minusTree)));

    }

}
