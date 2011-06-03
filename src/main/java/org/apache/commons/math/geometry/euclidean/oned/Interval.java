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
package org.apache.commons.math.geometry.euclidean.oned;


/** This class represents a 1D interval.
 * @see IntervalsSet
 * @version $Id$
 * @since 3.0
 */
public class Interval {

    /** The lower bound of the interval. */
    private final double lower;

    /** The upper bound of the interval. */
    private final double upper;

    /** Simple constructor.
     * @param lower lower bound of the interval
     * @param upper upper bound of the interval
     */
    public Interval(final double lower, final double upper) {
        this.lower = lower;
        this.upper = upper;
    }

    /** Get the lower bound of the interval.
     * @return lower bound of the interval
     */
    public double getLower() {
        return lower;
    }

    /** Get the upper bound of the interval.
     * @return upper bound of the interval
     */
    public double getUpper() {
        return upper;
    }

    /** Get the length of the interval.
     * @return length of the interval
     */
    public double getLength() {
        return upper - lower;
    }

    /** Get the midpoint of the interval.
     * @return midpoint of the interval
     */
    public double getMidPoint() {
        return 0.5 * (lower + upper);
    }

}
