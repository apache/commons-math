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
package org.apache.commons.math3.geometry.partitioning;

import java.util.Formatter;
import java.util.Locale;

import org.apache.commons.math3.geometry.Space;
import org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math3.geometry.euclidean.oned.IntervalsSet;
import org.apache.commons.math3.geometry.euclidean.oned.OrientedPoint;
import org.apache.commons.math3.geometry.euclidean.oned.Vector1D;
import org.apache.commons.math3.geometry.euclidean.threed.Euclidean3D;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.PolyhedronsSet;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.PolygonsSet;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.spherical.oned.ArcsSet;
import org.apache.commons.math3.geometry.spherical.oned.LimitAngle;
import org.apache.commons.math3.geometry.spherical.oned.Sphere1D;
import org.apache.commons.math3.geometry.spherical.twod.Circle;
import org.apache.commons.math3.geometry.spherical.twod.Sphere2D;
import org.apache.commons.math3.geometry.spherical.twod.SphericalPolygonsSet;

/** Class dumping a string representation of an {@link AbstractRegion}.
 * <p>
 * This class is intended for tests and debug purposes only.
 * </p>
 * @see RegionParser
 * @since 3.5
 */
public class RegionDumper {

    /** Private constructor for a utility class
     */
    private RegionDumper() {
    }

    /** Get a string representation of an {@link ArcsSet}.
     * @param arcsSet region to dump
     * @return string representation of the region
     */
    public static String dump(final ArcsSet arcsSet) {
        final TreeDumper<Sphere1D> visitor = new TreeDumper<Sphere1D>("ArcsSet", arcsSet.getTolerance()) {

            /** {@inheritDoc} */
            @Override
            protected void formatHyperplane(final Hyperplane<Sphere1D> hyperplane) {
                final LimitAngle h = (LimitAngle) hyperplane;
                getFormatter().format("%22.15e %b %22.15e",
                                      h.getLocation().getAlpha(), h.isDirect(), h.getTolerance());
            }

        };
        arcsSet.getTree(false).visit(visitor);
        return visitor.getDump();
    }

    /** Get a string representation of a {@link SphericalPolygonsSet}.
     * @param sphericalPolygonsSet region to dump
     * @return string representation of the region
     */
    public static String dump(final SphericalPolygonsSet sphericalPolygonsSet) {
        final TreeDumper<Sphere2D> visitor = new TreeDumper<Sphere2D>("SphericalPolygonsSet", sphericalPolygonsSet.getTolerance()) {

            /** {@inheritDoc} */
            @Override
            protected void formatHyperplane(final Hyperplane<Sphere2D> hyperplane) {
                final Circle h = (Circle) hyperplane;
                getFormatter().format("%22.15e %22.15e %22.15e %22.15e",
                                      h.getPole().getX(), h.getPole().getY(), h.getPole().getZ(),
                                      h.getTolerance());
            }

        };
        sphericalPolygonsSet.getTree(false).visit(visitor);
        return visitor.getDump();
    }

    /** Get a string representation of an {@link IntervalsSet}.
     * @param intervalsSet region to dump
     * @return string representation of the region
     */
    public static String dump(final IntervalsSet intervalsSet) {
        final TreeDumper<Euclidean1D> visitor = new TreeDumper<Euclidean1D>("IntervalsSet", intervalsSet.getTolerance()) {

            /** {@inheritDoc} */
            @Override
            protected void formatHyperplane(final Hyperplane<Euclidean1D> hyperplane) {
                final OrientedPoint h = (OrientedPoint) hyperplane;
                getFormatter().format("%22.15e %b %22.15e",
                                      h.getLocation().getX(), h.isDirect(), h.getTolerance());
            }

        };
        intervalsSet.getTree(false).visit(visitor);
        return visitor.getDump();
    }

