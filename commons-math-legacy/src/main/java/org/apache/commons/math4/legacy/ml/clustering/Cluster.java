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
import java.util.List;

/**
 * Cluster holding a set of {@link Clusterable} points.
 * @param <T> the type of points that can be clustered
 * @since 3.2
 */
public class Cluster<T extends Clusterable> {

    /** The points contained in this cluster. */
    private final List<T> points;

    /**
     * Build a cluster centered at a specified point.
     */
    public Cluster() {
        points = new ArrayList<>();
    }

    /**
     * Add a point to this cluster.
     * @param point point to add
     */
    public void addPoint(final T point) {
        points.add(point);
    }

    /**
     * Get the points contained in the cluster.
     * @return points contained in the cluster
     */
    public List<T> getPoints() {
        return points;
    }

    /**
     * Computes the centroid of the cluster.
     *
     * @return the centroid for the cluster, or {@code null} if the
     * cluster does not contain any points.
     */
    public Clusterable centroid() {
        if (points.isEmpty()) {
            return null;
        } else {
            final int dimension = points.get(0).getPoint().length;
            final double[] centroid = new double[dimension];
            for (final T p : points) {
                final double[] point = p.getPoint();
                for (int i = 0; i < centroid.length; i++) {
                    centroid[i] += point[i];
                }
            }
            for (int i = 0; i < centroid.length; i++) {
                centroid[i] /= points.size();
            }
            return new DoublePoint(centroid);
        }
    }
}
