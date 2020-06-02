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
package org.apache.commons.math4.distribution;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.statistics.distribution.ContinuousDistribution;
import org.apache.commons.statistics.distribution.ConstantContinuousDistribution;
import org.apache.commons.statistics.distribution.UniformContinuousDistribution;
import org.apache.commons.statistics.distribution.NormalDistribution;
import org.apache.commons.math4.TestUtils;
import org.apache.commons.math4.analysis.UnivariateFunction;
import org.apache.commons.math4.analysis.integration.BaseAbstractUnivariateIntegrator;
import org.apache.commons.math4.analysis.integration.IterativeLegendreGaussIntegrator;
import org.apache.commons.math4.exception.MathIllegalStateException;
import org.apache.commons.math4.exception.NullArgumentException;
import org.apache.commons.math4.exception.NotStrictlyPositiveException;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.math4.stat.descriptive.SummaryStatistics;
import org.apache.commons.math4.util.FastMath;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for the {@link EmpiricalDistribution} class.
 */
public final class EmpiricalDistributionTest extends RealDistributionAbstractTest {

    protected EmpiricalDistribution empiricalDistribution = null;
    protected EmpiricalDistribution empiricalDistribution2 = null;
    protected File file = null;
    protected URL url = null;
    protected double[] dataArray = null;
    protected final int n = 10000;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        empiricalDistribution = new EmpiricalDistribution(100);
        url = getClass().getResource("testData.txt");
        final ArrayList<Double> list = new ArrayList<>();
        try {
            empiricalDistribution2 = new EmpiricalDistribution(100);
            BufferedReader in =
                new BufferedReader(new InputStreamReader(
                        url.openStream()));
            String str = null;
            while ((str = in.readLine()) != null) {
                list.add(Double.valueOf(str));
            }
            in.close();
            in = null;
        } catch (IOException ex) {
            Assert.fail("IOException " + ex);
        }

