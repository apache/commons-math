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

package org.apache.commons.math4.legacy.analysis.function;

import org.apache.commons.math4.legacy.analysis.UnivariateFunction;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for all classes in org.apache.commons.math4.legacy.analysis.function that implement UnivariateFunction explicitly.
 */
public class UnivariateFunctionTest {


    private final double EPS = Math.ulp(1d);

    @Test
    public void testAbs() {
        Abs abs = new Abs();
        Assert.assertEquals(5, abs.value(-5), EPS);
        Assert.assertEquals(5, abs.value(5), EPS);
        Assert.assertEquals(5.123, abs.value(-5.123), EPS);
        Assert.assertEquals(5.123, abs.value(5.123), EPS);
        Assert.assertEquals(0, abs.value(0), EPS);
    }

    @Test
    public void testCeil() {
        Ceil ceil = new Ceil();
        Assert.assertEquals(-5, ceil.value(-5), EPS);
        Assert.assertEquals(-4, ceil.value(-4.999), EPS);
        Assert.assertEquals(0, ceil.value(0), EPS);
        Assert.assertEquals(1, ceil.value(1), EPS);
        Assert.assertEquals(2, ceil.value(1.000000001), EPS);
    }

    @Test
    public void testFloor() {
        Floor floor = new Floor();
        Assert.assertEquals(-5, floor.value(-5), EPS);
        Assert.assertEquals(-5, floor.value(-4.999), EPS);
        Assert.assertEquals(0, floor.value(0), EPS);
        Assert.assertEquals(1, floor.value(1), EPS);
        Assert.assertEquals(1, floor.value(1.000000001), EPS);
    }

    @Test
    public void testRint() {
        //Rint function is round half even.
        Rint rint = new Rint();
        Assert.assertEquals(-5, rint.value(-5), EPS);
        Assert.assertEquals(-4, rint.value(-4.5), EPS);
        Assert.assertEquals(0, rint.value(0), EPS);
        Assert.assertEquals(1, rint.value(1), EPS);
        Assert.assertEquals(2, rint.value(1.5), EPS);
        Assert.assertEquals(2, rint.value(2.5), EPS);
        Assert.assertEquals(-1, rint.value(-0.99999999), EPS);
        Assert.assertEquals(11, rint.value(10.99999999), EPS);
    }

    @Test
    public void testSignum() {
        Signum signum = new Signum();
        Assert.assertEquals(-1, signum.value(-5), EPS);
        Assert.assertEquals(-1, signum.value(-4.5), EPS);
        Assert.assertEquals(0, signum.value(0), EPS);
        Assert.assertEquals(-0, signum.value(-0), EPS);
        Assert.assertEquals(1, signum.value(1), EPS);
        Assert.assertEquals(1, signum.value(1.5), EPS);
        Assert.assertEquals(1, signum.value(2.5), EPS);
        Assert.assertEquals(-1, signum.value(-0.99999999), EPS);
        Assert.assertEquals(1, signum.value(10.99999999), EPS);
    }

    @Test
    public void testStepFunction() {
        final double[] x = { -2, -0.5, 0, 1.9, 7.4, 21.3 };
        final double[] y = { 4, -1, -5.5, 0.4, 5.8, 51.2 };

        final UnivariateFunction f = new StepFunction(x, y);

        Assert.assertEquals(4, f.value(Double.NEGATIVE_INFINITY), EPS);
        Assert.assertEquals(4, f.value(-10), EPS);
        Assert.assertEquals(-1, f.value(-0.4), EPS);
        Assert.assertEquals(-5.5, f.value(0), EPS);
        Assert.assertEquals(0.4, f.value(2), EPS);
        Assert.assertEquals(5.8, f.value(10), EPS);
        Assert.assertEquals(51.2, f.value(30), EPS);
        Assert.assertEquals(51.2, f.value(Double.POSITIVE_INFINITY), EPS);
    }

    @Test
    public void testUlp() {
        Ulp ulp = new Ulp();
        Assert.assertEquals(expectedUlp(1),ulp.value(1), EPS);
        Assert.assertEquals(expectedUlp(1.123456789),ulp.value(1.123456789), EPS);
        Assert.assertEquals(expectedUlp(-1),ulp.value(-1), EPS);
        Assert.assertEquals(expectedUlp(-1.123456789),ulp.value(-1.123456789), EPS);
        Assert.assertEquals(expectedUlp(0),ulp.value(0), EPS);
        Assert.assertEquals(expectedUlp(500000000),ulp.value(500000000), EPS);
        Assert.assertEquals(expectedUlp(-500000000),ulp.value(-500000000), EPS);
    }

    private double expectedUlp(double x) {
        return JdkMath.abs(x - Double.longBitsToDouble(Double.doubleToRawLongBits(x) ^ 1));
    }

}
