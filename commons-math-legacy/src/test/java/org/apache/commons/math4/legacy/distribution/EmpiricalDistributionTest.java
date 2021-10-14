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
package org.apache.commons.math4.legacy.distribution;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.statistics.distribution.ContinuousDistribution;
import org.apache.commons.statistics.distribution.UniformContinuousDistribution;
import org.apache.commons.statistics.distribution.NormalDistribution;
import org.apache.commons.statistics.distribution.ExponentialDistribution;
import org.apache.commons.math4.legacy.TestUtils;
import org.apache.commons.math4.legacy.analysis.UnivariateFunction;
import org.apache.commons.math4.legacy.analysis.integration.BaseAbstractUnivariateIntegrator;
import org.apache.commons.math4.legacy.analysis.integration.IterativeLegendreGaussIntegrator;
import org.apache.commons.math4.legacy.exception.NotStrictlyPositiveException;
import org.apache.commons.math4.legacy.stat.descriptive.SummaryStatistics;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for the {@link EmpiricalDistribution} class.
 */
public final class EmpiricalDistributionTest extends RealDistributionAbstractTest {
    private EmpiricalDistribution empiricalDistribution = null;
    private double[] dataArray = null;
    private final int n = 10000;
    /** Uniform bin mass = 10/10001 == mass of all but the first bin */
    private final double binMass = 10d / (n + 1);
    /** Mass of first bin = 11/10001 */
    private final double firstBinMass = 11d / (n + 1);

    @Override
    @Before
    public void setUp() {
        super.setUp();

        final URL url = getClass().getResource("testData.txt");
        final ArrayList<Double> list = new ArrayList<>();
        try {
            final BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str = null;
            while ((str = in.readLine()) != null) {
                list.add(Double.valueOf(str));
            }
            in.close();
        } catch (IOException ex) {
            Assert.fail("IOException " + ex);
        }

        dataArray = new double[list.size()];
        int i = 0;
        for (Double data : list) {
            dataArray[i] = data.doubleValue();
            i++;
        }

        empiricalDistribution = EmpiricalDistribution.from(100, dataArray);
    }

    // MATH-1279
    @Test(expected=NotStrictlyPositiveException.class)
    public void testPrecondition1() {
        EmpiricalDistribution.from(0, new double[] {1,2,3});
    }

    /**
     * Test using data taken from sample data file.
     * Check that the sampleCount, mu and sigma match data in the sample data file.
     */
    @Test
    public void testDoubleLoad() {
        // testData File has 10000 values, with mean ~ 5.0, std dev ~ 1
        // Make sure that loaded distribution matches this
        Assert.assertEquals(empiricalDistribution.getSampleStats().getN(),
                            1000, 1e-7);
        //TODO: replace with statistical tests
        Assert.assertEquals(empiricalDistribution.getSampleStats().getMean(),
                            5.069831575018909, 1e-7);
        Assert.assertEquals(empiricalDistribution.getSampleStats().getStandardDeviation(),
                            1.0173699343977738, 1e-7);

        double[] bounds = empiricalDistribution.getGeneratorUpperBounds();
        Assert.assertEquals(bounds.length, 100);
        Assert.assertEquals(bounds[99], 1.0, 10e-12);
    }

