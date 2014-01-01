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

import java.util.List;

import org.apache.commons.math3.geometry.partitioning.Side;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane.SplitSubHyperplane;
import org.apache.commons.math3.util.MathUtils;
import org.junit.Assert;
import org.junit.Test;

public class SubChordTest {

    @Test
    public void testSubChord() {

        SubChord subChord = new SubChord(new Chord(2.3, 5.7, 1.0e-10));
        Assert.assertEquals(3.4, subChord.getSize(), 1.0e-10);
        Assert.assertFalse(subChord.isEmpty());

        Assert.assertEquals(1,   subChord.getNbSubArcs());
        Assert.assertEquals(2.3, subChord.getStart(0), 1.0e-10);
        Assert.assertEquals(5.7, subChord.getEnd(0), 1.0e-10);
        Assert.assertEquals(1,   subChord.getSubArcs().size());
        Assert.assertEquals(2.3, subChord.getSubArcs().get(0).getInf(), 1.0e-10);
        Assert.assertEquals(5.7, subChord.getSubArcs().get(0).getSup(), 1.0e-10);

        // despite a deep copy is used, chord being immutable its copy returns the same instance
        Assert.assertTrue(subChord.getHyperplane() == subChord.copySelf().getHyperplane());

    }

    @Test
    public void testSideEmbedded() {

        SubChord s35 = new SubChord(new Chord(3.0, 5.0, 1.0e-10));
        SubChord s16 = new SubChord(new Chord(1.0, 6.0, 1.0e-10));

        Assert.assertEquals(Side.BOTH,  s16.side(s35.getHyperplane()));
        Assert.assertEquals(Side.BOTH,  s16.side(s35.getHyperplane().getReverse()));
        Assert.assertEquals(Side.MINUS, s35.side(s16.getHyperplane()));
        Assert.assertEquals(Side.PLUS,  s35.side(s16.getHyperplane().getReverse()));

    }

    @Test
    public void testSideOverlapping() {
        SubChord s35 = new SubChord(new Chord(3.0, 5.0, 1.0e-10));
        SubChord s46 = new SubChord(new Chord(4.0, 6.0, 1.0e-10));

        Assert.assertEquals(Side.BOTH,  s46.side(s35.getHyperplane()));
        Assert.assertEquals(Side.BOTH,  s46.side(s35.getHyperplane().getReverse()));
        Assert.assertEquals(Side.BOTH, s35.side(s46.getHyperplane()));
        Assert.assertEquals(Side.BOTH,  s35.side(s46.getHyperplane().getReverse()));
    }

    @Test
    public void testSideHyper() {
        Chord zeroLength = new Chord(2.0, 2.0, 1.0e-10);
        SubChord sub = new SubChord(zeroLength);
        Assert.assertTrue(sub.isEmpty());
        Assert.assertEquals(Side.HYPER,  sub.side(zeroLength));
    }

