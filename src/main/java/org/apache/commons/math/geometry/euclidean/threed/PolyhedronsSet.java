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
package org.apache.commons.math.geometry.euclidean.threed;

import java.awt.geom.AffineTransform;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.math.geometry.euclidean.twod.Point2D;
import org.apache.commons.math.geometry.partitioning.BSPTree;
import org.apache.commons.math.geometry.partitioning.BSPTreeVisitor;
import org.apache.commons.math.geometry.partitioning.Hyperplane;
import org.apache.commons.math.geometry.partitioning.Point;
import org.apache.commons.math.geometry.partitioning.Region;
import org.apache.commons.math.geometry.partitioning.SubHyperplane;
import org.apache.commons.math.geometry.partitioning.Transform;
import org.apache.commons.math.util.FastMath;

/** This class represents a 3D region: a set of polyhedrons.
 * @version $Revision$ $Date$
 */
public class PolyhedronsSet extends Region {

    /** Build a polyhedrons set representing the whole real line.
     */
    public PolyhedronsSet() {
        super();
    }

    /** Build a polyhedrons set from a BSP tree.
     * <p>The leaf nodes of the BSP tree <em>must</em> have a
     * {@code Boolean} attribute representing the inside status of
     * the corresponding cell (true for inside cells, false for outside
     * cells). In order to avoid building too many small objects, it is
     * recommended to use the predefined constants
     * {@code Boolean.TRUE} and {@code Boolean.FALSE}</p>
     * @param tree inside/outside BSP tree representing the region
     */
    public PolyhedronsSet(final BSPTree tree) {
        super(tree);
    }

    /** Build a polyhedrons set from a Boundary REPresentation (B-rep).
     * <p>The boundary is provided as a collection of {@link
     * SubHyperplane sub-hyperplanes}. Each sub-hyperplane has the
     * interior part of the region on its minus side and the exterior on
     * its plus side.</p>
     * <p>The boundary elements can be in any order, and can form
     * several non-connected sets (like for example polyhedrons with holes
     * or a set of disjoints polyhedrons considered as a whole). In
     * fact, the elements do not even need to be connected together
     * (their topological connections are not used here). However, if the
     * boundary does not really separate an inside open from an outside
     * open (open having here its topological meaning), then subsequent
     * calls to the {@link Region#checkPoint(Point) checkPoint} method will
     * not be meaningful anymore.</p>
     * <p>If the boundary is empty, the region will represent the whole
     * space.</p>
     * @param boundary collection of boundary elements, as a
     * collection of {@link SubHyperplane SubHyperplane} objects
     */
    public PolyhedronsSet(final Collection<SubHyperplane> boundary) {
        super(boundary);
    }

    /** Build a parallellepipedic box.
     * @param xMin low bound along the x direction
     * @param xMax high bound along the x direction
     * @param yMin low bound along the y direction
     * @param yMax high bound along the y direction
     * @param zMin low bound along the z direction
     * @param zMax high bound along the z direction
     */
    public PolyhedronsSet(final double xMin, final double xMax,
                          final double yMin, final double yMax,
                          final double zMin, final double zMax) {
        this(buildConvex(Arrays.asList(new Hyperplane[] {
            new Plane(new Vector3D(xMin, 0,    0),   Vector3D.MINUS_I),
            new Plane(new Vector3D(xMax, 0,    0),   Vector3D.PLUS_I),
            new Plane(new Vector3D(0,    yMin, 0),   Vector3D.MINUS_J),
            new Plane(new Vector3D(0,    yMax, 0),   Vector3D.PLUS_J),
            new Plane(new Vector3D(0,    0,   zMin), Vector3D.MINUS_K),
            new Plane(new Vector3D(0,    0,   zMax), Vector3D.PLUS_K)
        })).getTree(false));
    }

    /** {@inheritDoc} */
    public Region buildNew(final BSPTree tree) {
        return new PolyhedronsSet(tree);
    }

    /** {@inheritDoc} */
    protected void computeGeometricalProperties() {

        // compute the contribution of all boundary facets
        getTree(true).visit(new FacetsContributionVisitor());

        if (getSize() < 0) {
            // the polyhedrons set as a finite outside
            // surrounded by an infinite inside
            setSize(Double.POSITIVE_INFINITY);
            setBarycenter(Point3D.UNDEFINED);
        } else {
            // the polyhedrons set is finite, apply the remaining scaling factors
            setSize(getSize() / 3.0);
            setBarycenter(new Point3D(1.0 / (4 * getSize()), (Vector3D) getBarycenter()));
        }

    }

    /** Visitor computing geometrical properties. */
    private class FacetsContributionVisitor implements BSPTreeVisitor {

