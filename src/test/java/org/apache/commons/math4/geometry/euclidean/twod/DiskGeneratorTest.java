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
package org.apache.commons.math4.geometry.euclidean.twod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math4.geometry.enclosing.EnclosingBall;
import org.apache.commons.math4.geometry.euclidean.twod.DiskGenerator;
import org.apache.commons.math4.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math4.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math4.random.RandomGenerator;
import org.apache.commons.math4.random.UnitSphereRandomVectorGenerator;
import org.apache.commons.math4.random.Well1024a;
import org.junit.Assert;
import org.junit.Test;


public class DiskGeneratorTest {

    @Test
    public void testSupport0Point() {
        List<Vector2D> support = Arrays.asList(new Vector2D[0]);
        EnclosingBall<Euclidean2D, Vector2D> disk = new DiskGenerator().ballOnSupport(support);
        Assert.assertTrue(disk.getRadius() < 0);
        Assert.assertEquals(0, disk.getSupportSize());
        Assert.assertEquals(0, disk.getSupport().length);
    }

    @Test
    public void testSupport1Point() {
        List<Vector2D> support = Arrays.asList(new Vector2D(1, 2));
        EnclosingBall<Euclidean2D, Vector2D> disk = new DiskGenerator().ballOnSupport(support);
        Assert.assertEquals(0.0, disk.getRadius(), 1.0e-10);
        Assert.assertTrue(disk.contains(support.get(0)));
        Assert.assertTrue(disk.contains(support.get(0), 0.5));
        Assert.assertFalse(disk.contains(new Vector2D(support.get(0).getX() + 0.1,
                                                      support.get(0).getY() - 0.1),
                                         0.001));
        Assert.assertTrue(disk.contains(new Vector2D(support.get(0).getX() + 0.1,
                                                     support.get(0).getY() - 0.1),
                                        0.5));
        Assert.assertEquals(0, support.get(0).distance(disk.getCenter()), 1.0e-10);
        Assert.assertEquals(1, disk.getSupportSize());
        Assert.assertTrue(support.get(0) == disk.getSupport()[0]);
    }

    @Test
    public void testSupport2Points() {
        List<Vector2D> support = Arrays.asList(new Vector2D(1, 0),
                                               new Vector2D(3, 0));
        EnclosingBall<Euclidean2D, Vector2D> disk = new DiskGenerator().ballOnSupport(support);
        Assert.assertEquals(1.0, disk.getRadius(), 1.0e-10);
        int i = 0;
        for (Vector2D v : support) {
            Assert.assertTrue(disk.contains(v));
            Assert.assertEquals(1.0, v.distance(disk.getCenter()), 1.0e-10);
            Assert.assertTrue(v == disk.getSupport()[i++]);
        }
        Assert.assertTrue(disk.contains(new Vector2D(2, 0.9)));
        Assert.assertFalse(disk.contains(Vector2D.ZERO));
        Assert.assertEquals(0.0, new Vector2D(2, 0).distance(disk.getCenter()), 1.0e-10);
        Assert.assertEquals(2, disk.getSupportSize());
    }

    @Test
    public void testSupport3Points() {
        List<Vector2D> support = Arrays.asList(new Vector2D(1, 0),
                                               new Vector2D(3, 0),
                                               new Vector2D(2, 2));
        EnclosingBall<Euclidean2D, Vector2D> disk = new DiskGenerator().ballOnSupport(support);
        Assert.assertEquals(5.0 / 4.0, disk.getRadius(), 1.0e-10);
        int i = 0;
        for (Vector2D v : support) {
            Assert.assertTrue(disk.contains(v));
            Assert.assertEquals(5.0 / 4.0, v.distance(disk.getCenter()), 1.0e-10);
            Assert.assertTrue(v == disk.getSupport()[i++]);
        }
        Assert.assertTrue(disk.contains(new Vector2D(2, 0.9)));
        Assert.assertFalse(disk.contains(new Vector2D(0.9,  0)));
        Assert.assertFalse(disk.contains(new Vector2D(3.1,  0)));
        Assert.assertTrue(disk.contains(new Vector2D(2.0, -0.499)));
        Assert.assertFalse(disk.contains(new Vector2D(2.0, -0.501)));
        Assert.assertEquals(0.0, new Vector2D(2.0, 3.0 / 4.0).distance(disk.getCenter()), 1.0e-10);
        Assert.assertEquals(3, disk.getSupportSize());
    }

    @Test
    public void testRandom() {
        final RandomGenerator random = new Well1024a(0x12faa818373ffe90l);
        final UnitSphereRandomVectorGenerator sr = new UnitSphereRandomVectorGenerator(2, random);
        for (int i = 0; i < 500; ++i) {
            double d = 25 * random.nextDouble();
            double refRadius = 10 * random.nextDouble();
            Vector2D refCenter = new Vector2D(d, new Vector2D(sr.nextVector()));
            List<Vector2D> support = new ArrayList<Vector2D>();
            for (int j = 0; j < 3; ++j) {
                support.add(new Vector2D(1.0, refCenter, refRadius, new Vector2D(sr.nextVector())));
            }
            EnclosingBall<Euclidean2D, Vector2D> disk = new DiskGenerator().ballOnSupport(support);
            Assert.assertEquals(0.0, refCenter.distance(disk.getCenter()), 3e-9 * refRadius);
            Assert.assertEquals(refRadius, disk.getRadius(), 7e-10 * refRadius);
        }

    }
}
