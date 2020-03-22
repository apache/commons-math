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

import java.util.Collection;
import java.util.List;

/**
 * Interface abstract the algorithm for clusterer to choose the initial centers.
 */
public interface CentroidInitializer {

    /**
     * Choose the initial centers.
     *
     * @param <T> Type of points to cluster.
     * @param points the points to choose the initial centers from
     * @param k      The number of clusters
     * @return the initial centers
     */
    <T extends Clusterable> List<CentroidCluster<T>> selectCentroids(final Collection<T> points, final int k);
}
