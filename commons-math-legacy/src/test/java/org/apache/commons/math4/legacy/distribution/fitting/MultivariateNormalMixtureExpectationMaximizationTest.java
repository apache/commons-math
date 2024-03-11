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
package org.apache.commons.math4.legacy.distribution.fitting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math4.legacy.distribution.MixtureMultivariateNormalDistribution;
import org.apache.commons.math4.legacy.distribution.MultivariateNormalDistribution;
import org.apache.commons.math4.legacy.exception.ConvergenceException;
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.NotStrictlyPositiveException;
import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;
import org.apache.commons.math4.legacy.linear.Array2DRowRealMatrix;
import org.apache.commons.math4.legacy.linear.RealMatrix;
import org.apache.commons.math4.legacy.core.Pair;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test that demonstrates the use of
 * {@link MultivariateNormalMixtureExpectationMaximization}.
 */
public class MultivariateNormalMixtureExpectationMaximizationTest {

    @Test(expected = NotStrictlyPositiveException.class)
    public void testNonEmptyData() {
        // Should not accept empty data
        new MultivariateNormalMixtureExpectationMaximization(new double[][] {});
    }

    @Test(expected = DimensionMismatchException.class)
    public void testNonJaggedData() {
        // Reject data with nonconstant numbers of columns
        double[][] data = new double[][] {
                { 1, 2, 3 },
                { 4, 5, 6, 7 },
        };
        new MultivariateNormalMixtureExpectationMaximization(data);
    }

    @Test(expected = NumberIsTooSmallException.class)
    public void testMultipleColumnsRequired() {
        // Data should have at least 1 column
        double[][] data = new double[][] {
                {}, {}
        };
        new MultivariateNormalMixtureExpectationMaximization(data);
    }

    @Test(expected = NotStrictlyPositiveException.class)
    public void testMaxIterationsPositive() {
        // Maximum iterations for fit must be positive integer
        double[][] data = getTestSamples();
        MultivariateNormalMixtureExpectationMaximization fitter =
                new MultivariateNormalMixtureExpectationMaximization(data);

        MixtureMultivariateNormalDistribution
            initialMix = MultivariateNormalMixtureExpectationMaximization.estimate(data, 2);

        fitter.fit(initialMix, 0, 1E-5);
    }

    @Test(expected = NotStrictlyPositiveException.class)
    public void testThresholdPositive() {
        // Maximum iterations for fit must be positive
        double[][] data = getTestSamples();
        MultivariateNormalMixtureExpectationMaximization fitter =
                new MultivariateNormalMixtureExpectationMaximization(
                    data);

        MixtureMultivariateNormalDistribution
            initialMix = MultivariateNormalMixtureExpectationMaximization.estimate(data, 2);

        fitter.fit(initialMix, 1000, 0);
    }

    @Test(expected = ConvergenceException.class)
    public void testConvergenceException() {
        // ConvergenceException thrown if fit terminates before threshold met
        double[][] data = getTestSamples();
        MultivariateNormalMixtureExpectationMaximization fitter
            = new MultivariateNormalMixtureExpectationMaximization(data);

        MixtureMultivariateNormalDistribution
            initialMix = MultivariateNormalMixtureExpectationMaximization.estimate(data, 2);

        // 5 iterations not enough to meet convergence threshold
        fitter.fit(initialMix, 5, 1E-5);
    }

    @Test(expected = DimensionMismatchException.class)
    public void testIncompatibleInitialMixture() {
        // Data has 3 columns
        double[][] data = new double[][] {
                { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 }
        };
        double[] weights = new double[] { 0.5, 0.5 };

        // These distributions are compatible with 2-column data, not 3-column
        // data
        MultivariateNormalDistribution[] mvns = new MultivariateNormalDistribution[2];

        mvns[0] = new MultivariateNormalDistribution(new double[] {
                        -0.0021722935000328823, 3.5432892936887908 },
                        new double[][] {
                                { 4.537422569229048, 3.5266152281729304 },
                                { 3.5266152281729304, 6.175448814169779 } });
        mvns[1] = new MultivariateNormalDistribution(new double[] {
                        5.090902706507635, 8.68540656355283 }, new double[][] {
                        { 2.886778573963039, 1.5257474543463154 },
                        { 1.5257474543463154, 3.3794567673616918 } });

        // Create components and mixture
        List<Pair<Double, MultivariateNormalDistribution>> components =
                new ArrayList<>();
        components.add(new Pair<>(
                weights[0], mvns[0]));
        components.add(new Pair<>(
                weights[1], mvns[1]));

        MixtureMultivariateNormalDistribution badInitialMix
            = new MixtureMultivariateNormalDistribution(components);

        MultivariateNormalMixtureExpectationMaximization fitter
            = new MultivariateNormalMixtureExpectationMaximization(data);

        fitter.fit(badInitialMix);
    }

