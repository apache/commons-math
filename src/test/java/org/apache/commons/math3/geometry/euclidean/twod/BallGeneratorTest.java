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
package org.apache.commons.math3.geometry.euclidean.twod;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.geometry.enclosing.EnclosingBall;
import org.junit.Assert;
import org.junit.Test;


public class BallGeneratorTest {

    @Test
    public void testSupport0Point() {
        List<Vector2D> support = Arrays.asList(new Vector2D[0]);
        EnclosingBall<Euclidean2D, Vector2D> ball = new BallGenerator().ballOnSupport(support);
        Assert.assertTrue(ball.getRadius() < 0);
        Assert.assertEquals(0, ball.getSupportSize());
        Assert.assertEquals(0, ball.getSupport().length);
    }

    @Test
    public void testSupport1Point() {
        List<Vector2D> support = Arrays.asList(new Vector2D(1, 2));
        EnclosingBall<Euclidean2D, Vector2D> ball = new BallGenerator().ballOnSupport(support);
        Assert.assertEquals(0.0, ball.getRadius(), 1.0e-10);
        Assert.assertTrue(ball.contains(support.get(0)));
        Assert.assertTrue(ball.contains(support.get(0), 0.5));
        Assert.assertFalse(ball.contains(new Vector2D(support.get(0).getX() + 0.1,
                                                      support.get(0).getY() - 0.1),
                                         0.001));
        Assert.assertTrue(ball.contains(new Vector2D(support.get(0).getX() + 0.1,
                                                     support.get(0).getY() - 0.1),
                                        0.5));
        Assert.assertEquals(0, support.get(0).distance(ball.getCenter()), 1.0e-10);
        Assert.assertEquals(1, ball.getSupportSize());
        Assert.assertTrue(support.get(0) == ball.getSupport()[0]);
    }

    @Test
    public void testSupport2Points() {
        List<Vector2D> support = Arrays.asList(new Vector2D(1, 0),
                                               new Vector2D(3, 0));
        EnclosingBall<Euclidean2D, Vector2D> ball = new BallGenerator().ballOnSupport(support);
        Assert.assertEquals(1.0, ball.getRadius(), 1.0e-10);
        int i = 0;
        for (Vector2D v : support) {
            Assert.assertTrue(ball.contains(v));
            Assert.assertEquals(1.0, v.distance(ball.getCenter()), 1.0e-10);
            Assert.assertTrue(v == ball.getSupport()[i++]);
        }
        Assert.assertTrue(ball.contains(new Vector2D(2, 0.9)));
        Assert.assertFalse(ball.contains(Vector2D.ZERO));
        Assert.assertEquals(0.0, new Vector2D(2, 0).distance(ball.getCenter()), 1.0e-10);
        Assert.assertEquals(2, ball.getSupportSize());
    }

    @Test
    public void testSupport3Points() {
        List<Vector2D> support = Arrays.asList(new Vector2D(1, 0),
                                               new Vector2D(3, 0),
                                               new Vector2D(2, 2));
        EnclosingBall<Euclidean2D, Vector2D> ball = new BallGenerator().ballOnSupport(support);
        Assert.assertEquals(5.0 / 4.0, ball.getRadius(), 1.0e-10);
        int i = 0;
        for (Vector2D v : support) {
            Assert.assertTrue(ball.contains(v));
            Assert.assertEquals(5.0 / 4.0, v.distance(ball.getCenter()), 1.0e-10);
            Assert.assertTrue(v == ball.getSupport()[i++]);
        }
        Assert.assertTrue(ball.contains(new Vector2D(2, 0.9)));
        Assert.assertFalse(ball.contains(new Vector2D(0.9,  0)));
        Assert.assertFalse(ball.contains(new Vector2D(3.1,  0)));
        Assert.assertTrue(ball.contains(new Vector2D(2.0, -0.499)));
        Assert.assertFalse(ball.contains(new Vector2D(2.0, -0.501)));
        Assert.assertEquals(0.0, new Vector2D(2.0, 3.0 / 4.0).distance(ball.getCenter()), 1.0e-10);
        Assert.assertEquals(3, ball.getSupportSize());
    }

}
