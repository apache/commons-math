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

import org.apache.commons.math3.util.MathUtils;
import org.junit.Assert;
import org.junit.Test;

public class ChordTest {

    @Test
    public void testChord() {
        Chord chord = new Chord(2.3, 5.7, 1.0e-10);
        Assert.assertEquals(2.3, chord.copySelf().getStart(), 1.0e-10);
        Assert.assertEquals(5.7, chord.copySelf().getEnd(), 1.0e-10);
        Assert.assertEquals(1.0e-10, chord.copySelf().getTolerance(),  1.0e-20);
        Assert.assertEquals(3.4, chord.wholeHyperplane().getSize(), 1.0e-10);
        checkOutside(chord,  2.25);
        checkBoundary(chord, 2.3);
        checkInside(chord,   2.35);
        checkInside(chord,  5.65);
        checkBoundary(chord, 5.7);
        checkOutside(chord,   5.75);
    }

    @Test
    public void testReverse() {
        Chord chord = new Chord(2.3, 5.7, 1.0e-10);
        Chord reversed = chord.getReverse();
        Assert.assertEquals(chord.getEnd(), reversed.getStart(), 1.0e-10);
        Assert.assertEquals(chord.getStart() + MathUtils.TWO_PI, reversed.getEnd(), 1.0e-10);
        Assert.assertEquals(1.0e-10, reversed.getTolerance(), 1.0e-20);
        Assert.assertEquals(MathUtils.TWO_PI - 3.4, reversed.wholeHyperplane().getSize(), 1.0e-10);
        Assert.assertFalse(chord.sameOrientationAs(reversed));
        checkInside(reversed,  2.25);
        checkBoundary(reversed, 2.3);
        checkOutside(reversed,   2.35);
        checkOutside(reversed,  5.65);
        checkBoundary(reversed, 5.7);
        checkInside(reversed,   5.75);
    }

    @Test
    public void testWholeHyperplane() {
        Chord chord = new Chord(2.3, 5.7, 1.0e-10);
        SubChord subChord = chord.wholeHyperplane();
        Assert.assertTrue(chord == subChord.getHyperplane());
        Assert.assertEquals(chord.getEnd() - chord.getStart(), subChord.getSize(), 1.0e-10);
    }

    @Test
    public void testWholeSpace() {
        Chord chord = new Chord(2.3, 5.7, 1.0e-10);
        ArcsSet set = chord.wholeSpace();
        Assert.assertEquals(1,   set.asList().size());
        Assert.assertEquals(0.0, set.asList().get(0).getInf(), 1.0e-10);
        Assert.assertEquals(MathUtils.TWO_PI, set.asList().get(0).getSup(), 1.0e-10);
    }

    private void checkInside(Chord chord, double alpha) {
        for (int i = -2; i < 3; ++i) {
            Assert.assertTrue(chord.getOffset(new S1Point(alpha + i * MathUtils.TWO_PI)) < 0.0);
        }
    }

    private void checkOutside(Chord chord, double alpha) {
        for (int i = -2; i < 3; ++i) {
            Assert.assertTrue(chord.getOffset(new S1Point(alpha + i * MathUtils.TWO_PI)) > 0.0);
        }
    }

    private void checkBoundary(Chord chord, double alpha) {
        for (int i = -2; i < 3; ++i) {
            Assert.assertEquals(0.0, chord.getOffset(new S1Point(alpha + i * MathUtils.TWO_PI)), chord.getTolerance());
        }
    }

}
