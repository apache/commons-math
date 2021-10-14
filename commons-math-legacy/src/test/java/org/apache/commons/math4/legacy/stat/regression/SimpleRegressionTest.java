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
package org.apache.commons.math4.legacy.stat.regression;

import java.util.Random;

import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.apache.commons.math4.legacy.exception.OutOfRangeException;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for the TestStatistic class.
 *
 */

public final class SimpleRegressionTest {

    /*
     * NIST "Norris" reference data set from
     * http://www.itl.nist.gov/div898/strd/lls/data/LINKS/DATA/Norris.dat
     * Strangely, order is {y,x}
     */
    private double[][] data = { { 0.1, 0.2 }, {338.8, 337.4 }, {118.1, 118.2 },
            {888.0, 884.6 }, {9.2, 10.1 }, {228.1, 226.5 }, {668.5, 666.3 }, {998.5, 996.3 },
            {449.1, 448.6 }, {778.9, 777.0 }, {559.2, 558.2 }, {0.3, 0.4 }, {0.1, 0.6 }, {778.1, 775.5 },
            {668.8, 666.9 }, {339.3, 338.0 }, {448.9, 447.5 }, {10.8, 11.6 }, {557.7, 556.0 },
            {228.3, 228.1 }, {998.0, 995.8 }, {888.8, 887.6 }, {119.6, 120.2 }, {0.3, 0.3 },
            {0.6, 0.3 }, {557.6, 556.8 }, {339.3, 339.1 }, {888.0, 887.2 }, {998.5, 999.0 },
            {778.9, 779.0 }, {10.2, 11.1 }, {117.6, 118.3 }, {228.9, 229.2 }, {668.4, 669.1 },
            {449.2, 448.9 }, {0.2, 0.5 }
    };

    /*
     * Correlation example from
     * http://www.xycoon.com/correlation.htm
     */
    private double[][] corrData = { { 101.0, 99.2 }, {100.1, 99.0 }, {100.0, 100.0 },
            {90.6, 111.6 }, {86.5, 122.2 }, {89.7, 117.6 }, {90.6, 121.1 }, {82.8, 136.0 },
            {70.1, 154.2 }, {65.4, 153.6 }, {61.3, 158.5 }, {62.5, 140.6 }, {63.6, 136.2 },
            {52.6, 168.0 }, {59.7, 154.3 }, {59.5, 149.0 }, {61.3, 165.5 }
    };

    /*
     * From Moore and Mcabe, "Introduction to the Practice of Statistics"
     * Example 10.3
     */
    private double[][] infData = { { 15.6, 5.2 }, {26.8, 6.1 }, {37.8, 8.7 }, {36.4, 8.5 },
            {35.5, 8.8 }, {18.6, 4.9 }, {15.3, 4.5 }, {7.9, 2.5 }, {0.0, 1.1 }
    };

    /*
     * Points to remove in the remove tests
     */
    private double[][] removeSingle = {infData[1]};
    private double[][] removeMultiple = { infData[1], infData[2] };
    private double removeX = infData[0][0];
    private double removeY = infData[0][1];


    /*
     * Data with bad linear fit
     */
    private double[][] infData2 = { { 1, 1 }, {2, 0 }, {3, 5 }, {4, 2 },
            {5, -1 }, {6, 12 }
    };


    /*
     * Data from NIST NOINT1
     */
    private double[][] noint1 = {
        {130.0,60.0},
        {131.0,61.0},
        {132.0,62.0},
        {133.0,63.0},
        {134.0,64.0},
        {135.0,65.0},
        {136.0,66.0},
        {137.0,67.0},
        {138.0,68.0},
        {139.0,69.0},
        {140.0,70.0}
    };

    /*
     * Data from NIST NOINT2
     *
     */
    private double[][] noint2 = {
        {3.0,4},
        {4,5},
        {4,6}
    };


    /**
     * Test that the SimpleRegression objects generated from combining two
     * SimpleRegression objects created from subsets of data are identical to
     * SimpleRegression objects created from the combined data.
     */
    @Test
    public void testAppend() {
        check(false);
        check(true);
    }

