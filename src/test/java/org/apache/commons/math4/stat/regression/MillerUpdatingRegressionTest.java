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
package org.apache.commons.math4.stat.regression;

import org.apache.commons.math4.TestUtils;
import org.apache.commons.math4.exception.MathIllegalArgumentException;
import org.apache.commons.math4.linear.RealMatrix;
import org.apache.commons.math4.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math4.stat.regression.MillerUpdatingRegression;
import org.apache.commons.math4.stat.regression.OLSMultipleLinearRegression;
import org.apache.commons.math4.stat.regression.RegressionResults;
import org.apache.commons.math4.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * MillerUpdatingRegression tests.
 */
public class MillerUpdatingRegressionTest {

    public MillerUpdatingRegressionTest() {
    }
    /* This is the Greene Airline Cost data.
     * The data can be downloaded from http://www.indiana.edu/~statmath/stat/all/panel/airline.csv
     */
    private final static double[][] airdata = {
        /*"I",*/new double[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6},
        /*"T",*/ new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
        /*"C",*/ new double[]{1140640, 1215690, 1309570, 1511530, 1676730, 1823740, 2022890, 2314760, 2639160, 3247620, 3787750, 3867750, 3996020, 4282880, 4748320, 569292, 640614, 777655, 999294, 1203970, 1358100, 1501350, 1709270, 2025400, 2548370, 3137740, 3557700, 3717740, 3962370, 4209390, 286298, 309290, 342056, 374595, 450037, 510412, 575347, 669331, 783799, 913883, 1041520, 1125800, 1096070, 1198930, 1170470, 145167, 170192, 247506, 309391, 354338, 373941, 420915, 474017, 532590, 676771, 880438, 1052020, 1193680, 1303390, 1436970, 91361, 95428, 98187, 115967, 138382, 156228, 183169, 210212, 274024, 356915, 432344, 524294, 530924, 581447, 610257, 68978, 74904, 83829, 98148, 118449, 133161, 145062, 170711, 199775, 276797, 381478, 506969, 633388, 804388, 1009500},
        /*"Q",*/ new double[]{0.952757, 0.986757, 1.09198, 1.17578, 1.16017, 1.17376, 1.29051, 1.39067, 1.61273, 1.82544, 1.54604, 1.5279, 1.6602, 1.82231, 1.93646, 0.520635, 0.534627, 0.655192, 0.791575, 0.842945, 0.852892, 0.922843, 1, 1.19845, 1.34067, 1.32624, 1.24852, 1.25432, 1.37177, 1.38974, 0.262424, 0.266433, 0.306043, 0.325586, 0.345706, 0.367517, 0.409937, 0.448023, 0.539595, 0.539382, 0.467967, 0.450544, 0.468793, 0.494397, 0.493317, 0.086393, 0.09674, 0.1415, 0.169715, 0.173805, 0.164272, 0.170906, 0.17784, 0.192248, 0.242469, 0.256505, 0.249657, 0.273923, 0.371131, 0.421411, 0.051028, 0.052646, 0.056348, 0.066953, 0.070308, 0.073961, 0.084946, 0.095474, 0.119814, 0.150046, 0.144014, 0.1693, 0.172761, 0.18667, 0.213279, 0.037682, 0.039784, 0.044331, 0.050245, 0.055046, 0.052462, 0.056977, 0.06149, 0.069027, 0.092749, 0.11264, 0.154154, 0.186461, 0.246847, 0.304013},
        /*"PF",*/ new double[]{106650, 110307, 110574, 121974, 196606, 265609, 263451, 316411, 384110, 569251, 871636, 997239, 938002, 859572, 823411, 103795, 111477, 118664, 114797, 215322, 281704, 304818, 348609, 374579, 544109, 853356, 1003200, 941977, 856533, 821361, 118788, 123798, 122882, 131274, 222037, 278721, 306564, 356073, 378311, 555267, 850322, 1015610, 954508, 886999, 844079, 114987, 120501, 121908, 127220, 209405, 263148, 316724, 363598, 389436, 547376, 850418, 1011170, 951934, 881323, 831374, 118222, 116223, 115853, 129372, 243266, 277930, 317273, 358794, 397667, 566672, 848393, 1005740, 958231, 872924, 844622, 117112, 119420, 116087, 122997, 194309, 307923, 323595, 363081, 386422, 564867, 874818, 1013170, 930477, 851676, 819476},
        /*"LF",*/ new double[]{0.534487, 0.532328, 0.547736, 0.540846, 0.591167, 0.575417, 0.594495, 0.597409, 0.638522, 0.676287, 0.605735, 0.61436, 0.633366, 0.650117, 0.625603, 0.490851, 0.473449, 0.503013, 0.512501, 0.566782, 0.558133, 0.558799, 0.57207, 0.624763, 0.628706, 0.58915, 0.532612, 0.526652, 0.540163, 0.528775, 0.524334, 0.537185, 0.582119, 0.579489, 0.606592, 0.60727, 0.582425, 0.573972, 0.654256, 0.631055, 0.56924, 0.589682, 0.587953, 0.565388, 0.577078, 0.432066, 0.439669, 0.488932, 0.484181, 0.529925, 0.532723, 0.549067, 0.55714, 0.611377, 0.645319, 0.611734, 0.580884, 0.572047, 0.59457, 0.585525, 0.442875, 0.462473, 0.519118, 0.529331, 0.557797, 0.556181, 0.569327, 0.583465, 0.631818, 0.604723, 0.587921, 0.616159, 0.605868, 0.594688, 0.635545, 0.448539, 0.475889, 0.500562, 0.500344, 0.528897, 0.495361, 0.510342, 0.518296, 0.546723, 0.554276, 0.517766, 0.580049, 0.556024, 0.537791, 0.525775}
    };

