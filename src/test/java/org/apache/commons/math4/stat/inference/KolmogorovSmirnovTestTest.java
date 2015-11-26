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

package org.apache.commons.math4.stat.inference;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.apache.commons.math4.TestUtils;
import org.apache.commons.math4.distribution.NormalDistribution;
import org.apache.commons.math4.distribution.UniformRealDistribution;
import org.apache.commons.math4.random.RandomGenerator;
import org.apache.commons.math4.random.Well19937c;
import org.apache.commons.math4.stat.inference.KolmogorovSmirnovTest;
import org.apache.commons.math4.util.CombinatoricsUtils;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.math4.util.MathArrays;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link KolmogorovSmirnovTest}.
 *
 * @since 3.3
 */
public class KolmogorovSmirnovTestTest {

    protected static final double TOLERANCE = 10e-10;

    // Random N(0,1) values generated using R rnorm
    protected static final double[] gaussian = {
        0.26055895, -0.63665233, 1.51221323, 0.61246988, -0.03013003, -1.73025682, -0.51435805, 0.70494168, 0.18242945,
        0.94734336, -0.04286604, -0.37931719, -1.07026403, -2.05861425, 0.11201862, 0.71400136, -0.52122185,
        -0.02478725, -1.86811649, -1.79907688, 0.15046279, 1.32390193, 1.55889719, 1.83149171, -0.03948003,
        -0.98579207, -0.76790540, 0.89080682, 0.19532153, 0.40692841, 0.15047336, -0.58546562, -0.39865469, 0.77604271,
        -0.65188221, -1.80368554, 0.65273365, -0.75283102, -1.91022150, -0.07640869, -1.08681188, -0.89270600,
        2.09017508, 0.43907981, 0.10744033, -0.70961218, 1.15707300, 0.44560525, -2.04593349, 0.53816843, -0.08366640,
        0.24652218, 1.80549401, -0.99220707, -1.14589408, -0.27170290, -0.49696855, 0.00968353, -1.87113545,
        -1.91116529, 0.97151891, -0.73576115, -0.59437029, 0.72148436, 0.01747695, -0.62601157, -1.00971538,
        -1.42691397, 1.03250131, -0.30672627, -0.15353992, -1.19976069, -0.68364218, 0.37525652, -0.46592881,
        -0.52116168, -0.17162202, 1.04679215, 0.25165971, -0.04125231, -0.23756244, -0.93389975, 0.75551407,
        0.08347445, -0.27482228, -0.4717632, -0.1867746, -0.1166976, 0.5763333, 0.1307952, 0.7630584, -0.3616248,
        2.1383790, -0.7946630, 0.0231885, 0.7919195, 1.6057144, -0.3802508, 0.1229078, 1.5252901, -0.8543149, 0.3025040
    };

    // Random N(0, 1.6) values generated using R rnorm
    protected static final double[] gaussian2 = {
        2.88041498038308, -0.632349445671017, 0.402121295225571, 0.692626364613243, 1.30693446815426,
        -0.714176317131286, -0.233169206599583, 1.09113298322107, -1.53149079994305, 1.23259966205809,
        1.01389927412503, 0.0143898711497477, -0.512813545447559, 2.79364360835469, 0.662008875538092,
        1.04861546834788, -0.321280099931466, 0.250296656278743, 1.75820367603736, -2.31433523590905,
        -0.462694696086403, 0.187725700950191, -2.24410950019152, 2.83473751105445, 0.252460174391016,
        1.39051945380281, -1.56270144203134, 0.998522814471644, -1.50147469080896, 0.145307533554146,
        0.469089457043406, -0.0914780723809334, -0.123446939266548, -0.610513388160565, -3.71548343891957,
        -0.329577317349478, -0.312973794075871, 2.02051909758923, 2.85214308266271, 0.0193222002327237,
        -0.0322422268266562, 0.514736012106768, 0.231484953375887, -2.22468798953629, 1.42197716075595,
        2.69988043856357, 0.0443757119128293, 0.721536984407798, -0.0445688839903234, -0.294372724550705,
        0.234041580912698, -0.868973119365727, 1.3524893453845, -0.931054600134503, -0.263514296006792,
        0.540949457402918, -0.882544288773685, -0.34148675747989, 1.56664494810034, 2.19850536566584,
        -0.667972122928022, -0.70889669526203, -0.00251758193079668, 2.39527162977682, -2.7559594317269,
        -0.547393502656671, -2.62144031572617, 2.81504147017922, -1.02036850201042, -1.00713927602786,
        -0.520197775122254, 1.00625480138649, 2.46756916531313, 1.64364743727799, 0.704545210648595,
        -0.425885789416992, -1.78387854908546, -0.286783886710481, 0.404183648369076, -0.369324280845769,
        -0.0391185138840443, 2.41257787857293, 2.49744281317859, -0.826964496939021, -0.792555379958975,
        1.81097685787403, -0.475014580016638, 1.23387615291805, 0.646615294802053, 1.88496377454523, 1.20390698380814,
        -0.27812153371728, 2.50149494533101, 0.406964323253817, -1.72253451309982, 1.98432494184332, 2.2223658560333,
        0.393086362404685, -0.504073151377089, -0.0484610869883821
    };