    /**
     * Checks that adding data to a single model gives the same result
     * as adding "parts" of the dataset to smaller models and using append
     * to aggregate the smaller models.
     *
     * @param includeIntercept
     */
    private void check(boolean includeIntercept) {
        final int sets = 2;
        final UniformRandomProvider rand = RandomSource.ISAAC.create(10L);// Seed can be changed
        final SimpleRegression whole = new SimpleRegression(includeIntercept);// regression of the whole set
        final SimpleRegression parts = new SimpleRegression(includeIntercept);// regression with parts.

        for (int s = 0; s < sets; s++) {// loop through each subset of data.
            final double coef = rand.nextDouble();
            final SimpleRegression sub = new SimpleRegression(includeIntercept);// sub regression
            for (int i = 0; i < 5; i++) { // loop through individual samlpes.
                final double x = rand.nextDouble();
                final double y = x * coef + rand.nextDouble();// some noise
                sub.addData(x, y);
                whole.addData(x, y);
            }
            parts.append(sub);
            Assert.assertTrue(equals(parts, whole, 1E-6));
        }
    }

    /**
     * Returns true iff the statistics reported by model1 are all within tol of
     * those reported by model2.
     *
     * @param model1 first model
     * @param model2 second model
     * @param tol tolerance
     * @return true if the two models report the same regression stats
     */
    private boolean equals(SimpleRegression model1, SimpleRegression model2, double tol) {
        if (model1.getN() != model2.getN()) {
            return false;
        }
        if (JdkMath.abs(model1.getIntercept() - model2.getIntercept()) > tol) {
            return false;
        }
        if (JdkMath.abs(model1.getInterceptStdErr() - model2.getInterceptStdErr()) > tol) {
            return false;
        }
        if (JdkMath.abs(model1.getMeanSquareError() - model2.getMeanSquareError()) > tol) {
            return false;
        }
        if (JdkMath.abs(model1.getR() - model2.getR()) > tol) {
            return false;
        }
        if (JdkMath.abs(model1.getRegressionSumSquares() - model2.getRegressionSumSquares()) > tol) {
            return false;
        }
        if (JdkMath.abs(model1.getRSquare() - model2.getRSquare()) > tol) {
            return false;
        }
        if (JdkMath.abs(model1.getSignificance() - model2.getSignificance()) > tol) {
            return false;
        }
        if (JdkMath.abs(model1.getSlope() - model2.getSlope()) > tol) {
            return false;
        }
        if (JdkMath.abs(model1.getSlopeConfidenceInterval() - model2.getSlopeConfidenceInterval()) > tol) {
            return false;
        }
        if (JdkMath.abs(model1.getSlopeStdErr() - model2.getSlopeStdErr()) > tol) {
            return false;
        }
        if (JdkMath.abs(model1.getSumOfCrossProducts() - model2.getSumOfCrossProducts()) > tol) {
            return false;
        }
        if (JdkMath.abs(model1.getSumSquaredErrors() - model2.getSumSquaredErrors()) > tol) {
            return false;
        }
        if (JdkMath.abs(model1.getTotalSumSquares() - model2.getTotalSumSquares()) > tol) {
            return false;
        }
        if (JdkMath.abs(model1.getXSumSquares() - model2.getXSumSquares()) > tol) {
            return false;
        }
        return true;
    }

