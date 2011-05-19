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
package org.apache.commons.math.geometry.euclidean.threed;

import org.apache.commons.math.geometry.partitioning.Point;

/** This class represents a 3D point.
 * <p>Instances of this class are guaranteed to be immutable.</p>
 * @version $Revision$ $Date$
 */
public class Point3D extends Vector3D implements Point {

    /** Point at undefined (NaN) coordinates. */
    public static final Point3D UNDEFINED = new Point3D(Double.NaN, Double.NaN, Double.NaN);

    /** Serializable UID. */
    private static final long serialVersionUID = 9128130934224884451L;

    /** Simple constructor.
     * Build a vector from its coordinates
     * @param x abscissa
     * @param y ordinate
     * @param z height
     * @see #getX()
     * @see #getY()
     * @see #getZ()
     */
    public Point3D(final double x, final double y, final double z) {
        super(x, y, z);
    }

    /** Simple constructor.
     * Build a vector from its azimuthal coordinates
     * @param alpha azimuth (&alpha;) around Z
     *              (0 is +X, &pi;/2 is +Y, &pi; is -X and 3&pi;/2 is -Y)
     * @param delta elevation (&delta;) above (XY) plane, from -&pi;/2 to +&pi;/2
     * @see #getAlpha()
     * @see #getDelta()
     */
    public Point3D(final double alpha, final double delta) {
        super(alpha, delta);
    }

    /** Multiplicative constructor
     * Build a vector from another one and a scale factor.
     * The vector built will be a * u
     * @param a scale factor
     * @param u base (unscaled) vector
     */
    public Point3D(final double a, final Vector3D u) {
        super(a, u);
    }

    /** Linear constructor
     * Build a vector from two other ones and corresponding scale factors.
     * The vector built will be a1 * u1 + a2 * u2
     * @param a1 first scale factor
     * @param u1 first base (unscaled) vector
     * @param a2 second scale factor
     * @param u2 second base (unscaled) vector
     */
    public Point3D(final double a1, final Vector3D u1, final double a2, final Vector3D u2) {
        super(a1, u1, a2, u2);
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
    public Point3D(final double a1, final Vector3D u1, final double a2, final Vector3D u2,
                   final double a3, final Vector3D u3) {
        super(a1, u1, a2, u2, a3, u3);
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
    public Point3D(final double a1, final Vector3D u1, final double a2, final Vector3D u2,
                   final double a3, final Vector3D u3, final double a4, final Vector3D u4) {
        super(a1, u1, a2, u2, a3, u3, a4, u4);
    }

}
