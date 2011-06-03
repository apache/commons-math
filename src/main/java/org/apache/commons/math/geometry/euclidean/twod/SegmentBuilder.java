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

import java.util.List;

import org.apache.commons.math.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math.geometry.euclidean.oned.Interval;
import org.apache.commons.math.geometry.euclidean.oned.IntervalsSet;
import org.apache.commons.math.geometry.euclidean.oned.Vector1D;
import org.apache.commons.math.geometry.partitioning.AbstractSubHyperplane;
import org.apache.commons.math.geometry.partitioning.BSPTree;
import org.apache.commons.math.geometry.partitioning.BSPTreeVisitor;
import org.apache.commons.math.geometry.partitioning.BoundaryAttribute;
import org.apache.commons.math.geometry.partitioning.SubHyperplane;
import org.apache.commons.math.geometry.partitioning.utilities.AVLTree;

/** Visitor building segments.
 * @version $Id$
 * @since 3.0
 */
class SegmentsBuilder implements BSPTreeVisitor<Euclidean2D> {

    /** Sorted segments. */
    private AVLTree<Segment> sorted;

    /** Simple constructor. */
    public SegmentsBuilder() {
        sorted = new AVLTree<Segment>();
    }

    /** {@inheritDoc} */
    public Order visitOrder(final BSPTree<Euclidean2D> node) {
        return Order.MINUS_SUB_PLUS;
    }

    /** {@inheritDoc} */
    public void visitInternalNode(final BSPTree<Euclidean2D> node) {
        @SuppressWarnings("unchecked")
        final BoundaryAttribute<Euclidean2D> attribute = (BoundaryAttribute<Euclidean2D>) node.getAttribute();
        if (attribute.getPlusOutside() != null) {
            addContribution(attribute.getPlusOutside(), false);
        }
        if (attribute.getPlusInside() != null) {
            addContribution(attribute.getPlusInside(), true);
        }
    }

    /** {@inheritDoc} */
    public void visitLeafNode(final BSPTree<Euclidean2D> node) {
    }

    /** Add he contribution of a boundary facet.
     * @param sub boundary facet
     * @param reversed if true, the facet has the inside on its plus side
     */
    private void addContribution(final SubHyperplane<Euclidean2D> sub, final boolean reversed) {
        @SuppressWarnings("unchecked")
        final AbstractSubHyperplane<Euclidean2D, Euclidean1D> absSub =
            (AbstractSubHyperplane<Euclidean2D, Euclidean1D>) sub;
        final Line line      = (Line) sub.getHyperplane();
        final List<Interval> intervals = ((IntervalsSet) absSub.getRemainingRegion()).asList();
        for (final Interval i : intervals) {
            final Vector2D start = Double.isInfinite(i.getLower()) ?
                                  null : (Vector2D) line.toSpace(new Vector1D(i.getLower()));
            final Vector2D end   = Double.isInfinite(i.getUpper()) ?
                                  null : (Vector2D) line.toSpace(new Vector1D(i.getUpper()));
            if (reversed) {
                sorted.insert(new Segment(end, start, line.getReverse()));
            } else {
                sorted.insert(new Segment(start, end, line));
            }
        }
    }

    /** Get the sorted segments.
     * @return sorted segments
     */
    public AVLTree<Segment> getSorted() {
        return sorted;
    }

}
