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

package org.apache.commons.math4.ml.clustering.evaluation;

import org.apache.commons.math4.ml.clustering.CentroidCluster;
import org.apache.commons.math4.ml.clustering.DoublePoint;
import org.apache.commons.math4.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math4.ml.distance.DistanceMeasure;
import org.apache.commons.math4.ml.distance.EuclideanDistance;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CalinskiHarabaszTest {
    private ClusterEvaluator<DoublePoint> evaluator;

    @Before
    public void setUp() {
        evaluator = new CalinskiHarabasz<>();
    }

    @Test
    public void test_k_equals_4_is_best_for_a_4_center_points() {
        final int dimension = 2;
        final double[][] centers = {{-1, -1}, {0, 0}, {1, 1}, {2, 2}};
        UniformRandomProvider rnd = RandomSource.create(RandomSource.MT_64, 0);
        List<DoublePoint> points = new ArrayList<>();
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
        double[] evaluateResults = new double[5];
        double expect = 0.0;
        DistanceMeasure distanceMeasure = new EuclideanDistance();
        for (int i = 0; i < 5; i++) {
            final int k = i + 2;
            KMeansPlusPlusClusterer<DoublePoint> kMeans = new KMeansPlusPlusClusterer<>(k, -1, distanceMeasure, rnd);
            List<CentroidCluster<DoublePoint>> clusters = kMeans.cluster(points);
            evaluateResults[i] = evaluator.score(clusters);
            if (k == 4) expect = evaluateResults[i];
            // System.out.format("%d: %f\n", k, evaluator.score(clusters));
        }
        // k=4 get the highest score
        Assert.assertEquals(expect, max(evaluateResults), 0.0);
    }

    /**
     * Get the max value in a double array
     *
     * @param ary double array
     * @return the max value
     */
    private double max(double[] ary) {
        double max = Double.NEGATIVE_INFINITY;
        for (double v : ary) {
            max = FastMath.max(v, max);
        }
        return max;
    }

    @Test
    public void testOrdering() {
        assertFalse(evaluator.isBetterScore(10, 20));
        assertTrue(evaluator.isBetterScore(20, 1));
    }
}
