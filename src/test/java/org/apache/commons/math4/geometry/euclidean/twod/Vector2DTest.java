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

import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.MathArithmeticException;
import org.apache.commons.math4.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

public class Vector2DTest {

    private static final double EPS = Math.ulp(1d);

    @Test
    public void testScaledVectorTripleConstructor() {
        Coordinates2D oneOne = new Coordinates2D(1.0,1.0);
        Coordinates2D oneTwo = new Coordinates2D(1.0,2.0);
        Coordinates2D oneThree = new Coordinates2D(1.0,3.0);

        Coordinates2D tripleCombo = new Coordinates2D(3.0, oneOne, 1.0, oneTwo, 2.5, oneThree);

        Assert.assertEquals(3.0 * 1 + 1.0 * 1 + 2.5 * 1,tripleCombo.getX(), EPS);
        Assert.assertEquals(3.0 * 1 + 1.0 * 2 + 2.5 * 3,tripleCombo.getY(), EPS);
    }

    @Test
    public void testScaledVectorQuadrupleConstructor() {
        Coordinates2D oneOne = new Coordinates2D(1.0, 1.0);
        Coordinates2D oneTwo = new Coordinates2D(1.0, 2.0);
        Coordinates2D oneThree = new Coordinates2D(1.0, 3.0);
        Coordinates2D oneFour = new Coordinates2D(1.0, 4.0);

        Coordinates2D tripleCombo = new Coordinates2D(3.0, oneOne, 1.0, oneTwo, 2.5, oneThree, 2.0, oneFour);

        Assert.assertEquals(3.0 * 1.0 + 1.0 * 1.0 + 2.5 * 1.0 + 2.0 * 1.0,tripleCombo.getX(), EPS);
        Assert.assertEquals(3.0 * 1.0 + 1.0 * 2.0 + 2.5 * 3.0 + 2.0 * 4.0,tripleCombo.getY(), EPS);
    }

    @Test
    public void testConstructorExceptions() {
        double[] v = new double[] {0.0, 1.0, 2.0};
        try {
            new Coordinates2D(v);
        }
        catch (Exception e) {
            Assert.assertTrue(e instanceof DimensionMismatchException);
        }
    }

    @Test
    public void testToArray() {
        Coordinates2D oneTwo = new Coordinates2D(1.0, 2.0);
        double[] array = oneTwo.toArray();
        Assert.assertEquals(1.0, array[0], EPS);
        Assert.assertEquals(2.0, array[1], EPS);
    }

    @Test
    public void testGetZero() {
        Coordinates2D zero = (new Coordinates2D(1.0, 1.0)).getZero();
        Assert.assertEquals(0.0, zero.getX(), EPS);
        Assert.assertEquals(0.0, zero.getY(), EPS);
    }

    @Test
    public void testNorm1() {
        Coordinates2D oneTwo = new Coordinates2D(-1.0, 2.0);
        Assert.assertEquals(3.0, oneTwo.getNorm1(), EPS);
    }

    @Test
    public void testNormSq() {
        Coordinates2D oneTwo = new Coordinates2D(-1.0, 2.0);
        Assert.assertEquals(5.0, oneTwo.getNormSq(), EPS);
    }

    @Test
    public void testNormInf() {
        Coordinates2D oneTwo = new Coordinates2D(-1.0, 2.0);
        Assert.assertEquals(2.0, oneTwo.getNormInf(), EPS);
    }

    @Test
    public void testVectorAddition() {
        Coordinates2D minusOneTwo = new Coordinates2D(-1.0,2.0);
        Coordinates2D threeFive = new Coordinates2D(3.0,5.0);
        Coordinates2D addition = minusOneTwo.add(threeFive);
        Assert.assertEquals(2.0, addition.getX(), EPS);
        Assert.assertEquals(7.0, addition.getY(), EPS);
    }

    @Test
    public void testScaledVectorAddition() {
        Coordinates2D minusOneTwo = new Coordinates2D(-1.0,2.0);
        Coordinates2D threeFive = new Coordinates2D(3.0,5.0);
        Coordinates2D addition = minusOneTwo.add(2.0, threeFive);
        Assert.assertEquals(5.0, addition.getX(), EPS);
        Assert.assertEquals(12.0, addition.getY(), EPS);
    }

    @Test
    public void testVectorSubtraction() {
        Coordinates2D minusOneTwo = new Coordinates2D(-1.0,2.0);
        Coordinates2D threeFive = new Coordinates2D(3.0,5.0);
        Coordinates2D addition = minusOneTwo.subtract(threeFive);
        Assert.assertEquals(-4.0, addition.getX(), EPS);
        Assert.assertEquals(-3.0, addition.getY(), EPS);
    }

