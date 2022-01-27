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

package org.apache.commons.math4.legacy.ml.clustering.evaluation;

import org.apache.commons.math4.legacy.ml.clustering.CentroidCluster;
import org.apache.commons.math4.legacy.ml.clustering.ClusterEvaluator;
import org.apache.commons.math4.legacy.ml.clustering.DoublePoint;
import org.apache.commons.math4.legacy.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math4.legacy.ml.distance.DistanceMeasure;
import org.apache.commons.math4.legacy.ml.distance.EuclideanDistance;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CalinskiHarabaszTest {
    private ClusterEvaluator evaluator;
    private DistanceMeasure distanceMeasure;

    @Before
    public void setUp() {
        evaluator = new CalinskiHarabasz();
        distanceMeasure = new EuclideanDistance();
    }

    @Test
    public void test_k_equals_4_is_best_for_a_4_center_points() {
        final int dimension = 2;
        final double[][] centers = {{-1, -1}, {0, 0}, {1, 1}, {2, 2}};
        final UniformRandomProvider rnd = RandomSource.MT_64.create();
        final List<DoublePoint> points = new ArrayList<>();
        // Generate 1000 points around 4 centers for test.
        for (int i = 0; i < 1000; i++) {
            double[] center = centers[i % centers.length];
            double[] point = new double[dimension];
            for (int j = 0; j < dimension; j++) {
                double offset = (rnd.nextDouble() - 0.5) / 2;
                Assert.assertTrue(offset < 0.25 && offset > -0.25);
                point[j] = offset + center[j];
            }
            points.add(new DoublePoint(point));
        }
        double expectBestScore = 0.0;
        double actualBestScore = 0.0;
        for (int i = 0; i < 5; i++) {
            final int k = i + 2;
            KMeansPlusPlusClusterer<DoublePoint> kMeans = new KMeansPlusPlusClusterer<>(k, Integer.MAX_VALUE, distanceMeasure, rnd);
            List<CentroidCluster<DoublePoint>> clusters = kMeans.cluster(points);
            double score = evaluator.score(clusters);
            if (score > expectBestScore) {
                expectBestScore = score;
            }
            if (k == centers.length) {
                actualBestScore = score;
            }
        }

        // k=4 get the highest score
        Assert.assertEquals(expectBestScore, actualBestScore, 0.0);
    }

    @Test
    public void test_compare_to_skLearn() {
        final UniformRandomProvider rnd = RandomSource.MT_64.create();
        final List<DoublePoint> points = new ArrayList<>();
        for (double[] p : dataFromSkLearn) {
            points.add(new DoublePoint(p));
        }
        double expectBestScore = 0.0;
        double actualBestScore = 0.0;
        for (int i = 0; i < 5; i++) {
            final int k = i + 2;
            KMeansPlusPlusClusterer<DoublePoint> kMeans = new KMeansPlusPlusClusterer<>(k, Integer.MAX_VALUE, distanceMeasure, rnd);
            List<CentroidCluster<DoublePoint>> clusters = kMeans.cluster(points);
            double score = evaluator.score(clusters);
            if (score > expectBestScore) {
                expectBestScore = score;
            }

            // The score is approximately equals sklearn's score when k is smaller or equals to best k.
            if (k <= kFromSkLearn) {
                actualBestScore = score;
                final double relScore = score / scoreFromSkLearn[i];
                Assert.assertEquals(1, relScore, 2e-2);
            }
        }

        // k=4 get the highest score
        Assert.assertEquals(expectBestScore, actualBestScore, 0.0);
    }

    static final int kFromSkLearn = 4;
    static final double[] scoreFromSkLearn = {
        622.487247165719, 597.7763150683217, 1157.7901325495295, 1136.8201767857847, 1092.708039201163
    };
    static final double[][] dataFromSkLearn = {
            {1.403414, 1.148639}, {0.203959, 0.172137}, {2.132351, 1.883029}, {0.176704, -0.106040},
            {-0.729892, -0.987217}, {2.073591, 1.891133}, {-0.632742, -0.847796}, {-0.080353, 0.388064},
            {1.293772, 0.999236}, {-0.478476, -0.444240}, {1.154994, 0.922124}, {0.213056, 0.247446},
            {1.246047, 1.329821}, {2.010432, 1.939522}, {-0.249074, 0.060909}, {1.960038, 1.883771},
            {0.068528, -0.119460}, {1.035851, 0.992598}, {2.206471, 2.040334}, {2.114869, 2.186366},
            {0.192118, 0.042242}, {0.194172, 0.230945}, {1.969581, 2.118761}, {1.211497, 0.803267},
            {0.852534, 1.171513}, {2.032709, 2.068391}, {0.862354, 1.096274}, {-1.151345, -1.192454},
            {2.642026, 1.905175}, {-1.009092, -1.383999}, {1.123967, 0.799541}, {2.452222, 2.079981},
            {0.665412, 0.829890}, {2.145178, 1.991171}, {-1.186327, -1.110976}, {2.009537, 1.683832},
            {1.900143, 2.059320}, {1.217072, 1.073173}, {-0.011930, 0.182649}, {-1.255492, -0.670092},
            {0.221479, -0.239351}, {-0.155211, -0.129519}, {0.076976, 0.070879}, {2.340748, 1.728946},
            {-0.785182, -1.003191}, {-0.048162, 0.054161}, {-0.590787, -1.261207}, {-0.322545, -1.678934},
            {1.721805, 2.019360}, {-0.055982, 0.406160}, {1.786591, 2.030543}, {2.319241, 1.662943},
            {-0.037710, 0.140065}, {1.255095, 1.042194}, {1.111086, 1.165950}, {-0.218115, -0.034970},
            {2.187137, 1.692329}, {1.316916, 1.077612}, {0.112255, 0.047945}, {0.739778, 0.945151},
            {-0.452803, -0.989958}, {2.105973, 2.005392}, {-1.090926, -0.892274}, {-0.016388, -0.243725},
            {1.069622, 0.746740}, {2.071495, 1.707953}, {-0.734458, -0.700208}, {-0.793453, -1.142096},
            {0.279182, 0.216376}, {-1.280766, -1.789708}, {-0.547815, -0.583041}, {1.320526, 1.312906},
            {-0.881327, -0.716999}, {0.779240, 0.887246}, {1.925328, 1.547436}, {-0.024202, -0.206561},
            {2.320019, 2.209286}, {-0.265125, 0.187406}, {-0.841028, -0.336119}, {-1.158193, -0.486245},
            {2.107928, 2.027572}, {-0.203312, -0.058400}, {1.746752, 1.692956}, {-0.943192, -1.661465},
            {-0.692261, -1.359602}, {1.189437, 1.239394}, {2.122793, 1.946352}, {0.808161, 1.145078},
            {-0.214102, -0.254642}, {1.964497, 1.659230}, {0.162827, -0.203977}, {-1.197499, -1.150439},
            {0.893478, 1.187206}, {2.268571, 1.937285}, {1.874589, 1.792590}, {2.115534, 2.148600},
            {0.971884, 0.741704}, {-2.068844, -1.365312}, {1.923238, 2.135497}, {0.943657, 1.303986},
            {2.059181, 1.866467}, {-1.150325, -1.369225}, {-0.090138, 0.186226}, {-0.361086, 0.086080},
            {0.781402, 0.552706}, {1.788317, 2.180373}, {0.798725, 1.200775}, {-1.054850, -0.480968},
            {-0.161374, 0.263608}, {1.261640, 0.869688}, {0.924957, 1.192590}, {1.094182, 1.031706},
            {1.622207, 1.731404}, {-2.117348, -1.090460}, {1.005802, 1.040883}, {2.015137, 1.958903},
            {-0.248881, 0.187862}, {1.890444, 2.059389}, {1.074242, 0.875771}, {2.004657, 1.895254},
            {0.854140, 0.811218}, {-0.798992, -1.633529}, {0.311872, -0.109260}, {-0.219108, 0.480269},
            {1.138654, 1.324903}, {-2.062293, -1.023073}, {0.141443, -0.087330}, {-0.745644, -0.303953},
            {0.763012, 0.793850}, {0.975160, 0.969506}, {-1.262475, -1.264683}, {-0.934801, -0.516551},
            {-1.342065, -0.999911}, {-0.113459, 0.213991}, {2.359609, 1.856216}, {0.408595, 0.377997},
            {-0.382908, -1.360288}, {1.873100, 1.984283}, {-0.158167, 0.128779}, {1.001959, 0.842014},
            {2.073056, 1.993139}, {-0.916489, -0.868636}, {1.350903, 1.159256}, {-0.999557, -1.115818},
            {1.699934, 2.255168}, {-0.451647, 0.135991}, {1.761330, 2.091668}, {0.158764, -0.052111},
            {0.948387, 0.928156}, {-1.723536, -0.864100}, {1.791458, 2.053596}, {0.765689, 1.028344},
            {2.232360, 1.956492}, {-0.270874, -0.827692}, {0.702813, 0.784622}, {-0.205446, -0.314226},
            {0.817023, 0.835158}, {-1.484335, -1.201362}, {1.875541, 1.974222}, {1.096270, 0.543190},
            {-1.096272, -1.259179}, {-0.985800, -0.660712}, {0.095980, 0.012351}, {0.905097, 0.998787},
            {2.087597, 1.879789}, {-0.146487, 0.088045}, {-1.606932, -1.196349}, {1.168532, 0.837345},
            {2.119787, 2.128731}, {-0.115728, 0.016410}, {1.049650, 1.258826}, {-0.207201, -0.026785},
            {-0.119676, 0.024613}, {-0.167932, -0.295941}, {-0.233100, -1.060121}, {1.379617, 1.104958},
            {-0.097467, 0.075053}, {-1.153246, -0.956188}, {-0.159732, -0.364957}, {0.184015, 0.210984},
            {-1.446427, -1.005153}, {1.970006, 2.084909}, {1.443284, 1.450596}, {1.133778, 1.024311},
            {2.236527, 2.063874}, {0.167056, -0.170384}, {0.108058, 0.061813}, {-0.630086, -0.981357},
            {-1.262581, -1.022503}, {0.993000, 1.033955}, {1.939089, 2.116008}, {0.888129, 1.150939},
            {-1.033035, -0.017927}, {-1.067896, -0.033157}, {2.082978, 2.321452}, {0.975302, 0.964340},
            {-1.199290, -1.836711}, {-1.199961, -0.825432}, {0.084522, 0.199842}, {0.129213, 0.052383}
    };
}
