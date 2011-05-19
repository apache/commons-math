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

import org.apache.commons.math.geometry.partitioning.utilities.OrderedTuple;

/** This class holds segments information before they are connected.
 * @version $Revision$ $Date$
 */
class Segment implements Comparable<Segment> {

    /** Start point of the segment. */
    private final Point2D      start;

    /** End point of the segments. */
    private final Point2D      end;

    /** Line containing the segment. */
    private final Line         line;

    /** Sorting key. */
    private      OrderedTuple sortingKey;

    /** Build a segment.
     * @param start start point of the segment
     * @param end end point of the segment
     * @param line line containing the segment
     */
    public Segment(final Point2D start, final Point2D end, final Line line) {
        this.start  = start;
        this.end    = end;
        this.line   = line;
        sortingKey = (start == null) ?
                     new OrderedTuple(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY) :
                     new OrderedTuple(start.x, start.y);
    }

    /** Build a dummy segment.
     * <p>
     * The object built is not a real segment, only the sorting key is used to
     * allow searching in the neighborhood of a point. This is an horrible hack ...
     * </p>
     * @param start start point of the segment
     * @param dx abscissa offset from the start point
     * @param dy ordinate offset from the start point
     */
    public Segment(final Point2D start, final double dx, final double dy) {
        this.start = null;
        this.end   = null;
        this.line  = null;
        sortingKey = new OrderedTuple(start.x + dx, start.y + dy);
    }

    /** Get the start point of the segment.
     * @return start point of the segment
     */
    public Point2D getStart() {
        return start;
    }

    /** Get the end point of the segment.
     * @return end point of the segment
     */
    public Point2D getEnd() {
        return end;
    }

    /** Get the line containing the segment.
     * @return line containing the segment
     */
    public Line getLine() {
        return line;
    }

    /** {@inheritDoc} */
    public int compareTo(final Segment o) {
        return sortingKey.compareTo(o.sortingKey);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        } else if (other instanceof Segment) {
            return compareTo((Segment) other) == 0;
        } else {
            return false;
        }
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return start.hashCode() ^ end.hashCode() ^ line.hashCode() ^ sortingKey.hashCode();
    }

}