    @Test
    public void testRegressIfaceMethod(){
        final SimpleRegression regression = new SimpleRegression(true);
        final UpdatingMultipleLinearRegression iface = regression;
        final SimpleRegression regressionNoint = new SimpleRegression( false );
        final SimpleRegression regressionIntOnly= new SimpleRegression( false );
        for (int i = 0; i < data.length; i++) {
            iface.addObservation( new double[]{data[i][1]}, data[i][0]);
            regressionNoint.addData(data[i][1], data[i][0]);
            regressionIntOnly.addData(1.0, data[i][0]);
        }

        //should not be null
        final RegressionResults fullReg = iface.regress( );
        Assert.assertNotNull(fullReg);
        Assert.assertEquals("intercept", regression.getIntercept(), fullReg.getParameterEstimate(0), 1.0e-16);
        Assert.assertEquals("intercept std err",regression.getInterceptStdErr(), fullReg.getStdErrorOfEstimate(0),1.0E-16);
        Assert.assertEquals("slope", regression.getSlope(), fullReg.getParameterEstimate(1), 1.0e-16);
        Assert.assertEquals("slope std err",regression.getSlopeStdErr(), fullReg.getStdErrorOfEstimate(1),1.0E-16);
        Assert.assertEquals("number of observations",regression.getN(), fullReg.getN());
        Assert.assertEquals("r-square",regression.getRSquare(), fullReg.getRSquared(), 1.0E-16);
        Assert.assertEquals("SSR", regression.getRegressionSumSquares(), fullReg.getRegressionSumSquares() ,1.0E-16);
        Assert.assertEquals("MSE", regression.getMeanSquareError(), fullReg.getMeanSquareError() ,1.0E-16);
        Assert.assertEquals("SSE", regression.getSumSquaredErrors(), fullReg.getErrorSumSquares() ,1.0E-16);


        final RegressionResults noInt   = iface.regress( new int[]{1} );
        Assert.assertNotNull(noInt);
        Assert.assertEquals("slope", regressionNoint.getSlope(), noInt.getParameterEstimate(0), 1.0e-12);
        Assert.assertEquals("slope std err",regressionNoint.getSlopeStdErr(), noInt.getStdErrorOfEstimate(0),1.0E-16);
        Assert.assertEquals("number of observations",regressionNoint.getN(), noInt.getN());
        Assert.assertEquals("r-square",regressionNoint.getRSquare(), noInt.getRSquared(), 1.0E-16);
        Assert.assertEquals("SSR", regressionNoint.getRegressionSumSquares(), noInt.getRegressionSumSquares() ,1.0E-8);
        Assert.assertEquals("MSE", regressionNoint.getMeanSquareError(), noInt.getMeanSquareError() ,1.0E-16);
        Assert.assertEquals("SSE", regressionNoint.getSumSquaredErrors(), noInt.getErrorSumSquares() ,1.0E-16);

        final RegressionResults onlyInt = iface.regress( new int[]{0} );
        Assert.assertNotNull(onlyInt);
        Assert.assertEquals("slope", regressionIntOnly.getSlope(), onlyInt.getParameterEstimate(0), 1.0e-12);
        Assert.assertEquals("slope std err",regressionIntOnly.getSlopeStdErr(), onlyInt.getStdErrorOfEstimate(0),1.0E-12);
        Assert.assertEquals("number of observations",regressionIntOnly.getN(), onlyInt.getN());
        Assert.assertEquals("r-square",regressionIntOnly.getRSquare(), onlyInt.getRSquared(), 1.0E-14);
        Assert.assertEquals("SSE", regressionIntOnly.getSumSquaredErrors(), onlyInt.getErrorSumSquares() ,1.0E-8);
        Assert.assertEquals("SSR", regressionIntOnly.getRegressionSumSquares(), onlyInt.getRegressionSumSquares() ,1.0E-8);
        Assert.assertEquals("MSE", regressionIntOnly.getMeanSquareError(), onlyInt.getMeanSquareError() ,1.0E-8);

    }

    /**
     * Verify that regress generates exceptions as advertised for bad model specifications.
     */
    @Test
    public void testRegressExceptions() {
        // No intercept
        final SimpleRegression noIntRegression = new SimpleRegression(false);
        noIntRegression.addData(noint2[0][1], noint2[0][0]);
        noIntRegression.addData(noint2[1][1], noint2[1][0]);
        noIntRegression.addData(noint2[2][1], noint2[2][0]);
        try { // null array
            noIntRegression.regress(null);
            Assert.fail("Expecting MathIllegalArgumentException for null array");
        } catch (MathIllegalArgumentException ex) {
            // Expected
        }
        try { // empty array
            noIntRegression.regress(new int[] {});
            Assert.fail("Expecting MathIllegalArgumentException for empty array");
        } catch (MathIllegalArgumentException ex) {
            // Expected
        }
        try { // more than 1 regressor
            noIntRegression.regress(new int[] {0, 1});
            Assert.fail("Expecting ModelSpecificationException - too many regressors");
        } catch (ModelSpecificationException ex) {
            // Expected
        }
        try { // invalid regressor
            noIntRegression.regress(new int[] {1});
            Assert.fail("Expecting OutOfRangeException - invalid regression");
        } catch (OutOfRangeException ex) {
            // Expected
        }

        // With intercept
        final SimpleRegression regression = new SimpleRegression(true);
        regression.addData(noint2[0][1], noint2[0][0]);
        regression.addData(noint2[1][1], noint2[1][0]);
        regression.addData(noint2[2][1], noint2[2][0]);
        try { // null array
            regression.regress(null);
            Assert.fail("Expecting MathIllegalArgumentException for null array");
        } catch (MathIllegalArgumentException ex) {
            // Expected
        }
        try { // empty array
            regression.regress(new int[] {});
            Assert.fail("Expecting MathIllegalArgumentException for empty array");
        } catch (MathIllegalArgumentException ex) {
            // Expected
        }
        try { // more than 2 regressors
            regression.regress(new int[] {0, 1, 2});
            Assert.fail("Expecting ModelSpecificationException - too many regressors");
        } catch (ModelSpecificationException ex) {
            // Expected
        }
        try { // wrong order
            regression.regress(new int[] {1,0});
            Assert.fail("Expecting ModelSpecificationException - invalid regression");
        } catch (ModelSpecificationException ex) {
            // Expected
        }
        try { // out of range
            regression.regress(new int[] {3,4});
            Assert.fail("Expecting OutOfRangeException");
        } catch (OutOfRangeException ex) {
            // Expected
        }
        try { // out of range
            regression.regress(new int[] {0,2});
            Assert.fail("Expecting OutOfRangeException");
        } catch (OutOfRangeException ex) {
            // Expected
        }
        try { // out of range
            regression.regress(new int[] {2});
            Assert.fail("Expecting OutOfRangeException");
        } catch (OutOfRangeException ex) {
            // Expected
        }
    }

