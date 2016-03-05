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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math4.geometry.enclosing.EnclosingBall;
import org.apache.commons.math4.geometry.enclosing.WelzlEncloser;
import org.apache.commons.math4.geometry.euclidean.twod.DiskGenerator;
import org.apache.commons.math4.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math4.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math4.random.RandomGenerator;
import org.apache.commons.math4.random.Well1024a;
import org.junit.Assert;
import org.junit.Test;


public class WelzlEncloser2DTest {

    @Test
    public void testNullList() {
        DiskGenerator generator = new DiskGenerator();
        WelzlEncloser<Euclidean2D, Vector2D> encloser =
                new WelzlEncloser<Euclidean2D, Vector2D>(1.0e-10, generator);
        EnclosingBall<Euclidean2D, Vector2D> ball = encloser.enclose(null);
        Assert.assertTrue(ball.getRadius() < 0);
    }

    @Test
    public void testNoPoints() {
        DiskGenerator generator = new DiskGenerator();
        WelzlEncloser<Euclidean2D, Vector2D> encloser =
                new WelzlEncloser<Euclidean2D, Vector2D>(1.0e-10, generator);
        EnclosingBall<Euclidean2D, Vector2D> ball = encloser.enclose(new ArrayList<Vector2D>());
        Assert.assertTrue(ball.getRadius() < 0);
    }

    @Test
    public void testRegularPoints() {
        List<Vector2D> list = buildList(22, 26, 30, 38, 64, 28,  8, 54, 11, 15);
        checkDisk(list, Arrays.asList(list.get(2), list.get(3), list.get(4)));
    }

    @Test
    public void testSolutionOnDiameter() {
        List<Vector2D> list = buildList(22, 26, 30, 38, 64, 28,  8, 54);
        checkDisk(list, Arrays.asList(list.get(2), list.get(3)));
    }

    @Test
    public void testReducingBall1() {
        List<Vector2D> list = buildList(0.05380958511396061, 0.57332359658700000,
                                        0.99348810731127870, 0.02056421361521466,
                                        0.01203950647796437, 0.99779675042261860,
                                        0.00810189987706078, 0.00589246003827815,
                                        0.00465180821202149, 0.99219972923046940);
        checkDisk(list, Arrays.asList(list.get(1), list.get(3), list.get(4)));
    }

    @Test
    public void testReducingBall2() {
        List<Vector2D> list = buildList(0.016930586154703, 0.333955448537779,
                                        0.987189104892331, 0.969778855274507,
                                        0.983696889599935, 0.012904580013266,
                                        0.013114499572905, 0.034740156356895);
        checkDisk(list, Arrays.asList(list.get(1), list.get(2), list.get(3)));
    }

    @Test
    public void testLargeSamples() {
        RandomGenerator random = new Well1024a(0xa2a63cad12c01fb2l);
        for (int k = 0; k < 100; ++k) {
            int nbPoints = random.nextInt(10000);
            List<Vector2D> points = new ArrayList<Vector2D>();
            for (int i = 0; i < nbPoints; ++i) {
                double x = random.nextDouble();
                double y = random.nextDouble();
                points.add(new Vector2D(x, y));
            }
            checkDisk(points);
        }
    }

    private List<Vector2D> buildList(final double ... coordinates) {
        List<Vector2D> list = new ArrayList<Vector2D>(coordinates.length / 2);
        for (int i = 0; i < coordinates.length; i += 2) {
            list.add(new Vector2D(coordinates[i], coordinates[i + 1]));
        }
        return list;
    }

    private void checkDisk(List<Vector2D> points, List<Vector2D> refSupport) {

        EnclosingBall<Euclidean2D, Vector2D> disk = checkDisk(points);

        // compare computed disk with expected disk
        DiskGenerator generator = new DiskGenerator();
        EnclosingBall<Euclidean2D, Vector2D> expected = generator.ballOnSupport(refSupport);
        Assert.assertEquals(refSupport.size(), disk.getSupportSize());
        Assert.assertEquals(expected.getRadius(),        disk.getRadius(),        1.0e-10);
        Assert.assertEquals(expected.getCenter().getX(), disk.getCenter().getX(), 1.0e-10);
        Assert.assertEquals(expected.getCenter().getY(), disk.getCenter().getY(), 1.0e-10);

        for (Vector2D s : disk.getSupport()) {
            boolean found = false;
            for (Vector2D rs : refSupport) {
                if (s == rs) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }

        // check removing any point of the support disk fails to enclose the point
        for (int i = 0; i < disk.getSupportSize(); ++i) {
            List<Vector2D> reducedSupport = new ArrayList<Vector2D>();
            int count = 0;
            for (Vector2D s : disk.getSupport()) {
                if (count++ != i) {
                    reducedSupport.add(s);
                }
            }
            EnclosingBall<Euclidean2D, Vector2D> reducedDisk = generator.ballOnSupport(reducedSupport);
            boolean foundOutside = false;
            for (int j = 0; j < points.size() && !foundOutside; ++j) {
                if (!reducedDisk.contains(points.get(j), 1.0e-10)) {
                    foundOutside = true;
                }
            }
            Assert.assertTrue(foundOutside);
        }

    }

    private EnclosingBall<Euclidean2D, Vector2D> checkDisk(List<Vector2D> points) {

        WelzlEncloser<Euclidean2D, Vector2D> encloser =
                new WelzlEncloser<Euclidean2D, Vector2D>(1.0e-10, new DiskGenerator());
        EnclosingBall<Euclidean2D, Vector2D> disk = encloser.enclose(points);

        // all points are enclosed
        for (Vector2D v : points) {
            Assert.assertTrue(disk.contains(v, 1.0e-10));
        }

        for (Vector2D v : points) {
            boolean inSupport = false;
            for (Vector2D s : disk.getSupport()) {
                if (v == s) {
                    inSupport = true;
                }
            }
            if (inSupport) {
                // points on the support should be outside of reduced ball
                Assert.assertFalse(disk.contains(v, -0.001));
            }
        }

        return disk;

    }

}
