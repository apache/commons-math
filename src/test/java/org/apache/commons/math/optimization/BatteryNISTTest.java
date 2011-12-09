/*
 * Copyright 2011 The Apache Software Foundation.
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
package org.apache.commons.math.optimization;

import java.util.Arrays;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.TestUtils;
import org.apache.commons.math.analysis.DifferentiableMultivariateFunction;
import org.apache.commons.math.analysis.MultivariateFunction;
import org.apache.commons.math.analysis.MultivariateVectorFunction;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.optimization.direct.BOBYQAOptimizer;
import org.apache.commons.math.optimization.direct.PowellOptimizer;
import org.apache.commons.math.optimization.general.AbstractScalarDifferentiableOptimizer;
import org.apache.commons.math.optimization.general.ConjugateGradientFormula;
import org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizer;
import org.apache.commons.math.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * an ever growing set of tests from NIST
 * http://www.itl.nist.gov/div898/strd/nls/nls_main.shtml
 * @author gregs
 */
public class BatteryNISTTest {

    public static double[] lanczosNIST = {
        2.5134, 0.00000,
        2.0443, 5.00000e-2,
        1.6684, 1.00000e-1,
        1.3664, 1.50000e-1,
        1.1232, 2.00000e-1,
        0.9269, 2.50000e-1,
        0.7679, 3.00000e-1,
        0.6389, 3.50000e-1,
        0.5338, 4.00000e-1,
        0.4479, 4.50000e-1,
        0.3776, 5.00000e-1,
        0.3197, 5.50000e-1,
        0.2720, 6.00000e-1,
        0.2325, 6.50000e-1,
        0.1997, 7.00000e-1,
        0.1723, 7.50000e-1,
        0.1493, 8.00000e-1,
        0.1301, 8.50000e-1,
        0.1138, 9.00000e-1,
        0.1000, 9.50000e-1,
        0.0883, 1.00000,
        0.0783, 1.05000,
        0.0698, 1.10000,
        0.0624, 1.15000};
    /* the lanzcos objective function -------------------------------*/
    private final nistMVRF lanczosObjectFunc = new nistMVRF(lanczosNIST, 1, 24, 6) {

        @Override
        protected double partialDeriv(double[] point, int idx) {
            double cy, cx, r, ret = 0.0, d;
            int ptr = 0, ptr1;
            for (int i = 0; i < this.nobs; i++) {
                cy = data[ptr++];
                cx = data[ptr++];
                ptr1 = 0;
                d = 0.0;
                for (int j = 0; j < 3; j++) {
                    d += point[ptr1++] * FastMath.exp(-cx * point[ptr1++]);
                }
                r = cy - d;
                if (idx == 0) {
                    ret -= (2.0 * r) * FastMath.exp(-cx * point[1]);
                } else if (idx == 1) {
                    ret += (2.0 * r) * FastMath.exp(-cx * point[1]) * cx * point[0];
                } else if (idx == 2) {
                    ret -= (2.0 * r) * FastMath.exp(-cx * point[3]);
                } else if (idx == 3) {
                    ret += (2.0 * r) * FastMath.exp(-cx * point[3]) * cx * point[2];
                } else if (idx == 4) {
                    ret -= (2.0 * r) * FastMath.exp(-cx * point[5]);
                } else {
                    ret += (2.0 * r) * FastMath.exp(-cx * point[5]) * cx * point[4];
                }
            }
            return (ret);
        }

        public double value(double[] point) {
            double ret = 0.0, err, d, cx, cy;
            int ptr = 0, ptr1 = 0;
            for (int i = 0; i < this.nobs; i++) {
                cy = data[ptr++];
                cx = data[ptr++];
                d = 0.0;
                ptr1 = 0;
                for (int j = 0; j < 3; j++) {
                    d += point[ptr1++] * FastMath.exp(-cx * point[ptr1++]);
                }
                err = cy - d;
                ret += err * err;
            }
            return (ret);
        }

        @Override
        protected double[] getGradient(double[] point) {
            Arrays.fill(gradient, 0.0);
            double cy, cx, r, d = 0;
            int ptr = 0, ptr1;
            for (int i = 0; i < this.nobs; i++) {
                cy = data[ptr++];
                cx = data[ptr++];
                ptr1 = 0;
                d = 0.0;
                for (int j = 0; j < 3; j++) {
                    d += point[ptr1++] * FastMath.exp(-cx * point[ptr1++]);
                }
                r = cy - d;
                gradient[0] -= (2.0 * r) * FastMath.exp(-cx * point[1]);
                gradient[1] += (2.0 * r) * FastMath.exp(-cx * point[1]) * cx * point[0];

                gradient[2] -= (2.0 * r) * FastMath.exp(-cx * point[3]);
                gradient[3] += (2.0 * r) * FastMath.exp(-cx * point[3]) * cx * point[2];

                gradient[4] -= (2.0 * r) * FastMath.exp(-cx * point[5]);
                gradient[5] += (2.0 * r) * FastMath.exp(-cx * point[5]) * cx * point[4];
            }
            return this.gradient;
        }
    };