    // Random uniform (0, 1) generated using R runif
    protected static final double[] uniform = {
        0.7930305, 0.6424382, 0.8747699, 0.7156518, 0.1845909, 0.2022326, 0.4877206, 0.8928752, 0.2293062, 0.4222006,
        0.1610459, 0.2830535, 0.9946345, 0.7329499, 0.26411126, 0.87958133, 0.29827437, 0.39185988, 0.38351185,
        0.36359611, 0.48646472, 0.05577866, 0.56152250, 0.52672013, 0.13171783, 0.95864085, 0.03060207, 0.33514887,
        0.72508148, 0.38901437, 0.9978665, 0.5981300, 0.1065388, 0.7036991, 0.1071584, 0.4423963, 0.1107071, 0.6437221,
        0.58523872, 0.05044634, 0.65999539, 0.37367260, 0.73270024, 0.47473755, 0.74661163, 0.50765549, 0.05377347,
        0.40998009, 0.55235182, 0.21361998, 0.63117971, 0.18109222, 0.89153510, 0.23203248, 0.6177106, 0.6856418,
        0.2158557, 0.9870501, 0.2036914, 0.2100311, 0.9065020, 0.7459159, 0.56631790, 0.06753629, 0.39684629,
        0.52504615, 0.14199103, 0.78551120, 0.90503321, 0.80452362, 0.9960115, 0.8172592, 0.5831134, 0.8794187,
        0.2021501, 0.2923505, 0.9561824, 0.8792248, 0.85201008, 0.02945562, 0.26200374, 0.11382818, 0.17238856,
        0.36449473, 0.69688273, 0.96216330, 0.4859432, 0.4503438, 0.1917656, 0.8357845, 0.9957812, 0.4633570,
        0.8654599, 0.4597996, 0.68190289, 0.58887855, 0.09359396, 0.98081979, 0.73659533, 0.89344777, 0.18903099,
        0.97660425
    };

    /** Unit normal distribution, unit normal data */
    @Test
    public void testOneSampleGaussianGaussian() {
        final KolmogorovSmirnovTest test = new KolmogorovSmirnovTest();
        final NormalDistribution unitNormal = new NormalDistribution(0d, 1d);
        // Uncomment to run exact test - takes about a minute. Same value is used in R tests and for
        // approx.
        // Assert.assertEquals(0.3172069207622391, test.kolmogorovSmirnovTest(unitNormal, gaussian,
        // true), TOLERANCE);
        Assert.assertEquals(0.3172069207622391, test.kolmogorovSmirnovTest(unitNormal, gaussian, false), TOLERANCE);
        Assert.assertFalse(test.kolmogorovSmirnovTest(unitNormal, gaussian, 0.05));
        Assert.assertEquals(0.0932947561266756, test.kolmogorovSmirnovStatistic(unitNormal, gaussian), TOLERANCE);
    }

    /** Unit normal distribution, unit normal data, small dataset */
    @Test
    public void testOneSampleGaussianGaussianSmallSample() {
        final KolmogorovSmirnovTest test = new KolmogorovSmirnovTest();
        final NormalDistribution unitNormal = new NormalDistribution(0d, 1d);
        final double[] shortGaussian = new double[50];
        System.arraycopy(gaussian, 0, shortGaussian, 0, 50);
        Assert.assertEquals(0.683736463728347, test.kolmogorovSmirnovTest(unitNormal, shortGaussian, false), TOLERANCE);
        Assert.assertFalse(test.kolmogorovSmirnovTest(unitNormal, gaussian, 0.05));
        Assert.assertEquals(0.09820779969463278, test.kolmogorovSmirnovStatistic(unitNormal, shortGaussian), TOLERANCE);
    }

