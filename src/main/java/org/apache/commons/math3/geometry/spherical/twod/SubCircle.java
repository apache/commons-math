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
package org.apache.commons.math3.geometry.spherical.twod;

import org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.Side;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.geometry.spherical.oned.Arc;
import org.apache.commons.math3.geometry.spherical.oned.ArcsSet;
import org.apache.commons.math3.geometry.spherical.oned.Chord;
import org.apache.commons.math3.geometry.spherical.oned.Sphere1D;

/** This class represents a sub-hyperplane for {@link Circle}.
 * @version $Id$
 * @since 3.3
 */
public class SubCircle extends AbstractSubHyperplane<Sphere2D, Sphere1D> {

    /** Simple constructor.
     * @param hyperplane underlying hyperplane
     * @param remainingRegion remaining region of the hyperplane
     */
    public SubCircle(final Hyperplane<Sphere2D> hyperplane,
                     final Region<Sphere1D> remainingRegion) {
        super(hyperplane, remainingRegion);
    }

    /** {@inheritDoc} */
    @Override
    protected AbstractSubHyperplane<Sphere2D, Sphere1D> buildNew(final Hyperplane<Sphere2D> hyperplane,
                                                                 final Region<Sphere1D> remainingRegion) {
        return new SubCircle(hyperplane, remainingRegion);
    }

    /** {@inheritDoc} */
    @Override
    public Side side(final Hyperplane<Sphere2D> hyperplane) {

        final Circle thisCircle  = (Circle) getHyperplane();
        final Circle otherCircle = (Circle) hyperplane;
        final Arc    arc         = thisCircle.getInsideArc(otherCircle);
        return ((ArcsSet) getRemainingRegion()).side(arc);

    }

    /** {@inheritDoc} */
    @Override
    public SplitSubHyperplane<Sphere2D> split(final Hyperplane<Sphere2D> hyperplane) {

        final Circle thisCircle  = (Circle) getHyperplane();
        final Circle otherCircle = (Circle) hyperplane;
        final Arc    arc         = thisCircle.getInsideArc(otherCircle);

        final SubHyperplane<Sphere1D> subMinus = chord.wholeHyperplane();
        final SubHyperplane<Sphere1D> subPlus  = chord.getReverse().wholeHyperplane();
        final BSPTree<Sphere1D> splitTree = getRemainingRegion().getTree(false).split(subMinus);
        final BSPTree<Sphere1D> plusTree  = getRemainingRegion().isEmpty(splitTree.getPlus()) ?
                                               new BSPTree<Sphere1D>(Boolean.FALSE) :
                                               new BSPTree<Sphere1D>(subPlus, new BSPTree<Sphere1D>(Boolean.FALSE),
                                                                     splitTree.getPlus(), null);
        final BSPTree<Sphere1D> minusTree = getRemainingRegion().isEmpty(splitTree.getMinus()) ?
                                               new BSPTree<Sphere1D>(Boolean.FALSE) :
                                               new BSPTree<Sphere1D>(subMinus, new BSPTree<Sphere1D>(Boolean.FALSE),
                                                                     splitTree.getMinus(), null);

        return new SplitSubHyperplane<Sphere2D>(new SubCircle(thisCircle.copySelf(), new ArcsSet(plusTree, thisCircle.getTolerance())),
                                                new SubCircle(thisCircle.copySelf(), new ArcsSet(minusTree, thisCircle.getTolerance())));

    }

}
