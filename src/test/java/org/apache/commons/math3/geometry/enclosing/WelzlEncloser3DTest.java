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
package org.apache.commons.math3.geometry.enclosing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.Euclidean3D;
import org.apache.commons.math3.geometry.euclidean.threed.SphereGenerator;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.UnitSphereRandomVectorGenerator;
import org.apache.commons.math3.random.Well1024a;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


public class WelzlEncloser3DTest {

    @Test
    public void testNullList() {
        SphereGenerator generator = new SphereGenerator();
        WelzlEncloser<Euclidean3D, Vector3D> encloser =
                new WelzlEncloser<Euclidean3D, Vector3D>(1.0e-10, 2, generator);
        EnclosingBall<Euclidean3D, Vector3D> ball = encloser.enclose(null);
        Assert.assertTrue(ball.getRadius() < 0);
    }

    @Test
    public void testNoPoints() {
        SphereGenerator generator = new SphereGenerator();
        WelzlEncloser<Euclidean3D, Vector3D> encloser =
                new WelzlEncloser<Euclidean3D, Vector3D>(1.0e-10, 2, generator);
        EnclosingBall<Euclidean3D, Vector3D> ball = encloser.enclose(new ArrayList<Vector3D>());
        Assert.assertTrue(ball.getRadius() < 0);
    }

    @Test
    @Ignore // this test currently fails, it generates an infinite loop
    public void testInfiniteLoop() {
        // this test used to generate an infinite loop
        List<Vector3D> list =
                Arrays.asList(new Vector3D( -0.89227075512164380,  -2.89317694645713900,  14.84572323743355500),
                              new Vector3D( -0.92099498940693580,  -2.31086108263908940,  12.92071026467688300),
                              new Vector3D( -0.85227999411005200,  -3.06314731441320730,  15.40163831651287000),
                              new Vector3D( -1.77399413020785970,  -3.65630391378114260,  14.13190097751873400),
                              new Vector3D(  0.33157833272465354,  -2.22813591757792160,  14.21225234159008200),
                              new Vector3D( -1.53065579165484400,  -1.65692084770139570,  14.61483055714788500),
                              new Vector3D( -1.08457093941217140,  -1.96100325935602980,  13.09265170575555000),
                              new Vector3D(  0.30029469589708850,  -3.05470831395667370,  14.56352400426342600),
                              new Vector3D( -0.95007443938638460,  -1.86810946486118360,  15.14491234340057000),
                              new Vector3D( -1.89661503804130830,  -2.17004080885185860,  14.81235128513927000),
                              new Vector3D( -0.72193328761607530,  -1.44513142833618270,  14.52355724218561800),
                              new Vector3D( -0.26895980939606550,  -3.69512371522084140,  14.72272846327652000),
                              new Vector3D( -1.53501693431786170,  -3.25055166611021900,  15.15509062584274800),
                              new Vector3D( -0.71727553535519410,  -3.62284279460799100,  13.26256700929380700),
                              new Vector3D( -0.30220950676137365,  -3.25410412500779070,  13.13682612771606000),
                              new Vector3D( -0.04543996608267075,  -1.93081853923797750,  14.79497997883171400),
                              new Vector3D( -1.53348892951571640,  -3.66688919703524900,  14.73095600812074200),
                              new Vector3D( -0.98034899533935820,  -3.34004481162763960,  13.03245014017556800));

        WelzlEncloser<Euclidean3D, Vector3D> encloser =
                new WelzlEncloser<Euclidean3D, Vector3D>(1.0e-10, 2, new SphereGenerator());
        EnclosingBall<Euclidean3D, Vector3D> ball = encloser.enclose(list);
        Assert.assertTrue(ball.getRadius() > 0);
    }

