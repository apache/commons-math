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
import java.util.Collection;
import java.util.List;

import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;
import org.apache.commons.math4.legacy.ml.distance.EuclideanDistance;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.rng.UniformRandomProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class KMeansPlusPlusClustererTest {

    private UniformRandomProvider random;

    @Before
    public void setUp() {
        random = RandomSource.MT_64.create(1746432956321L);
    }

    /**
     * JIRA: MATH-305
     *
     * Two points, one cluster, one iteration
     */
    @Test
    public void testPerformClusterAnalysisDegenerate() {
        KMeansPlusPlusClusterer<DoublePoint> transformer =
                new KMeansPlusPlusClusterer<>(1, 1);

        DoublePoint[] points = new DoublePoint[] {
                new DoublePoint(new int[] { 1959, 325100 }),
                new DoublePoint(new int[] { 1960, 373200 }), };
        List<? extends Cluster<DoublePoint>> clusters = transformer.cluster(Arrays.asList(points));
        Assert.assertEquals(1, clusters.size());
        Assert.assertEquals(2, clusters.get(0).getPoints().size());
        DoublePoint pt1 = new DoublePoint(new int[] { 1959, 325100 });
        DoublePoint pt2 = new DoublePoint(new int[] { 1960, 373200 });
        Assert.assertTrue(clusters.get(0).getPoints().contains(pt1));
        Assert.assertTrue(clusters.get(0).getPoints().contains(pt2));
    }

    @Test
    public void testCertainSpace() {
        KMeansPlusPlusClusterer.EmptyClusterStrategy[] strategies = {
            KMeansPlusPlusClusterer.EmptyClusterStrategy.LARGEST_VARIANCE,
            KMeansPlusPlusClusterer.EmptyClusterStrategy.LARGEST_POINTS_NUMBER,
            KMeansPlusPlusClusterer.EmptyClusterStrategy.FARTHEST_POINT
        };
        for (KMeansPlusPlusClusterer.EmptyClusterStrategy strategy : strategies) {
            int numberOfVariables = 27;
            // initialize test values
            int position1 = 1;
            int position2 = position1 + numberOfVariables;
            int position3 = position2 + numberOfVariables;
            int position4 = position3 + numberOfVariables;
            // test values will be multiplied
            int multiplier = 1000000;

            DoublePoint[] breakingPoints = new DoublePoint[numberOfVariables];
            // define the space which will break the cluster algorithm
            for (int i = 0; i < numberOfVariables; i++) {
                int points[] = { position1, position2, position3, position4 };
                // multiply the values
                for (int j = 0; j < points.length; j++) {
                    points[j] *= multiplier;
                }
                DoublePoint DoublePoint = new DoublePoint(points);
                breakingPoints[i] = DoublePoint;
                position1 += numberOfVariables;
                position2 += numberOfVariables;
                position3 += numberOfVariables;
                position4 += numberOfVariables;
            }

            for (int n = 2; n < 27; ++n) {
                KMeansPlusPlusClusterer<DoublePoint> transformer =
                    new KMeansPlusPlusClusterer<>(n, 100, new EuclideanDistance(), random, strategy);

                List<? extends Cluster<DoublePoint>> clusters =
                        transformer.cluster(Arrays.asList(breakingPoints));

                Assert.assertEquals(n, clusters.size());
                int sum = 0;
                for (Cluster<DoublePoint> cluster : clusters) {
                    sum += cluster.getPoints().size();
                }
                Assert.assertEquals(numberOfVariables, sum);
            }
        }
    }

    /**
     * A helper class for testSmallDistances(). This class is similar to DoublePoint, but
     * it defines a different distanceFrom() method that tends to return distances less than 1.
     */
    private class CloseDistance extends EuclideanDistance {
        private static final long serialVersionUID = 1L;

        @Override
        public double compute(double[] a, double[] b) {
            return super.compute(a, b) * 0.001;
        }
    }

    /**
     * Test points that are very close together. See issue MATH-546.
     */
    @Test
    public void testSmallDistances() {
        // Create a bunch of CloseDoublePoints. Most are identical, but one is different by a
        // small distance.
        final int[] repeatedArray = { 0 };
        final int[] uniqueArray = { 1 };
        final DoublePoint repeatedPoint = new DoublePoint(repeatedArray);
        final DoublePoint uniquePoint = new DoublePoint(uniqueArray);

        final Collection<DoublePoint> points = new ArrayList<>();
        final int numRepeated = 10000;
        for (int i = 0; i < numRepeated; i++) {
            points.add(repeatedPoint);
        }
        points.add(uniquePoint);

        final KMeansPlusPlusClusterer<DoublePoint> clusterer =
            new KMeansPlusPlusClusterer<>(2, 1, new CloseDistance(), random);
        final List<CentroidCluster<DoublePoint>> clusters = clusterer.cluster(points);

        // Check that one of the chosen centers is the unique point.
        boolean uniquePointIsCenter = false;
        for (CentroidCluster<DoublePoint> cluster : clusters) {
            if (cluster.getCenter().equals(uniquePoint)) {
                uniquePointIsCenter = true;
            }
        }
        Assert.assertTrue(uniquePointIsCenter);
    }

    /**
     * 2 variables cannot be clustered into 3 clusters. See issue MATH-436.
     */
    @Test(expected=NumberIsTooSmallException.class)
    public void testPerformClusterAnalysisToManyClusters() {
        KMeansPlusPlusClusterer<DoublePoint> transformer =
            new KMeansPlusPlusClusterer<>(3, 1, new EuclideanDistance(), random);

        DoublePoint[] points = new DoublePoint[] {
            new DoublePoint(new int[] {
                1959, 325100
            }), new DoublePoint(new int[] {
                1960, 373200
            })
        };

        transformer.cluster(Arrays.asList(points));
    }
}
