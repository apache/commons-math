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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.rng.sampling.shape.BoxSampler;
import org.apache.commons.math4.legacy.exception.NotStrictlyPositiveException;
import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;
import org.apache.commons.math4.legacy.stat.descriptive.moment.VectorialMean;
import org.apache.commons.math4.legacy.core.MathArrays;

/**
 * Tests for {@link ElkanKmeansPlusPlusClusterer}.
 */
public class ElkanKMeansPlusPlusClustererTest {
    @Test
    public void validateOneDimensionSingleClusterZeroMean() {
        final List<DoublePoint> testPoints = Arrays.asList(new DoublePoint(new double[]{1}),
                                                           new DoublePoint(new double[]{2}),
                                                           new DoublePoint(new double[]{-3}));
        final ElkanKMeansPlusPlusClusterer<DoublePoint> clusterer = new ElkanKMeansPlusPlusClusterer<>(1);
        final List<CentroidCluster<DoublePoint>> clusters = clusterer.cluster(testPoints);
        Assert.assertEquals(1, clusters.size());
        Assert.assertTrue(MathArrays.equals(new double[]{0}, clusters.get(0).getCenter().getPoint()));
    }

    @Test(expected = NumberIsTooSmallException.class)
    public void illegalKParameter() {
        final int n = 20;
        final int d = 3;
        final int k = 100;

        final List<DoublePoint> testPoints = generatePoints(n, d);
        final ElkanKMeansPlusPlusClusterer<DoublePoint> clusterer = new ElkanKMeansPlusPlusClusterer<>(k);
        clusterer.cluster(testPoints);
    }

    @Test
    public void numberOfClustersSameAsInputSize() {
        final int n = 3;
        final int d = 2;
        final int k = 3;

        final List<DoublePoint> testPoints = generatePoints(n, d);
        final ElkanKMeansPlusPlusClusterer<DoublePoint> clusterer = new ElkanKMeansPlusPlusClusterer<>(k);
        final List<CentroidCluster<DoublePoint>> clusters = clusterer.cluster(testPoints);
        Assert.assertEquals(k, clusters.size());
        Assert.assertEquals(1, clusters.get(0).getPoints().size());
        Assert.assertEquals(1, clusters.get(1).getPoints().size());
        Assert.assertEquals(1, clusters.get(2).getPoints().size());
    }

    @Test(expected = NullPointerException.class)
    public void illegalInputParameter() {
        final ElkanKMeansPlusPlusClusterer<DoublePoint> clusterer = new ElkanKMeansPlusPlusClusterer<>(10);
        clusterer.cluster(null);
    }

    @Test(expected = NumberIsTooSmallException.class)
    public void emptyInputPointsList() {
        final ElkanKMeansPlusPlusClusterer<DoublePoint> clusterer = new ElkanKMeansPlusPlusClusterer<>(10);
        clusterer.cluster(Collections.<DoublePoint>emptyList());
    }

    @Test(expected = NotStrictlyPositiveException.class)
    public void negativeKParameterValue() {
        new ElkanKMeansPlusPlusClusterer<>(-1);
    }

    @Test(expected = NotStrictlyPositiveException.class)
    public void kParameterEqualsZero() {
        new ElkanKMeansPlusPlusClusterer<>(0);
    }

    @Test
    public void oneClusterCenterShouldBeTheMean() {
        final int n = 100;
        final int d = 2;

        final List<DoublePoint> testPoints = generatePoints(n, d);
        final KMeansPlusPlusClusterer<DoublePoint> clusterer = new KMeansPlusPlusClusterer<>(1);

        final List<CentroidCluster<DoublePoint>> clusters = clusterer.cluster(testPoints);

        final VectorialMean mean = new VectorialMean(d);
        for (DoublePoint each : testPoints) {
            mean.increment(each.getPoint());
        }
        Assert.assertEquals(1, clusters.size());
        Assert.assertArrayEquals(mean.getResult(), clusters.get(0).getCenter().getPoint(), 1e-6);
    }

    /**
     * Generates a list of random uncorrelated points to cluster.
     *
     * @param n number of points
     * @param d dimensionality
     * @return list of n generated random vectors of dimension d.
     */
    private static List<DoublePoint> generatePoints(int n, int d) {
        final List<DoublePoint> results = new ArrayList<>();
        final double[] lower = new double[d];
        final double[] upper = new double[d];
        Arrays.fill(upper, 1);
        final BoxSampler rnd = BoxSampler.of(RandomSource.KISS.create(),
                                             lower,
                                             upper);

        for (int i = 0; i < n; i++) {
            results.add(new DoublePoint(rnd.sample()));
        }

        return results;
    }
}
