package org.apache.commons.math4.ml.clustering;

import org.apache.commons.math4.PerfTestUtils;
import org.apache.commons.math4.exception.NullArgumentException;
import org.apache.commons.math4.exception.NumberIsTooSmallException;
import org.apache.commons.math4.random.UncorrelatedRandomVectorGenerator;
import org.apache.commons.math4.random.UniformRandomGenerator;
import org.apache.commons.math4.rng.RandomSource;
import org.apache.commons.math4.stat.descriptive.moment.VectorialMean;
import org.apache.commons.math4.util.MathArrays;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ElkanKmeansPlusPlusClustererTest {


    static public final class KmeansRunTest extends PerfTestUtils.RunTest {

        private final Clusterer<DoublePoint> clusterer;

        private final List<DoublePoint> points;

        /**
         * @param name Test name.
         */
        public KmeansRunTest(String name, Clusterer<DoublePoint> clusterer, List<DoublePoint> points) {
            super(name);
            this.clusterer = clusterer;
            this.points = points;
        }

        @Override
        public Double call() throws Exception {
            clusterer.cluster(points);
            return 0.0;
        }
    }

    @Test
    public void validateOneDimensionSingleClusterZeroMean() {
        final List<DoublePoint> testPoints = Arrays.asList(new DoublePoint(new double[]{1}), new DoublePoint(new double[]{2}), new DoublePoint(new double[]{-3}));
        final ElkanKmeansPlusPlusClusterer<DoublePoint> clusterer = new ElkanKmeansPlusPlusClusterer<>(1);
        final List<CentroidCluster<DoublePoint>> clusters = clusterer.cluster(testPoints);
        Assert.assertEquals(1, clusters.size());
        Assert.assertTrue(MathArrays.equals(new double[]{0}, clusters.get(0).getCenter().getPoint()));
    }

    @Test(expected = NumberIsTooSmallException.class)
    public void illegalKParameter() {
        final int N = 20;
        final int d = 3;
        final int k = 100;

        final List<DoublePoint> testPoints = generatePoints(N, d);
        final ElkanKmeansPlusPlusClusterer<DoublePoint> clusterer = new ElkanKmeansPlusPlusClusterer<>(k);
        clusterer.cluster(testPoints);
    }

    @Test
    public void numberOfClustersSameAsInputSize() {
        final int N = 3;
        final int d = 2;
        final int k = 3;

        final List<DoublePoint> testPoints = generatePoints(N, d);
        final ElkanKmeansPlusPlusClusterer<DoublePoint> clusterer = new ElkanKmeansPlusPlusClusterer<>(k);
        final List<CentroidCluster<DoublePoint>> clusters = clusterer.cluster(testPoints);
        Assert.assertEquals(k, clusters.size());
        Assert.assertEquals(1, clusters.get(0).getPoints().size());
        Assert.assertEquals(1, clusters.get(1).getPoints().size());
        Assert.assertEquals(1, clusters.get(2).getPoints().size());
    }

    @Test(expected = NullArgumentException.class)
    public void illegalInputParameter() {
        final ElkanKmeansPlusPlusClusterer<DoublePoint> clusterer = new ElkanKmeansPlusPlusClusterer<>(10);
        clusterer.cluster(null);
    }

    @Test(expected = NumberIsTooSmallException.class)
    public void emptyInputPointsList() {
        final ElkanKmeansPlusPlusClusterer<DoublePoint> clusterer = new ElkanKmeansPlusPlusClusterer<>(10);
        clusterer.cluster(Collections.<DoublePoint>emptyList());
    }

    @Test(expected = IllegalStateException.class)
    public void negativeKParameterValue() {
        new ElkanKmeansPlusPlusClusterer<>(-1);
    }

    @Test(expected = IllegalStateException.class)
    public void kParameterEqualsZero() {
        new ElkanKmeansPlusPlusClusterer<>(0);
    }

    @Test
    public void oneClusterCenterShouldBeTheMean() {
        final int N = 100;
        final int d = 2;

        final List<DoublePoint> testPoints = generatePoints(N, d);
        final KMeansPlusPlusClusterer<DoublePoint> clusterer = new KMeansPlusPlusClusterer<>(1);

        final List<CentroidCluster<DoublePoint>> clusters = clusterer.cluster(testPoints);

        VectorialMean mean = new VectorialMean(d);
        for (DoublePoint each : testPoints) {
            mean.increment(each.getPoint());
        }
        Assert.assertEquals(1, clusters.size());
        Assert.assertArrayEquals(mean.getResult(), clusters.get(0).getCenter().getPoint(), 1e-6);
    }

    /**
     * Utility function to generate list of random uncorrelated points to cluster.
     * @param n - number of points
     * @param d - dimensionality
     * @return - list of n generated random vectors of dimension d.
     */
    static private List<DoublePoint> generatePoints(int n, int d) {
        List<DoublePoint> results = new ArrayList<>();
        final UncorrelatedRandomVectorGenerator rnd = new UncorrelatedRandomVectorGenerator(d, new UniformRandomGenerator(RandomSource.create(RandomSource.MT_64)));

        for (int i = 0; i < n; i++) {
            results.add(new DoublePoint(rnd.nextVector()));
        }

        return results;
    }

    /**
     * To see comparision of the two kmeans implementations.
     * @param args
     */
    public static void main(String[] args) {
        int N = 100_000;
        final List<DoublePoint> points_d5 = generatePoints(N, 5);
        final List<DoublePoint> points_d10 = generatePoints(N, 10);
        final List<DoublePoint> points_d20 = generatePoints(N, 20);

        PerfTestUtils.timeAndReport("kmeans comparison", 100, 3, 3, false,
                new PerfTestUtils.RunTest[]{
                        new KmeansRunTest("elkan kmeans++, k=20, d=5", new ElkanKmeansPlusPlusClusterer<DoublePoint>(20), points_d5),
                        new KmeansRunTest("elkan kmeans++, k=20, d=10", new ElkanKmeansPlusPlusClusterer<DoublePoint>(20), points_d10),
                        new KmeansRunTest("elkan kmeans++, k=20, d=20", new ElkanKmeansPlusPlusClusterer<DoublePoint>(20), points_d20),
                        new KmeansRunTest("kmeans++, k=20, d=5", new KMeansPlusPlusClusterer<DoublePoint>(20), points_d5),
                        new KmeansRunTest("kmeans++, k=20, d=10", new KMeansPlusPlusClusterer<DoublePoint>(20), points_d10),
                        new KmeansRunTest("kmeans++, k=20, d=20", new KMeansPlusPlusClusterer<DoublePoint>(20), points_d20),
                });

    }
}
