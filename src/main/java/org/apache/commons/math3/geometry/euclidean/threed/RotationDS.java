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

import org.apache.commons.math3.Field;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;

/**
 * This class is a re-implementation of {@link Rotation} using {@link DerivativeStructure}.
 * <p>Instance of this class are guaranteed to be immutable.</p>
 *
 * @version $Id$
 * @see Vector3DDSDS
 * @see RotationOrder
 * @since 3.2
 */

public class RotationDS implements Serializable {

    /** Serializable version identifier */
    private static final long serialVersionUID = 20130215l;

    /** Scalar coordinate of the quaternion. */
    private final DerivativeStructure q0;

    /** First coordinate of the vectorial part of the quaternion. */
    private final DerivativeStructure q1;

    /** Second coordinate of the vectorial part of the quaternion. */
    private final DerivativeStructure q2;

    /** Third coordinate of the vectorial part of the quaternion. */
    private final DerivativeStructure q3;

    /** Build a rotation from the quaternion coordinates.
     * <p>A rotation can be built from a <em>normalized</em> quaternion,
     * i.e. a quaternion for which q<sub>0</sub><sup>2</sup> +
     * q<sub>1</sub><sup>2</sup> + q<sub>2</sub><sup>2</sup> +
     * q<sub>3</sub><sup>2</sup> = 1. If the quaternion is not normalized,
     * the constructor can normalize it in a preprocessing step.</p>
     * <p>Note that some conventions put the scalar part of the quaternion
     * as the 4<sup>th</sup> component and the vector part as the first three
     * components. This is <em>not</em> our convention. We put the scalar part
     * as the first component.</p>
     * @param q0 scalar part of the quaternion
     * @param q1 first coordinate of the vectorial part of the quaternion
     * @param q2 second coordinate of the vectorial part of the quaternion
     * @param q3 third coordinate of the vectorial part of the quaternion
     * @param needsNormalization if true, the coordinates are considered
     * not to be normalized, a normalization preprocessing step is performed
     * before using them
     */
    public RotationDS(final DerivativeStructure q0, final DerivativeStructure q1,
                      final DerivativeStructure q2, final DerivativeStructure q3,
                      final boolean needsNormalization) {

        if (needsNormalization) {
            // normalization preprocessing
            final DerivativeStructure inv =
                    q0.multiply(q0).add(q1.multiply(q1)).add(q2.multiply(q2)).add(q3.multiply(q3)).sqrt().reciprocal();
            this.q0 = inv.multiply(q0);
            this.q1 = inv.multiply(q1);
            this.q2 = inv.multiply(q2);
            this.q3 = inv.multiply(q3);
        } else {
            this.q0 = q0;
            this.q1 = q1;
            this.q2 = q2;
            this.q3 = q3;
        }

    }

    /** Build a rotation from an axis and an angle.
     * <p>We use the convention that angles are oriented according to
     * the effect of the rotation on vectors around the axis. That means
     * that if (i, j, k) is a direct frame and if we first provide +k as
     * the axis and &pi;/2 as the angle to this constructor, and then
     * {@link #applyTo(Vector3DDS) apply} the instance to +i, we will get
     * +j.</p>
     * <p>Another way to represent our convention is to say that a rotation
     * of angle &theta; about the unit vector (x, y, z) is the same as the
     * rotation build from quaternion components { cos(-&theta;/2),
     * x * sin(-&theta;/2), y * sin(-&theta;/2), z * sin(-&theta;/2) }.
     * Note the minus sign on the angle!</p>
     * <p>On the one hand this convention is consistent with a vectorial
     * perspective (moving vectors in fixed frames), on the other hand it
     * is different from conventions with a frame perspective (fixed vectors
     * viewed from different frames) like the ones used for example in spacecraft
     * attitude community or in the graphics community.</p>
     * @param axis axis around which to rotate
     * @param angle rotation angle.
     * @exception MathIllegalArgumentException if the axis norm is zero
     */
    public RotationDS(final Vector3DDS axis, final DerivativeStructure angle)
        throws MathIllegalArgumentException {

        final DerivativeStructure norm = axis.getNorm();
        if (norm.getValue() == 0) {
            throw new MathIllegalArgumentException(LocalizedFormats.ZERO_NORM_FOR_ROTATION_AXIS);
        }

        final DerivativeStructure halfAngle = angle.multiply(-0.5);
        final DerivativeStructure coeff = halfAngle.sin().divide(norm);

        q0 = halfAngle.cos();
        q1 = coeff.multiply(axis.getX());
        q2 = coeff.multiply(axis.getY());
        q3 = coeff.multiply(axis.getZ());

    }

    /** Build a rotation from a 3X3 matrix.

     * <p>Rotation matrices are orthogonal matrices, i.e. unit matrices
     * (which are matrices for which m.m<sup>T</sup> = I) with real
     * coefficients. The module of the determinant of unit matrices is
     * 1, among the orthogonal 3X3 matrices, only the ones having a
     * positive determinant (+1) are rotation matrices.</p>

     * <p>When a rotation is defined by a matrix with truncated values
     * (typically when it is extracted from a technical sheet where only
     * four to five significant digits are available), the matrix is not
     * orthogonal anymore. This constructor handles this case
     * transparently by using a copy of the given matrix and applying a
     * correction to the copy in order to perfect its orthogonality. If
     * the Frobenius norm of the correction needed is above the given
     * threshold, then the matrix is considered to be too far from a
     * true rotation matrix and an exception is thrown.<p>

     * @param m rotation matrix
     * @param threshold convergence threshold for the iterative
     * orthogonality correction (convergence is reached when the
     * difference between two steps of the Frobenius norm of the
     * correction is below this threshold)

     * @exception NotARotationMatrixException if the matrix is not a 3X3
     * matrix, or if it cannot be transformed into an orthogonal matrix
     * with the given threshold, or if the determinant of the resulting
     * orthogonal matrix is negative

     */
    public RotationDS(final DerivativeStructure[][] m, final double threshold)
        throws NotARotationMatrixException {

        // dimension check
        if ((m.length != 3) || (m[0].length != 3) ||
                (m[1].length != 3) || (m[2].length != 3)) {
            throw new NotARotationMatrixException(
                                                  LocalizedFormats.ROTATION_MATRIX_DIMENSIONS,
                                                  m.length, m[0].length);
        }

        // compute a "close" orthogonal matrix
        final DerivativeStructure[][] ort = orthogonalizeMatrix(m, threshold);

        // check the sign of the determinant
        final DerivativeStructure d0 = ort[1][1].multiply(ort[2][2]).subtract(ort[2][1].multiply(ort[1][2]));
        final DerivativeStructure d1 = ort[0][1].multiply(ort[2][2]).subtract(ort[2][1].multiply(ort[0][2]));
        final DerivativeStructure d2 = ort[0][1].multiply(ort[1][2]).subtract(ort[1][1].multiply(ort[0][2]));
        final DerivativeStructure det =
                ort[0][0].multiply(d0).subtract(ort[1][0].multiply(d1)).add(ort[2][0].multiply(d2));
        if (det.getValue() < 0.0) {
            throw new NotARotationMatrixException(
                                                  LocalizedFormats.CLOSEST_ORTHOGONAL_MATRIX_HAS_NEGATIVE_DETERMINANT,
                                                  det);
        }

        final DerivativeStructure[] quat = mat2quat(ort);
        q0 = quat[0];
        q1 = quat[1];
        q2 = quat[2];
        q3 = quat[3];

    }

