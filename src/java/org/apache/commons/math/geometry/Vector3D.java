// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
// 
//   http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.apache.commons.math.geometry;

import java.io.Serializable;

/** This class implements vectors in a three-dimensional space.
 * <p>Instance of this class are guaranteed to be immutable.</p>
 * @version $Id: Vector3D.java 1716 2006-12-13 22:56:35Z luc $
 * @author L. Maisonobe
 */

public class Vector3D
  implements Serializable {

  /** First canonical vector (coordinates : 1, 0, 0). */
  public static final Vector3D plusI = new Vector3D(1, 0, 0);

  /** Opposite of the first canonical vector (coordinates : -1, 0, 0). */
  public static final Vector3D minusI = new Vector3D(-1, 0, 0);

  /** Second canonical vector (coordinates : 0, 1, 0). */
  public static final Vector3D plusJ = new Vector3D(0, 1, 0);

  /** Opposite of the second canonical vector (coordinates : 0, -1, 0). */
  public static final Vector3D minusJ = new Vector3D(0, -1, 0);

  /** Third canonical vector (coordinates : 0, 0, 1). */
  public static final Vector3D plusK = new Vector3D(0, 0, 1);

  /** Opposite of the third canonical vector (coordinates : 0, 0, -1).  */
  public static final Vector3D minusK = new Vector3D(0, 0, -1);

  /** Simple constructor.
   * Build a null vector.
   */
  public Vector3D() {
    x = 0;
    y = 0;
    z = 0;
  }

  /** Simple constructor.
   * Build a vector from its coordinates
   * @param x abscissa
   * @param y ordinate
   * @param z height
   * @see #getX()
   * @see #getY()
   * @see #getZ()
   */
  public Vector3D(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /** Simple constructor.
   * Build a vector from its azimuthal coordinates
   * @param alpha azimuth (&alpha;) around Z
   *              (0 is +X, &pi;/2 is +Y, &pi; is -X and 3&pi;/2 is -Y)
   * @param delta elevation (&delta;) above (XY) plane, from -&pi;/2 to +&pi;/2
   * @see #getAlpha()
   * @see #getDelta()
   */
  public Vector3D(double alpha, double delta) {
    double cosDelta = Math.cos(delta);
    this.x = Math.cos(alpha) * cosDelta;
    this.y = Math.sin(alpha) * cosDelta;
    this.z = Math.sin(delta);
  }

  /** Multiplicative constructor
   * Build a vector from another one and a scale factor. 
   * The vector built will be a * u
   * @param a scale factor
   * @param u base (unscaled) vector
   */
  public Vector3D(double a, Vector3D u) {
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
  public Vector3D(double a1, Vector3D u1, double a2, Vector3D u2) {
    this.x = a1 * u1.x + a2 * u2.x;
    this.y = a1 * u1.y + a2 * u2.y;
    this.z = a1 * u1.z + a2 * u2.z;
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
  public Vector3D(double a1, Vector3D u1, double a2, Vector3D u2,
                  double a3, Vector3D u3) {
    this.x = a1 * u1.x + a2 * u2.x + a3 * u3.x;
    this.y = a1 * u1.y + a2 * u2.y + a3 * u3.y;
    this.z = a1 * u1.z + a2 * u2.z + a3 * u3.z;
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
  public Vector3D(double a1, Vector3D u1, double a2, Vector3D u2,
                  double a3, Vector3D u3, double a4, Vector3D u4) {
    this.x = a1 * u1.x + a2 * u2.x + a3 * u3.x + a4 * u4.x;
    this.y = a1 * u1.y + a2 * u2.y + a3 * u3.y + a4 * u4.y;
    this.z = a1 * u1.z + a2 * u2.z + a3 * u3.z + a4 * u4.z;
  }

  /** Get the abscissa of the vector.
   * @return abscissa of the vector
   * @see #Vector3D(double, double, double)
   */
  public double getX() {
    return x;
  }

  /** Get the ordinate of the vector.
   * @return ordinate of the vector
   * @see #Vector3D(double, double, double)
   */
  public double getY() {
    return y;
  }

  /** Get the height of the vector.
   * @return height of the vector
   * @see #Vector3D(double, double, double)
   */
  public double getZ() {
    return z;
  }

  /** Get the norm for the vector.
   * @return euclidian norm for the vector
   */
  public double getNorm() {
    return Math.sqrt (x * x + y * y + z * z);
  }

  /** Get the azimuth of the vector.
   * @return azimuth (&alpha;) of the vector, between -&pi; and +&pi;
   * @see #Vector3D(double, double)
   */
  public double getAlpha() {
    return Math.atan2(y, x);
  }

  /** Get the elevation of the vector.
   * @return elevation (&delta;) of the vector, between -&pi;/2 and +&pi;/2
   * @see #Vector3D(double, double)
   */
  public double getDelta() {
    return Math.asin(z / getNorm());
  }

  /** Add a vector to the instance.
   * @param v vector to add
   * @return a new vector
   */
  public Vector3D add(Vector3D v) {
    return new Vector3D(x + v.x, y + v.y, z + v.z);
  }

  /** Add a scaled vector to the instance.
   * @param factor scale factor to apply to v before adding it
   * @param v vector to add
   * @return a new vector
   */
  public Vector3D add(double factor, Vector3D v) {
    return new Vector3D(x + factor * v.x, y + factor * v.y, z + factor * v.z);
  }

  /** Subtract a vector from the instance.
   * @param v vector to subtract
   * @return a new vector
   */
  public Vector3D subtract(Vector3D v) {
    return new Vector3D(x - v.x, y - v.y, z - v.z);
  }

  /** Subtract a scaled vector from the instance.
   * @param factor scale factor to apply to v before subtracting it
   * @param v vector to subtract
   * @return a new vector
   */
  public Vector3D subtract(double factor, Vector3D v) {
    return new Vector3D(x - factor * v.x, y - factor * v.y, z - factor * v.z);
  }

  /** Normalize the instance.
   * @return a new normalized vector
   * @exception ArithmeticException if the norm is null
   */
  public Vector3D normalize() {
    double s = getNorm();
    if (s == 0) {
      throw new ArithmeticException("null norm");
    }
    return multiply(1 / s);
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
   * @exception ArithmeticException if the norm of the instance is null
   */
  public Vector3D orthogonal() {

    double threshold = 0.6 * getNorm();
    if (threshold == 0) {
      throw new ArithmeticException("null norm");
    }

    if ((x >= -threshold) && (x <= threshold)) {
      double inverse  = 1 / Math.sqrt(y * y + z * z);
      return new Vector3D(0, inverse * z, -inverse * y);
    } else if ((y >= -threshold) && (y <= threshold)) {
      double inverse  = 1 / Math.sqrt(x * x + z * z);
      return new Vector3D(-inverse * z, 0, inverse * x);
    } else {
      double inverse  = 1 / Math.sqrt(x * x + y * y);
      return new Vector3D(inverse * y, -inverse * x, 0);
    }

  }

  /** Compute the angular separation between two vectors.
   * <p>This method computes the angular separation between two
   * vectors using the dot product for well separated vectors and the
   * cross product for almost aligned vectors. This allow to have a
   * good accuracy in all cases, even for vectors very close to each
   * other.</p>
   * @param v1 first vector
   * @param v2 second vector
   * @exception ArithmeticException if either vector has a null norm
   */
  public static double angle(Vector3D v1, Vector3D v2) {

    double normProduct = v1.getNorm() * v2.getNorm();
    if (normProduct == 0) {
      throw new ArithmeticException("null norm");
    }

    double dot = dotProduct(v1, v2);
    double threshold = normProduct * 0.9999;
    if ((dot < -threshold) || (dot > threshold)) {
      // the vectors are almost aligned, compute using the sine
      Vector3D v3 = crossProduct(v1, v2);
      if (dot >= 0) {
        return Math.asin(v3.getNorm() / normProduct);
      }
      return Math.PI - Math.asin(v3.getNorm() / normProduct);
    }
    
    // the vectors are sufficiently separated to use the cosine
    return Math.acos(dot / normProduct);

  }

  /** Get the opposite of the instance.
   * @return a new vector which is opposite to the instance
   */
  public Vector3D negate() {
    return new Vector3D(-x, -y, -z);
  }

  /** Multiply the instance by a scalar
   * @param a scalar
   * @return a new vector
   */
  public Vector3D multiply(double a) {
    return new Vector3D(a * x, a * y, a * z);
  }

  /** Compute the dot-product of two vectors.
   * @param v1 first vector
   * @param v2 second vector
   * @return the dot product v1.v2
   */
  public static double dotProduct(Vector3D v1, Vector3D v2) {
    return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
  }

  /** Compute the cross-product of two vectors.
   * @param v1 first vector
   * @param v2 second vector
   * @return the cross product v1 ^ v2 as a new Vector
   */
  public static Vector3D crossProduct(Vector3D v1, Vector3D v2) {
    return new Vector3D(v1.y * v2.z - v1.z * v2.y,
                        v1.z * v2.x - v1.x * v2.z,
                        v1.x * v2.y - v1.y * v2.x);
  }

  /** Abscissa. */
  private final double x;

  /** Ordinate. */
  private final double y;

  /** Height. */
  private final double z;

  private static final long serialVersionUID = 7318440192750283659L;

}
