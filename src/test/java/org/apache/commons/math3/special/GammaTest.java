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
package org.apache.commons.math3.special;

import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.util.FastMath;

import org.junit.Test;
import org.junit.Assert;

/**
 * @version $Id$
 */
public class GammaTest {
    private void testRegularizedGamma(double expected, double a, double x) {
        double actualP = Gamma.regularizedGammaP(a, x);
        double actualQ = Gamma.regularizedGammaQ(a, x);
        TestUtils.assertEquals(expected, actualP, 10e-15);
        TestUtils.assertEquals(actualP, 1.0 - actualQ, 10e-15);
    }

    private void testLogGamma(double expected, double x) {
        double actual = Gamma.logGamma(x);
        TestUtils.assertEquals(expected, actual, 10e-15);
    }

    @Test
    public void testRegularizedGammaNanPositive() {
        testRegularizedGamma(Double.NaN, Double.NaN, 1.0);
    }

    @Test
    public void testRegularizedGammaPositiveNan() {
        testRegularizedGamma(Double.NaN, 1.0, Double.NaN);
    }

    @Test
    public void testRegularizedGammaNegativePositive() {
        testRegularizedGamma(Double.NaN, -1.5, 1.0);
    }

    @Test
    public void testRegularizedGammaPositiveNegative() {
        testRegularizedGamma(Double.NaN, 1.0, -1.0);
    }

    @Test
    public void testRegularizedGammaZeroPositive() {
        testRegularizedGamma(Double.NaN, 0.0, 1.0);
    }

    @Test
    public void testRegularizedGammaPositiveZero() {
        testRegularizedGamma(0.0, 1.0, 0.0);
    }

    @Test
    public void testRegularizedGammaPositivePositive() {
        testRegularizedGamma(0.632120558828558, 1.0, 1.0);
    }

    @Test
    public void testLogGammaNan() {
        testLogGamma(Double.NaN, Double.NaN);
    }

    @Test
    public void testLogGammaNegative() {
        testLogGamma(Double.NaN, -1.0);
    }

    @Test
    public void testLogGammaZero() {
        testLogGamma(Double.NaN, 0.0);
    }

    @Test
    public void testLogGammaPositive() {
        testLogGamma(0.6931471805599457, 3.0);
    }

    @Test
    public void testDigammaLargeArgs() {
        double eps = 1e-8;
        Assert.assertEquals(4.6001618527380874002, Gamma.digamma(100), eps);
        Assert.assertEquals(3.9019896734278921970, Gamma.digamma(50), eps);
        Assert.assertEquals(2.9705239922421490509, Gamma.digamma(20), eps);
        Assert.assertEquals(2.9958363947076465821, Gamma.digamma(20.5), eps);
        Assert.assertEquals(2.2622143570941481605, Gamma.digamma(10.1), eps);
        Assert.assertEquals(2.1168588189004379233, Gamma.digamma(8.8), eps);
        Assert.assertEquals(1.8727843350984671394, Gamma.digamma(7), eps);
        Assert.assertEquals(0.42278433509846713939, Gamma.digamma(2), eps);
        Assert.assertEquals(-100.56088545786867450, Gamma.digamma(0.01), eps);
        Assert.assertEquals(-4.0390398965921882955, Gamma.digamma(-0.8), eps);
        Assert.assertEquals(4.2003210041401844726, Gamma.digamma(-6.3), eps);
    }

    @Test
    public void testDigammaSmallArgs() {
        // values for negative powers of 10 from 1 to 30 as computed by webMathematica with 20 digits
        // see functions.wolfram.com
        double[] expected = {-10.423754940411076795, -100.56088545786867450, -1000.5755719318103005,
                -10000.577051183514335, -100000.57719921568107, -1.0000005772140199687e6, -1.0000000577215500408e7,
                -1.0000000057721564845e8, -1.0000000005772156633e9, -1.0000000000577215665e10, -1.0000000000057721566e11,
                -1.0000000000005772157e12, -1.0000000000000577216e13, -1.0000000000000057722e14, -1.0000000000000005772e15, -1e+16,
                -1e+17, -1e+18, -1e+19, -1e+20, -1e+21, -1e+22, -1e+23, -1e+24, -1e+25, -1e+26,
                -1e+27, -1e+28, -1e+29, -1e+30};
        for (double n = 1; n < 30; n++) {
            checkRelativeError(String.format("Test %.0f: ", n), expected[(int) (n - 1)], Gamma.digamma(FastMath.pow(10.0, -n)), 1e-8);
        }
    }

    @Test
    public void testTrigamma() {
        double eps = 1e-8;
        // computed using webMathematica.  For example, to compute trigamma($i) = Polygamma(1, $i), use
        //
        // http://functions.wolfram.com/webMathematica/Evaluated.jsp?name=PolyGamma2&plottype=0&vars={%221%22,%22$i%22}&digits=20
        double[] data = {
                1e-4, 1.0000000164469368793e8,
                1e-3, 1.0000016425331958690e6,
                1e-2, 10001.621213528313220,
                1e-1, 101.43329915079275882,
                1, 1.6449340668482264365,
                2, 0.64493406684822643647,
                3, 0.39493406684822643647,
                4, 0.28382295573711532536,
                5, 0.22132295573711532536,
                10, 0.10516633568168574612,
                20, 0.051270822935203119832,
                50, 0.020201333226697125806,
                100, 0.010050166663333571395
        };
        for (int i = data.length - 2; i >= 0; i -= 2) {
            Assert.assertEquals(String.format("trigamma %.0f", data[i]), data[i + 1], Gamma.trigamma(data[i]), eps);
        }
    }

