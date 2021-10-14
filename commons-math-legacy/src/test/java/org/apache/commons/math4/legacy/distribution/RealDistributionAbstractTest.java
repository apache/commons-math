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


import java.util.ArrayList;
import java.util.Collections;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.statistics.distribution.ContinuousDistribution;
import org.apache.commons.math4.legacy.TestUtils;
import org.apache.commons.math4.legacy.analysis.UnivariateFunction;
import org.apache.commons.math4.legacy.analysis.integration.BaseAbstractUnivariateIntegrator;
import org.apache.commons.math4.legacy.analysis.integration.IterativeLegendreGaussIntegrator;
import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.apache.commons.math4.legacy.exception.NumberIsTooLargeException;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Abstract base class for {@link ContinuousDistribution} tests.
 * <p>
 * To create a concrete test class for a continuous distribution
 * implementation, first implement makeDistribution() to return a distribution
 * instance to use in tests. Then implement each of the test data generation
 * methods below.  In each case, the test points and test values arrays
 * returned represent parallel arrays of inputs and expected values for the
 * distribution returned by makeDistribution().  Default implementations
 * are provided for the makeInverseXxx methods that just invert the mapping
 * defined by the arrays returned by the makeCumulativeXxx methods.
 * <p>
 * makeCumulativeTestPoints() -- arguments used to test cumulative probabilities
 * makeCumulativeTestValues() -- expected cumulative probabilities
 * makeDensityTestValues() -- expected density values at cumulativeTestPoints
 * makeInverseCumulativeTestPoints() -- arguments used to test inverse cdf
 * makeInverseCumulativeTestValues() -- expected inverse cdf values
 * <p>
 * To implement additional test cases with different distribution instances and
 * test data, use the setXxx methods for the instance data in test cases and
 * call the verifyXxx methods to verify results.
 * <p>
 * Error tolerance can be overridden by implementing getTolerance().
 * <p>
 * Test data should be validated against reference tables or other packages
 * where possible, and the source of the reference data and/or validation
 * should be documented in the test cases.  A framework for validating
 * distribution data against R is included in the /src/test/R source tree.
 *
 */
public abstract class RealDistributionAbstractTest {

//-------------------- Private test instance data -------------------------
    /**  Distribution instance used to perform tests */
    private ContinuousDistribution distribution;

    /** Tolerance used in comparing expected and returned values */
    private double tolerance = 1E-4;

    /** Arguments used to test cumulative probability density calculations */
    private double[] cumulativeTestPoints;

    /** Values used to test cumulative probability density calculations */
    private double[] cumulativeTestValues;

    /** Arguments used to test inverse cumulative probability density calculations */
    private double[] inverseCumulativeTestPoints;

    /** Values used to test inverse cumulative probability density calculations */
    private double[] inverseCumulativeTestValues;

    /** Values used to test density calculations */
    private double[] densityTestValues;

    /** Values used to test logarithmic density calculations */
    private double[] logDensityTestValues;

    //-------------------- Abstract methods -----------------------------------

    /** Creates the default continuous distribution instance to use in tests. */
    public abstract ContinuousDistribution makeDistribution();

    /** Creates the default cumulative probability test input values */
    public abstract double[] makeCumulativeTestPoints();

    /** Creates the default cumulative probability test expected values */
    public abstract double[] makeCumulativeTestValues();

    /** Creates the default density test expected values */
    public abstract double[] makeDensityTestValues();

    /** Creates the default logarithmic density test expected values.
     * The default implementation simply computes the logarithm
     * of each value returned by {@link #makeDensityTestValues()}.*/
    public double[] makeLogDensityTestValues() {
        final double[] densityTestValues = makeDensityTestValues();
        final double[] logDensityTestValues = new double[densityTestValues.length];
        for (int i = 0; i < densityTestValues.length; i++) {
            logDensityTestValues[i] = JdkMath.log(densityTestValues[i]);
        }
        return logDensityTestValues;
    }

