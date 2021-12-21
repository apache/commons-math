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
package org.apache.commons.math4.ga.mutation;

import org.apache.commons.math4.ga.chromosome.Chromosome;

/**
 * Algorithm used to mutate a chromosome.
 * @param <P> phenotype of chromosome
 * @since 4.0
 */
public interface MutationPolicy<P> {

    /**
     * Mutate the given chromosome.
     * @param original     the original chromosome.
     * @param mutationRate The probability of mutation
     * @return the mutated chromosome.
     */
    Chromosome<P> mutate(Chromosome<P> original, double mutationRate);

}
