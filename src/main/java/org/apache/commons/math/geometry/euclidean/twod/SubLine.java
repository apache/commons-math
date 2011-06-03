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
package org.apache.commons.math.geometry.euclidean.twod;

import org.apache.commons.math.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math.geometry.euclidean.oned.IntervalsSet;
import org.apache.commons.math.geometry.euclidean.oned.OrientedPoint;
import org.apache.commons.math.geometry.euclidean.oned.Vector1D;
import org.apache.commons.math.geometry.partitioning.AbstractSubHyperplane;
import org.apache.commons.math.geometry.partitioning.BSPTree;
import org.apache.commons.math.geometry.partitioning.Hyperplane;
import org.apache.commons.math.geometry.partitioning.Region;
import org.apache.commons.math.geometry.partitioning.Side;
import org.apache.commons.math.geometry.partitioning.SubHyperplane;
import org.apache.commons.math.util.FastMath;

/** This class represents a sub-hyperplane for {@link Line}.
 * @version $Id:$
 * @since 3.0
 */
public class SubLine extends AbstractSubHyperplane<Euclidean2D, Euclidean1D> {

    /** Simple constructor.
     * @param hyperplane underlying hyperplane
     * @param remainingRegion remaining region of the hyperplane
     */
    public SubLine(final Hyperplane<Euclidean2D> hyperplane,
                   final Region<Euclidean1D> remainingRegion) {
        super(hyperplane, remainingRegion);
    }

    /** {@inheritDoc} */
    protected AbstractSubHyperplane<Euclidean2D, Euclidean1D> buildNew(final Hyperplane<Euclidean2D> hyperplane,
                                                                       final Region<Euclidean1D> remainingRegion) {
        return new SubLine(hyperplane, remainingRegion);
    }

    /** {@inheritDoc} */
    public Side side(final Hyperplane<Euclidean2D> hyperplane) {

        final Line    thisLine  = (Line) getHyperplane();
        final Line    otherLine = (Line) hyperplane;
        final Vector2D crossing  = thisLine.intersection(otherLine);

        if (crossing == null) {
            // the lines are parallel,
            final double global = otherLine.getOffset(thisLine);
            return (global < -1.0e-10) ? Side.MINUS : ((global > 1.0e-10) ? Side.PLUS : Side.HYPER);
        }

        // the lines do intersect
        final boolean direct = FastMath.sin(thisLine.getAngle() - otherLine.getAngle()) < 0;
        final Vector1D x = (Vector1D) thisLine.toSubSpace(crossing);
        return getRemainingRegion().side(new OrientedPoint(x, direct));

    }

    /** {@inheritDoc} */
    public SplitSubHyperplane<Euclidean2D> split(final Hyperplane<Euclidean2D> hyperplane) {

        final Line    thisLine  = (Line) getHyperplane();
        final Line    otherLine = (Line) hyperplane;
        final Vector2D crossing  = thisLine.intersection(otherLine);

        if (crossing == null) {
            // the lines are parallel
            final double global = otherLine.getOffset(thisLine);
            return (global < -1.0e-10) ?
                   new SplitSubHyperplane<Euclidean2D>(null, this) :
                   new SplitSubHyperplane<Euclidean2D>(this, null);
        }

        // the lines do intersect
        final boolean direct = FastMath.sin(thisLine.getAngle() - otherLine.getAngle()) < 0;
        final Vector1D x      = (Vector1D) thisLine.toSubSpace(crossing);
        final SubHyperplane<Euclidean1D> subPlus  = new OrientedPoint(x, !direct).wholeHyperplane();
        final SubHyperplane<Euclidean1D> subMinus = new OrientedPoint(x,  direct).wholeHyperplane();

        final BSPTree<Euclidean1D> splitTree = getRemainingRegion().getTree(false).split(subMinus);
        final BSPTree<Euclidean1D> plusTree  = getRemainingRegion().isEmpty(splitTree.getPlus()) ?
                                               new BSPTree<Euclidean1D>(Boolean.FALSE) :
                                               new BSPTree<Euclidean1D>(subPlus, new BSPTree<Euclidean1D>(Boolean.FALSE),
                                                                        splitTree.getPlus(), null);
        final BSPTree<Euclidean1D> minusTree = getRemainingRegion().isEmpty(splitTree.getMinus()) ?
                                               new BSPTree<Euclidean1D>(Boolean.FALSE) :
                                               new BSPTree<Euclidean1D>(subMinus, new BSPTree<Euclidean1D>(Boolean.FALSE),
                                                                        splitTree.getMinus(), null);

        return new SplitSubHyperplane<Euclidean2D>(new SubLine(thisLine.copySelf(), new IntervalsSet(plusTree)),
                                                   new SubLine(thisLine.copySelf(), new IntervalsSet(minusTree)));

    }

}
