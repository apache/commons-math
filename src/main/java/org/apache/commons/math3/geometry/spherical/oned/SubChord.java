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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.partitioning.Side;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.util.Precision;

/** This class represents sub-hyperplane for {@link Chord}.
 * <p>Instances of this class are guaranteed to be immutable.</p>
 * @version $Id$
 * @since 3.3
 */
public class SubChord implements SubHyperplane<Sphere1D> {

    /** Underlying hyperplane. */
    private final Chord chord;

    /** Boundary angles. */
    private final List<Double> limits;

    /** Simple constructor.
     * @param chord underlying hyperplane
     */
    public SubChord(final Chord chord) {
        this.chord = chord;
        this.limits = new ArrayList<Double>();
        limits.add(chord.getStart());
        limits.add(chord.getEnd());
    }

    /** Simple constructor.
     * @param chord underlying hyperplane
     * @param limits limit angles
     */
    private SubChord(final Chord chord, final List<Double> limits) {
        this.chord = chord;
        this.limits = new ArrayList<Double>(limits);
    }

    /** {@inheritDoc} */
    public double getSize() {
        double sum = 0;
        for (int i = 0; i < limits.size(); i += 2) {
            sum += limits.get(i + 1) - limits.get(i);
        }
        return sum;
    }

    /** {@inheritDoc} */
    public Side side(final Hyperplane<Sphere1D> hyperplane) {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    public SplitSubHyperplane<Sphere1D> split(final Hyperplane<Sphere1D> hyperplane) {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    public SubHyperplane<Sphere1D> copySelf() {
        return new SubChord(chord, limits);
    }

    /** {@inheritDoc} */
    public Hyperplane<Sphere1D> getHyperplane() {
        return chord;
    }

    /** {@inheritDoc} */
    public boolean isEmpty() {
        return getSize() <= Precision.SAFE_MIN;
    }

    /** {@inheritDoc} */
    public SubHyperplane<Sphere1D> reunite(SubHyperplane<Sphere1D> other) {
        final List<Double> otherLimits = ((SubChord) other).limits;
        final List<Double> merged = new ArrayList<Double>(limits.size() + otherLimits.size());

        int i = 0;
        int j = 0;
        boolean inside = false;
        while (i < limits.size() || j < otherLimits.size()) {
            // TODO: implement merging loop
        }

        return new SubChord(chord, merged);

    }

}
