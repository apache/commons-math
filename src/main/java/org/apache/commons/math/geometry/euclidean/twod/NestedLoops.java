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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.math.exception.MathIllegalArgumentException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.geometry.euclidean.oned.OrientedPoint;
import org.apache.commons.math.geometry.euclidean.oned.Point1D;
import org.apache.commons.math.geometry.partitioning.Hyperplane;
import org.apache.commons.math.geometry.partitioning.Region;
import org.apache.commons.math.geometry.partitioning.SubHyperplane;

/** This class represent a tree of nested 2D boundary loops.

 * <p>This class is used during Piece instances construction.
 * Beams are built using the outline edges as
 * representative of facets, the orientation of these facets are
 * meaningful. However, we want to allow the user to specify its
 * outline loops without having to take care of this orientation. This
 * class is devoted to correct mis-oriented loops.<p>

 * <p>Orientation is computed assuming the piece is finite, i.e. the
 * outermost loops have their exterior side facing points at infinity,
 * and hence are oriented counter-clockwise. The orientation of
 * internal loops is computed as the reverse of the orientation of
 * their immediate surrounding loop.</p>

 * @version $Revision$ $Date$
 */
class NestedLoops {

    /** Boundary loop. */
    private Point2D[] loop;

    /** Surrounded loops. */
    private ArrayList<NestedLoops> surrounded;

    /** Polygon enclosing a finite region. */
    private Region polygon;

    /** Indicator for original loop orientation. */
    private boolean originalIsClockwise;

    /** Simple Constructor.
     * <p>Build an empty tree of nested loops. This instance will become
     * the root node of a complete tree, it is not associated with any
     * loop by itself, the outermost loops are in the root tree child
     * nodes.</p>
     */
    public NestedLoops() {
        surrounded = new ArrayList<NestedLoops>();
    }

    /** Constructor.
     * <p>Build a tree node with neither parent nor children</p>
     * @param loop boundary loop (will be reversed in place if needed)
     * @exception MathIllegalArgumentException if an outline has an open boundary loop
     */
    private NestedLoops(final Point2D[] loop) throws MathIllegalArgumentException {

        if (loop[0] == null) {
            throw new MathIllegalArgumentException(LocalizedFormats.OUTLINE_BOUNDARY_LOOP_OPEN);
        }

        this.loop = loop;
        surrounded = new ArrayList<NestedLoops>();

        // build the polygon defined by the loop
        final ArrayList<SubHyperplane> edges = new ArrayList<SubHyperplane>();
        Point2D current = loop[loop.length - 1];
        for (int i = 0; i < loop.length; ++i) {
            final Point2D previous = current;
            current = loop[i];
            final Line   line   = new Line(previous, current);
            final Region region =  Region.buildConvex(Arrays.asList(new Hyperplane[] {
                new OrientedPoint((Point1D) line.toSubSpace(previous), false),
                new OrientedPoint((Point1D) line.toSubSpace(current),  true)
            }));
            edges.add(new SubHyperplane(line, region));
        }
        polygon = new PolygonsSet(edges);

        // ensure the polygon encloses a finite region of the plane
        if (Double.isInfinite(polygon.getSize())) {
            polygon = polygon.getComplement();
            originalIsClockwise = false;
        } else {
            originalIsClockwise = true;
        }

    }

    /** Add a loop in a tree.
     * @param bLoop boundary loop (will be reversed in place if needed)
     * @exception MathIllegalArgumentException if an outline has crossing
     * boundary loops or open boundary loops
     */
    public void add(final Point2D[] bLoop) throws MathIllegalArgumentException {
        add(new NestedLoops(bLoop));
    }

    /** Add a loop in a tree.
     * @param node boundary loop (will be reversed in place if needed)
     * @exception MathIllegalArgumentException if an outline has boundary
     * loops that cross each other
     */
    private void add(final NestedLoops node) throws MathIllegalArgumentException {

        // check if we can go deeper in the tree
        for (final NestedLoops child : surrounded) {
            if (child.polygon.contains(node.polygon)) {
                child.add(node);
                return;
            }
        }

        // check if we can absorb some of the instance children
        for (final Iterator<NestedLoops> iterator = surrounded.iterator(); iterator.hasNext();) {
            final NestedLoops child = iterator.next();
            if (node.polygon.contains(child.polygon)) {
                node.surrounded.add(child);
                iterator.remove();
            }
        }

        // we should be separate from the remaining children
        for (final NestedLoops child : surrounded) {
            if (!Region.intersection(node.polygon, child.polygon).isEmpty()) {
                throw new MathIllegalArgumentException(LocalizedFormats.CROSSING_BOUNDARY_LOOPS);
            }
        }

        surrounded.add(node);

    }

    /** Correct the orientation of the loops contained in the tree.
     * <p>This is this method that really inverts the loops that where
     * provided through the {@link #add(Point2D[]) add} method if
     * they are mis-oriented</p>
     */
    public void correctOrientation() {
        for (NestedLoops child : surrounded) {
            child.setClockWise(true);
        }
    }

    /** Set the loop orientation.
     * @param clockwise if true, the loop should be set to clockwise
     * orientation
     */
    private void setClockWise(final boolean clockwise) {

        if (originalIsClockwise ^ clockwise) {
            // we need to inverse the original loop
            int min = -1;
            int max = loop.length;
            while (++min < --max) {
                final Point2D tmp = loop[min];
                loop[min] = loop[max];
                loop[max] = tmp;
            }
        }

        // go deeper in the tree
        for (final NestedLoops child : surrounded) {
            child.setClockWise(!clockwise);
        }

    }

}