   /* chwirut1 data ------------------------*/
    public static double[] chwirut1NIST = {
        92.9000, 0.5000,
        78.7000, 0.6250,
        64.2000, 0.7500,
        64.9000, 0.8750,
        57.1000, 1.0000,
        43.3000, 1.2500,
        31.1000, 1.7500,
        23.6000, 2.2500,
        31.0500, 1.7500,
        23.7750, 2.2500,
        17.7375, 2.7500,
        13.8000, 3.2500,
        11.5875, 3.7500,
        9.4125, 4.2500,
        7.7250, 4.7500,
        7.3500, 5.2500,
        8.0250, 5.7500,
        90.6000, 0.5000,
        76.9000, 0.6250,
        71.6000, 0.7500,
        63.6000, 0.8750,
        54.0000, 1.0000,
        39.2000, 1.2500,
        29.3000, 1.7500,
        21.4000, 2.2500,
        29.1750, 1.7500,
        22.1250, 2.2500,
        17.5125, 2.7500,
        14.2500, 3.2500,
        9.4500, 3.7500,
        9.1500, 4.2500,
        7.9125, 4.7500,
        8.4750, 5.2500,
        6.1125, 5.7500,
        80.0000, 0.5000,
        79.0000, 0.6250,
        63.8000, 0.7500,
        57.2000, 0.8750,
        53.2000, 1.0000,
        42.5000, 1.2500,
        26.8000, 1.7500,
        20.4000, 2.2500,
        26.8500, 1.7500,
        21.0000, 2.2500,
        16.4625, 2.7500,
        12.5250, 3.2500,
        10.5375, 3.7500,
        8.5875, 4.2500,
        7.1250, 4.7500,
        6.1125, 5.2500,
        5.9625, 5.7500,
        74.1000, 0.5000,
        67.3000, 0.6250,
        60.8000, 0.7500,
        55.5000, 0.8750,
        50.3000, 1.0000,
        41.0000, 1.2500,
        29.4000, 1.7500,
        20.4000, 2.2500,
        29.3625, 1.7500,
        21.1500, 2.2500,
        16.7625, 2.7500,
        13.2000, 3.2500,
        10.8750, 3.7500,
        8.1750, 4.2500,
        7.3500, 4.7500,
        5.9625, 5.2500,
        5.6250, 5.7500,
        81.5000, .5000,
        62.4000, .7500,
        32.5000, 1.5000,
        12.4100, 3.0000,
        13.1200, 3.0000,
        15.5600, 3.0000,
        5.6300, 6.0000,
        78.0000, .5000,
        59.9000, .7500,
        33.2000, 1.5000,
        13.8400, 3.0000,
        12.7500, 3.0000,
        14.6200, 3.0000,
        3.9400, 6.0000,
        76.8000, .5000,
        61.0000, .7500,
        32.9000, 1.5000,
        13.8700, 3.0000,
        11.8100, 3.0000,
        13.3100, 3.0000,
        5.4400, 6.0000,
        78.0000, .5000,
        63.5000, .7500,
        33.8000, 1.5000,
        12.5600, 3.0000,
        5.6300, 6.0000,
        12.7500, 3.0000,
        13.1200, 3.0000,
        5.4400, 6.0000,
        76.8000, .5000,
        60.0000, .7500,
        47.8000, 1.0000,
        32.0000, 1.5000,
        22.2000, 2.0000,
        22.5700, 2.0000,
        18.8200, 2.5000,
        13.9500, 3.0000,
        11.2500, 4.0000,
        9.0000, 5.0000,
        6.6700, 6.0000,
        75.8000, .5000,
        62.0000, .7500,
        48.8000, 1.0000,
        35.2000, 1.5000,
        20.0000, 2.0000,
        20.3200, 2.0000,
        19.3100, 2.5000,
        12.7500, 3.0000,
        10.4200, 4.0000,
        7.3100, 5.0000,
        7.4200, 6.0000,
        70.5000, .5000,
        59.5000, .7500,
        48.5000, 1.0000,
        35.8000, 1.5000,
        21.0000, 2.0000,
        21.6700, 2.0000,
        21.0000, 2.5000,
        15.6400, 3.0000,
        8.1700, 4.0000,
        8.5500, 5.0000,
        10.1200, 6.0000,
        78.0000, .5000,
        66.0000, .6250,
        62.0000, .7500,
        58.0000, .8750,
        47.7000, 1.0000,
        37.8000, 1.2500,
        20.2000, 2.2500,
        21.0700, 2.2500,
        13.8700, 2.7500,
        9.6700, 3.2500,
        7.7600, 3.7500,
        5.4400, 4.2500,
        4.8700, 4.7500,
        4.0100, 5.2500,
        3.7500, 5.7500,
        24.1900, 3.0000,
        25.7600, 3.0000,
        18.0700, 3.0000,
        11.8100, 3.0000,
        12.0700, 3.0000,
        16.1200, 3.0000,
        70.8000, .5000,
        54.7000, .7500,
        48.0000, 1.0000,
        39.8000, 1.5000,
        29.8000, 2.0000,
        23.7000, 2.5000,
        29.6200, 2.0000,
        23.8100, 2.5000,
        17.7000, 3.0000,
        11.5500, 4.0000,
        12.0700, 5.0000,
        8.7400, 6.0000,
        80.7000, .5000,
        61.3000, .7500,
        47.5000, 1.0000,
        29.0000, 1.5000,
        24.0000, 2.0000,
        17.7000, 2.5000,
        24.5600, 2.0000,
        18.6700, 2.5000,
        16.2400, 3.0000,
        8.7400, 4.0000,
        7.8700, 5.0000,
        8.5100, 6.0000,
        66.7000, .5000,
        59.2000, .7500,
        40.8000, 1.0000,
        30.7000, 1.5000,
        25.7000, 2.0000,
        16.3000, 2.5000,
        25.9900, 2.0000,
        16.9500, 2.5000,
        13.3500, 3.0000,
        8.6200, 4.0000,
        7.2000, 5.0000,
        6.6400, 6.0000,
        13.6900, 3.0000,
        81.0000, .5000,
        64.5000, .7500,
        35.5000, 1.5000,
        13.3100, 3.0000,
        4.8700, 6.0000,
        12.9400, 3.0000,
        5.0600, 6.0000,
        15.1900, 3.0000,
        14.6200, 3.0000,
        15.6400, 3.0000,
        25.5000, 1.7500,
        25.9500, 1.7500,
        81.7000, .5000,
        61.6000, .7500,
        29.8000, 1.7500,
        29.8100, 1.7500,
        17.1700, 2.7500,
        10.3900, 3.7500,
        28.4000, 1.7500,
        28.6900, 1.7500,
        81.3000, .5000,
        60.9000, .7500,
        16.6500, 2.7500,
        10.0500, 3.7500,
        28.9000, 1.7500,
        28.9500, 1.7500
    };

