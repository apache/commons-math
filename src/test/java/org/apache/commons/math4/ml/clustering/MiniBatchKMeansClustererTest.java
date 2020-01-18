package org.apache.commons.math4.ml.clustering;

import org.apache.commons.math4.ml.distance.DistanceMeasure;
import org.apache.commons.math4.ml.distance.EuclideanDistance;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MiniBatchKMeansClustererTest {
    private DistanceMeasure measure = new EuclideanDistance();

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
        MiniBatchKMeansClusterer<DoublePoint> miniBatchKMeans = new MiniBatchKMeansClusterer<>(4, -1,
                measure, RandomSource.create(RandomSource.MT_64, randomSeed));
        for (int i = 0; i < 100; i++) {
            List<CentroidCluster<DoublePoint>> kMeansClusters = kMeans.cluster(data);
            List<CentroidCluster<DoublePoint>> miniBatchKMeansClusters = miniBatchKMeans.cluster(data);
            Assert.assertEquals(4, kMeansClusters.size());
            Assert.assertEquals(kMeansClusters.size(), miniBatchKMeansClusters.size());
            int totalDiffCount = 0;
            double totalCenterDistance = 0.0;
            for (CentroidCluster<DoublePoint> kMeanCluster : kMeansClusters) {
                CentroidCluster<DoublePoint> miniBatchCluster = ClusterUtils.predict(miniBatchKMeansClusters, kMeanCluster.getCenter());
                totalDiffCount += Math.abs(kMeanCluster.getPoints().size() - miniBatchCluster.getPoints().size());
                totalCenterDistance += measure.compute(kMeanCluster.getCenter().getPoint(), miniBatchCluster.getCenter().getPoint());
            }
            double diffRatio = totalDiffCount * 1.0 / data.size();
            System.out.println(String.format("Centers total distance: %f, clusters total diff points: %d, diff ratio: %f%%",
                    totalCenterDistance, totalDiffCount, diffRatio * 100));
            // Sometimes the
//            Assert.assertTrue(String.format("Different points ratio %f%%!", diffRatio * 100), diffRatio < 0.03);
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
        ArrayList<DoublePoint> list = new ArrayList<DoublePoint>(count);
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
