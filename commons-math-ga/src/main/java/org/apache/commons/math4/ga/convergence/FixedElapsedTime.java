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

import java.util.concurrent.TimeUnit;

import org.apache.commons.math4.ga.internal.exception.GeneticException;
import org.apache.commons.math4.ga.population.Population;

/**
 * Stops after a fixed amount of time has elapsed.
 * <p>
 * The first time {@link #isSatisfied(Population)} is invoked, the end time of
 * the evolution is determined based on the provided <code>maxTime</code> value.
 * Once the elapsed time reaches the configured <code>maxTime</code> value,
 * {@link #isSatisfied(Population)} returns true.
 *
 * @param <P> phenotype of chromosome
 * @since 3.1
 */
public class FixedElapsedTime<P> implements StoppingCondition<P> {

    /** Maximum allowed time period (in nanoseconds). */
    private final long maxTimePeriod;

    /** The predetermined termination time (stopping condition). */
    private long endTime = -1;

    /**
     * Create a new {@link FixedElapsedTime} instance.
     *
     * @param maxTime maximum number of seconds generations are allowed to evolve
     */
    public FixedElapsedTime(final long maxTime) {
        this(maxTime, TimeUnit.SECONDS);
    }

    /**
     * Create a new {@link FixedElapsedTime} instance.
     *
     * @param maxTime maximum time generations are allowed to evolve
     * @param unit    {@link TimeUnit} of the maxTime argument
     */
    public FixedElapsedTime(final long maxTime, final TimeUnit unit) {
        if (maxTime < 0) {
            throw new GeneticException(GeneticException.TOO_SMALL, maxTime, 0);
        }
        maxTimePeriod = unit.toNanos(maxTime);
    }

    /**
     * Determine whether or not the maximum allowed time has passed. The termination
     * time is determined after the first generation.
     *
     * @return <code>true</code> IFF the maximum allowed time period has elapsed
     */
    @Override
    public boolean isSatisfied(Population<P> population) {
        if (endTime < 0) {
            endTime = System.nanoTime() + maxTimePeriod;
        }

        return System.nanoTime() >= endTime;
    }

}