    /**
     * Test of hasIntercept method, of class MillerUpdatingRegression.
     */
    @Test
    public void testHasIntercept() {
        MillerUpdatingRegression instance = new MillerUpdatingRegression(10, false);
        if (instance.hasIntercept()) {
            Assert.fail("Should not have intercept");
        }
        instance = new MillerUpdatingRegression(10, true);
        if (!instance.hasIntercept()) {
            Assert.fail("Should have intercept");
        }
    }

    /**
     * Test of getN method, of class MillerUpdatingRegression.
     */
    @Test
    public void testAddObsGetNClear() {
        MillerUpdatingRegression instance = new MillerUpdatingRegression(3, true);
        double[][] xAll = new double[airdata[0].length][];
        double[] y = new double[airdata[0].length];
        for (int i = 0; i < airdata[0].length; i++) {
            xAll[i] = new double[3];
            xAll[i][0] = FastMath.log(airdata[3][i]);
            xAll[i][1] = FastMath.log(airdata[4][i]);
            xAll[i][2] = airdata[5][i];
            y[i] = FastMath.log(airdata[2][i]);
        }
        instance.addObservations(xAll, y);
        if (instance.getN() != xAll.length) {
            Assert.fail("Number of observations not correct in bulk addition");
        }
        instance.clear();
        for (int i = 0; i < xAll.length; i++) {
            instance.addObservation(xAll[i], y[i]);
        }
        if (instance.getN() != xAll.length) {
            Assert.fail("Number of observations not correct in drip addition");
        }
        return;
    }

    @Test
    public void testNegativeTestAddObs() {
        MillerUpdatingRegression instance = new MillerUpdatingRegression(3, true);
        try {
            instance.addObservation(new double[]{1.0}, 0.0);
            Assert.fail("Should throw MathIllegalArgumentException");
        } catch (MathIllegalArgumentException iae) {
        } catch (Exception e) {
            Assert.fail("Should throw MathIllegalArgumentException");
        }
        try {
            instance.addObservation(new double[]{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0}, 0.0);
            Assert.fail("Should throw MathIllegalArgumentException");
        } catch (MathIllegalArgumentException iae) {
        } catch (Exception e) {
            Assert.fail("Should throw MathIllegalArgumentException");
        }
        try {
            instance.addObservation(new double[]{1.0, 1.0, 1.0}, 0.0);
        } catch (Exception e) {
            Assert.fail("Should throw MathIllegalArgumentException");
        }

        //now we try it without an intercept
        instance = new MillerUpdatingRegression(3, false);
        try {
            instance.addObservation(new double[]{1.0}, 0.0);
            Assert.fail("Should throw MathIllegalArgumentException [NOINTERCEPT]");
        } catch (MathIllegalArgumentException iae) {
        } catch (Exception e) {
            Assert.fail("Should throw MathIllegalArgumentException [NOINTERCEPT]");
        }
        try {
            instance.addObservation(new double[]{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0}, 0.0);
            Assert.fail("Should throw MathIllegalArgumentException [NOINTERCEPT]");
        } catch (MathIllegalArgumentException iae) {
        } catch (Exception e) {
            Assert.fail("Should throw MathIllegalArgumentException [NOINTERCEPT]");
        }
        try {
            instance.addObservation(new double[]{1.0, 1.0, 1.0}, 0.0);
        } catch (Exception e) {
            Assert.fail("Should throw MathIllegalArgumentException [NOINTERCEPT]");
        }
    }

    @Test
    public void testNegativeTestAddMultipleObs() {
        MillerUpdatingRegression instance = new MillerUpdatingRegression(3, true);
        try {
            double[][] tst = {{1.0, 1.0, 1.0}, {1.20, 2.0, 2.1}};
            double[] y = {1.0};
            instance.addObservations(tst, y);

            Assert.fail("Should throw MathIllegalArgumentException");
        } catch (MathIllegalArgumentException iae) {
        } catch (Exception e) {
            Assert.fail("Should throw MathIllegalArgumentException");
        }

        try {
            double[][] tst = {{1.0, 1.0, 1.0}, {1.20, 2.0, 2.1}};
            double[] y = {1.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};
            instance.addObservations(tst, y);

            Assert.fail("Should throw MathIllegalArgumentException");
        } catch (MathIllegalArgumentException iae) {
        } catch (Exception e) {
            Assert.fail("Should throw MathIllegalArgumentException");
        }
    }

    /* Results can be found at http://www.indiana.edu/~statmath/stat/all/panel/panel4.html
     * This test concerns a known data set
     */
    @Test
    public void testRegressAirlineConstantExternal() {
        MillerUpdatingRegression instance = new MillerUpdatingRegression(4, false);
        double[][] x = new double[airdata[0].length][];
        double[] y = new double[airdata[0].length];
        for (int i = 0; i < airdata[0].length; i++) {
            x[i] = new double[4];
            x[i][0] = 1.0;
            x[i][1] = FastMath.log(airdata[3][i]);
            x[i][2] = FastMath.log(airdata[4][i]);
            x[i][3] = airdata[5][i];
            y[i] = FastMath.log(airdata[2][i]);
        }

        instance.addObservations(x, y);
        try {
            RegressionResults result = instance.regress();
            Assert.assertNotNull("The test case is a prototype.", result);
            TestUtils.assertEquals(
                    new double[]{9.5169, 0.8827, 0.4540, -1.6275},
                    result.getParameterEstimates(), 1e-4);


            TestUtils.assertEquals(
                    new double[]{.2292445, .0132545, .0203042, .345302},
                    result.getStdErrorOfEstimates(), 1.0e-4);

            TestUtils.assertEquals(0.01552839, result.getMeanSquareError(), 1.0e-8);
        } catch (Exception e) {
            Assert.fail("Should not throw exception but does");
        }
    }