    @Test
    public void testNoInterceot_noint2(){
         SimpleRegression regression = new SimpleRegression(false);
         regression.addData(noint2[0][1], noint2[0][0]);
         regression.addData(noint2[1][1], noint2[1][0]);
         regression.addData(noint2[2][1], noint2[2][0]);
         Assert.assertEquals("intercept", 0, regression.getIntercept(), 0);
         Assert.assertEquals("slope", 0.727272727272727,
                 regression.getSlope(), 10E-12);
         Assert.assertEquals("slope std err", 0.420827318078432E-01,
                regression.getSlopeStdErr(),10E-12);
        Assert.assertEquals("number of observations", 3, regression.getN());
        Assert.assertEquals("r-square", 0.993348115299335,
            regression.getRSquare(), 10E-12);
        Assert.assertEquals("SSR", 40.7272727272727,
            regression.getRegressionSumSquares(), 10E-9);
        Assert.assertEquals("MSE", 0.136363636363636,
            regression.getMeanSquareError(), 10E-10);
        Assert.assertEquals("SSE", 0.272727272727273,
            regression.getSumSquaredErrors(),10E-9);
    }

    @Test
    public void testNoIntercept_noint1(){
        SimpleRegression regression = new SimpleRegression(false);
        for (int i = 0; i < noint1.length; i++) {
            regression.addData(noint1[i][1], noint1[i][0]);
        }
        Assert.assertEquals("intercept", 0, regression.getIntercept(), 0);
        Assert.assertEquals("slope", 2.07438016528926, regression.getSlope(), 10E-12);
        Assert.assertEquals("slope std err", 0.165289256198347E-01,
                regression.getSlopeStdErr(),10E-12);
        Assert.assertEquals("number of observations", 11, regression.getN());
        Assert.assertEquals("r-square", 0.999365492298663,
            regression.getRSquare(), 10E-12);
        Assert.assertEquals("SSR", 200457.727272727,
            regression.getRegressionSumSquares(), 10E-9);
        Assert.assertEquals("MSE", 12.7272727272727,
            regression.getMeanSquareError(), 10E-10);
        Assert.assertEquals("SSE", 127.272727272727,
            regression.getSumSquaredErrors(),10E-9);

    }

    @Test
    public void testNorris() {
        SimpleRegression regression = new SimpleRegression();
        for (int i = 0; i < data.length; i++) {
            regression.addData(data[i][1], data[i][0]);
        }
        // Tests against certified values from
        // http://www.itl.nist.gov/div898/strd/lls/data/LINKS/DATA/Norris.dat
        Assert.assertEquals("slope", 1.00211681802045, regression.getSlope(), 10E-12);
        Assert.assertEquals("slope std err", 0.429796848199937E-03,
                regression.getSlopeStdErr(),10E-12);
        Assert.assertEquals("number of observations", 36, regression.getN());
        Assert.assertEquals( "intercept", -0.262323073774029,
            regression.getIntercept(),10E-12);
        Assert.assertEquals("std err intercept", 0.232818234301152,
            regression.getInterceptStdErr(),10E-12);
        Assert.assertEquals("r-square", 0.999993745883712,
            regression.getRSquare(), 10E-12);
        Assert.assertEquals("SSR", 4255954.13232369,
            regression.getRegressionSumSquares(), 10E-9);
        Assert.assertEquals("MSE", 0.782864662630069,
            regression.getMeanSquareError(), 10E-10);
        Assert.assertEquals("SSE", 26.6173985294224,
            regression.getSumSquaredErrors(),10E-9);
        // ------------  End certified data tests

        Assert.assertEquals( "predict(0)",  -0.262323073774029,
            regression.predict(0), 10E-12);
        Assert.assertEquals("predict(1)", 1.00211681802045 - 0.262323073774029,
            regression.predict(1), 10E-12);
    }