        /** Simple constructor. */
        public FacetsContributionVisitor() {
            setSize(0);
            setBarycenter(new Point3D(0, 0, 0));
        }

        /** {@inheritDoc} */
        public Order visitOrder(final BSPTree node) {
            return Order.MINUS_SUB_PLUS;
        }

        /** {@inheritDoc} */
        public void visitInternalNode(final BSPTree node) {
            final BoundaryAttribute attribute = (BoundaryAttribute) node.getAttribute();
            if (attribute.getPlusOutside() != null) {
                addContribution(attribute.getPlusOutside(), false);
            }
            if (attribute.getPlusInside() != null) {
                addContribution(attribute.getPlusInside(), true);
            }
        }

        /** {@inheritDoc} */
        public void visitLeafNode(final BSPTree node) {
        }

        /** Add he contribution of a boundary facet.
         * @param facet boundary facet
         * @param reversed if true, the facet has the inside on its plus side
         */
        private void addContribution(final SubHyperplane facet, final boolean reversed) {

            final Region polygon = facet.getRemainingRegion();
            final double area    = polygon.getSize();

            if (Double.isInfinite(area)) {
                setSize(Double.POSITIVE_INFINITY);
                setBarycenter(Point3D.UNDEFINED);
            } else {

                final Plane    plane  = (Plane) facet.getHyperplane();
                final Vector3D facetB = (Point3D) plane.toSpace(polygon.getBarycenter());
                double   scaled = area * Vector3D.dotProduct(facetB, plane.getNormal());
                if (reversed) {
                    scaled = -scaled;
                }

                setSize(getSize() + scaled);
                setBarycenter(new Point3D(1.0, (Point3D) getBarycenter(), scaled, facetB));

            }

        }

    }

    /** Get the first sub-hyperplane crossed by a semi-infinite line.
     * @param point start point of the part of the line considered
     * @param line line to consider (contains point)
     * @return the first sub-hyperplaned crossed by the line after the
     * given point, or null if the line does not intersect any
     * sub-hyperplaned
     */
    public SubHyperplane firstIntersection(final Vector3D point, final Line line) {
        return recurseFirstIntersection(getTree(true), point, line);
    }

    /** Get the first sub-hyperplane crossed by a semi-infinite line.
     * @param node current node
     * @param point start point of the part of the line considered
     * @param line line to consider (contains point)
     * @return the first sub-hyperplaned crossed by the line after the
     * given point, or null if the line does not intersect any
     * sub-hyperplaned
     */
    private SubHyperplane recurseFirstIntersection(final BSPTree node,
                                                   final Vector3D point,
                                                   final Line line) {

        final SubHyperplane cut = node.getCut();
        if (cut == null) {
            return null;
        }
        final BSPTree minus = node.getMinus();
        final BSPTree plus  = node.getPlus();
        final Plane   plane = (Plane) cut.getHyperplane();

        // establish search order
        final double offset = plane.getOffset((Point) point);
        final boolean in    = FastMath.abs(offset) < 1.0e-10;
        final BSPTree near;
        final BSPTree far;
        if (offset < 0) {
            near = minus;
            far  = plus;
        } else {
            near = plus;
            far  = minus;
        }

        if (in) {
            // search in the cut hyperplane
            final SubHyperplane facet = boundaryFacet(point, node);
            if (facet != null) {
                return facet;
            }
        }

        // search in the near branch
        final SubHyperplane crossed = recurseFirstIntersection(near, point, line);
        if (crossed != null) {
            return crossed;
        }

        if (!in) {
            // search in the cut hyperplane
            final Vector3D hit3D = plane.intersection(line);
            if (hit3D != null) {
                final SubHyperplane facet = boundaryFacet(hit3D, node);
                if (facet != null) {
                    return facet;
                }
            }
        }

        // search in the far branch
        return recurseFirstIntersection(far, point, line);

    }

    /** Check if a point belongs to the boundary part of a node.
     * @param point point to check
     * @param node node containing the boundary facet to check
     * @return the boundary facet this points belongs to (or null if it
     * does not belong to any boundary facet)
     */
    private SubHyperplane boundaryFacet(final Vector3D point, final BSPTree node) {
        final Point point2D = node.getCut().getHyperplane().toSubSpace((Point) point);
        final BoundaryAttribute attribute = (BoundaryAttribute) node.getAttribute();
        if ((attribute.getPlusOutside() != null) &&
            (attribute.getPlusOutside().getRemainingRegion().checkPoint(point2D) == Location.INSIDE)) {
            return attribute.getPlusOutside();
        }
        if ((attribute.getPlusInside() != null) &&
            (attribute.getPlusInside().getRemainingRegion().checkPoint(point2D) == Location.INSIDE)) {
            return attribute.getPlusInside();
        }
        return null;
    }

