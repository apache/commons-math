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

package org.apache.commons.math4.ml.clustering;

import java.util.List;

public interface ClusterEvaluator {
    /**
     * @param cList List of clusters.
     * @return the score attributed by the evaluator.
     */
    double score(List<? extends Cluster<? extends Clusterable>> cList);

    /**
     * @param a Score computed by this evaluator.
     * @param b Score computed by this evaluator.
     * @return true if the evaluator considers score {@code a} is
     * considered better than score {@code b}.
     */
    boolean isBetterScore(double a, double b);

    /**
     * Converts to a {@link ClusterRanking ranking function}
     * (as required by clustering implementations).
     *
     * @param eval Evaluator function.
     * @return a ranking function.
     */
    static <T extends Clusterable> ClusterRanking ranking(ClusterEvaluator eval) {
        return clusters -> {
            double score = eval.score(clusters);
            return eval.isBetterScore(1, 2) ? score : 1 / score;
        };
    }
}