    @Test
    public void testRegressAirlineConstantInternal() {
        MillerUpdatingRegression instance = new MillerUpdatingRegression(3, true);
        double[][] x = new double[airdata[0].length][];
        double[] y = new double[airdata[0].length];
        for (int i = 0; i < airdata[0].length; i++) {
            x[i] = new double[3];
            x[i][0] = FastMath.log(airdata[3][i]);
            x[i][1] = FastMath.log(airdata[4][i]);
            x[i][2] = airdata[5][i];
            y[i] = FastMath.log(airdata[2][i]);
        }

        instance.addObservations(x, y);
        try {
            RegressionResults result = instance.regress();
            Assert.assertNotNull("The test case is a prototype.", result);
            TestUtils.assertEquals(
                    new double[]{9.5169, 0.8827, 0.4540, -1.6275},
                    result.getParameterEstimates(), 1e-4);


            TestUtils.assertEquals(
                    new double[]{.2292445, .0132545, .0203042, .345302},
                    result.getStdErrorOfEstimates(), 1.0e-4);

            TestUtils.assertEquals(0.9883, result.getRSquared(), 1.0e-4);
            TestUtils.assertEquals(0.01552839, result.getMeanSquareError(), 1.0e-8);
        } catch (Exception e) {
            Assert.fail("Should not throw exception but does");
        }
    }

    @Test
    public void testFilippelli() {
        double[] data = new double[]{
            0.8116, -6.860120914,
            0.9072, -4.324130045,
            0.9052, -4.358625055,
            0.9039, -4.358426747,
            0.8053, -6.955852379,
            0.8377, -6.661145254,
            0.8667, -6.355462942,
            0.8809, -6.118102026,
            0.7975, -7.115148017,
            0.8162, -6.815308569,
            0.8515, -6.519993057,
            0.8766, -6.204119983,
            0.8885, -5.853871964,
            0.8859, -6.109523091,
            0.8959, -5.79832982,
            0.8913, -5.482672118,
            0.8959, -5.171791386,
            0.8971, -4.851705903,
            0.9021, -4.517126416,
            0.909, -4.143573228,
            0.9139, -3.709075441,
            0.9199, -3.499489089,
            0.8692, -6.300769497,
            0.8872, -5.953504836,
            0.89, -5.642065153,
            0.891, -5.031376979,
            0.8977, -4.680685696,
            0.9035, -4.329846955,
            0.9078, -3.928486195,
            0.7675, -8.56735134,
            0.7705, -8.363211311,
            0.7713, -8.107682739,
            0.7736, -7.823908741,
            0.7775, -7.522878745,
            0.7841, -7.218819279,
            0.7971, -6.920818754,
            0.8329, -6.628932138,
            0.8641, -6.323946875,
            0.8804, -5.991399828,
            0.7668, -8.781464495,
            0.7633, -8.663140179,
            0.7678, -8.473531488,
            0.7697, -8.247337057,
            0.77, -7.971428747,
            0.7749, -7.676129393,
            0.7796, -7.352812702,
            0.7897, -7.072065318,
            0.8131, -6.774174009,
            0.8498, -6.478861916,
            0.8741, -6.159517513,
            0.8061, -6.835647144,
            0.846, -6.53165267,
            0.8751, -6.224098421,
            0.8856, -5.910094889,
            0.8919, -5.598599459,
            0.8934, -5.290645224,
            0.894, -4.974284616,
            0.8957, -4.64454848,
            0.9047, -4.290560426,
            0.9129, -3.885055584,
            0.9209, -3.408378962,
            0.9219, -3.13200249,
            0.7739, -8.726767166,
            0.7681, -8.66695597,
            0.7665, -8.511026475,
            0.7703, -8.165388579,
            0.7702, -7.886056648,
            0.7761, -7.588043762,
            0.7809, -7.283412422,
            0.7961, -6.995678626,
            0.8253, -6.691862621,
            0.8602, -6.392544977,
            0.8809, -6.067374056,
            0.8301, -6.684029655,
            0.8664, -6.378719832,
            0.8834, -6.065855188,
            0.8898, -5.752272167,
            0.8964, -5.132414673,
            0.8963, -4.811352704,
            0.9074, -4.098269308,
            0.9119, -3.66174277,
            0.9228, -3.2644011
        };
        MillerUpdatingRegression model = new MillerUpdatingRegression(10, true);
        int off = 0;
        double[] tmp = new double[10];
        int nobs = 82;
        for (int i = 0; i < nobs; i++) {
            tmp[0] = data[off + 1];
//            tmp[1] = tmp[0] * tmp[0];
//            tmp[2] = tmp[0] * tmp[1]; //^3
//            tmp[3] = tmp[1] * tmp[1]; //^4
//            tmp[4] = tmp[2] * tmp[1]; //^5
//            tmp[5] = tmp[2] * tmp[2]; //^6
//            tmp[6] = tmp[2] * tmp[3]; //^7
//            tmp[7] = tmp[3] * tmp[3]; //^8
//            tmp[8] = tmp[4] * tmp[3]; //^9
//            tmp[9] = tmp[4] * tmp[4]; //^10
            tmp[1] = tmp[0] * tmp[0];
            tmp[2] = tmp[0] * tmp[1];
            tmp[3] = tmp[0] * tmp[2];
            tmp[4] = tmp[0] * tmp[3];
            tmp[5] = tmp[0] * tmp[4];
            tmp[6] = tmp[0] * tmp[5];
            tmp[7] = tmp[0] * tmp[6];
            tmp[8] = tmp[0] * tmp[7];
            tmp[9] = tmp[0] * tmp[8];
            model.addObservation(tmp, data[off]);
            off += 2;
        }
        RegressionResults result = model.regress();
        double[] betaHat = result.getParameterEstimates();
        TestUtils.assertEquals(betaHat,
                new double[]{
                    -1467.48961422980,
                    -2772.17959193342,
                    -2316.37108160893,
                    -1127.97394098372,
                    -354.478233703349,
                    -75.1242017393757,
                    -10.8753180355343,
                    -1.06221498588947,
                    -0.670191154593408E-01,
                    -0.246781078275479E-02,
                    -0.402962525080404E-04
                }, 1E-5); //
//
        double[] se = result.getStdErrorOfEstimates();
        TestUtils.assertEquals(se,
                new double[]{
                    298.084530995537,
                    559.779865474950,
                    466.477572127796,
                    227.204274477751,
                    71.6478660875927,
                    15.2897178747400,
                    2.23691159816033,
                    0.221624321934227,
                    0.142363763154724E-01,
                    0.535617408889821E-03,
                    0.896632837373868E-05
                }, 1E-5); //

        TestUtils.assertEquals(0.996727416185620, result.getRSquared(), 1.0e-8);
        TestUtils.assertEquals(0.112091743968020E-04, result.getMeanSquareError(), 1.0e-10);
        TestUtils.assertEquals(0.795851382172941E-03, result.getErrorSumSquares(), 1.0e-10);

    }

