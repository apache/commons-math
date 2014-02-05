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
package org.apache.commons.math3.geometry.spherical.twod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.geometry.enclosing.EnclosingBall;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.UnitSphereRandomVectorGenerator;
import org.apache.commons.math3.random.Well1024a;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.junit.Assert;
import org.junit.Test;


public class SphericalCapGeneratorTest {

    @Test
    public void testSupport0Point() {
        List<S2Point> support = Arrays.asList(new S2Point[0]);
        EnclosingBall<Sphere2D, S2Point> cap = new SphericalCapGenerator(Vector3D.MINUS_K).ballOnSupport(support);
        Assert.assertTrue(cap.getRadius() < 0);
        Assert.assertEquals(0, cap.getSupportSize());
        Assert.assertEquals(0, cap.getSupport().length);
    }

    @Test
    public void testSupport1Point() {
        List<S2Point> support = Arrays.asList(new S2Point(1, 2));
        EnclosingBall<Sphere2D, S2Point> cap = new SphericalCapGenerator(Vector3D.MINUS_K).ballOnSupport(support);
        Assert.assertEquals(0.0, cap.getRadius(), 1.0e-10);
        Assert.assertTrue(cap.contains(support.get(0)));
        Assert.assertTrue(cap.contains(support.get(0), 0.5));
        Assert.assertFalse(cap.contains(new S2Point(support.get(0).getTheta() + 0.1,
                                                    support.get(0).getPhi()   - 0.1),
                                         0.001));
        Assert.assertTrue(cap.contains(new S2Point(support.get(0).getTheta() + 0.1,
                                                   support.get(0).getPhi()   - 0.1),
                                        0.5));
        Assert.assertEquals(0, support.get(0).distance(cap.getCenter()), 1.0e-10);
        Assert.assertEquals(1, cap.getSupportSize());
        Assert.assertTrue(support.get(0) == cap.getSupport()[0]);
    }

    @Test
    public void testSupport2Points() {
        double phi = 1.0;
        List<S2Point> support = Arrays.asList(new S2Point(0, phi), new S2Point(0.5 * FastMath.PI, phi));
        EnclosingBall<Sphere2D, S2Point> cap =
                new SphericalCapGenerator(new Vector3D(-1, -1, -1)).ballOnSupport(support);
        double cosPhi = FastMath.cos(phi);
        Assert.assertEquals(0.5 * FastMath.acos(cosPhi * cosPhi), cap.getRadius(), 1.0e-10);
        int i = 0;
        for (S2Point v : support) {
            Assert.assertTrue(cap.contains(v));
            Assert.assertEquals(0.5 * FastMath.acos(cosPhi * cosPhi),
                                v.distance(cap.getCenter()), 1.0e-10);
            Assert.assertTrue(v == cap.getSupport()[i++]);
        }
        Assert.assertTrue(cap.contains(new S2Point(0.8, phi)));
        Assert.assertTrue(cap.contains(new S2Point(0.25 * FastMath.PI, 1.35)));
        Assert.assertFalse(cap.contains(S2Point.MINUS_K));
        Assert.assertEquals(0.25 * FastMath.PI, cap.getCenter().getTheta(), 1.0e-10);
        Assert.assertEquals(2, cap.getSupportSize());
    }

    @Test
    public void testSupport2PointsReversed() {
        double phi = 1.0;
        List<S2Point> support = Arrays.asList(new S2Point(0, phi), new S2Point(0.5 * FastMath.PI, phi));
        EnclosingBall<Sphere2D, S2Point> cap =
                new SphericalCapGenerator(new Vector3D(1, 1, 1)).ballOnSupport(support);
        double cosPhi = FastMath.cos(phi);
        Assert.assertEquals(FastMath.PI - 0.5 * FastMath.acos(cosPhi * cosPhi), cap.getRadius(), 1.0e-10);
        int i = 0;
        for (S2Point v : support) {
            Assert.assertTrue(cap.contains(v));
            Assert.assertEquals(FastMath.PI - 0.5 * FastMath.acos(cosPhi * cosPhi),
                                v.distance(cap.getCenter()), 1.0e-10);
            Assert.assertTrue(v == cap.getSupport()[i++]);
        }
        Assert.assertFalse(cap.contains(new S2Point(0.8, phi)));
        Assert.assertFalse(cap.contains(new S2Point(0.25 * FastMath.PI, 1.35)));
        Assert.assertTrue(cap.contains(S2Point.MINUS_K));
        Assert.assertEquals(-0.75 * FastMath.PI, cap.getCenter().getTheta(), 1.0e-10);
        Assert.assertEquals(2, cap.getSupportSize());
    }

