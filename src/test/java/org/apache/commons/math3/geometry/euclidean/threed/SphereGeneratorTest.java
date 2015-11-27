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
package org.apache.commons.math3.geometry.euclidean.threed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.geometry.enclosing.EnclosingBall;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.UnitSphereRandomVectorGenerator;
import org.apache.commons.math3.random.Well1024a;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;


public class SphereGeneratorTest {

    @Test
    public void testSupport0Point() {
        List<Vector3D> support = Arrays.asList(new Vector3D[0]);
        EnclosingBall<Euclidean3D, Vector3D> sphere = new SphereGenerator().ballOnSupport(support);
        Assert.assertTrue(sphere.getRadius() < 0);
        Assert.assertEquals(0, sphere.getSupportSize());
        Assert.assertEquals(0, sphere.getSupport().length);
    }

    @Test
    public void testSupport1Point() {
        List<Vector3D> support = Arrays.asList(new Vector3D(1, 2, 3));
        EnclosingBall<Euclidean3D, Vector3D> sphere = new SphereGenerator().ballOnSupport(support);
        Assert.assertEquals(0.0, sphere.getRadius(), 1.0e-10);
        Assert.assertTrue(sphere.contains(support.get(0)));
        Assert.assertTrue(sphere.contains(support.get(0), 0.5));
        Assert.assertFalse(sphere.contains(new Vector3D(support.get(0).getX() + 0.1,
                                                        support.get(0).getY() + 0.1,
                                                        support.get(0).getZ() + 0.1),
                                           0.001));
        Assert.assertTrue(sphere.contains(new Vector3D(support.get(0).getX() + 0.1,
                                                       support.get(0).getY() + 0.1,
                                                       support.get(0).getZ() + 0.1),
                                          0.5));
        Assert.assertEquals(0, support.get(0).distance(sphere.getCenter()), 1.0e-10);
        Assert.assertEquals(1, sphere.getSupportSize());
        Assert.assertTrue(support.get(0) == sphere.getSupport()[0]);
    }

    @Test
    public void testSupport2Points() {
        List<Vector3D> support = Arrays.asList(new Vector3D(1, 0, 0),
                                               new Vector3D(3, 0, 0));
        EnclosingBall<Euclidean3D, Vector3D> sphere = new SphereGenerator().ballOnSupport(support);
        Assert.assertEquals(1.0, sphere.getRadius(), 1.0e-10);
        int i = 0;
        for (Vector3D v : support) {
            Assert.assertTrue(sphere.contains(v));
            Assert.assertEquals(1.0, v.distance(sphere.getCenter()), 1.0e-10);
            Assert.assertTrue(v == sphere.getSupport()[i++]);
        }
        Assert.assertTrue(sphere.contains(new Vector3D(2, 0.9, 0)));
        Assert.assertFalse(sphere.contains(Vector3D.ZERO));
        Assert.assertEquals(0.0, new Vector3D(2, 0, 0).distance(sphere.getCenter()), 1.0e-10);
        Assert.assertEquals(2, sphere.getSupportSize());
    }

    @Test
    public void testSupport3Points() {
        List<Vector3D> support = Arrays.asList(new Vector3D(1, 0, 0),
                                               new Vector3D(3, 0, 0),
                                               new Vector3D(2, 2, 0));
        EnclosingBall<Euclidean3D, Vector3D> sphere = new SphereGenerator().ballOnSupport(support);
        Assert.assertEquals(5.0 / 4.0, sphere.getRadius(), 1.0e-10);
        int i = 0;
        for (Vector3D v : support) {
            Assert.assertTrue(sphere.contains(v));
            Assert.assertEquals(5.0 / 4.0, v.distance(sphere.getCenter()), 1.0e-10);
            Assert.assertTrue(v == sphere.getSupport()[i++]);
        }
        Assert.assertTrue(sphere.contains(new Vector3D(2, 0.9, 0)));
        Assert.assertFalse(sphere.contains(new Vector3D(0.9,  0, 0)));
        Assert.assertFalse(sphere.contains(new Vector3D(3.1,  0, 0)));
        Assert.assertTrue(sphere.contains(new Vector3D(2.0, -0.499, 0)));
        Assert.assertFalse(sphere.contains(new Vector3D(2.0, -0.501, 0)));
        Assert.assertTrue(sphere.contains(new Vector3D(2.0, 3.0 / 4.0, -1.249)));
        Assert.assertFalse(sphere.contains(new Vector3D(2.0, 3.0 / 4.0, -1.251)));
        Assert.assertEquals(0.0, new Vector3D(2.0, 3.0 / 4.0, 0).distance(sphere.getCenter()), 1.0e-10);
        Assert.assertEquals(3, sphere.getSupportSize());
    }

