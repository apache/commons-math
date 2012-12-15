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
package org.apache.commons.math3.geometry.euclidean.twod;

import java.awt.geom.AffineTransform;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math3.geometry.euclidean.oned.IntervalsSet;
import org.apache.commons.math3.geometry.euclidean.oned.OrientedPoint;
import org.apache.commons.math3.geometry.euclidean.oned.Vector1D;
import org.apache.commons.math3.geometry.partitioning.Embedding;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.geometry.partitioning.Transform;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

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

 * @version $Id$
 * @since 3.0
 */
public class Line implements Hyperplane<Euclidean2D>, Embedding<Euclidean2D, Euclidean1D> {

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
    public Line(final Vector2D p1, final Vector2D p2) {
        reset(p1, p2);
    }

    /** Build a line from a point and an angle.
     * @param p point belonging to the line
     * @param angle angle of the line with respect to abscissa axis
     */
    public Line(final Vector2D p, final double angle) {
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
     * <p>The created instance is completely independent from the
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
    public Line copySelf() {
        return new Line(this);
    }

    /** Reset the instance as if built from two points.
     * <p>The line is oriented from p1 to p2</p>
     * @param p1 first point
     * @param p2 second point
     */
    public void reset(final Vector2D p1, final Vector2D p2) {
        final double dx = p2.getX() - p1.getX();
        final double dy = p2.getY() - p1.getY();
        final double d = FastMath.hypot(dx, dy);
        if (d == 0.0) {
            angle        = 0.0;
            cos          = 1.0;
            sin          = 0.0;
            originOffset = p1.getY();
        } else {
            angle        = FastMath.PI + FastMath.atan2(-dy, -dx);
            cos          = FastMath.cos(angle);
            sin          = FastMath.sin(angle);
            originOffset = (p2.getX() * p1.getY() - p1.getX() * p2.getY()) / d;
        }
    }

    /** Reset the instance as if built from a line and an angle.
     * @param p point belonging to the line
     * @param alpha angle of the line with respect to abscissa axis
     */
    public void reset(final Vector2D p, final double alpha) {
        this.angle   = MathUtils.normalizeAngle(alpha, FastMath.PI);
        cos          = FastMath.cos(this.angle);
        sin          = FastMath.sin(this.angle);
        originOffset = cos * p.getY() - sin * p.getX();
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

    /** {@inheritDoc} */
    public Vector1D toSubSpace(final Vector<Euclidean2D> point) {
        Vector2D p2 = (Vector2D) point;
        return new Vector1D(cos * p2.getX() + sin * p2.getY());
    }

    /** {@inheritDoc} */
    public Vector2D toSpace(final Vector<Euclidean1D> point) {
        final double abscissa = ((Vector1D) point).getX();
        return new Vector2D(abscissa * cos - originOffset * sin,
                            abscissa * sin + originOffset * cos);
    }

    /** Get the intersection point of the instance and another line.
     * @param other other line
     * @return intersection point of the instance and the other line
     * or null if there are no intersection points
     */
    public Vector2D intersection(final Line other) {
        final double d = sin * other.cos - other.sin * cos;
        if (FastMath.abs(d) < 1.0e-10) {
            return null;
        }
        return new Vector2D((cos * other.originOffset - other.cos * originOffset) / d,
                            (sin * other.originOffset - other.sin * originOffset) / d);
    }

    /** {@inheritDoc} */
    public SubLine wholeHyperplane() {
        return new SubLine(this, new IntervalsSet());
    }

    /** Build a region covering the whole space.
     * @return a region containing the instance (really a {@link
     * PolygonsSet PolygonsSet} instance)
     */
    public PolygonsSet wholeSpace() {
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

    /** {@inheritDoc} */
    public double getOffset(final Vector<Euclidean2D> point) {
        Vector2D p2 = (Vector2D) point;
        return sin * p2.getX() - cos * p2.getY() + originOffset;
    }

    /** {@inheritDoc} */
    public boolean sameOrientationAs(final Hyperplane<Euclidean2D> other) {
        final Line otherL = (Line) other;
        return (sin * otherL.sin + cos * otherL.cos) >= 0.0;
    }

    /** Get one point from the plane.
     * @param abscissa desired abscissa for the point
     * @param offset desired offset for the point
     * @return one point in the plane, with given abscissa and offset
     * relative to the line
     */
    public Vector2D getPointAt(final Vector1D abscissa, final double offset) {
        final double x       = abscissa.getX();
        final double dOffset = offset - originOffset;
        return new Vector2D(x * cos + dOffset * sin, x * sin - dOffset * cos);
    }

    /** Check if the line contains a point.
     * @param p point to check
     * @return true if p belongs to the line
     */
    public boolean contains(final Vector2D p) {
        return FastMath.abs(getOffset(p)) < 1.0e-10;
    }

    /** Compute the distance between the instance and a point.
     * <p>This is a shortcut for invoking FastMath.abs(getOffset(p)),
     * and provides consistency with what is in the
     * org.apache.commons.math3.geometry.euclidean.threed.Line class.</p>
     *
     * @param p to check
     * @return distance between the instance and the point
     * @since 3.1
     */
    public double distance(final Vector2D p) {
        return FastMath.abs(getOffset(p));
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
    public void translateToPoint(final Vector2D p) {
        originOffset = cos * p.getY() - sin * p.getX();
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

    /** Get a {@link org.apache.commons.math3.geometry.partitioning.Transform
     * Transform} embedding an affine transform.
     * @param transform affine transform to embed (must be inversible
     * otherwise the {@link
     * org.apache.commons.math3.geometry.partitioning.Transform#apply(Hyperplane)
     * apply(Hyperplane)} method would work only for some lines, and
     * fail for other ones)
     * @return a new transform that can be applied to either {@link
     * Vector2D Vector2D}, {@link Line Line} or {@link
     * org.apache.commons.math3.geometry.partitioning.SubHyperplane
     * SubHyperplane} instances
     * @exception MathIllegalArgumentException if the transform is non invertible
     */
    public static Transform<Euclidean2D, Euclidean1D> getTransform(final AffineTransform transform)
        throws MathIllegalArgumentException {
        return new LineTransform(transform);
    }

    /** Class embedding an affine transform.
     * <p>This class is used in order to apply an affine transform to a
     * line. Using a specific object allow to perform some computations
     * on the transform only once even if the same transform is to be
     * applied to a large number of lines (for example to a large
     * polygon)./<p>
     */
    private static class LineTransform implements Transform<Euclidean2D, Euclidean1D> {

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
        public Vector2D apply(final Vector<Euclidean2D> point) {
            final Vector2D p2D = (Vector2D) point;
            final double  x   = p2D.getX();
            final double  y   = p2D.getY();
            return new Vector2D(cXX * x + cXY * y + cX1,
                               cYX * x + cYY * y + cY1);
        }

        /** {@inheritDoc} */
        public Line apply(final Hyperplane<Euclidean2D> hyperplane) {
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
        public SubHyperplane<Euclidean1D> apply(final SubHyperplane<Euclidean1D> sub,
                                                final Hyperplane<Euclidean2D> original,
                                                final Hyperplane<Euclidean2D> transformed) {
            final OrientedPoint op     = (OrientedPoint) sub.getHyperplane();
            final Line originalLine    = (Line) original;
            final Line transformedLine = (Line) transformed;
            final Vector1D newLoc =
                transformedLine.toSubSpace(apply(originalLine.toSpace(op.getLocation())));
            return new OrientedPoint(newLoc, op.isDirect()).wholeHyperplane();
        }

    }

}
