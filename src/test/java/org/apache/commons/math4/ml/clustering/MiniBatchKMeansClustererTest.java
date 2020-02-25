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

package org.apache.commons.math4.ml.clustering;

import org.apache.commons.math4.TestUtils;
import org.apache.commons.math4.exception.NumberIsTooSmallException;
import org.apache.commons.math4.ml.clustering.evaluation.ClusterEvaluator;
import org.apache.commons.math4.ml.clustering.evaluation.SumOfClusterVariances;
import org.apache.commons.math4.ml.distance.DistanceMeasure;
import org.apache.commons.math4.ml.distance.EuclideanDistance;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MiniBatchKMeansClustererTest {
    private final DistanceMeasure measure = new EuclideanDistance();

    @Test
    public void testConstructorParameterChecks() {
        expectNumberIsTooSmallException(() -> new MiniBatchKMeansClusterer<>(0, -1, 100, 3, 300, 10, null, null, null));
        expectNumberIsTooSmallException(() -> new MiniBatchKMeansClusterer<>(1, -1, -1, 3, 300, 10, null, null, null));
        expectNumberIsTooSmallException(() -> new MiniBatchKMeansClusterer<>(1, -1, 100, -2, 300, 10, null, null, null));
        expectNumberIsTooSmallException(() -> new MiniBatchKMeansClusterer<>(1, -1, 100, 3, -300, 10, null, null, null));
        expectNumberIsTooSmallException(() -> new MiniBatchKMeansClusterer<>(1, -1, 100, 3, 300, -10, null, null, null));
    }

    private void expectNumberIsTooSmallException(Runnable block) {
        TestUtils.assertException(block, NumberIsTooSmallException.class);
    }

    /**
     * Compare the result to KMeansPlusPlusClusterer
     */
    @Test
    public void testCompareToKMeans() {
        //Generate 4 cluster
        int randomSeed = 0;
        List<DoublePoint> data = generateCircles(randomSeed);
        KMeansPlusPlusClusterer<DoublePoint> kMeans = new KMeansPlusPlusClusterer<>(4, -1, measure,
                RandomSource.create(RandomSource.MT_64, randomSeed));
        MiniBatchKMeansClusterer<DoublePoint> miniBatchKMeans = new MiniBatchKMeansClusterer<>(4, 100, 100,
                RandomSource.create(RandomSource.MT_64, randomSeed));
        for (int i = 0; i < 100; i++) {
            List<CentroidCluster<DoublePoint>> kMeansClusters = kMeans.cluster(data);
            List<CentroidCluster<DoublePoint>> miniBatchKMeansClusters = miniBatchKMeans.cluster(data);
            Assert.assertEquals(4, kMeansClusters.size());
            Assert.assertEquals(kMeansClusters.size(), miniBatchKMeansClusters.size());
            int totalDiffCount = 0;
            for (CentroidCluster<DoublePoint> kMeanCluster : kMeansClusters) {
                CentroidCluster<DoublePoint> miniBatchCluster = ClusterUtils.predict(miniBatchKMeansClusters, kMeanCluster.getCenter());
                totalDiffCount += Math.abs(kMeanCluster.getPoints().size() - miniBatchCluster.getPoints().size());
            }
            ClusterEvaluator<DoublePoint> clusterEvaluator = new SumOfClusterVariances<>(measure);
            double kMeansScore = clusterEvaluator.score(kMeansClusters);
            double miniBatchKMeansScore = clusterEvaluator.score(miniBatchKMeansClusters);
            double diffPointsRatio = totalDiffCount * 1.0 / data.size();
            double scoreDiffRatio = (miniBatchKMeansScore - kMeansScore) /
                    kMeansScore;
            // MiniBatchKMeansClusterer has few score differences between KMeansClusterer
            Assert.assertTrue(String.format("Different score ratio %f%%!, diff points ratio: %f%%\"", scoreDiffRatio * 100, diffPointsRatio * 100),
                    scoreDiffRatio < 0.1);
        }
    }

    private List<DoublePoint> generateCircles(int randomSeed) {
        List<DoublePoint> data = new ArrayList<>();
        Random random = new Random(randomSeed);
        data.addAll(generateCircle(250, new double[]{-1.0, -1.0}, 1.0, random));
        data.addAll(generateCircle(260, new double[]{0.0, 0.0}, 0.7, random));
        data.addAll(generateCircle(270, new double[]{1.0, 1.0}, 0.7, random));
        data.addAll(generateCircle(280, new double[]{2.0, 2.0}, 0.7, random));
        return data;
    }

    List<DoublePoint> generateCircle(int count, double[] center, double radius, Random random) {
        double x0 = center[0];
        double y0 = center[1];
        ArrayList<DoublePoint> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            double ao = random.nextDouble() * 720 - 360;
            double r = random.nextDouble() * radius * 2 - radius;
            double x1 = x0 + r * Math.cos(ao * Math.PI / 180);
            double y1 = y0 + r * Math.sin(ao * Math.PI / 180);
            list.add(new DoublePoint(new double[]{x1, y1}));
        }
        return list;
    }

}
