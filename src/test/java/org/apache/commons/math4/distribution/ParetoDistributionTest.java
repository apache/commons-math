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

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link ParetoDistribution}.
 * <p>
 * Extends {@link RealDistributionAbstractTest}. See class javadoc of that class for details.
 *
 * @since 3.3
 */
public class ParetoDistributionTest extends RealDistributionAbstractTest {

    //-------------- Implementations for abstract methods -----------------------

    /** Creates the default real distribution instance to use in tests. */
    @Override
    public ParetoDistribution makeDistribution() {
        return new ParetoDistribution(2.1, 1.4);
    }

    /** Creates the default cumulative probability distribution test input values */
    @Override
    public double[] makeCumulativeTestPoints() {
        // quantiles computed using R
        return new double[] { -2.226325228634938, -1.156887023657177, -0.643949578356075, -0.2027950777320613, 0.305827808237559,
                              +6.42632522863494, 5.35688702365718, 4.843949578356074, 4.40279507773206, 3.89417219176244 };
    }

    /** Creates the default cumulative probability density test expected values */
    @Override
    public double[] makeCumulativeTestValues() {
        return new double[] { 0, 0, 0, 0, 0, 0.791089998892, 0.730456085931, 0.689667290488, 0.645278794701, 0.578763688757 };
    }

    /** Creates the default probability density test expected values */
    @Override
    public double[] makeDensityTestValues() {
        return new double[] { 0, 0, 0, 0, 0, 0.0455118580441, 0.070444173646, 0.0896924681582, 0.112794186114, 0.151439332084 };
    }

    /**
     * Creates the default inverse cumulative probability distribution test input values.
     */
    @Override
    public double[] makeInverseCumulativeTestPoints() {
        // Exclude the test points less than zero, as they have cumulative
        // probability of zero, meaning the inverse returns zero, and not the
        // points less than zero.
        double[] points = makeCumulativeTestValues();
        double[] points2 = new double[points.length - 5];
        System.arraycopy(points, 5, points2, 0, points.length - 5);
        return points2;
    }

    /**
     * Creates the default inverse cumulative probability test expected values.
     */
    @Override
    public double[] makeInverseCumulativeTestValues() {
        // Exclude the test points less than zero, as they have cumulative
        // probability of zero, meaning the inverse returns zero, and not the
        // points less than zero.
        double[] points = makeCumulativeTestPoints();
        double[] points2 = new double[points.length - 5];
        System.arraycopy(points, 5, points2, 0, points.length - 5);
        return points2;
    }

