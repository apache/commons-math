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

import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.util.FastMath;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Abstract base class for {@link IntegerDistribution} tests.
 * <p>
 * To create a concrete test class for an integer distribution implementation,
 *  implement makeDistribution() to return a distribution instance to use in
 *  tests and each of the test data generation methods below.  In each case, the
 *  test points and test values arrays returned represent parallel arrays of
 *  inputs and expected values for the distribution returned by makeDistribution().
 *  <p>
 *  makeDensityTestPoints() -- arguments used to test probability density calculation
 *  makeDensityTestValues() -- expected probability densities
 *  makeCumulativeTestPoints() -- arguments used to test cumulative probabilities
 *  makeCumulativeTestValues() -- expected cumulative probabilites
 *  makeInverseCumulativeTestPoints() -- arguments used to test inverse cdf evaluation
 *  makeInverseCumulativeTestValues() -- expected inverse cdf values
 * <p>
 *  To implement additional test cases with different distribution instances and test data,
 *  use the setXxx methods for the instance data in test cases and call the verifyXxx methods
 *  to verify results.
 *
 */
public abstract class IntegerDistributionAbstractTest {

//-------------------- Private test instance data -------------------------
    /** Discrete distribution instance used to perform tests */
    private IntegerDistribution distribution;

    /** Tolerance used in comparing expected and returned values */
    private double tolerance = 1E-12;

    /** Arguments used to test probability density calculations */
    private int[] densityTestPoints;

    /** Values used to test probability density calculations */
    private double[] densityTestValues;

    /** Values used to test logarithmic probability density calculations */
    private double[] logDensityTestValues;

    /** Arguments used to test cumulative probability density calculations */
    private int[] cumulativeTestPoints;

    /** Values used to test cumulative probability density calculations */
    private double[] cumulativeTestValues;

    /** Arguments used to test inverse cumulative probability density calculations */
    private double[] inverseCumulativeTestPoints;

    /** Values used to test inverse cumulative probability density calculations */
    private int[] inverseCumulativeTestValues;

    //-------------------- Abstract methods -----------------------------------

    /** Creates the default discrete distribution instance to use in tests. */
    public abstract IntegerDistribution makeDistribution();

    /** Creates the default probability density test input values */
    public abstract int[] makeDensityTestPoints();

    /** Creates the default probability density test expected values */
    public abstract double[] makeDensityTestValues();

    /** Creates the default logarithmic probability density test expected values.
     *
     * The default implementation simply computes the logarithm of all the values in
     * {@link #makeDensityTestValues()}.
     *
     * @return double[] the default logarithmic probability density test expected values.
     */
    public double[] makeLogDensityTestValues() {
        final double[] densityTestValues = makeDensityTestValues();
        final double[] logDensityTestValues = new double[densityTestValues.length];
        for (int i = 0; i < densityTestValues.length; i++) {
            logDensityTestValues[i] = FastMath.log(densityTestValues[i]);
        }
        return logDensityTestValues;
    }

    /** Creates the default cumulative probability density test input values */
    public abstract int[] makeCumulativeTestPoints();

    /** Creates the default cumulative probability density test expected values */
    public abstract double[] makeCumulativeTestValues();

    /** Creates the default inverse cumulative probability test input values */
    public abstract double[] makeInverseCumulativeTestPoints();

    /** Creates the default inverse cumulative probability density test expected values */
    public abstract int[] makeInverseCumulativeTestValues();

    //-------------------- Setup / tear down ----------------------------------

    /**
     * Setup sets all test instance data to default values
     */
    @Before
    public void setUp() {
        distribution = makeDistribution();
        densityTestPoints = makeDensityTestPoints();
        densityTestValues = makeDensityTestValues();
        logDensityTestValues = makeLogDensityTestValues();
        cumulativeTestPoints = makeCumulativeTestPoints();
        cumulativeTestValues = makeCumulativeTestValues();
        inverseCumulativeTestPoints = makeInverseCumulativeTestPoints();
        inverseCumulativeTestValues = makeInverseCumulativeTestValues();
    }

    /**
     * Cleans up test instance data
     */
    @After
    public void tearDown() {
        distribution = null;
        densityTestPoints = null;
        densityTestValues = null;
        logDensityTestValues = null;
        cumulativeTestPoints = null;
        cumulativeTestValues = null;
        inverseCumulativeTestPoints = null;
        inverseCumulativeTestValues = null;
    }

    //-------------------- Verification methods -------------------------------

    /**
     * Verifies that probability density calculations match expected values
     * using current test instance data
     */
    protected void verifyDensities() {
        for (int i = 0; i < densityTestPoints.length; i++) {
            Assert.assertEquals("Incorrect density value returned for " + densityTestPoints[i],
                    densityTestValues[i],
                    distribution.probability(densityTestPoints[i]), getTolerance());
        }
    }

    /**
     * Verifies that logarithmic probability density calculations match expected values
     * using current test instance data.
     */
    protected void verifyLogDensities() {
        for (int i = 0; i < densityTestPoints.length; i++) {
            // FIXME: when logProbability methods are added to IntegerDistribution in 4.0, remove cast below
            Assert.assertEquals("Incorrect log density value returned for " + densityTestPoints[i],
                    logDensityTestValues[i],
                    ((AbstractIntegerDistribution) distribution).logProbability(densityTestPoints[i]), tolerance);
        }
    }