    /**
     * Reference data for the {@link Gamma#logGamma(double)} function. This data
     * was generated with the following <a
     * href="http://maxima.sourceforge.net/">Maxima</a> script.
     *
     * <pre>
     * kill(all);
     *
     * fpprec : 64;
     * gamln(x) := log(gamma(x));
     * x : append(makelist(bfloat(i / 8), i, 1, 80),
     *     [0.8b0, 1b2, 1b3, 1b4, 1b5, 1b6, 1b7, 1b8, 1b9, 1b10]);
     *
     * for i : 1 while i <= length(x) do
     *     print("{", float(x[i]), ",", float(gamln(x[i])), "},");
     * </pre>
     */
    private static final double[][] LOG_GAMMA_REF = {
        { 0.125 , 2.019418357553796 },
        { 0.25 , 1.288022524698077 },
        { 0.375 , .8630739822706475 },
        { 0.5 , .5723649429247001 },
        { 0.625 , .3608294954889402 },
        { 0.75 , .2032809514312954 },
        { 0.875 , .08585870722533433 },
        { 1.0 , 0.0 },
        { 1.125 , - .06002318412603958 },
        { 1.25 , - .09827183642181316 },
        { 1.375 , - .1177552707410788 },
        { 1.5 , - .1207822376352452 },
        { 1.625 , - .1091741337567954 },
        { 1.75 , - .08440112102048555 },
        { 1.875 , - 0.0476726853991883 },
        { 2.0 , 0.0 },
        { 2.125 , .05775985153034387 },
        { 2.25 , .1248717148923966 },
        { 2.375 , .2006984603774558 },
        { 2.5 , .2846828704729192 },
        { 2.625 , .3763336820249054 },
        { 2.75 , .4752146669149371 },
        { 2.875 , .5809359740231859 },
        { 3.0 , .6931471805599453 },
        { 3.125 , 0.811531653906724 },
        { 3.25 , .9358019311087253 },
        { 3.375 , 1.06569589786406 },
        { 3.5 , 1.200973602347074 },
        { 3.625 , 1.341414578068493 },
        { 3.75 , 1.486815578593417 },
        { 3.875 , 1.6369886482725 },
        { 4.0 , 1.791759469228055 },
        { 4.125 , 1.950965937095089 },
        { 4.25 , 2.114456927450371 },
        { 4.375 , 2.282091222188554 },
        { 4.5 , 2.453736570842442 },
        { 4.625 , 2.62926886637513 },
        { 4.75 , 2.808571418575736 },
        { 4.875 , 2.99153431107781 },
        { 5.0 , 3.178053830347946 },
        { 5.125 , 3.368031956881733 },
        { 5.25 , 3.561375910386697 },
        { 5.375 , 3.757997741998131 },
        { 5.5 , 3.957813967618717 },
        { 5.625 , 4.160745237339519 },
        { 5.75 , 4.366716036622286 },
        { 5.875 , 4.57565441552762 },
        { 6.0 , 4.787491742782046 },
        { 6.125 , 5.002162481906205 },
        { 6.25 , 5.219603986990229 },
        { 6.375 , 5.439756316011858 },
        { 6.5 , 5.662562059857142 },
        { 6.625 , 5.887966185430003 },
        { 6.75 , 6.115915891431546 },
        { 6.875 , 6.346360475557843 },
        { 7.0 , 6.579251212010101 },
        { 7.125 , 6.814541238336996 },
        { 7.25 , 7.05218545073854 },
        { 7.375 , 7.292140407056348 },
        { 7.5 , 7.534364236758733 },
        { 7.625 , 7.778816557302289 },
        { 7.75 , 8.025458396315983 },
        { 7.875 , 8.274252119110479 },
        { 8.0 , 8.525161361065415 },
        { 8.125 , 8.77815096449171 },
        { 8.25 , 9.033186919605123 },
        { 8.375 , 9.290236309282232 },
        { 8.5 , 9.549267257300997 },
        { 8.625 , 9.810248879795765 },
        { 8.75 , 10.07315123968124 },
        { 8.875 , 10.33794530382217 },
        { 9.0 , 10.60460290274525 },
        { 9.125 , 10.87309669270751 },
        { 9.25 , 11.14340011995171 },
        { 9.375 , 11.41548738699336 },
        { 9.5 , 11.68933342079727 },
        { 9.625 , 11.96491384271319 },
        { 9.75 , 12.24220494005076 },
        { 9.875 , 12.52118363918365 },
        { 10.0 , 12.80182748008147 },
        { 0.8 , .1520596783998376 },
        { 100.0 , 359.1342053695754 },
        { 1000.0 , 5905.220423209181 },
        { 10000.0 , 82099.71749644238 },
        { 100000.0 , 1051287.708973657 },
        { 1000000.0 , 1.2815504569147612e+7 },
        { 10000000.0 , 1.511809493694739e+8 },
        { 1.e+8 , 1.7420680661038346e+9 },
        { 1.e+9 , 1.972326582750371e+10 },
        { 1.e+10 , 2.202585092888106e+11 },
    };

    @Test
    public void testLogGamma() {
        final int ulps = 130;
        for (int i = 0; i < LOG_GAMMA_REF.length; i++) {
            final double[] data = LOG_GAMMA_REF[i];
            final double x = data[0];
            final double expected = data[1];
            final double actual = Gamma.logGamma(x);
            final double tol;
            if (expected == 0.0) {
                tol = 1E-15;
            } else {
                tol = ulps * FastMath.ulp(expected);
            }
            Assert.assertEquals(Double.toString(x), expected, actual, tol);
        }
    }

    private void checkRelativeError(String msg, double expected, double actual, double tolerance) {
        Assert.assertEquals(msg, expected, actual, FastMath.abs(tolerance * actual));
    }
}