    @Test
    public void testWampler1() {
        double[] data = new double[]{
            1, 0,
            6, 1,
            63, 2,
            364, 3,
            1365, 4,
            3906, 5,
            9331, 6,
            19608, 7,
            37449, 8,
            66430, 9,
            111111, 10,
            177156, 11,
            271453, 12,
            402234, 13,
            579195, 14,
            813616, 15,
            1118481, 16,
            1508598, 17,
            2000719, 18,
            2613660, 19,
            3368421, 20};

        MillerUpdatingRegression model = new MillerUpdatingRegression(5, true);
        int off = 0;
        double[] tmp = new double[5];
        int nobs = 21;
        for (int i = 0; i < nobs; i++) {
            tmp[0] = data[off + 1];
            tmp[1] = tmp[0] * tmp[0];
            tmp[2] = tmp[0] * tmp[1];
            tmp[3] = tmp[0] * tmp[2];
            tmp[4] = tmp[0] * tmp[3];
            model.addObservation(tmp, data[off]);
            off += 2;
        }
        RegressionResults result = model.regress();
        double[] betaHat = result.getParameterEstimates();
        TestUtils.assertEquals(betaHat,
                new double[]{1.0,
                    1.0, 1.0,
                    1.0, 1.0,
                    1.0}, 1E-8); //
//
        double[] se = result.getStdErrorOfEstimates();
        TestUtils.assertEquals(se,
                new double[]{0.0,
                    0.0, 0.0,
                    0.0, 0.0,
                    0.0}, 1E-8); //

        TestUtils.assertEquals(1.0, result.getRSquared(), 1.0e-10);
        TestUtils.assertEquals(0, result.getMeanSquareError(), 1.0e-7);
        TestUtils.assertEquals(0.00, result.getErrorSumSquares(), 1.0e-6);

        return;
    }

    @Test
    public void testWampler2() {
        double[] data = new double[]{
            1.00000, 0,
            1.11111, 1,
            1.24992, 2,
            1.42753, 3,
            1.65984, 4,
            1.96875, 5,
            2.38336, 6,
            2.94117, 7,
            3.68928, 8,
            4.68559, 9,
            6.00000, 10,
            7.71561, 11,
            9.92992, 12,
            12.75603, 13,
            16.32384, 14,
            20.78125, 15,
            26.29536, 16,
            33.05367, 17,
            41.26528, 18,
            51.16209, 19,
            63.00000, 20};

        MillerUpdatingRegression model = new MillerUpdatingRegression(5, true);
        int off = 0;
        double[] tmp = new double[5];
        int nobs = 21;
        for (int i = 0; i < nobs; i++) {
            tmp[0] = data[off + 1];
            tmp[1] = tmp[0] * tmp[0];
            tmp[2] = tmp[0] * tmp[1];
            tmp[3] = tmp[0] * tmp[2];
            tmp[4] = tmp[0] * tmp[3];
            model.addObservation(tmp, data[off]);
            off += 2;
        }
        RegressionResults result = model.regress();
        double[] betaHat = result.getParameterEstimates();
        TestUtils.assertEquals(betaHat,
                new double[]{1.0,
                    1.0e-1, 1.0e-2,
                    1.0e-3, 1.0e-4,
                    1.0e-5}, 1E-8); //
//
        double[] se = result.getStdErrorOfEstimates();
        TestUtils.assertEquals(se,
                new double[]{0.0,
                    0.0, 0.0,
                    0.0, 0.0,
                    0.0}, 1E-8); //

        TestUtils.assertEquals(1.0, result.getRSquared(), 1.0e-10);
        TestUtils.assertEquals(0, result.getMeanSquareError(), 1.0e-7);
        TestUtils.assertEquals(0.00, result.getErrorSumSquares(), 1.0e-6);
        return;
    }

    @Test
    public void testWampler3() {
        double[] data = new double[]{
            760, 0,
            -2042, 1,
            2111, 2,
            -1684, 3,
            3888, 4,
            1858, 5,
            11379, 6,
            17560, 7,
            39287, 8,
            64382, 9,
            113159, 10,
            175108, 11,
            273291, 12,
            400186, 13,
            581243, 14,
            811568, 15,
            1121004, 16,
            1506550, 17,
            2002767, 18,
            2611612, 19,
            3369180, 20};
        MillerUpdatingRegression model = new MillerUpdatingRegression(5, true);
        int off = 0;
        double[] tmp = new double[5];
        int nobs = 21;
        for (int i = 0; i < nobs; i++) {
            tmp[0] = data[off + 1];
            tmp[1] = tmp[0] * tmp[0];
            tmp[2] = tmp[0] * tmp[1];
            tmp[3] = tmp[0] * tmp[2];
            tmp[4] = tmp[0] * tmp[3];
            model.addObservation(tmp, data[off]);
            off += 2;
        }
        RegressionResults result = model.regress();
        double[] betaHat = result.getParameterEstimates();
        TestUtils.assertEquals(betaHat,
                new double[]{1.0,
                    1.0, 1.0,
                    1.0, 1.0,
                    1.0}, 1E-8); //
        double[] se = result.getStdErrorOfEstimates();
        TestUtils.assertEquals(se,
                new double[]{2152.32624678170,
                    2363.55173469681, 779.343524331583,
                    101.475507550350, 5.64566512170752,
                    0.112324854679312}, 1E-8); //

        TestUtils.assertEquals(.999995559025820, result.getRSquared(), 1.0e-10);
        TestUtils.assertEquals(5570284.53333333, result.getMeanSquareError(), 1.0e-7);
        TestUtils.assertEquals(83554268.0000000, result.getErrorSumSquares(), 1.0e-6);
        return;
    }