    @Test
    public void testSplitEmbedded() {

        SubChord s35 = new SubChord(new Chord(3.0, 5.0, 1.0e-10));
        SubChord s16 = new SubChord(new Chord(1.0, 6.0, 1.0e-10));

        SplitSubHyperplane<Sphere1D> split1 = s16.split(s35.getHyperplane());
        SubChord split1Plus  = (SubChord) split1.getPlus();
        SubChord split1Minus = (SubChord) split1.getMinus();
        Assert.assertEquals(3.0, split1Plus.getSize(), 1.0e-10);
        Assert.assertEquals(2,   split1Plus.getNbSubArcs());
        Assert.assertEquals(1.0, split1Plus.getStart(0), 1.0e-10);
        Assert.assertEquals(3.0, split1Plus.getEnd(0), 1.0e-10);
        Assert.assertEquals(5.0, split1Plus.getStart(1), 1.0e-10);
        Assert.assertEquals(6.0, split1Plus.getEnd(1), 1.0e-10);
        Assert.assertEquals(2.0, split1Minus.getSize(), 1.0e-10);
        Assert.assertEquals(1,   split1Minus.getNbSubArcs());
        Assert.assertEquals(3.0, split1Minus.getStart(0), 1.0e-10);
        Assert.assertEquals(5.0, split1Minus.getEnd(0), 1.0e-10);

        SplitSubHyperplane<Sphere1D> split2 = s16.split(s35.getHyperplane().getReverse());
        SubChord split2Plus  = (SubChord) split2.getPlus();
        SubChord split2Minus = (SubChord) split2.getMinus();
        Assert.assertEquals(2.0, split2Plus.getSize(), 1.0e-10);
        Assert.assertEquals(1,   split2Plus.getNbSubArcs());
        Assert.assertEquals(3.0, split2Plus.getStart(0), 1.0e-10);
        Assert.assertEquals(5.0, split2Plus.getEnd(0), 1.0e-10);
        Assert.assertEquals(3.0, split2Minus.getSize(), 1.0e-10);
        Assert.assertEquals(2,   split2Minus.getNbSubArcs());
        Assert.assertEquals(1.0, split2Minus.getStart(0), 1.0e-10);
        Assert.assertEquals(3.0, split2Minus.getEnd(0), 1.0e-10);
        Assert.assertEquals(5.0, split2Minus.getStart(1), 1.0e-10);
        Assert.assertEquals(6.0, split2Minus.getEnd(1), 1.0e-10);

        SplitSubHyperplane<Sphere1D> split3 = s35.split(s16.getHyperplane());
        SubChord split3Plus  = (SubChord) split3.getPlus();
        SubChord split3Minus = (SubChord) split3.getMinus();
        Assert.assertNull(split3Plus);
        Assert.assertEquals(2.0, split3Minus.getSize(), 1.0e-10);
        Assert.assertEquals(1,   split3Minus.getNbSubArcs());
        Assert.assertEquals(3.0, split3Minus.getStart(0), 1.0e-10);
        Assert.assertEquals(5.0, split3Minus.getEnd(0), 1.0e-10);

        SplitSubHyperplane<Sphere1D> split4 = s35.split(s16.getHyperplane().getReverse());
        SubChord split4Plus  = (SubChord) split4.getPlus();
        SubChord split4Minus = (SubChord) split4.getMinus();
        Assert.assertEquals(2.0, split4Plus.getSize(), 1.0e-10);
        Assert.assertEquals(1,   split4Plus.getNbSubArcs());
        Assert.assertEquals(3.0, split4Plus.getStart(0), 1.0e-10);
        Assert.assertEquals(5.0, split4Plus.getEnd(0), 1.0e-10);
        Assert.assertNull(split4Minus);

    }

    @Test
    public void testSplitOverlapping() {

        SubChord s35 = new SubChord(new Chord(3.0, 5.0, 1.0e-10));
        SubChord s46 = new SubChord(new Chord(4.0, 6.0, 1.0e-10));

        SplitSubHyperplane<Sphere1D> split1 = s46.split(s35.getHyperplane());
        SubChord split1Plus  = (SubChord) split1.getPlus();
        SubChord split1Minus = (SubChord) split1.getMinus();
        Assert.assertEquals(1.0, split1Plus.getSize(), 1.0e-10);
        Assert.assertEquals(1,   split1Plus.getNbSubArcs());
        Assert.assertEquals(5.0, split1Plus.getStart(0), 1.0e-10);
        Assert.assertEquals(6.0, split1Plus.getEnd(0), 1.0e-10);
        Assert.assertEquals(1.0, split1Minus.getSize(), 1.0e-10);
        Assert.assertEquals(1,   split1Minus.getNbSubArcs());
        Assert.assertEquals(4.0, split1Minus.getStart(0), 1.0e-10);
        Assert.assertEquals(5.0, split1Minus.getEnd(0), 1.0e-10);

        SplitSubHyperplane<Sphere1D> split2 = s46.split(s35.getHyperplane().getReverse());
        SubChord split2Plus  = (SubChord) split2.getPlus();
        SubChord split2Minus = (SubChord) split2.getMinus();
        Assert.assertEquals(1.0, split2Plus.getSize(), 1.0e-10);
        Assert.assertEquals(1,   split2Plus.getNbSubArcs());
        Assert.assertEquals(4.0, split2Plus.getStart(0), 1.0e-10);
        Assert.assertEquals(5.0, split2Plus.getEnd(0), 1.0e-10);
        Assert.assertEquals(1.0, split2Minus.getSize(), 1.0e-10);
        Assert.assertEquals(1,   split2Minus.getNbSubArcs());
        Assert.assertEquals(5.0, split2Minus.getStart(0), 1.0e-10);
        Assert.assertEquals(6.0, split2Minus.getEnd(0), 1.0e-10);

        SplitSubHyperplane<Sphere1D> split3 = s35.split(s46.getHyperplane());
        SubChord split3Plus  = (SubChord) split3.getPlus();
        SubChord split3Minus = (SubChord) split3.getMinus();
        Assert.assertEquals(1.0, split3Plus.getSize(), 1.0e-10);
        Assert.assertEquals(1,   split3Plus.getNbSubArcs());
        Assert.assertEquals(3.0, split3Plus.getStart(0), 1.0e-10);
        Assert.assertEquals(4.0, split3Plus.getEnd(0), 1.0e-10);
        Assert.assertEquals(1.0, split3Minus.getSize(), 1.0e-10);
        Assert.assertEquals(1,   split3Minus.getNbSubArcs());
        Assert.assertEquals(4.0, split3Minus.getStart(0), 1.0e-10);
        Assert.assertEquals(5.0, split3Minus.getEnd(0), 1.0e-10);

        SplitSubHyperplane<Sphere1D> split4 = s35.split(s46.getHyperplane().getReverse());
        SubChord split4Plus  = (SubChord) split4.getPlus();
        SubChord split4Minus = (SubChord) split4.getMinus();
        Assert.assertEquals(1.0, split4Plus.getSize(), 1.0e-10);
        Assert.assertEquals(1,   split4Plus.getNbSubArcs());
        Assert.assertEquals(4.0, split4Plus.getStart(0), 1.0e-10);
        Assert.assertEquals(5.0, split4Plus.getEnd(0), 1.0e-10);
        Assert.assertEquals(1.0, split4Minus.getSize(), 1.0e-10);
        Assert.assertEquals(1,   split4Minus.getNbSubArcs());
        Assert.assertEquals(3.0, split4Minus.getStart(0), 1.0e-10);
        Assert.assertEquals(4.0, split4Minus.getEnd(0), 1.0e-10);

    }

