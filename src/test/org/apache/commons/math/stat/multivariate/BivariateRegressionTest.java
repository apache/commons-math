/*
 * Copyright 2003-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.stat.multivariate;

import java.util.Random;

import org.apache.commons.math.MathException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Test cases for the TestStatistic class.
 *
 * @version $Revision: 1.1 $ $Date: 2004/04/11 21:52:28 $
 */

public final class BivariateRegressionTest extends TestCase {

    /* 
     * NIST "Norris" refernce data set from 
     * http://www.itl.nist.gov/div898/strd/lls/data/LINKS/DATA/Norris.dat
     * Strangely, order is {y,x}
     */
    private double[][] data = { { 0.1, 0.2 }, {
            338.8, 337.4 }, {
            118.1, 118.2 }, {
            888.0, 884.6 }, {
            9.2, 10.1 }, {
            228.1, 226.5 }, {
            668.5, 666.3 }, {
            998.5, 996.3 }, {
            449.1, 448.6 }, {
            778.9, 777.0 }, {
            559.2, 558.2 }, {
            0.3, 0.4 }, {
            0.1, 0.6 }, {
            778.1, 775.5 }, {
            668.8, 666.9 }, {
            339.3, 338.0 }, {
            448.9, 447.5 }, {
            10.8, 11.6 }, {
            557.7, 556.0 }, {
            228.3, 228.1 }, {
            998.0, 995.8 }, {
            888.8, 887.6 }, {
            119.6, 120.2 }, {
            0.3, 0.3 }, {
            0.6, 0.3 }, {
            557.6, 556.8 }, {
            339.3, 339.1 }, {
            888.0, 887.2 }, {
            998.5, 999.0 }, {
            778.9, 779.0 }, {
            10.2, 11.1 }, {
            117.6, 118.3 }, {
            228.9, 229.2 }, {
            668.4, 669.1 }, {
            449.2, 448.9 }, {
            0.2, 0.5 }
    };

    /* 
     * Correlation example from 
     * http://www.xycoon.com/correlation.htm
     */
    private double[][] corrData = { { 101.0, 99.2 }, {
            100.1, 99.0 }, {
            100.0, 100.0 }, {
            90.6, 111.6 }, {
            86.5, 122.2 }, {
            89.7, 117.6 }, {
            90.6, 121.1 }, {
            82.8, 136.0 }, {
            70.1, 154.2 }, {
            65.4, 153.6 }, {
            61.3, 158.5 }, {
            62.5, 140.6 }, {
            63.6, 136.2 }, {
            52.6, 168.0 }, {
            59.7, 154.3 }, {
            59.5, 149.0 }, {
            61.3, 165.5 }
    };

    /*
     * From Moore and Mcabe, "Introduction to the Practice of Statistics"
     * Example 10.3 
     */
    private double[][] infData = { { 15.6, 5.2 }, {
            26.8, 6.1 }, {
            37.8, 8.7 }, {
            36.4, 8.5 }, {
            35.5, 8.8 }, {
            18.6, 4.9 }, {
            15.3, 4.5 }, {
            7.9, 2.5 }, {
            0.0, 1.1 }
    };

    /*
     * From http://www.xycoon.com/simple_linear_regression.htm
     */
    private double[][] infData2 = { { 1, 3 }, {
            2, 5 }, {
            3, 7 }, {
            4, 14 }, {
            5, 11 }
    };

    public BivariateRegressionTest(String name) {
        super(name);
    }

    public void setUp() {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(BivariateRegressionTest.class);
        suite.setName("BivariateRegression Tests");
        return suite;
    }

