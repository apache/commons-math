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

import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.partitioning.Embedding;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.geometry.partitioning.Transform;
import org.apache.commons.math3.geometry.spherical.oned.Chord;
import org.apache.commons.math3.geometry.spherical.oned.ArcsSet;
import org.apache.commons.math3.geometry.spherical.oned.S1Point;
import org.apache.commons.math3.geometry.spherical.oned.Sphere1D;
import org.apache.commons.math3.util.FastMath;

/** This class represents an oriented circle on the 2-sphere.

 * <p>An oriented circle can be defined by a center point and an
 * angular radius. The circle is the the set of points that are exactly
 * at the specified angular radius from the center (which does not
 * belong to the circle it defines except if angular radius is 0).</p>

 * <p>Since it is oriented the two spherical caps at its two sides are
 * unambiguously identified as a left cap and a right cap. This can be
 * used to identify the interior and the exterior in a simple way by
 * local properties only when part of a line is used to define part of
 * a spherical polygon boundary.</p>

 * @version $Id$
 * @since 3.3
 */
public class Circle implements Hyperplane<Sphere2D>, Embedding<Sphere2D, Sphere1D> {

    /** Pole or circle center. */
    private Vector3D pole;

    /** First axis in the equator plane, origin of the phase angles. */
    private Vector3D x;

    /** Second axis in the equator plane, in quadrature with respect to x. */
    private Vector3D y;

    /** Angular radius. */
    private double radius;

    /** Cosine of the radius. */
    private double cos;

    /** Sine of the radius. */
    private double sin;

    /** Build a circle from a center and a radius.
     * <p>The circle is oriented in the trigonometric direction around center.</p>
     * @param center circle enter
     * @param radius cirle radius
     */
    public Circle(final Vector3D center, final double radius) {
        reset(center, radius);
    }

    /** Build a great circle from a center only, radius being forced to \( \pi/2 \).
     * <p>This constructor is recommended to build great circles as it does
     * ensure the exact values of \( \cos(\pi/2) = 0 and \sin(\pi) = 1 \) are used.</p>
     * <p>The circle is oriented in the trigonometric direction around center.</p>
     * @param center circle enter
     */
    public Circle(final Vector3D center) {
        reset(center);
    }

    /** Build a circle from its internal components.
     * <p>The circle is oriented in the trigonometric direction around center.</p>
     * @param pole circle pole
     * @param x first axis in the equator plane
     * @param y second axis in the equator plane
     * @param radius cirle radius
     * @param cos cosine of the radius
     * @param sin sine of the radius
     */
    private Circle(final Vector3D pole, final Vector3D x, final Vector3D y,
                   final double radius, final double cos, final double sin) {
        this.pole   = pole;
        this.x      = x;
        this.y      = y;
        this.radius = radius;
        this.cos    = cos;
        this.sin    = sin;
    }

    /** Copy constructor.
     * <p>The created instance is completely independent from the
     * original instance, it is a deep copy.</p>
     * @param circle circle to copy
     */
    public Circle(final Circle circle) {
        this(circle.pole, circle.x, circle.y, circle.radius, circle.cos, circle.sin);
    }

    /** {@inheritDoc} */
    public Circle copySelf() {
        return new Circle(this);
    }

    /** Reset the instance as if built from a center and a radius.
     * <p>The circle is oriented in the trigonometric direction around center.</p>
     * @param newCenter circle enter
     * @param newRadius cirle radius
     */
    public void reset(final Vector3D newCenter, final double newRadius) {
        reset(newCenter, newRadius, FastMath.cos(radius), FastMath.sin(radius));
    }

    /** Reset the instance as if built from a center, radius being forced to \( \pi/2 \).
     * <p>This constructor is recommended to build great circles as it does
     * ensure the exact values of \( \cos(\pi/2) = 0 and \sin(\pi) = 1 \) are used.</p>
     * <p>The circle is oriented in the trigonometric direction around center.</p>
     * @param newCenter circle enter
     */
    public void reset(final Vector3D newCenter) {
        reset(newCenter, 0.5 * FastMath.PI, 0.0, 1.0);
    }

    /** Reset the instance.
     * @param newCenter circle enter
     * @param newRadius cirle radius
     * @param newCos cosine of the radius
     * @param newSin sine of the radius
     */
    private void reset(final Vector3D newCenter, final double newRadius,
                       final double newCos, final double newSin) {
        this.pole   = newCenter.normalize();
        this.x      = newCenter.orthogonal();
        this.y      = Vector3D.crossProduct(newCenter, x).normalize();
        this.radius = newRadius;
        this.cos    = newCos;
        this.sin    = newSin;
    }

    /** Revert the instance.
     */
    public void revertSelf() {
        // x remains the same
        y      = y.negate();
        pole   = pole.negate();
        radius = FastMath.PI - radius;
        cos    = -cos;
    }

    /** Get the reverse of the instance.
     * <p>Get a circle with reversed orientation with respect to the
     * instance. A new object is built, the instance is untouched.</p>
     * @return a new circle, with orientation opposite to the instance orientation
     */
    public Circle getReverse() {
        return new Circle(pole.negate(), x, y.negate(), FastMath.PI - radius, -cos, sin);
    }

    /** {@inheritDoc}
     * @see #getPhase(Vector3D)
     */
    public S1Point toSubSpace(final Point<Sphere2D> point) {
        return new S1Point(getPhase(((S2Point) point).getVector()));
    }

