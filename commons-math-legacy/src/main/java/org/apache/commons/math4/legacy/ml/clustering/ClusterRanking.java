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

import java.util.List;

/**
 * Evaluates the quality of a set of clusters.
 * It is assumed that
 * <ul>
 *  <li>rank is positive,</li>
 *  <li>higher rank means better clustering.</li>
 * </ul>
 */
@FunctionalInterface
public interface ClusterRanking {
    /**
     * Computes the rank (higher is better).
     *
     * @param clusters Clusters to be evaluated.
     * @return the rank of the provided {@code clusters}.
     */
    double compute(List<? extends Cluster<? extends Clusterable>> clusters);
}