    /* the chwirut1 objective function */
    private final nistMVRF chwirut1ObjectFunc = new chwirut(chwirut1NIST, 1, 214, 3);

    //http://www.itl.nist.gov/div898/strd/nls/data/LINKS/DATA/Chwirut2.dat
    public static double[] chwirut2NIST = {
        92.9000, 0.500,
        57.1000, 1.000,
        31.0500, 1.750,
        11.5875, 3.750,
        8.0250, 5.750,
        63.6000, 0.875,
        21.4000, 2.250,
        14.2500, 3.250,
        8.4750, 5.250,
        63.8000, 0.750,
        26.8000, 1.750,
        16.4625, 2.750,
        7.1250, 4.750,
        67.3000, 0.625,
        41.0000, 1.250,
        21.1500, 2.250,
        8.1750, 4.250,
        81.5000, .500,
        13.1200, 3.000,
        59.9000, .750,
        14.6200, 3.000,
        32.9000, 1.500,
        5.4400, 6.000,
        12.5600, 3.000,
        5.4400, 6.000,
        32.0000, 1.500,
        13.9500, 3.000,
        75.8000, .500,
        20.0000, 2.000,
        10.4200, 4.000,
        59.5000, .750,
        21.6700, 2.000,
        8.5500, 5.000,
        62.0000, .750,
        20.2000, 2.250,
        7.7600, 3.750,
        3.7500, 5.750,
        11.8100, 3.000,
        54.7000, .750,
        23.7000, 2.500,
        11.5500, 4.000,
        61.3000, .750,
        17.7000, 2.500,
        8.7400, 4.000,
        59.2000, .750,
        16.3000, 2.500,
        8.6200, 4.000,
        81.0000, .500,
        4.8700, 6.000,
        14.6200, 3.000,
        81.7000, .500,
        17.1700, 2.750,
        81.3000, .500,
        28.9000, 1.750
    };
    
