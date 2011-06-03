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

import org.apache.commons.math.geometry.Vector;
import org.apache.commons.math.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math.geometry.euclidean.oned.Vector1D;
import org.apache.commons.math.geometry.partitioning.Embedding;
import org.apache.commons.math.util.FastMath;

/** The class represent lines in a three dimensional space.

 * <p>Each oriented line is intrinsically associated with an abscissa
 * wich is a coordinate on the line. The point at abscissa 0 is the
 * orthogonal projection of the origin on the line, another equivalent
 * way to express this is to say that it is the point of the line
 * which is closest to the origin. Abscissa increases in the line
 * direction.</p>

 * @version $Id$
 * @since 3.0
 */
public class Line implements Embedding<Euclidean3D, Euclidean1D> {

    /** Line direction. */
    private Vector3D direction;

    /** Line point closest to the origin. */
    private Vector3D zero;

    /** Build a line from a point and a direction.
     * @param p point belonging to the line (this can be any point)
     * @param direction direction of the line
     * @exception IllegalArgumentException if the direction norm is too small
     */
    public Line(final Vector3D p, final Vector3D direction) {
        reset(p, direction);
    }

    /** Reset the instance as if built from a point and a normal.
     * @param p point belonging to the line (this can be any point)
     * @param dir direction of the line
     * @exception IllegalArgumentException if the direction norm is too small
     */
    public void reset(final Vector3D p, final Vector3D dir) {
        final double norm = dir.getNorm();
        if (norm == 0.0) {
            throw new IllegalArgumentException("null norm");
        }
        this.direction = new Vector3D(1.0 / norm, dir);
        zero = new Vector3D(1.0, p, -Vector3D.dotProduct(p, this.direction), this.direction);
    }

    /** Get a line with reversed direction.
     * @return a new instance, with reversed direction
     */
    public Line revert() {
        return new Line(zero, direction.negate());
    }

    /** Get the normalized direction vector.
     * @return normalized direction vector
     */
    public Vector3D getDirection() {
        return direction;
    }

    /** Get the line point closest to the origin.
     * @return line point closest to the origin
     */
    public Vector3D getOrigin() {
        return zero;
    }

    /** Get the abscissa of a point with respect to the line.
     * <p>The abscissa is 0 if the projection of the point and the
     * projection of the frame origin on the line are the same
     * point.</p>
     * @param point point to check (must be a {@link Vector3D Vector3D}
     * instance)
     * @return abscissa of the point (really a
     * {org.apache.commons.math.geometry.euclidean.oned.Vector1D Vector1D} instance)
     */
    public Vector1D toSubSpace(final Vector<Euclidean3D> point) {
        Vector3D p3 = (Vector3D) point;
        return new Vector1D(Vector3D.dotProduct(p3.subtract(zero), direction));
    }

    /** Get one point from the line.
     * @param point desired abscissa for the point (must be a
     * {org.apache.commons.math.geometry.euclidean.oned.Vector1D Vector1D} instance)
     * @return one point belonging to the line, at specified abscissa
     * (really a {@link Vector3D Vector3D} instance)
     */
    public Vector3D toSpace(final Vector<Euclidean1D> point) {
        Vector1D p1 = (Vector1D) point;
        return new Vector3D(1.0, zero, p1.getX(), direction);
    }

    /** Check if the instance is similar to another line.
     * <p>Lines are considered similar if they contain the same
     * points. This does not mean they are equal since they can have
     * opposite directions.</p>
     * @param line line to which instance should be compared
     * @return true if the lines are similar
     */
    public boolean isSimilarTo(final Line line) {
        final double angle = Vector3D.angle(direction, line.direction);
        return ((angle < 1.0e-10) || (angle > (FastMath.PI - 1.0e-10))) && contains(line.zero);
    }

    /** Check if the instance contains a point.
     * @param p point to check
     * @return true if p belongs to the line
     */
    public boolean contains(final Vector3D p) {
        return distance(p) < 1.0e-10;
    }

    /** Compute the distance between the instance and a point.
     * @param p to check
     * @return distance between the instance and the point
     */
    public double distance(final Vector3D p) {
        final Vector3D d = p.subtract(zero);
        final Vector3D n = new Vector3D(1.0, d, -Vector3D.dotProduct(d, direction), direction);
        return n.getNorm();
    }

    /** Compute the shortest distance between the instance and another line.
     * @param line line to check agains the instance
     * @return shortest distance between the instance and the line
     */
    public double distance(final Line line) {

        final Vector3D normal = Vector3D.crossProduct(direction, line.direction);
        if (normal.getNorm() < 1.0e-10) {
            // lines are parallel
            return distance(line.zero);
        }

        // separating middle plane
        final Plane middle = new Plane(new Vector3D(0.5, zero, 0.5, line.zero), normal);

        // the lines are at the same distance on either side of the plane
        return 2 * FastMath.abs(middle.getOffset(zero));

    }

}
