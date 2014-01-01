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
package org.apache.commons.math3.geometry.spherical.oned;

import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.util.FastMath;

/** This class represents a 1D oriented hyperplane on the circle.
 * <p>An hyperplane on the 1-sphere is a chord that splits
 * the circle in two parts.</p>
 * <p>Instances of this class are guaranteed to be immutable.</p>
 * @version $Id$
 * @since 3.3
 */
public class Chord implements Hyperplane<Sphere1D> {

    /** Start angle of the chord. */
    private final double start;

    /** End angle of the chord. */
    private final double end;

    /** Cosine of the half aperture. */
    private final double cos;

    /** Middle point of the chord. */
    private final S1Point middle;

    /** Simple constructor.
     * @param start start angle of the chord
     * @param end end angle of the chord
     */
    public Chord(final double start, final double end) {
        this.start  = start;
        this.end    = end;
        this.middle = new S1Point(0.5 * (start + end));
        this.cos    = FastMath.cos(0.5 * (end - start));
    }

    /** Copy the instance.
     * <p>Since instances are immutable, this method directly returns
     * the instance.</p>
     * @return the instance itself
     */
    public Chord copySelf() {
        return this;
    }

    /** {@inheritDoc} */
    public double getOffset(final Point<Sphere1D> point) {
        return cos - middle.getVector().dotProduct(((S1Point) point).getVector());
    }

    /** Build a region covering the whole hyperplane.
     * <p>Since this class represent zero dimension spaces which does
     * not have lower dimension sub-spaces, this method returns a dummy
     * implementation of a {@link
     * org.apache.commons.math3.geometry.partitioning.SubHyperplane SubHyperplane}.
     * This implementation is only used to allow the {@link
     * org.apache.commons.math3.geometry.partitioning.SubHyperplane
     * SubHyperplane} class implementation to work properly, it should
     * <em>not</em> be used otherwise.</p>
     * @return a dummy sub hyperplane
     */
    public SubChord wholeHyperplane() {
        return new SubChord(this);
    }

    /** Build a region covering the whole space.
     * @return a region containing the instance (really an {@link
     * ArcsSet IntervalsSet} instance)
     */
    public ArcsSet wholeSpace() {
        return new ArcsSet();
    }

    /** {@inheritDoc} */
    public boolean sameOrientationAs(final Hyperplane<Sphere1D> other) {
        return middle.getVector().dotProduct(((Chord) other).middle.getVector()) >= 0.0;
    }

    /** Get the start angle of the chord.
     * @return start angle of the chord.
     */
    public double getStart() {
        return start;
    }

    /** Get the end angle of the chord.
     * @return end angle of the chord.
     */
    public double getEnd() {
        return end;
    }

}