    //@Test
    public void testWampler4() {
        double[] data = new double[]{
            75901, 0,
            -204794, 1,
            204863, 2,
            -204436, 3,
            253665, 4,
            -200894, 5,
            214131, 6,
            -185192, 7,
            221249, 8,
            -138370, 9,
            315911, 10,
            -27644, 11,
            455253, 12,
            197434, 13,
            783995, 14,
            608816, 15,
            1370781, 16,
            1303798, 17,
            2205519, 18,
            2408860, 19,
            3444321, 20};
        MillerUpdatingRegression model = new MillerUpdatingRegression(5, true);
        int off = 0;
        double[] tmp = new double[5];
        int nobs = 21;
        for (int i = 0; i < nobs; i++) {
            tmp[0] = data[off + 1];
            tmp[1] = tmp[0] * tmp[0];
            tmp[2] = tmp[0] * tmp[1];
            tmp[3] = tmp[0] * tmp[2];
            tmp[4] = tmp[0] * tmp[3];
            model.addObservation(tmp, data[off]);
            off += 2;
        }
        RegressionResults result = model.regress();
        double[] betaHat = result.getParameterEstimates();
        TestUtils.assertEquals(betaHat,
                new double[]{1.0,
                    1.0, 1.0,
                    1.0, 1.0,
                    1.0}, 1E-8); //
//
        double[] se = result.getStdErrorOfEstimates();
        TestUtils.assertEquals(se,
                new double[]{215232.624678170,
                    236355.173469681, 77934.3524331583,
                    10147.5507550350, 564.566512170752,
                    11.2324854679312}, 1E-8); //

        TestUtils.assertEquals(.957478440825662, result.getRSquared(), 1.0e-10);
        TestUtils.assertEquals(55702845333.3333, result.getMeanSquareError(), 1.0e-4);
        TestUtils.assertEquals(835542680000.000, result.getErrorSumSquares(), 1.0e-3);

        return;
    }

    /**
     * Test Longley dataset against certified values provided by NIST.
     * Data Source: J. Longley (1967) "An Appraisal of Least Squares
     * Programs for the Electronic Computer from the Point of View of the User"
     * Journal of the American Statistical Association, vol. 62. September,
     * pp. 819-841.
     *
     * Certified values (and data) are from NIST:
     * http://www.itl.nist.gov/div898/strd/lls/data/LINKS/DATA/Longley.dat
     */
    @Test
    public void testLongly() {
        // Y values are first, then independent vars
        // Each row is one observation
        double[] design = new double[]{
            60323, 83.0, 234289, 2356, 1590, 107608, 1947,
            61122, 88.5, 259426, 2325, 1456, 108632, 1948,
            60171, 88.2, 258054, 3682, 1616, 109773, 1949,
            61187, 89.5, 284599, 3351, 1650, 110929, 1950,
            63221, 96.2, 328975, 2099, 3099, 112075, 1951,
            63639, 98.1, 346999, 1932, 3594, 113270, 1952,
            64989, 99.0, 365385, 1870, 3547, 115094, 1953,
            63761, 100.0, 363112, 3578, 3350, 116219, 1954,
            66019, 101.2, 397469, 2904, 3048, 117388, 1955,
            67857, 104.6, 419180, 2822, 2857, 118734, 1956,
            68169, 108.4, 442769, 2936, 2798, 120445, 1957,
            66513, 110.8, 444546, 4681, 2637, 121950, 1958,
            68655, 112.6, 482704, 3813, 2552, 123366, 1959,
            69564, 114.2, 502601, 3931, 2514, 125368, 1960,
            69331, 115.7, 518173, 4806, 2572, 127852, 1961,
            70551, 116.9, 554894, 4007, 2827, 130081, 1962
        };

        final int nobs = 16;
        final int nvars = 6;

        // Estimate the model
        MillerUpdatingRegression model = new MillerUpdatingRegression(6, true);
        int off = 0;
        double[] tmp = new double[6];
        for (int i = 0; i < nobs; i++) {
            System.arraycopy(design, off + 1, tmp, 0, nvars);
            model.addObservation(tmp, design[off]);
            off += nvars + 1;
        }

        // Check expected beta values from NIST
        RegressionResults result = model.regress();
        double[] betaHat = result.getParameterEstimates();
        TestUtils.assertEquals(betaHat,
                new double[]{-3482258.63459582, 15.0618722713733,
                    -0.358191792925910E-01, -2.02022980381683,
                    -1.03322686717359, -0.511041056535807E-01,
                    1829.15146461355}, 1E-8); //

        // Check standard errors from NIST
        double[] errors = result.getStdErrorOfEstimates();
        TestUtils.assertEquals(new double[]{890420.383607373,
                    84.9149257747669,
                    0.334910077722432E-01,
                    0.488399681651699,
                    0.214274163161675,
                    0.226073200069370,
                    455.478499142212}, errors, 1E-6);
//
        // Check R-Square statistics against R
        TestUtils.assertEquals(0.995479004577296, result.getRSquared(), 1E-12);
        TestUtils.assertEquals(0.992465007628826, result.getAdjustedRSquared(), 1E-12);
//
//
//        // Estimate model without intercept
        model = new MillerUpdatingRegression(6, false);
        off = 0;
        for (int i = 0; i < nobs; i++) {
            System.arraycopy(design, off + 1, tmp, 0, nvars);
            model.addObservation(tmp, design[off]);
            off += nvars + 1;
        }
        // Check expected beta values from R
        result = model.regress();
        betaHat = result.getParameterEstimates();
        TestUtils.assertEquals(betaHat,
                new double[]{-52.99357013868291, 0.07107319907358,
                    -0.42346585566399, -0.57256866841929,
                    -0.41420358884978, 48.41786562001326}, 1E-11);
//
        // Check standard errors from R
        errors = result.getStdErrorOfEstimates();
        TestUtils.assertEquals(new double[]{129.54486693117232, 0.03016640003786,
                    0.41773654056612, 0.27899087467676, 0.32128496193363,
                    17.68948737819961}, errors, 1E-11);
//

//        // Check R-Square statistics against R
        TestUtils.assertEquals(0.9999670130706, result.getRSquared(), 1E-12);
        TestUtils.assertEquals(0.999947220913, result.getAdjustedRSquared(), 1E-12);

    }

//    @Test
//    public void testRegressReorder() {
//        // System.out.println("testRegressReorder");
//        MillerUpdatingRegression instance = new MillerUpdatingRegression(4, false);
//        double[][] x = new double[airdata[0].length][];
//        double[] y = new double[airdata[0].length];
//        for (int i = 0; i < airdata[0].length; i++) {
//            x[i] = new double[4];
//            x[i][0] = 1.0;
//            x[i][1] = FastMath.log(airdata[3][i]);
//            x[i][2] = FastMath.log(airdata[4][i]);
//            x[i][3] = airdata[5][i];
//            y[i] = FastMath.log(airdata[2][i]);
//        }
//
//        instance.addObservations(x, y);
//        RegressionResults result = instance.regress();
//        if (result == null) {
//            Assert.fail("Null result....");
//        }
//
//        instance.reorderRegressors(new int[]{3, 2}, 0);
//        RegressionResults resultInverse = instance.regress();
//
//        double[] beta = result.getParameterEstimates();
//        double[] betar = resultInverse.getParameterEstimates();
//        if (FastMath.abs(beta[0] - betar[0]) > 1.0e-14) {
//            Assert.fail("Parameters not correct after reorder (0,3)");
//        }
//        if (FastMath.abs(beta[1] - betar[1]) > 1.0e-14) {
//            Assert.fail("Parameters not correct after reorder (1,2)");
//        }
//        if (FastMath.abs(beta[2] - betar[2]) > 1.0e-14) {
//            Assert.fail("Parameters not correct after reorder (2,1)");
//        }
//        if (FastMath.abs(beta[3] - betar[3]) > 1.0e-14) {
//            Assert.fail("Parameters not correct after reorder (3,0)");
//        }
//    }

