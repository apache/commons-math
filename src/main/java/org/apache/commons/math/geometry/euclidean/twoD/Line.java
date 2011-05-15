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
package org.apache.commons.math.geometry.euclidean.twoD;

import java.awt.geom.AffineTransform;

import org.apache.commons.math.exception.MathIllegalArgumentException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.geometry.euclidean.oneD.IntervalsSet;
import org.apache.commons.math.geometry.euclidean.oneD.OrientedPoint;
import org.apache.commons.math.geometry.euclidean.oneD.Point1D;
import org.apache.commons.math.geometry.partitioning.BSPTree;
import org.apache.commons.math.geometry.partitioning.Hyperplane;
import org.apache.commons.math.geometry.partitioning.Point;
import org.apache.commons.math.geometry.partitioning.Region;
import org.apache.commons.math.geometry.partitioning.SubHyperplane;
import org.apache.commons.math.geometry.partitioning.SubSpace;
import org.apache.commons.math.geometry.partitioning.Transform;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.util.MathUtils;

/** This class represents an oriented line in the 2D plane.

 * <p>An oriented line can be defined either by prolongating a line
 * segment between two points past these points, or by one point and
 * an angular direction (in trigonometric orientation).</p>

 * <p>Since it is oriented the two half planes at its two sides are
 * unambiguously identified as a left half plane and a right half
 * plane. This can be used to identify the interior and the exterior
 * in a simple way by local properties only when part of a line is
 * used to define part of a polygon boundary.</p>

 * <p>A line can also be used to completely define a reference frame
 * in the plane. It is sufficient to select one specific point in the
 * line (the orthogonal projection of the original reference frame on
 * the line) and to use the unit vector in the line direction and the
 * orthogonal vector oriented from left half plane to right half
 * plane. We define two coordinates by the process, the
 * <em>abscissa</em> along the line, and the <em>offset</em> across
 * the line. All points of the plane are uniquely identified by these
 * two coordinates. The line is the set of points at zero offset, the
 * left half plane is the set of points with negative offsets and the
 * right half plane is the set of points with positive offsets.</p>

 * @version $Revision$ $Date$
 */
public class Line implements Hyperplane {

    /** Angle with respect to the abscissa axis. */
    private double angle;

    /** Cosine of the line angle. */
    private double cos;

    /** Sine of the line angle. */
    private double sin;

    /** Offset of the frame origin. */
    private double originOffset;

    /** Build a line from two points.
     * <p>The line is oriented from p1 to p2</p>
     * @param p1 first point
     * @param p2 second point
     */
    public Line(final Point2D p1, final Point2D p2) {
        reset(p1, p2);
    }

    /** Build a line from a point and an angle.
     * @param p point belonging to the line
     * @param angle angle of the line with respect to abscissa axis
     */
    public Line(final Point2D p, final double angle) {
        reset(p, angle);
    }

    /** Build a line from its internal characteristics.
     * @param angle angle of the line with respect to abscissa axis
     * @param cos cosine of the angle
     * @param sin sine of the angle
     * @param originOffset offset of the origin
     */
    private Line(final double angle, final double cos, final double sin, final double originOffset) {
        this.angle        = angle;
        this.cos          = cos;
        this.sin          = sin;
        this.originOffset = originOffset;
    }

    /** Copy constructor.
     * <p>The created instance is completely independant from the
     * original instance, it is a deep copy.</p>
     * @param line line to copy
     */
    public Line(final Line line) {
        angle        = MathUtils.normalizeAngle(line.angle, FastMath.PI);
        cos          = FastMath.cos(angle);
        sin          = FastMath.sin(angle);
        originOffset = line.originOffset;
    }

    /** {@inheritDoc} */
    public Hyperplane copySelf() {
        return new Line(this);
    }

    /** Reset the instance as if built from two points.
     * <p>The line is oriented from p1 to p2</p>
     * @param p1 first point
     * @param p2 second point
     */
    public void reset(final Point2D p1, final Point2D p2) {
        final double dx = p2.x - p1.x;
        final double dy = p2.y - p1.y;
        final double d = FastMath.hypot(dx, dy);
        if (d == 0.0) {
            angle        = 0.0;
            cos          = 1.0;
            sin          = 0.0;
            originOffset = p1.y;
        } else {
            angle        = FastMath.PI + FastMath.atan2(-dy, -dx);
            cos          = FastMath.cos(angle);
            sin          = FastMath.sin(angle);
            originOffset = (p2.x * p1.y - p1.x * p2.y) / d;
        }
    }