    @Test
    public void testSupport4Points() {
        List<Vector3D> support = Arrays.asList(new Vector3D(17, 14,  18),
                                               new Vector3D(11, 14,  22),
                                               new Vector3D( 2, 22,  17),
                                               new Vector3D(22, 11, -10));
        EnclosingBall<Euclidean3D, Vector3D> sphere = new SphereGenerator().ballOnSupport(support);
        Assert.assertEquals(25.0, sphere.getRadius(), 1.0e-10);
        int i = 0;
        for (Vector3D v : support) {
            Assert.assertTrue(sphere.contains(v));
            Assert.assertEquals(25.0, v.distance(sphere.getCenter()), 1.0e-10);
            Assert.assertTrue(v == sphere.getSupport()[i++]);
        }
        Assert.assertTrue(sphere.contains (new Vector3D(-22.999, 2, 2)));
        Assert.assertFalse(sphere.contains(new Vector3D(-23.001, 2, 2)));
        Assert.assertTrue(sphere.contains (new Vector3D( 26.999, 2, 2)));
        Assert.assertFalse(sphere.contains(new Vector3D( 27.001, 2, 2)));
        Assert.assertTrue(sphere.contains (new Vector3D(2, -22.999, 2)));
        Assert.assertFalse(sphere.contains(new Vector3D(2, -23.001, 2)));
        Assert.assertTrue(sphere.contains (new Vector3D(2,  26.999, 2)));
        Assert.assertFalse(sphere.contains(new Vector3D(2,  27.001, 2)));
        Assert.assertTrue(sphere.contains (new Vector3D(2, 2, -22.999)));
        Assert.assertFalse(sphere.contains(new Vector3D(2, 2, -23.001)));
        Assert.assertTrue(sphere.contains (new Vector3D(2, 2,  26.999)));
        Assert.assertFalse(sphere.contains(new Vector3D(2, 2,  27.001)));
        Assert.assertEquals(0.0, new Vector3D(2.0, 2.0, 2.0).distance(sphere.getCenter()), 1.0e-10);
        Assert.assertEquals(4, sphere.getSupportSize());
    }

    @Test
    public void testRandom() {
        final RandomGenerator random = new Well1024a(0xd015982e9f31ee04l);
        final UnitSphereRandomVectorGenerator sr = new UnitSphereRandomVectorGenerator(3, random);
        for (int i = 0; i < 100; ++i) {
            double d = 25 * random.nextDouble();
            double refRadius = 10 * random.nextDouble();
            Vector3D refCenter = new Vector3D(d, new Vector3D(sr.nextVector()));
            List<Vector3D> support = new ArrayList<Vector3D>();
            for (int j = 0; j < 5; ++j) {
                support.add(new Vector3D(1.0, refCenter, refRadius, new Vector3D(sr.nextVector())));
            }
            EnclosingBall<Euclidean3D, Vector3D> sphere = new SphereGenerator().ballOnSupport(support);
            Assert.assertEquals(0.0, refCenter.distance(sphere.getCenter()), 4e-7 * refRadius);
            Assert.assertEquals(refRadius, sphere.getRadius(), 1e-7 * refRadius);
        }

    }

    @Test
    public void testDegeneratedCase() {
       final List<Vector3D> support =
               Arrays.asList(new Vector3D(FastMath.scalb(-8039905610797991.0, -50),   //   -7.140870659936730
                                          FastMath.scalb(-4663475464714142.0, -48),   //  -16.567993074240455
                                          FastMath.scalb( 6592658872616184.0, -49)),  //   11.710914678204503
                             new Vector3D(FastMath.scalb(-8036658568968473.0, -50),   //   -7.137986707455888
                                          FastMath.scalb(-4664256346424880.0, -48),   //  -16.570767323375720
                                          FastMath.scalb( 6591357011730307.0, -49)),  //  11.708602108715928)
                             new Vector3D(FastMath.scalb(-8037820142977230.0, -50),   //   -7.139018392423351
                                          FastMath.scalb(-4665280434237813.0, -48),   //  -16.574405614157020
                                          FastMath.scalb( 6592435966112099.0, -49)),  //   11.710518716711425
                             new Vector3D(FastMath.scalb(-8038007803611611.0, -50),   //   -7.139185068549035
                                          FastMath.scalb(-4664291215918380.0, -48),   //  -16.570891204702250
                                          FastMath.scalb( 6595270610894208.0, -49))); //   11.715554057357394
        EnclosingBall<Euclidean3D, Vector3D> sphere = new SphereGenerator().ballOnSupport(support);

        // the following values have been computed using Emacs calc with exact arithmetic from the
        // rational representation corresponding to the scalb calls (i.e. -8039905610797991/2^50, ...)
        // The results were converted to decimal representation rounded to 1.0e-30 when writing the reference
        // values in this test
        Assert.assertEquals(  0.003616820213530053297575846168, sphere.getRadius(),        1.0e-20);
        Assert.assertEquals( -7.139325643360503322823511839511, sphere.getCenter().getX(), 1.0e-20);
        Assert.assertEquals(-16.571096474251747245361467833760, sphere.getCenter().getY(), 1.0e-20);
        Assert.assertEquals( 11.711945804096960876521111630800, sphere.getCenter().getZ(), 1.0e-20);

        for (Vector3D v : support) {
            Assert.assertTrue(sphere.contains(v, 1.0e-14));
        }

    }

}