    @Test
    public void testScaledVectorSubtraction() {
        Coordinates2D minusOneTwo = new Coordinates2D(-1.0,2.0);
        Coordinates2D threeFive = new Coordinates2D(3.0,5.0);
        Coordinates2D addition = minusOneTwo.subtract(2.0, threeFive);
        Assert.assertEquals(-7.0, addition.getX(), EPS);
        Assert.assertEquals(-8.0, addition.getY(), EPS);
    }

    @Test
    public void testNormalize() {
        Coordinates2D minusOneTwo = new Coordinates2D(-1.0,2.0);
        Coordinates2D normalizedMinusOneTwo = minusOneTwo.normalize();
        Assert.assertEquals(-1.0/FastMath.sqrt(5), normalizedMinusOneTwo.getX(), EPS);
        Assert.assertEquals(2.0/FastMath.sqrt(5), normalizedMinusOneTwo.getY(), EPS);
        Coordinates2D zero = minusOneTwo.getZero();
        try {
            zero.normalize();
        }
        catch (Exception e) {
            Assert.assertTrue(e instanceof MathArithmeticException);
        }
    }

    @Test
    public void testAngle() {
        Coordinates2D oneOne = new Coordinates2D(1.0, 1.0);
        try {
            Coordinates2D.angle(oneOne.getZero(), oneOne.getZero());
        }
        catch (Exception e) {
            Assert.assertTrue(e instanceof MathArithmeticException);
        }
        Coordinates2D oneZero = new Coordinates2D(1.0,0.0);
        double angle = Coordinates2D.angle(oneOne, oneZero);
        Assert.assertEquals(FastMath.PI/4, angle, EPS);
        Assert.assertEquals(0.004999958333958323, Coordinates2D.angle(new Coordinates2D(20.0,0.0), new Coordinates2D(20.0,0.1)), EPS);
    }

    @Test
    public void testNegate() {
        Coordinates2D oneOne = new Coordinates2D(1.0,1.0);
        Coordinates2D negated = oneOne.negate();
        Assert.assertEquals(-1.0, negated.getX(), EPS);
        Assert.assertEquals(-1.0, negated.getY(), EPS);
    }

    @Test
    public void testIsInfinite() {
        Coordinates2D oneOne = new Coordinates2D(1.0, 1.0);
        Coordinates2D infiniteVector = new Coordinates2D(Double.POSITIVE_INFINITY, 0.0);
        Assert.assertFalse(oneOne.isInfinite());
        Assert.assertTrue(infiniteVector.isInfinite());
    }

    @Test
    public void testDistance1() {
        Coordinates2D oneOne = new Coordinates2D(1.0,1.0);
        Coordinates2D fiveEleven = new Coordinates2D(5.0,11.0);
        double distance1 = oneOne.distance1(fiveEleven);
        Assert.assertEquals(14.0, distance1, EPS);
    }

    @Test
    public void testDistanceInf() {
        Coordinates2D oneOne = new Coordinates2D(1.0,1.0);
        Coordinates2D fiveEleven = new Coordinates2D(5.0,11.0);
        double distanceInf = oneOne.distanceInf(fiveEleven);
        double staticDistanceInf = Coordinates2D.distanceInf(oneOne, fiveEleven);
        Assert.assertEquals(10.0, distanceInf, EPS);
        Assert.assertEquals(distanceInf, staticDistanceInf, EPS);
    }

    @Test
    public void testDistanceSq() {
        Coordinates2D oneFive = new Coordinates2D(1.0, 5.0);
        Coordinates2D fourOne = new Coordinates2D(4.0, 1.0);
        double distanceSq = oneFive.distanceSq(fourOne);
        double staticDistanceSq = Coordinates2D.distanceSq(oneFive, fourOne);
        Assert.assertEquals(25.0, distanceSq, EPS);
        Assert.assertEquals(distanceSq, staticDistanceSq, EPS);
    }

    @Test
    public void testHashCode() {
        int hashCode = (new Coordinates2D(1.0,1.0)).hashCode();
        Assert.assertEquals(887095296, hashCode);
        Assert.assertEquals(542, (new Coordinates2D(Double.NaN, Double.NaN)).hashCode());
    }


    @Test
    public void testToString() {
        Assert.assertEquals("{1; 2}", (new Coordinates2D(1.0,2.0)).toString());
    }

    @Test
    public void testCrossProduct() {
        Coordinates2D p1 = new Coordinates2D(1, 1);
        Coordinates2D p2 = new Coordinates2D(2, 2);

        Coordinates2D p3 = new Coordinates2D(3, 3);
        Assert.assertEquals(0.0, p3.crossProduct(p1, p2), EPS);

        Coordinates2D p4 = new Coordinates2D(1, 2);
        Assert.assertEquals(1.0, p4.crossProduct(p1, p2), EPS);

        Coordinates2D p5 = new Coordinates2D(2, 1);
        Assert.assertEquals(-1.0, p5.crossProduct(p1, p2), EPS);
    }
}