    @Test
    @Ignore // this test currently fails, it generates an infinite loop
    public void testLargeSamples() {
        RandomGenerator random = new Well1024a(0x35ddecfc78131e1dl);
        final UnitSphereRandomVectorGenerator sr = new UnitSphereRandomVectorGenerator(3, random);
        for (int k = 0; k < 100; ++k) {

            // define the reference sphere we want to compute
            double d = 25 * random.nextDouble();
            double refRadius = 10 * random.nextDouble();
            Vector3D refCenter = new Vector3D(d, new Vector3D(sr.nextVector()));
            List<Vector3D> support = new ArrayList<Vector3D>();
            for (int i = 0; i < 4; ++i) {
                support.add(new Vector3D(1.0, refCenter, refRadius, new Vector3D(sr.nextVector())));
            }

            // set up a large sample inside the reference sphere
            int nbPoints = random.nextInt(10000);
            System.out.println(nbPoints);
            List<Vector3D> points = new ArrayList<Vector3D>();
            for (int i = 0; i < nbPoints; ++i) {
                double r = refRadius * random.nextDouble();
                points.add(new Vector3D(1.0, refCenter, r, new Vector3D(sr.nextVector())));
            }

            // hide the support point belonging to sphere boundary in the sample
            points.add(random.nextInt(nbPoints), support.get(0));
            points.add(random.nextInt(nbPoints), support.get(1));
            points.add(random.nextInt(nbPoints), support.get(2));
            points.add(random.nextInt(nbPoints), support.get(3));

            // test we find our sphere again
            checkSphere(points, support);

        }
    }

    private void checkSphere(List<Vector3D> points, List<Vector3D> refSupport) {

        EnclosingBall<Euclidean3D, Vector3D> Sphere = checkSphere(points);

        // compare computed Sphere with expected Sphere
        SphereGenerator generator = new SphereGenerator();
        EnclosingBall<Euclidean3D, Vector3D> expected = generator.ballOnSupport(refSupport);
        Assert.assertEquals(refSupport.size(), Sphere.getSupportSize());
        Assert.assertEquals(expected.getRadius(),        Sphere.getRadius(),        1.0e-10);
        Assert.assertEquals(expected.getCenter().getX(), Sphere.getCenter().getX(), 1.0e-10);
        Assert.assertEquals(expected.getCenter().getY(), Sphere.getCenter().getY(), 1.0e-10);

        for (Vector3D s : Sphere.getSupport()) {
            boolean found = false;
            for (Vector3D rs : refSupport) {
                if (s == rs) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }

        // check removing any point of the support Sphere fails to enclose the point
        for (int i = 0; i < Sphere.getSupportSize(); ++i) {
            List<Vector3D> reducedSupport = new ArrayList<Vector3D>();
            int count = 0;
            for (Vector3D s : Sphere.getSupport()) {
                if (count++ != i) {
                    reducedSupport.add(s);
                }
            }
            EnclosingBall<Euclidean3D, Vector3D> reducedSphere = generator.ballOnSupport(reducedSupport);
            boolean foundOutside = false;
            for (int j = 0; j < points.size() && !foundOutside; ++j) {
                if (!reducedSphere.contains(points.get(j), 1.0e-10)) {
                    foundOutside = true;
                }
            }
            Assert.assertTrue(foundOutside);
        }

    }

    private EnclosingBall<Euclidean3D, Vector3D> checkSphere(List<Vector3D> points) {

        WelzlEncloser<Euclidean3D, Vector3D> encloser =
                new WelzlEncloser<Euclidean3D, Vector3D>(1.0e-10, 2, new SphereGenerator());
        EnclosingBall<Euclidean3D, Vector3D> Sphere = encloser.enclose(points);

        // all points are enclosed
        for (Vector3D v : points) {
            Assert.assertTrue(Sphere.contains(v, 1.0e-10));
        }

        for (Vector3D v : points) {
            boolean inSupport = false;
            for (Vector3D s : Sphere.getSupport()) {
                if (v == s) {
                    inSupport = true;
                }
            }
            if (inSupport) {
                // points on the support should be outside of reduced ball
                Assert.assertFalse(Sphere.contains(v, -0.001));
            }
        }

        return Sphere;

    }

}
