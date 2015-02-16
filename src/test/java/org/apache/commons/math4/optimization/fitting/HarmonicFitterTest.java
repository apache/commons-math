// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.apache.commons.math3.optimization.fitting;

import java.util.Random;

import org.apache.commons.math3.analysis.function.HarmonicOscillator;
import org.apache.commons.math3.optimization.general.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.junit.Test;
import org.junit.Assert;

@Deprecated
public class HarmonicFitterTest {
    @Test(expected=NumberIsTooSmallException.class)
    public void testPreconditions1() {
        HarmonicFitter fitter =
            new HarmonicFitter(new LevenbergMarquardtOptimizer());

        fitter.fit();
    }

    // This test fails (throwing "ConvergenceException" instead).
//     @Test(expected=ZeroException.class)
//     public void testPreconditions2() {
//         HarmonicFitter fitter =
//             new HarmonicFitter(new LevenbergMarquardtOptimizer());

//         final double x = 1.2;
//         fitter.addObservedPoint(1, x, 1);
//         fitter.addObservedPoint(1, x, -1);
//         fitter.addObservedPoint(1, x, 0.5);
//         fitter.addObservedPoint(1, x, 0);

//         final double[] fitted = fitter.fit();
//     }

    @Test
    public void testNoError() {
        final double a = 0.2;
        final double w = 3.4;
        final double p = 4.1;
        HarmonicOscillator f = new HarmonicOscillator(a, w, p);

        HarmonicFitter fitter =
            new HarmonicFitter(new LevenbergMarquardtOptimizer());
        for (double x = 0.0; x < 1.3; x += 0.01) {
            fitter.addObservedPoint(1, x, f.value(x));
        }

        final double[] fitted = fitter.fit();
        Assert.assertEquals(a, fitted[0], 1.0e-13);
        Assert.assertEquals(w, fitted[1], 1.0e-13);
        Assert.assertEquals(p, MathUtils.normalizeAngle(fitted[2], p), 1e-13);

        HarmonicOscillator ff = new HarmonicOscillator(fitted[0], fitted[1], fitted[2]);

        for (double x = -1.0; x < 1.0; x += 0.01) {
            Assert.assertTrue(FastMath.abs(f.value(x) - ff.value(x)) < 1e-13);
        }
    }

    @Test
    public void test1PercentError() {
        Random randomizer = new Random(64925784252l);
        final double a = 0.2;
        final double w = 3.4;
        final double p = 4.1;
        HarmonicOscillator f = new HarmonicOscillator(a, w, p);

        HarmonicFitter fitter =
            new HarmonicFitter(new LevenbergMarquardtOptimizer());
        for (double x = 0.0; x < 10.0; x += 0.1) {
            fitter.addObservedPoint(1, x,
                                    f.value(x) + 0.01 * randomizer.nextGaussian());
        }

        final double[] fitted = fitter.fit();
        Assert.assertEquals(a, fitted[0], 7.6e-4);
        Assert.assertEquals(w, fitted[1], 2.7e-3);
        Assert.assertEquals(p, MathUtils.normalizeAngle(fitted[2], p), 1.3e-2);
    }

    @Test
    public void testTinyVariationsData() {
        Random randomizer = new Random(64925784252l);

        HarmonicFitter fitter =
            new HarmonicFitter(new LevenbergMarquardtOptimizer());
        for (double x = 0.0; x < 10.0; x += 0.1) {
            fitter.addObservedPoint(1, x, 1e-7 * randomizer.nextGaussian());
        }

        fitter.fit();
        // This test serves to cover the part of the code of "guessAOmega"
        // when the algorithm using integrals fails.
    }

    @Test
    public void testInitialGuess() {
        Random randomizer = new Random(45314242l);
        final double a = 0.2;
        final double w = 3.4;
        final double p = 4.1;
        HarmonicOscillator f = new HarmonicOscillator(a, w, p);

        HarmonicFitter fitter =
            new HarmonicFitter(new LevenbergMarquardtOptimizer());
        for (double x = 0.0; x < 10.0; x += 0.1) {
            fitter.addObservedPoint(1, x,
                                    f.value(x) + 0.01 * randomizer.nextGaussian());
        }

        final double[] fitted = fitter.fit(new double[] { 0.15, 3.6, 4.5 });
        Assert.assertEquals(a, fitted[0], 1.2e-3);
        Assert.assertEquals(w, fitted[1], 3.3e-3);
        Assert.assertEquals(p, MathUtils.normalizeAngle(fitted[2], p), 1.7e-2);
    }

    @Test
    public void testUnsorted() {
        Random randomizer = new Random(64925784252l);
        final double a = 0.2;
        final double w = 3.4;
        final double p = 4.1;
        HarmonicOscillator f = new HarmonicOscillator(a, w, p);

        HarmonicFitter fitter =
            new HarmonicFitter(new LevenbergMarquardtOptimizer());

        // build a regularly spaced array of measurements
        int size = 100;
        double[] xTab = new double[size];
        double[] yTab = new double[size];
        for (int i = 0; i < size; ++i) {
            xTab[i] = 0.1 * i;
            yTab[i] = f.value(xTab[i]) + 0.01 * randomizer.nextGaussian();
        }

        // shake it
        for (int i = 0; i < size; ++i) {
            int i1 = randomizer.nextInt(size);
            int i2 = randomizer.nextInt(size);
            double xTmp = xTab[i1];
            double yTmp = yTab[i1];
            xTab[i1] = xTab[i2];
            yTab[i1] = yTab[i2];
            xTab[i2] = xTmp;
            yTab[i2] = yTmp;
        }

        // pass it to the fitter
        for (int i = 0; i < size; ++i) {
            fitter.addObservedPoint(1, xTab[i], yTab[i]);
        }

        final double[] fitted = fitter.fit();
        Assert.assertEquals(a, fitted[0], 7.6e-4);
        Assert.assertEquals(w, fitted[1], 3.5e-3);
        Assert.assertEquals(p, MathUtils.normalizeAngle(fitted[2], p), 1.5e-2);
    }

    @Test(expected=MathIllegalStateException.class)
    public void testMath844() {
        final double[] y = { 0, 1, 2, 3, 2, 1,
                             0, -1, -2, -3, -2, -1,
                             0, 1, 2, 3, 2, 1,
                             0, -1, -2, -3, -2, -1,
                             0, 1, 2, 3, 2, 1, 0 };
        final int len = y.length;
        final WeightedObservedPoint[] points = new WeightedObservedPoint[len];
        for (int i = 0; i < len; i++) {
            points[i] = new WeightedObservedPoint(1, i, y[i]);
        }

        // The guesser fails because the function is far from an harmonic
        // function: It is a triangular periodic function with amplitude 3
        // and period 12, and all sample points are taken at integer abscissae
        // so function values all belong to the integer subset {-3, -2, -1, 0,
        // 1, 2, 3}.
        new HarmonicFitter.ParameterGuesser(points);
    }
}
