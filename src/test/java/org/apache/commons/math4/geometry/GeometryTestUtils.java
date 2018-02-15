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
package org.apache.commons.math4.geometry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math4.geometry.euclidean.oned.Cartesian1D;
import org.apache.commons.math4.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math4.geometry.euclidean.oned.IntervalsSet;
import org.apache.commons.math4.geometry.euclidean.oned.OrientedPoint;
import org.apache.commons.math4.geometry.euclidean.oned.SubOrientedPoint;
import org.apache.commons.math4.geometry.euclidean.oned.Vector1D;
import org.apache.commons.math4.geometry.euclidean.threed.Cartesian3D;
import org.apache.commons.math4.geometry.euclidean.threed.Euclidean3D;
import org.apache.commons.math4.geometry.euclidean.threed.Plane;
import org.apache.commons.math4.geometry.euclidean.threed.SubPlane;
import org.apache.commons.math4.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math4.geometry.euclidean.twod.Cartesian2D;
import org.apache.commons.math4.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math4.geometry.euclidean.twod.Line;
import org.apache.commons.math4.geometry.euclidean.twod.PolygonsSet;
import org.apache.commons.math4.geometry.euclidean.twod.SubLine;
import org.apache.commons.math4.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math4.geometry.partitioning.BSPTree;
import org.apache.commons.math4.geometry.partitioning.BSPTreeVisitor;
import org.junit.Assert;

/** Class containing various geometry-related test utilities.
 * @since 4.0
 */
public class GeometryTestUtils {

    /** Asserts that corresponding values in the given vectors are equal, using the specified
     * tolerance value.
     * @param expected
     * @param actual
     * @param tolerance
     */
    public static void assertVectorEquals(Vector1D expected, Vector1D actual, double tolerance) {
        String msg = "Expected vector to equal " + expected + " but was " + actual + ";";
        Assert.assertEquals(msg, expected.getX(), actual.getX(), tolerance);
    }

    /** Asserts that corresponding values in the given vectors are equal, using the specified
     * tolerance value.
     * @param expected
     * @param actual
     * @param tolerance
     */
    public static void assertVectorEquals(Vector2D expected, Vector2D actual, double tolerance) {
        String msg = "Expected vector to equal " + expected + " but was " + actual + ";";
        Assert.assertEquals(msg, expected.getX(), actual.getX(), tolerance);
        Assert.assertEquals(msg, expected.getY(), actual.getY(), tolerance);
    }

    /** Asserts that corresponding values in the given vectors are equal, using the specified
     * tolerance value.
     * @param expected
     * @param actual
     * @param tolerance
     */
    public static void assertVectorEquals(Vector3D expected, Vector3D actual, double tolerance) {
        String msg = "Expected vector to equal " + expected + " but was " + actual + ";";
        Assert.assertEquals(msg, expected.getX(), actual.getX(), tolerance);
        Assert.assertEquals(msg, expected.getY(), actual.getY(), tolerance);
        Assert.assertEquals(msg, expected.getZ(), actual.getZ(), tolerance);
    }

    /** Asserts that the given value is positive infinity.
     * @param value
     */
    public static void assertPositiveInfinity(double value) {
        String msg = "Expected value to be positive infinity but was " + value;
        Assert.assertTrue(msg, Double.isInfinite(value));
        Assert.assertTrue(msg, value > 0);
    }

    /** Asserts that the given value is negative infinity..
     * @param value
     */
    public static void assertNegativeInfinity(double value) {
        String msg = "Expected value to be negative infinity but was " + value;
        Assert.assertTrue(msg, Double.isInfinite(value));
        Assert.assertTrue(msg, value < 0);
    }

    /** Prints a string representation of the given 1D {@link BSPTree} to
     * the console. This is intended for quick debugging of small trees.
     * @param tree
     */
    public static void printTree1D(BSPTree<Euclidean1D> tree) {
        TreePrinter1D printer = new TreePrinter1D();
        System.out.println(printer.writeAsString(tree));
    }

    /** Prints a string representation of the given 2D {@link BSPTree} to
     * the console. This is intended for quick debugging of small trees.
     * @param tree
     */
    public static void printTree2D(BSPTree<Euclidean2D> tree) {
        TreePrinter2D printer = new TreePrinter2D();
        System.out.println(printer.writeAsString(tree));
    }

    /** Prints a string representation of the given 3D {@link BSPTree} to
     * the console. This is intended for quick debugging of small trees.
     * @param tree
     */
    public static void printTree3D(BSPTree<Euclidean3D> tree) {
        TreePrinter3D printer = new TreePrinter3D();
        System.out.println(printer.writeAsString(tree));
    }

    /**
     * Base for classes that create string representations of {@link BSPTree}s.
     * @param <S>
     */
    public static abstract class TreePrinter<S extends Space> implements BSPTreeVisitor<S> {

        /** Indent per tree level */
        protected static final String INDENT = "    ";

        /** Current depth in the tree */
        protected int depth;

        /** Contains the string output */
        protected StringBuilder output = new StringBuilder();

        /** Returns a string representation of the given {@link BSPTree}.
         * @param tree
         * @return
         */
        public String writeAsString(BSPTree<S> tree) {
            output.delete(0, output.length());

            tree.visit(this);

            return output.toString();
        }

