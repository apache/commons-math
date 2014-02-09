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

import java.util.List;

import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.geometry.enclosing.EnclosingBall;
import org.apache.commons.math3.geometry.enclosing.SupportBallGenerator;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;

/** Class generating an enclosing ball from its support points.
 * <p>
 * On the 2-sphere, enclosing balls are defined to be spherical caps,
 * i.e. regions of the sphere that are closer to the cap pole than the
 * cap angular radius. If the radius is 0, the cap contains only one
 * point: its pole. If the radius is \( \pi/2 \), the cap is the hemisphere
 * on the same side as its pole. If the radius is \( \pi \), the cap
 * contains the full sphere.
 * </p>
 * <p>
 * Due to the 2-sphere topology, the complement of any spherical cap with
 * pole P and angular radius \( \alpha \) is another spherical cap, with
 * pole -P and angular radius \( \pi - \alpha \). Both caps share the
 * same boundary points. So defining a spherical cap from a few of its
 * boundary points only is ambiguous: one cannot say if what has been
 * computed is the cap or its complement without further information. For
 * this reason, the generator must be built with a point known to lie
 * inside of the spherical cap to generate, so it can decide which of the
 * two caps should be generated.
 * </p>
 * @version $Id$
 * @since 3.3
 */
public class SphericalCapGenerator implements SupportBallGenerator<Sphere2D, S2Point> {

    /** Reference point that must be inside of the cap. */
    private final Vector3D inside;

    /** Simple constructor.
     * @param inside reference point that must be inside of generated caps
     */
    public SphericalCapGenerator(final Vector3D inside) {
        this.inside = inside;
    }

    /** {@inheritDoc} */
    public EnclosingBall<Sphere2D, S2Point> ballOnSupport(final List<S2Point> support) {

        if (support.size() < 1) {
            return new EnclosingBall<Sphere2D, S2Point>(new S2Point(inside), -1.0);
        } else {
            final S2Point vA = support.get(0);
            if (support.size() < 2) {
                return new EnclosingBall<Sphere2D, S2Point>(vA, 0, vA);
            } else {
                final S2Point vB = support.get(1);
                if (support.size() < 3) {
                    return selectCap(vA.getVector().add(vB.getVector()), 0.5 * vA.distance(vB), vA, vB);
                } else {
                    final S2Point vC = support.get(2);

                    // for improved accuracy in degenerate cases, we use exact arithmetic
                    // to compute the spherical cap pole
                    final BigFraction[] bfA    = convert(vA.getVector());
                    final BigFraction[] bfB    = convert(vB.getVector());
                    final BigFraction[] bfC    = convert(vC.getVector());
                    final BigFraction[] bfPole = crossProduct(bisectorNormal(bfA, bfB),
                                                              bisectorNormal(bfB, bfC));
                    final Vector3D      pole   = convert(bfPole);

                    // select the proper spherical cap to generate
                    return selectCap(pole, Vector3D.angle(pole, vA.getVector()), vA, vB, vC);

                }
            }
        }
    }

    /** Generate a spherical cap enclosing three circles.
     * @param c1 first circle
     * @param c2 second circle
     * @param c3 third circle
     * @return spherical cap enclosing the circles
     */
    public EnclosingBall<Sphere2D, S2Point> ballOnSupport(final Circle c1,
                                                          final Circle c2,
                                                          final Circle c3) {
        final BigFraction[] p1     = convert(c1.getPole());
        final BigFraction[] p2     = convert(c2.getPole());
        final BigFraction[] p3     = convert(c3.getPole());
        final BigFraction[] bfPole = crossProduct(subtract(p1, p2),
                                                  subtract(p2, p3));
        if (dotProduct(bfPole, p1).doubleValue() < 0) {
            bfPole[0] = bfPole[0].negate();
            bfPole[1] = bfPole[1].negate();
            bfPole[2] = bfPole[2].negate();
        }
        final Vector3D pole = convert(bfPole);

        return new EnclosingBall<Sphere2D, S2Point>(new S2Point(pole),
                                                    Vector3D.angle(pole, c1.getPole()) + 0.5 * FastMath.PI);

    }

    /** Convert a vector to exact arithmetic array of Cartesian coordinates.
     * @param v vector to convert
     * @return exact arithmetic coordinates array
     */
    private BigFraction[] convert(final Vector3D v) {
        return new BigFraction[] {
            new BigFraction(v.getX()), new BigFraction(v.getY()), new BigFraction(v.getZ())
        };
    }

    /** Convert a point from exact arithmetic array of Cartesian coordinates.
     * @param a exact arithmetic coordinates array
     * @return converted point
     */
    private Vector3D convert(final BigFraction[] a) {
        return new Vector3D(a[0].doubleValue(), a[1].doubleValue(), a[2].doubleValue());
    }

    /** Find the normal to the perpendicular bisector between two points.
     * @param u first vector
     * @param v second vector
     * @return normal to the plane bisecting (u, v)
     */
    private BigFraction[] bisectorNormal(final BigFraction[] u, final BigFraction[] v) {
        return crossProduct(crossProduct(u, v), add(u, v));
    }

    /** Add two vectors
     * @param u first vector
     * @param v second vector
     * @return u + v
     */
    private BigFraction[] add(final BigFraction[] u, final BigFraction[] v) {
        return new BigFraction[] {
            u[0].add(v[0]),
            u[1].add(v[1]),
            u[2].add(v[2]),
        };
    }

    /** Subtract two vectors
     * @param u first vector
     * @param v second vector
     * @return u - v
     */
    private BigFraction[] subtract(final BigFraction[] u, final BigFraction[] v) {
        return new BigFraction[] {
            u[0].subtract(v[0]),
            u[1].subtract(v[1]),
            u[2].subtract(v[2]),
        };
    }

    /** Compute cross product of two vectors
     * @param u first vector
     * @param v second vector
     * @return u ^ v
     */
    private BigFraction[] crossProduct(final BigFraction[] u, final BigFraction[] v) {
        return new BigFraction[] {
            u[1].multiply(v[2]).subtract(u[2].multiply(v[1])),
            u[2].multiply(v[0]).subtract(u[0].multiply(v[2])),
            u[0].multiply(v[1]).subtract(u[1].multiply(v[0]))
        };
    }

    /** Compute dot product of two vectors
     * @param u first vector
     * @param v second vector
     * @return u.v
     */
    private BigFraction dotProduct(final BigFraction[] u, final BigFraction[] v) {
        return u[0].multiply(v[0]).add(u[1].multiply(v[1])).add(u[2].multiply(v[2]));
    }

    /** Select the spherical cap or its complement to ensure outside point is outside.
     * @param pole spherical cap pole (or its opposite)
     * @param radius spherical cap angular radius (or its complement to \( \pi \))
     * @param support support points
     * @return spherical cap that does <em>not</em> contain the outside point
     */
    private EnclosingBall<Sphere2D, S2Point> selectCap(final Vector3D pole, final double radius,
                                                       final S2Point ... support) {
        if (Vector3D.angle(pole, inside) <= radius) {
            // it is already the good pole and radius
            return new EnclosingBall<Sphere2D, S2Point>(new S2Point(pole), radius, support);
        } else {
            // we have to select the complement spherical cap
            return new EnclosingBall<Sphere2D, S2Point>(new S2Point(pole.negate()),
                                                        FastMath.PI - radius,
                                                        support);
        }
    }

}