    //---- Default implementations of inverse test data generation methods ----

    /** Creates the default inverse cumulative probability test input values */
    public double[] makeInverseCumulativeTestPoints() {
        return makeCumulativeTestValues();
    }

    /** Creates the default inverse cumulative probability density test expected values */
    public double[] makeInverseCumulativeTestValues() {
        return makeCumulativeTestPoints();
    }

    //-------------------- Setup / tear down ----------------------------------

    /**
     * Setup sets all test instance data to default values
     */
    @Before
    public void setUp() {
        distribution = makeDistribution();
        cumulativeTestPoints = makeCumulativeTestPoints();
        cumulativeTestValues = makeCumulativeTestValues();
        inverseCumulativeTestPoints = makeInverseCumulativeTestPoints();
        inverseCumulativeTestValues = makeInverseCumulativeTestValues();
        densityTestValues = makeDensityTestValues();
        logDensityTestValues = makeLogDensityTestValues();
    }

    /**
     * Cleans up test instance data
     */
    @After
    public void tearDown() {
        distribution = null;
        cumulativeTestPoints = null;
        cumulativeTestValues = null;
        inverseCumulativeTestPoints = null;
        inverseCumulativeTestValues = null;
        densityTestValues = null;
        logDensityTestValues = null;
    }

    //-------------------- Verification methods -------------------------------

    /**
     * Verifies that cumulative probability density calculations match expected values
     * using current test instance data
     */
    protected void verifyCumulativeProbabilities() {
        // verify cumulativeProbability(double)
        for (int i = 0; i < cumulativeTestPoints.length; i++) {
            TestUtils.assertEquals("Incorrect cumulative probability value returned for "
                + cumulativeTestPoints[i], cumulativeTestValues[i],
                distribution.cumulativeProbability(cumulativeTestPoints[i]),
                getTolerance());
        }
        // verify probability(double, double)
        for (int i = 0; i < cumulativeTestPoints.length; i++) {
            for (int j = 0; j < cumulativeTestPoints.length; j++) {
                if (cumulativeTestPoints[i] <= cumulativeTestPoints[j]) {
                    TestUtils.assertEquals(cumulativeTestValues[j] - cumulativeTestValues[i],
                        distribution.probability(cumulativeTestPoints[i], cumulativeTestPoints[j]),
                        getTolerance());
                } else {
                    try {
                        distribution.probability(cumulativeTestPoints[i], cumulativeTestPoints[j]);
                    } catch (NumberIsTooLargeException e) {
                        continue;
                    }
                    Assert.fail("distribution.probability(double, double) should have thrown an exception that second argument is too large");
                }
            }
        }
    }

    /**
     * Verifies that inverse cumulative probability density calculations match expected values
     * using current test instance data
     */
    protected void verifyInverseCumulativeProbabilities() {
        for (int i = 0; i < inverseCumulativeTestPoints.length; i++) {
            TestUtils.assertEquals("Incorrect inverse cumulative probability value returned for "
                + inverseCumulativeTestPoints[i], inverseCumulativeTestValues[i],
                 distribution.inverseCumulativeProbability(inverseCumulativeTestPoints[i]),
                 getTolerance());
        }
    }

    /**
     * Verifies that density calculations match expected values
     */
    protected void verifyDensities() {
        for (int i = 0; i < cumulativeTestPoints.length; i++) {
            TestUtils.assertEquals("Incorrect probability density value returned for "
                + cumulativeTestPoints[i], densityTestValues[i],
                 distribution.density(cumulativeTestPoints[i]),
                 getTolerance());
        }
    }

    /**
     * Verifies that logarithmic density calculations match expected values
     */
    protected void verifyLogDensities() {
        for (int i = 0; i < cumulativeTestPoints.length; i++) {
            TestUtils.assertEquals("Incorrect probability density value returned for "
                    + cumulativeTestPoints[i], logDensityTestValues[i],
                    distribution.logDensity(cumulativeTestPoints[i]),
                    getTolerance());
        }
    }