    @Test
    public void testCorr() {
        SimpleRegression regression = new SimpleRegression();
        regression.addData(corrData);
        Assert.assertEquals("number of observations", 17, regression.getN());
        Assert.assertEquals("r-square", .896123, regression.getRSquare(), 10E-6);
        Assert.assertEquals("r", -0.94663767742, regression.getR(), 1E-10);
    }

    @Test
    public void testNaNs() {
        SimpleRegression regression = new SimpleRegression();
        Assert.assertTrue("intercept not NaN", Double.isNaN(regression.getIntercept()));
        Assert.assertTrue("slope not NaN", Double.isNaN(regression.getSlope()));
        Assert.assertTrue("slope std err not NaN", Double.isNaN(regression.getSlopeStdErr()));
        Assert.assertTrue("intercept std err not NaN", Double.isNaN(regression.getInterceptStdErr()));
        Assert.assertTrue("MSE not NaN", Double.isNaN(regression.getMeanSquareError()));
        Assert.assertTrue("e not NaN", Double.isNaN(regression.getR()));
        Assert.assertTrue("r-square not NaN", Double.isNaN(regression.getRSquare()));
        Assert.assertTrue( "RSS not NaN", Double.isNaN(regression.getRegressionSumSquares()));
        Assert.assertTrue("SSE not NaN",Double.isNaN(regression.getSumSquaredErrors()));
        Assert.assertTrue("SSTO not NaN", Double.isNaN(regression.getTotalSumSquares()));
        Assert.assertTrue("predict not NaN", Double.isNaN(regression.predict(0)));

        regression.addData(1, 2);
        regression.addData(1, 3);

        // No x variation, so these should still blow...
        Assert.assertTrue("intercept not NaN", Double.isNaN(regression.getIntercept()));
        Assert.assertTrue("slope not NaN", Double.isNaN(regression.getSlope()));
        Assert.assertTrue("slope std err not NaN", Double.isNaN(regression.getSlopeStdErr()));
        Assert.assertTrue("intercept std err not NaN", Double.isNaN(regression.getInterceptStdErr()));
        Assert.assertTrue("MSE not NaN", Double.isNaN(regression.getMeanSquareError()));
        Assert.assertTrue("e not NaN", Double.isNaN(regression.getR()));
        Assert.assertTrue("r-square not NaN", Double.isNaN(regression.getRSquare()));
        Assert.assertTrue("RSS not NaN", Double.isNaN(regression.getRegressionSumSquares()));
        Assert.assertTrue("SSE not NaN", Double.isNaN(regression.getSumSquaredErrors()));
        Assert.assertTrue("predict not NaN", Double.isNaN(regression.predict(0)));

        // but SSTO should be OK
        Assert.assertFalse("SSTO NaN", Double.isNaN(regression.getTotalSumSquares()));

        regression = new SimpleRegression();

        regression.addData(1, 2);
        regression.addData(3, 3);

        // All should be OK except MSE, s(b0), s(b1) which need one more df
        Assert.assertFalse("interceptNaN", Double.isNaN(regression.getIntercept()));
        Assert.assertFalse("slope NaN", Double.isNaN(regression.getSlope()));
        Assert.assertTrue("slope std err not NaN", Double.isNaN(regression.getSlopeStdErr()));
        Assert.assertTrue("intercept std err not NaN", Double.isNaN(regression.getInterceptStdErr()));
        Assert.assertTrue("MSE not NaN", Double.isNaN(regression.getMeanSquareError()));
        Assert.assertFalse("r NaN", Double.isNaN(regression.getR()));
        Assert.assertFalse("r-square NaN", Double.isNaN(regression.getRSquare()));
        Assert.assertFalse("RSS NaN", Double.isNaN(regression.getRegressionSumSquares()));
        Assert.assertFalse("SSE NaN", Double.isNaN(regression.getSumSquaredErrors()));
        Assert.assertFalse("SSTO NaN", Double.isNaN(regression.getTotalSumSquares()));
        Assert.assertFalse("predict NaN", Double.isNaN(regression.predict(0)));

        regression.addData(1, 4);

        // MSE, MSE, s(b0), s(b1) should all be OK now
        Assert.assertFalse("MSE NaN", Double.isNaN(regression.getMeanSquareError()));
        Assert.assertFalse("slope std err NaN", Double.isNaN(regression.getSlopeStdErr()));
        Assert.assertFalse("intercept std err NaN", Double.isNaN(regression.getInterceptStdErr()));
    }