    /** Reset the instance as if built from a line and an angle.
     * @param p point belonging to the line
     * @param alpha angle of the line with respect to abscissa axis
     */
    public void reset(final Point2D p, final double alpha) {
        this.angle   = MathUtils.normalizeAngle(alpha, FastMath.PI);
        cos          = FastMath.cos(this.angle);
        sin          = FastMath.sin(this.angle);
        originOffset = cos * p.y - sin * p.x;
    }

    /** Revert the instance.
     */
    public void revertSelf() {
        if (angle < FastMath.PI) {
            angle += FastMath.PI;
        } else {
            angle -= FastMath.PI;
        }
        cos          = -cos;
        sin          = -sin;
        originOffset = -originOffset;
    }

    /** Get the reverse of the instance.
     * <p>Get a line with reversed orientation with respect to the
     * instance. A new object is built, the instance is untouched.</p>
     * @return a new line, with orientation opposite to the instance orientation
     */
    public Line getReverse() {
        return new Line((angle < FastMath.PI) ? (angle + FastMath.PI) : (angle - FastMath.PI),
                        -cos, -sin, -originOffset);
    }

    /** Transform a 2D space point into a line point.
     * @param point 2D point (must be a {@link Point2D Point2D}
     * instance)
     * @return line point corresponding to the 2D point (really a {@link
     * org.apache.commons.math.geometry.euclidean.oneD.Point1D Point1D} instance)
     * @see #toSpace
     */
    public Point toSubSpace(final Point point) {
        final Point2D p2D = (Point2D) point;
        return new Point1D(cos * p2D.x + sin * p2D.y);
    }

    /** Get one point from the line.
     * @param point desired abscissa for the point (must be a {@link
     * org.apache.commons.math.geometry.euclidean.oneD.Point1D Point1D} instance)
     * @return line point at specified abscissa (really a {@link Point2D
     * Point2D} instance)
     */
    public Point toSpace(final Point point) {
        final double abscissa = ((Point1D) point).getAbscissa();
        return new Point2D(abscissa * cos - originOffset * sin,
                           abscissa * sin + originOffset * cos);
    }

    /** Get the intersection point of the instance and another line.
     * @param other other line
     * @return intersection point of the instance and the other line
     * (really a {@link Point2D Point2D} instance)
     */
    public SubSpace intersection(final Hyperplane other) {
        final Line otherL = (Line) other;
        final double d = sin * otherL.cos - otherL.sin * cos;
        if (FastMath.abs(d) < 1.0e-10) {
            return null;
        }
        return new Point2D((cos * otherL.originOffset - otherL.cos * originOffset) / d,
                           (sin * otherL.originOffset - otherL.sin * originOffset) / d);
    }

    /** Build a region covering the whole hyperplane.
     * @return a region covering the whole hyperplane
     */
    public Region wholeHyperplane() {
        return new IntervalsSet();
    }

    /** Build a region covering the whole space.
     * @return a region containing the instance (really a {@link
     * PolygonsSet PolygonsSet} instance)
     */
    public Region wholeSpace() {
        return new PolygonsSet();
    }

    /** Get the offset (oriented distance) of a parallel line.
     * <p>This method should be called only for parallel lines otherwise
     * the result is not meaningful.</p>
     * <p>The offset is 0 if both lines are the same, it is
     * positive if the line is on the right side of the instance and
     * negative if it is on the left side, according to its natural
     * orientation.</p>
     * @param line line to check
     * @return offset of the line
     */
    public double getOffset(final Line line) {
        return originOffset +
               ((cos * line.cos + sin * line.sin > 0) ? -line.originOffset : line.originOffset);
    }

    /** Get the offset (oriented distance) of a point to the line.
     * <p>The offset is 0 if the point belongs to the line, it is
     * positive if the point is on the right side of the line and
     * negative if it is on the left side, according to its natural
     * orientation.</p>
     * @param point point to check (must be a {@link Point2D Point2D} instance)
     * @return offset of the point
     */
    public double getOffset(final Point point) {
        final Point2D p2D = (Point2D) point;
        return sin * p2D.x - cos * p2D.y + originOffset;
    }

