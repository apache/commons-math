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

import java.util.List;
import java.util.ArrayList;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.util.Pair;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test that demonstrates the use of {@link MixtureMultivariateRealDistribution}
 * in order to create a mixture model composed of {@link MultivariateNormalDistribution
 * normal distributions}.
 */
public class MultivariateNormalMixtureModelDistributionTest {

    @Test
    public void testNonUnitWeightSum() {
        final double[] weights = { 1, 2 };
        final double[][] means = { { -1.5, 2.0 },
                                   { 4.0, 8.2 } };
        final double[][][] covariances = { { { 2.0, -1.1 },
                                             { -1.1, 2.0 } },
                                           { { 3.5, 1.5 },
                                             { 1.5, 3.5 } } };
        final MultivariateNormalMixtureModelDistribution d
            = create(weights, means, covariances);

        final List<Pair<Double, MultivariateNormalDistribution>> comp = d.getComponents();

        Assert.assertEquals(1d / 3, comp.get(0).getFirst(), Math.ulp(1d));
        Assert.assertEquals(2d / 3, comp.get(1).getFirst(), Math.ulp(1d));
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
        final MultivariateNormalMixtureModelDistribution d
            = create(weights, means, covariances);
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
        final MultivariateNormalMixtureModelDistribution d
            = create(negativeWeights, means, covariances);
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
        final MultivariateNormalMixtureModelDistribution d
            = create(weights, means, covariances);

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
    @Test
    public void testSampling() {
        final double[] weights = { 0.3, 0.7 };
        final double[][] means = { { -1.5, 2.0 },
                                   { 4.0, 8.2 } };
        final double[][][] covariances = { { { 2.0, -1.1 },
                                             { -1.1, 2.0 } },
                                           { { 3.5, 1.5 },
                                             { 1.5, 3.5 } } };
        final MultivariateNormalMixtureModelDistribution d
            = create(weights, means, covariances);
        d.reseedRandomGenerator(50);

        final double[][] correctSamples = getCorrectSamples();
        final int n = correctSamples.length;
        final double[][] samples = d.sample(n);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < samples[i].length; j++) {
                Assert.assertEquals(correctSamples[i][j], samples[i][j], 1e-16);
            }
        }
    }

    /**
     * Creates a mixture of Gaussian distributions.
     *
     * @param weights Weights.
     * @param means Means.
     * @param covariances Covariances.
     * @return the mixture distribution.
     */
    private MultivariateNormalMixtureModelDistribution create(double[] weights,
                                                              double[][] means,
                                                              double[][][] covariances) {
        final List<Pair<Double, MultivariateNormalDistribution>> mvns
            = new ArrayList<Pair<Double, MultivariateNormalDistribution>>();

        for (int i = 0; i < weights.length; i++) {
            final MultivariateNormalDistribution dist
                = new MultivariateNormalDistribution(means[i], covariances[i]);
            mvns.add(new Pair<Double, MultivariateNormalDistribution>(weights[i], dist));
        }

        return new MultivariateNormalMixtureModelDistribution(mvns);
    }

    /**
     * Values used in {@link #testSampling()}.
     */
    private double[][] getCorrectSamples() {
        // These were sampled from the MultivariateNormalMixtureModel class
        // with seed 50. They were then fit to a MVN mixture model in R
        // using mixtools. The fitted parameters were:
        //
        // - component weights: {0.3730363, 0.6269637}
        // - mean vectors: {-1.760221, 2.080246}, {4.042673 8.239901}
        // - covariance matrices:
        // {2.343314 -1.161423
        // -1.161423 2.168186},
        // {4.538306 1.999180
        // 1.999180 3.707478}
        //
        // These parameters are quite close to the given test parameters,
        // considering that the sample size is only 100. This indicates that
        // we are sampling from the given distributions correctly.
        //
        // MVN mixture model fitting is tested more directly in
        // MultivariateNormalMixtureModelEMTest.java.
        return new double[][] {
            { 0.2583074099703975, 8.664499710373795 },
            { -4.813531184031044, 3.9231359989545105 },
            { -0.7419261132564453, 2.4007823469659315 },
            { 5.509216904195162, 9.422233433392439 },
            { -2.381049190850173, 4.943660886154495 },
            { -0.8993376528270461, 0.8938743025320182 },
            { 4.894770663858121, 12.166851725156436 },
            { 4.0897683028395075, 6.918843155044206 },
            { 4.719618558675267, 8.341707066796028 },
            { 5.0119792918378385, 8.718202270235704 },
            { -0.9575382403048307, 1.8229677546531289 },
            { 6.096419130934251, 10.015298483992137 },
            { -0.8680257551663279, 2.017226810559624 },
            { 1.5561509591914775, 6.428382522063888 },
            { 2.9573609580937585, 4.984112447026558 },
            { 2.9607883517629716, 5.040859511876173 },
            { 6.693223483958679, 8.293058555022974 },
            { -0.49520319305205684, 2.654804424012847 },
            { 2.5982446884625903, 4.403704372523807 },
            { -2.8126962615987825, 1.524225284837275 },
            { 4.617557399108285, 10.112045833514983 },
            { -1.0505336217175474, -0.25620619814406176 },
            { 6.468546093824413, 8.492764345471349 },
            { -1.070959919055369, -0.21025004020250515 },
            { 0.7096492923917137, 0.972470062045611 },
            { 5.398941052924385, 9.02323432480054 },
            { 4.0619884910872885, 3.9387544688324274 },
            { 3.060654105075946, 8.375879742835302 },
            { -1.1120837945401831, -0.5932624462175884 },
            { 1.7076086958821626, 10.076176310641726 },
            { 0.8025522475696087, 7.376934311816509 },
            { -1.7692026721062535, 3.342422431805849 },
            { 6.347756985325947, 9.473453725270733 },
            { 5.881105197046502, 7.489181251843213 },
            { 1.2360421752575719, 6.795186398153966 },
            { 0.4401756166827726, 1.2313491852695706 },
            { 2.3000936045037568, 8.287718300406317 },
            { -0.670230868276056, 2.1130200354211004 },
            { 0.3003945370882637, -1.5819884144799348 },
            { 7.277242408774614, 10.927490482503183 },
            { 7.923140481277026, 12.004508589648411 },
            { 5.522112916642092, 9.78447909183086 },
            { 1.7119808481577339, 6.085145192818114 },
            { 2.105629604263621, 10.1449475519281 },
            { 4.972245882289706, 9.190876138075419 },
            { 3.55810426807224, 7.654818193514658 },
            { -2.356177988964854, 2.532266732955841 },
            { -4.1832049468547385, 4.506619880263655 },
            { -3.830920610669877, 2.6251612200440366 },
            { -0.06307681885555017, 2.099426408596512 },
            { 0.36171488935238116, 0.6347956010120039 },
            { 4.151714086462804, 8.647657431234045 },
            { 0.7057392641959304, 6.692562958393579 },
            { -0.6550353398872, 7.692019868324891 },
            { 4.320276127581665, 7.202292152597195 },
            { -3.2303904191995527, 1.961730492225101 },
            { -2.954784139519494, 2.7707906587677087 },
            { 4.467020781304962, 11.98783401317258 },
            { 2.516614661485048, 5.7484664274488395 },
            { 0.5769047448998346, 5.631710672821962 },
            { 0.1160140182849414, 2.9315783896969196 },
            { 2.5049583778078652, 8.761243869910377 },
            { 5.709406025351957, 8.257826327804949 },
            { 0.21312967519917736, 3.5654341612096037 },
            { 3.6569574551599913, 5.771827106887553 },
            { -0.8798149482991884, 2.6978303136418855 },
            { 2.414118561546714, 4.53422981267982 },
            { 1.4898922645162216, 7.761630474438329 },
            { -2.541784545189355, 1.1738033253079712 },
            { -3.879227113624057, 4.4006851836938345 },
            { 6.661993934276366, 7.141516646050444 },
            { 3.181178356801352, 8.231063903955624 },
            { 1.8841679086668033, 7.8838333882950895 },
            { -1.0951561728318044, 2.4280383085698944 },
            { 5.35219559310147, 10.747404919992816 },
            { 6.960010647189781, 6.95945895094293 },
            { 8.66048279380016, 10.240215090544776 },
            { 5.322414316267673, 11.183073632332036 },
            { 4.286246289231014, 9.881220496364916 },
            { 3.1594608386471306, 7.472785192202438 },
            { 7.490325449520623, 9.327482647357861 },
            { -1.6450957033850353, 1.7263141708037384 },
            { 7.640267915158525, 9.040106401524124 },
            { 3.225359471647853, 7.698098961183428 },
            { 5.511639004578083, 9.186863380867411 },
            { -1.5570773470706916, 2.3113691458388006 },
            { 3.6254977322201625, 8.605119642693507 },
            { 6.825831664908241, 8.090893354857114 },
            { 4.614281342893763, 9.430714925646987 },
            { 3.244449074987682, 7.4974070774986705 },
            { -3.630882697488002, 2.0936948617188036 },
            { 4.668632614740622, 9.56561505235852 },
            { 3.291602850422323, 7.676202933382092 },
            { -5.17676202387167, 3.486280073084009 },
            { -0.8232665608471817, 1.035856623121346 },
            { -2.664598562579467, 4.969780569462966 },
            { -1.7320549381441652, 0.5626102308837451 },
            { -3.3030769989674384, 1.2611477988721695 },
            { -2.978115812942412, 3.0819389792053005 },
            { 4.061270197659891, 10.977521917377931 }
        };
    }
}

/**
 * Class that implements a mixture of Gaussian ditributions.
 */
class MultivariateNormalMixtureModelDistribution
    extends MixtureMultivariateRealDistribution<MultivariateNormalDistribution> {

    public MultivariateNormalMixtureModelDistribution(List<Pair<Double, MultivariateNormalDistribution>> components) {
        super(components);
    }
}
