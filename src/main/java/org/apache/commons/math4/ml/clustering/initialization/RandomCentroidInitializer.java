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
import org.apache.commons.math4.ml.clustering.Clusterable;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.sampling.ListSampler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Random choose the initial centers.
 */
public class RandomCentroidInitializer implements CentroidInitializer {
    private final UniformRandomProvider random;

    /**
     * Build a random RandomCentroidInitializer
     *
     * @param random the random to use.
     */
    public RandomCentroidInitializer(final UniformRandomProvider random) {
        this.random = random;
    }

    /**
     * Random choose the initial centers.
     *
     * @param points the points to choose the initial centers from
     * @param k      The number of clusters
     * @return the initial centers
     */
    @Override
    public <T extends Clusterable> List<CentroidCluster<T>> selectCentroids(final Collection<T> points, final int k) {
        if (k < 1) {
            return Collections.emptyList();
        }
        final ArrayList<T> list = new ArrayList<>(points);
        ListSampler.shuffle(random, list);
        final List<CentroidCluster<T>> result = new ArrayList<>(k);
        for (int i = 0; i < k; i++) {
            result.add(new CentroidCluster<>(list.get(i)));
        }
        return result;
    }
}