    /** Check if the instance has the same orientation as another hyperplane.
     * <p>This method is expected to be called on parallel hyperplanes
     * (i.e. when the {@link #side side} method would return {@link
     * org.apache.commons.math.geometry.partitioning.Hyperplane.Side#HYPER HYPER}
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
        final Line otherL = (Line) other;
        return (sin * otherL.sin + cos * otherL.cos) >= 0.0;
    }

    /** Get one point from the plane.
     * @param abscissa desired abscissa for the point
     * @param offset desired offset for the point
     * @return one point in the plane, with given abscissa and offset
     * relative to the line
     */
    public Point2D getPointAt(final Point1D abscissa, final double offset) {
        final double x       = abscissa.getAbscissa();
        final double dOffset = offset - originOffset;
        return new Point2D(x * cos + dOffset * sin, x * sin - dOffset * cos);
    }

    /** Check if the line contains a point.
     * @param p point to check
     * @return true if p belongs to the line
     */
    public boolean contains(final Point2D p) {
        return FastMath.abs(getOffset(p)) < 1.0e-10;
    }

    /** Check the instance is parallel to another line.
     * @param line other line to check
     * @return true if the instance is parallel to the other line
     * (they can have either the same or opposite orientations)
     */
    public boolean isParallelTo(final Line line) {
        return FastMath.abs(sin * line.cos - cos * line.sin) < 1.0e-10;
    }

    /** Translate the line to force it passing by a point.
     * @param p point by which the line should pass
     */
    public void translateToPoint(final Point2D p) {
        originOffset = cos * p.y - sin * p.x;
    }

    /** Get the angle of the line.
     * @return the angle of the line with respect to the abscissa axis
     */
    public double getAngle() {
        return MathUtils.normalizeAngle(angle, FastMath.PI);
    }

    /** Set the angle of the line.
     * @param angle new angle of the line with respect to the abscissa axis
     */
    public void setAngle(final double angle) {
        this.angle = MathUtils.normalizeAngle(angle, FastMath.PI);
        cos        = FastMath.cos(this.angle);
        sin        = FastMath.sin(this.angle);
    }

    /** Get the offset of the origin.
     * @return the offset of the origin
     */
    public double getOriginOffset() {
        return originOffset;
    }

    /** Set the offset of the origin.
     * @param offset offset of the origin
     */
    public void setOriginOffset(final double offset) {
        originOffset = offset;
    }

    /** Compute the relative position of a sub-hyperplane with respect
     * to the instance.
     * @param sub sub-hyperplane to check
     * @return one of {@link org.apache.commons.math.geometry.partitioning.Hyperplane.Side#PLUS PLUS},
     * {@link org.apache.commons.math.geometry.partitioning.Hyperplane.Side#MINUS MINUS},
     * {@link org.apache.commons.math.geometry.partitioning.Hyperplane.Side#BOTH BOTH},
     * {@link org.apache.commons.math.geometry.partitioning.Hyperplane.Side#HYPER HYPER}
     */
    public Side side(final SubHyperplane sub) {

        final Hyperplane otherHyp = sub.getHyperplane();
        final Point2D    crossing = (Point2D) intersection(otherHyp);

        if (crossing == null) {
            // the lines are parallel,
            final double global = getOffset((Line) otherHyp);
            return (global < -1.0e-10) ? Side.MINUS : ((global > 1.0e-10) ? Side.PLUS : Side.HYPER);
        }

        // the lines do intersect
        final boolean direct = FastMath.sin(((Line) otherHyp).angle - angle) < 0;
        final Point1D x = (Point1D) otherHyp.toSubSpace(crossing);
        return sub.getRemainingRegion().side(new OrientedPoint(x, direct));

    }

    /** Split a sub-hyperplane in two parts by the instance.
     * @param sub sub-hyperplane to split
     * @return an object containing both the part of the sub-hyperplane
     * on the plus side of the instance and the part of the
     * sub-hyperplane on the minus side of the instance
     */
    public SplitSubHyperplane split(final SubHyperplane sub) {

        final Line    otherLine = (Line) sub.getHyperplane();
        final Point2D crossing  = (Point2D) intersection(otherLine);

        if (crossing == null) {
            // the lines are parallel
            final double global = getOffset(otherLine);
            return (global < -1.0e-10) ?
                   new SplitSubHyperplane(null, sub) :
                   new SplitSubHyperplane(sub, null);
        }

        // the lines do intersect
        final boolean direct = FastMath.sin(otherLine.angle - angle) < 0;
        final Point1D x      = (Point1D) otherLine.toSubSpace(crossing);
        final SubHyperplane subPlus  = new SubHyperplane(new OrientedPoint(x, !direct));
        final SubHyperplane subMinus = new SubHyperplane(new OrientedPoint(x, direct));

        final BSPTree splitTree =
            sub.getRemainingRegion().getTree(false).split(subMinus);
        final BSPTree plusTree  = Region.isEmpty(splitTree.getPlus()) ?
                                  new BSPTree(Boolean.FALSE) :
                                  new BSPTree(subPlus, new BSPTree(Boolean.FALSE),
                                              splitTree.getPlus(), null);
        final BSPTree minusTree = Region.isEmpty(splitTree.getMinus()) ?
                                  new BSPTree(Boolean.FALSE) :
                                  new BSPTree(subMinus, new BSPTree(Boolean.FALSE),
                                              splitTree.getMinus(), null);

        return new SplitSubHyperplane(new SubHyperplane(otherLine.copySelf(),
                                                        new IntervalsSet(plusTree)),
                                                        new SubHyperplane(otherLine.copySelf(),
                                                                          new IntervalsSet(minusTree)));

    }

