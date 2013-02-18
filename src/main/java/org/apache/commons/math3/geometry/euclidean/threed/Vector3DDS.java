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

package org.apache.commons.math3.geometry.euclidean.threed;

import java.io.Serializable;
import java.text.NumberFormat;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;

/**
 * This class is a re-implementation of {@link Vector3D} using {@link DerivativeStructure}.
 * <p>Instance of this class are guaranteed to be immutable.</p>
 * @version $Id$
 * @since 3.2
 */
public class Vector3DDS implements Serializable {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 20130214L;

    /** Abscissa. */
    private final DerivativeStructure x;

    /** Ordinate. */
    private final DerivativeStructure y;

    /** Height. */
    private final DerivativeStructure z;

    /** Simple constructor.
     * Build a vector from its coordinates
     * @param x abscissa
     * @param y ordinate
     * @param z height
     * @see #getX()
     * @see #getY()
     * @see #getZ()
     */
    public Vector3DDS(final DerivativeStructure x,
                      final DerivativeStructure y,
                      final DerivativeStructure z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /** Simple constructor.
     * Build a vector from its coordinates
     * @param v coordinates array
     * @exception DimensionMismatchException if array does not have 3 elements
     * @see #toArray()
     */
    public Vector3DDS(final DerivativeStructure[] v) throws DimensionMismatchException {
        if (v.length != 3) {
            throw new DimensionMismatchException(v.length, 3);
        }
        this.x = v[0];
        this.y = v[1];
        this.z = v[2];
    }

    /** Simple constructor.
     * Build a vector from its azimuthal coordinates
     * @param alpha azimuth (&alpha;) around Z
     *              (0 is +X, &pi;/2 is +Y, &pi; is -X and 3&pi;/2 is -Y)
     * @param delta elevation (&delta;) above (XY) plane, from -&pi;/2 to +&pi;/2
     * @see #getAlpha()
     * @see #getDelta()
     */
    public Vector3DDS(final DerivativeStructure alpha, final DerivativeStructure delta) {
        DerivativeStructure cosDelta = delta.cos();
        this.x = alpha.cos().multiply(cosDelta);
        this.y = alpha.sin().multiply(cosDelta);
        this.z = delta.sin();
    }

    /** Multiplicative constructor
     * Build a vector from another one and a scale factor.
     * The vector built will be a * u
     * @param a scale factor
     * @param u base (unscaled) vector
     */
    public Vector3DDS(final DerivativeStructure a, final Vector3DDS u) {
        this.x = a.multiply(u.x);
        this.y = a.multiply(u.y);
        this.z = a.multiply(u.z);
    }

    /** Multiplicative constructor
     * Build a vector from another one and a scale factor.
     * The vector built will be a * u
     * @param a scale factor
     * @param u base (unscaled) vector
     */
    public Vector3DDS(final DerivativeStructure a, final Vector3D u) {
        this.x = a.multiply(u.getX());
        this.y = a.multiply(u.getY());
        this.z = a.multiply(u.getZ());
    }

    /** Multiplicative constructor
     * Build a vector from another one and a scale factor.
     * The vector built will be a * u
     * @param a scale factor
     * @param u base (unscaled) vector
     */
    public Vector3DDS(final double a, final Vector3DDS u) {
        this.x = u.x.multiply(a);
        this.y = u.y.multiply(a);
        this.z = u.z.multiply(a);
    }

    /** Linear constructor
     * Build a vector from two other ones and corresponding scale factors.
     * The vector built will be a1 * u1 + a2 * u2
     * @param a1 first scale factor
     * @param u1 first base (unscaled) vector
     * @param a2 second scale factor
     * @param u2 second base (unscaled) vector
     */
    public Vector3DDS(final DerivativeStructure a1, final Vector3DDS u1,
                      final DerivativeStructure a2, final Vector3DDS u2) {
        this.x = a1.multiply(u1.x).add(a2.multiply(u2.x));
        this.y = a1.multiply(u1.y).add(a2.multiply(u2.y));
        this.z = a1.multiply(u1.z).add(a2.multiply(u2.z));
    }

    /** Linear constructor
     * Build a vector from two other ones and corresponding scale factors.
     * The vector built will be a1 * u1 + a2 * u2
     * @param a1 first scale factor
     * @param u1 first base (unscaled) vector
     * @param a2 second scale factor
     * @param u2 second base (unscaled) vector
     */
    public Vector3DDS(final DerivativeStructure a1, final Vector3D u1,
                      final DerivativeStructure a2, final Vector3D u2) {
        this.x = a1.multiply(u1.getX()).add(a2.multiply(u2.getX()));
        this.y = a1.multiply(u1.getY()).add(a2.multiply(u2.getY()));
        this.z = a1.multiply(u1.getZ()).add(a2.multiply(u2.getZ()));
    }

    /** Linear constructor
     * Build a vector from two other ones and corresponding scale factors.
     * The vector built will be a1 * u1 + a2 * u2
     * @param a1 first scale factor
     * @param u1 first base (unscaled) vector
     * @param a2 second scale factor
     * @param u2 second base (unscaled) vector
     */
    public Vector3DDS(final double a1, final Vector3DDS u1,
                      final double a2, final Vector3DDS u2) {
        this.x = u1.x.multiply(a1).add(u2.x.multiply(a2));
        this.y = u1.y.multiply(a1).add(u2.y.multiply(a2));
        this.z = u1.z.multiply(a1).add(u2.z.multiply(a2));
    }

    /** Linear constructor
     * Build a vector from three other ones and corresponding scale factors.
     * The vector built will be a1 * u1 + a2 * u2 + a3 * u3
     * @param a1 first scale factor
     * @param u1 first base (unscaled) vector
     * @param a2 second scale factor
     * @param u2 second base (unscaled) vector
     * @param a3 third scale factor
     * @param u3 third base (unscaled) vector
     */
    public Vector3DDS(final DerivativeStructure a1, final Vector3DDS u1,
                      final DerivativeStructure a2, final Vector3DDS u2,
                      final DerivativeStructure a3, final Vector3DDS u3) {
        this.x = a1.multiply(u1.x).add(a2.multiply(u2.x)).add(a3.multiply(u3.x));
        this.y = a1.multiply(u1.y).add(a2.multiply(u2.y)).add(a3.multiply(u3.y));
        this.z = a1.multiply(u1.z).add(a2.multiply(u2.z)).add(a3.multiply(u3.z));
    }

    /** Linear constructor
     * Build a vector from three other ones and corresponding scale factors.
     * The vector built will be a1 * u1 + a2 * u2 + a3 * u3
     * @param a1 first scale factor
     * @param u1 first base (unscaled) vector
     * @param a2 second scale factor
     * @param u2 second base (unscaled) vector
     * @param a3 third scale factor
     * @param u3 third base (unscaled) vector
     */
    public Vector3DDS(final DerivativeStructure a1, final Vector3D u1,
                      final DerivativeStructure a2, final Vector3D u2,
                      final DerivativeStructure a3, final Vector3D u3) {
        this.x = a1.multiply(u1.getX()).add(a2.multiply(u2.getX())).add(a3.multiply(u3.getX()));
        this.y = a1.multiply(u1.getY()).add(a2.multiply(u2.getY())).add(a3.multiply(u3.getY()));
        this.z = a1.multiply(u1.getZ()).add(a2.multiply(u2.getZ())).add(a3.multiply(u3.getZ()));
    }

    /** Linear constructor
     * Build a vector from three other ones and corresponding scale factors.
     * The vector built will be a1 * u1 + a2 * u2 + a3 * u3
     * @param a1 first scale factor
     * @param u1 first base (unscaled) vector
     * @param a2 second scale factor
     * @param u2 second base (unscaled) vector
     * @param a3 third scale factor
     * @param u3 third base (unscaled) vector
     */
    public Vector3DDS(final double a1, final Vector3DDS u1,
                      final double a2, final Vector3DDS u2,
                      final double a3, final Vector3DDS u3) {
        this.x = u1.x.multiply(a1).add(u2.x.multiply(a2)).add(u3.x.multiply(a3));
        this.y = u1.y.multiply(a1).add(u2.y.multiply(a2)).add(u3.y.multiply(a3));
        this.z = u1.z.multiply(a1).add(u2.z.multiply(a2)).add(u3.z.multiply(a3));
    }

    /** Linear constructor
     * Build a vector from four other ones and corresponding scale factors.
     * The vector built will be a1 * u1 + a2 * u2 + a3 * u3 + a4 * u4
     * @param a1 first scale factor
     * @param u1 first base (unscaled) vector
     * @param a2 second scale factor
     * @param u2 second base (unscaled) vector
     * @param a3 third scale factor
     * @param u3 third base (unscaled) vector
     * @param a4 fourth scale factor
     * @param u4 fourth base (unscaled) vector
     */
    public Vector3DDS(final DerivativeStructure a1, final Vector3DDS u1,
                      final DerivativeStructure a2, final Vector3DDS u2,
                      final DerivativeStructure a3, final Vector3DDS u3,
                      final DerivativeStructure a4, final Vector3DDS u4) {
        this.x = a1.multiply(u1.x).add(a2.multiply(u2.x)).add(a3.multiply(u3.x)).add(a4.multiply(u4.x));
        this.y = a1.multiply(u1.y).add(a2.multiply(u2.y)).add(a3.multiply(u3.y)).add(a4.multiply(u4.y));
        this.z = a1.multiply(u1.z).add(a2.multiply(u2.z)).add(a3.multiply(u3.z)).add(a4.multiply(u4.z));
    }

    /** Linear constructor
     * Build a vector from four other ones and corresponding scale factors.
     * The vector built will be a1 * u1 + a2 * u2 + a3 * u3 + a4 * u4
     * @param a1 first scale factor
     * @param u1 first base (unscaled) vector
     * @param a2 second scale factor
     * @param u2 second base (unscaled) vector
     * @param a3 third scale factor
     * @param u3 third base (unscaled) vector
     * @param a4 fourth scale factor
     * @param u4 fourth base (unscaled) vector
     */
    public Vector3DDS(final DerivativeStructure a1, final Vector3D u1,
                      final DerivativeStructure a2, final Vector3D u2,
                      final DerivativeStructure a3, final Vector3D u3,
                      final DerivativeStructure a4, final Vector3D u4) {
        this.x = a1.multiply(u1.getX()).add(a2.multiply(u2.getX())).add(a3.multiply(u3.getX())).add(a4.multiply(u4.getX()));
        this.y = a1.multiply(u1.getY()).add(a2.multiply(u2.getY())).add(a3.multiply(u3.getY())).add(a4.multiply(u4.getY()));
        this.z = a1.multiply(u1.getZ()).add(a2.multiply(u2.getZ())).add(a3.multiply(u3.getZ())).add(a4.multiply(u4.getZ()));
    }

    /** Linear constructor
     * Build a vector from four other ones and corresponding scale factors.
     * The vector built will be a1 * u1 + a2 * u2 + a3 * u3 + a4 * u4
     * @param a1 first scale factor
     * @param u1 first base (unscaled) vector
     * @param a2 second scale factor
     * @param u2 second base (unscaled) vector
     * @param a3 third scale factor
     * @param u3 third base (unscaled) vector
     * @param a4 fourth scale factor
     * @param u4 fourth base (unscaled) vector
     */
    public Vector3DDS(final double a1, final Vector3DDS u1,
                      final double a2, final Vector3DDS u2,
                      final double a3, final Vector3DDS u3,
                      final double a4, final Vector3DDS u4) {
        this.x = u1.x.multiply(a1).add(u2.x.multiply(a2)).add(u3.x.multiply(a3)).add(u4.x.multiply(a4));
        this.y = u1.y.multiply(a1).add(u2.y.multiply(a2)).add(u3.y.multiply(a3)).add(u4.y.multiply(a4));
        this.z = u1.z.multiply(a1).add(u2.z.multiply(a2)).add(u3.z.multiply(a3)).add(u4.z.multiply(a4));
    }

    /** Get the abscissa of the vector.
     * @return abscissa of the vector
     * @see #Vector3D(DerivativeStructure, DerivativeStructure, DerivativeStructure)
     */
    public DerivativeStructure getX() {
        return x;
    }

    /** Get the ordinate of the vector.
     * @return ordinate of the vector
     * @see #Vector3D(DerivativeStructure, DerivativeStructure, DerivativeStructure)
     */
    public DerivativeStructure getY() {
        return y;
    }

    /** Get the height of the vector.
     * @return height of the vector
     * @see #Vector3D(DerivativeStructure, DerivativeStructure, DerivativeStructure)
     */
    public DerivativeStructure getZ() {
        return z;
    }

    /** Get the vector coordinates as a dimension 3 array.
     * @return vector coordinates
     * @see #Vector3D(DerivativeStructure[])
     */
    public DerivativeStructure[] toArray() {
        return new DerivativeStructure[] { x, y, z };
    }

    /** Convert to a constant vector without derivatives.
     * @return a constant vector
     */
    public Vector3D toVector3D() {
        return new Vector3D(x.getValue(), y.getValue(), z.getValue());
    }

    /** Get the L<sub>1</sub> norm for the vector.
     * @return L<sub>1</sub> norm for the vector
     */
    public DerivativeStructure getNorm1() {
        return x.abs().add(y.abs()).add(z.abs());
    }

    /** Get the L<sub>2</sub> norm for the vector.
     * @return Euclidean norm for the vector
     */
    public DerivativeStructure getNorm() {
        // there are no cancellation problems here, so we use the straightforward formula
        return x.multiply(x).add(y.multiply(y)).add(z.multiply(z)).sqrt();
    }

    /** Get the square of the norm for the vector.
     * @return square of the Euclidean norm for the vector
     */
    public DerivativeStructure getNormSq() {
        // there are no cancellation problems here, so we use the straightforward formula
        return x.multiply(x).add(y.multiply(y)).add(z.multiply(z));
    }

    /** Get the L<sub>&infin;</sub> norm for the vector.
     * @return L<sub>&infin;</sub> norm for the vector
     */
    public DerivativeStructure getNormInf() {
        final DerivativeStructure xAbs = x.abs();
        final DerivativeStructure yAbs = y.abs();
        final DerivativeStructure zAbs = z.abs();
        if (xAbs.getValue() <= yAbs.getValue()) {
            if (yAbs.getValue() <= zAbs.getValue()) {
                return zAbs;
            } else {
                return yAbs;
            }
        } else {
            if (xAbs.getValue() <= zAbs.getValue()) {
                return zAbs;
            } else {
                return xAbs;
            }
        }
    }

    /** Get the azimuth of the vector.
     * @return azimuth (&alpha;) of the vector, between -&pi; and +&pi;
     * @see #Vector3D(DerivativeStructure, DerivativeStructure)
     */
    public DerivativeStructure getAlpha() {
        return DerivativeStructure.atan2(y, x);
    }

    /** Get the elevation of the vector.
     * @return elevation (&delta;) of the vector, between -&pi;/2 and +&pi;/2
     * @see #Vector3D(DerivativeStructure, DerivativeStructure)
     */
    public DerivativeStructure getDelta() {
        return z.divide(getNorm()).asin();
    }

    /** Add a vector to the instance.
     * @param v vector to add
     * @return a new vector
     */
    public Vector3DDS add(final Vector3DDS v) {
        return new Vector3DDS(x.add(v.x), y.add(v.y), z.add(v.z));
    }

    /** Add a vector to the instance.
     * @param v vector to add
     * @return a new vector
     */
    public Vector3DDS add(final Vector3D v) {
        return new Vector3DDS(x.add(v.getX()), y.add(v.getY()), z.add(v.getZ()));
    }

    /** Add a scaled vector to the instance.
     * @param factor scale factor to apply to v before adding it
     * @param v vector to add
     * @return a new vector
     */
    public Vector3DDS add(final DerivativeStructure factor, final Vector3DDS v) {
        return new Vector3DDS(x.add(factor.multiply(v.x)),
                              y.add(factor.multiply(v.y)),
                              z.add(factor.multiply(v.z)));
    }

    /** Add a scaled vector to the instance.
     * @param factor scale factor to apply to v before adding it
     * @param v vector to add
     * @return a new vector
     */
    public Vector3DDS add(final DerivativeStructure factor, final Vector3D v) {
        return new Vector3DDS(x.add(factor.multiply(v.getX())),
                              y.add(factor.multiply(v.getY())),
                              z.add(factor.multiply(v.getZ())));
    }

    /** Add a scaled vector to the instance.
     * @param factor scale factor to apply to v before adding it
     * @param v vector to add
     * @return a new vector
     */
    public Vector3DDS add(final double factor, final Vector3DDS v) {
        return new Vector3DDS(x.add(v.x.multiply(factor)),
                              y.add(v.y.multiply(factor)),
                              z.add(v.z.multiply(factor)));
    }

    /** Add a scaled vector to the instance.
     * @param factor scale factor to apply to v before adding it
     * @param v vector to add
     * @return a new vector
     */
    public Vector3DDS add(final double factor, final Vector3D v) {
        return new Vector3DDS(x.add(factor * v.getX()),
                              y.add(factor * v.getY()),
                              z.add(factor * v.getZ()));
    }

    /** Subtract a vector from the instance.
     * @param v vector to subtract
     * @return a new vector
     */
    public Vector3DDS subtract(final Vector3DDS v) {
        return new Vector3DDS(x.subtract(v.x), y.subtract(v.y), z.subtract(v.z));
    }

    /** Subtract a vector from the instance.
     * @param v vector to subtract
     * @return a new vector
     */
    public Vector3DDS subtract(final Vector3D v) {
        return new Vector3DDS(x.subtract(v.getX()), y.subtract(v.getY()), z.subtract(v.getZ()));
    }

    /** Subtract a scaled vector from the instance.
     * @param factor scale factor to apply to v before subtracting it
     * @param v vector to subtract
     * @return a new vector
     */
    public Vector3DDS subtract(final DerivativeStructure factor, final Vector3DDS v) {
        return new Vector3DDS(x.subtract(factor.multiply(v.x)),
                              y.subtract(factor.multiply(v.y)),
                              z.subtract(factor.multiply(v.z)));
    }

    /** Subtract a scaled vector from the instance.
     * @param factor scale factor to apply to v before subtracting it
     * @param v vector to subtract
     * @return a new vector
     */
    public Vector3DDS subtract(final DerivativeStructure factor, final Vector3D v) {
        return new Vector3DDS(x.subtract(factor.multiply(v.getX())),
                              y.subtract(factor.multiply(v.getY())),
                              z.subtract(factor.multiply(v.getZ())));
    }

    /** Subtract a scaled vector from the instance.
     * @param factor scale factor to apply to v before subtracting it
     * @param v vector to subtract
     * @return a new vector
     */
    public Vector3DDS subtract(final double factor, final Vector3DDS v) {
        return new Vector3DDS(x.subtract(v.x.multiply(factor)),
                              y.subtract(v.y.multiply(factor)),
                              z.subtract(v.z.multiply(factor)));
    }

    /** Subtract a scaled vector from the instance.
     * @param factor scale factor to apply to v before subtracting it
     * @param v vector to subtract
     * @return a new vector
     */
    public Vector3DDS subtract(final double factor, final Vector3D v) {
        return new Vector3DDS(x.subtract(factor * v.getX()),
                              y.subtract(factor * v.getY()),
                              z.subtract(factor * v.getZ()));
    }

    /** Get a normalized vector aligned with the instance.
     * @return a new normalized vector
     * @exception MathArithmeticException if the norm is zero
     */
    public Vector3DDS normalize() throws MathArithmeticException {
        final DerivativeStructure s = getNorm();
        if (s.getValue() == 0) {
            throw new MathArithmeticException(LocalizedFormats.CANNOT_NORMALIZE_A_ZERO_NORM_VECTOR);
        }
        return scalarMultiply(s.reciprocal());
    }

    /** Get a vector orthogonal to the instance.
     * <p>There are an infinite number of normalized vectors orthogonal
     * to the instance. This method picks up one of them almost
     * arbitrarily. It is useful when one needs to compute a reference
     * frame with one of the axes in a predefined direction. The
     * following example shows how to build a frame having the k axis
     * aligned with the known vector u :
     * <pre><code>
     *   Vector3D k = u.normalize();
     *   Vector3D i = k.orthogonal();
     *   Vector3D j = Vector3D.crossProduct(k, i);
     * </code></pre></p>
     * @return a new normalized vector orthogonal to the instance
     * @exception MathArithmeticException if the norm of the instance is null
     */
    public Vector3DDS orthogonal() throws MathArithmeticException {

        final double threshold = 0.6 * getNorm().getValue();
        if (threshold == 0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM);
        }

        if (FastMath.abs(x.getValue()) <= threshold) {
            final DerivativeStructure inverse  = y.multiply(y).add(z.multiply(z)).sqrt().reciprocal();
            return new Vector3DDS(inverse.getField().getZero(), inverse.multiply(z), inverse.multiply(y).negate());
        } else if (FastMath.abs(y.getValue()) <= threshold) {
            final DerivativeStructure inverse  = x.multiply(x).add(z.multiply(z)).sqrt().reciprocal();
            return new Vector3DDS(inverse.multiply(z).negate(), inverse.getField().getZero(), inverse.multiply(x));
        } else {
            final DerivativeStructure inverse  = x.multiply(x).add(y.multiply(y)).sqrt().reciprocal();
            return new Vector3DDS(inverse.multiply(y), inverse.multiply(x).negate(), inverse.getField().getZero());
        }

    }