    /** Get the phase angle of a direction.
     * <p>
     * The direction may not belong to the circle as the
     * phase is computed for the meridian plane between the circle
     * pole and the direction.
     * </p>
     * @param direction direction for which phase is requested
     * @return phase angle of the direction around the circle
     * @see #toSubSpace(Point)
     */
    public double getPhase(final Vector3D direction) {
        return FastMath.PI + FastMath.atan2(-direction.dotProduct(y), -direction.dotProduct(x));
    }

    /** {@inheritDoc}
     * @see #getPointAt(double)
     */
    public S2Point toSpace(final Point<Sphere1D> point) {
        return new S2Point(getPointAt(((S1Point) point).getAlpha()));
    }

    /** Get a circle point from its phase around the circle.
     * @param alpha phase around the circle
     * @return circle point on the sphere
     * @see #toSpace(Point)
     */
    public Vector3D getPointAt(final double alpha) {
        final double cosAlpha = FastMath.cos(alpha);
        final double sinAlpha = FastMath.sin(alpha);
        return new Vector3D(cosAlpha * sin, x, sinAlpha * sin, y, cos, pole);
    }

    /** Get the intersection points of the instance and another circle.
     * @param other other circle
     * @return intersection points of the instance and the other circle
     * or null if there are no intersection points
     */
    public S2Point[] intersection(final Circle other) {

        // we look for a vector as v = a pole + b other.pole +/- c pole ^ other.pole
        // and such that v angular separation with both centers is consistent
        // with each circle radius, and v is also on the 2-sphere

        final double dot = Vector3D.dotProduct(pole, other.pole);
        final double f = 1.0 / (1.0 - dot * dot);
        final double a = f * (cos - dot * other.cos);
        final double b = f * (other.cos - dot * cos);
        final Vector3D inPlane = new Vector3D(a, pole, b, other.pole);
        final double omN2 = 1.0 - inPlane.getNormSq();
        if (omN2 <= 0) {
            // no intersections (we include the just tangent case too)
            return null;
        }

        final double c = FastMath.sqrt(f * omN2);
        final Vector3D outOfPlane = new Vector3D(c, Vector3D.crossProduct(pole, other.pole));

        return new S2Point[] {
            new S2Point(inPlane.add(outOfPlane)),
            new S2Point(inPlane.subtract(outOfPlane))
        };

    }

    /** {@inheritDoc} */
    public SubCircle wholeHyperplane() {
        return new SubCircle(this, new ArcsSet());
    }

    /** Build a region covering the whole space.
     * @return a region containing the instance (really a {@link
     * SphericalPolygonsSet SphericalPolygonsSet} instance)
     */
    public SphericalPolygonsSet wholeSpace() {
        return new SphericalPolygonsSet();
    }

    /** {@inheritDoc}
     * @see #getOffset(Vector3D)
     */
    public double getOffset(final Point<Sphere2D> point) {
        return getOffset(((S2Point) point).getVector());
    }

    /** Get the offset (oriented distance) of a direction.
     * <p>The offset is defined as the angular distance between the
     * circle center and the direction minus the circle radius. It
     * is therefore 0 on the circle, positive for directions outside of
     * the cone delimited by the circle, and negative inside the cone.</p>
     * @param direction direction to check
     * @return offset of the direction
     * @see #getOffset(Point)
     */
    public double getOffset(final Vector3D direction) {
        return Vector3D.angle(pole, direction) - radius;
    }

    /** {@inheritDoc} */
    public boolean sameOrientationAs(final Hyperplane<Sphere2D> other) {
        final Circle otherC = (Circle) other;
        return Vector3D.dotProduct(pole, otherC.pole) >= 0.0;
    }

    /** Check if the circle contains a point.
     * @param p point to check
     * @return true if p belongs to the circle
     */
    public boolean contains(final Point<Sphere2D> p) {
        return FastMath.abs(getOffset(p)) < 1.0e-10;
    }

    /** Get a {@link org.apache.commons.math3.geometry.partitioning.Transform
     * Transform} embedding a 3D rotation.
     * @param rotation rotation to use
     * @return a new transform that can be applied to either {@link
     * Point<Sphere2D> Point<Sphere2D>}, {@link Circle Line} or {@link
     * org.apache.commons.math3.geometry.partitioning.SubHyperplane
     * SubHyperplane} instances
     */
    public static Transform<Sphere2D, Sphere1D> getTransform(final Rotation rotation) {
        return new CircleTransform(rotation);
    }

    /** Class embedding a 3D rotation. */
    private static class CircleTransform implements Transform<Sphere2D, Sphere1D> {

        /** Underlying rotation. */
        private final Rotation rotation;

        /** Build a transform from a {@code Rotation}.
         * @param rotation rotation to use
         */
        public CircleTransform(final Rotation rotation) {
            this.rotation = rotation;
        }

        /** {@inheritDoc} */
        public S2Point apply(final Point<Sphere2D> point) {
            return new S2Point(rotation.applyTo(((S2Point) point).getVector()));
        }

        /** {@inheritDoc} */
        public Circle apply(final Hyperplane<Sphere2D> hyperplane) {
            final Circle circle = (Circle) hyperplane;
            return new Circle(rotation.applyTo(circle.pole),
                              rotation.applyTo(circle.x),
                              rotation.applyTo(circle.y),
                              circle.radius, circle.cos, circle.sin);
        }

        /** {@inheritDoc} */
        public SubHyperplane<Sphere1D> apply(final SubHyperplane<Sphere1D> sub,
                                             final Hyperplane<Sphere2D> original,
                                             final Hyperplane<Sphere2D> transformed) {
            // as the circle is rotated, the chords are rotated too
            return sub;
        }

    }

}