    /** Build the rotation that transforms a pair of vector into another pair.

     * <p>Except for possible scale factors, if the instance were applied to
     * the pair (u<sub>1</sub>, u<sub>2</sub>) it will produce the pair
     * (v<sub>1</sub>, v<sub>2</sub>).</p>

     * <p>If the angular separation between u<sub>1</sub> and u<sub>2</sub> is
     * not the same as the angular separation between v<sub>1</sub> and
     * v<sub>2</sub>, then a corrected v'<sub>2</sub> will be used rather than
     * v<sub>2</sub>, the corrected vector will be in the (v<sub>1</sub>,
     * v<sub>2</sub>) plane.</p>

     * @param u1 first vector of the origin pair
     * @param u2 second vector of the origin pair
     * @param v1 desired image of u1 by the rotation
     * @param v2 desired image of u2 by the rotation
     * @exception MathArithmeticException if the norm of one of the vectors is zero,
     * or if one of the pair is degenerated (i.e. the vectors of the pair are colinear)
     */
    public RotationDS(Vector3DDS u1, Vector3DDS u2, Vector3DDS v1, Vector3DDS v2)
        throws MathArithmeticException {

        // build orthonormalized base from u1, u2
        // this fails when vectors are null or colinear, which is forbidden to define a rotation
        final Vector3DDS u3 = u1.crossProduct(u2).normalize();
        u2 = u3.crossProduct(u1).normalize();
        u1 = u1.normalize();

        // build an orthonormalized base from v1, v2
        // this fails when vectors are null or colinear, which is forbidden to define a rotation
        final Vector3DDS v3 = v1.crossProduct(v2).normalize();
        v2 = v3.crossProduct(v1).normalize();
        v1 = v1.normalize();

        // buid a matrix transforming the first base into the second one
        final DerivativeStructure[][] m = new DerivativeStructure[][] {
            {
                MathArrays.linearCombination(u1.getX(), v1.getX(), u2.getX(), v2.getX(), u3.getX(), v3.getX()),
                MathArrays.linearCombination(u1.getY(), v1.getX(), u2.getY(), v2.getX(), u3.getY(), v3.getX()),
                MathArrays.linearCombination(u1.getZ(), v1.getX(), u2.getZ(), v2.getX(), u3.getZ(), v3.getX())
            },
            {
                MathArrays.linearCombination(u1.getX(), v1.getY(), u2.getX(), v2.getY(), u3.getX(), v3.getY()),
                MathArrays.linearCombination(u1.getY(), v1.getY(), u2.getY(), v2.getY(), u3.getY(), v3.getY()),
                MathArrays.linearCombination(u1.getZ(), v1.getY(), u2.getZ(), v2.getY(), u3.getZ(), v3.getY())
            },
            {
                MathArrays.linearCombination(u1.getX(), v1.getZ(), u2.getX(), v2.getZ(), u3.getX(), v3.getZ()),
                MathArrays.linearCombination(u1.getY(), v1.getZ(), u2.getY(), v2.getZ(), u3.getY(), v3.getZ()),
                MathArrays.linearCombination(u1.getZ(), v1.getZ(), u2.getZ(), v2.getZ(), u3.getZ(), v3.getZ())
            }
        };

        DerivativeStructure[] quat = mat2quat(m);
        q0 = quat[0];
        q1 = quat[1];
        q2 = quat[2];
        q3 = quat[3];

    }

    /** Build one of the rotations that transform one vector into another one.

     * <p>Except for a possible scale factor, if the instance were
     * applied to the vector u it will produce the vector v. There is an
     * infinite number of such rotations, this constructor choose the
     * one with the smallest associated angle (i.e. the one whose axis
     * is orthogonal to the (u, v) plane). If u and v are colinear, an
     * arbitrary rotation axis is chosen.</p>

     * @param u origin vector
     * @param v desired image of u by the rotation
     * @exception MathArithmeticException if the norm of one of the vectors is zero
     */
    public RotationDS(final Vector3DDS u, final Vector3DDS v) throws MathArithmeticException {

        final DerivativeStructure normProduct = u.getNorm().multiply(v.getNorm());
        if (normProduct.getValue() == 0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM_FOR_ROTATION_DEFINING_VECTOR);
        }

        final DerivativeStructure dot = u.dotProduct(v);