    @Test
    public void testInitialMixture() {
        // Testing initial mixture estimated from data
        final double[] correctWeights = new double[] { 0.5, 0.5 };

        final double[][] correctMeans = new double[][] {
            {-0.0021722935000328823, 3.5432892936887908},
            {5.090902706507635, 8.68540656355283},
        };

        final RealMatrix[] correctCovMats = new Array2DRowRealMatrix[2];

        correctCovMats[0] = new Array2DRowRealMatrix(new double[][] {
                { 4.537422569229048, 3.5266152281729304 },
                { 3.5266152281729304, 6.175448814169779 } });

        correctCovMats[1] = new Array2DRowRealMatrix( new double[][] {
                { 2.886778573963039, 1.5257474543463154 },
                { 1.5257474543463154, 3.3794567673616918 } });

        final MultivariateNormalDistribution[] correctMVNs = new
                MultivariateNormalDistribution[2];

        correctMVNs[0] = new MultivariateNormalDistribution(correctMeans[0],
                correctCovMats[0].getData());

        correctMVNs[1] = new MultivariateNormalDistribution(correctMeans[1],
                correctCovMats[1].getData());

        final MixtureMultivariateNormalDistribution initialMix
            = MultivariateNormalMixtureExpectationMaximization.estimate(getTestSamples(), 2);

        int i = 0;
        for (Pair<Double, MultivariateNormalDistribution> component : initialMix
                .getComponents()) {
            Assert.assertEquals(correctWeights[i], component.getFirst(),
                    Math.ulp(1d));

            final double[] means = component.getValue().getMeans();
            Assert.assertArrayEquals(correctMeans[i], means, 0.0);

            final RealMatrix covMat = component.getValue().getCovariances();
            Assert.assertEquals(correctCovMats[i], covMat);
            i++;
        }
    }

    @Test
    public void testFit2Dimensions2Components() {
        final double[][] data = getTestSamples();

        // Fit using the test samples using Matlab R2023b (Update 6):
        // GMModel = fitgmdist(X,2);

        // Expected results use the component order generated by the CM code for convenience
        // i.e. ComponentProportion from matlab is reversed: [0.703722, 0.296278]

        // NegativeLogLikelihood (CM code use the positive log-likehood divided by the number of observations)
        final double logLikelihood = -4.292430883324220e+02 / data.length;
        // ComponentProportion
        final double[] weights = new double[] {0.2962324189652912, 0.7037675810347089};
        // mu
        final double[][] means = new double[][]{
            {-1.421239458366293, 1.692604555824222},
            {4.213949861591596, 7.975974466776790}
        };
        // Sigma
        final double[][][] covar = new double[][][] {
            {{1.739441346307267, -0.586740858187563},
             {-0.586740858187563, 1.023420964341543}},
            {{4.243780645051973, 2.578176622652551},
             {2.578176622652551, 3.918302056479298}}
        };

        assertFit(data, 2, logLikelihood, weights, means, covar, 1e-3);
    }

    @Test
    public void testFit1Dimension2Components() {
        // Use only the first column of the test data
        final double[][] data = Arrays.stream(getTestSamples())
            .map(x -> new double[] {x[0]}).toArray(double[][]::new);

        // Fit the first column of test samples using Matlab R2023b (Update 6):
        // GMModel = fitgmdist(X,2);

        // NegativeLogLikelihood (CM code use the positive log-likehood divided by the number of observations)
        final double logLikelihood = -2.512197016873482e+02 / data.length;
        // ComponentProportion
        final double[] weights = new double[] {0.240510201974078, 0.759489798025922};
        // Since data has 1 dimension the means and covariances are single values
        // mu
        final double[][] means = new double[][]{
            {-1.736139126623031},
            {3.899886984922886}
        };
        // Sigma
        final double[][][] covar = new double[][][] {
            {{1.371327786710623}},
            {{5.254286022455004}}
        };

        assertFit(data, 2, logLikelihood, weights, means, covar, 0.05);
    }

