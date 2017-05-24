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

package org.apache.commons.math4.geometry.euclidean.threed;

import java.io.Serializable;
import java.text.NumberFormat;

import org.apache.commons.numbers.arrays.LinearCombination;
import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.MathArithmeticException;
import org.apache.commons.math4.exception.util.LocalizedFormats;
import org.apache.commons.math4.geometry.Point;
import org.apache.commons.math4.geometry.Space;
import org.apache.commons.math4.geometry.Vector;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.math4.util.MathUtils;

/**
 * This class represents points or vectors in a three-dimensional space.
 * <p>An instance of Cartesian3D represents the point with the corresponding
 * coordinates.</p>
 * <p>An instance of Cartesian3D also represents the vector which begins at
 * the origin and ends at the point corresponding to the coordinates.</p>
 * <p>Instance of this class are guaranteed to be immutable.</p>
 * @since 4.0
 */
public class Cartesian3D extends Vector3D implements Serializable, Point<Euclidean3D> {

    /** Null vector (coordinates: 0, 0, 0). */
    public static final Cartesian3D ZERO   = new Cartesian3D(0, 0, 0);

    /** First canonical vector (coordinates: 1, 0, 0). */
    public static final Cartesian3D PLUS_I = new Cartesian3D(1, 0, 0);

    /** Opposite of the first canonical vector (coordinates: -1, 0, 0). */
    public static final Cartesian3D MINUS_I = new Cartesian3D(-1, 0, 0);

    /** Second canonical vector (coordinates: 0, 1, 0). */
    public static final Cartesian3D PLUS_J = new Cartesian3D(0, 1, 0);

    /** Opposite of the second canonical vector (coordinates: 0, -1, 0). */
    public static final Cartesian3D MINUS_J = new Cartesian3D(0, -1, 0);

    /** Third canonical vector (coordinates: 0, 0, 1). */
    public static final Cartesian3D PLUS_K = new Cartesian3D(0, 0, 1);

    /** Opposite of the third canonical vector (coordinates: 0, 0, -1).  */
    public static final Cartesian3D MINUS_K = new Cartesian3D(0, 0, -1);

    // CHECKSTYLE: stop ConstantName
    /** A vector with all coordinates set to NaN. */
    public static final Cartesian3D NaN = new Cartesian3D(Double.NaN, Double.NaN, Double.NaN);
    // CHECKSTYLE: resume ConstantName

    /** A vector with all coordinates set to positive infinity. */
    public static final Cartesian3D POSITIVE_INFINITY =
        new Cartesian3D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

    /** A vector with all coordinates set to negative infinity. */
    public static final Cartesian3D NEGATIVE_INFINITY =
        new Cartesian3D(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);

    /** Serializable version identifier. */
    private static final long serialVersionUID = 1313493323784566947L;

    /** Abscissa. */
    private final double x;

    /** Ordinate. */
    private final double y;

    /** Height. */
    private final double z;

