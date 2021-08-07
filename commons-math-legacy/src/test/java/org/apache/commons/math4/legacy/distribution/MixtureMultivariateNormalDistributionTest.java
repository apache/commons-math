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

import java.util.List;

import org.apache.commons.math4.legacy.exception.MathArithmeticException;
import org.apache.commons.math4.legacy.exception.NotPositiveException;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.math4.legacy.core.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;

/**
 * Test case {@link MixtureMultivariateNormalDistribution}.
 */
public class MixtureMultivariateNormalDistributionTest {

    @Test
    public void testNonUnitWeightSum() {
        final double[] weights = { 1, 2 };
        final double[][] means = { { -1.5, 2.0 },
                                   { 4.0, 8.2 } };
        final double[][][] covariances = { { { 2.0, -1.1 },
                                             { -1.1, 2.0 } },
                                           { { 3.5, 1.5 },
                                             { 1.5, 3.5 } } };
        final MixtureMultivariateNormalDistribution d
            = new MixtureMultivariateNormalDistribution(weights, means, covariances);

        final List<Pair<Double, MultivariateNormalDistribution>> comp = d.getComponents();

        Assert.assertEquals(1d / 3, comp.get(0).getFirst().doubleValue(), Math.ulp(1d));
        Assert.assertEquals(2d / 3, comp.get(1).getFirst().doubleValue(), Math.ulp(1d));
    }

    @Test(expected=MathArithmeticException.class)
    public void testWeightSumOverFlow() {
        final double[] weights = { 0.5 * Double.MAX_VALUE, 0.51 * Double.MAX_VALUE };
        final double[][] means = { { -1.5, 2.0 },
                                   { 4.0, 8.2 } };
        final double[][][] covariances = { { { 2.0, -1.1 },
                                             { -1.1, 2.0 } },
                                           { { 3.5, 1.5 },
                                             { 1.5, 3.5 } } };
        new MixtureMultivariateNormalDistribution(weights, means, covariances);
    }

    @Test(expected=NotPositiveException.class)
    public void testPreconditionPositiveWeights() {
        final double[] negativeWeights = { -0.5, 1.5 };
        final double[][] means = { { -1.5, 2.0 },
                                   { 4.0, 8.2 } };
        final double[][][] covariances = { { { 2.0, -1.1 },
                                             { -1.1, 2.0 } },
                                           { { 3.5, 1.5 },
                                             { 1.5, 3.5 } } };
        new MixtureMultivariateNormalDistribution(negativeWeights, means, covariances);
    }

    /**
     * Test the accuracy of the density calculation.
     */
    @Test
    public void testDensities() {
        final double[] weights = { 0.3, 0.7 };
        final double[][] means = { { -1.5, 2.0 },
                                   { 4.0, 8.2 } };
        final double[][][] covariances = { { { 2.0, -1.1 },
                                             { -1.1, 2.0 } },
                                           { { 3.5, 1.5 },
                                             { 1.5, 3.5 } } };
        final MixtureMultivariateNormalDistribution d
            = new MixtureMultivariateNormalDistribution(weights, means, covariances);

        // Test vectors
        final double[][] testValues = { { -1.5, 2 },
                                        { 4, 8.2 },
                                        { 1.5, -2 },
                                        { 0, 0 } };

        // Densities that we should get back.
        // Calculated by assigning weights to multivariate normal distribution
        // and summing
        // values from dmvnorm function in R 2.15 CRAN package Mixtools v0.4.
        // Like: .3*dmvnorm(val,mu1,sigma1)+.7*dmvnorm(val,mu2,sigma2)
        final double[] correctDensities = { 0.02862037278930575,
                                            0.03523044847314091,
                                            0.000416241365629767,
                                            0.009932042831700297 };

        for (int i = 0; i < testValues.length; i++) {
            Assert.assertEquals(correctDensities[i], d.density(testValues[i]), Math.ulp(1d));
        }
    }

