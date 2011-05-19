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
package org.apache.commons.math.geometry.euclidean.twod;

import org.apache.commons.math.geometry.partitioning.Point;
import org.apache.commons.math.geometry.partitioning.SubSpace;

/** This class represents a 2D point.
 * <p>Instances of this class are guaranteed to be immutable.</p>
 * @version $Revision$ $Date$
 */
public class Point2D extends java.awt.geom.Point2D.Double implements Point, SubSpace {

    /** Point at undefined (NaN) coordinates. */
    public static final Point2D UNDEFINED = new Point2D(java.lang.Double.NaN, java.lang.Double.NaN);

    /** Serializable UID. */
    private static final long serialVersionUID = 8883702098988517151L;

    /** Build a point with default coordinates.
     */
    public Point2D() {
    }

    /** Build a point from its coordinates.
     * @param x abscissa
     * @param y ordinate
     */
    public Point2D(final double x, final double y) {
        super(x, y);
    }

    /** Build a point from a java awt point.
     * @param point java awt point
     */
    public Point2D(final java.awt.geom.Point2D.Double point) {
        super(point.x, point.y);
    }

    /** Transform a 2D space point into a sub-space point.
     * @param point 2D point of the space
     * @return always return null
     * @see #toSpace
     */
    public Point toSubSpace(final Point point) {
        return null;
    }

    /** Transform a sub-space point into a space point.
     * @param point ignored parameter
     * @return always return the instance
     * @see #toSubSpace
     */
    public Point toSpace(final Point point) {
        return this;
    }

}
