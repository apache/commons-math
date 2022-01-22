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

package org.apache.commons.math4.legacy.ml.clustering;

import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;
import org.apache.commons.math4.legacy.ml.clustering.evaluation.CalinskiHarabasz;
import org.apache.commons.math4.legacy.ml.distance.DistanceMeasure;
import org.apache.commons.math4.legacy.ml.distance.EuclideanDistance;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class MiniBatchKMeansClustererTest {
    /**
     * Assert the illegal parameter throws proper Exceptions.
     */
    @Test
    public void testConstructorParameterChecks() {
        expectNumberIsTooSmallException(() -> new MiniBatchKMeansClusterer<>(1, -1, -1, 3, 300, 10, null, null, null));
        expectNumberIsTooSmallException(() -> new MiniBatchKMeansClusterer<>(1, -1, 100, -2, 300, 10, null, null, null));
        expectNumberIsTooSmallException(() -> new MiniBatchKMeansClusterer<>(1, -1, 100, 3, -300, 10, null, null, null));
        expectNumberIsTooSmallException(() -> new MiniBatchKMeansClusterer<>(1, -1, 100, 3, 300, -10, null, null, null));
    }

    /**
     * Expects block throws NumberIsTooSmallException.
     * @param block the block need to run.
     */
    private void expectNumberIsTooSmallException(Runnable block) {
        assertException(block, NumberIsTooSmallException.class);
    }

    /**
     * Compare the result to KMeansPlusPlusClusterer
     */
    @Test
    public void testCompareToKMeans() {
        //Generate 4 cluster
        final UniformRandomProvider rng = RandomSource.MT_64.create();
        List<DoublePoint> data = generateCircles(rng);
        KMeansPlusPlusClusterer<DoublePoint> kMeans =
            new KMeansPlusPlusClusterer<>(4, Integer.MAX_VALUE, DEFAULT_MEASURE, rng);
        MiniBatchKMeansClusterer<DoublePoint> miniBatchKMeans =
            new MiniBatchKMeansClusterer<>(4, Integer.MAX_VALUE, 100, 3, 300, 10, DEFAULT_MEASURE, rng,
                                           KMeansPlusPlusClusterer.EmptyClusterStrategy.LARGEST_VARIANCE);
        // Test 100 times between KMeansPlusPlusClusterer and MiniBatchKMeansClusterer
        for (int i = 0; i < 100; i++) {
            List<CentroidCluster<DoublePoint>> kMeansClusters = kMeans.cluster(data);
            List<CentroidCluster<DoublePoint>> miniBatchKMeansClusters = miniBatchKMeans.cluster(data);
            // Assert cluster result has proper clusters count.
            Assert.assertEquals(4, kMeansClusters.size());
            Assert.assertEquals(kMeansClusters.size(), miniBatchKMeansClusters.size());
            int totalDiffCount = 0;
            for (CentroidCluster<DoublePoint> kMeanCluster : kMeansClusters) {
                // Find out most similar cluster between two clusters, and summary the points count variances.
                CentroidCluster<DoublePoint> miniBatchCluster = predict(miniBatchKMeansClusters, kMeanCluster.getCenter());
                totalDiffCount += Math.abs(kMeanCluster.getPoints().size() - miniBatchCluster.getPoints().size());
            }
            // Statistic points different ratio.
            double diffPointsRatio = totalDiffCount * 1.0 / data.size();
            // Evaluator score different ratio by "CalinskiHarabasz" algorithm.
            ClusterEvaluator clusterEvaluator = new CalinskiHarabasz();
            double kMeansScore = clusterEvaluator.score(kMeansClusters);
            double miniBatchKMeansScore = clusterEvaluator.score(miniBatchKMeansClusters);
            double scoreDiffRatio = (kMeansScore - miniBatchKMeansScore) /
                    kMeansScore;
            // MiniBatchKMeansClusterer has few score differences between KMeansClusterer(less then 10%).
            Assert.assertTrue(String.format("Different score ratio %f%%!, diff points ratio: %f%%", scoreDiffRatio * 100, diffPointsRatio * 100),
                    scoreDiffRatio < 0.1);
        }
    }

    /**
     * Generate points around 4 circles.
     * @param rng RNG.
     * @return Generated points.
     */
    private List<DoublePoint> generateCircles(UniformRandomProvider random) {
        List<DoublePoint> data = new ArrayList<>();
        data.addAll(generateCircle(250, new double[]{-1.0, -1.0}, 1.0, random));
        data.addAll(generateCircle(260, new double[]{0.0, 0.0}, 0.7, random));
        data.addAll(generateCircle(270, new double[]{1.0, 1.0}, 0.7, random));
        data.addAll(generateCircle(280, new double[]{2.0, 2.0}, 0.7, random));
        return data;
    }

    /**
     * Generate points as circles.
     * @param count total points count.
     * @param center circle center point.
     * @param radius the circle radius points around.
     * @param random the Random source.
     * @return Generated points.
     */
    List<DoublePoint> generateCircle(int count, double[] center, double radius,
                                     UniformRandomProvider random) {
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

    /**
     * Assert there should be a exception.
     *
     * @param block          The code block need to assert.
     * @param exceptionClass A exception class.
     */
    public static void assertException(Runnable block, Class<? extends Throwable> exceptionClass) {
        try {
            block.run();
            Assert.fail(String.format("Expects %s", exceptionClass.getSimpleName()));
        } catch (Throwable e) {
            if (!exceptionClass.isInstance(e)) {
                throw e;
            }
        }
    }

    /**
     * Use EuclideanDistance as default DistanceMeasure
     */
    public static final DistanceMeasure DEFAULT_MEASURE = new EuclideanDistance();

    /**
     * Predict which cluster is best for the point
     *
     * @param clusters cluster to predict into
     * @param point    point to predict
     * @param measure  distance measurer
     * @param <T>      type of cluster point
     * @return the cluster which has nearest center to the point
     */
    public static <T extends Clusterable> CentroidCluster<T> predict(List<CentroidCluster<T>> clusters, Clusterable point, DistanceMeasure measure) {
        double minDistance = Double.POSITIVE_INFINITY;
        CentroidCluster<T> nearestCluster = null;
        for (CentroidCluster<T> cluster : clusters) {
            double distance = measure.compute(point.getPoint(), cluster.getCenter().getPoint());
            if (distance < minDistance) {
                minDistance = distance;
                nearestCluster = cluster;
            }
        }
        return nearestCluster;
    }

    /**
     * Predict which cluster is best for the point
     *
     * @param clusters cluster to predict into
     * @param point    point to predict
     * @param <T>      type of cluster point
     * @return the cluster which has nearest center to the point
     */
    public static <T extends Clusterable> CentroidCluster<T> predict(List<CentroidCluster<T>> clusters, Clusterable point) {
        return predict(clusters, point, DEFAULT_MEASURE);
    }
}