    @Test
    public void testClear() {
        SimpleRegression regression = new SimpleRegression();
        regression.addData(corrData);
        Assert.assertEquals("number of observations", 17, regression.getN());
        regression.clear();
        Assert.assertEquals("number of observations", 0, regression.getN());
        regression.addData(corrData);
        Assert.assertEquals("r-square", .896123, regression.getRSquare(), 10E-6);
        regression.addData(data);
        Assert.assertEquals("number of observations", 53, regression.getN());
    }

    @Test
    public void testInference() {
        //----------  verified against R, version 1.8.1 -----
        // infData
        SimpleRegression regression = new SimpleRegression();
        regression.addData(infData);
        Assert.assertEquals("slope std err", 0.011448491,
                regression.getSlopeStdErr(), 1E-10);
        Assert.assertEquals("std err intercept", 0.286036932,
                regression.getInterceptStdErr(),1E-8);
        Assert.assertEquals("significance", 4.596e-07,
                regression.getSignificance(),1E-8);
        Assert.assertEquals("slope conf interval half-width", 0.0270713794287,
                regression.getSlopeConfidenceInterval(),1E-8);
        // infData2
        regression = new SimpleRegression();
        regression.addData(infData2);
        Assert.assertEquals("slope std err", 1.07260253,
                regression.getSlopeStdErr(), 1E-8);
        Assert.assertEquals("std err intercept",4.17718672,
                regression.getInterceptStdErr(),1E-8);
        Assert.assertEquals("significance", 0.261829133982,
                regression.getSignificance(),1E-11);
        Assert.assertEquals("slope conf interval half-width", 2.97802204827,
                regression.getSlopeConfidenceInterval(),1E-8);
        //------------- End R-verified tests -------------------------------

        //FIXME: get a real example to test against with alpha = .01
        Assert.assertTrue("tighter means wider",
                regression.getSlopeConfidenceInterval() < regression.getSlopeConfidenceInterval(0.01));

        try {
            regression.getSlopeConfidenceInterval(1);
            Assert.fail("expecting MathIllegalArgumentException for alpha = 1");
        } catch (MathIllegalArgumentException ex) {
            // ignored
        }

    }

    @Test
    public void testPerfect() {
        SimpleRegression regression = new SimpleRegression();
        int n = 100;
        for (int i = 0; i < n; i++) {
            regression.addData(((double) i) / (n - 1), i);
        }
        Assert.assertEquals(0.0, regression.getSignificance(), 1.0e-5);
        Assert.assertTrue(regression.getSlope() > 0.0);
        Assert.assertTrue(regression.getSumSquaredErrors() >= 0.0);
    }

    @Test
    public void testPerfect2() {
        SimpleRegression regression = new SimpleRegression();
        regression.addData(0, 0);
        regression.addData(1, 1);
        regression.addData(2, 2);
        Assert.assertEquals(0.0, regression.getSlopeStdErr(), 0.0);
        Assert.assertEquals(0.0, regression.getSignificance(), Double.MIN_VALUE);
        Assert.assertEquals(1, regression.getRSquare(), Double.MIN_VALUE);
    }

    @Test
    public void testPerfectNegative() {
        SimpleRegression regression = new SimpleRegression();
        int n = 100;
        for (int i = 0; i < n; i++) {
            regression.addData(- ((double) i) / (n - 1), i);
        }

        Assert.assertEquals(0.0, regression.getSignificance(), 1.0e-5);
        Assert.assertTrue(regression.getSlope() < 0.0);
    }