    //------------------------ Default test cases -----------------------------

    /**
     * Verifies that cumulative probability density calculations match expected values
     * using default test instance data
     */
    @Test
    public void testCumulativeProbabilities() {
        verifyCumulativeProbabilities();
    }

    /**
     * Verifies that inverse cumulative probability density calculations match expected values
     * using default test instance data
     */
    @Test
    public void testInverseCumulativeProbabilities() {
        verifyInverseCumulativeProbabilities();
    }

    /**
     * Verifies that density calculations return expected values
     * for default test instance data
     */
    @Test
    public void testDensities() {
        verifyDensities();
    }

    /**
     * Verifies that logarithmic density calculations return expected values
     * for default test instance data
     */
    @Test
    public void testLogDensities() {
        verifyLogDensities();
    }

    /**
     * Verifies that probability computations are consistent
     */
    @Test
    public void testConsistency() {
        for (int i=1; i < cumulativeTestPoints.length; i++) {

            // check that cdf(x, x) = 0
            TestUtils.assertEquals(0d,
               distribution.probability
                 (cumulativeTestPoints[i], cumulativeTestPoints[i]), tolerance);

            // check that P(a < X <= b) = P(X <= b) - P(X <= a)
            double upper = JdkMath.max(cumulativeTestPoints[i], cumulativeTestPoints[i -1]);
            double lower = JdkMath.min(cumulativeTestPoints[i], cumulativeTestPoints[i -1]);
            double diff = distribution.cumulativeProbability(upper) -
                distribution.cumulativeProbability(lower);
            double direct = distribution.probability(lower, upper);
            TestUtils.assertEquals("Inconsistent probability for ("
                    + lower + "," + upper + ")", diff, direct, tolerance);
        }
    }

