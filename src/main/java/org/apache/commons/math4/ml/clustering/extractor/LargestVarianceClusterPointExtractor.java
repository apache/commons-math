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

package org.apache.commons.math4.ml.clustering.extractor;

import org.apache.commons.math4.exception.ConvergenceException;
import org.apache.commons.math4.exception.util.LocalizedFormats;
import org.apache.commons.math4.ml.clustering.CentroidCluster;
import org.apache.commons.math4.ml.clustering.Cluster;
import org.apache.commons.math4.ml.clustering.Clusterable;
import org.apache.commons.math4.ml.clustering.ClustersPointExtractor;
import org.apache.commons.math4.ml.distance.DistanceMeasure;
import org.apache.commons.math4.stat.descriptive.moment.Variance;
import org.apache.commons.rng.UniformRandomProvider;

import java.util.Collection;
import java.util.List;

/**
 * Get a random point from the {@link Cluster} with the largest distance variance.
 */
public class LargestVarianceClusterPointExtractor implements ClustersPointExtractor {

    /**
     * The distance measure to use.
     */
    private final DistanceMeasure distanceMeasure;

    /**
     * Random generator for choosing initial centers.
     */
    private final UniformRandomProvider random;

    /**
     * Build a ClustersPointExtractor.
     *
     * @param measure the distance measure to use
     * @param random  random generator to use for choosing initial centers
     */
    public LargestVarianceClusterPointExtractor(DistanceMeasure measure, UniformRandomProvider random) {
        this.distanceMeasure = measure;
        this.random = random;
    }

    /**
     * Get a random point from the {@link Cluster} with the largest distance variance.
     *
     * @param clusters the {@link Cluster}s to search
     * @param <T> type of the points to cluster
     * @return a random point from the selected cluster
     * @throws ConvergenceException if clusters are all empty
     */
    @Override
    public <T extends Clusterable> T extract(Collection<CentroidCluster<T>> clusters) {
        double maxVariance = Double.NEGATIVE_INFINITY;
        Cluster<T> selected = null;
        for (final CentroidCluster<T> cluster : clusters) {
            if (!cluster.getPoints().isEmpty()) {

                // compute the distance variance of the current cluster
                final Clusterable center = cluster.centroid();
                final Variance stat = new Variance();
                for (final T point : cluster.getPoints()) {
                    stat.increment(distanceMeasure.compute(point.getPoint(), center.getPoint()));
                }
                final double variance = stat.getResult();

                // select the cluster with the largest variance
                if (variance > maxVariance) {
                    maxVariance = variance;
                    selected = cluster;
                }

            }
        }

        // did we find at least one non-empty cluster ?
        if (selected == null) {
            throw new ConvergenceException(LocalizedFormats.EMPTY_CLUSTER_IN_K_MEANS);
        }

        // extract a random point from the cluster
        final List<T> selectedPoints = selected.getPoints();
        return selectedPoints.remove(random.nextInt(selectedPoints.size()));
    }
}