    // --------------------- Override tolerance  --------------
    @Override
    public void setUp() {
        super.setUp();
        setTolerance(ParetoDistribution.DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
    }

    //---------------------------- Additional test cases -------------------------

    private void verifyQuantiles() {
        ParetoDistribution distribution = (ParetoDistribution)getDistribution();
        double mu = distribution.getScale();
        double sigma = distribution.getShape();
        setCumulativeTestPoints( new double[] { mu - 2 *sigma,  mu - sigma,
                                                mu,             mu + sigma,
                                                mu + 2 * sigma, mu + 3 * sigma,
                                                mu + 4 * sigma, mu + 5 * sigma });
        verifyCumulativeProbabilities();
    }

    @Test
    public void testQuantiles() {
        setCumulativeTestValues(new double[] {0, 0, 0, 0.510884134236, 0.694625688662, 0.785201995008, 0.837811522357, 0.871634279326});
        setDensityTestValues(new double[] {0, 0, 0.666666666, 0.195646346305, 0.0872498032394, 0.0477328899983, 0.0294888141169, 0.0197485724114});
        verifyQuantiles();
        verifyDensities();

        setDistribution(new ParetoDistribution(1, 1));
        setCumulativeTestValues(new double[] {0, 0, 0, 0.5, 0.666666666667, 0.75, 0.8, 0.833333333333});
        setDensityTestValues(new double[] {0, 0, 1.0, 0.25, 0.111111111111, 0.0625, 0.04, 0.0277777777778});
        verifyQuantiles();
        verifyDensities();

        setDistribution(new ParetoDistribution(0.1, 0.1));
        setCumulativeTestValues(new double[] {0, 0, 0, 0.0669670084632, 0.104041540159, 0.129449436704, 0.148660077479, 0.164041197922});
        setDensityTestValues(new double[] {0, 0, 1.0, 0.466516495768, 0.298652819947, 0.217637640824, 0.170267984504, 0.139326467013});
        verifyQuantiles();
        verifyDensities();
    }

    @Test
    public void testInverseCumulativeProbabilityExtremes() {
        setInverseCumulativeTestPoints(new double[] {0, 1});
        setInverseCumulativeTestValues(new double[] {2.1, Double.POSITIVE_INFINITY});
        verifyInverseCumulativeProbabilities();
    }

    @Test
    public void testGetScale() {
        ParetoDistribution distribution = (ParetoDistribution)getDistribution();
        Assert.assertEquals(2.1, distribution.getScale(), 0);
    }

    @Test
    public void testGetShape() {
        ParetoDistribution distribution = (ParetoDistribution)getDistribution();
        Assert.assertEquals(1.4, distribution.getShape(), 0);
    }

    @Test(expected=NotStrictlyPositiveException.class)
    public void testPreconditions() {
        new ParetoDistribution(1, 0);
    }

    @Test
    public void testDensity() {
        double [] x = new double[]{-2, -1, 0, 1, 2};
        // R 2.14: print(dpareto(c(-2,-1,0,1,2), scale=1, shape=1), digits=10)
        checkDensity(1, 1, x, new double[] { 0.00, 0.00, 0.00, 1.00, 0.25 });
        // R 2.14: print(dpareto(c(-2,-1,0,1,2), scale=1.1, shape=1), digits=10)
        checkDensity(1.1, 1, x, new double[] { 0.000, 0.000, 0.000, 0.000, 0.275 });
    }

    private void checkDensity(double scale, double shape, double[] x,
        double[] expected) {
        ParetoDistribution d = new ParetoDistribution(scale, shape);
        for (int i = 0; i < x.length; i++) {
            Assert.assertEquals(expected[i], d.density(x[i]), 1e-9);
        }
    }

    /**
     * Check to make sure top-coding of extreme values works correctly.
     */
    @Test
    public void testExtremeValues() {
        ParetoDistribution d = new ParetoDistribution(1, 1);
        for (int i = 0; i < 1e5; i++) { // make sure no convergence exception
            double upperTail = d.cumulativeProbability(i);
            if (i <= 1000) { // make sure not top-coded
                Assert.assertTrue(upperTail < 1.0d);
            }
            else { // make sure top coding not reversed
                Assert.assertTrue(upperTail > 0.999);
            }
        }

        Assert.assertEquals(d.cumulativeProbability(Double.MAX_VALUE), 1, 0);
        Assert.assertEquals(d.cumulativeProbability(-Double.MAX_VALUE), 0, 0);
        Assert.assertEquals(d.cumulativeProbability(Double.POSITIVE_INFINITY), 1, 0);
        Assert.assertEquals(d.cumulativeProbability(Double.NEGATIVE_INFINITY), 0, 0);
    }

    @Test
    public void testMeanVariance() {
        final double tol = 1e-9;
        ParetoDistribution dist;

        dist = new ParetoDistribution(1, 1);
        Assert.assertEquals(dist.getNumericalMean(), Double.POSITIVE_INFINITY, tol);
        Assert.assertEquals(dist.getNumericalVariance(), Double.POSITIVE_INFINITY, tol);

        dist = new ParetoDistribution(2.2, 2.4);
        Assert.assertEquals(dist.getNumericalMean(), 3.771428571428, tol);
        Assert.assertEquals(dist.getNumericalVariance(), 14.816326530, tol);
    }
}
