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
package org.apache.commons.math4.geometry.enclosing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math4.geometry.enclosing.EnclosingBall;
import org.apache.commons.math4.geometry.enclosing.WelzlEncloser;
import org.apache.commons.math4.geometry.euclidean.threed.Euclidean3D;
import org.apache.commons.math4.geometry.euclidean.threed.SphereGenerator;
import org.apache.commons.math4.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math4.random.RandomGenerator;
import org.apache.commons.math4.random.UnitSphereRandomVectorGenerator;
import org.apache.commons.math4.random.Well1024a;
import org.junit.Assert;
import org.junit.Test;


public class WelzlEncloser3DTest {

    @Test
    public void testNullList() {
        SphereGenerator generator = new SphereGenerator();
        WelzlEncloser<Euclidean3D, Vector3D> encloser =
                new WelzlEncloser<Euclidean3D, Vector3D>(1.0e-10, generator);
        EnclosingBall<Euclidean3D, Vector3D> ball = encloser.enclose(null);
        Assert.assertTrue(ball.getRadius() < 0);
    }

    @Test
    public void testNoPoints() {
        SphereGenerator generator = new SphereGenerator();
        WelzlEncloser<Euclidean3D, Vector3D> encloser =
                new WelzlEncloser<Euclidean3D, Vector3D>(1.0e-10, generator);
        EnclosingBall<Euclidean3D, Vector3D> ball = encloser.enclose(new ArrayList<Vector3D>());
        Assert.assertTrue(ball.getRadius() < 0);
    }

    @Test
    public void testReducingBall() {
        List<Vector3D> list =
                Arrays.asList(new Vector3D(-7.140397329568118, -16.571661242582177,  11.714458961735405),
                              new Vector3D(-7.137986707455888, -16.570767323375720,  11.708602108715928),
                              new Vector3D(-7.139185068549035, -16.570891204702250,  11.715554057357394),
                              new Vector3D(-7.142682716997507, -16.571609818234290,  11.710787934580328),
                              new Vector3D(-7.139018392423351, -16.574405614157020,  11.710518716711425),
                              new Vector3D(-7.140870659936730, -16.567993074240455,  11.710914678204503),
                              new Vector3D(-7.136350173659562, -16.570498228820930,  11.713965225900928),
                              new Vector3D(-7.141675762759172, -16.572852471407028,  11.714033471449508),
                              new Vector3D(-7.140453077221105, -16.570212820780647,  11.708624578004980),
                              new Vector3D(-7.140322188726825, -16.574152894557717,  11.710305611121410),
                              new Vector3D(-7.141116131477088, -16.574061164624560,  11.712938509321699));
        WelzlEncloser<Euclidean3D, Vector3D> encloser =
                new WelzlEncloser<Euclidean3D, Vector3D>(1.0e-10, new SphereGenerator());
        EnclosingBall<Euclidean3D, Vector3D> ball = encloser.enclose(list);
        Assert.assertTrue(ball.getRadius() > 0);
    }

    @Test
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
                new WelzlEncloser<Euclidean3D, Vector3D>(1.0e-10, new SphereGenerator());
        EnclosingBall<Euclidean3D, Vector3D> ball = encloser.enclose(list);
        Assert.assertTrue(ball.getRadius() > 0);
    }

    @Test
    public void testLargeSamples() throws IOException {
        RandomGenerator random = new Well1024a(0x35ddecfc78131e1dl);
        final UnitSphereRandomVectorGenerator sr = new UnitSphereRandomVectorGenerator(3, random);
        for (int k = 0; k < 50; ++k) {

            // define the reference sphere we want to compute
            double d = 25 * random.nextDouble();
            double refRadius = 10 * random.nextDouble();
            Vector3D refCenter = new Vector3D(d, new Vector3D(sr.nextVector()));
            // set up a large sample inside the reference sphere
            int nbPoints = random.nextInt(1000);
            List<Vector3D> points = new ArrayList<Vector3D>();
            for (int i = 0; i < nbPoints; ++i) {
                double r = refRadius * random.nextDouble();
                points.add(new Vector3D(1.0, refCenter, r, new Vector3D(sr.nextVector())));
            }

            // test we find a sphere at most as large as the one used for random drawings
            checkSphere(points, refRadius);

        }
    }

    private void checkSphere(List<Vector3D> points, double refRadius) {

        EnclosingBall<Euclidean3D, Vector3D> sphere = checkSphere(points);

        // compare computed sphere with bounding sphere
        Assert.assertTrue(sphere.getRadius() <= refRadius);

        // check removing any point of the support Sphere fails to enclose the point
        for (int i = 0; i < sphere.getSupportSize(); ++i) {
            List<Vector3D> reducedSupport = new ArrayList<Vector3D>();
            int count = 0;
            for (Vector3D s : sphere.getSupport()) {
                if (count++ != i) {
                    reducedSupport.add(s);
                }
            }
            EnclosingBall<Euclidean3D, Vector3D> reducedSphere =
                    new SphereGenerator().ballOnSupport(reducedSupport);
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
                new WelzlEncloser<Euclidean3D, Vector3D>(1.0e-10, new SphereGenerator());
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
