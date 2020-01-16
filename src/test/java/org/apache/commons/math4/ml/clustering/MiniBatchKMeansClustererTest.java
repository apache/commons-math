package org.apache.commons.math4.ml.clustering;

import org.apache.commons.math4.linear.MatrixUtils;
import org.apache.commons.math4.linear.RealMatrix;
import org.apache.commons.math4.ml.distance.DistanceMeasure;
import org.apache.commons.math4.ml.distance.EuclideanDistance;
import org.apache.commons.math4.random.CorrelatedRandomVectorGenerator;
import org.apache.commons.math4.random.GaussianRandomGenerator;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MiniBatchKMeansClustererTest {
    private DistanceMeasure measure = new EuclideanDistance();

    @Test
    public void compareToKMeans() {
        //Generate 4 cluster
        List<DoublePoint> data = generateCircles();
        KMeansPlusPlusClusterer<DoublePoint> kMeans = new KMeansPlusPlusClusterer<>(4);
        MiniBatchKMeansClusterer<DoublePoint> miniBatchKMeans = new MiniBatchKMeansClusterer<>(4);
        List<CentroidCluster<DoublePoint>> kMeansClusters = kMeans.cluster(data);
        List<CentroidCluster<DoublePoint>> miniBatchKMeansClusters = miniBatchKMeans.cluster(data);
        Assert.assertEquals(4, kMeansClusters.size());
        Assert.assertEquals(kMeansClusters.size(), miniBatchKMeansClusters.size());
        int totalDiffCount = 0;
        double totalCenterDistance = 0.0;
        for (CentroidCluster<DoublePoint> kMeanCluster : kMeansClusters) {
            CentroidCluster<DoublePoint> miniBatchCluster = predict(kMeanCluster.getCenter(), miniBatchKMeansClusters);
            totalDiffCount += Math.abs(kMeanCluster.getPoints().size() - miniBatchCluster.getPoints().size());
            totalCenterDistance += measure.compute(kMeanCluster.getCenter().getPoint(), miniBatchCluster.getCenter().getPoint());
        }
        double diffRatio = totalDiffCount * 1.0 / data.size();
        System.out.println(String.format("Centers total distance: %f, clusters total diff points: %d, diff ratio: %f%%",
                totalCenterDistance, totalDiffCount, diffRatio * 100));
        // Difference ratio less than 2%
        Assert.assertTrue(String.format("Different points ratio %f%%!", diffRatio * 100), diffRatio < 0.02);
    }

    private <T extends Clusterable> CentroidCluster<T> predict(Clusterable point, List<CentroidCluster<T>> clusters) {
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

    private List<DoublePoint> generateClusters() {
        List<DoublePoint> data = new ArrayList<>();
        data.addAll(generateCluster(250, new double[]{-1.0, -1.0}, 0.5));
        data.addAll(generateCluster(250, new double[]{0.0, 0.0}, 0.5));
        data.addAll(generateCluster(250, new double[]{1.0, 1.0}, 0.5));
        data.addAll(generateCluster(250, new double[]{2.0, 2.0}, 0.5));
        return data;
    }

    private List<DoublePoint> generateCluster(int size, double[] center, double radius) {
        UniformRandomProvider rg = RandomSource.create(RandomSource.MT_64, 0);
        GaussianRandomGenerator rawGenerator = new GaussianRandomGenerator(rg);
        double[] standardDeviation = {0.5, 0.5};
        double c = standardDeviation[0] * standardDeviation[1] * radius;
        double[][] cov = {{standardDeviation[0] * standardDeviation[0], c}, {c, standardDeviation[1] * standardDeviation[1]}};
        RealMatrix covariance = MatrixUtils.createRealMatrix(cov);
        // Create a CorrelatedRandomVectorGenerator using "rawGenerator" for the components.
        CorrelatedRandomVectorGenerator generator =
                new CorrelatedRandomVectorGenerator(center, covariance, 1.0e-12 * covariance.getNorm(), rawGenerator);
        // Use the generator to generate correlated vectors.
        List<DoublePoint> data = new ArrayList<DoublePoint>(size);
        for (int i = 0; i < size; i++) {
            // Use the generator to generate vectors
            double[] randomVector = generator.nextVector();
            data.add(new DoublePoint(randomVector));
        }
        return data;
    }

    private List<DoublePoint> generateCircles() {
        List<DoublePoint> data = new ArrayList<>();
        Random random = new Random(0);
        data.addAll(generateCircle(250, new double[]{-1.0, -1.0}, 1.0, random));
        data.addAll(generateCircle(250, new double[]{0.0, 0.0}, 0.7, random));
        data.addAll(generateCircle(250, new double[]{1.0, 1.0}, 0.7, random));
        data.addAll(generateCircle(250, new double[]{2.0, 2.0}, 0.7, random));
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
