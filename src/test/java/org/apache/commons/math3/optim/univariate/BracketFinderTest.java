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
package org.apache.commons.math3.optim.univariate;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link BracketFinder}.
 */
public class BracketFinderTest {

    @Test
    public void testCubicMin() {
        final BracketFinder bFind = new BracketFinder();
        final UnivariateFunction func = new UnivariateFunction() {
                public double value(double x) {
                    if (x < -2) {
                        return value(-2);
                    }
                    else  {
                        return (x - 1) * (x + 2) * (x + 3);
                    }
                }
            };

        bFind.search(func, GoalType.MINIMIZE, -2 , -1);
        final double tol = 1e-15;
        // Comparing with results computed in Python.
        Assert.assertEquals(-2, bFind.getLo(), tol);
        Assert.assertEquals(-1, bFind.getMid(), tol);
        Assert.assertEquals(0.61803399999999997, bFind.getHi(), tol);
    }

    @Test
    public void testCubicMax() {
        final BracketFinder bFind = new BracketFinder();
        final UnivariateFunction func = new UnivariateFunction() {
                public double value(double x) {
                    if (x < -2) {
                        return value(-2);
                    }
                    else  {
                        return -(x - 1) * (x + 2) * (x + 3);
                    }
                }
            };

        bFind.search(func, GoalType.MAXIMIZE, -2 , -1);
        final double tol = 1e-15;
        Assert.assertEquals(-2, bFind.getLo(), tol);
        Assert.assertEquals(-1, bFind.getMid(), tol);
        Assert.assertEquals(0.61803399999999997, bFind.getHi(), tol);
    }

    @Test
    public void testMinimumIsOnIntervalBoundary() {
        final UnivariateFunction func = new UnivariateFunction() {
                public double value(double x) {
                    return x * x;
                }
            };

        final BracketFinder bFind = new BracketFinder();

        bFind.search(func, GoalType.MINIMIZE, 0, 1);
        Assert.assertTrue(bFind.getLo() <= 0);
        Assert.assertTrue(0 <= bFind.getHi());

        bFind.search(func, GoalType.MINIMIZE, -1, 0);
        Assert.assertTrue(bFind.getLo() <= 0);
        Assert.assertTrue(0 <= bFind.getHi());
    }

    @Test
    public void testIntervalBoundsOrdering() {
        final UnivariateFunction func = new UnivariateFunction() {
                public double value(double x) {
                    return x * x;
                }
            };

        final BracketFinder bFind = new BracketFinder();

        bFind.search(func, GoalType.MINIMIZE, -1, 1);
        Assert.assertTrue(bFind.getLo() <= 0);
        Assert.assertTrue(0 <= bFind.getHi());

        bFind.search(func, GoalType.MINIMIZE, 1, -1);
        Assert.assertTrue(bFind.getLo() <= 0);
        Assert.assertTrue(0 <= bFind.getHi());

        bFind.search(func, GoalType.MINIMIZE, 1, 2);
        Assert.assertTrue(bFind.getLo() <= 0);
        Assert.assertTrue(0 <= bFind.getHi());

        bFind.search(func, GoalType.MINIMIZE, 2, 1);
        Assert.assertTrue(bFind.getLo() <= 0);
        Assert.assertTrue(0 <= bFind.getHi());
    }
}