        dataArray = new double[list.size()];
        int i = 0;
        for (Double data : list) {
            dataArray[i] = data.doubleValue();
            i++;
        }
    }

    // MATH-1279
    @Test(expected=NotStrictlyPositiveException.class)
    public void testPrecondition1() {
        new EmpiricalDistribution(0);
    }

    /**
     * Test EmpiricalDistribution.load() using sample data file.<br>
     * Check that the sampleCount, mu and sigma match data in
     * the sample data file. Also verify that load is idempotent.
     */
    @Test
    public void testLoad() throws Exception {
        // Load from a URL
        empiricalDistribution.load(url);
        checkDistribution();

        // Load again from a file (also verifies idempotency of load)
        File file = new File(url.toURI());
        empiricalDistribution.load(file);
        checkDistribution();
    }

    private void checkDistribution() {
        // testData File has 10000 values, with mean ~ 5.0, std dev ~ 1
        // Make sure that loaded distribution matches this
        Assert.assertEquals(empiricalDistribution.getSampleStats().getN(),1000,10E-7);
        //TODO: replace with statistical tests
        Assert.assertEquals(empiricalDistribution.getSampleStats().getMean(),
                5.069831575018909,10E-7);
        Assert.assertEquals(empiricalDistribution.getSampleStats().getStandardDeviation(),
                1.0173699343977738,10E-7);
    }

    /**
     * Test EmpiricalDistribution.load(double[]) using data taken from
     * sample data file.<br>
     * Check that the sampleCount, mu and sigma match data in
     * the sample data file.
     */
    @Test
    public void testDoubleLoad() throws Exception {
        empiricalDistribution2.load(dataArray);
        // testData File has 10000 values, with mean ~ 5.0, std dev ~ 1
        // Make sure that loaded distribution matches this
        Assert.assertEquals(empiricalDistribution2.getSampleStats().getN(),1000,10E-7);
        //TODO: replace with statistical tests
        Assert.assertEquals(empiricalDistribution2.getSampleStats().getMean(),
                5.069831575018909,10E-7);
        Assert.assertEquals(empiricalDistribution2.getSampleStats().getStandardDeviation(),
                1.0173699343977738,10E-7);

        double[] bounds = empiricalDistribution2.getGeneratorUpperBounds();
        Assert.assertEquals(bounds.length, 100);
        Assert.assertEquals(bounds[99], 1.0, 10e-12);

    }


    // MATH-1531
    @Test
    public void testMath1531() {
        final EmpiricalDistribution inputDistribution = new EmpiricalDistribution(120);
        inputDistribution.load(new double[] {
                50.993456376721454,
                49.455345691918055,
                49.527276095295804,
                50.017183448668845,
                49.10508147470046,
                49.813998274118696,
                50.87195348756139,
                50.419474110037,
                50.63614906979689,
                49.49694777179407,
                50.71799078406067,
                50.03192853759164,
                49.915092423165994,
                49.56895392597687,
                51.034638001064934,
                50.681227971275945,
                50.43749845081759,
                49.86513120270245,
                50.21475262482965,
                49.99202971042547,
                50.02382189838519,
                49.386888585302884,
                49.45585010202781,
                49.988009479855435,
                49.8136712206123,
                49.6715197127997,
                50.1981278397565,
                49.842297508010276,
                49.62491227740015,
                50.05101916097176,
                48.834912763303926,
                49.806787657848574,
                49.478236106374695,
                49.56648347371614,
                49.95069238081982,
                49.71845132077346,
                50.6097468705947,
                49.80724637775541,
                49.90448813086025,
                49.39641861662603,
                50.434295712893714,
                49.227176959566734,
                49.541126466050905,
                49.03416593170446,
                49.11584328494423,
                49.61387482435674,
                49.92877857995328,
                50.70638552955101,
                50.60078208448842,
                49.39326233277838,
                49.21488424364095,
                49.69503351015096,
                50.13733214001718,
                50.22084761458942,
                51.09804435604931,
                49.18559131120419,
                49.52286371605357,
                49.34804374996689,
                49.6901827776375,
                50.01316351359638,
                48.7751460520373,
                50.12961836291053,
                49.9978419772511,
                49.885658399408584,
                49.673438879979834,
                49.45565980965606,
                50.429747484906564,
                49.40129274804164,
                50.13034614008073,
                49.87685735146651,
                50.12967905393557,
                50.323560376181696,
                49.83519233651367,
                49.37333369733053,
                49.70074301611427,
                50.11626105774947,
                50.28249500380083,
                50.543354367136466,
                50.05866241335002,
                50.39516515672527,
                49.4838561463057,
                50.451757089234796,
                50.31370674203726,
                49.79063762614284,
                50.19652349768548,
                49.75881420748814,
                49.98371855036422,
                49.82171344472916,
                48.810793204162415,
                49.37040569084592,
                50.050641186203976,
                50.48360952263646,
                50.86666450358076,
                50.463268776129844,
                50.137489751888666,
                50.23823061444118,
                49.881460479468004,
                50.641174398764356,
                49.09314136851421,
                48.80877928574451,
                50.46197084844826,
                49.97691704141741,
                49.99933997561926,
                50.25692254481885,
                49.52973451252715,
                49.81229858420664,
                48.996112655915994,
                48.740531054814674,
                50.026642633066416,
                49.98696633604899,
                49.61307159972952,
                50.5115278979726,
                50.75245152442404,
                50.51807785445929,
                49.60929671768147,
                49.1079533564074,
                49.65347196551866,
                49.31684818724059,
                50.4906368627049,
                50.37483603684714});
        inputDistribution.inverseCumulativeProbability(0.7166666666666669);
    }

    /**
      * Generate 1000 random values and make sure they look OK.<br>
      * Note that there is a non-zero (but very small) probability that
      * these tests will fail even if the code is working as designed.
      */
    @Test
    public void testNext() throws Exception {
        tstGen(0.1);
        tstDoubleGen(0.1);
    }

    /**
     * Make sure exception thrown if sampling is attempted
     * before loading empiricalDistribution.
     */
    @Test
    public void testNextFail1() {
        try {
            empiricalDistribution.createSampler(RandomSource.create(RandomSource.JDK)).sample();
            Assert.fail("Expecting MathIllegalStateException");
        } catch (MathIllegalStateException ex) {
            // expected
        }
    }

    /**
     * Make sure exception thrown if sampling is attempted
     * before loading empiricalDistribution.
     */
    @Test
    public void testNextFail2() {
        try {
            empiricalDistribution2.createSampler(RandomSource.create(RandomSource.JDK)).sample();
            Assert.fail("Expecting MathIllegalStateException");
        } catch (MathIllegalStateException ex) {
            // expected
        }
    }

    /**
     * Make sure we can handle a grid size that is too fine
     */
    @Test
    public void testGridTooFine() throws Exception {
        empiricalDistribution = new EmpiricalDistribution(1001);
        tstGen(0.1);
        empiricalDistribution2 = new EmpiricalDistribution(1001);
        tstDoubleGen(0.1);
    }

    /**
     * How about too fat?
     */
    @Test
    public void testGridTooFat() throws Exception {
        empiricalDistribution = new EmpiricalDistribution(1);
        tstGen(5); // ridiculous tolerance; but ridiculous grid size
                   // really just checking to make sure we do not bomb
        empiricalDistribution2 = new EmpiricalDistribution(1);
        tstDoubleGen(5);
    }

    /**
     * Test bin index overflow problem (BZ 36450)
     */
    @Test
    public void testBinIndexOverflow() throws Exception {
        double[] x = new double[] {9474.94326071674, 2080107.8865462579};
        new EmpiricalDistribution().load(x);
    }

    @Test
    public void testSerialization() {
        // Empty
        EmpiricalDistribution dist = new EmpiricalDistribution();
        EmpiricalDistribution dist2 = (EmpiricalDistribution) TestUtils.serializeAndRecover(dist);
        verifySame(dist, dist2);

        // Loaded
        empiricalDistribution2.load(dataArray);
        dist2 = (EmpiricalDistribution) TestUtils.serializeAndRecover(empiricalDistribution2);
        verifySame(empiricalDistribution2, dist2);
    }

    @Test(expected=NullArgumentException.class)
    public void testLoadNullDoubleArray() {
       new EmpiricalDistribution().load((double[]) null);
    }

    @Test(expected=NullArgumentException.class)
    public void testLoadNullURL() throws Exception {
        new EmpiricalDistribution().load((URL) null);
    }

    @Test(expected=NullArgumentException.class)
    public void testLoadNullFile() throws Exception {
        new EmpiricalDistribution().load((File) null);
    }

    /**
     * MATH-298
     */
    @Test
    public void testGetBinUpperBounds() {
        double[] testData = {0, 1, 1, 2, 3, 4, 4, 5, 6, 7, 8, 9, 10};
        EmpiricalDistribution dist = new EmpiricalDistribution(5);
        dist.load(testData);
        double[] expectedBinUpperBounds = {2, 4, 6, 8, 10};
        double[] expectedGeneratorUpperBounds = {4d/13d, 7d/13d, 9d/13d, 11d/13d, 1};
        double tol = 10E-12;
        TestUtils.assertEquals(expectedBinUpperBounds, dist.getUpperBounds(), tol);
        TestUtils.assertEquals(expectedGeneratorUpperBounds, dist.getGeneratorUpperBounds(), tol);
    }

    private void verifySame(EmpiricalDistribution d1, EmpiricalDistribution d2) {
        Assert.assertEquals(d1.isLoaded(), d2.isLoaded());
        Assert.assertEquals(d1.getBinCount(), d2.getBinCount());
        Assert.assertEquals(d1.getSampleStats(), d2.getSampleStats());
        if (d1.isLoaded()) {
            for (int i = 0;  i < d1.getUpperBounds().length; i++) {
                Assert.assertEquals(d1.getUpperBounds()[i], d2.getUpperBounds()[i], 0);
            }
            Assert.assertEquals(d1.getBinStats(), d2.getBinStats());
        }
    }

    private void tstGen(double tolerance)throws Exception {
        empiricalDistribution.load(url);
        ContinuousDistribution.Sampler sampler
            = empiricalDistribution.createSampler(RandomSource.create(RandomSource.WELL_19937_C, 1000));
        SummaryStatistics stats = new SummaryStatistics();
        for (int i = 1; i < 1000; i++) {
            stats.addValue(sampler.sample());
        }
        Assert.assertEquals("mean", 5.069831575018909, stats.getMean(),tolerance);
        Assert.assertEquals("std dev", 1.0173699343977738, stats.getStandardDeviation(),tolerance);
    }

    private void tstDoubleGen(double tolerance)throws Exception {
        empiricalDistribution2.load(dataArray);
        ContinuousDistribution.Sampler sampler
            = empiricalDistribution2.createSampler(RandomSource.create(RandomSource.WELL_19937_C, 1000));
        SummaryStatistics stats = new SummaryStatistics();
        for (int i = 1; i < 1000; i++) {
            stats.addValue(sampler.sample());
        }
        Assert.assertEquals("mean", 5.069831575018909, stats.getMean(), tolerance);
        Assert.assertEquals("std dev", 1.0173699343977738, stats.getStandardDeviation(), tolerance);
    }

    //  Setup for distribution tests

    @Override
    public ContinuousDistribution makeDistribution() {
        // Create a uniform distribution on [0, 10,000]
        final double[] sourceData = new double[n + 1];
        for (int i = 0; i < n + 1; i++) {
            sourceData[i] = i;
        }
        EmpiricalDistribution dist = new EmpiricalDistribution();
        dist.load(sourceData);
        return dist;
    }

    /** Uniform bin mass = 10/10001 == mass of all but the first bin */
    private final double binMass = 10d / (n + 1);

    /** Mass of first bin = 11/10001 */
    private final double firstBinMass = 11d / (n + 1);

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
        EmpiricalDistribution dist = new EmpiricalDistribution(10);
        dist.load(data);
        ContinuousDistribution.Sampler sampler
            = dist.createSampler(RandomSource.create(RandomSource.WELL_19937_C, 1000));
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
        EmpiricalDistribution dist = new EmpiricalDistribution(2);
        dist.load(data);
        ContinuousDistribution.Sampler sampler
            = dist.createSampler(RandomSource.create(RandomSource.WELL_19937_C, 1000));
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
        final double nMinus = FastMath.floor(x / 10);
        final int bin =  (int) FastMath.round(nMinus);
        // If x falls on a bin boundary, it is in the lower bin
        return FastMath.floor(x / 10) == x / 10 ? bin - 1 : bin;
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
            return new NormalDistribution(5d, 3.3166247903554);
        } else {
            return new NormalDistribution((upper + lower + 1) / 2d, 3.0276503540974917);
        }
    }

    @Test
    public void testKernelOverrideConstant() {
        final EmpiricalDistribution dist = new ConstantKernelEmpiricalDistribution(5);
        final double[] data = {1d,2d,3d, 4d,5d,6d, 7d,8d,9d, 10d,11d,12d, 13d,14d,15d};
        dist.load(data);
        ContinuousDistribution.Sampler sampler
            = dist.createSampler(RandomSource.create(RandomSource.WELL_19937_C, 1000));
        // Bin masses concentrated on 2, 5, 8, 11, 14 <- effectively discrete uniform distribution over these
        double[] values = {2d, 5d, 8d, 11d, 14d};
        for (int i = 0; i < 20; i++) {
            Assert.assertTrue(Arrays.binarySearch(values, sampler.sample()) >= 0);
        }
        final double tol = 10E-12;
        Assert.assertEquals(0.0, dist.cumulativeProbability(1), tol);
        Assert.assertEquals(0.2, dist.cumulativeProbability(2), tol);
        Assert.assertEquals(0.6, dist.cumulativeProbability(10), tol);
        Assert.assertEquals(0.8, dist.cumulativeProbability(12), tol);
        Assert.assertEquals(0.8, dist.cumulativeProbability(13), tol);
        Assert.assertEquals(1.0, dist.cumulativeProbability(15), tol);

        Assert.assertEquals(2.0, dist.inverseCumulativeProbability(0.1), tol);
        Assert.assertEquals(2.0, dist.inverseCumulativeProbability(0.2), tol);
        Assert.assertEquals(5.0, dist.inverseCumulativeProbability(0.3), tol);
        Assert.assertEquals(5.0, dist.inverseCumulativeProbability(0.4), tol);
        Assert.assertEquals(8.0, dist.inverseCumulativeProbability(0.5), tol);
        Assert.assertEquals(8.0, dist.inverseCumulativeProbability(0.6), tol);
    }

    @Test
    public void testKernelOverrideUniform() {
        final EmpiricalDistribution dist = new UniformKernelEmpiricalDistribution(5);
        final double[] data = {1d,2d,3d, 4d,5d,6d, 7d,8d,9d, 10d,11d,12d, 13d,14d,15d};
        dist.load(data);
        ContinuousDistribution.Sampler sampler
            = dist.createSampler(RandomSource.create(RandomSource.WELL_19937_C, 1000));
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


    /**
     * Empirical distribution using a constant smoothing kernel.
     */
    private class ConstantKernelEmpiricalDistribution extends EmpiricalDistribution {
        private static final long serialVersionUID = 1L;
        public ConstantKernelEmpiricalDistribution(int i) {
            super(i);
        }
        // Use constant distribution equal to bin mean within bin
        @Override
        protected ContinuousDistribution getKernel(SummaryStatistics bStats) {
            return new ConstantContinuousDistribution(bStats.getMean());
        }
    }

    /**
     * Empirical distribution using a uniform smoothing kernel.
     */
    private class UniformKernelEmpiricalDistribution extends EmpiricalDistribution {
        private static final long serialVersionUID = 2963149194515159653L;
        public UniformKernelEmpiricalDistribution(int i) {
            super(i);
        }
        @Override
        protected ContinuousDistribution getKernel(SummaryStatistics bStats) {
            return new UniformContinuousDistribution(bStats.getMin(), bStats.getMax());
        }
    }
}