    public void testNorris() {
        BivariateRegression regression = new BivariateRegression();
        for (int i = 0; i < data.length; i++) {
            regression.addData(data[i][1], data[i][0]);
        }
        assertEquals("slope", 1.00211681802045, regression.getSlope(), 10E-12);
        assertEquals(
            "slope std err",
            0.429796848199937E-03,
            regression.getSlopeStdErr(),
            10E-12);
        assertEquals("number of observations", 36, regression.getN());
        assertEquals(
            "intercept",
            -0.262323073774029,
            regression.getIntercept(),
            10E-12);
        assertEquals(
            "std err intercept",
            0.232818234301152,
            regression.getInterceptStdErr(),
            10E-12);
        assertEquals(
            "r-square",
            0.999993745883712,
            regression.getRSquare(),
            10E-12);
        assertEquals(
            "SSR",
            4255954.13232369,
            regression.getRegressionSumSquares(),
            10E-9);
        assertEquals(
            "MSE",
            0.782864662630069,
            regression.getMeanSquareError(),
            10E-10);
        assertEquals(
            "SSE",
            26.6173985294224,
            regression.getSumSquaredErrors(),
            10E-9);
        assertEquals(
            "predict(0)",
            -0.262323073774029,
            regression.predict(0),
            10E-12);
        assertEquals(
            "predict(1)",
            1.00211681802045 - 0.262323073774029,
            regression.predict(1),
            10E-12);
    }

    public void testCorr() {
        BivariateRegression regression = new BivariateRegression();
        regression.addData(corrData);
        assertEquals("number of observations", 17, regression.getN());
        assertEquals("r-square", .896123, regression.getRSquare(), 10E-6);
        assertEquals("r", -.946638, regression.getR(), 10E-6);
    }

    public void testNaNs() {

        BivariateRegression regression = new BivariateRegression();

        assertTrue(
            "intercept not NaN",
            Double.isNaN(regression.getIntercept()));
        assertTrue("slope not NaN", Double.isNaN(regression.getSlope()));
        assertTrue(
            "slope std err not NaN",
            Double.isNaN(regression.getSlopeStdErr()));
        assertTrue(
            "intercept std err not NaN",
            Double.isNaN(regression.getInterceptStdErr()));
        assertTrue(
            "MSE not NaN",
            Double.isNaN(regression.getMeanSquareError()));
        assertTrue("e not NaN", Double.isNaN(regression.getR()));
        assertTrue("r-square not NaN", Double.isNaN(regression.getRSquare()));
        assertTrue(
            "RSS not NaN",
            Double.isNaN(regression.getRegressionSumSquares()));
        assertTrue(
            "SSE not NaN",
            Double.isNaN(regression.getSumSquaredErrors()));
        assertTrue(
            "SSTO not NaN",
            Double.isNaN(regression.getTotalSumSquares()));
        assertTrue("predict not NaN", Double.isNaN(regression.predict(0)));

        regression.addData(1, 2);
        regression.addData(1, 3);

        // No x variation, so these should still blow...
        assertTrue(
            "intercept not NaN",
            Double.isNaN(regression.getIntercept()));
        assertTrue("slope not NaN", Double.isNaN(regression.getSlope()));
        assertTrue(
            "slope std err not NaN",
            Double.isNaN(regression.getSlopeStdErr()));
        assertTrue(
            "intercept std err not NaN",
            Double.isNaN(regression.getInterceptStdErr()));
        assertTrue(
            "MSE not NaN",
            Double.isNaN(regression.getMeanSquareError()));
        assertTrue("e not NaN", Double.isNaN(regression.getR()));
        assertTrue("r-square not NaN", Double.isNaN(regression.getRSquare()));
        assertTrue(
            "RSS not NaN",
            Double.isNaN(regression.getRegressionSumSquares()));
        assertTrue(
            "SSE not NaN",
            Double.isNaN(regression.getSumSquaredErrors()));
        assertTrue("predict not NaN", Double.isNaN(regression.predict(0)));

        // but SSTO should be OK
        assertTrue("SSTO NaN", !Double.isNaN(regression.getTotalSumSquares()));

        regression = new BivariateRegression();

        regression.addData(1, 2);
        regression.addData(3, 3);

        // All should be OK except MSE, s(b0), s(b1) which need one more df 
        assertTrue("interceptNaN", !Double.isNaN(regression.getIntercept()));
        assertTrue("slope NaN", !Double.isNaN(regression.getSlope()));
        assertTrue(
            "slope std err not NaN",
            Double.isNaN(regression.getSlopeStdErr()));
        assertTrue(
            "intercept std err not NaN",
            Double.isNaN(regression.getInterceptStdErr()));
        assertTrue(
            "MSE not NaN",
            Double.isNaN(regression.getMeanSquareError()));
        assertTrue("r NaN", !Double.isNaN(regression.getR()));
        assertTrue("r-square NaN", !Double.isNaN(regression.getRSquare()));
        assertTrue(
            "RSS NaN",
            !Double.isNaN(regression.getRegressionSumSquares()));
        assertTrue("SSE NaN", !Double.isNaN(regression.getSumSquaredErrors()));
        assertTrue("SSTO NaN", !Double.isNaN(regression.getTotalSumSquares()));
        assertTrue("predict NaN", !Double.isNaN(regression.predict(0)));

        regression.addData(1, 4);

        // MSE, MSE, s(b0), s(b1) should all be OK now
        assertTrue("MSE NaN", !Double.isNaN(regression.getMeanSquareError()));
        assertTrue(
            "slope std err NaN",
            !Double.isNaN(regression.getSlopeStdErr()));
        assertTrue(
            "intercept std err NaN",
            !Double.isNaN(regression.getInterceptStdErr()));
    }