    /** Unit normal distribution, uniform data */
    @Test
    public void testOneSampleGaussianUniform() {
        final KolmogorovSmirnovTest test = new KolmogorovSmirnovTest();
        final NormalDistribution unitNormal = new NormalDistribution(0d, 1d);
        // Uncomment to run exact test - takes a long time. Same value is used in R tests and for
        // approx.
        // Assert.assertEquals(0.3172069207622391, test.kolmogorovSmirnovTest(unitNormal, uniform,
        // true), TOLERANCE);
        Assert.assertEquals(8.881784197001252E-16, test.kolmogorovSmirnovTest(unitNormal, uniform, false), TOLERANCE);
        Assert.assertFalse(test.kolmogorovSmirnovTest(unitNormal, gaussian, 0.05));
        Assert.assertEquals(0.5117493931609258, test.kolmogorovSmirnovStatistic(unitNormal, uniform), TOLERANCE);
    }

    /** Uniform distribution, uniform data */
    // @Test - takes about 6 seconds, uncomment for
    public void testOneSampleUniformUniform() {
        final KolmogorovSmirnovTest test = new KolmogorovSmirnovTest();
        final UniformRealDistribution unif = new UniformRealDistribution(-0.5, 0.5);
        Assert.assertEquals(8.881784197001252E-16, test.kolmogorovSmirnovTest(unif, uniform, false), TOLERANCE);
        Assert.assertTrue(test.kolmogorovSmirnovTest(unif, uniform, 0.05));
        Assert.assertEquals(0.5400666982352942, test.kolmogorovSmirnovStatistic(unif, uniform), TOLERANCE);
    }

    /** Uniform distribution, uniform data, small dataset */
    @Test
    public void testOneSampleUniformUniformSmallSample() {
        final KolmogorovSmirnovTest test = new KolmogorovSmirnovTest();
        final UniformRealDistribution unif = new UniformRealDistribution(-0.5, 0.5);
        final double[] shortUniform = new double[20];
        System.arraycopy(uniform, 0, shortUniform, 0, 20);
        Assert.assertEquals(4.117594598618268E-9, test.kolmogorovSmirnovTest(unif, shortUniform, false), TOLERANCE);
        Assert.assertTrue(test.kolmogorovSmirnovTest(unif, shortUniform, 0.05));
        Assert.assertEquals(0.6610459, test.kolmogorovSmirnovStatistic(unif, shortUniform), TOLERANCE);
    }

    /** Uniform distribution, unit normal dataset */
    @Test
    public void testOneSampleUniformGaussian() {
        final KolmogorovSmirnovTest test = new KolmogorovSmirnovTest();
        final UniformRealDistribution unif = new UniformRealDistribution(-0.5, 0.5);
        // Value was obtained via exact test, validated against R. Running exact test takes a long
        // time.
        Assert.assertEquals(4.9405812774239166E-11, test.kolmogorovSmirnovTest(unif, gaussian, false), TOLERANCE);
        Assert.assertTrue(test.kolmogorovSmirnovTest(unif, gaussian, 0.05));
        Assert.assertEquals(0.3401058049019608, test.kolmogorovSmirnovStatistic(unif, gaussian), TOLERANCE);
    }

    /** Small samples - exact p-value, checked against R */
    @Test
    public void testTwoSampleSmallSampleExact() {
        final KolmogorovSmirnovTest test = new KolmogorovSmirnovTest();
        final double[] smallSample1 = {
            6, 7, 9, 13, 19, 21, 22, 23, 24
        };
        final double[] smallSample2 = {
            10, 11, 12, 16, 20, 27, 28, 32, 44, 54
        };
        // Reference values from R, version 2.15.3 - R uses non-strict inequality in null hypothesis
        Assert
            .assertEquals(0.105577085453247, test.kolmogorovSmirnovTest(smallSample1, smallSample2, false), TOLERANCE);
        Assert.assertEquals(0.5, test.kolmogorovSmirnovStatistic(smallSample1, smallSample2), TOLERANCE);
    }

