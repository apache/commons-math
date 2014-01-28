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

import org.apache.commons.math3.geometry.euclidean.twod.BallGenerator;
import org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well1024a;
import org.junit.Assert;
import org.junit.Test;


public class WelzlEncloserTest {

    @Test
    public void testNullList() {
        BallGenerator generator = new BallGenerator();
        WelzlEncloser<Euclidean2D, Vector2D> encloser =
                new WelzlEncloser<Euclidean2D, Vector2D>(1.0e-10, 2, generator);
        EnclosingBall<Euclidean2D, Vector2D> ball = encloser.enclose(null);
        Assert.assertTrue(ball.getRadius() < 0);
    }

    @Test
    public void testNoPoints() {
        BallGenerator generator = new BallGenerator();
        WelzlEncloser<Euclidean2D, Vector2D> encloser =
                new WelzlEncloser<Euclidean2D, Vector2D>(1.0e-10, 2, generator);
        EnclosingBall<Euclidean2D, Vector2D> ball = encloser.enclose(new ArrayList<Vector2D>());
        Assert.assertTrue(ball.getRadius() < 0);
    }

    @Test
    public void testRegularPoints() {
        List<Vector2D> list = buildList(22, 26, 30, 38, 64, 28,  8, 54, 11, 15);
        checkBall(list, Arrays.asList(list.get(2), list.get(3), list.get(4)));
    }

    @Test
    public void testSolutionOnDiameter() {
        List<Vector2D> list = buildList(22, 26, 30, 38, 64, 28,  8, 54);
        checkBall(list, Arrays.asList(list.get(2), list.get(3)));
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
            checkBall(points);
        }
    }

    private List<Vector2D> buildList(final double ... coordinates) {
        List<Vector2D> list = new ArrayList<Vector2D>(coordinates.length / 2);
        for (int i = 0; i < coordinates.length; i += 2) {
            list.add(new Vector2D(coordinates[i], coordinates[i + 1]));
        }
        return list;
    }

    private void checkBall(List<Vector2D> points, List<Vector2D> refSupport) {

        EnclosingBall<Euclidean2D, Vector2D> ball = checkBall(points);

        // compare computed ball with expected ball
        BallGenerator generator = new BallGenerator();
        EnclosingBall<Euclidean2D, Vector2D> expected = generator.ballOnSupport(refSupport);
        Assert.assertEquals(refSupport.size(), ball.getSupportSize());
        Assert.assertEquals(expected.getRadius(),        ball.getRadius(),        1.0e-10);
        Assert.assertEquals(expected.getCenter().getX(), ball.getCenter().getX(), 1.0e-10);
        Assert.assertEquals(expected.getCenter().getY(), ball.getCenter().getY(), 1.0e-10);

        for (Vector2D s : ball.getSupport()) {
            boolean found = false;
            for (Vector2D rs : refSupport) {
                if (s == rs) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }

        // check removing any point of the support ball fails to enclose the point
        for (int i = 0; i < ball.getSupportSize(); ++i) {
            List<Vector2D> reducedSupport = new ArrayList<Vector2D>();
            int count = 0;
            for (Vector2D s : ball.getSupport()) {
                if (count++ != i) {
                    reducedSupport.add(s);
                }
            }
            EnclosingBall<Euclidean2D, Vector2D> reducedBall = generator.ballOnSupport(reducedSupport);
            boolean foundOutside = false;
            for (int j = 0; j < points.size() && !foundOutside; ++j) {
                if (!reducedBall.contains(points.get(j), 1.0e-10)) {
                    foundOutside = true;
                }
            }
            Assert.assertTrue(foundOutside);
        }

    }

    private EnclosingBall<Euclidean2D, Vector2D> checkBall(List<Vector2D> points) {

        WelzlEncloser<Euclidean2D, Vector2D> encloser =
                new WelzlEncloser<Euclidean2D, Vector2D>(1.0e-10, 2, new BallGenerator());
        EnclosingBall<Euclidean2D, Vector2D> ball = encloser.enclose(points);

        // all points are enclosed
        for (Vector2D v : points) {
            Assert.assertTrue(ball.contains(v, 1.0e-10));
        }

        for (Vector2D v : points) {
            boolean inSupport = false;
            for (Vector2D s : ball.getSupport()) {
                if (v == s) {
                    inSupport = true;
                }
            }
            if (inSupport) {
                // points on the support should be outside of reduced ball
                Assert.assertFalse(ball.contains(v, -0.001));
            }
        }

        return ball;

    }

}