    // MATH-1531
    @Test
    public void testMath1531() {
        final double[] data = new double[] {
            50.993456376721454, 49.455345691918055, 49.527276095295804, 50.017183448668845, 49.10508147470046,
            49.813998274118696, 50.87195348756139, 50.419474110037, 50.63614906979689, 49.49694777179407,
            50.71799078406067, 50.03192853759164, 49.915092423165994, 49.56895392597687, 51.034638001064934,
            50.681227971275945, 50.43749845081759, 49.86513120270245, 50.21475262482965, 49.99202971042547,
            50.02382189838519, 49.386888585302884, 49.45585010202781, 49.988009479855435, 49.8136712206123,
            49.6715197127997, 50.1981278397565, 49.842297508010276, 49.62491227740015, 50.05101916097176,
            48.834912763303926, 49.806787657848574, 49.478236106374695, 49.56648347371614, 49.95069238081982,
            49.71845132077346, 50.6097468705947, 49.80724637775541, 49.90448813086025, 49.39641861662603,
            50.434295712893714, 49.227176959566734, 49.541126466050905, 49.03416593170446, 49.11584328494423,
            49.61387482435674, 49.92877857995328, 50.70638552955101, 50.60078208448842, 49.39326233277838,
            49.21488424364095, 49.69503351015096, 50.13733214001718, 50.22084761458942, 51.09804435604931,
            49.18559131120419, 49.52286371605357, 49.34804374996689, 49.6901827776375, 50.01316351359638,
            48.7751460520373, 50.12961836291053, 49.9978419772511, 49.885658399408584, 49.673438879979834,
            49.45565980965606, 50.429747484906564, 49.40129274804164, 50.13034614008073, 49.87685735146651,
            50.12967905393557, 50.323560376181696, 49.83519233651367, 49.37333369733053, 49.70074301611427,
            50.11626105774947, 50.28249500380083, 50.543354367136466, 50.05866241335002, 50.39516515672527,
            49.4838561463057, 50.451757089234796, 50.31370674203726, 49.79063762614284, 50.19652349768548,
            49.75881420748814, 49.98371855036422, 49.82171344472916, 48.810793204162415, 49.37040569084592,
            50.050641186203976, 50.48360952263646, 50.86666450358076, 50.463268776129844, 50.137489751888666,
            50.23823061444118, 49.881460479468004, 50.641174398764356, 49.09314136851421, 48.80877928574451,
            50.46197084844826, 49.97691704141741, 49.99933997561926, 50.25692254481885, 49.52973451252715,
            49.81229858420664, 48.996112655915994, 48.740531054814674, 50.026642633066416, 49.98696633604899,
            49.61307159972952, 50.5115278979726, 50.75245152442404, 50.51807785445929, 49.60929671768147,
            49.1079533564074, 49.65347196551866, 49.31684818724059, 50.4906368627049, 50.37483603684714
        };

        EmpiricalDistribution.from(120, data).inverseCumulativeProbability(0.7166666666666669);
    }

    /**
      * Generate 1000 random values and make sure they look OK.<br>
      * Note that there is a non-zero (but very small) probability that
      * these tests will fail even if the code is working as designed.
      */
    @Test
    public void testNext() {
        tstGen(empiricalDistribution,
               0.1);
    }

    /**
     * Make sure we can handle a grid size that is too fine
     */
    @Test
    public void testGridTooFine() {
        tstGen(EmpiricalDistribution.from(1001, dataArray),
               0.1);
    }

    /**
     * How about too fat?
     */
    @Test
    public void testGridTooFat() {
        tstGen(EmpiricalDistribution.from(1, dataArray),
               5); // ridiculous tolerance; but ridiculous grid size
                   // really just checking to make sure we do not bomb
    }

    /**
     * Test bin index overflow problem (BZ 36450)
     */
    @Test
    public void testBinIndexOverflow() {
        double[] x = new double[] {9474.94326071674, 2080107.8865462579};
        EmpiricalDistribution.from(1000, x);
    }

    @Test(expected=NullPointerException.class)
    public void testLoadNullDoubleArray() {
        EmpiricalDistribution.from(1000, null);
    }

    /**
     * MATH-298
     */
    @Test
    public void testGetBinUpperBounds() {
        double[] testData = {0, 1, 1, 2, 3, 4, 4, 5, 6, 7, 8, 9, 10};
        EmpiricalDistribution dist = EmpiricalDistribution.from(5, testData);
        double[] expectedBinUpperBounds = {2, 4, 6, 8, 10};
        double[] expectedGeneratorUpperBounds = {4d/13d, 7d/13d, 9d/13d, 11d/13d, 1};
        double tol = 10E-12;
        TestUtils.assertEquals(expectedBinUpperBounds, dist.getUpperBounds(), tol);
        TestUtils.assertEquals(expectedGeneratorUpperBounds, dist.getGeneratorUpperBounds(), tol);
    }

    private void verifySame(EmpiricalDistribution d1,
                            EmpiricalDistribution d2) {
        Assert.assertEquals(d1.getBinCount(), d2.getBinCount());
        Assert.assertEquals(d1.getSampleStats(), d2.getSampleStats());

        for (int i = 0;  i < d1.getUpperBounds().length; i++) {
            Assert.assertEquals(d1.getUpperBounds()[i], d2.getUpperBounds()[i], 0);
        }
        Assert.assertEquals(d1.getBinStats(), d2.getBinStats());
    }