    /* the chwirut 2 objective --------------------------------------------------*/
    private final nistMVRF chwirut2ObjectFunc = new chwirut(chwirut2NIST, 1, 54, 3);
    
    //http://www.itl.nist.gov/div898/strd/nls/data/LINKS/DATA/Misra1a.dat
    //y               x
    private static double[] misra1aNIST = {
        10.07, 77.6,
        14.73, 114.9,
        17.94, 141.1,
        23.93, 190.8,
        29.61, 239.9,
        35.18, 289.0,
        40.02, 332.8,
        44.82, 378.4,
        50.76, 434.8,
        55.05, 477.3,
        61.01, 536.8,
        66.40, 593.1,
        75.47, 689.1,
        81.78, 760.0
    };

    /* the misra1a objective function */
    private final nistMVRF misra1aObjectFunc = new nistMVRF(misra1aNIST, 1, 14, 2) {

        @Override
        protected double partialDeriv(double[] point, int idx) {
            double cy, cx, r, ret = 0.0;
            int ptr = 0;
            for (int i = 0; i < this.nobs; i++) {
                cy = data[ptr++];
                cx = data[ptr++];
                r = cy - point[0] * (1.0 - FastMath.exp(-cx * point[1]));
                if (idx == 0) {
                    ret -= (2.0 * r) * (1.0 - FastMath.exp(-cx * point[1]));
                } else {
                    ret -= (2.0 * r) * cx * point[0] * FastMath.exp(-cx * point[1]);
                }
            }
            return (ret);
        }

        public double value(double[] point) {
            double ret = 0.0, err;
            int ptr = 0;
            for (int i = 0; i < this.nobs; i++) {
                err = data[ptr++] - point[0] * (1.0 - FastMath.exp(-data[ptr++] * point[1]));
                ret += err * err;
            }
            return (ret);
        }

        @Override
        protected double[] getGradient(double[] point) {
            Arrays.fill(gradient, 0.0);
            double cy, cx, r;
            int ptr = 0;
            for (int i = 0; i < this.nobs; i++) {
                cy = data[ptr++];
                cx = data[ptr++];
                r = cy - point[0] * (1.0 - FastMath.exp(-cx * point[1]));
                gradient[0] -= (2.0 * r) * (1.0 - FastMath.exp(-cx * point[1]));
                gradient[1] -= (2.0 * r) * cx * point[0] * FastMath.exp(-cx * point[1]);
            }
            return this.gradient;
        }
    };
    private static double[] correctParamMisra1a = {2.3894212918e2, 5.5015643181E-4};
    private static double[] correctParamChwirut2 = {1.6657666537e-1, 5.1653291286e-3, 1.2150007096e-2};
    private static double[] correctParamChwirut1 = {1.9027818370e-1, 6.1314004477e-3, 1.0530908399e-2};
    private static double[] correctParamLanczos = {8.6816414977e-2, 9.5498101505e-01, 8.4400777463E-01, 2.9515951832, 1.5825685901, 4.9863565084};

    @Test
    public void lanczosTest() {
        //first check to see that the NIST Object function is being replicated correctly
        double obj = this.lanczosObjectFunc.value(correctParamLanczos);
        Assert.assertEquals(1.6117193594E-08, obj, 1.0e-8);

        double[] grad = this.lanczosObjectFunc.getGradient(correctParamLanczos);
        double[] grad2 = new double[6];
        grad2[0] = this.lanczosObjectFunc.partialDeriv(correctParamLanczos, 0);
        grad2[1] = this.lanczosObjectFunc.partialDeriv(correctParamLanczos, 1);
        grad2[2] = this.lanczosObjectFunc.partialDeriv(correctParamLanczos, 2);
        grad2[3] = this.lanczosObjectFunc.partialDeriv(correctParamLanczos, 3);
        grad2[4] = this.lanczosObjectFunc.partialDeriv(correctParamLanczos, 4);
        grad2[5] = this.lanczosObjectFunc.partialDeriv(correctParamLanczos, 5);
        TestUtils.assertEquals("Grads...", grad, grad2, 1.0e-12);

        double[] n_grad = this.getGradient(lanczosObjectFunc, correctParamLanczos, 1.0e-5);
        //System.out.println("g = " + grad[0] + " ng = " + n_grad[0]);
        //System.out.println("g = " + grad[1] + " ng = " + n_grad[1]);
        if (FastMath.abs(grad[0] - n_grad[0]) > FastMath.max(1.0e-6, 1.0e-6 * (grad[0] + n_grad[0]) / 2.0)) {
            Assert.fail("Check gradient at 1");
        }
        if (FastMath.abs(grad[1] - n_grad[1]) > FastMath.max(1.0e-6, 1.0e-6 * (grad[1] + n_grad[1]) / 2.0)) {
            Assert.fail("Check gradient at 2");
        }
        if (FastMath.abs(grad[2] - n_grad[2]) > FastMath.max(1.0e-6, 1.0e-6 * (grad[2] + n_grad[2]) / 2.0)) {
            Assert.fail("Check gradient at 2");
        }
        if (FastMath.abs(grad[3] - n_grad[3]) > FastMath.max(1.0e-6, 1.0e-6 * (grad[3] + n_grad[3]) / 2.0)) {
            Assert.fail("Check gradient at 2");
        }
        if (FastMath.abs(grad[4] - n_grad[4]) > FastMath.max(1.0e-6, 1.0e-6 * (grad[4] + n_grad[4]) / 2.0)) {
            Assert.fail("Check gradient at 2");
        }
        if (FastMath.abs(grad[5] - n_grad[5]) > FastMath.max(1.0e-6, 1.0e-6 * (grad[5] + n_grad[5]) / 2.0)) {
            Assert.fail("Check gradient at 2");
        }
        return;
    }