    @Test
    public void testOneRedundantColumn() {
        MillerUpdatingRegression instance = new MillerUpdatingRegression(4, false);
        MillerUpdatingRegression instance2 = new MillerUpdatingRegression(5, false);
        double[][] x = new double[airdata[0].length][];
        double[][] x2 = new double[airdata[0].length][];
        double[] y = new double[airdata[0].length];
        for (int i = 0; i < airdata[0].length; i++) {
            x[i] = new double[4];
            x2[i] = new double[5];
            x[i][0] = 1.0;
            x[i][1] = FastMath.log(airdata[3][i]);
            x[i][2] = FastMath.log(airdata[4][i]);
            x[i][3] = airdata[5][i];

            x2[i][0] = x[i][0];
            x2[i][1] = x[i][1];
            x2[i][2] = x[i][2];
            x2[i][3] = x[i][3];
            x2[i][4] = x[i][3];

            y[i] = FastMath.log(airdata[2][i]);
        }

        instance.addObservations(x, y);
        RegressionResults result = instance.regress();
        Assert.assertNotNull("Could not estimate initial regression", result);

        instance2.addObservations(x2, y);
        RegressionResults resultRedundant = instance2.regress();
        Assert.assertNotNull("Could not estimate redundant regression", resultRedundant);
        double[] beta = result.getParameterEstimates();
        double[] betar = resultRedundant.getParameterEstimates();
        double[] se = result.getStdErrorOfEstimates();
        double[] ser = resultRedundant.getStdErrorOfEstimates();

        for (int i = 0; i < beta.length; i++) {
            if (FastMath.abs(beta[i] - betar[i]) > 1.0e-8) {
                Assert.fail("Parameters not correctly estimated");
            }
            if (FastMath.abs(se[i] - ser[i]) > 1.0e-8) {
                Assert.fail("Standard errors not correctly estimated");
            }
            for (int j = 0; j < i; j++) {
                if (FastMath.abs(result.getCovarianceOfParameters(i, j)
                        - resultRedundant.getCovarianceOfParameters(i, j)) > 1.0e-8) {
                    Assert.fail("Variance Covariance not correct");
                }
            }
        }


        TestUtils.assertEquals(result.getAdjustedRSquared(), resultRedundant.getAdjustedRSquared(), 1.0e-8);
        TestUtils.assertEquals(result.getErrorSumSquares(), resultRedundant.getErrorSumSquares(), 1.0e-8);
        TestUtils.assertEquals(result.getMeanSquareError(), resultRedundant.getMeanSquareError(), 1.0e-8);
        TestUtils.assertEquals(result.getRSquared(), resultRedundant.getRSquared(), 1.0e-8);
        return;
    }