    private void tstGen(EmpiricalDistribution dist,
                        double tolerance) {
        final ContinuousDistribution.Sampler sampler
            = dist.createSampler(RandomSource.WELL_19937_C.create(1000));
        final SummaryStatistics stats = new SummaryStatistics();
        for (int i = 1; i < 1000; i++) {
            stats.addValue(sampler.sample());
        }
        Assert.assertEquals("mean", 5.069831575018909, stats.getMean(),tolerance);
        Assert.assertEquals("std dev", 1.0173699343977738, stats.getStandardDeviation(),tolerance);
    }

    //  Setup for distribution tests

    @Override
    public ContinuousDistribution makeDistribution() {
        // Create a uniform distribution on [0, 10,000].
        final double[] sourceData = new double[n + 1];
        for (int i = 0; i < n + 1; i++) {
            sourceData[i] = i;
        }
        EmpiricalDistribution dist = EmpiricalDistribution.from(1000, sourceData);
        return dist;
    }

    @Override
    public double[] makeCumulativeTestPoints() {
       final double[] testPoints = new double[] {9, 10, 15, 1000, 5004, 9999};
       return testPoints;
    }


    @Override
    public double[] makeCumulativeTestValues() {
        /*
         * Bins should be [0, 10], (10, 20], ..., (9990, 10000]
         * Kernels should be N(4.5, 3.02765), N(14.5, 3.02765)...
         * Each bin should have mass 10/10000 = .001
         */
        final double[] testPoints = getCumulativeTestPoints();
        final double[] cumValues = new double[testPoints.length];
        final EmpiricalDistribution empiricalDistribution = (EmpiricalDistribution) makeDistribution();
        final double[] binBounds = empiricalDistribution.getUpperBounds();
        for (int i = 0; i < testPoints.length; i++) {
            final int bin = findBin(testPoints[i]);
            final double lower = bin == 0 ? empiricalDistribution.getSupportLowerBound() :
                binBounds[bin - 1];
            final double upper = binBounds[bin];
            // Compute bMinus = sum or mass of bins below the bin containing the point
            // First bin has mass 11 / 10000, the rest have mass 10 / 10000.
            final double bMinus = bin == 0 ? 0 : (bin - 1) * binMass + firstBinMass;
            final ContinuousDistribution kernel = findKernel(lower, upper);
            final double withinBinKernelMass = kernel.probability(lower, upper);
            final double kernelCum = kernel.probability(lower, testPoints[i]);
            cumValues[i] = bMinus + (bin == 0 ? firstBinMass : binMass) * kernelCum/withinBinKernelMass;
        }
        return cumValues;
    }

    @Override
    public double[] makeDensityTestValues() {
        final double[] testPoints = getCumulativeTestPoints();
        final double[] densityValues = new double[testPoints.length];
        final EmpiricalDistribution empiricalDistribution = (EmpiricalDistribution) makeDistribution();
        final double[] binBounds = empiricalDistribution.getUpperBounds();
        for (int i = 0; i < testPoints.length; i++) {
            final int bin = findBin(testPoints[i]);
            final double lower = bin == 0 ? empiricalDistribution.getSupportLowerBound() :
                binBounds[bin - 1];
            final double upper = binBounds[bin];
            final ContinuousDistribution kernel = findKernel(lower, upper);
            final double withinBinKernelMass = kernel.probability(lower, upper);
            final double density = kernel.density(testPoints[i]);
            densityValues[i] = density * (bin == 0 ? firstBinMass : binMass) / withinBinKernelMass;
        }
        return densityValues;
    }

    /**
     * Modify test integration bounds from the default. Because the distribution
     * has discontinuities at bin boundaries, integrals spanning multiple bins
     * will face convergence problems.  Only test within-bin integrals and spans
     * across no more than 3 bin boundaries.
     */
    @Override
    @Test
    public void testDensityIntegrals() {
        final ContinuousDistribution distribution = makeDistribution();
        final double tol = 1.0e-9;
        final BaseAbstractUnivariateIntegrator integrator =
            new IterativeLegendreGaussIntegrator(5, 1.0e-12, 1.0e-10);
        final UnivariateFunction d = new UnivariateFunction() {
            @Override
            public double value(double x) {
                return distribution.density(x);
            }
        };
        final double[] lower = {0, 5, 1000, 5001, 9995};
        final double[] upper = {5, 12, 1030, 5010, 10000};
        for (int i = 1; i < 5; i++) {
            Assert.assertEquals(
                    distribution.probability(
                            lower[i], upper[i]),
                            integrator.integrate(
                                    1000000, // Triangle integrals are very slow to converge
                                    d, lower[i], upper[i]), tol);
        }
    }

