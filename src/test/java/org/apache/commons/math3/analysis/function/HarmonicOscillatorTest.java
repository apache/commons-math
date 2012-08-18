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

package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for class {@link HarmonicOscillator}.
 */
public class HarmonicOscillatorTest {
    private final double EPS = Math.ulp(1d);

    @Test
    public void testSomeValues() {
        final double a = -1.2;
        final double w = 0.34;
        final double p = 5.6;
        final UnivariateFunction f = new HarmonicOscillator(a, w, p);

        final double d = 0.12345;
        for (int i = 0; i < 10; i++) {
            final double v = i * d;
            Assert.assertEquals(a * FastMath.cos(w * v + p), f.value(v), 0);
        }
    }

    @Test
    public void testDerivative() {
        final double a = -1.2;
        final double w = 0.34;
        final double p = 5.6;
        final HarmonicOscillator f = new HarmonicOscillator(a, w, p);

        for (int maxOrder = 0; maxOrder < 6; ++maxOrder) {
            final double d = 0.12345;
            for (int i = 0; i < 10; i++) {
                final double v = i * d;
                final DerivativeStructure h = f.value(new DerivativeStructure(1, maxOrder, 0, v));
                for (int k = 0; k <= maxOrder; ++k) {
                    final double trigo;
                    switch (k % 4) {
                        case 0:
                            trigo = +FastMath.cos(w * v + p);
                            break;
                        case 1:
                            trigo = -FastMath.sin(w * v + p);
                            break;
                        case 2:
                            trigo = -FastMath.cos(w * v + p);
                            break;
                        default:
                            trigo = +FastMath.sin(w * v + p);
                            break;
                    }
                    Assert.assertEquals(a * FastMath.pow(w, k) * trigo,
                                        h.getPartialDerivative(k),
                                        Precision.EPSILON);
                }
            }
        }
    }

    @Test(expected=NullArgumentException.class)
    public void testParametricUsage1() {
        final HarmonicOscillator.Parametric g = new HarmonicOscillator.Parametric();
        g.value(0, null);
    }

    @Test(expected=DimensionMismatchException.class)
    public void testParametricUsage2() {
        final HarmonicOscillator.Parametric g = new HarmonicOscillator.Parametric();
        g.value(0, new double[] {0});
    }

    @Test(expected=NullArgumentException.class)
    public void testParametricUsage3() {
        final HarmonicOscillator.Parametric g = new HarmonicOscillator.Parametric();
        g.gradient(0, null);
    }

    @Test(expected=DimensionMismatchException.class)
    public void testParametricUsage4() {
        final HarmonicOscillator.Parametric g = new HarmonicOscillator.Parametric();
        g.gradient(0, new double[] {0});
    }

    @Test
    public void testParametricValue() {
        final double amplitude = 2;
        final double omega = 3;
        final double phase = 4;
        final HarmonicOscillator f = new HarmonicOscillator(amplitude, omega, phase);

        final HarmonicOscillator.Parametric g = new HarmonicOscillator.Parametric();
        Assert.assertEquals(f.value(-1), g.value(-1, new double[] {amplitude, omega, phase}), 0);
        Assert.assertEquals(f.value(0), g.value(0, new double[] {amplitude, omega, phase}), 0);
        Assert.assertEquals(f.value(2), g.value(2, new double[] {amplitude, omega, phase}), 0);
    }

    @Test
    public void testParametricGradient() {
        final double amplitude = 2;
        final double omega = 3;
        final double phase = 4;
        final HarmonicOscillator.Parametric f = new HarmonicOscillator.Parametric();

        final double x = 1;
        final double[] grad = f.gradient(1, new double[] {amplitude, omega, phase});
        final double xTimesOmegaPlusPhase = omega * x + phase;
        final double a = FastMath.cos(xTimesOmegaPlusPhase);
        Assert.assertEquals(a, grad[0], EPS);
        final double w = -amplitude * x * FastMath.sin(xTimesOmegaPlusPhase);
        Assert.assertEquals(w, grad[1], EPS);
        final double p = -amplitude * FastMath.sin(xTimesOmegaPlusPhase);
        Assert.assertEquals(p, grad[2], EPS);
    }
}
