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

import org.apache.commons.math4.geometry.Vector;

/**
 * This class implements vectors in a three-dimensional space.
 * @since 1.2
 */
public abstract class Vector3D implements Vector<Euclidean3D> {

    /** Get the abscissa of the vector.
     * @return abscissa of the vector
     * @see Cartesian3D#Cartesian3D(double, double, double)
     */
    public abstract double getX();

    /** Get the ordinate of the vector.
     * @return ordinate of the vector
     * @see Cartesian3D#Cartesian3D(double, double, double)
     */
    public abstract double getY();

    /** Get the height of the vector.
     * @return height of the vector
     * @see Cartesian3D#Cartesian3D(double, double, double)
     */
    public abstract double getZ();

}