    /** Rotate the region around the specified point.
     * <p>The instance is not modified, a new instance is created.</p>
     * @param center rotation center
     * @param rotation vectorial rotation operator
     * @return a new instance representing the rotated region
     */
    public PolyhedronsSet rotate(final Vector3D center, final Rotation rotation) {
        return (PolyhedronsSet) applyTransform(new RotationTransform(center, rotation));
    }

    /** 3D rotation as a Transform. */
    private static class RotationTransform implements Transform {

        /** Center point of the rotation. */
        private Vector3D   center;

        /** Vectorial rotation. */
        private Rotation   rotation;

        /** Cached original hyperplane. */
        private Hyperplane cachedOriginal;

        /** Cached 2D transform valid inside the cached original hyperplane. */
        private Transform  cachedTransform;

        /** Build a rotation transform.
         * @param center center point of the rotation
         * @param rotation vectorial rotation
         */
        public RotationTransform(final Vector3D center, final Rotation rotation) {
            this.center   = center;
            this.rotation = rotation;
        }

        /** {@inheritDoc} */
        public Point apply(final Point point) {
            final Vector3D delta = ((Vector3D) point).subtract(center);
            return new Point3D(1.0, center, 1.0, rotation.applyTo(delta));
        }

        /** {@inheritDoc} */
        public Hyperplane apply(final Hyperplane hyperplane) {
            return ((Plane) hyperplane).rotate(center, rotation);
        }

        /** {@inheritDoc} */
        public SubHyperplane apply(final SubHyperplane sub,
                                   final Hyperplane original, final Hyperplane transformed) {
            if (original != cachedOriginal) {
                // we have changed hyperplane, reset the in-hyperplane transform

                final Plane    oPlane = (Plane) original;
                final Plane    tPlane = (Plane) transformed;
                final Vector3D p00    = oPlane.getOrigin();
                final Vector3D p10    = (Vector3D) oPlane.toSpace(new Point2D(1.0, 0.0));
                final Vector3D p01    = (Vector3D) oPlane.toSpace(new Point2D(0.0, 1.0));
                final Point2D  tP00   = (Point2D) tPlane.toSubSpace(apply((Point) p00));
                final Point2D  tP10   = (Point2D) tPlane.toSubSpace(apply((Point) p10));
                final Point2D  tP01   = (Point2D) tPlane.toSubSpace(apply((Point) p01));
                final AffineTransform at =
                    new AffineTransform(tP10.getX() - tP00.getX(), tP10.getY() - tP00.getY(),
                                        tP01.getX() - tP00.getX(), tP01.getY() - tP00.getY(),
                                        tP00.getX(), tP00.getY());

                cachedOriginal  = original;
                cachedTransform = org.apache.commons.math.geometry.euclidean.twod.Line.getTransform(at);

            }
            return sub.applyTransform(cachedTransform);
        }

    }

    /** Translate the region by the specified amount.
     * <p>The instance is not modified, a new instance is created.</p>
     * @param translation translation to apply
     * @return a new instance representing the translated region
     */
    public PolyhedronsSet translate(final Vector3D translation) {
        return (PolyhedronsSet) applyTransform(new TranslationTransform(translation));
    }

    /** 3D translation as a transform. */
    private static class TranslationTransform implements Transform {

        /** Translation vector. */
        private Vector3D   translation;

        /** Cached original hyperplane. */
        private Hyperplane cachedOriginal;

        /** Cached 2D transform valid inside the cached original hyperplane. */
        private Transform  cachedTransform;

        /** Build a translation transform.
         * @param translation translation vector
         */
        public TranslationTransform(final Vector3D translation) {
            this.translation = translation;
        }

        /** {@inheritDoc} */
        public Point apply(final Point point) {
            return new Point3D(1.0, (Vector3D) point, 1.0, translation);
        }

        /** {@inheritDoc} */
        public Hyperplane apply(final Hyperplane hyperplane) {
            return ((Plane) hyperplane).translate(translation);
        }

        /** {@inheritDoc} */
        public SubHyperplane apply(final SubHyperplane sub,
                                   final Hyperplane original, final Hyperplane transformed) {
            if (original != cachedOriginal) {
                // we have changed hyperplane, reset the in-hyperplane transform

                final Plane   oPlane = (Plane) original;
                final Plane   tPlane = (Plane) transformed;
                final Point2D shift  = (Point2D) tPlane.toSubSpace(apply((Point) oPlane.getOrigin()));
                final AffineTransform at =
                    AffineTransform.getTranslateInstance(shift.getX(), shift.getY());

                cachedOriginal  = original;
                cachedTransform =
                    org.apache.commons.math.geometry.euclidean.twod.Line.getTransform(at);

            }

            return sub.applyTransform(cachedTransform);

        }

    }

}
