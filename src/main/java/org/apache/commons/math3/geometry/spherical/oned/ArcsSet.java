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
package org.apache.commons.math3.geometry.spherical.oned;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.math3.geometry.partitioning.AbstractRegion;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.Precision;

/** This class represents a region of a circle: a set of arcs.
 * @version $Id$
 * @since 3.3
 */
public class ArcsSet extends AbstractRegion<Sphere1D, Sphere1D> {

    /** Tolerance below which close sub-arcs are merged together. */
    private final double tolerance;

    /** Build an arcs set representing the whole circle.
     * @param tolerance tolerance below which close sub-arcs are merged together
     */
    public ArcsSet(final double tolerance) {
        this.tolerance = tolerance;
    }

    /** Build an arcs set corresponding to a single arc.
     * <p>
     * As the circle is a closed curve, {@code lower} is
     * allowed to be greater than {@code upper}, and will
     * be automatically canonicalized so the arc wraps
     * around \( 2\pi \), but without exceeding a total
     * length of \( 2\pi \). If {@code lower} is equals
     * to {@code upper}, the arc is considered to be the full
     * circle.
     * </p>
     * @param lower lower bound of the arc
     * @param upper upper bound of the arc
     * @param tolerance tolerance below which close sub-arcs are merged together
     */
    public ArcsSet(final double lower, final double upper, final double tolerance) {
        super(buildTree(lower, upper, tolerance));
        this.tolerance = tolerance;
    }

    /** Build an arcs set from an inside/outside BSP tree.
     * <p>The leaf nodes of the BSP tree <em>must</em> have a
     * {@code Boolean} attribute representing the inside status of
     * the corresponding cell (true for inside cells, false for outside
     * cells). In order to avoid building too many small objects, it is
     * recommended to use the predefined constants
     * {@code Boolean.TRUE} and {@code Boolean.FALSE}</p>
     * @param tree inside/outside BSP tree representing the arcs set
     * @param tolerance tolerance below which close sub-arcs are merged together
     */
    public ArcsSet(final BSPTree<Sphere1D> tree, final double tolerance) {
        super(tree);
        this.tolerance = tolerance;
    }

    /** Build an arcs set from a Boundary REPresentation (B-rep).
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
     * calls to the {@link
     * org.apache.commons.math3.geometry.partitioning.Region#checkPoint(org.apache.commons.math3.geometry.Vector)
     * checkPoint} method will not be meaningful anymore.</p>
     * <p>If the boundary is empty, the region will represent the whole
     * space.</p>
     * @param boundary collection of boundary elements
     * @param tolerance tolerance below which close sub-arcs are merged together
     */
    public ArcsSet(final Collection<SubHyperplane<Sphere1D>> boundary, final double tolerance) {
        super(boundary);
        this.tolerance = tolerance;
    }

    /** Build an inside/outside tree representing a single arc.
     * <p>
     * As the circle is a closed curve, {@code lower} is
     * allowed to be greater than {@code upper}, and will
     * be automatically canonicalized so the arc wraps
     * around \( 2\pi \), but without exceeding a total
     * length of \( 2\pi \). If {@code lower} is equals
     * to {@code upper}, the arc is considered to be the full
     * circle.
     * </p>
     * @param lower lower angular bound of the arc
     * @param upper upper angular bound of the arc
     * @param tolerance tolerance below which close sub-arcs are merged together
     * @return the built tree
     */
    private static BSPTree<Sphere1D> buildTree(final double lower, final double upper, final double tolerance) {

        if (Precision.equals(lower, upper, 0)) {
            // the tree must cover the whole real line
            return new BSPTree<Sphere1D>(Boolean.TRUE);
        }

        // the two boundary angles define only one cutting chord
        final double normalizedUpper = MathUtils.normalizeAngle(upper, lower + FastMath.PI);
        return new BSPTree<Sphere1D>(new Chord(lower, normalizedUpper, tolerance).wholeHyperplane(),
                                     new BSPTree<Sphere1D>(Boolean.FALSE),
                                     new BSPTree<Sphere1D>(Boolean.TRUE),
                                     null);

    }

    /** {@inheritDoc} */
    @Override
    public ArcsSet buildNew(final BSPTree<Sphere1D> tree) {
        return new ArcsSet(tree, tolerance);
    }

    /** {@inheritDoc} */
    @Override
    protected void computeGeometricalProperties() {
        if (getTree(false).getCut() == null) {
            setBarycenter(S1Point.NaN);
            setSize(((Boolean) getTree(false).getAttribute()) ? 2 * FastMath.PI : 0);
        } else {
            double size = 0.0;
            double sum  = 0.0;
            for (final Arc arc : asList()) {
                size += arc.getSize();
                sum  += arc.getSize() * arc.getBarycenter();
            }
            setSize(size);
            if (Precision.equals(size, 2 * FastMath.PI, 0)) {
                setBarycenter(S1Point.NaN);
            } else if (size >= Precision.SAFE_MIN) {
                setBarycenter(new S1Point(sum / size));
            } else {
                final Chord chord = (Chord) getTree(false).getCut().getHyperplane();
                setBarycenter(new S1Point(0.5 * (chord.getStart() + chord.getEnd())));
            }
        }
    }

    /** Build an ordered list of arcs representing the instance.
     * <p>This method builds this arcs set as an ordered list of
     * {@link Arc Arc} elements. An empty tree will build an empty list
     * while a tree representing the whole real line will build a one
     * element list with bounds set to \( 0 and \pi \).</p>
     * @return a new ordered list containing {@link Arc Arc} elements
     */
    public List<Arc> asList() {
        final List<Arc> list = new ArrayList<Arc>();
        recurseList(getTree(false), list, 0, 2 * FastMath.PI);
        return list;
    }

    /** Update an arcs list.
     * @param node current node
     * @param list list to update
     * @param lower lower bound of the current convex cell
     * @param upper upper bound of the current convex cell
     */
    private void recurseList(final BSPTree<Sphere1D> node, final List<Arc> list,
                             final double lower, final double upper) {

        if (node.getCut() == null) {
            if ((Boolean) node.getAttribute()) {
                // this leaf cell is an inside cell: an arc
                list.add(new Arc(lower, upper));
            }
        } else {
            final SubChord  cut      = (SubChord) node.getCut();
            final List<Arc> cutArcs  = cut.getSubArcs();
            final double    cutStart = cutArcs.get(0).getInf();
            final double    cutEnd   = cutArcs.get(cutArcs.size() - 1).getSup();

            recurseList(node.getMinus(), list, lower, cutStart);
            if (list.get(list.size() - 1).checkPoint(cutStart, tolerance) == Location.INSIDE) {
                // merge the last arc before the sub-chord and the sub-chord
                final Arc merged = new Arc(list.remove(list.size() - 1).getInf(),
                                           cutArcs.remove(0).getSup());
                cutArcs.add(0, merged);
            }

            final List<Arc> highList = new ArrayList<Arc>();
            recurseList(node.getPlus(), highList, cutEnd, upper);
            if (highList.get(0).checkPoint(cutEnd, tolerance) == Location.INSIDE) {
                // merge the first arc after the sub-chord and the sub-chord
                final Arc merged = new Arc(cutArcs.remove(cutArcs.size() - 1).getInf(),
                                           highList.remove(0).getSup());
                highList.add(0, merged);
            }

            list.addAll(highList);

        }

    }

}