    @Test
    public void testFit1Dimension1Component() {
        // Use only the first column of the test data
        final double[][] data = Arrays.stream(getTestSamples())
            .map(x -> new double[] {x[0]}).toArray(double[][]::new);

        // Fit the first column of test samples using Matlab R2023b (Update 6):
        // GMModel = fitgmdist(X,1);

        // NegativeLogLikelihood (CM code use the positive log-likehood divided by the number of observations)
        final double logLikelihood = -2.576329329354790e+02 / data.length;
        // ComponentProportion
        final double[] weights = new double[] {1.0};
        // Since data has 1 dimension the means and covariances are single values
        // mu
        final double[][] means = new double[][]{
            {2.544365206503801},
        };
        // Sigma
        final double[][][] covar = new double[][][] {
            {{10.122711799089901}},
        };

        assertFit(data, 1, logLikelihood, weights, means, covar, 1e-3);
    }

    private static void assertFit(double[][] data, int numComponents,
            double logLikelihood, double[] weights,
            double[][] means, double[][][] covar, double relError) {
        MultivariateNormalMixtureExpectationMaximization fitter
            = new MultivariateNormalMixtureExpectationMaximization(data);

        MixtureMultivariateNormalDistribution initialMix
            = MultivariateNormalMixtureExpectationMaximization.estimate(data, numComponents);
        fitter.fit(initialMix);
        MixtureMultivariateNormalDistribution fittedMix = fitter.getFittedModel();
        List<Pair<Double, MultivariateNormalDistribution>> components = fittedMix.getComponents();

        Assert.assertEquals(logLikelihood,
            fitter.getLogLikelihood(),
            Math.abs(logLikelihood) * relError);

        int i = 0;
        for (Pair<Double, MultivariateNormalDistribution> component : components) {
            final double weight = component.getFirst();
            final MultivariateNormalDistribution mvn = component.getSecond();
            Assert.assertEquals(weights[i], weight, weights[i] * relError);
            assertArrayEquals(means[i], mvn.getMeans(), relError);
            final double[][] c = mvn.getCovariances().getData();
            Assert.assertEquals(covar[i].length, c.length);
            for (int j = 0; j < covar[i].length; j++) {
                assertArrayEquals(covar[i][j], c[j], relError);
            }
            i++;
        }
    }

    private static void assertArrayEquals(double[] e, double[] a, double relError) {
        Assert.assertEquals("length", e.length, a.length);
        for (int i = 0; i < e.length; i++) {
            Assert.assertEquals(e[i], a[i], Math.abs(e[i]) * relError);
        }
    }