    /**
     * Test the accuracy of sampling from the distribution.
     */
    @Ignore@Test
    public void testSampling() {
        final double[] weights = { 0.3, 0.7 };
        final double[][] means = { { -1.5, 2.0 },
                                   { 4.0, 8.2 } };
        final double[][][] covariances = { { { 2.0, -1.1 },
                                             { -1.1, 2.0 } },
                                           { { 3.5, 1.5 },
                                             { 1.5, 3.5 } } };
        final MixtureMultivariateNormalDistribution d =
            new MixtureMultivariateNormalDistribution(weights, means, covariances);
        final MultivariateRealDistribution.Sampler sampler =
            d.createSampler(RandomSource.WELL_19937_C.create(50));

        final double[][] correctSamples = getCorrectSamples();
        final int n = correctSamples.length;
        final double[][] samples = AbstractMultivariateRealDistribution.sample(n, sampler);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < samples[i].length; j++) {
                Assert.assertEquals("sample[" + j + "]",
                                    correctSamples[i][j], samples[i][j], 1e-16);
            }
        }
    }

    /**
     * Values used in {@link #testSampling()}.
     */
    private double[][] getCorrectSamples() {
        // These were sampled from the MultivariateNormalMixtureModelDistribution class
        // with seed 50.
        //
        // They were then fit to a MVN mixture model in R using mixtools.
        //
        // The optimal parameters were:
        // - component weights: {0.3595186, 0.6404814}
        // - mean vectors: {-1.645879, 1.989797}, {3.474328, 7.782232}
        // - covariance matrices:
        //     { 1.397738 -1.167732
        //       -1.167732 1.801782 }
        //   and
        //     { 3.934593 2.354787
        //       2.354787 4.428024 }
        //
        // It is considered fairly close to the actual test parameters,
        // considering that the sample size is only 100.
        return new double[][] {
            { 6.259990922080121, 11.972954175355897 },
            { -2.5296544304801847, 1.0031292519854365 },
            { 0.49037886081440396, 0.9758251727325711 },
            { 5.022970993312015, 9.289348879616787 },
            { -1.686183146603914, 2.007244382745706 },
            { -1.4729253946002685, 2.762166644212484 },
            { 4.329788143963888, 11.514016497132253 },
            { 3.008674596114442, 4.960246550446107 },
            { 3.342379304090846, 5.937630105198625 },
            { 2.6993068328674754, 7.42190871572571 },
            { -2.446569340219571, 1.9687117791378763 },
            { 1.922417883170056, 4.917616702617099 },
            { -1.1969741543898518, 2.4576126277884387 },
            { 2.4216948702967196, 8.227710158117134 },
            { 6.701424725804463, 9.098666475042428 },
            { 2.9890253545698964, 9.643807939324331 },
            { 0.7162632354907799, 8.978811120287553 },
            { -2.7548699149775877, 4.1354812280794215 },
            { 8.304528180745018, 11.602319388898287 },
            { -2.7633253389165926, 2.786173883989795 },
            { 1.3322228389460813, 5.447481218602913 },
            { -1.8120096092851508, 1.605624499560037 },
            { 3.6546253437206504, 8.195304526564376 },
            { -2.312349539658588, 1.868941220444169 },
            { -1.882322136356522, 2.033795570464242 },
            { 4.562770714939441, 7.414967958885031 },
            { 4.731882017875329, 8.890676665580747 },
            { 3.492186010427425, 8.9005225241848 },
            { -1.619700190174894, 3.314060142479045 },
            { 3.5466090064003315, 7.75182101001913 },
            { 5.455682472787392, 8.143119287755635 },
            { -2.3859602945473197, 1.8826732217294837 },
            { 3.9095306088680015, 9.258129209626317 },
            { 7.443020189508173, 7.837840713329312 },
            { 2.136004873917428, 6.917636475958297 },
            { -1.7203379410395119, 2.3212878757611524 },
            { 4.618991257611526, 12.095065976419436 },
            { -0.4837044029854387, 0.8255970441255125 },
            { -4.438938966557163, 4.948666297280241 },
            { -0.4539625134045906, 4.700922454655341 },
            { 2.1285488271265356, 8.457941480487563 },
            { 3.4873561871454393, 11.99809827845933 },
            { 4.723049431412658, 7.813095742563365 },
            { 1.1245583037967455, 5.20587873556688 },
            { 1.3411933634409197, 6.069796875785409 },
            { 4.585119332463686, 7.967669543767418 },
            { 1.3076522817963823, -0.647431033653445 },
            { -1.4449446442803178, 1.9400424267464862 },
            { -2.069794456383682, 3.5824162107496544 },
            { -0.15959481421417276, 1.5466782303315405 },
            { -2.0823081278810136, 3.0914366458581437 },
            { 3.521944615248141, 10.276112932926408 },
            { 1.0164326704884257, 4.342329556442856 },
            { 5.3718868590295275, 8.374761158360922 },
            { 0.3673656866959396, 8.75168581694866 },
            { -2.250268955954753, 1.4610850300996527 },
            { -2.312739727403522, 1.5921126297576362 },
            { 3.138993360831055, 6.7338392374947365 },
            { 2.6978650950790115, 7.941857288979095 },
            { 4.387985088655384, 8.253499976968 },
            { -1.8928961721456705, 0.23631082388724223 },
            { 4.43509029544109, 8.565290285488782 },
            { 4.904728034106502, 5.79936660133754 },
            { -1.7640371853739507, 2.7343727594167433 },
            { 2.4553674733053463, 7.875871017408807 },
            { -2.6478965122565006, 4.465127753193949 },
            { 3.493873671142299, 10.443093773532448 },
            { 1.1321916197409103, 7.127108479263268 },
            { -1.7335075535240392, 2.550629648463023 },
            { -0.9772679734368084, 4.377196298969238 },
            { 3.6388366973980357, 6.947299283206256 },
            { 0.27043799318823325, 6.587978599614367 },
            { 5.356782352010253, 7.388957912116327 },
            { -0.09187745751354681, 0.23612399246659743 },
            { 2.903203580353435, 3.8076727621794415 },
            { 5.297014824937293, 8.650985262326508 },
            { 4.934508602170976, 9.164571423190052 },
            { -1.0004911869654256, 4.797064194444461 },
            { 6.782491700298046, 11.852373338280497 },
            { 2.8983678524536014, 8.303837362117521 },
            { 4.805003269830865, 6.790462904325329 },
            { -0.8815799740744226, 1.3015810062131394 },
            { 5.115138859802104, 6.376895810201089 },
            { 4.301239328205988, 8.60546337560793 },
            { 3.276423626317666, 9.889429652591947 },
            { -4.001924973153122, 4.3353864592328515 },
            { 3.9571892554119517, 4.500569057308562 },
            { 4.783067027436208, 7.451125480601317 },
            { 4.79065438272821, 9.614122776979698 },
            { 2.677655270279617, 6.8875223698210135 },
            { -1.3714746289327362, 2.3992153193382437 },
            { 3.240136859745249, 7.748339397522042 },
            { 5.107885374416291, 8.508324480583724 },
            { -1.5830830226666048, 0.9139127045208315 },
            { -1.1596156791652918, -0.04502759384531929 },
            { -0.4670021307952068, 3.6193633227841624 },
            { -0.7026065228267798, 0.4811423031997131 },
            { -2.719979836732917, 2.5165041618080104 },
            { 1.0336754331123372, -0.34966029029320644 },
            { 4.743217291882213, 5.750060115251131 }
        };
    }
}
