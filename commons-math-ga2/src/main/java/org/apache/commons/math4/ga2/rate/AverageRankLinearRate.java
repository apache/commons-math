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
package org.apache.commons.math4.ga2.rate;

import java.util.List;
import org.apache.commons.math4.ga2.ApplicationRate;
import org.apache.commons.math4.ga2.Population;

/**
 * Average-rank-weighted linear interpolation.
 */
/* package-private */ class AverageRankLinearRate extends ApplicationRate {
    /**
     * @param min Minimum probability.
     * @param max Maximum probability.
     */
    AverageRankLinearRate(double min,
                          double max) {
        super(min, max);
    }

    /** {@inheritDoc} */
    @Override
    public <G, P> double compute(Population<G, P> population,
                                 List<G> chromosomes) {
        if (chromosomes.isEmpty()) {
            throw new IllegalArgumentException("Empty list");
        }

        double avg = 0;
        for (G c : chromosomes) {
            avg += population.rank(c);
        }
        avg /= chromosomes.size();

        return min() + (max() - min()) * avg / (population.size() - 1);
    }
}