    /**
     * MATH-984
     * Verify that sampled values do not go outside of the range of the data.
     */
    @Test
    public void testSampleValuesRange() {
        // Concentrate values near the endpoints of (0, 1).
        // Unconstrained Gaussian kernel would generate values outside the interval.
        final double[] data = new double[100];
        for (int i = 0; i < 50; i++) {
            data[i] = 1 / ((double) i + 1);
        }
        for (int i = 51; i < 100; i++) {
            data[i] = 1 - 1 / (100 - (double) i + 2);
        }
        EmpiricalDistribution dist = EmpiricalDistribution.from(10, data);
        ContinuousDistribution.Sampler sampler
            = dist.createSampler(RandomSource.WELL_19937_C.create(1000));
        for (int i = 0; i < 1000; i++) {
            final double dev = sampler.sample();
            Assert.assertTrue(dev < 1);
            Assert.assertTrue(dev > 0);
        }
    }

    /**
     * MATH-1203, MATH-1208
     */
    @Test
    public void testNoBinVariance() {
        final double[] data = {0, 0, 1, 1};
        EmpiricalDistribution dist = EmpiricalDistribution.from(2, data);
        ContinuousDistribution.Sampler sampler
            = dist.createSampler(RandomSource.WELL_19937_C.create(1000));
        for (int i = 0; i < 1000; i++) {
            final double dev = sampler.sample();
            Assert.assertTrue(dev == 0 || dev == 1);
        }
        Assert.assertEquals(0.5, dist.cumulativeProbability(0), Double.MIN_VALUE);
        Assert.assertEquals(1.0, dist.cumulativeProbability(1), Double.MIN_VALUE);
        Assert.assertEquals(0.5, dist.cumulativeProbability(0.5), Double.MIN_VALUE);
        Assert.assertEquals(0.5, dist.cumulativeProbability(0.7), Double.MIN_VALUE);
    }

    /**
     * Find the bin that x belongs (relative to {@link #makeDistribution()}).
     */
    private int findBin(double x) {
        // Number of bins below x should be trunc(x/10)
        final double nMinus = JdkMath.floor(x / 10);
        final int bin =  (int) JdkMath.round(nMinus);
        // If x falls on a bin boundary, it is in the lower bin
        return JdkMath.floor(x / 10) == x / 10 ? bin - 1 : bin;
    }

    /**
     * Find the within-bin kernel for the bin with lower bound lower
     * and upper bound upper. All bins other than the first contain 10 points
     * exclusive of the lower bound and are centered at (lower + upper + 1) / 2.
     * The first bin includes its lower bound, 0, so has different mean and
     * standard deviation.
     */
    private ContinuousDistribution findKernel(double lower, double upper) {
        if (lower < 1) {
            return NormalDistribution.of(5d, 3.3166247903554);
        } else {
            return NormalDistribution.of((upper + lower + 1) / 2d, 3.0276503540974917);
        }
    }

    @Test
    public void testKernelOverrideUniform() {
        final double[] data = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        final EmpiricalDistribution dist =
            EmpiricalDistribution.from(5, data,
                                       s -> UniformContinuousDistribution.of(s.getMin(), s.getMax()));
        final ContinuousDistribution.Sampler sampler
            = dist.createSampler(RandomSource.WELL_19937_C.create(1000));
        // Kernels are uniform distributions on [1,3], [4,6], [7,9], [10,12], [13,15]
        final double bounds[] = {3d, 6d, 9d, 12d};
        final double tol = 10E-12;
        for (int i = 0; i < 20; i++) {
            final double v = sampler.sample();
            // Make sure v is not in the excluded range between bins - that is (bounds[i], bounds[i] + 1)
            for (int j = 0; j < bounds.length; j++) {
                Assert.assertFalse(v > bounds[j] + tol && v < bounds[j] + 1 - tol);
            }
        }
        Assert.assertEquals(0.0, dist.cumulativeProbability(1), tol);
        Assert.assertEquals(0.1, dist.cumulativeProbability(2), tol);
        Assert.assertEquals(0.6, dist.cumulativeProbability(10), tol);
        Assert.assertEquals(0.8, dist.cumulativeProbability(12), tol);
        Assert.assertEquals(0.8, dist.cumulativeProbability(13), tol);
        Assert.assertEquals(1.0, dist.cumulativeProbability(15), tol);

        Assert.assertEquals(2.0, dist.inverseCumulativeProbability(0.1), tol);
        Assert.assertEquals(3.0, dist.inverseCumulativeProbability(0.2), tol);
        Assert.assertEquals(5.0, dist.inverseCumulativeProbability(0.3), tol);
        Assert.assertEquals(6.0, dist.inverseCumulativeProbability(0.4), tol);
        Assert.assertEquals(8.0, dist.inverseCumulativeProbability(0.5), tol);
        Assert.assertEquals(9.0, dist.inverseCumulativeProbability(0.6), tol);
    }

