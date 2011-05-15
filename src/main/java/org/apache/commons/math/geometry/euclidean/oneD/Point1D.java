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
package org.apache.commons.math.geometry.euclidean.oneD;

import org.apache.commons.math.geometry.partitioning.Point;

/** This class represents a 1D point.
 * <p>Instances of this class are guaranteed to be immutable.</p>
 * @version $Revision$ $Date$
 */
public class Point1D implements Point {

    /** Point at 0.0 abscissa. */
    public static final Point1D ZERO = new Point1D(0.0);

    /** Point at 1.0 abscissa. */
    public static final Point1D ONE = new Point1D(1.0);

    /** Point at undefined (NaN) abscissa. */
    public static final Point1D UNDEFINED = new Point1D(Double.NaN);

    /** Abscissa of the point. */
    private double x;

    /** Simple constructor.
     * @param x abscissa of the point
     */
    public Point1D(final double x) {
        this.x = x;
    }

    /** Get the abscissa of the point.
     * @return abscissa of the point
     */
    public double getAbscissa() {
        return x;
    }

}
