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

package org.apache.commons.math4.legacy.ml.clustering.evaluation;

import java.util.List;

import org.apache.commons.math4.legacy.ml.clustering.Cluster;
import org.apache.commons.math4.legacy.ml.clustering.Clusterable;
import org.apache.commons.math4.legacy.ml.clustering.ClusterEvaluator;
import org.apache.commons.math4.legacy.ml.distance.DistanceMeasure;
import org.apache.commons.math4.legacy.stat.descriptive.moment.Variance;

/**
 * Computes the sum of intra-cluster distance variances according to the formula:
 * <pre>
 * \( score = \sum\limits_{i=1}^n \sigma_i^2 \)
 * </pre>
 * where n is the number of clusters and \( \sigma_i^2 \) is the variance of
 * intra-cluster distances of cluster \( c_i \).
 *
 * @since 3.3
 */
public class SumOfClusterVariances implements ClusterEvaluator {
    /** The distance measure to use when evaluating the cluster. */
    private final DistanceMeasure measure;

    /**
     * @param measure Distance measure.
     */
    public SumOfClusterVariances(final DistanceMeasure measure) {
        this.measure = measure;
    }

    /** {@inheritDoc} */
    @Override
    public double score(List<? extends Cluster<? extends Clusterable>> clusters) {
        double varianceSum = 0.0;
        for (final Cluster<? extends Clusterable> cluster : clusters) {
            if (!cluster.getPoints().isEmpty()) {

                final Clusterable center = cluster.centroid();

                // compute the distance variance of the current cluster
                final Variance stat = new Variance();
                for (final Clusterable point : cluster.getPoints()) {
                    stat.increment(distance(point, center));
                }

                varianceSum += stat.getResult();
            }
        }
        return varianceSum;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isBetterScore(double a,
                                 double b) {
        return a < b;
    }

    /**
     * Calculates the distance between two {@link Clusterable} instances
     * with the configured {@link DistanceMeasure}.
     *
     * @param p1 the first clusterable
     * @param p2 the second clusterable
     * @return the distance between the two clusterables
     */
    private double distance(final Clusterable p1, final Clusterable p2) {
        return measure.compute(p1.getPoint(), p2.getPoint());
    }
}