    @Test
    public void testMath1431() {
        final UniformRandomProvider rng = RandomSource.WELL_19937_C.create(1000);
        final ContinuousDistribution.Sampler exponentialDistributionSampler
            = ExponentialDistribution.of(0.05).createSampler(rng);
        final double[] empiricalDataPoints = new double[3000];
        for (int i = 0; i < empiricalDataPoints.length; i++) {
            empiricalDataPoints[i] = exponentialDistributionSampler.sample();
        }

        final EmpiricalDistribution testDistribution = EmpiricalDistribution.from(100, empiricalDataPoints);

        for (int i = 0; i < 1000; i++) {
            final double point = rng.nextDouble();
            final double cdf = testDistribution.cumulativeProbability(point);
            Assert.assertFalse("point: " + point, Double.isNaN(cdf));
        }
    }

    @Test
    public void testMath1462() {
        final double[] data = {
            6464.0205, 6449.1328, 6489.4569, 6497.5533, 6251.6487,
            6252.6513, 6339.7883, 6356.2622, 6222.1251, 6157.3813,
            6242.4741, 6332.5347, 6468.0633, 6471.2319, 6473.9929,
            6589.1322, 6511.2191, 6339.4349, 6307.7735, 6288.0915,
            6354.0572, 6385.8283, 6325.3756, 6433.1699, 6433.6507,
            6424.6806, 6380.5268, 6407.6705, 6241.2198, 6230.3681,
            6367.5943, 6358.4817, 6272.8039, 6269.0211, 6312.9027,
            6349.5926, 6404.0775, 6326.986, 6283.8685, 6309.9021,
            6336.8554, 6389.1598, 6281.0372, 6304.8852, 6359.2651,
            6426.519, 6400.3926, 6440.6798, 6292.5812, 6398.4911,
            6307.0002, 6284.2111, 6271.371, 6368.6377, 6323.3372,
            6276.2155, 6335.0117, 6319.2466, 6252.9969, 6445.2074,
            6461.3944, 6384.1345
        };

        final EmpiricalDistribution ed = EmpiricalDistribution.from(data.length / 10, data);

        double v;
        double p;

        p = 0.49999;
        v = ed.inverseCumulativeProbability(p);
        Assert.assertTrue("p=" + p + " => v=" + v, v < 6344);

        p = 0.5;
        v = ed.inverseCumulativeProbability(p);
        Assert.assertTrue("p=" + p + " => v=" + v, v < 7000);

        p = 0.51111;
        v = ed.inverseCumulativeProbability(p);
        Assert.assertTrue("p=" + p + " => v=" + v, v < 6350);
    }

    @Test
    public void testMath1462InfiniteQuantile() {
        final double[] data = {
            18054, 17548, 17350, 17860, 17827, 17653, 18113, 18405, 17746,
            17647, 18160, 17955, 17705, 17890, 17974, 17857, 13287, 18645,
            17775, 17730, 17996, 18263, 17861, 17161, 17717, 18134, 18669,
            18340, 17221, 18292, 18146, 17520, 18207, 17829, 18206, 13301,
            18257, 17626, 18358, 18340, 18320, 17852, 17804, 17577, 17718,
            18099, 13395, 17763, 17911, 17978, 12935, 17519, 17550, 18728,
            18518, 17698, 18739, 18553, 17982, 18113, 17974, 17961, 17645,
            17867, 17890, 17498, 18718, 18191, 18177, 17923, 18164, 18155,
            6212, 5961, 711
        };

        final double p = 0.32;
        for (int i = 745; i <= 1100; i++) {
            final EmpiricalDistribution ed = EmpiricalDistribution.from(i, data);
            final double v = ed.inverseCumulativeProbability(p);

            Assert.assertTrue("p=" + p + " => v=" + v, Double.isFinite(v));
        }
    }
}
