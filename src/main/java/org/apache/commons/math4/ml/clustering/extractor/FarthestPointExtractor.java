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

import java.util.Collection;
import java.util.List;

/**
 * Get the point farthest to its cluster center
 */
public class FarthestPointExtractor implements ClustersPointExtractor {

    /**
     * The distance measure to use.
     */
    private final DistanceMeasure distanceMeasure;

    /**
     * Build a ClustersPointExtractor.
     *
     * @param measure the distance measure to use
     */
    public FarthestPointExtractor(DistanceMeasure measure) {
        this.distanceMeasure = measure;
    }

    /**
     * Get the point farthest to its cluster center
     *
     * @param clusters the {@link Cluster}s to search
     * @param <T> type of the points to cluster
     * @return point farthest to its cluster center
     * @throws ConvergenceException if clusters are all empty
     */
    @Override
    public <T extends Clusterable> T extract(Collection<CentroidCluster<T>> clusters) {
        double maxDistance = Double.NEGATIVE_INFINITY;
        Cluster<T> selectedCluster = null;
        int selectedPoint = -1;
        for (final CentroidCluster<T> cluster : clusters) {

            // get the farthest point
            final Clusterable center = cluster.centroid();
            final List<T> points = cluster.getPoints();
            for (int i = 0; i < points.size(); ++i) {
                final double distance = distanceMeasure.compute(points.get(i).getPoint(), center.getPoint());
                if (distance > maxDistance) {
                    maxDistance     = distance;
                    selectedCluster = cluster;
                    selectedPoint   = i;
                }
            }

        }

        // did we find at least one non-empty cluster ?
        if (selectedCluster == null) {
            throw new ConvergenceException(LocalizedFormats.EMPTY_CLUSTER_IN_K_MEANS);
        }

        return selectedCluster.getPoints().remove(selectedPoint);
    }
}
