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

import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;
import org.junit.Assert;
import org.junit.Test;

public class ArcTest {

    @Test
    public void testArc() {
        Arc arc = new Arc(2.3, 5.7);
        Assert.assertEquals(3.4, arc.getSize(), 1.0e-10);
        Assert.assertEquals(4.0, arc.getBarycenter(), 1.0e-10);
        Assert.assertEquals(Region.Location.BOUNDARY, arc.checkPoint(2.3, 1.0e-10));
        Assert.assertEquals(Region.Location.BOUNDARY, arc.checkPoint(5.7, 1.0e-10));
        Assert.assertEquals(Region.Location.OUTSIDE,  arc.checkPoint(1.2, 1.0e-10));
        Assert.assertEquals(Region.Location.OUTSIDE,  arc.checkPoint(8.5, 1.0e-10));
        Assert.assertEquals(Region.Location.INSIDE,   arc.checkPoint(8.7, 1.0e-10));
        Assert.assertEquals(Region.Location.INSIDE,   arc.checkPoint(3.0, 1.0e-10));
        Assert.assertEquals(2.3, arc.getInf(), 1.0e-10);
        Assert.assertEquals(5.7, arc.getSup(), 1.0e-10);
        Assert.assertEquals(4.0, arc.getBarycenter(), 1.0e-10);
        Assert.assertEquals(3.4, arc.getSize(), 1.0e-10);
    }

    @Test
    public void testTolerance() {
        Arc arc = new Arc(2.3, 5.7);
        Assert.assertEquals(Region.Location.OUTSIDE,  arc.checkPoint(1.2, 1.0));
        Assert.assertEquals(Region.Location.BOUNDARY, arc.checkPoint(1.2, 1.2));
        Assert.assertEquals(Region.Location.OUTSIDE,  arc.checkPoint(6.5, 0.7));
        Assert.assertEquals(Region.Location.BOUNDARY, arc.checkPoint(6.5, 0.9));
        Assert.assertEquals(Region.Location.INSIDE,   arc.checkPoint(3.0, 0.6));
        Assert.assertEquals(Region.Location.BOUNDARY, arc.checkPoint(3.0, 0.8));
    }

    @Test
    public void testFullCircle() {
        Arc arc = new Arc(9.0, 9.0);
        // no boundaries on a full circle
        Assert.assertEquals(Region.Location.INSIDE, arc.checkPoint(9.0, 1.0e-10));
        Assert.assertEquals(9.0, arc.getInf(), 1.0e-10);
        Assert.assertEquals(9.0 + 2.0 * FastMath.PI, arc.getSup(), 1.0e-10);
        Assert.assertEquals(2.0 * FastMath.PI, arc.getSize(), 1.0e-10);
        for (double alpha = -20.0; alpha <= 20.0; alpha += 0.1) {
            Assert.assertEquals(Region.Location.INSIDE,
                                arc.checkPoint(alpha, 1.0e-10));
        }
    }

    @Test
    public void testSmall() {
        Arc arc = new Arc(1.0, FastMath.nextAfter(1.0, Double.POSITIVE_INFINITY));
        Assert.assertEquals(2 * Precision.EPSILON, arc.getSize(), Precision.SAFE_MIN);
        Assert.assertEquals(1.0, arc.getBarycenter(), Precision.EPSILON);
    }

}