    /**
     * Verifies that illegal arguments are correctly handled
     */
    @Test
    public void testIllegalArguments() {
        try {
            distribution.probability(1, 0);
            Assert.fail("Expecting MathIllegalArgumentException for bad cumulativeProbability interval");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
        try {
            distribution.inverseCumulativeProbability(-1);
            Assert.fail("Expecting MathIllegalArgumentException for p = -1");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
        try {
            distribution.inverseCumulativeProbability(2);
            Assert.fail("Expecting MathIllegalArgumentException for p = 2");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
    }

    /**
     * Test sampling
     */
    @Test
    public void testSampler() {
        final int sampleSize = 1000;
        final ContinuousDistribution.Sampler sampler =
            distribution.createSampler(RandomSource.WELL_19937_C.create(123456789L));
        final double[] sample = AbstractRealDistribution.sample(sampleSize, sampler);
        final double[] quartiles = TestUtils.getDistributionQuartiles(distribution);
        final double[] expected = {250, 250, 250, 250};
        final long[] counts = new long[4];

        for (int i = 0; i < sampleSize; i++) {
            TestUtils.updateCounts(sample[i], counts, quartiles);
        }
        TestUtils.assertChiSquareAccept(expected, counts, 0.001);
    }

    /**
     * Verify that density integrals match the distribution.
     * The (filtered, sorted) cumulativeTestPoints array is used to source
     * integration limits. The integral of the density (estimated using a
     * Legendre-Gauss integrator) is compared with the cdf over the same
     * interval. Test points outside of the domain of the density function
     * are discarded.
     */
    @Test
    public void testDensityIntegrals() {
        final double tol = 1.0e-9;
        final BaseAbstractUnivariateIntegrator integrator =
            new IterativeLegendreGaussIntegrator(5, 1.0e-12, 1.0e-10);
        final UnivariateFunction d = new UnivariateFunction() {
            @Override
            public double value(double x) {
                return distribution.density(x);
            }
        };
        final ArrayList<Double> integrationTestPoints = new ArrayList<>();
        for (int i = 0; i < cumulativeTestPoints.length; i++) {
            if (Double.isNaN(cumulativeTestValues[i]) ||
                    cumulativeTestValues[i] < 1.0e-5 ||
                    cumulativeTestValues[i] > 1 - 1.0e-5) {
                continue; // exclude integrals outside domain.
            }
            integrationTestPoints.add(cumulativeTestPoints[i]);
        }
        Collections.sort(integrationTestPoints);
        for (int i = 1; i < integrationTestPoints.size(); i++) {
            Assert.assertEquals(
                    distribution.probability(
                            integrationTestPoints.get(0), integrationTestPoints.get(i)),
                            integrator.integrate(
                                    1000000, // Triangle integrals are very slow to converge
                                    d, integrationTestPoints.get(0),
                                    integrationTestPoints.get(i)), tol);
        }
    }

    //------------------ Getters / Setters for test instance data -----------
    /**
     * @return Returns the cumulativeTestPoints.
     */
    protected double[] getCumulativeTestPoints() {
        return cumulativeTestPoints;
    }

    /**
     * @param cumulativeTestPoints The cumulativeTestPoints to set.
     */
    protected void setCumulativeTestPoints(double[] cumulativeTestPoints) {
        this.cumulativeTestPoints = cumulativeTestPoints;
    }

    /**
     * @return Returns the cumulativeTestValues.
     */
    protected double[] getCumulativeTestValues() {
        return cumulativeTestValues;
    }

    /**
     * @param cumulativeTestValues The cumulativeTestValues to set.
     */
    protected void setCumulativeTestValues(double[] cumulativeTestValues) {
        this.cumulativeTestValues = cumulativeTestValues;
    }

    protected double[] getDensityTestValues() {
        return densityTestValues;
    }

    protected void setDensityTestValues(double[] densityTestValues) {
        this.densityTestValues = densityTestValues;
    }

    /**
     * @return Returns the distribution.
     */
    protected ContinuousDistribution getDistribution() {
        return distribution;
    }

    /**
     * @param distribution The distribution to set.
     */
    protected void setDistribution(ContinuousDistribution distribution) {
        this.distribution = distribution;
    }

    /**
     * @return Returns the inverseCumulativeTestPoints.
     */
    protected double[] getInverseCumulativeTestPoints() {
        return inverseCumulativeTestPoints;
    }

    /**
     * @param inverseCumulativeTestPoints The inverseCumulativeTestPoints to set.
     */
    protected void setInverseCumulativeTestPoints(double[] inverseCumulativeTestPoints) {
        this.inverseCumulativeTestPoints = inverseCumulativeTestPoints;
    }

    /**
     * @return Returns the inverseCumulativeTestValues.
     */
    protected double[] getInverseCumulativeTestValues() {
        return inverseCumulativeTestValues;
    }

    /**
     * @param inverseCumulativeTestValues The inverseCumulativeTestValues to set.
     */
    protected void setInverseCumulativeTestValues(double[] inverseCumulativeTestValues) {
        this.inverseCumulativeTestValues = inverseCumulativeTestValues;
    }

    /**
     * @return Returns the tolerance.
     */
    protected double getTolerance() {
        return tolerance;
    }

    /**
     * @param tolerance The tolerance to set.
     */
    protected void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }

    /**
     * Serialization and deserialization loop of the {@link #distribution}.
     */
    private ContinuousDistribution deepClone()
        throws IOException,
               ClassNotFoundException {
        // Serialize to byte array.
        final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        final ObjectOutputStream oOut = new ObjectOutputStream(bOut);
        oOut.writeObject(distribution);
        final byte[] data = bOut.toByteArray();

        // Deserialize from byte array.
        final ByteArrayInputStream bIn = new ByteArrayInputStream(data);
        final ObjectInputStream oIn = new ObjectInputStream(bIn);
        final Object clone = oIn.readObject();
        oIn.close();

        return (ContinuousDistribution) clone;
    }
}