    public void testClear() {
        BivariateRegression regression = new BivariateRegression();
        regression.addData(corrData);
        assertEquals("number of observations", 17, regression.getN());
        regression.clear();
        assertEquals("number of observations", 0, regression.getN());
        regression.addData(corrData);
        assertEquals("r-square", .896123, regression.getRSquare(), 10E-6);
        regression.addData(data);
        assertEquals("number of observations", 53, regression.getN());
    }

    public void testInference() {

        BivariateRegression regression = new BivariateRegression();
        regression.addData(infData);

        try {
            assertEquals(
                "slope confidence interval",
                0.0271,
                regression.getSlopeConfidenceInterval(),
                0.0001);
            assertEquals(
                "slope std err",
                0.01146,
                regression.getSlopeStdErr(),
                0.0001);

            regression = new BivariateRegression();
            regression.addData(infData2);
            assertEquals(
                "significance",
                0.023331,
                regression.getSignificance(),
                0.0001);

            //FIXME: get a real example to test against with alpha = .01
            assertTrue(
                "tighter means wider",
                regression.getSlopeConfidenceInterval()
                    < regression.getSlopeConfidenceInterval(0.01));

        } catch (MathException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            double x = regression.getSlopeConfidenceInterval(1);
            fail("expecting IllegalArgumentException for alpha = 1");
        } catch (IllegalArgumentException ex) {
            ;
        } catch (MathException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void testPerfect() {
        BivariateRegression regression = new BivariateRegression();
        int n = 100;
        for (int i = 0; i < n; i++) {
            regression.addData(((double) i) / (n - 1), i);
        }

        try {
            assertEquals(0.0, regression.getSignificance(), 1.0e-5);
            assertTrue(regression.getSlope() > 0.0);
        } catch (MathException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void testPerfectNegative() {
        BivariateRegression regression = new BivariateRegression();
        int n = 100;
        for (int i = 0; i < n; i++) {
            regression.addData(- ((double) i) / (n - 1), i);
        }
        try {
            assertEquals(0.0, regression.getSignificance(), 1.0e-5);
            assertTrue(regression.getSlope() < 0.0);
        } catch (MathException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void testRandom() {
        BivariateRegression regression = new BivariateRegression();
        Random random = new Random(1);
        int n = 100;
        for (int i = 0; i < n; i++) {
            regression.addData(((double) i) / (n - 1), random.nextDouble());
        }

        try {
            assertTrue(
                0.0 < regression.getSignificance()
                    && regression.getSignificance() < 1.0);
        } catch (MathException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
