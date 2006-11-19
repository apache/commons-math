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

package org.spaceroots.mantissa.geometry;

import java.io.Serializable;
import org.spaceroots.mantissa.utilities.ArraySliceMappable;


/** This class implements vectors in a three-dimensional space.
 * @version $Id: Vector3D.java 1705 2006-09-17 19:57:39Z luc $
 * @author L. Maisonobe
 */

public class Vector3D
  implements ArraySliceMappable, Serializable {

  /** First canonical vector (coordinates : 1, 0, 0).
   * This is really an {@link ImmutableVector3D ImmutableVector3D},
   * hence it can't be changed in any way.
   */
  public static final Vector3D plusI = new ImmutableVector3D(1, 0, 0);

  /** Opposite of the first canonical vector (coordinates : -1, 0, 0).
   * This is really an {@link ImmutableVector3D ImmutableVector3D},
   * hence it can't be changed in any way.
   */
  public static final Vector3D minusI = new ImmutableVector3D(-1, 0, 0);

  /** Second canonical vector (coordinates : 0, 1, 0).
   * This is really an {@link ImmutableVector3D ImmutableVector3D},
   * hence it can't be changed in any way.
   */
  public static final Vector3D plusJ = new ImmutableVector3D(0, 1, 0);

  /** Opposite of the second canonical vector (coordinates : 0, -1, 0).
   * This is really an {@link ImmutableVector3D ImmutableVector3D},
   * hence it can't be changed in any way.
   */
  public static final Vector3D minusJ = new ImmutableVector3D(0, -1, 0);

  /** Third canonical vector (coordinates : 0, 0, 1).
   * This is really an {@link ImmutableVector3D ImmutableVector3D},
   * hence it can't be changed in any way.
   */
  public static final Vector3D plusK = new ImmutableVector3D(0, 0, 1);

  /** Opposite of the third canonical vector (coordinates : 0, 0, -1).
   * This is really an {@link ImmutableVector3D ImmutableVector3D},
   * hence it can't be changed in any way.
   */
  public static final Vector3D minusK = new ImmutableVector3D(0, 0, -1);

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
   * The vector built will be a * u +  b * v
   * @param a first scale factor
   * @param u first base (unscaled) vector
   * @param b second scale factor
   * @param v second base (unscaled) vector
   */
  public Vector3D(double a, Vector3D u, double b, Vector3D v) {
    this.x = a * u.x + b * v.x;
    this.y = a * u.y + b * v.y;
    this.z = a * u.z + b * v.z;
  }

  /** Linear constructor
   * Build a vector from three other ones and corresponding scale factors.
   * The vector built will be a * u +  b * v + c * w
   * @param a first scale factor
   * @param u first base (unscaled) vector
   * @param b second scale factor
   * @param v second base (unscaled) vector
   * @param c third scale factor
   * @param w third base (unscaled) vector
   */
  public Vector3D(double a, Vector3D u,
                  double b, Vector3D v,
                  double c, Vector3D w) {
    this.x = a * u.x + b * v.x + c * w.x;
    this.y = a * u.y + b * v.y + c * w.y;
    this.z = a * u.z + b * v.z + c * w.z;
  }

  /** Copy constructor.
   * Build a copy of a vector
   * @param v vector to copy
   */
  public Vector3D(Vector3D v) {
    x = v.x;
    y = v.y;
    z = v.z;
  }

  /** Reset the instance.
   * @param v vector to copy data from
   */
  public void reset(Vector3D v) {
    x = v.x;
    y = v.y;
    z = v.z;
  }

  /** Get the abscissa of the vector.
   * @return abscissa of the vector
   * @see #Vector3D(double, double, double)
   * @see #setX(double)
   */
  public double getX() {
    return x;
  }

  /** Set the abscissa of the vector.
   * @param x new abscissa for the vector
   * @see #getX()
   * @see #setCoordinates(double, double, double)
   */
  public void setX(double x) {
    this.x = x;
  }

  /** Get the ordinate of the vector.
   * @return ordinate of the vector
   * @see #Vector3D(double, double, double)
   * @see #setY(double)
   */
  public double getY() {
    return y;
  }

  /** Set the ordinate of the vector.
   * @param y new ordinate for the vector
   * @see #getY()
   * @see #setCoordinates(double, double, double)
   */
  public void setY(double y) {
    this.y = y;
  }

  /** Get the height of the vector.
   * @return height of the vector
   * @see #Vector3D(double, double, double)
   * @see #setZ(double)
   */
  public double getZ() {
    return z;
  }

  /** Set the height of the vector.
   * @param z new height for the vector
   * @see #getZ()
   * @see #setCoordinates(double, double, double)
   */
  public void setZ(double z) {
    this.z = z;
  }

  /** Set all coordinates of the vector.
   * @param x new abscissa for the vector
   * @param y new ordinate for the vector
   * @param z new height for the vector
   * @see #Vector3D(double, double, double)
   */
  public void setCoordinates(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
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
   * Add a vector to the instance. The instance is changed.
   * @param v vector to add
   */
  public void addToSelf(Vector3D v) {
    x += v.x;
    y += v.y;
    z += v.z;
  }

  /** Add a scaled vector to the instance.
   * Add a scaled vector to the instance. The instance is changed.
   * @param factor scale factor to apply to v before adding it
   * @param v vector to add
   */
  public void addToSelf(double factor, Vector3D v) {
    x += factor * v.x;
    y += factor * v.y;
    z += factor * v.z;
  }

  /** Add two vectors.
   * Add two vectors and return the sum as a new vector
   * @param v1 first vector
   * @param v2 second vector
   * @return a new vector equal to v1 + v2
   */
  public static Vector3D add(Vector3D v1, Vector3D v2) {
    return new Vector3D(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
  }

  /** Subtract a vector from the instance.
   * Subtract a vector from the instance. The instance is changed.
   * @param v vector to subtract
   */
  public void subtractFromSelf(Vector3D v) {
    x -= v.x;
    y -= v.y;
    z -= v.z;
  }

  /** Subtract two vectors.
   * Subtract two vectors and return the difference as a new vector
   * @param v1 first vector
   * @param v2 second vector
   * @return a new vector equal to v1 - v2
   */
  public static Vector3D subtract(Vector3D v1, Vector3D v2) {
    return new Vector3D(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
  }

  /** Normalize the instance.
   * Divide the instance by its norm in order to have a unit
   * vector. The instance is changed.
   * @exception ArithmeticException if the norm is null
   */
  public void normalizeSelf() {
    double s = getNorm();
    if (s == 0) {
      throw new ArithmeticException("null norm");
    }
    double invNorm = 1 / s;
    x *= invNorm;
    y *= invNorm;
    z *= invNorm;
  }

  /** Get a vector orthogonal to the instance.
   * <p>There are an infinite number of normalized vectors orthogonal
   * to the instance. This method picks up one of them almost
   * arbitrarily. It is useful when one needs to compute a reference
   * frame with one of the axes in a predefined direction. The
   * following example shows how to build a frame having the k axis
   * aligned with the known vector u :
   * <pre><code>
   *   Vector3D k = u;
   *   k.normalizeSelf();
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

  /** Revert the instance.
   * Replace the instance u by -u
   */
  public void negateSelf() {
    x = -x;
    y = -y;
    z = -z;
  }

  /** Get the opposite of a vector.
   * @param u vector to revert
   * @return a new vector which is -u
   */
  public static Vector3D negate(Vector3D u) {
    return new Vector3D(-u.x, -u.y, -u.z);
  }

  /** Multiply the instance by a scalar
   * Multiply the instance by a scalar. The instance is changed.
   * @param a scalar by which the instance should be multiplied
   */
  public void multiplySelf(double a) {
    x *= a;
    y *= a;
    z *= a;
  }

  /** Multiply a vector by a scalar
   * Multiply a vectors by a scalar and return the product as a new vector
   * @param a scalar
   * @param v vector
   * @return a new vector equal to a * v
   */
  public static Vector3D multiply(double a, Vector3D v) {
    return new Vector3D(a * v.x, a * v.y, a * v.z);
  }

  /** Compute the dot-product of two vectors.
   * @param v1 first vector
   * @param v2 second vector
   * @return the dot product v1.v2
   */
  public static double dotProduct(Vector3D v1, Vector3D v2) {
    return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
  }

  /** Set the instance to the result of the cross-product of two vectors.
   * @param v1 first vector (can be the instance)
   * @param v2 second vector (can be the instance)
   */
  public void setToCrossProduct(Vector3D v1, Vector3D v2) {
    double newX = v1.y * v2.z - v1.z * v2.y;
    double newY = v1.z * v2.x - v1.x * v2.z;
    z = v1.x * v2.y - v1.y * v2.x;
    x = newX;
    y = newY;
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

  public int getStateDimension() {
    return 3;
  }
    
  public void mapStateFromArray(int start, double[] array) {
    x = array[start];
    y = array[start + 1];
    z = array[start + 2];
  }

  public void mapStateToArray(int start, double[] array) {
    array[start]     = x;
    array[start + 1] = y;
    array[start + 2] = z;
  }

  /** Abscissa. */
  protected double x;

  /** Ordinate. */
  protected double y;

  /** Height. */
  protected double z;

   private static final long serialVersionUID = 4115635019045864211L;

}