        if (dot.getValue() < ((2.0e-15 - 1.0) * normProduct.getValue())) {
            // special case u = -v: we select a PI angle rotation around
            // an arbitrary vector orthogonal to u
            final Vector3DDS w = u.orthogonal();
            q0 = normProduct.getField().getZero();
            q1 = w.getX().negate();
            q2 = w.getY().negate();
            q3 = w.getZ().negate();
        } else {
            // general case: (u, v) defines a plane, we select
            // the shortest possible rotation: axis orthogonal to this plane
            q0 = dot.divide(normProduct).add(1.0).multiply(0.5).sqrt();
            final DerivativeStructure coeff = q0.multiply(normProduct).multiply(2.0).reciprocal();
            final Vector3DDS q = v.crossProduct(u);
            q1 = coeff.multiply(q.getX());
            q2 = coeff.multiply(q.getY());
            q3 = coeff.multiply(q.getZ());
        }

    }

    /** Build a rotation from three Cardan or Euler elementary rotations.

     * <p>Cardan rotations are three successive rotations around the
     * canonical axes X, Y and Z, each axis being used once. There are
     * 6 such sets of rotations (XYZ, XZY, YXZ, YZX, ZXY and ZYX). Euler
     * rotations are three successive rotations around the canonical
     * axes X, Y and Z, the first and last rotations being around the
     * same axis. There are 6 such sets of rotations (XYX, XZX, YXY,
     * YZY, ZXZ and ZYZ), the most popular one being ZXZ.</p>
     * <p>Beware that many people routinely use the term Euler angles even
     * for what really are Cardan angles (this confusion is especially
     * widespread in the aerospace business where Roll, Pitch and Yaw angles
     * are often wrongly tagged as Euler angles).</p>

     * @param order order of rotations to use
     * @param alpha1 angle of the first elementary rotation
     * @param alpha2 angle of the second elementary rotation
     * @param alpha3 angle of the third elementary rotation
     */
    public RotationDS(final RotationOrder order, final DerivativeStructure alpha1,
                      final DerivativeStructure alpha2, final DerivativeStructure alpha3) {
        final int p = alpha1.getFreeParameters();
        final int o  = alpha1.getOrder();
        final RotationDS r1 =
                new RotationDS(new Vector3DDS(new DerivativeStructure(p, o, order.getA1().getX()),
                                              new DerivativeStructure(p, o, order.getA1().getY()),
                                              new DerivativeStructure(p, o, order.getA1().getZ())),
                                              alpha1);
        final RotationDS r2 =
                new RotationDS(new Vector3DDS(new DerivativeStructure(p, o, order.getA2().getX()),
                                              new DerivativeStructure(p, o, order.getA2().getY()),
                                              new DerivativeStructure(p, o, order.getA2().getZ())),
                                              alpha2);
        final RotationDS r3 =
                new RotationDS(new Vector3DDS(new DerivativeStructure(p, o, order.getA3().getX()),
                                              new DerivativeStructure(p, o, order.getA3().getY()),
                                              new DerivativeStructure(p, o, order.getA3().getZ())),
                                              alpha3);
        final RotationDS composed = r1.applyTo(r2.applyTo(r3));
        q0 = composed.q0;
        q1 = composed.q1;
        q2 = composed.q2;
        q3 = composed.q3;
    }

    /** Convert an orthogonal rotation matrix to a quaternion.
     * @param ort orthogonal rotation matrix
     * @return quaternion corresponding to the matrix
     */
    private static DerivativeStructure[] mat2quat(final DerivativeStructure[][] ort) {

        final DerivativeStructure[] quat = new DerivativeStructure[4];

        // There are different ways to compute the quaternions elements
        // from the matrix. They all involve computing one element from
        // the diagonal of the matrix, and computing the three other ones
        // using a formula involving a division by the first element,
        // which unfortunately can be zero. Since the norm of the
        // quaternion is 1, we know at least one element has an absolute
        // value greater or equal to 0.5, so it is always possible to
        // select the right formula and avoid division by zero and even
        // numerical inaccuracy. Checking the elements in turn and using
        // the first one greater than 0.45 is safe (this leads to a simple
        // test since qi = 0.45 implies 4 qi^2 - 1 = -0.19)
        DerivativeStructure s = ort[0][0].add(ort[1][1]).add(ort[2][2]);
        if (s.getValue() > -0.19) {
            // compute q0 and deduce q1, q2 and q3
            quat[0] = s.add(1.0).sqrt().multiply(0.5);
            DerivativeStructure inv = quat[0].reciprocal().multiply(0.25);
            quat[1] = inv.multiply(ort[1][2].subtract(ort[2][1]));
            quat[2] = inv.multiply(ort[2][0].subtract(ort[0][2]));
            quat[3] = inv.multiply(ort[0][1].subtract(ort[1][0]));
        } else {
            s = ort[0][0].subtract(ort[1][1]).subtract(ort[2][2]);
            if (s.getValue() > -0.19) {
                // compute q1 and deduce q0, q2 and q3
                quat[1] = s.add(1.0).sqrt().multiply(0.5);
                DerivativeStructure inv = quat[1].reciprocal().multiply(0.25);
                quat[0] = inv.multiply(ort[1][2].subtract(ort[2][1]));
                quat[2] = inv.multiply(ort[0][1].add(ort[1][0]));
                quat[3] = inv.multiply(ort[0][2].add(ort[2][0]));
            } else {
                s = ort[1][1].subtract(ort[0][0]).subtract(ort[2][2]);
                if (s.getValue() > -0.19) {
                    // compute q2 and deduce q0, q1 and q3
                    quat[2] = s.add(1.0).sqrt().multiply(0.5);
                    DerivativeStructure inv = quat[2].reciprocal().multiply(0.25);
                    quat[0] = inv.multiply(ort[2][0].subtract(ort[0][2]));
                    quat[1] = inv.multiply(ort[0][1].add(ort[1][0]));
                    quat[3] = inv.multiply(ort[2][1].add(ort[1][2]));
                } else {
                    // compute q3 and deduce q0, q1 and q2
                    s = ort[2][2].subtract(ort[0][0]).subtract(ort[1][1]);
                    quat[3] = s.add(1.0).sqrt().multiply(0.5);
                    DerivativeStructure inv = quat[3].reciprocal().multiply(0.25);
                    quat[0] = inv.multiply(ort[0][1].subtract(ort[1][0]));
                    quat[1] = inv.multiply(ort[0][2].add(ort[2][0]));
                    quat[2] = inv.multiply(ort[2][1].add(ort[1][2]));
                }
            }
        }

        return quat;

    }

    /** Revert a rotation.
     * Build a rotation which reverse the effect of another
     * rotation. This means that if r(u) = v, then r.revert(v) = u. The
     * instance is not changed.
     * @return a new rotation whose effect is the reverse of the effect
     * of the instance
     */
    public RotationDS revert() {
        return new RotationDS(q0.negate(), q1, q2, q3, false);
    }

    /** Get the scalar coordinate of the quaternion.
     * @return scalar coordinate of the quaternion
     */
    public DerivativeStructure getQ0() {
        return q0;
    }

    /** Get the first coordinate of the vectorial part of the quaternion.
     * @return first coordinate of the vectorial part of the quaternion
     */
    public DerivativeStructure getQ1() {
        return q1;
    }

    /** Get the second coordinate of the vectorial part of the quaternion.
     * @return second coordinate of the vectorial part of the quaternion
     */
    public DerivativeStructure getQ2() {
        return q2;
    }

    /** Get the third coordinate of the vectorial part of the quaternion.
     * @return third coordinate of the vectorial part of the quaternion
     */
    public DerivativeStructure getQ3() {
        return q3;
    }

    /** Get the normalized axis of the rotation.
     * @return normalized axis of the rotation
     * @see #Rotation(Vector3DDS, DerivativeStructure)
     */
    public Vector3DDS getAxis() {
        final DerivativeStructure squaredSine = q1.multiply(q1).add(q2.multiply(q2)).add(q3.multiply(q3));
        if (squaredSine.getValue() == 0) {
            final Field<DerivativeStructure> field = squaredSine.getField();
            return new Vector3DDS(field.getOne(), field.getZero(), field.getZero());
        } else if (q0.getValue() < 0) {
            DerivativeStructure inverse = squaredSine.sqrt().reciprocal();
            return new Vector3DDS(q1.multiply(inverse), q2.multiply(inverse), q3.multiply(inverse));
        }
        final DerivativeStructure inverse = squaredSine.sqrt().reciprocal().negate();
        return new Vector3DDS(q1.multiply(inverse), q2.multiply(inverse), q3.multiply(inverse));
    }

    /** Get the angle of the rotation.
     * @return angle of the rotation (between 0 and &pi;)
     * @see #Rotation(Vector3DDS, DerivativeStructure)
     */
    public DerivativeStructure getAngle() {
        if ((q0.getValue() < -0.1) || (q0.getValue() > 0.1)) {
            return q1.multiply(q1).add(q2.multiply(q2)).add(q3.multiply(q3)).sqrt().asin().multiply(2);
        } else if (q0.getValue() < 0) {
            return q0.negate().acos().multiply(2);
        }
        return q0.acos().multiply(2);
    }

    /** Get the Cardan or Euler angles corresponding to the instance.

     * <p>The equations show that each rotation can be defined by two
     * different values of the Cardan or Euler angles set. For example
     * if Cardan angles are used, the rotation defined by the angles
     * a<sub>1</sub>, a<sub>2</sub> and a<sub>3</sub> is the same as
     * the rotation defined by the angles &pi; + a<sub>1</sub>, &pi;
     * - a<sub>2</sub> and &pi; + a<sub>3</sub>. This method implements
     * the following arbitrary choices:</p>
     * <ul>
     *   <li>for Cardan angles, the chosen set is the one for which the
     *   second angle is between -&pi;/2 and &pi;/2 (i.e its cosine is
     *   positive),</li>
     *   <li>for Euler angles, the chosen set is the one for which the
     *   second angle is between 0 and &pi; (i.e its sine is positive).</li>
     * </ul>

     * <p>Cardan and Euler angle have a very disappointing drawback: all
     * of them have singularities. This means that if the instance is
     * too close to the singularities corresponding to the given
     * rotation order, it will be impossible to retrieve the angles. For
     * Cardan angles, this is often called gimbal lock. There is
     * <em>nothing</em> to do to prevent this, it is an intrinsic problem
     * with Cardan and Euler representation (but not a problem with the
     * rotation itself, which is perfectly well defined). For Cardan
     * angles, singularities occur when the second angle is close to
     * -&pi;/2 or +&pi;/2, for Euler angle singularities occur when the
     * second angle is close to 0 or &pi;, this implies that the identity
     * rotation is always singular for Euler angles!</p>

     * @param order rotation order to use
     * @return an array of three angles, in the order specified by the set
     * @exception CardanEulerSingularityException if the rotation is
     * singular with respect to the angles set specified
     */
    public DerivativeStructure[] getAngles(final RotationOrder order)
        throws CardanEulerSingularityException {

        if (order == RotationOrder.XYZ) {

            // r (+K) coordinates are :
            //  sin (theta), -cos (theta) sin (phi), cos (theta) cos (phi)
            // (-r) (+I) coordinates are :
            // cos (psi) cos (theta), -sin (psi) cos (theta), sin (theta)
            final // and we can choose to have theta in the interval [-PI/2 ; +PI/2]
            Vector3DDS v1 = applyTo(vector(0, 0, 1));
            final Vector3DDS v2 = applyInverseTo(vector(1, 0, 0));
            if  ((v2.getZ().getValue() < -0.9999999999) || (v2.getZ().getValue() > 0.9999999999)) {
                throw new CardanEulerSingularityException(true);
            }
            return new DerivativeStructure[] {
                DerivativeStructure.atan2(v1.getY().negate(), v1.getZ()),
                v2.getZ().asin(),
                DerivativeStructure.atan2(v2.getY().negate(), v2.getX())
            };

        } else if (order == RotationOrder.XZY) {

            // r (+J) coordinates are :
            // -sin (psi), cos (psi) cos (phi), cos (psi) sin (phi)
            // (-r) (+I) coordinates are :
            // cos (theta) cos (psi), -sin (psi), sin (theta) cos (psi)
            // and we can choose to have psi in the interval [-PI/2 ; +PI/2]
            final Vector3DDS v1 = applyTo(vector(0, 1, 0));
            final Vector3DDS v2 = applyInverseTo(vector(1, 0, 0));
            if ((v2.getY().getValue() < -0.9999999999) || (v2.getY().getValue() > 0.9999999999)) {
                throw new CardanEulerSingularityException(true);
            }
            return new DerivativeStructure[] {
                DerivativeStructure.atan2(v1.getZ(), v1.getY()),
                v2.getY().asin().negate(),
                DerivativeStructure.atan2(v2.getZ(), v2.getX())
            };

        } else if (order == RotationOrder.YXZ) {

            // r (+K) coordinates are :
            //  cos (phi) sin (theta), -sin (phi), cos (phi) cos (theta)
            // (-r) (+J) coordinates are :
            // sin (psi) cos (phi), cos (psi) cos (phi), -sin (phi)
            // and we can choose to have phi in the interval [-PI/2 ; +PI/2]
            final Vector3DDS v1 = applyTo(vector(0, 0, 1));
            final Vector3DDS v2 = applyInverseTo(vector(0, 1, 0));
            if ((v2.getZ().getValue() < -0.9999999999) || (v2.getZ().getValue() > 0.9999999999)) {
                throw new CardanEulerSingularityException(true);
            }
            return new DerivativeStructure[] {
                DerivativeStructure.atan2(v1.getX(), v1.getZ()),
                v2.getZ().asin().negate(),
                DerivativeStructure.atan2(v2.getX(), v2.getY())
            };

        } else if (order == RotationOrder.YZX) {

            // r (+I) coordinates are :
            // cos (psi) cos (theta), sin (psi), -cos (psi) sin (theta)
            // (-r) (+J) coordinates are :
            // sin (psi), cos (phi) cos (psi), -sin (phi) cos (psi)
            // and we can choose to have psi in the interval [-PI/2 ; +PI/2]
            final Vector3DDS v1 = applyTo(vector(1, 0, 0));
            final Vector3DDS v2 = applyInverseTo(vector(0, 1, 0));
            if ((v2.getX().getValue() < -0.9999999999) || (v2.getX().getValue() > 0.9999999999)) {
                throw new CardanEulerSingularityException(true);
            }
            return new DerivativeStructure[] {
                DerivativeStructure.atan2(v1.getZ().negate(), v1.getX()),
                v2.getX().asin(),
                DerivativeStructure.atan2(v2.getZ().negate(), v2.getY())
            };

        } else if (order == RotationOrder.ZXY) {

            // r (+J) coordinates are :
            // -cos (phi) sin (psi), cos (phi) cos (psi), sin (phi)
            // (-r) (+K) coordinates are :
            // -sin (theta) cos (phi), sin (phi), cos (theta) cos (phi)
            // and we can choose to have phi in the interval [-PI/2 ; +PI/2]
            final Vector3DDS v1 = applyTo(vector(0, 1, 0));
            final Vector3DDS v2 = applyInverseTo(vector(0, 0, 1));
            if ((v2.getY().getValue() < -0.9999999999) || (v2.getY().getValue() > 0.9999999999)) {
                throw new CardanEulerSingularityException(true);
            }
            return new DerivativeStructure[] {
                DerivativeStructure.atan2(v1.getX().negate(), v1.getY()),
                v2.getY().asin(),
                DerivativeStructure.atan2(v2.getX().negate(), v2.getZ())
            };

        } else if (order == RotationOrder.ZYX) {

            // r (+I) coordinates are :
            //  cos (theta) cos (psi), cos (theta) sin (psi), -sin (theta)
            // (-r) (+K) coordinates are :
            // -sin (theta), sin (phi) cos (theta), cos (phi) cos (theta)
            // and we can choose to have theta in the interval [-PI/2 ; +PI/2]
            final Vector3DDS v1 = applyTo(vector(1, 0, 0));
            final Vector3DDS v2 = applyInverseTo(vector(0, 0, 1));
            if ((v2.getX().getValue() < -0.9999999999) || (v2.getX().getValue() > 0.9999999999)) {
                throw new CardanEulerSingularityException(true);
            }
            return new DerivativeStructure[] {
                DerivativeStructure.atan2(v1.getY(), v1.getX()),
                v2.getX().asin().negate(),
                DerivativeStructure.atan2(v2.getY(), v2.getZ())
            };

        } else if (order == RotationOrder.XYX) {

            // r (+I) coordinates are :
            //  cos (theta), sin (phi1) sin (theta), -cos (phi1) sin (theta)
            // (-r) (+I) coordinates are :
            // cos (theta), sin (theta) sin (phi2), sin (theta) cos (phi2)
            // and we can choose to have theta in the interval [0 ; PI]
            final Vector3DDS v1 = applyTo(vector(1, 0, 0));
            final Vector3DDS v2 = applyInverseTo(vector(1, 0, 0));
            if ((v2.getX().getValue() < -0.9999999999) || (v2.getX().getValue() > 0.9999999999)) {
                throw new CardanEulerSingularityException(false);
            }
            return new DerivativeStructure[] {
                DerivativeStructure.atan2(v1.getY(), v1.getZ().negate()),
                v2.getX().acos(),
                DerivativeStructure.atan2(v2.getY(), v2.getZ())
            };

        } else if (order == RotationOrder.XZX) {

            // r (+I) coordinates are :
            //  cos (psi), cos (phi1) sin (psi), sin (phi1) sin (psi)
            // (-r) (+I) coordinates are :
            // cos (psi), -sin (psi) cos (phi2), sin (psi) sin (phi2)
            // and we can choose to have psi in the interval [0 ; PI]
            final Vector3DDS v1 = applyTo(vector(1, 0, 0));
            final Vector3DDS v2 = applyInverseTo(vector(1, 0, 0));
            if ((v2.getX().getValue() < -0.9999999999) || (v2.getX().getValue() > 0.9999999999)) {
                throw new CardanEulerSingularityException(false);
            }
            return new DerivativeStructure[] {
                DerivativeStructure.atan2(v1.getZ(), v1.getY()),
                v2.getX().acos(),
                DerivativeStructure.atan2(v2.getZ(), v2.getY().negate())
            };

        } else if (order == RotationOrder.YXY) {

            // r (+J) coordinates are :
            //  sin (theta1) sin (phi), cos (phi), cos (theta1) sin (phi)
            // (-r) (+J) coordinates are :
            // sin (phi) sin (theta2), cos (phi), -sin (phi) cos (theta2)
            // and we can choose to have phi in the interval [0 ; PI]
            final Vector3DDS v1 = applyTo(vector(0, 1, 0));
            final Vector3DDS v2 = applyInverseTo(vector(0, 1, 0));
            if ((v2.getY().getValue() < -0.9999999999) || (v2.getY().getValue() > 0.9999999999)) {
                throw new CardanEulerSingularityException(false);
            }
            return new DerivativeStructure[] {
                DerivativeStructure.atan2(v1.getX(), v1.getZ()),
                v2.getY().acos(),
                DerivativeStructure.atan2(v2.getX(), v2.getZ().negate())
            };

        } else if (order == RotationOrder.YZY) {

            // r (+J) coordinates are :
            //  -cos (theta1) sin (psi), cos (psi), sin (theta1) sin (psi)
            // (-r) (+J) coordinates are :
            // sin (psi) cos (theta2), cos (psi), sin (psi) sin (theta2)
            // and we can choose to have psi in the interval [0 ; PI]
            final Vector3DDS v1 = applyTo(vector(0, 1, 0));
            final Vector3DDS v2 = applyInverseTo(vector(0, 1, 0));
            if ((v2.getY().getValue() < -0.9999999999) || (v2.getY().getValue() > 0.9999999999)) {
                throw new CardanEulerSingularityException(false);
            }
            return new DerivativeStructure[] {
                DerivativeStructure.atan2(v1.getZ(), v1.getX().negate()),
                v2.getY().acos(),
                DerivativeStructure.atan2(v2.getZ(), v2.getX())
            };

        } else if (order == RotationOrder.ZXZ) {

            // r (+K) coordinates are :
            //  sin (psi1) sin (phi), -cos (psi1) sin (phi), cos (phi)
            // (-r) (+K) coordinates are :
            // sin (phi) sin (psi2), sin (phi) cos (psi2), cos (phi)
            // and we can choose to have phi in the interval [0 ; PI]
            final Vector3DDS v1 = applyTo(vector(0, 0, 1));
            final Vector3DDS v2 = applyInverseTo(vector(0, 0, 1));
            if ((v2.getZ().getValue() < -0.9999999999) || (v2.getZ().getValue() > 0.9999999999)) {
                throw new CardanEulerSingularityException(false);
            }
            return new DerivativeStructure[] {
                DerivativeStructure.atan2(v1.getX(), v1.getY().negate()),
                v2.getZ().acos(),
                DerivativeStructure.atan2(v2.getX(), v2.getY())
            };

        } else { // last possibility is ZYZ

            // r (+K) coordinates are :
            //  cos (psi1) sin (theta), sin (psi1) sin (theta), cos (theta)
            // (-r) (+K) coordinates are :
            // -sin (theta) cos (psi2), sin (theta) sin (psi2), cos (theta)
            // and we can choose to have theta in the interval [0 ; PI]
            final Vector3DDS v1 = applyTo(vector(0, 0, 1));
            final Vector3DDS v2 = applyInverseTo(vector(0, 0, 1));
            if ((v2.getZ().getValue() < -0.9999999999) || (v2.getZ().getValue() > 0.9999999999)) {
                throw new CardanEulerSingularityException(false);
            }
            return new DerivativeStructure[] {
                DerivativeStructure.atan2(v1.getY(), v1.getX()),
                v2.getZ().acos(),
                DerivativeStructure.atan2(v2.getY(), v2.getX().negate())
            };

        }

    }

    /** Create a constant vector with appropriate derivation parameters.
     * @param x abscissa
     * @param y ordinate
     * @param z height
     * @return a constant vector
     */
    private Vector3DDS vector(final double x, final double y, final double z) {
        final int parameters = q0.getFreeParameters();
        final int order      = q0.getOrder();
        return new Vector3DDS(new DerivativeStructure(parameters, order, x),
                              new DerivativeStructure(parameters, order, y),
                              new DerivativeStructure(parameters, order, z));
    }

    /** Get the 3X3 matrix corresponding to the instance
     * @return the matrix corresponding to the instance
     */
    public DerivativeStructure[][] getMatrix() {

        // products
        final DerivativeStructure q0q0  = q0.multiply(q0);
        final DerivativeStructure q0q1  = q0.multiply(q1);
        final DerivativeStructure q0q2  = q0.multiply(q2);
        final DerivativeStructure q0q3  = q0.multiply(q3);
        final DerivativeStructure q1q1  = q1.multiply(q1);
        final DerivativeStructure q1q2  = q1.multiply(q2);
        final DerivativeStructure q1q3  = q1.multiply(q3);
        final DerivativeStructure q2q2  = q2.multiply(q2);
        final DerivativeStructure q2q3  = q2.multiply(q3);
        final DerivativeStructure q3q3  = q3.multiply(q3);

        // create the matrix
        final DerivativeStructure[][] m = new DerivativeStructure[3][];
        m[0] = new DerivativeStructure[3];
        m[1] = new DerivativeStructure[3];
        m[2] = new DerivativeStructure[3];

        m [0][0] = q0q0.add(q1q1).multiply(2).subtract(1);
        m [1][0] = q1q2.subtract(q0q3).multiply(2);
        m [2][0] = q1q3.add(q0q2).multiply(2);

        m [0][1] = q1q2.add(q0q3).multiply(2);
        m [1][1] = q0q0.add(q2q2).multiply(2).subtract(1);
        m [2][1] = q2q3.subtract(q0q1).multiply(2);

        m [0][2] = q1q3.subtract(q0q2).multiply(2);
        m [1][2] = q2q3.add(q0q1).multiply(2);
        m [2][2] = q0q0.add(q3q3).multiply(2).subtract(1);

        return m;

    }

    /** Convert to a constant vector without derivatives.
     * @return a constant vector
     */
    public Rotation toRotation() {
        return new Rotation(q0.getValue(), q1.getValue(), q2.getValue(), q3.getValue(), false);
    }

    /** Apply the rotation to a vector.
     * @param u vector to apply the rotation to
     * @return a new vector which is the image of u by the rotation
     */
    public Vector3DDS applyTo(final Vector3DDS u) {

        final DerivativeStructure x = u.getX();
        final DerivativeStructure y = u.getY();
        final DerivativeStructure z = u.getZ();

        final DerivativeStructure s = q1.multiply(x).add(q2.multiply(y)).add(q3.multiply(z));

        return new Vector3DDS(q0.multiply(x.multiply(q0).subtract(q2.multiply(z).subtract(q3.multiply(y)))).add(s.multiply(q1)).multiply(2).subtract(x),
                              q0.multiply(y.multiply(q0).subtract(q3.multiply(x).subtract(q1.multiply(z)))).add(s.multiply(q2)).multiply(2).subtract(y),
                              q0.multiply(z.multiply(q0).subtract(q1.multiply(y).subtract(q2.multiply(x)))).add(s.multiply(q3)).multiply(2).subtract(z));

    }

    /** Apply the rotation to a vector.
     * @param u vector to apply the rotation to
     * @return a new vector which is the image of u by the rotation
     */
    public Vector3DDS applyTo(final Vector3D u) {

        final double x = u.getX();
        final double y = u.getY();
        final double z = u.getZ();

        final DerivativeStructure s = q1.multiply(x).add(q2.multiply(y)).add(q3.multiply(z));

        return new Vector3DDS(q0.multiply(q0.multiply(x).subtract(q2.multiply(z).subtract(q3.multiply(y)))).add(s.multiply(q1)).multiply(2).subtract(x),
                              q0.multiply(q0.multiply(y).subtract(q3.multiply(x).subtract(q1.multiply(z)))).add(s.multiply(q2)).multiply(2).subtract(y),
                              q0.multiply(q0.multiply(z).subtract(q1.multiply(y).subtract(q2.multiply(x)))).add(s.multiply(q3)).multiply(2).subtract(z));

    }

    /** Apply the rotation to a vector stored in an array.
     * @param in an array with three items which stores vector to rotate
     * @param out an array with three items to put result to (it can be the same
     * array as in)
     */
    public void applyTo(final DerivativeStructure[] in, final DerivativeStructure[] out) {

        final DerivativeStructure x = in[0];
        final DerivativeStructure y = in[1];
        final DerivativeStructure z = in[2];

        final DerivativeStructure s = q1.multiply(x).add(q2.multiply(y)).add(q3.multiply(z));

        out[0] = q0.multiply(x.multiply(q0).subtract(q2.multiply(z).subtract(q3.multiply(y)))).add(s.multiply(q1)).multiply(2).subtract(x);
        out[1] = q0.multiply(y.multiply(q0).subtract(q3.multiply(x).subtract(q1.multiply(z)))).add(s.multiply(q2)).multiply(2).subtract(y);
        out[2] = q0.multiply(z.multiply(q0).subtract(q1.multiply(y).subtract(q2.multiply(x)))).add(s.multiply(q3)).multiply(2).subtract(z);

    }

    /** Apply the rotation to a vector stored in an array.
     * @param in an array with three items which stores vector to rotate
     * @param out an array with three items to put result to
     */
    public void applyTo(final double[] in, final DerivativeStructure[] out) {

        final double x = in[0];
        final double y = in[1];
        final double z = in[2];

        final DerivativeStructure s = q1.multiply(x).add(q2.multiply(y)).add(q3.multiply(z));

        out[0] = q0.multiply(q0.multiply(x).subtract(q2.multiply(z).subtract(q3.multiply(y)))).add(s.multiply(q1)).multiply(2).subtract(x);
        out[1] = q0.multiply(q0.multiply(y).subtract(q3.multiply(x).subtract(q1.multiply(z)))).add(s.multiply(q2)).multiply(2).subtract(y);
        out[2] = q0.multiply(q0.multiply(z).subtract(q1.multiply(y).subtract(q2.multiply(x)))).add(s.multiply(q3)).multiply(2).subtract(z);

    }

    /** Apply a rotation to a vector.
     * @param r rotation to apply
     * @param u vector to apply the rotation to
     * @return a new vector which is the image of u by the rotation
     */
    public static Vector3DDS applyTo(final Rotation r, final Vector3DDS u) {

        final DerivativeStructure x = u.getX();
        final DerivativeStructure y = u.getY();
        final DerivativeStructure z = u.getZ();

        final DerivativeStructure s = x.multiply(r.getQ1()).add(y.multiply(r.getQ2())).add(z.multiply(r.getQ3()));

        return new Vector3DDS(x.multiply(r.getQ0()).subtract(z.multiply(r.getQ2()).subtract(y.multiply(r.getQ3()))).multiply(r.getQ0()).add(s.multiply(r.getQ1())).multiply(2).subtract(x),
                              y.multiply(r.getQ0()).subtract(x.multiply(r.getQ3()).subtract(z.multiply(r.getQ1()))).multiply(r.getQ0()).add(s.multiply(r.getQ2())).multiply(2).subtract(y),
                              z.multiply(r.getQ0()).subtract(y.multiply(r.getQ1()).subtract(x.multiply(r.getQ2()))).multiply(r.getQ0()).add(s.multiply(r.getQ3())).multiply(2).subtract(z));

    }

    /** Apply the inverse of the rotation to a vector.
     * @param u vector to apply the inverse of the rotation to
     * @return a new vector which such that u is its image by the rotation
     */
    public Vector3DDS applyInverseTo(final Vector3DDS u) {

        final DerivativeStructure x = u.getX();
        final DerivativeStructure y = u.getY();
        final DerivativeStructure z = u.getZ();

        final DerivativeStructure s  = q1.multiply(x).add(q2.multiply(y)).add(q3.multiply(z));
        final DerivativeStructure m0 = q0.negate();

        return new Vector3DDS(m0.multiply(x.multiply(m0).subtract(q2.multiply(z).subtract(q3.multiply(y)))).add(s.multiply(q1)).multiply(2).subtract(x),
                              m0.multiply(y.multiply(m0).subtract(q3.multiply(x).subtract(q1.multiply(z)))).add(s.multiply(q2)).multiply(2).subtract(y),
                              m0.multiply(z.multiply(m0).subtract(q1.multiply(y).subtract(q2.multiply(x)))).add(s.multiply(q3)).multiply(2).subtract(z));

    }

    /** Apply the inverse of the rotation to a vector.
     * @param u vector to apply the inverse of the rotation to
     * @return a new vector which such that u is its image by the rotation
     */
    public Vector3DDS applyInverseTo(final Vector3D u) {

        final double x = u.getX();
        final double y = u.getY();
        final double z = u.getZ();

        final DerivativeStructure s  = q1.multiply(x).add(q2.multiply(y)).add(q3.multiply(z));
        final DerivativeStructure m0 = q0.negate();

        return new Vector3DDS(m0.multiply(m0.multiply(x).subtract(q2.multiply(z).subtract(q3.multiply(y)))).add(s.multiply(q1)).multiply(2).subtract(x),
                              m0.multiply(m0.multiply(y).subtract(q3.multiply(x).subtract(q1.multiply(z)))).add(s.multiply(q2)).multiply(2).subtract(y),
                              m0.multiply(m0.multiply(z).subtract(q1.multiply(y).subtract(q2.multiply(x)))).add(s.multiply(q3)).multiply(2).subtract(z));

    }

    /** Apply the inverse of the rotation to a vector stored in an array.
     * @param in an array with three items which stores vector to rotate
     * @param out an array with three items to put result to (it can be the same
     * array as in)
     */
    public void applyInverseTo(final DerivativeStructure[] in, final DerivativeStructure[] out) {

        final DerivativeStructure x = in[0];
        final DerivativeStructure y = in[1];
        final DerivativeStructure z = in[2];

        final DerivativeStructure s = q1.multiply(x).add(q2.multiply(y)).add(q3.multiply(z));
        final DerivativeStructure m0 = q0.negate();

        out[0] = m0.multiply(x.multiply(m0).subtract(q2.multiply(z).subtract(q3.multiply(y)))).add(s.multiply(q1)).multiply(2).subtract(x);
        out[1] = m0.multiply(y.multiply(m0).subtract(q3.multiply(x).subtract(q1.multiply(z)))).add(s.multiply(q2)).multiply(2).subtract(y);
        out[2] = m0.multiply(z.multiply(m0).subtract(q1.multiply(y).subtract(q2.multiply(x)))).add(s.multiply(q3)).multiply(2).subtract(z);

    }

    /** Apply the inverse of the rotation to a vector stored in an array.
     * @param in an array with three items which stores vector to rotate
     * @param out an array with three items to put result to
     */
    public void applyInverseTo(final double[] in, final DerivativeStructure[] out) {

        final double x = in[0];
        final double y = in[1];
        final double z = in[2];

        final DerivativeStructure s = q1.multiply(x).add(q2.multiply(y)).add(q3.multiply(z));
        final DerivativeStructure m0 = q0.negate();

        out[0] = m0.multiply(m0.multiply(x).subtract(q2.multiply(z).subtract(q3.multiply(y)))).add(s.multiply(q1)).multiply(2).subtract(x);
        out[1] = m0.multiply(m0.multiply(y).subtract(q3.multiply(x).subtract(q1.multiply(z)))).add(s.multiply(q2)).multiply(2).subtract(y);
        out[2] = m0.multiply(m0.multiply(z).subtract(q1.multiply(y).subtract(q2.multiply(x)))).add(s.multiply(q3)).multiply(2).subtract(z);

    }

    /** Apply the inverse of a rotation to a vector.
     * @param r rotation to apply
     * @param u vector to apply the inverse of the rotation to
     * @return a new vector which such that u is its image by the rotation
     */
    public static Vector3DDS applyInverseTo(final Rotation r, final Vector3DDS u) {

        final DerivativeStructure x = u.getX();
        final DerivativeStructure y = u.getY();
        final DerivativeStructure z = u.getZ();

        final DerivativeStructure s  = x.multiply(r.getQ1()).add(y.multiply(r.getQ2())).add(z.multiply(r.getQ3()));
        final double m0 = -r.getQ0();

        return new Vector3DDS(x.multiply(m0).subtract(z.multiply(r.getQ2()).subtract(y.multiply(r.getQ3()))).multiply(m0).add(s.multiply(r.getQ1())).multiply(2).subtract(x),
                              y.multiply(m0).subtract(x.multiply(r.getQ3()).subtract(z.multiply(r.getQ1()))).multiply(m0).add(s.multiply(r.getQ2())).multiply(2).subtract(y),
                              z.multiply(m0).subtract(y.multiply(r.getQ1()).subtract(x.multiply(r.getQ2()))).multiply(m0).add(s.multiply(r.getQ3())).multiply(2).subtract(z));

    }

    /** Apply the instance to another rotation.
     * Applying the instance to a rotation is computing the composition
     * in an order compliant with the following rule : let u be any
     * vector and v its image by r (i.e. r.applyTo(u) = v), let w be the image
     * of v by the instance (i.e. applyTo(v) = w), then w = comp.applyTo(u),
     * where comp = applyTo(r).
     * @param r rotation to apply the rotation to
     * @return a new rotation which is the composition of r by the instance
     */
    public RotationDS applyTo(final RotationDS r) {
        return new RotationDS(r.q0.multiply(q0).subtract(r.q1.multiply(q1).add(r.q2.multiply(q2)).add(r.q3.multiply(q3))),
                              r.q1.multiply(q0).add(r.q0.multiply(q1)).add(r.q2.multiply(q3).subtract(r.q3.multiply(q2))),
                              r.q2.multiply(q0).add(r.q0.multiply(q2)).add(r.q3.multiply(q1).subtract(r.q1.multiply(q3))),
                              r.q3.multiply(q0).add(r.q0.multiply(q3)).add(r.q1.multiply(q2).subtract(r.q2.multiply(q1))),
                              false);
    }

    /** Apply the instance to another rotation.
     * Applying the instance to a rotation is computing the composition
     * in an order compliant with the following rule : let u be any
     * vector and v its image by r (i.e. r.applyTo(u) = v), let w be the image
     * of v by the instance (i.e. applyTo(v) = w), then w = comp.applyTo(u),
     * where comp = applyTo(r).
     * @param r rotation to apply the rotation to
     * @return a new rotation which is the composition of r by the instance
     */
    public RotationDS applyTo(final Rotation r) {
        return new RotationDS(q0.multiply(r.getQ0()).subtract(q1.multiply(r.getQ1()).add(q2.multiply(r.getQ2())).add(q3.multiply(r.getQ3()))),
                              q0.multiply(r.getQ1()).add(q1.multiply(r.getQ0())).add(q3.multiply(r.getQ2()).subtract(q2.multiply(r.getQ3()))),
                              q0.multiply(r.getQ2()).add(q2.multiply(r.getQ0())).add(q1.multiply(r.getQ3()).subtract(q3.multiply(r.getQ1()))),
                              q0.multiply(r.getQ3()).add(q3.multiply(r.getQ0())).add(q2.multiply(r.getQ1()).subtract(q1.multiply(r.getQ2()))),
                              false);
    }

    /** Apply a rotation to another rotation.
     * Applying a rotation to another rotation is computing the composition
     * in an order compliant with the following rule : let u be any
     * vector and v its image by rInner (i.e. rInner.applyTo(u) = v), let w be the image
     * of v by rOuter (i.e. rOuter.applyTo(v) = w), then w = comp.applyTo(u),
     * where comp = applyTo(rOuter, rInner).
     * @param r1 rotation to apply
     * @param rInner rotation to apply the rotation to
     * @return a new rotation which is the composition of r by the instance
     */
    public static RotationDS applyTo(final Rotation r1, final RotationDS rInner) {
        return new RotationDS(rInner.q0.multiply(r1.getQ0()).subtract(rInner.q1.multiply(r1.getQ1()).add(rInner.q2.multiply(r1.getQ2())).add(rInner.q3.multiply(r1.getQ3()))),
                              rInner.q1.multiply(r1.getQ0()).add(rInner.q0.multiply(r1.getQ1())).add(rInner.q2.multiply(r1.getQ3()).subtract(rInner.q3.multiply(r1.getQ2()))),
                              rInner.q2.multiply(r1.getQ0()).add(rInner.q0.multiply(r1.getQ2())).add(rInner.q3.multiply(r1.getQ1()).subtract(rInner.q1.multiply(r1.getQ3()))),
                              rInner.q3.multiply(r1.getQ0()).add(rInner.q0.multiply(r1.getQ3())).add(rInner.q1.multiply(r1.getQ2()).subtract(rInner.q2.multiply(r1.getQ1()))),
                              false);
    }

    /** Apply the inverse of the instance to another rotation.
     * Applying the inverse of the instance to a rotation is computing
     * the composition in an order compliant with the following rule :
     * let u be any vector and v its image by r (i.e. r.applyTo(u) = v),
     * let w be the inverse image of v by the instance
     * (i.e. applyInverseTo(v) = w), then w = comp.applyTo(u), where
     * comp = applyInverseTo(r).
     * @param r rotation to apply the rotation to
     * @return a new rotation which is the composition of r by the inverse
     * of the instance
     */
    public RotationDS applyInverseTo(final RotationDS r) {
        return new RotationDS(r.q0.multiply(q0).add(r.q1.multiply(q1).add(r.q2.multiply(q2)).add(r.q3.multiply(q3))).negate(),
                              r.q0.multiply(q1).add(r.q2.multiply(q3).subtract(r.q3.multiply(q2))).subtract(r.q1.multiply(q0)),
                              r.q0.multiply(q2).add(r.q3.multiply(q1).subtract(r.q1.multiply(q3))).subtract(r.q2.multiply(q0)),
                              r.q0.multiply(q3).add(r.q1.multiply(q2).subtract(r.q2.multiply(q1))).subtract(r.q3.multiply(q0)),
                              false);
    }

    /** Apply the inverse of the instance to another rotation.
     * Applying the inverse of the instance to a rotation is computing
     * the composition in an order compliant with the following rule :
     * let u be any vector and v its image by r (i.e. r.applyTo(u) = v),
     * let w be the inverse image of v by the instance
     * (i.e. applyInverseTo(v) = w), then w = comp.applyTo(u), where
     * comp = applyInverseTo(r).
     * @param r rotation to apply the rotation to
     * @return a new rotation which is the composition of r by the inverse
     * of the instance
     */
    public RotationDS applyInverseTo(final Rotation r) {
        return new RotationDS(q0.multiply(r.getQ0()).add(q1.multiply(r.getQ1()).add(q2.multiply(r.getQ2())).add(q3.multiply(r.getQ3()))).negate(),
                              q1.multiply(r.getQ0()).add(q3.multiply(r.getQ2()).subtract(q2.multiply(r.getQ3()))).subtract(q0.multiply(r.getQ1())),
                              q2.multiply(r.getQ0()).add(q1.multiply(r.getQ3()).subtract(q3.multiply(r.getQ1()))).subtract(q0.multiply(r.getQ2())),
                              q3.multiply(r.getQ0()).add(q2.multiply(r.getQ1()).subtract(q1.multiply(r.getQ2()))).subtract(q0.multiply(r.getQ3())),
                              false);
    }

    /** Apply the inverse of a rotation to another rotation.
     * Applying the inverse of a rotation to another rotation is computing
     * the composition in an order compliant with the following rule :
     * let u be any vector and v its image by rInner (i.e. rInner.applyTo(u) = v),
     * let w be the inverse image of v by rOuter
     * (i.e. rOuter.applyInverseTo(v) = w), then w = comp.applyTo(u), where
     * comp = applyInverseTo(rOuter, rInner).
     * @param rOuter rotation to apply the rotation to
     * @param rInner rotation to apply the rotation to
     * @return a new rotation which is the composition of r by the inverse
     * of the instance
     */
    public static RotationDS applyInverseTo(final Rotation rOuter, final RotationDS rInner) {
        return new RotationDS(rInner.q0.multiply(rOuter.getQ0()).add(rInner.q1.multiply(rOuter.getQ1()).add(rInner.q2.multiply(rOuter.getQ2())).add(rInner.q3.multiply(rOuter.getQ3()))).negate(),
                              rInner.q0.multiply(rOuter.getQ1()).add(rInner.q2.multiply(rOuter.getQ3()).subtract(rInner.q3.multiply(rOuter.getQ2()))).subtract(rInner.q1.multiply(rOuter.getQ0())),
                              rInner.q0.multiply(rOuter.getQ2()).add(rInner.q3.multiply(rOuter.getQ1()).subtract(rInner.q1.multiply(rOuter.getQ3()))).subtract(rInner.q2.multiply(rOuter.getQ0())),
                              rInner.q0.multiply(rOuter.getQ3()).add(rInner.q1.multiply(rOuter.getQ2()).subtract(rInner.q2.multiply(rOuter.getQ1()))).subtract(rInner.q3.multiply(rOuter.getQ0())),
                              false);
    }

    /** Perfect orthogonality on a 3X3 matrix.
     * @param m initial matrix (not exactly orthogonal)
     * @param threshold convergence threshold for the iterative
     * orthogonality correction (convergence is reached when the
     * difference between two steps of the Frobenius norm of the
     * correction is below this threshold)
     * @return an orthogonal matrix close to m
     * @exception NotARotationMatrixException if the matrix cannot be
     * orthogonalized with the given threshold after 10 iterations
     */
    private DerivativeStructure[][] orthogonalizeMatrix(final DerivativeStructure[][] m,
                                                        final double threshold)
        throws NotARotationMatrixException {

        DerivativeStructure x00 = m[0][0];
        DerivativeStructure x01 = m[0][1];
        DerivativeStructure x02 = m[0][2];
        DerivativeStructure x10 = m[1][0];
        DerivativeStructure x11 = m[1][1];
        DerivativeStructure x12 = m[1][2];
        DerivativeStructure x20 = m[2][0];
        DerivativeStructure x21 = m[2][1];
        DerivativeStructure x22 = m[2][2];
        double fn = 0;
        double fn1;

        final DerivativeStructure[][] o = new DerivativeStructure[3][3];

        // iterative correction: Xn+1 = Xn - 0.5 * (Xn.Mt.Xn - M)
        int i = 0;
        while (++i < 11) {

            // Mt.Xn
            final DerivativeStructure mx00 = m[0][0].multiply(x00).add(m[1][0].multiply(x10)).add(m[2][0].multiply(x20));
            final DerivativeStructure mx10 = m[0][1].multiply(x00).add(m[1][1].multiply(x10)).add(m[2][1].multiply(x20));
            final DerivativeStructure mx20 = m[0][2].multiply(x00).add(m[1][2].multiply(x10)).add(m[2][2].multiply(x20));
            final DerivativeStructure mx01 = m[0][0].multiply(x01).add(m[1][0].multiply(x11)).add(m[2][0].multiply(x21));
            final DerivativeStructure mx11 = m[0][1].multiply(x01).add(m[1][1].multiply(x11)).add(m[2][1].multiply(x21));
            final DerivativeStructure mx21 = m[0][2].multiply(x01).add(m[1][2].multiply(x11)).add(m[2][2].multiply(x21));
            final DerivativeStructure mx02 = m[0][0].multiply(x02).add(m[1][0].multiply(x12)).add(m[2][0].multiply(x22));
            final DerivativeStructure mx12 = m[0][1].multiply(x02).add(m[1][1].multiply(x12)).add(m[2][1].multiply(x22));
            final DerivativeStructure mx22 = m[0][2].multiply(x02).add(m[1][2].multiply(x12)).add(m[2][2].multiply(x22));

            // Xn+1
            o[0][0] = x00.subtract(x00.multiply(mx00).add(x01.multiply(mx10)).add(x02.multiply(mx20)).subtract(m[0][0]).multiply(0.5));
            o[0][1] = x01.subtract(x00.multiply(mx01).add(x01.multiply(mx11)).add(x02.multiply(mx21)).subtract(m[0][1]).multiply(0.5));
            o[0][2] = x02.subtract(x00.multiply(mx02).add(x01.multiply(mx12)).add(x02.multiply(mx22)).subtract(m[0][2]).multiply(0.5));
            o[1][0] = x10.subtract(x10.multiply(mx00).add(x11.multiply(mx10)).add(x12.multiply(mx20)).subtract(m[1][0]).multiply(0.5));
            o[1][1] = x11.subtract(x10.multiply(mx01).add(x11.multiply(mx11)).add(x12.multiply(mx21)).subtract(m[1][1]).multiply(0.5));
            o[1][2] = x12.subtract(x10.multiply(mx02).add(x11.multiply(mx12)).add(x12.multiply(mx22)).subtract(m[1][2]).multiply(0.5));
            o[2][0] = x20.subtract(x20.multiply(mx00).add(x21.multiply(mx10)).add(x22.multiply(mx20)).subtract(m[2][0]).multiply(0.5));
            o[2][1] = x21.subtract(x20.multiply(mx01).add(x21.multiply(mx11)).add(x22.multiply(mx21)).subtract(m[2][1]).multiply(0.5));
            o[2][2] = x22.subtract(x20.multiply(mx02).add(x21.multiply(mx12)).add(x22.multiply(mx22)).subtract(m[2][2]).multiply(0.5));

            // correction on each elements
            final double corr00 = o[0][0].getValue() - m[0][0].getValue();
            final double corr01 = o[0][1].getValue() - m[0][1].getValue();
            final double corr02 = o[0][2].getValue() - m[0][2].getValue();
            final double corr10 = o[1][0].getValue() - m[1][0].getValue();
            final double corr11 = o[1][1].getValue() - m[1][1].getValue();
            final double corr12 = o[1][2].getValue() - m[1][2].getValue();
            final double corr20 = o[2][0].getValue() - m[2][0].getValue();
            final double corr21 = o[2][1].getValue() - m[2][1].getValue();
            final double corr22 = o[2][2].getValue() - m[2][2].getValue();

            // Frobenius norm of the correction
            fn1 = corr00 * corr00 + corr01 * corr01 + corr02 * corr02 +
                  corr10 * corr10 + corr11 * corr11 + corr12 * corr12 +
                  corr20 * corr20 + corr21 * corr21 + corr22 * corr22;

            // convergence test
            if (FastMath.abs(fn1 - fn) <= threshold) {
                return o;
            }

            // prepare next iteration
            x00 = o[0][0];
            x01 = o[0][1];
            x02 = o[0][2];
            x10 = o[1][0];
            x11 = o[1][1];
            x12 = o[1][2];
            x20 = o[2][0];
            x21 = o[2][1];
            x22 = o[2][2];
            fn  = fn1;

        }

        // the algorithm did not converge after 10 iterations
        throw new NotARotationMatrixException(LocalizedFormats.UNABLE_TO_ORTHOGONOLIZE_MATRIX,
                                              i - 1);

    }

    /** Compute the <i>distance</i> between two rotations.
     * <p>The <i>distance</i> is intended here as a way to check if two
     * rotations are almost similar (i.e. they transform vectors the same way)
     * or very different. It is mathematically defined as the angle of
     * the rotation r that prepended to one of the rotations gives the other
     * one:</p>
     * <pre>
     *        r<sub>1</sub>(r) = r<sub>2</sub>
     * </pre>
     * <p>This distance is an angle between 0 and &pi;. Its value is the smallest
     * possible upper bound of the angle in radians between r<sub>1</sub>(v)
     * and r<sub>2</sub>(v) for all possible vectors v. This upper bound is
     * reached for some v. The distance is equal to 0 if and only if the two
     * rotations are identical.</p>
     * <p>Comparing two rotations should always be done using this value rather
     * than for example comparing the components of the quaternions. It is much
     * more stable, and has a geometric meaning. Also comparing quaternions
     * components is error prone since for example quaternions (0.36, 0.48, -0.48, -0.64)
     * and (-0.36, -0.48, 0.48, 0.64) represent exactly the same rotation despite
     * their components are different (they are exact opposites).</p>
     * @param r1 first rotation
     * @param r2 second rotation
     * @return <i>distance</i> between r1 and r2
     */
    public static DerivativeStructure distance(final RotationDS r1, final RotationDS r2) {
        return r1.applyInverseTo(r2).getAngle();
    }

}