    /** Get a {@link org.apache.commons.math.geometry.partitioning.Transform
     * Transform} embedding an affine transform.
     * @param transform affine transform to embed (must be inversible
     * otherwise the {@link
     * org.apache.commons.math.geometry.partitioning.Transform#apply(Hyperplane)
     * apply(Hyperplane)} method would work only for some lines, and
     * fail for other ones)
     * @return a new transform that can be applied to either {@link
     * Point2D Point2D}, {@link Line Line} or {@link
     * org.apache.commons.math.geometry.partitioning.SubHyperplane
     * SubHyperplane} instances
     * @exception MathIllegalArgumentException if the transform is non invertible
     */
    public static Transform getTransform(final AffineTransform transform) throws MathIllegalArgumentException {
        return new LineTransform(transform);
    }

    /** Class embedding an affine transform.
     * <p>This class is used in order to apply an affine transform to a
     * line. Using a specific object allow to perform some computations
     * on the transform only once even if the same transform is to be
     * applied to a large number of lines (for example to a large
     * polygon)./<p>
     */
    private static class LineTransform implements Transform {

        // CHECKSTYLE: stop JavadocVariable check
        private double cXX;
        private double cXY;
        private double cX1;
        private double cYX;
        private double cYY;
        private double cY1;

        private double c1Y;
        private double c1X;
        private double c11;
        // CHECKSTYLE: resume JavadocVariable check

        /** Build an affine line transform from a n {@code AffineTransform}.
         * @param transform transform to use (must be invertible otherwise
         * the {@link LineTransform#apply(Hyperplane)} method would work
         * only for some lines, and fail for other ones)
         * @exception MathIllegalArgumentException if the transform is non invertible
         */
        public LineTransform(final AffineTransform transform) throws MathIllegalArgumentException {

            final double[] m = new double[6];
            transform.getMatrix(m);
            cXX = m[0];
            cXY = m[2];
            cX1 = m[4];
            cYX = m[1];
            cYY = m[3];
            cY1 = m[5];

            c1Y = cXY * cY1 - cYY * cX1;
            c1X = cXX * cY1 - cYX * cX1;
            c11 = cXX * cYY - cYX * cXY;

            if (FastMath.abs(c11) < 1.0e-20) {
                throw new MathIllegalArgumentException(LocalizedFormats.NON_INVERTIBLE_TRANSFORM);
            }

        }

        /** {@inheritDoc} */
        public Point apply(final Point point) {
            final Point2D p2D = (Point2D) point;
            final double  x   = p2D.getX();
            final double  y   = p2D.getY();
            return new Point2D(cXX * x + cXY * y + cX1,
                               cYX * x + cYY * y + cY1);
        }

        /** {@inheritDoc} */
        public Hyperplane apply(final Hyperplane hyperplane) {
            final Line   line    = (Line) hyperplane;
            final double rOffset = c1X * line.cos + c1Y * line.sin + c11 * line.originOffset;
            final double rCos    = cXX * line.cos + cXY * line.sin;
            final double rSin    = cYX * line.cos + cYY * line.sin;
            final double inv     = 1.0 / FastMath.sqrt(rSin * rSin + rCos * rCos);
            return new Line(FastMath.PI + FastMath.atan2(-rSin, -rCos),
                            inv * rCos, inv * rSin,
                            inv * rOffset);
        }

        /** {@inheritDoc} */
        public SubHyperplane apply(final SubHyperplane sub,
                                   final Hyperplane original, final Hyperplane transformed) {
            final OrientedPoint op = (OrientedPoint) sub.getHyperplane();
            final Point1D newLoc =
                (Point1D) transformed.toSubSpace(apply(original.toSpace(op.getLocation())));
            return new SubHyperplane(new OrientedPoint(newLoc, op.isDirect()));
        }

    }

}