    /** Simple constructor.
     * Build a vector from its coordinates
     * @param x abscissa
     * @param y ordinate
     * @param z height
     * @see #getX()
     * @see #getY()
     * @see #getZ()
     */
    public Cartesian3D(double x, double y, double z) {
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
    public Cartesian3D(double[] v) throws DimensionMismatchException {
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
    public Cartesian3D(double alpha, double delta) {
        double cosDelta = FastMath.cos(delta);
        this.x = FastMath.cos(alpha) * cosDelta;
        this.y = FastMath.sin(alpha) * cosDelta;
        this.z = FastMath.sin(delta);
    }

    /** Multiplicative constructor
     * Build a vector from another one and a scale factor.
     * The vector built will be a * u
     * @param a scale factor
     * @param u base (unscaled) vector
     */
    public Cartesian3D(double a, Cartesian3D u) {
        this.x = a * u.x;
        this.y = a * u.y;
        this.z = a * u.z;
    }

    /** Linear constructor
     * Build a vector from two other ones and corresponding scale factors.
     * The vector built will be a1 * u1 + a2 * u2
     * @param a1 first scale factor
     * @param u1 first base (unscaled) vector
     * @param a2 second scale factor
     * @param u2 second base (unscaled) vector
     */
    public Cartesian3D(double a1, Cartesian3D u1, double a2, Cartesian3D u2) {
        this.x = LinearCombination.value(a1, u1.x, a2, u2.x);
        this.y = LinearCombination.value(a1, u1.y, a2, u2.y);
        this.z = LinearCombination.value(a1, u1.z, a2, u2.z);
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
    public Cartesian3D(double a1, Cartesian3D u1, double a2, Cartesian3D u2,
                    double a3, Cartesian3D u3) {
        this.x = LinearCombination.value(a1, u1.x, a2, u2.x, a3, u3.x);
        this.y = LinearCombination.value(a1, u1.y, a2, u2.y, a3, u3.y);
        this.z = LinearCombination.value(a1, u1.z, a2, u2.z, a3, u3.z);
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
    public Cartesian3D(double a1, Cartesian3D u1, double a2, Cartesian3D u2,
                    double a3, Cartesian3D u3, double a4, Cartesian3D u4) {
        this.x = LinearCombination.value(a1, u1.x, a2, u2.x, a3, u3.x, a4, u4.x);
        this.y = LinearCombination.value(a1, u1.y, a2, u2.y, a3, u3.y, a4, u4.y);
        this.z = LinearCombination.value(a1, u1.z, a2, u2.z, a3, u3.z, a4, u4.z);
    }

    /** Get the abscissa of the vector.
     * @return abscissa of the vector
     * @see #Cartesian3D(double, double, double)
     */
    public double getX() {
        return x;
    }

    /** Get the ordinate of the vector.
     * @return ordinate of the vector
     * @see #Cartesian3D(double, double, double)
     */
    public double getY() {
        return y;
    }

    /** Get the height of the vector.
     * @return height of the vector
     * @see #Cartesian3D(double, double, double)
     */
    public double getZ() {
        return z;
    }

    /** Get the vector coordinates as a dimension 3 array.
     * @return vector coordinates
     * @see #Cartesian3D(double[])
     */
    public double[] toArray() {
        return new double[] { x, y, z };
    }

    /** {@inheritDoc} */
    @Override
    public Space getSpace() {
        return Euclidean3D.getInstance();
    }

    /** {@inheritDoc} */
    @Override
    public Cartesian3D getZero() {
        return ZERO;
    }

    /** {@inheritDoc} */
    @Override
    public double getNorm1() {
        return FastMath.abs(x) + FastMath.abs(y) + FastMath.abs(z);
    }

    /** {@inheritDoc} */
    @Override
    public double getNorm() {
        // there are no cancellation problems here, so we use the straightforward formula
        return FastMath.sqrt (x * x + y * y + z * z);
    }

    /** {@inheritDoc} */
    @Override
    public double getNormSq() {
        // there are no cancellation problems here, so we use the straightforward formula
        return x * x + y * y + z * z;
    }

    /** {@inheritDoc} */
    @Override
    public double getNormInf() {
        return FastMath.max(FastMath.max(FastMath.abs(x), FastMath.abs(y)), FastMath.abs(z));
    }

    /** Get the azimuth of the vector.
     * @return azimuth (&alpha;) of the vector, between -&pi; and +&pi;
     * @see #Cartesian3D(double, double)
     */
    public double getAlpha() {
        return FastMath.atan2(y, x);
    }

    /** Get the elevation of the vector.
     * @return elevation (&delta;) of the vector, between -&pi;/2 and +&pi;/2
     * @see #Cartesian3D(double, double)
     */
    public double getDelta() {
        return FastMath.asin(z / getNorm());
    }

    /** {@inheritDoc} */
    @Override
    public Cartesian3D add(final Vector<Euclidean3D> v) {
        final Cartesian3D v3 = (Cartesian3D) v;
        return new Cartesian3D(x + v3.x, y + v3.y, z + v3.z);
    }

    /** {@inheritDoc} */
    @Override
    public Cartesian3D add(double factor, final Vector<Euclidean3D> v) {
        return new Cartesian3D(1, this, factor, (Cartesian3D) v);
    }

    /** {@inheritDoc} */
    @Override
    public Cartesian3D subtract(final Vector<Euclidean3D> v) {
        final Cartesian3D v3 = (Cartesian3D) v;
        return new Cartesian3D(x - v3.x, y - v3.y, z - v3.z);
    }

    /** {@inheritDoc} */
    @Override
    public Cartesian3D subtract(final double factor, final Vector<Euclidean3D> v) {
        return new Cartesian3D(1, this, -factor, (Cartesian3D) v);
    }

    /** {@inheritDoc} */
    @Override
    public Cartesian3D normalize() throws MathArithmeticException {
        double s = getNorm();
        if (s == 0) {
            throw new MathArithmeticException(LocalizedFormats.CANNOT_NORMALIZE_A_ZERO_NORM_VECTOR);
        }
        return scalarMultiply(1 / s);
    }

    /** Get a vector orthogonal to the instance.
     * <p>There are an infinite number of normalized vectors orthogonal
     * to the instance. This method picks up one of them almost
     * arbitrarily. It is useful when one needs to compute a reference
     * frame with one of the axes in a predefined direction. The
     * following example shows how to build a frame having the k axis
     * aligned with the known vector u :
     * <pre><code>
     *   Cartesian3D k = u.normalize();
     *   Cartesian3D i = k.orthogonal();
     *   Cartesian3D j = Cartesian3D.crossProduct(k, i);
     * </code></pre>
     * @return a new normalized vector orthogonal to the instance
     * @exception MathArithmeticException if the norm of the instance is null
     */
    public Cartesian3D orthogonal() throws MathArithmeticException {

        double threshold = 0.6 * getNorm();
        if (threshold == 0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM);
        }

        if (FastMath.abs(x) <= threshold) {
            double inverse  = 1 / FastMath.sqrt(y * y + z * z);
            return new Cartesian3D(0, inverse * z, -inverse * y);
        } else if (FastMath.abs(y) <= threshold) {
            double inverse  = 1 / FastMath.sqrt(x * x + z * z);
            return new Cartesian3D(-inverse * z, 0, inverse * x);
        }
        double inverse  = 1 / FastMath.sqrt(x * x + y * y);
        return new Cartesian3D(inverse * y, -inverse * x, 0);

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
    public static double angle(Cartesian3D v1, Cartesian3D v2) throws MathArithmeticException {

        double normProduct = v1.getNorm() * v2.getNorm();
        if (normProduct == 0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM);
        }

        double dot = v1.dotProduct(v2);
        double threshold = normProduct * 0.9999;
        if ((dot < -threshold) || (dot > threshold)) {
            // the vectors are almost aligned, compute using the sine
            Cartesian3D v3 = crossProduct(v1, v2);
            if (dot >= 0) {
                return FastMath.asin(v3.getNorm() / normProduct);
            }
            return FastMath.PI - FastMath.asin(v3.getNorm() / normProduct);
        }

        // the vectors are sufficiently separated to use the cosine
        return FastMath.acos(dot / normProduct);

    }

    /** {@inheritDoc} */
    @Override
    public Cartesian3D negate() {
        return new Cartesian3D(-x, -y, -z);
    }

    /** {@inheritDoc} */
    @Override
    public Cartesian3D scalarMultiply(double a) {
        return new Cartesian3D(a * x, a * y, a * z);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isNaN() {
        return Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isInfinite() {
        return !isNaN() && (Double.isInfinite(x) || Double.isInfinite(y) || Double.isInfinite(z));
    }

    /**
     * Test for the equality of two 3D vectors.
     * <p>
     * If all coordinates of two 3D vectors are exactly the same, and none are
     * <code>Double.NaN</code>, the two 3D vectors are considered to be equal.
     * </p>
     * <p>
     * <code>NaN</code> coordinates are considered to affect globally the vector
     * and be equals to each other - i.e, if either (or all) coordinates of the
     * 3D vector are equal to <code>Double.NaN</code>, the 3D vector is equal to
     * {@link #NaN}.
     * </p>
     *
     * @param other Object to test for equality to this
     * @return true if two 3D vector objects are equal, false if
     *         object is null, not an instance of Cartesian3D, or
     *         not equal to this Cartesian3D instance
     *
     */
    @Override
    public boolean equals(Object other) {

        if (this == other) {
            return true;
        }

        if (other instanceof Cartesian3D) {
            final Cartesian3D rhs = (Cartesian3D)other;
            if (rhs.isNaN()) {
                return this.isNaN();
            }

            return (x == rhs.x) && (y == rhs.y) && (z == rhs.z);
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
            return 642;
        }
        return 643 * (164 * MathUtils.hash(x) +  3 * MathUtils.hash(y) +  MathUtils.hash(z));
    }

    /** {@inheritDoc}
     * <p>
     * The implementation uses specific multiplication and addition
     * algorithms to preserve accuracy and reduce cancellation effects.
     * It should be very accurate even for nearly orthogonal vectors.
     * </p>
     * @see LinearCombination#value(double, double, double, double, double, double)
     */
    @Override
    public double dotProduct(final Vector<Euclidean3D> v) {
        final Cartesian3D v3 = (Cartesian3D) v;
        return LinearCombination.value(x, v3.x, y, v3.y, z, v3.z);
    }

    /** Compute the cross-product of the instance with another vector.
     * @param v other vector
     * @return the cross product this ^ v as a new Cartesian3D
     */
    public Cartesian3D crossProduct(final Vector<Euclidean3D> v) {
        final Cartesian3D v3 = (Cartesian3D) v;
        return new Cartesian3D(LinearCombination.value(y, v3.z, -z, v3.y),
                            LinearCombination.value(z, v3.x, -x, v3.z),
                            LinearCombination.value(x, v3.y, -y, v3.x));
    }

    /** {@inheritDoc} */
    @Override
    public double distance1(Vector<Euclidean3D> v) {
        final Cartesian3D v3 = (Cartesian3D) v;
        final double dx = FastMath.abs(v3.x - x);
        final double dy = FastMath.abs(v3.y - y);
        final double dz = FastMath.abs(v3.z - z);
        return dx + dy + dz;
    }

    /** {@inheritDoc} */
    @Override
    public double distance(Point<Euclidean3D> p) {
        return distance((Cartesian3D) p);
    }

    /** {@inheritDoc} */
    @Override
    public double distance(Vector<Euclidean3D> v) {
        return distance((Cartesian3D) v);
    }

    /** Compute the distance between the instance and other coordinates.
     * @param c other coordinates
     * @return the distance between the instance and c
     */
    public double distance(Cartesian3D c) {
        final double dx = c.x - x;
        final double dy = c.y - y;
        final double dz = c.z - z;
        return FastMath.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /** {@inheritDoc} */
    @Override
    public double distanceInf(Vector<Euclidean3D> v) {
        final Cartesian3D v3 = (Cartesian3D) v;
        final double dx = FastMath.abs(v3.x - x);
        final double dy = FastMath.abs(v3.y - y);
        final double dz = FastMath.abs(v3.z - z);
        return FastMath.max(FastMath.max(dx, dy), dz);
    }

    /** {@inheritDoc} */
    @Override
    public double distanceSq(Vector<Euclidean3D> v) {
        final Cartesian3D v3 = (Cartesian3D) v;
        final double dx = v3.x - x;
        final double dy = v3.y - y;
        final double dz = v3.z - z;
        return dx * dx + dy * dy + dz * dz;
    }

    /** Compute the dot-product of two vectors.
     * @param v1 first vector
     * @param v2 second vector
     * @return the dot product v1.v2
     */
    public static double dotProduct(Cartesian3D v1, Cartesian3D v2) {
        return v1.dotProduct(v2);
    }

    /** Compute the cross-product of two vectors.
     * @param v1 first vector
     * @param v2 second vector
     * @return the cross product v1 ^ v2 as a new Vector
     */
    public static Cartesian3D crossProduct(final Cartesian3D v1, final Cartesian3D v2) {
        return v1.crossProduct(v2);
    }

    /** Compute the distance between two vectors according to the L<sub>1</sub> norm.
     * <p>Calling this method is equivalent to calling:
     * <code>v1.subtract(v2).getNorm1()</code> except that no intermediate
     * vector is built</p>
     * @param v1 first vector
     * @param v2 second vector
     * @return the distance between v1 and v2 according to the L<sub>1</sub> norm
     */
    public static double distance1(Cartesian3D v1, Cartesian3D v2) {
        return v1.distance1(v2);
    }

    /** Compute the distance between two vectors according to the L<sub>2</sub> norm.
     * <p>Calling this method is equivalent to calling:
     * <code>v1.subtract(v2).getNorm()</code> except that no intermediate
     * vector is built</p>
     * @param v1 first vector
     * @param v2 second vector
     * @return the distance between v1 and v2 according to the L<sub>2</sub> norm
     */
    public static double distance(Cartesian3D v1, Cartesian3D v2) {
        return v1.distance(v2);
    }

    /** Compute the distance between two vectors according to the L<sub>&infin;</sub> norm.
     * <p>Calling this method is equivalent to calling:
     * <code>v1.subtract(v2).getNormInf()</code> except that no intermediate
     * vector is built</p>
     * @param v1 first vector
     * @param v2 second vector
     * @return the distance between v1 and v2 according to the L<sub>&infin;</sub> norm
     */
    public static double distanceInf(Cartesian3D v1, Cartesian3D v2) {
        return v1.distanceInf(v2);
    }

    /** Compute the square of the distance between two vectors.
     * <p>Calling this method is equivalent to calling:
     * <code>v1.subtract(v2).getNormSq()</code> except that no intermediate
     * vector is built</p>
     * @param v1 first vector
     * @param v2 second vector
     * @return the square of the distance between v1 and v2
     */
    public static double distanceSq(Cartesian3D v1, Cartesian3D v2) {
        return v1.distanceSq(v2);
    }

    /** Get a string representation of this vector.
     * @return a string representation of this vector
     */
    @Override
    public String toString() {
        return Vector3DFormat.getInstance().format(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString(final NumberFormat format) {
        return new Vector3DFormat(format).format(this);
    }

}
