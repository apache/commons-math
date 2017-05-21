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
package org.apache.commons.math4.geometry.euclidean.oned;

import org.apache.commons.math4.geometry.Vector;

/** This class represents a 1D vector.
 *
 * @since 3.0
 */
public abstract class Vector1D implements Vector<Euclidean1D> {

    /** Get the abscissa of the vector.
     * @return abscissa of the vector
     * @see Cartesian1D#Cartesian1D(double)
     */
    public abstract double getX();

}
