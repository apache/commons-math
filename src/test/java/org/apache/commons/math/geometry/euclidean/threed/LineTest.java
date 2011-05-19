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
package org.apache.commons.math.geometry.euclidean.threed;

import org.apache.commons.math.geometry.euclidean.threed.Line;
import org.apache.commons.math.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

public class LineTest {

    @Test
    public void testContains() {
        Vector3D p1 = new Vector3D(0, 0, 1);
        Line l = new Line(p1, new Vector3D(0, 0, 1));
        Assert.assertTrue(l.contains(p1));
        Assert.assertTrue(l.contains(new Vector3D(1.0, p1, 0.3, l.getDirection())));
        Vector3D u = l.getDirection().orthogonal();
        Vector3D v = Vector3D.crossProduct(l.getDirection(), u);
        for (double alpha = 0; alpha < 2 * FastMath.PI; alpha += 0.3) {
            Assert.assertTrue(! l.contains(p1.add(new Vector3D(FastMath.cos(alpha), u,
                                                               FastMath.sin(alpha), v))));
        }
    }

    @Test
    public void testSimilar() {
        Vector3D p1  = new Vector3D (1.2, 3.4, -5.8);
        Vector3D p2  = new Vector3D (3.4, -5.8, 1.2);
        Line     lA  = new Line(p1, p2.subtract(p1));
        Line     lB  = new Line(p2, p1.subtract(p2));
        Assert.assertTrue(lA.isSimilarTo(lB));
        Assert.assertTrue(! lA.isSimilarTo(new Line(p1, lA.getDirection().orthogonal())));
    }

    @Test
    public void testPointDistance() {
        Line l = new Line(new Vector3D(0, 1, 1), new Vector3D(0, 1, 1));
        Assert.assertEquals(FastMath.sqrt(3.0 / 2.0), l.distance(new Vector3D(1, 0, 1)), 1.0e-10);
        Assert.assertEquals(0, l.distance(new Vector3D(0, -4, -4)), 1.0e-10);
    }

    @Test
    public void testLineDistance() {
        Line l = new Line(new Vector3D(0, 1, 1), new Vector3D(0, 1, 1));
        Assert.assertEquals(1.0,
                            l.distance(new Line(new Vector3D(1, 0, 1), Vector3D.PLUS_K)),
                            1.0e-10);
        Assert.assertEquals(0.5,
                            l.distance(new Line(new Vector3D(-0.5, 0, 0), new Vector3D(0, -1, -1))),
                            1.0e-10);
        Assert.assertEquals(0.0,
                            l.distance(l),
                            1.0e-10);
        Assert.assertEquals(0.0,
                            l.distance(new Line(new Vector3D(0, -4, -4), new Vector3D(0, -1, -1))),
                            1.0e-10);
    }

}