    @Test
    public void testRandom() {
        SimpleRegression regression = new SimpleRegression();
        Random random = new Random(1);
        int n = 100;
        for (int i = 0; i < n; i++) {
            regression.addData(((double) i) / (n - 1), random.nextDouble());
        }

        Assert.assertTrue( 0.0 < regression.getSignificance()
                    && regression.getSignificance() < 1.0);
    }


    // Jira MATH-85 = Bugzilla 39432
    @Test
    public void testSSENonNegative() {
        double[] y = { 8915.102, 8919.302, 8923.502 };
        double[] x = { 1.107178495E2, 1.107264895E2, 1.107351295E2 };
        SimpleRegression reg = new SimpleRegression();
        for (int i = 0; i < x.length; i++) {
            reg.addData(x[i], y[i]);
        }
        Assert.assertTrue(reg.getSumSquaredErrors() >= 0.0);
    }

    // Test remove X,Y (single observation)
    @Test
    public void testRemoveXY() {
        // Create regression with inference data then remove to test
        SimpleRegression regression = new SimpleRegression();
        regression.addData(infData);
        regression.removeData(removeX, removeY);
        regression.addData(removeX, removeY);
        // Use the inference assertions to make sure that everything worked
        Assert.assertEquals("slope std err", 0.011448491,
                regression.getSlopeStdErr(), 1E-10);
        Assert.assertEquals("std err intercept", 0.286036932,
                regression.getInterceptStdErr(),1E-8);
        Assert.assertEquals("significance", 4.596e-07,
                regression.getSignificance(),1E-8);
        Assert.assertEquals("slope conf interval half-width", 0.0270713794287,
                regression.getSlopeConfidenceInterval(),1E-8);
     }


    // Test remove single observation in array
    @Test
    public void testRemoveSingle() {
        // Create regression with inference data then remove to test
        SimpleRegression regression = new SimpleRegression();
        regression.addData(infData);
        regression.removeData(removeSingle);
        regression.addData(removeSingle);
        // Use the inference assertions to make sure that everything worked
        Assert.assertEquals("slope std err", 0.011448491,
                regression.getSlopeStdErr(), 1E-10);
        Assert.assertEquals("std err intercept", 0.286036932,
                regression.getInterceptStdErr(),1E-8);
        Assert.assertEquals("significance", 4.596e-07,
                regression.getSignificance(),1E-8);
        Assert.assertEquals("slope conf interval half-width", 0.0270713794287,
                regression.getSlopeConfidenceInterval(),1E-8);
     }

    // Test remove multiple observations
    @Test
    public void testRemoveMultiple() {
        // Create regression with inference data then remove to test
        SimpleRegression regression = new SimpleRegression();
        regression.addData(infData);
        regression.removeData(removeMultiple);
        regression.addData(removeMultiple);
        // Use the inference assertions to make sure that everything worked
        Assert.assertEquals("slope std err", 0.011448491,
                regression.getSlopeStdErr(), 1E-10);
        Assert.assertEquals("std err intercept", 0.286036932,
                regression.getInterceptStdErr(),1E-8);
        Assert.assertEquals("significance", 4.596e-07,
                regression.getSignificance(),1E-8);
        Assert.assertEquals("slope conf interval half-width", 0.0270713794287,
                regression.getSlopeConfidenceInterval(),1E-8);
     }

    // Remove observation when empty
    @Test
    public void testRemoveObsFromEmpty() {
        SimpleRegression regression = new SimpleRegression();
        regression.removeData(removeX, removeY);
        Assert.assertEquals(regression.getN(), 0);
    }

    // Remove single observation to empty
    @Test
    public void testRemoveObsFromSingle() {
        SimpleRegression regression = new SimpleRegression();
        regression.addData(removeX, removeY);
        regression.removeData(removeX, removeY);
        Assert.assertEquals(regression.getN(), 0);
    }

    // Remove multiple observations to empty
    @Test
    public void testRemoveMultipleToEmpty() {
        SimpleRegression regression = new SimpleRegression();
        regression.addData(removeMultiple);
        regression.removeData(removeMultiple);
        Assert.assertEquals(regression.getN(), 0);
    }

    // Remove multiple observations past empty (i.e. size of array > n)
    @Test
    public void testRemoveMultiplePastEmpty() {
        SimpleRegression regression = new SimpleRegression();
        regression.addData(removeX, removeY);
        regression.removeData(removeMultiple);
        Assert.assertEquals(regression.getN(), 0);
    }
}