    @Test
    public void testThreeRedundantColumn() {

        MillerUpdatingRegression instance = new MillerUpdatingRegression(4, false);
        MillerUpdatingRegression instance2 = new MillerUpdatingRegression(7, false);
        double[][] x = new double[airdata[0].length][];
        double[][] x2 = new double[airdata[0].length][];
        double[] y = new double[airdata[0].length];
        for (int i = 0; i < airdata[0].length; i++) {
            x[i] = new double[4];
            x2[i] = new double[7];
            x[i][0] = 1.0;
            x[i][1] = FastMath.log(airdata[3][i]);
            x[i][2] = FastMath.log(airdata[4][i]);
            x[i][3] = airdata[5][i];

            x2[i][0] = x[i][0];
            x2[i][1] = x[i][0];
            x2[i][2] = x[i][1];
            x2[i][3] = x[i][2];
            x2[i][4] = x[i][1];
            x2[i][5] = x[i][3];
            x2[i][6] = x[i][2];

            y[i] = FastMath.log(airdata[2][i]);
        }

        instance.addObservations(x, y);
        RegressionResults result = instance.regress();
        Assert.assertNotNull("Could not estimate initial regression", result);

        instance2.addObservations(x2, y);
        RegressionResults resultRedundant = instance2.regress();
        Assert.assertNotNull("Could not estimate redundant regression", resultRedundant);
        double[] beta = result.getParameterEstimates();
        double[] betar = resultRedundant.getParameterEstimates();
        double[] se = result.getStdErrorOfEstimates();
        double[] ser = resultRedundant.getStdErrorOfEstimates();

        if (FastMath.abs(beta[0] - betar[0]) > 1.0e-8) {
            Assert.fail("Parameters not correct after reorder (0,3)");
        }
        if (FastMath.abs(beta[1] - betar[2]) > 1.0e-8) {
            Assert.fail("Parameters not correct after reorder (1,2)");
        }
        if (FastMath.abs(beta[2] - betar[3]) > 1.0e-8) {
            Assert.fail("Parameters not correct after reorder (2,1)");
        }
        if (FastMath.abs(beta[3] - betar[5]) > 1.0e-8) {
            Assert.fail("Parameters not correct after reorder (3,0)");
        }

        if (FastMath.abs(se[0] - ser[0]) > 1.0e-8) {
            Assert.fail("Se not correct after reorder (0,3)");
        }
        if (FastMath.abs(se[1] - ser[2]) > 1.0e-8) {
            Assert.fail("Se not correct after reorder (1,2)");
        }
        if (FastMath.abs(se[2] - ser[3]) > 1.0e-8) {
            Assert.fail("Se not correct after reorder (2,1)");
        }
        if (FastMath.abs(se[3] - ser[5]) > 1.0e-8) {
            Assert.fail("Se not correct after reorder (3,0)");
        }

        if (FastMath.abs(result.getCovarianceOfParameters(0, 0)
                - resultRedundant.getCovarianceOfParameters(0, 0)) > 1.0e-8) {
            Assert.fail("VCV not correct after reorder (0,0)");
        }
        if (FastMath.abs(result.getCovarianceOfParameters(0, 1)
                - resultRedundant.getCovarianceOfParameters(0, 2)) > 1.0e-8) {
            Assert.fail("VCV not correct after reorder (0,1)<->(0,2)");
        }
        if (FastMath.abs(result.getCovarianceOfParameters(0, 2)
                - resultRedundant.getCovarianceOfParameters(0, 3)) > 1.0e-8) {
            Assert.fail("VCV not correct after reorder (0,2)<->(0,1)");
        }
        if (FastMath.abs(result.getCovarianceOfParameters(0, 3)
                - resultRedundant.getCovarianceOfParameters(0, 5)) > 1.0e-8) {
            Assert.fail("VCV not correct after reorder (0,3)<->(0,3)");
        }
        if (FastMath.abs(result.getCovarianceOfParameters(1, 0)
                - resultRedundant.getCovarianceOfParameters(2, 0)) > 1.0e-8) {
            Assert.fail("VCV not correct after reorder (1,0)<->(2,0)");
        }
        if (FastMath.abs(result.getCovarianceOfParameters(1, 1)
                - resultRedundant.getCovarianceOfParameters(2, 2)) > 1.0e-8) {
            Assert.fail("VCV not correct  (1,1)<->(2,1)");
        }
        if (FastMath.abs(result.getCovarianceOfParameters(1, 2)
                - resultRedundant.getCovarianceOfParameters(2, 3)) > 1.0e-8) {
            Assert.fail("VCV not correct  (1,2)<->(2,2)");
        }

        if (FastMath.abs(result.getCovarianceOfParameters(2, 0)
                - resultRedundant.getCovarianceOfParameters(3, 0)) > 1.0e-8) {
            Assert.fail("VCV not correct  (2,0)<->(1,0)");
        }
        if (FastMath.abs(result.getCovarianceOfParameters(2, 1)
                - resultRedundant.getCovarianceOfParameters(3, 2)) > 1.0e-8) {
            Assert.fail("VCV not correct  (2,1)<->(1,2)");
        }

        if (FastMath.abs(result.getCovarianceOfParameters(3, 3)
                - resultRedundant.getCovarianceOfParameters(5, 5)) > 1.0e-8) {
            Assert.fail("VCV not correct  (3,3)<->(3,2)");
        }

        TestUtils.assertEquals(result.getAdjustedRSquared(), resultRedundant.getAdjustedRSquared(), 1.0e-8);
        TestUtils.assertEquals(result.getErrorSumSquares(), resultRedundant.getErrorSumSquares(), 1.0e-8);
        TestUtils.assertEquals(result.getMeanSquareError(), resultRedundant.getMeanSquareError(), 1.0e-8);
        TestUtils.assertEquals(result.getRSquared(), resultRedundant.getRSquared(), 1.0e-8);
        return;
    }

