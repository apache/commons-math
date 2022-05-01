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
package org.apache.commons.math4.ga2;

import java.util.List;

/**
 * Computes the probability to apply a genetic operator (the higher the
 * probability, the higher the chance that offsrpings will be different
 * from their parents.
 */
public abstract class ApplicationRate {
    /** Minimum probability. */
    private final double min;
    /** Maximum probability. */
    private final double max;

    /**
     * @param min Minimum probability.
     * @param max Maximum probability.
     * @throws IllegalArgumentException if either probability is out of
     * the {@code [0 1]} interval or {@code min > max}.
     */
    protected ApplicationRate(double min,
                              double max) {
        if (min < 0 ||
            min > 1) {
            throw new IllegalArgumentException("Probability (min) is out of range");
        }
        if (max < 0 ||
            max > 1) {
            throw new IllegalArgumentException("Probability (max) is out of range");
        }
        if (min >= max) {
            throw new IllegalArgumentException("max <= min");
        }

        this.min = min;
        this.max = max;
    }

    /**
     * @param p Probability.
     * @throws IllegalArgumentException if the probability is out of
     * the {@code [0 1]} interval.
     */
    protected ApplicationRate(double p) {
        if (p < 0 ||
            p > 1) {
            throw new IllegalArgumentException("Probability is out of range");
        }
        this.min = p;
        this.max = p;
    }

    /** @return the minimum probability. */
    protected double min() {
        return min;
    }
    /** @return the maximum probability. */
    protected double max() {
        return max;
    }

    /**
     * Computes the probability that some operator will be applied to the
     * given {@code chromosomes}.
     *
     * @param population Population.
     * @param chromosomes Chromosomes that belong to the {@code population}.
     * @return the probability to apply the operator.
     *
     * @param <G> Genotype.
     * @param <P> Phenotype.
     */
    public abstract <G, P> double compute(Population<G, P> population,
                                          List<G> chromosomes);
}