        /** {@inheritDoc} */
        @Override
        public Order visitOrder(BSPTree<S> node) {
            return Order.SUB_MINUS_PLUS;
        }

        /** {@inheritDoc} */
        @Override
        public void visitInternalNode(BSPTree<S> node) {
            writeLinePrefix(node);
            writeInternalNode(node);

            write("\n");

            ++depth;
        }

        /** {@inheritDoc} */
        @Override
        public void visitLeafNode(BSPTree<S> node) {
            writeLinePrefix(node);
            writeLeafNode(node);

            write("\n");

            BSPTree<S> cur = node;
            while (cur.getParent() != null && cur.getParent().getPlus() == cur) {
                --depth;
                cur = cur.getParent();
            }
        }

        /** Writes the prefix for the current line in the output. This includes
         * the line indent, the plus/minus node indicator, and a string identifier
         * for the node itself.
         * @param node
         */
        protected void writeLinePrefix(BSPTree<S> node) {
            for (int i=0; i<depth; ++i) {
                write(INDENT);
            }

            if (node.getParent() != null) {
                if (node.getParent().getMinus() == node) {
                    write("[-] ");
                }
                else {
                    write("[+] ");
                }
            }

            write(nodeIdString(node) + " | ");
        }

        /** Returns a short string identifier for the given node.
         * @param node
         * @return
         */
        protected String nodeIdString(BSPTree<S> node) {
            String str = Objects.toString(node);
            int idx = str.lastIndexOf('.');
            if (idx > -1) {
                return str.substring(idx + 1, str.length());
            }
            return str;
        }

        /** Adds the given string to the output.
         * @param str
         */
        protected void write(String str) {
            output.append(str);
        }

        /** Method for subclasses to provide their own string representation
         * of the given internal node.
         */
        protected abstract void writeInternalNode(BSPTree<S> node);

        /** Writes a leaf node. The default implementation here simply writes
         * the node attribute as a string.
         * @param node
         */
        protected void writeLeafNode(BSPTree<S> node) {
            write(String.valueOf(node.getAttribute()));
        }
    }

    /** Class for creating string representations of 1D {@link BSPTree}s.
     */
    public static class TreePrinter1D extends TreePrinter<Euclidean1D> {

        /** {@inheritDoc} */
        @Override
        protected void writeInternalNode(BSPTree<Euclidean1D> node) {
            SubOrientedPoint cut = (SubOrientedPoint) node.getCut();

            OrientedPoint hyper = (OrientedPoint) cut.getHyperplane();
            write("cut = { hyperplane: ");
            if (hyper.isDirect()) {
                write("[" + hyper.getLocation().getX() + ", inf)");
            }
            else {
                write("(-inf, " + hyper.getLocation().getX() + "]");
            }

            IntervalsSet remainingRegion = (IntervalsSet) cut.getRemainingRegion();
            if (remainingRegion != null) {
                write(", remainingRegion: [");

                boolean isFirst = true;
                for (double[] interval : remainingRegion) {
                    if (isFirst) {
                        isFirst = false;
                    }
                    else {
                        write(", ");
                    }
                    write(Arrays.toString(interval));
                }

                write("]");
            }

            write("}");
        }
    }

    /** Class for creating string representations of 2D {@link BSPTree}s.
     */
    public static class TreePrinter2D extends TreePrinter<Euclidean2D> {

        /** {@inheritDoc} */
        @Override
        protected void writeInternalNode(BSPTree<Euclidean2D> node) {
            SubLine cut = (SubLine) node.getCut();
            Line line = (Line) cut.getHyperplane();
            IntervalsSet remainingRegion = (IntervalsSet) cut.getRemainingRegion();

            write("cut = { angle: " + FastMath.toDegrees(line.getAngle()) + ", origin: " + line.toSpace(Cartesian1D.ZERO) + "}");
            write(", remainingRegion: [");

            boolean isFirst = true;
            for (double[] interval : remainingRegion) {
                if (isFirst) {
                    isFirst = false;
                }
                else {
                    write(", ");
                }
                write(Arrays.toString(interval));
            }

            write("]");
        }
    }

    /** Class for creating string representations of 3D {@link BSPTree}s.
     */
    public static class TreePrinter3D extends TreePrinter<Euclidean3D> {

        /** {@inheritDoc} */
        @Override
        protected void writeInternalNode(BSPTree<Euclidean3D> node) {
            SubPlane cut = (SubPlane) node.getCut();
            Plane plane = (Plane) cut.getHyperplane();
            PolygonsSet polygon = (PolygonsSet) cut.getRemainingRegion();

            write("cut = { normal: " + plane.getNormal() + ", origin: " + plane.getOrigin() + "}");
            write(", remainingRegion = [");

            boolean isFirst = true;
            for (Cartesian2D[] loop : polygon.getVertices()) {
                // convert to 3-space for easier debugging
                List<Cartesian3D> loop3 = new ArrayList<>();
                for (Cartesian2D vertex : loop) {
                    if (vertex != null) {
                        loop3.add(plane.toSpace(vertex));
                    }
                    else {
                        loop3.add(null);
                    }
                }

                if (isFirst) {
                    isFirst = false;
                }
                else {
                    write(", ");
                }

                write(loop3.toString());
            }

            write("]");
        }
    }
}