    /**
     * Verifies that cumulative probability density calculations match expected values
     * using current test instance data
     */
    protected void verifyCumulativeProbabilities() {
        for (int i = 0; i < cumulativeTestPoints.length; i++) {
            Assert.assertEquals("Incorrect cumulative probability value returned for " + cumulativeTestPoints[i],
                    cumulativeTestValues[i],
                    distribution.cumulativeProbability(cumulativeTestPoints[i]), getTolerance());
        }
    }


    /**
     * Verifies that inverse cumulative probability density calculations match expected values
     * using current test instance data
     */
    protected void verifyInverseCumulativeProbabilities() {
        for (int i = 0; i < inverseCumulativeTestPoints.length; i++) {
            Assert.assertEquals("Incorrect inverse cumulative probability value returned for "
                    + inverseCumulativeTestPoints[i], inverseCumulativeTestValues[i],
                    distribution.inverseCumulativeProbability(inverseCumulativeTestPoints[i]));
        }
    }

    //------------------------ Default test cases -----------------------------

    /**
     * Verifies that probability density calculations match expected values
     * using default test instance data
     */
    @Test
    public void testDensities() {
        verifyDensities();
    }

    /**
     * Verifies that logarithmic probability density calculations match expected values
     * using default test instance data
     */
    @Test
    public void testLogDensities() {
        verifyLogDensities();
    }

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

    @Test
    public void testConsistencyAtSupportBounds() {
        final int lower = distribution.getSupportLowerBound();
        Assert.assertEquals("Cumulative probability mmust be 0 below support lower bound.",
                0.0, distribution.cumulativeProbability(lower - 1), 0.0);
        Assert.assertEquals("Cumulative probability of support lower bound must be equal to probability mass at this point.",
                distribution.probability(lower), distribution.cumulativeProbability(lower), getTolerance());
        Assert.assertEquals("Inverse cumulative probability of 0 must be equal to support lower bound.",
                lower, distribution.inverseCumulativeProbability(0.0));

        final int upper = distribution.getSupportUpperBound();
        if (upper != Integer.MAX_VALUE)
            Assert.assertEquals("Cumulative probability of support upper bound must be equal to 1.",
                    1.0, distribution.cumulativeProbability(upper), 0.0);
        Assert.assertEquals("Inverse cumulative probability of 1 must be equal to support upper bound.",
                upper, distribution.inverseCumulativeProbability(1.0));
    }

    /**
     * Verifies that illegal arguments are correctly handled
     */
    @Test
    public void testIllegalArguments() {
        try {
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
        int[] densityPoints = makeDensityTestPoints();
        double[] densityValues = makeDensityTestValues();
        int sampleSize = 1000;
        int length = TestUtils.eliminateZeroMassPoints(densityPoints, densityValues);
        AbstractIntegerDistribution distribution = (AbstractIntegerDistribution) makeDistribution();
        double[] expectedCounts = new double[length];
        long[] observedCounts = new long[length];
        for (int i = 0; i < length; i++) {
            expectedCounts[i] = sampleSize * densityValues[i];
        }
        distribution.reseedRandomGenerator(1000); // Use fixed seed
        int[] sample = distribution.sample(sampleSize);
        for (int i = 0; i < sampleSize; i++) {
          for (int j = 0; j < length; j++) {
              if (sample[i] == densityPoints[j]) {
                  observedCounts[j]++;
              }
          }
        }
        TestUtils.assertChiSquareAccept(densityPoints, expectedCounts, observedCounts, .001);
    }

    //------------------ Getters / Setters for test instance data -----------
    /**
     * @return Returns the cumulativeTestPoints.
     */
    protected int[] getCumulativeTestPoints() {
        return cumulativeTestPoints;
    }

    /**
     * @param cumulativeTestPoints The cumulativeTestPoints to set.
     */
    protected void setCumulativeTestPoints(int[] cumulativeTestPoints) {
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

    /**
     * @return Returns the densityTestPoints.
     */
    protected int[] getDensityTestPoints() {
        return densityTestPoints;
    }

    /**
     * @param densityTestPoints The densityTestPoints to set.
     */
    protected void setDensityTestPoints(int[] densityTestPoints) {
        this.densityTestPoints = densityTestPoints;
    }

    /**
     * @return Returns the densityTestValues.
     */
    protected double[] getDensityTestValues() {
        return densityTestValues;
    }

    /**
     * @param densityTestValues The densityTestValues to set.
     */
    protected void setDensityTestValues(double[] densityTestValues) {
        this.densityTestValues = densityTestValues;
    }

    /**
     * @return Returns the distribution.
     */
    protected IntegerDistribution getDistribution() {
        return distribution;
    }

    /**
     * @param distribution The distribution to set.
     */
    protected void setDistribution(IntegerDistribution distribution) {
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
    protected int[] getInverseCumulativeTestValues() {
        return inverseCumulativeTestValues;
    }

    /**
     * @param inverseCumulativeTestValues The inverseCumulativeTestValues to set.
     */
    protected void setInverseCumulativeTestValues(int[] inverseCumulativeTestValues) {
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

}
