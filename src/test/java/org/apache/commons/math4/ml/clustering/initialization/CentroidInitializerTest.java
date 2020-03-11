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
