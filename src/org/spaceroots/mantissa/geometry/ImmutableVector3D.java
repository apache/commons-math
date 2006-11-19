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

/**
 * This class implements immutable vectors in a three-dimensional space.

 * @version $Id: ImmutableVector3D.java 1705 2006-09-17 19:57:39Z luc $
 * @author L. Maisonobe

 */

public class ImmutableVector3D
  extends Vector3D {

  /** Simple constructor.
   * Build a vector from its coordinates
   * @param x abscissa
   * @param y ordinate
   * @param z height
   */
  public ImmutableVector3D(double x, double y, double z) {
    super(x, y, z);
    computeNorm();
  }

  /** Simple constructor.
   * Build a vector from its azimuthal coordinates
   * @param alpha azimuth around Z
   *              (0 is +X, PI/2 is +Y, PI is -X and 3PI/2 is -Y)
   * @param delta elevation above (XY) plane, from -PI to +PI
   */
  public ImmutableVector3D(double alpha, double delta) {
    super(alpha, delta);
    computeNorm();
  }

  /** Copy constructor.
   * Build a copy of a vector
   * @param v vector to copy
   */
  public ImmutableVector3D(Vector3D v) {
    super(v);
    computeNorm();
  }

  /** Multiplicative constructor
   * Build a vector from another one and a scale factor. 
   * The vector built will be a * u
   * @param a scale factor
   * @param u base (unscaled) vector
   */
  public ImmutableVector3D(double a, Vector3D u) {
    super(a, u);
    computeNorm();
  }

  /** Linear constructor
   * Build a vector from two other ones and corresponding scale factors.
   * The vector built will be a * u +  b * v
   * @param a first scale factor
   * @param u first base (unscaled) vector
   * @param b second scale factor
   * @param v second base (unscaled) vector
   */
  public ImmutableVector3D(double a, Vector3D u, double b, Vector3D v) {
    super(a, u, b, v);
    computeNorm();
  }

  /** Set the abscissa of the vector.
   * This method should not be called for immutable vectors, it always
   * throws an <code>UnsupportedOperationException</code> exception
   * @param x new abscissa for the vector
   * @exception UnsupportedOperationException thrown in every case
   */
  public void setX(double x) {
    throw new UnsupportedOperationException("vector is immutable");
  }

  /** Set the ordinate of the vector.
   * This method should not be called for immutable vectors, it always
   * throws an <code>UnsupportedOperationException</code> exception
   * @param y new ordinate for the vector
   * @exception UnsupportedOperationException thrown in every case
   */
  public void setY(double y) {
    throw new UnsupportedOperationException("vector is immutable");
  }

  /** Set the height of the vector.
   * This method should not be called for immutable vectors, it always
   * throws an <code>UnsupportedOperationException</code> exception
   * @param z new height for the vector
   * @exception UnsupportedOperationException thrown in every case
   */
  public void setZ(double z) {
    throw new UnsupportedOperationException("vector is immutable");
  }

  /** Set all coordinates of the vector.
   * This method should not be called for immutable vectors, it always
   * throws an <code>UnsupportedOperationException</code> exception
   * @param x new abscissa for the vector
   * @param y new ordinate for the vector
   * @param z new height for the vector
   * @exception UnsupportedOperationException thrown in every case
   */
  public void setCoordinates(double x, double y, double z) {
    throw new UnsupportedOperationException("vector is immutable");
  }

  /** Compute the norm once and for all. */
  private void computeNorm() {
    norm = Math.sqrt(x * x + y * y + z * z);
  }

  /** Get the norm for the vector.
   * @return euclidian norm for the vector
   */
  public double getNorm() {
    return norm;
  }

  /** Add a vector to the instance.
   * This method should not be called for immutable vectors, it always
   * throws an <code>UnsupportedOperationException</code> exception
   * @param v vector to add
   * @exception UnsupportedOperationException thrown in every case
   */
  public void addToSelf(Vector3D v) {
    throw new UnsupportedOperationException("vector is immutable");
  }

  /** Subtract a vector from the instance.
   * This method should not be called for immutable vectors, it always
   * throws an <code>UnsupportedOperationException</code> exception
   * @param v vector to subtract
   * @exception UnsupportedOperationException thrown in every case
   */
  public void subtractFromSelf(Vector3D v) {
    throw new UnsupportedOperationException("vector is immutable");
  }

  /** Normalize the instance.
   * This method should not be called for immutable vectors, it always
   * throws an <code>UnsupportedOperationException</code> exception
   * @exception UnsupportedOperationException thrown in every case
   */
  public void normalizeSelf() {
    throw new UnsupportedOperationException("vector is immutable");
  }

  /** Revert the instance.
   * This method should not be called for immutable vectors, it always
   * throws an <code>UnsupportedOperationException</code> exception
   * @exception UnsupportedOperationException thrown in every case
   */
  public void negateSelf() {
    throw new UnsupportedOperationException("vector is immutable");
  }

  /** Multiply the instance by a scalar
   * This method should not be called for immutable vectors, it always
   * throws an <code>UnsupportedOperationException</code> exception
   * @param a scalar by which the instance should be multiplied
   * @exception UnsupportedOperationException thrown in every case
   */
  public void multiplySelf(double a) {
    throw new UnsupportedOperationException("vector is immutable");
  }

  /** Reinitialize internal state from the specified array slice data.
   * This method should not be called for immutable vectors, it always
   * throws an <code>UnsupportedOperationException</code> exception
   * @param start start index in the array
   * @param array array holding the data to extract
   * @exception UnsupportedOperationException thrown in every case
   */
  public void mapStateFromArray(int start, double[] array) {
    throw new UnsupportedOperationException("vector is immutable");
  }

  /** Norm of the vector. */
  private double norm;

  private static final long serialVersionUID = 5377895850033895270L;

}