    /**
     * Checks exact p-value computations using critical values from Table 9 in V.K Rohatgi, An
     * Introduction to Probability and Mathematical Statistics, Wiley, 1976, ISBN 0-471-73135-8.
     */
    @Test
    public void testTwoSampleExactP() {
        checkExactTable(4, 6, 5d / 6d, 0.01d);
        checkExactTable(4, 7, 17d / 28d, 0.2d);
        checkExactTable(6, 7, 29d / 42d, 0.05d);
        checkExactTable(4, 10, 7d / 10d, 0.05d);
        checkExactTable(5, 15, 11d / 15d, 0.02d);
        checkExactTable(9, 10, 31d / 45d, 0.01d);
        checkExactTable(7, 10, 43d / 70d, 0.05d);
    }

    @Test
    public void testTwoSampleApproximateCritialValues() {
        final double tol = .01;
        final double[] alpha = {
            0.10, 0.05, 0.025, 0.01, 0.005, 0.001
        };
        // From Wikipedia KS article - TODO: get (and test) more precise values
        final double[] c = {
            1.22, 1.36, 1.48, 1.63, 1.73, 1.95
        };
        final int k[] = {
            60, 100, 500
        };
        double n;
        double m;
        for (int i = 0; i < k.length; i++) {
            for (int j = 0; j < i; j++) {
                n = k[i];
                m = k[j];
                for (int l = 0; l < alpha.length; l++) {
                    final double dCrit = c[l] * FastMath.sqrt((n + m) / (n * m));
                    checkApproximateTable(k[i], k[j], dCrit, alpha[l], tol);
                }
            }
        }
    }