    /** Get a string representation of a {@link PolygonsSet}.
     * @param polygonsSet region to dump
     * @return string representation of the region
     */
    public static String dump(final PolygonsSet polygonsSet) {
        final TreeDumper<Euclidean2D> visitor = new TreeDumper<Euclidean2D>("PolygonsSet", polygonsSet.getTolerance()) {

            /** {@inheritDoc} */
            @Override
            protected void formatHyperplane(final Hyperplane<Euclidean2D> hyperplane) {
                final Line h = (Line) hyperplane;
                final Vector2D p = h.toSpace(Vector1D.ZERO);
                getFormatter().format("%22.15e %22.15e %22.15e %22.15e",
                                      p.getX(), p.getY(), h.getAngle(), h.getTolerance());
            }

        };
        polygonsSet.getTree(false).visit(visitor);
        return visitor.getDump();
    }

    /** Get a string representation of a {@link PolyhedronsSet}.
     * @param polyhedronsSet region to dump
     * @return string representation of the region
     */
    public static String dump(final PolyhedronsSet polyhedronsSet) {
        final TreeDumper<Euclidean3D> visitor = new TreeDumper<Euclidean3D>("PolyhedronsSet", polyhedronsSet.getTolerance()) {

            /** {@inheritDoc} */
            @Override
            protected void formatHyperplane(final Hyperplane<Euclidean3D> hyperplane) {
                final Plane h = (Plane) hyperplane;
                final Vector3D p = h.toSpace(Vector2D.ZERO);
                getFormatter().format("%22.15e %22.15e %22.15e %22.15e %22.15e %22.15e %22.15e",
                                      p.getX(), p.getY(), p.getZ(),
                                      h.getNormal().getX(), h.getNormal().getY(), h.getNormal().getZ(),
                                      h.getTolerance());
            }

        };
        polyhedronsSet.getTree(false).visit(visitor);
        return visitor.getDump();
    }

    /** Dumping visitor.
     * @param <S> Type of the space.
     */
    private abstract static class TreeDumper<S extends Space> implements BSPTreeVisitor<S> {

        /** Builder for the string representation of the dumped tree. */
        private final StringBuilder dump;

        /** Formatter for strings. */
        private final Formatter formatter;

        /** Current indentation prefix. */
        private String prefix;

        /** Simple constructor.
         * @param type type of the region to dump
         * @param tolerance tolerance of the region
         */
        public TreeDumper(final String type, final double tolerance) {
            this.dump      = new StringBuilder();
            this.formatter = new Formatter(dump, Locale.US);
            this.prefix    = "";
            formatter.format("%s%n", type);
            formatter.format("tolerance %22.15e%n", tolerance);
        }

        /** Get the string representation of the tree.
         * @return string representation of the tree.
         */
        public String getDump() {
            return dump.toString();
        }

        /** Get the formatter to use.
         * @return formatter to use
         */
        protected Formatter getFormatter() {
            return formatter;
        }

        /** Format a string representation of the hyperplane underlying a cut sub-hyperplane.
         * @param hyperplane hyperplane to format
         */
        protected abstract void formatHyperplane(Hyperplane<S> hyperplane);

        /** {@inheritDoc} */
        public Order visitOrder(final BSPTree<S> node) {
            return Order.SUB_MINUS_PLUS;
        }

        /** {@inheritDoc} */
        public void visitInternalNode(final BSPTree<S> node) {
            formatter.format("%s %s internal ", prefix, type(node));
            formatHyperplane(node.getCut().getHyperplane());
            formatter.format("%n");
            prefix = prefix + "  ";
        }

        /** {@inheritDoc} */
        public void visitLeafNode(final BSPTree<S> node) {
            formatter.format("%s %s leaf %s%n",
                             prefix, type(node), node.getAttribute());
            for (BSPTree<S> n = node;
                 n.getParent() != null && n == n.getParent().getPlus();
                 n = n.getParent()) {
                prefix = prefix.substring(0, prefix.length() - 2);
            }
        }

        /** Get the type of the node.
         * @param node node to check
         * @return "plus " or "minus" depending on the node being the plus or minus
         * child of its parent ("plus " is arbitrarily returned for the root node)
         */
        private String type(final BSPTree<S> node) {
            return (node.getParent() != null && node == node.getParent().getMinus()) ? "minus" : "plus ";
        }

    }

}