    private double[][] getTestSamples() {
        // generated using R Mixtools rmvnorm with mean vectors [-1.5, 2] and
        // [4, 8.2]
        return new double[][] { { 7.358553610469948, 11.31260831446758 },
                { 7.175770420124739, 8.988812210204454 },
                { 4.324151905768422, 6.837727899051482 },
                { 2.157832219173036, 6.317444585521968 },
                { -1.890157421896651, 1.74271202875498 },
                { 0.8922409354455803, 1.999119343923781 },
                { 3.396949764787055, 6.813170372579068 },
                { -2.057498232686068, -0.002522983830852255 },
                { 6.359932157365045, 8.343600029975851 },
                { 3.353102234276168, 7.087541882898689 },
                { -1.763877221595639, 0.9688890460330644 },
                { 6.151457185125111, 9.075011757431174 },
                { 4.281597398048899, 5.953270070976117 },
                { 3.549576703974894, 8.616038155992861 },
                { 6.004706732349854, 8.959423391087469 },
                { 2.802915014676262, 6.285676742173564 },
                { -0.6029879029880616, 1.083332958357485 },
                { 3.631827105398369, 6.743428504049444 },
                { 6.161125014007315, 9.60920569689001 },
                { -1.049582894255342, 0.2020017892080281 },
                { 3.910573022688315, 8.19609909534937 },
                { 8.180454017634863, 7.861055769719962 },
                { 1.488945440439716, 8.02699903761247 },
                { 4.813750847823778, 12.34416881332515 },
                { 0.0443208501259158, 5.901148093240691 },
                { 4.416417235068346, 4.465243084006094 },
                { 4.0002433603072, 6.721937850166174 },
                { 3.190113818788205, 10.51648348411058 },
                { 4.493600914967883, 7.938224231022314 },
                { -3.675669533266189, 4.472845076673303 },
                { 6.648645511703989, 12.03544085965724 },
                { -1.330031331404445, 1.33931042964811 },
                { -3.812111460708707, 2.50534195568356 },
                { 5.669339356648331, 6.214488981177026 },
                { 1.006596727153816, 1.51165463112716 },
                { 5.039466365033024, 7.476532610478689 },
                { 4.349091929968925, 7.446356406259756 },
                { -1.220289665119069, 3.403926955951437 },
                { 5.553003979122395, 6.886518211202239 },
                { 2.274487732222856, 7.009541508533196 },
                { 4.147567059965864, 7.34025244349202 },
                { 4.083882618965819, 6.362852861075623 },
                { 2.203122344647599, 7.260295257904624 },
                { -2.147497550770442, 1.262293431529498 },
                { 2.473700950426512, 6.558900135505638 },
                { 8.267081298847554, 12.10214104577748 },
                { 6.91977329776865, 9.91998488301285 },
                { 0.1680479852730894, 6.28286034168897 },
                { -1.268578659195158, 2.326711221485755 },
                { 1.829966451374701, 6.254187605304518 },
                { 5.648849025754848, 9.330002040750291 },
                { -2.302874793257666, 3.585545172776065 },
                { -2.629218791709046, 2.156215538500288 },
                { 4.036618140700114, 10.2962785719958 },
                { 0.4616386422783874, 0.6782756325806778 },
                { -0.3447896073408363, 0.4999834691645118 },
                { -0.475281453118318, 1.931470384180492 },
                { 2.382509690609731, 6.071782429815853 },
                { -3.203934441889096, 2.572079552602468 },
                { 8.465636032165087, 13.96462998683518 },
                { 2.36755660870416, 5.7844595007273 },
                { 0.5935496528993371, 1.374615871358943 },
                { -2.467481505748694, 2.097224634713005 },
                { 4.27867444328542, 10.24772361238549 },
                { -2.013791907543137, 2.013799426047639 },
                { 6.424588084404173, 9.185334939684516 },
                { -0.8448238876802175, 0.5447382022282812 },
                { 1.342955703473923, 8.645456317633556 },
                { 3.108712208751979, 8.512156853800064 },
                { 4.343205178315472, 8.056869549234374 },
                { -2.971767642212396, 3.201180146824761 },
                { 2.583820931523672, 5.459873414473854 },
                { 4.209139115268925, 8.171098193546225 },
                { 0.4064909057902746, 1.454390775518743 },
                { 3.068642411145223, 6.959485153620035 },
                { 6.085968972900461, 7.391429799500965 },
                { -1.342265795764202, 1.454550012997143 },
                { 6.249773274516883, 6.290269880772023 },
                { 4.986225847822566, 7.75266344868907 },
                { 7.642443254378944, 10.19914817500263 },
                { 6.438181159163673, 8.464396764810347 },
                { 2.520859761025108, 7.68222425260111 },
                { 2.883699944257541, 6.777960331348503 },
                { 2.788004550956599, 6.634735386652733 },
                { 3.331661231995638, 5.794191300046592 },
                { 3.526172276645504, 6.710802266815884 },
                { 3.188298528138741, 10.34495528210205 },
                { 0.7345539486114623, 5.807604004180681 },
                { 1.165044595880125, 7.830121829295257 },
                { 7.146962523500671, 11.62995162065415 },
                { 7.813872137162087, 10.62827008714735 },
                { 3.118099164870063, 8.286003148186371 },
                { -1.708739286262571, 1.561026755374264 },
                { 1.786163047580084, 4.172394388214604 },
                { 3.718506403232386, 7.807752990130349 },
                { 6.167414046828899, 10.01104941031293 },
                { -1.063477247689196, 1.61176085846339 },
                { -3.396739609433642, 0.7127911050002151 },
                { 2.438885945896797, 7.353011138689225 },
                { -0.2073204144780931, 0.850771146627012 }, };
    }
}
