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

import java.util.Collection;

/**
 * Just throw an exception
 */
public class ErrorClustersPointExtractor implements ClustersPointExtractor {
    /**
     * Just throw an exception
     *
     * @param clusters the {@link Cluster}s to search
     * @param <T> type of the points to cluster
     * @return a random point from the selected cluster
     */
    @Override
    public <T extends Clusterable> T extract(Collection<CentroidCluster<T>> clusters) {
        throw new ConvergenceException(LocalizedFormats.EMPTY_CLUSTER_IN_K_MEANS);
    }
}