    //@Test
    public void lanczos_BOBYQA() {
        double[] bobyqa = run(new BOBYQAOptimizer(10),
                lanczosObjectFunc, new double[]{1.2,0.3,5.6,5.5,6.5,7.6});
        TestUtils.assertEquals(correctParamLanczos, bobyqa, 1.0e-8);
    }

    //@Test
    public void lanczosTest_cgPolakRibiere() {
        double[] cgPolakRibiere = run(new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.POLAK_RIBIERE),
                lanczosObjectFunc, new double[]{1.2,0.3,5.6,5.5,6.5,7.6});
        TestUtils.assertEquals(correctParamLanczos, cgPolakRibiere, 1.0e-8);
    }

    //@Test
    public void lanczosTest_cgPolakRibiere2() {
        double[] cgPolakRibiere2 = run(new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.POLAK_RIBIERE),
                lanczosObjectFunc, new double[]{0.5,0.7,3.6,4.2,4,6.3});
        TestUtils.assertEquals(correctParamLanczos, cgPolakRibiere2, 1.0e-8);
    }

    //@Test
    public void lanczosTest_cgFletcherReeves() {
        double[] cgFletcherReeves = run(new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.FLETCHER_REEVES),
                lanczosObjectFunc, new double[]{1.2,0.3,5.6,5.5,6.5,7.6});
        TestUtils.assertEquals(correctParamLanczos, cgFletcherReeves, 1.0e-8);
    }

    //@Test
    public void lanczosTest_cgFletcherReeves2() {
        double[] cgFletcherReeves2 = run(new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.FLETCHER_REEVES),
                lanczosObjectFunc, new double[]{0.5,0.7,3.6,4.2,4,6.3});
        TestUtils.assertEquals(correctParamLanczos, cgFletcherReeves2, 1.0e-8);
    }

    //@Test
    public void lanczosTest_powell() {
        double[] resPowell = run(new PowellOptimizer(1.0e-8, 1.0e-8), lanczosObjectFunc,
                new double[]{1.2,0.3,5.6,5.5,6.5,7.6});
        TestUtils.assertEquals(correctParamLanczos, resPowell, 1.0e-8);
    }

    //@Test
    public void lanczosTest_powell2() {
        double[] resPowell2 = run(new PowellOptimizer(1.0e-8, 1.0e-8), lanczosObjectFunc,
                new double[]{0.5,0.7,3.6,4.2,4,6.3});
        TestUtils.assertEquals(correctParamLanczos, resPowell2, 1.0e-8);
    }

    @Test
    public void chwirut1Test() {
        //first check to see that the NIST Object function is being replicated correctly
        double obj = this.chwirut1ObjectFunc.value(correctParamChwirut1);
        Assert.assertEquals(2.3844771393e3, obj, 1.0e-8);

        double[] grad = this.chwirut1ObjectFunc.getGradient(correctParamChwirut1);
        double[] grad2 = new double[3];
        grad2[0] = this.chwirut1ObjectFunc.partialDeriv(correctParamChwirut1, 0);
        grad2[1] = this.chwirut1ObjectFunc.partialDeriv(correctParamChwirut1, 1);
        grad2[2] = this.chwirut1ObjectFunc.partialDeriv(correctParamChwirut1, 2);
        TestUtils.assertEquals("Grads...", grad, grad2, 1.0e-12);
        return;
    }

    //@Test
    public void chwirut1_BOBYQA() {
        double[] bobyqa = run(new BOBYQAOptimizer(5),
                chwirut1ObjectFunc, new double[]{0.1, 0.01, 0.02});
        TestUtils.assertEquals(correctParamChwirut1, bobyqa, 1.0e-8);
    }

    //@Test
    public void chwirut1Test_cgPolakRibiere() {
        double[] cgPolakRibiere = run(new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.POLAK_RIBIERE),
                chwirut1ObjectFunc, new double[]{0.1, 0.01, 0.02});
        TestUtils.assertEquals(correctParamChwirut1, cgPolakRibiere, 1.0e-8);
    }

    //@Test
    public void chwirut1Test_cgPolakRibiere2() {
        double[] cgPolakRibiere2 = run(new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.POLAK_RIBIERE),
                chwirut1ObjectFunc, new double[]{0.15, 0.008, 0.01});
        TestUtils.assertEquals(correctParamChwirut1, cgPolakRibiere2, 1.0e-8);
    }

    //@Test
    public void chwirut1Test_cgFletcherReeves() {
        double[] cgFletcherReeves = run(new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.FLETCHER_REEVES),
                chwirut1ObjectFunc, new double[]{0.1, 0.01, 0.02});
        TestUtils.assertEquals(correctParamChwirut1, cgFletcherReeves, 1.0e-8);
    }

    //@Test
    public void chwirut1Test_cgFletcherReeves2() {
        double[] cgFletcherReeves2 = run(new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.FLETCHER_REEVES),
                chwirut1ObjectFunc, new double[]{0.15, 0.008, 0.01});
        TestUtils.assertEquals(correctParamChwirut1, cgFletcherReeves2, 1.0e-8);
    }

    //@Test
    public void chwirut1Test_powell() {
        double[] resPowell = run(new PowellOptimizer(1.0e-8, 1.0e-8), chwirut1ObjectFunc, new double[]{0.1, 0.01, 0.02});
        TestUtils.assertEquals(correctParamChwirut1, resPowell, 1.0e-8);
    }

    //@Test
    public void chwirut1Test_powell2() {
        double[] resPowell2 = run(new PowellOptimizer(1.0e-8, 1.0e-8), chwirut1ObjectFunc, new double[]{0.15, 0.08, 0.01});
        TestUtils.assertEquals(correctParamChwirut1, resPowell2, 1.0e-8);
    }

    @Test
    public void chwirut2Test() {
        //first check to see that the NIST Object function is being replicated correctly
        double obj = this.chwirut2ObjectFunc.value(correctParamChwirut2);
        Assert.assertEquals(5.1304802941e02, obj, 1.0e-8);

        double[] grad = this.chwirut2ObjectFunc.getGradient(correctParamChwirut2);
        double[] grad2 = new double[3];
        grad2[0] = this.chwirut2ObjectFunc.partialDeriv(correctParamChwirut2, 0);
        grad2[1] = this.chwirut2ObjectFunc.partialDeriv(correctParamChwirut2, 1);
        grad2[2] = this.chwirut2ObjectFunc.partialDeriv(correctParamChwirut2, 2);
        TestUtils.assertEquals("Grads...", grad, grad2, 1.0e-12);
        return;
    }

    //@Test
    public void chwirut2_BOBYQA() {
        double[] bobyqa = run(new BOBYQAOptimizer(5),
                chwirut2ObjectFunc, new double[]{0.1, 0.01, 0.02});
        TestUtils.assertEquals(correctParamChwirut2, bobyqa, 1.0e-8);
    }

    //@Test
    public void chwirut2Test_cgPolakRibiere() {
        double[] cgPolakRibiere = run(new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.POLAK_RIBIERE),
                chwirut2ObjectFunc, new double[]{0.1, 0.01, 0.02});
        TestUtils.assertEquals(correctParamChwirut2, cgPolakRibiere, 1.0e-8);
    }

    //@Test
    public void chwirut2Test_cgPolakRibiere2() {
        double[] cgPolakRibiere2 = run(new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.POLAK_RIBIERE),
                chwirut2ObjectFunc, new double[]{0.15, 0.008, 0.01});
        TestUtils.assertEquals(correctParamChwirut2, cgPolakRibiere2, 1.0e-8);
    }

    //@Test
    public void chwirut2Test_cgFletcherReeves() {
        double[] cgFletcherReeves = run(new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.FLETCHER_REEVES),
                chwirut2ObjectFunc, new double[]{0.1, 0.01, 0.02});
        TestUtils.assertEquals(correctParamChwirut2, cgFletcherReeves, 1.0e-8);
    }

    //@Test
    public void chwirut2Test_cgFletcherReeves2() {
        double[] cgFletcherReeves2 = run(new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.FLETCHER_REEVES),
                chwirut2ObjectFunc, new double[]{0.15, 0.008, 0.01});
        TestUtils.assertEquals(correctParamChwirut2, cgFletcherReeves2, 1.0e-8);
    }

    //@Test
    public void chwirut2Test_powell() {
        double[] resPowell = run(new PowellOptimizer(1.0e-8, 1.0e-8), chwirut2ObjectFunc, new double[]{0.1, 0.01, 0.02});
        TestUtils.assertEquals(correctParamChwirut2, resPowell, 1.0e-8);
    }

    //@Test
    public void chwirut2Test_powell2() {
        double[] resPowell2 = run(new PowellOptimizer(1.0e-8, 1.0e-8), chwirut2ObjectFunc, new double[]{0.15, 0.08, 0.01});
        TestUtils.assertEquals(correctParamChwirut2, resPowell2, 1.0e-8);
    }

    @Test
    public void misra1aTest() {
        //first check to see that the NIST Object function is being replicated correctly
        double obj = this.misra1aObjectFunc.value(correctParamMisra1a);
        Assert.assertEquals(1.2455138894e-01, obj, 1.0e-8);

        double[] grad = this.misra1aObjectFunc.getGradient(correctParamMisra1a);
        double[] grad2 = new double[2];
        grad2[0] = this.misra1aObjectFunc.partialDeriv(correctParamMisra1a, 0);
        grad2[1] = this.misra1aObjectFunc.partialDeriv(correctParamMisra1a, 1);

        TestUtils.assertEquals("Grads...", grad, grad2, 1.0e-12);

//        double[] n_grad = this.getGradient(misra1aObjectFunc, correctParamMisra1a, 1.0e-5);
//        System.out.println("g = " + grad[0] + " ng = " + n_grad[0]);
//        System.out.println("g = " + grad[1] + " ng = " + n_grad[1]);
//        if( FastMath.abs(grad[0] - n_grad[0] ) > FastMath.max(1.0e-6, 1.0e-6 * (grad[0]+n_grad[0])/2.0) ){
//            Assert.fail("Check gradient at 1");
//        }
//        if( FastMath.abs(grad[1] - n_grad[1] ) > FastMath.max(1.0e-6, 1.0e-6 * (grad[1]+n_grad[1])/2.0) ){
//            Assert.fail("Check gradient at 2");
//        }
        return;
    }

    //@Test
    public void misra1a_BOBYQA() {
        double[] bobyqa = run(new BOBYQAOptimizer(4),
                misra1aObjectFunc, new double[]{500.0, 0.0001});
        TestUtils.assertEquals(correctParamMisra1a, bobyqa, 1.0e-8);
    }

    //@Test
    public void misra1aTest_cgPolakRibiere() {
        double[] cgPolakRibiere = run(new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.POLAK_RIBIERE),
                misra1aObjectFunc, new double[]{500.0, 0.0001});
        TestUtils.assertEquals(correctParamMisra1a, cgPolakRibiere, 1.0e-8);
    }

    //@Test
    public void misra1aTest_cgPolakRibiere2() {
        double[] cgPolakRibiere2 = run(new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.POLAK_RIBIERE),
                misra1aObjectFunc, new double[]{250.0, 0.0005});
        TestUtils.assertEquals(correctParamMisra1a, cgPolakRibiere2, 1.0e-8);
    }

    //@Test
    public void misra1aTest_cgFletcherReeves() {
        double[] cgFletcherReeves = run(new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.FLETCHER_REEVES),
                misra1aObjectFunc, new double[]{500.0, 0.0001});
        TestUtils.assertEquals(correctParamMisra1a, cgFletcherReeves, 1.0e-8);
    }

    //@Test
    public void misra1aTest_cgFletcherReeves2() {
        double[] cgFletcherReeves2 = run(new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.FLETCHER_REEVES),
                misra1aObjectFunc, new double[]{250.0, 0.0005});
        TestUtils.assertEquals(correctParamMisra1a, cgFletcherReeves2, 1.0e-8);
    }

    //@Test
    public void misra1aTest_powell() {
        double[] resPowell = run(new PowellOptimizer(1.0e-8, 1.0e-8), misra1aObjectFunc, new double[]{500.0, 0.0001});
        TestUtils.assertEquals(correctParamMisra1a, resPowell, 1.0e-8);
    }

    //@Test
    public void misra1aTest_powell2() {
        double[] resPowell2 = run(new PowellOptimizer(1.0e-8, 1.0e-8), misra1aObjectFunc, new double[]{250.0, 0.0005});
        TestUtils.assertEquals(correctParamMisra1a, resPowell2, 1.0e-8);
    }

    /* numerical gradients */
    private double[] getGradient(nistMVRF func, double[] xo, double eps) {
        double[] ret = new double[func.getNumberOfParameters()];
        for (int i = 0; i < ret.length; i++) {
            final double tmp = xo[i];
            xo[i] += eps;
            ret[i] = func.value(xo);
            xo[i] = tmp - eps;
            ret[i] -= func.value(xo);
            ret[i] /= (2.0 * eps);
            xo[i] = tmp;
        }
        return (ret);
    }
    
    /* generic test runner */
    private double[] run(MultivariateOptimizer optim, DifferentiableMultivariateFunction func, double[] start) {
        return (optim.optimize(1000000, func, GoalType.MINIMIZE, start).getPointRef());
    }
    /* generic test runner for AbstractScalarDifferentiableOptimizer */
    private double[] run(AbstractScalarDifferentiableOptimizer optim, DifferentiableMultivariateFunction func, double[] start) {
        return (optim.optimize(1000000, func, GoalType.MINIMIZE, start).getPointRef());
    }

    /* base objective function class for these tests */
    private abstract static class nistMVRF implements DifferentiableMultivariateFunction {
        protected final MultivariateFunction[] mrf;
        protected final MultivariateVectorFunction mvf = new MultivariateVectorFunction() {

            public double[] value(double[] point) throws IllegalArgumentException {
                return getGradient(point);
            }
        };
        protected double[] gradient;
        protected double[] data;
        protected int nvars;
        protected int nobs;
        protected int nparams;

        public int getNumberOfParameters() {
            return nparams;
        }

        public nistMVRF(double[] data, int nvars, int nobs, int nparams) {
            if ((nvars + 1) * nobs != data.length) {
                throw MathRuntimeException.createIllegalArgumentException(
                        LocalizedFormats.INVALID_REGRESSION_ARRAY, data.length, nobs, nvars);
            }
            this.nobs = nobs;
            this.nvars = nvars;
            this.gradient = new double[nparams];
            this.nparams = nparams;
            this.data = data;
            mrf = new MultivariateFunction[nvars];
            for (int i = 0; i < nvars; i++) {
                final int idx = i;
                mrf[i] = new MultivariateFunction() {

                    private int myIdx = idx;

                    public double value(double[] point) {
                        return partialDeriv(point, myIdx);
                    }
                };
            }
        }

        public MultivariateVectorFunction gradient() {
            return mvf;
        }

        public MultivariateFunction partialDerivative(int k) {
            return mrf[k];
        }

        protected abstract double partialDeriv(double[] point, int idx);

        protected abstract double[] getGradient(double[] point);
    }

    /* since there are multiple chwirut tests create an object       */
    private static class chwirut extends nistMVRF {

        public chwirut(double[] data, int nvars, int nobs, int nparams) {
            super(data, nvars, nobs, nparams);
        }

        @Override
        protected double partialDeriv(double[] point, int idx) {
            double cy, cx, r, ret = 0.0, d;
            int ptr = 0;
            for (int i = 0; i < this.nobs; i++) {
                cy = data[ptr++];
                cx = data[ptr++];
                d = (point[1] + point[2] * cx);
                r = cy - FastMath.exp(-cx * point[0]) / d;
                if (idx == 0) {
                    ret -= (2.0 * r * r) * cx;
                } else if (idx == 1) {
                    ret += (2.0 * r * r) / d;
                } else {
                    ret += (2.0 * r * r) * cx / d;
                }
            }
            return (ret);
        }

        public double value(double[] point) {
            double ret = 0.0, err, cx, cy;
            int ptr = 0;
            for (int i = 0; i < this.nobs; i++) {
                cy = data[ptr++];
                cx = data[ptr++];
                err = cy - (FastMath.exp(-cx * point[0]) / (point[1] + point[2] * cx));
                ret += err * err;
            }
            return (ret);
        }

        @Override
        protected double[] getGradient(double[] point) {
            Arrays.fill(gradient, 0.0);
            double cy, cx, r, d;
            int ptr = 0;
            for (int i = 0; i < this.nobs; i++) {
                cy = data[ptr++];
                cx = data[ptr++];
                d = (point[1] + point[2] * cx);
                r = cy - FastMath.exp(-cx * point[0]) / d;
                gradient[0] -= (2.0 * r * r) * cx;
                gradient[1] += (2.0 * r * r) / d;
                gradient[2] += (2.0 * r * r) * cx / d;
            }
            return this.gradient;
        }
    }
}
