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
package org.apache.commons.math3.distribution;


import java.util.ArrayList;
import java.util.Collections;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.BaseAbstractUnivariateIntegrator;
import org.apache.commons.math3.analysis.integration.IterativeLegendreGaussIntegrator;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Abstract base class for {@link RealDistribution} tests.
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
 * makeCumulativeTestValues() -- expected cumulative probabilites
 * makeDensityTestValues() -- expected density values at cumulativeTestPoints
 * makeInverseCumulativeTestPoints() -- arguments used to test inverse cdf
 * makeInverseCumulativeTestValues() -- expected inverse cdf values
 * <p>
 * To implement additional test cases with different distribution instances and
 * test data, use the setXxx methods for the instance data in test cases and
 * call the verifyXxx methods to verify results.
 * <p>
 * Error tolerance can be overriden by implementing getTolerance().
 * <p>
 * Test data should be validated against reference tables or other packages
 * where possible, and the source of the reference data and/or validation
 * should be documented in the test cases.  A framework for validating
 * distribution data against R is included in the /src/test/R source tree.
 * <p>
 * See {@link NormalDistributionTest} and {@link ChiSquaredDistributionTest}
 * for examples.
 *
 */
public abstract class RealDistributionAbstractTest {

//-------------------- Private test instance data -------------------------
    /**  Distribution instance used to perform tests */
    private RealDistribution distribution;

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
    public abstract RealDistribution makeDistribution();

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
            logDensityTestValues[i] = FastMath.log(densityTestValues[i]);
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
    @SuppressWarnings("deprecation")
    protected void verifyCumulativeProbabilities() {
        // verify cumulativeProbability(double)
        for (int i = 0; i < cumulativeTestPoints.length; i++) {
            TestUtils.assertEquals("Incorrect cumulative probability value returned for "
                + cumulativeTestPoints[i], cumulativeTestValues[i],
                distribution.cumulativeProbability(cumulativeTestPoints[i]),
                getTolerance());
        }
        // verify cumulativeProbability(double, double)
        // XXX In 4.0, "cumulativeProbability(double,double)" must be replaced with "probability" (MATH-839).
        for (int i = 0; i < cumulativeTestPoints.length; i++) {
            for (int j = 0; j < cumulativeTestPoints.length; j++) {
                if (cumulativeTestPoints[i] <= cumulativeTestPoints[j]) {
                    TestUtils.assertEquals(cumulativeTestValues[j] - cumulativeTestValues[i],
                        distribution.cumulativeProbability(cumulativeTestPoints[i], cumulativeTestPoints[j]),
                        getTolerance());
                } else {
                    try {
                        distribution.cumulativeProbability(cumulativeTestPoints[i], cumulativeTestPoints[j]);
                    } catch (NumberIsTooLargeException e) {
                        continue;
                    }
                    Assert.fail("distribution.cumulativeProbability(double, double) should have thrown an exception that second argument is too large");
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
        // FIXME: when logProbability methods are added to RealDistribution in 4.0, remove cast below
        for (int i = 0; i < cumulativeTestPoints.length; i++) {
            TestUtils.assertEquals("Incorrect probability density value returned for "
                    + cumulativeTestPoints[i], logDensityTestValues[i],
                    ((AbstractRealDistribution) distribution).logDensity(cumulativeTestPoints[i]),
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
    @SuppressWarnings("deprecation")
    @Test
    public void testConsistency() {
        for (int i=1; i < cumulativeTestPoints.length; i++) {

            // check that cdf(x, x) = 0
            // XXX In 4.0, "cumulativeProbability(double,double)" must be replaced with "probability" (MATH-839).
            TestUtils.assertEquals(0d,
               distribution.cumulativeProbability
                 (cumulativeTestPoints[i], cumulativeTestPoints[i]), tolerance);

            // check that P(a < X <= b) = P(X <= b) - P(X <= a)
            double upper = FastMath.max(cumulativeTestPoints[i], cumulativeTestPoints[i -1]);
            double lower = FastMath.min(cumulativeTestPoints[i], cumulativeTestPoints[i -1]);
            double diff = distribution.cumulativeProbability(upper) -
                distribution.cumulativeProbability(lower);
            // XXX In 4.0, "cumulativeProbability(double,double)" must be replaced with "probability" (MATH-839).
            double direct = distribution.cumulativeProbability(lower, upper);
            TestUtils.assertEquals("Inconsistent cumulative probabilities for ("
                    + lower + "," + upper + ")", diff, direct, tolerance);
        }
    }

    /**
     * Verifies that illegal arguments are correctly handled
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testIllegalArguments() {
        try {
            // XXX In 4.0, "cumulativeProbability(double,double)" must be replaced with "probability" (MATH-839).
            distribution.cumulativeProbability(1, 0);
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
    public void testSampling() {
        final int sampleSize = 1000;
        distribution.reseedRandomGenerator(1000); // Use fixed seed
        double[] sample = distribution.sample(sampleSize);
        double[] quartiles = TestUtils.getDistributionQuartiles(distribution);
        double[] expected = {250, 250, 250, 250};
        long[] counts = new long[4];
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
    @SuppressWarnings("deprecation")
    @Test
    public void testDensityIntegrals() {
        final double tol = 1.0e-9;
        final BaseAbstractUnivariateIntegrator integrator =
            new IterativeLegendreGaussIntegrator(5, 1.0e-12, 1.0e-10);
        final UnivariateFunction d = new UnivariateFunction() {
            public double value(double x) {
                return distribution.density(x);
            }
        };
        final ArrayList<Double> integrationTestPoints = new ArrayList<Double>();
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
                    distribution.cumulativeProbability(  // FIXME @4.0 when rename happens
                            integrationTestPoints.get(0), integrationTestPoints.get(i)),
                            integrator.integrate(
                                    1000000, // Triangle integrals are very slow to converge
                                    d, integrationTestPoints.get(0),
                                    integrationTestPoints.get(i)), tol);
        }
    }

    /**
     * Verify that isSupportLowerBoundInclusvie returns true iff the lower bound
     * is finite and density is non-NaN, non-infinite there.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testIsSupportLowerBoundInclusive() {
        final double lowerBound = distribution.getSupportLowerBound();
        double result = Double.NaN;
        result = distribution.density(lowerBound);
        Assert.assertEquals(
                !Double.isInfinite(lowerBound) && !Double.isNaN(result) &&
                !Double.isInfinite(result),
                distribution.isSupportLowerBoundInclusive());

    }

    /**
     * Verify that isSupportUpperBoundInclusvie returns true iff the upper bound
     * is finite and density is non-NaN, non-infinite there.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testIsSupportUpperBoundInclusive() {
        final double upperBound = distribution.getSupportUpperBound();
        double result = Double.NaN;
        result = distribution.density(upperBound);
        Assert.assertEquals(
                !Double.isInfinite(upperBound) && !Double.isNaN(result) &&
                !Double.isInfinite(result),
                distribution.isSupportUpperBoundInclusive());

    }

    @Test
    public void testDistributionClone()
        throws IOException,
               ClassNotFoundException {
        // Construct a distribution and initialize its internal random
        // generator, using a fixed seed for deterministic results.
        distribution.reseedRandomGenerator(123);
        distribution.sample();

        // Clone the distribution.
        final RealDistribution cloned = deepClone();

        // Make sure they still produce the same samples.
        final double s1 = distribution.sample();
        final double s2 = cloned.sample();
        Assert.assertEquals(s1, s2, 0d);
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
    protected RealDistribution getDistribution() {
        return distribution;
    }

    /**
     * @param distribution The distribution to set.
     */
    protected void setDistribution(RealDistribution distribution) {
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
    private RealDistribution deepClone()
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

        return (RealDistribution) clone;
    }
}