    @Test
    public void testPelzGoodApproximation() {
        KolmogorovSmirnovTest ksTest = new KolmogorovSmirnovTest();
        final double d[] = {0.15, 0.20, 0.25, 0.3, 0.35, 0.4};
        final int n[] = {141, 150, 180, 220, 1000};
        // Reference values computed using the Pelz method from
        // http://simul.iro.umontreal.ca/ksdir/KolmogorovSmirnovDist.java
        final double ref[] = {
            0.9968940168727819, 0.9979326624184857, 0.9994677598604506, 0.9999128354780209, 0.9999999999998661,
            0.9999797514476236, 0.9999902122242081, 0.9999991327060908, 0.9999999657681911, 0.9999999999977929,
            0.9999999706444976, 0.9999999906571532, 0.9999999997949596, 0.999999999998745, 0.9999999999993876,
            0.9999999999916627, 0.9999999999984447, 0.9999999999999936, 0.999999999999341, 0.9999999999971508,
            0.9999999999999877, 0.9999999999999191, 0.9999999999999254, 0.9999999999998178, 0.9999999999917788,
            0.9999999999998556, 0.9999999999992014, 0.9999999999988859, 0.9999999999999325, 0.9999999999821726
        };

        final double tol = 10e-15;
        int k = 0;
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++, k++) {
                Assert.assertEquals(ref[k], ksTest.pelzGood(d[i], n[j]), tol);
            }
        }
    }

    /** Verifies large sample approximate p values against R */
    @Test
    public void testTwoSampleApproximateP() {
        final KolmogorovSmirnovTest test = new KolmogorovSmirnovTest();
        // Reference values from R, version 2.15.3
        Assert.assertEquals(0.0319983962391632, test.kolmogorovSmirnovTest(gaussian, gaussian2), TOLERANCE);
        Assert.assertEquals(0.202352941176471, test.kolmogorovSmirnovStatistic(gaussian, gaussian2), TOLERANCE);
    }

    /**
     * MATH-1181
     * Verify that large sample method is selected for sample product > Integer.MAX_VALUE
     * (integer overflow in sample product)
     */
    @Test(timeout=5000)
    public void testTwoSampleProductSizeOverflow() {
        final int n = 50000;
        Assert.assertTrue(n * n < 0);
        double[] x = new double[n];
        double[] y = new double[n];
        final KolmogorovSmirnovTest test = new KolmogorovSmirnovTest();
        Assert.assertFalse(Double.isNaN(test.kolmogorovSmirnovTest(x, y)));
    }

    /**
     * Verifies that Monte Carlo simulation gives results close to exact p values. This test is a
     * little long-running (more than two minutes on a fast machine), so is disabled by default.
     */
    // @Test
    public void testTwoSampleMonteCarlo() {
        final KolmogorovSmirnovTest test = new KolmogorovSmirnovTest(new Well19937c(1000));
        final int sampleSize = 14;
        final double tol = .001;
        final double[] shortUniform = new double[sampleSize];
        System.arraycopy(uniform, 0, shortUniform, 0, sampleSize);
        final double[] shortGaussian = new double[sampleSize];
        final double[] shortGaussian2 = new double[sampleSize];
        System.arraycopy(gaussian, 0, shortGaussian, 0, sampleSize);
        System.arraycopy(gaussian, 10, shortGaussian2, 0, sampleSize);
        final double[] d = {
            test.kolmogorovSmirnovStatistic(shortGaussian, shortUniform),
            test.kolmogorovSmirnovStatistic(shortGaussian2, shortGaussian)
        };
        for (double dv : d) {
            double exactPStrict = test.exactP(dv, sampleSize, sampleSize, true);
            double exactPNonStrict = test.exactP(dv, sampleSize, sampleSize, false);
            double montePStrict = test.monteCarloP(dv, sampleSize, sampleSize, true,
                                                   KolmogorovSmirnovTest.MONTE_CARLO_ITERATIONS);
            double montePNonStrict = test.monteCarloP(dv, sampleSize, sampleSize, false,
                                                      KolmogorovSmirnovTest.MONTE_CARLO_ITERATIONS);
            Assert.assertEquals(exactPStrict, montePStrict, tol);
            Assert.assertEquals(exactPNonStrict, montePNonStrict, tol);
        }
    }

    @Test
    public void testTwoSampleMonteCarloDifferentSampleSizes() {
        final KolmogorovSmirnovTest test = new KolmogorovSmirnovTest(new Well19937c(1000));
        final int sampleSize1 = 14;
        final int sampleSize2 = 7;
        final double d = 0.3;
        final boolean strict = false;
        final double tol = 1e-2;
        Assert.assertEquals(test.exactP(d, sampleSize1, sampleSize2, strict),
                            test.monteCarloP(d, sampleSize1, sampleSize2, strict,
                                             KolmogorovSmirnovTest.MONTE_CARLO_ITERATIONS),
                            tol);
    }

    /**
     * Performance test for monteCarlo method. Disabled by default.
     */
    // @Test
    public void testTwoSampleMonteCarloPerformance() {
        int numIterations = 100_000;
        int N = (int)Math.sqrt(KolmogorovSmirnovTest.LARGE_SAMPLE_PRODUCT);
        final KolmogorovSmirnovTest test = new KolmogorovSmirnovTest(new Well19937c(1000));
        for (int n = 2; n <= N; ++n) {
            long startMillis = System.currentTimeMillis();
            int m = KolmogorovSmirnovTest.LARGE_SAMPLE_PRODUCT/n;
            Assert.assertEquals(0d, test.monteCarloP(Double.POSITIVE_INFINITY, n, m, true, numIterations), 0d);
            long endMillis = System.currentTimeMillis();
            System.out.println("n=" + n + ", m=" + m + ", time=" + (endMillis-startMillis)/1000d + "s");
        }
    }

    @Test
    public void testTwoSampleWithManyTies() {
        // MATH-1197
        final double[] x = {
            0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
            0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
            0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
            0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
            0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
            0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
            0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
            0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
            0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
            0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
            0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
            0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
            0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
            0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
            0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
            0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
            0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
            0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
            0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
            0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
            0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
            0.000000, 2.202653, 2.202653, 2.202653, 2.202653, 2.202653,
            2.202653, 2.202653, 2.202653, 2.202653, 2.202653, 2.202653,
            2.202653, 2.202653, 2.202653, 2.202653, 2.202653, 2.202653,
            2.202653, 2.202653, 2.202653, 2.202653, 2.202653, 2.202653,
            2.202653, 2.202653, 2.202653, 2.202653, 2.202653, 2.202653,
            2.202653, 2.202653, 2.202653, 2.202653, 2.202653, 2.202653,
            3.181199, 3.181199, 3.181199, 3.181199, 3.181199, 3.181199,
            3.723539, 3.723539, 3.723539, 3.723539, 4.383482, 4.383482,
            4.383482, 4.383482, 5.320671, 5.320671, 5.320671, 5.717284,
            6.964001, 7.352165, 8.710510, 8.710510, 8.710510, 8.710510,
            8.710510, 8.710510, 9.539004, 9.539004, 10.720619, 17.726077,
            17.726077, 17.726077, 17.726077, 22.053875, 23.799144, 27.355308,
            30.584960, 30.584960, 30.584960, 30.584960, 30.751808
        };

        final double[] y = {
            0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
            0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
            0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
            0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
            0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
            0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
            0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
            0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000,
            0.000000, 0.000000, 0.000000, 2.202653, 2.202653, 2.202653,
            2.202653, 2.202653, 2.202653, 2.202653, 2.202653, 3.061758,
            3.723539, 5.628420, 5.628420, 5.628420, 5.628420, 5.628420,
            6.916982, 6.916982, 6.916982, 10.178538, 10.178538, 10.178538,
            10.178538, 10.178538
        };

        final KolmogorovSmirnovTest test = new KolmogorovSmirnovTest();

        Assert.assertEquals(0.0640394088, test.kolmogorovSmirnovStatistic(x, y), 1e-6);
        Assert.assertEquals(0.9792777290, test.kolmogorovSmirnovTest(x, y), 1e-6);

    }

    @Test
    public void testTwoSamplesAllEqual() {
        int iterations = 10_000;
        final KolmogorovSmirnovTest test = new KolmogorovSmirnovTest();
        for (int i = 2; i < 30; ++i) {
            // testing values with ties
            double[] values = new double[i];
            Arrays.fill(values, i);
            // testing values without ties
            double[] ascendingValues = new double[i];
            for (int j = 0; j < ascendingValues.length; j++) {
                ascendingValues[j] = j;
            }

            Assert.assertEquals(0., test.kolmogorovSmirnovStatistic(values, values), 0.);
            Assert.assertEquals(0., test.kolmogorovSmirnovStatistic(ascendingValues, ascendingValues), 0.);

            if (i < 10) {
                Assert.assertEquals(1.0, test.exactP(0, values.length, values.length, true), 0.);
                Assert.assertEquals(1.0, test.exactP(0, values.length, values.length, false), 0.);
            }

            Assert.assertEquals(1.0, test.monteCarloP(0, values.length, values.length, true, iterations), 0.);
            Assert.assertEquals(1.0, test.monteCarloP(0, values.length, values.length, false, iterations), 0.);

            Assert.assertEquals(1.0, test.approximateP(0, values.length, values.length), 0.);
            Assert.assertEquals(1.0, test.approximateP(0, values.length, values.length), 0.);
        }
    }

    /**
     * JIRA: MATH-1245
     *
     * Verify that D-values are not viewed as distinct when they are mathematically equal
     * when computing p-statistics for small sample tests. Reference values are from R 3.2.0.
     */
    @Test
    public void testDRounding() {
        final double tol = 1e-12;
        final double[] x = {0, 2, 3, 4, 5, 6, 7, 8, 9, 12};
        final double[] y = {1, 10, 11, 13, 14, 15, 16, 17, 18};
        final KolmogorovSmirnovTest test = new KolmogorovSmirnovTest();
        Assert.assertEquals(0.0027495724090154106, test.kolmogorovSmirnovTest(x, y,false), tol);

        final double[] x1 = {2, 4, 6, 8, 9, 10, 11, 12, 13};
        final double[] y1 = {0, 1, 3, 5, 7};
        Assert.assertEquals(0.085914085914085896, test.kolmogorovSmirnovTest(x1, y1, false), tol);

        final double[] x2 = {4, 6, 7, 8, 9, 10, 11};
        final double[] y2 = {0, 1, 2, 3, 5};
        Assert.assertEquals(0.015151515151515027, test.kolmogorovSmirnovTest(x2, y2, false), tol);
    }

    /**
     * JIRA: MATH-1245
     *
     * Verify that D-values are not viewed as distinct when they are mathematically equal
     * when computing p-statistics for small sample tests. Reference values are from R 3.2.0.
     */
    @Test
    public void testDRoundingMonteCarlo() {
        final double tol = 1e-2;
        final int iterations = 1000000;
        final KolmogorovSmirnovTest test = new KolmogorovSmirnovTest(new Well19937c(1000));

        final double[] x = {0, 2, 3, 4, 5, 6, 7, 8, 9, 12};
        final double[] y = {1, 10, 11, 13, 14, 15, 16, 17, 18};
        double d = test.kolmogorovSmirnovStatistic(x, y);
        Assert.assertEquals(0.0027495724090154106, test.monteCarloP(d, x.length, y.length, false, iterations), tol);

        final double[] x1 = {2, 4, 6, 8, 9, 10, 11, 12, 13};
        final double[] y1 = {0, 1, 3, 5, 7};
        d = test.kolmogorovSmirnovStatistic(x1, y1);
        Assert.assertEquals(0.085914085914085896, test.monteCarloP(d, x1.length, y1.length, false, iterations), tol);

        final double[] x2 = {4, 6, 7, 8, 9, 10, 11};
        final double[] y2 = {0, 1, 2, 3, 5};
        d = test.kolmogorovSmirnovStatistic(x2, y2);
        Assert.assertEquals(0.015151515151515027, test.monteCarloP(d, x2.length, y2.length, false, iterations), tol);
    }

    @Test
    public void testFillBooleanArrayRandomlyWithFixedNumberTrueValues() {

        final int[][] parameters = {{5, 1}, {5, 2}, {5, 3}, {5, 4}, {8, 1}, {8, 2}, {8, 3}, {8, 4}, {8, 5}, {8, 6}, {8, 7}};

        final double alpha = 0.001;
        final int numIterations = 1000000;

        final RandomGenerator rng = new Well19937c(0);

        for (final int[] parameter : parameters) {

            final int arraySize = parameter[0];
            final int numberOfTrueValues = parameter[1];

            final boolean[] b = new boolean[arraySize];
            final long[] counts = new long[1 << arraySize];

            for (int i = 0; i < numIterations; ++i) {
                KolmogorovSmirnovTest.fillBooleanArrayRandomlyWithFixedNumberTrueValues(b, numberOfTrueValues, rng);
                int x = 0;
                for (int j = 0; j < arraySize; ++j) {
                    x = ((x << 1) | ((b[j])?1:0));
                }
                counts[x] += 1;
            }

            final int numCombinations = (int) CombinatoricsUtils.binomialCoefficient(arraySize, numberOfTrueValues);

            final long[] observed = new long[numCombinations];
            final double[] expected = new double[numCombinations];
            Arrays.fill(expected, numIterations / (double) numCombinations);

            int observedIdx = 0;

            for (int i = 0; i < (1 << arraySize); ++i) {
                if (Integer.bitCount(i) == numberOfTrueValues) {
                    observed[observedIdx] = counts[i];
                    observedIdx += 1;
                }
                else {
                    Assert.assertEquals(0, counts[i]);
                }
            }

            Assert.assertEquals(numCombinations, observedIdx);
            TestUtils.assertChiSquareAccept(expected, observed, alpha);
        }
    }

    /**
     * Test an example with ties in the data.  Reference data is R 3.2.0,
     * ks.boot implemented in Matching (Version 4.8-3.4, Build Date: 2013/10/28)
     */
    @Test
    public void testBootstrapSmallSamplesWithTies() {
        final double[] x = {0, 2, 4, 6, 8, 8, 10, 15, 22, 30, 33, 36, 38};
        final double[] y = {9, 17, 20, 33, 40, 51, 60, 60, 72, 90, 101};
        final KolmogorovSmirnovTest test = new KolmogorovSmirnovTest(new Well19937c(2000));
        Assert.assertEquals(0.0059, test.bootstrap(x, y, 10000, false), 1E-3);
    }

    /**
     * Reference data is R 3.2.0, ks.boot implemented in
     * Matching (Version 4.8-3.4, Build Date: 2013/10/28)
     */
    @Test
    public void testBootstrapLargeSamples() {
        final KolmogorovSmirnovTest test = new KolmogorovSmirnovTest(new Well19937c(1000));
        Assert.assertEquals(0.0237, test.bootstrap(gaussian, gaussian2, 10000), 1E-2);
    }

    /**
     * Test an example where D-values are close (subject to rounding).
     * Reference data is R 3.2.0, ks.boot implemented in
     * Matching (Version 4.8-3.4, Build Date: 2013/10/28)
     */
    @Test
    public void testBootstrapRounding() {
        final double[] x = {2,4,6,8,9,10,11,12,13};
        final double[] y = {0,1,3,5,7};
        final KolmogorovSmirnovTest test = new KolmogorovSmirnovTest(new Well19937c(1000));
        Assert.assertEquals(0.06303, test.bootstrap(x, y, 10000, false), 1E-2);
    }

    @Test
    public void testFixTiesNoOp() throws Exception {
        final double[] x = {0, 1, 2, 3, 4};
        final double[] y = {5, 6, 7, 8};
        final double[] origX = MathArrays.copyOf(x);
        final double[] origY = MathArrays.copyOf(y);
        fixTies(x,y);
        Assert.assertArrayEquals(origX, x, 0);
        Assert.assertArrayEquals(origY, y, 0);
    }

    /**
     * Verify that fixTies is deterministic, i.e,
     * x = x', y = y' => fixTies(x,y) = fixTies(x', y')
     */
    @Test
    public void testFixTiesConsistency() throws Exception {
        final double[] x = {0, 1, 2, 3, 4, 2};
        final double[] y = {5, 6, 7, 8, 1, 2};
        final double[] xP = MathArrays.copyOf(x);
        final double[] yP = MathArrays.copyOf(y);
        checkFixTies(x, y);
        final double[] fixedX = MathArrays.copyOf(x);
        final double[] fixedY = MathArrays.copyOf(y);
        checkFixTies(xP, yP);
        Assert.assertArrayEquals(fixedX, xP, 0);
        Assert.assertArrayEquals(fixedY,  yP, 0);
    }

    @Test
    public void testFixTies() throws Exception {
        checkFixTies(new double[] {0, 1, 1, 4, 0}, new double[] {0, 5, 0.5, 0.55, 7});
        checkFixTies(new double[] {1, 1, 1, 1, 1}, new double[] {1, 1});
        checkFixTies(new double[] {1, 2, 3}, new double[] {1});
        checkFixTies(new double[] {1, 1, 0, 1, 0}, new double[] {});
    }

    /**
     * Checks that fixTies eliminates ties in the data but does not otherwise
     * perturb the ordering.
     */
    private void checkFixTies(double[] x, double[] y) throws Exception {
        final double[] origCombined = MathArrays.concatenate(x, y);
        fixTies(x, y);
        Assert.assertFalse(hasTies(x, y));
        final double[] combined = MathArrays.concatenate(x, y);
        for (int i = 0; i < combined.length; i++) {
            for (int j = 0; j < i; j++) {
                Assert.assertTrue(combined[i] != combined[j]);
                if (combined[i] < combined[j])
                    Assert.assertTrue(origCombined[i] < origCombined[j]
                                          || origCombined[i] == origCombined[j]);
            }

        }
    }

    /**
     * Verifies the inequality exactP(criticalValue, n, m, true) < alpha < exactP(criticalValue, n,
     * m, false).
     *
     * Note that the validity of this check depends on the fact that alpha lies strictly between two
     * attained values of the distribution and that criticalValue is one of the attained values. The
     * critical value table (reference below) uses attained values. This test therefore also
     * verifies that criticalValue is attained.
     *
     * @param n first sample size
     * @param m second sample size
     * @param criticalValue critical value
     * @param alpha significance level
     */
    private void checkExactTable(int n, int m, double criticalValue, double alpha) {
        final KolmogorovSmirnovTest test = new KolmogorovSmirnovTest();
        Assert.assertTrue(test.exactP(criticalValue, n, m, true) < alpha);
        Assert.assertTrue(test.exactP(criticalValue, n, m, false) > alpha);
    }

    /**
     * Verifies that approximateP(criticalValue, n, m) is within epsilon of alpha.
     *
     * @param n first sample size
     * @param m second sample size
     * @param criticalValue critical value (from table)
     * @param alpha significance level
     * @param epsilon tolerance
     */
    private void checkApproximateTable(int n, int m, double criticalValue, double alpha, double epsilon) {
        final KolmogorovSmirnovTest test = new KolmogorovSmirnovTest();
        Assert.assertEquals(alpha, test.approximateP(criticalValue, n, m), epsilon);
    }

    /**
     * Reflection hack to expose private fixTies method for testing.
     */
    private static void fixTies(double[] x, double[] y) throws Exception {
        Method method = KolmogorovSmirnovTest.class.getDeclaredMethod("fixTies",
                                             double[].class, double[].class);
        method.setAccessible(true);
        method.invoke(KolmogorovSmirnovTest.class, x, y);
    }

    /**
     * Reflection hack to expose private hasTies method.
     */
    private static boolean hasTies(double[] x, double[] y) throws Exception {
        Method method = KolmogorovSmirnovTest.class.getDeclaredMethod("hasTies",
                                               double[].class, double[].class);
        method.setAccessible(true);
        return (boolean) method.invoke(KolmogorovSmirnovTest.class, x, y);
    }

}