    /** Compute the angular separation between two vectors.
     * <p>This method computes the angular separation between two
     * vectors using the dot product for well separated vectors and the
     * cross product for almost aligned vectors. This allows to have a
     * good accuracy in all cases, even for vectors very close to each
     * other.</p>
     * @param v1 first vector
     * @param v2 second vector
     * @return angular separation between v1 and v2
     * @exception MathArithmeticException if either vector has a null norm
     */
    public static DerivativeStructure angle(Vector3DDS v1, Vector3DDS v2) throws MathArithmeticException {

        final DerivativeStructure normProduct = v1.getNorm().multiply(v2.getNorm());
        if (normProduct.getValue() == 0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM);
        }

        final DerivativeStructure dot = v1.dotProduct(v2);
        final double threshold = normProduct.getValue() * 0.9999;
        if ((dot.getValue() < -threshold) || (dot.getValue() > threshold)) {
            // the vectors are almost aligned, compute using the sine
            Vector3DDS v3 = crossProduct(v1, v2);
            if (dot.getValue() >= 0) {
                return v3.getNorm().divide(normProduct).asin();
            }
            return v3.getNorm().divide(normProduct).asin().subtract(FastMath.PI).negate();
        }

        // the vectors are sufficiently separated to use the cosine
        return dot.divide(normProduct).acos();

    }

    /** Get the opposite of the instance.
     * @return a new vector which is opposite to the instance
     */
    public Vector3DDS negate() {
        return new Vector3DDS(x.negate(), y.negate(), z.negate());
    }

    /** Multiply the instance by a scalar.
     * @param a scalar
     * @return a new vector
     */
    public Vector3DDS scalarMultiply(final DerivativeStructure a) {
        return new Vector3DDS(x.multiply(a), y.multiply(a), z.multiply(a));
    }

    /** Multiply the instance by a scalar.
     * @param a scalar
     * @return a new vector
     */
    public Vector3DDS scalarMultiply(final double a) {
        return new Vector3DDS(x.multiply(a), y.multiply(a), z.multiply(a));
    }

    /**
     * Returns true if any coordinate of this vector is NaN; false otherwise
     * @return  true if any coordinate of this vector is NaN; false otherwise
     */
    public boolean isNaN() {
        return Double.isNaN(x.getValue()) || Double.isNaN(y.getValue()) || Double.isNaN(z.getValue());
    }

    /**
     * Returns true if any coordinate of this vector is infinite and none are NaN;
     * false otherwise
     * @return  true if any coordinate of this vector is infinite and none are NaN;
     * false otherwise
     */
    public boolean isInfinite() {
        return !isNaN() && (Double.isInfinite(x.getValue()) || Double.isInfinite(y.getValue()) || Double.isInfinite(z.getValue()));
    }

    /**
     * Test for the equality of two 3D vectors.
     * <p>
     * If all coordinates of two 3D vectors are exactly the same, and none are
     * <code>DerivativeStructure.NaN</code>, the two 3D vectors are considered to be equal.
     * </p>
     * <p>
     * <code>NaN</code> coordinates are considered to affect globally the vector
     * and be equals to each other - i.e, if either (or all) coordinates of the
     * 3D vector are equal to <code>DerivativeStructure.NaN</code>, the 3D vector is equal to
     * {@link #NaN}.
     * </p>
     *
     * @param other Object to test for equality to this
     * @return true if two 3D vector objects are equal, false if
     *         object is null, not an instance of Vector3D, or
     *         not equal to this Vector3D instance
     *
     */
    @Override
    public boolean equals(Object other) {

        if (this == other) {
            return true;
        }

        if (other instanceof Vector3DDS) {
            final Vector3DDS rhs = (Vector3DDS)other;
            if (rhs.isNaN()) {
                return this.isNaN();
            }

            return MathArrays.equals(x.getAllDerivatives(), rhs.x.getAllDerivatives()) &&
                   MathArrays.equals(y.getAllDerivatives(), rhs.y.getAllDerivatives()) &&
                   MathArrays.equals(z.getAllDerivatives(), rhs.z.getAllDerivatives());

        }
        return false;
    }

    /**
     * Get a hashCode for the 3D vector.
     * <p>
     * All NaN values have the same hash code.</p>
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        if (isNaN()) {
            return 409;
        }
        return 311 * (107 * x.hashCode() + 83 * y.hashCode() +  z.hashCode());
    }

    /** Compute the dot-product of the instance and another vector.
     * <p>
     * The implementation uses specific multiplication and addition
     * algorithms to preserve accuracy and reduce cancellation effects.
     * It should be very accurate even for nearly orthogonal vectors.
     * </p>
     * @see MathArrays#linearCombination(double, double, double, double, double, double)
     * @param v second vector
     * @return the dot product this.v
     */
    public DerivativeStructure dotProduct(final Vector3DDS v) {
        return MathArrays.linearCombination(x, v.x, y, v.y, z, v.z);
    }

    /** Compute the dot-product of the instance and another vector.
     * <p>
     * The implementation uses specific multiplication and addition
     * algorithms to preserve accuracy and reduce cancellation effects.
     * It should be very accurate even for nearly orthogonal vectors.
     * </p>
     * @see MathArrays#linearCombination(double, double, double, double, double, double)
     * @param v second vector
     * @return the dot product this.v
     */
    public DerivativeStructure dotProduct(final Vector3D v) {
        return MathArrays.linearCombination(v.getX(), x, v.getY(), y, v.getZ(), z);
    }

    /** Compute the cross-product of the instance with another vector.
     * @param v other vector
     * @return the cross product this ^ v as a new Vector3D
     */
    public Vector3DDS crossProduct(final Vector3DDS v) {
        return new Vector3DDS(MathArrays.linearCombination(y, v.z, z.negate(), v.y),
                              MathArrays.linearCombination(z, v.x, x.negate(), v.z),
                              MathArrays.linearCombination(x, v.y, y.negate(), v.x));
    }

    /** Compute the cross-product of the instance with another vector.
     * @param v other vector
     * @return the cross product this ^ v as a new Vector3D
     */
    public Vector3DDS crossProduct(final Vector3D v) {
        return new Vector3DDS(MathArrays.linearCombination(v.getZ(), y, v.getY(), z.negate()),
                              MathArrays.linearCombination(v.getX(), z, v.getZ(), x.negate()),
                              MathArrays.linearCombination(v.getY(), x, v.getX(), y.negate()));
    }

    /** Compute the distance between the instance and another vector according to the L<sub>1</sub> norm.
     * <p>Calling this method is equivalent to calling:
     * <code>q.subtract(p).getNorm1()</code> except that no intermediate
     * vector is built</p>
     * @param v second vector
     * @return the distance between the instance and p according to the L<sub>1</sub> norm
     */
    public DerivativeStructure distance1(final Vector3DDS v) {
        final DerivativeStructure dx = v.x.subtract(x).abs();
        final DerivativeStructure dy = v.y.subtract(y).abs();
        final DerivativeStructure dz = v.z.subtract(z).abs();
        return dx.add(dy).add(dz);
    }

    /** Compute the distance between the instance and another vector according to the L<sub>1</sub> norm.
     * <p>Calling this method is equivalent to calling:
     * <code>q.subtract(p).getNorm1()</code> except that no intermediate
     * vector is built</p>
     * @param v second vector
     * @return the distance between the instance and p according to the L<sub>1</sub> norm
     */
    public DerivativeStructure distance1(final Vector3D v) {
        final DerivativeStructure dx = x.subtract(v.getX()).abs();
        final DerivativeStructure dy = y.subtract(v.getY()).abs();
        final DerivativeStructure dz = z.subtract(v.getZ()).abs();
        return dx.add(dy).add(dz);
    }

    /** Compute the distance between the instance and another vector according to the L<sub>2</sub> norm.
     * <p>Calling this method is equivalent to calling:
     * <code>q.subtract(p).getNorm()</code> except that no intermediate
     * vector is built</p>
     * @param v second vector
     * @return the distance between the instance and p according to the L<sub>2</sub> norm
     */
    public DerivativeStructure distance(final Vector3DDS v) {
        final DerivativeStructure dx = v.x.subtract(x);
        final DerivativeStructure dy = v.y.subtract(y);
        final DerivativeStructure dz = v.z.subtract(z);
        return dx.multiply(dx).add(dy.multiply(dy)).add(dz.multiply(dz)).sqrt();
    }

    /** Compute the distance between the instance and another vector according to the L<sub>2</sub> norm.
     * <p>Calling this method is equivalent to calling:
     * <code>q.subtract(p).getNorm()</code> except that no intermediate
     * vector is built</p>
     * @param v second vector
     * @return the distance between the instance and p according to the L<sub>2</sub> norm
     */
    public DerivativeStructure distance(final Vector3D v) {
        final DerivativeStructure dx = x.subtract(v.getX());
        final DerivativeStructure dy = y.subtract(v.getY());
        final DerivativeStructure dz = z.subtract(v.getZ());
        return dx.multiply(dx).add(dy.multiply(dy)).add(dz.multiply(dz)).sqrt();
    }

    /** Compute the distance between the instance and another vector according to the L<sub>&infin;</sub> norm.
     * <p>Calling this method is equivalent to calling:
     * <code>q.subtract(p).getNormInf()</code> except that no intermediate
     * vector is built</p>
     * @param v second vector
     * @return the distance between the instance and p according to the L<sub>&infin;</sub> norm
     */
    public DerivativeStructure distanceInf(final Vector3DDS v) {
        final DerivativeStructure dx = v.x.subtract(x).abs();
        final DerivativeStructure dy = v.y.subtract(y).abs();
        final DerivativeStructure dz = v.z.subtract(z).abs();
        if (dx.getValue() <= dy.getValue()) {
            if (dy.getValue() <= dz.getValue()) {
                return dz;
            } else {
                return dy;
            }
        } else {
            if (dx.getValue() <= dz.getValue()) {
                return dz;
            } else {
                return dx;
            }
        }
    }

    /** Compute the distance between the instance and another vector according to the L<sub>&infin;</sub> norm.
     * <p>Calling this method is equivalent to calling:
     * <code>q.subtract(p).getNormInf()</code> except that no intermediate
     * vector is built</p>
     * @param v second vector
     * @return the distance between the instance and p according to the L<sub>&infin;</sub> norm
     */
    public DerivativeStructure distanceInf(final Vector3D v) {
        final DerivativeStructure dx = x.subtract(v.getX()).abs();
        final DerivativeStructure dy = y.subtract(v.getY()).abs();
        final DerivativeStructure dz = z.subtract(v.getZ()).abs();
        if (dx.getValue() <= dy.getValue()) {
            if (dy.getValue() <= dz.getValue()) {
                return dz;
            } else {
                return dy;
            }
        } else {
            if (dx.getValue() <= dz.getValue()) {
                return dz;
            } else {
                return dx;
            }
        }
    }

    /** Compute the square of the distance between the instance and another vector.
     * <p>Calling this method is equivalent to calling:
     * <code>q.subtract(p).getNormSq()</code> except that no intermediate
     * vector is built</p>
     * @param v second vector
     * @return the square of the distance between the instance and p
     */
    public DerivativeStructure distanceSq(final Vector3DDS v) {
        final DerivativeStructure dx = v.x.subtract(x);
        final DerivativeStructure dy = v.y.subtract(y);
        final DerivativeStructure dz = v.z.subtract(z);
        return dx.multiply(dx).add(dy.multiply(dy)).add(dz.multiply(dz));
    }

    /** Compute the square of the distance between the instance and another vector.
     * <p>Calling this method is equivalent to calling:
     * <code>q.subtract(p).getNormSq()</code> except that no intermediate
     * vector is built</p>
     * @param v second vector
     * @return the square of the distance between the instance and p
     */
    public DerivativeStructure distanceSq(final Vector3D v) {
        final DerivativeStructure dx = x.subtract(v.getX());
        final DerivativeStructure dy = y.subtract(v.getY());
        final DerivativeStructure dz = z.subtract(v.getZ());
        return dx.multiply(dx).add(dy.multiply(dy)).add(dz.multiply(dz));
    }

    /** Compute the dot-product of two vectors.
     * @param v1 first vector
     * @param v2 second vector
     * @return the dot product v1.v2
     */
    public static DerivativeStructure dotProduct(Vector3DDS v1, Vector3DDS v2) {
        return v1.dotProduct(v2);
    }

    /** Compute the dot-product of two vectors.
     * @param v1 first vector
     * @param v2 second vector
     * @return the dot product v1.v2
     */
    public static DerivativeStructure dotProduct(Vector3DDS v1, Vector3D v2) {
        return v1.dotProduct(v2);
    }

    /** Compute the dot-product of two vectors.
     * @param v1 first vector
     * @param v2 second vector
     * @return the dot product v1.v2
     */
    public static DerivativeStructure dotProduct(Vector3D v1, Vector3DDS v2) {
        return v2.dotProduct(v1);
    }

    /** Compute the cross-product of two vectors.
     * @param v1 first vector
     * @param v2 second vector
     * @return the cross product v1 ^ v2 as a new Vector
     */
    public static Vector3DDS crossProduct(final Vector3DDS v1, final Vector3DDS v2) {
        return v1.crossProduct(v2);
    }

    /** Compute the cross-product of two vectors.
     * @param v1 first vector
     * @param v2 second vector
     * @return the cross product v1 ^ v2 as a new Vector
     */
    public static Vector3DDS crossProduct(final Vector3DDS v1, final Vector3D v2) {
        return v1.crossProduct(v2);
    }

    /** Compute the cross-product of two vectors.
     * @param v1 first vector
     * @param v2 second vector
     * @return the cross product v1 ^ v2 as a new Vector
     */
    public static Vector3DDS crossProduct(final Vector3D v1, final Vector3DDS v2) {
        return v2.crossProduct(v1).negate();
    }

    /** Compute the distance between two vectors according to the L<sub>1</sub> norm.
     * <p>Calling this method is equivalent to calling:
     * <code>v1.subtract(v2).getNorm1()</code> except that no intermediate
     * vector is built</p>
     * @param v1 first vector
     * @param v2 second vector
     * @return the distance between v1 and v2 according to the L<sub>1</sub> norm
     */
    public static DerivativeStructure distance1(Vector3DDS v1, Vector3DDS v2) {
        return v1.distance1(v2);
    }

    /** Compute the distance between two vectors according to the L<sub>1</sub> norm.
     * <p>Calling this method is equivalent to calling:
     * <code>v1.subtract(v2).getNorm1()</code> except that no intermediate
     * vector is built</p>
     * @param v1 first vector
     * @param v2 second vector
     * @return the distance between v1 and v2 according to the L<sub>1</sub> norm
     */
    public static DerivativeStructure distance1(Vector3DDS v1, Vector3D v2) {
        return v1.distance1(v2);
    }

    /** Compute the distance between two vectors according to the L<sub>1</sub> norm.
     * <p>Calling this method is equivalent to calling:
     * <code>v1.subtract(v2).getNorm1()</code> except that no intermediate
     * vector is built</p>
     * @param v1 first vector
     * @param v2 second vector
     * @return the distance between v1 and v2 according to the L<sub>1</sub> norm
     */
    public static DerivativeStructure distance1(Vector3D v1, Vector3DDS v2) {
        return v2.distance1(v1);
    }

    /** Compute the distance between two vectors according to the L<sub>2</sub> norm.
     * <p>Calling this method is equivalent to calling:
     * <code>v1.subtract(v2).getNorm()</code> except that no intermediate
     * vector is built</p>
     * @param v1 first vector
     * @param v2 second vector
     * @return the distance between v1 and v2 according to the L<sub>2</sub> norm
     */
    public static DerivativeStructure distance(Vector3DDS v1, Vector3DDS v2) {
        return v1.distance(v2);
    }

    /** Compute the distance between two vectors according to the L<sub>2</sub> norm.
     * <p>Calling this method is equivalent to calling:
     * <code>v1.subtract(v2).getNorm()</code> except that no intermediate
     * vector is built</p>
     * @param v1 first vector
     * @param v2 second vector
     * @return the distance between v1 and v2 according to the L<sub>2</sub> norm
     */
    public static DerivativeStructure distance(Vector3DDS v1, Vector3D v2) {
        return v1.distance(v2);
    }

    /** Compute the distance between two vectors according to the L<sub>2</sub> norm.
     * <p>Calling this method is equivalent to calling:
     * <code>v1.subtract(v2).getNorm()</code> except that no intermediate
     * vector is built</p>
     * @param v1 first vector
     * @param v2 second vector
     * @return the distance between v1 and v2 according to the L<sub>2</sub> norm
     */
    public static DerivativeStructure distance(Vector3D v1, Vector3DDS v2) {
        return v2.distance(v1);
    }

    /** Compute the distance between two vectors according to the L<sub>&infin;</sub> norm.
     * <p>Calling this method is equivalent to calling:
     * <code>v1.subtract(v2).getNormInf()</code> except that no intermediate
     * vector is built</p>
     * @param v1 first vector
     * @param v2 second vector
     * @return the distance between v1 and v2 according to the L<sub>&infin;</sub> norm
     */
    public static DerivativeStructure distanceInf(Vector3DDS v1, Vector3DDS v2) {
        return v1.distanceInf(v2);
    }

    /** Compute the distance between two vectors according to the L<sub>&infin;</sub> norm.
     * <p>Calling this method is equivalent to calling:
     * <code>v1.subtract(v2).getNormInf()</code> except that no intermediate
     * vector is built</p>
     * @param v1 first vector
     * @param v2 second vector
     * @return the distance between v1 and v2 according to the L<sub>&infin;</sub> norm
     */
    public static DerivativeStructure distanceInf(Vector3DDS v1, Vector3D v2) {
        return v1.distanceInf(v2);
    }

    /** Compute the distance between two vectors according to the L<sub>&infin;</sub> norm.
     * <p>Calling this method is equivalent to calling:
     * <code>v1.subtract(v2).getNormInf()</code> except that no intermediate
     * vector is built</p>
     * @param v1 first vector
     * @param v2 second vector
     * @return the distance between v1 and v2 according to the L<sub>&infin;</sub> norm
     */
    public static DerivativeStructure distanceInf(Vector3D v1, Vector3DDS v2) {
        return v2.distanceInf(v1);
    }

    /** Compute the square of the distance between two vectors.
     * <p>Calling this method is equivalent to calling:
     * <code>v1.subtract(v2).getNormSq()</code> except that no intermediate
     * vector is built</p>
     * @param v1 first vector
     * @param v2 second vector
     * @return the square of the distance between v1 and v2
     */
    public static DerivativeStructure distanceSq(Vector3DDS v1, Vector3DDS v2) {
        return v1.distanceSq(v2);
    }

    /** Compute the square of the distance between two vectors.
     * <p>Calling this method is equivalent to calling:
     * <code>v1.subtract(v2).getNormSq()</code> except that no intermediate
     * vector is built</p>
     * @param v1 first vector
     * @param v2 second vector
     * @return the square of the distance between v1 and v2
     */
    public static DerivativeStructure distanceSq(Vector3DDS v1, Vector3D v2) {
        return v1.distanceSq(v2);
    }

    /** Compute the square of the distance between two vectors.
     * <p>Calling this method is equivalent to calling:
     * <code>v1.subtract(v2).getNormSq()</code> except that no intermediate
     * vector is built</p>
     * @param v1 first vector
     * @param v2 second vector
     * @return the square of the distance between v1 and v2
     */
    public static DerivativeStructure distanceSq(Vector3D v1, Vector3DDS v2) {
        return v2.distanceSq(v1);
    }

    /** Get a string representation of this vector.
     * @return a string representation of this vector
     */
    @Override
    public String toString() {
        return Vector3DFormat.getInstance().format(toVector3D());
    }

    /** {@inheritDoc} */
    public String toString(final NumberFormat format) {
        return new Vector3DFormat(format).format(toVector3D());
    }

}
