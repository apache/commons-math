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
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
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
     * @param limits limit angles (the list will be copied into a new independent list)
     */
    private SubChord(final Chord chord, final List<Double> limits) {
        this.chord = chord;
        this.limits = new ArrayList<Double>(limits);
    }

    /** Get the sub-arcs.
     * @return a newly created list with sub-arcs
     */
    public List<Arc> getSubArcs() {
        final List<Arc> subArcs = new ArrayList<Arc>(limits.size() / 2);
        for (int i = 0; i < limits.size(); i += 2) {
            subArcs.add(new Arc(limits.get(i), limits.get(i + 1)));
        }
        return subArcs;
    }

    /** Get the number of sub-arcs.
     * @return number of sub-arcs
     */
    public int getNbSubArcs() {
        return limits.size() / 2;
    }

    /** Get the start of the i<sup>th</sup> sub-arc.
     * @param i index of the desired arc (counting from 0)
     * @return start of the i<sup>th</sup> sub-arc
     */
    public double getStart(final int i) {
        return limits.get(2 * i);
    }

    /** Get the end of the i<sup>th</sup> sub-arc.
     * @param i index of the desired arc (counting from 0)
     * @return end of the i<sup>th</sup> sub-arc
     */
    public double getEnd(final int i) {
        return limits.get(2 * i + 1);
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

        final Chord  testChord   = (Chord) hyperplane;
        final double reference   = FastMath.PI + testChord.getStart();
        final double chordLength = testChord.getEnd() - testChord.getStart();

        boolean inMinus = false;
        boolean inPlus  = false;
        for (int i = 0; i < limits.size(); i += 2) {
            final double syncedStart = MathUtils.normalizeAngle(limits.get(i), reference) - testChord.getStart();
            final double chordOffset = limits.get(i) - syncedStart;
            final double syncedEnd   = limits.get(i + 1) - chordOffset;
            if (syncedStart < chordLength || syncedEnd > MathUtils.TWO_PI) {
                inMinus = true;
            }
            if (syncedEnd > chordLength) {
                inPlus = true;
            }
        }

        if (inMinus) {
            if (inPlus) {
                return Side.BOTH;
            } else {
                return Side.MINUS;
            }
        } else {
            if (inPlus) {
                return Side.PLUS;
            } else {
                return Side.HYPER;
            }
        }

    }

    /** {@inheritDoc} */
    public SplitSubHyperplane<Sphere1D> split(final Hyperplane<Sphere1D> hyperplane) {

        final List<Double> minus = new ArrayList<Double>(limits.size());
        final List<Double> plus  = new ArrayList<Double>(limits.size());

        final Chord  testChord   = (Chord) hyperplane;
        final double reference   = FastMath.PI + testChord.getStart();
        final double chordLength = testChord.getEnd() - testChord.getStart();

        for (int i = 0; i < limits.size(); i += 2) {
            final double syncedStart = MathUtils.normalizeAngle(limits.get(i), reference) - testChord.getStart();
            final double chordOffset = limits.get(i) - syncedStart;
            final double syncedEnd   = limits.get(i + 1) - chordOffset;
            if (syncedStart < chordLength) {
                // the start point limits.get(i) is in the minus part of the chord
                minus.add(limits.get(i));
                if (syncedEnd > chordLength) {
                    // the end point limits.get(i + 1) is past the end of the chord
                    // so we leave the minus part and enter the plus part
                    final double minusToPlus = chordLength + chordOffset;
                    minus.add(minusToPlus);
                    plus.add(minusToPlus);
                    if (syncedEnd > MathUtils.TWO_PI) {
                        // in fact the end point limits.get(i + 1) goes far enough that we
                        // leave the plus part of the chord and enter the minus part again
                        final double plusToMinus = MathUtils.TWO_PI + chordOffset;
                        plus.add(plusToMinus);
                        minus.add(plusToMinus);
                        minus.add(limits.get(i + 1));
                    } else {
                        // the end point limits.get(i + 1) is in the plus part of the chord
                        plus.add(limits.get(i + 1));
                    }
                } else {
                    // the end point limits.get(i + 1) is in the minus part of the chord
                    minus.add(limits.get(i + 1));
                }
            } else {
                // the start point limits.get(i) is in the plus part of the chord
                plus.add(limits.get(i));
                if (syncedEnd > MathUtils.TWO_PI) {
                    // the end point limits.get(i + 1) wraps around to the start of the chord
                    // so we leave the plus part and enter the minus part
                    final double plusToMinus = MathUtils.TWO_PI + chordOffset;
                    plus.add(plusToMinus);
                    minus.add(plusToMinus);
                    if (syncedEnd > MathUtils.TWO_PI + chordLength) {
                        // in fact the end point limits.get(i + 1) goes far enough that we
                        // leave the minus part of the chord and enter the plus part again
                        final double minusToPlus = MathUtils.TWO_PI + chordLength + chordOffset;
                        minus.add(minusToPlus);
                        plus.add(minusToPlus);
                        plus.add(limits.get(i + 1));
                    } else {
                        // the end point limits.get(i + 1) is in the minus part of the chord
                        minus.add(limits.get(i + 1));
                    }
                } else {
                    // the end point limits.get(i + 1) is in the plus part of the chord
                    plus.add(limits.get(i + 1));
                }
            }
        }

        return new SplitSubHyperplane<Sphere1D>(plus.isEmpty()  ? null : new SubChord(chord, plus),
                                                minus.isEmpty() ? null : new SubChord(chord, minus));

    }

    /** {@inheritDoc} */
    public SubChord copySelf() {
        return new SubChord(chord.copySelf(), limits);
    }

    /** {@inheritDoc} */
    public Chord getHyperplane() {
        return chord;
    }

    /** {@inheritDoc} */
    public boolean isEmpty() {
        return getSize() <= Precision.SAFE_MIN;
    }

    /** {@inheritDoc} */
    public SubChord reunite(SubHyperplane<Sphere1D> other) {

        final List<Double> otherLimits = ((SubChord) other).limits;

        final List<Double> merged;
        if (other.isEmpty()) {
            merged = limits;
        } else if (isEmpty()) {
            merged = otherLimits;
        } else {

            merged = new ArrayList<Double>(limits.size() + otherLimits.size());
            final double reference = limits.get(0) + FastMath.PI;

            // initialize loop on first limits list
            int i    = 0;
            int iEnd = limits.size() - 1;
            boolean enteringI = true;

            // initialize loop on second limits list
            int j             =  otherLimits.size() - 1;
            double angleAfter = Double.POSITIVE_INFINITY;
            for (int jSearch = 0; jSearch < otherLimits.size(); ++jSearch) {
                // look for the first angle in the second list that lies just after first limits start
                final double angleJ = MathUtils.normalizeAngle(otherLimits.get(jSearch), reference);
                if (angleJ < angleAfter) {
                    j          = jSearch;
                    angleAfter = angleJ;
                }
            }
            int jEnd = (j + otherLimits.size() - 1) % otherLimits.size();
            boolean enteringJ = j % 2 == 0;

            // perform merging loop
            boolean inMerged  = !enteringJ;
            double angleI = MathUtils.normalizeAngle(limits.get(i),      reference);
            double angleJ = MathUtils.normalizeAngle(otherLimits.get(j), reference);
            while (i >= 0 || j >= 0) {

                if (i >= 0 && (j < 0 || angleI <= angleJ)) {
                    if (inMerged && (!enteringI) && enteringJ) {
                        // we were in a merged arc and exit from it
                        merged.add(angleI);
                        inMerged = false;
                    } else if (!inMerged && enteringI) {
                        // we were outside and enter into a merged arc
                        merged.add(angleI);
                        inMerged = true;
                    }
                    if (i == iEnd) {
                        i = -1;
                    } else {
                        ++i;
                        angleI = MathUtils.normalizeAngle(limits.get(i), reference);
                    }
                    enteringI = !enteringI;
                } else {
                    if (inMerged) {
                        if (enteringI && !enteringJ) {
                            // we were in a merged arc and exit from it
                            merged.add(angleJ);
                            inMerged = false;
                        }
                    } else {
                        if (enteringJ) {
                            // we were outside and enter into a merged arc
                            merged.add(angleJ);
                            inMerged = true;
                        }
                    }
                    if (j == jEnd) {
                        j = -1;
                    } else {
                        j      = (j + 1) % otherLimits.size();
                        angleJ = MathUtils.normalizeAngle(otherLimits.get(j), reference);
                    }
                    enteringJ = !enteringJ;
                }

            }

            if (inMerged) {
                // we end the merging loop inside a merged arc,
                // we have to put its start at the front of the limits list
                if (merged.isEmpty()) {
                    // the merged arc covers all the circle
                    merged.add(0.0);
                    merged.add(MathUtils.TWO_PI);
                } else {
                    double previousAngle = merged.get(merged.size() - 1) - MathUtils.TWO_PI;
                    for (int k = 0; k < merged.size() - 1; ++k) {
                        final double tmp = merged.get(k);
                        merged.set(k, previousAngle);
                        previousAngle = tmp;
                    }
                    merged.set(merged.size() - 1, previousAngle);
                }
            }

        }

        return new SubChord(chord, merged);

    }

}
