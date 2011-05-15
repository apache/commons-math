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
package org.apache.commons.math.geometry.partitioning;


/** This interface represents a sub-space of a space.

 * <p>Sub-spaces are the lower dimensions subsets of a n-dimensions
 * space. The (n-1)-dimension sub-spaces are specific sub-spaces known
 * as {@link Hyperplane hyperplanes}.</p>

 * <p>In the 3D euclidean space, hyperplanes are 2D planes, and the 1D
 * sub-spaces are lines.</p>

 * @see Hyperplane
 * @version $Revision$ $Date$
 */
public interface SubSpace {

    /** Transform a space point into a sub-space point.
     * @param point n-dimension point of the space
     * @return (n-1)-dimension point of the sub-space corresponding to
     * the specified space point
     * @see #toSpace
     */
    Point toSubSpace(Point point);

    /** Transform a sub-space point into a space point.
     * @param point (n-1)-dimension point of the sub-space
     * @return n-dimension point of the space corresponding to the
     * specified sub-space point
     * @see #toSubSpace
     */
    Point toSpace(Point point);

}