    @Test
    public void testReunite() {

        // build sub-chord with arcs: [0.5, 1.375], [1.5, 1.75], [2.25, 4.5]
        SubChord sA1 = new SubChord(new Chord(0.5, 4.5, 1.0e-10));
        SubChord sA2 = (SubChord) sA1.split(new Chord(1.375, 1.5, 1.0e-10)).getPlus();
        SubChord sA  = (SubChord) sA2.split(new Chord(1.75, 2.25, 1.0e-10)).getPlus();
        List<Arc> listSa = sA.getSubArcs();
        Assert.assertEquals(3, listSa.size());
        Assert.assertEquals(0.5,   listSa.get(0).getInf(), 1.0e-10);
        Assert.assertEquals(1.375, listSa.get(0).getSup(), 1.0e-10);
        Assert.assertEquals(1.5,   listSa.get(1).getInf(), 1.0e-10);
        Assert.assertEquals(1.75,  listSa.get(1).getSup(), 1.0e-10);
        Assert.assertEquals(2.25,  listSa.get(2).getInf(), 1.0e-10);
        Assert.assertEquals(4.5,   listSa.get(2).getSup(), 1.0e-10);

        // sub-chord with arcs: [5.0, 5.5], [5.75, 1.0+2pi], [ 1.625+2pi, 1.875+2pi], [2.0+2pi, 2.125+2pi], [2.5+2pi, 3.5+2pi]
        SubChord sB1 = new SubChord(new Chord(5.0, 3.5 + MathUtils.TWO_PI, 1.0e-10));
        SubChord sB2 = (SubChord) sB1.split(new Chord(5.5, 5.75, 1.0e-10)).getPlus();
        SubChord sB3 = (SubChord) sB2.split(new Chord(1.0 + MathUtils.TWO_PI, 1.625 + MathUtils.TWO_PI, 1.0e-10)).getPlus();
        SubChord sB4 = (SubChord) sB3.split(new Chord(1.875 + MathUtils.TWO_PI, 2.0 + MathUtils.TWO_PI, 1.0e-10)).getPlus();
        SubChord sB  = (SubChord) sB4.split(new Chord(2.125 + MathUtils.TWO_PI, 2.5 + MathUtils.TWO_PI, 1.0e-10)).getPlus();
        List<Arc> listSb = sB.getSubArcs();
        Assert.assertEquals(5, listSb.size());
        Assert.assertEquals(5.0,                       listSb.get(0).getInf(), 1.0e-10);
        Assert.assertEquals(5.5,                       listSb.get(0).getSup(), 1.0e-10);
        Assert.assertEquals(5.75,                      listSb.get(1).getInf(), 1.0e-10);
        Assert.assertEquals(1.0   + MathUtils.TWO_PI,  listSb.get(1).getSup(), 1.0e-10);
        Assert.assertEquals(1.625 + MathUtils.TWO_PI,  listSb.get(2).getInf(), 1.0e-10);
        Assert.assertEquals(1.875 + MathUtils.TWO_PI,  listSb.get(2).getSup(), 1.0e-10);
        Assert.assertEquals(2.0   + MathUtils.TWO_PI,  listSb.get(3).getInf(), 1.0e-10);
        Assert.assertEquals(2.125 + MathUtils.TWO_PI,  listSb.get(3).getSup(), 1.0e-10);
        Assert.assertEquals(2.5   + MathUtils.TWO_PI,  listSb.get(4).getInf(), 1.0e-10);
        Assert.assertEquals(3.5   + MathUtils.TWO_PI,  listSb.get(4).getSup(), 1.0e-10);

        List<Arc> listAB = sA.reunite(sB).getSubArcs();
        Assert.assertEquals(5, listAB.size());
        Assert.assertEquals(5.75 - MathUtils.TWO_PI,   listAB.get(0).getInf(), 1.0e-10);
        Assert.assertEquals(1.375,                     listAB.get(0).getSup(), 1.0e-10);
        Assert.assertEquals(1.5,                       listAB.get(1).getInf(), 1.0e-10);
        Assert.assertEquals(1.875,                     listAB.get(1).getSup(), 1.0e-10);
        Assert.assertEquals(2.0,                       listAB.get(2).getInf(), 1.0e-10);
        Assert.assertEquals(2.125,                     listAB.get(2).getSup(), 1.0e-10);
        Assert.assertEquals(2.25,                      listAB.get(3).getInf(), 1.0e-10);
        Assert.assertEquals(4.5,                       listAB.get(3).getSup(), 1.0e-10);
        Assert.assertEquals(5.0,                       listAB.get(4).getInf(), 1.0e-10);
        Assert.assertEquals(5.5,                       listAB.get(4).getSup(), 1.0e-10);

        List<Arc> listBA  = sB.reunite(sA).getSubArcs();
        Assert.assertEquals(5, listBA.size());
        Assert.assertEquals(5.0,                       listBA.get(0).getInf(), 1.0e-10);
        Assert.assertEquals(5.5,                       listBA.get(0).getSup(), 1.0e-10);
        Assert.assertEquals(5.75,                      listBA.get(1).getInf(), 1.0e-10);
        Assert.assertEquals(1.375 + MathUtils.TWO_PI,  listBA.get(1).getSup(), 1.0e-10);
        Assert.assertEquals(1.5   + MathUtils.TWO_PI,  listBA.get(2).getInf(), 1.0e-10);
        Assert.assertEquals(1.875 + MathUtils.TWO_PI,  listBA.get(2).getSup(), 1.0e-10);
        Assert.assertEquals(2.0   + MathUtils.TWO_PI,  listBA.get(3).getInf(), 1.0e-10);
        Assert.assertEquals(2.125 + MathUtils.TWO_PI,  listBA.get(3).getSup(), 1.0e-10);
        Assert.assertEquals(2.25  + MathUtils.TWO_PI,  listBA.get(4).getInf(), 1.0e-10);
        Assert.assertEquals(4.5   + MathUtils.TWO_PI,  listBA.get(4).getSup(), 1.0e-10);

        // special cases
        List<Arc> listAEmpty  = sA.reunite(new SubChord(new Chord(0, 0, 1.0e-10))).getSubArcs();
        Assert.assertEquals(3, listAEmpty.size());
        Assert.assertEquals(0.5,   listAEmpty.get(0).getInf(), 1.0e-10);
        Assert.assertEquals(1.375, listAEmpty.get(0).getSup(), 1.0e-10);
        Assert.assertEquals(1.5,   listAEmpty.get(1).getInf(), 1.0e-10);
        Assert.assertEquals(1.75,  listAEmpty.get(1).getSup(), 1.0e-10);
        Assert.assertEquals(2.25,  listAEmpty.get(2).getInf(), 1.0e-10);
        Assert.assertEquals(4.5,   listAEmpty.get(2).getSup(), 1.0e-10);
        
        List<Arc> listEmptyA  = new SubChord(new Chord(0, 0, 1.0e-10)).reunite(sA).getSubArcs();
        Assert.assertEquals(3, listAEmpty.size());
        Assert.assertEquals(0.5,   listEmptyA.get(0).getInf(), 1.0e-10);
        Assert.assertEquals(1.375, listEmptyA.get(0).getSup(), 1.0e-10);
        Assert.assertEquals(1.5,   listEmptyA.get(1).getInf(), 1.0e-10);
        Assert.assertEquals(1.75,  listEmptyA.get(1).getSup(), 1.0e-10);
        Assert.assertEquals(2.25,  listEmptyA.get(2).getInf(), 1.0e-10);
        Assert.assertEquals(4.5,   listEmptyA.get(2).getSup(), 1.0e-10);
        
    }

    @Test
    public void testReuniteFullCircle() {
        SubChord s1 = new SubChord(new Chord(0, 4, 1.0e-10));
        SubChord s2 = new SubChord(new Chord(3, 7, 1.0e-10));
        Assert.assertEquals(MathUtils.TWO_PI, s1.reunite(s2).getSize(), 1.0e-10);
    }

}
