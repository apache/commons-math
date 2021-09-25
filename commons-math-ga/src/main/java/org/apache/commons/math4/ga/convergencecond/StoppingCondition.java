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
package org.apache.commons.math4.ga.convergencecond;

import org.apache.commons.math4.ga.population.Population;

/**
 * Algorithm used to determine when to stop evolution.
 *
 * @param <P>   phenotype of chromosome
 * @since 2.0
 */
public interface StoppingCondition<P> {

    /**
     * Determine whether or not the given population satisfies the stopping
     * condition.
     * @param population population of chromosome
     *
     * @return <code>true</code> if this stopping condition is met by the given
     *         population, <code>false</code> otherwise.
     */
    boolean isSatisfied(Population<P> population);
}