package org.apache.commons.math4.ml.clustering.initialization;

import org.apache.commons.math4.ml.clustering.CentroidCluster;
import org.apache.commons.math4.ml.clustering.DoublePoint;
import org.apache.commons.math4.ml.distance.EuclideanDistance;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CentroidInitializerTest {
    private void test_generate_appropriate_number_of_cluster(
            final CentroidInitializer initializer) {
        // Generate some data
        final List<DoublePoint> points = new ArrayList<>();
        final UniformRandomProvider rnd = RandomSource.create(RandomSource.MT_64);
        for (int i = 0; i < 500; i++) {
            double[] p = new double[2];
            p[0] = rnd.nextDouble();
            p[1] = rnd.nextDouble();
            points.add(new DoublePoint(p));
        }
        // We can only assert that the centroid initializer
        // implementation generate appropriate number of cluster
        for (int k = 1; k < 50; k++) {
            final List<CentroidCluster<DoublePoint>> centroidClusters =
                    initializer.selectCentroids(points, k);
            Assert.assertEquals(k, centroidClusters.size());
        }
    }

    @Test
    public void test_RandomCentroidInitializer() {
        final CentroidInitializer initializer =
                new RandomCentroidInitializer(RandomSource.create(RandomSource.MT_64));
        test_generate_appropriate_number_of_cluster(initializer);
    }

    @Test
    public void test_KMeanPlusPlusCentroidInitializer() {
        final CentroidInitializer initializer =
                new KMeansPlusPlusCentroidInitializer(new EuclideanDistance(),
                        RandomSource.create(RandomSource.MT_64));
        test_generate_appropriate_number_of_cluster(initializer);
    }
}