    @Test
    public void testSupport3Points() {
        List<S2Point> support = Arrays.asList(new S2Point(0, 1), new S2Point(0, 1.6), new S2Point(2, 2));
        EnclosingBall<Sphere2D, S2Point> cap =
                new SphericalCapGenerator(Vector3D.MINUS_I).ballOnSupport(support);

        // reference value computed using expression found in I. Todhunter book
        // Spherical Trigonometry: For the Use of Colleges and Schools
        // article 92 in chapter VII Circumscribed and Inscribed Circles
        // book available from project Gutenberg at http://www.gutenberg.org/ebooks/19770
        double bigA = Vector3D.angle(Vector3D.crossProduct(support.get(0).getVector(),
                                                           support.get(1).getVector()),
                                     Vector3D.crossProduct(support.get(0).getVector(),
                                                           support.get(2).getVector()));
        double bigB = Vector3D.angle(Vector3D.crossProduct(support.get(1).getVector(),
                                                           support.get(2).getVector()),
                                     Vector3D.crossProduct(support.get(1).getVector(),
                                                           support.get(0).getVector()));
        double bigC = Vector3D.angle(Vector3D.crossProduct(support.get(2).getVector(),
                                                           support.get(0).getVector()),
                                     Vector3D.crossProduct(support.get(2).getVector(),
                                                           support.get(1).getVector()));
        double bigS = 0.5 * (bigA + bigB + bigC);
        double smallA = Vector3D.angle(support.get(1).getVector(), support.get(2).getVector());
        Assert.assertEquals(FastMath.tan(0.5 * smallA) / FastMath.cos(bigS - bigA),
                            FastMath.tan(cap.getRadius()), 1.0e-10);
        int i = 0;
        for (S2Point v : support) {
            Assert.assertTrue(cap.contains(v));
            Assert.assertEquals(cap.getRadius(), v.distance(cap.getCenter()), 1.0e-10);
            Assert.assertTrue(v == cap.getSupport()[i++]);
        }
        Assert.assertTrue(cap.contains(new S2Point(2, 0.9)));
        Assert.assertFalse(cap.contains(new S2Point(3.1,  0)));
        Assert.assertTrue(cap.contains(new S2Point(1.9,  1.99)));
        Assert.assertFalse(cap.contains(new S2Point(1.9, 2.7)));
        Assert.assertEquals(3, cap.getSupportSize());
    }

    @Test
    public void testRandom() {
        final RandomGenerator random = new Well1024a(0x4f1ce0a065960a00l);
        final UnitSphereRandomVectorGenerator sr = new UnitSphereRandomVectorGenerator(3, random);
        for (int i = 0; i < 500; ++i) {
            double refRadius = 0.5 * FastMath.PI * random.nextDouble();
            double cosR = FastMath.cos(refRadius);
            double sinR = FastMath.sin(refRadius);
            S2Point refCenter = new S2Point(new Vector3D(sr.nextVector()));
            Vector3D x = refCenter.getVector().orthogonal();
            Vector3D y = Vector3D.crossProduct(refCenter.getVector(), x);
            List<S2Point> support = new ArrayList<S2Point>();
            for (int j = 0; j < 3; ++j) {
                double alpha = MathUtils.TWO_PI * random.nextDouble();
                support.add(new S2Point(new Vector3D(cosR, refCenter.getVector(),
                                                     sinR * FastMath.cos(alpha), x,
                                                     sinR * FastMath.sin(alpha), y)));
            }
            EnclosingBall<Sphere2D, S2Point> cap =
                    new SphericalCapGenerator(Vector3D.MINUS_K).ballOnSupport(support);
            if (refCenter.distance(S2Point.MINUS_K) > refRadius) {
            Assert.assertEquals(0.0, refCenter.distance(cap.getCenter()), 3e-9 * refRadius);
            Assert.assertEquals(refRadius, cap.getRadius(), 7e-10 * refRadius);
            } else {
                Assert.assertEquals(FastMath.PI, refCenter.distance(cap.getCenter()), 3e-9 * refRadius);
                 Assert.assertEquals(FastMath.PI - refRadius, cap.getRadius(), 7e-10 * refRadius);
            }
        }
        
    }
}
