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
package org.apache.commons.math4.ga.convergence;

import org.apache.commons.math4.ga.internal.exception.GeneticException;
import org.apache.commons.math4.ga.population.Population;

/**
 * Stops after a fixed number of generations.
 * <p>
 * Each time {@link #isSatisfied(Population)} is invoked, a generation counter
 * is incremented. Once the counter reaches the configured
 * {@code maxGenerations} value, {@link #isSatisfied(Population)} returns true.
 *
 * @param <P> phenotype of chromosome
 * @since 2.0
 */
public class FixedGenerationCount<P> implements StoppingCondition<P> {
    /** Number of generations that have passed. */
    private int numGenerations;

    /** Maximum number of generations (stopping criteria). */
    private final int maxGenerations;

    /**
     * Create a new FixedGenerationCount instance.
     *
     * @param maxGenerations number of generations to evolve
     */
    public FixedGenerationCount(final int maxGenerations) {
        if (maxGenerations <= 0) {
            throw new GeneticException(GeneticException.TOO_SMALL, maxGenerations, 1);
        }
        this.maxGenerations = maxGenerations;
    }

    /**
     * Determine whether or not the given number of generations have passed.
     * Increments the number of generations counter if the maximum has not been
     * reached.
     *
     * @return <code>true</code> IFF the maximum number of generations has been
     *         exceeded
     */
    @Override
    public boolean isSatisfied(Population<P> population) {
        if (this.numGenerations < this.maxGenerations) {
            numGenerations++;
            return false;
        }
        return true;
    }

    /**
     * Returns the number of generations that have already passed.
     * @return the number of generations that have passed
     */
    public int getNumGenerations() {
        return numGenerations;
    }

}