    @Test
    public void testPCorr() {
        MillerUpdatingRegression instance = new MillerUpdatingRegression(4, false);
        double[][] x = new double[airdata[0].length][];
        double[] y = new double[airdata[0].length];
        double[] cp = new double[10];
        double[] yxcorr = new double[4];
        double[] diag = new double[4];
        double sumysq = 0.0;
        int off = 0;
        for (int i = 0; i < airdata[0].length; i++) {
            x[i] = new double[4];
            x[i][0] = 1.0;
            x[i][1] = FastMath.log(airdata[3][i]);
            x[i][2] = FastMath.log(airdata[4][i]);
            x[i][3] = airdata[5][i];
            y[i] = FastMath.log(airdata[2][i]);
            off = 0;
            for (int j = 0; j < 4; j++) {
                double tmp = x[i][j];
                for (int k = 0; k <= j; k++, off++) {
                    cp[off] += tmp * x[i][k];
                }
                yxcorr[j] += tmp * y[i];
            }
            sumysq += y[i] * y[i];
        }
        PearsonsCorrelation pearson = new PearsonsCorrelation(x);
        RealMatrix corr = pearson.getCorrelationMatrix();
        off = 0;
        for (int i = 0; i < 4; i++, off += (i + 1)) {
            diag[i] = FastMath.sqrt(cp[off]);
        }

        instance.addObservations(x, y);
        double[] pc = instance.getPartialCorrelations(0);
        int idx = 0;
        off = 0;
        int off2 = 6;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < i; j++) {
                if (FastMath.abs(pc[idx] - cp[off] / (diag[i] * diag[j])) > 1.0e-8) {
                    Assert.fail("Failed cross products... i = " + i + " j = " + j);
                }
                ++idx;
                ++off;
            }
            ++off;
            if (FastMath.abs(pc[i+off2] - yxcorr[ i] / (FastMath.sqrt(sumysq) * diag[i])) > 1.0e-8) {
                Assert.fail("Assert.failed cross product i = " + i + " y");
            }
        }
        double[] pc2 = instance.getPartialCorrelations(1);

        idx = 0;

        for (int i = 1; i < 4; i++) {
            for (int j = 1; j < i; j++) {
                if (FastMath.abs(pc2[idx] - corr.getEntry(j, i)) > 1.0e-8) {
                    Assert.fail("Failed cross products... i = " + i + " j = " + j);
                }
                ++idx;
            }
        }
        double[] pc3 = instance.getPartialCorrelations(2);
        if (pc3 == null) {
            Assert.fail("Should not be null");
        }
        return;
    }

    @Test
    public void testHdiag() {
        MillerUpdatingRegression instance = new MillerUpdatingRegression(4, false);
        double[][] x = new double[airdata[0].length][];
        double[] y = new double[airdata[0].length];
        for (int i = 0; i < airdata[0].length; i++) {
            x[i] = new double[4];
            x[i][0] = 1.0;
            x[i][1] = FastMath.log(airdata[3][i]);
            x[i][2] = FastMath.log(airdata[4][i]);
            x[i][3] = airdata[5][i];
            y[i] = FastMath.log(airdata[2][i]);
        }
        instance.addObservations(x, y);
        OLSMultipleLinearRegression ols = new OLSMultipleLinearRegression();
        ols.setNoIntercept(true);
        ols.newSampleData(y, x);

        RealMatrix rm = ols.calculateHat();
        for (int i = 0; i < x.length; i++) {
            TestUtils.assertEquals(instance.getDiagonalOfHatMatrix(x[i]), rm.getEntry(i, i), 1.0e-8);
        }
        return;
    }
    @Test
    public void testHdiagConstant() {
        MillerUpdatingRegression instance = new MillerUpdatingRegression(3, true);
        double[][] x = new double[airdata[0].length][];
        double[] y = new double[airdata[0].length];
        for (int i = 0; i < airdata[0].length; i++) {
            x[i] = new double[3];
            x[i][0] = FastMath.log(airdata[3][i]);
            x[i][1] = FastMath.log(airdata[4][i]);
            x[i][2] = airdata[5][i];
            y[i] = FastMath.log(airdata[2][i]);
        }
        instance.addObservations(x, y);
        OLSMultipleLinearRegression ols = new OLSMultipleLinearRegression();
        ols.setNoIntercept(false);
        ols.newSampleData(y, x);

        RealMatrix rm = ols.calculateHat();
        for (int i = 0; i < x.length; i++) {
            TestUtils.assertEquals(instance.getDiagonalOfHatMatrix(x[i]), rm.getEntry(i, i), 1.0e-8);
        }
        return;
    }


    @Test
    public void testSubsetRegression() {

        MillerUpdatingRegression instance = new MillerUpdatingRegression(3, true);
        MillerUpdatingRegression redRegression = new MillerUpdatingRegression(2, true);
        double[][] x = new double[airdata[0].length][];
        double[][] xReduced = new double[airdata[0].length][];
        double[] y = new double[airdata[0].length];
        for (int i = 0; i < airdata[0].length; i++) {
            x[i] = new double[3];
            x[i][0] = FastMath.log(airdata[3][i]);
            x[i][1] = FastMath.log(airdata[4][i]);
            x[i][2] = airdata[5][i];

            xReduced[i] = new double[2];
            xReduced[i][0] = FastMath.log(airdata[3][i]);
            xReduced[i][1] = FastMath.log(airdata[4][i]);

            y[i] = FastMath.log(airdata[2][i]);
        }

        instance.addObservations(x, y);
        redRegression.addObservations(xReduced, y);

        RegressionResults resultsInstance = instance.regress( new int[]{0,1,2} );
        RegressionResults resultsReduced = redRegression.regress();

        TestUtils.assertEquals(resultsInstance.getParameterEstimates(), resultsReduced.getParameterEstimates(), 1.0e-12);
        TestUtils.assertEquals(resultsInstance.getStdErrorOfEstimates(), resultsReduced.getStdErrorOfEstimates(), 1.0e-12);
    }


}
